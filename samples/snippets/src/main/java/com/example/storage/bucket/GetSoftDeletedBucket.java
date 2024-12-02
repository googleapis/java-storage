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

package com.example.storage.bucket;

// [START storage_get_soft_deleted_bucket]
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GetSoftDeletedBucket {
  public static void getSoftDeletedBucket(String projectId, String bucketName, long generation) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The generation of the bucket to restore
    // long generation = 123456789;

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Bucket bucket =
        storage.get(
            bucketName,
            Storage.BucketGetOption.softDeleted(true),
            Storage.BucketGetOption.generation(generation));

    // The following fields are only set for soft-deleted buckets
    String softDeleteTime = bucket.getSoftDeleteTime().toString();
    String hardDeleteTime = bucket.getHardDeleteTime().toString();

    System.out.println(
        "The bucket "
            + bucketName
            + " was soft-deleted at "
            + softDeleteTime
            + " and will be fully deleted at "
            + hardDeleteTime);
  }
}
// [END storage_get_soft_deleted_bucket]
