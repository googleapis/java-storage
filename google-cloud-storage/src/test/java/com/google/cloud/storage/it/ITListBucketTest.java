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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.TEST_BENCH},
    transports = {Transport.GRPC, Transport.HTTP})

public class ITListBucketTest {
  @Inject public Storage storage;

  @Inject public BucketInfo defaultBucket;
  
  @Inject public Generator generator;

  @Inject
  @BucketFixture(BucketType.HNS)
  public BucketInfo hnsBucket;

  @Test
  public void testListBucketWithPartialSuccess() throws Exception {
    doTest(Reachability.Unreachable, BucketListOption.returnPartialSuccess(true));
  }

  @Test
  public void testListBucketWithoutPartialSuccess() throws Exception {
    doTest(Reachability.Reachable);
  }

  private void doTest(
      Reachability expectedReachabilityOfUnreachableBucket, BucketListOption... bucketListOption)
      throws Exception {
    // TESTBENCH considers a bucket to be unreachable if the bucket name contains "unreachable"
    String name = generator.randomBucketName() + ".unreachable";
    BucketInfo info = BucketInfo.of(name);
    try (TemporaryBucket tmpBucket =
        TemporaryBucket.newBuilder().setBucketInfo(info).setStorage(storage).build()) {
      // bucket name to unreachable status
      Map<String, Reachability> expected =
          ImmutableMap.of(
              defaultBucket.getName(), Reachability.Reachable,
              hnsBucket.getName(), Reachability.Reachable,
              tmpBucket.getBucket().getName(), expectedReachabilityOfUnreachableBucket);

      Page<Bucket> page = storage.list(bucketListOption);

      Map<String, Reachability> actual =
          page.streamAll().collect(Collectors.toMap(BucketInfo::getName, Reachability::forBucket));

      assertThat(actual).containsAtLeastEntriesIn(expected);
    }
  }

  private enum Reachability {
    Reachable,
    Unreachable;

    static Reachability forBucket(BucketInfo b) {
      if (b.isUnreachable() != null && b.isUnreachable()) {
        return Unreachable;
      } else {
        return Reachable;
      }
    }
  }

  @Test
  public void testListBucketWithPartialSuccessPagination() {
    // Create additional buckets to force pagination
    // We already have NORMAL_BUCKET_NAME and UNREACHABLE_BUCKET_NAME
    // Total 2 buckets. If we set pageSize=1, we should get at least 2 pages.
    // To be safe and ensure we have enough data, let's create a few more normal buckets.
    createBucket("normal_bucket_2");
    createBucket("normal_bucket_3");

    Page<Bucket> page =
        storage.list(
            Storage.BucketListOption.returnPartialSuccess(true),
            Storage.BucketListOption.pageSize(1));

    List<Bucket> allBuckets = new ArrayList<>();
    page.iterateAll().forEach(allBuckets::add);

    // Verify we found all expected buckets
    ImmutableList<String> bucketNames =
        allBuckets.stream().map(Bucket::getName).collect(ImmutableList.toImmutableList());

    assertThat(bucketNames)
        .containsExactly(
            NORMAL_BUCKET_NAME,
            "normal_bucket_2",
            "normal_bucket_3",
            EXPECTED_UNREACHABLE_BUCKET_NAME);

    // Verify unreachable bucket is present and marked correctly
    Bucket actualUnreachableBucket =
        allBuckets.stream()
            .filter(b -> b.getName().contains(UNREACHABLE_BUCKET_NAME))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Unreachable bucket not found in list"));

    assertThat(actualUnreachableBucket.getName()).isEqualTo(EXPECTED_UNREACHABLE_BUCKET_NAME);
    assertTrue(
        "The unreachable bucket must have the isUnreachable flag set to true",
        actualUnreachableBucket.isUnreachable());
    assertThat(actualUnreachableBucket.getOwner()).isNull();

    // Verify normal buckets are NOT unreachable
    allBuckets.stream()
        .filter(b -> !b.getName().contains(UNREACHABLE_BUCKET_NAME))
        .forEach(b -> assertThat(b.isUnreachable()).isNull());
  }
}
