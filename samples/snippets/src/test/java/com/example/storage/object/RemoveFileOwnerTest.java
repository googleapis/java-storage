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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.TestBase;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import org.junit.Rule;
import org.junit.Test;

public class RemoveFileOwnerTest extends TestBase {

  @Rule public MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(5);

  public static final String IT_SERVICE_ACCOUNT_EMAIL = System.getenv("IT_SERVICE_ACCOUNT_EMAIL");

  @Test
  public void testRemoveFileOwner() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);

    // Add User as Owner
    Acl newFileOwner = Acl.of(new User(IT_SERVICE_ACCOUNT_EMAIL), Role.OWNER);
    blob.createAcl(newFileOwner);

    // Remove User as owner
    RemoveFileOwner.removeFileOwner(
        System.getenv("GOOGLE_CLOUD_PROJECT"), bucketName, IT_SERVICE_ACCOUNT_EMAIL, blobName);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("Removed user");
    assertThat(blob.getAcl(new User(IT_SERVICE_ACCOUNT_EMAIL))).isNull();
  }

  @Test
  public void testUserNotFound() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);

    // Remove User without Owner Permissions
    RemoveFileOwner.removeFileOwner(
        System.getenv("GOOGLE_CLOUD_PROJECT"), bucketName, IT_SERVICE_ACCOUNT_EMAIL, blobName);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("was not found");
  }
}
