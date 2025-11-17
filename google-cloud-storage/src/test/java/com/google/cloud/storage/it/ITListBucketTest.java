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

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

@RunWith(StorageITRunner.class)
@CrossRun(
        backends = {Backend.TEST_BENCH},
        transports = {Transport.HTTP})
public class ITListBucketTest {
    @Inject
    public Storage storage;

    @Inject public BucketInfo bucketInfo;

    @Inject public Generator generator;


    @Test
    public void testCreateBucket() {
        String bucketName = generator.randomBucketName();
        Bucket bucket = storage.create(BucketInfo.of(bucketName));
        assertThat(bucket.getName()).isEqualTo(bucketName);
    }

    @Test
    public void testListBucketWithPartialSuccess() {
        String NORMAL_BUCKET_NAME = "normal_bucket";
        Bucket normalBucket = storage.create(BucketInfo.of(NORMAL_BUCKET_NAME));
        String UNREACHABLE_BUCKET_NAME = "unreachable_bucket";
        Bucket unreachableBucket = storage.create(BucketInfo.of(UNREACHABLE_BUCKET_NAME));

        Page<Bucket> page = storage.list(Storage.BucketListOption.returnPartialSuccess(true));
        ImmutableList<Bucket> bucketList = ImmutableList.of(normalBucket, unreachableBucket);
        System.out.println("page buckets: " + page.getValues());
        assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
        Bucket secondBucket = Iterables.get(page.getValues(), 1);
        assertTrue("The second bucket should be unreachable", secondBucket.isUnreachable());
    }

}
