/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.storage.object;

// [START storage_delete_file]
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class DeleteObject {
  public static void deleteObject(String projectId, String bucketName, String objectName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Optional: set a generation-match precondition to avoid potential race
    // conditions and data corruptions. The request to upload returns a 412 error if
    // the object's generation number does not match your precondition.
    Storage.BlobSourceOption precondition =
        Storage.BlobSourceOption.generationMatch(
            storage.get(bucketName, objectName).getGeneration());

    storage.delete(bucketName, objectName, precondition);

    System.out.println("Object " + objectName + " was deleted from " + bucketName);
  }
}
// [END storage_delete_file]
