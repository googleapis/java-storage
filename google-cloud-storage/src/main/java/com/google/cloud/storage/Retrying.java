/*
 * Copyright 2021 Google LLC
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

import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.storage.Backoff.Jitterer;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.spi.v1.HttpRpcContext;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

final class Retrying {

  /**
   * A convenience wrapper around {@link com.google.cloud.RetryHelper#runWithRetries(Callable,
   * RetrySettings, ResultRetryAlgorithm, ApiClock)} that gives us centralized error translation and
   * reduces some duplication in how we resolved the {@link RetrySettings} and {@link ApiClock}.
   *
   * @param <T> The result type of {@code c}
   * @param <U> The result type of any mapping that takes place via {@code f}
   * @param options The {@link HttpStorageOptions} which {@link RetrySettings} and {@link ApiClock}
   *     will be resolved from.
   * @param algorithm The {@link ResultRetryAlgorithm} to use when determining if a retry is
   *     possible
   * @param c The {@link Callable} which will be passed to runWithRetries producing some {@code T},
   *     can optionally return null
   * @param f A post process mapping {@link Function} which can be used to transform the result from
   *     {@code c} if it is successful and non-null
   * @return A {@code U} (possibly null) after applying {@code f} to the result of {@code c}
   * @throws StorageException if {@code c} fails due to any retry exhaustion
   */
  static <T, U> U run(
      HttpStorageOptions options,
      ResultRetryAlgorithm<?> algorithm,
      Callable<T> c,
      Function<T, U> f) {
    HttpRpcContext httpRpcContext = HttpRpcContext.getInstance();
    try {
      httpRpcContext.newInvocationId();
      return run(
          RetryingDependencies.simple(options.getClock(), options.getRetrySettings()),
          algorithm,
          c,
          f::apply);
    } finally {
      httpRpcContext.clearInvocationId();
    }
  }

  /**
   * A convenience wrapper around {@link com.google.cloud.RetryHelper#runWithRetries(Callable,
   * RetrySettings, ResultRetryAlgorithm, ApiClock)} that gives us centralized error translation and
   * reduces some duplication in how we resolved the {@link RetrySettings} and {@link ApiClock}.
   *
   * @param <T> The result type of {@code c}
   * @param <U> The result type of any mapping that takes place via {@code f}
   * @param deps The {@link RetryingDependencies} which {@link RetrySettings} and {@link ApiClock}
   *     will be resolved from.
   * @param algorithm The {@link ResultRetryAlgorithm} to use when determining if a retry is
   *     possible
   * @param c The {@link Callable} which will be passed to runWithRetries producing some {@code T},
   *     can optionally return null
   * @param f A post process mapping {@link Function} which can be used to transform the result from
   *     {@code c} if it is successful and non-null
   * @return A {@code U} (possibly null) after applying {@code f} to the result of {@code c}
   * @throws StorageException if {@code c} fails due to any retry exhaustion
   */
  static <T, U> U run(
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> algorithm,
      Callable<T> c,
      Decoder<T, U> f) {
    RetryContext ctx =
        RetryContext.of(
            RetryContext.directScheduledExecutorService(),
            deps,
            algorithm,
            Jitterer.threadLocalRandom());
    AtomicReference<Exception> failure = new AtomicReference<>();
    AtomicBoolean attemptAgain = new AtomicBoolean(false);
    do {
      attemptAgain.set(false);
      try {
        T result = c.call();
        return result == null ? null : f.decode(result);
      } catch (StorageException se) {
        // we hope for this case
        ctx.recordError(se, () -> attemptAgain.set(true), failure::set);
      } catch (IllegalArgumentException iae) {
        // IllegalArgumentException can happen if there is no json in the body and we try
        // to parse it Our retry algorithms have special case for this, so in an effort to
        // keep compatibility with those existing behaviors, explicitly rethrow an
        // IllegalArgumentException that may have happened
        ctx.recordError(iae, () -> attemptAgain.set(true), failure::set);
      } catch (Exception e) {
        // Wire in this fall through just in case.
        // all of our retry algorithms are centered around StorageException so this helps
        // those be more effective
        ctx.recordError(StorageException.coalesce(e), () -> attemptAgain.set(true), failure::set);
      }
    } while (attemptAgain.get());

    Exception throwable = failure.get();
    if (throwable instanceof StorageException) {
      throw (StorageException) throwable;
    } else {
      throw StorageException.coalesce(throwable);
    }
  }

  @NonNull
  static GrpcCallContext newCallContext() {
    return GrpcCallContext.createDefault()
        .withExtraHeaders(
            ImmutableMap.of(
                "x-goog-gcs-idempotency-token", ImmutableList.of(UUID.randomUUID().toString())));
  }

  static ResultRetryAlgorithm<?> neverRetry() {
    return new BasicResultRetryAlgorithm<Object>() {
      @Override
      public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
        return false;
      }
    };
  }

  static ResultRetryAlgorithm<?> alwaysRetry() {
    return new BasicResultRetryAlgorithm<Object>() {
      @Override
      public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
        return true;
      }
    };
  }

  /**
   * Rather than requiring a full set of {@link StorageOptions} to be passed specify what we
   * actually need and have StorageOptions implement this interface.
   */
  interface RetryingDependencies {

    RetrySettings getRetrySettings();

    ApiClock getClock();

    static RetryingDependencies attemptOnce() {
      return RetryingDependencies.simple(
          NanoClock.getDefaultClock(), RetrySettings.newBuilder().setMaxAttempts(1).build());
    }

    static RetryingDependencies simple(ApiClock clock, RetrySettings retrySettings) {
      return new SimpleRetryingDependencies(clock, retrySettings);
    }
  }

  private static final class SimpleRetryingDependencies implements RetryingDependencies {
    private final ApiClock clock;
    private final RetrySettings retrySettings;

    private SimpleRetryingDependencies(ApiClock clock, RetrySettings retrySettings) {
      this.retrySettings = retrySettings;
      this.clock = clock;
    }

    @Override
    public ApiClock getClock() {
      return clock;
    }

    @Override
    public RetrySettings getRetrySettings() {
      return retrySettings;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("clock", clock)
          .add("retrySettings", retrySettings)
          .toString();
    }
  }
}
