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

import com.google.cloud.storage.StorageOptions;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class TransferManagerConfig {
  private final int maxWorkers;
  private final int perWorkerBufferSize;
  private final boolean allowChunking;

  private final StorageOptions storageOptions;

  TransferManagerConfig(
      int maxWorkers,
      int perWorkerBufferSize,
      boolean allowChunking,
      StorageOptions storageOptions) {
    this.maxWorkers = maxWorkers;
    this.perWorkerBufferSize = perWorkerBufferSize;
    this.allowChunking = allowChunking;
    this.storageOptions = storageOptions;
  }

  public int getMaxWorkers() {
    return maxWorkers;
  }

  public int getPerWorkerBufferSize() {
    return perWorkerBufferSize;
  }

  public boolean isAllowChunking() {
    return allowChunking;
  }

  public StorageOptions getStorageOptions() {
    return storageOptions;
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
        && allowChunking == that.allowChunking
        && Objects.equals(storageOptions, that.storageOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxWorkers, perWorkerBufferSize, allowChunking, storageOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("maxWorkers", maxWorkers)
        .add("perWorkerBufferSize", perWorkerBufferSize)
        .add("allowChunking", allowChunking)
        .add("storageOptions", storageOptions)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private int maxWorkers;
    private int perWorkerBufferSize;
    private boolean allowChunking;

    private StorageOptions storageOptions;

    private Builder() {
      // TODO: add default values
      //  bufferSize tbd?
      this.perWorkerBufferSize = 16 * 1024 * 1024;
      this.maxWorkers = 2 * Runtime.getRuntime().availableProcessors();
      this.allowChunking = false;
      this.storageOptions = StorageOptions.getDefaultInstance();
    }

    public Builder setMaxWorkers(int maxWorkers) {
      this.maxWorkers = maxWorkers;
      return this;
    }

    public Builder setPerWorkerBufferSize(int perWorkerBufferSize) {
      this.perWorkerBufferSize = perWorkerBufferSize;
      return this;
    }

    public Builder setAllowChunking(boolean allowChunking) {
      this.allowChunking = allowChunking;
      return this;
    }

    public void setStorageOptions(StorageOptions storageOptions) {
      this.storageOptions = storageOptions;
    }

    public TransferManagerConfig build() {
      return new TransferManagerConfig(
          maxWorkers, perWorkerBufferSize, allowChunking, storageOptions);
    }
  }
}
