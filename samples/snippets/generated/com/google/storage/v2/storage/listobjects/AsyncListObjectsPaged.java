/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.v2.samples;

// [START storage_v2_generated_Storage_ListObjects_Paged_async]
import com.google.common.base.Strings;
import com.google.protobuf.FieldMask;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.ListObjectsResponse;
import com.google.storage.v2.Object;
import com.google.storage.v2.StorageClient;

public class AsyncListObjectsPaged {

  public static void main(String[] args) throws Exception {
    asyncListObjectsPaged();
  }

  public static void asyncListObjectsPaged() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      ListObjectsRequest request =
          ListObjectsRequest.newBuilder()
              .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
              .setPageSize(883849137)
              .setPageToken("pageToken873572522")
              .setDelimiter("delimiter-250518009")
              .setIncludeTrailingDelimiter(true)
              .setPrefix("prefix-980110702")
              .setVersions(true)
              .setReadMask(FieldMask.newBuilder().build())
              .setLexicographicStart("lexicographicStart-2093413008")
              .setLexicographicEnd("lexicographicEnd1646968169")
              .setSoftDeleted(true)
              .setIncludeFoldersAsPrefixes(true)
              .setMatchGlob("matchGlob613636317")
              .build();
      while (true) {
        ListObjectsResponse response = storageClient.listObjectsCallable().call(request);
        for (Object element : response.getObjectsList()) {
          // doThingsWith(element);
        }
        String nextPageToken = response.getNextPageToken();
        if (!Strings.isNullOrEmpty(nextPageToken)) {
          request = request.toBuilder().setPageToken(nextPageToken).build();
        } else {
          break;
        }
      }
    }
  }
}
// [END storage_v2_generated_Storage_ListObjects_Paged_async]
