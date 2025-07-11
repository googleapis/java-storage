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
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.8
package com.google.storage.v2;

public interface QueryWriteStatusResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.QueryWriteStatusResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The total number of bytes that have been processed for the given object
   * from all `WriteObject` calls. This is the correct value for the
   * 'write_offset' field to use when resuming the `WriteObject` operation.
   * Only set if the upload has not finalized.
   * </pre>
   *
   * <code>int64 persisted_size = 1;</code>
   *
   * @return Whether the persistedSize field is set.
   */
  boolean hasPersistedSize();

  /**
   *
   *
   * <pre>
   * The total number of bytes that have been processed for the given object
   * from all `WriteObject` calls. This is the correct value for the
   * 'write_offset' field to use when resuming the `WriteObject` operation.
   * Only set if the upload has not finalized.
   * </pre>
   *
   * <code>int64 persisted_size = 1;</code>
   *
   * @return The persistedSize.
   */
  long getPersistedSize();

  /**
   *
   *
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   *
   * @return Whether the resource field is set.
   */
  boolean hasResource();

  /**
   *
   *
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   *
   * @return The resource.
   */
  com.google.storage.v2.Object getResource();

  /**
   *
   *
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   */
  com.google.storage.v2.ObjectOrBuilder getResourceOrBuilder();

  com.google.storage.v2.QueryWriteStatusResponse.WriteStatusCase getWriteStatusCase();
}
