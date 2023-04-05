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

// [START storage_set_client_endpoint]
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class SetClientEndpoint {

  public static ServiceAccountCredentials setOAuthEndpoint(
      String oauthEndpoint, String serviceAccountPath) throws IOException, URISyntaxException {
    // The oauth endpoint you wish to target
    // String oauthEndpoint = "https://oauth2.googleapis.com/token";

    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.fromStream(new FileInputStream(serviceAccountPath));
    credentials = credentials.toBuilder().setTokenServerUri(new URL(oauthEndpoint).toURI()).build();
    return credentials;
  }

  public static void setClientEndpoint(String projectId, String endpoint) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The endpoint you wish to target
    // String endpoint = "https://storage.googleapis.com"

    // You might want to change the oauth2 endpoint as well
    // ServiceAccountCredentials credentials = setOAuthEndpoint(oauthEndpoint, serviceAccountPath);

    Storage storage =
        StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setHost(endpoint)
            // .setCredentials(credentials)
            .build()
            .getService();

    System.out.println(
        "Storage Client initialized with endpoint " + storage.getOptions().getHost());
  }
}

// [END storage_set_client_endpoint]
