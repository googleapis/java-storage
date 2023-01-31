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
import org.checkerframework.checker.nullness.qual.NonNull;

public class DownloadJob {

  @NonNull private final List<DownloadResult> successResponses;
  @NonNull private final List<DownloadResult> failedResponses;
  @NonNull private final boolean anyFailed;

  private DownloadJob(
      @NonNull List<DownloadResult> successResponses,
      @NonNull List<DownloadResult> failedResponses,
      @NonNull boolean anyFailed) {
    this.successResponses = successResponses;
    this.failedResponses = failedResponses;
    this.anyFailed = anyFailed;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DownloadJob that = (DownloadJob) o;
    return anyFailed == that.anyFailed
        && successResponses.equals(that.successResponses)
        && failedResponses.equals(that.failedResponses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(successResponses, failedResponses, anyFailed);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("successResponses", successResponses)
        .add("failedResponses", failedResponses)
        .add("anyFailed", anyFailed)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private @NonNull List<DownloadResult> successResponses;
    private @NonNull List<DownloadResult> failedResponses;
    private @NonNull boolean anyFailed;

    private Builder() {
      this.successResponses = ImmutableList.of();
      this.failedResponses = ImmutableList.of();
      this.anyFailed = false;
    }

    public Builder setSuccessResponses(@NonNull List<DownloadResult> successResponses) {
      this.successResponses = ImmutableList.copyOf(successResponses);
      return this;
    }

    public Builder setFailedResponses(@NonNull List<DownloadResult> failedResponses) {
      this.failedResponses = ImmutableList.copyOf(failedResponses);
      return this;
    }

    public Builder setAnyFailed(@NonNull boolean anyFailed) {
      this.anyFailed = anyFailed;
      return this;
    }

    public DownloadJob build() {
      checkNotNull(successResponses);
      checkNotNull(failedResponses);
      checkNotNull(anyFailed);
      return new DownloadJob(successResponses, failedResponses, anyFailed);
    }
  }
}
