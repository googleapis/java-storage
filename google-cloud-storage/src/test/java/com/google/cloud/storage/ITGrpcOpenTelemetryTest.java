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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.it.runner.registry.TestBench;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
public class ITGrpcOpenTelemetryTest {
  @Inject public TestBench testBench;
  private StorageOptions options;
  private SpanExporter exporter;
  private Storage storage;
  private static final byte[] helloWorldTextBytes = "hello world".getBytes();
  private BlobId blobId;
  @Inject public Generator generator;
  @Inject public BucketInfo testBucket;

  @Before
  public void setUp() {
    exporter = new TestExporter();
    OpenTelemetrySdk openTelemetrySdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                    .build())
            .build();
    options =
        StorageOptions.grpc()
            .setHost(testBench.getGRPCBaseUri())
            .setProjectId("projectId")
            .setCredentials(NoCredentials.getInstance())
            .setOpenTelemetrySdk(openTelemetrySdk)
            .build();
    storage = options.getService();
    String objectString = generator.randomObjectName();
    blobId = BlobId.of(testBucket.getName(), objectString);
  }

  @Test
  public void runCreateBucket() {
    String bucket = "random-bucket";
    storage.create(BucketInfo.of(bucket));
    TestExporter testExported = (TestExporter) exporter;
    List<SpanData> spanData = testExported.getExportedSpans();
    checkCommonAttributes(spanData);
  }

  @Test
  public void runCreateBlob() {
    byte[] content = "Hello, World!".getBytes(UTF_8);
    BlobId toCreate = BlobId.of(testBucket.getName(), generator.randomObjectName());
    storage.create(BlobInfo.newBuilder(toCreate).build(), content);
    TestExporter testExported = (TestExporter) exporter;
    List<SpanData> spanData = testExported.getExportedSpans();
    checkCommonAttributes(spanData);
    Assert.assertTrue(spanData.stream().anyMatch(x -> x.getName().contains("create")));
    Assert.assertTrue(
        spanData.stream().anyMatch(x -> x.getName().contains("internalDirectUpload")));
    Assert.assertEquals(spanData.get(1).getSpanContext(), spanData.get(0).getParentSpanContext());
  }

  @Test
  public void runReadAllBytes() {
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
    storage.create(blobInfo, helloWorldTextBytes);
    byte[] read = storage.readAllBytes(blobId);
    TestExporter testExported = (TestExporter) exporter;
    List<SpanData> spanData = testExported.getExportedSpans();
    checkCommonAttributes(spanData);
    Assert.assertTrue(spanData.stream().anyMatch(x -> x.getName().contains("readAllBytes")));
  }

  @Test
  public void createFrom() throws IOException {
    Path helloWorldTxtGz = File.createTempFile(blobId.getName(), ".txt.gz").toPath();
    storage.createFrom(BlobInfo.newBuilder(blobId).build(), helloWorldTxtGz);
    TestExporter testExported = (TestExporter) exporter;
    List<SpanData> spanData = testExported.getExportedSpans();
    checkCommonAttributes(spanData);
    Assert.assertTrue(spanData.stream().anyMatch(x -> x.getName().contains("createFrom")));
    Assert.assertTrue(spanData.stream().anyMatch(x -> x.getName().contains("internalCreateFrom")));
  }

  private void checkCommonAttributes(List<SpanData> spanData) {
    for (SpanData span : spanData) {
      Assert.assertEquals("Storage", getAttributeValue(span, "gcp.client.service"));
      Assert.assertEquals("googleapis/java-storage", getAttributeValue(span, "gcp.client.repo"));
      Assert.assertEquals(
          "com.google.cloud.google-cloud-storage", getAttributeValue(span, "gcp.client.artifact"));
      Assert.assertEquals("grpc", getAttributeValue(span, "rpc.system"));
    }
  }

  private String getAttributeValue(SpanData spanData, String key) {
    return spanData.getAttributes().get(AttributeKey.stringKey(key)).toString();
  }
}
