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

package com.example.storage.bucket;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.TestBase;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import org.junit.Test;

public class RemoveBucketOwnerTest extends TestBase {

  public static final String IT_SERVICE_ACCOUNT_EMAIL = System.getenv("IT_SERVICE_ACCOUNT_EMAIL");

  @Test
  public void testRemoveBucketOwner() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);
    // Add User as Owner
    Acl newOwner = Acl.of(new User(IT_SERVICE_ACCOUNT_EMAIL), Role.OWNER);
    bucket.createAcl(newOwner);

    // Remove User as owner
    RemoveBucketOwner.removeBucketOwner(
        System.getenv("GOOGLE_CLOUD_PROJECT"), bucketName, IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("Removed user");
    assertThat(bucket.getAcl(new User(IT_SERVICE_ACCOUNT_EMAIL))).isNull();
  }

  @Test
  public void testUserNotFound() {
    // Remove User without Owner Permissions
    RemoveBucketOwner.removeBucketOwner(
        System.getenv("GOOGLE_CLOUD_PROJECT"), bucketName, IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("was not found");
  }
}
