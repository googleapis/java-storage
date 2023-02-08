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

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class TransferManagerConfig {
  private final int maxWorkers;
  private final int perWorkerBufferSize;
  private final boolean allowChunking;

  // Getting stuff in for implementation bits
  // getService to get Storage instance
  // private final StorageOptions storageOptions;

  private TransferManagerConfig(int maxWorkers, int perWorkerBufferSize, boolean allowChunking) {
    this.maxWorkers = maxWorkers;
    this.perWorkerBufferSize = perWorkerBufferSize;
    this.allowChunking = allowChunking;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransferManagerConfig that = (TransferManagerConfig) o;
    return maxWorkers == that.maxWorkers
        && perWorkerBufferSize == that.perWorkerBufferSize
        && allowChunking == that.allowChunking;
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxWorkers, perWorkerBufferSize, allowChunking);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("maxWorkers", maxWorkers)
        .add("perWorkerBufferSize", perWorkerBufferSize)
        .add("allowChunking", allowChunking)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private int maxWorkers;
    private int perWorkerBufferSize;
    private boolean allowChunking;

    private Builder() {
      // TODO: add default values
      //  bufferSize tbd?
      this.perWorkerBufferSize = 16 * 1024 * 1024;
      this.maxWorkers = 2 * Runtime.getRuntime().availableProcessors();
      this.allowChunking = false;
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

    public TransferManagerConfig build() {
      return new TransferManagerConfig(maxWorkers, perWorkerBufferSize, allowChunking);
    }
  }
}
