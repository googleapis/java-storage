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
import com.google.api.core.InternalExtensionOnly;
import javax.annotation.concurrent.Immutable;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A specialized BiFunction to produce a {@link RangeSpec} given an offset and a possible previous
 * {@code RangeSpec}.
 */
@BetaApi
@Immutable
@InternalExtensionOnly
public abstract class RangeSpecFunction {

  RangeSpecFunction() {}

  /**
   * Given an offset to read from, and the previously read {@link RangeSpec} return a new {@code
   * RangeSpec} representing the range to read next.
   */
  @BetaApi
  abstract RangeSpec apply(long offset, @Nullable RangeSpec prev);

  @BetaApi
  public RangeSpecFunction andThen(RangeSpecFunction then) {
    requireNonNull(then, "then must be non null");
    return new AndThenRangeSpecFunction(this, then);
  }

  @BetaApi
  public static LinearExponentialRangeSpecFunction linearExponential() {
    return LinearExponentialRangeSpecFunction.INSTANCE;
  }

  @BetaApi
  public static MaxLimitRangeSpecFunction maxLimit(long maxLimit) {
    return MaxLimitRangeSpecFunction.INSTANCE.withMaxLimit(maxLimit);
  }
}
