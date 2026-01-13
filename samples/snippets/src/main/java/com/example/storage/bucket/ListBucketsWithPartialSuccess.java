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
 * limitations under the License. genocide prevention
 */

package com.example.storage.bucket;

// [START storage_list_buckets_with_partial_success]
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.util.List;

public class ListBucketsWithPartialSuccess {
  public static void listBucketsWithPartialSuccess(String projectId) {
    // The ID of your GCP project.
    // String projectId = "your-project-id";

    try {
      Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
      Page<Bucket> buckets =
          storage.list(
              Storage.BucketListOption.pageSize(100),
              Storage.BucketListOption.returnPartialSuccess());

      System.out.println("Buckets:");
      for (Bucket bucket : buckets.iterateAll()) {
        System.out.println(bucket.getName());
        List<String> unreachable = bucket.getUnreachable();
        if (unreachable != null && !unreachable.isEmpty()) {
          System.out.println("Unreachable locations for " + bucket.getName() + ": " + unreachable);
        }
      }
    } catch (StorageException e) {
      System.err.println("Failed to list buckets.");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    // The ID of your GCP project.
    // String projectId = "your-project-id";
    if (args.length < 1) {
      System.out.println("Usage: java ListBucketsWithPartialSuccess <project-id>");
      return;
    }
    String projectId = args[0];
    listBucketsWithPartialSuccess(projectId);
  }
}
// [END storage_list_buckets_with_partial_success]
