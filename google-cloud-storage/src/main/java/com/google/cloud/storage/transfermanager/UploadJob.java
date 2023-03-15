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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UploadJob {

  @NonNull private final List<Future<UploadResult>> uploadResponses;

  @NonNull private final ParallelUploadConfig parallelUploadConfig;

  private UploadJob(
      @NonNull List<Future<UploadResult>> successResponses,
      @NonNull ParallelUploadConfig parallelUploadConfig) {
    this.uploadResponses = successResponses;
    this.parallelUploadConfig = parallelUploadConfig;
  }

  public List<Future<UploadResult>> getUploadResponses() {
    return uploadResponses;
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
    return uploadResponses.equals(uploadJob.uploadResponses)
        && parallelUploadConfig.equals(uploadJob.parallelUploadConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploadResponses, parallelUploadConfig);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("successResponses", uploadResponses)
        .add("parallelUploadConfig", parallelUploadConfig)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private @NonNull List<Future<UploadResult>> uploadResponses;

    private @MonotonicNonNull ParallelUploadConfig parallelUploadConfig;

    private Builder() {
      this.uploadResponses = ImmutableList.of();
    }

    public Builder setUploadResponses(@NonNull List<Future<UploadResult>> uploadResponses) {
      this.uploadResponses = ImmutableList.copyOf(uploadResponses);
      return this;
    }

    public Builder setParallelUploadConfig(@NonNull ParallelUploadConfig parallelUploadConfig) {
      this.parallelUploadConfig = parallelUploadConfig;
      return this;
    }

    public UploadJob build() {
      checkNotNull(uploadResponses);
      checkNotNull(parallelUploadConfig);
      return new UploadJob(uploadResponses, parallelUploadConfig);
    }
  }
}
