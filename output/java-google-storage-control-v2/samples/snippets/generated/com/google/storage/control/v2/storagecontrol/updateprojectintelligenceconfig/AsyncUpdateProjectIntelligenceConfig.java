/*
 * Copyright 2025 Google LLC
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

package com.google.storage.control.v2.samples;

// [START storage_v2_generated_StorageControl_UpdateProjectIntelligenceConfig_async]
import com.google.api.core.ApiFuture;
import com.google.protobuf.FieldMask;
import com.google.storage.control.v2.IntelligenceConfig;
import com.google.storage.control.v2.StorageControlClient;
import com.google.storage.control.v2.UpdateProjectIntelligenceConfigRequest;

public class AsyncUpdateProjectIntelligenceConfig {

  public static void main(String[] args) throws Exception {
    asyncUpdateProjectIntelligenceConfig();
  }

  public static void asyncUpdateProjectIntelligenceConfig() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (StorageControlClient storageControlClient = StorageControlClient.create()) {
      UpdateProjectIntelligenceConfigRequest request =
          UpdateProjectIntelligenceConfigRequest.newBuilder()
              .setIntelligenceConfig(IntelligenceConfig.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .setRequestId("requestId693933066")
              .build();
      ApiFuture<IntelligenceConfig> future =
          storageControlClient.updateProjectIntelligenceConfigCallable().futureCall(request);
      // Do something.
      IntelligenceConfig response = future.get();
    }
  }
}
// [END storage_v2_generated_StorageControl_UpdateProjectIntelligenceConfig_async]
