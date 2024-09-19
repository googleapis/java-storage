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

public interface UpdateBucketRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.UpdateBucketRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The bucket to update.
   * The bucket's `name` field will be used to identify the bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return Whether the bucket field is set.
   */
  boolean hasBucket();
  /**
   *
   *
   * <pre>
   * Required. The bucket to update.
   * The bucket's `name` field will be used to identify the bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bucket.
   */
  com.google.storage.v2.Bucket getBucket();
  /**
   *
   *
   * <pre>
   * Required. The bucket to update.
   * The bucket's `name` field will be used to identify the bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   */
  com.google.storage.v2.BucketOrBuilder getBucketOrBuilder();

  /**
   *
   *
   * <pre>
   * If set, will only modify the bucket if its metageneration matches this
   * value.
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
   * If set, will only modify the bucket if its metageneration matches this
   * value.
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
   * If set, will only modify the bucket if its metageneration does not match
   * this value.
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
   * If set, will only modify the bucket if its metageneration does not match
   * this value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 3;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Apply a predefined set of access controls to this bucket.
   * Valid values are "authenticatedRead", "private", "projectPrivate",
   * "publicRead", or "publicReadWrite".
   * </pre>
   *
   * <code>string predefined_acl = 8;</code>
   *
   * @return The predefinedAcl.
   */
  java.lang.String getPredefinedAcl();
  /**
   *
   *
   * <pre>
   * Apply a predefined set of access controls to this bucket.
   * Valid values are "authenticatedRead", "private", "projectPrivate",
   * "publicRead", or "publicReadWrite".
   * </pre>
   *
   * <code>string predefined_acl = 8;</code>
   *
   * @return The bytes for predefinedAcl.
   */
  com.google.protobuf.ByteString getPredefinedAclBytes();

  /**
   *
   *
   * <pre>
   * Apply a predefined set of default object access controls to this bucket.
   * Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_default_object_acl = 9;</code>
   *
   * @return The predefinedDefaultObjectAcl.
   */
  java.lang.String getPredefinedDefaultObjectAcl();
  /**
   *
   *
   * <pre>
   * Apply a predefined set of default object access controls to this bucket.
   * Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_default_object_acl = 9;</code>
   *
   * @return The bytes for predefinedDefaultObjectAcl.
   */
  com.google.protobuf.ByteString getPredefinedDefaultObjectAclBytes();

  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated.
   *
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 6 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the updateMask field is set.
   */
  boolean hasUpdateMask();
  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated.
   *
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 6 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The updateMask.
   */
  com.google.protobuf.FieldMask getUpdateMask();
  /**
   *
   *
   * <pre>
   * Required. List of fields to be updated.
   *
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   *
   * Not specifying any fields is an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 6 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.protobuf.FieldMaskOrBuilder getUpdateMaskOrBuilder();
}
