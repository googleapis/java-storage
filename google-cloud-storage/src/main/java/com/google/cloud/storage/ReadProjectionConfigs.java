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
import com.google.api.core.BetaApi;
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.AccumulatingRead;
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.StreamingRead;
import com.google.cloud.storage.ZeroCopySupport.DisposableByteString;
import com.google.common.base.MoreObjects;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@BetaApi
public final class ReadProjectionConfigs {

  abstract static class BaseConfig<Projection, Read extends ObjectReadSessionStreamRead<Projection>>
      extends ReadProjectionConfig<Projection> {
    abstract Read newRead(long readId, RetryContext retryContext);

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
  @BetaApi
  public static ReadAsChannel asChannel() {
    return ReadAsChannel.INSTANCE;
  }

  @BetaApi
  static ReadAsFutureBytes asFutureBytes() {
    return ReadAsFutureBytes.INSTANCE;
  }

  @BetaApi
  static ReadAsFutureByteString asFutureByteString() {
    return ReadAsFutureByteString.INSTANCE;
  }

  @BetaApi
  public static ReadAsSeekableChannel asSeekableChannel() {
    return ReadAsSeekableChannel.INSTANCE;
  }

  @BetaApi
  @Immutable
  public static final class ReadAsSeekableChannel
      extends ReadProjectionConfig<SeekableByteChannel> {

    private static final ReadAsSeekableChannel INSTANCE =
        new ReadAsSeekableChannel(Hasher.enabled(), LinearExponentialRangeSpecFunction.INSTANCE);

    private final Hasher hasher;
    private final RangeSpecFunction rangeSpecFunction;

    private ReadAsSeekableChannel(Hasher hasher, RangeSpecFunction rangeSpecFunction) {
      this.hasher = hasher;
      this.rangeSpecFunction = rangeSpecFunction;
    }

    @BetaApi
    public RangeSpecFunction getRangeSpecFunction() {
      return rangeSpecFunction;
    }

    @BetaApi
    public ReadAsSeekableChannel withRangeSpecFunction(RangeSpecFunction rangeSpecFunction) {
      requireNonNull(rangeSpecFunction, "rangeSpecFunction must be non null");
      return new ReadAsSeekableChannel(hasher, rangeSpecFunction);
    }

    @BetaApi
    boolean getCrc32cValidationEnabled() {
      return Hasher.enabled().equals(hasher);
    }

    @BetaApi
    ReadAsSeekableChannel withCrc32cValidationEnabled(boolean enabled) {
      if (enabled && Hasher.enabled().equals(hasher)) {
        return this;
      } else if (!enabled && Hasher.noop().equals(hasher)) {
        return this;
      }
      return new ReadAsSeekableChannel(
          enabled ? Hasher.enabled() : Hasher.noop(), rangeSpecFunction);
    }

    @Override
    SeekableByteChannel project(ObjectReadSession session, IOAutoCloseable closeAlongWith) {
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
      if (!(o instanceof ReadAsSeekableChannel)) {
        return false;
      }
      ReadAsSeekableChannel that = (ReadAsSeekableChannel) o;
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

  @BetaApi
  @Immutable
  public static final class ReadAsChannel extends BaseConfig<ScatteringByteChannel, StreamingRead> {
    private static final ReadAsChannel INSTANCE =
        new ReadAsChannel(RangeSpec.all(), Hasher.enabled());

    private final RangeSpec range;
    private final Hasher hasher;

    private ReadAsChannel(RangeSpec range, Hasher hasher) {
      super();
      this.range = range;
      this.hasher = hasher;
    }

    @BetaApi
    public RangeSpec getRange() {
      return range;
    }

    @BetaApi
    public ReadAsChannel withRangeSpec(RangeSpec range) {
      requireNonNull(range, "range must be non null");
      if (this.range.equals(range)) {
        return this;
      }
      return new ReadAsChannel(range, hasher);
    }

    boolean getCrc32cValidationEnabled() {
      return Hasher.enabled().equals(hasher);
    }

    @BetaApi
    ReadAsChannel withCrc32cValidationEnabled(boolean enabled) {
      if (enabled && Hasher.enabled().equals(hasher)) {
        return this;
      } else if (!enabled && Hasher.noop().equals(hasher)) {
        return this;
      }
      return new ReadAsChannel(range, enabled ? Hasher.enabled() : Hasher.noop());
    }

    @Override
    BaseConfig<ScatteringByteChannel, ?> cast() {
      return this;
    }

    @Override
    StreamingRead newRead(long readId, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.streamingRead(readId, range, hasher, retryContext);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ReadAsChannel)) {
        return false;
      }
      ReadAsChannel that = (ReadAsChannel) o;
      return Objects.equals(range, that.range) && Objects.equals(hasher, that.hasher);
    }

    @Override
    public int hashCode() {
      return Objects.hash(range, hasher);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("range", range)
          .add("crc32cValidationEnabled", getCrc32cValidationEnabled())
          .toString();
    }
  }

  @BetaApi
  @Immutable
  static final class ReadAsFutureBytes
      extends BaseConfig<ApiFuture<byte[]>, AccumulatingRead<byte[]>> {
    private static final ReadAsFutureBytes INSTANCE =
        new ReadAsFutureBytes(RangeSpec.all(), Hasher.enabled());

    private final RangeSpec range;
    private final Hasher hasher;

    private ReadAsFutureBytes(RangeSpec range, Hasher hasher) {
      super();
      this.range = range;
      this.hasher = hasher;
    }

    @BetaApi
    public RangeSpec getRange() {
      return range;
    }

    @BetaApi
    public ReadAsFutureBytes withRangeSpec(RangeSpec range) {
      requireNonNull(range, "range must be non null");
      if (this.range.equals(range)) {
        return this;
      }
      return new ReadAsFutureBytes(range, hasher);
    }

    @BetaApi
    boolean getCrc32cValidationEnabled() {
      return Hasher.enabled().equals(hasher);
    }

    @BetaApi
    ReadAsFutureBytes withCrc32cValidationEnabled(boolean enabled) {
      if (enabled && Hasher.enabled().equals(hasher)) {
        return this;
      } else if (!enabled && Hasher.noop().equals(hasher)) {
        return this;
      }
      return new ReadAsFutureBytes(range, enabled ? Hasher.enabled() : Hasher.noop());
    }

    @Override
    BaseConfig<ApiFuture<byte[]>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<byte[]> newRead(long readId, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createByteArrayAccumulatingRead(
          readId, range, hasher, retryContext);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ReadAsFutureBytes)) {
        return false;
      }
      ReadAsFutureBytes that = (ReadAsFutureBytes) o;
      return Objects.equals(range, that.range) && Objects.equals(hasher, that.hasher);
    }

