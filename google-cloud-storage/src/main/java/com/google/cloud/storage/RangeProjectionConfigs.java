/*
 * Copyright 2024 Google LLC
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
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.AccumulatingRead;
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.StreamingRead;
import com.google.cloud.storage.ZeroCopySupport.DisposableByteString;
import com.google.common.base.MoreObjects;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;

public final class RangeProjectionConfigs {

  abstract static class BaseConfig<Projection, Read extends ObjectReadSessionStreamRead<Projection>>
      extends RangeProjectionConfig<Projection> {
    abstract Read newRead(long readId, RangeSpec range, RetryContext retryContext);

    @Override
    ProjectionType getType() {
      return ProjectionType.STREAM_READ;
    }
  }

  /**
   * Read a range as a non-blocking Channel.
   *
   * <p>The returned channel will be non-blocking for all read calls. If bytes have not yet
   * asynchronously been delivered from gcs the method will return rather than waiting for the bytes
   * to arrive.
   */
  public static RangeAsChannel asChannel() {
    return RangeAsChannel.INSTANCE;
  }

  public static RangeAsFutureBytes asFutureBytes() {
    return RangeAsFutureBytes.INSTANCE;
  }

  static RangeAsFutureByteString asFutureByteString() {
    return RangeAsFutureByteString.INSTANCE;
  }

  public static SeekableChannelConfig asSeekableChannel() {
    return SeekableChannelConfig.INSTANCE;
  }

  public static final class SeekableChannelConfig
      extends RangeProjectionConfig<SeekableByteChannel> {

    private static final SeekableChannelConfig INSTANCE =
        new SeekableChannelConfig(LinearExponentialRangeSpecFunction.INSTANCE);

    private final RangeSpecFunction rangeSpecFunction;

    private SeekableChannelConfig(RangeSpecFunction rangeSpecFunction) {
      this.rangeSpecFunction = rangeSpecFunction;
    }

    public RangeSpecFunction getRangeSpecFunction() {
      return rangeSpecFunction;
    }

    public SeekableChannelConfig withRangeSpecFunction(RangeSpecFunction rangeSpecFunction) {
      requireNonNull(rangeSpecFunction, "rangeSpecFunction must be non null");
      return new SeekableChannelConfig(rangeSpecFunction);
    }

    @Override
    SeekableByteChannel project(
        RangeSpec range, ObjectReadSession session, IOAutoCloseable closeAlongWith) {
      return StorageByteChannels.seekable(
          new ObjectReadSessionSeekableByteChannel(session, this, closeAlongWith));
    }

    @Override
    ProjectionType getType() {
      return ProjectionType.SESSION_USER;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SeekableChannelConfig)) {
        return false;
      }
      SeekableChannelConfig that = (SeekableChannelConfig) o;
      return Objects.equals(rangeSpecFunction, that.rangeSpecFunction);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(rangeSpecFunction);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("rangeSpecFunction", rangeSpecFunction)
          .toString();
    }
  }

  public static final class RangeAsChannel
      extends BaseConfig<ScatteringByteChannel, StreamingRead> {
    private static final RangeAsChannel INSTANCE = new RangeAsChannel();

    private RangeAsChannel() {
      super();
    }

    @Override
    BaseConfig<ScatteringByteChannel, ?> cast() {
      return this;
    }

    @Override
    StreamingRead newRead(long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.streamingRead(readId, range, retryContext);
    }
  }

  public static final class RangeAsFutureBytes
      extends BaseConfig<ApiFuture<byte[]>, AccumulatingRead<byte[]>> {
    private static final RangeAsFutureBytes INSTANCE = new RangeAsFutureBytes();

    private RangeAsFutureBytes() {
      super();
    }

    @Override
    BaseConfig<ApiFuture<byte[]>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<byte[]> newRead(long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createByteArrayAccumulatingRead(
          readId, range, retryContext);
    }
  }

  static final class RangeAsFutureByteString
      extends BaseConfig<ApiFuture<DisposableByteString>, AccumulatingRead<DisposableByteString>> {
    private static final RangeAsFutureByteString INSTANCE = new RangeAsFutureByteString();

    private RangeAsFutureByteString() {
      super();
    }

    @Override
    BaseConfig<ApiFuture<DisposableByteString>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<DisposableByteString> newRead(
        long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createZeroCopyByteStringAccumulatingRead(
          readId, range, retryContext);
    }
  }
}
