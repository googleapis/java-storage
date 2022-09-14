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

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * When writing to GCS using the WriteObject rpc, there are some behavioral differences between
 * performing a "direct" upload vs a "resumable" upload.
 *
 * <p>This class provides the encapsulation of the stream and "fsync" lifecycles and possible
 * automatic retry where applicable.
 *
 * <p>In this class "fsync" is used to mean "complete the client stream to GCS and await its
 * response". We are loosely following the concept used in linux to flush data to disk <a
 * target="_blank" rel="noopener noreferrer"
 * href="https://man7.org/linux/man-pages/man2/fdatasync.2.html">fsync(2)</a>
 */
final class WriteFlushStrategy {

  private WriteFlushStrategy() {}

  /**
   * Create a {@link Flusher} which will "fsync" every time {@link Flusher#flush(List)} is called
   * along with {@link Flusher#close(WriteObjectRequest)}.
   */
  static FlusherFactory fsyncEveryFlush(
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg) {
    return (String bucketName,
        LongConsumer committedTotalBytesCallback,
        Consumer<WriteObjectResponse> onSuccessCallback) ->
        new FsyncEveryFlusher(
            write, deps, alg, bucketName, committedTotalBytesCallback, onSuccessCallback);
  }

  /**
   * Create a {@link Flusher} which will "fsync" only on {@link Flusher#close(WriteObjectRequest)}.
   * Calls to {@link Flusher#flush(List)} will be sent but not synced.
   *
   * @see FlusherFactory#newFlusher(String, LongConsumer, Consumer)
   */
  static FlusherFactory fsyncOnClose(
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write) {
    return (String bucketName,
        LongConsumer committedTotalBytesCallback,
        Consumer<WriteObjectResponse> onSuccessCallback) ->
        new FsyncOnClose(write, bucketName, committedTotalBytesCallback, onSuccessCallback);
  }

  private static GrpcCallContext contextWithBucketName(String bucketName) {
    GrpcCallContext ret = GrpcCallContext.createDefault();
    if (bucketName != null && !bucketName.isEmpty()) {
      return ret.withExtraHeaders(
          ImmutableMap.of(
              "x-goog-request-params", ImmutableList.of(String.format("bucket=%s", bucketName))));
    }
    return ret;
  }

  @FunctionalInterface
  interface FlusherFactory {
    /**
     * @param committedTotalBytesCallback Callback to signal the total number of bytes committed by
     *     this flusher.
     * @param onSuccessCallback Callback to signal success, and provide the final response.
     */
    Flusher newFlusher(
        String bucketName,
        LongConsumer committedTotalBytesCallback,
        Consumer<WriteObjectResponse> onSuccessCallback);
  }

  interface Flusher {
    void flush(@NonNull List<WriteObjectRequest> segments);

    void close(@Nullable WriteObjectRequest req);
  }

  private static final class FsyncEveryFlusher implements Flusher {

    private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;
    private final RetryingDependencies deps;
    private final ResultRetryAlgorithm<?> alg;
    private final String bucketName;
    private final LongConsumer sizeCallback;
    private final Consumer<WriteObjectResponse> completeCallback;

    private FsyncEveryFlusher(
        ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
        RetryingDependencies deps,
        ResultRetryAlgorithm<?> alg,
        String bucketName,
        LongConsumer sizeCallback,
        Consumer<WriteObjectResponse> completeCallback) {
      this.write = write;
      this.deps = deps;
      this.alg = alg;
      this.bucketName = bucketName;
      this.sizeCallback = sizeCallback;
      this.completeCallback = completeCallback;
    }

    public void flush(@NonNull List<WriteObjectRequest> segments) {
      Retrying.run(
          deps,
          alg,
          () -> {
            Observer observer = new Observer(sizeCallback, completeCallback);
            GrpcCallContext internalContext = contextWithBucketName(bucketName);
            ApiStreamObserver<WriteObjectRequest> write =
                this.write.withDefaultCallContext(internalContext).clientStreamingCall(observer);

            boolean first = true;
            for (WriteObjectRequest message : segments) {
              if (!first) {
                message = message.toBuilder().clearUploadId().clearWriteObjectSpec().build();
              }

              write.onNext(message);
              first = false;
            }
            write.onCompleted();
            observer.await();
            return null;
          },
          Decoder.identity());
    }

    public void close(@Nullable WriteObjectRequest req) {
      if (req != null) {
        flush(ImmutableList.of(req));
      }
    }
  }

  private static final class FsyncOnClose implements Flusher {

    private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;
    private final String bucketName;
    private final Observer responseObserver;

    private volatile ApiStreamObserver<WriteObjectRequest> stream;
    private boolean first = true;

    private FsyncOnClose(
        ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
        String bucketName,
        LongConsumer sizeCallback,
        Consumer<WriteObjectResponse> completeCallback) {
      this.write = write;
      this.bucketName = bucketName;
      this.responseObserver = new Observer(sizeCallback, completeCallback);
    }

    @Override
    public void flush(@NonNull List<WriteObjectRequest> segments) {
      ensureOpen();
      for (WriteObjectRequest message : segments) {
        if (!first) {
          message = message.toBuilder().clearUploadId().clearWriteObjectSpec().build();
        }

        stream.onNext(message);
        first = false;
      }
    }

    @Override
    public void close(@Nullable WriteObjectRequest message) {
      ensureOpen();
      if (message != null) {
        if (!first) {
          message = message.toBuilder().clearUploadId().clearWriteObjectSpec().build();
        }
        stream.onNext(message);
      }
      stream.onCompleted();
      responseObserver.await();
    }

    private void ensureOpen() {
      if (stream == null) {
        synchronized (this) {
          if (stream == null) {
            GrpcCallContext internalContext = contextWithBucketName(bucketName);
            stream =
                this.write
                    .withDefaultCallContext(internalContext)
                    .clientStreamingCall(responseObserver);
          }
        }
      }
    }
  }

  private static class Observer implements ApiStreamObserver<WriteObjectResponse> {

    private final LongConsumer sizeCallback;
    private final Consumer<WriteObjectResponse> completeCallback;

    private final SettableApiFuture<Void> invocationHandle;
    private volatile WriteObjectResponse last;

    private Observer(LongConsumer sizeCallback, Consumer<WriteObjectResponse> completeCallback) {
      this.sizeCallback = sizeCallback;
      this.completeCallback = completeCallback;
      this.invocationHandle = SettableApiFuture.create();
    }

    @Override
    public void onNext(WriteObjectResponse value) {
      // incremental update
      if (value.hasPersistedSize()) {
        sizeCallback.accept(value.getPersistedSize());
      } else if (value.hasResource()) {
        sizeCallback.accept(value.getResource().getSize());
      }
      last = value;
    }

    /**
     * observed exceptions so far
     *
     * <ol>
     *   <li>{@link com.google.api.gax.rpc.OutOfRangeException}
     *   <li>{@link com.google.api.gax.rpc.AlreadyExistsException}
     *   <li>{@link io.grpc.StatusRuntimeException}
     * </ol>
     */
    @Override
    public void onError(Throwable t) {
      invocationHandle.setException(t);
    }

    @Override
    public void onCompleted() {
      if (last != null && last.hasResource()) {
        completeCallback.accept(last);
      }
      invocationHandle.set(null);
    }

    private void await() {
      try {
        invocationHandle.get();
      } catch (InterruptedException | ExecutionException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        } else {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
