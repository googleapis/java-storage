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

// [START storage_get_soft_delete_policy]
import com.google.cloud.storage.BucketInfo.SoftDeletePolicy;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Duration;

public class GetSoftDeletePolicy {
  public static void getSoftDeletePolicy(String projectId, String bucketName) throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    try (Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {
    SoftDeletePolicy policy = storage.get(bucketName).getSoftDeletePolicy();

    if (Duration.ofSeconds(0).equals(policy.getRetentionDuration())) {
      System.out.println("Soft delete is disabled for " + bucketName);
    } else {
      System.out.println("The soft delete policy for " + bucketName + " is:");
      System.out.println(policy);
    }
    }
  }
}
// [END storage_get_soft_delete_policy]
