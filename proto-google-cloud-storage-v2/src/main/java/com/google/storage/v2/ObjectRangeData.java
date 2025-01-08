/*
 * Copyright 2025 Google LLC
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

// Protobuf Java Version: 3.25.5
package com.google.storage.v2;

/**
 *
 *
 * <pre>
 * Contains data and metadata for a range of an object.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.ObjectRangeData}
 */
public final class ObjectRangeData extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.ObjectRangeData)
    ObjectRangeDataOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ObjectRangeData.newBuilder() to construct.
  private ObjectRangeData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ObjectRangeData() {}

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ObjectRangeData();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ObjectRangeData_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ObjectRangeData_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.ObjectRangeData.class,
            com.google.storage.v2.ObjectRangeData.Builder.class);
  }

  private int bitField0_;
  public static final int CHECKSUMMED_DATA_FIELD_NUMBER = 1;
  private com.google.storage.v2.ChecksummedData checksummedData_;
  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   *
   * @return Whether the checksummedData field is set.
   */
  @java.lang.Override
  public boolean hasChecksummedData() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   *
   * @return The checksummedData.
   */
  @java.lang.Override
  public com.google.storage.v2.ChecksummedData getChecksummedData() {
    return checksummedData_ == null
        ? com.google.storage.v2.ChecksummedData.getDefaultInstance()
        : checksummedData_;
  }
  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.ChecksummedDataOrBuilder getChecksummedDataOrBuilder() {
    return checksummedData_ == null
        ? com.google.storage.v2.ChecksummedData.getDefaultInstance()
        : checksummedData_;
  }

  public static final int READ_RANGE_FIELD_NUMBER = 2;
  private com.google.storage.v2.ReadRange readRange_;
  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   *
   * @return Whether the readRange field is set.
   */
  @java.lang.Override
  public boolean hasReadRange() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   *
   * @return The readRange.
   */
  @java.lang.Override
  public com.google.storage.v2.ReadRange getReadRange() {
    return readRange_ == null ? com.google.storage.v2.ReadRange.getDefaultInstance() : readRange_;
  }
  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.ReadRangeOrBuilder getReadRangeOrBuilder() {
    return readRange_ == null ? com.google.storage.v2.ReadRange.getDefaultInstance() : readRange_;
  }

  public static final int RANGE_END_FIELD_NUMBER = 3;
  private boolean rangeEnd_ = false;
  /**
   *
   *
   * <pre>
   * If set, indicates there are no more bytes to read for the given ReadRange.
   * </pre>
   *
   * <code>bool range_end = 3;</code>
   *
   * @return The rangeEnd.
   */
  @java.lang.Override
  public boolean getRangeEnd() {
    return rangeEnd_;
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
      output.writeMessage(1, getChecksummedData());
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeMessage(2, getReadRange());
    }
    if (rangeEnd_ != false) {
      output.writeBool(3, rangeEnd_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getChecksummedData());
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, getReadRange());
    }
    if (rangeEnd_ != false) {
      size += com.google.protobuf.CodedOutputStream.computeBoolSize(3, rangeEnd_);
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
    if (!(obj instanceof com.google.storage.v2.ObjectRangeData)) {
      return super.equals(obj);
    }
    com.google.storage.v2.ObjectRangeData other = (com.google.storage.v2.ObjectRangeData) obj;

    if (hasChecksummedData() != other.hasChecksummedData()) return false;
    if (hasChecksummedData()) {
      if (!getChecksummedData().equals(other.getChecksummedData())) return false;
    }
    if (hasReadRange() != other.hasReadRange()) return false;
    if (hasReadRange()) {
      if (!getReadRange().equals(other.getReadRange())) return false;
    }
    if (getRangeEnd() != other.getRangeEnd()) return false;
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
    if (hasChecksummedData()) {
      hash = (37 * hash) + CHECKSUMMED_DATA_FIELD_NUMBER;
      hash = (53 * hash) + getChecksummedData().hashCode();
    }
    if (hasReadRange()) {
      hash = (37 * hash) + READ_RANGE_FIELD_NUMBER;
      hash = (53 * hash) + getReadRange().hashCode();
    }
    hash = (37 * hash) + RANGE_END_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(getRangeEnd());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectRangeData parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectRangeData parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ObjectRangeData parseFrom(
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

  public static Builder newBuilder(com.google.storage.v2.ObjectRangeData prototype) {
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
   * Contains data and metadata for a range of an object.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.ObjectRangeData}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.ObjectRangeData)
      com.google.storage.v2.ObjectRangeDataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectRangeData_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectRangeData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.ObjectRangeData.class,
              com.google.storage.v2.ObjectRangeData.Builder.class);
    }

    // Construct using com.google.storage.v2.ObjectRangeData.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        getChecksummedDataFieldBuilder();
        getReadRangeFieldBuilder();
      }
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      checksummedData_ = null;
      if (checksummedDataBuilder_ != null) {
        checksummedDataBuilder_.dispose();
        checksummedDataBuilder_ = null;
      }
      readRange_ = null;
      if (readRangeBuilder_ != null) {
        readRangeBuilder_.dispose();
        readRangeBuilder_ = null;
      }
      rangeEnd_ = false;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ObjectRangeData_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectRangeData getDefaultInstanceForType() {
      return com.google.storage.v2.ObjectRangeData.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectRangeData build() {
      com.google.storage.v2.ObjectRangeData result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.ObjectRangeData buildPartial() {
      com.google.storage.v2.ObjectRangeData result =
          new com.google.storage.v2.ObjectRangeData(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.storage.v2.ObjectRangeData result) {
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.checksummedData_ =
            checksummedDataBuilder_ == null ? checksummedData_ : checksummedDataBuilder_.build();
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.readRange_ = readRangeBuilder_ == null ? readRange_ : readRangeBuilder_.build();
        to_bitField0_ |= 0x00000002;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.rangeEnd_ = rangeEnd_;
      }
      result.bitField0_ |= to_bitField0_;
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
      if (other instanceof com.google.storage.v2.ObjectRangeData) {
        return mergeFrom((com.google.storage.v2.ObjectRangeData) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.ObjectRangeData other) {
      if (other == com.google.storage.v2.ObjectRangeData.getDefaultInstance()) return this;
      if (other.hasChecksummedData()) {
        mergeChecksummedData(other.getChecksummedData());
      }
      if (other.hasReadRange()) {
        mergeReadRange(other.getReadRange());
      }
      if (other.getRangeEnd() != false) {
        setRangeEnd(other.getRangeEnd());
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
            case 10:
              {
                input.readMessage(getChecksummedDataFieldBuilder().getBuilder(), extensionRegistry);
                bitField0_ |= 0x00000001;
                break;
              } // case 10
            case 18:
              {
                input.readMessage(getReadRangeFieldBuilder().getBuilder(), extensionRegistry);
                bitField0_ |= 0x00000002;
                break;
              } // case 18
            case 24:
              {
                rangeEnd_ = input.readBool();
                bitField0_ |= 0x00000004;
                break;
              } // case 24
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

    private com.google.storage.v2.ChecksummedData checksummedData_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.ChecksummedData,
            com.google.storage.v2.ChecksummedData.Builder,
            com.google.storage.v2.ChecksummedDataOrBuilder>
        checksummedDataBuilder_;
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     *
     * @return Whether the checksummedData field is set.
     */
    public boolean hasChecksummedData() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     *
     * @return The checksummedData.
     */
    public com.google.storage.v2.ChecksummedData getChecksummedData() {
      if (checksummedDataBuilder_ == null) {
        return checksummedData_ == null
            ? com.google.storage.v2.ChecksummedData.getDefaultInstance()
            : checksummedData_;
      } else {
        return checksummedDataBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public Builder setChecksummedData(com.google.storage.v2.ChecksummedData value) {
      if (checksummedDataBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        checksummedData_ = value;
      } else {
        checksummedDataBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public Builder setChecksummedData(
        com.google.storage.v2.ChecksummedData.Builder builderForValue) {
      if (checksummedDataBuilder_ == null) {
        checksummedData_ = builderForValue.build();
      } else {
        checksummedDataBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public Builder mergeChecksummedData(com.google.storage.v2.ChecksummedData value) {
      if (checksummedDataBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)
            && checksummedData_ != null
            && checksummedData_ != com.google.storage.v2.ChecksummedData.getDefaultInstance()) {
          getChecksummedDataBuilder().mergeFrom(value);
        } else {
          checksummedData_ = value;
        }
      } else {
        checksummedDataBuilder_.mergeFrom(value);
      }
      if (checksummedData_ != null) {
        bitField0_ |= 0x00000001;
        onChanged();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public Builder clearChecksummedData() {
      bitField0_ = (bitField0_ & ~0x00000001);
      checksummedData_ = null;
      if (checksummedDataBuilder_ != null) {
        checksummedDataBuilder_.dispose();
        checksummedDataBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public com.google.storage.v2.ChecksummedData.Builder getChecksummedDataBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getChecksummedDataFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    public com.google.storage.v2.ChecksummedDataOrBuilder getChecksummedDataOrBuilder() {
      if (checksummedDataBuilder_ != null) {
        return checksummedDataBuilder_.getMessageOrBuilder();
      } else {
        return checksummedData_ == null
            ? com.google.storage.v2.ChecksummedData.getDefaultInstance()
            : checksummedData_;
      }
    }
    /**
     *
     *
     * <pre>
     * A portion of the data for the object.
     * </pre>
     *
     * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.ChecksummedData,
            com.google.storage.v2.ChecksummedData.Builder,
            com.google.storage.v2.ChecksummedDataOrBuilder>
        getChecksummedDataFieldBuilder() {
      if (checksummedDataBuilder_ == null) {
        checksummedDataBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.storage.v2.ChecksummedData,
                com.google.storage.v2.ChecksummedData.Builder,
                com.google.storage.v2.ChecksummedDataOrBuilder>(
                getChecksummedData(), getParentForChildren(), isClean());
        checksummedData_ = null;
      }
      return checksummedDataBuilder_;
    }

    private com.google.storage.v2.ReadRange readRange_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.ReadRange,
            com.google.storage.v2.ReadRange.Builder,
            com.google.storage.v2.ReadRangeOrBuilder>
        readRangeBuilder_;
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     *
     * @return Whether the readRange field is set.
     */
    public boolean hasReadRange() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     *
     * @return The readRange.
     */
    public com.google.storage.v2.ReadRange getReadRange() {
      if (readRangeBuilder_ == null) {
        return readRange_ == null
            ? com.google.storage.v2.ReadRange.getDefaultInstance()
            : readRange_;
      } else {
        return readRangeBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public Builder setReadRange(com.google.storage.v2.ReadRange value) {
      if (readRangeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        readRange_ = value;
      } else {
        readRangeBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public Builder setReadRange(com.google.storage.v2.ReadRange.Builder builderForValue) {
      if (readRangeBuilder_ == null) {
        readRange_ = builderForValue.build();
      } else {
        readRangeBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public Builder mergeReadRange(com.google.storage.v2.ReadRange value) {
      if (readRangeBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0)
            && readRange_ != null
            && readRange_ != com.google.storage.v2.ReadRange.getDefaultInstance()) {
          getReadRangeBuilder().mergeFrom(value);
        } else {
          readRange_ = value;
        }
      } else {
        readRangeBuilder_.mergeFrom(value);
      }
      if (readRange_ != null) {
        bitField0_ |= 0x00000002;
        onChanged();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public Builder clearReadRange() {
      bitField0_ = (bitField0_ & ~0x00000002);
      readRange_ = null;
      if (readRangeBuilder_ != null) {
        readRangeBuilder_.dispose();
        readRangeBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public com.google.storage.v2.ReadRange.Builder getReadRangeBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getReadRangeFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    public com.google.storage.v2.ReadRangeOrBuilder getReadRangeOrBuilder() {
      if (readRangeBuilder_ != null) {
        return readRangeBuilder_.getMessageOrBuilder();
      } else {
        return readRange_ == null
            ? com.google.storage.v2.ReadRange.getDefaultInstance()
            : readRange_;
      }
    }
    /**
     *
     *
     * <pre>
     * The ReadRange describes the content being returned with read_id set to the
     * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
     * messages may have the same read_id but increasing offsets.
     * ReadObjectResponse messages with the same read_id are guaranteed to be
     * delivered in increasing offset order.
     * </pre>
     *
     * <code>.google.storage.v2.ReadRange read_range = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.ReadRange,
            com.google.storage.v2.ReadRange.Builder,
            com.google.storage.v2.ReadRangeOrBuilder>
        getReadRangeFieldBuilder() {
      if (readRangeBuilder_ == null) {
        readRangeBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.storage.v2.ReadRange,
                com.google.storage.v2.ReadRange.Builder,
                com.google.storage.v2.ReadRangeOrBuilder>(
                getReadRange(), getParentForChildren(), isClean());
        readRange_ = null;
      }
      return readRangeBuilder_;
    }

    private boolean rangeEnd_;
    /**
     *
     *
     * <pre>
     * If set, indicates there are no more bytes to read for the given ReadRange.
     * </pre>
     *
     * <code>bool range_end = 3;</code>
     *
     * @return The rangeEnd.
     */
    @java.lang.Override
    public boolean getRangeEnd() {
      return rangeEnd_;
    }
    /**
     *
     *
     * <pre>
     * If set, indicates there are no more bytes to read for the given ReadRange.
     * </pre>
     *
     * <code>bool range_end = 3;</code>
     *
     * @param value The rangeEnd to set.
     * @return This builder for chaining.
     */
    public Builder setRangeEnd(boolean value) {

      rangeEnd_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * If set, indicates there are no more bytes to read for the given ReadRange.
     * </pre>
     *
     * <code>bool range_end = 3;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearRangeEnd() {
      bitField0_ = (bitField0_ & ~0x00000004);
      rangeEnd_ = false;
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

    // @@protoc_insertion_point(builder_scope:google.storage.v2.ObjectRangeData)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.ObjectRangeData)
  private static final com.google.storage.v2.ObjectRangeData DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.ObjectRangeData();
  }

  public static com.google.storage.v2.ObjectRangeData getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ObjectRangeData> PARSER =
      new com.google.protobuf.AbstractParser<ObjectRangeData>() {
        @java.lang.Override
        public ObjectRangeData parsePartialFrom(
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

  public static com.google.protobuf.Parser<ObjectRangeData> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ObjectRangeData> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.ObjectRangeData getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
