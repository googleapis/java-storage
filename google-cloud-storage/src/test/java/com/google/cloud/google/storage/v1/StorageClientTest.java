/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.google.storage.v1;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.StatusCode;
import com.google.iam.v1.Binding;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.storage.v1.Bucket;
import com.google.storage.v1.BucketAccessControl;
import com.google.storage.v1.Channel;
import com.google.storage.v1.ChecksummedData;
import com.google.storage.v1.CommonObjectRequestParams;
import com.google.storage.v1.CommonRequestParams;
import com.google.storage.v1.ComposeObjectRequest;
import com.google.storage.v1.ContentRange;
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
import com.google.storage.v1.InsertObjectRequest;
import com.google.storage.v1.InsertObjectSpec;
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
import com.google.storage.v1.Object;
import com.google.storage.v1.ObjectAccessControl;
import com.google.storage.v1.ObjectChecksums;
import com.google.storage.v1.Owner;
import com.google.storage.v1.PatchBucketAccessControlRequest;
import com.google.storage.v1.PatchBucketRequest;
import com.google.storage.v1.PatchDefaultObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectRequest;
import com.google.storage.v1.ProjectTeam;
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
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class StorageClientTest {
  private static MockStorage mockStorage;
  private static MockServiceHelper mockServiceHelper;
  private StorageClient client;
  private LocalChannelProvider channelProvider;

  @BeforeClass
  public static void startStaticServer() {
    mockStorage = new MockStorage();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockStorage));
    mockServiceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    mockServiceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    mockServiceHelper.reset();
    channelProvider = mockServiceHelper.createChannelProvider();
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
  public void deleteBucketAccessControlTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteBucketAccessControlRequest request =
        DeleteBucketAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteBucketAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteBucketAccessControlRequest actualRequest =
        ((DeleteBucketAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteBucketAccessControlRequest request =
          DeleteBucketAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getBucketAccessControlTest() throws Exception {
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    GetBucketAccessControlRequest request =
        GetBucketAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    BucketAccessControl actualResponse = client.getBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetBucketAccessControlRequest actualRequest =
        ((GetBucketAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetBucketAccessControlRequest request =
          GetBucketAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void insertBucketAccessControlTest() throws Exception {
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    InsertBucketAccessControlRequest request =
        InsertBucketAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setBucketAccessControl(BucketAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    BucketAccessControl actualResponse = client.insertBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertBucketAccessControlRequest actualRequest =
        ((InsertBucketAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getBucketAccessControl(), actualRequest.getBucketAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void insertBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      InsertBucketAccessControlRequest request =
          InsertBucketAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setBucketAccessControl(BucketAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.insertBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listBucketAccessControlsTest() throws Exception {
    ListBucketAccessControlsResponse expectedResponse =
        ListBucketAccessControlsResponse.newBuilder()
            .addAllItems(new ArrayList<BucketAccessControl>())
            .build();
    mockStorage.addResponse(expectedResponse);

    ListBucketAccessControlsRequest request =
        ListBucketAccessControlsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListBucketAccessControlsResponse actualResponse = client.listBucketAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListBucketAccessControlsRequest actualRequest =
        ((ListBucketAccessControlsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listBucketAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListBucketAccessControlsRequest request =
          ListBucketAccessControlsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listBucketAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateBucketAccessControlTest() throws Exception {
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateBucketAccessControlRequest request =
        UpdateBucketAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setBucketAccessControl(BucketAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    BucketAccessControl actualResponse = client.updateBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateBucketAccessControlRequest actualRequest =
        ((UpdateBucketAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getBucketAccessControl(), actualRequest.getBucketAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateBucketAccessControlRequest request =
          UpdateBucketAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setBucketAccessControl(BucketAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.updateBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void patchBucketAccessControlTest() throws Exception {
    BucketAccessControl expectedResponse =
        BucketAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    PatchBucketAccessControlRequest request =
        PatchBucketAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setBucketAccessControl(BucketAccessControl.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    BucketAccessControl actualResponse = client.patchBucketAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchBucketAccessControlRequest actualRequest =
        ((PatchBucketAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getBucketAccessControl(), actualRequest.getBucketAccessControl());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void patchBucketAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      PatchBucketAccessControlRequest request =
          PatchBucketAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setBucketAccessControl(BucketAccessControl.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.patchBucketAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteBucketTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteBucketRequest request =
        DeleteBucketRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteBucket(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteBucketRequest actualRequest = ((DeleteBucketRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteBucketRequest request =
          DeleteBucketRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getBucketTest() throws Exception {
    Bucket expectedResponse =
        Bucket.newBuilder()
            .addAllAcl(new ArrayList<BucketAccessControl>())
            .addAllDefaultObjectAcl(new ArrayList<ObjectAccessControl>())
            .setLifecycle(Bucket.Lifecycle.newBuilder().build())
            .setTimeCreated(Timestamp.newBuilder().build())
            .setId("id3355")
            .setName("name3373707")
            .setProjectNumber(828084015)
            .setMetageneration(1048558813)
            .addAllCors(new ArrayList<Bucket.Cors>())
            .setLocation("location1901043637")
            .setStorageClass("storageClass871353277")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setDefaultEventBasedHold(true)
            .putAllLabels(new HashMap<String, String>())
            .setWebsite(Bucket.Website.newBuilder().build())
            .setVersioning(Bucket.Versioning.newBuilder().build())
            .setLogging(Bucket.Logging.newBuilder().build())
            .setOwner(Owner.newBuilder().build())
            .setEncryption(Bucket.Encryption.newBuilder().build())
            .setBilling(Bucket.Billing.newBuilder().build())
            .setRetentionPolicy(Bucket.RetentionPolicy.newBuilder().build())
            .setLocationType("locationType-58277745")
            .setIamConfiguration(Bucket.IamConfiguration.newBuilder().build())
            .addAllZoneAffinity(new ArrayList<String>())
            .build();
    mockStorage.addResponse(expectedResponse);

    GetBucketRequest request =
        GetBucketRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Bucket actualResponse = client.getBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetBucketRequest actualRequest = ((GetBucketRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetBucketRequest request =
          GetBucketRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void insertBucketTest() throws Exception {
    Bucket expectedResponse =
        Bucket.newBuilder()
            .addAllAcl(new ArrayList<BucketAccessControl>())
            .addAllDefaultObjectAcl(new ArrayList<ObjectAccessControl>())
            .setLifecycle(Bucket.Lifecycle.newBuilder().build())
            .setTimeCreated(Timestamp.newBuilder().build())
            .setId("id3355")
            .setName("name3373707")
            .setProjectNumber(828084015)
            .setMetageneration(1048558813)
            .addAllCors(new ArrayList<Bucket.Cors>())
            .setLocation("location1901043637")
            .setStorageClass("storageClass871353277")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setDefaultEventBasedHold(true)
            .putAllLabels(new HashMap<String, String>())
            .setWebsite(Bucket.Website.newBuilder().build())
            .setVersioning(Bucket.Versioning.newBuilder().build())
            .setLogging(Bucket.Logging.newBuilder().build())
            .setOwner(Owner.newBuilder().build())
            .setEncryption(Bucket.Encryption.newBuilder().build())
            .setBilling(Bucket.Billing.newBuilder().build())
            .setRetentionPolicy(Bucket.RetentionPolicy.newBuilder().build())
            .setLocationType("locationType-58277745")
            .setIamConfiguration(Bucket.IamConfiguration.newBuilder().build())
            .addAllZoneAffinity(new ArrayList<String>())
            .build();
    mockStorage.addResponse(expectedResponse);

    InsertBucketRequest request =
        InsertBucketRequest.newBuilder()
            .setProject("project-309310695")
            .setBucket(Bucket.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Bucket actualResponse = client.insertBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertBucketRequest actualRequest = ((InsertBucketRequest) actualRequests.get(0));

    Assert.assertEquals(request.getPredefinedAcl(), actualRequest.getPredefinedAcl());
    Assert.assertEquals(
        request.getPredefinedDefaultObjectAcl(), actualRequest.getPredefinedDefaultObjectAcl());
    Assert.assertEquals(request.getProject(), actualRequest.getProject());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void insertBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      InsertBucketRequest request =
          InsertBucketRequest.newBuilder()
              .setProject("project-309310695")
              .setBucket(Bucket.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.insertBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listChannelsTest() throws Exception {
    ListChannelsResponse expectedResponse =
        ListChannelsResponse.newBuilder()
            .addAllItems(new ArrayList<ListChannelsResponse.Items>())
            .build();
    mockStorage.addResponse(expectedResponse);

    ListChannelsRequest request =
        ListChannelsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListChannelsResponse actualResponse = client.listChannels(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListChannelsRequest actualRequest = ((ListChannelsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listChannelsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListChannelsRequest request =
          ListChannelsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listChannels(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listBucketsTest() throws Exception {
    ListBucketsResponse expectedResponse =
        ListBucketsResponse.newBuilder()
            .addAllItems(new ArrayList<Bucket>())
            .setNextPageToken("nextPageToken-1386094857")
            .build();
    mockStorage.addResponse(expectedResponse);

    ListBucketsRequest request =
        ListBucketsRequest.newBuilder()
            .setMaxResults(1128457243)
            .setPageToken("pageToken873572522")
            .setPrefix("prefix-980110702")
            .setProject("project-309310695")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListBucketsResponse actualResponse = client.listBuckets(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListBucketsRequest actualRequest = ((ListBucketsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getMaxResults(), actualRequest.getMaxResults());
    Assert.assertEquals(request.getPageToken(), actualRequest.getPageToken());
    Assert.assertEquals(request.getPrefix(), actualRequest.getPrefix());
    Assert.assertEquals(request.getProject(), actualRequest.getProject());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listBucketsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListBucketsRequest request =
          ListBucketsRequest.newBuilder()
              .setMaxResults(1128457243)
              .setPageToken("pageToken873572522")
              .setPrefix("prefix-980110702")
              .setProject("project-309310695")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listBuckets(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void lockBucketRetentionPolicyTest() throws Exception {
    Bucket expectedResponse =
        Bucket.newBuilder()
            .addAllAcl(new ArrayList<BucketAccessControl>())
            .addAllDefaultObjectAcl(new ArrayList<ObjectAccessControl>())
            .setLifecycle(Bucket.Lifecycle.newBuilder().build())
            .setTimeCreated(Timestamp.newBuilder().build())
            .setId("id3355")
            .setName("name3373707")
            .setProjectNumber(828084015)
            .setMetageneration(1048558813)
            .addAllCors(new ArrayList<Bucket.Cors>())
            .setLocation("location1901043637")
            .setStorageClass("storageClass871353277")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setDefaultEventBasedHold(true)
            .putAllLabels(new HashMap<String, String>())
            .setWebsite(Bucket.Website.newBuilder().build())
            .setVersioning(Bucket.Versioning.newBuilder().build())
            .setLogging(Bucket.Logging.newBuilder().build())
            .setOwner(Owner.newBuilder().build())
            .setEncryption(Bucket.Encryption.newBuilder().build())
            .setBilling(Bucket.Billing.newBuilder().build())
            .setRetentionPolicy(Bucket.RetentionPolicy.newBuilder().build())
            .setLocationType("locationType-58277745")
            .setIamConfiguration(Bucket.IamConfiguration.newBuilder().build())
            .addAllZoneAffinity(new ArrayList<String>())
            .build();
    mockStorage.addResponse(expectedResponse);

    LockRetentionPolicyRequest request =
        LockRetentionPolicyRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(1043427781)
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Bucket actualResponse = client.lockBucketRetentionPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    LockRetentionPolicyRequest actualRequest = ((LockRetentionPolicyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void lockBucketRetentionPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      LockRetentionPolicyRequest request =
          LockRetentionPolicyRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(1043427781)
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.lockBucketRetentionPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getBucketIamPolicyTest() throws Exception {
    Policy expectedResponse =
        Policy.newBuilder()
            .setVersion(351608024)
            .addAllBindings(new ArrayList<Binding>())
            .setEtag(ByteString.EMPTY)
            .build();
    mockStorage.addResponse(expectedResponse);

    GetIamPolicyRequest request =
        GetIamPolicyRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.GetIamPolicyRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Policy actualResponse = client.getBucketIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getBucketIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetIamPolicyRequest request =
          GetIamPolicyRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.GetIamPolicyRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getBucketIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void setBucketIamPolicyTest() throws Exception {
    Policy expectedResponse =
        Policy.newBuilder()
            .setVersion(351608024)
            .addAllBindings(new ArrayList<Binding>())
            .setEtag(ByteString.EMPTY)
            .build();
    mockStorage.addResponse(expectedResponse);

    SetIamPolicyRequest request =
        SetIamPolicyRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.SetIamPolicyRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Policy actualResponse = client.setBucketIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    SetIamPolicyRequest actualRequest = ((SetIamPolicyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void setBucketIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      SetIamPolicyRequest request =
          SetIamPolicyRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.SetIamPolicyRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.setBucketIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void testBucketIamPermissionsTest() throws Exception {
    TestIamPermissionsResponse expectedResponse =
        TestIamPermissionsResponse.newBuilder().addAllPermissions(new ArrayList<String>()).build();
    mockStorage.addResponse(expectedResponse);

    TestIamPermissionsRequest request =
        TestIamPermissionsRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.TestIamPermissionsRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    TestIamPermissionsResponse actualResponse = client.testBucketIamPermissions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    TestIamPermissionsRequest actualRequest = ((TestIamPermissionsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void testBucketIamPermissionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      TestIamPermissionsRequest request =
          TestIamPermissionsRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.TestIamPermissionsRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.testBucketIamPermissions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void patchBucketTest() throws Exception {
    Bucket expectedResponse =
        Bucket.newBuilder()
            .addAllAcl(new ArrayList<BucketAccessControl>())
            .addAllDefaultObjectAcl(new ArrayList<ObjectAccessControl>())
            .setLifecycle(Bucket.Lifecycle.newBuilder().build())
            .setTimeCreated(Timestamp.newBuilder().build())
            .setId("id3355")
            .setName("name3373707")
            .setProjectNumber(828084015)
            .setMetageneration(1048558813)
            .addAllCors(new ArrayList<Bucket.Cors>())
            .setLocation("location1901043637")
            .setStorageClass("storageClass871353277")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setDefaultEventBasedHold(true)
            .putAllLabels(new HashMap<String, String>())
            .setWebsite(Bucket.Website.newBuilder().build())
            .setVersioning(Bucket.Versioning.newBuilder().build())
            .setLogging(Bucket.Logging.newBuilder().build())
            .setOwner(Owner.newBuilder().build())
            .setEncryption(Bucket.Encryption.newBuilder().build())
            .setBilling(Bucket.Billing.newBuilder().build())
            .setRetentionPolicy(Bucket.RetentionPolicy.newBuilder().build())
            .setLocationType("locationType-58277745")
            .setIamConfiguration(Bucket.IamConfiguration.newBuilder().build())
            .addAllZoneAffinity(new ArrayList<String>())
            .build();
    mockStorage.addResponse(expectedResponse);

    PatchBucketRequest request =
        PatchBucketRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setMetadata(Bucket.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Bucket actualResponse = client.patchBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchBucketRequest actualRequest = ((PatchBucketRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getPredefinedAcl(), actualRequest.getPredefinedAcl());
    Assert.assertEquals(
        request.getPredefinedDefaultObjectAcl(), actualRequest.getPredefinedDefaultObjectAcl());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getMetadata(), actualRequest.getMetadata());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void patchBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      PatchBucketRequest request =
          PatchBucketRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setMetadata(Bucket.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.patchBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateBucketTest() throws Exception {
    Bucket expectedResponse =
        Bucket.newBuilder()
            .addAllAcl(new ArrayList<BucketAccessControl>())
            .addAllDefaultObjectAcl(new ArrayList<ObjectAccessControl>())
            .setLifecycle(Bucket.Lifecycle.newBuilder().build())
            .setTimeCreated(Timestamp.newBuilder().build())
            .setId("id3355")
            .setName("name3373707")
            .setProjectNumber(828084015)
            .setMetageneration(1048558813)
            .addAllCors(new ArrayList<Bucket.Cors>())
            .setLocation("location1901043637")
            .setStorageClass("storageClass871353277")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setDefaultEventBasedHold(true)
            .putAllLabels(new HashMap<String, String>())
            .setWebsite(Bucket.Website.newBuilder().build())
            .setVersioning(Bucket.Versioning.newBuilder().build())
            .setLogging(Bucket.Logging.newBuilder().build())
            .setOwner(Owner.newBuilder().build())
            .setEncryption(Bucket.Encryption.newBuilder().build())
            .setBilling(Bucket.Billing.newBuilder().build())
            .setRetentionPolicy(Bucket.RetentionPolicy.newBuilder().build())
            .setLocationType("locationType-58277745")
            .setIamConfiguration(Bucket.IamConfiguration.newBuilder().build())
            .addAllZoneAffinity(new ArrayList<String>())
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateBucketRequest request =
        UpdateBucketRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setMetadata(Bucket.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Bucket actualResponse = client.updateBucket(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateBucketRequest actualRequest = ((UpdateBucketRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getPredefinedAcl(), actualRequest.getPredefinedAcl());
    Assert.assertEquals(
        request.getPredefinedDefaultObjectAcl(), actualRequest.getPredefinedDefaultObjectAcl());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getMetadata(), actualRequest.getMetadata());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateBucketExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateBucketRequest request =
          UpdateBucketRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setMetadata(Bucket.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.updateBucket(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void stopChannelTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    StopChannelRequest request =
        StopChannelRequest.newBuilder()
            .setChannel(Channel.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.stopChannel(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    StopChannelRequest actualRequest = ((StopChannelRequest) actualRequests.get(0));

    Assert.assertEquals(request.getChannel(), actualRequest.getChannel());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void stopChannelExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      StopChannelRequest request =
          StopChannelRequest.newBuilder()
              .setChannel(Channel.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.stopChannel(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteDefaultObjectAccessControlTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteDefaultObjectAccessControlRequest request =
        DeleteDefaultObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteDefaultObjectAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteDefaultObjectAccessControlRequest actualRequest =
        ((DeleteDefaultObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteDefaultObjectAccessControlRequest request =
          DeleteDefaultObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getDefaultObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    GetDefaultObjectAccessControlRequest request =
        GetDefaultObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.getDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetDefaultObjectAccessControlRequest actualRequest =
        ((GetDefaultObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetDefaultObjectAccessControlRequest request =
          GetDefaultObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void insertDefaultObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    InsertDefaultObjectAccessControlRequest request =
        InsertDefaultObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.insertDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertDefaultObjectAccessControlRequest actualRequest =
        ((InsertDefaultObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void insertDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      InsertDefaultObjectAccessControlRequest request =
          InsertDefaultObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.insertDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listDefaultObjectAccessControlsTest() throws Exception {
    ListObjectAccessControlsResponse expectedResponse =
        ListObjectAccessControlsResponse.newBuilder()
            .addAllItems(new ArrayList<ObjectAccessControl>())
            .build();
    mockStorage.addResponse(expectedResponse);

    ListDefaultObjectAccessControlsRequest request =
        ListDefaultObjectAccessControlsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListObjectAccessControlsResponse actualResponse =
        client.listDefaultObjectAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListDefaultObjectAccessControlsRequest actualRequest =
        ((ListDefaultObjectAccessControlsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listDefaultObjectAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListDefaultObjectAccessControlsRequest request =
          ListDefaultObjectAccessControlsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listDefaultObjectAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void patchDefaultObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    PatchDefaultObjectAccessControlRequest request =
        PatchDefaultObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.patchDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchDefaultObjectAccessControlRequest actualRequest =
        ((PatchDefaultObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void patchDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      PatchDefaultObjectAccessControlRequest request =
          PatchDefaultObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.patchDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateDefaultObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateDefaultObjectAccessControlRequest request =
        UpdateDefaultObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.updateDefaultObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateDefaultObjectAccessControlRequest actualRequest =
        ((UpdateDefaultObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateDefaultObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateDefaultObjectAccessControlRequest request =
          UpdateDefaultObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.updateDefaultObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteNotificationTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteNotificationRequest request =
        DeleteNotificationRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setNotification("notification595233003")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteNotification(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteNotificationRequest actualRequest = ((DeleteNotificationRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getNotification(), actualRequest.getNotification());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteNotificationRequest request =
          DeleteNotificationRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setNotification("notification595233003")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getNotificationTest() throws Exception {
    Notification expectedResponse =
        Notification.newBuilder()
            .setTopic("topic110546223")
            .addAllEventTypes(new ArrayList<String>())
            .putAllCustomAttributes(new HashMap<String, String>())
            .setEtag("etag3123477")
            .setObjectNamePrefix("objectNamePrefix-1978236516")
            .setPayloadFormat("payloadFormat-2140609755")
            .setId("id3355")
            .build();
    mockStorage.addResponse(expectedResponse);

    GetNotificationRequest request =
        GetNotificationRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setNotification("notification595233003")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Notification actualResponse = client.getNotification(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetNotificationRequest actualRequest = ((GetNotificationRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getNotification(), actualRequest.getNotification());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetNotificationRequest request =
          GetNotificationRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setNotification("notification595233003")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void insertNotificationTest() throws Exception {
    Notification expectedResponse =
        Notification.newBuilder()
            .setTopic("topic110546223")
            .addAllEventTypes(new ArrayList<String>())
            .putAllCustomAttributes(new HashMap<String, String>())
            .setEtag("etag3123477")
            .setObjectNamePrefix("objectNamePrefix-1978236516")
            .setPayloadFormat("payloadFormat-2140609755")
            .setId("id3355")
            .build();
    mockStorage.addResponse(expectedResponse);

    InsertNotificationRequest request =
        InsertNotificationRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setNotification(Notification.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Notification actualResponse = client.insertNotification(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertNotificationRequest actualRequest = ((InsertNotificationRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getNotification(), actualRequest.getNotification());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void insertNotificationExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      InsertNotificationRequest request =
          InsertNotificationRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setNotification(Notification.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.insertNotification(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listNotificationsTest() throws Exception {
    ListNotificationsResponse expectedResponse =
        ListNotificationsResponse.newBuilder().addAllItems(new ArrayList<Notification>()).build();
    mockStorage.addResponse(expectedResponse);

    ListNotificationsRequest request =
        ListNotificationsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListNotificationsResponse actualResponse = client.listNotifications(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListNotificationsRequest actualRequest = ((ListNotificationsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listNotificationsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListNotificationsRequest request =
          ListNotificationsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listNotifications(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteObjectAccessControlTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteObjectAccessControlRequest request =
        DeleteObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteObjectAccessControl(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteObjectAccessControlRequest actualRequest =
        ((DeleteObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteObjectAccessControlRequest request =
          DeleteObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    GetObjectAccessControlRequest request =
        GetObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.getObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetObjectAccessControlRequest actualRequest =
        ((GetObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetObjectAccessControlRequest request =
          GetObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void insertObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    InsertObjectAccessControlRequest request =
        InsertObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.insertObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    InsertObjectAccessControlRequest actualRequest =
        ((InsertObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void insertObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      InsertObjectAccessControlRequest request =
          InsertObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.insertObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listObjectAccessControlsTest() throws Exception {
    ListObjectAccessControlsResponse expectedResponse =
        ListObjectAccessControlsResponse.newBuilder()
            .addAllItems(new ArrayList<ObjectAccessControl>())
            .build();
    mockStorage.addResponse(expectedResponse);

    ListObjectAccessControlsRequest request =
        ListObjectAccessControlsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListObjectAccessControlsResponse actualResponse = client.listObjectAccessControls(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListObjectAccessControlsRequest actualRequest =
        ((ListObjectAccessControlsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listObjectAccessControlsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListObjectAccessControlsRequest request =
          ListObjectAccessControlsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listObjectAccessControls(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void patchObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    PatchObjectAccessControlRequest request =
        PatchObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.patchObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchObjectAccessControlRequest actualRequest =
        ((PatchObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void patchObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      PatchObjectAccessControlRequest request =
          PatchObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .build();
      client.patchObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateObjectAccessControlTest() throws Exception {
    ObjectAccessControl expectedResponse =
        ObjectAccessControl.newBuilder()
            .setRole("role3506294")
            .setEtag("etag3123477")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setEntity("entity-1298275357")
            .setEntityId("entityId-2102099874")
            .setEmail("email96619420")
            .setDomain("domain-1326197564")
            .setProjectTeam(ProjectTeam.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateObjectAccessControlRequest request =
        UpdateObjectAccessControlRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setEntity("entity-1298275357")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .build();

    ObjectAccessControl actualResponse = client.updateObjectAccessControl(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateObjectAccessControlRequest actualRequest =
        ((UpdateObjectAccessControlRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getEntity(), actualRequest.getEntity());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getObjectAccessControl(), actualRequest.getObjectAccessControl());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateObjectAccessControlExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateObjectAccessControlRequest request =
          UpdateObjectAccessControlRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setEntity("entity-1298275357")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .build();
      client.updateObjectAccessControl(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void composeObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    ComposeObjectRequest request =
        ComposeObjectRequest.newBuilder()
            .setDestinationBucket("destinationBucket-2116438120")
            .setDestinationObject("destinationObject-1761603347")
            .setDestination(Object.newBuilder().build())
            .addAllSourceObjects(new ArrayList<ComposeObjectRequest.SourceObjects>())
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setKmsKeyName("kmsKeyName412586233")
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Object actualResponse = client.composeObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComposeObjectRequest actualRequest = ((ComposeObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getDestinationBucket(), actualRequest.getDestinationBucket());
    Assert.assertEquals(request.getDestinationObject(), actualRequest.getDestinationObject());
    Assert.assertEquals(
        request.getDestinationPredefinedAcl(), actualRequest.getDestinationPredefinedAcl());
    Assert.assertEquals(request.getDestination(), actualRequest.getDestination());
    Assert.assertEquals(request.getSourceObjectsList(), actualRequest.getSourceObjectsList());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(request.getKmsKeyName(), actualRequest.getKmsKeyName());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void composeObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ComposeObjectRequest request =
          ComposeObjectRequest.newBuilder()
              .setDestinationBucket("destinationBucket-2116438120")
              .setDestinationObject("destinationObject-1761603347")
              .setDestination(Object.newBuilder().build())
              .addAllSourceObjects(new ArrayList<ComposeObjectRequest.SourceObjects>())
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setKmsKeyName("kmsKeyName412586233")
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.composeObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void copyObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    CopyObjectRequest request =
        CopyObjectRequest.newBuilder()
            .setDestinationBucket("destinationBucket-2116438120")
            .setDestinationObject("destinationObject-1761603347")
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
            .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setSourceBucket("sourceBucket841604581")
            .setSourceObject("sourceObject1196439354")
            .setSourceGeneration(1232209852)
            .setDestination(Object.newBuilder().build())
            .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Object actualResponse = client.copyObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CopyObjectRequest actualRequest = ((CopyObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getDestinationBucket(), actualRequest.getDestinationBucket());
    Assert.assertEquals(request.getDestinationObject(), actualRequest.getDestinationObject());
    Assert.assertEquals(
        request.getDestinationPredefinedAcl(), actualRequest.getDestinationPredefinedAcl());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(
        request.getIfSourceGenerationMatch(), actualRequest.getIfSourceGenerationMatch());
    Assert.assertEquals(
        request.getIfSourceGenerationNotMatch(), actualRequest.getIfSourceGenerationNotMatch());
    Assert.assertEquals(
        request.getIfSourceMetagenerationMatch(), actualRequest.getIfSourceMetagenerationMatch());
    Assert.assertEquals(
        request.getIfSourceMetagenerationNotMatch(),
        actualRequest.getIfSourceMetagenerationNotMatch());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getSourceBucket(), actualRequest.getSourceBucket());
    Assert.assertEquals(request.getSourceObject(), actualRequest.getSourceObject());
    Assert.assertEquals(request.getSourceGeneration(), actualRequest.getSourceGeneration());
    Assert.assertEquals(request.getDestination(), actualRequest.getDestination());
    Assert.assertEquals(
        request.getDestinationKmsKeyName(), actualRequest.getDestinationKmsKeyName());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void copyObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      CopyObjectRequest request =
          CopyObjectRequest.newBuilder()
              .setDestinationBucket("destinationBucket-2116438120")
              .setDestinationObject("destinationObject-1761603347")
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
              .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setSourceBucket("sourceBucket841604581")
              .setSourceObject("sourceObject1196439354")
              .setSourceGeneration(1232209852)
              .setDestination(Object.newBuilder().build())
              .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.copyObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteObjectTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteObjectRequest request =
        DeleteObjectRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setUploadId("uploadId1563990780")
            .setGeneration(305703192)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteObject(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteObjectRequest actualRequest = ((DeleteObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getUploadId(), actualRequest.getUploadId());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteObjectRequest request =
          DeleteObjectRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setUploadId("uploadId1563990780")
              .setGeneration(305703192)
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    GetObjectRequest request =
        GetObjectRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Object actualResponse = client.getObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetObjectRequest actualRequest = ((GetObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetObjectRequest request =
          GetObjectRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getObjectMediaTest() throws Exception {
    GetObjectMediaResponse expectedResponse =
        GetObjectMediaResponse.newBuilder()
            .setChecksummedData(ChecksummedData.newBuilder().build())
            .setObjectChecksums(ObjectChecksums.newBuilder().build())
            .setContentRange(ContentRange.newBuilder().build())
            .setMetadata(Object.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);
    GetObjectMediaRequest request =
        GetObjectMediaRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setReadOffset(-715377828)
            .setReadLimit(-164298798)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    MockStreamObserver<GetObjectMediaResponse> responseObserver = new MockStreamObserver<>();

    ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse> callable =
        client.getObjectMediaCallable();
    callable.serverStreamingCall(request, responseObserver);

    List<GetObjectMediaResponse> actualResponses = responseObserver.future().get();
    Assert.assertEquals(1, actualResponses.size());
    Assert.assertEquals(expectedResponse, actualResponses.get(0));
  }

  @Test
  public void getObjectMediaExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);
    GetObjectMediaRequest request =
        GetObjectMediaRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setReadOffset(-715377828)
            .setReadLimit(-164298798)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    MockStreamObserver<GetObjectMediaResponse> responseObserver = new MockStreamObserver<>();

    ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse> callable =
        client.getObjectMediaCallable();
    callable.serverStreamingCall(request, responseObserver);

    try {
      List<GetObjectMediaResponse> actualResponses = responseObserver.future().get();
      Assert.fail("No exception thrown");
    } catch (ExecutionException e) {
      Assert.assertTrue(e.getCause() instanceof InvalidArgumentException);
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void insertObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);
    InsertObjectRequest request =
        InsertObjectRequest.newBuilder()
            .setWriteOffset(-1559543565)
            .setObjectChecksums(ObjectChecksums.newBuilder().build())
            .setFinishWrite(true)
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    MockStreamObserver<Object> responseObserver = new MockStreamObserver<>();

    ClientStreamingCallable<InsertObjectRequest, Object> callable = client.insertObjectCallable();
    ApiStreamObserver<InsertObjectRequest> requestObserver =
        callable.clientStreamingCall(responseObserver);

    requestObserver.onNext(request);
    requestObserver.onCompleted();

    List<Object> actualResponses = responseObserver.future().get();
    Assert.assertEquals(1, actualResponses.size());
    Assert.assertEquals(expectedResponse, actualResponses.get(0));
  }

  @Test
  public void insertObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);
    InsertObjectRequest request =
        InsertObjectRequest.newBuilder()
            .setWriteOffset(-1559543565)
            .setObjectChecksums(ObjectChecksums.newBuilder().build())
            .setFinishWrite(true)
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    MockStreamObserver<Object> responseObserver = new MockStreamObserver<>();

    ClientStreamingCallable<InsertObjectRequest, Object> callable = client.insertObjectCallable();
    ApiStreamObserver<InsertObjectRequest> requestObserver =
        callable.clientStreamingCall(responseObserver);

    requestObserver.onNext(request);

    try {
      List<Object> actualResponses = responseObserver.future().get();
      Assert.fail("No exception thrown");
    } catch (ExecutionException e) {
      Assert.assertTrue(e.getCause() instanceof InvalidArgumentException);
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void listObjectsTest() throws Exception {
    ListObjectsResponse expectedResponse =
        ListObjectsResponse.newBuilder()
            .addAllPrefixes(new ArrayList<String>())
            .addAllItems(new ArrayList<Object>())
            .setNextPageToken("nextPageToken-1386094857")
            .build();
    mockStorage.addResponse(expectedResponse);

    ListObjectsRequest request =
        ListObjectsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setDelimiter("delimiter-250518009")
            .setIncludeTrailingDelimiter(true)
            .setMaxResults(1128457243)
            .setPageToken("pageToken873572522")
            .setPrefix("prefix-980110702")
            .setVersions(true)
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListObjectsResponse actualResponse = client.listObjects(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListObjectsRequest actualRequest = ((ListObjectsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getDelimiter(), actualRequest.getDelimiter());
    Assert.assertEquals(
        request.getIncludeTrailingDelimiter(), actualRequest.getIncludeTrailingDelimiter());
    Assert.assertEquals(request.getMaxResults(), actualRequest.getMaxResults());
    Assert.assertEquals(request.getPageToken(), actualRequest.getPageToken());
    Assert.assertEquals(request.getPrefix(), actualRequest.getPrefix());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getVersions(), actualRequest.getVersions());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listObjectsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListObjectsRequest request =
          ListObjectsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setDelimiter("delimiter-250518009")
              .setIncludeTrailingDelimiter(true)
              .setMaxResults(1128457243)
              .setPageToken("pageToken873572522")
              .setPrefix("prefix-980110702")
              .setVersions(true)
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listObjects(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void rewriteObjectTest() throws Exception {
    RewriteResponse expectedResponse =
        RewriteResponse.newBuilder()
            .setTotalBytesRewritten(-1109205579)
            .setObjectSize(-1277221631)
            .setDone(true)
            .setRewriteToken("rewriteToken80654285")
            .setResource(Object.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    RewriteObjectRequest request =
        RewriteObjectRequest.newBuilder()
            .setDestinationBucket("destinationBucket-2116438120")
            .setDestinationObject("destinationObject-1761603347")
            .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
            .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setMaxBytesRewrittenPerCall(1178170730)
            .setRewriteToken("rewriteToken80654285")
            .setSourceBucket("sourceBucket841604581")
            .setSourceObject("sourceObject1196439354")
            .setSourceGeneration(1232209852)
            .setObject(Object.newBuilder().build())
            .setCopySourceEncryptionAlgorithm("copySourceEncryptionAlgorithm-1524952548")
            .setCopySourceEncryptionKey("copySourceEncryptionKey1199243724")
            .setCopySourceEncryptionKeySha256("copySourceEncryptionKeySha256544611091")
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    RewriteResponse actualResponse = client.rewriteObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    RewriteObjectRequest actualRequest = ((RewriteObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getDestinationBucket(), actualRequest.getDestinationBucket());
    Assert.assertEquals(request.getDestinationObject(), actualRequest.getDestinationObject());
    Assert.assertEquals(
        request.getDestinationKmsKeyName(), actualRequest.getDestinationKmsKeyName());
    Assert.assertEquals(
        request.getDestinationPredefinedAcl(), actualRequest.getDestinationPredefinedAcl());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(
        request.getIfSourceGenerationMatch(), actualRequest.getIfSourceGenerationMatch());
    Assert.assertEquals(
        request.getIfSourceGenerationNotMatch(), actualRequest.getIfSourceGenerationNotMatch());
    Assert.assertEquals(
        request.getIfSourceMetagenerationMatch(), actualRequest.getIfSourceMetagenerationMatch());
    Assert.assertEquals(
        request.getIfSourceMetagenerationNotMatch(),
        actualRequest.getIfSourceMetagenerationNotMatch());
    Assert.assertEquals(
        request.getMaxBytesRewrittenPerCall(), actualRequest.getMaxBytesRewrittenPerCall());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getRewriteToken(), actualRequest.getRewriteToken());
    Assert.assertEquals(request.getSourceBucket(), actualRequest.getSourceBucket());
    Assert.assertEquals(request.getSourceObject(), actualRequest.getSourceObject());
    Assert.assertEquals(request.getSourceGeneration(), actualRequest.getSourceGeneration());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(
        request.getCopySourceEncryptionAlgorithm(),
        actualRequest.getCopySourceEncryptionAlgorithm());
    Assert.assertEquals(
        request.getCopySourceEncryptionKey(), actualRequest.getCopySourceEncryptionKey());
    Assert.assertEquals(
        request.getCopySourceEncryptionKeySha256(),
        actualRequest.getCopySourceEncryptionKeySha256());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void rewriteObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      RewriteObjectRequest request =
          RewriteObjectRequest.newBuilder()
              .setDestinationBucket("destinationBucket-2116438120")
              .setDestinationObject("destinationObject-1761603347")
              .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
              .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setMaxBytesRewrittenPerCall(1178170730)
              .setRewriteToken("rewriteToken80654285")
              .setSourceBucket("sourceBucket841604581")
              .setSourceObject("sourceObject1196439354")
              .setSourceGeneration(1232209852)
              .setObject(Object.newBuilder().build())
              .setCopySourceEncryptionAlgorithm("copySourceEncryptionAlgorithm-1524952548")
              .setCopySourceEncryptionKey("copySourceEncryptionKey1199243724")
              .setCopySourceEncryptionKeySha256("copySourceEncryptionKeySha256544611091")
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.rewriteObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void startResumableWriteTest() throws Exception {
    StartResumableWriteResponse expectedResponse =
        StartResumableWriteResponse.newBuilder().setUploadId("uploadId1563990780").build();
    mockStorage.addResponse(expectedResponse);

    StartResumableWriteRequest request =
        StartResumableWriteRequest.newBuilder()
            .setInsertObjectSpec(InsertObjectSpec.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    StartResumableWriteResponse actualResponse = client.startResumableWrite(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    StartResumableWriteRequest actualRequest = ((StartResumableWriteRequest) actualRequests.get(0));

    Assert.assertEquals(request.getInsertObjectSpec(), actualRequest.getInsertObjectSpec());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void startResumableWriteExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      StartResumableWriteRequest request =
          StartResumableWriteRequest.newBuilder()
              .setInsertObjectSpec(InsertObjectSpec.newBuilder().build())
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.startResumableWrite(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void queryWriteStatusTest() throws Exception {
    QueryWriteStatusResponse expectedResponse =
        QueryWriteStatusResponse.newBuilder()
            .setCommittedSize(1907158756)
            .setComplete(true)
            .build();
    mockStorage.addResponse(expectedResponse);

    QueryWriteStatusRequest request =
        QueryWriteStatusRequest.newBuilder()
            .setUploadId("uploadId1563990780")
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    QueryWriteStatusResponse actualResponse = client.queryWriteStatus(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    QueryWriteStatusRequest actualRequest = ((QueryWriteStatusRequest) actualRequests.get(0));

    Assert.assertEquals(request.getUploadId(), actualRequest.getUploadId());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void queryWriteStatusExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      QueryWriteStatusRequest request =
          QueryWriteStatusRequest.newBuilder()
              .setUploadId("uploadId1563990780")
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.queryWriteStatus(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void patchObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    PatchObjectRequest request =
        PatchObjectRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setMetadata(Object.newBuilder().build())
            .setUpdateMask(FieldMask.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Object actualResponse = client.patchObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PatchObjectRequest actualRequest = ((PatchObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getPredefinedAcl(), actualRequest.getPredefinedAcl());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getMetadata(), actualRequest.getMetadata());
    Assert.assertEquals(request.getUpdateMask(), actualRequest.getUpdateMask());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void patchObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      PatchObjectRequest request =
          PatchObjectRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setMetadata(Object.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.patchObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateObjectTest() throws Exception {
    Object expectedResponse =
        Object.newBuilder()
            .setContentEncoding("contentEncoding-160088852")
            .setContentDisposition("contentDisposition1034341758")
            .setCacheControl("cacheControl-1336592517")
            .addAllAcl(new ArrayList<ObjectAccessControl>())
            .setContentLanguage("contentLanguage810066673")
            .setMetageneration(1048558813)
            .setTimeDeleted(Timestamp.newBuilder().build())
            .setContentType("contentType-389131437")
            .setSize(3530753)
            .setTimeCreated(Timestamp.newBuilder().build())
            .setCrc32C(UInt32Value.newBuilder().build())
            .setComponentCount(-485073075)
            .setMd5Hash("md5Hash867756972")
            .setEtag("etag3123477")
            .setUpdated(Timestamp.newBuilder().build())
            .setStorageClass("storageClass871353277")
            .setKmsKeyName("kmsKeyName412586233")
            .setTimeStorageClassUpdated(Timestamp.newBuilder().build())
            .setTemporaryHold(true)
            .setRetentionExpirationTime(Timestamp.newBuilder().build())
            .putAllMetadata(new HashMap<String, String>())
            .setEventBasedHold(BoolValue.newBuilder().build())
            .setName("name3373707")
            .setId("id3355")
            .setBucket("bucket-1378203158")
            .setGeneration(305703192)
            .setOwner(Owner.newBuilder().build())
            .setCustomerEncryption(Object.CustomerEncryption.newBuilder().build())
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateObjectRequest request =
        UpdateObjectRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setObject("object-1023368385")
            .setGeneration(305703192)
            .setIfGenerationMatch(Int64Value.newBuilder().build())
            .setIfGenerationNotMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationMatch(Int64Value.newBuilder().build())
            .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
            .setMetadata(Object.newBuilder().build())
            .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Object actualResponse = client.updateObject(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateObjectRequest actualRequest = ((UpdateObjectRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getObject(), actualRequest.getObject());
    Assert.assertEquals(request.getGeneration(), actualRequest.getGeneration());
    Assert.assertEquals(request.getIfGenerationMatch(), actualRequest.getIfGenerationMatch());
    Assert.assertEquals(request.getIfGenerationNotMatch(), actualRequest.getIfGenerationNotMatch());
    Assert.assertEquals(
        request.getIfMetagenerationMatch(), actualRequest.getIfMetagenerationMatch());
    Assert.assertEquals(
        request.getIfMetagenerationNotMatch(), actualRequest.getIfMetagenerationNotMatch());
    Assert.assertEquals(request.getPredefinedAcl(), actualRequest.getPredefinedAcl());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getMetadata(), actualRequest.getMetadata());
    Assert.assertEquals(
        request.getCommonObjectRequestParams(), actualRequest.getCommonObjectRequestParams());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateObjectExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateObjectRequest request =
          UpdateObjectRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setGeneration(305703192)
              .setIfGenerationMatch(Int64Value.newBuilder().build())
              .setIfGenerationNotMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationMatch(Int64Value.newBuilder().build())
              .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
              .setMetadata(Object.newBuilder().build())
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.updateObject(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getObjectIamPolicyTest() throws Exception {
    Policy expectedResponse =
        Policy.newBuilder()
            .setVersion(351608024)
            .addAllBindings(new ArrayList<Binding>())
            .setEtag(ByteString.EMPTY)
            .build();
    mockStorage.addResponse(expectedResponse);

    GetIamPolicyRequest request =
        GetIamPolicyRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.GetIamPolicyRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Policy actualResponse = client.getObjectIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getObjectIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetIamPolicyRequest request =
          GetIamPolicyRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.GetIamPolicyRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getObjectIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void setObjectIamPolicyTest() throws Exception {
    Policy expectedResponse =
        Policy.newBuilder()
            .setVersion(351608024)
            .addAllBindings(new ArrayList<Binding>())
            .setEtag(ByteString.EMPTY)
            .build();
    mockStorage.addResponse(expectedResponse);

    SetIamPolicyRequest request =
        SetIamPolicyRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.SetIamPolicyRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Policy actualResponse = client.setObjectIamPolicy(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    SetIamPolicyRequest actualRequest = ((SetIamPolicyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void setObjectIamPolicyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      SetIamPolicyRequest request =
          SetIamPolicyRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.SetIamPolicyRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.setObjectIamPolicy(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void testObjectIamPermissionsTest() throws Exception {
    TestIamPermissionsResponse expectedResponse =
        TestIamPermissionsResponse.newBuilder().addAllPermissions(new ArrayList<String>()).build();
    mockStorage.addResponse(expectedResponse);

    TestIamPermissionsRequest request =
        TestIamPermissionsRequest.newBuilder()
            .setIamRequest(com.google.iam.v1.TestIamPermissionsRequest.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    TestIamPermissionsResponse actualResponse = client.testObjectIamPermissions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    TestIamPermissionsRequest actualRequest = ((TestIamPermissionsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getIamRequest(), actualRequest.getIamRequest());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void testObjectIamPermissionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      TestIamPermissionsRequest request =
          TestIamPermissionsRequest.newBuilder()
              .setIamRequest(com.google.iam.v1.TestIamPermissionsRequest.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.testObjectIamPermissions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void watchAllObjectsTest() throws Exception {
    Channel expectedResponse =
        Channel.newBuilder()
            .setId("id3355")
            .setResourceId("resourceId-1345650231")
            .setResourceUri("resourceUri1234527870")
            .setToken("token110541305")
            .setExpiration(Timestamp.newBuilder().build())
            .setType("type3575610")
            .setAddress("address-1147692044")
            .putAllParams(new HashMap<String, String>())
            .setPayload(true)
            .build();
    mockStorage.addResponse(expectedResponse);

    WatchAllObjectsRequest request =
        WatchAllObjectsRequest.newBuilder()
            .setBucket("bucket-1378203158")
            .setVersions(true)
            .setDelimiter("delimiter-250518009")
            .setMaxResults(1128457243)
            .setPrefix("prefix-980110702")
            .setIncludeTrailingDelimiter(true)
            .setPageToken("pageToken873572522")
            .setChannel(Channel.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    Channel actualResponse = client.watchAllObjects(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    WatchAllObjectsRequest actualRequest = ((WatchAllObjectsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getBucket(), actualRequest.getBucket());
    Assert.assertEquals(request.getVersions(), actualRequest.getVersions());
    Assert.assertEquals(request.getDelimiter(), actualRequest.getDelimiter());
    Assert.assertEquals(request.getMaxResults(), actualRequest.getMaxResults());
    Assert.assertEquals(request.getPrefix(), actualRequest.getPrefix());
    Assert.assertEquals(
        request.getIncludeTrailingDelimiter(), actualRequest.getIncludeTrailingDelimiter());
    Assert.assertEquals(request.getPageToken(), actualRequest.getPageToken());
    Assert.assertEquals(request.getProjection(), actualRequest.getProjection());
    Assert.assertEquals(request.getChannel(), actualRequest.getChannel());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void watchAllObjectsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      WatchAllObjectsRequest request =
          WatchAllObjectsRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setVersions(true)
              .setDelimiter("delimiter-250518009")
              .setMaxResults(1128457243)
              .setPrefix("prefix-980110702")
              .setIncludeTrailingDelimiter(true)
              .setPageToken("pageToken873572522")
              .setChannel(Channel.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.watchAllObjects(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getServiceAccountTest() throws Exception {
    ServiceAccount expectedResponse =
        ServiceAccount.newBuilder().setEmailAddress("emailAddress-1070931784").build();
    mockStorage.addResponse(expectedResponse);

    GetProjectServiceAccountRequest request =
        GetProjectServiceAccountRequest.newBuilder()
            .setProjectId("projectId-894832108")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ServiceAccount actualResponse = client.getServiceAccount(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetProjectServiceAccountRequest actualRequest =
        ((GetProjectServiceAccountRequest) actualRequests.get(0));

    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getServiceAccountExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetProjectServiceAccountRequest request =
          GetProjectServiceAccountRequest.newBuilder()
              .setProjectId("projectId-894832108")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getServiceAccount(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createHmacKeyTest() throws Exception {
    CreateHmacKeyResponse expectedResponse =
        CreateHmacKeyResponse.newBuilder()
            .setMetadata(HmacKeyMetadata.newBuilder().build())
            .setSecret("secret-906277200")
            .build();
    mockStorage.addResponse(expectedResponse);

    CreateHmacKeyRequest request =
        CreateHmacKeyRequest.newBuilder()
            .setProjectId("projectId-894832108")
            .setServiceAccountEmail("serviceAccountEmail1825953988")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    CreateHmacKeyResponse actualResponse = client.createHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateHmacKeyRequest actualRequest = ((CreateHmacKeyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getServiceAccountEmail(), actualRequest.getServiceAccountEmail());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      CreateHmacKeyRequest request =
          CreateHmacKeyRequest.newBuilder()
              .setProjectId("projectId-894832108")
              .setServiceAccountEmail("serviceAccountEmail1825953988")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.createHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteHmacKeyTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorage.addResponse(expectedResponse);

    DeleteHmacKeyRequest request =
        DeleteHmacKeyRequest.newBuilder()
            .setAccessId("accessId-2146437729")
            .setProjectId("projectId-894832108")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    client.deleteHmacKey(request);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteHmacKeyRequest actualRequest = ((DeleteHmacKeyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getAccessId(), actualRequest.getAccessId());
    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      DeleteHmacKeyRequest request =
          DeleteHmacKeyRequest.newBuilder()
              .setAccessId("accessId-2146437729")
              .setProjectId("projectId-894832108")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.deleteHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getHmacKeyTest() throws Exception {
    HmacKeyMetadata expectedResponse =
        HmacKeyMetadata.newBuilder()
            .setId("id3355")
            .setAccessId("accessId-2146437729")
            .setProjectId("projectId-894832108")
            .setServiceAccountEmail("serviceAccountEmail1825953988")
            .setState("state109757585")
            .setTimeCreated(Timestamp.newBuilder().build())
            .setUpdated(Timestamp.newBuilder().build())
            .setEtag("etag3123477")
            .build();
    mockStorage.addResponse(expectedResponse);

    GetHmacKeyRequest request =
        GetHmacKeyRequest.newBuilder()
            .setAccessId("accessId-2146437729")
            .setProjectId("projectId-894832108")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    HmacKeyMetadata actualResponse = client.getHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetHmacKeyRequest actualRequest = ((GetHmacKeyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getAccessId(), actualRequest.getAccessId());
    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      GetHmacKeyRequest request =
          GetHmacKeyRequest.newBuilder()
              .setAccessId("accessId-2146437729")
              .setProjectId("projectId-894832108")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.getHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listHmacKeysTest() throws Exception {
    ListHmacKeysResponse expectedResponse =
        ListHmacKeysResponse.newBuilder()
            .setNextPageToken("nextPageToken-1386094857")
            .addAllItems(new ArrayList<HmacKeyMetadata>())
            .build();
    mockStorage.addResponse(expectedResponse);

    ListHmacKeysRequest request =
        ListHmacKeysRequest.newBuilder()
            .setProjectId("projectId-894832108")
            .setServiceAccountEmail("serviceAccountEmail1825953988")
            .setShowDeletedKeys(true)
            .setMaxResults(1128457243)
            .setPageToken("pageToken873572522")
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    ListHmacKeysResponse actualResponse = client.listHmacKeys(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListHmacKeysRequest actualRequest = ((ListHmacKeysRequest) actualRequests.get(0));

    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getServiceAccountEmail(), actualRequest.getServiceAccountEmail());
    Assert.assertEquals(request.getShowDeletedKeys(), actualRequest.getShowDeletedKeys());
    Assert.assertEquals(request.getMaxResults(), actualRequest.getMaxResults());
    Assert.assertEquals(request.getPageToken(), actualRequest.getPageToken());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listHmacKeysExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      ListHmacKeysRequest request =
          ListHmacKeysRequest.newBuilder()
              .setProjectId("projectId-894832108")
              .setServiceAccountEmail("serviceAccountEmail1825953988")
              .setShowDeletedKeys(true)
              .setMaxResults(1128457243)
              .setPageToken("pageToken873572522")
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.listHmacKeys(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateHmacKeyTest() throws Exception {
    HmacKeyMetadata expectedResponse =
        HmacKeyMetadata.newBuilder()
            .setId("id3355")
            .setAccessId("accessId-2146437729")
            .setProjectId("projectId-894832108")
            .setServiceAccountEmail("serviceAccountEmail1825953988")
            .setState("state109757585")
            .setTimeCreated(Timestamp.newBuilder().build())
            .setUpdated(Timestamp.newBuilder().build())
            .setEtag("etag3123477")
            .build();
    mockStorage.addResponse(expectedResponse);

    UpdateHmacKeyRequest request =
        UpdateHmacKeyRequest.newBuilder()
            .setAccessId("accessId-2146437729")
            .setProjectId("projectId-894832108")
            .setMetadata(HmacKeyMetadata.newBuilder().build())
            .setCommonRequestParams(CommonRequestParams.newBuilder().build())
            .build();

    HmacKeyMetadata actualResponse = client.updateHmacKey(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorage.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateHmacKeyRequest actualRequest = ((UpdateHmacKeyRequest) actualRequests.get(0));

    Assert.assertEquals(request.getAccessId(), actualRequest.getAccessId());
    Assert.assertEquals(request.getProjectId(), actualRequest.getProjectId());
    Assert.assertEquals(request.getMetadata(), actualRequest.getMetadata());
    Assert.assertEquals(request.getCommonRequestParams(), actualRequest.getCommonRequestParams());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateHmacKeyExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorage.addException(exception);

    try {
      UpdateHmacKeyRequest request =
          UpdateHmacKeyRequest.newBuilder()
              .setAccessId("accessId-2146437729")
              .setProjectId("projectId-894832108")
              .setMetadata(HmacKeyMetadata.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      client.updateHmacKey(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
