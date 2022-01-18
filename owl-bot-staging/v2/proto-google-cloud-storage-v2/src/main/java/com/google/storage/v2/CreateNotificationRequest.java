// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

/**
 * <pre>
 * Request message for CreateNotification.
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.CreateNotificationRequest}
 */
public final class CreateNotificationRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.CreateNotificationRequest)
    CreateNotificationRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CreateNotificationRequest.newBuilder() to construct.
  private CreateNotificationRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CreateNotificationRequest() {
    parent_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new CreateNotificationRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CreateNotificationRequest(
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
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            parent_ = s;
            break;
          }
          case 18: {
            com.google.storage.v2.Notification.Builder subBuilder = null;
            if (notification_ != null) {
              subBuilder = notification_.toBuilder();
            }
            notification_ = input.readMessage(com.google.storage.v2.Notification.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(notification_);
              notification_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateNotificationRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateNotificationRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.CreateNotificationRequest.class, com.google.storage.v2.CreateNotificationRequest.Builder.class);
  }

  public static final int PARENT_FIELD_NUMBER = 1;
  private volatile java.lang.Object parent_;
  /**
   * <pre>
   * Required. The bucket to which this notification belongs.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The parent.
   */
  @java.lang.Override
  public java.lang.String getParent() {
    java.lang.Object ref = parent_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      parent_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * Required. The bucket to which this notification belongs.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for parent.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getParentBytes() {
    java.lang.Object ref = parent_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      parent_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int NOTIFICATION_FIELD_NUMBER = 2;
  private com.google.storage.v2.Notification notification_;
  /**
   * <pre>
   * Required. Properties of the notification to be inserted.
   * </pre>
   *
   * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return Whether the notification field is set.
   */
  @java.lang.Override
  public boolean hasNotification() {
    return notification_ != null;
  }
  /**
   * <pre>
   * Required. Properties of the notification to be inserted.
   * </pre>
   *
   * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The notification.
   */
  @java.lang.Override
  public com.google.storage.v2.Notification getNotification() {
    return notification_ == null ? com.google.storage.v2.Notification.getDefaultInstance() : notification_;
  }
  /**
   * <pre>
   * Required. Properties of the notification to be inserted.
   * </pre>
   *
   * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   */
  @java.lang.Override
  public com.google.storage.v2.NotificationOrBuilder getNotificationOrBuilder() {
    return getNotification();
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(parent_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, parent_);
    }
    if (notification_ != null) {
      output.writeMessage(2, getNotification());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(parent_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, parent_);
    }
    if (notification_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getNotification());
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
    if (!(obj instanceof com.google.storage.v2.CreateNotificationRequest)) {
      return super.equals(obj);
    }
    com.google.storage.v2.CreateNotificationRequest other = (com.google.storage.v2.CreateNotificationRequest) obj;

    if (!getParent()
        .equals(other.getParent())) return false;
    if (hasNotification() != other.hasNotification()) return false;
    if (hasNotification()) {
      if (!getNotification()
          .equals(other.getNotification())) return false;
    }
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
    hash = (37 * hash) + PARENT_FIELD_NUMBER;
    hash = (53 * hash) + getParent().hashCode();
    if (hasNotification()) {
      hash = (37 * hash) + NOTIFICATION_FIELD_NUMBER;
      hash = (53 * hash) + getNotification().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.CreateNotificationRequest parseFrom(
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
  public static Builder newBuilder(com.google.storage.v2.CreateNotificationRequest prototype) {
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
   * Request message for CreateNotification.
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.CreateNotificationRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.CreateNotificationRequest)
      com.google.storage.v2.CreateNotificationRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateNotificationRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateNotificationRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.CreateNotificationRequest.class, com.google.storage.v2.CreateNotificationRequest.Builder.class);
    }

    // Construct using com.google.storage.v2.CreateNotificationRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      parent_ = "";

      if (notificationBuilder_ == null) {
        notification_ = null;
      } else {
        notification_ = null;
        notificationBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_CreateNotificationRequest_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateNotificationRequest getDefaultInstanceForType() {
      return com.google.storage.v2.CreateNotificationRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.CreateNotificationRequest build() {
      com.google.storage.v2.CreateNotificationRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.CreateNotificationRequest buildPartial() {
      com.google.storage.v2.CreateNotificationRequest result = new com.google.storage.v2.CreateNotificationRequest(this);
      result.parent_ = parent_;
      if (notificationBuilder_ == null) {
        result.notification_ = notification_;
      } else {
        result.notification_ = notificationBuilder_.build();
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
      if (other instanceof com.google.storage.v2.CreateNotificationRequest) {
        return mergeFrom((com.google.storage.v2.CreateNotificationRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.CreateNotificationRequest other) {
      if (other == com.google.storage.v2.CreateNotificationRequest.getDefaultInstance()) return this;
      if (!other.getParent().isEmpty()) {
        parent_ = other.parent_;
        onChanged();
      }
      if (other.hasNotification()) {
        mergeNotification(other.getNotification());
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
      com.google.storage.v2.CreateNotificationRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.storage.v2.CreateNotificationRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object parent_ = "";
    /**
     * <pre>
     * Required. The bucket to which this notification belongs.
     * </pre>
     *
     * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return The parent.
     */
    public java.lang.String getParent() {
      java.lang.Object ref = parent_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        parent_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * Required. The bucket to which this notification belongs.
     * </pre>
     *
     * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return The bytes for parent.
     */
    public com.google.protobuf.ByteString
        getParentBytes() {
      java.lang.Object ref = parent_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        parent_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * Required. The bucket to which this notification belongs.
     * </pre>
     *
     * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @param value The parent to set.
     * @return This builder for chaining.
     */
    public Builder setParent(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      parent_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The bucket to which this notification belongs.
     * </pre>
     *
     * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @return This builder for chaining.
     */
    public Builder clearParent() {
      
      parent_ = getDefaultInstance().getParent();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Required. The bucket to which this notification belongs.
     * </pre>
     *
     * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
     * @param value The bytes for parent to set.
     * @return This builder for chaining.
     */
    public Builder setParentBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      parent_ = value;
      onChanged();
      return this;
    }

    private com.google.storage.v2.Notification notification_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder> notificationBuilder_;
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @return Whether the notification field is set.
     */
    public boolean hasNotification() {
      return notificationBuilder_ != null || notification_ != null;
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     * @return The notification.
     */
    public com.google.storage.v2.Notification getNotification() {
      if (notificationBuilder_ == null) {
        return notification_ == null ? com.google.storage.v2.Notification.getDefaultInstance() : notification_;
      } else {
        return notificationBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public Builder setNotification(com.google.storage.v2.Notification value) {
      if (notificationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        notification_ = value;
        onChanged();
      } else {
        notificationBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public Builder setNotification(
        com.google.storage.v2.Notification.Builder builderForValue) {
      if (notificationBuilder_ == null) {
        notification_ = builderForValue.build();
        onChanged();
      } else {
        notificationBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public Builder mergeNotification(com.google.storage.v2.Notification value) {
      if (notificationBuilder_ == null) {
        if (notification_ != null) {
          notification_ =
            com.google.storage.v2.Notification.newBuilder(notification_).mergeFrom(value).buildPartial();
        } else {
          notification_ = value;
        }
        onChanged();
      } else {
        notificationBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public Builder clearNotification() {
      if (notificationBuilder_ == null) {
        notification_ = null;
        onChanged();
      } else {
        notification_ = null;
        notificationBuilder_ = null;
      }

      return this;
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public com.google.storage.v2.Notification.Builder getNotificationBuilder() {
      
      onChanged();
      return getNotificationFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    public com.google.storage.v2.NotificationOrBuilder getNotificationOrBuilder() {
      if (notificationBuilder_ != null) {
        return notificationBuilder_.getMessageOrBuilder();
      } else {
        return notification_ == null ?
            com.google.storage.v2.Notification.getDefaultInstance() : notification_;
      }
    }
    /**
     * <pre>
     * Required. Properties of the notification to be inserted.
     * </pre>
     *
     * <code>.google.storage.v2.Notification notification = 2 [(.google.api.field_behavior) = REQUIRED];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder> 
        getNotificationFieldBuilder() {
      if (notificationBuilder_ == null) {
        notificationBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder>(
                getNotification(),
                getParentForChildren(),
                isClean());
        notification_ = null;
      }
      return notificationBuilder_;
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


    // @@protoc_insertion_point(builder_scope:google.storage.v2.CreateNotificationRequest)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.CreateNotificationRequest)
  private static final com.google.storage.v2.CreateNotificationRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.CreateNotificationRequest();
  }

  public static com.google.storage.v2.CreateNotificationRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CreateNotificationRequest>
      PARSER = new com.google.protobuf.AbstractParser<CreateNotificationRequest>() {
    @java.lang.Override
    public CreateNotificationRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CreateNotificationRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CreateNotificationRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CreateNotificationRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.CreateNotificationRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

