/*
 * Copyright 2017 Google LLC
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

import static com.google.cloud.storage.spi.v1.HttpRpcContextTest.*;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.cloud.TransportOptions;
import com.google.cloud.Tuple;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.spi.v1.AuditingHttpTransport;
import java.util.Optional;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class StorageOptionsTest {

  @Test
  public void testInvalidTransport() {
    try {
      StorageOptions.newBuilder()
          .setTransportOptions(EasyMock.<TransportOptions>createMock(TransportOptions.class));
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      Assert.assertNotNull(ex.getMessage());
    }
  }

  @Test
  public void testConfigureHostShouldBeKeptOnToBuilder() {
    StorageOptions opts1 = StorageOptions.newBuilder().setHost("custom-host").build();
    StorageOptions opts2 = opts1.toBuilder().build();

    assertThat(opts2.getHost()).isEqualTo("custom-host");
  }

  @Test
  public void testToBuilderShouldSpecifyDefaultIfNotOtherwiseSet() {
    StorageOptions opts1 = StorageOptions.newBuilder().build();
    StorageOptions opts2 = opts1.toBuilder().build();

    assertThat(opts2.getHost()).isEqualTo("https://storage.googleapis.com");
  }

  @Test
  public void testNewBuilderSpecifiesCorrectHost() {
    StorageOptions opts1 = StorageOptions.newBuilder().build();

    assertThat(opts1.getHost()).isEqualTo("https://storage.googleapis.com");
  }

  @Test
  public void testDefaultInstanceSpecifiesCorrectHost() {
    StorageOptions opts1 = StorageOptions.getDefaultInstance();

    assertThat(opts1.getHost()).isEqualTo("https://storage.googleapis.com");
  }

  @Test
  public void testDefaultInvocationId() {
    StorageOptions opts1 = StorageOptions.getDefaultInstance();

    assertTrue(opts1.isIncludeInvocationId());
  }

  @Test
  public void testDisableInvocationId() {
    StorageOptions opts1 = StorageOptions.newBuilder().setIncludeInvocationId(false).build();

    assertFalse(opts1.isIncludeInvocationId());
  }

  @Test
  public void testInvocationIdIsNotPassedThroughWhenDisabled() {
    MockLowLevelHttpResponse response =
        new MockLowLevelHttpResponse()
            .setContentType("application/json")
            .setContent(
                "{\n"
                    + "  \"kind\": \"storage#serviceAccount\",\n"
                    + "  \"email_address\": \"service-234234@gs-project-accounts.iam.gserviceaccount.com\"\n"
                    + "}\n")
            .setStatusCode(200);
    AuditingHttpTransport transport = new AuditingHttpTransport(response);
    TransportOptions transportOptions =
        HttpTransportOptions.newBuilder().setHttpTransportFactory(() -> transport).build();
    Storage service =
        StorageOptions.getDefaultInstance()
            .toBuilder()
            .setTransportOptions(transportOptions)
            .setIncludeInvocationId(false)
            .build()
            .getService();
    service.getServiceAccount("test-project");
    Optional<Tuple<String, String>> anyXGoogApiClientWithGcclInvocationId =
        transport.getAddHeaderCalls().stream()
            .filter(t -> "x-goog-api-client".equals(t.x()) && t.y().contains("gccl-invocation-id/"))
            .findFirst();

    assertFalse(anyXGoogApiClientWithGcclInvocationId.isPresent());
    assertThat(transport.getBuildRequestCalls()).hasSize(1);
  }
}
