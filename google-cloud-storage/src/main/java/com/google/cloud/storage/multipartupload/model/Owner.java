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

/** Represents the owner of a resource. */
public final class Owner {

  @JacksonXmlProperty(localName = "ID")
  private String id;

  @JacksonXmlProperty(localName = "DisplayName")
  private String displayName;

  // for jackson
  private Owner() {}

  private Owner(Builder builder) {
    this.id = builder.id;
    this.displayName = builder.displayName;
  }

  /**
   * Returns the ID of the owner.
   *
   * @return the ID of the owner.
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the display name of the owner.
   *
   * @return the display name of the owner.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns a new builder for this class.
   *
   * @return a new builder for this class.
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Owner)) {
      return false;
    }
    Owner owner = (Owner) o;
    return Objects.equals(id, owner.id) && Objects.equals(displayName, owner.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, displayName);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("displayName", displayName)
        .toString();
  }

  /** A builder for {@link Owner}. */
  public static final class Builder {
    private String id;
    private String displayName;

    private Builder() {}

    /**
     * Sets the ID of the owner.
     *
     * @param id the ID of the owner.
     * @return this builder.
     */
    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the display name of the owner.
     *
     * @param displayName the display name of the owner.
     * @return this builder.
     */
    public Builder setDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    /**
     * Builds a new {@link Owner} object.
     *
     * @return a new {@link Owner} object.
     */
    public Owner build() {
      return new Owner(this);
    }
  }
}
