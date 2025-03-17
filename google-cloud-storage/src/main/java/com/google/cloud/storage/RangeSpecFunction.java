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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A specialized BiFunction to produce a RangeSpec given an offset and a possible previous
 * RangeSpec.
 */
@FunctionalInterface
interface RangeSpecFunction {

  /**
   * Given an offset to read from, and the previously read {@link RangeSpec} return a new {@code
   * RangeSpec} representing the range to read next.
   */
  RangeSpec apply(long offset, @Nullable RangeSpec prev);

  default RangeSpecFunction andThen(RangeSpecFunction then) {
    return new AndThenRangeSpecFunction(this, then);
  }

  static LinearExponentialRangeSpecFunction linearExponential() {
    return LinearExponentialRangeSpecFunction.INSTANCE;
  }

  static MaxLimitRangeSpecFunction maxLimit(long maxLimit) {
    return MaxLimitRangeSpecFunction.INSTANCE.withMaxLimit(maxLimit);
  }
}
