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

// [START storage_v2_generated_Storage_UpdateBucket_sync]
import com.google.protobuf.FieldMask;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.UpdateBucketRequest;

public class SyncUpdateBucket {

  public static void main(String[] args) throws Exception {
    syncUpdateBucket();
  }

  public static void syncUpdateBucket() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      UpdateBucketRequest request =
          UpdateBucketRequest.newBuilder()
              .setBucket(Bucket.newBuilder().build())
              .setIfMetagenerationMatch(1043427781)
              .setIfMetagenerationNotMatch(1025430873)
              .setPredefinedAcl("predefinedAcl1207041188")
              .setPredefinedDefaultObjectAcl("predefinedDefaultObjectAcl2109168048")
              .setUpdateMask(FieldMask.newBuilder().build())
              .build();
      Bucket response = storageClient.updateBucket(request);
    }
  }
}
// [END storage_v2_generated_Storage_UpdateBucket_sync]
