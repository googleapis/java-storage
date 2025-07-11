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

public interface DeleteBucketRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.DeleteBucketRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Name of a bucket to delete.
   * </pre>
   *
   * <code>
   * string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The name.
   */
  java.lang.String getName();

  /**
   *
   *
   * <pre>
   * Required. Name of a bucket to delete.
   * </pre>
   *
   * <code>
   * string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * If set, only deletes the bucket if its metageneration matches this value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 2;</code>
   *
   * @return Whether the ifMetagenerationMatch field is set.
   */
  boolean hasIfMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * If set, only deletes the bucket if its metageneration matches this value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 2;</code>
   *
   * @return The ifMetagenerationMatch.
   */
  long getIfMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * If set, only deletes the bucket if its metageneration does not match this
   * value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 3;</code>
   *
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  boolean hasIfMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * If set, only deletes the bucket if its metageneration does not match this
   * value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 3;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();
}
