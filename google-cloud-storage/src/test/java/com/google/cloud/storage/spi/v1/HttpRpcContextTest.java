/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage.spi.v1;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.cloud.TransportOptions;
import com.google.cloud.Tuple;
import com.google.cloud.WriteChannel;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;

public class HttpRpcContextTest {
  @Test
  public void testNewInvocationId() {
    UUID uuid = UUID.fromString("28220dff-1e8b-4770-9e10-022c2a99d8f3");
    HttpRpcContext testContext = new HttpRpcContext(() -> uuid);

    assertThat(testContext.newInvocationId()).isEqualTo(uuid);
    assertThat(testContext.getInvocationId()).isEqualTo(uuid);
    // call again to ensure the id is consistent with our supplier
    assertThat(testContext.newInvocationId()).isEqualTo(uuid);
    assertThat(testContext.getInvocationId()).isEqualTo(uuid);
  }

  @Test
  public void testInvocationIdIsPassedThrough() {
    MockLowLevelHttpResponse response =
        new MockLowLevelHttpResponse()
            .setContentType("application/json")
            .setContent(
                "{\n"
                    + "  \"kind\": \"storage#serviceAccount\",\n"
                    + "  \"email_address\": \"service-787021104993@gs-project-accounts.iam.gserviceaccount.com\"\n"
                    + "}\n")
            .setStatusCode(200);
    AuditingHttpTransport transport = new AuditingHttpTransport(response);
    TransportOptions transportOptions =
        HttpTransportOptions.newBuilder().setHttpTransportFactory(() -> transport).build();
    Storage service =
        StorageOptions.getDefaultInstance()
            .toBuilder()
            .setTransportOptions(transportOptions)
            .build()
            .getService();
    service.getServiceAccount("test-project");

    Optional<Tuple<String, String>> anyXGoogApiClientWithGcclInvocationId =
        transport.getAddHeaderCalls().stream()
            .filter(t -> "x-goog-api-client".equals(t.x()) && t.y().contains("gccl-invocation-id/"))
            .findFirst();

    assertTrue(anyXGoogApiClientWithGcclInvocationId.isPresent());
    assertThat(transport.getBuildRequestCalls()).hasSize(1);
  }

  @Test
  public void testInvocationIdNotInSignedURL_v2() throws IOException {
    URL signedUrlV2 =
        new URL(
            "http://www.test.com/test-bucket/test1.txt?GoogleAccessId=testClient-test@test.com&Expires=1553839761&Signature=MJUBXAZ7");
    doTestInvocationIdNotInSignedURL(signedUrlV2);
  }

  @Test
  public void testInvocationIdNotInSignedURL_v4() throws IOException {
    URL signedUrlV4 =
        new URL("http://www.test.com/test-bucket/test1.txt?X-Goog-Signature=MJUBXAZ7");
    doTestInvocationIdNotInSignedURL(signedUrlV4);
  }

  private void doTestInvocationIdNotInSignedURL(URL signedUrl) throws IOException {
    MockLowLevelHttpResponse response =
        new MockLowLevelHttpResponse()
            .setContentType("text/plain")
            .setHeaderNames(ImmutableList.of("Location"))
            .setHeaderValues(ImmutableList.of("http://test"))
            .setStatusCode(201);
    AuditingHttpTransport transport = new AuditingHttpTransport(response);
    TransportOptions transportOptions =
        HttpTransportOptions.newBuilder().setHttpTransportFactory(() -> transport).build();
    Storage service =
        StorageOptions.getDefaultInstance()
            .toBuilder()
            .setTransportOptions(transportOptions)
            .build()
            .getService();
    WriteChannel writerV2 = service.writer(signedUrl);
    writerV2.write(ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)));

    Optional<Tuple<String, String>> anyXGoogApiClientWithGcclInvocationId =
        transport.getAddHeaderCalls().stream()
            .filter(t -> "x-goog-api-client".equals(t.x()) && t.y().contains("gccl-invocation-id/"))
            .findFirst();

    assertFalse(anyXGoogApiClientWithGcclInvocationId.isPresent());
    assertThat(transport.getBuildRequestCalls()).hasSize(1);
  }

  private static final class AuditingHttpTransport extends HttpTransport {
    private final LowLevelHttpResponse response;
    private final List<Tuple<String, String>> buildRequestCalls;
    private final List<Tuple<String, String>> addHeaderCalls;

    private AuditingHttpTransport(LowLevelHttpResponse response) {
      this.response = response;
      this.buildRequestCalls = Collections.synchronizedList(new ArrayList<>());
      this.addHeaderCalls = Collections.synchronizedList(new ArrayList<>());
    }

    public List<Tuple<String, String>> getBuildRequestCalls() {
      return ImmutableList.copyOf(buildRequestCalls);
    }

    public List<Tuple<String, String>> getAddHeaderCalls() {
      return ImmutableList.copyOf(addHeaderCalls);
    }

    @Override
    protected LowLevelHttpRequest buildRequest(String method, String url) {
      buildRequestCalls.add(Tuple.of(method, url));
      return new LowLevelHttpRequest() {
        @Override
        public void addHeader(String name, String value) {
          addHeaderCalls.add(Tuple.of(name, value));
        }

        @Override
        public LowLevelHttpResponse execute() {
          return response;
        }
      };
    }
  }
}
