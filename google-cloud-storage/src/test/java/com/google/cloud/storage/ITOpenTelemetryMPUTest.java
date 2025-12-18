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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompletedMultipartUpload;
import com.google.cloud.storage.multipartupload.model.CompletedPart;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListMultipartUploadsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import com.google.cloud.storage.otel.TestExporter;
import com.google.common.collect.ImmutableList;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = Backend.PROD,
    transports = {Transport.HTTP})
public final class ITOpenTelemetryMPUTest {

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Test
  public void checkMPUTracing() throws Exception {
    TestExporter exporter = new TestExporter();

    OpenTelemetrySdk openTelemetrySdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                    .build())
            .build();

    String objectName = generator.randomObjectName();
    runMpuOperations(openTelemetrySdk, objectName);

    List<SpanData> spans = exporter.getExportedSpans();
    assertThat(spans).hasSize(7);

    SpanData createSpan = spans.get(0);
    assertThat(createSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/createMultipartUpload");
    assertThat(createSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/%s", bucket.getName(), objectName));

    SpanData uploadSpan = spans.get(1);
    assertThat(uploadSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/uploadPart");
    assertThat(uploadSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/%s", bucket.getName(), objectName));
    assertThat(uploadSpan.getAttributes().get(AttributeKey.longKey("partNumber"))).isEqualTo(1);

    SpanData listSpan = spans.get(2);
    assertThat(listSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/listParts");
    assertThat(listSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/%s", bucket.getName(), objectName));

    SpanData completeSpan = spans.get(3);
    assertThat(completeSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/completeMultipartUpload");
    assertThat(completeSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/%s", bucket.getName(), objectName));

    SpanData listUploadsSpan = spans.get(4);
    assertThat(listUploadsSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/listMultipartUploads");
    assertThat(listUploadsSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/", bucket.getName()));

    SpanData create2Span = spans.get(5);
    assertThat(create2Span.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/createMultipartUpload");

    SpanData abortSpan = spans.get(6);
    assertThat(abortSpan.getName())
        .isEqualTo("com.google.cloud.storage.MultipartUploadClient/abortMultipartUpload");
    assertThat(abortSpan.getAttributes().get(AttributeKey.stringKey("gsutil.uri")))
        .isEqualTo(String.format("gs://%s/%s-abort", bucket.getName(), objectName));
  }

  @Test
  public void checkMPUMetrics() throws Exception {
    InMemoryMetricReader metricReader = InMemoryMetricReader.create();
    SdkMeterProvider meterProvider =
        SdkMeterProvider.builder()
            .registerMetricReader(metricReader)
            .build();

    OpenTelemetrySdk openTelemetrySdk =
        OpenTelemetrySdk.builder().setMeterProvider(meterProvider).build();

    String objectName = generator.randomObjectName();
    runMpuOperations(openTelemetrySdk, objectName);

    Collection<MetricData> metrics = metricReader.collectAllMetrics();
    System.err.println("Exported metrics count: " + metrics.size());
    metrics.forEach(m -> System.err.println("Metric: " + m.getName()));

    assertThat(metrics).hasSize(8);

    MetricData createMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("create_multipart_upload"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("create_multipart_upload metric not found"));
    assertThat(createMetric.getName())
        .isEqualTo("storage.multipart_upload.create_multipart_upload.latency");
    assertThat(createMetric.getData().getPoints()).hasSize(2); // 2 create calls

    MetricData uploadPartMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("upload_part"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("upload_part metric not found"));
    assertThat(uploadPartMetric.getName())
        .isEqualTo("storage.multipart_upload.upload_part.latency");
    assertThat(uploadPartMetric.getData().getPoints()).hasSize(1);

    MetricData completeMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("complete_multipart_upload"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("complete_multipart_upload metric not found"));
    assertThat(completeMetric.getName())
        .isEqualTo("storage.multipart_upload.complete_multipart_upload.latency");
    assertThat(completeMetric.getData().getPoints()).hasSize(1);

    MetricData listPartsMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("list_parts"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("list_parts metric not found"));
    assertThat(listPartsMetric.getName())
        .isEqualTo("storage.multipart_upload.list_parts.latency");
    assertThat(listPartsMetric.getData().getPoints()).hasSize(1);

    MetricData listUploadsMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("list_multipart_uploads"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("list_multipart_uploads metric not found"));
    assertThat(listUploadsMetric.getName())
        .isEqualTo("storage.multipart_upload.list_multipart_uploads.latency");
    assertThat(listUploadsMetric.getData().getPoints()).hasSize(1);

    MetricData abortMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("abort_multipart_upload"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("abort_multipart_upload metric not found"));
    assertThat(abortMetric.getName())
        .isEqualTo("storage.multipart_upload.abort_multipart_upload.latency");
    assertThat(abortMetric.getData().getPoints()).hasSize(1);

    MetricData uploadedBytesMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("uploaded_bytes"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("uploaded_bytes metric not found"));
    assertThat(uploadedBytesMetric.getName())
        .isEqualTo("storage.multipart_upload.uploaded_bytes");
    assertThat(uploadedBytesMetric.getData().getPoints()).hasSize(1);
    
    // "Hello, World!" is 13 bytes
    assertThat(uploadedBytesMetric.getLongSumData().getPoints().iterator().next().getValue())
        .isEqualTo(13);

    MetricData partSizeMetric =
        metrics.stream()
            .filter(m -> m.getName().contains("part_size"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("part_size metric not found"));
    assertThat(partSizeMetric.getName())
        .isEqualTo("storage.multipart_upload.part_size");
    assertThat(partSizeMetric.getData().getPoints()).hasSize(1);
    assertThat(partSizeMetric.getHistogramData().getPoints().iterator().next().getSum())
        .isEqualTo(13);
  }

  private void runMpuOperations(OpenTelemetrySdk openTelemetrySdk, String objectName) {
    HttpStorageOptions httpStorageOptions = (HttpStorageOptions) storage.getOptions();
    StorageOptions storageOptions =
        httpStorageOptions.toBuilder().setOpenTelemetry(openTelemetrySdk).build();

    try (Storage storage = storageOptions.getService()) {
      MultipartUploadClient mpuClient =
          MultipartUploadClient.create(
              MultipartUploadSettings.of((HttpStorageOptions) storage.getOptions()));

      CreateMultipartUploadResponse create =
          mpuClient.createMultipartUpload(
              CreateMultipartUploadRequest.builder()
                  .bucket(bucket.getName())
                  .key(objectName)
                  .build());

      byte[] data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
      RequestBody body = RequestBody.of(ByteBuffer.wrap(data));
      UploadPartResponse upload =
          mpuClient.uploadPart(
              UploadPartRequest.builder()
                  .bucket(bucket.getName())
                  .key(objectName)
                  .uploadId(create.uploadId())
                  .partNumber(1)
                  .build(),
              body);

      mpuClient.listParts(
          ListPartsRequest.builder()
              .bucket(bucket.getName())
              .key(objectName)
              .uploadId(create.uploadId())
              .build());

      mpuClient.completeMultipartUpload(
          CompleteMultipartUploadRequest.builder()
              .bucket(bucket.getName())
              .key(objectName)
              .uploadId(create.uploadId())
              .multipartUpload(
                  CompletedMultipartUpload.builder()
                      .parts(
                          ImmutableList.of(
                              CompletedPart.builder().partNumber(1).eTag(upload.eTag()).build()))
                      .build())
              .build());

      mpuClient.listMultipartUploads(
          ListMultipartUploadsRequest.builder().bucket(bucket.getName()).build());

      CreateMultipartUploadResponse create2 =
          mpuClient.createMultipartUpload(
              CreateMultipartUploadRequest.builder()
                  .bucket(bucket.getName())
                  .key(objectName + "-abort")
                  .build());
      mpuClient.abortMultipartUpload(
          AbortMultipartUploadRequest.builder()
              .bucket(bucket.getName())
              .key(objectName + "-abort")
              .uploadId(create2.uploadId())
              .build());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
