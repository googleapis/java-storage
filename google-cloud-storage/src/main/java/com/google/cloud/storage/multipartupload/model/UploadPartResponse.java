/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.storage.multipartupload.model;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class UploadPartResponse {

  private final String eTag;

  private UploadPartResponse(Builder builder) {
    this.eTag = builder.etag;
  }

  public String eTag() {
    return eTag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UploadPartResponse)) {
      return false;
    }
    UploadPartResponse that = (UploadPartResponse) o;
    return Objects.equals(eTag, that.eTag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eTag);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("etag", eTag).toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String etag;

    private Builder() {}

    public Builder eTag(String etag) {
      this.etag = etag;
      return this;
    }

    public UploadPartResponse build() {
      return new UploadPartResponse(this);
    }
  }
}
