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

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;

/** Shim to lift a BucketInfo to be a managed bucket instance */
final class BucketInfoShim implements ManagedLifecycle {

  private final BucketInfo bucketInfo;
  private final Storage s;

  private BucketInfo createdBucket;

  BucketInfoShim(BucketInfo bucketInfo, Storage s) {
    this.bucketInfo = bucketInfo;
    this.s = s;
  }

  public BucketInfo getBucketInfo() {
    return createdBucket;
  }

  @Override
  public Object get() {
    return bucketInfo;
  }

  @Override
  public void start() {
    createdBucket = s.create(bucketInfo).asBucketInfo();
  }

  @Override
  public void stop() {
    // BucketCleaner.doCleanup(bucketInfo.getName(), s);
  }
}
