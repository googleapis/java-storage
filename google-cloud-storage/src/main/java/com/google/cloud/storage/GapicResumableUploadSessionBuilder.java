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

import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;

final class GapicResumableUploadSessionBuilder {

  private GapicResumableUploadSessionBuilder() {}

  public static GapicResumableUploadSessionBuilder create() {
    return new GapicResumableUploadSessionBuilder();
  }

  public WritableByteChannelSessionBuilder byteChannel(
      ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write) {
    return new WritableByteChannelSessionBuilder(write);
  }

  public static final class WritableByteChannelSessionBuilder {

    private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write;
    private ByteStringStrategy byteStringStrategy;
    private Hasher hasher;

    private WritableByteChannelSessionBuilder(
        ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write) {
      this.write = write;
      this.byteStringStrategy = ByteStringStrategy.noCopy();
      this.hasher = Hasher.noop();
    }

    public BufferedWritableByteChannelSessionBuilder buffered() {
      return buffered(ByteBuffer.allocate(16 * 1024 * 1024));
    }

    public WritableByteChannelSessionBuilder setByteStringStrategy(
        ByteStringStrategy byteStringStrategy) {
      this.byteStringStrategy =
          requireNonNull(byteStringStrategy, "checksumSupport must be non null");
      return this;
    }

    public WritableByteChannelSessionBuilder setHasher(Hasher hasher) {
      this.hasher = hasher;
      return this;
    }

    public BufferedWritableByteChannelSessionBuilder buffered(ByteBuffer buffer) {
      return new BufferedWritableByteChannelSessionBuilder(
          buffer, getF(write, byteStringStrategy, hasher));
    }

    public UnbufferedWritableByteChannelSessionBuilder unbuffered() {
      return new UnbufferedWritableByteChannelSessionBuilder(
          getF(write, byteStringStrategy, hasher));
    }

    private static BiFunction<
            ResumableWrite, SettableApiFuture<WriteObjectResponse>, UnbufferedWritableByteChannel>
        getF(
            ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write,
            ByteStringStrategy byteStringStrategy,
            Hasher hasher) {
      return (start, resultFuture) ->
          new GapicUnbufferedWritableByteChannel(
              resultFuture, write, start, byteStringStrategy, hasher);
    }

    public static final class BufferedWritableByteChannelSessionBuilder {

      private BiFunction<
              ResumableWrite, SettableApiFuture<WriteObjectResponse>, BufferedWritableByteChannel>
          f;
      private ApiFuture<ResumableWrite> uploadIdFuture;

      private BufferedWritableByteChannelSessionBuilder(
          ByteBuffer buffer,
          BiFunction<
                  ResumableWrite,
                  SettableApiFuture<WriteObjectResponse>,
                  UnbufferedWritableByteChannel>
              f) {
        this.f = f.andThen(c -> new DefaultBufferedWritableByteChannel(buffer, c));
      }

      public BufferedWritableByteChannelSessionBuilder setStartAsync(
          ApiFuture<ResumableWrite> uploadIdFuture) {
        this.uploadIdFuture = uploadIdFuture;
        return this;
      }

      public BufferedWritableByteChannelSession<WriteObjectResponse> build() {
        return new ChannelSession.BufferedWriteSession<>(
            requireNonNull(uploadIdFuture, "uploadIdFuture must be non null"),
            f.andThen(StorageByteChannels.writable()::createSynchronized));
      }
    }

    public static final class UnbufferedWritableByteChannelSessionBuilder {

      private BiFunction<
              ResumableWrite, SettableApiFuture<WriteObjectResponse>, UnbufferedWritableByteChannel>
          f;
      private ApiFuture<ResumableWrite> uploadIdFuture;

      private UnbufferedWritableByteChannelSessionBuilder(
          BiFunction<
                  ResumableWrite,
                  SettableApiFuture<WriteObjectResponse>,
                  UnbufferedWritableByteChannel>
              f) {
        this.f = f;
      }

      public UnbufferedWritableByteChannelSessionBuilder setStartAsync(
          ApiFuture<ResumableWrite> uploadIdFuture) {
        this.uploadIdFuture = uploadIdFuture;
        return this;
      }

      public UnbufferedWritableByteChannelSession<WriteObjectResponse> build() {
        return new ChannelSession.UnbufferedWriteSession<>(
            requireNonNull(uploadIdFuture, "uploadIdFuture must be non null"),
            f.andThen(StorageByteChannels.writable()::createSynchronized));
      }
    }
  }
}
