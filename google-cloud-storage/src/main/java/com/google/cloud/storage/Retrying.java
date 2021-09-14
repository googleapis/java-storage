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

import com.google.cloud.BaseService;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.util.concurrent.Callable;
import java.util.function.Function;

public final class Retrying {

  static <T, U> U run(StorageOptions options, Callable<T> c, Function<T, U> f) {
    try {
      T answer =
          runWithRetries(
              c, options.getRetrySettings(), BaseService.EXCEPTION_HANDLER, options.getClock());
      return answer == null ? null : f.apply(answer);
    } catch (RetryHelperException e) {
      throw StorageException.translateAndThrow(e);
    }
  }
}
