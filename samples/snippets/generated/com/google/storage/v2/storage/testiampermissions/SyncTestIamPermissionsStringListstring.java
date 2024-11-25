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

// [START storage_v2_generated_Storage_TestIamPermissions_StringListstring_sync]
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.storage.v2.CryptoKeyName;
import com.google.storage.v2.StorageClient;
import java.util.ArrayList;
import java.util.List;

public class SyncTestIamPermissionsStringListstring {

  public static void main(String[] args) throws Exception {
    syncTestIamPermissionsStringListstring();
  }

  public static void syncTestIamPermissionsStringListstring() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageClient storageClient = StorageClient.create()) {
      String resource =
          CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]").toString();
      List<String> permissions = new ArrayList<>();
      TestIamPermissionsResponse response = storageClient.testIamPermissions(resource, permissions);
    }
  }
}
// [END storage_v2_generated_Storage_TestIamPermissions_StringListstring_sync]
