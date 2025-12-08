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

// [START storage_v2_generated_StorageControl_ListManagedFolders_async]
import com.google.api.core.ApiFuture;
import com.google.storage.control.v2.BucketName;
import com.google.storage.control.v2.ListManagedFoldersRequest;
import com.google.storage.control.v2.ManagedFolder;
import com.google.storage.control.v2.StorageControlClient;

public class AsyncListManagedFolders {

  public static void main(String[] args) throws Exception {
    asyncListManagedFolders();
  }

  public static void asyncListManagedFolders() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageControlClient storageControlClient = StorageControlClient.create()) {
      ListManagedFoldersRequest request =
          ListManagedFoldersRequest.newBuilder()
              .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setPageSize(883849137)
              .setPageToken("pageToken873572522")
              .setPrefix("prefix-980110702")
              .setRequestId("requestId693933066")
              .build();
      ApiFuture<ManagedFolder> future =
          storageControlClient.listManagedFoldersPagedCallable().futureCall(request);
      // Do something.
      for (ManagedFolder element : future.get().iterateAll()) {
        // doThingsWith(element);
      }
    }
  }
}
// [END storage_v2_generated_StorageControl_ListManagedFolders_async]
