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

import static com.google.cloud.storage.GrpcUtils.contextWithBucketName;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
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
import org.checkerframework.checker.nullness.qual.NonNull;

final class GapicUnbufferedFinalizeOnCloseResumableWritableByteChannel
    implements UnbufferedWritableByteChannel {

  private final SettableApiFuture<WriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;
  private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;

  private final WriteCtx<ResumableWrite> writeCtx;

  private final Observer responseObserver;
  private volatile ApiStreamObserver<WriteObjectRequest> stream;

  private boolean open = true;
  private boolean first = true;
  private boolean finished = false;

  GapicUnbufferedFinalizeOnCloseResumableWritableByteChannel(
      SettableApiFuture<WriteObjectResponse> resultFuture,
      ChunkSegmenter chunkSegmenter,
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      WriteCtx<ResumableWrite> writeCtx) {
    String bucketName = writeCtx.getRequestFactory().bucketName();
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;

    GrpcCallContext internalContext =
        contextWithBucketName(bucketName, GrpcCallContext.createDefault());
    this.write = write.withDefaultCallContext(internalContext);

    this.writeCtx = writeCtx;
    this.responseObserver = new Observer();
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
    ApiStreamObserver<WriteObjectRequest> openedStream = openedStream();
    if (!finished) {
      WriteObjectRequest message = finishMessage();
      try {
        openedStream.onNext(message);
        openedStream.onCompleted();
        finished = true;
      } catch (RuntimeException e) {
        resultFuture.setException(e);
        throw e;
      }
    } else {
      try {
        openedStream.onCompleted();
      } catch (RuntimeException e) {
        resultFuture.setException(e);
        throw e;
      }
    }
    open = false;
    responseObserver.await();
  }

  private long internalWrite(ByteBuffer[] srcs, int srcsOffset, int srcsLength, boolean finalize)
      throws ClosedChannelException {
    if (!open) {
      throw new ClosedChannelException();
    }

    ChunkSegment[] data = chunkSegmenter.segmentBuffers(srcs, srcsOffset, srcsLength);
    if (data.length == 0) {
      return 0;
    }

    List<WriteObjectRequest> messages = new ArrayList<>();

    ApiStreamObserver<WriteObjectRequest> openedStream = openedStream();
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
      WriteObjectRequest.Builder builder = writeCtx.newRequestBuilder();
      if (!first) {
        builder.clearUploadId();
        builder.clearWriteObjectSpec();
        builder.clearObjectChecksums();
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

      WriteObjectRequest build = builder.build();
      first = false;
      messages.add(build);
      bytesConsumed += contentSize;
    }
    if (finalize && !finished) {
      messages.add(finishMessage());
      finished = true;
    }

    try {
      for (WriteObjectRequest message : messages) {
        openedStream.onNext(message);
      }
    } catch (RuntimeException e) {
      resultFuture.setException(e);
      throw e;
    }

    return bytesConsumed;
  }

  @NonNull
  private WriteObjectRequest finishMessage() {
    long offset = writeCtx.getTotalSentBytes().get();
    Crc32cLengthKnown crc32cValue = writeCtx.getCumulativeCrc32c().get();

    WriteObjectRequest.Builder b =
        writeCtx.newRequestBuilder().setFinishWrite(true).setWriteOffset(offset);
    if (crc32cValue != null) {
      b.setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
    }
    WriteObjectRequest message = b.build();
    return message;
  }

  private ApiStreamObserver<WriteObjectRequest> openedStream() {
    if (stream == null) {
      synchronized (this) {
        if (stream == null) {
          stream = write.clientStreamingCall(responseObserver);
        }
      }
    }
    return stream;
  }

  class Observer implements ApiStreamObserver<WriteObjectResponse> {

    private final SettableApiFuture<Void> invocationHandle;
    private volatile WriteObjectResponse last;

    Observer() {
      this.invocationHandle = SettableApiFuture.create();
    }

    @Override
    public void onNext(WriteObjectResponse value) {
      // incremental update
      if (value.hasPersistedSize()) {
        writeCtx.getConfirmedBytes().set(value.getPersistedSize());
      } else if (value.hasResource()) {
        writeCtx.getConfirmedBytes().set(value.getResource().getSize());
      }
      last = value;
    }

    @Override
    public void onError(Throwable t) {
      invocationHandle.setException(t);
    }

    @Override
    public void onCompleted() {
      if (last != null && last.hasResource()) {
        resultFuture.set(last);
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
