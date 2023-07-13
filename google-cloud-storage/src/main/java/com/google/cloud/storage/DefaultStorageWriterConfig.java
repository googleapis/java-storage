/*
 * Copyright 2023 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.storage.v2.WriteObjectResponse;
import java.nio.channels.WritableByteChannel;
import java.time.Clock;
import javax.annotation.concurrent.Immutable;

/**
 * Configure a writer which is logically equivalent to the following:
 *
 * <pre>{@code
 * Storage storage = ...;
 * WriteChannel writeChannel = storage.writer(BlobInfo, BlobWriteOption);
 * writeChannel.setChunkSize(chunkSize);
 * }</pre>
 */
@Immutable
@BetaApi
public final class DefaultStorageWriterConfig extends StorageWriterConfig {

  private final int chunkSize;

  @InternalApi
  DefaultStorageWriterConfig(int chunkSize) {
    this.chunkSize = chunkSize;
  }

  public int getChunkSize() {
    return chunkSize;
  }

  @BetaApi
  public DefaultStorageWriterConfig withChunkSize(int chunkSize) {
    Preconditions.checkArgument(
        chunkSize >= ByteSizeConstants._256KiB,
        "chunkSize must be >= %d",
        ByteSizeConstants._256KiB);
    return new DefaultStorageWriterConfig(chunkSize);
  }

  @Override
  @InternalApi
  WriterFactory createFactory(Clock clock) {
    return new Factory(chunkSize);
  }

  @InternalApi
  private static final class Factory implements WriterFactory {

    private final int chunkSize;

    private Factory(int chunkSize) {
      this.chunkSize = chunkSize;
    }

    @InternalApi
    @Override
    public WritableByteChannelSession<?, BlobInfo> writeSession(
        StorageInternal s,
        BlobInfo info,
        Opts<ObjectTargetOpt> opts,
        Decoder<WriteObjectResponse, BlobInfo> d) {
      // todo: invert this
      //   make GrpcBlobWriteChannel use this factory to produce its WriteSession
      if (s instanceof GrpcStorageImpl) {
        GrpcStorageImpl g = (GrpcStorageImpl) s;
        GrpcBlobWriteChannel writer = g.writer(info);
        writer.setChunkSize(chunkSize);
        WritableByteChannelSession<BufferedWritableByteChannel, WriteObjectResponse> session =
            writer.newLazyWriteChannel().getSession();
        return new DecoratedWritableByteChannelSession<>(session, d);
      }
      return CrossTransportUtils.throwGrpcOnly(DefaultStorageWriterConfig.class, "");
    }
  }

  private static final class DecoratedWritableByteChannelSession<WBC extends WritableByteChannel, T>
      implements WritableByteChannelSession<WBC, BlobInfo> {

    private final WritableByteChannelSession<WBC, T> delegate;
    private final Decoder<T, BlobInfo> d;

    private DecoratedWritableByteChannelSession(
        WritableByteChannelSession<WBC, T> delegate, Decoder<T, BlobInfo> d) {
      this.delegate = delegate;
      this.d = d;
    }

    @Override
    public ApiFuture<WBC> openAsync() {
      return delegate.openAsync();
    }

    @Override
    public ApiFuture<BlobInfo> getResult() {
      return ApiFutures.transform(delegate.getResult(), d::decode, MoreExecutors.directExecutor());
    }
  }
}
