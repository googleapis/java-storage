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

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.StorageFixture;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class ITBucketLifecycleRulesTest {

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureHttp = StorageFixture.defaultHttp();

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureGrpc = StorageFixture.defaultGrpc();

  @Rule public final TestName testName = new TestName();

  private final Storage storage;

  public ITBucketLifecycleRulesTest(String ignore, StorageFixture storageFixture) {
    this.storage = storageFixture.getInstance();
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> parameters() {
    return ImmutableList.of(
        new Object[] {"JSON/Prod", storageFixtureHttp},
        new Object[] {"GRPC/Prod", storageFixtureGrpc});
  }

  @Test
  public void deleteRule_addingALabelToABucketWithASingleDeleteRuleOnlyModifiesTheLabels()
      throws Exception {
    LifecycleRule d1 =
        new LifecycleRule(
            LifecycleAction.newDeleteAction(),
            LifecycleCondition.newBuilder()
                .setMatchesPrefix(ImmutableList.of("pre"))
                .setMatchesSuffix(ImmutableList.of("suf"))
                .setAge(50)
                .build());
    BucketInfo info = baseInfo().setLifecycleRules(ImmutableList.of(d1)).build();

    try (TemporaryBucket tmp =
        TemporaryBucket.newBuilder()
            .setBucketInfo(info)
            .setStorage(storageFixtureHttp.getInstance())
            .build()) {
      BucketInfo bucket = tmp.getBucket();
      assertThat(bucket.getLabels()).isNull();

      ImmutableMap<String, String> labels = ImmutableMap.of("label1", "val1");
      BucketInfo withLabels = bucket.toBuilder().setLabels(labels).build();
      Bucket update = storage.update(withLabels, BucketTargetOption.metagenerationMatch());
      assertThat(update.getLabels()).isEqualTo(labels);
      assertThat(update.getLifecycleRules()).isEqualTo(ImmutableList.of(d1));
    }
  }

  @Test
  public void deleteRule_modifyingLifecycleRulesMatchesLastOperation() throws Exception {
    BucketInfo info;
    {
      LifecycleRule d1 =
          new LifecycleRule(
              LifecycleAction.newDeleteAction(),
              LifecycleCondition.newBuilder()
                  .setMatchesPrefix(ImmutableList.of("pre"))
                  .setMatchesSuffix(ImmutableList.of("suf"))
                  .setAge(50)
                  .build());
      info = baseInfo().setLifecycleRules(ImmutableList.of(d1)).build();
    }

    try (TemporaryBucket tmp =
        TemporaryBucket.newBuilder()
            .setBucketInfo(info)
            .setStorage(storageFixtureHttp.getInstance())
            .build()) {
      BucketInfo bucket = tmp.getBucket();

      ImmutableList<LifecycleRule> newRules =
          bucket.getLifecycleRules().stream()
              .map(
                  r -> {
                    if (r.getAction().equals(LifecycleAction.newDeleteAction())) {
                      LifecycleCondition condition = r.getCondition();
                      LifecycleCondition.Builder b = condition.toBuilder();
                      b.setMatchesPrefix(
                          ImmutableList.<String>builder()
                              .addAll(condition.getMatchesPrefix())
                              .add("a")
                              .build());
                      b.setMatchesSuffix(
                          ImmutableList.<String>builder()
                              .addAll(condition.getMatchesSuffix())
                              .add("z")
                              .build());
                      return new LifecycleRule(LifecycleAction.newDeleteAction(), b.build());
                    } else {
                      return r;
                    }
                  })
              .collect(ImmutableList.toImmutableList());

      BucketInfo modifiedRules = bucket.toBuilder().setLifecycleRules(newRules).build();
      Bucket update = storage.update(modifiedRules, BucketTargetOption.metagenerationMatch());
      assertThat(update.getLifecycleRules()).isEqualTo(newRules);
    }
  }

  private static BucketInfo.Builder baseInfo() {
    return BucketInfo.newBuilder(String.format("java-storage-grpc-%s", UUID.randomUUID()));
  }
}
