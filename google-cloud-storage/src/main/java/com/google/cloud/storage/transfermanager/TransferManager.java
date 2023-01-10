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

import com.google.cloud.storage.BlobInfo;
import com.google.common.base.MoreObjects;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface TransferManager {

  class TransferManagerConfig {
    @NonNull private final int maxWorkers;
    @NonNull private final int perWorkerBufferSize;
    @NonNull private final boolean allowChunking;

    private TransferManagerConfig(
        @NonNull int maxWorkers, @NonNull int perWorkerBufferSize, @NonNull boolean allowChunking) {
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
      return maxWorkers == that.maxWorkers && perWorkerBufferSize == that.perWorkerBufferSize
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

      private @NonNull int maxWorkers;
      private @NonNull int perWorkerBufferSize;
      private @NonNull boolean allowChunking;

      private Builder() {
        //TODO: add null values
      }

      public Builder setMaxWorkers(@NonNull int maxWorkers) {
        this.maxWorkers = maxWorkers;
        return this;
      }

      public Builder setPerWorkerBufferSize(@NonNull int perWorkerBufferSize) {
        this.perWorkerBufferSize = perWorkerBufferSize;
        return this;
      }

      public Builder setAllowChunking(@NonNull boolean allowChunking) {
        this.allowChunking = allowChunking;
        return this;
      }

      public TransferManagerConfig build() {
        return new TransferManagerConfig(maxWorkers, perWorkerBufferSize, allowChunking);
      }
    }
  }

  // Separate package for  transfer manager,
  // Might be allowing public access to ChannelSessionBuilder
  // We do not want to use ReadChannel and WriteChannel. We want to use the SessionBuilder.

  UploadJob uploadFiles(List<Path> files, ParallelUploadConfig opts);

  DownloadJob downloadBlobs(List<BlobInfo> blobs, ParallelDownloadConfig opts);
}
