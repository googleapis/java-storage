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

package com.google.storage.v2;

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
import com.google.storage.v2.stub.StorageStubSettings;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
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
 * <p>For example, to set the total timeout of startResumableWrite to 30 seconds:
 *
 * <pre>{@code
 * StorageSettings.Builder storageSettingsBuilder = StorageSettings.newBuilder();
 * storageSettingsBuilder
 *     .startResumableWriteSettings()
 *     .setRetrySettings(
 *         storageSettingsBuilder
 *             .startResumableWriteSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * StorageSettings storageSettings = storageSettingsBuilder.build();
 * }</pre>
 */
@Generated("by gapic-generator-java")
public class StorageSettings extends ClientSettings<StorageSettings> {

  /** Returns the object with the settings used for calls to readObject. */
  public ServerStreamingCallSettings<ReadObjectRequest, ReadObjectResponse> readObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).readObjectSettings();
  }

  /** Returns the object with the settings used for calls to writeObject. */
  public StreamingCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectSettings() {
    return ((StorageStubSettings) getStubSettings()).writeObjectSettings();
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
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(StorageStubSettings.newBuilder(clientContext));
    }

    protected Builder(StorageSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(StorageStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    private static Builder createDefault() {
      return new Builder(StorageStubSettings.newBuilder());
    }

    public StorageStubSettings.Builder getStubSettingsBuilder() {
      return ((StorageStubSettings.Builder) getStubSettings());
    }

    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) {
      super.applyToAllUnaryMethods(
          getStubSettingsBuilder().unaryMethodSettingsBuilders(), settingsUpdater);
      return this;
    }

    /** Returns the builder for the settings used for calls to readObject. */
    public ServerStreamingCallSettings.Builder<ReadObjectRequest, ReadObjectResponse>
        readObjectSettings() {
      return getStubSettingsBuilder().readObjectSettings();
    }

    /** Returns the builder for the settings used for calls to writeObject. */
    public StreamingCallSettings.Builder<WriteObjectRequest, WriteObjectResponse>
        writeObjectSettings() {
      return getStubSettingsBuilder().writeObjectSettings();
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

    @Override
    public StorageSettings build() throws IOException {
      return new StorageSettings(this);
    }
  }
}
