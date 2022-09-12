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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.AbortIncompleteMPUAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.ParallelParameterized;
import com.google.cloud.storage.conformance.retry.TestBench;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(ParallelParameterized.class)
public class ITBucketLifecycleTest {

  @ClassRule(order = 0)
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("it-grpc").build();

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureGrpc =
      StorageFixture.from(
          () ->
              StorageOptions.grpc()
                  .setHost(TEST_BENCH.getGRPCBaseUri())
                  .setCredentials(NoCredentials.getInstance())
                  .setProjectId("test-project-id")
                  .build());

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureHttp = StorageFixture.defaultHttp();

  private static final LifecycleRule LIFECYCLE_RULE_1 =
      new LifecycleRule(
          LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
          LifecycleCondition.newBuilder()
              .setAge(1)
              .setNumberOfNewerVersions(3)
              .setIsLive(false)
              .setMatchesStorageClass(ImmutableList.of(StorageClass.COLDLINE))
              .build());
  private static final LifecycleRule LIFECYCLE_RULE_2 =
      new LifecycleRule(
          LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(1).build());
  private static final ImmutableList<LifecycleRule> LIFECYCLE_RULES =
      ImmutableList.of(LIFECYCLE_RULE_1, LIFECYCLE_RULE_2);

  private final StorageFixture storageFixture;
  private final String clientName;

  public ITBucketLifecycleTest(String clientName, StorageFixture storageFixture) {
    this.clientName = clientName;
    this.storageFixture = storageFixture;
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(
        new Object[] {"JSON/Prod", storageFixtureHttp},
        new Object[] {"GRPC/TestBench", storageFixtureGrpc});
  }

  @Test
  public void testGetBucketLifecycleRules() {
    String lifecycleTestBucketName = RemoteStorageHelper.generateBucketName();
    storageFixture
        .getInstance()
        .create(
            BucketInfo.newBuilder(lifecycleTestBucketName)
                .setLocation("us")
                .setLifecycleRules(
                    ImmutableList.of(
                        new LifecycleRule(
                            LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
                            LifecycleCondition.newBuilder()
                                .setAge(1)
                                .setNumberOfNewerVersions(3)
                                .setIsLive(false)
                                .setCreatedBeforeOffsetDateTime(OffsetDateTime.now())
                                .setMatchesStorageClass(ImmutableList.of(StorageClass.COLDLINE))
                                .setDaysSinceNoncurrentTime(30)
                                .setNoncurrentTimeBeforeOffsetDateTime(OffsetDateTime.now())
                                .setCustomTimeBeforeOffsetDateTime(OffsetDateTime.now())
                                .setDaysSinceCustomTime(30)
                                .build())))
                .build());
    Bucket remoteBucket =
        storageFixture
            .getInstance()
            .get(lifecycleTestBucketName, Storage.BucketGetOption.fields(BucketField.LIFECYCLE));
    LifecycleRule lifecycleRule = remoteBucket.getLifecycleRules().get(0);
    try {
      assertTrue(
          lifecycleRule
              .getAction()
              .getActionType()
              .equals(LifecycleRule.SetStorageClassLifecycleAction.TYPE));
      assertEquals(3, lifecycleRule.getCondition().getNumberOfNewerVersions().intValue());
      assertNotNull(lifecycleRule.getCondition().getCreatedBeforeOffsetDateTime());
      assertFalse(lifecycleRule.getCondition().getIsLive());
      assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
      assertEquals(1, lifecycleRule.getCondition().getMatchesStorageClass().size());
      assertEquals(30, lifecycleRule.getCondition().getDaysSinceNoncurrentTime().intValue());
      assertNotNull(lifecycleRule.getCondition().getNoncurrentTimeBeforeOffsetDateTime());
      assertEquals(30, lifecycleRule.getCondition().getDaysSinceCustomTime().intValue());
      assertNotNull(lifecycleRule.getCondition().getCustomTimeBeforeOffsetDateTime());
    } finally {
      storageFixture.getInstance().delete(lifecycleTestBucketName);
    }
  }

  @Test
  public void testGetBucketAbortMPULifecycle() {
    String lifecycleTestBucketName = RemoteStorageHelper.generateBucketName();
    storageFixture
        .getInstance()
        .create(
            BucketInfo.newBuilder(lifecycleTestBucketName)
                .setLocation("us")
                .setLifecycleRules(
                    ImmutableList.of(
                        new LifecycleRule(
                            LifecycleAction.newAbortIncompleteMPUploadAction(),
                            LifecycleCondition.newBuilder().setAge(1).build())))
                .build());
    Bucket remoteBucket =
        storageFixture
            .getInstance()
            .get(lifecycleTestBucketName, Storage.BucketGetOption.fields(BucketField.LIFECYCLE));
    LifecycleRule lifecycleRule = remoteBucket.getLifecycleRules().get(0);
    try {
      assertEquals(AbortIncompleteMPUAction.TYPE, lifecycleRule.getAction().getActionType());
      assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
    } finally {
      storageFixture.getInstance().delete(lifecycleTestBucketName);
    }
  }

  @Test
  public void testDeleteLifecycleRules() throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket bucket =
        storageFixture
            .getInstance()
            .create(
                BucketInfo.newBuilder(bucketName)
                    .setLocation("us")
                    .setLifecycleRules(LIFECYCLE_RULES)
                    .build());
    assertThat(bucket.getLifecycleRules()).isNotNull();
    assertThat(bucket.getLifecycleRules()).hasSize(2);
    try {
      Bucket updatedBucket = bucket.toBuilder().deleteLifecycleRules().build();
      storageFixture.getInstance().update(updatedBucket);
      assertThat(updatedBucket.getLifecycleRules()).hasSize(0);
    } finally {
      RemoteStorageHelper.forceDelete(
          storageFixture.getInstance(), bucketName, 5, TimeUnit.SECONDS);
    }
  }
}
