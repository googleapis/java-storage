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

package com.example.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.BucketCleaner;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.common.collect.ImmutableList;
import com.google.storage.control.v2.StorageControlClient;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.PROD)
public final class ITCleanupOldBucketsTest {

  @Inject
  @StorageFixture(Transport.HTTP)
  public Storage storage;
  @Inject
  public StorageControlClient ctrl;

  @Test
  public void cleanupOldBuckets() {
    Page<Bucket> page = storage.list(
        BucketListOption.fields(BucketField.NAME, BucketField.TIME_CREATED));

    OffsetDateTime now = Instant.now().atOffset(ZoneOffset.UTC);
    OffsetDateTime _24hoursAgo = now.minusHours(24);

    ImmutableList<String> bucketsToClean = page.streamAll()
        .map(Bucket::asBucketInfo)
        .filter(bucket -> {
          OffsetDateTime ctime = bucket.getCreateTimeOffsetDateTime();
          return ctime.isBefore(_24hoursAgo);
        })
        .map(BucketInfo::getName)
        .collect(ImmutableList.toImmutableList());

    for (String bucketName : bucketsToClean) {
      BucketCleaner.doCleanup(bucketName, storage, ctrl);
    }
  }
}
