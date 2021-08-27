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

package com.google.storage.v2.stub;

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
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.threeten.bp.Duration;

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
 * <p>For example, to set the total timeout of startResumableWrite to 30 seconds:
 *
 * <pre>{@code
 * StorageStubSettings.Builder storageSettingsBuilder = StorageStubSettings.newBuilder();
 * storageSettingsBuilder
 *     .startResumableWriteSettings()
 *     .setRetrySettings(
 *         storageSettingsBuilder
 *             .startResumableWriteSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * StorageStubSettings storageSettings = storageSettingsBuilder.build();
 * }</pre>
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

  private final ServerStreamingCallSettings<ReadObjectRequest, ReadObjectResponse>
      readObjectSettings;
  private final StreamingCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectSettings;
  private final UnaryCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteSettings;
  private final UnaryCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusSettings;

  /** Returns the object with the settings used for calls to readObject. */
  public ServerStreamingCallSettings<ReadObjectRequest, ReadObjectResponse> readObjectSettings() {
    return readObjectSettings;
  }

  /** Returns the object with the settings used for calls to writeObject. */
  public StreamingCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectSettings() {
    return writeObjectSettings;
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

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
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

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return InstantiatingExecutorProvider.newBuilder();
  }

  /** Returns the default service endpoint. */
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

    readObjectSettings = settingsBuilder.readObjectSettings().build();
    writeObjectSettings = settingsBuilder.writeObjectSettings().build();
    startResumableWriteSettings = settingsBuilder.startResumableWriteSettings().build();
    queryWriteStatusSettings = settingsBuilder.queryWriteStatusSettings().build();
  }

  /** Builder for StorageStubSettings. */
  public static class Builder extends StubSettings.Builder<StorageStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;
    private final ServerStreamingCallSettings.Builder<ReadObjectRequest, ReadObjectResponse>
        readObjectSettings;
    private final StreamingCallSettings.Builder<WriteObjectRequest, WriteObjectResponse>
        writeObjectSettings;
    private final UnaryCallSettings.Builder<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteSettings;
    private final UnaryCallSettings.Builder<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusSettings;
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
              .setInitialRetryDelay(Duration.ofMillis(1000L))
              .setRetryDelayMultiplier(2.0)
              .setMaxRetryDelay(Duration.ofMillis(60000L))
              .setInitialRpcTimeout(Duration.ofMillis(60000L))
              .setRpcTimeoutMultiplier(1.0)
              .setMaxRpcTimeout(Duration.ofMillis(60000L))
              .setTotalTimeout(Duration.ofMillis(60000L))
              .build();
      definitions.put("retry_policy_0_params", settings);
      RETRY_PARAM_DEFINITIONS = definitions.build();
    }

    protected Builder() {
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(clientContext);

      readObjectSettings = ServerStreamingCallSettings.newBuilder();
      writeObjectSettings = StreamingCallSettings.newBuilder();
      startResumableWriteSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      queryWriteStatusSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              startResumableWriteSettings, queryWriteStatusSettings);
      initDefaults(this);
    }

    protected Builder(StorageStubSettings settings) {
      super(settings);

      readObjectSettings = settings.readObjectSettings.toBuilder();
      writeObjectSettings = settings.writeObjectSettings.toBuilder();
      startResumableWriteSettings = settings.startResumableWriteSettings.toBuilder();
      queryWriteStatusSettings = settings.queryWriteStatusSettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              startResumableWriteSettings, queryWriteStatusSettings);
    }

    private static Builder createDefault() {
      Builder builder = new Builder(((ClientContext) null));

      builder.setTransportChannelProvider(defaultTransportChannelProvider());
      builder.setCredentialsProvider(defaultCredentialsProviderBuilder().build());
      builder.setInternalHeaderProvider(defaultApiClientHeaderProviderBuilder().build());
      builder.setEndpoint(getDefaultEndpoint());
      builder.setMtlsEndpoint(getDefaultMtlsEndpoint());
      builder.setSwitchToMtlsEndpointAllowed(true);

      return initDefaults(builder);
    }

    private static Builder initDefaults(Builder builder) {
      builder
          .readObjectSettings()
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

    /** Returns the builder for the settings used for calls to readObject. */
    public ServerStreamingCallSettings.Builder<ReadObjectRequest, ReadObjectResponse>
        readObjectSettings() {
      return readObjectSettings;
    }

    /** Returns the builder for the settings used for calls to writeObject. */
    public StreamingCallSettings.Builder<WriteObjectRequest, WriteObjectResponse>
        writeObjectSettings() {
      return writeObjectSettings;
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

    @Override
    public StorageStubSettings build() throws IOException {
      return new StorageStubSettings(this);
    }
  }
}
