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

package com.google.cloud.storage.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.ParallelParameterized;
import com.google.cloud.storage.conformance.retry.TestBench;
import java.util.Arrays;
import java.util.Collection;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(ParallelParameterized.class)
public class ITServiceAccountTest {
  @ClassRule
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("it-grpc").build();

  @Rule public final StorageFixture storageFixture;

  private static final String SERVICE_ACCOUNT_EMAIL_SUFFIX =
      "@gs-project-accounts.iam.gserviceaccount.com";

  public ITServiceAccountTest(String clientName, StorageFixture storageFixture) {
    this.storageFixture = storageFixture;
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    StorageFixture grpcStorageFixture =
        StorageFixture.from(
            () ->
                StorageOptions.grpc()
                    .setHost(TEST_BENCH.getGRPCBaseUri())
                    .setCredentials(NoCredentials.getInstance())
                    .setProjectId("test-project-id")
                    .build());
    StorageFixture jsonStorageFixture = StorageFixture.defaultHttp();
    return Arrays.asList(
        new Object[] {"JSON/storage.googleapis.com", jsonStorageFixture},
        new Object[] {"GRPC/" + TEST_BENCH.getGRPCBaseUri(), grpcStorageFixture});
  }

  @Test
  public void testGetServiceAccount() {
    String projectId = storageFixture.getInstance().getOptions().getProjectId();
    ServiceAccount serviceAccount = storageFixture.getInstance().getServiceAccount(projectId);
    assertNotNull(serviceAccount);
    assertTrue(serviceAccount.getEmail().endsWith(SERVICE_ACCOUNT_EMAIL_SUFFIX));
  }
}
