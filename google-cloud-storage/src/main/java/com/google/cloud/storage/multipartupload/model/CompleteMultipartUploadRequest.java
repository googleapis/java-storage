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

public final class CompleteMultipartUploadRequest {

  private final String bucket;
  private final String key;
  private final String uploadId;
  private final CompletedMultipartUpload multipartUpload;
  private final String requestPayer;
  private final String expectedBucketOwner;

  private CompleteMultipartUploadRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.uploadId = builder.uploadId;
    this.multipartUpload = builder.multipartUpload;
    this.requestPayer = builder.requestPayer;
    this.expectedBucketOwner = builder.expectedBucketOwner;
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

  public CompletedMultipartUpload multipartUpload() {
    return multipartUpload;
  }

  public String requestPayer() {
    return requestPayer;
  }

  public String expectedBucketOwner() {
    return expectedBucketOwner;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompleteMultipartUploadRequest)) {
      return false;
    }
    CompleteMultipartUploadRequest that = (CompleteMultipartUploadRequest) o;
    return Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && Objects.equals(uploadId, that.uploadId)
        && Objects.equals(multipartUpload, that.multipartUpload)
        && Objects.equals(requestPayer, that.requestPayer)
        && Objects.equals(expectedBucketOwner, that.expectedBucketOwner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, key, uploadId, multipartUpload, requestPayer, expectedBucketOwner);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("uploadId", uploadId)
        .add("completedMultipartUpload", multipartUpload)
        .add("requestPayer", requestPayer)
        .add("expectedBucketOwner", expectedBucketOwner)
        .toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String bucket;
    private String key;
    private String uploadId;
    private CompletedMultipartUpload multipartUpload;
    private String requestPayer;
    private String expectedBucketOwner;

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

    public Builder multipartUpload(CompletedMultipartUpload completedMultipartUpload) {
      this.multipartUpload = completedMultipartUpload;
      return this;
    }

    public Builder requestPayer(String requestPayer) {
      this.requestPayer = requestPayer;
      return this;
    }

    public Builder expectedBucketOwner(String expectedBucketOwner) {
      this.expectedBucketOwner = expectedBucketOwner;
      return this;
    }

    public CompleteMultipartUploadRequest build() {
      return new CompleteMultipartUploadRequest(this);
    }
  }
}
