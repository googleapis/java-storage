/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.storage.transfermanager;

import com.google.api.core.BetaApi;
import com.google.cloud.storage.StorageOptions;
import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * Configuration for an instance of {@link TransferManager}
 *
 * @see Builder
 */
@BetaApi
public final class TransferManagerConfig {
  private final int maxWorkers;
  private final int perWorkerBufferSize;
  private final boolean allowDivideAndConquer;

  private final StorageOptions storageOptions;
  private final Qos qos;

  TransferManagerConfig(
      int maxWorkers,
      int perWorkerBufferSize,
      boolean allowDivideAndConquer,
      StorageOptions storageOptions,
      Qos qos) {
    this.maxWorkers = maxWorkers;
    this.perWorkerBufferSize = perWorkerBufferSize;
    this.allowDivideAndConquer = allowDivideAndConquer;
    this.storageOptions = storageOptions;
    this.qos = qos;
  }

  /**
   * Maximum amount of workers to be allocated to perform work in Transfer Manager
   *
   * @see Builder#setMaxWorkers(int)
   */
  @BetaApi
  public int getMaxWorkers() {
    return maxWorkers;
  }

  /**
   * Buffer size allowed to each worker
   *
   * @see Builder#setPerWorkerBufferSize(int)
   */
  @BetaApi
  public int getPerWorkerBufferSize() {
    return perWorkerBufferSize;
  }

  /**
   * Whether to allow Transfer Manager to perform chunked Uploads/Downloads if it determines
   * chunking will be beneficial
   *
   * @see Builder#setAllowDivideAndConquer(boolean)
   */
  @BetaApi
  public boolean isAllowDivideAndConquer() {
    return allowDivideAndConquer;
  }

  /**
   * Storage options that Transfer Manager will use to interact with GCS
   *
   * @see Builder#setStorageOptions(StorageOptions)
   */
  @BetaApi
  public StorageOptions getStorageOptions() {
    return storageOptions;
  }

  /** The service object for {@link TransferManager} */
  @BetaApi
  public TransferManager getService() {
    return new TransferManagerImpl(this);
  }

  @BetaApi
  public Builder toBuilder() {
    return new Builder()
        .setAllowDivideAndConquer(allowDivideAndConquer)
        .setMaxWorkers(maxWorkers)
        .setPerWorkerBufferSize(perWorkerBufferSize)
        .setQos(qos)
        .setStorageOptions(storageOptions);
  }

  Qos getQos() {
    return qos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TransferManagerConfig)) {
      return false;
    }
    TransferManagerConfig that = (TransferManagerConfig) o;
    return maxWorkers == that.maxWorkers
        && perWorkerBufferSize == that.perWorkerBufferSize
        && allowDivideAndConquer == that.allowDivideAndConquer
        && Objects.equals(storageOptions, that.storageOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxWorkers, perWorkerBufferSize, allowDivideAndConquer, storageOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("maxWorkers", maxWorkers)
        .add("perWorkerBufferSize", perWorkerBufferSize)
        .add("allowChunking", allowDivideAndConquer)
        .add("storageOptions", storageOptions)
        .toString();
  }

  @BetaApi
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds an instance of TransferManagerConfig
   *
   * @see TransferManagerConfig
   */
  @BetaApi
  public static class Builder {

    private int maxWorkers;
    private int perWorkerBufferSize;
    private boolean allowDivideAndConquer;

    private StorageOptions storageOptions;
    private Qos qos;

    private Builder() {
      this.perWorkerBufferSize = 16 * 1024 * 1024;
      this.maxWorkers = 2 * Runtime.getRuntime().availableProcessors();
      this.allowDivideAndConquer = false;
      this.storageOptions = StorageOptions.getDefaultInstance();
      this.qos = DefaultQos.of();
    }

    /**
     * Maximum amount of workers to be allocated to perform work in Transfer Manager
     * <p><i>Default Value:</i> {@code 2 * }{@link Runtime#getRuntime()}{@code .}{@link
     * Runtime#availableProcessors() availableProcessors()}
     *
     * @return the instance of Builder with the value for maxWorkers modified.
     * @see TransferManagerConfig#getMaxWorkers()
     */
    @BetaApi
    public Builder setMaxWorkers(int maxWorkers) {
      this.maxWorkers = maxWorkers;
      return this;
    }

    /**
     * Buffer size allowed to each worker
     *
     * @return the instance of Builder with the value for maxWorkers modified.
     * @see TransferManagerConfig#getPerWorkerBufferSize()
     */
    @BetaApi
    public Builder setPerWorkerBufferSize(int perWorkerBufferSize) {
      this.perWorkerBufferSize = perWorkerBufferSize;
      return this;
    }

    /**
     * Whether to allow Transfer Manager to perform chunked Uploads/Downloads if it determines
     * chunking will be beneficial
     *
     * @return the instance of Builder with the value for allowDivideAndConquer modified.
     * @see TransferManagerConfig#isAllowDivideAndConquer()
     */
    @BetaApi
    public Builder setAllowDivideAndConquer(boolean allowDivideAndConquer) {
      this.allowDivideAndConquer = allowDivideAndConquer;
      return this;
    }

    /**
     * Storage options that Transfer Manager will use to interact with GCS
     *
     * @return the instance of Builder with the value for storageOptions modified.
     * @see TransferManagerConfig#getStorageOptions()
     */
    @BetaApi
    public Builder setStorageOptions(StorageOptions storageOptions) {
      this.storageOptions = storageOptions;
      return this;
    }

    @BetaApi
    Builder setQos(Qos qos) {
      this.qos = qos;
      return this;
    }

    /**
     * Creates a TransferManagerConfig object.
     *
     * @return {@link TransferManagerConfig}
     */
    @BetaApi
    public TransferManagerConfig build() {
      return new TransferManagerConfig(
          maxWorkers, perWorkerBufferSize, allowDivideAndConquer, storageOptions, qos);
    }
  }
}
