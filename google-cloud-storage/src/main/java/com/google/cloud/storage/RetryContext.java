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

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import io.grpc.Status.Code;
import java.util.LinkedList;
import java.util.List;

final class RetryContext {

  private static final GrpcStatusCode CANCELLED = GrpcStatusCode.of(Code.CANCELLED);
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

  public void recordError(Throwable t, OnSuccess onSuccess, OnFailure onFailure) {
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
          String.format("%s (attempts: %d, maxAttempts: %d)", msgPrefix, failureCount, maxAttempts);
      ApiException cancelled = ApiExceptionFactory.createException(msg, t, CANCELLED, false);
      for (Throwable failure : failures) {
        cancelled.addSuppressed(failure);
      }
      onFailure.onFailure(cancelled);
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
  interface OnFailure {
    void onFailure(Throwable t);
  }
}
