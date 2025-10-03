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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;

public final class ListPartsResponse {

  @JacksonXmlProperty(localName = "Bucket")
  private String bucket;

  @JacksonXmlProperty(localName = "Key")
  private String key;

  @JacksonXmlProperty(localName = "UploadId")
  private String uploadId;

  @JacksonXmlProperty(localName = "PartNumberMarker")
  private Integer partNumberMarker;

  @JacksonXmlProperty(localName = "NextPartNumberMarker")
  private Integer nextPartNumberMarker;

  @JacksonXmlProperty(localName = "MaxParts")
  private Integer maxParts;

  @JacksonXmlProperty(localName = "IsTruncated")
  private boolean isTruncated;

  @JacksonXmlProperty(localName = "Owner")
  private String owner;

  @JacksonXmlProperty(localName = "StorageClass")
  private String storageClass;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "Part")
  private List<Part> parts;

  public String getBucket() {
    return bucket;
  }

  public String getKey() {
    return key;
  }

  public String getUploadId() {
    return uploadId;
  }

  public Integer getPartNumberMarker() {
    return partNumberMarker;
  }

  public Integer getNextPartNumberMarker() {
    return nextPartNumberMarker;
  }

  public Integer getMaxParts() {
    return maxParts;
  }

  public boolean isTruncated() {
    return isTruncated;
  }

  public String getOwner() {
    return owner;
  }

  public String getStorageClass() {
    return storageClass;
  }

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
}
