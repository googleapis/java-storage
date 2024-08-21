/*
 * Copyright 2024 Google LLC
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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.CancelledException;
import com.google.api.gax.rpc.ResourceExhaustedException;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import io.grpc.Status.Code;
import org.junit.Test;

public final class RetryContextTest {

  private static final Throwable T1 = apiException(Code.UNAVAILABLE, "{unavailable}");
  private static final Throwable T2 = apiException(Code.INTERNAL, "{internal}");
  private static final Throwable T3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");

  @Test
  public void retriable_cancelledException_when_maxAttemptBudget_consumed() {
    RetryContext ctx = RetryContext.of(maxAttempts(1), Retrying.alwaysRetry());

    CancelledException cancelled =
        assertThrows(CancelledException.class, () -> ctx.recordError(T1));

    assertThat(cancelled).hasCauseThat().isEqualTo(T1);
  }

  @Test
  public void retriable_maxAttemptBudget_still_available() {
    RetryContext ctx = RetryContext.of(maxAttempts(2), Retrying.alwaysRetry());

    ctx.recordError(T1);
  }

  @Test
  public void
      retriable_cancelledException_when_maxAttemptBudget_multipleAttempts_previousErrorsIncludedAsSuppressed() {
    RetryContext ctx = RetryContext.of(maxAttempts(3), Retrying.alwaysRetry());

    ctx.recordError(T1);
    ctx.recordError(T2);

    CancelledException cancelled =
        assertThrows(CancelledException.class, () -> ctx.recordError(T3));

    assertThat(cancelled).hasCauseThat().isEqualTo(T3);
    Throwable[] suppressed = cancelled.getSuppressed();
    assertThat(suppressed).asList().containsExactly(T1, T2);
  }

  @Test
  public void nonRetriable_cancelledException_regardlessOfAttemptBudget() {
    RetryContext ctx = RetryContext.of(maxAttempts(3), Retrying.neverRetry());

    CancelledException cancelled =
        assertThrows(CancelledException.class, () -> ctx.recordError(T1));

    assertThat(cancelled).hasCauseThat().isEqualTo(T1);
  }

  @Test
  public void
      nonRetriable_cancelledException_regardlessOfAttemptBudget_previousErrorsIncludedAsSuppressed() {
    RetryContext ctx =
        RetryContext.of(
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            });

    ctx.recordError(T1);
    ctx.recordError(T2);

    CancelledException cancelled =
        assertThrows(CancelledException.class, () -> ctx.recordError(T3));

    assertThat(cancelled).hasCauseThat().isEqualTo(T3);
    Throwable[] suppressed = cancelled.getSuppressed();
    assertThat(suppressed).asList().containsExactly(T1, T2);
  }

  @Test
  public void resetDiscardsPreviousErrors() {
    RetryContext ctx =
        RetryContext.of(
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            });

    ctx.recordError(T1);
    ctx.recordError(T2);
    ctx.reset();

    CancelledException cancelled =
        assertThrows(CancelledException.class, () -> ctx.recordError(T3));

    assertThat(cancelled).hasCauseThat().isEqualTo(T3);
    Throwable[] suppressed = cancelled.getSuppressed();
    assertThat(suppressed).asList().isEmpty();
  }

  private static ApiException apiException(Code code, String message) {
    return ApiExceptionFactory.createException(message, null, GrpcStatusCode.of(code), false);
  }

  private static MaxAttemptRetryingDependencies maxAttempts(int maxAttempts) {
    return new MaxAttemptRetryingDependencies(
        RetrySettings.newBuilder().setMaxAttempts(maxAttempts).build(),
        NanoClock.getDefaultClock());
  }

  private static final class MaxAttemptRetryingDependencies implements RetryingDependencies {
    private final RetrySettings settings;
    private final ApiClock clock;

    private MaxAttemptRetryingDependencies(RetrySettings settings, ApiClock clock) {
      this.settings = settings;
      this.clock = clock;
    }

    @Override
    public RetrySettings getRetrySettings() {
      return settings;
    }

    @Override
    public ApiClock getClock() {
      return clock;
    }
  }
}
