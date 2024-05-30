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
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.OutOfRangeException;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.ObjectChecksums;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GapicBidiUnbufferedWritableByteChannel implements UnbufferedWritableByteChannel {
  private final BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write;
  private final RetryingDependencies deps;
  private final ResultRetryAlgorithm<?> alg;
  private final SettableApiFuture<BidiWriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;

  private final BidiWriteCtx<BidiResumableWrite> writeCtx;
  private final GrpcCallContext context;
  private final BidiObserver responseObserver;

  private volatile ApiStreamObserver<BidiWriteObjectRequest> stream;
  private boolean open = true;
  private boolean first = true;
  private boolean finished = false;
  private volatile BidiWriteObjectRequest lastWrittenRequest;

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
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;

    this.writeCtx = writeCtx;
    this.responseObserver = new BidiObserver();
    String bucketName = writeCtx.getRequestFactory().bucketName();
    this.context = contextWithBucketName(bucketName, baseContextSupplier.get());
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
    try {
      if (!finished) {
        BidiWriteObjectRequest message = finishMessage();
        lastWrittenRequest = message;
        flush(Collections.singletonList(message));
      } else {
        if (stream != null) {
          stream.onCompleted();
        }
      }
      responseObserver.await();
    } finally {
      open = false;
      stream = null;
      lastWrittenRequest = null;
    }
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
      } else {
        first = false;
      }
      builder.setWriteOffset(offset).setChecksummedData(checksummedData.build());
      if (!datum.isOnlyFullBlocks()) {
        builder.setFinishWrite(true);
        if (cumulative != null) {
          builder.setObjectChecksums(
              ObjectChecksums.newBuilder().setCrc32C(cumulative.getValue()).build());
        }
        finished = true;
      }

      if (i == data.length - 1 && !finished) {
        builder.setFlush(true).setStateLookup(true);
      }

      BidiWriteObjectRequest build = builder.build();
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
      open = false;
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
          stream = this.write.bidiStreamingCall(responseObserver, context);
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
              lastWrittenRequest = message;
            }
            if (lastWrittenRequest.getFinishWrite()) {
              opened.onCompleted();
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

  private class BidiObserver implements ApiStreamObserver<BidiWriteObjectResponse> {

    private final Semaphore sem;
    private volatile BidiWriteObjectResponse last;
    private volatile RuntimeException previousError;

    private BidiObserver() {
      this.sem = new Semaphore(0);
    }

    @Override
    public void onNext(BidiWriteObjectResponse value) {
      boolean finalizing = lastWrittenRequest.getFinishWrite();
      if (!finalizing && value.hasPersistedSize()) { // incremental
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long persistedSize = value.getPersistedSize();

        if (totalSentBytes == persistedSize) {
          writeCtx.getConfirmedBytes().set(persistedSize);
          last = value;
          sem.release();
          // } else if (persistedSize < totalSentBytes) {
          //   long delta = totalSentBytes - persistedSize;
          //   // rewind our content and any state that my have run ahead of the actual ack'd bytes
          //   content.rewindTo(delta);
          //   writeCtx.getTotalSentBytes().set(persistedSize);
          //   writeCtx.getConfirmedBytes().set(persistedSize);
        } else {
          onError(
              ResumableSessionFailureScenario.SCENARIO_7.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else if (finalizing && value.hasResource()) {
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long finalSize = value.getResource().getSize();
        if (totalSentBytes == finalSize) {
          writeCtx.getConfirmedBytes().set(finalSize);
          last = value;
          sem.release();
        } else if (finalSize < totalSentBytes) {
          onError(
              ResumableSessionFailureScenario.SCENARIO_4_1.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        } else {
          onError(
              ResumableSessionFailureScenario.SCENARIO_4_2.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else if (!finalizing && value.hasResource()) {
        onError(
            ResumableSessionFailureScenario.SCENARIO_1.toStorageException(
                ImmutableList.of(lastWrittenRequest), value, context, null));
      } else if (finalizing && value.hasPersistedSize()) {
        long totalSentBytes = writeCtx.getTotalSentBytes().get();
        long persistedSize = value.getPersistedSize();
        if (persistedSize < totalSentBytes) {
          onError(
              ResumableSessionFailureScenario.SCENARIO_3.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        } else {
          onError(
              ResumableSessionFailureScenario.SCENARIO_2.toStorageException(
                  ImmutableList.of(lastWrittenRequest), value, context, null));
        }
      } else {
        onError(
            ResumableSessionFailureScenario.SCENARIO_0.toStorageException(
                ImmutableList.of(lastWrittenRequest), value, context, null));
      }
    }

    @Override
    public void onError(Throwable t) {
      if (t instanceof OutOfRangeException) {
        OutOfRangeException oore = (OutOfRangeException) t;
        open = false;
        previousError =
            ResumableSessionFailureScenario.SCENARIO_5.toStorageException(
                ImmutableList.of(lastWrittenRequest), null, context, oore);
      } else if (t instanceof ApiException) {
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
                ImmutableList.of(lastWrittenRequest),
                null,
                context,
                t);
      } else if (t instanceof RuntimeException) {
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
