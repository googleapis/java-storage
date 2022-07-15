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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.TestBench;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public final class ITGrpcTest {
  private static final Logger LOGGER = Logger.getLogger(ITGrpcTest.class.getName());

  @ClassRule public static final TestBench TEST_BENCH = TestBench.newBuilder().build();

  @Rule
  public final StorageFixture storageFixture =
      StorageFixture.from(
          () ->
              StorageOptions.grpc()
                  .setHost(TEST_BENCH.getGRPCBaseUri())
                  .setCredentials(NoCredentials.getInstance())
                  .setProjectId("test-project-id")
                  .build());

  private Storage storage;

  @Before
  public void setUp() throws Exception {
    LOGGER.fine("Running setup...");
    // ...set up additional stuff.
    LOGGER.fine("Running setup complete");
  }

  @After
  public void tearDown() throws Exception {
    LOGGER.fine("Running teardown...");
    // ...tear down stuff.
    LOGGER.fine("Running teardown complete");
  }

  @Test
  public void testCreateBucket() {
    final String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket bucket = storageFixture.getInstance().create(BucketInfo.of(bucketName));
    assertThat(bucket.getName()).isEqualTo(bucketName);
  }
}
