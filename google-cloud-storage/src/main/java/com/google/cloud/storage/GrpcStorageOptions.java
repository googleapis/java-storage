/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage;

import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiClock;
import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.StreamResumptionStrategy;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.NoHeaderProvider;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceFactory;
import com.google.cloud.ServiceOptions;
import com.google.cloud.ServiceRpc;
import com.google.cloud.TransportOptions;
import com.google.cloud.grpc.GrpcTransportOptions;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageSettings;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.threeten.bp.Duration;

/** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
@BetaApi
@TransportCompatibility(Transport.GRPC)
public final class GrpcStorageOptions extends StorageOptions
    implements Retrying.RetryingDependencies {

  private static final long serialVersionUID = -4499446543857945349L;
  private static final String GCS_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";
  private static final Set<String> SCOPES = ImmutableSet.of(GCS_SCOPE);
  private static final String DEFAULT_HOST = "https://storage.googleapis.com";

  private final GrpcRetryAlgorithmManager retryAlgorithmManager;
  private final Duration terminationAwaitDuration;
  private final boolean attemptDirectPath;

  private GrpcStorageOptions(Builder builder, GrpcStorageDefaults serviceDefaults) {
    super(builder, serviceDefaults);
    this.retryAlgorithmManager =
        new GrpcRetryAlgorithmManager(
            MoreObjects.firstNonNull(
                builder.storageRetryStrategy, serviceDefaults.getStorageRetryStrategy()));
    this.terminationAwaitDuration =
        MoreObjects.firstNonNull(
            builder.terminationAwaitDuration, serviceDefaults.getTerminationAwaitDuration());
    this.attemptDirectPath = builder.attemptDirectPath;
  }

  @Override
  protected Set<String> getScopes() {
    return SCOPES;
  }

  @InternalApi
  GrpcRetryAlgorithmManager getRetryAlgorithmManager() {
    return retryAlgorithmManager;
  }

  @InternalApi
  Duration getTerminationAwaitDuration() {
    return terminationAwaitDuration;
  }

  @InternalApi
  StorageSettings getStorageSettings() throws IOException {
    String endpoint = getHost();
    URI uri = URI.create(endpoint);
    String scheme = uri.getScheme();
    int port = uri.getPort();
    // Gax routes the endpoint into a method which can't handle schemes, unless for direct path
    // try and strip here if we can
    switch (scheme) {
      case "http":
        endpoint = String.format("%s:%s", uri.getHost(), port > 0 ? port : 80);
        break;
      case "https":
        endpoint = String.format("%s:%s", uri.getHost(), port > 0 ? port : 443);
        break;
    }

    CredentialsProvider credentialsProvider;
    if (credentials instanceof NoCredentials) {
      credentialsProvider = NoCredentialsProvider.create();
    } else {
      credentialsProvider = FixedCredentialsProvider.create(credentials);
    }

    HeaderProvider internalHeaderProvider =
        StorageSettings.defaultApiClientHeaderProviderBuilder()
            .setClientLibToken(
                ServiceOptions.getGoogApiClientLibName(),
                GaxProperties.getLibraryVersion(this.getClass()))
            .build();

    StorageSettings.Builder builder =
        new StorageSettingsBuilder(StorageSettings.newBuilder().build())
            .setInternalHeaderProvider(internalHeaderProvider)
            .setEndpoint(endpoint)
            .setCredentialsProvider(credentialsProvider)
            .setClock(getClock());

    builder.setHeaderProvider(this.getMergedHeaderProvider(new NoHeaderProvider()));

    InstantiatingGrpcChannelProvider.Builder channelProviderBuilder =
        InstantiatingGrpcChannelProvider.newBuilder()
            .setEndpoint(endpoint)
            .setAttemptDirectPath(attemptDirectPath);

    if (scheme.equals("http")) {
      channelProviderBuilder.setChannelConfigurator(ManagedChannelBuilder::usePlaintext);
    }
    builder.setTransportChannelProvider(channelProviderBuilder.build());
    RetrySettings baseRetrySettings = getRetrySettings();
    RetrySettings readRetrySettings =
        baseRetrySettings
            .toBuilder()
            // when performing a read via ReadObject, the ServerStream will have a default relative
            // deadline set of `requestStartTime() + totalTimeout`, meaning if the specified
            // RetrySettings have a totalTimeout of 10 seconds -- which should be plenty for
            // metadata RPCs -- the entire ReadObject stream would need to complete within 10
            // seconds.
            // To allow read streams to have longer lifespans, crank up their timeouts, instead rely
            // on idleTimeout below.
            .setLogicalTimeout(Duration.ofDays(28))
            .build();
    Duration totalTimeout = baseRetrySettings.getTotalTimeout();
    Set<Code> startResumableWriteRetryableCodes =
        builder.startResumableWriteSettings().getRetryableCodes();

    // retries for unary methods are generally handled at a different level, except
    // StartResumableWrite
    builder.applyToAllUnaryMethods(
        input -> {
          input.setSimpleTimeoutNoRetries(totalTimeout);
          return null;
        });

    // configure the settings for StartResumableWrite
    builder
        .startResumableWriteSettings()
        .setRetrySettings(baseRetrySettings)
        .setRetryableCodes(startResumableWriteRetryableCodes);
    // for ReadObject we are configuring the server stream handling to do its own retries, so wire
    // things through. Retryable codes will be controlled closer to the use site as idempotency
    // considerations need to be made.
    builder
        .readObjectSettings()
        .setRetrySettings(readRetrySettings)
        // even though we might want to default to the empty set for retryable codes, don't ever
        // actually do this. Doing so prevents any retry capability from being wired into the stream
        // pipeline, ever.
        // For our use, we will always set it one way or the other to ensure it's appropriate
        // DO NOT: .setRetryableCodes(Collections.emptySet())
        .setResumptionStrategy(new ReadObjectResumptionStrategy())
        // for reads, the stream can be held open for a long time in order to read all bytes,
        // this is totally valid. instead we want to monitor if the stream is doing work and if not
        // timeout.
        .setIdleTimeout(totalTimeout);
    return builder.build();
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  @Override
  public GrpcStorageOptions.Builder toBuilder() {
    return new GrpcStorageOptions.Builder(this);
  }

  @Override
  public int hashCode() {
    return baseHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof GrpcStorageOptions && baseEquals((GrpcStorageOptions) obj);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions.Builder newBuilder() {
    return new GrpcStorageOptions.Builder().setHost(DEFAULT_HOST);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions getDefaultInstance() {
    return newBuilder().build();
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static GrpcStorageOptions.GrpcStorageDefaults defaults() {
    return GrpcStorageOptions.GrpcStorageDefaults.INSTANCE;
  }

  // since our new GrpcStorageImpl can "close" we need to help ServiceOptions know whether it can
  // use it's cached instance.
  @Override
  protected boolean shouldRefreshService(Storage cachedService) {
    if (cachedService instanceof GrpcStorageImpl) {
      GrpcStorageImpl service = (GrpcStorageImpl) cachedService;
      return service.isClosed();
    }
    return super.shouldRefreshService(cachedService);
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static final class Builder extends StorageOptions.Builder {

    private StorageRetryStrategy storageRetryStrategy;
    private Duration terminationAwaitDuration;
    private boolean attemptDirectPath = GrpcStorageDefaults.INSTANCE.isAttemptDirectPath();

    Builder() {}

    Builder(StorageOptions options) {
      super(options);
    }

    /**
     * Set the maximum duration in which to await termination of any outstanding requests when
     * calling {@link Storage#close()}
     *
     * @param terminationAwaitDuration a non-null Duration to use
     * @return the builder
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public Builder setTerminationAwaitDuration(Duration terminationAwaitDuration) {
      this.terminationAwaitDuration =
          requireNonNull(terminationAwaitDuration, "terminationAwaitDuration must be non null");
      return this;
    }

    /**
     * Option which signifies the client should attempt to connect to gcs via Direct Path.
     *
     * <p>In order to use direct path, both this option must be true and the environment variable
     * (not system property) {@code GOOGLE_CLOUD_ENABLE_DIRECT_PATH_XDS} must be true.
     *
     * <p><i>NOTE</i>There is no need to specify a new endpoint via {@link #setHost(String)} as the
     * underlying code will translate the normal {@code https://storage.googleapis.com:443} into the
     * proper Direct Path URI for you.
     *
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setAttemptDirectPath(boolean attemptDirectPath) {
      this.attemptDirectPath = attemptDirectPath;
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setTransportOptions(TransportOptions transportOptions) {
      if (!(transportOptions instanceof GrpcTransportOptions)) {
        throw new IllegalArgumentException("Only gRPC transport is allowed.");
      }
      super.setTransportOptions(transportOptions);
      return this;
    }

    /**
     * Override the default retry handling behavior with an alternate strategy.
     *
     * @param storageRetryStrategy a non-null storageRetryStrategy to use
     * @return the builder
     * @see StorageRetryStrategy#getDefaultStorageRetryStrategy()
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public GrpcStorageOptions.Builder setStorageRetryStrategy(
        StorageRetryStrategy storageRetryStrategy) {
      this.storageRetryStrategy =
          requireNonNull(storageRetryStrategy, "storageRetryStrategy must be non null");
      return this;
    }

    @Override
    protected GrpcStorageOptions.Builder self() {
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceFactory(
        ServiceFactory<Storage, StorageOptions> serviceFactory) {
      super.setServiceFactory(serviceFactory);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClock(ApiClock clock) {
      super.setClock(clock);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setProjectId(String projectId) {
      super.setProjectId(projectId);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHost(String host) {
      super.setHost(host);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setCredentials(Credentials credentials) {
      super.setCredentials(credentials);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setRetrySettings(RetrySettings retrySettings) {
      super.setRetrySettings(retrySettings);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceRpcFactory(
        ServiceRpcFactory<StorageOptions> serviceRpcFactory) {
      throw new UnsupportedOperationException(
          "GrpcStorageOptions does not support setting a custom instance of ServiceRpcFactory");
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHeaderProvider(HeaderProvider headerProvider) {
      super.setHeaderProvider(headerProvider);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClientLibToken(String clientLibToken) {
      super.setClientLibToken(clientLibToken);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setQuotaProjectId(String quotaProjectId) {
      super.setQuotaProjectId(quotaProjectId);
      return this;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcStorageOptions build() {
      return new GrpcStorageOptions(this, defaults());
    }
  }

  /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
  @BetaApi
  public static final class GrpcStorageDefaults extends StorageDefaults {
    static final GrpcStorageDefaults INSTANCE = new GrpcStorageOptions.GrpcStorageDefaults();
    static final StorageFactory STORAGE_FACTORY = new GrpcStorageFactory();
    static final StorageRpcFactory STORAGE_RPC_FACTORY = new GrpcStorageRpcFactory();

    private GrpcStorageDefaults() {}

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public StorageFactory getDefaultServiceFactory() {
      return STORAGE_FACTORY;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public StorageRpcFactory getDefaultRpcFactory() {
      return STORAGE_RPC_FACTORY;
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    @Override
    public GrpcTransportOptions getDefaultTransportOptions() {
      return GrpcTransportOptions.newBuilder().build();
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public StorageRetryStrategy getStorageRetryStrategy() {
      return StorageRetryStrategy.getDefaultStorageRetryStrategy();
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public Duration getTerminationAwaitDuration() {
      return Duration.ofMinutes(1);
    }

    /** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
    @BetaApi
    public boolean isAttemptDirectPath() {
      return false;
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}
   * compatibility in {@link com.google.cloud.ServiceOptions}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   * GrpcStorageOptions.defaults().getDefaultServiceFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @InternalApi
  @BetaApi
  public static class GrpcStorageFactory implements StorageFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}
     * compatibility in {@link com.google.cloud.ServiceOptions}.
     *
     * <p>To access an instance of this class instead use {@link
     * GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     * GrpcStorageOptions.defaults().getDefaultServiceFactory()}.
     *
     * @see GrpcStorageOptions#defaults()
     * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     * @deprecated instead use {@link
     *     GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
     *     GrpcStorageOptions.defaults().getDefaultServiceFactory()}
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    @BetaApi
    public GrpcStorageFactory() {}

    @Override
    public Storage create(StorageOptions options) {
      if (options instanceof GrpcStorageOptions) {
        GrpcStorageOptions grpcStorageOptions = (GrpcStorageOptions) options;
        try {
          StorageSettings storageSettings = grpcStorageOptions.getStorageSettings();
          return new GrpcStorageImpl(grpcStorageOptions, StorageClient.create(storageSettings));
        } catch (IOException e) {
          throw new IllegalStateException(
              "Unable to instantiate gRPC com.google.cloud.storage.Storage client.", e);
        }
      } else {
        throw new IllegalArgumentException("Only GrpcStorageOptions supported");
      }
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}
   * compatibility in {@link com.google.cloud.ServiceOptions}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @InternalApi
  @BetaApi
  @Deprecated
  public static class GrpcStorageRpcFactory implements StorageRpcFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}
     * compatibility in {@link com.google.cloud.ServiceOptions}.
     *
     * <p>To access an instance of this class instead use {@link
     * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
     *
     * @see GrpcStorageOptions#defaults()
     * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * @deprecated instead use {@link GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     *     GrpcStorageOptions.defaults().getDefaultRpcFactory()}
     * @since 2.14.0 This new api is in preview and is subject to breaking changes.
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    @BetaApi
    public GrpcStorageRpcFactory() {}

    @Override
    public ServiceRpc create(StorageOptions options) {
      throw new IllegalStateException("No supported for grpc");
    }
  }

  // TODO: See if we can change gax to allow shifting this to callable.withContext so it doesn't
  //   have to be set globally
  private static class ReadObjectResumptionStrategy
      implements StreamResumptionStrategy<ReadObjectRequest, ReadObjectResponse> {
    private long readOffset = 0;

    @NonNull
    @Override
    public StreamResumptionStrategy<ReadObjectRequest, ReadObjectResponse> createNew() {
      return new ReadObjectResumptionStrategy();
    }

    @NonNull
    @Override
    public ReadObjectResponse processResponse(ReadObjectResponse response) {
      readOffset += response.getChecksummedData().getContent().size();
      return response;
    }

    @Nullable
    @Override
    public ReadObjectRequest getResumeRequest(ReadObjectRequest originalRequest) {
      if (readOffset != 0) {
        return originalRequest.toBuilder().setReadOffset(readOffset).build();
      }
      return originalRequest;
    }

    @Override
    public boolean canResume() {
      return true;
    }
  }

  // setInternalHeaderProvider is protected so we need to open its scope in order to set it
  // we are adding an entry for gccl which is set via this provider
  private static final class StorageSettingsBuilder extends StorageSettings.Builder {
    private StorageSettingsBuilder(StorageSettings settings) {
      super(settings);
    }

    @Override
    protected StorageSettings.Builder setInternalHeaderProvider(
        HeaderProvider internalHeaderProvider) {
      return super.setInternalHeaderProvider(internalHeaderProvider);
    }
  }
}
