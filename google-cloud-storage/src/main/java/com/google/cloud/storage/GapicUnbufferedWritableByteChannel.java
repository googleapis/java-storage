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
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.WriteCtx.WriteObjectRequestBuilderFactory;
import com.google.cloud.storage.WriteFlushStrategy.Flusher;
import com.google.cloud.storage.WriteFlushStrategy.FlusherFactory;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.ServiceConstants.Values;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;

final class GapicUnbufferedWritableByteChannel<
        RequestFactoryT extends WriteObjectRequestBuilderFactory>
    implements UnbufferedWritableByteChannel {

  private final SettableApiFuture<WriteObjectResponse> resultFuture;
  private final ByteStringStrategy byteStringStrategy;
  private final Hasher hasher;
  private final WriteCtx<RequestFactoryT> writeCtx;
  private final int perMessageLimit;

  private final Flusher flusher;

  private boolean open = true;
  private boolean finished = false;

  GapicUnbufferedWritableByteChannel(
      SettableApiFuture<WriteObjectResponse> resultFuture,
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      RequestFactoryT requestFactory,
      ByteStringStrategy byteStringStrategy,
      Hasher hasher,
      FlusherFactory flusher) {
    this.resultFuture = resultFuture;
    this.byteStringStrategy = byteStringStrategy;
    this.hasher = hasher;

    this.writeCtx = new WriteCtx<>(requestFactory);
    this.perMessageLimit = Values.MAX_WRITE_CHUNK_BYTES_VALUE;
    this.flusher =
        flusher.newFlusher(write, writeCtx.getConfirmedBytes()::addAndGet, resultFuture::set);
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    return Math.toIntExact(write(new ByteBuffer[] {src}));
  }

  @Override
  public long write(ByteBuffer[] srcs) throws IOException {
    return write(srcs, 0, srcs.length);
  }

  @Override
  public long write(ByteBuffer[] srcs, int srcsOffset, int srcLength) throws IOException {
    if (!open) {
      throw new ClosedChannelException();
    }

    ChunkSegment[] data =
        ChunkSegmenter.segmentBuffers(
            srcs, hasher, byteStringStrategy, perMessageLimit, srcsOffset, srcLength);

    List<WriteObjectRequest> messages = new ArrayList<>();

    int bytesConsumed = 0;
    for (ChunkSegment datum : data) {
      Crc32cLengthKnown crc32c = datum.getCrc32c();
      ByteString b = datum.getB();
      int contentSize = b.size();
      long offset = writeCtx.getTotalSentBytes().getAndAdd(contentSize);
      ChecksummedData.Builder checksummedData = ChecksummedData.newBuilder().setContent(b);
      if (crc32c != null) {
        writeCtx.getCumulativeCrc32c().getAndAccumulate(crc32c, Crc32cValue::nullSafeConcat);
        checksummedData.setCrc32C(crc32c.getValue());
      }
      WriteObjectRequest.Builder builder =
          writeCtx
              .newRequestBuilder()
              .setWriteOffset(offset)
              .setChecksummedData(checksummedData.build());
      if (contentSize < perMessageLimit) {
        builder.setFinishWrite(true);
        if (crc32c != null) {
          builder.setObjectChecksums(
              ObjectChecksums.newBuilder()
                  .setCrc32C(writeCtx.getCumulativeCrc32c().get().getValue())
                  .build());
        }
        finished = true;
      }

      WriteObjectRequest build = builder.build();
      messages.add(build);
      bytesConsumed += contentSize;
    }

    try {
      flusher.flush(messages);
    } catch (RuntimeException e) {
      resultFuture.setException(e);
      throw e;
    }

    return bytesConsumed;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    if (!finished) {
      long offset = writeCtx.getTotalSentBytes().get();
      Crc32cLengthKnown crc32cValue = writeCtx.getCumulativeCrc32c().get();

      WriteObjectRequest.Builder b =
          writeCtx.newRequestBuilder().setFinishWrite(true).setWriteOffset(offset);
      if (crc32cValue != null) {
        b.setObjectChecksums(
            ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
      }
      WriteObjectRequest message = b.build();
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
}
