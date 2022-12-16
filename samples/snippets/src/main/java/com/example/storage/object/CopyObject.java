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

// [START storage_copy_file]

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class CopyObject {
  public static void copyObject(
      String projectId, String sourceBucketName, String objectName, String targetBucketName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of the bucket the original object is in
    // String sourceBucketName = "your-source-bucket";

    // The ID of the GCS object to copy
    // String objectName = "your-object-name";

    // The ID of the bucket to copy the object to
    // String targetBucketName = "target-object-bucket";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId source = BlobId.of(sourceBucketName, objectName);
    BlobId target =
        BlobId.of(
            targetBucketName, objectName); // you could change "objectName" to rename the object

    // Optional: set a generation-match precondition to avoid potential race
    // conditions and data corruptions. The request returns a 412 error if the
    // preconditions are not met.
    Storage.BlobTargetOption precondition;
    if (storage.get(targetBucketName, objectName) == null) {
      // For a target object that does not yet exist, set the DoesNotExist precondition.
      // This will cause the request to fail if the object is created before the request runs.
      precondition = Storage.BlobTargetOption.doesNotExist();

    } else {
      // If the destination already exists in your bucket, instead set a generation-match
      // precondition. This will cause the request to fail if the existing object's generation
      // changes before the request runs.
      precondition =
          Storage.BlobTargetOption.generationMatch(
              storage.get(targetBucketName, objectName).getGeneration());
    }

    storage.copy(
        Storage.CopyRequest.newBuilder().setSource(source).setTarget(target, precondition).build());

    System.out.println(
        "Copied object "
            + objectName
            + " from bucket "
            + sourceBucketName
            + " to "
            + targetBucketName);
  }
}
// [END storage_copy_file]