    @Override
    public int hashCode() {
      return Objects.hash(range, hasher);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("range", range)
          .add("crc32cValidationEnabled", getCrc32cValidationEnabled())
          .toString();
    }
  }

  @Immutable
  static final class ReadAsFutureByteString
      extends BaseConfig<ApiFuture<DisposableByteString>, AccumulatingRead<DisposableByteString>> {
    private static final ReadAsFutureByteString INSTANCE =
        new ReadAsFutureByteString(RangeSpec.all(), Hasher.enabled());

    private final RangeSpec range;
    private final Hasher hasher;

    private ReadAsFutureByteString(RangeSpec range, Hasher hasher) {
      super();
      this.range = range;
      this.hasher = hasher;
    }

    @BetaApi
    public RangeSpec getRange() {
      return range;
    }

    @BetaApi
    public ReadAsFutureByteString withRangeSpec(RangeSpec range) {
      requireNonNull(range, "range must be non null");
      if (this.range.equals(range)) {
        return this;
      }
      return new ReadAsFutureByteString(range, hasher);
    }

    @BetaApi
    boolean getCrc32cValidationEnabled() {
      return Hasher.enabled().equals(hasher);
    }

    @BetaApi
    ReadAsFutureByteString withCrc32cValidationEnabled(boolean enabled) {
      if (enabled && Hasher.enabled().equals(hasher)) {
        return this;
      } else if (!enabled && Hasher.noop().equals(hasher)) {
        return this;
      }
      return new ReadAsFutureByteString(range, enabled ? Hasher.enabled() : Hasher.noop());
    }

    @Override
    BaseConfig<ApiFuture<DisposableByteString>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<DisposableByteString> newRead(long readId, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createZeroCopyByteStringAccumulatingRead(
          readId, range, hasher, retryContext);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ReadAsFutureByteString)) {
        return false;
      }
      ReadAsFutureByteString that = (ReadAsFutureByteString) o;
      return Objects.equals(range, that.range) && Objects.equals(hasher, that.hasher);
    }

    @Override
    public int hashCode() {
      return Objects.hash(range, hasher);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("range", range)
          .add("crc32cValidationEnabled", getCrc32cValidationEnabled())
          .toString();
    }
  }
}
