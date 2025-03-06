/*
 * Copyright 2023 Google LLC
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
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ErrorDetails;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.OutOfRangeException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetrierWithAlg;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import com.google.storage.v2.AppendObjectSpec;
import com.google.storage.v2.BidiWriteHandle;
import com.google.storage.v2.BidiWriteObjectRedirectedError;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class GapicBidiUnbufferedAppendableWritableByteChannel
    implements UnbufferedWritableByteChannel {
  private final BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write;
  private final UnaryCallable<GetObjectRequest, Object> get;
  private final RetrierWithAlg retrier;
  private final SettableApiFuture<BidiWriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;
  private final BidiWriteCtx<BidiAppendableWrite> writeCtx;
  private final GrpcCallContext context;
  private final RedirectHandlingResponseObserver responseObserver;

  private volatile ApiStreamObserver<BidiWriteObjectRequest> stream;
  private boolean open = true;
  private boolean first = true;
  private boolean redirecting = false;
  volatile boolean retry = false;
  private long begin;
  private volatile BidiWriteObjectRequest lastWrittenRequest;
  private final AtomicInteger redirectCounter;
  private final int maxRedirectsAllowed = 3;
  private final AtomicReference<@Nullable BidiWriteHandle> bidiWriteHandle =
      new AtomicReference<>();
  private final AtomicReference<@Nullable String> routingToken = new AtomicReference<>();
  private final AtomicLong generation = new AtomicLong();
  private final ReentrantLock lock = new ReentrantLock();
  private final Supplier<GrpcCallContext> baseContextSupplier;
  private volatile List<BidiWriteObjectRequest> messages;

  GapicBidiUnbufferedAppendableWritableByteChannel(
      BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write,
      UnaryCallable<GetObjectRequest, Object> get,
      RetrierWithAlg retrier,
      SettableApiFuture<BidiWriteObjectResponse> resultFuture,
      ChunkSegmenter chunkSegmenter,
      BidiWriteCtx<BidiAppendableWrite> writeCtx,
      Supplier<GrpcCallContext> baseContextSupplier) {
    this.write = write;
    this.get = get;
    this.retrier = retrier;
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;
    this.writeCtx = writeCtx;
    this.responseObserver = new RedirectHandlingResponseObserver(new BidiObserver());
    this.baseContextSupplier = baseContextSupplier;
    this.context = baseContextSupplier.get().withExtraHeaders(getHeaders());
    this.redirectCounter = new AtomicInteger();
  }

  @Override
  public long write(ByteBuffer[] srcs, int srcsOffset, int srcsLength) throws IOException {
    return internalWrite(srcs, srcsOffset, srcsLength);
  }

  @Override
  public long writeAndClose(ByteBuffer[] srcs, int offset, int length) throws IOException {
    long written = internalWrite(srcs, offset, length);
    close();
    return written;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    if (!open) {
      return;
    }
    try {
      if (stream != null) {
        stream.onCompleted();
        responseObserver.await();
      }

    } finally {
      open = false;
      stream = null;
      lastWrittenRequest = null;
    }
  }

  public void finalizeWrite() throws IOException {
    if (stream == null) {
      restart();
    }
    BidiWriteObjectRequest message = finishMessage();
    lastWrittenRequest = message;
    begin = writeCtx.getConfirmedBytes().get();
    this.messages = Collections.singletonList(message);
    flush();
    close();
  }

  /**
   * After a reconnect, opens a new stream by using an AppendObjectSpec with a state lookup to get
   * the persisted size. We expect to be able to retry anything failed as normal after calling this
   * method, on the new stream.
   */
  @VisibleForTesting
  void restart() {
    Preconditions.checkState(
        stream == null, "attempting to restart stream when stream is already active");

    ReconnectArguments reconnectArguments = getReconnectArguments();
    BidiWriteObjectRequest req = reconnectArguments.getReq();
    if (!resultFuture.isDone()) {
      ApiStreamObserver<BidiWriteObjectRequest> requestStream1 =
          openedStream(reconnectArguments.getCtx());
      if (req != null) {
        requestStream1.onNext(req);
        lastWrittenRequest = req;
        responseObserver.await();
        first = false;
      } else {
        // This means we did a metadata lookup and determined that GCS never received the initial
        // WriteObjectSpec,
        // So we can just start over and send it again
        first = true;
      }
    }
  }

  public void startAppendableTakeoverStream() {
    BidiWriteObjectRequest req =
        writeCtx.newRequestBuilder().setFlush(true).setStateLookup(true).build();
    generation.set(req.getAppendObjectSpec().getGeneration());
    this.messages = Collections.singletonList(req);
    flush();
    first = false;
  }

  @VisibleForTesting
  BidiWriteCtx<BidiAppendableWrite> getWriteCtx() {
    return writeCtx;
  }

  private long internalWrite(ByteBuffer[] srcs, int srcsOffset, int srcsLength)
      throws ClosedChannelException {
    if (!open) {
      throw new ClosedChannelException();
    }

    begin = writeCtx.getConfirmedBytes().get();

    ChunkSegment[] data = chunkSegmenter.segmentBuffers(srcs, srcsOffset, srcsLength, true);
    if (data.length == 0) {
      return 0;
    }

    ImmutableList.Builder<BidiWriteObjectRequest> messages = new ImmutableList.Builder<>();

    for (int i = 0; i < data.length; i++) {
      ChunkSegment datum = data[i];
      Crc32cLengthKnown crc32c = datum.getCrc32c();
      ByteString b = datum.getB();
      int contentSize = b.size();
      long offset = writeCtx.getTotalSentBytes().getAndAdd(contentSize);
      Crc32cLengthKnown cumulative =
          writeCtx
              .getCumulativeCrc32c()
              .accumulateAndGet(crc32c, chunkSegmenter.getHasher()::nullSafeConcat);
      ChecksummedData.Builder checksummedData = ChecksummedData.newBuilder().setContent(b);
      if (crc32c != null) {
        checksummedData.setCrc32C(crc32c.getValue());
      }
      BidiWriteObjectRequest.Builder builder = writeCtx.newRequestBuilder();
      if (!first) {
        builder.clearUploadId();
        builder.clearObjectChecksums();
        builder.clearWriteObjectSpec();
        builder.clearAppendObjectSpec();
      } else {
        first = false;
      }
      builder.setWriteOffset(offset).setChecksummedData(checksummedData.build());

      if (!datum.isOnlyFullBlocks()) {
        if (cumulative != null) {
          builder.setObjectChecksums(
              ObjectChecksums.newBuilder().setCrc32C(cumulative.getValue()).build());
        }
      }

      if (i == data.length - 1) {
        builder.setFlush(true).setStateLookup(true);
      }
      BidiWriteObjectRequest build = builder.build();
      messages.add(build);
    }

    this.messages = messages.build();

    try {
      flush();
    } catch (Exception e) {
      open = false;
      resultFuture.setException(e);
      throw e;
    }

    long end = writeCtx.getConfirmedBytes().get();

    long bytesConsumed = end - begin;
    return bytesConsumed;
  }

  @NonNull
  private BidiWriteObjectRequest finishMessage() {
    long offset = writeCtx.getTotalSentBytes().get();
    Crc32cLengthKnown crc32cValue = writeCtx.getCumulativeCrc32c().get();

    BidiWriteObjectRequest.Builder b = writeCtx.newRequestBuilder();

    b.clearUploadId().clearObjectChecksums().clearWriteObjectSpec().clearAppendObjectSpec();

    b.setFinishWrite(true).setWriteOffset(offset);
    if (crc32cValue != null) {
      b.setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
    }
    BidiWriteObjectRequest message = b.build();
    return message;
  }

  private ApiStreamObserver<BidiWriteObjectRequest> openedStream(
      @Nullable GrpcCallContext context) {
    if (stream == null) {
      synchronized (this) {
        if (stream == null) {
          responseObserver.reset();
          stream =
              new GracefulOutboundStream(this.write.bidiStreamingCall(responseObserver, context));
        }
      }
    }
    return stream;
  }

  private void flush() {
    retrier.run(
        () -> {
          if (retry) {
            retry = false;
            restart();
            processRetryingMessages();
            if (this.messages.isEmpty()) {
              // This can happen if proccessRetryingMessages ends up dropping every message
              return null;
            }
          }
          try {
            ApiStreamObserver<BidiWriteObjectRequest> opened = openedStream(context);
            for (BidiWriteObjectRequest message : this.messages) {

              opened.onNext(message);
              lastWrittenRequest = message;
            }
            if (lastWrittenRequest.getFinishWrite()) {
              opened.onCompleted();
            }
            responseObserver.await();
            return null;
          } catch (Throwable t) {
            retry = true;
            stream = null;
            t.addSuppressed(new AsyncStorageTaskException());
            throw t;
          }
        },
        Decoder.identity());
  }

  /**
   * Handles a retry. Processes segments by skipping any necessary bytes and stripping
   * first-specific elements, then restarts the stream and flushes the processed segments.
   */
  private void processRetryingMessages() {
    ImmutableList.Builder<BidiWriteObjectRequest> segmentsToRetry = new ImmutableList.Builder<>();
    long confirmed = writeCtx.getConfirmedBytes().get();
    long bytesSeen = begin;
    boolean caughtUp = false;
    for (BidiWriteObjectRequest message : this.messages) {
      if (message.hasAppendObjectSpec() && first) {
        // If this is the first message of a takeover, then running the restart() method will
        // actually get us to the state we want to be in (i.e. the persisted_size has been
        // captured), so we don't actually need to try to write the original message again--we just
        // drop it entirely
        continue;
      }
      if (message.hasWriteObjectSpec()
          && redirecting) { // This is a first message and we got a Redirect
        message = message.toBuilder().clearWriteObjectSpec().clearObjectChecksums().build();
      }
      if (!caughtUp) {
        bytesSeen += message.getChecksummedData().getContent().size();
        if (bytesSeen <= confirmed) {
          // We already flushed this message and persisted the bytes, skip it
          continue;
        }
        ByteString before = message.getChecksummedData().getContent();
        long beforeSize = before.size();
        if ((bytesSeen - confirmed) != beforeSize) {
          // This means a partial flush occurred--we need to skip over some of the bytes and adjust
          // the offset
          long delta = bytesSeen - confirmed;
          int bytesToSkip = Math.toIntExact(beforeSize - delta);
          ByteString after = before.substring(bytesToSkip);

          if (after.size() == 0) { // GCS somehow flushed the whole request but still errored
            continue;
          }
          message =
              message
                  .toBuilder()
                  .setChecksummedData(ChecksummedData.newBuilder().setContent(after).build())
                  .setWriteOffset(confirmed)
                  .build();
        }
        caughtUp = true;
      }
      segmentsToRetry.add(message);
    }
    this.messages = segmentsToRetry.build();
  }

  private class BidiObserver implements ApiStreamObserver<BidiWriteObjectResponse> {

    private final Semaphore sem;
    private volatile BidiWriteObjectResponse last;
    private volatile StorageException clientDetectedError;
    private volatile RuntimeException previousError;

    private BidiObserver() {
      this.sem = new Semaphore(0);
    }

    @Override
    public void onNext(BidiWriteObjectResponse value) {
      if (value.hasWriteHandle()) {
        bidiWriteHandle.set(value.getWriteHandle());
      }
      if (lastWrittenRequest.hasAppendObjectSpec() && first) {
        long persistedSize =
            value.hasPersistedSize() ? value.getPersistedSize() : value.getResource().getSize();
        writeCtx.getConfirmedBytes().set(persistedSize);
        writeCtx.getTotalSentBytes().set(persistedSize);
        ok(value);
        return;
      }
      boolean finalizing = lastWrittenRequest.getFinishWrite();
      boolean firstResponse = !finalizing && value.hasResource();
      if (firstResponse) {
        generation.set(value.getResource().getGeneration());
      }

      if (!finalizing && (firstResponse || value.hasPersistedSize())) { // incremental
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long persistedSize =
            firstResponse ? value.getResource().getSize() : value.getPersistedSize();

        // todo: replace this with a state tracking variable
        if (lastWrittenRequest.hasAppendObjectSpec()) {
          writeCtx.getConfirmedBytes().set(persistedSize);
          ok(value);
        } else if (totalSentBytes == persistedSize) {
          writeCtx.getConfirmedBytes().set(persistedSize);
          ok(value);
        } else if (persistedSize < totalSentBytes) {
          writeCtx.getConfirmedBytes().set(persistedSize);
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_9.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        } else {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_7.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else if (finalizing && value.hasResource()) {
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long finalSize = value.getResource().getSize();
        if (totalSentBytes == finalSize) {
          writeCtx.getConfirmedBytes().set(finalSize);
          ok(value);
        } else if (finalSize < totalSentBytes) {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_4_1.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        } else {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_4_2.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else if (finalizing && value.hasPersistedSize()) {
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long persistedSize = value.getPersistedSize();
        // if a flush: true, state_lookup: true message is in the stream along with a
        // finish_write: true, GCS can respond with the incremental update, gracefully handle this
        // message
        if (totalSentBytes == persistedSize) {
          writeCtx.getConfirmedBytes().set(persistedSize);
        } else if (persistedSize < totalSentBytes) {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_3.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        } else {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_2.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else {
        clientDetectedError(
            ResumableSessionFailureScenario.SCENARIO_0.toStorageException(
                ImmutableList.of(lastWrittenRequest), value, context, null));
      }
    }

    @Override
    public void onError(Throwable t) {
      if (t instanceof OutOfRangeException) {
        OutOfRangeException oore = (OutOfRangeException) t;
        ErrorDetails ed = oore.getErrorDetails();
        if (!(ed != null
            && ed.getErrorInfo() != null
            && ed.getErrorInfo().getReason().equals("GRPC_MISMATCHED_UPLOAD_SIZE"))) {
          clientDetectedError(
              ResumableSessionFailureScenario.SCENARIO_5.toStorageException(
                  ImmutableList.of(lastWrittenRequest), null, context, oore));
          return;
        }
      }
      if (t instanceof ApiException) {
        // use StorageExceptions logic to translate from ApiException to our status codes ensuring
        // things fall in line with our retry handlers.
        // This is suboptimal, as it will initialize a second exception, however this is the
        // unusual case, and it should not cause a significant overhead given its rarity.
        StorageException tmp = StorageException.asStorageException((ApiException) t);
        previousError =
            ResumableSessionFailureScenario.toStorageException(
                tmp.getCode(),
                tmp.getMessage(),
                tmp.getReason(),
                lastWrittenRequest != null
                    ? ImmutableList.of(lastWrittenRequest)
                    : ImmutableList.of(),
                null,
                context,
                t);
        sem.release();
      } else if (t instanceof RuntimeException) {
        previousError = (RuntimeException) t;
        sem.release();
      }
    }

    @Override
    public void onCompleted() {
      if (last != null) {
        resultFuture.set(last);
      }
      sem.release();
    }

    private void ok(BidiWriteObjectResponse value) {
      last = value;
      first = false;
      sem.release();
    }

    private void clientDetectedError(StorageException storageException) {
      clientDetectedError = storageException;
      // yes, check that previousError is not the same instance as e
      if (previousError != null && previousError != storageException) {
        storageException.addSuppressed(previousError);
        previousError = null;
      }
      if (previousError == null) {
        previousError = storageException;
      }
      sem.release();
    }

    void await() {
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        } else {
          throw new RuntimeException(e);
        }
      }
      StorageException e = clientDetectedError;
      RuntimeException err = previousError;
      clientDetectedError = null;
      previousError = null;
      if ((e != null || err != null) && stream != null) {
        if (lastWrittenRequest.getFinishWrite()) {
          stream.onCompleted();
        }
      }
      if (e != null) {
        throw e;
      }
      if (err != null) {
        throw err;
      }
    }

    public void reset() {
      sem.drainPermits();
      last = null;
      clientDetectedError = null;
      previousError = null;
    }
  }

  /**
   * Prevent "already half-closed" if we previously called onComplete but then detect an error and
   * call onError
   */
  private static final class GracefulOutboundStream
      implements ApiStreamObserver<BidiWriteObjectRequest> {

    private final ApiStreamObserver<BidiWriteObjectRequest> delegate;
    private volatile boolean closing;

    private GracefulOutboundStream(ApiStreamObserver<BidiWriteObjectRequest> delegate) {
      this.delegate = delegate;
      this.closing = false;
    }

    @Override
    public void onNext(BidiWriteObjectRequest value) {
      delegate.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
      if (closing) {
        return;
      }
      closing = true;
      delegate.onError(t);
    }

    @Override
    public void onCompleted() {
      if (closing) {
        return;
      }
      closing = true;
      delegate.onCompleted();
    }
  }

  private final class RedirectHandlingResponseObserver
      implements ApiStreamObserver<BidiWriteObjectResponse> {
    private final BidiObserver delegate;

    private RedirectHandlingResponseObserver(BidiObserver delegate) {
      this.delegate = delegate;
    }

    @Override
    public void onNext(BidiWriteObjectResponse response) {
      redirectCounter.set(0);
      delegate.onNext(response);
    }

    @Override
    public void onError(Throwable t) {
      BidiWriteObjectRedirectedError error = GrpcUtils.getBidiWriteObjectRedirectedError(t);
      if (error == null) {
        delegate.onError(t);
        return;
      }
      redirecting = true;
      stream = null;
      int redirectCount = redirectCounter.incrementAndGet();
      if (redirectCount > maxRedirectsAllowed) {
        // attach the fact we're ignoring the redirect to the original exception as a suppressed
        // Exception. The lower level handler can then perform its usual handling, but if things
        // bubble all the way up to the invoker we'll be able to see it in a bug report.
        redirecting = false; // disable the special case that makes ABORTED retryable
        t.addSuppressed(new MaxRedirectsExceededException(maxRedirectsAllowed, redirectCount));
        delegate.onError(t);
        resultFuture.setException(t);
        return;
      }
      if (error.hasWriteHandle()) {
        bidiWriteHandle.set(error.getWriteHandle());
      }
      if (error.hasRoutingToken()) {
        routingToken.set(error.getRoutingToken());
      }
      if (error.hasGeneration()) {
        generation.set(error.getGeneration());
      }
      delegate.onError(t);
    }

    public void await() {
      delegate.await();
    }

    public void reset() {
      delegate.reset();
    }

    @Override
    public void onCompleted() {
      delegate.onCompleted();
    }
  }

  ReconnectArguments getReconnectArguments() {
    lock.lock();
    try {
      BidiWriteObjectRequest.Builder b = writeCtx.newRequestBuilder();

      AppendObjectSpec.Builder spec;
      if (b.hasAppendObjectSpec()) {
        spec = b.getAppendObjectSpec().toBuilder();
      } else {
        spec =
            AppendObjectSpec.newBuilder()
                .setBucket(b.getWriteObjectSpec().getResource().getBucket())
                .setObject(b.getWriteObjectSpec().getResource().getName());
      }

      // Reconnects always use AppendObjectSpec, never WriteObjectSpec
      b.clearWriteObjectSpec();

      String routingToken = this.routingToken.get();
      if (routingToken != null) {
        spec.setRoutingToken(routingToken);
      }

      long generation = this.generation.get();
      if (generation > 0) {
        spec.setGeneration(generation);
      } else {
        GetObjectRequest req =
            GetObjectRequest.newBuilder()
                .setBucket(spec.getBucket())
                .setObject(spec.getObject())
                .setReadMask(
                    FieldMask.newBuilder()
                        .addPaths(Storage.BlobField.GENERATION.getGrpcName())
                        .build())
                .build();
        boolean objectNotFound = false;
        try {
          retrier.run(
              () -> {
                this.generation.set(get.call(req).getGeneration());
                return null;
              },
              Decoder.identity());
        } catch (Throwable t) {
          if (t.getCause() instanceof NotFoundException) {
            objectNotFound = true;
          } else {
            t.addSuppressed(new AsyncStorageTaskException());
            throw t;
          }
        }
        generation = this.generation.get();
        if (generation > 0) {
          spec.setGeneration(generation);
        } else if (objectNotFound) {
          // If the object wasn't found, that means GCS never saw the initial WriteObjectSpec, which
          // means we'll need
          // to send it again. We can process this retry by just starting over again
          return ReconnectArguments.of(
              baseContextSupplier.get().withExtraHeaders(getHeaders()), null);
        }
      }

      BidiWriteHandle bidiWriteHandle = this.bidiWriteHandle.get();
      if (bidiWriteHandle != null) {
        spec.setWriteHandle(bidiWriteHandle);
      }

      b.setAppendObjectSpec(spec.build());
      b.setFlush(true).setStateLookup(true);

      return ReconnectArguments.of(
          baseContextSupplier.get().withExtraHeaders(getHeaders()), b.build());
    } finally {
      lock.unlock();
    }
  }

  static final class ReconnectArguments {
    private final GrpcCallContext ctx;
    private final BidiWriteObjectRequest req;

    private ReconnectArguments(GrpcCallContext ctx, BidiWriteObjectRequest req) {
      this.ctx = ctx;
      this.req = req;
    }

    public GrpcCallContext getCtx() {
      return ctx;
    }

    public BidiWriteObjectRequest getReq() {
      return req;
    }

    public static ReconnectArguments of(GrpcCallContext ctx, BidiWriteObjectRequest req) {
      return new ReconnectArguments(ctx, req);
    }
  }

  private Map<String, List<String>> getHeaders() {
    return ImmutableMap.of(
        "x-goog-request-params",
        ImmutableList.of(
            Stream.of(
                    "bucket=" + writeCtx.getRequestFactory().bucketName(),
                    "appendable=true",
                    this.routingToken.get() != null
                        ? "routing_token=" + this.routingToken.get()
                        : null)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("&"))));
  }
}
