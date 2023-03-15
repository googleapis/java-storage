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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ParallelUploadConfig {

  private final boolean skipIfExists;
  @NonNull private final String prefix;
  @NonNull private final String bucketName;
  @NonNull private final List<BlobTargetOption> targetOptsPerRequest;

  @NonNull private final List<BlobWriteOption> writeOptsPerRequest;

  private ParallelUploadConfig(
      boolean skipIfExists,
      @NonNull String prefix,
      @NonNull String bucketName,
      @NonNull List<BlobTargetOption> targetOptsPerRequest,
      @NonNull List<BlobWriteOption> writeOptsPerRequest) {
    this.skipIfExists = skipIfExists;
    this.prefix = prefix;
    this.bucketName = bucketName;
    this.targetOptsPerRequest = targetOptsPerRequest;
    this.writeOptsPerRequest = writeOptsPerRequest;
  }

  public boolean isSkipIfExists() {
    return skipIfExists;
  }

  public @NonNull String getPrefix() {
    return prefix;
  }

  public @NonNull String getBucketName() {
    return bucketName;
  }

  public @NonNull List<BlobTargetOption> getTargetOptsPerRequest() {
    return targetOptsPerRequest;
  }

  public @NonNull List<BlobWriteOption> getWriteOptsPerRequest() {
    return writeOptsPerRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParallelUploadConfig that = (ParallelUploadConfig) o;
    return skipIfExists == that.skipIfExists
        && prefix.equals(that.prefix)
        && bucketName.equals(that.bucketName)
        && targetOptsPerRequest.equals(that.targetOptsPerRequest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(skipIfExists, prefix, bucketName, targetOptsPerRequest);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("skipIfExists", skipIfExists)
        .add("prefix", prefix)
        .add("bucketName", bucketName)
        .add("optionsPerRequest", targetOptsPerRequest)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private boolean skipIfExists;
    private @NonNull String prefix;
    private @NonNull String bucketName;
    private @NonNull List<BlobTargetOption> optionsPerRequest;

    private @NonNull List<BlobWriteOption> writeOptsPerRequest;

    private Builder() {
      this.prefix = "";
      this.bucketName = "";
      this.optionsPerRequest = ImmutableList.of();
      this.writeOptsPerRequest = ImmutableList.of();
    }

    public Builder setSkipIfExists(boolean skipIfExists) {
      this.skipIfExists = skipIfExists;
      return this;
    }

    public Builder setPrefix(@NonNull String prefix) {
      this.prefix = prefix;
      return this;
    }

    public Builder setBucketName(@NonNull String bucketName) {
      this.bucketName = bucketName;
      return this;
    }

    public Builder setOptionsPerRequest(@NonNull List<BlobTargetOption> optionsPerRequest) {
      this.optionsPerRequest = ImmutableList.copyOf(optionsPerRequest);
      return this;
    }

    public Builder setWriteOptsPerRequest(@NonNull List<BlobWriteOption> writeOptsPerRequest) {
      this.writeOptsPerRequest = writeOptsPerRequest;
      return this;
    }

    public ParallelUploadConfig build() {
      checkNotNull(prefix);
      checkNotNull(bucketName);
      checkNotNull(optionsPerRequest);
      checkNotNull(writeOptsPerRequest);
      return new ParallelUploadConfig(
          skipIfExists, prefix, bucketName, optionsPerRequest, writeOptsPerRequest);
    }
  }
}
