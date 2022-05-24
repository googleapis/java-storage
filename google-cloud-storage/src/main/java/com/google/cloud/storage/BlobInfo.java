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

import static com.google.cloud.storage.BackwardCompatibilityUtils.millisOffsetDateTimeCodec;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.client.util.Data;
import com.google.api.core.BetaApi;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Information about an object in Google Cloud Storage. A {@code BlobInfo} object includes the
 * {@code BlobId} instance and the set of properties, such as the blob's access control
 * configuration, user provided metadata, the CRC32C checksum, etc. Instances of this class are used
 * to create a new object in Google Cloud Storage or update the properties of an existing object. To
 * deal with existing Storage objects the API includes the {@link Blob} class which extends {@code
 * BlobInfo} and declares methods to perform operations on the object. Neither {@code BlobInfo} nor
 * {@code Blob} instances keep the object content, just the object properties.
 *
 * <p>Example of usage {@code BlobInfo} to create an object in Google Cloud Storage:
 *
 * <pre>{@code
 * BlobId blobId = BlobId.of(bucketName, blobName);
 * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
 * Blob blob = storage.create(blobInfo, "Hello, world".getBytes(StandardCharsets.UTF_8));
 * }</pre>
 *
 * @see <a href="https://cloud.google.com/storage/docs/concepts-techniques#concepts">Concepts and
 *     Terminology</a>
 */
public class BlobInfo implements Serializable {

  private static final long serialVersionUID = -5625857076205028976L;
  private final BlobId blobId;
  private final String generatedId;
  private final String selfLink;
  private final String cacheControl;
  private final List<Acl> acl;
  private final Acl.Entity owner;
  private final Long size;
  private final String etag;
  private final String md5;
  private final String crc32c;
  private final OffsetDateTime customTime;
  private final String mediaLink;
  /**
   * The getter for this property never returns null, however null awareness is critical for
   * encoding
   *
   * @see ApiaryConversions#blobInfo() encoder
   */
  final Map<String, String> metadata;

  private final Long metageneration;
  private final OffsetDateTime deleteTime;
  private final OffsetDateTime updateTime;
  private final OffsetDateTime createTime;
  private final String contentType;
  private final String contentEncoding;
  private final String contentDisposition;
  private final String contentLanguage;
  private final StorageClass storageClass;
  private final OffsetDateTime timeStorageClassUpdated;
  private final Integer componentCount;
  private final boolean isDirectory;
  private final CustomerEncryption customerEncryption;
  private final String kmsKeyName;
  private final Boolean eventBasedHold;
  private final Boolean temporaryHold;
  private final OffsetDateTime retentionExpirationTime;

  /** This class is meant for internal use only. Users are discouraged from using this class. */
  public static final class ImmutableEmptyMap<K, V> extends AbstractMap<K, V> {

    @Override
    public Set<Entry<K, V>> entrySet() {
      return ImmutableSet.of();
    }
  }

  /**
   * Objects of this class hold information on the customer-supplied encryption key, if the blob is
   * encrypted using such a key.
   */
  public static class CustomerEncryption implements Serializable {

    private static final long serialVersionUID = -2133042982786959351L;

    private final String encryptionAlgorithm;
    private final String keySha256;

    CustomerEncryption(String encryptionAlgorithm, String keySha256) {
      this.encryptionAlgorithm = encryptionAlgorithm;
      this.keySha256 = keySha256;
    }

    /** Returns the algorithm used to encrypt the blob. */
    public String getEncryptionAlgorithm() {
      return encryptionAlgorithm;
    }

    /** Returns the SHA256 hash of the encryption key. */
    public String getKeySha256() {
      return keySha256;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("encryptionAlgorithm", getEncryptionAlgorithm())
          .add("keySha256", getKeySha256())
          .toString();
    }

    @Override
    public final int hashCode() {
      return Objects.hash(encryptionAlgorithm, keySha256);
    }

