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
package com.google.cloud.google.storage.v1.stub;

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ServerStreamingCallSettings;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StreamingCallSettings;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
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
import com.google.storage.v1.InsertObjectRequest;
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
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.threeten.bp.Duration;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Settings class to configure an instance of {@link StorageStub}.
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li>The default service address (storage.googleapis.com) and default port (443) are used.
 *   <li>Credentials are acquired automatically through Application Default Credentials.
 *   <li>Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 *
 * <p>The builder of this class is recursive, so contained classes are themselves builders. When
 * build() is called, the tree of builders is called to create the complete settings object.
 *
 * <p>For example, to set the total timeout of deleteBucketAccessControl to 30 seconds:
 *
 * <pre>
 * <code>
 * StorageStubSettings.Builder storageSettingsBuilder =
 *     StorageStubSettings.newBuilder();
 * storageSettingsBuilder
 *     .deleteBucketAccessControlSettings()
 *     .setRetrySettings(
 *         storageSettingsBuilder.deleteBucketAccessControlSettings().getRetrySettings().toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * StorageStubSettings storageSettings = storageSettingsBuilder.build();
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class StorageStubSettings extends StubSettings<StorageStubSettings> {
  /** The default scopes of the service. */
  private static final ImmutableList<String> DEFAULT_SERVICE_SCOPES =
      ImmutableList.<String>builder()
          .add("https://www.googleapis.com/auth/cloud-platform")
          .add("https://www.googleapis.com/auth/cloud-platform.read-only")
          .add("https://www.googleapis.com/auth/devstorage.full_control")
          .add("https://www.googleapis.com/auth/devstorage.read_only")
          .add("https://www.googleapis.com/auth/devstorage.read_write")
          .build();

  private final UnaryCallSettings<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlSettings;
  private final UnaryCallSettings<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlSettings;
  private final UnaryCallSettings<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlSettings;
  private final UnaryCallSettings<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsSettings;
  private final UnaryCallSettings<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlSettings;
  private final UnaryCallSettings<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlSettings;
  private final UnaryCallSettings<DeleteBucketRequest, Empty> deleteBucketSettings;
  private final UnaryCallSettings<GetBucketRequest, Bucket> getBucketSettings;
  private final UnaryCallSettings<InsertBucketRequest, Bucket> insertBucketSettings;
  private final UnaryCallSettings<ListChannelsRequest, ListChannelsResponse> listChannelsSettings;
  private final UnaryCallSettings<ListBucketsRequest, ListBucketsResponse> listBucketsSettings;
  private final UnaryCallSettings<LockRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicySettings;
  private final UnaryCallSettings<GetIamPolicyRequest, Policy> getBucketIamPolicySettings;
  private final UnaryCallSettings<SetIamPolicyRequest, Policy> setBucketIamPolicySettings;
  private final UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsSettings;
  private final UnaryCallSettings<PatchBucketRequest, Bucket> patchBucketSettings;
  private final UnaryCallSettings<UpdateBucketRequest, Bucket> updateBucketSettings;
  private final UnaryCallSettings<StopChannelRequest, Empty> stopChannelSettings;
  private final UnaryCallSettings<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlSettings;
  private final UnaryCallSettings<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlSettings;
  private final UnaryCallSettings<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlSettings;
  private final UnaryCallSettings<
          ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsSettings;
  private final UnaryCallSettings<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlSettings;
  private final UnaryCallSettings<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlSettings;
  private final UnaryCallSettings<DeleteNotificationRequest, Empty> deleteNotificationSettings;
  private final UnaryCallSettings<GetNotificationRequest, Notification> getNotificationSettings;
  private final UnaryCallSettings<InsertNotificationRequest, Notification>
      insertNotificationSettings;
  private final UnaryCallSettings<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsSettings;
  private final UnaryCallSettings<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlSettings;
  private final UnaryCallSettings<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlSettings;
  private final UnaryCallSettings<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlSettings;
  private final UnaryCallSettings<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsSettings;
  private final UnaryCallSettings<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlSettings;
  private final UnaryCallSettings<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlSettings;
  private final UnaryCallSettings<ComposeObjectRequest, com.google.storage.v1.Object>
      composeObjectSettings;
  private final UnaryCallSettings<CopyObjectRequest, com.google.storage.v1.Object>
      copyObjectSettings;
  private final UnaryCallSettings<DeleteObjectRequest, Empty> deleteObjectSettings;
  private final UnaryCallSettings<GetObjectRequest, com.google.storage.v1.Object> getObjectSettings;
  private final ServerStreamingCallSettings<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaSettings;
  private final StreamingCallSettings<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectSettings;
  private final UnaryCallSettings<ListObjectsRequest, ListObjectsResponse> listObjectsSettings;
  private final UnaryCallSettings<RewriteObjectRequest, RewriteResponse> rewriteObjectSettings;
  private final UnaryCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteSettings;
  private final UnaryCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusSettings;
  private final UnaryCallSettings<PatchObjectRequest, com.google.storage.v1.Object>
      patchObjectSettings;
  private final UnaryCallSettings<UpdateObjectRequest, com.google.storage.v1.Object>
      updateObjectSettings;
  private final UnaryCallSettings<GetIamPolicyRequest, Policy> getObjectIamPolicySettings;
  private final UnaryCallSettings<SetIamPolicyRequest, Policy> setObjectIamPolicySettings;
  private final UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsSettings;
  private final UnaryCallSettings<WatchAllObjectsRequest, Channel> watchAllObjectsSettings;
  private final UnaryCallSettings<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountSettings;
  private final UnaryCallSettings<CreateHmacKeyRequest, CreateHmacKeyResponse>
      createHmacKeySettings;
  private final UnaryCallSettings<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings;
  private final UnaryCallSettings<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings;
  private final UnaryCallSettings<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysSettings;
  private final UnaryCallSettings<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeySettings;

  /** Returns the object with the settings used for calls to deleteBucketAccessControl. */
  public UnaryCallSettings<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlSettings() {
    return deleteBucketAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to getBucketAccessControl. */
  public UnaryCallSettings<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlSettings() {
    return getBucketAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to insertBucketAccessControl. */
  public UnaryCallSettings<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlSettings() {
    return insertBucketAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to listBucketAccessControls. */
  public UnaryCallSettings<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsSettings() {
    return listBucketAccessControlsSettings;
  }

  /** Returns the object with the settings used for calls to updateBucketAccessControl. */
  public UnaryCallSettings<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlSettings() {
    return updateBucketAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to patchBucketAccessControl. */
  public UnaryCallSettings<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlSettings() {
    return patchBucketAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to deleteBucket. */
  public UnaryCallSettings<DeleteBucketRequest, Empty> deleteBucketSettings() {
    return deleteBucketSettings;
  }

  /** Returns the object with the settings used for calls to getBucket. */
  public UnaryCallSettings<GetBucketRequest, Bucket> getBucketSettings() {
    return getBucketSettings;
  }

  /** Returns the object with the settings used for calls to insertBucket. */
  public UnaryCallSettings<InsertBucketRequest, Bucket> insertBucketSettings() {
    return insertBucketSettings;
  }

  /** Returns the object with the settings used for calls to listChannels. */
  public UnaryCallSettings<ListChannelsRequest, ListChannelsResponse> listChannelsSettings() {
    return listChannelsSettings;
  }

  /** Returns the object with the settings used for calls to listBuckets. */
  public UnaryCallSettings<ListBucketsRequest, ListBucketsResponse> listBucketsSettings() {
    return listBucketsSettings;
  }

  /** Returns the object with the settings used for calls to lockBucketRetentionPolicy. */
  public UnaryCallSettings<LockRetentionPolicyRequest, Bucket> lockBucketRetentionPolicySettings() {
    return lockBucketRetentionPolicySettings;
  }

  /** Returns the object with the settings used for calls to getBucketIamPolicy. */
  public UnaryCallSettings<GetIamPolicyRequest, Policy> getBucketIamPolicySettings() {
    return getBucketIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to setBucketIamPolicy. */
  public UnaryCallSettings<SetIamPolicyRequest, Policy> setBucketIamPolicySettings() {
    return setBucketIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to testBucketIamPermissions. */
  public UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsSettings() {
    return testBucketIamPermissionsSettings;
  }

  /** Returns the object with the settings used for calls to patchBucket. */
  public UnaryCallSettings<PatchBucketRequest, Bucket> patchBucketSettings() {
    return patchBucketSettings;
  }

  /** Returns the object with the settings used for calls to updateBucket. */
  public UnaryCallSettings<UpdateBucketRequest, Bucket> updateBucketSettings() {
    return updateBucketSettings;
  }

  /** Returns the object with the settings used for calls to stopChannel. */
  public UnaryCallSettings<StopChannelRequest, Empty> stopChannelSettings() {
    return stopChannelSettings;
  }

  /** Returns the object with the settings used for calls to deleteDefaultObjectAccessControl. */
  public UnaryCallSettings<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlSettings() {
    return deleteDefaultObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to getDefaultObjectAccessControl. */
  public UnaryCallSettings<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlSettings() {
    return getDefaultObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to insertDefaultObjectAccessControl. */
  public UnaryCallSettings<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlSettings() {
    return insertDefaultObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to listDefaultObjectAccessControls. */
  public UnaryCallSettings<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsSettings() {
    return listDefaultObjectAccessControlsSettings;
  }

  /** Returns the object with the settings used for calls to patchDefaultObjectAccessControl. */
  public UnaryCallSettings<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlSettings() {
    return patchDefaultObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to updateDefaultObjectAccessControl. */
  public UnaryCallSettings<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlSettings() {
    return updateDefaultObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to deleteNotification. */
  public UnaryCallSettings<DeleteNotificationRequest, Empty> deleteNotificationSettings() {
    return deleteNotificationSettings;
  }

  /** Returns the object with the settings used for calls to getNotification. */
  public UnaryCallSettings<GetNotificationRequest, Notification> getNotificationSettings() {
    return getNotificationSettings;
  }

  /** Returns the object with the settings used for calls to insertNotification. */
  public UnaryCallSettings<InsertNotificationRequest, Notification> insertNotificationSettings() {
    return insertNotificationSettings;
  }

  /** Returns the object with the settings used for calls to listNotifications. */
  public UnaryCallSettings<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsSettings() {
    return listNotificationsSettings;
  }

  /** Returns the object with the settings used for calls to deleteObjectAccessControl. */
  public UnaryCallSettings<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlSettings() {
    return deleteObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to getObjectAccessControl. */
  public UnaryCallSettings<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlSettings() {
    return getObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to insertObjectAccessControl. */
  public UnaryCallSettings<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlSettings() {
    return insertObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to listObjectAccessControls. */
  public UnaryCallSettings<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsSettings() {
    return listObjectAccessControlsSettings;
  }

  /** Returns the object with the settings used for calls to patchObjectAccessControl. */
  public UnaryCallSettings<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlSettings() {
    return patchObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to updateObjectAccessControl. */
  public UnaryCallSettings<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlSettings() {
    return updateObjectAccessControlSettings;
  }

  /** Returns the object with the settings used for calls to composeObject. */
  public UnaryCallSettings<ComposeObjectRequest, com.google.storage.v1.Object>
      composeObjectSettings() {
    return composeObjectSettings;
  }

  /** Returns the object with the settings used for calls to copyObject. */
  public UnaryCallSettings<CopyObjectRequest, com.google.storage.v1.Object> copyObjectSettings() {
    return copyObjectSettings;
  }

  /** Returns the object with the settings used for calls to deleteObject. */
  public UnaryCallSettings<DeleteObjectRequest, Empty> deleteObjectSettings() {
    return deleteObjectSettings;
  }

  /** Returns the object with the settings used for calls to getObject. */
  public UnaryCallSettings<GetObjectRequest, com.google.storage.v1.Object> getObjectSettings() {
    return getObjectSettings;
  }

  /** Returns the object with the settings used for calls to getObjectMedia. */
  public ServerStreamingCallSettings<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaSettings() {
    return getObjectMediaSettings;
  }

  /** Returns the object with the settings used for calls to insertObject. */
  public StreamingCallSettings<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectSettings() {
    return insertObjectSettings;
  }

  /** Returns the object with the settings used for calls to listObjects. */
  public UnaryCallSettings<ListObjectsRequest, ListObjectsResponse> listObjectsSettings() {
    return listObjectsSettings;
  }

  /** Returns the object with the settings used for calls to rewriteObject. */
  public UnaryCallSettings<RewriteObjectRequest, RewriteResponse> rewriteObjectSettings() {
    return rewriteObjectSettings;
  }

  /** Returns the object with the settings used for calls to startResumableWrite. */
  public UnaryCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteSettings() {
    return startResumableWriteSettings;
  }

  /** Returns the object with the settings used for calls to queryWriteStatus. */
  public UnaryCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusSettings() {
    return queryWriteStatusSettings;
  }

  /** Returns the object with the settings used for calls to patchObject. */
  public UnaryCallSettings<PatchObjectRequest, com.google.storage.v1.Object> patchObjectSettings() {
    return patchObjectSettings;
  }

  /** Returns the object with the settings used for calls to updateObject. */
  public UnaryCallSettings<UpdateObjectRequest, com.google.storage.v1.Object>
      updateObjectSettings() {
    return updateObjectSettings;
  }

  /** Returns the object with the settings used for calls to getObjectIamPolicy. */
  public UnaryCallSettings<GetIamPolicyRequest, Policy> getObjectIamPolicySettings() {
    return getObjectIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to setObjectIamPolicy. */
  public UnaryCallSettings<SetIamPolicyRequest, Policy> setObjectIamPolicySettings() {
    return setObjectIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to testObjectIamPermissions. */
  public UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsSettings() {
    return testObjectIamPermissionsSettings;
  }

  /** Returns the object with the settings used for calls to watchAllObjects. */
  public UnaryCallSettings<WatchAllObjectsRequest, Channel> watchAllObjectsSettings() {
    return watchAllObjectsSettings;
  }

  /** Returns the object with the settings used for calls to getServiceAccount. */
  public UnaryCallSettings<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountSettings() {
    return getServiceAccountSettings;
  }

  /** Returns the object with the settings used for calls to createHmacKey. */
  public UnaryCallSettings<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeySettings() {
    return createHmacKeySettings;
  }

  /** Returns the object with the settings used for calls to deleteHmacKey. */
  public UnaryCallSettings<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings() {
    return deleteHmacKeySettings;
  }

  /** Returns the object with the settings used for calls to getHmacKey. */
  public UnaryCallSettings<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings() {
    return getHmacKeySettings;
  }

  /** Returns the object with the settings used for calls to listHmacKeys. */
  public UnaryCallSettings<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysSettings() {
    return listHmacKeysSettings;
  }

  /** Returns the object with the settings used for calls to updateHmacKey. */
  public UnaryCallSettings<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeySettings() {
    return updateHmacKeySettings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public StorageStub createStub() throws IOException {
    if (getTransportChannelProvider()
        .getTransportName()
        .equals(GrpcTransportChannel.getGrpcTransportName())) {
      return GrpcStorageStub.create(this);
    } else {
      throw new UnsupportedOperationException(
          "Transport not supported: " + getTransportChannelProvider().getTransportName());
    }
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return InstantiatingExecutorProvider.newBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return "storage.googleapis.com:443";
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return DEFAULT_SERVICE_SCOPES;
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return GoogleCredentialsProvider.newBuilder().setScopesToApply(DEFAULT_SERVICE_SCOPES);
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return InstantiatingGrpcChannelProvider.newBuilder()
        .setMaxInboundMessageSize(Integer.MAX_VALUE);
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return defaultGrpcTransportProviderBuilder().build();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return ApiClientHeaderProvider.newBuilder()
        .setGeneratedLibToken("gapic", GaxProperties.getLibraryVersion(StorageStubSettings.class))
        .setTransportToken(
            GaxGrpcProperties.getGrpcTokenName(), GaxGrpcProperties.getGrpcVersion());
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder() {
    return Builder.createDefault();
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder(ClientContext clientContext) {
    return new Builder(clientContext);
  }

  /** Returns a builder containing all the values of this settings class. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  protected StorageStubSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);

    deleteBucketAccessControlSettings = settingsBuilder.deleteBucketAccessControlSettings().build();
    getBucketAccessControlSettings = settingsBuilder.getBucketAccessControlSettings().build();
    insertBucketAccessControlSettings = settingsBuilder.insertBucketAccessControlSettings().build();
    listBucketAccessControlsSettings = settingsBuilder.listBucketAccessControlsSettings().build();
    updateBucketAccessControlSettings = settingsBuilder.updateBucketAccessControlSettings().build();
    patchBucketAccessControlSettings = settingsBuilder.patchBucketAccessControlSettings().build();
    deleteBucketSettings = settingsBuilder.deleteBucketSettings().build();
    getBucketSettings = settingsBuilder.getBucketSettings().build();
    insertBucketSettings = settingsBuilder.insertBucketSettings().build();
    listChannelsSettings = settingsBuilder.listChannelsSettings().build();
    listBucketsSettings = settingsBuilder.listBucketsSettings().build();
    lockBucketRetentionPolicySettings = settingsBuilder.lockBucketRetentionPolicySettings().build();
    getBucketIamPolicySettings = settingsBuilder.getBucketIamPolicySettings().build();
    setBucketIamPolicySettings = settingsBuilder.setBucketIamPolicySettings().build();
    testBucketIamPermissionsSettings = settingsBuilder.testBucketIamPermissionsSettings().build();
    patchBucketSettings = settingsBuilder.patchBucketSettings().build();
    updateBucketSettings = settingsBuilder.updateBucketSettings().build();
    stopChannelSettings = settingsBuilder.stopChannelSettings().build();
    deleteDefaultObjectAccessControlSettings =
        settingsBuilder.deleteDefaultObjectAccessControlSettings().build();
    getDefaultObjectAccessControlSettings =
        settingsBuilder.getDefaultObjectAccessControlSettings().build();
    insertDefaultObjectAccessControlSettings =
        settingsBuilder.insertDefaultObjectAccessControlSettings().build();
    listDefaultObjectAccessControlsSettings =
        settingsBuilder.listDefaultObjectAccessControlsSettings().build();
    patchDefaultObjectAccessControlSettings =
        settingsBuilder.patchDefaultObjectAccessControlSettings().build();
    updateDefaultObjectAccessControlSettings =
        settingsBuilder.updateDefaultObjectAccessControlSettings().build();
    deleteNotificationSettings = settingsBuilder.deleteNotificationSettings().build();
    getNotificationSettings = settingsBuilder.getNotificationSettings().build();
    insertNotificationSettings = settingsBuilder.insertNotificationSettings().build();
    listNotificationsSettings = settingsBuilder.listNotificationsSettings().build();
    deleteObjectAccessControlSettings = settingsBuilder.deleteObjectAccessControlSettings().build();
    getObjectAccessControlSettings = settingsBuilder.getObjectAccessControlSettings().build();
    insertObjectAccessControlSettings = settingsBuilder.insertObjectAccessControlSettings().build();
    listObjectAccessControlsSettings = settingsBuilder.listObjectAccessControlsSettings().build();
    patchObjectAccessControlSettings = settingsBuilder.patchObjectAccessControlSettings().build();
    updateObjectAccessControlSettings = settingsBuilder.updateObjectAccessControlSettings().build();
    composeObjectSettings = settingsBuilder.composeObjectSettings().build();
    copyObjectSettings = settingsBuilder.copyObjectSettings().build();
    deleteObjectSettings = settingsBuilder.deleteObjectSettings().build();
    getObjectSettings = settingsBuilder.getObjectSettings().build();
    getObjectMediaSettings = settingsBuilder.getObjectMediaSettings().build();
    insertObjectSettings = settingsBuilder.insertObjectSettings().build();
    listObjectsSettings = settingsBuilder.listObjectsSettings().build();
    rewriteObjectSettings = settingsBuilder.rewriteObjectSettings().build();
    startResumableWriteSettings = settingsBuilder.startResumableWriteSettings().build();
    queryWriteStatusSettings = settingsBuilder.queryWriteStatusSettings().build();
    patchObjectSettings = settingsBuilder.patchObjectSettings().build();
    updateObjectSettings = settingsBuilder.updateObjectSettings().build();
    getObjectIamPolicySettings = settingsBuilder.getObjectIamPolicySettings().build();
    setObjectIamPolicySettings = settingsBuilder.setObjectIamPolicySettings().build();
    testObjectIamPermissionsSettings = settingsBuilder.testObjectIamPermissionsSettings().build();
    watchAllObjectsSettings = settingsBuilder.watchAllObjectsSettings().build();
    getServiceAccountSettings = settingsBuilder.getServiceAccountSettings().build();
    createHmacKeySettings = settingsBuilder.createHmacKeySettings().build();
    deleteHmacKeySettings = settingsBuilder.deleteHmacKeySettings().build();
    getHmacKeySettings = settingsBuilder.getHmacKeySettings().build();
    listHmacKeysSettings = settingsBuilder.listHmacKeysSettings().build();
    updateHmacKeySettings = settingsBuilder.updateHmacKeySettings().build();
  }

  /** Builder for StorageStubSettings. */
  public static class Builder extends StubSettings.Builder<StorageStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;

    private final UnaryCallSettings.Builder<DeleteBucketAccessControlRequest, Empty>
        deleteBucketAccessControlSettings;
    private final UnaryCallSettings.Builder<GetBucketAccessControlRequest, BucketAccessControl>
        getBucketAccessControlSettings;
    private final UnaryCallSettings.Builder<InsertBucketAccessControlRequest, BucketAccessControl>
        insertBucketAccessControlSettings;
    private final UnaryCallSettings.Builder<
            ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
        listBucketAccessControlsSettings;
    private final UnaryCallSettings.Builder<UpdateBucketAccessControlRequest, BucketAccessControl>
        updateBucketAccessControlSettings;
    private final UnaryCallSettings.Builder<PatchBucketAccessControlRequest, BucketAccessControl>
        patchBucketAccessControlSettings;
    private final UnaryCallSettings.Builder<DeleteBucketRequest, Empty> deleteBucketSettings;
    private final UnaryCallSettings.Builder<GetBucketRequest, Bucket> getBucketSettings;
    private final UnaryCallSettings.Builder<InsertBucketRequest, Bucket> insertBucketSettings;
    private final UnaryCallSettings.Builder<ListChannelsRequest, ListChannelsResponse>
        listChannelsSettings;
    private final UnaryCallSettings.Builder<ListBucketsRequest, ListBucketsResponse>
        listBucketsSettings;
    private final UnaryCallSettings.Builder<LockRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicySettings;
    private final UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getBucketIamPolicySettings;
    private final UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setBucketIamPolicySettings;
    private final UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testBucketIamPermissionsSettings;
    private final UnaryCallSettings.Builder<PatchBucketRequest, Bucket> patchBucketSettings;
    private final UnaryCallSettings.Builder<UpdateBucketRequest, Bucket> updateBucketSettings;
    private final UnaryCallSettings.Builder<StopChannelRequest, Empty> stopChannelSettings;
    private final UnaryCallSettings.Builder<DeleteDefaultObjectAccessControlRequest, Empty>
        deleteDefaultObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<
            GetDefaultObjectAccessControlRequest, ObjectAccessControl>
        getDefaultObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<
            InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
        insertDefaultObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<
            ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listDefaultObjectAccessControlsSettings;
    private final UnaryCallSettings.Builder<
            PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
        patchDefaultObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<
            UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
        updateDefaultObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<DeleteNotificationRequest, Empty>
        deleteNotificationSettings;
    private final UnaryCallSettings.Builder<GetNotificationRequest, Notification>
        getNotificationSettings;
    private final UnaryCallSettings.Builder<InsertNotificationRequest, Notification>
        insertNotificationSettings;
    private final UnaryCallSettings.Builder<ListNotificationsRequest, ListNotificationsResponse>
        listNotificationsSettings;
    private final UnaryCallSettings.Builder<DeleteObjectAccessControlRequest, Empty>
        deleteObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<GetObjectAccessControlRequest, ObjectAccessControl>
        getObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<InsertObjectAccessControlRequest, ObjectAccessControl>
        insertObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<
            ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listObjectAccessControlsSettings;
    private final UnaryCallSettings.Builder<PatchObjectAccessControlRequest, ObjectAccessControl>
        patchObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<UpdateObjectAccessControlRequest, ObjectAccessControl>
        updateObjectAccessControlSettings;
    private final UnaryCallSettings.Builder<ComposeObjectRequest, com.google.storage.v1.Object>
        composeObjectSettings;
    private final UnaryCallSettings.Builder<CopyObjectRequest, com.google.storage.v1.Object>
        copyObjectSettings;
    private final UnaryCallSettings.Builder<DeleteObjectRequest, Empty> deleteObjectSettings;
    private final UnaryCallSettings.Builder<GetObjectRequest, com.google.storage.v1.Object>
        getObjectSettings;
    private final ServerStreamingCallSettings.Builder<GetObjectMediaRequest, GetObjectMediaResponse>
        getObjectMediaSettings;
    private final StreamingCallSettings.Builder<InsertObjectRequest, com.google.storage.v1.Object>
        insertObjectSettings;
    private final UnaryCallSettings.Builder<ListObjectsRequest, ListObjectsResponse>
        listObjectsSettings;
    private final UnaryCallSettings.Builder<RewriteObjectRequest, RewriteResponse>
        rewriteObjectSettings;
    private final UnaryCallSettings.Builder<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteSettings;
    private final UnaryCallSettings.Builder<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusSettings;
    private final UnaryCallSettings.Builder<PatchObjectRequest, com.google.storage.v1.Object>
        patchObjectSettings;
    private final UnaryCallSettings.Builder<UpdateObjectRequest, com.google.storage.v1.Object>
        updateObjectSettings;
    private final UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getObjectIamPolicySettings;
    private final UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setObjectIamPolicySettings;
    private final UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testObjectIamPermissionsSettings;
    private final UnaryCallSettings.Builder<WatchAllObjectsRequest, Channel>
        watchAllObjectsSettings;
    private final UnaryCallSettings.Builder<GetProjectServiceAccountRequest, ServiceAccount>
        getServiceAccountSettings;
    private final UnaryCallSettings.Builder<CreateHmacKeyRequest, CreateHmacKeyResponse>
        createHmacKeySettings;
    private final UnaryCallSettings.Builder<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings;
    private final UnaryCallSettings.Builder<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings;
    private final UnaryCallSettings.Builder<ListHmacKeysRequest, ListHmacKeysResponse>
        listHmacKeysSettings;
    private final UnaryCallSettings.Builder<UpdateHmacKeyRequest, HmacKeyMetadata>
        updateHmacKeySettings;

    private static final ImmutableMap<String, ImmutableSet<StatusCode.Code>>
        RETRYABLE_CODE_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, ImmutableSet<StatusCode.Code>> definitions =
          ImmutableMap.builder();
      definitions.put(
          "idempotent",
          ImmutableSet.copyOf(
              Lists.<StatusCode.Code>newArrayList(
                  StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)));
      definitions.put("non_idempotent", ImmutableSet.copyOf(Lists.<StatusCode.Code>newArrayList()));
      RETRYABLE_CODE_DEFINITIONS = definitions.build();
    }

    private static final ImmutableMap<String, RetrySettings> RETRY_PARAM_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, RetrySettings> definitions = ImmutableMap.builder();
      RetrySettings settings = null;
      settings =
          RetrySettings.newBuilder()
              .setInitialRetryDelay(Duration.ofMillis(100L))
              .setRetryDelayMultiplier(1.3)
              .setMaxRetryDelay(Duration.ofMillis(60000L))
              .setInitialRpcTimeout(Duration.ofMillis(20000L))
              .setRpcTimeoutMultiplier(1.0)
              .setMaxRpcTimeout(Duration.ofMillis(20000L))
              .setTotalTimeout(Duration.ofMillis(600000L))
              .build();
      definitions.put("default", settings);
      RETRY_PARAM_DEFINITIONS = definitions.build();
    }

    protected Builder() {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(clientContext);

      deleteBucketAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getBucketAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      insertBucketAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listBucketAccessControlsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateBucketAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      patchBucketAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      insertBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listChannelsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listBucketsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      lockBucketRetentionPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getBucketIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      setBucketIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      testBucketIamPermissionsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      patchBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      stopChannelSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteDefaultObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getDefaultObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      insertDefaultObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listDefaultObjectAccessControlsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      patchDefaultObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateDefaultObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteNotificationSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getNotificationSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      insertNotificationSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listNotificationsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      insertObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listObjectAccessControlsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      patchObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateObjectAccessControlSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      composeObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      copyObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getObjectMediaSettings = ServerStreamingCallSettings.newBuilder();

      insertObjectSettings = StreamingCallSettings.newBuilder();

      listObjectsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      rewriteObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      startResumableWriteSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      queryWriteStatusSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      patchObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getObjectIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      setObjectIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      testObjectIamPermissionsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      watchAllObjectsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getServiceAccountSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      createHmacKeySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      deleteHmacKeySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      getHmacKeySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listHmacKeysSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      updateHmacKeySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              deleteBucketAccessControlSettings,
              getBucketAccessControlSettings,
              insertBucketAccessControlSettings,
              listBucketAccessControlsSettings,
              updateBucketAccessControlSettings,
              patchBucketAccessControlSettings,
              deleteBucketSettings,
              getBucketSettings,
              insertBucketSettings,
              listChannelsSettings,
              listBucketsSettings,
              lockBucketRetentionPolicySettings,
              getBucketIamPolicySettings,
              setBucketIamPolicySettings,
              testBucketIamPermissionsSettings,
              patchBucketSettings,
              updateBucketSettings,
              stopChannelSettings,
              deleteDefaultObjectAccessControlSettings,
              getDefaultObjectAccessControlSettings,
              insertDefaultObjectAccessControlSettings,
              listDefaultObjectAccessControlsSettings,
              patchDefaultObjectAccessControlSettings,
              updateDefaultObjectAccessControlSettings,
              deleteNotificationSettings,
              getNotificationSettings,
              insertNotificationSettings,
              listNotificationsSettings,
              deleteObjectAccessControlSettings,
              getObjectAccessControlSettings,
              insertObjectAccessControlSettings,
              listObjectAccessControlsSettings,
              patchObjectAccessControlSettings,
              updateObjectAccessControlSettings,
              composeObjectSettings,
              copyObjectSettings,
              deleteObjectSettings,
              getObjectSettings,
              listObjectsSettings,
              rewriteObjectSettings,
              startResumableWriteSettings,
              queryWriteStatusSettings,
              patchObjectSettings,
              updateObjectSettings,
              getObjectIamPolicySettings,
              setObjectIamPolicySettings,
              testObjectIamPermissionsSettings,
              watchAllObjectsSettings,
              getServiceAccountSettings,
              createHmacKeySettings,
              deleteHmacKeySettings,
              getHmacKeySettings,
              listHmacKeysSettings,
              updateHmacKeySettings);

      initDefaults(this);
    }

    private static Builder createDefault() {
      Builder builder = new Builder((ClientContext) null);
      builder.setTransportChannelProvider(defaultTransportChannelProvider());
      builder.setCredentialsProvider(defaultCredentialsProviderBuilder().build());
      builder.setInternalHeaderProvider(defaultApiClientHeaderProviderBuilder().build());
      builder.setEndpoint(getDefaultEndpoint());
      return initDefaults(builder);
    }

    private static Builder initDefaults(Builder builder) {

      builder
          .deleteBucketAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getBucketAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .insertBucketAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listBucketAccessControlsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateBucketAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .patchBucketAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .insertBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listChannelsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listBucketsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .lockBucketRetentionPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getBucketIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .setBucketIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .testBucketIamPermissionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .patchBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .stopChannelSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteDefaultObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getDefaultObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .insertDefaultObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listDefaultObjectAccessControlsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .patchDefaultObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateDefaultObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteNotificationSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getNotificationSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .insertNotificationSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listNotificationsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .insertObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listObjectAccessControlsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .patchObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateObjectAccessControlSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .composeObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .copyObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getObjectMediaSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listObjectsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .rewriteObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .startResumableWriteSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .queryWriteStatusSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .patchObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getObjectIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .setObjectIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .testObjectIamPermissionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .watchAllObjectsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getServiceAccountSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .createHmacKeySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .deleteHmacKeySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .getHmacKeySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .listHmacKeysSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      builder
          .updateHmacKeySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("non_idempotent"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("default"));

      return builder;
    }

    protected Builder(StorageStubSettings settings) {
      super(settings);

      deleteBucketAccessControlSettings = settings.deleteBucketAccessControlSettings.toBuilder();
      getBucketAccessControlSettings = settings.getBucketAccessControlSettings.toBuilder();
      insertBucketAccessControlSettings = settings.insertBucketAccessControlSettings.toBuilder();
      listBucketAccessControlsSettings = settings.listBucketAccessControlsSettings.toBuilder();
      updateBucketAccessControlSettings = settings.updateBucketAccessControlSettings.toBuilder();
      patchBucketAccessControlSettings = settings.patchBucketAccessControlSettings.toBuilder();
      deleteBucketSettings = settings.deleteBucketSettings.toBuilder();
      getBucketSettings = settings.getBucketSettings.toBuilder();
      insertBucketSettings = settings.insertBucketSettings.toBuilder();
      listChannelsSettings = settings.listChannelsSettings.toBuilder();
      listBucketsSettings = settings.listBucketsSettings.toBuilder();
      lockBucketRetentionPolicySettings = settings.lockBucketRetentionPolicySettings.toBuilder();
      getBucketIamPolicySettings = settings.getBucketIamPolicySettings.toBuilder();
      setBucketIamPolicySettings = settings.setBucketIamPolicySettings.toBuilder();
      testBucketIamPermissionsSettings = settings.testBucketIamPermissionsSettings.toBuilder();
      patchBucketSettings = settings.patchBucketSettings.toBuilder();
      updateBucketSettings = settings.updateBucketSettings.toBuilder();
      stopChannelSettings = settings.stopChannelSettings.toBuilder();
      deleteDefaultObjectAccessControlSettings =
          settings.deleteDefaultObjectAccessControlSettings.toBuilder();
      getDefaultObjectAccessControlSettings =
          settings.getDefaultObjectAccessControlSettings.toBuilder();
      insertDefaultObjectAccessControlSettings =
          settings.insertDefaultObjectAccessControlSettings.toBuilder();
      listDefaultObjectAccessControlsSettings =
          settings.listDefaultObjectAccessControlsSettings.toBuilder();
      patchDefaultObjectAccessControlSettings =
          settings.patchDefaultObjectAccessControlSettings.toBuilder();
      updateDefaultObjectAccessControlSettings =
          settings.updateDefaultObjectAccessControlSettings.toBuilder();
      deleteNotificationSettings = settings.deleteNotificationSettings.toBuilder();
      getNotificationSettings = settings.getNotificationSettings.toBuilder();
      insertNotificationSettings = settings.insertNotificationSettings.toBuilder();
      listNotificationsSettings = settings.listNotificationsSettings.toBuilder();
      deleteObjectAccessControlSettings = settings.deleteObjectAccessControlSettings.toBuilder();
      getObjectAccessControlSettings = settings.getObjectAccessControlSettings.toBuilder();
      insertObjectAccessControlSettings = settings.insertObjectAccessControlSettings.toBuilder();
      listObjectAccessControlsSettings = settings.listObjectAccessControlsSettings.toBuilder();
      patchObjectAccessControlSettings = settings.patchObjectAccessControlSettings.toBuilder();
      updateObjectAccessControlSettings = settings.updateObjectAccessControlSettings.toBuilder();
      composeObjectSettings = settings.composeObjectSettings.toBuilder();
      copyObjectSettings = settings.copyObjectSettings.toBuilder();
      deleteObjectSettings = settings.deleteObjectSettings.toBuilder();
      getObjectSettings = settings.getObjectSettings.toBuilder();
      getObjectMediaSettings = settings.getObjectMediaSettings.toBuilder();
      insertObjectSettings = settings.insertObjectSettings.toBuilder();
      listObjectsSettings = settings.listObjectsSettings.toBuilder();
      rewriteObjectSettings = settings.rewriteObjectSettings.toBuilder();
      startResumableWriteSettings = settings.startResumableWriteSettings.toBuilder();
      queryWriteStatusSettings = settings.queryWriteStatusSettings.toBuilder();
      patchObjectSettings = settings.patchObjectSettings.toBuilder();
      updateObjectSettings = settings.updateObjectSettings.toBuilder();
      getObjectIamPolicySettings = settings.getObjectIamPolicySettings.toBuilder();
      setObjectIamPolicySettings = settings.setObjectIamPolicySettings.toBuilder();
      testObjectIamPermissionsSettings = settings.testObjectIamPermissionsSettings.toBuilder();
      watchAllObjectsSettings = settings.watchAllObjectsSettings.toBuilder();
      getServiceAccountSettings = settings.getServiceAccountSettings.toBuilder();
      createHmacKeySettings = settings.createHmacKeySettings.toBuilder();
      deleteHmacKeySettings = settings.deleteHmacKeySettings.toBuilder();
      getHmacKeySettings = settings.getHmacKeySettings.toBuilder();
      listHmacKeysSettings = settings.listHmacKeysSettings.toBuilder();
      updateHmacKeySettings = settings.updateHmacKeySettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              deleteBucketAccessControlSettings,
              getBucketAccessControlSettings,
              insertBucketAccessControlSettings,
              listBucketAccessControlsSettings,
              updateBucketAccessControlSettings,
              patchBucketAccessControlSettings,
              deleteBucketSettings,
              getBucketSettings,
              insertBucketSettings,
              listChannelsSettings,
              listBucketsSettings,
              lockBucketRetentionPolicySettings,
              getBucketIamPolicySettings,
              setBucketIamPolicySettings,
              testBucketIamPermissionsSettings,
              patchBucketSettings,
              updateBucketSettings,
              stopChannelSettings,
              deleteDefaultObjectAccessControlSettings,
              getDefaultObjectAccessControlSettings,
              insertDefaultObjectAccessControlSettings,
              listDefaultObjectAccessControlsSettings,
              patchDefaultObjectAccessControlSettings,
              updateDefaultObjectAccessControlSettings,
              deleteNotificationSettings,
              getNotificationSettings,
              insertNotificationSettings,
              listNotificationsSettings,
              deleteObjectAccessControlSettings,
              getObjectAccessControlSettings,
              insertObjectAccessControlSettings,
              listObjectAccessControlsSettings,
              patchObjectAccessControlSettings,
              updateObjectAccessControlSettings,
              composeObjectSettings,
              copyObjectSettings,
              deleteObjectSettings,
              getObjectSettings,
              listObjectsSettings,
              rewriteObjectSettings,
              startResumableWriteSettings,
              queryWriteStatusSettings,
              patchObjectSettings,
              updateObjectSettings,
              getObjectIamPolicySettings,
              setObjectIamPolicySettings,
              testObjectIamPermissionsSettings,
              watchAllObjectsSettings,
              getServiceAccountSettings,
              createHmacKeySettings,
              deleteHmacKeySettings,
              getHmacKeySettings,
              listHmacKeysSettings,
              updateHmacKeySettings);
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'
    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) throws Exception {
      super.applyToAllUnaryMethods(unaryMethodSettingsBuilders, settingsUpdater);
      return this;
    }

    public ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders() {
      return unaryMethodSettingsBuilders;
    }

    /** Returns the builder for the settings used for calls to deleteBucketAccessControl. */
    public UnaryCallSettings.Builder<DeleteBucketAccessControlRequest, Empty>
        deleteBucketAccessControlSettings() {
      return deleteBucketAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to getBucketAccessControl. */
    public UnaryCallSettings.Builder<GetBucketAccessControlRequest, BucketAccessControl>
        getBucketAccessControlSettings() {
      return getBucketAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to insertBucketAccessControl. */
    public UnaryCallSettings.Builder<InsertBucketAccessControlRequest, BucketAccessControl>
        insertBucketAccessControlSettings() {
      return insertBucketAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to listBucketAccessControls. */
    public UnaryCallSettings.Builder<
            ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
        listBucketAccessControlsSettings() {
      return listBucketAccessControlsSettings;
    }

    /** Returns the builder for the settings used for calls to updateBucketAccessControl. */
    public UnaryCallSettings.Builder<UpdateBucketAccessControlRequest, BucketAccessControl>
        updateBucketAccessControlSettings() {
      return updateBucketAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to patchBucketAccessControl. */
    public UnaryCallSettings.Builder<PatchBucketAccessControlRequest, BucketAccessControl>
        patchBucketAccessControlSettings() {
      return patchBucketAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to deleteBucket. */
    public UnaryCallSettings.Builder<DeleteBucketRequest, Empty> deleteBucketSettings() {
      return deleteBucketSettings;
    }

    /** Returns the builder for the settings used for calls to getBucket. */
    public UnaryCallSettings.Builder<GetBucketRequest, Bucket> getBucketSettings() {
      return getBucketSettings;
    }

    /** Returns the builder for the settings used for calls to insertBucket. */
    public UnaryCallSettings.Builder<InsertBucketRequest, Bucket> insertBucketSettings() {
      return insertBucketSettings;
    }

    /** Returns the builder for the settings used for calls to listChannels. */
    public UnaryCallSettings.Builder<ListChannelsRequest, ListChannelsResponse>
        listChannelsSettings() {
      return listChannelsSettings;
    }

    /** Returns the builder for the settings used for calls to listBuckets. */
    public UnaryCallSettings.Builder<ListBucketsRequest, ListBucketsResponse>
        listBucketsSettings() {
      return listBucketsSettings;
    }

    /** Returns the builder for the settings used for calls to lockBucketRetentionPolicy. */
    public UnaryCallSettings.Builder<LockRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicySettings() {
      return lockBucketRetentionPolicySettings;
    }

    /** Returns the builder for the settings used for calls to getBucketIamPolicy. */
    public UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getBucketIamPolicySettings() {
      return getBucketIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to setBucketIamPolicy. */
    public UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setBucketIamPolicySettings() {
      return setBucketIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to testBucketIamPermissions. */
    public UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testBucketIamPermissionsSettings() {
      return testBucketIamPermissionsSettings;
    }

    /** Returns the builder for the settings used for calls to patchBucket. */
    public UnaryCallSettings.Builder<PatchBucketRequest, Bucket> patchBucketSettings() {
      return patchBucketSettings;
    }

    /** Returns the builder for the settings used for calls to updateBucket. */
    public UnaryCallSettings.Builder<UpdateBucketRequest, Bucket> updateBucketSettings() {
      return updateBucketSettings;
    }

    /** Returns the builder for the settings used for calls to stopChannel. */
    public UnaryCallSettings.Builder<StopChannelRequest, Empty> stopChannelSettings() {
      return stopChannelSettings;
    }

    /** Returns the builder for the settings used for calls to deleteDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<DeleteDefaultObjectAccessControlRequest, Empty>
        deleteDefaultObjectAccessControlSettings() {
      return deleteDefaultObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to getDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
        getDefaultObjectAccessControlSettings() {
      return getDefaultObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to insertDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
        insertDefaultObjectAccessControlSettings() {
      return insertDefaultObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to listDefaultObjectAccessControls. */
    public UnaryCallSettings.Builder<
            ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listDefaultObjectAccessControlsSettings() {
      return listDefaultObjectAccessControlsSettings;
    }

    /** Returns the builder for the settings used for calls to patchDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
        patchDefaultObjectAccessControlSettings() {
      return patchDefaultObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to updateDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
        updateDefaultObjectAccessControlSettings() {
      return updateDefaultObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to deleteNotification. */
    public UnaryCallSettings.Builder<DeleteNotificationRequest, Empty>
        deleteNotificationSettings() {
      return deleteNotificationSettings;
    }

    /** Returns the builder for the settings used for calls to getNotification. */
    public UnaryCallSettings.Builder<GetNotificationRequest, Notification>
        getNotificationSettings() {
      return getNotificationSettings;
    }

    /** Returns the builder for the settings used for calls to insertNotification. */
    public UnaryCallSettings.Builder<InsertNotificationRequest, Notification>
        insertNotificationSettings() {
      return insertNotificationSettings;
    }

    /** Returns the builder for the settings used for calls to listNotifications. */
    public UnaryCallSettings.Builder<ListNotificationsRequest, ListNotificationsResponse>
        listNotificationsSettings() {
      return listNotificationsSettings;
    }

    /** Returns the builder for the settings used for calls to deleteObjectAccessControl. */
    public UnaryCallSettings.Builder<DeleteObjectAccessControlRequest, Empty>
        deleteObjectAccessControlSettings() {
      return deleteObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to getObjectAccessControl. */
    public UnaryCallSettings.Builder<GetObjectAccessControlRequest, ObjectAccessControl>
        getObjectAccessControlSettings() {
      return getObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to insertObjectAccessControl. */
    public UnaryCallSettings.Builder<InsertObjectAccessControlRequest, ObjectAccessControl>
        insertObjectAccessControlSettings() {
      return insertObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to listObjectAccessControls. */
    public UnaryCallSettings.Builder<
            ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listObjectAccessControlsSettings() {
      return listObjectAccessControlsSettings;
    }

    /** Returns the builder for the settings used for calls to patchObjectAccessControl. */
    public UnaryCallSettings.Builder<PatchObjectAccessControlRequest, ObjectAccessControl>
        patchObjectAccessControlSettings() {
      return patchObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to updateObjectAccessControl. */
    public UnaryCallSettings.Builder<UpdateObjectAccessControlRequest, ObjectAccessControl>
        updateObjectAccessControlSettings() {
      return updateObjectAccessControlSettings;
    }

    /** Returns the builder for the settings used for calls to composeObject. */
    public UnaryCallSettings.Builder<ComposeObjectRequest, com.google.storage.v1.Object>
        composeObjectSettings() {
      return composeObjectSettings;
    }

    /** Returns the builder for the settings used for calls to copyObject. */
    public UnaryCallSettings.Builder<CopyObjectRequest, com.google.storage.v1.Object>
        copyObjectSettings() {
      return copyObjectSettings;
    }

    /** Returns the builder for the settings used for calls to deleteObject. */
    public UnaryCallSettings.Builder<DeleteObjectRequest, Empty> deleteObjectSettings() {
      return deleteObjectSettings;
    }

    /** Returns the builder for the settings used for calls to getObject. */
    public UnaryCallSettings.Builder<GetObjectRequest, com.google.storage.v1.Object>
        getObjectSettings() {
      return getObjectSettings;
    }

    /** Returns the builder for the settings used for calls to getObjectMedia. */
    public ServerStreamingCallSettings.Builder<GetObjectMediaRequest, GetObjectMediaResponse>
        getObjectMediaSettings() {
      return getObjectMediaSettings;
    }

    /** Returns the builder for the settings used for calls to insertObject. */
    public StreamingCallSettings.Builder<InsertObjectRequest, com.google.storage.v1.Object>
        insertObjectSettings() {
      return insertObjectSettings;
    }

    /** Returns the builder for the settings used for calls to listObjects. */
    public UnaryCallSettings.Builder<ListObjectsRequest, ListObjectsResponse>
        listObjectsSettings() {
      return listObjectsSettings;
    }

    /** Returns the builder for the settings used for calls to rewriteObject. */
    public UnaryCallSettings.Builder<RewriteObjectRequest, RewriteResponse>
        rewriteObjectSettings() {
      return rewriteObjectSettings;
    }

    /** Returns the builder for the settings used for calls to startResumableWrite. */
    public UnaryCallSettings.Builder<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteSettings() {
      return startResumableWriteSettings;
    }

    /** Returns the builder for the settings used for calls to queryWriteStatus. */
    public UnaryCallSettings.Builder<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusSettings() {
      return queryWriteStatusSettings;
    }

    /** Returns the builder for the settings used for calls to patchObject. */
    public UnaryCallSettings.Builder<PatchObjectRequest, com.google.storage.v1.Object>
        patchObjectSettings() {
      return patchObjectSettings;
    }

    /** Returns the builder for the settings used for calls to updateObject. */
    public UnaryCallSettings.Builder<UpdateObjectRequest, com.google.storage.v1.Object>
        updateObjectSettings() {
      return updateObjectSettings;
    }

    /** Returns the builder for the settings used for calls to getObjectIamPolicy. */
    public UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getObjectIamPolicySettings() {
      return getObjectIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to setObjectIamPolicy. */
    public UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setObjectIamPolicySettings() {
      return setObjectIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to testObjectIamPermissions. */
    public UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testObjectIamPermissionsSettings() {
      return testObjectIamPermissionsSettings;
    }

    /** Returns the builder for the settings used for calls to watchAllObjects. */
    public UnaryCallSettings.Builder<WatchAllObjectsRequest, Channel> watchAllObjectsSettings() {
      return watchAllObjectsSettings;
    }

    /** Returns the builder for the settings used for calls to getServiceAccount. */
    public UnaryCallSettings.Builder<GetProjectServiceAccountRequest, ServiceAccount>
        getServiceAccountSettings() {
      return getServiceAccountSettings;
    }

    /** Returns the builder for the settings used for calls to createHmacKey. */
    public UnaryCallSettings.Builder<CreateHmacKeyRequest, CreateHmacKeyResponse>
        createHmacKeySettings() {
      return createHmacKeySettings;
    }

    /** Returns the builder for the settings used for calls to deleteHmacKey. */
    public UnaryCallSettings.Builder<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings() {
      return deleteHmacKeySettings;
    }

    /** Returns the builder for the settings used for calls to getHmacKey. */
    public UnaryCallSettings.Builder<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings() {
      return getHmacKeySettings;
    }

    /** Returns the builder for the settings used for calls to listHmacKeys. */
    public UnaryCallSettings.Builder<ListHmacKeysRequest, ListHmacKeysResponse>
        listHmacKeysSettings() {
      return listHmacKeysSettings;
    }

    /** Returns the builder for the settings used for calls to updateHmacKey. */
    public UnaryCallSettings.Builder<UpdateHmacKeyRequest, HmacKeyMetadata>
        updateHmacKeySettings() {
      return updateHmacKeySettings;
    }

    @Override
    public StorageStubSettings build() throws IOException {
      return new StorageStubSettings(this);
    }
  }
}
