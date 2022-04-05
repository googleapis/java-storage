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

// [START storage_create_bucket_dual_region]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class CreateBucketDualRegion {

  public static void createBucketDualRegion(
      String projectId, String bucketName, String firstRegion, String secondRegion) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID to give your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // One of the regions the dual region bucket is to be created in.
    // String firstRegion = "US-EAST1";

    // The second region the dual region bucket is to be created in.
    // String secondRegion = "US-WEST1";

    // Construct the dual region ie. "US-EAST1+US-WEST1"
    String dualRegion = firstRegion + "+" + secondRegion;

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    Bucket bucket =
        storage.create(BucketInfo.newBuilder(bucketName).setLocation(dualRegion).build());

    System.out.println(
        "Created bucket " + bucket.getName() + " in location " + bucket.getLocation());
  }
}
// [END storage_create_bucket_dual_region]
