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

package com.google.cloud.storage.otel;

import com.google.api.core.ApiFuture;
import io.opentelemetry.api.trace.StatusCode;
import java.util.Map;
import javax.annotation.Nonnull;

class NoOpOpenTelemetryInstance implements OpenTelemetryTraceUtil {

  @Override
  public OpenTelemetryTraceUtil.Span startSpan(String spanName) {
    return new Span();
  }

  @Override
  public OpenTelemetryTraceUtil.Span startSpan(
      String spanName, OpenTelemetryTraceUtil.Context parent) {
    return new Span();
  }

  @Nonnull
  @Override
  public Span currentSpan() {
    return new Span();
  }

  @Nonnull
  @Override
  public Context currentContext() {
    return new Context();
  }

  static class Span implements OpenTelemetryTraceUtil.Span {
    @Override
    public void end() {}

    @Override
    public void end(Throwable error) {}

    @Override
    public <T> void endAtFuture(ApiFuture<T> futureValue) {}

    @Override
    public OpenTelemetryTraceUtil.Span recordException(Throwable error) {
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span setStatus(StatusCode status, String name) {
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span addEvent(String name) {
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span addEvent(String name, Map<String, Object> attributes) {
      return this;
    }

    @Override
    public Scope makeCurrent() {
      return new Scope();
    }
  }

  static class Context implements OpenTelemetryTraceUtil.Context {
    @Override
    public Scope makeCurrent() {
      return new Scope();
    }
  }

  static class Scope implements OpenTelemetryTraceUtil.Scope {
    @Override
    public void close() {}
  }
}
