/*
 * Copyright 2022 Google LLC
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

package com.google.storage.v2;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.storage.v2.stub.HttpJsonStorageStub;
import java.io.IOException;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class StorageClientHttpJsonTest {
  private static MockHttpService mockService;
  private static StorageClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonStorageStub.getMethodDescriptors(), StorageSettings.getDefaultEndpoint());
    StorageSettings settings =
        StorageSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                StorageSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = StorageClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
  }

  @Test
  public void deleteBucketUnsupportedMethodTest() throws Exception {
    // The deleteBucket() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getBucketUnsupportedMethodTest() throws Exception {
    // The getBucket() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void createBucketUnsupportedMethodTest() throws Exception {
    // The createBucket() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void listBucketsUnsupportedMethodTest() throws Exception {
    // The listBuckets() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void lockBucketRetentionPolicyUnsupportedMethodTest() throws Exception {
    // The lockBucketRetentionPolicy() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getIamPolicyUnsupportedMethodTest() throws Exception {
    // The getIamPolicy() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void setIamPolicyUnsupportedMethodTest() throws Exception {
    // The setIamPolicy() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void testIamPermissionsUnsupportedMethodTest() throws Exception {
    // The testIamPermissions() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void updateBucketUnsupportedMethodTest() throws Exception {
    // The updateBucket() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void deleteNotificationUnsupportedMethodTest() throws Exception {
    // The deleteNotification() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getNotificationUnsupportedMethodTest() throws Exception {
    // The getNotification() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void createNotificationUnsupportedMethodTest() throws Exception {
    // The createNotification() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void listNotificationsUnsupportedMethodTest() throws Exception {
    // The listNotifications() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void composeObjectUnsupportedMethodTest() throws Exception {
    // The composeObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void deleteObjectUnsupportedMethodTest() throws Exception {
    // The deleteObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void cancelResumableWriteUnsupportedMethodTest() throws Exception {
    // The cancelResumableWrite() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getObjectUnsupportedMethodTest() throws Exception {
    // The getObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void readObjectUnsupportedMethodTest() throws Exception {
    // The readObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void updateObjectUnsupportedMethodTest() throws Exception {
    // The updateObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void writeObjectUnsupportedMethodTest() throws Exception {
    // The writeObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void listObjectsUnsupportedMethodTest() throws Exception {
    // The listObjects() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void rewriteObjectUnsupportedMethodTest() throws Exception {
    // The rewriteObject() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void startResumableWriteUnsupportedMethodTest() throws Exception {
    // The startResumableWrite() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void queryWriteStatusUnsupportedMethodTest() throws Exception {
    // The queryWriteStatus() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getServiceAccountUnsupportedMethodTest() throws Exception {
    // The getServiceAccount() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void createHmacKeyUnsupportedMethodTest() throws Exception {
    // The createHmacKey() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void deleteHmacKeyUnsupportedMethodTest() throws Exception {
    // The deleteHmacKey() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void getHmacKeyUnsupportedMethodTest() throws Exception {
    // The getHmacKey() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void listHmacKeysUnsupportedMethodTest() throws Exception {
    // The listHmacKeys() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }

  @Test
  public void updateHmacKeyUnsupportedMethodTest() throws Exception {
    // The updateHmacKey() method is not supported in REST transport.
    //This empty test is generated for technical reasons.
  }
}
