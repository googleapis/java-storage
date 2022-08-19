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

import com.google.api.client.util.DateTime;
import com.google.api.core.InternalApi;
import com.google.cloud.storage.Conversions.Codec;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.ProjectName;
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
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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

  static final Codec<OffsetDateTime, DateTime> nullableDateTimeCodec = dateTimeCodec.nullable();

  /**
   * Define a Codec which encapsulates the logic necessary to handle encoding and decoding bucket
   * names.
   *
   * <p>The "Model Type" in this case is the raw bucket name as would be read from {@link
   * BucketInfo#getName()}. The "Proto Type" in this case is the OnePlatform formatted
   * representation of the bucket name.
   *
   * <p>As of the time of writing this, project scoped buckets are not implemented by the backend
   * service. While we need to be cognisant that they are on the horizon, we do not need to track
   * any data related to this future feature. As such, this method attempts to make it easier to
   * work with bucket names that require the OnePlatform format while still preventing any subtle
   * bugs happening to customers if they happen to attempt to use project scoped bucket features in
   * this library once the service does support it.
   *
   * <p>TODO: this will need to change once the project scoped buckets first class feature work is
   * done.
   */
  static final Codec<String, String> bucketNameCodec =
      Codec.of(
          bucket -> {
            requireNonNull(bucket, "bucket must be non null");
            if (bucket.startsWith("projects/")) {
              if (bucket.startsWith("projects/_")) {
                return bucket;
              } else {
                throw new IllegalArgumentException(
                    "Project scoped buckets are not supported by this version of the library. (bucket = "
                        + bucket
                        + ")");
              }
            } else {
              return "projects/_/buckets/" + bucket;
            }
          },
          resourceName -> {
            requireNonNull(resourceName, "resourceName must be non null");
            if (BucketName.isParsableFrom(resourceName)) {
              BucketName parse = BucketName.parse(resourceName);
              return parse.getBucket();
            } else {
              return resourceName;
            }
          });

  /**
   * Define a Codec which encapsulates the logic necessary to handle encoding and decoding project
   * names.
   */
  static final Codec<String, String> projectNameCodec =
      Codec.of(
          project -> {
            requireNonNull(project, "project must be non null");
            if (project.startsWith("projects/")) {
              return project;
            } else {
              return "projects/" + project;
            }
          },
          resourceName -> {
            requireNonNull(resourceName, "resourceName must be non null");
            if (ProjectName.isParsableFrom(resourceName)) {
              ProjectName parse = ProjectName.parse(resourceName);
              return parse.getProject();
            } else {
              return resourceName;
            }
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
      T2 apply = map.apply(t);
      if (apply != null) {
        c.accept(apply);
      }
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

  /**
   * Convenience method to resolve the first non-null {@code T} from an array of suppliers.
   *
   * <p>Each supplier will have {@link Supplier#get()} called, and if non-null the value will be
   * returned.
   */
  @NonNull
  @SafeVarargs
  static <T> T firstNonNull(Supplier<@Nullable T>... ss) {
    for (Supplier<T> s : ss) {
      T t = s.get();
      if (t != null) {
        return t;
      }
    }
    throw new IllegalStateException("Unable to resolve non-null value");
  }
}
