package com.example.storage.bucket;

/*
 * Copyright 2024 Google LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.StorageOptions;

public class ListBucketsWithPartialSuccess {
  public static void listBucketsWithPartialSuccess(String projectId) {
    // [START storage_list_buckets_with_partial_success]
    // The ID of your GCP project
    // String projectId = "your-project-id";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Iterable<Bucket> buckets = storage.list(BucketListOption.returnPartialSuccess(true)).iterateAll();

    System.out.println("Buckets:");
    for (Bucket bucket : buckets) {
      System.out.println(bucket.getName() + ", isUnreachable: " + bucket.isUnreachable());
    }
    // [END storage_list_buckets_with_partial_success]
  }

  public static void main(String[] args) throws Exception {
    listBucketsWithPartialSuccess(System.getenv("GOOGLE_CLOUD_PROJECT"));
  }
}
