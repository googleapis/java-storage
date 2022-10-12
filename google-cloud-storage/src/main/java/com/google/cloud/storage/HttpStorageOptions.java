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
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.auth.Credentials;
import com.google.cloud.ServiceFactory;
import com.google.cloud.ServiceRpc;
import com.google.cloud.TransportOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.HttpStorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Set;

/** @since 2.14.0 This new api is in preview and is subject to breaking changes. */
@BetaApi
@TransportCompatibility(Transport.HTTP)
// non-final because of mocking frameworks
public class HttpStorageOptions extends StorageOptions {

  private static final long serialVersionUID = -5302637952911052045L;
  private static final String API_SHORT_NAME = "Storage";
  private static final String GCS_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";
  private static final Set<String> SCOPES = ImmutableSet.of(GCS_SCOPE);
  private static final String DEFAULT_HOST = "https://storage.googleapis.com";

  private final HttpRetryAlgorithmManager retryAlgorithmManager;

  private HttpStorageOptions(Builder builder, StorageDefaults serviceDefaults) {
    super(builder, serviceDefaults);
    this.retryAlgorithmManager =
        new HttpRetryAlgorithmManager(
            MoreObjects.firstNonNull(
                builder.storageRetryStrategy, defaults().getStorageRetryStrategy()));
  }

  @Override
  protected Set<String> getScopes() {
    return SCOPES;
  }

  @InternalApi
  HttpRetryAlgorithmManager getRetryAlgorithmManager() {
    return retryAlgorithmManager;
  }

  @InternalApi
  StorageRpc getStorageRpcV1() {
    return (StorageRpc) getRpc();
  }

  @Override
  public HttpStorageOptions.Builder toBuilder() {
    return new HttpStorageOptions.Builder(this);
  }

  @Override
  public int hashCode() {
    return baseHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof HttpStorageOptions && baseEquals((HttpStorageOptions) obj);
  }

  public static HttpStorageOptions.Builder newBuilder() {
    return new HttpStorageOptions.Builder().setHost(DEFAULT_HOST);
  }

  public static HttpStorageOptions getDefaultInstance() {
    return newBuilder().build();
  }

  public static HttpStorageDefaults defaults() {
    return HttpStorageDefaults.INSTANCE;
  }

  public static class Builder extends StorageOptions.Builder {

    private StorageRetryStrategy storageRetryStrategy;

    Builder() {}

    Builder(StorageOptions options) {
      super(options);
    }

