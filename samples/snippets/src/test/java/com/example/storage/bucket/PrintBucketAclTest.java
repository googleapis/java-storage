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

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.cloud.testing.junit4.StdOutCaptureRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PrintBucketAclTest {

  @Rule public StdOutCaptureRule stdOut = new StdOutCaptureRule();
  private static final String USER_EMAIL =
      "google-cloud-java-tests@" + "java-docs-samples-tests.iam.gserviceaccount.com";

  private String bucketName;
  private Storage storage;

  @Before
  public void setUp() {
    bucketName = RemoteStorageHelper.generateBucketName();
    storage = StorageOptions.getDefaultInstance().getService();
    storage.create(BucketInfo.of(bucketName));
  }

  @After
  public void tearDown() {
    storage.delete(bucketName);
  }

  @Test
  public void testPrintBucketAcls() {
    Entity testUser = new User(USER_EMAIL);
    storage.createAcl(bucketName, Acl.of(testUser, Role.READER));
    PrintBucketAcl.printBucketAcl(bucketName);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("READER: USER");
  }
}
