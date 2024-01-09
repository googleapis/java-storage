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
// source: google/storage/control/v2/storage_control.proto

package com.google.storage.control.v2;

public interface RenameFolderRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.control.v2.RenameFolderRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Name of the source folder being renamed.
   * Format: `projects/{project}/buckets/{bucket}/folders/{folder}`
   * </pre>
   *
   * <code>
   * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The name.
   */
  java.lang.String getName();
  /**
   *
   *
   * <pre>
   * Required. Name of the source folder being renamed.
   * Format: `projects/{project}/buckets/{bucket}/folders/{folder}`
   * </pre>
   *
   * <code>
   * string name = 7 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * Required. The destination folder ID, e.g. `foo/bar/`.
   * </pre>
   *
   * <code>string destination_folder_id = 8 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The destinationFolderId.
   */
  java.lang.String getDestinationFolderId();
  /**
   *
   *
   * <pre>
   * Required. The destination folder ID, e.g. `foo/bar/`.
   * </pre>
   *
   * <code>string destination_folder_id = 8 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for destinationFolderId.
   */
  com.google.protobuf.ByteString getDestinationFolderIdBytes();

  /**
   *
   *
   * <pre>
   * Makes the operation only succeed conditional on whether the source
   * folder's current metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 4;</code>
   *
   * @return Whether the ifMetagenerationMatch field is set.
   */
  boolean hasIfMetagenerationMatch();
  /**
   *
   *
   * <pre>
   * Makes the operation only succeed conditional on whether the source
   * folder's current metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 4;</code>
   *
   * @return The ifMetagenerationMatch.
   */
  long getIfMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation only succeed conditional on whether the source
   * folder's current metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 5;</code>
   *
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  boolean hasIfMetagenerationNotMatch();
  /**
   *
   *
   * <pre>
   * Makes the operation only succeed conditional on whether the source
   * folder's current metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 5;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted. This request is only
   * idempotent if a `request_id` is provided.
   * </pre>
   *
   * <code>
   * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
   * </code>
   *
   * @return The requestId.
   */
  java.lang.String getRequestId();
  /**
   *
   *
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted. This request is only
   * idempotent if a `request_id` is provided.
   * </pre>
   *
   * <code>
   * string request_id = 6 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
   * </code>
   *
   * @return The bytes for requestId.
   */
  com.google.protobuf.ByteString getRequestIdBytes();
}
