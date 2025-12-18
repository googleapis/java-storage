/*
 * Copyright 2025 Google LLC
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

import com.google.api.core.BetaApi;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListMultipartUploadsRequest;
import com.google.cloud.storage.multipartupload.model.ListMultipartUploadsResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter; 

/**
 * A decorator for {@link MultipartUploadClient} that adds OpenTelemetry tracing and metrics.
 *
 * @since 2.62.0 This new api is in preview and is subject to breaking changes.
 */
@BetaApi
final class OtelMultipartUploadClientDecorator extends MultipartUploadClient {

  private final MultipartUploadClient delegate;
  private final Tracer tracer;
  
  private final Meter meter;
  private final DoubleHistogram createMultipartUploadLatency;
  private final DoubleHistogram listPartsLatency;
  private final DoubleHistogram abortMultipartUploadLatency;
  private final DoubleHistogram completeMultipartUploadLatency;
  private final DoubleHistogram uploadPartLatency;
  private final DoubleHistogram listMultipartUploadsLatency; 

  private final LongCounter uploadedBytes;
  private final LongHistogram partSize;

  private OtelMultipartUploadClientDecorator(
      MultipartUploadClient delegate, OpenTelemetry otel, Attributes baseAttributes) {
    this.delegate = delegate;
    this.tracer =
        OtelStorageDecorator.TracerDecorator.decorate(
            null, otel, baseAttributes, MultipartUploadClient.class.getName() + "/");

    this.meter = otel.meterBuilder(MultipartUploadClient.class.getName())
        .build();

    this.createMultipartUploadLatency = meter
        .histogramBuilder("storage.multipart_upload.create_multipart_upload.latency")
        .setDescription("Latency of Create Multipart Upload API calls")
        .setUnit("ms")
        .build();
    this.listPartsLatency = meter
        .histogramBuilder("storage.multipart_upload.list_parts.latency")
        .setDescription("Latency of List Parts API calls")
        .setUnit("ms")
        .build();
    this.abortMultipartUploadLatency = meter
        .histogramBuilder("storage.multipart_upload.abort_multipart_upload.latency")
        .setDescription("Latency of Abort Multipart Upload API calls")
        .setUnit("ms")
        .build();
    this.completeMultipartUploadLatency = meter
        .histogramBuilder("storage.multipart_upload.complete_multipart_upload.latency")
        .setDescription("Latency of Complete Multipart Upload API calls")
        .setUnit("ms")
        .build();
    this.uploadPartLatency = meter
        .histogramBuilder("storage.multipart_upload.upload_part.latency")
        .setDescription("Latency of Upload Part API calls")
        .setUnit("ms")
        .build();
    this.listMultipartUploadsLatency = meter
        .histogramBuilder("storage.multipart_upload.list_multipart_uploads.latency")
        .setDescription("Latency of List Multipart Uploads API calls")
        .setUnit("ms")
        .build();
    this.uploadedBytes = meter
        .counterBuilder("storage.multipart_upload.uploaded_bytes")
        .setDescription("Total bytes uploaded via Multipart Upload")
        .setUnit("By")
        .build();
    this.partSize = meter
        .histogramBuilder("storage.multipart_upload.part_size")
        .ofLongs()
        .setDescription("Size of parts uploaded via Multipart Upload")
        .setUnit("By")
        .build();
  }

