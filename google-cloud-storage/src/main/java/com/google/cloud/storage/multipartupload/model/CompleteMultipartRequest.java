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

public final class CompleteMultipartRequest {

  private final String bucket;
  private final String key;
  private final String uploadId;
  private final CompletedMultipartUpload completedMultipartUpload;

  private CompleteMultipartRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.uploadId = builder.uploadId;
    this.completedMultipartUpload = builder.completedMultipartUpload;
  }

  public String getBucket() {
    return bucket;
  }

  public String getKey() {
    return key;
  }

  public String getUploadId() {
    return uploadId;
  }

  public CompletedMultipartUpload getCompletedMultipartUpload() {
    return completedMultipartUpload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompleteMultipartRequest)) {
      return false;
    }
    CompleteMultipartRequest that = (CompleteMultipartRequest) o;
    return Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && Objects.equals(uploadId, that.uploadId)
        && Objects.equals(completedMultipartUpload, that.completedMultipartUpload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, key, uploadId, completedMultipartUpload);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("uploadId", uploadId)
        .add("completedMultipartUpload", completedMultipartUpload)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private String bucket;
    private String key;
    private String uploadId;
    private CompletedMultipartUpload completedMultipartUpload;

    private Builder() {}

    public Builder setBucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    public Builder setKey(String key) {
      this.key = key;
      return this;
    }

    public Builder setUploadId(String uploadId) {
      this.uploadId = uploadId;
      return this;
    }

    public Builder setCompletedMultipartUpload(CompletedMultipartUpload completedMultipartUpload) {
      this.completedMultipartUpload = completedMultipartUpload;
      return this;
    }

    public CompleteMultipartRequest build() {
      return new CompleteMultipartRequest(this);
    }
  }
}
