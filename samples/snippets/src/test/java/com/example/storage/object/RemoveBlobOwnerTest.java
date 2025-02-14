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
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.Env;
import com.example.storage.TestBase;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import org.junit.Rule;
import org.junit.Test;

public class RemoveBlobOwnerTest extends TestBase {

  @Rule public MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(5);

  public static final String IT_SERVICE_ACCOUNT_EMAIL = Env.IT_SERVICE_ACCOUNT_EMAIL;

  @Test
  public void testRemoveBlobOwner() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);

    BlobInfo gen1 = createEmptyObject();
    BlobId id = gen1.getBlobId();
    // Add User as Owner
    Acl newFileOwner = Acl.of(Env.IT_SERVICE_ACCOUNT_USER, Role.OWNER);
    storage.createAcl(id, newFileOwner);

    // Remove User as owner
    RemoveBlobOwner.removeBlobOwner(
        GOOGLE_CLOUD_PROJECT, id.getBucket(), IT_SERVICE_ACCOUNT_EMAIL, id.getName());
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("Removed user");
    assertThat(storage.getAcl(id, Env.IT_SERVICE_ACCOUNT_USER)).isNull();
  }

  @Test
  public void testUserNotFound() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);

    BlobInfo gen1 = createEmptyObject();
    BlobId id = gen1.getBlobId();
    // Remove User without Owner Permissions
    RemoveBlobOwner.removeBlobOwner(
        GOOGLE_CLOUD_PROJECT, id.getBucket(), IT_SERVICE_ACCOUNT_EMAIL, id.getName());
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("was not found");
  }
}
