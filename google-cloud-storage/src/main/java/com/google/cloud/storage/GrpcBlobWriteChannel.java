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

import static com.google.cloud.storage.Utils.todo;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.StorageByteChannels.BufferedWritableByteChannel;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

final class GrpcBlobWriteChannel implements WriteChannel {

  private final LazyWriteChannel lazyWriteChannel;

  private int chunkSize = 16 * 1024 * 1024;

  GrpcBlobWriteChannel(
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
      Supplier<ApiFuture<ResumableWrite>> start) {
    lazyWriteChannel =
        new LazyWriteChannel(
            Suppliers.memoize(
                () ->
                    ResumableMedia.gapic()
                        .write()
                        .byteChannel(write)
                        .setHasher(Hasher.noop())
                        .setByteStringStrategy(ByteStringStrategy.copy())
                        .buffered(ByteBuffer.allocate(chunkSize))
                        .setStartAsync(start.get())
                        .build()));
  }

  @Override
  public void setChunkSize(int chunkSize) {
    // TODO: push value to next 256KiB boundary
    Preconditions.checkState(!isOpen(), "Unable to change chunkSize after write");
    this.chunkSize = chunkSize;
  }

  @Override
  public RestorableState<WriteChannel> capture() {
    return todo();
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    return lazyWriteChannel.getChannel().write(src);
  }

  @Override
  public boolean isOpen() {
    return lazyWriteChannel.getChannel().isOpen();
  }

  @Override
  public void close() throws IOException {
    if (isOpen()) {
      lazyWriteChannel.getChannel().close();
    }
  }

  ApiFuture<WriteObjectResponse> getResults() {
    return lazyWriteChannel.session.get().getResult();
  }

  private static final class LazyWriteChannel {
    private final Supplier<
            StorageByteChannels.Sessions.BufferedWritableByteChannelSession<WriteObjectResponse>>
        session;
    private final Supplier<BufferedWritableByteChannel> channel;

    public LazyWriteChannel(
        Supplier<
                StorageByteChannels.Sessions.BufferedWritableByteChannelSession<
                    WriteObjectResponse>>
            session) {
      this.session = session;
      this.channel = Suppliers.memoize(() -> session.get().open());
    }

    public BufferedWritableByteChannel getChannel() {
      return channel.get();
    }
  }
}
