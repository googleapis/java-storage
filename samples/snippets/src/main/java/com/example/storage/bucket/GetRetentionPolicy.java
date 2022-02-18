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

// [START storage_get_retention_policy]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import java.util.Date;

public class GetRetentionPolicy {
    public static void getRetentionPolicy(String projectId, String bucketName) throws StorageException {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Bucket bucket = storage.get(bucketName, Storage.BucketGetOption.fields(Storage.BucketField.RETENTION_POLICY));

        System.out.println("Retention Policy for " + bucketName);
        System.out.println("Retention Period: " + bucket.getRetentionPeriod());
        if (bucket.retentionPolicyIsLocked() != null && bucket.retentionPolicyIsLocked()) {
            System.out.println("Retention Policy is locked");
        }
        if (bucket.getRetentionEffectiveTime() != null) {
            System.out.println("Effective Time: " + new Date(bucket.getRetentionEffectiveTime()));
        }
    }
}
// [END storage_get_retention_policy]
