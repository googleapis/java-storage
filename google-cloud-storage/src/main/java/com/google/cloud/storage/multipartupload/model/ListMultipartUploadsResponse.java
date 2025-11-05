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
import com.google.common.collect.ImmutableList;
import java.util.Objects;

/**
 * A response from listing all multipart uploads in a bucket.
 *
 * @see <a href="https://cloud.google.com/storage/docs/multipart-uploads#listing-uploads">Listing
 *     multipart uploads</a>
 * @since 2.60.0 This new api is in preview and is subject to breaking changes.
 */
@BetaApi
public final class ListMultipartUploadsResponse {

  private ImmutableList<MultipartUpload> uploads;
  private String bucket;
  private String delimiter;
  private String encodingType;
  private String keyMarker;
  private String uploadIdMarker;
  private String nextKeyMarker;
  private String nextUploadIdMarker;
  private int maxUploads;
  private String prefix;
  private boolean isTruncated;
  private ImmutableList<String> commonPrefixes;

  private ListMultipartUploadsResponse(
      ImmutableList<MultipartUpload> uploads,
      String bucket,
      String delimiter,
      String encodingType,
      String keyMarker,
      String uploadIdMarker,
      String nextKeyMarker,
      String nextUploadIdMarker,
      int maxUploads,
      String prefix,
      boolean isTruncated,
      ImmutableList<String> commonPrefixes) {
    this.uploads = uploads;
    this.bucket = bucket;
    this.delimiter = delimiter;
    this.encodingType = encodingType;
    this.keyMarker = keyMarker;
    this.uploadIdMarker = uploadIdMarker;
    this.nextKeyMarker = nextKeyMarker;
    this.nextUploadIdMarker = nextUploadIdMarker;
    this.maxUploads = maxUploads;
    this.prefix = prefix;
    this.isTruncated = isTruncated;
    this.commonPrefixes = commonPrefixes;
  }

  private ListMultipartUploadsResponse() {}

