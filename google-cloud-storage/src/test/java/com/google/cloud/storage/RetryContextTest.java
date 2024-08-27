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

import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ResourceExhaustedException;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.cloud.storage.RetryContext.OnSuccess;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import io.grpc.Status.Code;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public final class RetryContextTest {
  private static final OnSuccess NOOP = () -> {};

  @Test
  public void retryable_cancelledException_when_maxAttemptBudget_consumed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx = RetryContext.of(maxAttempts(1), Retrying.alwaysRetry());

    ctx.recordError(
        t1,
        failOnSuccess(),
        actual -> {
          assertThat(actual).isEqualTo(t1);
          Throwable[] suppressed = actual.getSuppressed();
          List<String> suppressedMessages =
              Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.toList());
          assertThat(suppressedMessages)
              .containsExactly(
                  "Operation failed to complete within retry limit (attempts: 1, maxAttempts: 1)");
        });
  }

  @Test
  public void retryable_maxAttemptBudget_still_available() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx = RetryContext.of(maxAttempts(2), Retrying.alwaysRetry());

    ctx.recordError(t1, NOOP, failOnFailure());
  }

  @Test
  public void
      retryable_cancelledException_when_maxAttemptBudget_multipleAttempts_previousErrorsIncludedAsSuppressed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    Throwable t2 = apiException(Code.INTERNAL, "{internal}");
    Throwable t3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");
    RetryContext ctx = RetryContext.of(maxAttempts(3), Retrying.alwaysRetry());

    ctx.recordError(t1, NOOP, failOnFailure());
    ctx.recordError(t2, NOOP, failOnFailure());

    ctx.recordError(
        t3,
        failOnSuccess(),
        actual -> {
          assertThat(actual).isEqualTo(t3);
          Throwable[] suppressed = actual.getSuppressed();
          List<String> suppressedMessages =
              Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.toList());
          assertThat(suppressedMessages)
              .containsExactly(
                  "Operation failed to complete within retry limit (attempts: 3, maxAttempts: 3) previous failures follow in order of occurrence",
                  "{unavailable}",
                  "{internal}");
        });
  }

  @Test
  public void nonretryable_cancelledException_regardlessOfAttemptBudget() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx = RetryContext.of(maxAttempts(3), Retrying.neverRetry());

    ctx.recordError(
        t1,
        failOnSuccess(),
        actual -> {
          assertThat(actual).isEqualTo(t1);
          Throwable[] suppressed = actual.getSuppressed();
          List<String> suppressedMessages =
              Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.toList());
          assertThat(suppressedMessages)
              .containsExactly("Unretryable error (attempts: 1, maxAttempts: 3)");
        });
  }

  @Test
  public void nonRetryable_regardlessOfAttemptBudget_previousErrorsIncludedAsSuppressed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    Throwable t2 = apiException(Code.INTERNAL, "{internal}");
    Throwable t3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");
    RetryContext ctx =
        RetryContext.of(
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            });

    ctx.recordError(t1, NOOP, failOnFailure());
    ctx.recordError(t2, NOOP, failOnFailure());

    ctx.recordError(
        t3,
        failOnSuccess(),
        actual -> {
          assertThat(actual).isEqualTo(t3);
          Throwable[] suppressed = actual.getSuppressed();
          List<String> suppressedMessages =
              Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.toList());
          assertThat(suppressedMessages)
              .containsExactly(
                  "Unretryable error (attempts: 3, maxAttempts: 6) previous failures follow in order of occurrence",
                  "{unavailable}",
                  "{internal}");
        });
  }

  @Test
  public void resetDiscardsPreviousErrors() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    Throwable t2 = apiException(Code.INTERNAL, "{internal}");
    Throwable t3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");
    RetryContext ctx =
        RetryContext.of(
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            });

    ctx.recordError(t1, NOOP, failOnFailure());
    ctx.recordError(t2, NOOP, failOnFailure());
    ctx.reset();

    ctx.recordError(
        t3,
        failOnSuccess(),
        actual -> {
          assertThat(actual).isEqualTo(t3);
          Throwable[] suppressed = actual.getSuppressed();
          List<String> suppressedMessages =
              Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.toList());
          assertThat(suppressedMessages)
              .containsExactly("Unretryable error (attempts: 1, maxAttempts: 6)");
        });
  }

  @Test
  public void preservesCauseOfFailureAsReturnedFailure() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx = RetryContext.of(maxAttempts(1), Retrying.alwaysRetry());

    ctx.recordError(t1, failOnSuccess(), actual -> assertThat(actual).isEqualTo(t1));
  }

  private static ApiException apiException(Code code, String message) {
    return ApiExceptionFactory.createException(message, null, GrpcStatusCode.of(code), false);
  }

  private static MaxAttemptRetryingDependencies maxAttempts(int maxAttempts) {
    return new MaxAttemptRetryingDependencies(
        RetrySettings.newBuilder().setMaxAttempts(maxAttempts).build(),
        NanoClock.getDefaultClock());
  }

  private static <T extends Throwable> OnFailure<T> failOnFailure() {
    InvocationTracer invocationTracer = new InvocationTracer("Unexpected onFailure invocation");
    return t -> {
      throw invocationTracer;
    };
  }

  private static OnSuccess failOnSuccess() {
    InvocationTracer invocationTracer = new InvocationTracer("Unexpected onSuccess invocation");
    return () -> {
      throw invocationTracer;
    };
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

  private static final class InvocationTracer extends RuntimeException {
    private InvocationTracer(String message) {
      super(message);
    }
  }
}
