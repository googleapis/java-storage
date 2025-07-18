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

public interface AppendObjectSpecOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.AppendObjectSpec)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The name of the bucket containing the object to write.
   * </pre>
   *
   * <code>
   * string bucket = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bucket.
   */
  java.lang.String getBucket();

  /**
   *
   *
   * <pre>
   * Required. The name of the bucket containing the object to write.
   * </pre>
   *
   * <code>
   * string bucket = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for bucket.
   */
  com.google.protobuf.ByteString getBucketBytes();

  /**
   *
   *
   * <pre>
   * Required. The name of the object to open for writing.
   * </pre>
   *
   * <code>string object = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The object.
   */
  java.lang.String getObject();

  /**
   *
   *
   * <pre>
   * Required. The name of the object to open for writing.
   * </pre>
   *
   * <code>string object = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for object.
   */
  com.google.protobuf.ByteString getObjectBytes();

  /**
   *
   *
   * <pre>
   * Required. The generation number of the object to open for writing.
   * </pre>
   *
   * <code>int64 generation = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The generation.
   */
  long getGeneration();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration matches the given value.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
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
   * Makes the operation conditional on whether the object's current
   * metageneration matches the given value.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
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
   * Makes the operation conditional on whether the object's current
   * metageneration does not match the given value.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
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
   * Makes the operation conditional on whether the object's current
   * metageneration does not match the given value.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
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
   * An optional routing token that influences request routing for the stream.
   * Must be provided if a BidiWriteObjectRedirectedError is returned.
   * </pre>
   *
   * <code>optional string routing_token = 6;</code>
   *
   * @return Whether the routingToken field is set.
   */
  boolean hasRoutingToken();

  /**
   *
   *
   * <pre>
   * An optional routing token that influences request routing for the stream.
   * Must be provided if a BidiWriteObjectRedirectedError is returned.
   * </pre>
   *
   * <code>optional string routing_token = 6;</code>
   *
   * @return The routingToken.
   */
  java.lang.String getRoutingToken();

  /**
   *
   *
   * <pre>
   * An optional routing token that influences request routing for the stream.
   * Must be provided if a BidiWriteObjectRedirectedError is returned.
   * </pre>
   *
   * <code>optional string routing_token = 6;</code>
   *
   * @return The bytes for routingToken.
   */
  com.google.protobuf.ByteString getRoutingTokenBytes();

  /**
   *
   *
   * <pre>
   * An optional write handle returned from a previous BidiWriteObjectResponse
   * message or a BidiWriteObjectRedirectedError error.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
   * </pre>
   *
   * <code>optional .google.storage.v2.BidiWriteHandle write_handle = 7;</code>
   *
   * @return Whether the writeHandle field is set.
   */
  boolean hasWriteHandle();

  /**
   *
   *
   * <pre>
   * An optional write handle returned from a previous BidiWriteObjectResponse
   * message or a BidiWriteObjectRedirectedError error.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
   * </pre>
   *
   * <code>optional .google.storage.v2.BidiWriteHandle write_handle = 7;</code>
   *
   * @return The writeHandle.
   */
  com.google.storage.v2.BidiWriteHandle getWriteHandle();

  /**
   *
   *
   * <pre>
   * An optional write handle returned from a previous BidiWriteObjectResponse
   * message or a BidiWriteObjectRedirectedError error.
   *
   * Note that metageneration preconditions are only checked if `write_handle`
   * is empty.
   * </pre>
   *
   * <code>optional .google.storage.v2.BidiWriteHandle write_handle = 7;</code>
   */
  com.google.storage.v2.BidiWriteHandleOrBuilder getWriteHandleOrBuilder();
}
