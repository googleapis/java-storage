/*
 * Copyright 2024 Google LLC
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

// [START storage_disable_soft_delete]
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Duration;

public class DisableSoftDelete {
  public static void disableSoftDelete(String projectId, String bucketName) throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    try (Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {
    Bucket bucket = storage.get(bucketName);
    bucket.toBuilder()
        .setSoftDeletePolicy(
            // Setting the retention duration to 0 disables Soft Delete.
            BucketInfo.SoftDeletePolicy.newBuilder()
                .setRetentionDuration(Duration.ofSeconds(0))
                .build())
        .build()
        .update();

    System.out.println("Soft delete for " + bucketName + " was disabled");
    }
  }
}
// [END storage_disable_soft_delete]
