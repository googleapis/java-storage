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

public final class CompletedPart {

  private final int partNumber;

  private final String etag;

  private CompletedPart(int partNumber, String etag) {
    this.partNumber = partNumber;
    this.etag = etag;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getPartNumber() {
    return partNumber;
  }

  public String getEtag() {
    return etag;
  }

  public static class Builder {
    private int partNumber;
    private String etag;

    public Builder setPartNumber(int partNumber) {
      this.partNumber = partNumber;
      return this;
    }

    public Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    public CompletedPart build() {
      return new CompletedPart(partNumber, etag);
    }
  }
}
