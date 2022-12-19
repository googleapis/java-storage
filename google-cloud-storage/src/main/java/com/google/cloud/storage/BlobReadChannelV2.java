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

import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static java.util.Objects.requireNonNull;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.storage.ApiaryUnbufferedReadableByteChannel.ApiaryReadRequest;
import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

final class BlobReadChannelV2 implements StorageReadChannel {

  private final StorageObject storageObject;
  private final Map<StorageRpc.Option, ?> opts;
  private final BlobReadChannelContext blobReadChannelContext;

  private LazyReadChannel<StorageObject> lazyReadChannel;
  private StorageObject resolvedObject;
  private ByteRangeSpec byteRangeSpec;

  private int chunkSize = _2MiB;
  private BufferHandle bufferHandle;

  BlobReadChannelV2(
      StorageObject storageObject,
      Map<StorageRpc.Option, ?> opts,
      BlobReadChannelContext blobReadChannelContext) {
    this.storageObject = storageObject;
    this.opts = opts;
    this.blobReadChannelContext = blobReadChannelContext;
    this.byteRangeSpec = ByteRangeSpec.nullRange();
  }

  @Override
  public synchronized void setChunkSize(int chunkSize) {
    StorageException.wrapIOException(() -> maybeResetChannel(true));
    this.chunkSize = chunkSize;
  }

  @Override
  public synchronized boolean isOpen() {
    if (lazyReadChannel == null) {
      return true;
    } else {
      LazyReadChannel<StorageObject> tmp = internalGetLazyChannel();
      return tmp.isOpen();
    }
  }

  @Override
  public synchronized void close() {
    if (internalGetLazyChannel().isOpen()) {
      StorageException.wrapIOException(internalGetLazyChannel().getChannel()::close);
    }
  }

  @Override
  public synchronized StorageReadChannel setByteRangeSpec(ByteRangeSpec byteRangeSpec) {
    requireNonNull(byteRangeSpec, "byteRangeSpec must be non null");
    StorageException.wrapIOException(() -> maybeResetChannel(false));
    this.byteRangeSpec = byteRangeSpec;
    return this;
  }

  @Override
  public ByteRangeSpec getByteRangeSpec() {
    return byteRangeSpec;
  }

  @Override
  public synchronized int read(ByteBuffer dst) throws IOException {
    long diff = byteRangeSpec.length();
    if (diff <= 0) {
      close();
      return -1;
    }
    try {
      int read = internalGetLazyChannel().getChannel().read(dst);
      if (read != -1) {
        byteRangeSpec = byteRangeSpec.withShiftBeginOffset(read);
      } else {
        close();
      }
      return read;
    } catch (StorageException e) {
      if (e.getCode() == 416) {
        // HttpStorageRpc turns 416 into a null etag with an empty byte array, leading
        // BlobReadChannel to believe it read 0 bytes, returning -1 and leaving the channel open.
        // Emulate that same behavior here to preserve behavior compatibility, though this should
        // be removed in the next major version.
        return -1;
      } else {
        throw new IOException(e);
      }
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(StorageException.coalesce(e));
    }
  }

  @Override
  public RestorableState<ReadChannel> capture() {
    ApiaryReadRequest apiaryReadRequest = getApiaryReadRequest();
    return new BlobReadChannelV2State(
        apiaryReadRequest, blobReadChannelContext.getStorageOptions(), chunkSize);
  }

  private void maybeResetChannel(boolean umallocBuffer) throws IOException {
    if (lazyReadChannel != null && lazyReadChannel.isOpen()) {
      try (BufferedReadableByteChannel ignore = lazyReadChannel.getChannel()) {
        if (bufferHandle != null && !umallocBuffer) {
          bufferHandle.get().clear();
        } else if (umallocBuffer) {
          bufferHandle = null;
        }
        lazyReadChannel = null;
      }
    }
  }

  private LazyReadChannel<StorageObject> internalGetLazyChannel() {
    if (lazyReadChannel == null) {
      lazyReadChannel = newLazyReadChannel();
    }
    return lazyReadChannel;
  }

