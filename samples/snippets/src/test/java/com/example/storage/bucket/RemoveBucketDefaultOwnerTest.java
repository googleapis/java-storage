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

import static com.example.storage.Env.IT_SERVICE_ACCOUNT_EMAIL;
import static com.example.storage.Env.IT_SERVICE_ACCOUNT_USER;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.TestBase;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import org.junit.Test;

public class RemoveBucketDefaultOwnerTest extends TestBase {

  @Test
  public void testRemoveBucketDefaultOwner() {
    // Check for user email before the actual test.
    assertNotNull("Unable to determine user email", IT_SERVICE_ACCOUNT_EMAIL);
    // Add User as Default Owner
    Acl newDefaultOwner = Acl.of(IT_SERVICE_ACCOUNT_USER, Role.OWNER);
    storage.createDefaultAcl(bucket.getName(), newDefaultOwner);

    // Remove User as Default owner
    RemoveBucketDefaultOwner.removeBucketDefaultOwner(bucketName, IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("Removed user");
    assertThat(storage.getDefaultAcl(bucket.getName(), IT_SERVICE_ACCOUNT_USER)).isNull();
  }

  @Test
  public void testUserNotFound() {
    // Remove User without Default Owner Permissions
    RemoveBucketDefaultOwner.removeBucketDefaultOwner(bucketName, IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(IT_SERVICE_ACCOUNT_EMAIL);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("was not found");
  }
}
