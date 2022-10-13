/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

/**
 *
 *
 * <pre>
 * Message used for storing full (not subrange) object checksums.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.ObjectChecksums}
 */
public final class ObjectChecksums extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.ObjectChecksums)
    ObjectChecksumsOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ObjectChecksums.newBuilder() to construct.
  private ObjectChecksums(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ObjectChecksums() {
    md5Hash_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ObjectChecksums();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ObjectChecksums_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ObjectChecksums_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.ObjectChecksums.class,
            com.google.storage.v2.ObjectChecksums.Builder.class);
  }

  private int bitField0_;
  public static final int CRC32C_FIELD_NUMBER = 1;
  private int crc32C_;
  /**
   *
   *
   * <pre>
   * CRC32C digest of the object data. Computed by the Cloud Storage service for
   * all written objects.
   * If set in an WriteObjectRequest, service will validate that the stored
   * object matches this checksum.
   * </pre>
   *
   * <code>optional fixed32 crc32c = 1;</code>
   *
   * @return Whether the crc32c field is set.
   */
  @java.lang.Override
  public boolean hasCrc32C() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   *
   *
   * <pre>
   * CRC32C digest of the object data. Computed by the Cloud Storage service for
   * all written objects.
   * If set in an WriteObjectRequest, service will validate that the stored
   * object matches this checksum.
   * </pre>
   *
   * <code>optional fixed32 crc32c = 1;</code>
   *
   * @return The crc32c.
   */
  @java.lang.Override
  public int getCrc32C() {
    return crc32C_;
  }

  public static final int MD5_HASH_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString md5Hash_;
  /**
   *
   *
   * <pre>
   * 128 bit MD5 hash of the object data.
   * For more information about using the MD5 hash, see
   * [https://cloud.google.com/storage/docs/hashes-etags#json-api][Hashes and
   * ETags: Best Practices].
   * Not all objects will provide an MD5 hash. For example, composite objects
   * provide only crc32c hashes.
   * This value is equivalent to running `cat object.txt | openssl md5 -binary`
   * </pre>
   *
   * <code>bytes md5_hash = 2;</code>
   *
   * @return The md5Hash.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getMd5Hash() {
    return md5Hash_;
  }

  private byte memoizedIsInitialized = -1;

  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeFixed32(1, crc32C_);
    }
    if (!md5Hash_.isEmpty()) {
      output.writeBytes(2, md5Hash_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeFixed32Size(1, crc32C_);
    }
    if (!md5Hash_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, md5Hash_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof com.google.storage.v2.ObjectChecksums)) {
      return super.equals(obj);
    }
    com.google.storage.v2.ObjectChecksums other = (com.google.storage.v2.ObjectChecksums) obj;

    if (hasCrc32C() != other.hasCrc32C()) return false;
    if (hasCrc32C()) {
      if (getCrc32C() != other.getCrc32C()) return false;
    }
    if (!getMd5Hash().equals(other.getMd5Hash())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasCrc32C()) {
      hash = (37 * hash) + CRC32C_FIELD_NUMBER;
      hash = (53 * hash) + getCrc32C();
    }
    hash = (37 * hash) + MD5_HASH_FIELD_NUMBER;
    hash = (53 * hash) + getMd5Hash().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectChecksums parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectChecksums parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectChecksums parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }

  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }

  public static Builder newBuilder(com.google.storage.v2.ObjectChecksums prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }

  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   *
   *
   * <pre>
   * Message used for storing full (not subrange) object checksums.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.ObjectChecksums}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.ObjectChecksums)
      com.google.storage.v2.ObjectChecksumsOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectChecksums_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectChecksums_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.ObjectChecksums.class,
              com.google.storage.v2.ObjectChecksums.Builder.class);
    }

    // Construct using com.google.storage.v2.ObjectChecksums.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      crc32C_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      md5Hash_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectChecksums_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectChecksums getDefaultInstanceForType() {
      return com.google.storage.v2.ObjectChecksums.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectChecksums build() {
      com.google.storage.v2.ObjectChecksums result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectChecksums buildPartial() {
      com.google.storage.v2.ObjectChecksums result =
          new com.google.storage.v2.ObjectChecksums(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.crc32C_ = crc32C_;
        to_bitField0_ |= 0x00000001;
      }
      result.md5Hash_ = md5Hash_;
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }

    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.setField(field, value);
    }

    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }

    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }

    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }

    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.storage.v2.ObjectChecksums) {
        return mergeFrom((com.google.storage.v2.ObjectChecksums) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.ObjectChecksums other) {
      if (other == com.google.storage.v2.ObjectChecksums.getDefaultInstance()) return this;
      if (other.hasCrc32C()) {
        setCrc32C(other.getCrc32C());
      }
      if (other.getMd5Hash() != com.google.protobuf.ByteString.EMPTY) {
        setMd5Hash(other.getMd5Hash());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 13:
              {
                crc32C_ = input.readFixed32();
                bitField0_ |= 0x00000001;
                break;
              } // case 13
            case 18:
              {
                md5Hash_ = input.readBytes();

                break;
              } // case 18
            default:
              {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }

    private int bitField0_;

    private int crc32C_;
    /**
     *
     *
     * <pre>
     * CRC32C digest of the object data. Computed by the Cloud Storage service for
     * all written objects.
     * If set in an WriteObjectRequest, service will validate that the stored
     * object matches this checksum.
     * </pre>
     *
     * <code>optional fixed32 crc32c = 1;</code>
     *
     * @return Whether the crc32c field is set.
     */
    @java.lang.Override
    public boolean hasCrc32C() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     *
     *
     * <pre>
     * CRC32C digest of the object data. Computed by the Cloud Storage service for
     * all written objects.
     * If set in an WriteObjectRequest, service will validate that the stored
     * object matches this checksum.
     * </pre>
     *
     * <code>optional fixed32 crc32c = 1;</code>
     *
     * @return The crc32c.
     */
    @java.lang.Override
    public int getCrc32C() {
      return crc32C_;
    }
    /**
     *
     *
     * <pre>
     * CRC32C digest of the object data. Computed by the Cloud Storage service for
     * all written objects.
     * If set in an WriteObjectRequest, service will validate that the stored
     * object matches this checksum.
     * </pre>
     *
     * <code>optional fixed32 crc32c = 1;</code>
     *
     * @param value The crc32c to set.
     * @return This builder for chaining.
     */
    public Builder setCrc32C(int value) {
      bitField0_ |= 0x00000001;
      crc32C_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * CRC32C digest of the object data. Computed by the Cloud Storage service for
     * all written objects.
     * If set in an WriteObjectRequest, service will validate that the stored
     * object matches this checksum.
     * </pre>
     *
     * <code>optional fixed32 crc32c = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearCrc32C() {
      bitField0_ = (bitField0_ & ~0x00000001);
      crc32C_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString md5Hash_ = com.google.protobuf.ByteString.EMPTY;
    /**
     *
     *
     * <pre>
     * 128 bit MD5 hash of the object data.
     * For more information about using the MD5 hash, see
     * [https://cloud.google.com/storage/docs/hashes-etags#json-api][Hashes and
     * ETags: Best Practices].
     * Not all objects will provide an MD5 hash. For example, composite objects
     * provide only crc32c hashes.
     * This value is equivalent to running `cat object.txt | openssl md5 -binary`
     * </pre>
     *
     * <code>bytes md5_hash = 2;</code>
     *
     * @return The md5Hash.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getMd5Hash() {
      return md5Hash_;
    }
    /**
     *
     *
     * <pre>
     * 128 bit MD5 hash of the object data.
     * For more information about using the MD5 hash, see
     * [https://cloud.google.com/storage/docs/hashes-etags#json-api][Hashes and
     * ETags: Best Practices].
     * Not all objects will provide an MD5 hash. For example, composite objects
     * provide only crc32c hashes.
     * This value is equivalent to running `cat object.txt | openssl md5 -binary`
     * </pre>
     *
     * <code>bytes md5_hash = 2;</code>
     *
     * @param value The md5Hash to set.
     * @return This builder for chaining.
     */
    public Builder setMd5Hash(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }

      md5Hash_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * 128 bit MD5 hash of the object data.
     * For more information about using the MD5 hash, see
     * [https://cloud.google.com/storage/docs/hashes-etags#json-api][Hashes and
     * ETags: Best Practices].
     * Not all objects will provide an MD5 hash. For example, composite objects
     * provide only crc32c hashes.
     * This value is equivalent to running `cat object.txt | openssl md5 -binary`
     * </pre>
     *
     * <code>bytes md5_hash = 2;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearMd5Hash() {

      md5Hash_ = getDefaultInstance().getMd5Hash();
      onChanged();
      return this;
    }

    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:google.storage.v2.ObjectChecksums)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.ObjectChecksums)
  private static final com.google.storage.v2.ObjectChecksums DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.ObjectChecksums();
  }

  public static com.google.storage.v2.ObjectChecksums getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ObjectChecksums> PARSER =
      new com.google.protobuf.AbstractParser<ObjectChecksums>() {
        @java.lang.Override
        public ObjectChecksums parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          Builder builder = newBuilder();
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (com.google.protobuf.UninitializedMessageException e) {
            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
          } catch (java.io.IOException e) {
            throw new com.google.protobuf.InvalidProtocolBufferException(e)
                .setUnfinishedMessage(builder.buildPartial());
          }
          return builder.buildPartial();
        }
      };

  public static com.google.protobuf.Parser<ObjectChecksums> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ObjectChecksums> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.ObjectChecksums getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
