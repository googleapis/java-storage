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

import com.google.api.client.util.DateTime;
import com.google.api.core.InternalApi;
import com.google.cloud.storage.Conversions.Codec;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * A collection of general utility functions providing convenience facilities.
 *
 * <p>Nothing in here should be Storage specific. Anything Storage specific should go in an
 * appropriately named and scoped class.
 */
@InternalApi
final class Utils {

  static final DateTimeFormatter RFC_3339_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
  static final Codec<Duration, Long> durationMillisCodec =
      Codec.of(Duration::toMillis, Duration::ofMillis);

  @VisibleForTesting
  static final Codec<OffsetDateTime, DateTime> dateTimeCodec =
      Codec.of(
          odt -> {
            ZoneOffset offset = odt.getOffset();
            int i = Math.toIntExact(TimeUnit.SECONDS.toMinutes(offset.getTotalSeconds()));
            return new DateTime(odt.toInstant().toEpochMilli(), i);
          },
          dt -> {
            long milli = dt.getValue();
            int timeZoneShiftMinutes = dt.getTimeZoneShift();

            Duration timeZoneShift = Duration.of(timeZoneShiftMinutes, ChronoUnit.MINUTES);

            int hours = Math.toIntExact(timeZoneShift.toHours());
            int minutes =
                Math.toIntExact(
                    timeZoneShift.minusHours(timeZoneShift.toHours()).getSeconds() / 60);
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(hours, minutes);

            return Instant.ofEpochMilli(milli).atOffset(offset);
          });

  private Utils() {}

  /**
   * If the value provided as {@code t} is non-null, consume it via {@code c}.
   *
   * <p>Helper method to allow for more terse expression of:
   *
   * <pre>{@code
   * if (t != null) {
   *   x.setT(t);
   * }
   * }</pre>
   */
  @InternalApi
  static <T> void ifNonNull(@Nullable T t, Consumer<T> c) {
    if (t != null) {
      c.accept(t);
    }
  }

  /**
   * If the value provided as {@code t} is non-null, transform it using {@code map} and consume it
   * via {@code c}.
   *
   * <p>Helper method to allow for more terse expression of:
   *
   * <pre>{@code
   * if (t != null) {
   *   x.setT(map.apply(t));
   * }
   * }</pre>
   */
  @InternalApi
  static <T1, T2> void ifNonNull(@Nullable T1 t, Function<T1, T2> map, Consumer<T2> c) {
    if (t != null) {
      c.accept(map.apply(t));
    }
  }

  /**
   * Convenience method to "lift" a method reference to a {@link Function}.
   *
   * <p>While a method reference can be pass as an argument to a method which expects a {@code
   * Function} it does not then allow calling {@link Function#andThen(Function) #andThen(Function)}.
   * This method forces the method reference to be a {@code Function} thereby allowing {@code
   * #andThen} composition.
   */
  @InternalApi
  static <T1, T2> Function<T1, T2> lift(Function<T1, T2> f) {
    return f;
  }

  /**
   * Several properties are translating lists of one type to another. This convenience method allows
   * specifying a mapping function and composing as part of an {@code #isNonNull} definition.
   */
  @InternalApi
  static <T1, T2> Function<List<T1>, ImmutableList<T2>> toImmutableListOf(Function<T1, T2> f) {
    return l -> l.stream().map(f).collect(ImmutableList.toImmutableList());
  }

  static final <T> T todo() {
    throw new IllegalStateException("Not yet implemented");
  }

  static final <T1, T2> T2 todo(T1 t1) {
    throw new IllegalStateException("Not yet implemented");
  }
}
