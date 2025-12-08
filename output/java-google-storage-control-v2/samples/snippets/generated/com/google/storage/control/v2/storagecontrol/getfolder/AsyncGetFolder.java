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

// [START storage_v2_generated_StorageControl_GetFolder_async]
import com.google.api.core.ApiFuture;
import com.google.storage.control.v2.Folder;
import com.google.storage.control.v2.FolderName;
import com.google.storage.control.v2.GetFolderRequest;
import com.google.storage.control.v2.StorageControlClient;

public class AsyncGetFolder {

  public static void main(String[] args) throws Exception {
    asyncGetFolder();
  }

  public static void asyncGetFolder() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageControlClient storageControlClient = StorageControlClient.create()) {
      GetFolderRequest request =
          GetFolderRequest.newBuilder()
              .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
              .setIfMetagenerationMatch(1043427781)
              .setIfMetagenerationNotMatch(1025430873)
              .setRequestId("requestId693933066")
              .build();
      ApiFuture<Folder> future = storageControlClient.getFolderCallable().futureCall(request);
      // Do something.
      Folder response = future.get();
    }
  }
}
// [END storage_v2_generated_StorageControl_GetFolder_async]
