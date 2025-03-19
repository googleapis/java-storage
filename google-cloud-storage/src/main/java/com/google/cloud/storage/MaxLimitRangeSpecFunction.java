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
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@BetaApi
public final class MaxLimitRangeSpecFunction extends RangeSpecFunction {
  static final MaxLimitRangeSpecFunction INSTANCE = new MaxLimitRangeSpecFunction(0);
  private final long maxLimit;

  MaxLimitRangeSpecFunction(long maxLimit) {
    this.maxLimit = maxLimit;
  }

  public long getMaxLimit() {
    return maxLimit;
  }

  public MaxLimitRangeSpecFunction withMaxLimit(long maxLimit) {
    checkArgument(maxLimit >= 0, "maxLimit >= 0 (%s >= 0)", maxLimit);
    return new MaxLimitRangeSpecFunction(maxLimit);
  }

  @Override
  public RangeSpec apply(long offset, @Nullable RangeSpec prev) {
    if (prev == null || !prev.limit().isPresent()) {
      return RangeSpec.of(offset, maxLimit);
    }
    long limit = prev.limit().getAsLong();
    return RangeSpec.of(offset, Math.min(limit, maxLimit));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MaxLimitRangeSpecFunction)) {
      return false;
    }
    MaxLimitRangeSpecFunction that = (MaxLimitRangeSpecFunction) o;
    return maxLimit == that.maxLimit;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(maxLimit);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("maxLimit", maxLimit).toString();
  }
}
