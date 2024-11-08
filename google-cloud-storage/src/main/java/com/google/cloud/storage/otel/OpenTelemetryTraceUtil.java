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
import com.google.cloud.storage.StorageOptions;
import io.opentelemetry.api.trace.StatusCode;
import java.util.Map;
import javax.annotation.Nonnull;

public interface OpenTelemetryTraceUtil {

  static OpenTelemetryTraceUtil getInstance(@Nonnull StorageOptions storageOptions) {
    boolean createNoOp = storageOptions.getOpenTelemetrySdk() == null;

    if (createNoOp) {
      return new NoOpOpenTelemetryInstance();
    } else {
      return new OpenTelemetryInstance(storageOptions);
    }
  }

  /** Represents a trace span. */
  interface Span {
    Span recordException(Throwable error);

    Span setStatus(StatusCode status, String name);
    /** Adds the given event to this span. */
    Span addEvent(String name);

    /** Adds the given event with the given attributes to this span. */
    Span addEvent(String name, Map<String, Object> attributes);

    /** Marks this span as the current span. */
    Scope makeCurrent();

    /** Ends this span. */
    void end();

    /** Ends this span in an error. */
    void end(Throwable error);

    /**
     * If an operation ends in the future, its relevant span should end _after_ the future has been
     * completed. This method "appends" the span completion code at the completion of the given
     * future. In order for telemetry info to be recorded, the future returned by this method should
     * be completed.
     */
    <T> void endAtFuture(ApiFuture<T> futureValue);
  }

  /** Represents a trace context. */
  interface Context {
    /** Makes this context the current context. */
    Scope makeCurrent();
  }

  /** Represents a trace scope. */
  interface Scope extends AutoCloseable {
    /** Closes the current scope. */
    void close();
  }

  /** Starts a new span with the given name, sets it as the current span, and returns it. */
  Span startSpan(String spanName, String module);

  /**
   * Starts a new span with the given name and the given context as its parent, sets it as the
   * current span, and returns it.
   */
  Span startSpan(String spanName, String module, Context parent);

  /** Returns the current span. */
  @Nonnull
  Span currentSpan();

  /** Returns the current Context. */
  @Nonnull
  Context currentContext();
}
