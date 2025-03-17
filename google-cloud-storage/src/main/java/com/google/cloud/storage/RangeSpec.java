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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.OptionalLong;
import javax.annotation.concurrent.Immutable;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Defines a range with begin offset and limit. */
@Immutable
public abstract class RangeSpec {
  // seal this class to extension
  private RangeSpec() {}

  /** The beginning of the range. */
  public abstract long begin();

  /**
   * A limit of the range if defined.
   *
   * @see RangeSpecWithLimit
   */
  public abstract OptionalLong limit();

  /**
   * Create a new instance of {@link RangeSpec} keeping {@code this.begin()} and with {@code limit}
   * as its new limit.
   */
  @NonNull
  public abstract RangeSpec withLimit(long limit);

  /** {@inheritDoc} */
  @Override
  public abstract boolean equals(Object o);

  /** {@inheritDoc} */
  @Override
  public abstract int hashCode();

  /** {@inheritDoc} */
  @Override
  public abstract String toString();

  /**
   * Create a new RangeSpec with the provided {@code begin}.
   *
   * @throws IllegalArgumentException if begin is &lt; 0
   */
  @NonNull
  public static RangeSpec beginAt(long begin) {
    checkArgument(begin >= 0, "range being must be >= 0 (range begin = %s)", begin);
    return new RangeSpecWithoutLimit(begin);
  }

  /**
   * Create a new RangeSpec with the provided {@code begin} and {@code limit}.
   *
   * @throws IllegalArgumentException if begin is &lt; 0, or if limit is &lt; 0
   */
  @NonNull
  public static RangeSpec of(long begin, long limit) {
    checkArgument(begin >= 0, "range being must be >= 0 (range begin = %s)", begin);
    checkArgument(limit >= 0, "range limit must be >= 0 (range limit = %s)", limit);
    if (limit == 0) {
      return new RangeSpecWithoutLimit(begin);
    }
    return new RangeSpecWithLimit(begin, limit);
  }

  /** A RangeSpec that represents to read from {@code 0} to {@code EOF} */
  @NonNull
  public static RangeSpec all() {
    return RangeSpecWithoutLimit.ALL;
  }

  static final class RangeSpecWithoutLimit extends RangeSpec {
    private static final RangeSpecWithoutLimit ALL = new RangeSpecWithoutLimit(0);
    private final long begin;

    private RangeSpecWithoutLimit(long begin) {
      this.begin = begin;
    }

    @Override
    public long begin() {
      return begin;
    }

    @Override
    public OptionalLong limit() {
      return OptionalLong.empty();
    }

    @Override
    @NonNull
    public RangeSpec withLimit(long limit) {
      checkArgument(limit >= 0, "range limit must be >= 0 (range limit = %s)", limit);
      return new RangeSpecWithLimit(begin, limit);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof RangeSpecWithoutLimit)) {
        return false;
      }
      RangeSpecWithoutLimit that = (RangeSpecWithoutLimit) o;
      return begin == that.begin;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(begin);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("begin", begin).toString();
    }
  }

  static final class RangeSpecWithLimit extends RangeSpec {
    private final long begin;
    private final long limit;

    private RangeSpecWithLimit(long begin, long limit) {
      this.begin = begin;
      this.limit = limit;
    }

    @Override
    public long begin() {
      return begin;
    }

    @Override
    public OptionalLong limit() {
      return OptionalLong.of(limit);
    }

    @Override
    @NonNull
    public RangeSpec withLimit(long limit) {
      checkArgument(limit >= 0, "range limit must be >= 0 (range limit = %s)", limit);
      return new RangeSpecWithLimit(begin, limit);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof RangeSpecWithLimit)) {
        return false;
      }
      RangeSpecWithLimit that = (RangeSpecWithLimit) o;
      return begin == that.begin && limit == that.limit;
    }

    @Override
    public int hashCode() {
      return Objects.hash(begin, limit);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(RangeSpec.class)
          .add("begin", begin)
          .add("limit", limit)
          .toString();
    }
  }
}