  /**
   * The list of multipart uploads.
   *
   * @return The list of multipart uploads.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public ImmutableList<MultipartUpload> getUploads() {
    return uploads;
  }

  /**
   * The bucket that contains the multipart uploads.
   *
   * @return The bucket name.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getBucket() {
    return bucket;
  }

  /**
   * The delimiter applied to the request.
   *
   * @return The delimiter applied to the request.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * The encoding type used by Cloud Storage to encode object names in the response.
   *
   * @return The encoding type.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getEncodingType() {
    return encodingType;
  }

  /**
   * The key at or after which the listing began.
   *
   * @return The key marker.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getKeyMarker() {
    return keyMarker;
  }

  /**
   * The upload ID at or after which the listing began.
   *
   * @return The upload ID marker.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getUploadIdMarker() {
    return uploadIdMarker;
  }

  /**
   * The key after which listing should begin.
   *
   * @return The key after which listing should begin.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getNextKeyMarker() {
    return nextKeyMarker;
  }

  /**
   * The upload ID after which listing should begin.
   *
   * @return The upload ID after which listing should begin.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getNextUploadIdMarker() {
    return nextUploadIdMarker;
  }

  /**
   * The maximum number of uploads to return.
   *
   * @return The maximum number of uploads.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public int getMaxUploads() {
    return maxUploads;
  }

  /**
   * The prefix applied to the request.
   *
   * @return The prefix applied to the request.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getPrefix() {
    return prefix;
  }

  /**
   * A flag indicating whether or not the returned results are truncated.
   *
   * @return A flag indicating whether or not the returned results are truncated.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public boolean isTruncated() {
    return isTruncated;
  }

  /**
   * If you specify a delimiter in the request, this element is returned.
   *
   * @return The common prefixes.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public ImmutableList<String> getCommonPrefixes() {
    return commonPrefixes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListMultipartUploadsResponse that = (ListMultipartUploadsResponse) o;
    return isTruncated == that.isTruncated
        && maxUploads == that.maxUploads
        && Objects.equals(uploads, that.uploads)
        && Objects.equals(bucket, that.bucket)
        && Objects.equals(delimiter, that.delimiter)
        && Objects.equals(encodingType, that.encodingType)
        && Objects.equals(keyMarker, that.keyMarker)
        && Objects.equals(uploadIdMarker, that.uploadIdMarker)
        && Objects.equals(nextKeyMarker, that.nextKeyMarker)
        && Objects.equals(nextUploadIdMarker, that.nextUploadIdMarker)
        && Objects.equals(prefix, that.prefix)
        && Objects.equals(commonPrefixes, that.commonPrefixes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        uploads,
        bucket,
        delimiter,
        encodingType,
        keyMarker,
        uploadIdMarker,
        nextKeyMarker,
        nextUploadIdMarker,
        maxUploads,
        prefix,
        isTruncated,
        commonPrefixes);
  }

  @Override
  public String toString() {
    return "ListMultipartUploadsResponse{" +
        "uploads=" + uploads +
        ", bucket='" + bucket + "'" +
        ", delimiter='" + delimiter + "'" +
        ", encodingType='" + encodingType + "'" +
        ", keyMarker='" + keyMarker + "'" +
        ", uploadIdMarker='" + uploadIdMarker + "'" +
        ", nextKeyMarker='" + nextKeyMarker + "'" +
        ", nextUploadIdMarker='" + nextUploadIdMarker + "'" +
        ", maxUploads=" + maxUploads +
        ", prefix='" + prefix + "'" +
        ", isTruncated=" + isTruncated +
        ", commonPrefixes=" + commonPrefixes +
        '}';
  }

  /**
   * Returns a new builder for this response.
   *
   * @return A new builder.
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A builder for {@link ListMultipartUploadsResponse}.
   *
   * @since 2.60.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static final class Builder {
    private ImmutableList<MultipartUpload> uploads;
    private String bucket;
    private String delimiter;
    private String encodingType;
    private String keyMarker;
    private String uploadIdMarker;
    private String nextKeyMarker;
    private String nextUploadIdMarker;
    private int maxUploads;
    private String prefix;
    private boolean isTruncated;
    private ImmutableList<String> commonPrefixes;

    private Builder() {}

    /**
     * Sets the list of multipart uploads.
     *
     * @param uploads The list of multipart uploads.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setUploads(ImmutableList<MultipartUpload> uploads) {
      this.uploads = uploads;
      return this;
    }

    /**
     * Sets the bucket that contains the multipart uploads.
     *
     * @param bucket The bucket name.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setBucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * Sets the delimiter applied to the request.
     *
     * @param delimiter The delimiter applied to the request.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setDelimiter(String delimiter) {
      this.delimiter = delimiter;
      return this;
    }

    /**
     * Sets the encoding type used by Cloud Storage to encode object names in the response.
     *
     * @param encodingType The encoding type.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setEncodingType(String encodingType) {
      this.encodingType = encodingType;
      return this;
    }

    /**
     * Sets the key at or after which the listing began.
     *
     * @param keyMarker The key marker.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setKeyMarker(String keyMarker) {
      this.keyMarker = keyMarker;
      return this;
    }

    /**
     * Sets the upload ID at or after which the listing began.
     *
     * @param uploadIdMarker The upload ID marker.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setUploadIdMarker(String uploadIdMarker) {
      this.uploadIdMarker = uploadIdMarker;
      return this;
    }

    /**
     * Sets the key after which listing should begin.
     *
     * @param nextKeyMarker The key after which listing should begin.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setNextKeyMarker(String nextKeyMarker) {
      this.nextKeyMarker = nextKeyMarker;
      return this;
    }

    /**
     * Sets the upload ID after which listing should begin.
     *
     * @param nextUploadIdMarker The upload ID after which listing should begin.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setNextUploadIdMarker(String nextUploadIdMarker) {
      this.nextUploadIdMarker = nextUploadIdMarker;
      return this;
    }

    /**
     * Sets the maximum number of uploads to return.
     *
     * @param maxUploads The maximum number of uploads.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setMaxUploads(int maxUploads) {
      this.maxUploads = maxUploads;
      return this;
    }

    /**
     * Sets the prefix applied to the request.
     *
     * @param prefix The prefix applied to the request.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }

    /**
     * Sets the flag indicating whether or not the returned results are truncated.
     *
     * @param isTruncated The flag indicating whether or not the returned results are truncated.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setTruncated(boolean isTruncated) {
      this.isTruncated = isTruncated;
      return this;
    }

    /**
     * If you specify a delimiter in the request, this element is returned.
     *
     * @param commonPrefixes The common prefixes.
     * @return This builder.
     * @since 2.60.0 This new api is in preview.
     */
    @BetaApi
    public Builder setCommonPrefixes(ImmutableList<String> commonPrefixes) {
      this.commonPrefixes = commonPrefixes;
      return this;
    }

    /**
     * Builds the response.
     *
     * @return The built response.
     * @since 2.60.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    public ListMultipartUploadsResponse build() {
      return new ListMultipartUploadsResponse(
          uploads,
          bucket,
          delimiter,
          encodingType,
          keyMarker,
          uploadIdMarker,
          nextKeyMarker,
          nextUploadIdMarker,
          maxUploads,
          prefix,
          isTruncated,
          commonPrefixes);
    }
  }
}
