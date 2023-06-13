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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class DownloadJob {

  @NonNull private final List<ApiFuture<DownloadResult>> downloadResults;

  @NonNull private final ParallelDownloadConfig parallelDownloadConfig;

  private DownloadJob(
      @NonNull List<ApiFuture<DownloadResult>> downloadResults,
      @NonNull ParallelDownloadConfig parallelDownloadConfig) {
    this.downloadResults = downloadResults;
    this.parallelDownloadConfig = parallelDownloadConfig;
  }

  public @NonNull List<DownloadResult> getDownloadResults() {
    return ApiExceptions.callAndTranslateApiException(ApiFutures.allAsList(downloadResults));
  }

  public @NonNull ParallelDownloadConfig getParallelDownloadConfig() {
    return parallelDownloadConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DownloadJob)) {
      return false;
    }
    DownloadJob that = (DownloadJob) o;
    return downloadResults.equals(that.downloadResults)
        && parallelDownloadConfig.equals(that.parallelDownloadConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(downloadResults, parallelDownloadConfig);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("downloadResults", downloadResults)
        .add("parallelDownloadConfig", parallelDownloadConfig)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private @NonNull List<ApiFuture<DownloadResult>> downloadResults;
    private @MonotonicNonNull ParallelDownloadConfig parallelDownloadConfig;

    private Builder() {
      this.downloadResults = ImmutableList.of();
    }

    public Builder setDownloadResults(@NonNull List<ApiFuture<DownloadResult>> downloadResults) {
      this.downloadResults = ImmutableList.copyOf(downloadResults);
      return this;
    }

    public Builder setParallelDownloadConfig(
        @NonNull ParallelDownloadConfig parallelDownloadConfig) {
      this.parallelDownloadConfig = parallelDownloadConfig;
      return this;
    }

    public DownloadJob build() {
      checkNotNull(downloadResults);
      checkNotNull(parallelDownloadConfig);
      return new DownloadJob(downloadResults, parallelDownloadConfig);
    }
  }
}
