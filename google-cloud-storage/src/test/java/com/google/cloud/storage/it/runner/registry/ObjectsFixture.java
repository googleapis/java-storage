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

package com.google.cloud.storage.it.runner.registry;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;

/** Globally scoped objects correlated with a specific backend and bucket */
public final class ObjectsFixture implements ManagedLifecycle {

  private final Storage s;
  private final BucketInfo bucket;

  private BlobInfo info1;
  private BlobInfo info2;
  private BlobInfo info3;
  private BlobInfo info4;

  ObjectsFixture(Storage s, BucketInfo bucket) {
    this.s = s;
    this.bucket = bucket;
  }

  @Override
  public Object get() {
    return this;
  }

  public BlobInfo getInfo1() {
    return info1;
  }

  public BlobInfo getInfo2() {
    return info2;
  }

  public BlobInfo getInfo3() {
    return info3;
  }

  public BlobInfo getInfo4() {
    return info4;
  }

  @Override
  public void start() {
    String bucketName = bucket.getName();

    BlobId blobId1 = BlobId.of(bucketName, objName("001"));
    BlobId blobId2 = BlobId.of(bucketName, objName("002"));
    BlobId blobId3 = BlobId.of(bucketName, objName("003"));
    BlobId blobId4 = BlobId.of(bucketName, objName("004"));

    BlobInfo info1 = BlobInfo.newBuilder(blobId1).setMetadata(ImmutableMap.of("pow", "1")).build();
    BlobInfo info2 = BlobInfo.newBuilder(blobId2).setMetadata(ImmutableMap.of("pow", "2")).build();
    BlobInfo info3 = BlobInfo.newBuilder(blobId3).setMetadata(ImmutableMap.of("pow", "3")).build();
    BlobInfo info4 = BlobInfo.newBuilder(blobId4).setMetadata(ImmutableMap.of("pow", "4")).build();
    s.create(info1, "A".getBytes(StandardCharsets.UTF_8), BlobTargetOption.doesNotExist());

    ComposeRequest c2 =
        ComposeRequest.newBuilder()
            .addSource(blobId1.getName(), blobId1.getName())
            .setTarget(info2)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    ComposeRequest c3 =
        ComposeRequest.newBuilder()
            .addSource(blobId2.getName(), blobId2.getName())
            .setTarget(info3)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    ComposeRequest c4 =
        ComposeRequest.newBuilder()
            .addSource(blobId3.getName(), blobId3.getName())
            .setTarget(info4)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    s.compose(c2);
    s.compose(c3);
    s.compose(c4);

    this.info1 = s.get(blobId1).asBlobInfo();
    this.info2 = s.get(blobId2).asBlobInfo();
    this.info3 = s.get(blobId3).asBlobInfo();
    this.info4 = s.get(blobId4).asBlobInfo();
  }

  @Override
  public void stop() {}

  private static String objName(String name) {
    return String.format("%s/%s", ObjectsFixture.class.getSimpleName(), name);
  }
}
