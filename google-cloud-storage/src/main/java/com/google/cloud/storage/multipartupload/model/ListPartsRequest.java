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

public class ListPartsRequest {
  private final String bucket;

  private final String key;

  private final String uploadId;

  private final Integer maxParts;

  private final Integer partNumberMarker;

  private ListPartsRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.uploadId = builder.uploadId;
    this.maxParts = builder.maxParts;
    this.partNumberMarker = builder.partNumberMarker;
  }

  public String bucket() {
    return bucket;
  }

  public String key() {
    return key;
  }

  public String uploadId() {
    return uploadId;
  }

  public Integer getMaxParts() {
    return maxParts;
  }

  public Integer getPartNumberMarker() {
    return partNumberMarker;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ListPartsRequest)) {
      return false;
    }
    ListPartsRequest that = (ListPartsRequest) o;
    return Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && Objects.equals(uploadId, that.uploadId)
        && Objects.equals(maxParts, that.maxParts)
        && Objects.equals(partNumberMarker, that.partNumberMarker);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, key, uploadId, maxParts, partNumberMarker);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("uploadId", uploadId)
        .add("maxParts", maxParts)
        .add("partNumberMarker", partNumberMarker)
        .toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String bucket;
    private String key;
    private String uploadId;
    private Integer maxParts;
    private Integer partNumberMarker;

    private Builder() {}

    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder uploadId(String uploadId) {
      this.uploadId = uploadId;
      return this;
    }

    public Builder maxParts(Integer maxParts) {
      this.maxParts = maxParts;
      return this;
    }

    public Builder partNumberMarker(Integer partNumberMarker) {
      this.partNumberMarker = partNumberMarker;
      return this;
    }

    public ListPartsRequest build() {
      return new ListPartsRequest(this);
    }
  }
}
