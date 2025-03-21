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

package com.google.storage.v2.stub;

import static com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import static com.google.storage.v2.StorageClient.ListObjectsPagedResponse;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ObsoleteApi;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.PagedCallSettings;
import com.google.api.gax.rpc.PagedListDescriptor;
import com.google.api.gax.rpc.PagedListResponseFactory;
import com.google.api.gax.rpc.ServerStreamingCallSettings;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StreamingCallSettings;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.Empty;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.CancelResumableWriteRequest;
import com.google.storage.v2.CancelResumableWriteResponse;
import com.google.storage.v2.ComposeObjectRequest;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListBucketsResponse;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.ListObjectsResponse;
import com.google.storage.v2.LockBucketRetentionPolicyRequest;
import com.google.storage.v2.MoveObjectRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.RestoreObjectRequest;
import com.google.storage.v2.RewriteObjectRequest;
import com.google.storage.v2.RewriteResponse;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.UpdateBucketRequest;
import com.google.storage.v2.UpdateObjectRequest;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
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
 * <p>For example, to set the
 * [RetrySettings](https://cloud.google.com/java/docs/reference/gax/latest/com.google.api.gax.retrying.RetrySettings)
 * of deleteBucket:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * StorageStubSettings.Builder storageSettingsBuilder = StorageStubSettings.newBuilder();
 * storageSettingsBuilder
 *     .deleteBucketSettings()
 *     .setRetrySettings(
 *         storageSettingsBuilder
 *             .deleteBucketSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setInitialRetryDelayDuration(Duration.ofSeconds(1))
 *             .setInitialRpcTimeoutDuration(Duration.ofSeconds(5))
 *             .setMaxAttempts(5)
 *             .setMaxRetryDelayDuration(Duration.ofSeconds(30))
 *             .setMaxRpcTimeoutDuration(Duration.ofSeconds(60))
 *             .setRetryDelayMultiplier(1.3)
 *             .setRpcTimeoutMultiplier(1.5)
 *             .setTotalTimeoutDuration(Duration.ofSeconds(300))
 *             .build());
 * StorageStubSettings storageSettings = storageSettingsBuilder.build();
 * }</pre>
 *
 * Please refer to the [Client Side Retry
 * Guide](https://github.com/googleapis/google-cloud-java/blob/main/docs/client_retries.md) for
 * additional support in setting retries.
 */
@Generated("by gapic-generator-java")
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

  private final UnaryCallSettings<DeleteBucketRequest, Empty> deleteBucketSettings;
  private final UnaryCallSettings<GetBucketRequest, Bucket> getBucketSettings;
  private final UnaryCallSettings<CreateBucketRequest, Bucket> createBucketSettings;
  private final PagedCallSettings<ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>
      listBucketsSettings;
  private final UnaryCallSettings<LockBucketRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicySettings;
  private final UnaryCallSettings<GetIamPolicyRequest, Policy> getIamPolicySettings;
  private final UnaryCallSettings<SetIamPolicyRequest, Policy> setIamPolicySettings;
  private final UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testIamPermissionsSettings;
  private final UnaryCallSettings<UpdateBucketRequest, Bucket> updateBucketSettings;
  private final UnaryCallSettings<ComposeObjectRequest, Object> composeObjectSettings;
  private final UnaryCallSettings<DeleteObjectRequest, Empty> deleteObjectSettings;
  private final UnaryCallSettings<RestoreObjectRequest, Object> restoreObjectSettings;
  private final UnaryCallSettings<CancelResumableWriteRequest, CancelResumableWriteResponse>
      cancelResumableWriteSettings;
  private final UnaryCallSettings<GetObjectRequest, Object> getObjectSettings;
  private final ServerStreamingCallSettings<ReadObjectRequest, ReadObjectResponse>
      readObjectSettings;
  private final StreamingCallSettings<BidiReadObjectRequest, BidiReadObjectResponse>
      bidiReadObjectSettings;
  private final UnaryCallSettings<UpdateObjectRequest, Object> updateObjectSettings;
  private final StreamingCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectSettings;
  private final StreamingCallSettings<BidiWriteObjectRequest, BidiWriteObjectResponse>
      bidiWriteObjectSettings;
  private final PagedCallSettings<ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>
      listObjectsSettings;
  private final UnaryCallSettings<RewriteObjectRequest, RewriteResponse> rewriteObjectSettings;
  private final UnaryCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteSettings;
  private final UnaryCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusSettings;
  private final UnaryCallSettings<MoveObjectRequest, Object> moveObjectSettings;

  private static final PagedListDescriptor<ListBucketsRequest, ListBucketsResponse, Bucket>
      LIST_BUCKETS_PAGE_STR_DESC =
          new PagedListDescriptor<ListBucketsRequest, ListBucketsResponse, Bucket>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListBucketsRequest injectToken(ListBucketsRequest payload, String token) {
              return ListBucketsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListBucketsRequest injectPageSize(ListBucketsRequest payload, int pageSize) {
              return ListBucketsRequest.newBuilder(payload).setPageSize(pageSize).build();
            }

            @Override
            public Integer extractPageSize(ListBucketsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListBucketsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<Bucket> extractResources(ListBucketsResponse payload) {
              return payload.getBucketsList();
            }
          };

  private static final PagedListDescriptor<ListObjectsRequest, ListObjectsResponse, Object>
      LIST_OBJECTS_PAGE_STR_DESC =
          new PagedListDescriptor<ListObjectsRequest, ListObjectsResponse, Object>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListObjectsRequest injectToken(ListObjectsRequest payload, String token) {
              return ListObjectsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListObjectsRequest injectPageSize(ListObjectsRequest payload, int pageSize) {
              return ListObjectsRequest.newBuilder(payload).setPageSize(pageSize).build();
            }

            @Override
            public Integer extractPageSize(ListObjectsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListObjectsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<Object> extractResources(ListObjectsResponse payload) {
              return payload.getObjectsList();
            }
          };

  private static final PagedListResponseFactory<
          ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>
      LIST_BUCKETS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>() {
            @Override
            public ApiFuture<ListBucketsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListBucketsRequest, ListBucketsResponse> callable,
                ListBucketsRequest request,
                ApiCallContext context,
                ApiFuture<ListBucketsResponse> futureResponse) {
              PageContext<ListBucketsRequest, ListBucketsResponse, Bucket> pageContext =
                  PageContext.create(callable, LIST_BUCKETS_PAGE_STR_DESC, request, context);
              return ListBucketsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  private static final PagedListResponseFactory<
          ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>
      LIST_OBJECTS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>() {
            @Override
            public ApiFuture<ListObjectsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListObjectsRequest, ListObjectsResponse> callable,
                ListObjectsRequest request,
                ApiCallContext context,
                ApiFuture<ListObjectsResponse> futureResponse) {
              PageContext<ListObjectsRequest, ListObjectsResponse, Object> pageContext =
                  PageContext.create(callable, LIST_OBJECTS_PAGE_STR_DESC, request, context);
              return ListObjectsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  /** Returns the object with the settings used for calls to deleteBucket. */
  public UnaryCallSettings<DeleteBucketRequest, Empty> deleteBucketSettings() {
    return deleteBucketSettings;
  }

  /** Returns the object with the settings used for calls to getBucket. */
  public UnaryCallSettings<GetBucketRequest, Bucket> getBucketSettings() {
    return getBucketSettings;
  }

  /** Returns the object with the settings used for calls to createBucket. */
  public UnaryCallSettings<CreateBucketRequest, Bucket> createBucketSettings() {
    return createBucketSettings;
  }

  /** Returns the object with the settings used for calls to listBuckets. */
  public PagedCallSettings<ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>
      listBucketsSettings() {
    return listBucketsSettings;
  }

  /** Returns the object with the settings used for calls to lockBucketRetentionPolicy. */
  public UnaryCallSettings<LockBucketRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicySettings() {
    return lockBucketRetentionPolicySettings;
  }

  /** Returns the object with the settings used for calls to getIamPolicy. */
  public UnaryCallSettings<GetIamPolicyRequest, Policy> getIamPolicySettings() {
    return getIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to setIamPolicy. */
  public UnaryCallSettings<SetIamPolicyRequest, Policy> setIamPolicySettings() {
    return setIamPolicySettings;
  }

  /** Returns the object with the settings used for calls to testIamPermissions. */
  public UnaryCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testIamPermissionsSettings() {
    return testIamPermissionsSettings;
  }

  /** Returns the object with the settings used for calls to updateBucket. */
  public UnaryCallSettings<UpdateBucketRequest, Bucket> updateBucketSettings() {
    return updateBucketSettings;
  }

  /** Returns the object with the settings used for calls to composeObject. */
  public UnaryCallSettings<ComposeObjectRequest, Object> composeObjectSettings() {
    return composeObjectSettings;
  }

  /** Returns the object with the settings used for calls to deleteObject. */
  public UnaryCallSettings<DeleteObjectRequest, Empty> deleteObjectSettings() {
    return deleteObjectSettings;
  }

  /** Returns the object with the settings used for calls to restoreObject. */
  public UnaryCallSettings<RestoreObjectRequest, Object> restoreObjectSettings() {
    return restoreObjectSettings;
  }

  /** Returns the object with the settings used for calls to cancelResumableWrite. */
  public UnaryCallSettings<CancelResumableWriteRequest, CancelResumableWriteResponse>
      cancelResumableWriteSettings() {
    return cancelResumableWriteSettings;
  }

  /** Returns the object with the settings used for calls to getObject. */
  public UnaryCallSettings<GetObjectRequest, Object> getObjectSettings() {
    return getObjectSettings;
  }

  /** Returns the object with the settings used for calls to readObject. */
  public ServerStreamingCallSettings<ReadObjectRequest, ReadObjectResponse> readObjectSettings() {
    return readObjectSettings;
  }

  /** Returns the object with the settings used for calls to bidiReadObject. */
  public StreamingCallSettings<BidiReadObjectRequest, BidiReadObjectResponse>
      bidiReadObjectSettings() {
    return bidiReadObjectSettings;
  }

  /** Returns the object with the settings used for calls to updateObject. */
  public UnaryCallSettings<UpdateObjectRequest, Object> updateObjectSettings() {
    return updateObjectSettings;
  }

  /** Returns the object with the settings used for calls to writeObject. */
  public StreamingCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectSettings() {
    return writeObjectSettings;
  }

  /** Returns the object with the settings used for calls to bidiWriteObject. */
  public StreamingCallSettings<BidiWriteObjectRequest, BidiWriteObjectResponse>
      bidiWriteObjectSettings() {
    return bidiWriteObjectSettings;
  }

  /** Returns the object with the settings used for calls to listObjects. */
  public PagedCallSettings<ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>
      listObjectsSettings() {
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

  /** Returns the object with the settings used for calls to moveObject. */
  public UnaryCallSettings<MoveObjectRequest, Object> moveObjectSettings() {
    return moveObjectSettings;
  }

  public StorageStub createStub() throws IOException {
    if (getTransportChannelProvider()
        .getTransportName()
        .equals(GrpcTransportChannel.getGrpcTransportName())) {
      return GrpcStorageStub.create(this);
    }
    throw new UnsupportedOperationException(
        String.format(
            "Transport not supported: %s", getTransportChannelProvider().getTransportName()));
  }

  /** Returns the default service name. */
  @Override
  public String getServiceName() {
    return "storage";
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return InstantiatingExecutorProvider.newBuilder();
  }

  /** Returns the default service endpoint. */
  @ObsoleteApi("Use getEndpoint() instead")
  public static String getDefaultEndpoint() {
    return "storage.googleapis.com:443";
  }

  /** Returns the default mTLS service endpoint. */
  public static String getDefaultMtlsEndpoint() {
    return "storage.mtls.googleapis.com:443";
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return DEFAULT_SERVICE_SCOPES;
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return GoogleCredentialsProvider.newBuilder()
        .setScopesToApply(DEFAULT_SERVICE_SCOPES)
        .setUseJwtAccessWithScope(true);
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return InstantiatingGrpcChannelProvider.newBuilder()
        .setMaxInboundMessageSize(Integer.MAX_VALUE);
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return defaultGrpcTransportProviderBuilder().build();
  }

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

    deleteBucketSettings = settingsBuilder.deleteBucketSettings().build();
    getBucketSettings = settingsBuilder.getBucketSettings().build();
    createBucketSettings = settingsBuilder.createBucketSettings().build();
    listBucketsSettings = settingsBuilder.listBucketsSettings().build();
    lockBucketRetentionPolicySettings = settingsBuilder.lockBucketRetentionPolicySettings().build();
    getIamPolicySettings = settingsBuilder.getIamPolicySettings().build();
    setIamPolicySettings = settingsBuilder.setIamPolicySettings().build();
    testIamPermissionsSettings = settingsBuilder.testIamPermissionsSettings().build();
    updateBucketSettings = settingsBuilder.updateBucketSettings().build();
    composeObjectSettings = settingsBuilder.composeObjectSettings().build();
    deleteObjectSettings = settingsBuilder.deleteObjectSettings().build();
    restoreObjectSettings = settingsBuilder.restoreObjectSettings().build();
    cancelResumableWriteSettings = settingsBuilder.cancelResumableWriteSettings().build();
    getObjectSettings = settingsBuilder.getObjectSettings().build();
    readObjectSettings = settingsBuilder.readObjectSettings().build();
    bidiReadObjectSettings = settingsBuilder.bidiReadObjectSettings().build();
    updateObjectSettings = settingsBuilder.updateObjectSettings().build();
    writeObjectSettings = settingsBuilder.writeObjectSettings().build();
    bidiWriteObjectSettings = settingsBuilder.bidiWriteObjectSettings().build();
    listObjectsSettings = settingsBuilder.listObjectsSettings().build();
    rewriteObjectSettings = settingsBuilder.rewriteObjectSettings().build();
    startResumableWriteSettings = settingsBuilder.startResumableWriteSettings().build();
    queryWriteStatusSettings = settingsBuilder.queryWriteStatusSettings().build();
    moveObjectSettings = settingsBuilder.moveObjectSettings().build();
  }

  /** Builder for StorageStubSettings. */
  public static class Builder extends StubSettings.Builder<StorageStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;
    private final UnaryCallSettings.Builder<DeleteBucketRequest, Empty> deleteBucketSettings;
    private final UnaryCallSettings.Builder<GetBucketRequest, Bucket> getBucketSettings;
    private final UnaryCallSettings.Builder<CreateBucketRequest, Bucket> createBucketSettings;
    private final PagedCallSettings.Builder<
            ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>
        listBucketsSettings;
    private final UnaryCallSettings.Builder<LockBucketRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicySettings;
    private final UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getIamPolicySettings;
    private final UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setIamPolicySettings;
    private final UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testIamPermissionsSettings;
    private final UnaryCallSettings.Builder<UpdateBucketRequest, Bucket> updateBucketSettings;
    private final UnaryCallSettings.Builder<ComposeObjectRequest, Object> composeObjectSettings;
    private final UnaryCallSettings.Builder<DeleteObjectRequest, Empty> deleteObjectSettings;
    private final UnaryCallSettings.Builder<RestoreObjectRequest, Object> restoreObjectSettings;
    private final UnaryCallSettings.Builder<
            CancelResumableWriteRequest, CancelResumableWriteResponse>
        cancelResumableWriteSettings;
    private final UnaryCallSettings.Builder<GetObjectRequest, Object> getObjectSettings;
    private final ServerStreamingCallSettings.Builder<ReadObjectRequest, ReadObjectResponse>
        readObjectSettings;
    private final StreamingCallSettings.Builder<BidiReadObjectRequest, BidiReadObjectResponse>
        bidiReadObjectSettings;
    private final UnaryCallSettings.Builder<UpdateObjectRequest, Object> updateObjectSettings;
    private final StreamingCallSettings.Builder<WriteObjectRequest, WriteObjectResponse>
        writeObjectSettings;
    private final StreamingCallSettings.Builder<BidiWriteObjectRequest, BidiWriteObjectResponse>
        bidiWriteObjectSettings;
    private final PagedCallSettings.Builder<
            ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>
        listObjectsSettings;
    private final UnaryCallSettings.Builder<RewriteObjectRequest, RewriteResponse>
        rewriteObjectSettings;
    private final UnaryCallSettings.Builder<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteSettings;
    private final UnaryCallSettings.Builder<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusSettings;
    private final UnaryCallSettings.Builder<MoveObjectRequest, Object> moveObjectSettings;
    private static final ImmutableMap<String, ImmutableSet<StatusCode.Code>>
        RETRYABLE_CODE_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, ImmutableSet<StatusCode.Code>> definitions =
          ImmutableMap.builder();
      definitions.put(
          "retry_policy_0_codes",
          ImmutableSet.copyOf(
              Lists.<StatusCode.Code>newArrayList(
                  StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)));
      RETRYABLE_CODE_DEFINITIONS = definitions.build();
    }

    private static final ImmutableMap<String, RetrySettings> RETRY_PARAM_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, RetrySettings> definitions = ImmutableMap.builder();
      RetrySettings settings = null;
      settings =
          RetrySettings.newBuilder()
              .setInitialRetryDelayDuration(Duration.ofMillis(1000L))
              .setRetryDelayMultiplier(2.0)
              .setMaxRetryDelayDuration(Duration.ofMillis(60000L))
              .setInitialRpcTimeoutDuration(Duration.ofMillis(60000L))
              .setRpcTimeoutMultiplier(1.0)
              .setMaxRpcTimeoutDuration(Duration.ofMillis(60000L))
              .setTotalTimeoutDuration(Duration.ofMillis(60000L))
              .build();
      definitions.put("retry_policy_0_params", settings);
      RETRY_PARAM_DEFINITIONS = definitions.build();
    }

    protected Builder() {
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(clientContext);

      deleteBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      createBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      listBucketsSettings = PagedCallSettings.newBuilder(LIST_BUCKETS_PAGE_STR_FACT);
      lockBucketRetentionPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      setIamPolicySettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      testIamPermissionsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      updateBucketSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      composeObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      deleteObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      restoreObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      cancelResumableWriteSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      readObjectSettings = ServerStreamingCallSettings.newBuilder();
      bidiReadObjectSettings = StreamingCallSettings.newBuilder();
      updateObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      writeObjectSettings = StreamingCallSettings.newBuilder();
      bidiWriteObjectSettings = StreamingCallSettings.newBuilder();
      listObjectsSettings = PagedCallSettings.newBuilder(LIST_OBJECTS_PAGE_STR_FACT);
      rewriteObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      startResumableWriteSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      queryWriteStatusSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      moveObjectSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              deleteBucketSettings,
              getBucketSettings,
              createBucketSettings,
              listBucketsSettings,
              lockBucketRetentionPolicySettings,
              getIamPolicySettings,
              setIamPolicySettings,
              testIamPermissionsSettings,
              updateBucketSettings,
              composeObjectSettings,
              deleteObjectSettings,
              restoreObjectSettings,
              cancelResumableWriteSettings,
              getObjectSettings,
              updateObjectSettings,
              listObjectsSettings,
              rewriteObjectSettings,
              startResumableWriteSettings,
              queryWriteStatusSettings,
              moveObjectSettings);
      initDefaults(this);
    }

    protected Builder(StorageStubSettings settings) {
      super(settings);

      deleteBucketSettings = settings.deleteBucketSettings.toBuilder();
      getBucketSettings = settings.getBucketSettings.toBuilder();
      createBucketSettings = settings.createBucketSettings.toBuilder();
      listBucketsSettings = settings.listBucketsSettings.toBuilder();
      lockBucketRetentionPolicySettings = settings.lockBucketRetentionPolicySettings.toBuilder();
      getIamPolicySettings = settings.getIamPolicySettings.toBuilder();
      setIamPolicySettings = settings.setIamPolicySettings.toBuilder();
      testIamPermissionsSettings = settings.testIamPermissionsSettings.toBuilder();
      updateBucketSettings = settings.updateBucketSettings.toBuilder();
      composeObjectSettings = settings.composeObjectSettings.toBuilder();
      deleteObjectSettings = settings.deleteObjectSettings.toBuilder();
      restoreObjectSettings = settings.restoreObjectSettings.toBuilder();
      cancelResumableWriteSettings = settings.cancelResumableWriteSettings.toBuilder();
      getObjectSettings = settings.getObjectSettings.toBuilder();
      readObjectSettings = settings.readObjectSettings.toBuilder();
      bidiReadObjectSettings = settings.bidiReadObjectSettings.toBuilder();
      updateObjectSettings = settings.updateObjectSettings.toBuilder();
      writeObjectSettings = settings.writeObjectSettings.toBuilder();
      bidiWriteObjectSettings = settings.bidiWriteObjectSettings.toBuilder();
      listObjectsSettings = settings.listObjectsSettings.toBuilder();
      rewriteObjectSettings = settings.rewriteObjectSettings.toBuilder();
      startResumableWriteSettings = settings.startResumableWriteSettings.toBuilder();
      queryWriteStatusSettings = settings.queryWriteStatusSettings.toBuilder();
      moveObjectSettings = settings.moveObjectSettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              deleteBucketSettings,
              getBucketSettings,
              createBucketSettings,
              listBucketsSettings,
              lockBucketRetentionPolicySettings,
              getIamPolicySettings,
              setIamPolicySettings,
              testIamPermissionsSettings,
              updateBucketSettings,
              composeObjectSettings,
              deleteObjectSettings,
              restoreObjectSettings,
              cancelResumableWriteSettings,
              getObjectSettings,
              updateObjectSettings,
              listObjectsSettings,
              rewriteObjectSettings,
              startResumableWriteSettings,
              queryWriteStatusSettings,
              moveObjectSettings);
    }

    private static Builder createDefault() {
      Builder builder = new Builder(((ClientContext) null));

      builder.setTransportChannelProvider(defaultTransportChannelProvider());
      builder.setCredentialsProvider(defaultCredentialsProviderBuilder().build());
      builder.setInternalHeaderProvider(defaultApiClientHeaderProviderBuilder().build());
      builder.setMtlsEndpoint(getDefaultMtlsEndpoint());
      builder.setSwitchToMtlsEndpointAllowed(true);

      return initDefaults(builder);
    }

    private static Builder initDefaults(Builder builder) {
      builder
          .deleteBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .createBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .listBucketsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .lockBucketRetentionPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .setIamPolicySettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .testIamPermissionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .updateBucketSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .composeObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .deleteObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .restoreObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .cancelResumableWriteSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .readObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .updateObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .listObjectsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .rewriteObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .startResumableWriteSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .queryWriteStatusSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .moveObjectSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      return builder;
    }

    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) {
      super.applyToAllUnaryMethods(unaryMethodSettingsBuilders, settingsUpdater);
      return this;
    }

    public ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders() {
      return unaryMethodSettingsBuilders;
    }

    /** Returns the builder for the settings used for calls to deleteBucket. */
    public UnaryCallSettings.Builder<DeleteBucketRequest, Empty> deleteBucketSettings() {
      return deleteBucketSettings;
    }

    /** Returns the builder for the settings used for calls to getBucket. */
    public UnaryCallSettings.Builder<GetBucketRequest, Bucket> getBucketSettings() {
      return getBucketSettings;
    }

    /** Returns the builder for the settings used for calls to createBucket. */
    public UnaryCallSettings.Builder<CreateBucketRequest, Bucket> createBucketSettings() {
      return createBucketSettings;
    }

    /** Returns the builder for the settings used for calls to listBuckets. */
    public PagedCallSettings.Builder<
            ListBucketsRequest, ListBucketsResponse, ListBucketsPagedResponse>
        listBucketsSettings() {
      return listBucketsSettings;
    }

    /** Returns the builder for the settings used for calls to lockBucketRetentionPolicy. */
    public UnaryCallSettings.Builder<LockBucketRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicySettings() {
      return lockBucketRetentionPolicySettings;
    }

    /** Returns the builder for the settings used for calls to getIamPolicy. */
    public UnaryCallSettings.Builder<GetIamPolicyRequest, Policy> getIamPolicySettings() {
      return getIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to setIamPolicy. */
    public UnaryCallSettings.Builder<SetIamPolicyRequest, Policy> setIamPolicySettings() {
      return setIamPolicySettings;
    }

    /** Returns the builder for the settings used for calls to testIamPermissions. */
    public UnaryCallSettings.Builder<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testIamPermissionsSettings() {
      return testIamPermissionsSettings;
    }

    /** Returns the builder for the settings used for calls to updateBucket. */
    public UnaryCallSettings.Builder<UpdateBucketRequest, Bucket> updateBucketSettings() {
      return updateBucketSettings;
    }

    /** Returns the builder for the settings used for calls to composeObject. */
    public UnaryCallSettings.Builder<ComposeObjectRequest, Object> composeObjectSettings() {
      return composeObjectSettings;
    }

    /** Returns the builder for the settings used for calls to deleteObject. */
    public UnaryCallSettings.Builder<DeleteObjectRequest, Empty> deleteObjectSettings() {
      return deleteObjectSettings;
    }

    /** Returns the builder for the settings used for calls to restoreObject. */
    public UnaryCallSettings.Builder<RestoreObjectRequest, Object> restoreObjectSettings() {
      return restoreObjectSettings;
    }

    /** Returns the builder for the settings used for calls to cancelResumableWrite. */
    public UnaryCallSettings.Builder<CancelResumableWriteRequest, CancelResumableWriteResponse>
        cancelResumableWriteSettings() {
      return cancelResumableWriteSettings;
    }

    /** Returns the builder for the settings used for calls to getObject. */
    public UnaryCallSettings.Builder<GetObjectRequest, Object> getObjectSettings() {
      return getObjectSettings;
    }

    /** Returns the builder for the settings used for calls to readObject. */
    public ServerStreamingCallSettings.Builder<ReadObjectRequest, ReadObjectResponse>
        readObjectSettings() {
      return readObjectSettings;
    }

    /** Returns the builder for the settings used for calls to bidiReadObject. */
    public StreamingCallSettings.Builder<BidiReadObjectRequest, BidiReadObjectResponse>
        bidiReadObjectSettings() {
      return bidiReadObjectSettings;
    }

    /** Returns the builder for the settings used for calls to updateObject. */
    public UnaryCallSettings.Builder<UpdateObjectRequest, Object> updateObjectSettings() {
      return updateObjectSettings;
    }

    /** Returns the builder for the settings used for calls to writeObject. */
    public StreamingCallSettings.Builder<WriteObjectRequest, WriteObjectResponse>
        writeObjectSettings() {
      return writeObjectSettings;
    }

    /** Returns the builder for the settings used for calls to bidiWriteObject. */
    public StreamingCallSettings.Builder<BidiWriteObjectRequest, BidiWriteObjectResponse>
        bidiWriteObjectSettings() {
      return bidiWriteObjectSettings;
    }

    /** Returns the builder for the settings used for calls to listObjects. */
    public PagedCallSettings.Builder<
            ListObjectsRequest, ListObjectsResponse, ListObjectsPagedResponse>
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

    /** Returns the builder for the settings used for calls to moveObject. */
    public UnaryCallSettings.Builder<MoveObjectRequest, Object> moveObjectSettings() {
      return moveObjectSettings;
    }

    @Override
    public StorageStubSettings build() throws IOException {
      return new StorageStubSettings(this);
    }
  }
}
