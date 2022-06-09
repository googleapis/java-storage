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
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.storage.StorageByteChannels.BufferedReadableByteChannel;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

final class GrpcBlobReadChannel implements ReadChannel {

  private final LazyReadChannel lazyReadChannel;

  private int chunkSize = 16 * 1024 * 1024;

  GrpcBlobReadChannel(
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read, Supplier<Object> start) {
    this.lazyReadChannel =
        new LazyReadChannel(
            Suppliers.memoize(
                () ->
                    ResumableMedia.gapic()
                        .read()
                        .byteChannel(read)
                        .setHasher(Hasher.noop())
                        .buffered(ByteBuffer.allocate(chunkSize))
                        .setObject(start.get())
                        .build()));
  }

  @Override
  public void setChunkSize(int chunkSize) {
    Preconditions.checkState(!isOpen(), "Unable to change chunkSize after write");
    this.chunkSize = chunkSize;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public void close() {
    if (isOpen()) {
      try {
        lazyReadChannel.getChannel().close();
      } catch (IOException e) {
        // TODO: why does ReadChannel remove IOException?!
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void seek(long position) throws IOException {
    todo();
  }

  @Override
  public RestorableState<ReadChannel> capture() {
    return todo();
  }

  @Override
  public ReadChannel limit(long limit) {
    return todo();
  }

  @Override
  public long limit() {
    return todo();
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    return lazyReadChannel.getChannel().read(dst);
  }

  ApiFuture<Object> getResults() {
    return lazyReadChannel.session.get().getResult();
  }

  private static final class LazyReadChannel {
    private final Supplier<StorageByteChannels.Sessions.BufferedReadableByteChannelSession<Object>>
        session;
    private final Supplier<BufferedReadableByteChannel> channel;

    public LazyReadChannel(
        Supplier<StorageByteChannels.Sessions.BufferedReadableByteChannelSession<Object>> session) {
      this.session = session;
      this.channel = Suppliers.memoize(() -> session.get().open());
    }

    public BufferedReadableByteChannel getChannel() {
      return channel.get();
    }
  }
}
