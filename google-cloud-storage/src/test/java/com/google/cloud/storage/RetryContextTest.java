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

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ResourceExhaustedException;
import com.google.cloud.storage.Backoff.Jitterer;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.cloud.storage.RetryContext.OnSuccess;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import io.grpc.Status.Code;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public final class RetryContextTest {
  private static final OnSuccess NOOP = () -> {};

  private TestApiClock testClock;
  private TestScheduledExecutorService scheduledExecutorService;

  @Before
  public void setUp() throws Exception {
    testClock = TestApiClock.tickBy(0, Duration.ofMillis(1));
    scheduledExecutorService = new TestScheduledExecutorService(testClock);
  }

  @Test
  public void retryable_when_maxAttemptBudget_consumed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService, maxAttempts(1), Retrying.alwaysRetry(), Jitterer.noJitter());

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
                  "Operation failed to complete within attempt budget (attempts: 1, maxAttempts: 1, elapsed: PT0.001S, nextBackoff: PT3S)");
        });
  }

  @Test
  public void retryable_maxAttemptBudget_still_available() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService, maxAttempts(2), Retrying.alwaysRetry(), Jitterer.noJitter());

    ctx.recordError(t1, NOOP, failOnFailure());
  }

  @Test
  public void
      retryable_when_maxAttemptBudget_multipleAttempts_previousErrorsIncludedAsSuppressed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    Throwable t2 = apiException(Code.INTERNAL, "{internal}");
    Throwable t3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService, maxAttempts(3), Retrying.alwaysRetry(), Jitterer.noJitter());

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
                  "Operation failed to complete within attempt budget (attempts: 3, maxAttempts: 3, elapsed: PT15.003S, nextBackoff: PT3S) previous failures follow in order of occurrence",
                  "{unavailable}",
                  "{internal}");
        });
  }

  @Test
  public void nonretryable_regardlessOfAttemptBudget() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService, maxAttempts(3), Retrying.neverRetry(), Jitterer.noJitter());

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
                  "Unretryable error (attempts: 1, maxAttempts: 3, elapsed: PT0.001S, nextBackoff: PT3S)");
        });
  }

  @Test
  public void nonRetryable_regardlessOfAttemptBudget_previousErrorsIncludedAsSuppressed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    Throwable t2 = apiException(Code.INTERNAL, "{internal}");
    Throwable t3 = apiException(Code.RESOURCE_EXHAUSTED, "{resource exhausted}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService,
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            },
            Jitterer.noJitter());

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
                  "Unretryable error (attempts: 3, maxAttempts: 6, elapsed: PT15.003S, nextBackoff: PT3S) previous failures follow in order of occurrence",
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
            scheduledExecutorService,
            maxAttempts(6),
            new BasicResultRetryAlgorithm<Object>() {
              @Override
              public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
                return !(previousThrowable instanceof ResourceExhaustedException);
              }
            },
            Jitterer.noJitter());

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
              .containsExactly(
                  "Unretryable error (attempts: 1, maxAttempts: 6, elapsed: PT9.003S, nextBackoff: PT3S)");
        });
  }

  @Test
  public void preservesCauseOfFailureAsReturnedFailure() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService, maxAttempts(1), Retrying.alwaysRetry(), Jitterer.noJitter());

    ctx.recordError(t1, failOnSuccess(), actual -> assertThat(actual).isEqualTo(t1));
  }

  @Test
  public void retryable_when_timeoutBudget_consumed() {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable 1}");
    Throwable t2 = apiException(Code.UNAVAILABLE, "{unavailable 2}");
    Throwable t3 = apiException(Code.UNAVAILABLE, "{unavailable 3}");
    RetryContext ctx =
        RetryContext.of(
            scheduledExecutorService,
            RetryingDependencies.simple(
                testClock,
                RetrySettings.newBuilder()
                    .setInitialRetryDelayDuration(Duration.ofSeconds(2))
                    .setMaxRetryDelayDuration(Duration.ofSeconds(6))
                    .setTotalTimeoutDuration(Duration.ofSeconds(24))
                    .setRetryDelayMultiplier(2.0)
                    .build()),
            Retrying.alwaysRetry(),
            Jitterer.noJitter());

    testClock.advance(Duration.ofSeconds(3));
    ctx.recordError(t1, NOOP, failOnFailure());
    testClock.advance(Duration.ofSeconds(3));
    ctx.recordError(t2, NOOP, failOnFailure());
    testClock.advance(Duration.ofSeconds(3));
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
                  "Operation failed to complete within backoff budget (attempts: 3, elapsed: PT24S, nextBackoff: EXHAUSTED, timeout: PT24S) previous failures follow in order of occurrence",
                  "{unavailable 1}",
                  "{unavailable 2}");
        });
  }

  @Test
  public void recordErrorWhileAlreadyInBackoffTruncatesExistingBackoffAndReevaluates()
      throws Exception {
    Throwable t1 = apiException(Code.UNAVAILABLE, "{unavailable 1}");
    Throwable t2 = apiException(Code.UNAVAILABLE, "{unavailable 2}");
    Throwable t3 = apiException(Code.UNAVAILABLE, "{unavailable 3}");
    Throwable t4 = apiException(Code.ABORTED, "{aborted}");
    ScheduledExecutorService scheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor();
    try {
      RetryContext ctx =
          RetryContext.of(
              scheduledExecutorService,
              RetryingDependencies.simple(
                  NanoClock.getDefaultClock(),
                  RetrySettings.newBuilder()
                      .setMaxAttempts(4)
                      .setInitialRetryDelayDuration(Duration.ofMillis(250))
                      .setMaxRetryDelayDuration(Duration.ofSeconds(1))
                      .setRetryDelayMultiplier(2.0)
                      .build()),
              Retrying.alwaysRetry(),
              Jitterer.noJitter());

      BlockingOnSuccess s1 = new BlockingOnSuccess();

      ctx.recordError(t1, s1, failOnFailure());
      ctx.recordError(t2, NOOP, failOnFailure());
      s1.release();
      ctx.awaitBackoffComplete();
      ctx.recordError(t3, NOOP, failOnFailure());
      ctx.awaitBackoffComplete();
      AtomicReference<Throwable> t = new AtomicReference<>(null);
      ctx.recordError(t4, failOnSuccess(), t::set);

      Throwable actual = t.get();
      String messagesToText = TestUtils.messagesToText(actual);
      assertAll(
          () -> assertThat(messagesToText).contains("{aborted}"),
          () ->
              assertThat(messagesToText)
                  .contains(
                      "Operation failed to complete within attempt budget (attempts: 4, maxAttempts: 4, elapsed: PT"),
          () ->
              assertThat(messagesToText)
                  .contains(", nextBackoff: PT1S) previous failures follow in order of occurrence"),
          () -> assertThat(messagesToText).containsMatch("\\{unavailable 2}\n\\s*Previous"),
          () ->
              assertThat(messagesToText)
                  .contains(
                      "Previous backoff interrupted by this error (previousBackoff: PT0.25S, elapsed: PT"));
    } finally {
      scheduledExecutorService.shutdownNow();
      scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private static ApiException apiException(Code code, String message) {
    return ApiExceptionFactory.createException(message, null, GrpcStatusCode.of(code), false);
  }

  private MaxAttemptRetryingDependencies maxAttempts(int maxAttempts) {
    return new MaxAttemptRetryingDependencies(
        RetrySettings.newBuilder()
            .setMaxAttempts(maxAttempts)
            .setInitialRetryDelayDuration(Duration.ofSeconds(3))
            .setMaxRetryDelayDuration(Duration.ofSeconds(35))
            .setRetryDelayMultiplier(1.0)
            .build(),
        testClock);
  }

  static <T extends Throwable> OnFailure<T> failOnFailure() {
    InvocationTracer invocationTracer = new InvocationTracer("Unexpected onFailure invocation");
    return t -> {
      invocationTracer.addSuppressed(t);
      throw invocationTracer;
    };
  }

  static OnSuccess failOnSuccess() {
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

  static final class BlockingOnSuccess implements OnSuccess {
    private final CountDownLatch cdl;

    BlockingOnSuccess() {
      this.cdl = new CountDownLatch(1);
    }

    @Override
    public void onSuccess() {
      try {
        cdl.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }

    public void release() {
      cdl.countDown();
    }
  }
}
