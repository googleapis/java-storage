/*
 * Copyright 2023 Google LLC
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
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class ResumableSession<T> {

  ResumableSession() {}

  abstract ResumableOperationResult<@Nullable T> put(
      RewindableHttpContent content, HttpContentRange contentRange);

  abstract ResumableOperationResult<@Nullable T> query();

  static JsonResumableSession json(
      HttpClientContext context,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg,
      JsonResumableWrite resumableWrite) {
    return new JsonResumableSession(context, deps, alg, resumableWrite);
  }
}
