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

import static com.google.cloud.storage.WriteFlushStrategy.contextWithBucketName;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GapicUnbufferedChunkedResumableWritableByteChannel
    implements UnbufferedWritableByteChannel {

  private final SettableApiFuture<WriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;
  private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;

  private final String bucketName;
  private final WriteCtx<ResumableWrite> writeCtx;
  private final RetryingDependencies deps;
  private final ResultRetryAlgorithm<?> alg;
  private final Supplier<GrpcCallContext> baseContextSupplier;
  private final LongConsumer sizeCallback;
  private final Consumer<WriteObjectResponse> completeCallback;

  private boolean open = true;
  private boolean finished = false;

  GapicUnbufferedChunkedResumableWritableByteChannel(
      SettableApiFuture<WriteObjectResponse> resultFuture,
      @NonNull ChunkSegmenter chunkSegmenter,
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      ResumableWrite requestFactory,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg,
      Supplier<GrpcCallContext> baseContextSupplier) {
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;
    this.write = write;
    this.bucketName = requestFactory.bucketName();
    this.writeCtx = new WriteCtx<>(requestFactory);
    this.deps = deps;
    this.alg = alg;
    this.baseContextSupplier = baseContextSupplier;
    this.sizeCallback = writeCtx.getConfirmedBytes()::set;
    this.completeCallback = resultFuture::set;
  }

  @Override
  public long write(ByteBuffer[] srcs, int srcsOffset, int srcsLength) throws IOException {
    return internalWrite(srcs, srcsOffset, srcsLength, false);
  }

  @Override
  public long writeAndClose(ByteBuffer[] srcs, int srcsOffset, int srcsLength) throws IOException {
    long write = internalWrite(srcs, srcsOffset, srcsLength, true);
    close();
    return write;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    if (open && !finished) {
      WriteObjectRequest message = finishMessage(true);
      try {
        flush(ImmutableList.of(message));
        finished = true;
      } catch (RuntimeException e) {
        resultFuture.setException(e);
        throw e;
      }
    }
    open = false;
  }

  private long internalWrite(ByteBuffer[] srcs, int srcsOffset, int srcsLength, boolean finalize)
      throws ClosedChannelException {
    if (!open) {
      throw new ClosedChannelException();
    }

    ChunkSegment[] data = chunkSegmenter.segmentBuffers(srcs, srcsOffset, srcsLength);

    List<WriteObjectRequest> messages = new ArrayList<>();

    boolean first = true;
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
      WriteObjectRequest.Builder builder =
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

      WriteObjectRequest build = possiblyPairDownRequest(builder, first).build();
      first = false;
      messages.add(build);
      bytesConsumed += contentSize;
    }
    if (finalize && !finished) {
      messages.add(finishMessage(first));
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
  private WriteObjectRequest finishMessage(boolean first) {
    long offset = writeCtx.getTotalSentBytes().get();
    Crc32cLengthKnown crc32cValue = writeCtx.getCumulativeCrc32c().get();

    WriteObjectRequest.Builder b =
        writeCtx.newRequestBuilder().setFinishWrite(true).setWriteOffset(offset);
    if (crc32cValue != null) {
      b.setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
    }
    WriteObjectRequest message = possiblyPairDownRequest(b, first).build();
    return message;
  }

  private void flush(@NonNull List<WriteObjectRequest> segments) {
    GrpcCallContext internalContext = contextWithBucketName(bucketName, baseContextSupplier.get());
    ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> callable =
        write.withDefaultCallContext(internalContext);

    Retrying.run(
        deps,
        alg,
        () -> {
          Observer observer = new Observer(sizeCallback, completeCallback);
          ApiStreamObserver<WriteObjectRequest> write = callable.clientStreamingCall(observer);

          for (WriteObjectRequest message : segments) {
            write.onNext(message);
          }
          write.onCompleted();
          observer.await();
          return null;
        },
        Decoder.identity());
  }

  /**
   * Several fields of a WriteObjectRequest are only allowed on the "first" message sent to gcs,
   * this utility method centralizes the logic necessary to clear those fields for use by subsequent
   * messages.
   */
  private static WriteObjectRequest.Builder possiblyPairDownRequest(
      WriteObjectRequest.Builder b, boolean firstMessageOfStream) {
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

  @VisibleForTesting
  WriteCtx<?> getWriteCtx() {
    return writeCtx;
  }

  static class Observer implements ApiStreamObserver<WriteObjectResponse> {

    private final LongConsumer sizeCallback;
    private final Consumer<WriteObjectResponse> completeCallback;

    private final SettableApiFuture<Void> invocationHandle;
    private volatile WriteObjectResponse last;

    Observer(LongConsumer sizeCallback, Consumer<WriteObjectResponse> completeCallback) {
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

    void await() {
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
