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

import com.google.api.core.BetaApi;
import com.google.common.base.MoreObjects;
import com.google.common.math.DoubleMath;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalLong;
import javax.annotation.concurrent.Immutable;

@BetaApi
@Immutable
public final class LinearExponentialRangeSpecFunction extends RangeSpecFunction {

  static final LinearExponentialRangeSpecFunction INSTANCE =
      new LinearExponentialRangeSpecFunction(ByteSizeConstants._2MiB, 4.0d);
  private final long initialMaxLength;
  private final double maxLengthScalar;

  private LinearExponentialRangeSpecFunction(long initialMaxLength, double maxLengthScalar) {
    this.initialMaxLength = initialMaxLength;
    this.maxLengthScalar = maxLengthScalar;
  }

  public long getInitialMaxLength() {
    return initialMaxLength;
  }

  public LinearExponentialRangeSpecFunction withInitialMaxLength(long initialMaxLength) {
    checkArgument(initialMaxLength > 0, "initialMaxLength > 0 (%s > 0)", initialMaxLength);
    return new LinearExponentialRangeSpecFunction(initialMaxLength, maxLengthScalar);
  }

  public double getMaxLengthScalar() {
    return maxLengthScalar;
  }

  public LinearExponentialRangeSpecFunction withMaxLengthScalar(double maxLengthScalar) {
    checkArgument(maxLengthScalar >= 1.0, "maxLengthScalar >= 1.0 (%s >= 1.0)", maxLengthScalar);
    return new LinearExponentialRangeSpecFunction(initialMaxLength, maxLengthScalar);
  }

  @Override
  public RangeSpec apply(long offset, RangeSpec prev) {
    if (prev == null) {
      return RangeSpec.of(offset, initialMaxLength);
    }

    OptionalLong maybeMaxLength = prev.maxLength();
    long maxLength;
    if (maybeMaxLength.isPresent()) {
      maxLength = maybeMaxLength.getAsLong();

      long expectedOffset = prev.begin() + maxLength;
      if (offset != expectedOffset) {
        return RangeSpec.of(offset, initialMaxLength);
      }

    } else {
      maxLength = Long.MAX_VALUE;
    }

    long scaleReadSize = scaleMaxLength(maxLength, maxLengthScalar);

    return RangeSpec.of(offset, scaleReadSize);
  }

  private static long scaleMaxLength(long lastReadSize, double rangeMaxLengthScalar) {
    double scaled = lastReadSize * rangeMaxLengthScalar;
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
    return initialMaxLength == that.initialMaxLength
        && Double.compare(maxLengthScalar, that.maxLengthScalar) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(initialMaxLength, maxLengthScalar);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("initialMaxLength", initialMaxLength)
        .add("rangeMaxLengthScalar", maxLengthScalar)
        .toString();
  }
}
