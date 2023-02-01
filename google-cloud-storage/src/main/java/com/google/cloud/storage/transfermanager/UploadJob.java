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

public final class UploadJob {

  @NonNull private final List<UploadResult> successResponses;
  @NonNull private final List<UploadResult> failedResponses;
  @NonNull private final boolean anyFailed;

  private UploadJob(
      @NonNull List<UploadResult> successResponses,
      @NonNull List<UploadResult> failedResponses,
      @NonNull boolean anyFailed) {
    this.successResponses = successResponses;
    this.failedResponses = failedResponses;
    this.anyFailed = anyFailed;
  }

  public List<UploadResult> getSuccessResponses() {
    return successResponses;
  }

  public List<UploadResult> getFailedResponses() {
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
    UploadJob uploadJob = (UploadJob) o;
    return anyFailed == uploadJob.anyFailed
        && successResponses.equals(uploadJob.successResponses)
        && failedResponses.equals(uploadJob.failedResponses);
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

  public static final class Builder {

    private @NonNull List<UploadResult> successResponses;
    private @NonNull List<UploadResult> failedResponses;
    private @NonNull boolean anyFailed;

    private Builder() {
      this.successResponses = ImmutableList.of();
      this.failedResponses = ImmutableList.of();
      this.anyFailed = false;
    }

    public Builder setSuccessResponses(@NonNull List<UploadResult> successResponses) {
      this.successResponses = ImmutableList.copyOf(successResponses);
      return this;
    }

    public Builder setFailedResponses(@NonNull List<UploadResult> failedResponses) {
      this.failedResponses = ImmutableList.copyOf(failedResponses);
      return this;
    }

    public Builder setAnyFailed(@NonNull boolean anyFailed) {
      this.anyFailed = anyFailed;
      return this;
    }

    public UploadJob build() {
      checkNotNull(successResponses);
      checkNotNull(failedResponses);
      checkNotNull(anyFailed);
      return new UploadJob(successResponses, failedResponses, anyFailed);
    }
  }
}