    @Override
    public final boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof CustomerEncryption)) {
        return false;
      }
      CustomerEncryption that = (CustomerEncryption) o;
      return Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm)
          && Objects.equals(keySha256, that.keySha256);
    }
  }

  /** Builder for {@code BlobInfo}. */
  public abstract static class Builder {

    /** Sets the blob identity. */
    public abstract Builder setBlobId(BlobId blobId);

    abstract Builder setGeneratedId(String generatedId);

    /**
     * Sets the blob's data content type.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2616#section-14.17">Content-Type</a>
     */
    public abstract Builder setContentType(String contentType);

    /**
     * Sets the blob's data content disposition.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6266">Content-Disposition</a>
     */
    public abstract Builder setContentDisposition(String contentDisposition);

    /**
     * Sets the blob's data content language.
     *
     * @see <a href="http://tools.ietf.org/html/bcp47">Content-Language</a>
     */
    public abstract Builder setContentLanguage(String contentLanguage);

    /**
     * Sets the blob's data content encoding.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.2.2">Content-Encoding</a>
     */
    public abstract Builder setContentEncoding(String contentEncoding);

    abstract Builder setComponentCount(Integer componentCount);

    /**
     * Sets the blob's data cache control.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">Cache-Control</a>
     */
    public abstract Builder setCacheControl(String cacheControl);

    /**
     * Sets the blob's access control configuration.
     *
     * @see <a
     *     href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
     *     About Access Control Lists</a>
     */
    public abstract Builder setAcl(List<Acl> acl);

    abstract Builder setOwner(Acl.Entity owner);

    abstract Builder setSize(Long size);

    abstract Builder setEtag(String etag);

    abstract Builder setSelfLink(String selfLink);

    /**
     * Sets the MD5 hash of blob's data. MD5 value must be encoded in base64.
     *
     * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
     *     Best Practices</a>
     */
    public abstract Builder setMd5(String md5);

    /**
     * Sets the MD5 hash of blob's data from hex string.
     *
     * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
     *     Best Practices</a>
     * @throws IllegalArgumentException when given an invalid hexadecimal value.
     */
    public abstract Builder setMd5FromHexString(String md5HexString);

    /**
     * Sets the CRC32C checksum of blob's data as described in <a
     * href="http://tools.ietf.org/html/rfc4960#appendix-B">RFC 4960, Appendix B;</a> encoded in
     * base64 in big-endian order.
     *
     * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
     *     Best Practices</a>
     */
    public abstract Builder setCrc32c(String crc32c);

    /**
     * Sets the custom time for an object. Once set it can't be unset and only changed to a custom
     * datetime in the future. To unset the custom time, you must either perform a rewrite operation
     * or upload the data again.
     *
     * <p>Example of setting the custom time.
     *
     * <pre>{@code
     * String bucketName = "my-unique-bucket";
     * String blobName = "my-blob-name";
     * long customTime = 1598423868301L;
     * BlobInfo blob = BlobInfo.newBuilder(bucketName, blobName).setCustomTime(customTime).build();
     * }</pre>
     *
     * @deprecated Use {@link #setCustomTimeOffsetDateTime(OffsetDateTime)}
     */
    @Deprecated
    public Builder setCustomTime(Long customTime) {
      throw new UnsupportedOperationException(
          "Override setCustomTime with your own implementation,"
              + " or use com.google.cloud.storage.Blob.");
    }

    /**
     * Sets the custom time for an object. Once set it can't be unset and only changed to a custom
     * datetime in the future. To unset the custom time, you must either perform a rewrite operation
     * or upload the data again.
     *
     * <p>Example of setting the custom time.
     *
     * <pre>{@code
     * String bucketName = "my-unique-bucket";
     * String blobName = "my-blob-name";
     * OffsetDateTime customTime = Instant.ofEpochMilli(1598423868301L).atOffset(0); // UTC
     * BlobInfo blob = BlobInfo.newBuilder(bucketName, blobName).setCustomTime(customTime).build();
     * }</pre>
     */
    public Builder setCustomTimeOffsetDateTime(OffsetDateTime customTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setCustomTime(millisOffsetDateTimeCodec.decode(customTime));
    }

    /**
     * Sets the CRC32C checksum of blob's data as described in <a
     * href="http://tools.ietf.org/html/rfc4960#appendix-B">RFC 4960, Appendix B;</a> from hex
     * string.
     *
     * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
     *     Best Practices</a>
     * @throws IllegalArgumentException when given an invalid hexadecimal value.
     */
    public abstract Builder setCrc32cFromHexString(String crc32cHexString);

    abstract Builder setMediaLink(String mediaLink);

    /** Sets the blob's storage class. */
    public abstract Builder setStorageClass(StorageClass storageClass);

    /**
     * Sets the modification time of an object's storage class. Once set it can't be unset directly,
     * the only way is to rewrite the object with the desired storage class.
     *
     * @deprecated Use {@link #setTimeStorageClassUpdatedOffsetDateTime(OffsetDateTime)}
     */
    @Deprecated
    public Builder setTimeStorageClassUpdated(Long timeStorageClassUpdated) {
      throw new UnsupportedOperationException(
          "Override setTimeStorageClassUpdated with your own implementation,"
              + " or use com.google.cloud.storage.Blob.");
    }

    public Builder setTimeStorageClassUpdatedOffsetDateTime(
        OffsetDateTime timeStorageClassUpdated) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setTimeStorageClassUpdated(millisOffsetDateTimeCodec.decode(timeStorageClassUpdated));
    }

    /** Sets the blob's user provided metadata. */
    public abstract Builder setMetadata(Map<String, String> metadata);

    abstract Builder setMetageneration(Long metageneration);

    /** @deprecated Use {@link #setDeleteTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    abstract Builder setDeleteTime(Long deleteTime);

    Builder setDeleteTimeOffsetDateTime(OffsetDateTime deleteTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setDeleteTime(millisOffsetDateTimeCodec.decode(deleteTime));
    }

    /** @deprecated Use {@link #setUpdateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    abstract Builder setUpdateTime(Long updateTime);

    Builder setUpdateTimeOffsetDateTime(OffsetDateTime updateTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setUpdateTime(millisOffsetDateTimeCodec.decode(updateTime));
    }

    /** @deprecated Use {@link #setCreateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    abstract Builder setCreateTime(Long createTime);

    Builder setCreateTimeOffsetDateTime(OffsetDateTime createTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setCreateTime(millisOffsetDateTimeCodec.decode(createTime));
    }

    abstract Builder setIsDirectory(boolean isDirectory);

    abstract Builder setCustomerEncryption(CustomerEncryption customerEncryption);

    /**
     * Sets a customer-managed key for server-side encryption of the blob. Note that when a KMS key
     * is used to encrypt Cloud Storage object, object resource metadata will store the version of
     * the KMS cryptographic. If a {@code Blob} with KMS Key metadata is used to upload a new
     * version of the object then the existing kmsKeyName version value can't be used in the upload
     * request and the client instead ignores it.
     *
     * <p>Example of setting the KMS key name
     *
     * <pre>{@code
     * String bucketName = "my-unique-bucket";
     * String blobName = "my-blob-name";
     * String kmsKeyName = "projects/project-id/locations/us/keyRings/lab1/cryptoKeys/test-key"
     * BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
     * Blob blob = storage.create(blobInfo, Storage.BlobTargetOption.kmsKeyName(kmsKeyName));
     * }</pre>
     */
    abstract Builder setKmsKeyName(String kmsKeyName);

    /** Sets the blob's event-based hold. */
    @BetaApi
    public abstract Builder setEventBasedHold(Boolean eventBasedHold);

    /** Sets the blob's temporary hold. */
    @BetaApi
    public abstract Builder setTemporaryHold(Boolean temporaryHold);

    /** @deprecated {@link #setRetentionExpirationTimeOffsetDateTime(OffsetDateTime)} */
    @BetaApi
    @Deprecated
    abstract Builder setRetentionExpirationTime(Long retentionExpirationTime);

    @BetaApi
    Builder setRetentionExpirationTimeOffsetDateTime(OffsetDateTime retentionExpirationTime) {
      // provide an implementation for source and binary compatibility which we override ourselves
      return setRetentionExpirationTime(millisOffsetDateTimeCodec.decode(retentionExpirationTime));
    }

    /** Creates a {@code BlobInfo} object. */
    public abstract BlobInfo build();
  }

  static final class BuilderImpl extends Builder {
    private final String hexDecimalValues = "0123456789abcdef";
    private BlobId blobId;
    private String generatedId;
    private String contentType;
    private String contentEncoding;
    private String contentDisposition;
    private String contentLanguage;
    private Integer componentCount;
    private String cacheControl;
    private List<Acl> acl;
    private Acl.Entity owner;
    private Long size;
    private String etag;
    private String selfLink;
    private String md5;
    private String crc32c;
    private OffsetDateTime customTime;
    private String mediaLink;
    private Map<String, String> metadata;
    private Long metageneration;
    private OffsetDateTime deleteTime;
    private OffsetDateTime updateTime;
    private OffsetDateTime createTime;
    private Boolean isDirectory;
    private CustomerEncryption customerEncryption;
    private StorageClass storageClass;
    private OffsetDateTime timeStorageClassUpdated;
    private String kmsKeyName;
    private Boolean eventBasedHold;
    private Boolean temporaryHold;
    private OffsetDateTime retentionExpirationTime;

    BuilderImpl(BlobId blobId) {
      this.blobId = blobId;
    }

    BuilderImpl(BlobInfo blobInfo) {
      blobId = blobInfo.blobId;
      generatedId = blobInfo.generatedId;
      cacheControl = blobInfo.cacheControl;
      contentEncoding = blobInfo.contentEncoding;
      contentType = blobInfo.contentType;
      contentDisposition = blobInfo.contentDisposition;
      contentLanguage = blobInfo.contentLanguage;
      componentCount = blobInfo.componentCount;
      customerEncryption = blobInfo.customerEncryption;
      acl = blobInfo.acl;
      owner = blobInfo.owner;
      size = blobInfo.size;
      etag = blobInfo.etag;
      selfLink = blobInfo.selfLink;
      md5 = blobInfo.md5;
      crc32c = blobInfo.crc32c;
      customTime = blobInfo.customTime;
      mediaLink = blobInfo.mediaLink;
      metadata = blobInfo.metadata;
      metageneration = blobInfo.metageneration;
      deleteTime = blobInfo.deleteTime;
      updateTime = blobInfo.updateTime;
      createTime = blobInfo.createTime;
      isDirectory = blobInfo.isDirectory;
      storageClass = blobInfo.storageClass;
      timeStorageClassUpdated = blobInfo.timeStorageClassUpdated;
      kmsKeyName = blobInfo.kmsKeyName;
      eventBasedHold = blobInfo.eventBasedHold;
      temporaryHold = blobInfo.temporaryHold;
      retentionExpirationTime = blobInfo.retentionExpirationTime;
    }

    @Override
    public Builder setBlobId(BlobId blobId) {
      this.blobId = checkNotNull(blobId);
      return this;
    }

    @Override
    Builder setGeneratedId(String generatedId) {
      this.generatedId = generatedId;
      return this;
    }

    @Override
    public Builder setContentType(String contentType) {
      this.contentType = firstNonNull(contentType, Data.nullOf(String.class));
      return this;
    }

    @Override
    public Builder setContentDisposition(String contentDisposition) {
      this.contentDisposition = firstNonNull(contentDisposition, Data.nullOf(String.class));
      return this;
    }

    @Override
    public Builder setContentLanguage(String contentLanguage) {
      this.contentLanguage = firstNonNull(contentLanguage, Data.nullOf(String.class));
      return this;
    }

    @Override
    public Builder setContentEncoding(String contentEncoding) {
      this.contentEncoding = firstNonNull(contentEncoding, Data.nullOf(String.class));
      return this;
    }

    @Override
    Builder setComponentCount(Integer componentCount) {
      this.componentCount = componentCount;
      return this;
    }

    @Override
    public Builder setCacheControl(String cacheControl) {
      this.cacheControl = firstNonNull(cacheControl, Data.nullOf(String.class));
      return this;
    }

    @Override
    public Builder setAcl(List<Acl> acl) {
      this.acl = acl != null ? ImmutableList.copyOf(acl) : null;
      return this;
    }

    @Override
    Builder setOwner(Acl.Entity owner) {
      this.owner = owner;
      return this;
    }

    @Override
    Builder setSize(Long size) {
      this.size = size;
      return this;
    }

    @Override
    Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    @Override
    Builder setSelfLink(String selfLink) {
      this.selfLink = selfLink;
      return this;
    }

    @Override
    public Builder setMd5(String md5) {
      this.md5 = firstNonNull(md5, Data.nullOf(String.class));
      return this;
    }

    @Override
    public Builder setMd5FromHexString(String md5HexString) {
      if (md5HexString == null) {
        return this;
      }
      if (md5HexString.length() % 2 != 0) {
        throw new IllegalArgumentException(
            "each byte must be represented by 2 valid hexadecimal characters");
      }
      String md5HexStringLower = md5HexString.toLowerCase();
      ByteBuffer md5ByteBuffer = ByteBuffer.allocate(md5HexStringLower.length() / 2);
      for (int charIndex = 0; charIndex < md5HexStringLower.length(); charIndex += 2) {
        int higherOrderBits = this.hexDecimalValues.indexOf(md5HexStringLower.charAt(charIndex));
        int lowerOrderBits = this.hexDecimalValues.indexOf(md5HexStringLower.charAt(charIndex + 1));
        if (higherOrderBits == -1 || lowerOrderBits == -1) {
          throw new IllegalArgumentException(
              "each byte must be represented by 2 valid hexadecimal characters");
        }
        md5ByteBuffer.put((byte) (higherOrderBits << 4 | lowerOrderBits));
      }
      this.md5 = BaseEncoding.base64().encode(md5ByteBuffer.array());
      return this;
    }

    @Override
    public Builder setCrc32c(String crc32c) {
      this.crc32c = firstNonNull(crc32c, Data.nullOf(String.class));
      return this;
    }

    /** @deprecated {@link #setCustomTimeOffsetDateTime(OffsetDateTime)} */
    @Override
    @Deprecated
    public Builder setCustomTime(Long customTime) {
      return setCustomTimeOffsetDateTime(millisOffsetDateTimeCodec.encode(customTime));
    }

    @Override
    public Builder setCustomTimeOffsetDateTime(OffsetDateTime customTime) {
      this.customTime = customTime;
      return this;
    }

    @Override
    public Builder setCrc32cFromHexString(String crc32cHexString) {
      if (crc32cHexString == null) {
        return this;
      }
      if (crc32cHexString.length() % 2 != 0) {
        throw new IllegalArgumentException(
            "each byte must be represented by 2 valid hexadecimal characters");
      }
      String crc32cHexStringLower = crc32cHexString.toLowerCase();
      ByteBuffer crc32cByteBuffer = ByteBuffer.allocate(crc32cHexStringLower.length() / 2);
      for (int charIndex = 0; charIndex < crc32cHexStringLower.length(); charIndex += 2) {
        int higherOrderBits = this.hexDecimalValues.indexOf(crc32cHexStringLower.charAt(charIndex));
        int lowerOrderBits =
            this.hexDecimalValues.indexOf(crc32cHexStringLower.charAt(charIndex + 1));
        if (higherOrderBits == -1 || lowerOrderBits == -1) {
          throw new IllegalArgumentException(
              "each byte must be represented by 2 valid hexadecimal characters");
        }
        crc32cByteBuffer.put((byte) (higherOrderBits << 4 | lowerOrderBits));
      }
      this.crc32c = BaseEncoding.base64().encode(crc32cByteBuffer.array());
      return this;
    }

    @Override
    Builder setMediaLink(String mediaLink) {
      this.mediaLink = mediaLink;
      return this;
    }

    @Override
    public Builder setMetadata(Map<String, String> metadata) {
      if (metadata != null) {
        this.metadata = new HashMap<>(metadata);
      } else {
        this.metadata = (Map<String, String>) Data.nullOf(ImmutableEmptyMap.class);
      }
      return this;
    }

    @Override
    public Builder setStorageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
    }

    /** @deprecated Use {@link #setTimeStorageClassUpdatedOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    @Override
    public Builder setTimeStorageClassUpdated(Long timeStorageClassUpdated) {
      return setTimeStorageClassUpdatedOffsetDateTime(
          millisOffsetDateTimeCodec.encode(timeStorageClassUpdated));
    }

    @Override
    public Builder setTimeStorageClassUpdatedOffsetDateTime(
        OffsetDateTime timeStorageClassUpdated) {
      this.timeStorageClassUpdated = timeStorageClassUpdated;
      return this;
    }

    @Override
    Builder setMetageneration(Long metageneration) {
      this.metageneration = metageneration;
      return this;
    }

    /** @deprecated Use {@link #setDeleteTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    @Override
    Builder setDeleteTime(Long deleteTime) {
      return setDeleteTimeOffsetDateTime(millisOffsetDateTimeCodec.encode(deleteTime));
    }

    @Override
    Builder setDeleteTimeOffsetDateTime(OffsetDateTime deleteTime) {
      this.deleteTime = deleteTime;
      return this;
    }

    /** @deprecated Use {@link #setUpdateTimeOffsetDateTime(OffsetDateTime)} */
    @Override
    Builder setUpdateTime(Long updateTime) {
      return setUpdateTimeOffsetDateTime(millisOffsetDateTimeCodec.encode(updateTime));
    }

    @Override
    Builder setUpdateTimeOffsetDateTime(OffsetDateTime updateTime) {
      this.updateTime = updateTime;
      return this;
    }

    /** @deprecated Use {@link #setCreateTimeOffsetDateTime(OffsetDateTime)} */
    @Deprecated
    @Override
    Builder setCreateTime(Long createTime) {
      return setCreateTimeOffsetDateTime(millisOffsetDateTimeCodec.encode(createTime));
    }

    @Override
    Builder setCreateTimeOffsetDateTime(OffsetDateTime createTime) {
      this.createTime = createTime;
      return this;
    }

    @Override
    Builder setIsDirectory(boolean isDirectory) {
      this.isDirectory = isDirectory;
      return this;
    }

    @Override
    Builder setCustomerEncryption(CustomerEncryption customerEncryption) {
      this.customerEncryption = customerEncryption;
      return this;
    }

    @Override
    Builder setKmsKeyName(String kmsKeyName) {
      this.kmsKeyName = kmsKeyName;
      return this;
    }

    @Override
    public Builder setEventBasedHold(Boolean eventBasedHold) {
      this.eventBasedHold = eventBasedHold;
      return this;
    }

    @Override
    public Builder setTemporaryHold(Boolean temporaryHold) {
      this.temporaryHold = temporaryHold;
      return this;
    }

    /** @deprecated {@link #setRetentionExpirationTimeOffsetDateTime(OffsetDateTime)} */
    @Override
    @Deprecated
    Builder setRetentionExpirationTime(Long retentionExpirationTime) {
      return setRetentionExpirationTimeOffsetDateTime(
          millisOffsetDateTimeCodec.encode(retentionExpirationTime));
    }

    @Override
    Builder setRetentionExpirationTimeOffsetDateTime(OffsetDateTime retentionExpirationTime) {
      this.retentionExpirationTime = retentionExpirationTime;
      return this;
    }

    @Override
    public BlobInfo build() {
      checkNotNull(blobId);
      return new BlobInfo(this);
    }
  }

  BlobInfo(BuilderImpl builder) {
    blobId = builder.blobId;
    generatedId = builder.generatedId;
    cacheControl = builder.cacheControl;
    contentEncoding = builder.contentEncoding;
    contentType = builder.contentType;
    contentDisposition = builder.contentDisposition;
    contentLanguage = builder.contentLanguage;
    componentCount = builder.componentCount;
    customerEncryption = builder.customerEncryption;
    acl = builder.acl;
    owner = builder.owner;
    size = builder.size;
    etag = builder.etag;
    selfLink = builder.selfLink;
    md5 = builder.md5;
    crc32c = builder.crc32c;
    customTime = builder.customTime;
    mediaLink = builder.mediaLink;
    metadata = builder.metadata;
    metageneration = builder.metageneration;
    deleteTime = builder.deleteTime;
    updateTime = builder.updateTime;
    createTime = builder.createTime;
    isDirectory = firstNonNull(builder.isDirectory, Boolean.FALSE);
    storageClass = builder.storageClass;
    timeStorageClassUpdated = builder.timeStorageClassUpdated;
    kmsKeyName = builder.kmsKeyName;
    eventBasedHold = builder.eventBasedHold;
    temporaryHold = builder.temporaryHold;
    retentionExpirationTime = builder.retentionExpirationTime;
  }

  /** Returns the blob's identity. */
  public BlobId getBlobId() {
    return blobId;
  }

  /** Returns the name of the containing bucket. */
  public String getBucket() {
    return getBlobId().getBucket();
  }

  /** Returns the service-generated for the blob. */
  public String getGeneratedId() {
    return generatedId;
  }

  /** Returns the blob's name. */
  public String getName() {
    return getBlobId().getName();
  }

  /**
   * Returns the blob's data cache control.
   *
   * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">Cache-Control</a>
   */
  public String getCacheControl() {
    return Data.isNull(cacheControl) ? null : cacheControl;
  }

  /**
   * Returns the blob's access control configuration.
   *
   * @see <a href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
   *     About Access Control Lists</a>
   */
  public List<Acl> getAcl() {
    return acl;
  }

  /** Returns the blob's owner. This will always be the uploader of the blob. */
  public Acl.Entity getOwner() {
    return owner;
  }

  /**
   * Returns the content length of the data in bytes.
   *
   * @see <a href="https://tools.ietf.org/html/rfc2616#section-14.13">Content-Length</a>
   */
  public Long getSize() {
    return size;
  }

  /**
   * Returns the blob's data content type.
   *
   * @see <a href="https://tools.ietf.org/html/rfc2616#section-14.17">Content-Type</a>
   */
  public String getContentType() {
    return Data.isNull(contentType) ? null : contentType;
  }

  /**
   * Returns the blob's data content encoding.
   *
   * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.2.2">Content-Encoding</a>
   */
  public String getContentEncoding() {
    return Data.isNull(contentEncoding) ? null : contentEncoding;
  }

  /**
   * Returns the blob's data content disposition.
   *
   * @see <a href="https://tools.ietf.org/html/rfc6266">Content-Disposition</a>
   */
  public String getContentDisposition() {
    return Data.isNull(contentDisposition) ? null : contentDisposition;
  }

  /**
   * Returns the blob's data content language.
   *
   * @see <a href="http://tools.ietf.org/html/bcp47">Content-Language</a>
   */
  public String getContentLanguage() {
    return Data.isNull(contentLanguage) ? null : contentLanguage;
  }

  /**
   * Returns the number of components that make up this blob. Components are accumulated through the
   * {@link Storage#compose(Storage.ComposeRequest)} operation and are limited to a count of 1024,
   * counting 1 for each non-composite component blob and componentCount for each composite
   * component blob. This value is set only for composite blobs.
   *
   * @see <a href="https://cloud.google.com/storage/docs/composite-objects#_Count">Component Count
   *     Property</a>
   */
  public Integer getComponentCount() {
    return componentCount;
  }

  /**
   * Returns HTTP 1.1 Entity tag for the blob.
   *
   * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.11">Entity Tags</a>
   */
  public String getEtag() {
    return etag;
  }

  /** Returns the URI of this blob as a string. */
  public String getSelfLink() {
    return selfLink;
  }

  /**
   * Returns the MD5 hash of blob's data encoded in base64.
   *
   * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
   *     Best Practices</a>
   */
  public String getMd5() {
    return Data.isNull(md5) ? null : md5;
  }

  /**
   * Returns the MD5 hash of blob's data decoded to string.
   *
   * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
   *     Best Practices</a>
   */
  public String getMd5ToHexString() {
    if (md5 == null) {
      return null;
    }
    byte[] decodedMd5 = BaseEncoding.base64().decode(md5);
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : decodedMd5) {
      stringBuilder.append(String.format("%02x", b & 0xff));
    }
    return stringBuilder.toString();
  }

  /**
   * Returns the CRC32C checksum of blob's data as described in <a
   * href="http://tools.ietf.org/html/rfc4960#appendix-B">RFC 4960, Appendix B;</a> encoded in
   * base64 in big-endian order.
   *
   * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
   *     Best Practices</a>
   */
  public String getCrc32c() {
    return Data.isNull(crc32c) ? null : crc32c;
  }

  /**
   * Returns the CRC32C checksum of blob's data as described in <a
   * href="http://tools.ietf.org/html/rfc4960#appendix-B">RFC 4960, Appendix B;</a> decoded to
   * string.
   *
   * @see <a href="https://cloud.google.com/storage/docs/hashes-etags#_JSONAPI">Hashes and ETags:
   *     Best Practices</a>
   */
  public String getCrc32cToHexString() {
    if (crc32c == null) {
      return null;
    }
    byte[] decodeCrc32c = BaseEncoding.base64().decode(crc32c);
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : decodeCrc32c) {
      stringBuilder.append(String.format("%02x", b & 0xff));
    }
    return stringBuilder.toString();
  }

  /** Returns the blob's media download link. */
  public String getMediaLink() {
    return mediaLink;
  }

  /** Returns blob's user provided metadata. */
  public Map<String, String> getMetadata() {
    return metadata == null || Data.isNull(metadata) ? null : Collections.unmodifiableMap(metadata);
  }

  /** Returns blob's data generation. Used for blob versioning. */
  public Long getGeneration() {
    return getBlobId().getGeneration();
  }

  /**
   * Returns blob's metageneration. Used for preconditions and for detecting changes in metadata. A
   * metageneration number is only meaningful in the context of a particular generation of a
   * particular blob.
   */
  public Long getMetageneration() {
    return metageneration;
  }

  /**
   * Returns the deletion time of the blob expressed as the number of milliseconds since the Unix
   * epoch.
   *
   * @deprecated Use {@link #getDeleteTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getDeleteTime() {
    return millisOffsetDateTimeCodec.decode(deleteTime);
  }

  /** Returns the deletion time of the blob. */
  public OffsetDateTime getDeleteTimeOffsetDateTime() {
    return deleteTime;
  }

  /**
   * Returns the last modification time of the blob's metadata expressed as the number of
   * milliseconds since the Unix epoch.
   *
   * @deprecated Use {@link #getUpdateTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getUpdateTime() {
    return millisOffsetDateTimeCodec.decode(updateTime);
  }

  /** Returns the last modification time of the blob's metadata. */
  public OffsetDateTime getUpdateTimeOffsetDateTime() {
    return updateTime;
  }

  /**
   * Returns the creation time of the blob expressed as the number of milliseconds since the Unix
   * epoch.
   *
   * @deprecated Use {@link #getCreateTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getCreateTime() {
    return millisOffsetDateTimeCodec.decode(createTime);
  }

  /** Returns the creation time of the blob. */
  public OffsetDateTime getCreateTimeOffsetDateTime() {
    return createTime;
  }

  /**
   * Returns the custom time specified by the user for an object.
   *
   * @deprecated Use {@link #getCustomTimeOffsetDateTime()}
   */
  @Deprecated
  public Long getCustomTime() {
    return millisOffsetDateTimeCodec.decode(customTime);
  }

  /** Returns the custom time specified by the user for an object. */
  public OffsetDateTime getCustomTimeOffsetDateTime() {
    return customTime;
  }

  /**
   * Returns {@code true} if the current blob represents a directory. This can only happen if the
   * blob is returned by {@link Storage#list(String, Storage.BlobListOption...)} when the {@link
   * Storage.BlobListOption#currentDirectory()} option is used. When this is the case only {@link
   * #getBlobId()} and {@link #getSize()} are set for the current blob: {@link BlobId#getName()}
   * ends with the '/' character, {@link BlobId#getGeneration()} returns {@code null} and {@link
   * #getSize()} is {@code 0}.
   */
  public boolean isDirectory() {
    return isDirectory;
  }

  /**
   * Returns information on the customer-supplied encryption key, if the blob is encrypted using
   * such a key.
   */
  public CustomerEncryption getCustomerEncryption() {
    return customerEncryption;
  }

  /** Returns the storage class of the blob. */
  public StorageClass getStorageClass() {
    return storageClass;
  }

  /**
   * Returns the time that the object's storage class was last changed or the time of the object
   * creation.
   *
   * @deprecated Use {@link #getTimeStorageClassUpdatedOffsetDateTime()}
   */
  @Deprecated
  public Long getTimeStorageClassUpdated() {
    return millisOffsetDateTimeCodec.decode(timeStorageClassUpdated);
  }

  /**
   * Returns the time that the object's storage class was last changed or the time of the object
   * creation.
   */
  public OffsetDateTime getTimeStorageClassUpdatedOffsetDateTime() {
    return timeStorageClassUpdated;
  }

  /** Returns the Cloud KMS key used to encrypt the blob, if any. */
  public String getKmsKeyName() {
    return kmsKeyName;
  }

  /**
   * Returns a {@code Boolean} with either {@code true}, {@code null} and in certain cases {@code
   * false}.
   *
   * <p>Case 1: {@code true} the field {@link
   * com.google.cloud.storage.Storage.BlobField#EVENT_BASED_HOLD} is selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)} and event-based hold for the blob is enabled.
   *
   * <p>Case 2.1: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BlobField#EVENT_BASED_HOLD} is selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)}, but event-based hold for the blob is not
   * enabled. This case can be considered implicitly {@code false}.
   *
   * <p>Case 2.2: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BlobField#EVENT_BASED_HOLD} is not selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)}, and the state for this field is unknown.
   *
   * <p>Case 3: {@code false} event-based hold is explicitly set to false using in a {@link
   * Builder#setEventBasedHold(Boolean)} client side for a follow-up request e.g. {@link
   * Storage#update(BlobInfo, Storage.BlobTargetOption...)} in which case the value of event-based
   * hold will remain {@code false} for the given instance.
   */
  @BetaApi
  public Boolean getEventBasedHold() {
    return Data.isNull(eventBasedHold) ? null : eventBasedHold;
  }

  /**
   * Returns a {@code Boolean} with either {@code true}, {@code null} and in certain cases {@code
   * false}.
   *
   * <p>Case 1: {@code true} the field {@link
   * com.google.cloud.storage.Storage.BlobField#TEMPORARY_HOLD} is selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)} and temporary hold for the blob is enabled.
   *
   * <p>Case 2.1: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BlobField#TEMPORARY_HOLD} is selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)}, but temporary hold for the blob is not enabled.
   * This case can be considered implicitly {@code false}.
   *
   * <p>Case 2.2: {@code null} the field {@link
   * com.google.cloud.storage.Storage.BlobField#TEMPORARY_HOLD} is not selected in a {@link
   * Storage#get(BlobId, Storage.BlobGetOption...)}, and the state for this field is unknown.
   *
   * <p>Case 3: {@code false} event-based hold is explicitly set to false using in a {@link
   * Builder#setEventBasedHold(Boolean)} client side for a follow-up request e.g. {@link
   * Storage#update(BlobInfo, Storage.BlobTargetOption...)} in which case the value of temporary
   * hold will remain {@code false} for the given instance.
   */
  @BetaApi
  public Boolean getTemporaryHold() {
    return Data.isNull(temporaryHold) ? null : temporaryHold;
  }

  /**
   * Returns the retention expiration time of the blob as {@code Long}, if a retention period is
   * defined. If retention period is not defined this value returns {@code null}
   *
   * @deprecated Use {@link #getRetentionExpirationTimeOffsetDateTime()}
   */
  @BetaApi
  @Deprecated
  public Long getRetentionExpirationTime() {
    return Data.isNull(retentionExpirationTime)
        ? null
        : millisOffsetDateTimeCodec.decode(retentionExpirationTime);
  }

  /**
   * Returns the retention expiration time of the blob, if a retention period is defined. If
   * retention period is not defined this value returns {@code null}
   */
  @BetaApi
  public OffsetDateTime getRetentionExpirationTimeOffsetDateTime() {
    return retentionExpirationTime;
  }

  /** Returns a builder for the current blob. */
  public Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("bucket", getBucket())
        .add("name", getName())
        .add("generation", getGeneration())
        .add("size", getSize())
        .add("content-type", getContentType())
        .add("metadata", getMetadata())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(blobId);
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this
        || obj != null
            && obj.getClass().equals(BlobInfo.class)
            && Objects.equals(
                Conversions.apiary().blobInfo().encode(this),
                Conversions.apiary()
                    .blobInfo()
                    .encode(((BlobInfo) obj))); // TODO: remove this excessive allocation
  }

  /**
   * Attach this instance to an instance of {@link Storage} thereby allowing RPCs to be performed
   * using the methods from the resulting {@link Blob}
   */
  Blob asBlob(Storage storage) {
    return new Blob(storage, new BuilderImpl(this));
  }

  /** Returns a {@code BlobInfo} builder where blob identity is set using the provided values. */
  public static Builder newBuilder(BucketInfo bucketInfo, String name) {
    return newBuilder(bucketInfo.getName(), name);
  }

  /** Returns a {@code BlobInfo} builder where blob identity is set using the provided values. */
  public static Builder newBuilder(String bucket, String name) {
    return newBuilder(BlobId.of(bucket, name));
  }

  /** Returns a {@code BlobInfo} builder where blob identity is set using the provided values. */
  public static Builder newBuilder(BucketInfo bucketInfo, String name, Long generation) {
    return newBuilder(bucketInfo.getName(), name, generation);
  }

  /** Returns a {@code BlobInfo} builder where blob identity is set using the provided values. */
  public static Builder newBuilder(String bucket, String name, Long generation) {
    return newBuilder(BlobId.of(bucket, name, generation));
  }

  /** Returns a {@code BlobInfo} builder where blob identity is set using the provided value. */
  public static Builder newBuilder(BlobId blobId) {
    return new BuilderImpl(blobId);
  }
}
