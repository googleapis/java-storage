/*
 * Copyright 2023 Google LLC
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
 * Hmac key list response with next page information.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.ListHmacKeysResponse}
 */
public final class ListHmacKeysResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.ListHmacKeysResponse)
    ListHmacKeysResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ListHmacKeysResponse.newBuilder() to construct.
  private ListHmacKeysResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ListHmacKeysResponse() {
    hmacKeys_ = java.util.Collections.emptyList();
    nextPageToken_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ListHmacKeysResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ListHmacKeysResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_ListHmacKeysResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.ListHmacKeysResponse.class,
            com.google.storage.v2.ListHmacKeysResponse.Builder.class);
  }

  public static final int HMAC_KEYS_FIELD_NUMBER = 1;

  @SuppressWarnings("serial")
  private java.util.List<com.google.storage.v2.HmacKeyMetadata> hmacKeys_;
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
   */
  @java.lang.Override
  public java.util.List<com.google.storage.v2.HmacKeyMetadata> getHmacKeysList() {
    return hmacKeys_;
  }
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.google.storage.v2.HmacKeyMetadataOrBuilder>
      getHmacKeysOrBuilderList() {
    return hmacKeys_;
  }
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
   */
  @java.lang.Override
  public int getHmacKeysCount() {
    return hmacKeys_.size();
  }
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.HmacKeyMetadata getHmacKeys(int index) {
    return hmacKeys_.get(index);
  }
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.HmacKeyMetadataOrBuilder getHmacKeysOrBuilder(int index) {
    return hmacKeys_.get(index);
  }

  public static final int NEXT_PAGE_TOKEN_FIELD_NUMBER = 2;

  @SuppressWarnings("serial")
  private volatile java.lang.Object nextPageToken_ = "";
  /**
   *
   *
   * <pre>
   * The continuation token, used to page through large result sets. Provide
   * this value in a subsequent request to return the next page of results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The nextPageToken.
   */
  @java.lang.Override
  public java.lang.String getNextPageToken() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      nextPageToken_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * The continuation token, used to page through large result sets. Provide
   * this value in a subsequent request to return the next page of results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The bytes for nextPageToken.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getNextPageTokenBytes() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      nextPageToken_ = b;
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
    for (int i = 0; i < hmacKeys_.size(); i++) {
      output.writeMessage(1, hmacKeys_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, nextPageToken_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < hmacKeys_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, hmacKeys_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, nextPageToken_);
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
    if (!(obj instanceof com.google.storage.v2.ListHmacKeysResponse)) {
      return super.equals(obj);
    }
    com.google.storage.v2.ListHmacKeysResponse other =
        (com.google.storage.v2.ListHmacKeysResponse) obj;

    if (!getHmacKeysList().equals(other.getHmacKeysList())) return false;
    if (!getNextPageToken().equals(other.getNextPageToken())) return false;
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
    if (getHmacKeysCount() > 0) {
      hash = (37 * hash) + HMAC_KEYS_FIELD_NUMBER;
      hash = (53 * hash) + getHmacKeysList().hashCode();
    }
    hash = (37 * hash) + NEXT_PAGE_TOKEN_FIELD_NUMBER;
    hash = (53 * hash) + getNextPageToken().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.ListHmacKeysResponse parseFrom(
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

  public static Builder newBuilder(com.google.storage.v2.ListHmacKeysResponse prototype) {
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
   * Hmac key list response with next page information.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.ListHmacKeysResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.ListHmacKeysResponse)
      com.google.storage.v2.ListHmacKeysResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ListHmacKeysResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ListHmacKeysResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.ListHmacKeysResponse.class,
              com.google.storage.v2.ListHmacKeysResponse.Builder.class);
    }

    // Construct using com.google.storage.v2.ListHmacKeysResponse.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (hmacKeysBuilder_ == null) {
        hmacKeys_ = java.util.Collections.emptyList();
      } else {
        hmacKeys_ = null;
        hmacKeysBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      nextPageToken_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_ListHmacKeysResponse_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.ListHmacKeysResponse getDefaultInstanceForType() {
      return com.google.storage.v2.ListHmacKeysResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.ListHmacKeysResponse build() {
      com.google.storage.v2.ListHmacKeysResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.ListHmacKeysResponse buildPartial() {
      com.google.storage.v2.ListHmacKeysResponse result =
          new com.google.storage.v2.ListHmacKeysResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.google.storage.v2.ListHmacKeysResponse result) {
      if (hmacKeysBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          hmacKeys_ = java.util.Collections.unmodifiableList(hmacKeys_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.hmacKeys_ = hmacKeys_;
      } else {
        result.hmacKeys_ = hmacKeysBuilder_.build();
      }
    }

    private void buildPartial0(com.google.storage.v2.ListHmacKeysResponse result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.nextPageToken_ = nextPageToken_;
      }
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
      if (other instanceof com.google.storage.v2.ListHmacKeysResponse) {
        return mergeFrom((com.google.storage.v2.ListHmacKeysResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.ListHmacKeysResponse other) {
      if (other == com.google.storage.v2.ListHmacKeysResponse.getDefaultInstance()) return this;
      if (hmacKeysBuilder_ == null) {
        if (!other.hmacKeys_.isEmpty()) {
          if (hmacKeys_.isEmpty()) {
            hmacKeys_ = other.hmacKeys_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureHmacKeysIsMutable();
            hmacKeys_.addAll(other.hmacKeys_);
          }
          onChanged();
        }
      } else {
        if (!other.hmacKeys_.isEmpty()) {
          if (hmacKeysBuilder_.isEmpty()) {
            hmacKeysBuilder_.dispose();
            hmacKeysBuilder_ = null;
            hmacKeys_ = other.hmacKeys_;
            bitField0_ = (bitField0_ & ~0x00000001);
            hmacKeysBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders
                    ? getHmacKeysFieldBuilder()
                    : null;
          } else {
            hmacKeysBuilder_.addAllMessages(other.hmacKeys_);
          }
        }
      }
      if (!other.getNextPageToken().isEmpty()) {
        nextPageToken_ = other.nextPageToken_;
        bitField0_ |= 0x00000002;
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
            case 10:
              {
                com.google.storage.v2.HmacKeyMetadata m =
                    input.readMessage(
                        com.google.storage.v2.HmacKeyMetadata.parser(), extensionRegistry);
                if (hmacKeysBuilder_ == null) {
                  ensureHmacKeysIsMutable();
                  hmacKeys_.add(m);
                } else {
                  hmacKeysBuilder_.addMessage(m);
                }
                break;
              } // case 10
            case 18:
              {
                nextPageToken_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000002;
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

    private java.util.List<com.google.storage.v2.HmacKeyMetadata> hmacKeys_ =
        java.util.Collections.emptyList();

    private void ensureHmacKeysIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        hmacKeys_ = new java.util.ArrayList<com.google.storage.v2.HmacKeyMetadata>(hmacKeys_);
        bitField0_ |= 0x00000001;
      }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.storage.v2.HmacKeyMetadata,
            com.google.storage.v2.HmacKeyMetadata.Builder,
            com.google.storage.v2.HmacKeyMetadataOrBuilder>
        hmacKeysBuilder_;

    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public java.util.List<com.google.storage.v2.HmacKeyMetadata> getHmacKeysList() {
      if (hmacKeysBuilder_ == null) {
        return java.util.Collections.unmodifiableList(hmacKeys_);
      } else {
        return hmacKeysBuilder_.getMessageList();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public int getHmacKeysCount() {
      if (hmacKeysBuilder_ == null) {
        return hmacKeys_.size();
      } else {
        return hmacKeysBuilder_.getCount();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadata getHmacKeys(int index) {
      if (hmacKeysBuilder_ == null) {
        return hmacKeys_.get(index);
      } else {
        return hmacKeysBuilder_.getMessage(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder setHmacKeys(int index, com.google.storage.v2.HmacKeyMetadata value) {
      if (hmacKeysBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureHmacKeysIsMutable();
        hmacKeys_.set(index, value);
        onChanged();
      } else {
        hmacKeysBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder setHmacKeys(
        int index, com.google.storage.v2.HmacKeyMetadata.Builder builderForValue) {
      if (hmacKeysBuilder_ == null) {
        ensureHmacKeysIsMutable();
        hmacKeys_.set(index, builderForValue.build());
        onChanged();
      } else {
        hmacKeysBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder addHmacKeys(com.google.storage.v2.HmacKeyMetadata value) {
      if (hmacKeysBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureHmacKeysIsMutable();
        hmacKeys_.add(value);
        onChanged();
      } else {
        hmacKeysBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder addHmacKeys(int index, com.google.storage.v2.HmacKeyMetadata value) {
      if (hmacKeysBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureHmacKeysIsMutable();
        hmacKeys_.add(index, value);
        onChanged();
      } else {
        hmacKeysBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder addHmacKeys(com.google.storage.v2.HmacKeyMetadata.Builder builderForValue) {
      if (hmacKeysBuilder_ == null) {
        ensureHmacKeysIsMutable();
        hmacKeys_.add(builderForValue.build());
        onChanged();
      } else {
        hmacKeysBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder addHmacKeys(
        int index, com.google.storage.v2.HmacKeyMetadata.Builder builderForValue) {
      if (hmacKeysBuilder_ == null) {
        ensureHmacKeysIsMutable();
        hmacKeys_.add(index, builderForValue.build());
        onChanged();
      } else {
        hmacKeysBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder addAllHmacKeys(
        java.lang.Iterable<? extends com.google.storage.v2.HmacKeyMetadata> values) {
      if (hmacKeysBuilder_ == null) {
        ensureHmacKeysIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, hmacKeys_);
        onChanged();
      } else {
        hmacKeysBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder clearHmacKeys() {
      if (hmacKeysBuilder_ == null) {
        hmacKeys_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        hmacKeysBuilder_.clear();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public Builder removeHmacKeys(int index) {
      if (hmacKeysBuilder_ == null) {
        ensureHmacKeysIsMutable();
        hmacKeys_.remove(index);
        onChanged();
      } else {
        hmacKeysBuilder_.remove(index);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadata.Builder getHmacKeysBuilder(int index) {
      return getHmacKeysFieldBuilder().getBuilder(index);
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadataOrBuilder getHmacKeysOrBuilder(int index) {
      if (hmacKeysBuilder_ == null) {
        return hmacKeys_.get(index);
      } else {
        return hmacKeysBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public java.util.List<? extends com.google.storage.v2.HmacKeyMetadataOrBuilder>
        getHmacKeysOrBuilderList() {
      if (hmacKeysBuilder_ != null) {
        return hmacKeysBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(hmacKeys_);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadata.Builder addHmacKeysBuilder() {
      return getHmacKeysFieldBuilder()
          .addBuilder(com.google.storage.v2.HmacKeyMetadata.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadata.Builder addHmacKeysBuilder(int index) {
      return getHmacKeysFieldBuilder()
          .addBuilder(index, com.google.storage.v2.HmacKeyMetadata.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.HmacKeyMetadata hmac_keys = 1;</code>
     */
    public java.util.List<com.google.storage.v2.HmacKeyMetadata.Builder> getHmacKeysBuilderList() {
      return getHmacKeysFieldBuilder().getBuilderList();
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.storage.v2.HmacKeyMetadata,
            com.google.storage.v2.HmacKeyMetadata.Builder,
            com.google.storage.v2.HmacKeyMetadataOrBuilder>
        getHmacKeysFieldBuilder() {
      if (hmacKeysBuilder_ == null) {
        hmacKeysBuilder_ =
            new com.google.protobuf.RepeatedFieldBuilderV3<
                com.google.storage.v2.HmacKeyMetadata,
                com.google.storage.v2.HmacKeyMetadata.Builder,
                com.google.storage.v2.HmacKeyMetadataOrBuilder>(
                hmacKeys_, ((bitField0_ & 0x00000001) != 0), getParentForChildren(), isClean());
        hmacKeys_ = null;
      }
      return hmacKeysBuilder_;
    }

    private java.lang.Object nextPageToken_ = "";
    /**
     *
     *
     * <pre>
     * The continuation token, used to page through large result sets. Provide
     * this value in a subsequent request to return the next page of results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return The nextPageToken.
     */
    public java.lang.String getNextPageToken() {
      java.lang.Object ref = nextPageToken_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        nextPageToken_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * The continuation token, used to page through large result sets. Provide
     * this value in a subsequent request to return the next page of results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return The bytes for nextPageToken.
     */
    public com.google.protobuf.ByteString getNextPageTokenBytes() {
      java.lang.Object ref = nextPageToken_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        nextPageToken_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * The continuation token, used to page through large result sets. Provide
     * this value in a subsequent request to return the next page of results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @param value The nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageToken(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      nextPageToken_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The continuation token, used to page through large result sets. Provide
     * this value in a subsequent request to return the next page of results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearNextPageToken() {
      nextPageToken_ = getDefaultInstance().getNextPageToken();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The continuation token, used to page through large result sets. Provide
     * this value in a subsequent request to return the next page of results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @param value The bytes for nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageTokenBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);
      nextPageToken_ = value;
      bitField0_ |= 0x00000002;
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

    // @@protoc_insertion_point(builder_scope:google.storage.v2.ListHmacKeysResponse)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.ListHmacKeysResponse)
  private static final com.google.storage.v2.ListHmacKeysResponse DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.ListHmacKeysResponse();
  }

  public static com.google.storage.v2.ListHmacKeysResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ListHmacKeysResponse> PARSER =
      new com.google.protobuf.AbstractParser<ListHmacKeysResponse>() {
        @java.lang.Override
        public ListHmacKeysResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<ListHmacKeysResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ListHmacKeysResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.ListHmacKeysResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
