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
 * Create hmac response.  The only time the secret for an HMAC will be returned.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.CreateHmacKeyResponse}
 */
public final class CreateHmacKeyResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.CreateHmacKeyResponse)
    CreateHmacKeyResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use CreateHmacKeyResponse.newBuilder() to construct.
  private CreateHmacKeyResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private CreateHmacKeyResponse() {
    secretKeyBytes_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new CreateHmacKeyResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private CreateHmacKeyResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
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
              com.google.storage.v2.HmacKeyMetadata.Builder subBuilder = null;
              if (metadata_ != null) {
                subBuilder = metadata_.toBuilder();
              }
              metadata_ =
                  input.readMessage(
                      com.google.storage.v2.HmacKeyMetadata.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(metadata_);
                metadata_ = subBuilder.buildPartial();
              }

              break;
            }
          case 26:
            {
              secretKeyBytes_ = input.readBytes();
              break;
            }
          default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (com.google.protobuf.UninitializedMessageException e) {
      throw e.asInvalidProtocolBufferException().setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_CreateHmacKeyResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_CreateHmacKeyResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.CreateHmacKeyResponse.class,
            com.google.storage.v2.CreateHmacKeyResponse.Builder.class);
  }

  public static final int METADATA_FIELD_NUMBER = 1;
  private com.google.storage.v2.HmacKeyMetadata metadata_;
  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   *
   * @return Whether the metadata field is set.
   */
  @java.lang.Override
  public boolean hasMetadata() {
    return metadata_ != null;
  }
  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   *
   * @return The metadata.
   */
  @java.lang.Override
  public com.google.storage.v2.HmacKeyMetadata getMetadata() {
    return metadata_ == null
        ? com.google.storage.v2.HmacKeyMetadata.getDefaultInstance()
        : metadata_;
  }
  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.HmacKeyMetadataOrBuilder getMetadataOrBuilder() {
    return getMetadata();
  }

  public static final int SECRET_KEY_BYTES_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString secretKeyBytes_;
  /**
   *
   *
   * <pre>
   * HMAC key secret material.
   * In raw bytes format (not base64-encoded).
   * </pre>
   *
   * <code>bytes secret_key_bytes = 3;</code>
   *
   * @return The secretKeyBytes.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSecretKeyBytes() {
    return secretKeyBytes_;
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
    if (metadata_ != null) {
      output.writeMessage(1, getMetadata());
    }
    if (!secretKeyBytes_.isEmpty()) {
      output.writeBytes(3, secretKeyBytes_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (metadata_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getMetadata());
    }
    if (!secretKeyBytes_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream.computeBytesSize(3, secretKeyBytes_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof com.google.storage.v2.CreateHmacKeyResponse)) {
      return super.equals(obj);
    }
    com.google.storage.v2.CreateHmacKeyResponse other =
        (com.google.storage.v2.CreateHmacKeyResponse) obj;

    if (hasMetadata() != other.hasMetadata()) return false;
    if (hasMetadata()) {
      if (!getMetadata().equals(other.getMetadata())) return false;
    }
    if (!getSecretKeyBytes().equals(other.getSecretKeyBytes())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasMetadata()) {
      hash = (37 * hash) + METADATA_FIELD_NUMBER;
      hash = (53 * hash) + getMetadata().hashCode();
    }
    hash = (37 * hash) + SECRET_KEY_BYTES_FIELD_NUMBER;
    hash = (53 * hash) + getSecretKeyBytes().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.CreateHmacKeyResponse parseFrom(
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

  public static Builder newBuilder(com.google.storage.v2.CreateHmacKeyResponse prototype) {
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
   * Create hmac response.  The only time the secret for an HMAC will be returned.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.CreateHmacKeyResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.CreateHmacKeyResponse)
      com.google.storage.v2.CreateHmacKeyResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_CreateHmacKeyResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_CreateHmacKeyResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.CreateHmacKeyResponse.class,
              com.google.storage.v2.CreateHmacKeyResponse.Builder.class);
    }

    // Construct using com.google.storage.v2.CreateHmacKeyResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {}
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (metadataBuilder_ == null) {
        metadata_ = null;
      } else {
        metadata_ = null;
        metadataBuilder_ = null;
      }
      secretKeyBytes_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_CreateHmacKeyResponse_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyResponse getDefaultInstanceForType() {
      return com.google.storage.v2.CreateHmacKeyResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyResponse build() {
      com.google.storage.v2.CreateHmacKeyResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyResponse buildPartial() {
      com.google.storage.v2.CreateHmacKeyResponse result =
          new com.google.storage.v2.CreateHmacKeyResponse(this);
      if (metadataBuilder_ == null) {
        result.metadata_ = metadata_;
      } else {
        result.metadata_ = metadataBuilder_.build();
      }
      result.secretKeyBytes_ = secretKeyBytes_;
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
      if (other instanceof com.google.storage.v2.CreateHmacKeyResponse) {
        return mergeFrom((com.google.storage.v2.CreateHmacKeyResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.CreateHmacKeyResponse other) {
      if (other == com.google.storage.v2.CreateHmacKeyResponse.getDefaultInstance()) return this;
      if (other.hasMetadata()) {
        mergeMetadata(other.getMetadata());
      }
      if (other.getSecretKeyBytes() != com.google.protobuf.ByteString.EMPTY) {
        setSecretKeyBytes(other.getSecretKeyBytes());
      }
      this.mergeUnknownFields(other.unknownFields);
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
      com.google.storage.v2.CreateHmacKeyResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.storage.v2.CreateHmacKeyResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.storage.v2.HmacKeyMetadata metadata_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.HmacKeyMetadata,
            com.google.storage.v2.HmacKeyMetadata.Builder,
            com.google.storage.v2.HmacKeyMetadataOrBuilder>
        metadataBuilder_;
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     *
     * @return Whether the metadata field is set.
     */
    public boolean hasMetadata() {
      return metadataBuilder_ != null || metadata_ != null;
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     *
     * @return The metadata.
     */
    public com.google.storage.v2.HmacKeyMetadata getMetadata() {
      if (metadataBuilder_ == null) {
        return metadata_ == null
            ? com.google.storage.v2.HmacKeyMetadata.getDefaultInstance()
            : metadata_;
      } else {
        return metadataBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public Builder setMetadata(com.google.storage.v2.HmacKeyMetadata value) {
      if (metadataBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        metadata_ = value;
        onChanged();
      } else {
        metadataBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public Builder setMetadata(com.google.storage.v2.HmacKeyMetadata.Builder builderForValue) {
      if (metadataBuilder_ == null) {
        metadata_ = builderForValue.build();
        onChanged();
      } else {
        metadataBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public Builder mergeMetadata(com.google.storage.v2.HmacKeyMetadata value) {
      if (metadataBuilder_ == null) {
        if (metadata_ != null) {
          metadata_ =
              com.google.storage.v2.HmacKeyMetadata.newBuilder(metadata_)
                  .mergeFrom(value)
                  .buildPartial();
        } else {
          metadata_ = value;
        }
        onChanged();
      } else {
        metadataBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public Builder clearMetadata() {
      if (metadataBuilder_ == null) {
        metadata_ = null;
        onChanged();
      } else {
        metadata_ = null;
        metadataBuilder_ = null;
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadata.Builder getMetadataBuilder() {

      onChanged();
      return getMetadataFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    public com.google.storage.v2.HmacKeyMetadataOrBuilder getMetadataOrBuilder() {
      if (metadataBuilder_ != null) {
        return metadataBuilder_.getMessageOrBuilder();
      } else {
        return metadata_ == null
            ? com.google.storage.v2.HmacKeyMetadata.getDefaultInstance()
            : metadata_;
      }
    }
    /**
     *
     *
     * <pre>
     * Key metadata.
     * </pre>
     *
     * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.HmacKeyMetadata,
            com.google.storage.v2.HmacKeyMetadata.Builder,
            com.google.storage.v2.HmacKeyMetadataOrBuilder>
        getMetadataFieldBuilder() {
      if (metadataBuilder_ == null) {
        metadataBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.storage.v2.HmacKeyMetadata,
                com.google.storage.v2.HmacKeyMetadata.Builder,
                com.google.storage.v2.HmacKeyMetadataOrBuilder>(
                getMetadata(), getParentForChildren(), isClean());
        metadata_ = null;
      }
      return metadataBuilder_;
    }

    private com.google.protobuf.ByteString secretKeyBytes_ = com.google.protobuf.ByteString.EMPTY;
    /**
     *
     *
     * <pre>
     * HMAC key secret material.
     * In raw bytes format (not base64-encoded).
     * </pre>
     *
     * <code>bytes secret_key_bytes = 3;</code>
     *
     * @return The secretKeyBytes.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSecretKeyBytes() {
      return secretKeyBytes_;
    }
    /**
     *
     *
     * <pre>
     * HMAC key secret material.
     * In raw bytes format (not base64-encoded).
     * </pre>
     *
     * <code>bytes secret_key_bytes = 3;</code>
     *
     * @param value The secretKeyBytes to set.
     * @return This builder for chaining.
     */
    public Builder setSecretKeyBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }

      secretKeyBytes_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * HMAC key secret material.
     * In raw bytes format (not base64-encoded).
     * </pre>
     *
     * <code>bytes secret_key_bytes = 3;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearSecretKeyBytes() {

      secretKeyBytes_ = getDefaultInstance().getSecretKeyBytes();
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

    // @@protoc_insertion_point(builder_scope:google.storage.v2.CreateHmacKeyResponse)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.CreateHmacKeyResponse)
  private static final com.google.storage.v2.CreateHmacKeyResponse DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.CreateHmacKeyResponse();
  }

  public static com.google.storage.v2.CreateHmacKeyResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CreateHmacKeyResponse> PARSER =
      new com.google.protobuf.AbstractParser<CreateHmacKeyResponse>() {
        @java.lang.Override
        public CreateHmacKeyResponse parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new CreateHmacKeyResponse(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<CreateHmacKeyResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CreateHmacKeyResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.CreateHmacKeyResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
