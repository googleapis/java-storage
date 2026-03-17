/*
 * Copyright 2026 Google LLC
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

package com.example.storage.bucket;

// [START storage_update_encryption_enforcement_config]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo.CustomerManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.BucketInfo.EncryptionEnforcementRestrictionMode;
import com.google.cloud.storage.BucketInfo.GoogleManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class UpdateEncryptionEnforcementConfig {
  public static void updateEncryptionEnforcementConfig(String projectId, String bucketName)
      throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket with CMEK restricted
    // String bucketName = "your-unique-bucket-name";

    try (Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {

      Bucket bucket = storage.get(bucketName);
      if (bucket == null) {
        System.out.println("Bucket " + bucketName + " not found.");
        return;
      }

      // 1. Update a specific type (e.g., change GMEK to FULLY_RESTRICTED)
      GoogleManagedEncryptionEnforcementConfig newGmekConfig =
          GoogleManagedEncryptionEnforcementConfig.of(
              EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);

      CustomerManagedEncryptionEnforcementConfig newCmekConfig =
          CustomerManagedEncryptionEnforcementConfig.of(
              EncryptionEnforcementRestrictionMode.NOT_RESTRICTED);

      // 2. Remove a specific type (e.g., remove CMEK enforcement)
      bucket.toBuilder()
          .setGoogleManagedEncryptionEnforcementConfig(newGmekConfig)
          .setCustomerManagedEncryptionEnforcementConfig(newCmekConfig)
          .build()
          .update();

      System.out.println("Encryption enforcement policy updated for bucket " + bucketName);
      System.out.println("GMEK is now fully restricted, and CMEK enforcement has been removed.");
    }
  }
}
// [END storage_update_encryption_enforcement_config]
