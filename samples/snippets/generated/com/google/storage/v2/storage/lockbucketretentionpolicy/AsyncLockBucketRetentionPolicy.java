/*
 * Copyright 2024 Google LLC
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

package com.google.storage.v2.samples;

// [START storage_v2_generated_Storage_LockBucketRetentionPolicy_async]
import com.google.api.core.ApiFuture;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.LockBucketRetentionPolicyRequest;
import com.google.storage.v2.StorageClient;

public class AsyncLockBucketRetentionPolicy {

  public static void main(String[] args) throws Exception {
    asyncLockBucketRetentionPolicy();
  }

  public static void asyncLockBucketRetentionPolicy() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      LockBucketRetentionPolicyRequest request =
          LockBucketRetentionPolicyRequest.newBuilder()
              .setBucket(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setIfMetagenerationMatch(1043427781)
              .build();
      ApiFuture<Bucket> future =
          storageClient.lockBucketRetentionPolicyCallable().futureCall(request);
      // Do something.
      Bucket response = future.get();
    }
  }
}
// [END storage_v2_generated_Storage_LockBucketRetentionPolicy_async]
