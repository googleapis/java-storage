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

// [START storage_add_bucket_label]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.HashMap;
import java.util.Map;

public class AddBucketLabel {
  public static void addBucketLabel(
      String projectId, String bucketName, String labelKey, String labelValue) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The key of the label to add
    // String labelKey = "label-key-to-add";

    // The value of the label to add
    // String labelValue = "label-value-to-add";

    Map<String, String> newLabels = new HashMap<>();
    newLabels.put(labelKey, labelValue);

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Bucket bucket = storage.get(bucketName);
    Map<String, String> labels = bucket.getLabels();
    if (labels != null) {
      newLabels.putAll(labels);
    }
    bucket.toBuilder().setLabels(newLabels).build().update();

    System.out.println(
        "Added label " + labelKey + " with value " + labelValue + " to bucket " + bucketName + ".");
  }
}
// [END storage_add_bucket_label]
