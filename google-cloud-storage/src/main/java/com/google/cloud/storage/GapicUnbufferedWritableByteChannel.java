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
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.WriteCtx.WriteObjectRequestBuilderFactory;
import com.google.cloud.storage.WriteFlushStrategy.Flusher;
import com.google.cloud.storage.WriteFlushStrategy.FlusherFactory;
import com.google.common.annotations.VisibleForTesting;
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
import org.checkerframework.checker.nullness.qual.NonNull;

final class GapicUnbufferedWritableByteChannel<
        RequestFactoryT extends WriteObjectRequestBuilderFactory>
    implements UnbufferedWritableByteChannel {

  private final SettableApiFuture<WriteObjectResponse> resultFuture;
  private final ChunkSegmenter chunkSegmenter;

  private final WriteCtx<RequestFactoryT> writeCtx;
  private final Flusher flusher;

  private boolean open = true;
  private boolean finished = false;

  GapicUnbufferedWritableByteChannel(
      SettableApiFuture<WriteObjectResponse> resultFuture,
      ChunkSegmenter chunkSegmenter,
      RequestFactoryT requestFactory,
      FlusherFactory flusherFactory) {
    this.resultFuture = resultFuture;
    this.chunkSegmenter = chunkSegmenter;

    this.writeCtx = new WriteCtx<>(requestFactory);
    this.flusher =
        flusherFactory.newFlusher(
            requestFactory.bucketName(), writeCtx.getConfirmedBytes()::set, resultFuture::set);
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
    if (!finished) {
      WriteObjectRequest message = finishMessage();
      try {
        flusher.close(message);
        finished = true;
      } catch (RuntimeException e) {
        resultFuture.setException(e);
        throw e;
      }
    } else {
      flusher.close(null);
    }
    open = false;
  }

  @VisibleForTesting
  WriteCtx<RequestFactoryT> getWriteCtx() {
    return writeCtx;
  }

  private long internalWrite(ByteBuffer[] srcs, int srcsOffset, int srcsLength, boolean finalize)
      throws ClosedChannelException {
    if (!open) {
      throw new ClosedChannelException();
    }

    ChunkSegment[] data = chunkSegmenter.segmentBuffers(srcs, srcsOffset, srcsLength);

    List<WriteObjectRequest> messages = new ArrayList<>();

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

      WriteObjectRequest build = builder.build();
      messages.add(build);
      bytesConsumed += contentSize;
    }
    if (finalize && !finished) {
      messages.add(finishMessage());
      finished = true;
    }

    try {
      flusher.flush(messages);
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
}
