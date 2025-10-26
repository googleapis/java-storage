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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.api.core.BetaApi;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.StorageClass;
import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;

/** Represents a response to a list parts request. */
@BetaApi
public final class ListPartsResponse {

  @JacksonXmlProperty(localName = "Bucket")
  private String bucket;

  @JacksonXmlProperty(localName = "Key")
  private String key;

  @JacksonXmlProperty(localName = "UploadId")
  private String uploadId;

  @JacksonXmlProperty(localName = "PartNumberMarker")
  private int partNumberMarker;

  @JacksonXmlProperty(localName = "NextPartNumberMarker")
  private int nextPartNumberMarker;

  @JacksonXmlProperty(localName = "MaxParts")
  private int maxParts;

  @JsonAlias("truncated") // S3 returns "truncated", GCS returns "IsTruncated"
  @JacksonXmlProperty(localName = "IsTruncated")
  private boolean isTruncated;

  @JacksonXmlProperty(localName = "Owner")
  private Acl.Entity owner;

  @JacksonXmlProperty(localName = "StorageClass")
  private StorageClass storageClass;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "Part")
  private List<Part> parts;

  private ListPartsResponse(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.uploadId = builder.uploadId;
    this.partNumberMarker = builder.partNumberMarker;
    this.nextPartNumberMarker = builder.nextPartNumberMarker;
    this.maxParts = builder.maxParts;
    this.isTruncated = builder.isTruncated;
    this.owner = builder.owner;
    this.storageClass = builder.storageClass;
    this.parts = builder.parts;
  }

  /**
   * Creates a new {@code Builder} for {@code ListPartsResponse} objects.
   *
   * @return A new {@code Builder} instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the bucket name.
   *
   * @return the bucket name.
   */
  public String getBucket() {
    return bucket;
  }

  /**
   * Returns the object name.
   *
   * @return the object name.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the upload ID.
   *
   * @return the upload ID.
   */
  public String getUploadId() {
    return uploadId;
  }

  /**
   * Returns the part number marker.
   *
   * @return the part number marker.
   */
  public int getPartNumberMarker() {
    return partNumberMarker;
  }

  /**
   * Returns the next part number marker.
   *
   * @return the next part number marker.
   */
  public int getNextPartNumberMarker() {
    return nextPartNumberMarker;
  }

  /**
   * Returns the maximum number of parts to return.
   *
   * @return the maximum number of parts to return.
   */
  public int getMaxParts() {
    return maxParts;
  }

  /**
   * Returns true if the response is truncated.
   *
   * @return true if the response is truncated.
   */
  public boolean isTruncated() {
    return isTruncated;
  }

  /**
   * Returns the owner of the object.
   *
   * @return the owner of the object.
   */
  public Acl.Entity getOwner() {
    return owner;
  }

  /**
   * Returns the storage class of the object.
   *
   * @return the storage class of the object.
   */
  public StorageClass getStorageClass() {
    return storageClass;
  }

  /**
   * Returns the list of parts.
   *
   * @return the list of parts.
   */
  public List<Part> getParts() {
    return parts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ListPartsResponse)) {
      return false;
    }
    ListPartsResponse that = (ListPartsResponse) o;
    return Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && Objects.equals(uploadId, that.uploadId)
        && Objects.equals(partNumberMarker, that.partNumberMarker)
        && Objects.equals(nextPartNumberMarker, that.nextPartNumberMarker)
        && Objects.equals(maxParts, that.maxParts)
        && Objects.equals(isTruncated, that.isTruncated)
        && Objects.equals(owner, that.owner)
        && Objects.equals(storageClass, that.storageClass)
        && Objects.equals(parts, that.parts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        bucket,
        key,
        uploadId,
        partNumberMarker,
        nextPartNumberMarker,
        maxParts,
        isTruncated,
        owner,
        storageClass,
        parts);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("uploadId", uploadId)
        .add("partNumberMarker", partNumberMarker)
        .add("nextPartNumberMarker", nextPartNumberMarker)
        .add("maxParts", maxParts)
        .add("isTruncated", isTruncated)
        .add("owner", owner)
        .add("storageClass", storageClass)
        .add("parts", parts)
        .toString();
  }

  /** Builder for {@code ListPartsResponse}. */
  @BetaApi
  public static final class Builder {
    private String bucket;
    private String key;
    private String uploadId;
    private int partNumberMarker;
    private int nextPartNumberMarker;
    private int maxParts;
    private boolean isTruncated;
    private Acl.Entity owner;
    private StorageClass storageClass;
    private List<Part> parts;

    private Builder() {}

    /**
     * Sets the bucket name.
     *
     * @param bucket The bucket name.
     * @return The builder instance.
     */
    public Builder setBucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * Sets the object name.
     *
     * @param key The object name.
     * @return The builder instance.
     */
    public Builder setKey(String key) {
      this.key = key;
      return this;
    }

    /**
     * Sets the upload ID.
     *
     * @param uploadId The upload ID.
     * @return The builder instance.
     */
    public Builder setUploadId(String uploadId) {
      this.uploadId = uploadId;
      return this;
    }

    /**
     * Sets the part number marker.
     *
     * @param partNumberMarker The part number marker.
     * @return The builder instance.
     */
    public Builder setPartNumberMarker(int partNumberMarker) {
      this.partNumberMarker = partNumberMarker;
      return this;
    }

    /**
     * Sets the next part number marker.
     *
     * @param nextPartNumberMarker The next part number marker.
     * @return The builder instance.
     */
    public Builder setNextPartNumberMarker(int nextPartNumberMarker) {
      this.nextPartNumberMarker = nextPartNumberMarker;
      return this;
    }

    /**
     * Sets the maximum number of parts to return.
     *
     * @param maxParts The maximum number of parts to return.
     * @return The builder instance.
     */
    public Builder setMaxParts(int maxParts) {
      this.maxParts = maxParts;
      return this;
    }

    /**
     * Sets whether the response is truncated.
     *
     * @param isTruncated True if the response is truncated, false otherwise.
     * @return The builder instance.
     */
    public Builder setIsTruncated(boolean isTruncated) {
      this.isTruncated = isTruncated;
      return this;
    }

    /**
     * Sets the owner of the object.
     *
     * @param owner The owner of the object.
     * @return The builder instance.
     */
    public Builder setOwner(Acl.Entity owner) {
      this.owner = owner;
      return this;
    }

    /**
     * Sets the storage class of the object.
     *
     * @param storageClass The storage class of the object.
     * @return The builder instance.
     */
    public Builder setStorageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
    }

    /**
     * Sets the list of parts.
     *
     * @param parts The list of parts.
     * @return The builder instance.
     */
    public Builder setParts(List<Part> parts) {
      this.parts = parts;
      return this;
    }

    /**
     * Builds a {@code ListPartsResponse} object.
     *
     * @return A new {@code ListPartsResponse} instance.
     */
    public ListPartsResponse build() {
      return new ListPartsResponse(this);
    }
  }
}
