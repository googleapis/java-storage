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

/** Represents a completed part of a multipart upload. */
public final class CompletedPart {

  @JacksonXmlProperty(localName = "PartNumber")
  private final int partNumber;

  @JacksonXmlProperty(localName = "ETag")
  private final String eTag;

  private CompletedPart(int partNumber, String eTag) {
    this.partNumber = partNumber;
    this.eTag = eTag;
  }

  /**
   * Creates a new builder for {@link CompletedPart}.
   *
   * @return A new builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the part number of this completed part.
   *
   * @return The part number.
   */
  public int partNumber() {
    return partNumber;
  }

  /**
   * Returns the ETag of this completed part.
   *
   * @return The ETag.
   */
  public String eTag() {
    return eTag;
  }

  /** Builder for {@link CompletedPart}. */
  public static class Builder {
    private int partNumber;
    private String etag;

    /**
     * Sets the part number of the completed part.
     *
     * @param partNumber The part number.
     * @return This builder.
     */
    public Builder partNumber(int partNumber) {
      this.partNumber = partNumber;
      return this;
    }

    /**
     * Sets the ETag of the completed part.
     *
     * @param etag The ETag.
     * @return This builder.
     */
    public Builder eTag(String etag) {
      this.etag = etag;
      return this;
    }

    /**
     * Builds the {@link CompletedPart} object.
     *
     * @return The new {@link CompletedPart} object.
     */
    public CompletedPart build() {
      return new CompletedPart(partNumber, etag);
    }
  }
}
