/*
 * Copyright 2019 Google LLC
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

package com.example.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.storage.hmac.ActivateHmacKey;
import com.example.storage.hmac.CreateHmacKey;
import com.example.storage.hmac.DeactivateHmacKey;
import com.example.storage.hmac.DeleteHmacKey;
import com.example.storage.hmac.GetHmacKey;
import com.example.storage.hmac.ListHmacKeys;
import com.google.api.gax.paging.Page;
import com.google.cloud.ServiceOptions;
import com.google.cloud.storage.HmacKey;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITHmacSnippets {
  private static final String HMAC_KEY_TEST_SERVICE_ACCOUNT =
      System.getenv("IT_SERVICE_ACCOUNT_EMAIL");
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private final PrintStream standardOut = new PrintStream(new FileOutputStream(FileDescriptor.out));

  private static Storage storage;

  @BeforeClass
  public static void beforeClass() {
    RemoteStorageHelper helper = RemoteStorageHelper.create();
    storage = helper.getOptions().toBuilder().setRetrySettings(
            helper.getOptions().getRetrySettings().toBuilder().setRetryDelayMultiplier(3.0).build()
    ).build().getService();
  }

  @Before
  public void before() {
    cleanUpHmacKeys(ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT));
  }

  private static void cleanUpHmacKeys(ServiceAccount serviceAccount) {
    Page<HmacKey.HmacKeyMetadata> metadatas =
        storage.listHmacKeys(Storage.ListHmacKeysOption.serviceAccount(serviceAccount));
    for (HmacKey.HmacKeyMetadata hmacKeyMetadata : metadatas.iterateAll()) {
      if (hmacKeyMetadata.getState() == HmacKeyState.ACTIVE) {
        hmacKeyMetadata = storage.updateHmacKeyState(hmacKeyMetadata, HmacKeyState.INACTIVE);
      }
      if (hmacKeyMetadata.getState() == HmacKeyState.INACTIVE) {
        storage.deleteHmacKey(hmacKeyMetadata);
      }
    }
  }

  @Test
  public void testCreateHmacKey() {
    final ByteArrayOutputStream snippetOutputCapture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(snippetOutputCapture));
    CreateHmacKey.createHmacKey(HMAC_KEY_TEST_SERVICE_ACCOUNT, PROJECT_ID);
    String snippetOutput = snippetOutputCapture.toString();
    System.setOut(standardOut);
    String accessId = snippetOutput.split("Access ID: ")[1].split("\n")[0];
    assertNotNull(storage.getHmacKey(accessId));
  }

  @Test
  public void testGetHmacKey() {
    HmacKey hmacKey = storage.createHmacKey(ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT));

    final ByteArrayOutputStream snippetOutputCapture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(snippetOutputCapture));
    GetHmacKey.getHmacKey(hmacKey.getMetadata().getAccessId(), PROJECT_ID);
    String snippetOutput = snippetOutputCapture.toString();
    System.setOut(standardOut);
    Assert.assertTrue(snippetOutput.contains(HMAC_KEY_TEST_SERVICE_ACCOUNT));
  }

  @Test
  public void testActivateHmacKey() {
    HmacKey hmacKey = storage.createHmacKey(ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT));
    HmacKeyMetadata metadata =
        storage.updateHmacKeyState(hmacKey.getMetadata(), HmacKeyState.INACTIVE);

    ActivateHmacKey.activateHmacKey(metadata.getAccessId(), PROJECT_ID);
    assertEquals(HmacKeyState.ACTIVE, storage.getHmacKey(metadata.getAccessId()).getState());
  }

  @Test
  public void testDeactivateHmacKey() {
    HmacKey hmacKey = storage.createHmacKey(ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT));

    DeactivateHmacKey.deactivateHmacKey(hmacKey.getMetadata().getAccessId(), PROJECT_ID);
    assertEquals(
        HmacKeyState.INACTIVE, storage.getHmacKey(hmacKey.getMetadata().getAccessId()).getState());
  }

  @Test
  public void testDeleteHmacKey() {
    HmacKey hmacKey = storage.createHmacKey(ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT));
    HmacKeyMetadata metadata =
        storage.updateHmacKeyState(hmacKey.getMetadata(), HmacKeyState.INACTIVE);

    DeleteHmacKey.deleteHmacKey(metadata.getAccessId(), PROJECT_ID);
    assertEquals(HmacKeyState.DELETED, storage.getHmacKey(metadata.getAccessId()).getState());
  }

  @Test
  public void testListHmacKeys() {
    // Create 2 HMAC keys
    storage.createHmacKey(
        ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT),
        Storage.CreateHmacKeyOption.projectId(PROJECT_ID));
    storage.createHmacKey(
        ServiceAccount.of(HMAC_KEY_TEST_SERVICE_ACCOUNT),
        Storage.CreateHmacKeyOption.projectId(PROJECT_ID));

    final ByteArrayOutputStream snippetOutputCapture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(snippetOutputCapture));
    ListHmacKeys.listHmacKeys(PROJECT_ID);
    String snippetOutput = snippetOutputCapture.toString();
    assertEquals(4, snippetOutput.split("\n").length); // 2 lines per key
    System.setOut(standardOut);
  }
}
