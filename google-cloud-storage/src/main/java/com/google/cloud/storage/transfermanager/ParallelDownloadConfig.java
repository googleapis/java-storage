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

import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ParallelDownloadConfig {

  @NonNull private final String stripPrefix;
  @NonNull private final Path downloadDirectory;
  @NonNull private final String bucketName;
  @NonNull private final List<BlobSourceOption> optionsPerRequest;

  private ParallelDownloadConfig(
      @NonNull String stripPrefix,
      @NonNull Path downloadDirectory,
      @NonNull String bucketName,
      @NonNull List<BlobSourceOption> optionsPerRequest) {
    this.stripPrefix = stripPrefix;
    this.downloadDirectory = downloadDirectory;
    this.bucketName = bucketName;
    this.optionsPerRequest = optionsPerRequest;
  }

  /**
   * A common prefix that is removed from downloaded object's name before written to the filesystem
   */
  public @NonNull String getStripPrefix() {
    return stripPrefix;
  }

  /** The base directory in which all objects will be placed when downloaded. */
  public @NonNull Path getDownloadDirectory() {
    return downloadDirectory;
  }

  /** The bucket objects are being downloaded from */
  public @NonNull String getBucketName() {
    return bucketName;
  }

  /** A list of common BlobSourceOptions that are used for each download request */
  public @NonNull List<BlobSourceOption> getOptionsPerRequest() {
    return optionsPerRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ParallelDownloadConfig)) {
      return false;
    }
    ParallelDownloadConfig that = (ParallelDownloadConfig) o;
    return stripPrefix.equals(that.stripPrefix)
        && downloadDirectory.equals(that.downloadDirectory)
        && bucketName.equals(that.bucketName)
        && optionsPerRequest.equals(that.optionsPerRequest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stripPrefix, downloadDirectory, bucketName, optionsPerRequest);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("stripPrefix", stripPrefix)
        .add("downloadDirectory", downloadDirectory)
        .add("bucketName", bucketName)
        .add("optionsPerRequest", optionsPerRequest)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    @NonNull private String stripPrefix;
    @NonNull private Path downloadDirectory;
    @NonNull private String bucketName;
    @NonNull private List<BlobSourceOption> optionsPerRequest;

    private Builder() {
      this.stripPrefix = "";
      this.downloadDirectory = Paths.get("");
      this.bucketName = "";
      this.optionsPerRequest = ImmutableList.of();
    }

    public Builder setStripPrefix(String stripPrefix) {
      this.stripPrefix = stripPrefix;
      return this;
    }

    public Builder setDownloadDirectory(Path downloadDirectory) {
      this.downloadDirectory = downloadDirectory;
      return this;
    }

    public Builder setBucketName(String bucketName) {
      this.bucketName = bucketName;
      return this;
    }

    public Builder setOptionsPerRequest(List<BlobSourceOption> optionsPerRequest) {
      this.optionsPerRequest = ImmutableList.copyOf(optionsPerRequest);
      return this;
    }

    public ParallelDownloadConfig build() {
      checkNotNull(bucketName);
      checkNotNull(stripPrefix);
      checkNotNull(downloadDirectory);
      checkNotNull(optionsPerRequest);
      return new ParallelDownloadConfig(
          stripPrefix, downloadDirectory, bucketName, optionsPerRequest);
    }
  }
}