    @Override
    public HttpStorageOptions.Builder setTransportOptions(TransportOptions transportOptions) {
      if (!(transportOptions instanceof HttpTransportOptions)) {
        throw new IllegalArgumentException(
            "Only http transport is allowed for " + API_SHORT_NAME + ".");
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
    public HttpStorageOptions.Builder setStorageRetryStrategy(
        StorageRetryStrategy storageRetryStrategy) {
      this.storageRetryStrategy =
          requireNonNull(storageRetryStrategy, "storageRetryStrategy must be non null");
      return this;
    }

    @Override
    protected HttpStorageOptions.Builder self() {
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setServiceFactory(
        ServiceFactory<Storage, StorageOptions> serviceFactory) {
      super.setServiceFactory(serviceFactory);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setClock(ApiClock clock) {
      super.setClock(clock);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setProjectId(String projectId) {
      super.setProjectId(projectId);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setHost(String host) {
      super.setHost(host);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setCredentials(Credentials credentials) {
      super.setCredentials(credentials);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setRetrySettings(RetrySettings retrySettings) {
      super.setRetrySettings(retrySettings);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setServiceRpcFactory(
        ServiceRpcFactory<StorageOptions> serviceRpcFactory) {
      super.setServiceRpcFactory(serviceRpcFactory);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setHeaderProvider(HeaderProvider headerProvider) {
      super.setHeaderProvider(headerProvider);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setClientLibToken(String clientLibToken) {
      super.setClientLibToken(clientLibToken);
      return this;
    }

    @Override
    public HttpStorageOptions.Builder setQuotaProjectId(String quotaProjectId) {
      super.setQuotaProjectId(quotaProjectId);
      return this;
    }

    @Override
    public HttpStorageOptions build() {
      return new HttpStorageOptions(this, defaults());
    }
  }

  public static final class HttpStorageDefaults extends StorageDefaults {
    static final HttpStorageDefaults INSTANCE = new HttpStorageDefaults();
    static final StorageFactory STORAGE_FACTORY = new HttpStorageFactory();
    static final StorageRpcFactory STORAGE_RPC_FACTORY = new HttpStorageRpcFactory();

    private HttpStorageDefaults() {}

    @Override
    public StorageFactory getDefaultServiceFactory() {
      return STORAGE_FACTORY;
    }

    @Override
    public StorageRpcFactory getDefaultRpcFactory() {
      return STORAGE_RPC_FACTORY;
    }

    @Override
    public HttpTransportOptions getDefaultTransportOptions() {
      return HttpTransportOptions.newBuilder().build();
    }

    public StorageRetryStrategy getStorageRetryStrategy() {
      return StorageRetryStrategy.getDefaultStorageRetryStrategy();
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
   *
   * <p>To access an instance of this class instead use {@link
   * HttpStorageDefaults#getDefaultServiceFactory()
   * HttpStorageOptions.defaults().getDefaultServiceFactory()}.
   *
   * @see HttpStorageOptions#defaults()
   * @see HttpStorageDefaults#getDefaultServiceFactory()
   */
  @InternalApi
  public static class HttpStorageFactory implements StorageFactory, Serializable {
    private static final long serialVersionUID = 1063208433681579145L;

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
     *
     * <p>To access an instance of this class instead use {@link
     * HttpStorageDefaults#getDefaultServiceFactory()
     * HttpStorageOptions.defaults().getDefaultServiceFactory()}.
     *
     * @see HttpStorageOptions#defaults()
     * @see HttpStorageDefaults#getDefaultServiceFactory()
     * @deprecated instead use {@link HttpStorageDefaults#getDefaultServiceFactory()
     *     HttpStorageOptions.defaults().getDefaultServiceFactory()}
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public HttpStorageFactory() {}

    @Override
    public Storage create(StorageOptions options) {
      if (options instanceof HttpStorageOptions) {
        HttpStorageOptions httpStorageOptions = (HttpStorageOptions) options;
        return new StorageImpl(httpStorageOptions);
      } else {
        throw new IllegalArgumentException("Only HttpStorageOptions supported");
      }
    }
  }

  /**
   * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
   *
   * <p>To access an instance of this class instead use {@link
   * HttpStorageDefaults#getDefaultRpcFactory()
   * HttpStorageOptions.defaults().getDefaultRpcFactory()}.
   *
   * @see HttpStorageOptions#defaults()
   * @see HttpStorageDefaults#getDefaultRpcFactory()
   */
  @InternalApi
  public static class HttpStorageRpcFactory implements StorageRpcFactory, Serializable {
    private static final long serialVersionUID = -5896805045709989797L;

    /**
     * Internal implementation detail, only public to allow for {@link java.io.Serializable}.
     *
     * <p>To access an instance of this class instead use {@link
     * HttpStorageDefaults#getDefaultRpcFactory()
     * HttpStorageOptions.defaults().getDefaultRpcFactory()}.
     *
     * @see HttpStorageOptions#defaults()
     * @see HttpStorageDefaults#getDefaultRpcFactory()
     * @deprecated instead use {@link HttpStorageDefaults#getDefaultRpcFactory()
     *     HttpStorageOptions.defaults().getDefaultRpcFactory()}
     */
    // this class needs to be public due to ServiceOptions forName'ing it in it's readObject method
    @InternalApi
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public HttpStorageRpcFactory() {}

    @Override
    public ServiceRpc create(StorageOptions options) {
      if (options instanceof HttpStorageOptions) {
        HttpStorageOptions httpStorageOptions = (HttpStorageOptions) options;
        return new HttpStorageRpc(httpStorageOptions);
      } else {
        throw new IllegalArgumentException("Only HttpStorageOptions supported");
      }
    }
  }
}