  private LazyReadChannel<StorageObject> newLazyReadChannel() {
    return new LazyReadChannel<>(
        () -> {
          if (bufferHandle == null) {
            bufferHandle = BufferHandle.allocate(chunkSize);
          }
          return ResumableMedia.http()
              .read()
              .byteChannel(blobReadChannelContext)
              .setCallback(this::setResolvedObject)
              .buffered(bufferHandle)
              .setApiaryReadRequest(getApiaryReadRequest())
              .build();
        });
  }

  private void setResolvedObject(StorageObject resolvedObject) {
    this.resolvedObject = resolvedObject;
  }

  private ApiaryReadRequest getApiaryReadRequest() {
    StorageObject object = resolvedObject != null ? resolvedObject : storageObject;
    return new ApiaryReadRequest(object, opts, byteRangeSpec);
  }

  static class BlobReadChannelV2State implements RestorableState<ReadChannel>, Serializable {

    private static final long serialVersionUID = -7595661593080505431L;

    private final ApiaryReadRequest request;
    private final HttpStorageOptions options;

    private final Integer chunkSize;

    private BlobReadChannelV2State(
        ApiaryReadRequest request, HttpStorageOptions options, Integer chunkSize) {
      this.request = request;
      this.options = options;
      this.chunkSize = chunkSize;
    }

    @Override
    public ReadChannel restore() {
      BlobReadChannelV2 channel =
          new BlobReadChannelV2(
              request.getObject(), request.getOptions(), BlobReadChannelContext.from(options));
      channel.setByteRangeSpec(request.getByteRangeSpec());
      if (chunkSize != null) {
        channel.setChunkSize(chunkSize);
      }
      return channel;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof BlobReadChannelV2State)) {
        return false;
      }
      BlobReadChannelV2State that = (BlobReadChannelV2State) o;
      return Objects.equals(request, that.request)
          && Objects.equals(options, that.options)
          && Objects.equals(chunkSize, that.chunkSize);
    }

    @Override
    public int hashCode() {
      return Objects.hash(request, options, chunkSize);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("request", request)
          .add("options", options)
          .add("chunkSize", chunkSize)
          .toString();
    }
  }

  static final class BlobReadChannelContext {
    private final HttpStorageOptions storageOptions;
    private final HttpRetryAlgorithmManager retryAlgorithmManager;
    private final Storage apiaryClient;

    private BlobReadChannelContext(
        HttpStorageOptions storageOptions,
        Storage apiaryClient,
        HttpRetryAlgorithmManager retryAlgorithmManager) {
      this.storageOptions = storageOptions;
      this.apiaryClient = apiaryClient;
      this.retryAlgorithmManager = retryAlgorithmManager;
    }

    public HttpStorageOptions getStorageOptions() {
      return storageOptions;
    }

    public HttpRetryAlgorithmManager getRetryAlgorithmManager() {
      return retryAlgorithmManager;
    }

    public Storage getApiaryClient() {
      return apiaryClient;
    }

    static BlobReadChannelContext from(HttpStorageOptions options) {
      return new BlobReadChannelContext(
          options, options.getStorageRpcV1().getStorage(), options.getRetryAlgorithmManager());
    }

    static BlobReadChannelContext from(com.google.cloud.storage.Storage s) {
      StorageOptions options = s.getOptions();
      if (options instanceof HttpStorageOptions) {
        HttpStorageOptions httpStorageOptions = (HttpStorageOptions) options;
        return from(httpStorageOptions);
      }
      throw new IllegalArgumentException("Only HttpStorageOptions based instance supported");
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof BlobReadChannelContext)) {
        return false;
      }
      BlobReadChannelContext that = (BlobReadChannelContext) o;
      return Objects.equals(storageOptions, that.storageOptions)
          && Objects.equals(retryAlgorithmManager, that.retryAlgorithmManager);
    }

    @Override
    public int hashCode() {
      return Objects.hash(storageOptions, retryAlgorithmManager);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("storageOptions", storageOptions).toString();
    }
  }
}