  @Override
  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request) {
    long startTime = System.currentTimeMillis();
    Span span =
        tracer
            .spanBuilder("createMultipartUpload")
            .setAttribute(
                "gsutil.uri", String.format("gs://%s/%s", request.bucket(), request.key()))
            .startSpan();
    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("key", request.key())
        .put("method", "createMultipartUpload")
        .build();
    try (Scope ignore = span.makeCurrent()) {
      CreateMultipartUploadResponse response = delegate.createMultipartUpload(request);
      long duration = System.currentTimeMillis() - startTime;
      createMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      long duration = System.currentTimeMillis() - startTime;
      createMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public ListPartsResponse listParts(ListPartsRequest request) {
    long startTime = System.currentTimeMillis();
    Span span =
        tracer
            .spanBuilder("listParts")
            .setAttribute(
                "gsutil.uri", String.format("gs://%s/%s", request.bucket(), request.key()))
            .startSpan();
    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("key", request.key())
        .put("method", "listParts")
        .build();
    try (Scope ignore = span.makeCurrent()) {
      ListPartsResponse response = delegate.listParts(request);
      long duration = System.currentTimeMillis() - startTime;
      listPartsLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      long duration = System.currentTimeMillis() - startTime;
      listPartsLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request) {
    long startTime = System.currentTimeMillis();
    Span span =
        tracer
            .spanBuilder("abortMultipartUpload")
            .setAttribute(
                "gsutil.uri", String.format("gs://%s/%s", request.bucket(), request.key()))
            .startSpan();
    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("key", request.key())
        .put("method", "abortMultipartUpload")
        .build();
    try (Scope ignore = span.makeCurrent()) {
      AbortMultipartUploadResponse response = delegate.abortMultipartUpload(request);
      long duration = System.currentTimeMillis() - startTime;
      abortMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      long duration = System.currentTimeMillis() - startTime;
      abortMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public CompleteMultipartUploadResponse completeMultipartUpload(
      CompleteMultipartUploadRequest request) {
    long startTime = System.currentTimeMillis();
    Span span =
        tracer
            .spanBuilder("completeMultipartUpload")
            .setAttribute(
                "gsutil.uri", String.format("gs://%s/%s", request.bucket(), request.key()))
            .startSpan();
    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("key", request.key())
        .put("method", "completeMultipartUpload")
        .build();
    try (Scope ignore = span.makeCurrent()) {
      CompleteMultipartUploadResponse response = delegate.completeMultipartUpload(request);
      long duration = System.currentTimeMillis() - startTime;
      completeMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      long duration = System.currentTimeMillis() - startTime;
      completeMultipartUploadLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody) {
    long startTime = System.currentTimeMillis();
    
    Span span =
        tracer
            .spanBuilder("uploadPart")
            .setAttribute(
                "gsutil.uri", String.format("gs://%s/%s", request.bucket(), request.key()))
            .setAttribute("partNumber", request.partNumber())
            .startSpan();

    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("key", request.key())
        .put("partNumber", request.partNumber())
        .put("method", "uploadPart")
        .build();

    try (Scope ignore = span.makeCurrent()) {
      UploadPartResponse response = delegate.uploadPart(request, requestBody);

      long duration = System.currentTimeMillis() - startTime;
      uploadPartLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      uploadedBytes.add(requestBody.getContent().getLength(), metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      partSize.record(requestBody.getContent().getLength(), metricAttributes.toBuilder()
          .put("status", "success")
          .build());

      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());

      long duration = System.currentTimeMillis() - startTime;
      uploadPartLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());      

      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public ListMultipartUploadsResponse listMultipartUploads(ListMultipartUploadsRequest request) {
    long startTime = System.currentTimeMillis();
    Span span =
        tracer
            .spanBuilder("listMultipartUploads")
            .setAttribute("gsutil.uri", String.format("gs://%s/", request.bucket()))
            .startSpan();
    Attributes metricAttributes = Attributes.builder()
        .put("bucket", request.bucket())
        .put("method", "listMultipartUploads")
        .build();
    try (Scope ignore = span.makeCurrent()) {
      ListMultipartUploadsResponse response = delegate.listMultipartUploads(request);
      long duration = System.currentTimeMillis() - startTime;
      listMultipartUploadsLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "success")
          .build());
      return response;
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      long duration = System.currentTimeMillis() - startTime;
      listMultipartUploadsLatency.record((double) duration, metricAttributes.toBuilder()
          .put("status", "error")
          .put("exception_type", t.getClass().getSimpleName())
          .build());
      throw t;
    } finally {
      span.end();
    }
  }

  static MultipartUploadClient decorate(
      MultipartUploadClient delegate, OpenTelemetry otel, Transport transport) {
    if (otel == OpenTelemetry.noop()) {
      return delegate;
    }
    Attributes baseAttributes =
        Attributes.builder()
            .put("gcp.client.service", "Storage")
            .put("gcp.client.version", StorageOptions.getDefaultInstance().getLibraryVersion())
            .put("gcp.client.repo", "googleapis/java-storage")
            .put("gcp.client.artifact", "com.google.cloud:google-cloud-storage")
            .put("rpc.system", "XML")
            .put("service.name", "storage.googleapis.com")
            .build();
    return new OtelMultipartUploadClientDecorator(delegate, otel, baseAttributes);
  }
}
