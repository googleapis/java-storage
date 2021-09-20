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

import static com.google.cloud.RetryHelper.runWithRetries;

import com.google.api.core.ApiClock;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.util.concurrent.Callable;
import java.util.function.Function;

final class Retrying {

  /**
   * A convenience wrapper around {@link com.google.cloud.RetryHelper#runWithRetries(Callable,
   * RetrySettings, ResultRetryAlgorithm, ApiClock)} that gives us centralized error translation and
   * reduces some duplication in how we resolved the {@link RetrySettings} and {@link ApiClock}.
   *
   * @param <T> The result type of {@code c}
   * @param <U> The result type of any mapping that takes place via {@code f}
   * @param options The {@link StorageOptions} which {@link RetrySettings} and {@link ApiClock} will
   *     be resolved from.
   * @param exceptionHandler The {@link ExceptionHandler} to use when determining if a retry is
   *     possible
   * @param c The {@link Callable} which will be passed to runWithRetries producing some {@code T},
   *     can optionally return null
   * @param f A post process mapping {@link Function} which can be used to transform the result from
   *     {@code c} if it is successful and non-null
   * @return A {@code U} (possibly null) after applying {@code f} to the result of {@code c}
   * @throws StorageException if {@code c} fails due to any retry exhaustion
   */
  static <T, U> U run(
      StorageOptions options, ExceptionHandler exceptionHandler, Callable<T> c, Function<T, U> f) {
    try {
      T result =
          runWithRetries(c, options.getRetrySettings(), exceptionHandler, options.getClock());
      return result == null ? null : f.apply(result);
    } catch (RetryHelperException e) {
      throw StorageException.coalesce(e);
    }
  }
}
