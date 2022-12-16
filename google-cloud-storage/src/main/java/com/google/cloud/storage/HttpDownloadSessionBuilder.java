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
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.ApiaryUnbufferedReadableByteChannel.ApiaryReadRequest;
import com.google.cloud.storage.BlobReadChannelV2.BlobReadChannelContext;
import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.concurrent.Immutable;

@Immutable
final class HttpDownloadSessionBuilder {
  private static final HttpDownloadSessionBuilder INSTANCE = new HttpDownloadSessionBuilder();

  private static final int DEFAULT_BUFFER_CAPACITY = ByteSizeConstants._2MiB;

  private HttpDownloadSessionBuilder() {}

  public static HttpDownloadSessionBuilder create() {
    return INSTANCE;
  }

  public ReadableByteChannelSessionBuilder byteChannel(
      BlobReadChannelContext blobReadChannelContext) {
    // TODO: refactor BlobReadChannelContext to push retry to a lower individual config
    //   similar to GapicWritableByteChannelSessionBuilder.ResumableUploadBuilder.withRetryConfig
    return new ReadableByteChannelSessionBuilder(blobReadChannelContext);
  }

  public static final class ReadableByteChannelSessionBuilder {

    private final BlobReadChannelContext blobReadChannelContext;
    // private Hasher hasher; // TODO: wire in Hasher
    private Consumer<StorageObject> callback;

    private ReadableByteChannelSessionBuilder(BlobReadChannelContext blobReadChannelContext) {
      this.blobReadChannelContext = blobReadChannelContext;
    }

    public ReadableByteChannelSessionBuilder setCallback(Consumer<StorageObject> callback) {
      this.callback = callback;
      return this;
    }

    public BufferedReadableByteChannelSessionBuilder buffered() {
      return buffered(BufferHandle.allocate(DEFAULT_BUFFER_CAPACITY));
    }

    public BufferedReadableByteChannelSessionBuilder buffered(BufferHandle bufferHandle) {
      return new BufferedReadableByteChannelSessionBuilder(bufferHandle, bindFunction());
    }

    public BufferedReadableByteChannelSessionBuilder buffered(ByteBuffer buffer) {
      return buffered(BufferHandle.handleOf(buffer));
    }

    public UnbufferedReadableByteChannelSessionBuilder unbuffered() {
      return new UnbufferedReadableByteChannelSessionBuilder(bindFunction());
    }

    private BiFunction<
            ApiaryReadRequest, SettableApiFuture<StorageObject>, UnbufferedReadableByteChannel>
        bindFunction() {
      // for any non-final value, create a reference to the value at this point in time
      return (request, resultFuture) ->
          new ApiaryUnbufferedReadableByteChannel(
              request,
              blobReadChannelContext.getApiaryClient(),
              resultFuture,
              blobReadChannelContext.getStorageOptions(),
              blobReadChannelContext.getRetryAlgorithmManager().idempotent(),
              callback);
    }

    public static final class BufferedReadableByteChannelSessionBuilder {

      private final BiFunction<
              ApiaryReadRequest, SettableApiFuture<StorageObject>, BufferedReadableByteChannel>
          f;
      private ApiaryReadRequest request;

      private BufferedReadableByteChannelSessionBuilder(
          BufferHandle buffer,
          BiFunction<
                  ApiaryReadRequest,
                  SettableApiFuture<StorageObject>,
                  UnbufferedReadableByteChannel>
              f) {
        this.f = f.andThen(c -> new DefaultBufferedReadableByteChannel(buffer, c));
      }

      public BufferedReadableByteChannelSessionBuilder setApiaryReadRequest(
          ApiaryReadRequest request) {
        this.request = requireNonNull(request, "request must be non null");
        return this;
      }

      public BufferedReadableByteChannelSession<StorageObject> build() {
        return new ChannelSession.BufferedReadSession<>(
            ApiFutures.immediateFuture(request),
            f.andThen(StorageByteChannels.readable()::createSynchronized));
      }
    }

    public static final class UnbufferedReadableByteChannelSessionBuilder {

      private final BiFunction<
              ApiaryReadRequest, SettableApiFuture<StorageObject>, UnbufferedReadableByteChannel>
          f;
      private ApiaryReadRequest request;

      private UnbufferedReadableByteChannelSessionBuilder(
          BiFunction<
                  ApiaryReadRequest,
                  SettableApiFuture<StorageObject>,
                  UnbufferedReadableByteChannel>
              f) {
        this.f = f;
      }

      public UnbufferedReadableByteChannelSessionBuilder setApiaryReadRequest(
          ApiaryReadRequest request) {
        this.request = requireNonNull(request, "request must be non null");
        return this;
      }

      public UnbufferedReadableByteChannelSession<StorageObject> build() {
        return new ChannelSession.UnbufferedReadSession<>(
            ApiFutures.immediateFuture(request),
            f.andThen(StorageByteChannels.readable()::createSynchronized));
      }
    }
  }
}
