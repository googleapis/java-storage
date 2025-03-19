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
public final class MaxLengthRangeSpecFunction extends RangeSpecFunction {
  static final MaxLengthRangeSpecFunction INSTANCE = new MaxLengthRangeSpecFunction(0);
  private final long maxLength;

  MaxLengthRangeSpecFunction(long maxLength) {
    this.maxLength = maxLength;
  }

  public long getMaxLength() {
    return maxLength;
  }

  public MaxLengthRangeSpecFunction withMaxLength(long maxLength) {
    checkArgument(maxLength >= 0, "maxLength >= 0 (%s >= 0)", maxLength);
    return new MaxLengthRangeSpecFunction(maxLength);
  }

  @Override
  public RangeSpec apply(long offset, @Nullable RangeSpec prev) {
    if (prev == null || !prev.maxLength().isPresent()) {
      return RangeSpec.of(offset, maxLength);
    }
    long limit = prev.maxLength().getAsLong();
    return RangeSpec.of(offset, Math.min(limit, maxLength));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MaxLengthRangeSpecFunction)) {
      return false;
    }
    MaxLengthRangeSpecFunction that = (MaxLengthRangeSpecFunction) o;
    return maxLength == that.maxLength;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(maxLength);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("maxLength", maxLength).toString();
  }
}
