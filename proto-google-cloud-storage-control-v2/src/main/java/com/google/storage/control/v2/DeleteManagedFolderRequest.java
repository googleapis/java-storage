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
// source: google/storage/control/v2/storage_control.proto

// Protobuf Java Version: 3.25.5
package com.google.storage.control.v2;

/**
 *
 *
 * <pre>
 * DeleteManagedFolder RPC request message.
 * </pre>
 *
 * Protobuf type {@code google.storage.control.v2.DeleteManagedFolderRequest}
 */
public final class DeleteManagedFolderRequest extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.control.v2.DeleteManagedFolderRequest)
    DeleteManagedFolderRequestOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use DeleteManagedFolderRequest.newBuilder() to construct.
  private DeleteManagedFolderRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private DeleteManagedFolderRequest() {
    name_ = "";
    requestId_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new DeleteManagedFolderRequest();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.control.v2.StorageControlProto
        .internal_static_google_storage_control_v2_DeleteManagedFolderRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.control.v2.StorageControlProto
        .internal_static_google_storage_control_v2_DeleteManagedFolderRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.control.v2.DeleteManagedFolderRequest.class,
            com.google.storage.control.v2.DeleteManagedFolderRequest.Builder.class);
  }

  private int bitField0_;
  public static final int NAME_FIELD_NUMBER = 7;

  @SuppressWarnings("serial")
  private volatile java.lang.Object name_ = "";
  /**
   *
   *
   * <pre>
   * Required. Name of the managed folder.
   * Format:
   * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * </pre>
   *
   * <code>
   * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The name.
   */
  @java.lang.Override
  public java.lang.String getName() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      name_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * Required. Name of the managed folder.
   * Format:
   * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * </pre>
   *
   * <code>
   * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for name.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getNameBytes() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      name_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int IF_METAGENERATION_MATCH_FIELD_NUMBER = 3;
  private long ifMetagenerationMatch_ = 0L;
  /**
   *
   *
   * <pre>
   * The operation succeeds conditional on the managed folder's current
   * metageneration matching the value here specified.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 3;</code>
   *
   * @return Whether the ifMetagenerationMatch field is set.
   */
  @java.lang.Override
  public boolean hasIfMetagenerationMatch() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   *
   *
   * <pre>
   * The operation succeeds conditional on the managed folder's current
   * metageneration matching the value here specified.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 3;</code>
   *
   * @return The ifMetagenerationMatch.
   */
  @java.lang.Override
  public long getIfMetagenerationMatch() {
    return ifMetagenerationMatch_;
  }

  public static final int IF_METAGENERATION_NOT_MATCH_FIELD_NUMBER = 4;
  private long ifMetagenerationNotMatch_ = 0L;
  /**
   *
   *
   * <pre>
   * The operation succeeds conditional on the managed folder's current
   * metageneration NOT matching the value here specified.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 4;</code>
   *
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  @java.lang.Override
  public boolean hasIfMetagenerationNotMatch() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   *
   *
   * <pre>
   * The operation succeeds conditional on the managed folder's current
   * metageneration NOT matching the value here specified.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 4;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  @java.lang.Override
  public long getIfMetagenerationNotMatch() {
    return ifMetagenerationNotMatch_;
  }

  public static final int ALLOW_NON_EMPTY_FIELD_NUMBER = 5;
  private boolean allowNonEmpty_ = false;
  /**
   *
   *
   * <pre>
   * Allows deletion of a managed folder even if it is not empty.
   * A managed folder is empty if it manages no child managed folders or
   * objects. Caller must have permission for
   * storage.managedFolders.setIamPolicy.
   * </pre>
   *
   * <code>bool allow_non_empty = 5;</code>
   *
   * @return The allowNonEmpty.
   */
  @java.lang.Override
  public boolean getAllowNonEmpty() {
    return allowNonEmpty_;
  }

  public static final int REQUEST_ID_FIELD_NUMBER = 6;

  @SuppressWarnings("serial")
  private volatile java.lang.Object requestId_ = "";
  /**
   *
   *
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted.
   * </pre>
   *
   * <code>
   * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
   * </code>
   *
   * @return The requestId.
   */
  @java.lang.Override
  public java.lang.String getRequestId() {
    java.lang.Object ref = requestId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      requestId_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted.
   * </pre>
   *
   * <code>
   * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
   * </code>
   *
   * @return The bytes for requestId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getRequestIdBytes() {
    java.lang.Object ref = requestId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      requestId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
      output.writeInt64(3, ifMetagenerationMatch_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      output.writeInt64(4, ifMetagenerationNotMatch_);
    }
    if (allowNonEmpty_ != false) {
      output.writeBool(5, allowNonEmpty_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(requestId_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 6, requestId_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 7, name_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(3, ifMetagenerationMatch_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(4, ifMetagenerationNotMatch_);
    }
    if (allowNonEmpty_ != false) {
      size += com.google.protobuf.CodedOutputStream.computeBoolSize(5, allowNonEmpty_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(requestId_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, requestId_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, name_);
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
    if (!(obj instanceof com.google.storage.control.v2.DeleteManagedFolderRequest)) {
      return super.equals(obj);
    }
    com.google.storage.control.v2.DeleteManagedFolderRequest other =
        (com.google.storage.control.v2.DeleteManagedFolderRequest) obj;

    if (!getName().equals(other.getName())) return false;
    if (hasIfMetagenerationMatch() != other.hasIfMetagenerationMatch()) return false;
    if (hasIfMetagenerationMatch()) {
      if (getIfMetagenerationMatch() != other.getIfMetagenerationMatch()) return false;
    }
    if (hasIfMetagenerationNotMatch() != other.hasIfMetagenerationNotMatch()) return false;
    if (hasIfMetagenerationNotMatch()) {
      if (getIfMetagenerationNotMatch() != other.getIfMetagenerationNotMatch()) return false;
    }
    if (getAllowNonEmpty() != other.getAllowNonEmpty()) return false;
    if (!getRequestId().equals(other.getRequestId())) return false;
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
    hash = (37 * hash) + NAME_FIELD_NUMBER;
    hash = (53 * hash) + getName().hashCode();
    if (hasIfMetagenerationMatch()) {
      hash = (37 * hash) + IF_METAGENERATION_MATCH_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getIfMetagenerationMatch());
    }
    if (hasIfMetagenerationNotMatch()) {
      hash = (37 * hash) + IF_METAGENERATION_NOT_MATCH_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getIfMetagenerationNotMatch());
    }
    hash = (37 * hash) + ALLOW_NON_EMPTY_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(getAllowNonEmpty());
    hash = (37 * hash) + REQUEST_ID_FIELD_NUMBER;
    hash = (53 * hash) + getRequestId().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest parseFrom(
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

  public static Builder newBuilder(
      com.google.storage.control.v2.DeleteManagedFolderRequest prototype) {
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
   * DeleteManagedFolder RPC request message.
   * </pre>
   *
   * Protobuf type {@code google.storage.control.v2.DeleteManagedFolderRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.control.v2.DeleteManagedFolderRequest)
      com.google.storage.control.v2.DeleteManagedFolderRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_DeleteManagedFolderRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_DeleteManagedFolderRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.control.v2.DeleteManagedFolderRequest.class,
              com.google.storage.control.v2.DeleteManagedFolderRequest.Builder.class);
    }

    // Construct using com.google.storage.control.v2.DeleteManagedFolderRequest.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      name_ = "";
      ifMetagenerationMatch_ = 0L;
      ifMetagenerationNotMatch_ = 0L;
      allowNonEmpty_ = false;
      requestId_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_DeleteManagedFolderRequest_descriptor;
    }

    @java.lang.Override
    public com.google.storage.control.v2.DeleteManagedFolderRequest getDefaultInstanceForType() {
      return com.google.storage.control.v2.DeleteManagedFolderRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.control.v2.DeleteManagedFolderRequest build() {
      com.google.storage.control.v2.DeleteManagedFolderRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.control.v2.DeleteManagedFolderRequest buildPartial() {
      com.google.storage.control.v2.DeleteManagedFolderRequest result =
          new com.google.storage.control.v2.DeleteManagedFolderRequest(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.storage.control.v2.DeleteManagedFolderRequest result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.name_ = name_;
      }
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.ifMetagenerationMatch_ = ifMetagenerationMatch_;
        to_bitField0_ |= 0x00000001;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.ifMetagenerationNotMatch_ = ifMetagenerationNotMatch_;
        to_bitField0_ |= 0x00000002;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.allowNonEmpty_ = allowNonEmpty_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.requestId_ = requestId_;
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
      if (other instanceof com.google.storage.control.v2.DeleteManagedFolderRequest) {
        return mergeFrom((com.google.storage.control.v2.DeleteManagedFolderRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.control.v2.DeleteManagedFolderRequest other) {
      if (other == com.google.storage.control.v2.DeleteManagedFolderRequest.getDefaultInstance())
        return this;
      if (!other.getName().isEmpty()) {
        name_ = other.name_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (other.hasIfMetagenerationMatch()) {
        setIfMetagenerationMatch(other.getIfMetagenerationMatch());
      }
      if (other.hasIfMetagenerationNotMatch()) {
        setIfMetagenerationNotMatch(other.getIfMetagenerationNotMatch());
      }
      if (other.getAllowNonEmpty() != false) {
        setAllowNonEmpty(other.getAllowNonEmpty());
      }
      if (!other.getRequestId().isEmpty()) {
        requestId_ = other.requestId_;
        bitField0_ |= 0x00000010;
        onChanged();
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
            case 24:
              {
                ifMetagenerationMatch_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 24
            case 32:
              {
                ifMetagenerationNotMatch_ = input.readInt64();
                bitField0_ |= 0x00000004;
                break;
              } // case 32
            case 40:
              {
                allowNonEmpty_ = input.readBool();
                bitField0_ |= 0x00000008;
                break;
              } // case 40
            case 50:
              {
                requestId_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000010;
                break;
              } // case 50
            case 58:
              {
                name_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000001;
                break;
              } // case 58
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

    private java.lang.Object name_ = "";
    /**
     *
     *
     * <pre>
     * Required. Name of the managed folder.
     * Format:
     * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
     * </pre>
     *
     * <code>
     * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return The name.
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Required. Name of the managed folder.
     * Format:
     * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
     * </pre>
     *
     * <code>
     * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return The bytes for name.
     */
    public com.google.protobuf.ByteString getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Required. Name of the managed folder.
     * Format:
     * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
     * </pre>
     *
     * <code>
     * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @param value The name to set.
     * @return This builder for chaining.
     */
    public Builder setName(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      name_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Required. Name of the managed folder.
     * Format:
     * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
     * </pre>
     *
     * <code>
     * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return This builder for chaining.
     */
    public Builder clearName() {
      name_ = getDefaultInstance().getName();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Required. Name of the managed folder.
     * Format:
     * `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
     * </pre>
     *
     * <code>
     * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @param value The bytes for name to set.
     * @return This builder for chaining.
     */
    public Builder setNameBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);
      name_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private long ifMetagenerationMatch_;
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_match = 3;</code>
     *
     * @return Whether the ifMetagenerationMatch field is set.
     */
    @java.lang.Override
    public boolean hasIfMetagenerationMatch() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_match = 3;</code>
     *
     * @return The ifMetagenerationMatch.
     */
    @java.lang.Override
    public long getIfMetagenerationMatch() {
      return ifMetagenerationMatch_;
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_match = 3;</code>
     *
     * @param value The ifMetagenerationMatch to set.
     * @return This builder for chaining.
     */
    public Builder setIfMetagenerationMatch(long value) {

      ifMetagenerationMatch_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_match = 3;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearIfMetagenerationMatch() {
      bitField0_ = (bitField0_ & ~0x00000002);
      ifMetagenerationMatch_ = 0L;
      onChanged();
      return this;
    }

    private long ifMetagenerationNotMatch_;
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration NOT matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_not_match = 4;</code>
     *
     * @return Whether the ifMetagenerationNotMatch field is set.
     */
    @java.lang.Override
    public boolean hasIfMetagenerationNotMatch() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration NOT matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_not_match = 4;</code>
     *
     * @return The ifMetagenerationNotMatch.
     */
    @java.lang.Override
    public long getIfMetagenerationNotMatch() {
      return ifMetagenerationNotMatch_;
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration NOT matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_not_match = 4;</code>
     *
     * @param value The ifMetagenerationNotMatch to set.
     * @return This builder for chaining.
     */
    public Builder setIfMetagenerationNotMatch(long value) {

      ifMetagenerationNotMatch_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The operation succeeds conditional on the managed folder's current
     * metageneration NOT matching the value here specified.
     * </pre>
     *
     * <code>optional int64 if_metageneration_not_match = 4;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearIfMetagenerationNotMatch() {
      bitField0_ = (bitField0_ & ~0x00000004);
      ifMetagenerationNotMatch_ = 0L;
      onChanged();
      return this;
    }

    private boolean allowNonEmpty_;
    /**
     *
     *
     * <pre>
     * Allows deletion of a managed folder even if it is not empty.
     * A managed folder is empty if it manages no child managed folders or
     * objects. Caller must have permission for
     * storage.managedFolders.setIamPolicy.
     * </pre>
     *
     * <code>bool allow_non_empty = 5;</code>
     *
     * @return The allowNonEmpty.
     */
    @java.lang.Override
    public boolean getAllowNonEmpty() {
      return allowNonEmpty_;
    }
    /**
     *
     *
     * <pre>
     * Allows deletion of a managed folder even if it is not empty.
     * A managed folder is empty if it manages no child managed folders or
     * objects. Caller must have permission for
     * storage.managedFolders.setIamPolicy.
     * </pre>
     *
     * <code>bool allow_non_empty = 5;</code>
     *
     * @param value The allowNonEmpty to set.
     * @return This builder for chaining.
     */
    public Builder setAllowNonEmpty(boolean value) {

      allowNonEmpty_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Allows deletion of a managed folder even if it is not empty.
     * A managed folder is empty if it manages no child managed folders or
     * objects. Caller must have permission for
     * storage.managedFolders.setIamPolicy.
     * </pre>
     *
     * <code>bool allow_non_empty = 5;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearAllowNonEmpty() {
      bitField0_ = (bitField0_ & ~0x00000008);
      allowNonEmpty_ = false;
      onChanged();
      return this;
    }

    private java.lang.Object requestId_ = "";
    /**
     *
     *
     * <pre>
     * Optional. A unique identifier for this request. UUID is the recommended
     * format, but other formats are still accepted.
     * </pre>
     *
     * <code>
     * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
     * </code>
     *
     * @return The requestId.
     */
    public java.lang.String getRequestId() {
      java.lang.Object ref = requestId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        requestId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Optional. A unique identifier for this request. UUID is the recommended
     * format, but other formats are still accepted.
     * </pre>
     *
     * <code>
     * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
     * </code>
     *
     * @return The bytes for requestId.
     */
    public com.google.protobuf.ByteString getRequestIdBytes() {
      java.lang.Object ref = requestId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        requestId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Optional. A unique identifier for this request. UUID is the recommended
     * format, but other formats are still accepted.
     * </pre>
     *
     * <code>
     * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
     * </code>
     *
     * @param value The requestId to set.
     * @return This builder for chaining.
     */
    public Builder setRequestId(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      requestId_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. A unique identifier for this request. UUID is the recommended
     * format, but other formats are still accepted.
     * </pre>
     *
     * <code>
     * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
     * </code>
     *
     * @return This builder for chaining.
     */
    public Builder clearRequestId() {
      requestId_ = getDefaultInstance().getRequestId();
      bitField0_ = (bitField0_ & ~0x00000010);
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. A unique identifier for this request. UUID is the recommended
     * format, but other formats are still accepted.
     * </pre>
     *
     * <code>
     * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
     * </code>
     *
     * @param value The bytes for requestId to set.
     * @return This builder for chaining.
     */
    public Builder setRequestIdBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);
      requestId_ = value;
      bitField0_ |= 0x00000010;
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

    // @@protoc_insertion_point(builder_scope:google.storage.control.v2.DeleteManagedFolderRequest)
  }

  // @@protoc_insertion_point(class_scope:google.storage.control.v2.DeleteManagedFolderRequest)
  private static final com.google.storage.control.v2.DeleteManagedFolderRequest DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.control.v2.DeleteManagedFolderRequest();
  }

  public static com.google.storage.control.v2.DeleteManagedFolderRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DeleteManagedFolderRequest> PARSER =
      new com.google.protobuf.AbstractParser<DeleteManagedFolderRequest>() {
        @java.lang.Override
        public DeleteManagedFolderRequest parsePartialFrom(
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

  public static com.google.protobuf.Parser<DeleteManagedFolderRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DeleteManagedFolderRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.control.v2.DeleteManagedFolderRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
