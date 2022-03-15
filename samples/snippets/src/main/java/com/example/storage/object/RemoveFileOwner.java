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

// [START storage_remove_file_owner]

import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class RemoveFileOwner {

  public static void removeFileOwner(
      String projectId, String bucketName, String userEmail, String blobName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // Email of the user you wish to remove as a file owner
    // String userEmail = "someuser@domain.com"

    // The name of the blob/file that you wish to modify permissions on
    // String blobName = "your-blob-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Blob blob = storage.get(BlobId.of(bucketName, blobName));
    User ownerToRemove = new User(userEmail);

    boolean success = blob.deleteAcl(ownerToRemove);
    if (success) {
      System.out.println(
          "Removed user "
              + userEmail
              + " as an owner on file "
              + blobName
              + " in bucket "
              + bucketName);
    } else {
      System.out.println("User " + userEmail + " was not found");
    }
  }
}
// [END storage_remove_file_owner]
