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

// Protobuf Java Version: 3.25.8
package com.google.storage.control.v2;

public interface RenameFolderMetadataOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.control.v2.RenameFolderMetadata)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Generic metadata for the long running operation.
   * </pre>
   *
   * <code>.google.storage.control.v2.CommonLongRunningOperationMetadata common_metadata = 1;</code>
   *
   * @return Whether the commonMetadata field is set.
   */
  boolean hasCommonMetadata();

  /**
   *
   *
   * <pre>
   * Generic metadata for the long running operation.
   * </pre>
   *
   * <code>.google.storage.control.v2.CommonLongRunningOperationMetadata common_metadata = 1;</code>
   *
   * @return The commonMetadata.
   */
  com.google.storage.control.v2.CommonLongRunningOperationMetadata getCommonMetadata();

  /**
   *
   *
   * <pre>
   * Generic metadata for the long running operation.
   * </pre>
   *
   * <code>.google.storage.control.v2.CommonLongRunningOperationMetadata common_metadata = 1;</code>
   */
  com.google.storage.control.v2.CommonLongRunningOperationMetadataOrBuilder
      getCommonMetadataOrBuilder();

  /**
   *
   *
   * <pre>
   * The path of the source folder.
   * </pre>
   *
   * <code>string source_folder_id = 2;</code>
   *
   * @return The sourceFolderId.
   */
  java.lang.String getSourceFolderId();

  /**
   *
   *
   * <pre>
   * The path of the source folder.
   * </pre>
   *
   * <code>string source_folder_id = 2;</code>
   *
   * @return The bytes for sourceFolderId.
   */
  com.google.protobuf.ByteString getSourceFolderIdBytes();

  /**
   *
   *
   * <pre>
   * The path of the destination folder.
   * </pre>
   *
   * <code>string destination_folder_id = 3;</code>
   *
   * @return The destinationFolderId.
   */
  java.lang.String getDestinationFolderId();

  /**
   *
   *
   * <pre>
   * The path of the destination folder.
   * </pre>
   *
   * <code>string destination_folder_id = 3;</code>
   *
   * @return The bytes for destinationFolderId.
   */
  com.google.protobuf.ByteString getDestinationFolderIdBytes();
}
