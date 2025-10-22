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

import com.google.api.core.BetaApi;

/**
 * Represents a request to abort a multipart upload. This request is used to stop an in-progress
 * multipart upload, deleting any previously uploaded parts.
 */
@BetaApi
public final class AbortMultipartUploadRequest {
  private final String bucket;
  private final String key;
  private final String uploadId;

  private AbortMultipartUploadRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.uploadId = builder.uploadId;
  }

  /**
   * Returns the name of the bucket in which the multipart upload is stored.
   *
   * @return The bucket name.
   */
  public String bucket() {
    return bucket;
  }

  /**
   * Returns the name of the object that is being uploaded.
   *
   * @return The object name.
   */
  public String key() {
    return key;
  }

  /**
   * Returns the upload ID of the multipart upload to abort.
   *
   * @return The upload ID.
   */
  public String uploadId() {
    return uploadId;
  }

  /**
   * Returns a new builder for creating {@link AbortMultipartUploadRequest} instances.
   *
   * @return A new {@link Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /** A builder for creating {@link AbortMultipartUploadRequest} instances. */
  public static class Builder {
    private String bucket;
    private String key;
    private String uploadId;

    private Builder() {}

    /**
     * Sets the name of the bucket in which the multipart upload is stored.
     *
     * @param bucket The bucket name.
     * @return This builder.
     */
    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * Sets the name of the object that is being uploaded.
     *
     * @param key The object name.
     * @return This builder.
     */
    public Builder key(String key) {
      this.key = key;
      return this;
    }

    /**
     * Sets the upload ID of the multipart upload to abort.
     *
     * @param uploadId The upload ID.
     * @return This builder.
     */
    public Builder uploadId(String uploadId) {
      this.uploadId = uploadId;
      return this;
    }

    /**
     * Builds a new {@link AbortMultipartUploadRequest} instance.
     *
     * @return A new {@link AbortMultipartUploadRequest}.
     */
    public AbortMultipartUploadRequest build() {
      return new AbortMultipartUploadRequest(this);
    }
  }
}
