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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.auth.http.HttpTransportFactory;
import com.google.cloud.TransportOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.Test;

public class HttpRpcContextTest {
  @Test
  public void testNewInvocationId() {
    Supplier<UUID> testUUIDSupplier = () -> UUID.fromString("28220dff-1e8b-4770-9e10-022c2a99d8f3");
    HttpRpcContext testContext = new HttpRpcContext(testUUIDSupplier);
    testContext.newInvocationId();
    assertEquals(testUUIDSupplier.get(), testContext.getInvocationId());
  }

  @Test
  public void testInvocationIdIsPassedThrough() throws IOException {
    HttpTransportFactory mockTransportFactory =
        new HttpTransportFactory() {
          @Override
          public HttpTransport create() {
            return new HttpTransport() {
              @Override
              public LowLevelHttpRequest buildRequest(String method, String url)
                  throws IOException {

                return new LowLevelHttpRequest() {
                  boolean xGoogApiClientHeaderFound = false;

                  @Override
                  public void addHeader(String s, String s1) throws IOException {
                    if (s.equals("x-goog-api-client")) {
                      xGoogApiClientHeaderFound = true;
                      assertTrue(
                          s1.contains(
                              "gccl-invocation-id/"
                                  + HttpRpcContext.getInstance().getInvocationId()));
                    }
                  }

                  @Override
                  public LowLevelHttpResponse execute() {
                    assertTrue(this.xGoogApiClientHeaderFound);
                    return new MockLowLevelHttpResponse()
                        .setContentType("application/json")
                        .setContent(
                            "{\n"
                                + "  \"kind\": \"storage#serviceAccount\",\n"
                                + "  \"email_address\": \"service-787021104993@gs-project-accounts.iam.gserviceaccount.com\"\n"
                                + "}\n")
                        .setStatusCode(200);
                  }
                };
              }
            };
          }
        };
    TransportOptions transportOptions =
        HttpTransportOptions.newBuilder().setHttpTransportFactory(mockTransportFactory).build();
    Storage service =
        StorageOptions.getDefaultInstance()
            .toBuilder()
            .setTransportOptions(transportOptions)
            .build()
            .getService();
    service.getServiceAccount("test-project");
  }
}
