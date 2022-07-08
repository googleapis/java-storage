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

import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;

final class GapicDownloadSessionBuilder {

  private GapicDownloadSessionBuilder() {}

  public static GapicDownloadSessionBuilder create() {
    return new GapicDownloadSessionBuilder();
  }

  public ReadableByteChannelSessionBuilder byteChannel(
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read) {
    return new ReadableByteChannelSessionBuilder(read);
  }

  public static final class ReadableByteChannelSessionBuilder {

    private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
    private Hasher hasher;

    private ReadableByteChannelSessionBuilder(
        ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read) {
      this.read = read;
      this.hasher = Hasher.noop();
    }

    public BufferedReadableByteChannelSessionBuilder buffered() {
      return buffered(Buffers.allocate(16 * 1024 * 1024));
    }

    public ReadableByteChannelSessionBuilder setHasher(Hasher hasher) {
      this.hasher = hasher;
      return this;
    }

    public BufferedReadableByteChannelSessionBuilder buffered(int capacity) {
      return new BufferedReadableByteChannelSessionBuilder(
          BufferHandle.allocate(capacity), getF(read, hasher));
    }

    public BufferedReadableByteChannelSessionBuilder buffered(ByteBuffer buffer) {
      return new BufferedReadableByteChannelSessionBuilder(
          BufferHandle.handleOf(buffer), getF(read, hasher));
    }

    public UnbufferedReadableByteChannelSessionBuilder unbuffered() {
      return new UnbufferedReadableByteChannelSessionBuilder(getF(read, hasher));
    }

    private static BiFunction<
            ReadObjectRequest, SettableApiFuture<Object>, UnbufferedReadableByteChannel>
        getF(ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read, Hasher hasher) {
      return (object, resultFuture) ->
          new GapicUnbufferedReadableByteChannel(resultFuture, read, object, hasher);
    }

    public static final class BufferedReadableByteChannelSessionBuilder {

      private BiFunction<ReadObjectRequest, SettableApiFuture<Object>, BufferedReadableByteChannel>
          f;
      private ReadObjectRequest request;

      private BufferedReadableByteChannelSessionBuilder(
          BufferHandle buffer,
          BiFunction<ReadObjectRequest, SettableApiFuture<Object>, UnbufferedReadableByteChannel>
              f) {
        this.f = f.andThen(c -> new DefaultBufferedReadableByteChannel(buffer, c));
      }

      public BufferedReadableByteChannelSessionBuilder setReadObjectRequest(
          ReadObjectRequest request) {
        this.request = requireNonNull(request, "request must be non null");
        return this;
      }

      public BufferedReadableByteChannelSession<Object> build() {
        return new ChannelSession.BufferedReadSession<>(
            ApiFutures.immediateFuture(request),
            f.andThen(StorageByteChannels.readable()::createSynchronized));
      }
    }

    public static final class UnbufferedReadableByteChannelSessionBuilder {

      private BiFunction<
              ReadObjectRequest, SettableApiFuture<Object>, UnbufferedReadableByteChannel>
          f;
      private ReadObjectRequest request;

      private UnbufferedReadableByteChannelSessionBuilder(
          BiFunction<ReadObjectRequest, SettableApiFuture<Object>, UnbufferedReadableByteChannel>
              f) {
        this.f = f;
      }

      public UnbufferedReadableByteChannelSessionBuilder setReadObjectRequest(
          ReadObjectRequest request) {
        this.request = requireNonNull(request, "request must be non null");
        return this;
      }

      public UnbufferedReadableByteChannelSession<Object> build() {
        return new ChannelSession.UnbufferedReadSession<>(
            ApiFutures.immediateFuture(request),
            f.andThen(StorageByteChannels.readable()::createSynchronized));
      }
    }
  }
}
