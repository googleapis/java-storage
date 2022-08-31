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

// [START storage_move_file]
import com.google.cloud.storage.*;

public class MoveObject {
  public static void moveObject(
      String projectId,
      String sourceBucketName,
      String sourceObjectName,
      String targetBucketName,
      String targetObjectName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String sourceObjectName = "your-object-name";

    // The ID of the bucket to move the object objectName to
    // String targetBucketName = "target-object-bucket"

    // The ID of your GCS object
    // String targetObjectName = "your-new-object-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId source = BlobId.of(sourceBucketName, sourceObjectName);
    BlobId target = BlobId.of(targetBucketName, targetObjectName);

    // Optional: set a generation-match precondition to avoid potential race
    // conditions and data corruptions. The request returns a 412 error if the
    // preconditions are not met.
    // For a target object that does not yet exist, set the DoesNotExist precondition.
    Storage.BlobTargetOption precondition = Storage.BlobTargetOption.doesNotExist();
    // If the destination already exists in your bucket, instead set a generation-match precondition:
    // Storage.BlobTargetOption precondition = Storage.BlobTargetOption.generationMatch();

    // Copy source object to target object
    storage.copy(Storage.CopyRequest.newBuilder()
            .setSource(source)
            .setTarget(target, precondition)
            .build());
    Blob copiedObject = storage.get(target);
    // Delete the original blob now that we've copied to where we want it, finishing the "move"
    // operation
    storage.get(source).delete();

    System.out.println(
        "Moved object "
            + sourceObjectName
            + " from bucket "
            + sourceBucketName
            + " to "
            + targetObjectName
            + " in bucket "
            + copiedObject.getBucket());
  }
}
// [END storage_move_file]
