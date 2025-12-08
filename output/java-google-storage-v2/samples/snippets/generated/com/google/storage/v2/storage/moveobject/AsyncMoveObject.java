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

package com.google.storage.v2.samples;

// [START storage_v2_generated_Storage_MoveObject_async]
import com.google.api.core.ApiFuture;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.MoveObjectRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.StorageClient;

public class AsyncMoveObject {

  public static void main(String[] args) throws Exception {
    asyncMoveObject();
  }

  public static void asyncMoveObject() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      MoveObjectRequest request =
          MoveObjectRequest.newBuilder()
              .setBucket(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setSourceObject("sourceObject1196439354")
              .setDestinationObject("destinationObject-1761603347")
              .setIfSourceGenerationMatch(-1427877280)
              .setIfSourceGenerationNotMatch(1575612532)
              .setIfSourceMetagenerationMatch(1143319909)
              .setIfSourceMetagenerationNotMatch(1900822777)
              .setIfGenerationMatch(-1086241088)
              .setIfGenerationNotMatch(1475720404)
              .setIfMetagenerationMatch(1043427781)
              .setIfMetagenerationNotMatch(1025430873)
              .build();
      ApiFuture<Object> future = storageClient.moveObjectCallable().futureCall(request);
      // Do something.
      Object response = future.get();
    }
  }
}
// [END storage_v2_generated_Storage_MoveObject_async]
