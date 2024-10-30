/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.StateCheckingResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.BaseServiceException;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import io.grpc.Status.Code;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class GapicUnbufferedReadableByteChannel
    implements UnbufferedReadableByteChannel, ScatteringByteChannel {
  private static final java.lang.Object EOF_MARKER = new java.lang.Object();

  private final SettableApiFuture<Object> result;
  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
  private final ReadObjectRequest req;
  private final Hasher hasher;
  private final ResponseContentLifecycleManager rclm;
  private final RetryingDependencies retryingDeps;
  private final ResultRetryAlgorithm<?> alg;
  private final SimpleBlockingQueue<java.lang.Object> queue;

  private final AtomicLong fetchOffset;
  private volatile ReadObjectObserver readObjectObserver;
  private volatile boolean open = true;
  private volatile boolean complete = false;

  private long blobOffset;
  private Object metadata;
  private ResponseContentLifecycleHandle leftovers;

  GapicUnbufferedReadableByteChannel(
      SettableApiFuture<Object> result,
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      ReadObjectRequest req,
      Hasher hasher,
      RetryingDependencies retryingDependencies,
      ResultRetryAlgorithm<?> alg,
      ResponseContentLifecycleManager rclm) {
    this.result = result;
    this.read = read;
    this.req = req;
    this.hasher = hasher;
    this.fetchOffset = new AtomicLong(req.getReadOffset());
    this.blobOffset = req.getReadOffset();
    this.rclm = rclm;
    this.retryingDeps = retryingDependencies;
    this.alg = alg;
    // The reasoning for 2 elements below allow for a single response and the EOF/error signal
    // from onComplete or onError. Same thing com.google.api.gax.rpc.QueuingResponseObserver does.
    this.queue = new SimpleBlockingQueue<>(2);
  }

  @Override
  public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
    if (complete && open) {
      close();
      return -1;
    }
    if (!open) {
      throw new ClosedChannelException();
    }

    long totalBufferCapacity = Buffers.totalRemaining(dsts, offset, length);
    ReadCursor c = new ReadCursor(blobOffset, blobOffset + totalBufferCapacity);
    while (c.hasRemaining()) {
      if (leftovers != null) {
        leftovers.copy(c, dsts, offset, length);
        if (!leftovers.hasRemaining()) {
          leftovers.close();
          leftovers = null;
        }
        continue;
      }

      ensureStreamOpen();
      java.lang.Object take;
      try {
        take = queue.poll();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new InterruptedIOException();
      }
      if (take instanceof Throwable) {
        Throwable throwable = (Throwable) take;
        BaseServiceException coalesce = StorageException.coalesce(throwable);
        if (alg.shouldRetry(coalesce, null)) {
          readObjectObserver = null;
          continue;
        } else {
          throw new IOException(coalesce);
        }
      }
      if (take == EOF_MARKER) {
        complete = true;
        break;
      }
      readObjectObserver.request();

      ReadObjectResponse resp = (ReadObjectResponse) take;
      ResponseContentLifecycleHandle handle = rclm.get(resp);
      if (resp.hasMetadata()) {
        Object respMetadata = resp.getMetadata();
        if (metadata == null) {
          metadata = respMetadata;
        } else if (metadata.getGeneration() != respMetadata.getGeneration()) {
          throw closeWithError(
              String.format(
                  "Mismatch Generation between subsequent reads. Expected %d but received %d",
                  metadata.getGeneration(), respMetadata.getGeneration()));
        }
      }
      ChecksummedData checksummedData = resp.getChecksummedData();
      ByteString content = checksummedData.getContent();
      int contentSize = content.size();
      // Very important to know whether a crc32c value is set. Without checking, protobuf will
      // happily return 0, which is a valid crc32c value.
      if (checksummedData.hasCrc32C()) {
        Crc32cLengthKnown expected = Crc32cValue.of(checksummedData.getCrc32C(), contentSize);
        try {
          hasher.validate(expected, content.asReadOnlyByteBufferList());
        } catch (IOException e) {
          close();
          throw e;
        }
      }
      handle.copy(c, dsts, offset, length);
      if (handle.hasRemaining()) {
        leftovers = handle;
      } else {
        handle.close();
      }
    }
    long read = c.read();

    blobOffset += read;

    return read;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    open = false;
    try {
      if (leftovers != null) {
        leftovers.close();
      }
      ReadObjectObserver obs = readObjectObserver;
      if (obs != null && !obs.cancellation.isDone()) {
        obs.cancel();
        drainQueue();
        try {
          // make sure our waiting doesn't lockup permanently
          obs.cancellation.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          InterruptedIOException ioe = new InterruptedIOException();
          ioe.initCause(e);
          ioe.addSuppressed(new AsyncStorageTaskException());
          throw ioe;
        } catch (ExecutionException e) {
          Throwable cause = e;
          if (e.getCause() != null) {
            cause = e.getCause();
          }
          IOException ioException = new IOException(cause);
          ioException.addSuppressed(new AsyncStorageTaskException());
          throw ioException;
        } catch (TimeoutException ignore) {
        }
      }
    } finally {
      drainQueue();
    }
  }

  private void drainQueue() throws IOException {
    IOException ioException = null;
    while (queue.nonEmpty()) {
      try {
        java.lang.Object queueValue = queue.poll();
        if (queueValue instanceof ReadObjectResponse) {
          ReadObjectResponse resp = (ReadObjectResponse) queueValue;
          ResponseContentLifecycleHandle handle = rclm.get(resp);
          handle.close();
        } else if (queueValue == EOF_MARKER || queueValue instanceof Throwable) {
          break;
        }
      } catch (IOException e) {
        if (ioException == null) {
          ioException = e;
        } else if (ioException != e) {
          ioException.addSuppressed(e);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        if (ioException == null) {
          ioException = new InterruptedIOException();
        } else {
          ioException.addSuppressed(e);
        }
      }
    }
    if (ioException != null) {
      throw ioException;
    }
  }

  ApiFuture<Object> getResult() {
    return result;
  }

  private void ensureStreamOpen() {
    if (readObjectObserver == null) {
      java.lang.Object peek = queue.peek();
      if (peek instanceof Throwable || peek == EOF_MARKER) {
        // If our queue has an error or EOF, do not send another request
        return;
      }
      readObjectObserver =
          Retrying.run(
              retryingDeps,
              alg,
              () -> {
                ReadObjectObserver tmp = new ReadObjectObserver();
                ReadObjectRequest.Builder builder = req.toBuilder();
                long currentFetchOffset = fetchOffset.get();
                if (req.getReadOffset() != currentFetchOffset) {
                  builder.setReadOffset(currentFetchOffset);
                }
                if (metadata != null && req.getGeneration() == 0) {
                  builder.setGeneration(metadata.getGeneration());
                }
                read.call(builder.build(), tmp);
                ApiExceptions.callAndTranslateApiException(tmp.open);
                return tmp;
              },
              Decoder.identity());
    }
  }

  private IOException closeWithError(String message) throws IOException {
    close();
    StorageException cause =
        new StorageException(HttpStatusCodes.STATUS_CODE_PRECONDITION_FAILED, message);
    throw new IOException(message, cause);
  }

  private final class ReadObjectObserver extends StateCheckingResponseObserver<ReadObjectResponse> {

    private final SettableApiFuture<Void> open = SettableApiFuture.create();
    private final SettableApiFuture<Throwable> cancellation = SettableApiFuture.create();

    private volatile StreamController controller;

    void request() {
      controller.request(1);
    }

    void cancel() {
      controller.cancel();
    }

    @Override
    protected void onStartImpl(StreamController controller) {
      this.controller = controller;
      controller.disableAutoInboundFlowControl();
      controller.request(1);
    }

    @Override
    protected void onResponseImpl(ReadObjectResponse response) {
      try {
        open.set(null);
        queue.offer(response);
        fetchOffset.addAndGet(response.getChecksummedData().getContent().size());
        if (response.hasMetadata() && !result.isDone()) {
          result.set(response.getMetadata());
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw Code.ABORTED.toStatus().withCause(e).asRuntimeException();
      }
    }

    @Override
    protected void onErrorImpl(Throwable t) {
      if (t instanceof CancellationException) {
        cancellation.set(t);
      }
      if (!open.isDone()) {
        open.setException(t);
        if (!alg.shouldRetry(t, null)) {
          result.setException(StorageException.coalesce(t));
        }
      }
      try {
        queue.offer(t);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw Code.ABORTED.toStatus().withCause(e).asRuntimeException();
      }
    }

    @Override
    protected void onCompleteImpl() {
      try {
        cancellation.set(null);
        queue.offer(EOF_MARKER);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw Code.ABORTED.toStatus().withCause(e).asRuntimeException();
      }
    }
  }

  /**
   * Simplified wrapper around an {@link java.util.concurrent.ArrayBlockingQueue}. We don't need the
   * majority of methods/functionality just blocking offer/poll.
   */
  static final class SimpleBlockingQueue<T> {

    private final ArrayBlockingQueue<T> queue;

    SimpleBlockingQueue(int poolMaxSize) {
      this.queue = new ArrayBlockingQueue<>(poolMaxSize);
    }

    public boolean nonEmpty() {
      return !queue.isEmpty();
    }

    @Nullable
    public T peek() {
      return queue.peek();
    }

    @NonNull
    public T poll() throws InterruptedException {
      return queue.take();
    }

    public void offer(@NonNull T element) throws InterruptedException {
      queue.put(element);
    }
  }
}
