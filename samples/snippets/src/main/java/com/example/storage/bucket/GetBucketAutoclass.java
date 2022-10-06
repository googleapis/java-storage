/*
 * Copyright 2022 Google LLC
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

// [START storage_get_autoclass]

import com.google.cloud.storage.BucketInfo.Autoclass;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GetBucketAutoclass {
  public static void getBucketAutoclass(String projectId, String bucketName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Autoclass autoclass = storage.get(bucketName).getAutoclass();
    String status = autoclass.getEnabled() ? "enabled" : "disabled";
    String toggleTime = autoclass.getToggleTime().toString();

    System.out.println("Autoclass is currently " + status + " for bucket " + bucketName
        + " and was last changed at " + toggleTime);
  }
}
// [END storage_get_autoclass]
