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

import com.google.cloud.storage.testing.RemoteStorageHelper;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public final class ITOpenTelemetryTest {

  @Test
  public void checkInstrumentation() {
    SpanExporter exporter = new TestExporter();

    OpenTelemetrySdk openTelemetrySdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                    .build())
            .build();
    StorageOptions storageOptions =
        StorageOptions.http().setOpenTelemetrySdk(openTelemetrySdk).build();
    Storage storage = storageOptions.getService();
    String bucket = randomBucketName();
    storage.create(BucketInfo.of(bucket));
    TestExporter testExported = (TestExporter) exporter;
    SpanData spanData = testExported.getExportedSpans().get(0);
    Assert.assertEquals("Storage", getAttributeValue(spanData, "gcp.client.service"));
    Assert.assertEquals("googleapis/java-storage", getAttributeValue(spanData, "gcp.client.repo"));
    Assert.assertEquals(
        "com.google.cloud.google-cloud-storage",
        getAttributeValue(spanData, "gcp.client.artifact"));
    Assert.assertEquals("http", getAttributeValue(spanData, "rpc.system"));

    // Cleanup
    RemoteStorageHelper.forceDelete(storage, bucket);
  }

  @Test
  public void checkInstrumentationGrpc() {
    SpanExporter exporter = new TestExporter();

    OpenTelemetrySdk openTelemetrySdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                    .build())
            .build();
    StorageOptions storageOptions =
        StorageOptions.grpc().setOpenTelemetrySdk(openTelemetrySdk).build();
    Storage storage = storageOptions.getService();
    String bucket = randomBucketName();
    storage.create(BucketInfo.of(bucket));
    TestExporter testExported = (TestExporter) exporter;
    SpanData spanData = testExported.getExportedSpans().get(0);
    Assert.assertEquals("Storage", getAttributeValue(spanData, "gcp.client.service"));
    Assert.assertEquals("googleapis/java-storage", getAttributeValue(spanData, "gcp.client.repo"));
    Assert.assertEquals(
        "com.google.cloud.google-cloud-storage",
        getAttributeValue(spanData, "gcp.client.artifact"));
   Assert.assertEquals("grpc", getAttributeValue(spanData, "rpc.system"));

   // Cleanup
    RemoteStorageHelper.forceDelete(storage, bucket);
  }

  @Test
  public void noOpDoesNothing() {
    String httpBucket = randomBucketName();
    String grpcBucket = randomBucketName();
    // NoOp for HTTP
    StorageOptions storageOptionsHttp = StorageOptions.http().build();
    Storage storageHttp = storageOptionsHttp.getService();
    storageHttp.create(BucketInfo.of(httpBucket));

    //NoOp for Grpc
    StorageOptions storageOptionsGrpc = StorageOptions.grpc().build();
    Storage storageGrpc = storageOptionsGrpc.getService();
    storageGrpc.create(BucketInfo.of(grpcBucket));

    // cleanup
    RemoteStorageHelper.forceDelete(storageHttp, httpBucket);
    RemoteStorageHelper.forceDelete(storageGrpc, grpcBucket);
  }

  private String getAttributeValue(SpanData spanData, String key) {
    return spanData.getAttributes().get(AttributeKey.stringKey(key)).toString();
  }

  public String randomBucketName() {
    return "java-storage-grpc-rand-" + UUID.randomUUID();
  }
}

class TestExporter implements SpanExporter {

  public final List<SpanData> exportedSpans = Collections.synchronizedList(new ArrayList<>());

  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    exportedSpans.addAll(spans);
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode flush() {
    return null;
  }

  @Override
  public CompletableResultCode shutdown() {
    return null;
  }

  public List<SpanData> getExportedSpans() {
    return exportedSpans;
  }
}
