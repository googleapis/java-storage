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

// [START storage_v2_generated_Storage_BidiReadObject_async]
import com.google.api.gax.rpc.BidiStream;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.ReadRange;
import com.google.storage.v2.StorageClient;
import java.util.ArrayList;

public class AsyncBidiReadObject {

  public static void main(String[] args) throws Exception {
    asyncBidiReadObject();
  }

  public static void asyncBidiReadObject() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      BidiStream<BidiReadObjectRequest, BidiReadObjectResponse> bidiStream =
          storageClient.bidiReadObjectCallable().call();
      BidiReadObjectRequest request =
          BidiReadObjectRequest.newBuilder()
              .setReadObjectSpec(BidiReadObjectSpec.newBuilder().build())
              .addAllReadRanges(new ArrayList<ReadRange>())
              .build();
      bidiStream.send(request);
      for (BidiReadObjectResponse response : bidiStream) {
        // Do something when a response is received.
      }
    }
  }
}
// [END storage_v2_generated_Storage_BidiReadObject_async]
