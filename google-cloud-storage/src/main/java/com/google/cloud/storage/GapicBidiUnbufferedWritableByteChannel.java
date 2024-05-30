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

import static com.google.cloud.storage.GrpcUtils.contextWithBucketName;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.ObjectChecksums;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GapicBidiUnbufferedWritableByteChannel implements UnbufferedWritableByteChannel {
  private final BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write;
  private final RetryingDependencies deps;
  private final ResultRetryAlgorithm<?> alg;
  private final String bucketName;
  private final Supplier<GrpcCallContext> baseContextSupplier;
  private final SettableApiFuture<BidiWriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;

  private final BidiWriteCtx<BidiResumableWrite> writeCtx;
  private final BidiObserver responseObserver;

  private volatile ApiStreamObserver<BidiWriteObjectRequest> stream;
  private boolean open = true;
  private boolean first = true;
  private boolean finished = false;

  GapicBidiUnbufferedWritableByteChannel(
      BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg,
      SettableApiFuture<BidiWriteObjectResponse> resultFuture,
      ChunkSegmenter chunkSegmenter,
      BidiWriteCtx<BidiResumableWrite> writeCtx,
      Supplier<GrpcCallContext> baseContextSupplier) {
    this.write = write;
    this.deps = deps;
    this.alg = alg;
    this.baseContextSupplier = baseContextSupplier;
    this.bucketName = writeCtx.getRequestFactory().bucketName();
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;

    this.writeCtx = writeCtx;
    this.responseObserver = new BidiObserver();
  }

  @Override
  public long write(ByteBuffer[] srcs, int srcsOffset, int srcsLength) throws IOException {
    return internalWrite(srcs, srcsOffset, srcsLength, false);
  }

  @Override
  public long writeAndClose(ByteBuffer[] srcs, int offset, int length) throws IOException {
    long written = internalWrite(srcs, offset, length, true);
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
    ApiStreamObserver<BidiWriteObjectRequest> openedStream = openedStream();
    if (!finished) {
      BidiWriteObjectRequest message = finishMessage();
      try {
        openedStream.onNext(message);
        finished = true;
        openedStream.onCompleted();
      } catch (RuntimeException e) {
        resultFuture.setException(e);
        throw e;
      }
    } else {
      openedStream.onCompleted();
    }
    responseObserver.await();
    open = false;
  }

  @VisibleForTesting
  BidiWriteCtx<BidiResumableWrite> getWriteCtx() {
    return writeCtx;
  }

  private long internalWrite(ByteBuffer[] srcs, int srcsOffset, int srcsLength, boolean finalize)
      throws ClosedChannelException {
    if (!open) {
      throw new ClosedChannelException();
    }

    ChunkSegment[] data = chunkSegmenter.segmentBuffers(srcs, srcsOffset, srcsLength);

    List<BidiWriteObjectRequest> messages = new ArrayList<>();

    int bytesConsumed = 0;
    for (ChunkSegment datum : data) {
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
      BidiWriteObjectRequest.Builder builder =
          writeCtx
              .newRequestBuilder()
              .setWriteOffset(offset)
              .setChecksummedData(checksummedData.build());
      if (!datum.isOnlyFullBlocks()) {
        builder.setFinishWrite(true);
        if (cumulative != null) {
          builder.setObjectChecksums(
              ObjectChecksums.newBuilder().setCrc32C(cumulative.getValue()).build());
        }
        finished = true;
      }

      BidiWriteObjectRequest build = possiblyPairDownBidiRequest(builder, first).build();
      first = false;
      messages.add(build);
      bytesConsumed += contentSize;
    }
    if (finalize && !finished) {
      messages.add(finishMessage());
      finished = true;
    }

    try {
      flush(messages);
    } catch (RuntimeException e) {
      resultFuture.setException(e);
      throw e;
    }

    return bytesConsumed;
  }

  @NonNull
  private BidiWriteObjectRequest finishMessage() {
    long offset = writeCtx.getTotalSentBytes().get();
    Crc32cLengthKnown crc32cValue = writeCtx.getCumulativeCrc32c().get();

    BidiWriteObjectRequest.Builder b =
        writeCtx.newRequestBuilder().setFinishWrite(true).setWriteOffset(offset);
    if (crc32cValue != null) {
      b.setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
    }
    BidiWriteObjectRequest message = b.build();
    return message;
  }

  private ApiStreamObserver<BidiWriteObjectRequest> openedStream() {
    if (stream == null) {
      synchronized (this) {
        if (stream == null) {
          GrpcCallContext internalContext =
              contextWithBucketName(bucketName, baseContextSupplier.get());
          stream =
              this.write
                  .withDefaultCallContext(internalContext)
                  .bidiStreamingCall(responseObserver);
          responseObserver.sem.drainPermits();
        }
      }
    }
    return stream;
  }

  private void flush(@NonNull List<BidiWriteObjectRequest> segments) {
    Retrying.run(
        deps,
        alg,
        () -> {
          try {
            ApiStreamObserver<BidiWriteObjectRequest> opened = openedStream();
            for (BidiWriteObjectRequest message : segments) {
              opened.onNext(message);
            }
            if (!finished) {
              BidiWriteObjectRequest message =
                  BidiWriteObjectRequest.newBuilder().setFlush(true).setStateLookup(true).build();
              opened.onNext(message);
            }
            responseObserver.await();
            return null;
          } catch (Exception e) {
            stream = null;
            first = true;
            throw e;
          }
        },
        Decoder.identity());
  }

  private static BidiWriteObjectRequest.Builder possiblyPairDownBidiRequest(
      BidiWriteObjectRequest.Builder b, boolean firstMessageOfStream) {
    if (firstMessageOfStream && b.getWriteOffset() == 0) {
      return b;
    }

    if (!firstMessageOfStream) {
      b.clearUploadId();
    }

    if (b.getWriteOffset() > 0) {
      b.clearWriteObjectSpec();
    }

    if (b.getWriteOffset() > 0 && !b.getFinishWrite()) {
      b.clearObjectChecksums();
    }
    return b;
  }

  private class BidiObserver implements ApiStreamObserver<BidiWriteObjectResponse> {

    private final Semaphore sem;
    private volatile BidiWriteObjectResponse last;
    private volatile RuntimeException previousError;

    private BidiObserver() {
      this.sem = new Semaphore(0);
    }

    @Override
    public void onNext(BidiWriteObjectResponse value) {
      // incremental update
      if (value.hasPersistedSize()) {
        writeCtx.getConfirmedBytes().set((value.getPersistedSize()));
      } else if (value.hasResource()) {
        writeCtx.getConfirmedBytes().set(value.getResource().getSize());
      }
      sem.release();
      last = value;
    }

    @Override
    public void onError(Throwable t) {
      if (t instanceof RuntimeException) {
        previousError = (RuntimeException) t;
      }
      sem.release();
    }

    @Override
    public void onCompleted() {
      if (last != null && last.hasResource()) {
        resultFuture.set(last);
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
      RuntimeException err = previousError;
      if (err != null) {
        previousError = null;
        throw err;
      }
    }
  }
}
