/*
 * Copyright 2015 Google LLC
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

import com.google.api.core.BetaApi;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceDefaults;
import com.google.cloud.ServiceOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageDefaults;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageFactory;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageRpcFactory;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.spi.StorageRpcFactory;

public abstract class StorageOptions extends ServiceOptions<Storage, StorageOptions> {

  private static final long serialVersionUID = -7295846567928013233L;

  /** @deprecated Use {@link HttpStorageFactory} */
  @Deprecated
  public static class DefaultStorageFactory extends HttpStorageFactory {
    /** @deprecated Use {@link HttpStorageDefaults#getDefaultServiceFactory()} */
    @Deprecated
    public DefaultStorageFactory() {
      super();
    }
  }

  /** @deprecated Use {@link HttpStorageRpcFactory} */
  @Deprecated
  public static class DefaultStorageRpcFactory extends HttpStorageRpcFactory {

    /** @deprecated Use {@link HttpStorageDefaults#getDefaultRpcFactory()} */
    @Deprecated
    public DefaultStorageRpcFactory() {
      super();
    }
  }

  public abstract static class Builder
      extends ServiceOptions.Builder<Storage, StorageOptions, Builder> {

    Builder() {}

    Builder(StorageOptions options) {
      super(options);
    }

    public abstract Builder setStorageRetryStrategy(StorageRetryStrategy storageRetryStrategy);

    @Override
    public abstract StorageOptions build();
  }

  StorageOptions(Builder builder, StorageDefaults serviceDefaults) {
    super(StorageFactory.class, StorageRpcFactory.class, builder, serviceDefaults);
  }

  abstract static class StorageDefaults implements ServiceDefaults<Storage, StorageOptions> {}

  /** @deprecated Use {@link HttpStorageDefaults#getDefaultTransportOptions()} */
  @Deprecated
  public static HttpTransportOptions getDefaultHttpTransportOptions() {
    return HttpStorageOptions.defaults().getDefaultTransportOptions();
  }

  // Project ID is only required for creating buckets, so we don't require it for creating the
  // service.
  @Override
  protected boolean projectIdRequired() {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public abstract StorageOptions.Builder toBuilder();

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  /**
   * Returns a default {@code StorageOptions} instance. The default instance will use JSON over HTTP
   * for its transport.
   */
  @TransportCompatibility(Transport.HTTP)
  public static StorageOptions getDefaultInstance() {
    return HttpStorageOptions.newBuilder().build();
  }

  /**
   * Returns a unauthenticated {@code StorageOptions} instance. The returned instance will use JSON
   * over HTTP for its transport.
   */
  @TransportCompatibility(Transport.HTTP)
  public static StorageOptions getUnauthenticatedInstance() {
    return HttpStorageOptions.newBuilder().setCredentials(NoCredentials.getInstance()).build();
  }

  /** The returned instance will use JSON over HTTP for its transport. */
  @TransportCompatibility(Transport.HTTP)
  public static StorageOptions.Builder newBuilder() {
    return http();
  }

  /**
   * Builder factory method which will create a JSON over HTTP specific instance of storage options.
   *
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  @TransportCompatibility(Transport.HTTP)
  public static HttpStorageOptions.Builder http() {
    return HttpStorageOptions.newBuilder();
  }

  /**
   * Builder factory method which will create a gRPC specific instance of storage options.
   *
   * <p>Google Cloud Storage is in Private Preview for a gRPC centric transport.
   *
   * @since 2.14.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  @TransportCompatibility(Transport.GRPC)
  public static GrpcStorageOptions.Builder grpc() {
    return GrpcStorageOptions.newBuilder();
  }
}
