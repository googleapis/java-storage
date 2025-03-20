/*
 * Copyright 2025 Google LLC
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

import com.google.api.core.BetaApi;
import com.google.common.base.MoreObjects;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@BetaApi
@Immutable
public final class ReadAsSeekableChannel extends ReadProjectionConfig<SeekableByteChannel> {

  static final ReadAsSeekableChannel INSTANCE =
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
    return new ReadAsSeekableChannel(enabled ? Hasher.enabled() : Hasher.noop(), rangeSpecFunction);
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
    return MoreObjects.toStringHelper(this).add("rangeSpecFunction", rangeSpecFunction).toString();
  }
}
