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

import static com.google.cloud.storage.TestUtils.hashMapOf;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Objects.requireNonNull;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.ITNestedUpdateMaskTest.NestedUpdateMaskParametersProvider;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.ParallelFriendly;
import com.google.cloud.storage.it.runner.annotations.Parameterized;
import com.google.cloud.storage.it.runner.annotations.Parameterized.Parameter;
import com.google.cloud.storage.it.runner.annotations.Parameterized.ParametersProvider;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A set of tests to specifically test scenarios related to update handling of {@link
 * BlobInfo#getMetadata()} and {@link BucketInfo#getLabels()} and the various permutations which can
 * be used to add and remove keys.
 */
@RunWith(StorageITRunner.class)
@CrossRun(
    backends = Backend.PROD,
    transports = {Transport.HTTP, Transport.GRPC})
@Parameterized(NestedUpdateMaskParametersProvider.class)
@ParallelFriendly
public final class ITNestedUpdateMaskTest {

  @Inject public Generator generator;

  @Inject public Storage storage;

  @Inject public BucketInfo bucket;

  @Parameter public Param param;

  public static final class NestedUpdateMaskParametersProvider implements ParametersProvider {
    private static final Map<String, String> empty = ImmutableMap.of();
    private static final Map<String, String> k1a = ImmutableMap.of("k1", "a");
    private static final Map<String, String> k2b = ImmutableMap.of("k2", "b");
    private static final Map<String, String> k1z = ImmutableMap.of("k1", "z");
    private static final Map<String, String> k1a_k2b = ImmutableMap.of("k1", "a", "k2", "b");
    private static final Map<String, String> k1z_k2b = ImmutableMap.of("k1", "z", "k2", "b");
    private static final Map<String, String> k1a_k2null = hashMapOf("k1", "a", "k2", null);
    private static final Map<String, String> k1null = hashMapOf("k1", null);
    private static final Map<String, String> k2null = hashMapOf("k2", null);

    @Override
    public ImmutableList<?> parameters() {
      return ImmutableList.of(
          new Param(UpdateMethod.SET, "null to 1", null, k1a, k1a),
          new Param(UpdateMethod.ADD, "null to 1", null, k1a, k1a),
          new Param(UpdateMethod.SET, "empty to 1", empty, k1a, k1a),
          new Param(UpdateMethod.ADD, "empty to 1", empty, k1a, k1a),
          new Param(UpdateMethod.SET, "1 to 2", k1a, k1a_k2b, k1a_k2b),
          new Param(UpdateMethod.ADD, "1 to 2", k1a, k2b, k1a_k2b),
          new Param(UpdateMethod.SET, "2 keys, modify 1 value", k1a_k2b, k1z_k2b, k1z_k2b),
          new Param(UpdateMethod.ADD, "2 keys, modify 1 value", k1a_k2b, k1z, k1z_k2b),
          new Param(UpdateMethod.SET, "2 keys, modify 1 null", k1a_k2b, k1a_k2null, k1a),
          new Param(UpdateMethod.ADD, "2 keys, modify 1 null", k1a_k2b, k2null, k1a),
          new Param(UpdateMethod.SET, "1 key, set empty", k1a, empty, null),
          new Param(UpdateMethod.ADD, "1 key, null key", k1a, k1null, null),
          new Param(UpdateMethod.SET, "2 keys, set null", k1a_k2b, null, null));
    }
  }

  @Test
  public void testBucketLabels() throws Exception {
    BucketInfo bucket = newBucketInfo(param.initial);
    try (TemporaryBucket tempB =
        TemporaryBucket.newBuilder().setBucketInfo(bucket).setStorage(storage).build()) {
      BucketInfo gen1 = tempB.getBucket();

      BucketInfo.Builder b = gen1.toBuilder();
      switch (param.updateMethod) {
        case SET:
          b.setLabels(param.update);
          break;
        case ADD:
          assertThat(param.update).isNotNull();
          b.addAllLabels(param.update);
          break;
      }
      BucketInfo modified = b.build();
      Bucket gen2 = storage.update(modified, BucketTargetOption.metagenerationMatch());
      assertThat(gen2.getLabels()).isEqualTo(param.expected);
    }
  }

  @Test
  public void testBlobMetadata() {
    BlobInfo blob = newBlobInfo(param.initial);
    Blob gen1 = storage.create(blob, BlobTargetOption.doesNotExist());
    BlobInfo.Builder b = gen1.toBuilder();
    switch (param.updateMethod) {
      case SET:
        b.setMetadata(param.update);
        break;
      case ADD:
        assertThat(param.update).isNotNull();
        b.addAllMetadata(param.update);
        break;
    }
    BlobInfo modified = b.build();
    Blob gen2 = storage.update(modified, BlobTargetOption.metagenerationMatch());
    assertThat(gen2.getMetadata()).isEqualTo(param.expected);
  }

  private BlobInfo newBlobInfo(Map<String, String> metadata) {
    String blobName = generator.randomObjectName();
    BlobInfo.Builder builder = BlobInfo.newBuilder(bucket, blobName);
    if (metadata != null) {
      builder.setMetadata(metadata);
    }
    return builder.build();
  }

  private BucketInfo newBucketInfo(Map<String, String> metadata) {
    BucketInfo.Builder builder = BucketInfo.newBuilder(generator.randomBucketName());
    if (metadata != null) {
      builder.setLabels(metadata);
    }
    return builder.build();
  }

  private enum UpdateMethod {
    SET,
    ADD
  }

  private static final class Param {
    private final UpdateMethod updateMethod;
    private final String description;
    @Nullable private final Map<@NonNull String, @Nullable String> initial;
    @Nullable private final Map<@NonNull String, @Nullable String> update;
    @Nullable private final Map<@NonNull String, @Nullable String> expected;

    private Param(
        UpdateMethod updateMethod,
        String description,
        @Nullable Map<@NonNull String, @Nullable String> initial,
        @Nullable Map<@NonNull String, @Nullable String> update,
        @Nullable Map<@NonNull String, @Nullable String> expected) {
      requireNonNull(description, "description must be non null");
      requireNonNull(updateMethod, "updateMethod must be non null");
      assertWithMessage("Specifying null update with ADD invalid")
          .that(updateMethod == UpdateMethod.ADD && update == null)
          .isFalse();
      this.description = description;
      this.updateMethod = updateMethod;
      this.initial = initial;
      this.update = update;
      this.expected = expected;
    }

    @Override
    public String toString() {
      return String.format("%s via %s", description, updateMethod);
    }
  }
}
