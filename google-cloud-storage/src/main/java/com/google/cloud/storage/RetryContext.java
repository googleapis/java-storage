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

import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import java.util.LinkedList;
import java.util.List;

final class RetryContext {

  private final RetryingDependencies retryingDependencies;
  private final ResultRetryAlgorithm<?> algorithm;
  private List<Throwable> failures;

  private RetryContext(
      RetryingDependencies retryingDependencies, ResultRetryAlgorithm<?> algorithm) {
    this.retryingDependencies = retryingDependencies;
    this.algorithm = algorithm;
  }

  void reset() {
    failures = new LinkedList<>();
  }

  public <T extends Throwable> void recordError(T t, OnSuccess onSuccess, OnFailure<T> onFailure) {
    int failureCount = failures.size() + 1 /* include t in the count*/;
    int maxAttempts = retryingDependencies.getRetrySettings().getMaxAttempts();
    boolean shouldRetry = algorithm.shouldRetry(t, null);
    String msgPrefix = null;
    if (shouldRetry && failureCount >= maxAttempts) {
      msgPrefix = "Operation failed to complete within retry limit";
    } else if (!shouldRetry) {
      msgPrefix = "Unretryable error";
    }

    if (msgPrefix == null) {
      failures.add(t);
      onSuccess.onSuccess();
    } else {
      String msg =
          String.format(
              "%s (attempts: %d, maxAttempts: %d)%s",
              msgPrefix,
              failureCount,
              maxAttempts,
              failures.isEmpty() ? "" : " previous failures follow in order of occurrence");
      t.addSuppressed(new RetryBudgetExhaustedComment(msg));
      for (Throwable failure : failures) {
        t.addSuppressed(failure);
      }
      onFailure.onFailure(t);
    }
  }

  static RetryContext of(
      RetryingDependencies retryingDependencies, ResultRetryAlgorithm<?> algorithm) {
    RetryContext retryContext = new RetryContext(retryingDependencies, algorithm);
    retryContext.reset();
    return retryContext;
  }

  static RetryContext neverRetry() {
    return new RetryContext(RetryingDependencies.attemptOnce(), Retrying.neverRetry());
  }

  static RetryContextProvider providerFrom(RetryingDependencies deps, ResultRetryAlgorithm<?> alg) {
    return () -> RetryContext.of(deps, alg);
  }

  @FunctionalInterface
  interface RetryContextProvider {
    RetryContext create();
  }

  @FunctionalInterface
  interface OnSuccess {
    void onSuccess();
  }

  @FunctionalInterface
  interface OnFailure<T extends Throwable> {
    void onFailure(T t);
  }

  /**
   * Define a custom exception which can carry a comment about the budget exhaustion, so we can
   * include it as a suppressed exception, but don't fill in any stack frames. This is a throwable
   * only because it is the only way we can include it into an exception that will by default print
   * with the exception stacktrace.
   *
   * @see Throwable#addSuppressed(Throwable)
   */
  private static final class RetryBudgetExhaustedComment extends Throwable {
    private RetryBudgetExhaustedComment(String comment) {
      super(comment, /*cause=*/ null, /*enableSuppression=*/ true, /*writableStackTrace=*/ false);
    }
  }
}
