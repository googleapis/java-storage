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

public final class UploadJob {

  @NonNull private final List<ApiFuture<UploadResult>> uploadResults;

  @NonNull private final ParallelUploadConfig parallelUploadConfig;

  private UploadJob(
      @NonNull List<ApiFuture<UploadResult>> uploadResults,
      @NonNull ParallelUploadConfig parallelUploadConfig) {
    this.uploadResults = uploadResults;
    this.parallelUploadConfig = parallelUploadConfig;
  }

  public List<UploadResult> getUploadResults() {
    return ApiExceptions.callAndTranslateApiException(ApiFutures.allAsList(uploadResults));
  }

  public ParallelUploadConfig getParallelUploadConfig() {
    return parallelUploadConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UploadJob)) {
      return false;
    }
    UploadJob uploadJob = (UploadJob) o;
    return uploadResults.equals(uploadJob.uploadResults)
        && parallelUploadConfig.equals(uploadJob.parallelUploadConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploadResults, parallelUploadConfig);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("uploadResults", uploadResults)
        .add("parallelUploadConfig", parallelUploadConfig)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private @NonNull List<ApiFuture<UploadResult>> uploadResults;

    private @MonotonicNonNull ParallelUploadConfig parallelUploadConfig;

    private Builder() {
      this.uploadResults = ImmutableList.of();
    }

    public Builder setUploadResponses(@NonNull List<ApiFuture<UploadResult>> uploadResults) {
      this.uploadResults = ImmutableList.copyOf(uploadResults);
      return this;
    }

    public Builder setParallelUploadConfig(@NonNull ParallelUploadConfig parallelUploadConfig) {
      this.parallelUploadConfig = parallelUploadConfig;
      return this;
    }

    public UploadJob build() {
      checkNotNull(uploadResults);
      checkNotNull(parallelUploadConfig);
      return new UploadJob(uploadResults, parallelUploadConfig);
    }
  }
}
