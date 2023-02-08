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
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class DownloadJob {

  @NonNull private final List<DownloadResult> successResponses;
  @NonNull private final List<DownloadResult> failedResponses;

  @NonNull private final ParallelDownloadConfig parallelDownloadConfig;
  private final boolean anyFailed;

  private DownloadJob(
      @NonNull List<DownloadResult> successResponses,
      @NonNull List<DownloadResult> failedResponses,
      @NonNull ParallelDownloadConfig parallelDownloadConfig) {
    this.successResponses = successResponses;
    this.failedResponses = failedResponses;
    this.anyFailed = !failedResponses.isEmpty();
    this.parallelDownloadConfig = parallelDownloadConfig;
  }

  public List<DownloadResult> getSuccessResponses() {
    return successResponses;
  }

  public List<DownloadResult> getFailedResponses() {
    return failedResponses;
  }

  public boolean isAnyFailed() {
    return anyFailed;
  }

  public ParallelDownloadConfig getParallelDownloadConfig() {
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
    return anyFailed == that.anyFailed
        && successResponses.equals(that.successResponses)
        && failedResponses.equals(that.failedResponses)
        && parallelDownloadConfig.equals(that.parallelDownloadConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(successResponses, failedResponses, parallelDownloadConfig, anyFailed);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("successResponses", successResponses)
        .add("failedResponses", failedResponses)
        .add("parallelDownloadConfig", parallelDownloadConfig)
        .add("anyFailed", anyFailed)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private @NonNull List<DownloadResult> successResponses;
    private @NonNull List<DownloadResult> failedResponses;
    private @MonotonicNonNull ParallelDownloadConfig parallelDownloadConfig;

    private Builder() {
      this.successResponses = ImmutableList.of();
      this.failedResponses = ImmutableList.of();
    }

    public Builder setSuccessResponses(@NonNull List<DownloadResult> successResponses) {
      this.successResponses = ImmutableList.copyOf(successResponses);
      return this;
    }

    public Builder setFailedResponses(@NonNull List<DownloadResult> failedResponses) {
      this.failedResponses = ImmutableList.copyOf(failedResponses);
      return this;
    }

    public Builder setParallelDownloadConfig(
        @NonNull ParallelDownloadConfig parallelDownloadConfig) {
      this.parallelDownloadConfig = parallelDownloadConfig;
      return this;
    }

    public DownloadJob build() {
      checkNotNull(successResponses);
      checkNotNull(failedResponses);
      checkNotNull(parallelDownloadConfig);
      return new DownloadJob(successResponses, failedResponses, parallelDownloadConfig);
    }
  }
}
