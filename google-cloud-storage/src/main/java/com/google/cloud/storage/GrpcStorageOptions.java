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
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.StreamResumptionStrategy;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceFactory;
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

@BetaApi
@TransportCompatibility(Transport.GRPC)
public final class GrpcStorageOptions extends StorageOptions
    implements Retrying.RetryingDependencies {

  private static final long serialVersionUID = 4165732727259088956L;
  private static final String GCS_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";
  private static final Set<String> SCOPES = ImmutableSet.of(GCS_SCOPE);
  private static final String DEFAULT_HOST = "https://storage.googleapis.com";

  private final GrpcRetryAlgorithmManager retryAlgorithmManager;

  @BetaApi
  public GrpcStorageOptions(Builder builder, GrpcStorageDefaults serviceDefaults) {
    super(builder, serviceDefaults);
    this.retryAlgorithmManager =
        new GrpcRetryAlgorithmManager(
            MoreObjects.firstNonNull(
                builder.storageRetryStrategy, serviceDefaults.getStorageRetryStrategy()));
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
  StorageSettings getStorageSettings() throws IOException {
    URI uri = URI.create(getHost());
    String scheme = uri.getScheme();
    int port = uri.getPort() > 0 ? uri.getPort() : scheme.equals("http") ? 80 : 443;
    String endpoint = String.format("%s:%d", uri.getHost(), port);

    CredentialsProvider credentialsProvider;
    if (credentials instanceof NoCredentials) {
      credentialsProvider = NoCredentialsProvider.create();
    } else {
      credentialsProvider = FixedCredentialsProvider.create(credentials);
    }

    StorageSettings.Builder builder =
        StorageSettings.newBuilder()
            .setEndpoint(endpoint)
            .setCredentialsProvider(credentialsProvider);

    if (scheme.equals("http")) {
      builder.setTransportChannelProvider(
          com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.newBuilder()
              .setEndpoint(endpoint)
              .setChannelConfigurator(ManagedChannelBuilder::usePlaintext)
              .build());
    }
    RetrySettings retrySettings = getRetrySettings();
    RetrySettings attemptOnce = retrySettings.toBuilder().setMaxAttempts(1).build();
    // all retries for unary methods are handled at a different level
    builder.applyToAllUnaryMethods(
        input -> {
          input.setRetrySettings(attemptOnce);
          return null;
        });
    // for ReadObject we are configuring the server stream handling to do its own retries, so wire
    // things through. Retryable codes will be controlled closer to the use site as idempotency
    // considerations need to be made.
    builder
        .readObjectSettings()
        .setRetrySettings(retrySettings)
        // even though we might want to default to the empty set for retryable codes, don't ever
        // actually do this. Doing so prevents any retry capability from being wired into the stream
        // pipeline, ever.
        // For our use, we will always set it one way or the other to ensure it's appropriate
        // DO NOT: .setRetryableCodes(Collections.emptySet())
        .setResumptionStrategy(new ReadObjectResumptionStrategy());
    return builder.build();
  }

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
    return obj instanceof StorageOptions && baseEquals((StorageOptions) obj);
  }

  @BetaApi
  public static GrpcStorageOptions.Builder newBuilder() {
    return new GrpcStorageOptions.Builder().setHost(DEFAULT_HOST);
  }

  @BetaApi
  public static GrpcStorageOptions getDefaultInstance() {
    return newBuilder().build();
  }

  @BetaApi
  public static GrpcStorageOptions.GrpcStorageDefaults defaults() {
    return GrpcStorageOptions.GrpcStorageDefaults.INSTANCE;
  }

  @BetaApi
  public static class Builder extends StorageOptions.Builder {

    private StorageRetryStrategy storageRetryStrategy;

    Builder() {}

    Builder(StorageOptions options) {
      super(options);
    }

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

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceFactory(
        ServiceFactory<Storage, StorageOptions> serviceFactory) {
      super.setServiceFactory(serviceFactory);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClock(ApiClock clock) {
      super.setClock(clock);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setProjectId(String projectId) {
      super.setProjectId(projectId);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHost(String host) {
      super.setHost(host);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setCredentials(Credentials credentials) {
      super.setCredentials(credentials);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setRetrySettings(RetrySettings retrySettings) {
      super.setRetrySettings(retrySettings);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setServiceRpcFactory(
        ServiceRpcFactory<StorageOptions> serviceRpcFactory) {
      super.setServiceRpcFactory(serviceRpcFactory);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setHeaderProvider(HeaderProvider headerProvider) {
      super.setHeaderProvider(headerProvider);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setClientLibToken(String clientLibToken) {
      super.setClientLibToken(clientLibToken);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions.Builder setQuotaProjectId(String quotaProjectId) {
      super.setQuotaProjectId(quotaProjectId);
      return this;
    }

    @BetaApi
    @Override
    public GrpcStorageOptions build() {
      return new GrpcStorageOptions(this, defaults());
    }
  }

  @BetaApi
  public static final class GrpcStorageDefaults extends StorageDefaults {
    static final GrpcStorageDefaults INSTANCE = new GrpcStorageOptions.GrpcStorageDefaults();
    static final StorageFactory STORAGE_FACTORY = new GrpcStorageFactory();
    static final StorageRpcFactory STORAGE_RPC_FACTORY = new GrpcStorageRpcFactory();

    private GrpcStorageDefaults() {}

    @BetaApi
    @Override
    public StorageFactory getDefaultServiceFactory() {
      return STORAGE_FACTORY;
    }

    @BetaApi
    @Override
    public StorageRpcFactory getDefaultRpcFactory() {
      return STORAGE_RPC_FACTORY;
    }

    @BetaApi
    @Override
    public GrpcTransportOptions getDefaultTransportOptions() {
      return GrpcTransportOptions.newBuilder().build();
    }

    @BetaApi
    public StorageRetryStrategy getStorageRetryStrategy() {
      return StorageRetryStrategy.getDefaultStorageRetryStrategy();
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   * GrpcStorageOptions.defaults().getDefaultServiceFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultServiceFactory()
   */
  @InternalApi
  public static class GrpcStorageFactory implements StorageFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
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
     */
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
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
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
   *
   * <p>To access an instance of this class instead use {@link
   * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
   *
   * @see GrpcStorageOptions#defaults()
   * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
   */
  @InternalApi
  public static class GrpcStorageRpcFactory implements StorageRpcFactory {

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
     *
     * <p>To access an instance of this class instead use {@link
     * GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * GrpcStorageOptions.defaults().getDefaultRpcFactory()}.
     *
     * @see GrpcStorageOptions#defaults()
     * @see GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     * @deprecated instead use {@link GrpcStorageOptions.GrpcStorageDefaults#getDefaultRpcFactory()
     *     GrpcStorageOptions.defaults().getDefaultRpcFactory()}
     */
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
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
}
