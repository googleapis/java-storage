/*
 * Copyright 2024 Google LLC
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
import com.google.cloud.storage.BucketInfo.Autoclass;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.PROD},
    transports = {Transport.HTTP, Transport.GRPC})
public final class ITAutoclassTest {

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Test
  public void testAutoclassTerminalStorageClassWhenNotYetEnabled() throws Exception {
    BucketInfo tmpBucketBaseConfig = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tmpBucket =
        TemporaryBucket.newBuilder()
            .setBucketInfo(tmpBucketBaseConfig)
            .setStorage(storage)
            .build()) {

      BucketInfo gen1 = tmpBucket.getBucket();

      BucketInfo next =
          gen1.toBuilder()
              .setAutoclass(
                  Autoclass.newBuilder().setTerminalStorageClass(StorageClass.ARCHIVE).build())
              .build();

      Bucket gen2 = storage.update(next, BucketTargetOption.metagenerationMatch());

      assertThat(gen2.getAutoclass().getEnabled()).isTrue();
    }
  }

  @Test
  public void testAutoclassTerminalStorageClassWhenAlreadyEnabled() throws Exception {
    BucketInfo tmpBucketBaseConfig =
        BucketInfo.newBuilder(generator.randomBucketName())
            .setAutoclass(Autoclass.newBuilder().setEnabled(true).build())
            .build();
    try (TemporaryBucket tmpBucket =
        TemporaryBucket.newBuilder()
            .setBucketInfo(tmpBucketBaseConfig)
            .setStorage(storage)
            .build()) {

      BucketInfo gen1 = tmpBucket.getBucket();

      BucketInfo next =
          gen1.toBuilder()
              // .setAutoclass(Autoclass.newBuilder().setTerminalStorageClass(StorageClass.ARCHIVE).build())
              .setAutoclass(
                  gen1.getAutoclass()
                      .toBuilder()
                      .setTerminalStorageClass(StorageClass.ARCHIVE)
                      .build())
              .build();

      Bucket gen2 = storage.update(next, BucketTargetOption.metagenerationMatch());

      assertThat(gen2.getAutoclass().getEnabled()).isTrue();
    }
  }
}
