/*
 * Copyright 2024 Google LLC
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

// Protobuf Java Version: 3.25.4
package com.google.storage.control.v2;

/**
 *
 *
 * <pre>
 * Response message for ListFolders.
 * </pre>
 *
 * Protobuf type {@code google.storage.control.v2.ListFoldersResponse}
 */
public final class ListFoldersResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.storage.control.v2.ListFoldersResponse)
    ListFoldersResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ListFoldersResponse.newBuilder() to construct.
  private ListFoldersResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ListFoldersResponse() {
    folders_ = java.util.Collections.emptyList();
    nextPageToken_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ListFoldersResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.storage.control.v2.StorageControlProto
        .internal_static_google_storage_control_v2_ListFoldersResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.storage.control.v2.StorageControlProto
        .internal_static_google_storage_control_v2_ListFoldersResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.storage.control.v2.ListFoldersResponse.class,
            com.google.storage.control.v2.ListFoldersResponse.Builder.class);
  }

  public static final int FOLDERS_FIELD_NUMBER = 1;

  @SuppressWarnings("serial")
  private java.util.List<com.google.storage.control.v2.Folder> folders_;
  /**
   *
   *
   * <pre>
   * The list of child folders
   * </pre>
   *
   * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
   */
  @java.lang.Override
  public java.util.List<com.google.storage.control.v2.Folder> getFoldersList() {
    return folders_;
  }
  /**
   *
   *
   * <pre>
   * The list of child folders
   * </pre>
   *
   * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.google.storage.control.v2.FolderOrBuilder>
      getFoldersOrBuilderList() {
    return folders_;
  }
  /**
   *
   *
   * <pre>
   * The list of child folders
   * </pre>
   *
   * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
   */
  @java.lang.Override
  public int getFoldersCount() {
    return folders_.size();
  }
  /**
   *
   *
   * <pre>
   * The list of child folders
   * </pre>
   *
   * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.control.v2.Folder getFolders(int index) {
    return folders_.get(index);
  }
  /**
   *
   *
   * <pre>
   * The list of child folders
   * </pre>
   *
   * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
   */
  @java.lang.Override
  public com.google.storage.control.v2.FolderOrBuilder getFoldersOrBuilder(int index) {
    return folders_.get(index);
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
    for (int i = 0; i < folders_.size(); i++) {
      output.writeMessage(1, folders_.get(i));
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
    for (int i = 0; i < folders_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, folders_.get(i));
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
    if (!(obj instanceof com.google.storage.control.v2.ListFoldersResponse)) {
      return super.equals(obj);
    }
    com.google.storage.control.v2.ListFoldersResponse other =
        (com.google.storage.control.v2.ListFoldersResponse) obj;

    if (!getFoldersList().equals(other.getFoldersList())) return false;
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
    if (getFoldersCount() > 0) {
      hash = (37 * hash) + FOLDERS_FIELD_NUMBER;
      hash = (53 * hash) + getFoldersList().hashCode();
    }
    hash = (37 * hash) + NEXT_PAGE_TOKEN_FIELD_NUMBER;
    hash = (53 * hash) + getNextPageToken().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.storage.control.v2.ListFoldersResponse parseFrom(
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

  public static Builder newBuilder(com.google.storage.control.v2.ListFoldersResponse prototype) {
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
   * Response message for ListFolders.
   * </pre>
   *
   * Protobuf type {@code google.storage.control.v2.ListFoldersResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.storage.control.v2.ListFoldersResponse)
      com.google.storage.control.v2.ListFoldersResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_ListFoldersResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_ListFoldersResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.storage.control.v2.ListFoldersResponse.class,
              com.google.storage.control.v2.ListFoldersResponse.Builder.class);
    }

    // Construct using com.google.storage.control.v2.ListFoldersResponse.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (foldersBuilder_ == null) {
        folders_ = java.util.Collections.emptyList();
      } else {
        folders_ = null;
        foldersBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      nextPageToken_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.storage.control.v2.StorageControlProto
          .internal_static_google_storage_control_v2_ListFoldersResponse_descriptor;
    }

    @java.lang.Override
    public com.google.storage.control.v2.ListFoldersResponse getDefaultInstanceForType() {
      return com.google.storage.control.v2.ListFoldersResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.storage.control.v2.ListFoldersResponse build() {
      com.google.storage.control.v2.ListFoldersResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.storage.control.v2.ListFoldersResponse buildPartial() {
      com.google.storage.control.v2.ListFoldersResponse result =
          new com.google.storage.control.v2.ListFoldersResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(
        com.google.storage.control.v2.ListFoldersResponse result) {
      if (foldersBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          folders_ = java.util.Collections.unmodifiableList(folders_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.folders_ = folders_;
      } else {
        result.folders_ = foldersBuilder_.build();
      }
    }

    private void buildPartial0(com.google.storage.control.v2.ListFoldersResponse result) {
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
      if (other instanceof com.google.storage.control.v2.ListFoldersResponse) {
        return mergeFrom((com.google.storage.control.v2.ListFoldersResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.storage.control.v2.ListFoldersResponse other) {
      if (other == com.google.storage.control.v2.ListFoldersResponse.getDefaultInstance())
        return this;
      if (foldersBuilder_ == null) {
        if (!other.folders_.isEmpty()) {
          if (folders_.isEmpty()) {
            folders_ = other.folders_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureFoldersIsMutable();
            folders_.addAll(other.folders_);
          }
          onChanged();
        }
      } else {
        if (!other.folders_.isEmpty()) {
          if (foldersBuilder_.isEmpty()) {
            foldersBuilder_.dispose();
            foldersBuilder_ = null;
            folders_ = other.folders_;
            bitField0_ = (bitField0_ & ~0x00000001);
            foldersBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders
                    ? getFoldersFieldBuilder()
                    : null;
          } else {
            foldersBuilder_.addAllMessages(other.folders_);
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
                com.google.storage.control.v2.Folder m =
                    input.readMessage(
                        com.google.storage.control.v2.Folder.parser(), extensionRegistry);
                if (foldersBuilder_ == null) {
                  ensureFoldersIsMutable();
                  folders_.add(m);
                } else {
                  foldersBuilder_.addMessage(m);
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

    private java.util.List<com.google.storage.control.v2.Folder> folders_ =
        java.util.Collections.emptyList();

    private void ensureFoldersIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        folders_ = new java.util.ArrayList<com.google.storage.control.v2.Folder>(folders_);
        bitField0_ |= 0x00000001;
      }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.storage.control.v2.Folder,
            com.google.storage.control.v2.Folder.Builder,
            com.google.storage.control.v2.FolderOrBuilder>
        foldersBuilder_;

    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public java.util.List<com.google.storage.control.v2.Folder> getFoldersList() {
      if (foldersBuilder_ == null) {
        return java.util.Collections.unmodifiableList(folders_);
      } else {
        return foldersBuilder_.getMessageList();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public int getFoldersCount() {
      if (foldersBuilder_ == null) {
        return folders_.size();
      } else {
        return foldersBuilder_.getCount();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public com.google.storage.control.v2.Folder getFolders(int index) {
      if (foldersBuilder_ == null) {
        return folders_.get(index);
      } else {
        return foldersBuilder_.getMessage(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder setFolders(int index, com.google.storage.control.v2.Folder value) {
      if (foldersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFoldersIsMutable();
        folders_.set(index, value);
        onChanged();
      } else {
        foldersBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder setFolders(
        int index, com.google.storage.control.v2.Folder.Builder builderForValue) {
      if (foldersBuilder_ == null) {
        ensureFoldersIsMutable();
        folders_.set(index, builderForValue.build());
        onChanged();
      } else {
        foldersBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder addFolders(com.google.storage.control.v2.Folder value) {
      if (foldersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFoldersIsMutable();
        folders_.add(value);
        onChanged();
      } else {
        foldersBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder addFolders(int index, com.google.storage.control.v2.Folder value) {
      if (foldersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFoldersIsMutable();
        folders_.add(index, value);
        onChanged();
      } else {
        foldersBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder addFolders(com.google.storage.control.v2.Folder.Builder builderForValue) {
      if (foldersBuilder_ == null) {
        ensureFoldersIsMutable();
        folders_.add(builderForValue.build());
        onChanged();
      } else {
        foldersBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder addFolders(
        int index, com.google.storage.control.v2.Folder.Builder builderForValue) {
      if (foldersBuilder_ == null) {
        ensureFoldersIsMutable();
        folders_.add(index, builderForValue.build());
        onChanged();
      } else {
        foldersBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder addAllFolders(
        java.lang.Iterable<? extends com.google.storage.control.v2.Folder> values) {
      if (foldersBuilder_ == null) {
        ensureFoldersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, folders_);
        onChanged();
      } else {
        foldersBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder clearFolders() {
      if (foldersBuilder_ == null) {
        folders_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        foldersBuilder_.clear();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public Builder removeFolders(int index) {
      if (foldersBuilder_ == null) {
        ensureFoldersIsMutable();
        folders_.remove(index);
        onChanged();
      } else {
        foldersBuilder_.remove(index);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public com.google.storage.control.v2.Folder.Builder getFoldersBuilder(int index) {
      return getFoldersFieldBuilder().getBuilder(index);
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public com.google.storage.control.v2.FolderOrBuilder getFoldersOrBuilder(int index) {
      if (foldersBuilder_ == null) {
        return folders_.get(index);
      } else {
        return foldersBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public java.util.List<? extends com.google.storage.control.v2.FolderOrBuilder>
        getFoldersOrBuilderList() {
      if (foldersBuilder_ != null) {
        return foldersBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(folders_);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public com.google.storage.control.v2.Folder.Builder addFoldersBuilder() {
      return getFoldersFieldBuilder()
          .addBuilder(com.google.storage.control.v2.Folder.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public com.google.storage.control.v2.Folder.Builder addFoldersBuilder(int index) {
      return getFoldersFieldBuilder()
          .addBuilder(index, com.google.storage.control.v2.Folder.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of child folders
     * </pre>
     *
     * <code>repeated .google.storage.control.v2.Folder folders = 1;</code>
     */
    public java.util.List<com.google.storage.control.v2.Folder.Builder> getFoldersBuilderList() {
      return getFoldersFieldBuilder().getBuilderList();
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.storage.control.v2.Folder,
            com.google.storage.control.v2.Folder.Builder,
            com.google.storage.control.v2.FolderOrBuilder>
        getFoldersFieldBuilder() {
      if (foldersBuilder_ == null) {
        foldersBuilder_ =
            new com.google.protobuf.RepeatedFieldBuilderV3<
                com.google.storage.control.v2.Folder,
                com.google.storage.control.v2.Folder.Builder,
                com.google.storage.control.v2.FolderOrBuilder>(
                folders_, ((bitField0_ & 0x00000001) != 0), getParentForChildren(), isClean());
        folders_ = null;
      }
      return foldersBuilder_;
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

    // @@protoc_insertion_point(builder_scope:google.storage.control.v2.ListFoldersResponse)
  }

  // @@protoc_insertion_point(class_scope:google.storage.control.v2.ListFoldersResponse)
  private static final com.google.storage.control.v2.ListFoldersResponse DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.storage.control.v2.ListFoldersResponse();
  }

  public static com.google.storage.control.v2.ListFoldersResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ListFoldersResponse> PARSER =
      new com.google.protobuf.AbstractParser<ListFoldersResponse>() {
        @java.lang.Override
        public ListFoldersResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<ListFoldersResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ListFoldersResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.storage.control.v2.ListFoldersResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
