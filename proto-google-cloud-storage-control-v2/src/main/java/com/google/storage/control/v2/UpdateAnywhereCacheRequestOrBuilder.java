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

// Protobuf Java Version: 3.25.5
package com.google.storage.control.v2;

public interface UpdateAnywhereCacheRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.control.v2.UpdateAnywhereCacheRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The Anywhere Cache instance to be updated.
   * </pre>
   *
   * <code>
   * .google.storage.control.v2.AnywhereCache anywhere_cache = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the anywhereCache field is set.
   */
  boolean hasAnywhereCache();
  /**
   *
   *
   * <pre>
   * Required. The Anywhere Cache instance to be updated.
   * </pre>
   *
   * <code>
   * .google.storage.control.v2.AnywhereCache anywhere_cache = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The anywhereCache.
   */
  com.google.storage.control.v2.AnywhereCache getAnywhereCache();
  /**
   *
   *
   * <pre>
   * Required. The Anywhere Cache instance to be updated.
   * </pre>
   *
   * <code>
   * .google.storage.control.v2.AnywhereCache anywhere_cache = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.storage.control.v2.AnywhereCacheOrBuilder getAnywhereCacheOrBuilder();

  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated. Mutable fields of AnywhereCache
   * include `ttl` and `admission_policy`.
   *
   * To specify ALL fields, specify a single field with the value `*`. Note: We
   * recommend against doing this. If a new field is introduced at a later time,
   * an older client updating with the `*` may accidentally reset the new
   * field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the updateMask field is set.
   */
  boolean hasUpdateMask();
  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated. Mutable fields of AnywhereCache
   * include `ttl` and `admission_policy`.
   *
   * To specify ALL fields, specify a single field with the value `*`. Note: We
   * recommend against doing this. If a new field is introduced at a later time,
   * an older client updating with the `*` may accidentally reset the new
   * field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The updateMask.
   */
  com.google.protobuf.FieldMask getUpdateMask();
  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated. Mutable fields of AnywhereCache
   * include `ttl` and `admission_policy`.
   *
   * To specify ALL fields, specify a single field with the value `*`. Note: We
   * recommend against doing this. If a new field is introduced at a later time,
   * an older client updating with the `*` may accidentally reset the new
   * field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.protobuf.FieldMaskOrBuilder getUpdateMaskOrBuilder();

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
   * string request_id = 3 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
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
   * string request_id = 3 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }
   * </code>
   *
   * @return The bytes for requestId.
   */
  com.google.protobuf.ByteString getRequestIdBytes();
}
