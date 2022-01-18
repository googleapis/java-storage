// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

/**
 * <pre>
 * The result of a call to Notifications.ListNotifications
 * </pre>
 *
 * Protobuf type {@code google.storage.v2.ListNotificationsResponse}
 */
public final class ListNotificationsResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:google.storage.v2.ListNotificationsResponse)
    ListNotificationsResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ListNotificationsResponse.newBuilder() to construct.
  private ListNotificationsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ListNotificationsResponse() {
    notifications_ = java.util.Collections.emptyList();
    nextPageToken_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ListNotificationsResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ListNotificationsResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              notifications_ = new java.util.ArrayList<com.google.storage.v2.Notification>();
              mutable_bitField0_ |= 0x00000001;
            }
            notifications_.add(
                input.readMessage(com.google.storage.v2.Notification.parser(), extensionRegistry));
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            nextPageToken_ = s;
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
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        notifications_ = java.util.Collections.unmodifiableList(notifications_);
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_ListNotificationsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_ListNotificationsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.v2.ListNotificationsResponse.class, com.google.storage.v2.ListNotificationsResponse.Builder.class);
  }

  public static final int NOTIFICATIONS_FIELD_NUMBER = 1;
  private java.util.List<com.google.storage.v2.Notification> notifications_;
  /**
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
   */
  @java.lang.Override
  public java.util.List<com.google.storage.v2.Notification> getNotificationsList() {
    return notifications_;
  }
  /**
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.google.storage.v2.NotificationOrBuilder> 
      getNotificationsOrBuilderList() {
    return notifications_;
  }
  /**
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
   */
  @java.lang.Override
  public int getNotificationsCount() {
    return notifications_.size();
  }
  /**
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.Notification getNotifications(int index) {
    return notifications_.get(index);
  }
  /**
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.v2.NotificationOrBuilder getNotificationsOrBuilder(
      int index) {
    return notifications_.get(index);
  }

  public static final int NEXT_PAGE_TOKEN_FIELD_NUMBER = 2;
  private volatile java.lang.Object nextPageToken_;
  /**
   * <pre>
   * A token, which can be sent as `page_token` to retrieve the next page.
   * If this field is omitted, there are no subsequent pages.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   * @return The nextPageToken.
   */
  @java.lang.Override
  public java.lang.String getNextPageToken() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      nextPageToken_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * A token, which can be sent as `page_token` to retrieve the next page.
   * If this field is omitted, there are no subsequent pages.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   * @return The bytes for nextPageToken.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getNextPageTokenBytes() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
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
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    for (int i = 0; i < notifications_.size(); i++) {
      output.writeMessage(1, notifications_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, nextPageToken_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < notifications_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, notifications_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, nextPageToken_);
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
    if (!(obj instanceof com.google.storage.v2.ListNotificationsResponse)) {
      return super.equals(obj);
    }
    com.google.storage.v2.ListNotificationsResponse other = (com.google.storage.v2.ListNotificationsResponse) obj;

    if (!getNotificationsList()
        .equals(other.getNotificationsList())) return false;
    if (!getNextPageToken()
        .equals(other.getNextPageToken())) return false;
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
    if (getNotificationsCount() > 0) {
      hash = (37 * hash) + NOTIFICATIONS_FIELD_NUMBER;
      hash = (53 * hash) + getNotificationsList().hashCode();
    }
    hash = (37 * hash) + NEXT_PAGE_TOKEN_FIELD_NUMBER;
    hash = (53 * hash) + getNextPageToken().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.storage.v2.ListNotificationsResponse parseFrom(
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
  public static Builder newBuilder(com.google.storage.v2.ListNotificationsResponse prototype) {
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
   * The result of a call to Notifications.ListNotifications
   * </pre>
   *
   * Protobuf type {@code google.storage.v2.ListNotificationsResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.storage.v2.ListNotificationsResponse)
      com.google.storage.v2.ListNotificationsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_ListNotificationsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_ListNotificationsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.v2.ListNotificationsResponse.class, com.google.storage.v2.ListNotificationsResponse.Builder.class);
    }

    // Construct using com.google.storage.v2.ListNotificationsResponse.newBuilder()
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
        getNotificationsFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (notificationsBuilder_ == null) {
        notifications_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        notificationsBuilder_.clear();
      }
      nextPageToken_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.storage.v2.StorageProto.internal_static_google_storage_v2_ListNotificationsResponse_descriptor;
    }

    @java.lang.Override
    public com.google.storage.v2.ListNotificationsResponse getDefaultInstanceForType() {
      return com.google.storage.v2.ListNotificationsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.v2.ListNotificationsResponse build() {
      com.google.storage.v2.ListNotificationsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.v2.ListNotificationsResponse buildPartial() {
      com.google.storage.v2.ListNotificationsResponse result = new com.google.storage.v2.ListNotificationsResponse(this);
      int from_bitField0_ = bitField0_;
      if (notificationsBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          notifications_ = java.util.Collections.unmodifiableList(notifications_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.notifications_ = notifications_;
      } else {
        result.notifications_ = notificationsBuilder_.build();
      }
      result.nextPageToken_ = nextPageToken_;
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
      if (other instanceof com.google.storage.v2.ListNotificationsResponse) {
        return mergeFrom((com.google.storage.v2.ListNotificationsResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.v2.ListNotificationsResponse other) {
      if (other == com.google.storage.v2.ListNotificationsResponse.getDefaultInstance()) return this;
      if (notificationsBuilder_ == null) {
        if (!other.notifications_.isEmpty()) {
          if (notifications_.isEmpty()) {
            notifications_ = other.notifications_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureNotificationsIsMutable();
            notifications_.addAll(other.notifications_);
          }
          onChanged();
        }
      } else {
        if (!other.notifications_.isEmpty()) {
          if (notificationsBuilder_.isEmpty()) {
            notificationsBuilder_.dispose();
            notificationsBuilder_ = null;
            notifications_ = other.notifications_;
            bitField0_ = (bitField0_ & ~0x00000001);
            notificationsBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getNotificationsFieldBuilder() : null;
          } else {
            notificationsBuilder_.addAllMessages(other.notifications_);
          }
        }
      }
      if (!other.getNextPageToken().isEmpty()) {
        nextPageToken_ = other.nextPageToken_;
        onChanged();
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
      com.google.storage.v2.ListNotificationsResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.storage.v2.ListNotificationsResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.util.List<com.google.storage.v2.Notification> notifications_ =
      java.util.Collections.emptyList();
    private void ensureNotificationsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        notifications_ = new java.util.ArrayList<com.google.storage.v2.Notification>(notifications_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder> notificationsBuilder_;

    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public java.util.List<com.google.storage.v2.Notification> getNotificationsList() {
      if (notificationsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(notifications_);
      } else {
        return notificationsBuilder_.getMessageList();
      }
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public int getNotificationsCount() {
      if (notificationsBuilder_ == null) {
        return notifications_.size();
      } else {
        return notificationsBuilder_.getCount();
      }
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public com.google.storage.v2.Notification getNotifications(int index) {
      if (notificationsBuilder_ == null) {
        return notifications_.get(index);
      } else {
        return notificationsBuilder_.getMessage(index);
      }
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder setNotifications(
        int index, com.google.storage.v2.Notification value) {
      if (notificationsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureNotificationsIsMutable();
        notifications_.set(index, value);
        onChanged();
      } else {
        notificationsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder setNotifications(
        int index, com.google.storage.v2.Notification.Builder builderForValue) {
      if (notificationsBuilder_ == null) {
        ensureNotificationsIsMutable();
        notifications_.set(index, builderForValue.build());
        onChanged();
      } else {
        notificationsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder addNotifications(com.google.storage.v2.Notification value) {
      if (notificationsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureNotificationsIsMutable();
        notifications_.add(value);
        onChanged();
      } else {
        notificationsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder addNotifications(
        int index, com.google.storage.v2.Notification value) {
      if (notificationsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureNotificationsIsMutable();
        notifications_.add(index, value);
        onChanged();
      } else {
        notificationsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder addNotifications(
        com.google.storage.v2.Notification.Builder builderForValue) {
      if (notificationsBuilder_ == null) {
        ensureNotificationsIsMutable();
        notifications_.add(builderForValue.build());
        onChanged();
      } else {
        notificationsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder addNotifications(
        int index, com.google.storage.v2.Notification.Builder builderForValue) {
      if (notificationsBuilder_ == null) {
        ensureNotificationsIsMutable();
        notifications_.add(index, builderForValue.build());
        onChanged();
      } else {
        notificationsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder addAllNotifications(
        java.lang.Iterable<? extends com.google.storage.v2.Notification> values) {
      if (notificationsBuilder_ == null) {
        ensureNotificationsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, notifications_);
        onChanged();
      } else {
        notificationsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder clearNotifications() {
      if (notificationsBuilder_ == null) {
        notifications_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        notificationsBuilder_.clear();
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public Builder removeNotifications(int index) {
      if (notificationsBuilder_ == null) {
        ensureNotificationsIsMutable();
        notifications_.remove(index);
        onChanged();
      } else {
        notificationsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public com.google.storage.v2.Notification.Builder getNotificationsBuilder(
        int index) {
      return getNotificationsFieldBuilder().getBuilder(index);
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public com.google.storage.v2.NotificationOrBuilder getNotificationsOrBuilder(
        int index) {
      if (notificationsBuilder_ == null) {
        return notifications_.get(index);  } else {
        return notificationsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public java.util.List<? extends com.google.storage.v2.NotificationOrBuilder> 
         getNotificationsOrBuilderList() {
      if (notificationsBuilder_ != null) {
        return notificationsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(notifications_);
      }
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public com.google.storage.v2.Notification.Builder addNotificationsBuilder() {
      return getNotificationsFieldBuilder().addBuilder(
          com.google.storage.v2.Notification.getDefaultInstance());
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public com.google.storage.v2.Notification.Builder addNotificationsBuilder(
        int index) {
      return getNotificationsFieldBuilder().addBuilder(
          index, com.google.storage.v2.Notification.getDefaultInstance());
    }
    /**
     * <pre>
     * The list of items.
     * </pre>
     *
     * <code>repeated .google.storage.v2.Notification notifications = 1;</code>
     */
    public java.util.List<com.google.storage.v2.Notification.Builder> 
         getNotificationsBuilderList() {
      return getNotificationsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder> 
        getNotificationsFieldBuilder() {
      if (notificationsBuilder_ == null) {
        notificationsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.storage.v2.Notification, com.google.storage.v2.Notification.Builder, com.google.storage.v2.NotificationOrBuilder>(
                notifications_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        notifications_ = null;
      }
      return notificationsBuilder_;
    }

    private java.lang.Object nextPageToken_ = "";
    /**
     * <pre>
     * A token, which can be sent as `page_token` to retrieve the next page.
     * If this field is omitted, there are no subsequent pages.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     * @return The nextPageToken.
     */
    public java.lang.String getNextPageToken() {
      java.lang.Object ref = nextPageToken_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        nextPageToken_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * A token, which can be sent as `page_token` to retrieve the next page.
     * If this field is omitted, there are no subsequent pages.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     * @return The bytes for nextPageToken.
     */
    public com.google.protobuf.ByteString
        getNextPageTokenBytes() {
      java.lang.Object ref = nextPageToken_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        nextPageToken_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * A token, which can be sent as `page_token` to retrieve the next page.
     * If this field is omitted, there are no subsequent pages.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     * @param value The nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageToken(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      nextPageToken_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * A token, which can be sent as `page_token` to retrieve the next page.
     * If this field is omitted, there are no subsequent pages.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearNextPageToken() {
      
      nextPageToken_ = getDefaultInstance().getNextPageToken();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * A token, which can be sent as `page_token` to retrieve the next page.
     * If this field is omitted, there are no subsequent pages.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     * @param value The bytes for nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageTokenBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      nextPageToken_ = value;
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


    // @@protoc_insertion_point(builder_scope:google.storage.v2.ListNotificationsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.storage.v2.ListNotificationsResponse)
  private static final com.google.storage.v2.ListNotificationsResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.storage.v2.ListNotificationsResponse();
  }

  public static com.google.storage.v2.ListNotificationsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ListNotificationsResponse>
      PARSER = new com.google.protobuf.AbstractParser<ListNotificationsResponse>() {
    @java.lang.Override
    public ListNotificationsResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ListNotificationsResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ListNotificationsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ListNotificationsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.v2.ListNotificationsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

