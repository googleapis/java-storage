/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.google.cloud.storage.otel;

import javax.annotation.Nonnull;

class NoOpOpenTelemetryInstance implements OpenTelemetryTraceUtil {

  @Override
  public Span startSpan(String spanName) {
    return null;
  }

  @Override
  public Span startSpan(String spanName, Context parent) {
    return null;
  }

  @Nonnull
  @Override
  public Span currentSpan() {
    return null;
  }

  @Nonnull
  @Override
  public Context currentContext() {
    return null;
  }
}
