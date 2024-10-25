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

import com.google.api.core.ApiClock;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.TestBench;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
public class ITGrpcOpenTelemetryTest {
  @Inject public TestBench testBench;
  private static final ApiClock TIME_SOURCE =
      new ApiClock() {
        @Override
        public long nanoTime() {
          return 42_000_000_000L;
        }

        @Override
        public long millisTime() {
          return 42_000L;
        }
      };
  private StorageOptions options;
  private SpanExporter exporter = new TestExporter();

  @Before
  public void setUp() {
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
  }

  @Test
  public void runCreateBucket() {
    Storage storage = options.getService();
    String bucket = "random-bucket";
    storage.create(BucketInfo.of(bucket));
    TestExporter testExported = (TestExporter) exporter;
    SpanData spanData = testExported.getExportedSpans().get(0);
    Assert.assertEquals("Storage", getAttributeValue(spanData, "gcp.client.service"));
    Assert.assertEquals("googleapis/java-storage", getAttributeValue(spanData, "gcp.client.repo"));
    Assert.assertEquals(
        "com.google.cloud.google-cloud-storage",
        getAttributeValue(spanData, "gcp.client.artifact"));
    Assert.assertEquals("grpc", getAttributeValue(spanData, "rpc.system"));
  }

  @Test
  public void runCreateBlob() {
    Storage storage = options.getService();
    String bucket = "random-bucket";
    storage.create(BucketInfo.of(bucket));
    byte[] content = "Hello, World!".getBytes(UTF_8);
    BlobId toCreate = BlobId.of(bucket, "blob");
    storage.create(BlobInfo.newBuilder(toCreate).build(), content);
    TestExporter testExported = (TestExporter) exporter;
    List<SpanData> spanData = testExported.getExportedSpans();
    // (1) Span to create the bucket
    // (2) Span when calling create
    // (3) Span when passing call to internalDirectUpload
    Assert.assertEquals(3, spanData.size());
    for(SpanData span : spanData) {
      Assert.assertEquals("Storage", getAttributeValue(span, "gcp.client.service"));
      Assert.assertEquals("googleapis/java-storage",
          getAttributeValue(span, "gcp.client.repo"));
      Assert.assertEquals(
          "com.google.cloud.google-cloud-storage",
          getAttributeValue(span, "gcp.client.artifact"));
      Assert.assertEquals("grpc", getAttributeValue(span, "rpc.system"));
    }
  }

  private String getAttributeValue(SpanData spanData, String key) {
    return spanData.getAttributes().get(AttributeKey.stringKey(key)).toString();
  }
}
