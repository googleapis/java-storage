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

// [START storage_v2_generated_Storage_QueryWriteStatus_async]
import com.google.api.core.ApiFuture;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.StorageClient;

public class AsyncQueryWriteStatus {

  public static void main(String[] args) throws Exception {
    asyncQueryWriteStatus();
  }

  public static void asyncQueryWriteStatus() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      QueryWriteStatusRequest request =
          QueryWriteStatusRequest.newBuilder()
              .setUploadId("uploadId1563990780")
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .build();
      ApiFuture<QueryWriteStatusResponse> future =
          storageClient.queryWriteStatusCallable().futureCall(request);
      // Do something.
      QueryWriteStatusResponse response = future.get();
    }
  }
}
// [END storage_v2_generated_Storage_QueryWriteStatus_async]
