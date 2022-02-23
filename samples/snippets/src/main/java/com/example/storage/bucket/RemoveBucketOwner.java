/*
 * Copyright 2022 Google LLC
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

// [START storage_remove_bucket_owner]

import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class RemoveBucketOwner {

  public static void removeBucketOwner(String bucketName, String userEmail) {
    // The ID to give your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // Email of the user you wish to remove as an owner
    // String userEmail = "someuser@domain.com"

    Storage storage = StorageOptions.newBuilder().build().getService();
    Bucket bucket = storage.get(bucketName);
    User ownerToRemove = new User(userEmail);

    boolean success = bucket.deleteAcl(ownerToRemove);
    if (success) {
      System.out.println("Removed user " + userEmail + " as an owner on " + bucketName);
    } else {
      System.out.println("User " + userEmail + " was not found");
    }
  }
}

// [END storage_remove_bucket_owner]