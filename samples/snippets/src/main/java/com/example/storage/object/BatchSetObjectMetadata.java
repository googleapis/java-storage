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

package com.example.storage.object;

// [START storage_batch_request]
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageOptions;
import java.util.HashMap;
import java.util.Map;

public class BatchSetObjectMetadata {
  public static void batchSetObjectMetadata(
      String projectId, String bucketName, String directoryPrefix) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The directory prefix. All objects in the bucket with this prefix will have their metadata
    // updated
    // String directoryPrefix = "yourDirectory/";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Map<String, String> newMetadata = new HashMap<>();
    newMetadata.put("keyToAddOrUpdate", "value");
    Page<Blob> blobs =
        storage.list(
            bucketName,
            Storage.BlobListOption.prefix(directoryPrefix),
            Storage.BlobListOption.currentDirectory());
    StorageBatch batchRequest = storage.batch();

    // Add all blobs with the given prefix to the batch request
    for (Blob blob : blobs.iterateAll()) {
      batchRequest.update(blob.toBuilder().setMetadata(newMetadata).build());
    }

    // Execute the batch request
    batchRequest.submit();

    System.out.println(
        "All blobs in bucket "
            + bucketName
            + " with prefix '"
            + directoryPrefix
            + "' had their metadata updated.");
  }
}
// [END storage_batch_request]
