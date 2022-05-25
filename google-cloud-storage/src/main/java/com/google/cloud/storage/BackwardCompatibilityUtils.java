/*
 * Copyright 2022 Google LLC
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

import com.google.cloud.storage.Conversions.Codec;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A collection of utilities that only exist to enable backward compatibility.
 *
 * <p>In general, the expectation is that any references to this class only come from @Deprecated
 * things.
 */
final class BackwardCompatibilityUtils {

  @SuppressWarnings("RedundantTypeArguments")
  // the <Long, OffsetDateTime> doesn't auto carry all the way through like intellij thinks it
  // would.
  static final Codec<@Nullable Long, @Nullable OffsetDateTime> millisOffsetDateTimeCodec =
      Codec.<Long, OffsetDateTime>of(
              l ->
                  Instant.ofEpochMilli(requireNonNull(l, "l must be non null"))
                      .atOffset(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())),
              odt -> requireNonNull(odt, "odt must be non null").toInstant().toEpochMilli())
          .nullable();

  static final Codec<Long, OffsetDateTime> millisUtcCodec =
      Codec.of(
          l ->
              Instant.ofEpochMilli(requireNonNull(l, "l must be non null"))
                  .atOffset(ZoneOffset.UTC),
          odt -> requireNonNull(odt, "odt must be non null").toInstant().toEpochMilli());

  static final Codec<@Nullable Duration, @Nullable Long> nullableDurationMillisCodec =
      Utils.durationMillisCodec.nullable();

  private BackwardCompatibilityUtils() {}
}
