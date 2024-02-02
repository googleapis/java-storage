// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

/**
 * <pre>
 * Request message for CreateHmacKey.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.CreateHmacKeyRequest}
 */
public final class CreateHmacKeyRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.CreateHmacKeyRequest)
    CreateHmacKeyRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CreateHmacKeyRequest.newBuilder() to construct.
  private CreateHmacKeyRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CreateHmacKeyRequest() {
    project_ = "";
    serviceAccountEmail_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new CreateHmacKeyRequest();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateHmacKeyRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateHmacKeyRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.CreateHmacKeyRequest.class, com.google.storage.v2.CreateHmacKeyRequest.Builder.class);
  }

  public static final int PROJECT_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object project_ = "";
  /**
   * <pre>
   * Required. The project that the HMAC-owning service account lives in, in the
   * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
   * project ID or project number.
   * </pre>
   *
   * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The project.
   */
  @java.lang.Override
  public java.lang.String getProject() {
    java.lang.Object ref = project_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      project_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * Required. The project that the HMAC-owning service account lives in, in the
   * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
   * project ID or project number.
   * </pre>
   *
   * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for project.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getProjectBytes() {
    java.lang.Object ref = project_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      project_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int SERVICE_ACCOUNT_EMAIL_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private volatile java.lang.Object serviceAccountEmail_ = "";
  /**
   * <pre>
   * Required. The service account to create the HMAC for.
   * </pre>
   *
   * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The serviceAccountEmail.
   */
  @java.lang.Override
  public java.lang.String getServiceAccountEmail() {
    java.lang.Object ref = serviceAccountEmail_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      serviceAccountEmail_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * Required. The service account to create the HMAC for.
   * </pre>
   *
   * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The bytes for serviceAccountEmail.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getServiceAccountEmailBytes() {
    java.lang.Object ref = serviceAccountEmail_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      serviceAccountEmail_ = b;
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
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(project_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, project_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(serviceAccountEmail_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, serviceAccountEmail_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(project_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, project_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(serviceAccountEmail_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, serviceAccountEmail_);
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
    if (!(obj instanceof com.google.storage.v2.CreateHmacKeyRequest)) {
      return super.equals(obj);
    }
    com.google.storage.v2.CreateHmacKeyRequest other = (com.google.storage.v2.CreateHmacKeyRequest) obj;

    if (!getProject()
        .equals(other.getProject())) return false;
    if (!getServiceAccountEmail()
        .equals(other.getServiceAccountEmail())) return false;
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
    hash = (37 * hash) + PROJECT_FIELD_NUMBER;
    hash = (53 * hash) + getProject().hashCode();
    hash = (37 * hash) + SERVICE_ACCOUNT_EMAIL_FIELD_NUMBER;
    hash = (53 * hash) + getServiceAccountEmail().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.CreateHmacKeyRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.CreateHmacKeyRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.CreateHmacKeyRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.storage.v2.CreateHmacKeyRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * Request message for CreateHmacKey.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.CreateHmacKeyRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.CreateHmacKeyRequest)
      com.google.storage.v2.CreateHmacKeyRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateHmacKeyRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateHmacKeyRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.CreateHmacKeyRequest.class, com.google.storage.v2.CreateHmacKeyRequest.Builder.class);
    }

    // Construct using com.google.storage.v2.CreateHmacKeyRequest.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      project_ = "";
      serviceAccountEmail_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateHmacKeyRequest_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyRequest getDefaultInstanceForType() {
      return com.google.storage.v2.CreateHmacKeyRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyRequest build() {
      com.google.storage.v2.CreateHmacKeyRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateHmacKeyRequest buildPartial() {
      com.google.storage.v2.CreateHmacKeyRequest result = new com.google.storage.v2.CreateHmacKeyRequest(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.storage.v2.CreateHmacKeyRequest result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.project_ = project_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.serviceAccountEmail_ = serviceAccountEmail_;
      }
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.storage.v2.CreateHmacKeyRequest) {
        return mergeFrom((com.google.storage.v2.CreateHmacKeyRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.CreateHmacKeyRequest other) {
      if (other == com.google.storage.v2.CreateHmacKeyRequest.getDefaultInstance()) return this;
      if (!other.getProject().isEmpty()) {
        project_ = other.project_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (!other.getServiceAccountEmail().isEmpty()) {
        serviceAccountEmail_ = other.serviceAccountEmail_;
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
            case 10: {
              project_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              serviceAccountEmail_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            default: {
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

    private java.lang.Object project_ = "";
    /**
     * <pre>
     * Required. The project that the HMAC-owning service account lives in, in the
     * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
     * project ID or project number.
     * </pre>
     *
     * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return The project.
     */
    public java.lang.String getProject() {
      java.lang.Object ref = project_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        project_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * Required. The project that the HMAC-owning service account lives in, in the
     * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
     * project ID or project number.
     * </pre>
     *
     * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return The bytes for project.
     */
    public com.google.protobuf.ByteString
        getProjectBytes() {
      java.lang.Object ref = project_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        project_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * Required. The project that the HMAC-owning service account lives in, in the
     * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
     * project ID or project number.
     * </pre>
     *
     * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @param value The project to set.
     * @return This builder for chaining.
     */
    public Builder setProject(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      project_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The project that the HMAC-owning service account lives in, in the
     * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
     * project ID or project number.
     * </pre>
     *
     * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return This builder for chaining.
     */
    public Builder clearProject() {
      project_ = getDefaultInstance().getProject();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The project that the HMAC-owning service account lives in, in the
     * format of "projects/{projectIdentifier}". {projectIdentifier} can be the
     * project ID or project number.
     * </pre>
     *
     * <code>string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @param value The bytes for project to set.
     * @return This builder for chaining.
     */
    public Builder setProjectBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      project_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private java.lang.Object serviceAccountEmail_ = "";
    /**
     * <pre>
     * Required. The service account to create the HMAC for.
     * </pre>
     *
     * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @return The serviceAccountEmail.
     */
    public java.lang.String getServiceAccountEmail() {
      java.lang.Object ref = serviceAccountEmail_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        serviceAccountEmail_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * Required. The service account to create the HMAC for.
     * </pre>
     *
     * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @return The bytes for serviceAccountEmail.
     */
    public com.google.protobuf.ByteString
        getServiceAccountEmailBytes() {
      java.lang.Object ref = serviceAccountEmail_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        serviceAccountEmail_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * Required. The service account to create the HMAC for.
     * </pre>
     *
     * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @param value The serviceAccountEmail to set.
     * @return This builder for chaining.
     */
    public Builder setServiceAccountEmail(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      serviceAccountEmail_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The service account to create the HMAC for.
     * </pre>
     *
     * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @return This builder for chaining.
     */
    public Builder clearServiceAccountEmail() {
      serviceAccountEmail_ = getDefaultInstance().getServiceAccountEmail();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The service account to create the HMAC for.
     * </pre>
     *
     * <code>string service_account_email = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @param value The bytes for serviceAccountEmail to set.
     * @return This builder for chaining.
     */
    public Builder setServiceAccountEmailBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      serviceAccountEmail_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:google.storage.v2.CreateHmacKeyRequest)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.CreateHmacKeyRequest)
  private static final com.google.storage.v2.CreateHmacKeyRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.CreateHmacKeyRequest();
  }

  public static com.google.storage.v2.CreateHmacKeyRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CreateHmacKeyRequest>
      PARSER = new com.google.protobuf.AbstractParser<CreateHmacKeyRequest>() {
    @java.lang.Override
    public CreateHmacKeyRequest parsePartialFrom(
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

  public static com.google.protobuf.Parser<CreateHmacKeyRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CreateHmacKeyRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.CreateHmacKeyRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

