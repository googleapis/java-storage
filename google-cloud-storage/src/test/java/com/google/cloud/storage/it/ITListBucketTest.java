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
import static org.junit.Assert.assertTrue;

import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.TestBench;
import com.google.cloud.storage.it.runner.registry.TestBench.RetryTestResource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.stream.StreamSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.TEST_BENCH},
    transports = {Transport.GRPC})
public class ITListBucketTest {
  @Inject public Storage storage;

  private static final String NORMAL_BUCKET_NAME = "normal_bucket";
  // For testing purposes, the TESTBENCH considers a bucket to be unreachable if the bucket name
  // contains "unreachable"
  private static final String UNREACHABLE_BUCKET_NAME = "bucket-unreachable";

  // The unreachable buckets are returned as a list of bucket resource names in string form. (e.g.
  // "projects/_/buckets/bucket1")
  private static final String EXPECTED_UNREACHABLE_BUCKET_NAME =
      "projects/_/buckets/" + UNREACHABLE_BUCKET_NAME;

  @Before
  public void setup() {
    Bucket normalBucket = storage.create(BucketInfo.of(NORMAL_BUCKET_NAME));
    Bucket unreachableBucket = storage.create(BucketInfo.of(UNREACHABLE_BUCKET_NAME));
  }

  @After
  public void tearDown() {
    BucketCleaner.doCleanup(NORMAL_BUCKET_NAME, storage);
    BucketCleaner.doCleanup(UNREACHABLE_BUCKET_NAME, storage);
  }

  @Test
  public void testListBucketWithPartialSuccess() {
    Page<Bucket> page = storage.list(Storage.BucketListOption.returnPartialSuccess(true));
    System.out.println("page: " + page);
    Iterable<Bucket> allBuckets = page.getValues();

    Bucket actualNormalBucket =
        Iterables.getOnlyElement(
            Iterables.filter(allBuckets, b -> b.getName().equals(NORMAL_BUCKET_NAME)));
    assertThat(actualNormalBucket.getName()).isEqualTo(NORMAL_BUCKET_NAME);

    Bucket actualUnreachableBucket =
        Iterables.getOnlyElement(
            Iterables.filter(allBuckets, b -> b.getName().contains(UNREACHABLE_BUCKET_NAME)));
    assertThat(actualUnreachableBucket.getName()).isEqualTo(EXPECTED_UNREACHABLE_BUCKET_NAME);
    assertTrue(
        "The unreachable bucket must have the isUnreachable flag set to true",
        actualUnreachableBucket.isUnreachable());
  }

  @Test
  public void testListBucketWithoutPartialSuccess() {
    Page<Bucket> page = storage.list();
    ImmutableList<String> bucketNames =
        StreamSupport.stream(page.iterateAll().spliterator(), false)
            .map(Bucket::getName)
            .collect(ImmutableList.toImmutableList());
    assertThat(bucketNames).contains(NORMAL_BUCKET_NAME);
    assertThat(bucketNames).doesNotContain(EXPECTED_UNREACHABLE_BUCKET_NAME);
  }
}
