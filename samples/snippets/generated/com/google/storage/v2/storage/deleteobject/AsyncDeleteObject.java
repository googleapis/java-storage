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

// [START storage_v2_generated_Storage_DeleteObject_async]
import com.google.api.core.ApiFuture;
import com.google.protobuf.Empty;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.StorageClient;

public class AsyncDeleteObject {

  public static void main(String[] args) throws Exception {
    asyncDeleteObject();
  }

  public static void asyncDeleteObject() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      DeleteObjectRequest request =
          DeleteObjectRequest.newBuilder()
              .setBucket(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setIfGenerationMatch(-1086241088)
              .setIfGenerationNotMatch(1475720404)
              .setIfMetagenerationMatch(1043427781)
              .setIfMetagenerationNotMatch(1025430873)
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .build();
      ApiFuture<Empty> future = storageClient.deleteObjectCallable().futureCall(request);
      // Do something.
      future.get();
    }
  }
}
// [END storage_v2_generated_Storage_DeleteObject_async]
