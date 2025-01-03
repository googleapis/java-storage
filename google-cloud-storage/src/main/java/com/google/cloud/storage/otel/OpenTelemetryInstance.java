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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.GrpcStorageOptions;
import com.google.cloud.storage.StorageOptions;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import java.util.Map;
import javax.annotation.Nonnull;

class OpenTelemetryInstance implements OpenTelemetryTraceUtil {
  private final Tracer tracer;
  private final OpenTelemetry openTelemetry;
  private final StorageOptions storageOptions;

  private static final String LIBRARY_NAME = "cloud.google.com/java/storage";

  private final String transport;

  OpenTelemetryInstance(StorageOptions storageOptions) {
    this.storageOptions = storageOptions;
    this.openTelemetry = storageOptions.getOpenTelemetry();
    this.tracer = openTelemetry.getTracer(LIBRARY_NAME, storageOptions.getLibraryVersion());
    this.transport = storageOptions instanceof GrpcStorageOptions ? "grpc" : "http";
  }

  static class Span implements OpenTelemetryTraceUtil.Span {
    private final io.opentelemetry.api.trace.Span span;
    private final String spanName;

    private Span(io.opentelemetry.api.trace.Span span, String spanName) {
      this.span = span;
      this.spanName = spanName;
    }

    @Override
    public OpenTelemetryTraceUtil.Span recordException(Throwable error) {
      span.recordException(
          error,
          Attributes.of(
              AttributeKey.stringKey("exception.message"), error.getMessage(),
              AttributeKey.stringKey("exception.type"), error.getClass().getName(),
              AttributeKey.stringKey("exception.stacktrace"), error.getStackTrace().toString()));
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span setStatus(StatusCode status, String name) {
      span.setStatus(status, name);
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span addEvent(String name) {
      span.addEvent(name);
      return this;
    }

    @Override
    public OpenTelemetryTraceUtil.Span addEvent(String name, Map<String, Object> attributes) {
      AttributesBuilder attributesBuilder = Attributes.builder();
      attributes.forEach(
          (key, value) -> {
            if (value instanceof Integer) {
              attributesBuilder.put(key, (int) value);
            } else if (value instanceof Long) {
              attributesBuilder.put(key, (long) value);
            } else if (value instanceof Double) {
              attributesBuilder.put(key, (double) value);
            } else if (value instanceof Float) {
              attributesBuilder.put(key, (float) value);
            } else if (value instanceof Boolean) {
              attributesBuilder.put(key, (boolean) value);
            } else if (value instanceof String) {
              attributesBuilder.put(key, (String) value);
            } else {
              // OpenTelemetry APIs do not support any other type.
              throw new IllegalArgumentException(
                  "Unknown attribute type:" + value.getClass().getSimpleName());
            }
          });
      span.addEvent(name, attributesBuilder.build());
      return this;
    }

    @Override
    public Scope makeCurrent() {
      return new Scope(span.makeCurrent());
    }

    @Override
    public void end() {
      span.end();
    }

    @Override
    public void end(Throwable error) {}

    @Override
    public <T> void endAtFuture(ApiFuture<T> futureValue) {}
  }

  static class Scope implements OpenTelemetryTraceUtil.Scope {
    private final io.opentelemetry.context.Scope scope;

    private Scope(io.opentelemetry.context.Scope scope) {
      this.scope = scope;
    }

    @Override
    public void close() {
      scope.close();
    }
  }

  static class Context implements OpenTelemetryTraceUtil.Context {
    private final io.opentelemetry.context.Context context;

    private Context(io.opentelemetry.context.Context context) {
      this.context = context;
    }

    @Override
    public Scope makeCurrent() {
      return new Scope(context.makeCurrent());
    }
  }

  @Override
  public OpenTelemetryTraceUtil.Span startSpan(String methodName, String module) {
    String formatSpanName = String.format("%s/%s", module, methodName);
    SpanBuilder spanBuilder = tracer.spanBuilder(formatSpanName);
    io.opentelemetry.api.trace.Span span =
        addSettingsAttributesToCurrentSpan(spanBuilder).startSpan();
    return new Span(span, formatSpanName);
  }

  @Override
  public OpenTelemetryTraceUtil.Span startSpan(
      String methodName, String module, OpenTelemetryTraceUtil.Context parent) {
    checkArgument(
        parent instanceof OpenTelemetryInstance.Context,
        "parent must be an instance of " + OpenTelemetryInstance.Context.class.getName());
    String formatSpanName = String.format("%s/%s", module, methodName);
    Context p2 = (Context) parent;
    SpanBuilder spanBuilder =
        tracer.spanBuilder(formatSpanName).setSpanKind(SpanKind.CLIENT).setParent(p2.context);
    io.opentelemetry.api.trace.Span span =
        addSettingsAttributesToCurrentSpan(spanBuilder).startSpan();
    return new Span(span, formatSpanName);
  }

  @Nonnull
  @Override
  public OpenTelemetryTraceUtil.Span currentSpan() {
    return new Span(io.opentelemetry.api.trace.Span.current(), "");
  }

  @Nonnull
  @Override
  public OpenTelemetryTraceUtil.Context currentContext() {
    return new Context(io.opentelemetry.context.Context.current());
  }

  private SpanBuilder addSettingsAttributesToCurrentSpan(SpanBuilder spanBuilder) {
    spanBuilder = spanBuilder.setAttribute("gcp.client.service", "Storage");
    spanBuilder =
        spanBuilder.setAllAttributes(
            Attributes.builder()
                .put("gcp.client.version", storageOptions.getLibraryVersion())
                .put("gcp.client.repo", "googleapis/java-storage")
                .put("gcp.client.artifact", "com.google.cloud:google-cloud-storage")
                .put("rpc.system", transport)
                .build());
    return spanBuilder;
  }
}
