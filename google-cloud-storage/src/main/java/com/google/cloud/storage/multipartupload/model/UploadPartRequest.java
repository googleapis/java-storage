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

public final class UploadPartRequest {

  private final String bucket;
  private final String key;
  private final int partNumber;
  private final String uploadId;

  private UploadPartRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.partNumber = builder.partNumber;
    this.uploadId = builder.uploadId;
  }

  public String bucket() {
    return bucket;
  }

  public String key() {
    return key;
  }

  public int partNumber() {
    return partNumber;
  }

  public String uploadId() {
    return uploadId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UploadPartRequest)) {
      return false;
    }
    UploadPartRequest that = (UploadPartRequest) o;
    return partNumber == that.partNumber
        && Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && Objects.equals(uploadId, that.uploadId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, key, partNumber, uploadId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("partNumber", partNumber)
        .add("uploadId", uploadId)
        .toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String bucket;
    private String key;
    private int partNumber;
    private String uploadId;

    private Builder() {}

    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder partNumber(int partNumber) {
      this.partNumber = partNumber;
      return this;
    }

    public Builder uploadId(String uploadId) {
      this.uploadId = uploadId;
      return this;
    }

    public UploadPartRequest build() {
      return new UploadPartRequest(this);
    }
  }
}
