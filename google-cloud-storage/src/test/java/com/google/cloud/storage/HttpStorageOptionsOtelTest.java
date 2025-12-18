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

import io.opentelemetry.api.OpenTelemetry;
import org.junit.Test;

public class HttpStorageOptionsOtelTest {

  @Test
  public void testEnableHttpClientsMetrics() {
    HttpStorageOptions options =
        HttpStorageOptions.newBuilder()
            .setProjectId("test-project")
            .setEnableHttpClientsMetrics(true)
            .build();

    OpenTelemetry otel = options.getOpenTelemetry();
    assertThat(otel).isNotNull();
    assertThat(otel).isNotEqualTo(OpenTelemetry.noop());
  }

  @Test
  public void testDefaultHttpClientsMetrics() {
    HttpStorageOptions options =
        HttpStorageOptions.newBuilder()
            .setProjectId("test-project")
            .build();

    OpenTelemetry otel = options.getOpenTelemetry();
    assertThat(otel).isEqualTo(OpenTelemetry.noop());
  }
}
