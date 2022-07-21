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

import com.google.api.gax.paging.Page;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.CleanupStrategy;
import com.google.cloud.storage.conformance.retry.TestBench;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import java.util.stream.StreamSupport;
import org.junit.ClassRule;
import org.junit.Test;

public final class ITGrpcTest {
  @ClassRule(order = 1)
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("it-grpc").setDockerImageTag("v0.26.0").build();

  @ClassRule(order = 2)
  public static final StorageFixture storageFixture =
      StorageFixture.from(
          () ->
              StorageOptions.grpc()
                  .setHost(TEST_BENCH.getGRPCBaseUri())
                  .setCredentials(NoCredentials.getInstance())
                  .setProjectId("test-project-id")
                  .build());

  @ClassRule(order = 3)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder()
          .setBucketNameFmtString("java-storage-gcs-grpc-team-%s")
          .setCleanupStrategy(CleanupStrategy.ALWAYS)
          .setHandle(storageFixture::getInstance)
          .build();

  @Test
  public void testCreateBucket() {
    final String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket bucket = storageFixture.getInstance().create(BucketInfo.of(bucketName));
    assertThat(bucket.getName()).isEqualTo(bucketName);
  }

  @Test
  public void listBlobs() {
    BucketInfo bucketInfo = bucketFixture.getBucketInfo();
    Page<Blob> list = storageFixture.getInstance().list(bucketInfo.getName());
    ImmutableList<String> bucketNames =
        StreamSupport.stream(list.iterateAll().spliterator(), false)
            .map(Blob::getName)
            .collect(ImmutableList.toImmutableList());

    assertThat(bucketNames).isEmpty();
  }

  @Test
  public void listBuckets() {
    Page<Bucket> list = storageFixture.getInstance().list();
    ImmutableList<String> bucketNames =
        StreamSupport.stream(list.iterateAll().spliterator(), false)
            .map(Bucket::getName)
            .collect(ImmutableList.toImmutableList());

    assertThat(bucketNames).contains(bucketFixture.getBucketInfo().getName());
  }
}
