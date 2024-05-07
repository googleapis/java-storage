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

import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Configuration for performing Parallel Uploads with {@link TransferManager}.
 *
 * @see Builder
 */
public final class ParallelUploadConfig {

  private final boolean skipIfExists;
  @NonNull private final String prefix;
  @NonNull private final String bucketName;

  @NonNull private final List<BlobWriteOption> writeOptsPerRequest;

  private ParallelUploadConfig(
      boolean skipIfExists,
      @NonNull String prefix,
      @NonNull String bucketName,
      @NonNull List<BlobWriteOption> writeOptsPerRequest) {
    this.skipIfExists = skipIfExists;
    this.prefix = prefix;
    this.bucketName = bucketName;
    this.writeOptsPerRequest = applySkipIfExists(skipIfExists, writeOptsPerRequest);
  }

  /**
   * If set Transfer Manager will skip uploading an object if it already exists, equivalent to
   * providing {@link BlobWriteOption#doesNotExist()} in {@link #getWriteOptsPerRequest()}
   *
   * @see Builder#setSkipIfExists(boolean)
   */
  public boolean isSkipIfExists() {
    return skipIfExists;
  }

  /**
   * A common prefix that will be applied to all object paths in the destination bucket
   *
   * @see Builder#setPrefix(String)
   */
  public @NonNull String getPrefix() {
    return prefix;
  }

  /**
   * The bucket objects are being uploaded from
   *
   * @see Builder#setBucketName(String)
   */
  public @NonNull String getBucketName() {
    return bucketName;
  }

  /**
   * A list of common BlobWriteOptions, note these options will be applied to each upload request.
   *
   * @see Builder#setWriteOptsPerRequest(List)
   */
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
        && writeOptsPerRequest.equals(that.writeOptsPerRequest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(skipIfExists, prefix, bucketName, writeOptsPerRequest);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("skipIfExists", skipIfExists)
        .add("prefix", prefix)
        .add("bucketName", bucketName)
        .add("writeOptsPerRequest", writeOptsPerRequest)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private static List<BlobWriteOption> applySkipIfExists(
      boolean skipIfExists, List<BlobWriteOption> writeOptsPerRequest) {
    if (skipIfExists) {
      return ImmutableList.copyOf(
          BlobWriteOption.dedupe(writeOptsPerRequest, BlobWriteOption.doesNotExist()));
    }
    return writeOptsPerRequest;
  }

  /**
   * Builds an instance of ParallelUploadConfig.
   *
   * @see ParallelUploadConfig
   */
  public static final class Builder {

    private boolean skipIfExists;
    private @NonNull String prefix;
    private @NonNull String bucketName;
    private @NonNull List<BlobWriteOption> writeOptsPerRequest;

    private Builder() {
      this.prefix = "";
      this.bucketName = "";
      this.writeOptsPerRequest = ImmutableList.of();
    }

    /**
     * Sets the parameter for skipIfExists. When set to true Transfer Manager will skip uploading an
     * object if it already exists.
     *
     * @return the builder instance with the value for skipIfExists modified.
     * @see ParallelUploadConfig#isSkipIfExists()
     */
    public Builder setSkipIfExists(boolean skipIfExists) {
      this.skipIfExists = skipIfExists;
      return this;
    }

    /**
     * Sets a common prefix that will be applied to all object paths in the destination bucket.
     *
     * @return the builder instance with the value for prefix modified.
     * @see ParallelUploadConfig#getPrefix()
     */
    public Builder setPrefix(@NonNull String prefix) {
      this.prefix = prefix;
      return this;
    }

    /**
     * Sets the bucketName that Transfer Manager will upload to. This field is required.
     *
     * @return the builder instance with the value for bucketName modified.
     * @see ParallelUploadConfig#getBucketName()
     */
    public Builder setBucketName(@NonNull String bucketName) {
      this.bucketName = bucketName;
      return this;
    }

    /**
     * Sets the BlobWriteOptions that will be applied to each upload request. Note these options
     * will be applied to every single upload request.
     *
     * @return the builder instance with the value for WriteOptsPerRequest modified.
     * @see ParallelUploadConfig#getWriteOptsPerRequest()
     */
    public Builder setWriteOptsPerRequest(@NonNull List<BlobWriteOption> writeOptsPerRequest) {
      this.writeOptsPerRequest = writeOptsPerRequest;
      return this;
    }

    /**
     * Creates a ParallelUploadConfig object.
     *
     * @return {@link ParallelUploadConfig}
     */
    public ParallelUploadConfig build() {
      checkNotNull(prefix);
      checkNotNull(bucketName);
      checkNotNull(writeOptsPerRequest);
      return new ParallelUploadConfig(skipIfExists, prefix, bucketName, writeOptsPerRequest);
    }
  }
}
