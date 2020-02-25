/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.google.storage.v1;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.StatusCode;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.storage.v1.Bucket;
import com.google.storage.v1.BucketAccessControl;
import com.google.storage.v1.Channel;
import com.google.storage.v1.ComposeObjectRequest;
import com.google.storage.v1.CopyObjectRequest;
import com.google.storage.v1.CreateHmacKeyRequest;
import com.google.storage.v1.CreateHmacKeyResponse;
import com.google.storage.v1.DeleteBucketAccessControlRequest;
import com.google.storage.v1.DeleteBucketRequest;
import com.google.storage.v1.DeleteDefaultObjectAccessControlRequest;
import com.google.storage.v1.DeleteHmacKeyRequest;
import com.google.storage.v1.DeleteNotificationRequest;
import com.google.storage.v1.DeleteObjectAccessControlRequest;
import com.google.storage.v1.DeleteObjectRequest;
import com.google.storage.v1.GetBucketAccessControlRequest;
import com.google.storage.v1.GetBucketRequest;
import com.google.storage.v1.GetDefaultObjectAccessControlRequest;
import com.google.storage.v1.GetHmacKeyRequest;
import com.google.storage.v1.GetIamPolicyRequest;
import com.google.storage.v1.GetNotificationRequest;
import com.google.storage.v1.GetObjectAccessControlRequest;
import com.google.storage.v1.GetObjectMediaRequest;
import com.google.storage.v1.GetObjectMediaResponse;
import com.google.storage.v1.GetObjectRequest;
import com.google.storage.v1.GetProjectServiceAccountRequest;
import com.google.storage.v1.HmacKeyMetadata;
import com.google.storage.v1.InsertBucketAccessControlRequest;
import com.google.storage.v1.InsertBucketRequest;
import com.google.storage.v1.InsertDefaultObjectAccessControlRequest;
import com.google.storage.v1.InsertNotificationRequest;
import com.google.storage.v1.InsertObjectAccessControlRequest;
import com.google.storage.v1.ListBucketAccessControlsRequest;
import com.google.storage.v1.ListBucketAccessControlsResponse;
import com.google.storage.v1.ListBucketsRequest;
import com.google.storage.v1.ListBucketsResponse;
import com.google.storage.v1.ListChannelsRequest;
import com.google.storage.v1.ListChannelsResponse;
import com.google.storage.v1.ListDefaultObjectAccessControlsRequest;
import com.google.storage.v1.ListHmacKeysRequest;
import com.google.storage.v1.ListHmacKeysResponse;
import com.google.storage.v1.ListNotificationsRequest;
import com.google.storage.v1.ListNotificationsResponse;
import com.google.storage.v1.ListObjectAccessControlsRequest;
import com.google.storage.v1.ListObjectAccessControlsResponse;
import com.google.storage.v1.ListObjectsRequest;
import com.google.storage.v1.ListObjectsResponse;
import com.google.storage.v1.LockRetentionPolicyRequest;
import com.google.storage.v1.Notification;
import com.google.storage.v1.ObjectAccessControl;
import com.google.storage.v1.PatchBucketAccessControlRequest;
import com.google.storage.v1.PatchBucketRequest;
import com.google.storage.v1.PatchDefaultObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectRequest;
import com.google.storage.v1.QueryWriteStatusRequest;
import com.google.storage.v1.QueryWriteStatusResponse;
import com.google.storage.v1.RewriteObjectRequest;
import com.google.storage.v1.RewriteResponse;
import com.google.storage.v1.ServiceAccount;
import com.google.storage.v1.SetIamPolicyRequest;
import com.google.storage.v1.StartResumableWriteRequest;
import com.google.storage.v1.StartResumableWriteResponse;
import com.google.storage.v1.StopChannelRequest;
import com.google.storage.v1.TestIamPermissionsRequest;
import com.google.storage.v1.UpdateBucketAccessControlRequest;
import com.google.storage.v1.UpdateBucketRequest;
import com.google.storage.v1.UpdateDefaultObjectAccessControlRequest;
import com.google.storage.v1.UpdateHmacKeyRequest;
import com.google.storage.v1.UpdateObjectAccessControlRequest;
import com.google.storage.v1.UpdateObjectRequest;
import com.google.storage.v1.WatchAllObjectsRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@javax.annotation.Generated("by GAPIC")
public class StorageClientTest {
  private static MockStorage mockStorage;
  private static MockServiceHelper serviceHelper;
  private StorageClient client;
  private LocalChannelProvider channelProvider;

