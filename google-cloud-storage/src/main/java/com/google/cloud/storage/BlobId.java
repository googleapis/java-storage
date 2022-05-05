/*
 * Copyright 2015 Google LLC
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

package com.google.cloud.storage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Google Storage Object identifier. A {@code BlobId} object includes the name of the containing
 * bucket, the blob's name and possibly the blob's generation. If {@link #getGeneration()} is {@code
 * null} the identifier refers to the latest blob's generation.
 */
public final class BlobId implements Serializable {

  private static final long serialVersionUID = -6156002883225601925L;
  private final String bucket;
  private final String name;
  private final Long generation;

  private BlobId(String bucket, String name, Long generation) {
    this.bucket = bucket;
    this.name = name;
    this.generation = generation;
  }

  /** Returns the name of the bucket containing the blob. */
  public String getBucket() {
    return bucket;
  }

  /** Returns the name of the blob. */
  public String getName() {
    return name;
  }

  /** Returns blob's data generation. Used for versioning. */
  public Long getGeneration() {
    return generation;
  }

  /** Returns this blob's Storage url which can be used with gsutil */
  public String toGsUtilUri() {
    return "gs://" + bucket + "/" + name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", getBucket())
        .add("name", getName())
        .add("generation", getGeneration())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, name, generation);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !obj.getClass().equals(BlobId.class)) {
      return false;
    }
    BlobId other = (BlobId) obj;
    return Objects.equals(bucket, other.bucket)
        && Objects.equals(name, other.name)
        && Objects.equals(generation, other.generation);
  }

  /**
   * Creates a blob identifier. Generation is set to {@code null}.
   *
   * @param bucket the name of the bucket that contains the blob
   * @param name the name of the blob
   */
  public static BlobId of(String bucket, String name) {
    return new BlobId(checkNotNull(bucket), checkNotNull(name), null);
  }

  /**
   * Creates a {@code BlobId} object.
   *
   * @param bucket name of the containing bucket
   * @param name blob's name
   * @param generation blob's data generation, used for versioning. If {@code null} the identifier
   *     refers to the latest blob's generation
   */
  public static BlobId of(String bucket, String name, Long generation) {
    return new BlobId(checkNotNull(bucket), checkNotNull(name), generation);
  }

  /**
   * Creates a {@code BlobId} object.
   *
   * @param gsUtilUri the Storage url to create the blob from
   */
  public static BlobId fromGsUtilUri(String gsUtilUri) {
    if (!Pattern.matches("gs://.*/.*", gsUtilUri)) {
      throw new IllegalArgumentException(
          gsUtilUri + " is not a valid gsutil URI (i.e. \"gs://bucket/blob\")");
    }
    int blobNameStartIndex = gsUtilUri.indexOf('/', 5);
    String bucketName = gsUtilUri.substring(5, blobNameStartIndex);
    String blobName = gsUtilUri.substring(blobNameStartIndex + 1);

    return BlobId.of(bucketName, blobName);
  }
}
