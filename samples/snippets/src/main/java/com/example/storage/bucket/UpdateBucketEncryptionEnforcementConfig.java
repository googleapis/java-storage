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

// [START storage_update_bucket_encryption_enforcement_config]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.CustomerManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.BucketInfo.CustomerSuppliedEncryptionEnforcementConfig;
import com.google.cloud.storage.BucketInfo.EncryptionEnforcementRestrictionMode;
import com.google.cloud.storage.BucketInfo.GoogleManagedEncryptionEnforcementConfig;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public final class UpdateBucketEncryptionEnforcementConfig {

  private UpdateBucketEncryptionEnforcementConfig() { }

  /**
   * Example on how to update encryption enforcement configuration.
   *
   * @param projectId The ID of your GCP project
   * @param bucket The ID of your GCS bucket
   * @throws Exception If an error occurs
   */
  public static void updateBucketEncryptionEnforcementConfig(
      final String projectId, final String bucket) throws Exception {
    try (Storage s = StorageOptions.newBuilder().setProjectId(projectId)
        .build().getService()) {

      // Example 1: Remove all encryption enforcement
      removeAll(s, bucket);

      // Example 2: Remove specific enforcement
      removeSpecific(s, bucket, "GMEK");

      // Example 3: Update a specific encryption type's restriction mode
      EncryptionEnforcementRestrictionMode m =
          EncryptionEnforcementRestrictionMode.FULLY_RESTRICTED;
      update(s, bucket, "CMEK", m);
    }
  }

  /**
   * Removes all encryption enforcement policies from the given bucket.
   *
   * @param s The Cloud Storage service client
   * @param bucket The name of the bucket to update
   */
  public static void removeAll(
      final Storage s, final String bucket) {
    Bucket b = s.get(bucket);
    if (b == null) {
      System.out.println("Bucket " + bucket + " does not exist.");
      return;
    }

    BucketInfo bucketInfo =
        b.toBuilder()
            .setGoogleManagedEncryptionEnforcementConfig(null)
            .setCustomerManagedEncryptionEnforcementConfig(null)
            .setCustomerSuppliedEncryptionEnforcementConfig(null)
            .build();

    s.update(bucketInfo);
    System.out.println("All removed");
  }

  /**
   * Removes a specific encryption enforcement policy from the given bucket.
   *
   * @param s The Cloud Storage service client
   * @param bucket The name of the bucket to update
   * @param type The type of encryption to remove (GMEK, CMEK, or CSEK)
   */
  public static void removeSpecific(
      final Storage s,
      final String bucket,
      final String type) {
    Bucket b = s.get(bucket);
    if (b == null) {
      System.out.println("Bucket " + bucket + " does not exist.");
      return;
    }

    BucketInfo.Builder builder = b.toBuilder();

    switch (type.toUpperCase()) {
      case "GMEK":
        builder.setGoogleManagedEncryptionEnforcementConfig(null);
        break;
      case "CMEK":
        builder.setCustomerManagedEncryptionEnforcementConfig(null);
        break;
      case "CSEK":
        builder.setCustomerSuppliedEncryptionEnforcementConfig(null);
        break;
      default:
        System.out.println("Invalid encryption type.");
        return;
    }

    s.update(builder.build());
    System.out.println("Removed " + type);
  }

  /**
   * Updates the restriction mode for a specific encryption type.
   *
   * @param s The Cloud Storage service client
   * @param bucket The name of the bucket to update
   * @param type The type of encryption to update (GMEK, CMEK, CSEK)
   * @param mode The restriction mode to apply
   */
  public static void update(
      final Storage s,
      final String bucket,
      final String type,
      final EncryptionEnforcementRestrictionMode mode) {
    Bucket b = s.get(bucket);
    if (b == null) {
      System.out.println("Bucket " + bucket + " does not exist.");
      return;
    }

    BucketInfo.Builder builder = b.toBuilder();

    switch (type.toUpperCase()) {
      case "GMEK":
        builder.setGoogleManagedEncryptionEnforcementConfig(
            GoogleManagedEncryptionEnforcementConfig.of(mode));
        break;
      case "CMEK":
        builder.setCustomerManagedEncryptionEnforcementConfig(
            CustomerManagedEncryptionEnforcementConfig.of(mode));
        break;
      case "CSEK":
        builder.setCustomerSuppliedEncryptionEnforcementConfig(
            CustomerSuppliedEncryptionEnforcementConfig.of(mode));
        break;
      default:
        System.out.println("Invalid encryption type.");
        return;
    }

    s.update(builder.build());
    System.out.println("Updated " + type + " to " + mode.name());
  }
}
// [END storage_update_bucket_encryption_enforcement_config]
