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
 * Request message for GetServiceAccount.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.GetServiceAccountRequest}
 */
public final class GetServiceAccountRequest extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.GetServiceAccountRequest)
    GetServiceAccountRequestOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use GetServiceAccountRequest.newBuilder() to construct.
  private GetServiceAccountRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private GetServiceAccountRequest() {
    project_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new GetServiceAccountRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_GetServiceAccountRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto
        .internal_static_google_storage_v2_GetServiceAccountRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.GetServiceAccountRequest.class,
            com.google.storage.v2.GetServiceAccountRequest.Builder.class);
  }

  public static final int PROJECT_FIELD_NUMBER = 1;
  private volatile java.lang.Object project_;
  /**
   *
   *
   * <pre>
   * Required. Project ID.
   * </pre>
   *
   * <code>
   * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The project.
   */
  @java.lang.Override
  public java.lang.String getProject() {
    java.lang.Object ref = project_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      project_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * Required. Project ID.
   * </pre>
   *
   * <code>
   * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for project.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getProjectBytes() {
    java.lang.Object ref = project_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      project_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int COMMON_REQUEST_PARAMS_FIELD_NUMBER = 2;
  private com.google.storage.v2.CommonRequestParams commonRequestParams_;
  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
   *
   * @return Whether the commonRequestParams field is set.
   */
  @java.lang.Override
  public boolean hasCommonRequestParams() {
    return commonRequestParams_ != null;
  }
  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
   *
   * @return The commonRequestParams.
   */
  @java.lang.Override
  public com.google.storage.v2.CommonRequestParams getCommonRequestParams() {
    return commonRequestParams_ == null
        ? com.google.storage.v2.CommonRequestParams.getDefaultInstance()
        : commonRequestParams_;
  }
  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.CommonRequestParamsOrBuilder getCommonRequestParamsOrBuilder() {
    return getCommonRequestParams();
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(project_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, project_);
    }
    if (commonRequestParams_ != null) {
      output.writeMessage(2, getCommonRequestParams());
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
    if (commonRequestParams_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, getCommonRequestParams());
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
    if (!(obj instanceof com.google.storage.v2.GetServiceAccountRequest)) {
      return super.equals(obj);
    }
    com.google.storage.v2.GetServiceAccountRequest other =
        (com.google.storage.v2.GetServiceAccountRequest) obj;

    if (!getProject().equals(other.getProject())) return false;
    if (hasCommonRequestParams() != other.hasCommonRequestParams()) return false;
    if (hasCommonRequestParams()) {
      if (!getCommonRequestParams().equals(other.getCommonRequestParams())) return false;
    }
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
    if (hasCommonRequestParams()) {
      hash = (37 * hash) + COMMON_REQUEST_PARAMS_FIELD_NUMBER;
      hash = (53 * hash) + getCommonRequestParams().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.v2.GetServiceAccountRequest parseFrom(
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

  public static Builder newBuilder(com.google.storage.v2.GetServiceAccountRequest prototype) {
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
   * Request message for GetServiceAccount.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.GetServiceAccountRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.GetServiceAccountRequest)
      com.google.storage.v2.GetServiceAccountRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_GetServiceAccountRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_GetServiceAccountRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.GetServiceAccountRequest.class,
              com.google.storage.v2.GetServiceAccountRequest.Builder.class);
    }

    // Construct using com.google.storage.v2.GetServiceAccountRequest.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      project_ = "";

      if (commonRequestParamsBuilder_ == null) {
        commonRequestParams_ = null;
      } else {
        commonRequestParams_ = null;
        commonRequestParamsBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.v2.StorageProto
          .internal_static_google_storage_v2_GetServiceAccountRequest_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.GetServiceAccountRequest getDefaultInstanceForType() {
      return com.google.storage.v2.GetServiceAccountRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.GetServiceAccountRequest build() {
      com.google.storage.v2.GetServiceAccountRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.GetServiceAccountRequest buildPartial() {
      com.google.storage.v2.GetServiceAccountRequest result =
          new com.google.storage.v2.GetServiceAccountRequest(this);
      result.project_ = project_;
      if (commonRequestParamsBuilder_ == null) {
        result.commonRequestParams_ = commonRequestParams_;
      } else {
        result.commonRequestParams_ = commonRequestParamsBuilder_.build();
      }
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
      if (other instanceof com.google.storage.v2.GetServiceAccountRequest) {
        return mergeFrom((com.google.storage.v2.GetServiceAccountRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.GetServiceAccountRequest other) {
      if (other == com.google.storage.v2.GetServiceAccountRequest.getDefaultInstance()) return this;
      if (!other.getProject().isEmpty()) {
        project_ = other.project_;
        onChanged();
      }
      if (other.hasCommonRequestParams()) {
        mergeCommonRequestParams(other.getCommonRequestParams());
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
                project_ = input.readStringRequireUtf8();

                break;
              } // case 10
            case 18:
              {
                input.readMessage(
                    getCommonRequestParamsFieldBuilder().getBuilder(), extensionRegistry);

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

    private java.lang.Object project_ = "";
    /**
     *
     *
     * <pre>
     * Required. Project ID.
     * </pre>
     *
     * <code>
     * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return The project.
     */
    public java.lang.String getProject() {
      java.lang.Object ref = project_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        project_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Required. Project ID.
     * </pre>
     *
     * <code>
     * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return The bytes for project.
     */
    public com.google.protobuf.ByteString getProjectBytes() {
      java.lang.Object ref = project_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        project_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * Required. Project ID.
     * </pre>
     *
     * <code>
     * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @param value The project to set.
     * @return This builder for chaining.
     */
    public Builder setProject(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      project_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Required. Project ID.
     * </pre>
     *
     * <code>
     * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @return This builder for chaining.
     */
    public Builder clearProject() {

      project_ = getDefaultInstance().getProject();
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Required. Project ID.
     * </pre>
     *
     * <code>
     * string project = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
     * </code>
     *
     * @param value The bytes for project to set.
     * @return This builder for chaining.
     */
    public Builder setProjectBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      project_ = value;
      onChanged();
      return this;
    }

    private com.google.storage.v2.CommonRequestParams commonRequestParams_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.CommonRequestParams,
            com.google.storage.v2.CommonRequestParams.Builder,
            com.google.storage.v2.CommonRequestParamsOrBuilder>
        commonRequestParamsBuilder_;
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     *
     * @return Whether the commonRequestParams field is set.
     */
    public boolean hasCommonRequestParams() {
      return commonRequestParamsBuilder_ != null || commonRequestParams_ != null;
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     *
     * @return The commonRequestParams.
     */
    public com.google.storage.v2.CommonRequestParams getCommonRequestParams() {
      if (commonRequestParamsBuilder_ == null) {
        return commonRequestParams_ == null
            ? com.google.storage.v2.CommonRequestParams.getDefaultInstance()
            : commonRequestParams_;
      } else {
        return commonRequestParamsBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public Builder setCommonRequestParams(com.google.storage.v2.CommonRequestParams value) {
      if (commonRequestParamsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        commonRequestParams_ = value;
        onChanged();
      } else {
        commonRequestParamsBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public Builder setCommonRequestParams(
        com.google.storage.v2.CommonRequestParams.Builder builderForValue) {
      if (commonRequestParamsBuilder_ == null) {
        commonRequestParams_ = builderForValue.build();
        onChanged();
      } else {
        commonRequestParamsBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public Builder mergeCommonRequestParams(com.google.storage.v2.CommonRequestParams value) {
      if (commonRequestParamsBuilder_ == null) {
        if (commonRequestParams_ != null) {
          commonRequestParams_ =
              com.google.storage.v2.CommonRequestParams.newBuilder(commonRequestParams_)
                  .mergeFrom(value)
                  .buildPartial();
        } else {
          commonRequestParams_ = value;
        }
        onChanged();
      } else {
        commonRequestParamsBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public Builder clearCommonRequestParams() {
      if (commonRequestParamsBuilder_ == null) {
        commonRequestParams_ = null;
        onChanged();
      } else {
        commonRequestParams_ = null;
        commonRequestParamsBuilder_ = null;
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public com.google.storage.v2.CommonRequestParams.Builder getCommonRequestParamsBuilder() {

      onChanged();
      return getCommonRequestParamsFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    public com.google.storage.v2.CommonRequestParamsOrBuilder getCommonRequestParamsOrBuilder() {
      if (commonRequestParamsBuilder_ != null) {
        return commonRequestParamsBuilder_.getMessageOrBuilder();
      } else {
        return commonRequestParams_ == null
            ? com.google.storage.v2.CommonRequestParams.getDefaultInstance()
            : commonRequestParams_;
      }
    }
    /**
     *
     *
     * <pre>
     * A set of parameters common to all Storage API requests.
     * </pre>
     *
     * <code>.google.storage.v2.CommonRequestParams common_request_params = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.CommonRequestParams,
            com.google.storage.v2.CommonRequestParams.Builder,
            com.google.storage.v2.CommonRequestParamsOrBuilder>
        getCommonRequestParamsFieldBuilder() {
      if (commonRequestParamsBuilder_ == null) {
        commonRequestParamsBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.storage.v2.CommonRequestParams,
                com.google.storage.v2.CommonRequestParams.Builder,
                com.google.storage.v2.CommonRequestParamsOrBuilder>(
                getCommonRequestParams(), getParentForChildren(), isClean());
        commonRequestParams_ = null;
      }
      return commonRequestParamsBuilder_;
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

    // @@protoc_insertion_point(builder_scope:google.storage.v2.GetServiceAccountRequest)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.GetServiceAccountRequest)
  private static final com.google.storage.v2.GetServiceAccountRequest DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.GetServiceAccountRequest();
  }

  public static com.google.storage.v2.GetServiceAccountRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GetServiceAccountRequest> PARSER =
      new com.google.protobuf.AbstractParser<GetServiceAccountRequest>() {
        @java.lang.Override
        public GetServiceAccountRequest parsePartialFrom(
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

  public static com.google.protobuf.Parser<GetServiceAccountRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetServiceAccountRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.GetServiceAccountRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
