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

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.ServerStreamingCallSettings;
import com.google.api.gax.rpc.StreamingCallSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.google.storage.v1.stub.StorageStubSettings;
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

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Settings class to configure an instance of {@link StorageClient}.
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
 * StorageSettings.Builder storageSettingsBuilder =
 *     StorageSettings.newBuilder();
 * storageSettingsBuilder
 *     .deleteBucketAccessControlSettings()
 *     .setRetrySettings(
 *         storageSettingsBuilder.deleteBucketAccessControlSettings().getRetrySettings().toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * StorageSettings storageSettings = storageSettingsBuilder.build();
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class StorageSettings extends ClientSettings<StorageSettings> {
  /** Returns the object with the settings used for calls to deleteBucketAccessControl. */
  public UnaryCallSettings<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteBucketAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to getBucketAccessControl. */
  public UnaryCallSettings<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).getBucketAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to insertBucketAccessControl. */
  public UnaryCallSettings<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).insertBucketAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to listBucketAccessControls. */
  public UnaryCallSettings<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsSettings() {
    return ((StorageStubSettings) getStubSettings()).listBucketAccessControlsSettings();
  }

  /** Returns the object with the settings used for calls to updateBucketAccessControl. */
  public UnaryCallSettings<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).updateBucketAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to patchBucketAccessControl. */
  public UnaryCallSettings<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).patchBucketAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to deleteBucket. */
  public UnaryCallSettings<DeleteBucketRequest, Empty> deleteBucketSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteBucketSettings();
  }

  /** Returns the object with the settings used for calls to getBucket. */
  public UnaryCallSettings<GetBucketRequest, Bucket> getBucketSettings() {
    return ((StorageStubSettings) getStubSettings()).getBucketSettings();
  }

  /** Returns the object with the settings used for calls to insertBucket. */
  public UnaryCallSettings<InsertBucketRequest, Bucket> insertBucketSettings() {
    return ((StorageStubSettings) getStubSettings()).insertBucketSettings();
  }

  /** Returns the object with the settings used for calls to listChannels. */
  public UnaryCallSettings<ListChannelsRequest, ListChannelsResponse> listChannelsSettings() {
    return ((StorageStubSettings) getStubSettings()).listChannelsSettings();
  }

  /** Returns the object with the settings used for calls to listBuckets. */
  public UnaryCallSettings<ListBucketsRequest, ListBucketsResponse> listBucketsSettings() {
    return ((StorageStubSettings) getStubSettings()).listBucketsSettings();
  }

  /** Returns the object with the settings used for calls to lockBucketRetentionPolicy. */
  public UnaryCallSettings<LockRetentionPolicyRequest, Bucket> lockBucketRetentionPolicySettings() {
    return ((StorageStubSettings) getStubSettings()).lockBucketRetentionPolicySettings();
  }

  /** Returns the object with the settings used for calls to getBucketIamPolicy. */
  public UnaryCallSettings<GetIamPolicyRequest, Policy> getBucketIamPolicySettings() {
    return ((StorageStubSettings) getStubSettings()).getBucketIamPolicySettings();
  }

  /** Returns the object with the settings used for calls to setBucketIamPolicy. */
  public UnaryCallSettings<SetIamPolicyRequest, Policy> setBucketIamPolicySettings() {
    return ((StorageStubSettings) getStubSettings()).setBucketIamPolicySettings();
  }

  /** Returns the object with the settings used for calls to testBucketIamPermissions. */
  public UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsSettings() {
    return ((StorageStubSettings) getStubSettings()).testBucketIamPermissionsSettings();
  }

  /** Returns the object with the settings used for calls to patchBucket. */
  public UnaryCallSettings<PatchBucketRequest, Bucket> patchBucketSettings() {
    return ((StorageStubSettings) getStubSettings()).patchBucketSettings();
  }

  /** Returns the object with the settings used for calls to updateBucket. */
  public UnaryCallSettings<UpdateBucketRequest, Bucket> updateBucketSettings() {
    return ((StorageStubSettings) getStubSettings()).updateBucketSettings();
  }

  /** Returns the object with the settings used for calls to stopChannel. */
  public UnaryCallSettings<StopChannelRequest, Empty> stopChannelSettings() {
    return ((StorageStubSettings) getStubSettings()).stopChannelSettings();
  }

  /** Returns the object with the settings used for calls to deleteDefaultObjectAccessControl. */
  public UnaryCallSettings<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteDefaultObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to getDefaultObjectAccessControl. */
  public UnaryCallSettings<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).getDefaultObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to insertDefaultObjectAccessControl. */
  public UnaryCallSettings<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).insertDefaultObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to listDefaultObjectAccessControls. */
  public UnaryCallSettings<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsSettings() {
    return ((StorageStubSettings) getStubSettings()).listDefaultObjectAccessControlsSettings();
  }

  /** Returns the object with the settings used for calls to patchDefaultObjectAccessControl. */
  public UnaryCallSettings<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).patchDefaultObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to updateDefaultObjectAccessControl. */
  public UnaryCallSettings<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).updateDefaultObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to deleteNotification. */
  public UnaryCallSettings<DeleteNotificationRequest, Empty> deleteNotificationSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteNotificationSettings();
  }

  /** Returns the object with the settings used for calls to getNotification. */
  public UnaryCallSettings<GetNotificationRequest, Notification> getNotificationSettings() {
    return ((StorageStubSettings) getStubSettings()).getNotificationSettings();
  }

  /** Returns the object with the settings used for calls to insertNotification. */
  public UnaryCallSettings<InsertNotificationRequest, Notification> insertNotificationSettings() {
    return ((StorageStubSettings) getStubSettings()).insertNotificationSettings();
  }

  /** Returns the object with the settings used for calls to listNotifications. */
  public UnaryCallSettings<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsSettings() {
    return ((StorageStubSettings) getStubSettings()).listNotificationsSettings();
  }

  /** Returns the object with the settings used for calls to deleteObjectAccessControl. */
  public UnaryCallSettings<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to getObjectAccessControl. */
  public UnaryCallSettings<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).getObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to insertObjectAccessControl. */
  public UnaryCallSettings<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).insertObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to listObjectAccessControls. */
  public UnaryCallSettings<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsSettings() {
    return ((StorageStubSettings) getStubSettings()).listObjectAccessControlsSettings();
  }

  /** Returns the object with the settings used for calls to patchObjectAccessControl. */
  public UnaryCallSettings<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).patchObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to updateObjectAccessControl. */
  public UnaryCallSettings<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlSettings() {
    return ((StorageStubSettings) getStubSettings()).updateObjectAccessControlSettings();
  }

  /** Returns the object with the settings used for calls to composeObject. */
  public UnaryCallSettings<ComposeObjectRequest, com.google.storage.v1.Object>
      composeObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).composeObjectSettings();
  }

  /** Returns the object with the settings used for calls to copyObject. */
  public UnaryCallSettings<CopyObjectRequest, com.google.storage.v1.Object> copyObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).copyObjectSettings();
  }

  /** Returns the object with the settings used for calls to deleteObject. */
  public UnaryCallSettings<DeleteObjectRequest, Empty> deleteObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).deleteObjectSettings();
  }

  /** Returns the object with the settings used for calls to getObject. */
  public UnaryCallSettings<GetObjectRequest, com.google.storage.v1.Object> getObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).getObjectSettings();
  }

  /** Returns the object with the settings used for calls to getObjectMedia. */
  public ServerStreamingCallSettings<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaSettings() {
    return ((StorageStubSettings) getStubSettings()).getObjectMediaSettings();
  }

  /** Returns the object with the settings used for calls to insertObject. */
  public StreamingCallSettings<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).insertObjectSettings();
  }

  /** Returns the object with the settings used for calls to listObjects. */
  public UnaryCallSettings<ListObjectsRequest, ListObjectsResponse> listObjectsSettings() {
    return ((StorageStubSettings) getStubSettings()).listObjectsSettings();
  }

  /** Returns the object with the settings used for calls to rewriteObject. */
  public UnaryCallSettings<RewriteObjectRequest, RewriteResponse> rewriteObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).rewriteObjectSettings();
  }

  /** Returns the object with the settings used for calls to startResumableWrite. */
  public UnaryCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteSettings() {
    return ((StorageStubSettings) getStubSettings()).startResumableWriteSettings();
  }

  /** Returns the object with the settings used for calls to queryWriteStatus. */
  public UnaryCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusSettings() {
    return ((StorageStubSettings) getStubSettings()).queryWriteStatusSettings();
  }

  /** Returns the object with the settings used for calls to patchObject. */
  public UnaryCallSettings<PatchObjectRequest, com.google.storage.v1.Object> patchObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).patchObjectSettings();
  }

  /** Returns the object with the settings used for calls to updateObject. */
  public UnaryCallSettings<UpdateObjectRequest, com.google.storage.v1.Object>
      updateObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).updateObjectSettings();
  }

  /** Returns the object with the settings used for calls to getObjectIamPolicy. */
  public UnaryCallSettings<GetIamPolicyRequest, Policy> getObjectIamPolicySettings() {
    return ((StorageStubSettings) getStubSettings()).getObjectIamPolicySettings();
  }

  /** Returns the object with the settings used for calls to setObjectIamPolicy. */
  public UnaryCallSettings<SetIamPolicyRequest, Policy> setObjectIamPolicySettings() {
    return ((StorageStubSettings) getStubSettings()).setObjectIamPolicySettings();
  }

  /** Returns the object with the settings used for calls to testObjectIamPermissions. */
  public UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsSettings() {
    return ((StorageStubSettings) getStubSettings()).testObjectIamPermissionsSettings();
  }

  /** Returns the object with the settings used for calls to watchAllObjects. */
  public UnaryCallSettings<WatchAllObjectsRequest, Channel> watchAllObjectsSettings() {
    return ((StorageStubSettings) getStubSettings()).watchAllObjectsSettings();
  }

  /** Returns the object with the settings used for calls to getServiceAccount. */
  public UnaryCallSettings<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountSettings() {
    return ((StorageStubSettings) getStubSettings()).getServiceAccountSettings();
  }

  /** Returns the object with the settings used for calls to createHmacKey. */
  public UnaryCallSettings<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeySettings() {
    return ((StorageStubSettings) getStubSettings()).createHmacKeySettings();
  }

  /** Returns the object with the settings used for calls to deleteHmacKey. */
  public UnaryCallSettings<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings() {
    return ((StorageStubSettings) getStubSettings()).deleteHmacKeySettings();
  }

  /** Returns the object with the settings used for calls to getHmacKey. */
  public UnaryCallSettings<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings() {
    return ((StorageStubSettings) getStubSettings()).getHmacKeySettings();
  }

  /** Returns the object with the settings used for calls to listHmacKeys. */
  public UnaryCallSettings<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysSettings() {
    return ((StorageStubSettings) getStubSettings()).listHmacKeysSettings();
  }

  /** Returns the object with the settings used for calls to updateHmacKey. */
  public UnaryCallSettings<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeySettings() {
    return ((StorageStubSettings) getStubSettings()).updateHmacKeySettings();
  }

  public static final StorageSettings create(StorageStubSettings stub) throws IOException {
    return new StorageSettings.Builder(stub.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return StorageStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return StorageStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return StorageStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return StorageStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return StorageStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return StorageStubSettings.defaultTransportChannelProvider();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return StorageStubSettings.defaultApiClientHeaderProviderBuilder();
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

  protected StorageSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for StorageSettings. */
  public static class Builder extends ClientSettings.Builder<StorageSettings, Builder> {
    protected Builder() throws IOException {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(StorageStubSettings.newBuilder(clientContext));
    }

    private static Builder createDefault() {
      return new Builder(StorageStubSettings.newBuilder());
    }

    protected Builder(StorageSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(StorageStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    public StorageStubSettings.Builder getStubSettingsBuilder() {
      return ((StorageStubSettings.Builder) getStubSettings());
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'
    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) throws Exception {
      super.applyToAllUnaryMethods(
          getStubSettingsBuilder().unaryMethodSettingsBuilders(), settingsUpdater);
      return this;
    }

    /** Returns the builder for the settings used for calls to deleteBucketAccessControl. */
    public UnaryCallSettings.Builder<DeleteBucketAccessControlRequest, Empty>
        deleteBucketAccessControlSettings() {
      return getStubSettingsBuilder().deleteBucketAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to getBucketAccessControl. */
    public UnaryCallSettings.Builder<GetBucketAccessControlRequest, BucketAccessControl>
        getBucketAccessControlSettings() {
      return getStubSettingsBuilder().getBucketAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to insertBucketAccessControl. */
    public UnaryCallSettings.Builder<InsertBucketAccessControlRequest, BucketAccessControl>
        insertBucketAccessControlSettings() {
      return getStubSettingsBuilder().insertBucketAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to listBucketAccessControls. */
    public UnaryCallSettings.Builder<
            ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
        listBucketAccessControlsSettings() {
      return getStubSettingsBuilder().listBucketAccessControlsSettings();
    }

    /** Returns the builder for the settings used for calls to updateBucketAccessControl. */
    public UnaryCallSettings.Builder<UpdateBucketAccessControlRequest, BucketAccessControl>
        updateBucketAccessControlSettings() {
      return getStubSettingsBuilder().updateBucketAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to patchBucketAccessControl. */
    public UnaryCallSettings.Builder<PatchBucketAccessControlRequest, BucketAccessControl>
        patchBucketAccessControlSettings() {
      return getStubSettingsBuilder().patchBucketAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to deleteBucket. */
    public UnaryCallSettings.Builder<DeleteBucketRequest, Empty> deleteBucketSettings() {
      return getStubSettingsBuilder().deleteBucketSettings();
    }

    /** Returns the builder for the settings used for calls to getBucket. */
    public UnaryCallSettings.Builder<GetBucketRequest, Bucket> getBucketSettings() {
      return getStubSettingsBuilder().getBucketSettings();
    }

    /** Returns the builder for the settings used for calls to insertBucket. */
    public UnaryCallSettings.Builder<InsertBucketRequest, Bucket> insertBucketSettings() {
      return getStubSettingsBuilder().insertBucketSettings();
    }

    /** Returns the builder for the settings used for calls to listChannels. */
    public UnaryCallSettings.Builder<ListChannelsRequest, ListChannelsResponse>
        listChannelsSettings() {
      return getStubSettingsBuilder().listChannelsSettings();
    }

    /** Returns the builder for the settings used for calls to listBuckets. */
    public UnaryCallSettings.Builder<ListBucketsRequest, ListBucketsResponse>
        listBucketsSettings() {
      return getStubSettingsBuilder().listBucketsSettings();
    }

    /** Returns the builder for the settings used for calls to lockBucketRetentionPolicy. */
    public UnaryCallSettings.Builder<LockRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicySettings() {
      return getStubSettingsBuilder().lockBucketRetentionPolicySettings();
    }

    /** Returns the builder for the settings used for calls to getBucketIamPolicy. */
    public UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getBucketIamPolicySettings() {
      return getStubSettingsBuilder().getBucketIamPolicySettings();
    }

    /** Returns the builder for the settings used for calls to setBucketIamPolicy. */
    public UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setBucketIamPolicySettings() {
      return getStubSettingsBuilder().setBucketIamPolicySettings();
    }

    /** Returns the builder for the settings used for calls to testBucketIamPermissions. */
    public UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testBucketIamPermissionsSettings() {
      return getStubSettingsBuilder().testBucketIamPermissionsSettings();
    }

    /** Returns the builder for the settings used for calls to patchBucket. */
    public UnaryCallSettings.Builder<PatchBucketRequest, Bucket> patchBucketSettings() {
      return getStubSettingsBuilder().patchBucketSettings();
    }

    /** Returns the builder for the settings used for calls to updateBucket. */
    public UnaryCallSettings.Builder<UpdateBucketRequest, Bucket> updateBucketSettings() {
      return getStubSettingsBuilder().updateBucketSettings();
    }

    /** Returns the builder for the settings used for calls to stopChannel. */
    public UnaryCallSettings.Builder<StopChannelRequest, Empty> stopChannelSettings() {
      return getStubSettingsBuilder().stopChannelSettings();
    }

    /** Returns the builder for the settings used for calls to deleteDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<DeleteDefaultObjectAccessControlRequest, Empty>
        deleteDefaultObjectAccessControlSettings() {
      return getStubSettingsBuilder().deleteDefaultObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to getDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
        getDefaultObjectAccessControlSettings() {
      return getStubSettingsBuilder().getDefaultObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to insertDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
        insertDefaultObjectAccessControlSettings() {
      return getStubSettingsBuilder().insertDefaultObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to listDefaultObjectAccessControls. */
    public UnaryCallSettings.Builder<
            ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listDefaultObjectAccessControlsSettings() {
      return getStubSettingsBuilder().listDefaultObjectAccessControlsSettings();
    }

    /** Returns the builder for the settings used for calls to patchDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
        patchDefaultObjectAccessControlSettings() {
      return getStubSettingsBuilder().patchDefaultObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to updateDefaultObjectAccessControl. */
    public UnaryCallSettings.Builder<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
        updateDefaultObjectAccessControlSettings() {
      return getStubSettingsBuilder().updateDefaultObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to deleteNotification. */
    public UnaryCallSettings.Builder<DeleteNotificationRequest, Empty>
        deleteNotificationSettings() {
      return getStubSettingsBuilder().deleteNotificationSettings();
    }

    /** Returns the builder for the settings used for calls to getNotification. */
    public UnaryCallSettings.Builder<GetNotificationRequest, Notification>
        getNotificationSettings() {
      return getStubSettingsBuilder().getNotificationSettings();
    }

    /** Returns the builder for the settings used for calls to insertNotification. */
    public UnaryCallSettings.Builder<InsertNotificationRequest, Notification>
        insertNotificationSettings() {
      return getStubSettingsBuilder().insertNotificationSettings();
    }

    /** Returns the builder for the settings used for calls to listNotifications. */
    public UnaryCallSettings.Builder<ListNotificationsRequest, ListNotificationsResponse>
        listNotificationsSettings() {
      return getStubSettingsBuilder().listNotificationsSettings();
    }

    /** Returns the builder for the settings used for calls to deleteObjectAccessControl. */
    public UnaryCallSettings.Builder<DeleteObjectAccessControlRequest, Empty>
        deleteObjectAccessControlSettings() {
      return getStubSettingsBuilder().deleteObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to getObjectAccessControl. */
    public UnaryCallSettings.Builder<GetObjectAccessControlRequest, ObjectAccessControl>
        getObjectAccessControlSettings() {
      return getStubSettingsBuilder().getObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to insertObjectAccessControl. */
    public UnaryCallSettings.Builder<InsertObjectAccessControlRequest, ObjectAccessControl>
        insertObjectAccessControlSettings() {
      return getStubSettingsBuilder().insertObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to listObjectAccessControls. */
    public UnaryCallSettings.Builder<
            ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listObjectAccessControlsSettings() {
      return getStubSettingsBuilder().listObjectAccessControlsSettings();
    }

    /** Returns the builder for the settings used for calls to patchObjectAccessControl. */
    public UnaryCallSettings.Builder<PatchObjectAccessControlRequest, ObjectAccessControl>
        patchObjectAccessControlSettings() {
      return getStubSettingsBuilder().patchObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to updateObjectAccessControl. */
    public UnaryCallSettings.Builder<UpdateObjectAccessControlRequest, ObjectAccessControl>
        updateObjectAccessControlSettings() {
      return getStubSettingsBuilder().updateObjectAccessControlSettings();
    }

    /** Returns the builder for the settings used for calls to composeObject. */
    public UnaryCallSettings.Builder<ComposeObjectRequest, com.google.storage.v1.Object>
        composeObjectSettings() {
      return getStubSettingsBuilder().composeObjectSettings();
    }

    /** Returns the builder for the settings used for calls to copyObject. */
    public UnaryCallSettings.Builder<CopyObjectRequest, com.google.storage.v1.Object>
        copyObjectSettings() {
      return getStubSettingsBuilder().copyObjectSettings();
    }

    /** Returns the builder for the settings used for calls to deleteObject. */
    public UnaryCallSettings.Builder<DeleteObjectRequest, Empty> deleteObjectSettings() {
      return getStubSettingsBuilder().deleteObjectSettings();
    }

    /** Returns the builder for the settings used for calls to getObject. */
    public UnaryCallSettings.Builder<GetObjectRequest, com.google.storage.v1.Object>
        getObjectSettings() {
      return getStubSettingsBuilder().getObjectSettings();
    }

    /** Returns the builder for the settings used for calls to getObjectMedia. */
    public ServerStreamingCallSettings.Builder<GetObjectMediaRequest, GetObjectMediaResponse>
        getObjectMediaSettings() {
      return getStubSettingsBuilder().getObjectMediaSettings();
    }

    /** Returns the builder for the settings used for calls to insertObject. */
    public StreamingCallSettings.Builder<InsertObjectRequest, com.google.storage.v1.Object>
        insertObjectSettings() {
      return getStubSettingsBuilder().insertObjectSettings();
    }

    /** Returns the builder for the settings used for calls to listObjects. */
    public UnaryCallSettings.Builder<ListObjectsRequest, ListObjectsResponse>
        listObjectsSettings() {
      return getStubSettingsBuilder().listObjectsSettings();
    }

    /** Returns the builder for the settings used for calls to rewriteObject. */
    public UnaryCallSettings.Builder<RewriteObjectRequest, RewriteResponse>
        rewriteObjectSettings() {
      return getStubSettingsBuilder().rewriteObjectSettings();
    }

    /** Returns the builder for the settings used for calls to startResumableWrite. */
    public UnaryCallSettings.Builder<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteSettings() {
      return getStubSettingsBuilder().startResumableWriteSettings();
    }

    /** Returns the builder for the settings used for calls to queryWriteStatus. */
    public UnaryCallSettings.Builder<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusSettings() {
      return getStubSettingsBuilder().queryWriteStatusSettings();
    }

    /** Returns the builder for the settings used for calls to patchObject. */
    public UnaryCallSettings.Builder<PatchObjectRequest, com.google.storage.v1.Object>
        patchObjectSettings() {
      return getStubSettingsBuilder().patchObjectSettings();
    }

    /** Returns the builder for the settings used for calls to updateObject. */
    public UnaryCallSettings.Builder<UpdateObjectRequest, com.google.storage.v1.Object>
        updateObjectSettings() {
      return getStubSettingsBuilder().updateObjectSettings();
    }

    /** Returns the builder for the settings used for calls to getObjectIamPolicy. */
    public UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getObjectIamPolicySettings() {
      return getStubSettingsBuilder().getObjectIamPolicySettings();
    }

    /** Returns the builder for the settings used for calls to setObjectIamPolicy. */
    public UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setObjectIamPolicySettings() {
      return getStubSettingsBuilder().setObjectIamPolicySettings();
    }

    /** Returns the builder for the settings used for calls to testObjectIamPermissions. */
    public UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testObjectIamPermissionsSettings() {
      return getStubSettingsBuilder().testObjectIamPermissionsSettings();
    }

    /** Returns the builder for the settings used for calls to watchAllObjects. */
    public UnaryCallSettings.Builder<WatchAllObjectsRequest, Channel> watchAllObjectsSettings() {
      return getStubSettingsBuilder().watchAllObjectsSettings();
    }

    /** Returns the builder for the settings used for calls to getServiceAccount. */
    public UnaryCallSettings.Builder<GetProjectServiceAccountRequest, ServiceAccount>
        getServiceAccountSettings() {
      return getStubSettingsBuilder().getServiceAccountSettings();
    }

    /** Returns the builder for the settings used for calls to createHmacKey. */
    public UnaryCallSettings.Builder<CreateHmacKeyRequest, CreateHmacKeyResponse>
        createHmacKeySettings() {
      return getStubSettingsBuilder().createHmacKeySettings();
    }

    /** Returns the builder for the settings used for calls to deleteHmacKey. */
    public UnaryCallSettings.Builder<DeleteHmacKeyRequest, Empty> deleteHmacKeySettings() {
      return getStubSettingsBuilder().deleteHmacKeySettings();
    }

    /** Returns the builder for the settings used for calls to getHmacKey. */
    public UnaryCallSettings.Builder<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeySettings() {
      return getStubSettingsBuilder().getHmacKeySettings();
    }

    /** Returns the builder for the settings used for calls to listHmacKeys. */
    public UnaryCallSettings.Builder<ListHmacKeysRequest, ListHmacKeysResponse>
        listHmacKeysSettings() {
      return getStubSettingsBuilder().listHmacKeysSettings();
    }

    /** Returns the builder for the settings used for calls to updateHmacKey. */
    public UnaryCallSettings.Builder<UpdateHmacKeyRequest, HmacKeyMetadata>
        updateHmacKeySettings() {
      return getStubSettingsBuilder().updateHmacKeySettings();
    }

    @Override
    public StorageSettings build() throws IOException {
      return new StorageSettings(this);
    }
  }
}
