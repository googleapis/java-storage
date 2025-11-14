/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage.multipartupload.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.api.core.BetaApi;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A response from listing all multipart uploads in a bucket.
 *
 * @see <a href="https://cloud.google.com/storage/docs/multipart-uploads#listing-uploads">Listing
 *     multipart uploads</a>
 * @since 2.60.1 This new api is in preview and is subject to breaking changes.
 */
@BetaApi
public final class ListMultipartUploadsResponse {

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "Upload")
  private List<MultipartUpload> uploads;

  @JacksonXmlProperty(localName = "Bucket")
  private String bucket;

  @JacksonXmlProperty(localName = "Delimiter")
  private String delimiter;

  @JacksonXmlProperty(localName = "EncodingType")
  private String encodingType;

  @JacksonXmlProperty(localName = "KeyMarker")
  private String keyMarker;

  @JacksonXmlProperty(localName = "UploadIdMarker")
  private String uploadIdMarker;

  @JacksonXmlProperty(localName = "NextKeyMarker")
  private String nextKeyMarker;

  @JacksonXmlProperty(localName = "NextUploadIdMarker")
  private String nextUploadIdMarker;

  @JacksonXmlProperty(localName = "MaxUploads")
  private int maxUploads;

  @JacksonXmlProperty(localName = "Prefix")
  private String prefix;

  @JsonAlias("truncated")
  @JacksonXmlProperty(localName = "IsTruncated")
  private boolean isTruncated;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "CommonPrefixes")
  private List<CommonPrefixHelper> commonPrefixes;

  // Jackson requires a no-arg constructor
  private ListMultipartUploadsResponse() {}

  private ListMultipartUploadsResponse(
      List<MultipartUpload> uploads,
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
      List<String> commonPrefixes) {
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
    if (commonPrefixes != null) {
      this.commonPrefixes = new ArrayList<>();
      for (String p : commonPrefixes) {
        CommonPrefixHelper h = new CommonPrefixHelper();
        h.prefix = p;
        this.commonPrefixes.add(h);
      }
    }
  }

  /**
   * The list of multipart uploads.
   *
   * @return The list of multipart uploads.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public ImmutableList<MultipartUpload> getUploads() {
    return uploads == null ? ImmutableList.of() : ImmutableList.copyOf(uploads);
  }

  /**
   * The bucket that contains the multipart uploads.
   *
   * @return The bucket name.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getBucket() {
    return bucket;
  }

  /**
   * The delimiter applied to the request.
   *
   * @return The delimiter applied to the request.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * The encoding type used by Cloud Storage to encode object names in the response.
   *
   * @return The encoding type.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getEncodingType() {
    return encodingType;
  }

  /**
   * The key at or after which the listing began.
   *
   * @return The key marker.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getKeyMarker() {
    return keyMarker;
  }

  /**
   * The upload ID at or after which the listing began.
   *
   * @return The upload ID marker.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getUploadIdMarker() {
    return uploadIdMarker;
  }

  /**
   * The key after which listing should begin.
   *
   * @return The key after which listing should begin.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getNextKeyMarker() {
    return nextKeyMarker;
  }

  /**
   * The upload ID after which listing should begin.
   *
   * @return The upload ID after which listing should begin.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getNextUploadIdMarker() {
    return nextUploadIdMarker;
  }

  /**
   * The maximum number of uploads to return.
   *
   * @return The maximum number of uploads.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public int getMaxUploads() {
    return maxUploads;
  }

  /**
   * The prefix applied to the request.
   *
   * @return The prefix applied to the request.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public String getPrefix() {
    return prefix;
  }

  /**
   * A flag indicating whether or not the returned results are truncated.
   *
   * @return A flag indicating whether or not the returned results are truncated.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public boolean isTruncated() {
    return isTruncated;
  }

  /**
   * If you specify a delimiter in the request, this element is returned.
   *
   * @return The common prefixes.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public ImmutableList<String> getCommonPrefixes() {
    if (commonPrefixes == null) {
      return ImmutableList.of();
    }
    return commonPrefixes.stream().map(h -> h.prefix).collect(ImmutableList.toImmutableList());
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
        && Objects.equals(getUploads(), that.getUploads())
        && Objects.equals(bucket, that.bucket)
        && Objects.equals(delimiter, that.delimiter)
        && Objects.equals(encodingType, that.encodingType)
        && Objects.equals(keyMarker, that.keyMarker)
        && Objects.equals(uploadIdMarker, that.uploadIdMarker)
        && Objects.equals(nextKeyMarker, that.nextKeyMarker)
        && Objects.equals(nextUploadIdMarker, that.nextUploadIdMarker)
        && Objects.equals(prefix, that.prefix)
        && Objects.equals(getCommonPrefixes(), that.getCommonPrefixes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getUploads(),
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
        getCommonPrefixes());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("uploads", getUploads())
        .add("bucket", bucket)
        .add("delimiter", delimiter)
        .add("encodingType", encodingType)
        .add("keyMarker", keyMarker)
        .add("uploadIdMarker", uploadIdMarker)
        .add("nextKeyMarker", nextKeyMarker)
        .add("nextUploadIdMarker", nextUploadIdMarker)
        .add("maxUploads", maxUploads)
        .add("prefix", prefix)
        .add("isTruncated", isTruncated)
        .add("commonPrefixes", getCommonPrefixes())
        .toString();
  }

  /**
   * Returns a new builder for this response.
   *
   * @return A new builder.
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static Builder builder() {
    return new Builder();
  }

  static class CommonPrefixHelper {
    @JacksonXmlProperty(localName = "Prefix")
    public String prefix;
  }

  /**
   * A builder for {@link ListMultipartUploadsResponse}.
   *
   * @since 2.61.0 This new api is in preview and is subject to breaking changes.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview.
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
     * @since 2.61.0 This new api is in preview and is subject to breaking changes.
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
