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

import com.google.cloud.storage.ObjectLockMode;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.StorageClass;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a request to initiate a multipart upload. This class holds all the necessary
 * information to create a new multipart upload session.
 */
public final class CreateMultipartUploadRequest {
  private final String bucket;
  private final String key;
  private final PredefinedAcl cannedAcl;
  private final String contentType;
  private final Map<String, String> metadata;
  private final StorageClass storageClass;
  private final Date customTime;
  private final String kmsKeyName;
  private final ObjectLockMode objectLockMode;
  private final Date objectLockRetainUntilDate;

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

  public String bucket() {
    return bucket;
  }

  public String key() {
    return key;
  }

  public PredefinedAcl getCannedAcl() {
    return cannedAcl;
  }

  public String getContentType() {
    return contentType;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public StorageClass getStorageClass() {
    return storageClass;
  }

  public Date getCustomTime() {
    return customTime;
  }

  public String getKmsKeyName() {
    return kmsKeyName;
  }

  public ObjectLockMode getObjectLockMode() {
    return objectLockMode;
  }

  public Date getObjectLockRetainUntilDate() {
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String bucket;
    private String key;
    private PredefinedAcl cannedAcl;
    private String contentType;
    private Map<String, String> metadata;
    private StorageClass storageClass;
    private Date customTime;
    private String kmsKeyName;
    private ObjectLockMode objectLockMode;
    private Date objectLockRetainUntilDate;

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
    public Builder customTime(Date customTime) {
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
    public Builder objectLockRetainUntilDate(Date objectLockRetainUntilDate) {
      this.objectLockRetainUntilDate = objectLockRetainUntilDate;
      return this;
    }

    public CreateMultipartUploadRequest build() {
      return new CreateMultipartUploadRequest(this);
    }
  }
}
