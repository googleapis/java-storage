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

import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.common.base.MoreObjects;
import java.util.Map;
import java.util.Objects;

public class CreateMultipartUploadRequest {
  private final String bucket;

  private final String key;
  private final PredefinedAcl cannedAcl;
  private final String contentDisposition;
  private final String contentEncoding;
  private final String contentLanguage;
  private final String contentType;
  private final Map<String, String> metadata;
  private final String storageClass;
  private final String cacheControl;
  private final String customTime;
  private final String kmsKeyName;
  private final String objectLockMode;
  private final String objectLockRetainUntilDate;

  private CreateMultipartUploadRequest(Builder builder) {
    this.bucket = builder.bucket;
    this.key = builder.key;
    this.cannedAcl = builder.cannedAcl;
    this.contentDisposition = builder.contentDisposition;
    this.contentEncoding = builder.contentEncoding;
    this.contentLanguage = builder.contentLanguage;
    this.contentType = builder.contentType;
    this.metadata = builder.metadata;
    this.storageClass = builder.storageClass;
    this.cacheControl = builder.cacheControl;
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

  public String getContentDisposition() {
    return contentDisposition;
  }

  public String getContentEncoding() {
    return contentEncoding;
  }

  public String getContentLanguage() {
    return contentLanguage;
  }

  public String getContentType() {
    return contentType;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public String getStorageClass() {
    return storageClass;
  }

  public String getCacheControl() {
    return cacheControl;
  }

  public String getCustomTime() {
    return customTime;
  }

  public String getKmsKeyName() {
    return kmsKeyName;
  }

  public String getObjectLockMode() {
    return objectLockMode;
  }

  public String getObjectLockRetainUntilDate() {
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
        && Objects.equals(contentDisposition, that.contentDisposition)
        && Objects.equals(contentEncoding, that.contentEncoding)
        && Objects.equals(contentLanguage, that.contentLanguage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(storageClass, that.storageClass)
        && Objects.equals(cacheControl, that.cacheControl)
        && Objects.equals(customTime, that.customTime)
        && Objects.equals(kmsKeyName, that.kmsKeyName)
        && Objects.equals(objectLockMode, that.objectLockMode)
        && Objects.equals(objectLockRetainUntilDate, that.objectLockRetainUntilDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        bucket,
        key,
        cannedAcl,
        contentDisposition,
        contentEncoding,
        contentLanguage,
        contentType,
        metadata,
        storageClass,
        cacheControl,
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
        .add("contentDisposition", contentDisposition)
        .add("contentEncoding", contentEncoding)
        .add("contentLanguage", contentLanguage)
        .add("contentType", contentType)
        .add("metadata", metadata)
        .add("storageClass", storageClass)
        .add("cacheControl", cacheControl)
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
    private String contentDisposition;
    private String contentEncoding;
    private String contentLanguage;
    private String contentType;
    private Map<String, String> metadata;
    private String storageClass;
    private String cacheControl;
    private String customTime;
    private String kmsKeyName;
    private String objectLockMode;
    private String objectLockRetainUntilDate;

    private Builder() {}

    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder cannedAcl(PredefinedAcl cannedAcl) {
      this.cannedAcl = cannedAcl;
      return this;
    }

    public Builder contentDisposition(String contentDisposition) {
      this.contentDisposition = contentDisposition;
      return this;
    }

    public Builder contentEncoding(String contentEncoding) {
      this.contentEncoding = contentEncoding;
      return this;
    }

    public Builder contentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder metadata(Map<String, String> metadata) {
      this.metadata = metadata;
      return this;
    }

    public Builder storageClass(String storageClass) {
      this.storageClass = storageClass;
      return this;
    }

    public Builder cacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
      return this;
    }

    public Builder customTime(String customTime) {
      this.customTime = customTime;
      return this;
    }

    public Builder kmsKeyName(String kmsKeyName) {
      this.kmsKeyName = kmsKeyName;
      return this;
    }

    public Builder objectLockMode(String objectLockMode) {
      this.objectLockMode = objectLockMode;
      return this;
    }

    public Builder objectLockRetainUntilDate(String objectLockRetainUntilDate) {
      this.objectLockRetainUntilDate = objectLockRetainUntilDate;
      return this;
    }

    public CreateMultipartUploadRequest build() {
      return new CreateMultipartUploadRequest(this);
    }
  }
}
