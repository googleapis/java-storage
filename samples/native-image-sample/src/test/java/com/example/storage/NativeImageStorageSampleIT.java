/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.storage;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.ServiceOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class NativeImageStorageSampleIT {

  private static final String NATIVE_TEST_SECRET_ID = "native-test-secret" + UUID.randomUUID();
  private static String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private ByteArrayOutputStream bout;

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));
  }

  @Test
  public void createAndReadStorageResources() {
    NativeImageStorageSample.main(new String[]{});
    assertThat(bout.toString()).contains("Created bucket " + NativeImageStorageSample.BUCKET_NAME);
    assertThat(bout.toString()).contains("Created file " + NativeImageStorageSample.FILENAME);
    assertThat(bout.toString()).contains("Successfully wrote to file: Hello World!");
  }
}