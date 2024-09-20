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
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import javax.annotation.concurrent.Immutable;

@Immutable
final class GapicDownloadSessionBuilder {
  private static final GapicDownloadSessionBuilder INSTANCE = new GapicDownloadSessionBuilder();

  private static final int DEFAULT_BUFFER_CAPACITY = ByteSizeConstants._16MiB;

  private GapicDownloadSessionBuilder() {}

  public static GapicDownloadSessionBuilder create() {
    return INSTANCE;
  }

  public ReadableByteChannelSessionBuilder byteChannel(
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      RetryingDependencies retryingDependencies,
      ResultRetryAlgorithm<?> resultRetryAlgorithm,
      ResponseContentLifecycleManager responseContentLifecycleManager) {
    return new ReadableByteChannelSessionBuilder(
        read, retryingDependencies, resultRetryAlgorithm, responseContentLifecycleManager);
  }

  public static final class ReadableByteChannelSessionBuilder {

    private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
    private final RetryingDependencies retryingDependencies;
    private final ResultRetryAlgorithm<?> resultRetryAlgorithm;
    private final ResponseContentLifecycleManager responseContentLifecycleManager;
    private boolean autoGzipDecompression;
    private Hasher hasher;

    private ReadableByteChannelSessionBuilder(
        ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
        RetryingDependencies retryingDependencies,
        ResultRetryAlgorithm<?> resultRetryAlgorithm,
        ResponseContentLifecycleManager responseContentLifecycleManager) {
      this.read = read;
      this.retryingDependencies = retryingDependencies;
      this.resultRetryAlgorithm = resultRetryAlgorithm;
      this.responseContentLifecycleManager = responseContentLifecycleManager;
      this.hasher = Hasher.noop();
      this.autoGzipDecompression = false;
    }

    public BufferedReadableByteChannelSessionBuilder buffered() {
      return buffered(BufferHandle.allocate(DEFAULT_BUFFER_CAPACITY));
    }

    public ReadableByteChannelSessionBuilder setHasher(Hasher hasher) {
      this.hasher = hasher;
      return this;
    }

    public ReadableByteChannelSessionBuilder setAutoGzipDecompression(
        boolean autoGzipDecompression) {
      this.autoGzipDecompression = autoGzipDecompression;
      return this;
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

    private BiFunction<ReadObjectRequest, SettableApiFuture<Object>, UnbufferedReadableByteChannel>
        bindFunction() {
      // for any non-final value, create a reference to the value at this point in time
      Hasher hasher = this.hasher;
      boolean autoGzipDecompression = this.autoGzipDecompression;
      return (object, resultFuture) -> {
        if (autoGzipDecompression) {
          return new GzipReadableByteChannel(
              new GapicUnbufferedReadableByteChannel(
                  resultFuture,
                  read,
                  object,
                  hasher,
                  retryingDependencies,
                  resultRetryAlgorithm,
                  responseContentLifecycleManager),
              ApiFutures.transform(
                  resultFuture, Object::getContentEncoding, MoreExecutors.directExecutor()));
        } else {
          return new GapicUnbufferedReadableByteChannel(
              resultFuture,
              read,
              object,
              hasher,
              retryingDependencies,
              resultRetryAlgorithm,
              responseContentLifecycleManager);
        }
      };
    }

    public static final class BufferedReadableByteChannelSessionBuilder {

      private final BiFunction<
              ReadObjectRequest, SettableApiFuture<Object>, BufferedReadableByteChannel>
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

      private final BiFunction<
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
