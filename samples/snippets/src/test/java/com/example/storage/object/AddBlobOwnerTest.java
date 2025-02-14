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

package com.example.storage.object;

import static com.example.storage.Env.GOOGLE_CLOUD_PROJECT;
import static com.example.storage.Env.IT_SERVICE_ACCOUNT_EMAIL;
import static com.example.storage.Env.IT_SERVICE_ACCOUNT_USER;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.TestBase;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import org.junit.Test;

public class AddBlobOwnerTest extends TestBase {

  @Test
  public void testAddBlobOwner() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);

    BlobInfo gen1 = createEmptyObject();
    BlobId id = gen1.getBlobId();
    // Add Ownership to the file.
    AddBlobOwner.addBlobOwner(
        GOOGLE_CLOUD_PROJECT, id.getBucket(), IT_SERVICE_ACCOUNT_EMAIL, id.getName());
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(storage.getAcl(id, IT_SERVICE_ACCOUNT_USER)).isNotNull();
  }
}
