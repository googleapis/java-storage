/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.control.v2.samples;

// [START storage_v2_generated_StorageControl_CreateAnywhereCache_sync]
import com.google.storage.control.v2.AnywhereCache;
import com.google.storage.control.v2.BucketName;
import com.google.storage.control.v2.CreateAnywhereCacheRequest;
import com.google.storage.control.v2.StorageControlClient;

public class SyncCreateAnywhereCache {

  public static void main(String[] args) throws Exception {
    syncCreateAnywhereCache();
  }

  public static void syncCreateAnywhereCache() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageControlClient storageControlClient = StorageControlClient.create()) {
      CreateAnywhereCacheRequest request =
          CreateAnywhereCacheRequest.newBuilder()
              .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setAnywhereCache(AnywhereCache.newBuilder().build())
              .setRequestId("requestId693933066")
              .build();
      AnywhereCache response = storageControlClient.createAnywhereCacheAsync(request).get();
    }
  }
}
// [END storage_v2_generated_StorageControl_CreateAnywhereCache_sync]
