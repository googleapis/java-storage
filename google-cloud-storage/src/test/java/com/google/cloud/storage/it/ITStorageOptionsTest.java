/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.storage.it;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.junit.Test;

public final class ITStorageOptionsTest {

  @Test
  public void clientShouldConstructCleanly_WithNoCredentials_http() throws Exception {
    StorageOptions options =
        StorageOptions.http().setCredentials(NoCredentials.getInstance()).build();
    doTest(options);
  }

  @Test
  public void clientShouldConstructCleanly_WithNoCredentials_grpc() throws Exception {
    StorageOptions options =
        StorageOptions.grpc().setCredentials(NoCredentials.getInstance()).build();
    doTest(options);
  }

  @Test
  public void clientShouldConstructCleanly_nullAccessToken_google_http() throws Exception {
    GoogleCredentials cred = GoogleCredentials.create(/* accessToken= */ null);
    StorageOptions options = StorageOptions.http().setCredentials(cred).build();
    doTest(options);
  }

  @Test
  public void clientShouldConstructCleanly_nullAccessToken_google_grpc() throws Exception {
    GoogleCredentials cred = GoogleCredentials.create(/* accessToken= */ null);
    StorageOptions options = StorageOptions.grpc().setCredentials(cred).build();
    doTest(options);
  }

  @Test
  public void clientShouldConstructCleanly_nullAccessToken_oauth_http() throws Exception {
    OAuth2Credentials cred = OAuth2Credentials.create(null);
    StorageOptions options = StorageOptions.http().setCredentials(cred).build();
    doTest(options);
  }

  @Test
  public void clientShouldConstructCleanly_nullAccessToken_oauth_grpc() throws Exception {
    OAuth2Credentials cred = OAuth2Credentials.create(null);
    StorageOptions options = StorageOptions.grpc().setCredentials(cred).build();
    doTest(options);
  }

  private static void doTest(StorageOptions options) throws Exception {
    //noinspection EmptyTryBlock
    try (Storage ignore = options.getService()) {}
  }
}
