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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import com.google.common.math.DoubleMath;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalLong;

public final class LinearExponentialRangeSpecFunction implements RangeSpecFunction {

  static final LinearExponentialRangeSpecFunction INSTANCE =
      new LinearExponentialRangeSpecFunction(ByteSizeConstants._1KiB, 4.0d);
  private final long initialRangeSize;
  private final double rangeSizeScalar;

  private LinearExponentialRangeSpecFunction(long initialRangeSize, double rangeSizeScalar) {
    this.initialRangeSize = initialRangeSize;
    this.rangeSizeScalar = rangeSizeScalar;
  }

  public long getInitialRangeSize() {
    return initialRangeSize;
  }

  public LinearExponentialRangeSpecFunction withMinRangeSize(long initialRangeSize) {
    checkArgument(initialRangeSize > 0, "initialRangeSize > 0 (%s > 0)", initialRangeSize);
    return new LinearExponentialRangeSpecFunction(initialRangeSize, rangeSizeScalar);
  }

  public double getRangeSizeScalar() {
    return rangeSizeScalar;
  }

  public LinearExponentialRangeSpecFunction withRangeSizeScalar(double rangeSizeScalar) {
    checkArgument(rangeSizeScalar >= 1.0, "rangeSizeScalar >= 1.0 (%s >= 1.0)", rangeSizeScalar);
    return new LinearExponentialRangeSpecFunction(initialRangeSize, rangeSizeScalar);
  }

  @Override
  public RangeSpec apply(long offset, RangeSpec prev) {
    if (prev == null) {
      return RangeSpec.of(offset, initialRangeSize);
    }

    OptionalLong maybeLimit = prev.limit();
    long limit;
    if (maybeLimit.isPresent()) {
      limit = maybeLimit.getAsLong();
    } else {
      limit = Long.MAX_VALUE;
    }

    long scaleReadSize = scaleReadSize(limit, rangeSizeScalar);

    return RangeSpec.of(offset, scaleReadSize);
  }

  private static long scaleReadSize(long lastReadSize, double rangeSizeScalar) {
    double scaled = lastReadSize * rangeSizeScalar;
    if (Double.isInfinite(scaled)) {
      return Long.MAX_VALUE;
    }
    return DoubleMath.roundToLong(scaled, RoundingMode.HALF_EVEN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LinearExponentialRangeSpecFunction)) {
      return false;
    }
    LinearExponentialRangeSpecFunction that = (LinearExponentialRangeSpecFunction) o;
    return initialRangeSize == that.initialRangeSize
        && Double.compare(rangeSizeScalar, that.rangeSizeScalar) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(initialRangeSize, rangeSizeScalar);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("minRangeSize", initialRangeSize)
        .add("rangeSizeScalar", rangeSizeScalar)
        .toString();
  }
}
