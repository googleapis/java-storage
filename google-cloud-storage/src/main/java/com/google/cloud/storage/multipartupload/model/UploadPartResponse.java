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

/**
 * Represents the response from uploading a part in a multipart upload. It contains the ETag and
 * checksums of the uploaded part.
 */
public final class UploadPartResponse {

  private final String eTag;
  private final String crc32c;
  private final String md5;

  private UploadPartResponse(Builder builder) {
    this.eTag = builder.etag;
    this.crc32c = builder.crc32c;
    this.md5 = builder.md5;
  }

  /**
   * Returns the ETag of the uploaded part.
   *
   * @return The ETag.
   */
  public String eTag() {
    return eTag;
  }

  /**
   * Returns the CRC32C checksum of the uploaded part.
   *
   * @return The CRC32C checksum.
   */
  public String crc32c() {
    return crc32c;
  }

  /**
   * Returns the MD5 hash of the uploaded part.
   *
   * @return The MD5 hash.
   */
  public String md5() {
    return md5;
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
    return Objects.equals(eTag, that.eTag)
        && Objects.equals(crc32c, that.crc32c)
        && Objects.equals(md5, that.md5);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eTag, crc32c, md5);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("etag", eTag)
        .add("crc32c", crc32c)
        .add("md5", md5)
        .toString();
  }

  /**
   * Creates a new builder for creating an {@code UploadPartResponse}.
   *
   * @return A new builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /** A builder for creating {@code UploadPartResponse} instances. */
  public static class Builder {
    private String etag;
    private String crc32c;
    private String md5;

    private Builder() {}

    /**
     * Sets the ETag for the uploaded part.
     *
     * @param etag The ETag.
     * @return This builder.
     */
    public Builder eTag(String etag) {
      this.etag = etag;
      return this;
    }

    /**
     * Sets the CRC32C checksum for the uploaded part.
     *
     * @param crc32c The CRC32C checksum.
     * @return This builder.
     */
    public Builder crc32c(String crc32c) {
      this.crc32c = crc32c;
      return this;
    }

    /**
     * Sets the MD5 hash for the uploaded part.
     *
     * @param md5 The MD5 hash.
     * @return This builder.
     */
    public Builder md5(String md5) {
      this.md5 = md5;
      return this;
    }

    /**
     * Builds the {@code UploadPartResponse} object.
     *
     * @return The built {@code UploadPartResponse} object.
     */
    public UploadPartResponse build() {
      return new UploadPartResponse(this);
    }
  }
}
