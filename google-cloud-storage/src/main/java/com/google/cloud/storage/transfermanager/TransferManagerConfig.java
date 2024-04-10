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
  private final boolean allowDivideAndConquerDownload;
  private final boolean allowParallelCompositeUpload;

  private final StorageOptions storageOptions;

  TransferManagerConfig(
      int maxWorkers,
      int perWorkerBufferSize,
      boolean allowDivideAndConquerDownload,
      boolean allowParallelCompositeUpload,
      StorageOptions storageOptions) {
    this.maxWorkers = maxWorkers;
    this.perWorkerBufferSize = perWorkerBufferSize;
    this.allowDivideAndConquerDownload = allowDivideAndConquerDownload;
    this.allowParallelCompositeUpload = allowParallelCompositeUpload;
    this.storageOptions = storageOptions;
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
   * @see Builder#setAllowDivideAndConquerDownload(boolean)
   */
  @BetaApi
  public boolean isAllowDivideAndConquerDownload() {
    return allowDivideAndConquerDownload;
  }
  /**
   * Whether to allow Transfer Manager to perform Parallel Composite Uploads if it determines
   * chunking will be beneficial
   *
   * @see Builder#setAllowParallelCompositeUpload(boolean)
   */
  @BetaApi
  public boolean isAllowParallelCompositeUpload() {
    return allowParallelCompositeUpload;
  }

  /**
   * Storage options that Transfer Manager will use to interact with Google Cloud Storage
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
    return new TransferManagerImpl(this, DefaultQos.of(this));
  }

  @BetaApi
  public Builder toBuilder() {
    return new Builder()
        .setAllowDivideAndConquerDownload(allowDivideAndConquerDownload)
        .setAllowParallelCompositeUpload(allowParallelCompositeUpload)
        .setMaxWorkers(maxWorkers)
        .setPerWorkerBufferSize(perWorkerBufferSize)
        .setStorageOptions(storageOptions);
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
        && allowDivideAndConquerDownload == that.allowDivideAndConquerDownload
        && allowParallelCompositeUpload == that.allowParallelCompositeUpload
        && Objects.equals(storageOptions, that.storageOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        maxWorkers,
        perWorkerBufferSize,
        allowDivideAndConquerDownload,
        allowParallelCompositeUpload,
        storageOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("maxWorkers", maxWorkers)
        .add("perWorkerBufferSize", perWorkerBufferSize)
        .add("allowDivideAndConquerDownload", allowDivideAndConquerDownload)
        .add("allowParallelCompositeUpload", allowParallelCompositeUpload)
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
    private boolean allowDivideAndConquerDownload;
    private boolean allowParallelCompositeUpload;

    private StorageOptions storageOptions;

    private Builder() {
      this.perWorkerBufferSize = 16 * 1024 * 1024;
      this.maxWorkers = 2 * Runtime.getRuntime().availableProcessors();
      this.allowDivideAndConquerDownload = false;
      this.allowParallelCompositeUpload = false;
      this.storageOptions = StorageOptions.getDefaultInstance();
    }

    /**
     * Maximum amount of workers to be allocated to perform work in Transfer Manager
     *
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
     * <p><i>Default Value:</i> 16MiB
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
     * <p><i>Default Value:</i> false
     *
     * @return the instance of Builder with the value for allowDivideAndConquerDownload modified.
     * @see TransferManagerConfig#isAllowDivideAndConquerDownload()
     */
    @BetaApi
    public Builder setAllowDivideAndConquerDownload(boolean allowDivideAndConquerDownload) {
      this.allowDivideAndConquerDownload = allowDivideAndConquerDownload;
      return this;
    }

    /**
     * Whether to allow Transfer Manager to perform Parallel Composite Uploads if it determines
     * chunking will be beneficial
     *
     * <p><i>Default Value:</i> false
     *
     * @return the instance of Builder with the value for allowDivideAndConquerDownload modified.
     * @see TransferManagerConfig#isAllowDivideAndConquerDownload()
     */
    @BetaApi
    public Builder setAllowParallelCompositeUpload(boolean allowParallelCompositeUpload) {
      this.allowParallelCompositeUpload = allowParallelCompositeUpload;
      return this;
    }

    /**
     * Storage options that Transfer Manager will use to interact with Google Cloud Storage
     *
     * <p><i>Default Value:</i> {@link StorageOptions#getDefaultInstance()}
     *
     * @return the instance of Builder with the value for storageOptions modified.
     * @see TransferManagerConfig#getStorageOptions()
     */
    @BetaApi
    public Builder setStorageOptions(StorageOptions storageOptions) {
      this.storageOptions = storageOptions;
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
          maxWorkers,
          perWorkerBufferSize,
          allowDivideAndConquerDownload,
          allowParallelCompositeUpload,
          storageOptions);
    }
  }
}
