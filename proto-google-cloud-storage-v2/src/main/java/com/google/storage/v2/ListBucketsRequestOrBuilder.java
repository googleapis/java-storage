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
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.4
package com.google.storage.v2;

public interface ListBucketsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.ListBucketsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The project whose buckets we are listing.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The parent.
   */
  java.lang.String getParent();
  /**
   *
   *
   * <pre>
   * Required. The project whose buckets we are listing.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for parent.
   */
  com.google.protobuf.ByteString getParentBytes();

  /**
   *
   *
   * <pre>
   * Maximum number of buckets to return in a single response. The service will
   * use this parameter or 1,000 items, whichever is smaller. If "acl" is
   * present in the read_mask, the service will use this parameter of 200 items,
   * whichever is smaller.
   * </pre>
   *
   * <code>int32 page_size = 2;</code>
   *
   * @return The pageSize.
   */
  int getPageSize();

  /**
   *
   *
   * <pre>
   * A previously-returned page token representing part of the larger set of
   * results to view.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The pageToken.
   */
  java.lang.String getPageToken();
  /**
   *
   *
   * <pre>
   * A previously-returned page token representing part of the larger set of
   * results to view.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The bytes for pageToken.
   */
  com.google.protobuf.ByteString getPageTokenBytes();

  /**
   *
   *
   * <pre>
   * Filter results to buckets whose names begin with this prefix.
   * </pre>
   *
   * <code>string prefix = 4;</code>
   *
   * @return The prefix.
   */
  java.lang.String getPrefix();
  /**
   *
   *
   * <pre>
   * Filter results to buckets whose names begin with this prefix.
   * </pre>
   *
   * <code>string prefix = 4;</code>
   *
   * @return The bytes for prefix.
   */
  com.google.protobuf.ByteString getPrefixBytes();

  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.owner,
   * items.acl, and items.default_object_acl.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 5;</code>
   *
   * @return Whether the readMask field is set.
   */
  boolean hasReadMask();
  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.owner,
   * items.acl, and items.default_object_acl.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 5;</code>
   *
   * @return The readMask.
   */
  com.google.protobuf.FieldMask getReadMask();
  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.owner,
   * items.acl, and items.default_object_acl.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 5;</code>
   */
  com.google.protobuf.FieldMaskOrBuilder getReadMaskOrBuilder();
}
