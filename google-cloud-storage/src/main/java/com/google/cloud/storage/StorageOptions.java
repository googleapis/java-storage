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

import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceDefaults;
import com.google.cloud.ServiceOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageDefaults;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageFactory;
import com.google.cloud.storage.HttpStorageOptions.HttpStorageRpcFactory;
import com.google.cloud.storage.spi.StorageRpcFactory;

public abstract class StorageOptions extends ServiceOptions<Storage, StorageOptions> {

  private static final long serialVersionUID = 7784772908840175401L;

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

  /** Returns a default {@code StorageOptions} instance. */
  public static StorageOptions getDefaultInstance() {
    return HttpStorageOptions.newBuilder().build();
  }

  /** Returns a unauthenticated {@code StorageOptions} instance. */
  public static StorageOptions getUnauthenticatedInstance() {
    return HttpStorageOptions.newBuilder().setCredentials(NoCredentials.getInstance()).build();
  }

  public static StorageOptions.Builder newBuilder() {
    return http();
  }

  public static HttpStorageOptions.Builder http() {
    return HttpStorageOptions.newBuilder();
  }

  public static GrpcStorageOptions.Builder grpc() {
    return GrpcStorageOptions.newBuilder();
  }
}
