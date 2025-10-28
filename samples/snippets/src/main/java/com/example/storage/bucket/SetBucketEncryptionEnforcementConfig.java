/*
 * Copyright 2025 Google LLC
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

// [START storage_set_encryption_enforcement_config]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.CustomerManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.BucketInfo.CustomerSuppliedEncryptionEnforcementConfig;
import com.google.cloud.storage.BucketInfo.EncryptionEnforcementRestrictionMode;
import com.google.cloud.storage.BucketInfo.GoogleManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class SetBucketEncryptionEnforcementConfig {
  public static void setBucketEncryptionEnforcementConfig(
      String projectId, String bucketName, String kmsKeyName) throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The name of the KMS key to use
    // String kmsKeyName =
    // "projects/your-project-id/locations/us/keyRings/my_key_ring/cryptoKeys/my_key"

    try (Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {

      // Example 1: Enforce GMEK Only
      setGmekOnlyPolicy(storage, bucketName + "_gmek_only");

      // Example 2: Enforce CMEK Only
      setCmekOnlyPolicy(storage, bucketName + "_cmek_only", kmsKeyName);

      // Example 3: Restrict CSEK (Ransomware Protection)
      restrictCsekPolicy(storage, bucketName + "_restrict_csek");
    }
  }

  public static void setGmekOnlyPolicy(Storage storage, String bucketName) {
    System.out.println("--- Setting GMEK-Only Policy on bucket " + bucketName + " ---");
    GoogleManagedEncryptionEnforcementConfig gmekConfig =
        GoogleManagedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.NOT_RESTRICTED);
    CustomerManagedEncryptionEnforcementConfig cmekConfig =
        CustomerManagedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);
    CustomerSuppliedEncryptionEnforcementConfig csekConfig =
        CustomerSuppliedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);

    BucketInfo bucketInfo =
        BucketInfo.newBuilder(bucketName)
            .setGoogleManagedEncryptionEnforcementConfig(gmekConfig)
            .setCustomerManagedEncryptionEnforcementConfig(cmekConfig)
            .setCustomerSuppliedEncryptionEnforcementConfig(csekConfig)
            .build();

    Bucket bucket = storage.create(bucketInfo);
    System.out.println(
        "Bucket " + bucket.getName() + " created with GMEK-only enforcement policy.");
  }

  public static void setCmekOnlyPolicy(Storage storage, String bucketName, String kmsKeyName) {
    System.out.println("--- Setting CMEK-Only Policy on bucket " + bucketName + " ---");
    GoogleManagedEncryptionEnforcementConfig gmekConfig =
        GoogleManagedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);
    CustomerManagedEncryptionEnforcementConfig cmekConfig =
        CustomerManagedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.NOT_RESTRICTED);
    CustomerSuppliedEncryptionEnforcementConfig csekConfig =
        CustomerSuppliedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);

    BucketInfo bucketInfo =
        BucketInfo.newBuilder(bucketName)
            .setDefaultKmsKeyName(kmsKeyName)
            .setGoogleManagedEncryptionEnforcementConfig(gmekConfig)
            .setCustomerManagedEncryptionEnforcementConfig(cmekConfig)
            .setCustomerSuppliedEncryptionEnforcementConfig(csekConfig)
            .build();

    Bucket bucket = storage.create(bucketInfo);
    System.out.println(
        "Bucket " + bucket.getName() + " created with CMEK-only enforcement policy.");
  }

  public static void restrictCsekPolicy(Storage storage, String bucketName) {
    System.out.println("--- Setting Restrict-CSEK Policy on bucket " + bucketName + " ---");
    CustomerSuppliedEncryptionEnforcementConfig csekConfig =
        CustomerSuppliedEncryptionEnforcementConfig.of(
            EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED);

    BucketInfo bucketInfo =
        BucketInfo.newBuilder(bucketName)
            .setCustomerSuppliedEncryptionEnforcementConfig(csekConfig)
            .build();

    Bucket bucket = storage.create(bucketInfo);
    System.out.println("Bucket " + bucket.getName() + " created with a policy to restrict CSEK.");
  }
}
// [END storage_get_encryption_enforcement_config]