  @BeforeClass
  public static void startStaticServer() {
    mockStorage = new MockStorage();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockStorage));
    serviceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    serviceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    serviceHelper.reset();
    channelProvider = serviceHelper.createChannelProvider();
    StorageSettings settings =
        StorageSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = StorageClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  @SuppressWarnings("all")
  public void deleteBucketAccessControlTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    DeleteBucketAccessControlRequest request =
        DeleteBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

    client.deleteBucketAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteBucketAccessControlRequest actualRequest =
        (DeleteBucketAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      DeleteBucketAccessControlRequest request =
          DeleteBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

      client.deleteBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    GetBucketAccessControlRequest request =
        GetBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

    BucketAccessControl actualResponse = client.getBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetBucketAccessControlRequest actualRequest =
        (GetBucketAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      GetBucketAccessControlRequest request =
          GetBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

      client.getBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void insertBucketAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String entity = "entity-1298275357";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setEntity(entity)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    InsertBucketAccessControlRequest request =
        InsertBucketAccessControlRequest.newBuilder().setBucket(bucket).build();

    BucketAccessControl actualResponse = client.insertBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertBucketAccessControlRequest actualRequest =
        (InsertBucketAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void insertBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      InsertBucketAccessControlRequest request =
          InsertBucketAccessControlRequest.newBuilder().setBucket(bucket).build();

      client.insertBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listBucketAccessControlsTest() {
    ListBucketAccessControlsResponse expectedResponse =
        ListBucketAccessControlsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    ListBucketAccessControlsRequest request =
        ListBucketAccessControlsRequest.newBuilder().setBucket(bucket).build();

    ListBucketAccessControlsResponse actualResponse = client.listBucketAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListBucketAccessControlsRequest actualRequest =
        (ListBucketAccessControlsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listBucketAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      ListBucketAccessControlsRequest request =
          ListBucketAccessControlsRequest.newBuilder().setBucket(bucket).build();

      client.listBucketAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateBucketAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    UpdateBucketAccessControlRequest request =
        UpdateBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

    BucketAccessControl actualResponse = client.updateBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateBucketAccessControlRequest actualRequest =
        (UpdateBucketAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      UpdateBucketAccessControlRequest request =
          UpdateBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

      client.updateBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void patchBucketAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    PatchBucketAccessControlRequest request =
        PatchBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

    BucketAccessControl actualResponse = client.patchBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchBucketAccessControlRequest actualRequest =
        (PatchBucketAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void patchBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      PatchBucketAccessControlRequest request =
          PatchBucketAccessControlRequest.newBuilder().setBucket(bucket).setEntity(entity).build();

      client.patchBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteBucketTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    DeleteBucketRequest request = DeleteBucketRequest.newBuilder().setBucket(bucket).build();

    client.deleteBucket(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteBucketRequest actualRequest = (DeleteBucketRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      DeleteBucketRequest request = DeleteBucketRequest.newBuilder().setBucket(bucket).build();

      client.deleteBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketTest() {
    String id = "id3355";
    String name = "name3373707";
    long projectNumber = 828084015L;
    long metageneration = 1048558813L;
    String location = "location1901043637";
    String storageClass = "storageClass2035762868";
    String etag = "etag3123477";
    boolean defaultEventBasedHold = true;
    String locationType = "locationType-1796591228";
    Bucket expectedResponse =
        Bucket.newBuilder()
            .setId(id)
            .setName(name)
            .setProjectNumber(projectNumber)
            .setMetageneration(metageneration)
            .setLocation(location)
            .setStorageClass(storageClass)
            .setEtag(etag)
            .setDefaultEventBasedHold(defaultEventBasedHold)
            .setLocationType(locationType)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    GetBucketRequest request = GetBucketRequest.newBuilder().setBucket(bucket).build();

    Bucket actualResponse = client.getBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetBucketRequest actualRequest = (GetBucketRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      GetBucketRequest request = GetBucketRequest.newBuilder().setBucket(bucket).build();

      client.getBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void insertBucketTest() {
    String id = "id3355";
    String name = "name3373707";
    long projectNumber = 828084015L;
    long metageneration = 1048558813L;
    String location = "location1901043637";
    String storageClass = "storageClass2035762868";
    String etag = "etag3123477";
    boolean defaultEventBasedHold = true;
    String locationType = "locationType-1796591228";
    Bucket expectedResponse =
        Bucket.newBuilder()
            .setId(id)
            .setName(name)
            .setProjectNumber(projectNumber)
            .setMetageneration(metageneration)
            .setLocation(location)
            .setStorageClass(storageClass)
            .setEtag(etag)
            .setDefaultEventBasedHold(defaultEventBasedHold)
            .setLocationType(locationType)
            .build();
    mockStorage.addResponse(expectedResponse);

    String project = "project-309310695";
    InsertBucketRequest request = InsertBucketRequest.newBuilder().setProject(project).build();

    Bucket actualResponse = client.insertBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertBucketRequest actualRequest = (InsertBucketRequest) actualRequests.get(0);

    Assert.assertEquals(project, actualRequest.getProject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void insertBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String project = "project-309310695";
      InsertBucketRequest request = InsertBucketRequest.newBuilder().setProject(project).build();

      client.insertBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listChannelsTest() {
    ListChannelsResponse expectedResponse = ListChannelsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    ListChannelsRequest request = ListChannelsRequest.newBuilder().setBucket(bucket).build();

    ListChannelsResponse actualResponse = client.listChannels(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListChannelsRequest actualRequest = (ListChannelsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listChannelsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      ListChannelsRequest request = ListChannelsRequest.newBuilder().setBucket(bucket).build();

      client.listChannels(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listBucketsTest() {
    String nextPageToken = "nextPageToken-1530815211";
    ListBucketsResponse expectedResponse =
        ListBucketsResponse.newBuilder().setNextPageToken(nextPageToken).build();
    mockStorage.addResponse(expectedResponse);

    String project = "project-309310695";
    ListBucketsRequest request = ListBucketsRequest.newBuilder().setProject(project).build();

    ListBucketsResponse actualResponse = client.listBuckets(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListBucketsRequest actualRequest = (ListBucketsRequest) actualRequests.get(0);

    Assert.assertEquals(project, actualRequest.getProject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listBucketsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String project = "project-309310695";
      ListBucketsRequest request = ListBucketsRequest.newBuilder().setProject(project).build();

      client.listBuckets(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void lockBucketRetentionPolicyTest() {
    String id = "id3355";
    String name = "name3373707";
    long projectNumber = 828084015L;
    long metageneration = 1048558813L;
    String location = "location1901043637";
    String storageClass = "storageClass2035762868";
    String etag = "etag3123477";
    boolean defaultEventBasedHold = true;
    String locationType = "locationType-1796591228";
    Bucket expectedResponse =
        Bucket.newBuilder()
            .setId(id)
            .setName(name)
            .setProjectNumber(projectNumber)
            .setMetageneration(metageneration)
            .setLocation(location)
            .setStorageClass(storageClass)
            .setEtag(etag)
            .setDefaultEventBasedHold(defaultEventBasedHold)
            .setLocationType(locationType)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    LockRetentionPolicyRequest request =
        LockRetentionPolicyRequest.newBuilder().setBucket(bucket).build();

    Bucket actualResponse = client.lockBucketRetentionPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    LockRetentionPolicyRequest actualRequest = (LockRetentionPolicyRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void lockBucketRetentionPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      LockRetentionPolicyRequest request =
          LockRetentionPolicyRequest.newBuilder().setBucket(bucket).build();

      client.lockBucketRetentionPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketIamPolicyTest() {
    int version = 351608024;
    ByteString etag = ByteString.copyFromUtf8("etag3123477");
    Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
    mockStorage.addResponse(expectedResponse);

    GetIamPolicyRequest request = GetIamPolicyRequest.newBuilder().build();

    Policy actualResponse = client.getBucketIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetIamPolicyRequest actualRequest = (GetIamPolicyRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getBucketIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetIamPolicyRequest request = GetIamPolicyRequest.newBuilder().build();

      client.getBucketIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void setBucketIamPolicyTest() {
    int version = 351608024;
    ByteString etag = ByteString.copyFromUtf8("etag3123477");
    Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
    mockStorage.addResponse(expectedResponse);

    SetIamPolicyRequest request = SetIamPolicyRequest.newBuilder().build();

    Policy actualResponse = client.setBucketIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    SetIamPolicyRequest actualRequest = (SetIamPolicyRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void setBucketIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      SetIamPolicyRequest request = SetIamPolicyRequest.newBuilder().build();

      client.setBucketIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void testBucketIamPermissionsTest() {
    TestIamPermissionsResponse expectedResponse = TestIamPermissionsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    TestIamPermissionsRequest request = TestIamPermissionsRequest.newBuilder().build();

    TestIamPermissionsResponse actualResponse = client.testBucketIamPermissions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    TestIamPermissionsRequest actualRequest = (TestIamPermissionsRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void testBucketIamPermissionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      TestIamPermissionsRequest request = TestIamPermissionsRequest.newBuilder().build();

      client.testBucketIamPermissions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void patchBucketTest() {
    String id = "id3355";
    String name = "name3373707";
    long projectNumber = 828084015L;
    long metageneration = 1048558813L;
    String location = "location1901043637";
    String storageClass = "storageClass2035762868";
    String etag = "etag3123477";
    boolean defaultEventBasedHold = true;
    String locationType = "locationType-1796591228";
    Bucket expectedResponse =
        Bucket.newBuilder()
            .setId(id)
            .setName(name)
            .setProjectNumber(projectNumber)
            .setMetageneration(metageneration)
            .setLocation(location)
            .setStorageClass(storageClass)
            .setEtag(etag)
            .setDefaultEventBasedHold(defaultEventBasedHold)
            .setLocationType(locationType)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    PatchBucketRequest request = PatchBucketRequest.newBuilder().setBucket(bucket).build();

    Bucket actualResponse = client.patchBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchBucketRequest actualRequest = (PatchBucketRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void patchBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      PatchBucketRequest request = PatchBucketRequest.newBuilder().setBucket(bucket).build();

      client.patchBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateBucketTest() {
    String id = "id3355";
    String name = "name3373707";
    long projectNumber = 828084015L;
    long metageneration = 1048558813L;
    String location = "location1901043637";
    String storageClass = "storageClass2035762868";
    String etag = "etag3123477";
    boolean defaultEventBasedHold = true;
    String locationType = "locationType-1796591228";
    Bucket expectedResponse =
        Bucket.newBuilder()
            .setId(id)
            .setName(name)
            .setProjectNumber(projectNumber)
            .setMetageneration(metageneration)
            .setLocation(location)
            .setStorageClass(storageClass)
            .setEtag(etag)
            .setDefaultEventBasedHold(defaultEventBasedHold)
            .setLocationType(locationType)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    UpdateBucketRequest request = UpdateBucketRequest.newBuilder().setBucket(bucket).build();

    Bucket actualResponse = client.updateBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateBucketRequest actualRequest = (UpdateBucketRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      UpdateBucketRequest request = UpdateBucketRequest.newBuilder().setBucket(bucket).build();

      client.updateBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void stopChannelTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    StopChannelRequest request = StopChannelRequest.newBuilder().build();

    client.stopChannel(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    StopChannelRequest actualRequest = (StopChannelRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void stopChannelExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      StopChannelRequest request = StopChannelRequest.newBuilder().build();

      client.stopChannel(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteDefaultObjectAccessControlTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    DeleteDefaultObjectAccessControlRequest request =
        DeleteDefaultObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .build();

    client.deleteDefaultObjectAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteDefaultObjectAccessControlRequest actualRequest =
        (DeleteDefaultObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      DeleteDefaultObjectAccessControlRequest request =
          DeleteDefaultObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .build();

      client.deleteDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getDefaultObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object = "object-1023368385";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    GetDefaultObjectAccessControlRequest request =
        GetDefaultObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .build();

    ObjectAccessControl actualResponse = client.getDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetDefaultObjectAccessControlRequest actualRequest =
        (GetDefaultObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      GetDefaultObjectAccessControlRequest request =
          GetDefaultObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .build();

      client.getDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void insertDefaultObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object = "object-1023368385";
    long generation = 305703192L;
    String entity = "entity-1298275357";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object)
            .setGeneration(generation)
            .setEntity(entity)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    InsertDefaultObjectAccessControlRequest request =
        InsertDefaultObjectAccessControlRequest.newBuilder().setBucket(bucket).build();

    ObjectAccessControl actualResponse = client.insertDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertDefaultObjectAccessControlRequest actualRequest =
        (InsertDefaultObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void insertDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      InsertDefaultObjectAccessControlRequest request =
          InsertDefaultObjectAccessControlRequest.newBuilder().setBucket(bucket).build();

      client.insertDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listDefaultObjectAccessControlsTest() {
    ListObjectAccessControlsResponse expectedResponse =
        ListObjectAccessControlsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    ListDefaultObjectAccessControlsRequest request =
        ListDefaultObjectAccessControlsRequest.newBuilder().setBucket(bucket).build();

    ListObjectAccessControlsResponse actualResponse =
        client.listDefaultObjectAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListDefaultObjectAccessControlsRequest actualRequest =
        (ListDefaultObjectAccessControlsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listDefaultObjectAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      ListDefaultObjectAccessControlsRequest request =
          ListDefaultObjectAccessControlsRequest.newBuilder().setBucket(bucket).build();

      client.listDefaultObjectAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void patchDefaultObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object = "object-1023368385";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    PatchDefaultObjectAccessControlRequest request =
        PatchDefaultObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .build();

    ObjectAccessControl actualResponse = client.patchDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchDefaultObjectAccessControlRequest actualRequest =
        (PatchDefaultObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void patchDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      PatchDefaultObjectAccessControlRequest request =
          PatchDefaultObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .build();

      client.patchDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateDefaultObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object = "object-1023368385";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    UpdateDefaultObjectAccessControlRequest request =
        UpdateDefaultObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .build();

    ObjectAccessControl actualResponse = client.updateDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateDefaultObjectAccessControlRequest actualRequest =
        (UpdateDefaultObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      UpdateDefaultObjectAccessControlRequest request =
          UpdateDefaultObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .build();

      client.updateDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteNotificationTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String notification = "notification595233003";
    DeleteNotificationRequest request =
        DeleteNotificationRequest.newBuilder()
            .setBucket(bucket)
            .setNotification(notification)
            .build();

    client.deleteNotification(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteNotificationRequest actualRequest = (DeleteNotificationRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(notification, actualRequest.getNotification());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String notification = "notification595233003";
      DeleteNotificationRequest request =
          DeleteNotificationRequest.newBuilder()
              .setBucket(bucket)
              .setNotification(notification)
              .build();

      client.deleteNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getNotificationTest() {
    String topic = "topic110546223";
    String etag = "etag3123477";
    String objectNamePrefix = "objectNamePrefix1265003974";
    String payloadFormat = "payloadFormat-1481910328";
    String id = "id3355";
    Notification expectedResponse =
        Notification.newBuilder()
            .setTopic(topic)
            .setEtag(etag)
            .setObjectNamePrefix(objectNamePrefix)
            .setPayloadFormat(payloadFormat)
            .setId(id)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String notification = "notification595233003";
    GetNotificationRequest request =
        GetNotificationRequest.newBuilder().setBucket(bucket).setNotification(notification).build();

    Notification actualResponse = client.getNotification(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetNotificationRequest actualRequest = (GetNotificationRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(notification, actualRequest.getNotification());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String notification = "notification595233003";
      GetNotificationRequest request =
          GetNotificationRequest.newBuilder()
              .setBucket(bucket)
              .setNotification(notification)
              .build();

      client.getNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void insertNotificationTest() {
    String topic = "topic110546223";
    String etag = "etag3123477";
    String objectNamePrefix = "objectNamePrefix1265003974";
    String payloadFormat = "payloadFormat-1481910328";
    String id = "id3355";
    Notification expectedResponse =
        Notification.newBuilder()
            .setTopic(topic)
            .setEtag(etag)
            .setObjectNamePrefix(objectNamePrefix)
            .setPayloadFormat(payloadFormat)
            .setId(id)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    InsertNotificationRequest request =
        InsertNotificationRequest.newBuilder().setBucket(bucket).build();

    Notification actualResponse = client.insertNotification(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertNotificationRequest actualRequest = (InsertNotificationRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void insertNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      InsertNotificationRequest request =
          InsertNotificationRequest.newBuilder().setBucket(bucket).build();

      client.insertNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listNotificationsTest() {
    ListNotificationsResponse expectedResponse = ListNotificationsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    ListNotificationsRequest request =
        ListNotificationsRequest.newBuilder().setBucket(bucket).build();

    ListNotificationsResponse actualResponse = client.listNotifications(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListNotificationsRequest actualRequest = (ListNotificationsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listNotificationsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      ListNotificationsRequest request =
          ListNotificationsRequest.newBuilder().setBucket(bucket).build();

      client.listNotifications(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteObjectAccessControlTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    String object = "object-1023368385";
    DeleteObjectAccessControlRequest request =
        DeleteObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .setObject(object)
            .build();

    client.deleteObjectAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteObjectAccessControlRequest actualRequest =
        (DeleteObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      String object = "object-1023368385";
      DeleteObjectAccessControlRequest request =
          DeleteObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .setObject(object)
              .build();

      client.deleteObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object2 = "object290495794";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object2)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    String object = "object-1023368385";
    GetObjectAccessControlRequest request =
        GetObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .setObject(object)
            .build();

    ObjectAccessControl actualResponse = client.getObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetObjectAccessControlRequest actualRequest =
        (GetObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      String object = "object-1023368385";
      GetObjectAccessControlRequest request =
          GetObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .setObject(object)
              .build();

      client.getObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void insertObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object2 = "object290495794";
    long generation = 305703192L;
    String entity = "entity-1298275357";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object2)
            .setGeneration(generation)
            .setEntity(entity)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    InsertObjectAccessControlRequest request =
        InsertObjectAccessControlRequest.newBuilder().setBucket(bucket).setObject(object).build();

    ObjectAccessControl actualResponse = client.insertObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertObjectAccessControlRequest actualRequest =
        (InsertObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void insertObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      InsertObjectAccessControlRequest request =
          InsertObjectAccessControlRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.insertObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listObjectAccessControlsTest() {
    ListObjectAccessControlsResponse expectedResponse =
        ListObjectAccessControlsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    ListObjectAccessControlsRequest request =
        ListObjectAccessControlsRequest.newBuilder().setBucket(bucket).setObject(object).build();

    ListObjectAccessControlsResponse actualResponse = client.listObjectAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListObjectAccessControlsRequest actualRequest =
        (ListObjectAccessControlsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listObjectAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      ListObjectAccessControlsRequest request =
          ListObjectAccessControlsRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.listObjectAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void patchObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object2 = "object290495794";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object2)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    String object = "object-1023368385";
    PatchObjectAccessControlRequest request =
        PatchObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .setObject(object)
            .build();

    ObjectAccessControl actualResponse = client.patchObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchObjectAccessControlRequest actualRequest =
        (PatchObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void patchObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      String object = "object-1023368385";
      PatchObjectAccessControlRequest request =
          PatchObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .setObject(object)
              .build();

      client.patchObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateObjectAccessControlTest() {
    String role = "role3506294";
    String etag = "etag3123477";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    String object2 = "object290495794";
    long generation = 305703192L;
    String entity2 = "entity2-2102099242";
    String entityId = "entityId-740565257";
    String email = "email96619420";
    String domain = "domain-1326197564";
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole(role)
            .setEtag(etag)
            .setId(id)
            .setBucket(bucket2)
            .setObject(object2)
            .setGeneration(generation)
            .setEntity(entity2)
            .setEntityId(entityId)
            .setEmail(email)
            .setDomain(domain)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String entity = "entity-1298275357";
    String object = "object-1023368385";
    UpdateObjectAccessControlRequest request =
        UpdateObjectAccessControlRequest.newBuilder()
            .setBucket(bucket)
            .setEntity(entity)
            .setObject(object)
            .build();

    ObjectAccessControl actualResponse = client.updateObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateObjectAccessControlRequest actualRequest =
        (UpdateObjectAccessControlRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(entity, actualRequest.getEntity());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String entity = "entity-1298275357";
      String object = "object-1023368385";
      UpdateObjectAccessControlRequest request =
          UpdateObjectAccessControlRequest.newBuilder()
              .setBucket(bucket)
              .setEntity(entity)
              .setObject(object)
              .build();

      client.updateObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void composeObjectTest() {
    String contentEncoding = "contentEncoding1916674649";
    String contentDisposition = "contentDisposition891901169";
    String cacheControl = "cacheControl1032395168";
    String contentLanguage = "contentLanguage-1408137122";
    long metageneration = 1048558813L;
    String contentType = "contentType831846208";
    long size = 3530753L;
    int componentCount = 485073075;
    String md5Hash = "md5Hash1152095023";
    String etag = "etag3123477";
    String storageClass = "storageClass2035762868";
    String kmsKeyName = "kmsKeyName2094986649";
    boolean temporaryHold = false;
    String name = "name3373707";
    String id = "id3355";
    String bucket = "bucket-1378203158";
    long generation = 305703192L;
    com.google.storage.v1.Object expectedResponse =
        com.google.storage.v1.Object.newBuilder()
            .setContentEncoding(contentEncoding)
            .setContentDisposition(contentDisposition)
            .setCacheControl(cacheControl)
            .setContentLanguage(contentLanguage)
            .setMetageneration(metageneration)
            .setContentType(contentType)
            .setSize(size)
            .setComponentCount(componentCount)
            .setMd5Hash(md5Hash)
            .setEtag(etag)
            .setStorageClass(storageClass)
            .setKmsKeyName(kmsKeyName)
            .setTemporaryHold(temporaryHold)
            .setName(name)
            .setId(id)
            .setBucket(bucket)
            .setGeneration(generation)
            .build();
    mockStorage.addResponse(expectedResponse);

    String destinationBucket = "destinationBucket-1744832709";
    String destinationObject = "destinationObject-1389997936";
    ComposeObjectRequest request =
        ComposeObjectRequest.newBuilder()
            .setDestinationBucket(destinationBucket)
            .setDestinationObject(destinationObject)
            .build();

    com.google.storage.v1.Object actualResponse = client.composeObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComposeObjectRequest actualRequest = (ComposeObjectRequest) actualRequests.get(0);

    Assert.assertEquals(destinationBucket, actualRequest.getDestinationBucket());
    Assert.assertEquals(destinationObject, actualRequest.getDestinationObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void composeObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String destinationBucket = "destinationBucket-1744832709";
      String destinationObject = "destinationObject-1389997936";
      ComposeObjectRequest request =
          ComposeObjectRequest.newBuilder()
              .setDestinationBucket(destinationBucket)
              .setDestinationObject(destinationObject)
              .build();

      client.composeObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void copyObjectTest() {
    String contentEncoding = "contentEncoding1916674649";
    String contentDisposition = "contentDisposition891901169";
    String cacheControl = "cacheControl1032395168";
    String contentLanguage = "contentLanguage-1408137122";
    long metageneration = 1048558813L;
    String contentType = "contentType831846208";
    long size = 3530753L;
    int componentCount = 485073075;
    String md5Hash = "md5Hash1152095023";
    String etag = "etag3123477";
    String storageClass = "storageClass2035762868";
    String kmsKeyName = "kmsKeyName2094986649";
    boolean temporaryHold = false;
    String name = "name3373707";
    String id = "id3355";
    String bucket = "bucket-1378203158";
    long generation = 305703192L;
    com.google.storage.v1.Object expectedResponse =
        com.google.storage.v1.Object.newBuilder()
            .setContentEncoding(contentEncoding)
            .setContentDisposition(contentDisposition)
            .setCacheControl(cacheControl)
            .setContentLanguage(contentLanguage)
            .setMetageneration(metageneration)
            .setContentType(contentType)
            .setSize(size)
            .setComponentCount(componentCount)
            .setMd5Hash(md5Hash)
            .setEtag(etag)
            .setStorageClass(storageClass)
            .setKmsKeyName(kmsKeyName)
            .setTemporaryHold(temporaryHold)
            .setName(name)
            .setId(id)
            .setBucket(bucket)
            .setGeneration(generation)
            .build();
    mockStorage.addResponse(expectedResponse);

    String destinationBucket = "destinationBucket-1744832709";
    String destinationObject = "destinationObject-1389997936";
    String sourceBucket = "sourceBucket-239822194";
    String sourceObject = "sourceObject115012579";
    CopyObjectRequest request =
        CopyObjectRequest.newBuilder()
            .setDestinationBucket(destinationBucket)
            .setDestinationObject(destinationObject)
            .setSourceBucket(sourceBucket)
            .setSourceObject(sourceObject)
            .build();

    com.google.storage.v1.Object actualResponse = client.copyObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CopyObjectRequest actualRequest = (CopyObjectRequest) actualRequests.get(0);

    Assert.assertEquals(destinationBucket, actualRequest.getDestinationBucket());
    Assert.assertEquals(destinationObject, actualRequest.getDestinationObject());
    Assert.assertEquals(sourceBucket, actualRequest.getSourceBucket());
    Assert.assertEquals(sourceObject, actualRequest.getSourceObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void copyObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String destinationBucket = "destinationBucket-1744832709";
      String destinationObject = "destinationObject-1389997936";
      String sourceBucket = "sourceBucket-239822194";
      String sourceObject = "sourceObject115012579";
      CopyObjectRequest request =
          CopyObjectRequest.newBuilder()
              .setDestinationBucket(destinationBucket)
              .setDestinationObject(destinationObject)
              .setSourceBucket(sourceBucket)
              .setSourceObject(sourceObject)
              .build();

      client.copyObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteObjectTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    DeleteObjectRequest request =
        DeleteObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

    client.deleteObject(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteObjectRequest actualRequest = (DeleteObjectRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      DeleteObjectRequest request =
          DeleteObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.deleteObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectTest() {
    String contentEncoding = "contentEncoding1916674649";
    String contentDisposition = "contentDisposition891901169";
    String cacheControl = "cacheControl1032395168";
    String contentLanguage = "contentLanguage-1408137122";
    long metageneration = 1048558813L;
    String contentType = "contentType831846208";
    long size = 3530753L;
    int componentCount = 485073075;
    String md5Hash = "md5Hash1152095023";
    String etag = "etag3123477";
    String storageClass = "storageClass2035762868";
    String kmsKeyName = "kmsKeyName2094986649";
    boolean temporaryHold = false;
    String name = "name3373707";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    long generation = 305703192L;
    com.google.storage.v1.Object expectedResponse =
        com.google.storage.v1.Object.newBuilder()
            .setContentEncoding(contentEncoding)
            .setContentDisposition(contentDisposition)
            .setCacheControl(cacheControl)
            .setContentLanguage(contentLanguage)
            .setMetageneration(metageneration)
            .setContentType(contentType)
            .setSize(size)
            .setComponentCount(componentCount)
            .setMd5Hash(md5Hash)
            .setEtag(etag)
            .setStorageClass(storageClass)
            .setKmsKeyName(kmsKeyName)
            .setTemporaryHold(temporaryHold)
            .setName(name)
            .setId(id)
            .setBucket(bucket2)
            .setGeneration(generation)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    GetObjectRequest request =
        GetObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

    com.google.storage.v1.Object actualResponse = client.getObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetObjectRequest actualRequest = (GetObjectRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      GetObjectRequest request =
          GetObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.getObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectMediaTest() throws Exception {
    GetObjectMediaResponse expectedResponse = GetObjectMediaResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);
    GetObjectMediaRequest request = GetObjectMediaRequest.newBuilder().build();

    MockStreamObserver<GetObjectMediaResponse> responseObserver = new MockStreamObserver<>();

    ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse> callable =
        client.getObjectMediaCallable();
    callable.serverStreamingCall(request, responseObserver);

    List<GetObjectMediaResponse> actualResponses = responseObserver.future().get();
    Assert.assertEquals(1, actualResponses.size());
    Assert.assertEquals(expectedResponse, actualResponses.get(0));
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectMediaExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);
    GetObjectMediaRequest request = GetObjectMediaRequest.newBuilder().build();

    MockStreamObserver<GetObjectMediaResponse> responseObserver = new MockStreamObserver<>();

    ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse> callable =
        client.getObjectMediaCallable();
    callable.serverStreamingCall(request, responseObserver);

    try {
      List<GetObjectMediaResponse> actualResponses = responseObserver.future().get();
      Assert.fail("No exception thrown");
    } catch (ExecutionException e) {
      Assert.assertTrue(e.getCause() instanceof InvalidArgumentException);
      InvalidArgumentException apiException = (InvalidArgumentException) e.getCause();
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listObjectsTest() {
    String nextPageToken = "nextPageToken-1530815211";
    ListObjectsResponse expectedResponse =
        ListObjectsResponse.newBuilder().setNextPageToken(nextPageToken).build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    ListObjectsRequest request = ListObjectsRequest.newBuilder().setBucket(bucket).build();

    ListObjectsResponse actualResponse = client.listObjects(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListObjectsRequest actualRequest = (ListObjectsRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listObjectsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      ListObjectsRequest request = ListObjectsRequest.newBuilder().setBucket(bucket).build();

      client.listObjects(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void rewriteObjectTest() {
    long totalBytesRewritten = 1109205579L;
    long objectSize = 1277221631L;
    boolean done = true;
    String rewriteToken = "rewriteToken-1475021434";
    RewriteResponse expectedResponse =
        RewriteResponse.newBuilder()
            .setTotalBytesRewritten(totalBytesRewritten)
            .setObjectSize(objectSize)
            .setDone(done)
            .setRewriteToken(rewriteToken)
            .build();
    mockStorage.addResponse(expectedResponse);

    String destinationBucket = "destinationBucket-1744832709";
    String destinationObject = "destinationObject-1389997936";
    String sourceBucket = "sourceBucket-239822194";
    String sourceObject = "sourceObject115012579";
    RewriteObjectRequest request =
        RewriteObjectRequest.newBuilder()
            .setDestinationBucket(destinationBucket)
            .setDestinationObject(destinationObject)
            .setSourceBucket(sourceBucket)
            .setSourceObject(sourceObject)
            .build();

    RewriteResponse actualResponse = client.rewriteObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    RewriteObjectRequest actualRequest = (RewriteObjectRequest) actualRequests.get(0);

    Assert.assertEquals(destinationBucket, actualRequest.getDestinationBucket());
    Assert.assertEquals(destinationObject, actualRequest.getDestinationObject());
    Assert.assertEquals(sourceBucket, actualRequest.getSourceBucket());
    Assert.assertEquals(sourceObject, actualRequest.getSourceObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void rewriteObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String destinationBucket = "destinationBucket-1744832709";
      String destinationObject = "destinationObject-1389997936";
      String sourceBucket = "sourceBucket-239822194";
      String sourceObject = "sourceObject115012579";
      RewriteObjectRequest request =
          RewriteObjectRequest.newBuilder()
              .setDestinationBucket(destinationBucket)
              .setDestinationObject(destinationObject)
              .setSourceBucket(sourceBucket)
              .setSourceObject(sourceObject)
              .build();

      client.rewriteObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void startResumableWriteTest() {
    String uploadId = "uploadId1239095321";
    StartResumableWriteResponse expectedResponse =
        StartResumableWriteResponse.newBuilder().setUploadId(uploadId).build();
    mockStorage.addResponse(expectedResponse);

    StartResumableWriteRequest request = StartResumableWriteRequest.newBuilder().build();

    StartResumableWriteResponse actualResponse = client.startResumableWrite(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    StartResumableWriteRequest actualRequest = (StartResumableWriteRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void startResumableWriteExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      StartResumableWriteRequest request = StartResumableWriteRequest.newBuilder().build();

      client.startResumableWrite(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void queryWriteStatusTest() {
    long committedSize = 1907158756L;
    boolean complete = false;
    QueryWriteStatusResponse expectedResponse =
        QueryWriteStatusResponse.newBuilder()
            .setCommittedSize(committedSize)
            .setComplete(complete)
            .build();
    mockStorage.addResponse(expectedResponse);

    String uploadId = "uploadId1239095321";
    QueryWriteStatusRequest request =
        QueryWriteStatusRequest.newBuilder().setUploadId(uploadId).build();

    QueryWriteStatusResponse actualResponse = client.queryWriteStatus(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    QueryWriteStatusRequest actualRequest = (QueryWriteStatusRequest) actualRequests.get(0);

    Assert.assertEquals(uploadId, actualRequest.getUploadId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void queryWriteStatusExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String uploadId = "uploadId1239095321";
      QueryWriteStatusRequest request =
          QueryWriteStatusRequest.newBuilder().setUploadId(uploadId).build();

      client.queryWriteStatus(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void patchObjectTest() {
    String contentEncoding = "contentEncoding1916674649";
    String contentDisposition = "contentDisposition891901169";
    String cacheControl = "cacheControl1032395168";
    String contentLanguage = "contentLanguage-1408137122";
    long metageneration = 1048558813L;
    String contentType = "contentType831846208";
    long size = 3530753L;
    int componentCount = 485073075;
    String md5Hash = "md5Hash1152095023";
    String etag = "etag3123477";
    String storageClass = "storageClass2035762868";
    String kmsKeyName = "kmsKeyName2094986649";
    boolean temporaryHold = false;
    String name = "name3373707";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    long generation = 305703192L;
    com.google.storage.v1.Object expectedResponse =
        com.google.storage.v1.Object.newBuilder()
            .setContentEncoding(contentEncoding)
            .setContentDisposition(contentDisposition)
            .setCacheControl(cacheControl)
            .setContentLanguage(contentLanguage)
            .setMetageneration(metageneration)
            .setContentType(contentType)
            .setSize(size)
            .setComponentCount(componentCount)
            .setMd5Hash(md5Hash)
            .setEtag(etag)
            .setStorageClass(storageClass)
            .setKmsKeyName(kmsKeyName)
            .setTemporaryHold(temporaryHold)
            .setName(name)
            .setId(id)
            .setBucket(bucket2)
            .setGeneration(generation)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    PatchObjectRequest request =
        PatchObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

    com.google.storage.v1.Object actualResponse = client.patchObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchObjectRequest actualRequest = (PatchObjectRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void patchObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      PatchObjectRequest request =
          PatchObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.patchObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateObjectTest() {
    String contentEncoding = "contentEncoding1916674649";
    String contentDisposition = "contentDisposition891901169";
    String cacheControl = "cacheControl1032395168";
    String contentLanguage = "contentLanguage-1408137122";
    long metageneration = 1048558813L;
    String contentType = "contentType831846208";
    long size = 3530753L;
    int componentCount = 485073075;
    String md5Hash = "md5Hash1152095023";
    String etag = "etag3123477";
    String storageClass = "storageClass2035762868";
    String kmsKeyName = "kmsKeyName2094986649";
    boolean temporaryHold = false;
    String name = "name3373707";
    String id = "id3355";
    String bucket2 = "bucket2-1603304675";
    long generation = 305703192L;
    com.google.storage.v1.Object expectedResponse =
        com.google.storage.v1.Object.newBuilder()
            .setContentEncoding(contentEncoding)
            .setContentDisposition(contentDisposition)
            .setCacheControl(cacheControl)
            .setContentLanguage(contentLanguage)
            .setMetageneration(metageneration)
            .setContentType(contentType)
            .setSize(size)
            .setComponentCount(componentCount)
            .setMd5Hash(md5Hash)
            .setEtag(etag)
            .setStorageClass(storageClass)
            .setKmsKeyName(kmsKeyName)
            .setTemporaryHold(temporaryHold)
            .setName(name)
            .setId(id)
            .setBucket(bucket2)
            .setGeneration(generation)
            .build();
    mockStorage.addResponse(expectedResponse);

    String bucket = "bucket-1378203158";
    String object = "object-1023368385";
    UpdateObjectRequest request =
        UpdateObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

    com.google.storage.v1.Object actualResponse = client.updateObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateObjectRequest actualRequest = (UpdateObjectRequest) actualRequests.get(0);

    Assert.assertEquals(bucket, actualRequest.getBucket());
    Assert.assertEquals(object, actualRequest.getObject());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String bucket = "bucket-1378203158";
      String object = "object-1023368385";
      UpdateObjectRequest request =
          UpdateObjectRequest.newBuilder().setBucket(bucket).setObject(object).build();

      client.updateObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectIamPolicyTest() {
    int version = 351608024;
    ByteString etag = ByteString.copyFromUtf8("etag3123477");
    Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
    mockStorage.addResponse(expectedResponse);

    GetIamPolicyRequest request = GetIamPolicyRequest.newBuilder().build();

    Policy actualResponse = client.getObjectIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetIamPolicyRequest actualRequest = (GetIamPolicyRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getObjectIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetIamPolicyRequest request = GetIamPolicyRequest.newBuilder().build();

      client.getObjectIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void setObjectIamPolicyTest() {
    int version = 351608024;
    ByteString etag = ByteString.copyFromUtf8("etag3123477");
    Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
    mockStorage.addResponse(expectedResponse);

    SetIamPolicyRequest request = SetIamPolicyRequest.newBuilder().build();

    Policy actualResponse = client.setObjectIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    SetIamPolicyRequest actualRequest = (SetIamPolicyRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void setObjectIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      SetIamPolicyRequest request = SetIamPolicyRequest.newBuilder().build();

      client.setObjectIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void testObjectIamPermissionsTest() {
    TestIamPermissionsResponse expectedResponse = TestIamPermissionsResponse.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    TestIamPermissionsRequest request = TestIamPermissionsRequest.newBuilder().build();

    TestIamPermissionsResponse actualResponse = client.testObjectIamPermissions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    TestIamPermissionsRequest actualRequest = (TestIamPermissionsRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void testObjectIamPermissionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      TestIamPermissionsRequest request = TestIamPermissionsRequest.newBuilder().build();

      client.testObjectIamPermissions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void watchAllObjectsTest() {
    String id = "id3355";
    String resourceId = "resourceId1234537196";
    String resourceUri = "resourceUri-384040517";
    String token = "token110541305";
    String type = "type3575610";
    String address = "address-1147692044";
    boolean payload = true;
    Channel expectedResponse =
        Channel.newBuilder()
            .setId(id)
            .setResourceId(resourceId)
            .setResourceUri(resourceUri)
            .setToken(token)
            .setType(type)
            .setAddress(address)
            .setPayload(payload)
            .build();
    mockStorage.addResponse(expectedResponse);

    WatchAllObjectsRequest request = WatchAllObjectsRequest.newBuilder().build();

    Channel actualResponse = client.watchAllObjects(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    WatchAllObjectsRequest actualRequest = (WatchAllObjectsRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void watchAllObjectsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      WatchAllObjectsRequest request = WatchAllObjectsRequest.newBuilder().build();

      client.watchAllObjects(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getServiceAccountTest() {
    String emailAddress = "emailAddress-769510831";
    ServiceAccount expectedResponse =
        ServiceAccount.newBuilder().setEmailAddress(emailAddress).build();
    mockStorage.addResponse(expectedResponse);

    String projectId = "projectId-1969970175";
    GetProjectServiceAccountRequest request =
        GetProjectServiceAccountRequest.newBuilder().setProjectId(projectId).build();

    ServiceAccount actualResponse = client.getServiceAccount(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetProjectServiceAccountRequest actualRequest =
        (GetProjectServiceAccountRequest) actualRequests.get(0);

    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getServiceAccountExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String projectId = "projectId-1969970175";
      GetProjectServiceAccountRequest request =
          GetProjectServiceAccountRequest.newBuilder().setProjectId(projectId).build();

      client.getServiceAccount(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void createHmacKeyTest() {
    String secret = "secret-906277200";
    CreateHmacKeyResponse expectedResponse =
        CreateHmacKeyResponse.newBuilder().setSecret(secret).build();
    mockStorage.addResponse(expectedResponse);

    String projectId = "projectId-1969970175";
    String serviceAccountEmail = "serviceAccountEmail-1300473088";
    CreateHmacKeyRequest request =
        CreateHmacKeyRequest.newBuilder()
            .setProjectId(projectId)
            .setServiceAccountEmail(serviceAccountEmail)
            .build();

    CreateHmacKeyResponse actualResponse = client.createHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateHmacKeyRequest actualRequest = (CreateHmacKeyRequest) actualRequests.get(0);

    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertEquals(serviceAccountEmail, actualRequest.getServiceAccountEmail());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void createHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String projectId = "projectId-1969970175";
      String serviceAccountEmail = "serviceAccountEmail-1300473088";
      CreateHmacKeyRequest request =
          CreateHmacKeyRequest.newBuilder()
              .setProjectId(projectId)
              .setServiceAccountEmail(serviceAccountEmail)
              .build();

      client.createHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteHmacKeyTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    String accessId = "accessId-2115038762";
    String projectId = "projectId-1969970175";
    DeleteHmacKeyRequest request =
        DeleteHmacKeyRequest.newBuilder().setAccessId(accessId).setProjectId(projectId).build();

    client.deleteHmacKey(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteHmacKeyRequest actualRequest = (DeleteHmacKeyRequest) actualRequests.get(0);

    Assert.assertEquals(accessId, actualRequest.getAccessId());
    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String accessId = "accessId-2115038762";
      String projectId = "projectId-1969970175";
      DeleteHmacKeyRequest request =
          DeleteHmacKeyRequest.newBuilder().setAccessId(accessId).setProjectId(projectId).build();

      client.deleteHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getHmacKeyTest() {
    String id = "id3355";
    String accessId2 = "accessId2-1032716279";
    String projectId2 = "projectId2939242356";
    String serviceAccountEmail = "serviceAccountEmail-1300473088";
    String state = "state109757585";
    String etag = "etag3123477";
    HmacKeyMetadata expectedResponse =
        HmacKeyMetadata.newBuilder()
            .setId(id)
            .setAccessId(accessId2)
            .setProjectId(projectId2)
            .setServiceAccountEmail(serviceAccountEmail)
            .setState(state)
            .setEtag(etag)
            .build();
    mockStorage.addResponse(expectedResponse);

    String accessId = "accessId-2115038762";
    String projectId = "projectId-1969970175";
    GetHmacKeyRequest request =
        GetHmacKeyRequest.newBuilder().setAccessId(accessId).setProjectId(projectId).build();

    HmacKeyMetadata actualResponse = client.getHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetHmacKeyRequest actualRequest = (GetHmacKeyRequest) actualRequests.get(0);

    Assert.assertEquals(accessId, actualRequest.getAccessId());
    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String accessId = "accessId-2115038762";
      String projectId = "projectId-1969970175";
      GetHmacKeyRequest request =
          GetHmacKeyRequest.newBuilder().setAccessId(accessId).setProjectId(projectId).build();

      client.getHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listHmacKeysTest() {
    String nextPageToken = "nextPageToken-1530815211";
    ListHmacKeysResponse expectedResponse =
        ListHmacKeysResponse.newBuilder().setNextPageToken(nextPageToken).build();
    mockStorage.addResponse(expectedResponse);

    String projectId = "projectId-1969970175";
    ListHmacKeysRequest request = ListHmacKeysRequest.newBuilder().setProjectId(projectId).build();

    ListHmacKeysResponse actualResponse = client.listHmacKeys(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListHmacKeysRequest actualRequest = (ListHmacKeysRequest) actualRequests.get(0);

    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listHmacKeysExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String projectId = "projectId-1969970175";
      ListHmacKeysRequest request =
          ListHmacKeysRequest.newBuilder().setProjectId(projectId).build();

      client.listHmacKeys(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateHmacKeyTest() {
    String id = "id3355";
    String accessId2 = "accessId2-1032716279";
    String projectId2 = "projectId2939242356";
    String serviceAccountEmail = "serviceAccountEmail-1300473088";
    String state = "state109757585";
    String etag = "etag3123477";
    HmacKeyMetadata expectedResponse =
        HmacKeyMetadata.newBuilder()
            .setId(id)
            .setAccessId(accessId2)
            .setProjectId(projectId2)
            .setServiceAccountEmail(serviceAccountEmail)
            .setState(state)
            .setEtag(etag)
            .build();
    mockStorage.addResponse(expectedResponse);

    String accessId = "accessId-2115038762";
    String projectId = "projectId-1969970175";
    HmacKeyMetadata metadata = HmacKeyMetadata.newBuilder().build();
    UpdateHmacKeyRequest request =
        UpdateHmacKeyRequest.newBuilder()
            .setAccessId(accessId)
            .setProjectId(projectId)
            .setMetadata(metadata)
            .build();

    HmacKeyMetadata actualResponse = client.updateHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateHmacKeyRequest actualRequest = (UpdateHmacKeyRequest) actualRequests.get(0);

    Assert.assertEquals(accessId, actualRequest.getAccessId());
    Assert.assertEquals(projectId, actualRequest.getProjectId());
    Assert.assertEquals(metadata, actualRequest.getMetadata());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      String accessId = "accessId-2115038762";
      String projectId = "projectId-1969970175";
      HmacKeyMetadata metadata = HmacKeyMetadata.newBuilder().build();
      UpdateHmacKeyRequest request =
          UpdateHmacKeyRequest.newBuilder()
              .setAccessId(accessId)
              .setProjectId(projectId)
              .setMetadata(metadata)
              .build();

      client.updateHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }
}
