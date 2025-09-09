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
package com.google.cloud.storage.multipartuploader.data;

import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;

public class CompletedMultipartUpload {

  private final List<CompletedPart> completedPartList;

  private CompletedMultipartUpload(Builder builder) {
    this.completedPartList = builder.completedPartList;
  }

  public List<CompletedPart> getCompletedPartList() {
    return completedPartList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompletedMultipartUpload)) {
      return false;
    }
    CompletedMultipartUpload that = (CompletedMultipartUpload) o;
    return Objects.equals(completedPartList, that.completedPartList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedPartList);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("completedPartList", completedPartList)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private List<CompletedPart> completedPartList;

    private Builder() {}

    public Builder setCompletedPartList(List<CompletedPart> completedPartList) {
      this.completedPartList = completedPartList;
      return this;
    }

    public CompletedMultipartUpload build() {
      return new CompletedMultipartUpload(this);
    }
  }
}
