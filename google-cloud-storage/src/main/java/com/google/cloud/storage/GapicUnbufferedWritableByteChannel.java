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
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.collect.ImmutableList;
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
import java.util.concurrent.ExecutionException;

final class GapicUnbufferedWritableByteChannel implements UnbufferedWritableByteChannel {

  private final String uploadId;

  private final SettableApiFuture<WriteObjectResponse> resultFuture;
  private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;
  private final ByteStringStrategy byteStringStrategy;
  private final Hasher hasher;
  private final ServerState serverState;
  private final int perMessageLimit;

  private boolean open = true;
  private boolean finished = false;

  GapicUnbufferedWritableByteChannel(
      SettableApiFuture<WriteObjectResponse> resultFuture,
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      ResumableWrite resumableWrite,
      ByteStringStrategy byteStringStrategy,
      Hasher hasher) {
    this.uploadId = resumableWrite.getRes().getUploadId();
    this.resultFuture = resultFuture;
    this.write = write; // todo: transform to retry once if request is non-idempotent
    this.byteStringStrategy = byteStringStrategy;
    this.serverState = new ServerState(resumableWrite);
    this.hasher = hasher;
    this.perMessageLimit = Values.MAX_WRITE_CHUNK_BYTES_VALUE;
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
      long offset = serverState.getTotalSentBytes().getAndAdd(contentSize);
      ChecksummedData.Builder checksummedData = ChecksummedData.newBuilder().setContent(b);
      if (crc32c != null) {
        serverState.getCumulativeCrc32c().getAndAccumulate(crc32c, Crc32cValue::concat);
        checksummedData.setCrc32C(crc32c.getValue());
      }
      WriteObjectRequest.Builder builder =
          WriteObjectRequest.newBuilder()
              .setUploadId(uploadId)
              .setWriteOffset(offset)
              .setChecksummedData(checksummedData.build());
      if (contentSize < perMessageLimit) {
        builder.setFinishWrite(true);
        if (crc32c != null) {
          builder.setObjectChecksums(
              ObjectChecksums.newBuilder()
                  .setCrc32C(serverState.getCumulativeCrc32c().get().getValue())
                  .build());
        }
        finished = true;
      }

      WriteObjectRequest build = builder.build();
      messages.add(build);
      bytesConsumed += contentSize;
    }

    sendMessages(messages);

    return bytesConsumed;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    if (!finished) {
      long offset = serverState.getTotalSentBytes().get();
      WriteObjectRequest.Builder b =
          WriteObjectRequest.newBuilder()
              .setUploadId(uploadId)
              .setFinishWrite(true)
              .setWriteOffset(offset);
      Crc32cLengthKnown crc32cValue = serverState.getCumulativeCrc32c().get();
      if (crc32cValue != null) {
        b.setObjectChecksums(
            ObjectChecksums.newBuilder().setCrc32C(crc32cValue.getValue()).build());
      }
      WriteObjectRequest message = b.build();
      finished = true;
      sendMessages(ImmutableList.of(message));
    }
    open = false;
  }

  private void sendMessages(List<WriteObjectRequest> messages) {

    Observer observer = new Observer();
    ApiStreamObserver<WriteObjectRequest> write = this.write.clientStreamingCall(observer);

    messages.forEach(write::onNext);
    write.onCompleted();
    try {
      observer.invocationHandle.get();
    } catch (InterruptedException | ExecutionException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  private class Observer implements ApiStreamObserver<WriteObjectResponse> {

    private final SettableApiFuture<Void> invocationHandle;

    private Observer() {
      invocationHandle = SettableApiFuture.create();
    }

    @Override
    public void onNext(WriteObjectResponse value) {
      // incremental update
      if (value.hasPersistedSize()) {
        serverState.getConfirmedBytes().addAndGet(value.getPersistedSize());
      } else if (value.hasResource()) {
        // testbench_seems to return this even on finish
      } else {
        // serverState.confirmedBytes.set(serverState.totalSentBytes.get());
      }
      if (finished) {
        resultFuture.set(value);
      }
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
      // TODO: is retryable?
      invocationHandle.setException(t);
    }

    @Override
    public void onCompleted() {
      invocationHandle.set(null);
    }
  }
}
