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

package com.google.storage.control.v2;

import static com.google.storage.control.v2.StorageControlClient.ListAnywhereCachesPagedResponse;
import static com.google.storage.control.v2.StorageControlClient.ListFoldersPagedResponse;
import static com.google.storage.control.v2.StorageControlClient.ListManagedFoldersPagedResponse;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.common.collect.Lists;
import com.google.longrunning.Operation;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Any;
import com.google.protobuf.Duration;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
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
public class StorageControlClientTest {
  private static MockServiceHelper mockServiceHelper;
  private static MockStorageControl mockStorageControl;
  private LocalChannelProvider channelProvider;
  private StorageControlClient client;

  @BeforeClass
  public static void startStaticServer() {
    mockStorageControl = new MockStorageControl();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockStorageControl));
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
    StorageControlSettings settings =
        StorageControlSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = StorageControlClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  public void createFolderTest() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
    Folder folder = Folder.newBuilder().build();
    String folderId = "folderId294109737";

    Folder actualResponse = client.createFolder(parent, folder, folderId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateFolderRequest actualRequest = ((CreateFolderRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertEquals(folder, actualRequest.getFolder());
    Assert.assertEquals(folderId, actualRequest.getFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      Folder folder = Folder.newBuilder().build();
      String folderId = "folderId294109737";
      client.createFolder(parent, folder, folderId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createFolderTest2() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String parent = "parent-995424086";
    Folder folder = Folder.newBuilder().build();
    String folderId = "folderId294109737";

    Folder actualResponse = client.createFolder(parent, folder, folderId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateFolderRequest actualRequest = ((CreateFolderRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertEquals(folder, actualRequest.getFolder());
    Assert.assertEquals(folderId, actualRequest.getFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      Folder folder = Folder.newBuilder().build();
      String folderId = "folderId294109737";
      client.createFolder(parent, folder, folderId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteFolderTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorageControl.addResponse(expectedResponse);

    FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");

    client.deleteFolder(name);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteFolderRequest actualRequest = ((DeleteFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
      client.deleteFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteFolderTest2() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    client.deleteFolder(name);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteFolderRequest actualRequest = ((DeleteFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.deleteFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getFolderTest() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");

    Folder actualResponse = client.getFolder(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetFolderRequest actualRequest = ((GetFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
      client.getFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getFolderTest2() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    Folder actualResponse = client.getFolder(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetFolderRequest actualRequest = ((GetFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.getFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listFoldersTest() throws Exception {
    Folder responsesElement = Folder.newBuilder().build();
    ListFoldersResponse expectedResponse =
        ListFoldersResponse.newBuilder()
            .setNextPageToken("")
            .addAllFolders(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");

    ListFoldersPagedResponse pagedListResponse = client.listFolders(parent);

    List<Folder> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getFoldersList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListFoldersRequest actualRequest = ((ListFoldersRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listFoldersExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      client.listFolders(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listFoldersTest2() throws Exception {
    Folder responsesElement = Folder.newBuilder().build();
    ListFoldersResponse expectedResponse =
        ListFoldersResponse.newBuilder()
            .setNextPageToken("")
            .addAllFolders(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListFoldersPagedResponse pagedListResponse = client.listFolders(parent);

    List<Folder> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getFoldersList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListFoldersRequest actualRequest = ((ListFoldersRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listFoldersExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listFolders(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void renameFolderTest() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("renameFolderTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockStorageControl.addResponse(resultOperation);

    FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
    String destinationFolderId = "destinationFolderId-480084905";

    Folder actualResponse = client.renameFolderAsync(name, destinationFolderId).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    RenameFolderRequest actualRequest = ((RenameFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertEquals(destinationFolderId, actualRequest.getDestinationFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void renameFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
      String destinationFolderId = "destinationFolderId-480084905";
      client.renameFolderAsync(name, destinationFolderId).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
      Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void renameFolderTest2() throws Exception {
    Folder expectedResponse =
        Folder.newBuilder()
            .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingRenameInfo(PendingRenameInfo.newBuilder().build())
            .build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("renameFolderTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockStorageControl.addResponse(resultOperation);

    String name = "name3373707";
    String destinationFolderId = "destinationFolderId-480084905";

    Folder actualResponse = client.renameFolderAsync(name, destinationFolderId).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    RenameFolderRequest actualRequest = ((RenameFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertEquals(destinationFolderId, actualRequest.getDestinationFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void renameFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      String destinationFolderId = "destinationFolderId-480084905";
      client.renameFolderAsync(name, destinationFolderId).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
      Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void getStorageLayoutTest() throws Exception {
    StorageLayout expectedResponse =
        StorageLayout.newBuilder()
            .setName(StorageLayoutName.of("[PROJECT]", "[BUCKET]").toString())
            .setLocation("location1901043637")
            .setLocationType("locationType-58277745")
            .setCustomPlacementConfig(StorageLayout.CustomPlacementConfig.newBuilder().build())
            .setHierarchicalNamespace(StorageLayout.HierarchicalNamespace.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    StorageLayoutName name = StorageLayoutName.of("[PROJECT]", "[BUCKET]");

    StorageLayout actualResponse = client.getStorageLayout(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetStorageLayoutRequest actualRequest = ((GetStorageLayoutRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getStorageLayoutExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      StorageLayoutName name = StorageLayoutName.of("[PROJECT]", "[BUCKET]");
      client.getStorageLayout(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getStorageLayoutTest2() throws Exception {
    StorageLayout expectedResponse =
        StorageLayout.newBuilder()
            .setName(StorageLayoutName.of("[PROJECT]", "[BUCKET]").toString())
            .setLocation("location1901043637")
            .setLocationType("locationType-58277745")
            .setCustomPlacementConfig(StorageLayout.CustomPlacementConfig.newBuilder().build())
            .setHierarchicalNamespace(StorageLayout.HierarchicalNamespace.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    StorageLayout actualResponse = client.getStorageLayout(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetStorageLayoutRequest actualRequest = ((GetStorageLayoutRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getStorageLayoutExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.getStorageLayout(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createManagedFolderTest() throws Exception {
    ManagedFolder expectedResponse =
        ManagedFolder.newBuilder()
            .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
    ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
    String managedFolderId = "managedFolderId-2027084056";

    ManagedFolder actualResponse =
        client.createManagedFolder(parent, managedFolder, managedFolderId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateManagedFolderRequest actualRequest = ((CreateManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertEquals(managedFolder, actualRequest.getManagedFolder());
    Assert.assertEquals(managedFolderId, actualRequest.getManagedFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createManagedFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
      String managedFolderId = "managedFolderId-2027084056";
      client.createManagedFolder(parent, managedFolder, managedFolderId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createManagedFolderTest2() throws Exception {
    ManagedFolder expectedResponse =
        ManagedFolder.newBuilder()
            .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String parent = "parent-995424086";
    ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
    String managedFolderId = "managedFolderId-2027084056";

    ManagedFolder actualResponse =
        client.createManagedFolder(parent, managedFolder, managedFolderId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateManagedFolderRequest actualRequest = ((CreateManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertEquals(managedFolder, actualRequest.getManagedFolder());
    Assert.assertEquals(managedFolderId, actualRequest.getManagedFolderId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createManagedFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
      String managedFolderId = "managedFolderId-2027084056";
      client.createManagedFolder(parent, managedFolder, managedFolderId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteManagedFolderTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorageControl.addResponse(expectedResponse);

    ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");

    client.deleteManagedFolder(name);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteManagedFolderRequest actualRequest = ((DeleteManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteManagedFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");
      client.deleteManagedFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteManagedFolderTest2() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    client.deleteManagedFolder(name);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteManagedFolderRequest actualRequest = ((DeleteManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteManagedFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.deleteManagedFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getManagedFolderTest() throws Exception {
    ManagedFolder expectedResponse =
        ManagedFolder.newBuilder()
            .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");

    ManagedFolder actualResponse = client.getManagedFolder(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetManagedFolderRequest actualRequest = ((GetManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getManagedFolderExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");
      client.getManagedFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getManagedFolderTest2() throws Exception {
    ManagedFolder expectedResponse =
        ManagedFolder.newBuilder()
            .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
            .setMetageneration(1048558813)
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    ManagedFolder actualResponse = client.getManagedFolder(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetManagedFolderRequest actualRequest = ((GetManagedFolderRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getManagedFolderExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.getManagedFolder(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listManagedFoldersTest() throws Exception {
    ManagedFolder responsesElement = ManagedFolder.newBuilder().build();
    ListManagedFoldersResponse expectedResponse =
        ListManagedFoldersResponse.newBuilder()
            .setNextPageToken("")
            .addAllManagedFolders(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");

    ListManagedFoldersPagedResponse pagedListResponse = client.listManagedFolders(parent);

    List<ManagedFolder> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getManagedFoldersList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListManagedFoldersRequest actualRequest = ((ListManagedFoldersRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listManagedFoldersExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      client.listManagedFolders(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listManagedFoldersTest2() throws Exception {
    ManagedFolder responsesElement = ManagedFolder.newBuilder().build();
    ListManagedFoldersResponse expectedResponse =
        ListManagedFoldersResponse.newBuilder()
            .setNextPageToken("")
            .addAllManagedFolders(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListManagedFoldersPagedResponse pagedListResponse = client.listManagedFolders(parent);

    List<ManagedFolder> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getManagedFoldersList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListManagedFoldersRequest actualRequest = ((ListManagedFoldersRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listManagedFoldersExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listManagedFolders(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("createAnywhereCacheTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockStorageControl.addResponse(resultOperation);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
    AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
    String anywhereCacheId = "anywhereCacheId-862998206";

    AnywhereCache actualResponse =
        client.createAnywhereCacheAsync(parent, anywhereCache, anywhereCacheId).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateAnywhereCacheRequest actualRequest = ((CreateAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertEquals(anywhereCache, actualRequest.getAnywhereCache());
    Assert.assertEquals(anywhereCacheId, actualRequest.getAnywhereCacheId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
      String anywhereCacheId = "anywhereCacheId-862998206";
      client.createAnywhereCacheAsync(parent, anywhereCache, anywhereCacheId).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
      Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void createAnywhereCacheTest2() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("createAnywhereCacheTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockStorageControl.addResponse(resultOperation);

    String parent = "parent-995424086";
    AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
    String anywhereCacheId = "anywhereCacheId-862998206";

    AnywhereCache actualResponse =
        client.createAnywhereCacheAsync(parent, anywhereCache, anywhereCacheId).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateAnywhereCacheRequest actualRequest = ((CreateAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertEquals(anywhereCache, actualRequest.getAnywhereCache());
    Assert.assertEquals(anywhereCacheId, actualRequest.getAnywhereCacheId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createAnywhereCacheExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
      String anywhereCacheId = "anywhereCacheId-862998206";
      client.createAnywhereCacheAsync(parent, anywhereCache, anywhereCacheId).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
      Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void updateAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("updateAnywhereCacheTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockStorageControl.addResponse(resultOperation);

    AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    AnywhereCache actualResponse = client.updateAnywhereCacheAsync(anywhereCache, updateMask).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateAnywhereCacheRequest actualRequest = ((UpdateAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(anywhereCache, actualRequest.getAnywhereCache());
    Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      AnywhereCache anywhereCache = AnywhereCache.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();
      client.updateAnywhereCacheAsync(anywhereCache, updateMask).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
      Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void disableAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");

    AnywhereCache actualResponse = client.disableAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DisableAnywhereCacheRequest actualRequest =
        ((DisableAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void disableAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");
      client.disableAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void disableAnywhereCacheTest2() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    AnywhereCache actualResponse = client.disableAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DisableAnywhereCacheRequest actualRequest =
        ((DisableAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void disableAnywhereCacheExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.disableAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void pauseAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");

    AnywhereCache actualResponse = client.pauseAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PauseAnywhereCacheRequest actualRequest = ((PauseAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void pauseAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");
      client.pauseAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void pauseAnywhereCacheTest2() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    AnywhereCache actualResponse = client.pauseAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    PauseAnywhereCacheRequest actualRequest = ((PauseAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void pauseAnywhereCacheExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.pauseAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void resumeAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");

    AnywhereCache actualResponse = client.resumeAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ResumeAnywhereCacheRequest actualRequest = ((ResumeAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void resumeAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");
      client.resumeAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void resumeAnywhereCacheTest2() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    AnywhereCache actualResponse = client.resumeAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ResumeAnywhereCacheRequest actualRequest = ((ResumeAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void resumeAnywhereCacheExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.resumeAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getAnywhereCacheTest() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");

    AnywhereCache actualResponse = client.getAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetAnywhereCacheRequest actualRequest = ((GetAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getAnywhereCacheExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      AnywhereCacheName name = AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]");
      client.getAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getAnywhereCacheTest2() throws Exception {
    AnywhereCache expectedResponse =
        AnywhereCache.newBuilder()
            .setName(AnywhereCacheName.of("[PROJECT]", "[BUCKET]", "[ANYWHERE_CACHE]").toString())
            .setZone("zone3744684")
            .setTtl(Duration.newBuilder().build())
            .setAdmissionPolicy("admissionPolicy-1063600485")
            .setState("state109757585")
            .setCreateTime(Timestamp.newBuilder().build())
            .setUpdateTime(Timestamp.newBuilder().build())
            .setPendingUpdate(true)
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String name = "name3373707";

    AnywhereCache actualResponse = client.getAnywhereCache(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetAnywhereCacheRequest actualRequest = ((GetAnywhereCacheRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getAnywhereCacheExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String name = "name3373707";
      client.getAnywhereCache(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listAnywhereCachesTest() throws Exception {
    AnywhereCache responsesElement = AnywhereCache.newBuilder().build();
    ListAnywhereCachesResponse expectedResponse =
        ListAnywhereCachesResponse.newBuilder()
            .setNextPageToken("")
            .addAllAnywhereCaches(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");

    ListAnywhereCachesPagedResponse pagedListResponse = client.listAnywhereCaches(parent);

    List<AnywhereCache> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getAnywhereCachesList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListAnywhereCachesRequest actualRequest = ((ListAnywhereCachesRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listAnywhereCachesExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
      client.listAnywhereCaches(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listAnywhereCachesTest2() throws Exception {
    AnywhereCache responsesElement = AnywhereCache.newBuilder().build();
    ListAnywhereCachesResponse expectedResponse =
        ListAnywhereCachesResponse.newBuilder()
            .setNextPageToken("")
            .addAllAnywhereCaches(Arrays.asList(responsesElement))
            .build();
    mockStorageControl.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListAnywhereCachesPagedResponse pagedListResponse = client.listAnywhereCaches(parent);

    List<AnywhereCache> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getAnywhereCachesList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockStorageControl.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListAnywhereCachesRequest actualRequest = ((ListAnywhereCachesRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listAnywhereCachesExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockStorageControl.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listAnywhereCaches(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
