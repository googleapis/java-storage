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
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.StorageClass;
import com.google.common.base.MoreObjects;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a request to initiate a multipart upload. This class holds all the necessary
 * information to create a new multipart upload session.
 */
@BetaApi
public final class CreateMultipartUploadRequest {
  private final String bucket;
  private final String key;
  private final PredefinedAcl cannedAcl;
  private final String contentType;
  private final Map<String, String> metadata;
  private final StorageClass storageClass;
  private final OffsetDateTime customTime;
  private final String kmsKeyName;
  private final ObjectLockMode objectLockMode;
  private final OffsetDateTime objectLockRetainUntilDate;

  private CreateMultipartUploadRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.cannedAcl = builder.cannedAcl;
    this.contentType = builder.contentType;
    this.metadata = builder.metadata;
    this.storageClass = builder.storageClass;
    this.customTime = builder.customTime;
    this.kmsKeyName = builder.kmsKeyName;
    this.objectLockMode = builder.objectLockMode;
    this.objectLockRetainUntilDate = builder.objectLockRetainUntilDate;
  }

  /**
   * Returns the name of the bucket to which the object is being uploaded.
   *
   * @return The bucket name
   */
  public String bucket() {
    return bucket;
  }

  /**
   * Returns the name of the object.
   *
   * @see <a href="https://cloud.google.com/storage/docs/objects#naming">Object Naming</a>
   * @return The object name
   */
  public String key() {
    return key;
  }

  /**
   * Returns a canned ACL to apply to the object.
   *
   * @return The canned ACL
   */
  public PredefinedAcl getCannedAcl() {
    return cannedAcl;
  }

  /**
   * Returns the MIME type of the data you are uploading.
   *
   * @return The Content-Type
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Returns the custom metadata of the object.
   *
   * @return The custom metadata
   */
  public Map<String, String> getMetadata() {
    return metadata;
  }

  /**
   * Returns the storage class for the object.
   *
   * @return The Storage-Class
   */
  public StorageClass getStorageClass() {
    return storageClass;
  }

  /**
   * Returns a user-specified date and time.
   *
   * @return The custom time
   */
  public OffsetDateTime getCustomTime() {
    return customTime;
  }

  /**
   * Returns the customer-managed encryption key to use to encrypt the object.
   *
   * @return The Cloud KMS key
   */
  public String getKmsKeyName() {
    return kmsKeyName;
  }

  /**
   * Returns the mode of the object's retention configuration.
   *
   * @return The object lock mode
   */
  public ObjectLockMode getObjectLockMode() {
    return objectLockMode;
  }

  /**
   * Returns the date that determines the time until which the object is retained as immutable.
   *
   * @return The object lock retention until date
   */
  public OffsetDateTime getObjectLockRetainUntilDate() {
    return objectLockRetainUntilDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreateMultipartUploadRequest)) {
      return false;
    }
    CreateMultipartUploadRequest that = (CreateMultipartUploadRequest) o;
    return Objects.equals(bucket, that.bucket)
        && Objects.equals(key, that.key)
        && cannedAcl == that.cannedAcl
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(storageClass, that.storageClass)
        && Objects.equals(customTime, that.customTime)
        && Objects.equals(kmsKeyName, that.kmsKeyName)
        && objectLockMode == that.objectLockMode
        && Objects.equals(objectLockRetainUntilDate, that.objectLockRetainUntilDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        bucket,
        key,
        cannedAcl,
        contentType,
        metadata,
        storageClass,
        customTime,
        kmsKeyName,
        objectLockMode,
        objectLockRetainUntilDate);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", bucket)
        .add("key", key)
        .add("cannedAcl", cannedAcl)
        .add("contentType", contentType)
        .add("metadata", metadata)
        .add("storageClass", storageClass)
        .add("customTime", customTime)
        .add("kmsKeyName", kmsKeyName)
        .add("objectLockMode", objectLockMode)
        .add("objectLockRetainUntilDate", objectLockRetainUntilDate)
        .toString();
  }

  /**
   * Returns a new {@link Builder} for creating a {@link CreateMultipartUploadRequest}.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String bucket;
    private String key;
    private PredefinedAcl cannedAcl;
    private String contentType;
    private Map<String, String> metadata;
    private StorageClass storageClass;
    private OffsetDateTime customTime;
    private String kmsKeyName;
    private ObjectLockMode objectLockMode;
    private OffsetDateTime objectLockRetainUntilDate;

    private Builder() {}

    /**
     * The bucket to which the object is being uploaded.
     *
     * @param bucket The bucket name
     * @return this builder
     */
    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * The name of the object.
     *
     * @see <a href="https://cloud.google.com/storage/docs/objects#naming">Object Naming</a>
     * @param key The object name
     * @return this builder
     */
    public Builder key(String key) {
      this.key = key;
      return this;
    }

    /**
     * A canned ACL to apply to the object.
     *
     * @param cannedAcl The canned ACL
     * @return this builder
     */
    public Builder cannedAcl(PredefinedAcl cannedAcl) {
      this.cannedAcl = cannedAcl;
      return this;
    }

    /**
     * The MIME type of the data you are uploading.
     *
     * @param contentType The Content-Type
     * @return this builder
     */
    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    /**
     * The custom metadata of the object.
     *
     * @param metadata The custom metadata
     * @return this builder
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = metadata;
      return this;
    }

    /**
     * Gives each part of the upload and the resulting object a storage class besides the default
     * storage class of the associated bucket.
     *
     * @param storageClass The Storage-Class
     * @return this builder
     */
    public Builder storageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
    }

    /**
     * A user-specified date and time.
     *
     * @param customTime The custom time
     * @return this builder
     */
    public Builder customTime(OffsetDateTime customTime) {
      this.customTime = customTime;
      return this;
    }

    /**
     * The customer-managed encryption key to use to encrypt the object. Refer: <a
     * href="https://cloud.google.com/storage/docs/encryption/customer-managed-keys">Customer
     * Managed Keys</a>
     *
     * @param kmsKeyName The Cloud KMS key
     * @return this builder
     */
    public Builder kmsKeyName(String kmsKeyName) {
      this.kmsKeyName = kmsKeyName;
      return this;
    }

    /**
     * Mode of the object's retention configuration. GOVERNANCE corresponds to unlocked mode, and
     * COMPLIANCE corresponds to locked mode.
     *
     * @param objectLockMode The object lock mode
     * @return this builder
     */
    public Builder objectLockMode(ObjectLockMode objectLockMode) {
      this.objectLockMode = objectLockMode;
      return this;
    }

    /**
     * Date that determines the time until which the object is retained as immutable.
     *
     * @param objectLockRetainUntilDate The object lock retention until date
     * @return this builder
     */
    public Builder objectLockRetainUntilDate(OffsetDateTime objectLockRetainUntilDate) {
      this.objectLockRetainUntilDate = objectLockRetainUntilDate;
      return this;
    }

    /**
     * Creates a new {@link CreateMultipartUploadRequest} object.
     *
     * @return a new {@link CreateMultipartUploadRequest} object
     */
    public CreateMultipartUploadRequest build() {
      return new CreateMultipartUploadRequest(this);
    }
  }
}
