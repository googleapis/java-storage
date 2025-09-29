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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class Part {

  @JacksonXmlProperty(localName = "PartNumber")
  private int partNumber;

  @JacksonXmlProperty(localName = "ETag")
  private String eTag;

  @JacksonXmlProperty(localName = "Size")
  private long size;

  @JacksonXmlProperty(localName = "LastModified")
  private String lastModified;

  public int getPartNumber() {
    return partNumber;
  }

  public String getETag() {
    return eTag;
  }

  public long getSize() {
    return size;
  }

  public String getLastModified() {
    return lastModified;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Part)) {
      return false;
    }
    Part that = (Part) o;
    return Objects.equals(partNumber, that.partNumber)
        && Objects.equals(eTag, that.eTag)
        && Objects.equals(size, that.size)
        && Objects.equals(lastModified, that.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(partNumber, eTag, size, lastModified);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("partNumber", partNumber)
        .add("eTag", eTag)
        .add("size", size)
        .add("lastModified", lastModified)
        .toString();
  }
}
