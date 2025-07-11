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

public interface CreateBucketRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CreateBucketRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The project to which this bucket will belong. This field must
   * either be empty or `projects/_`. The project ID that owns this bucket
   * should be specified in the `bucket.project` field.
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
   * Required. The project to which this bucket will belong. This field must
   * either be empty or `projects/_`. The project ID that owns this bucket
   * should be specified in the `bucket.project` field.
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
   * Optional. Properties of the new bucket being inserted.
   * The name of the bucket is specified in the `bucket_id` field. Populating
   * `bucket.name` field will result in an error.
   * The project of the bucket must be specified in the `bucket.project` field.
   * This field must be in `projects/{projectIdentifier}` format,
   * {projectIdentifier} can be the project ID or project number. The `parent`
   * field must be either empty or `projects/_`.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 2 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return Whether the bucket field is set.
   */
  boolean hasBucket();

  /**
   *
   *
   * <pre>
   * Optional. Properties of the new bucket being inserted.
   * The name of the bucket is specified in the `bucket_id` field. Populating
   * `bucket.name` field will result in an error.
   * The project of the bucket must be specified in the `bucket.project` field.
   * This field must be in `projects/{projectIdentifier}` format,
   * {projectIdentifier} can be the project ID or project number. The `parent`
   * field must be either empty or `projects/_`.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 2 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bucket.
   */
  com.google.storage.v2.Bucket getBucket();

  /**
   *
   *
   * <pre>
   * Optional. Properties of the new bucket being inserted.
   * The name of the bucket is specified in the `bucket_id` field. Populating
   * `bucket.name` field will result in an error.
   * The project of the bucket must be specified in the `bucket.project` field.
   * This field must be in `projects/{projectIdentifier}` format,
   * {projectIdentifier} can be the project ID or project number. The `parent`
   * field must be either empty or `projects/_`.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket bucket = 2 [(.google.api.field_behavior) = OPTIONAL];</code>
   */
  com.google.storage.v2.BucketOrBuilder getBucketOrBuilder();

  /**
   *
   *
   * <pre>
   * Required. The ID to use for this bucket, which will become the final
   * component of the bucket's resource name. For example, the value `foo` might
   * result in a bucket with the name `projects/123456/buckets/foo`.
   * </pre>
   *
   * <code>string bucket_id = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bucketId.
   */
  java.lang.String getBucketId();

  /**
   *
   *
   * <pre>
   * Required. The ID to use for this bucket, which will become the final
   * component of the bucket's resource name. For example, the value `foo` might
   * result in a bucket with the name `projects/123456/buckets/foo`.
   * </pre>
   *
   * <code>string bucket_id = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for bucketId.
   */
  com.google.protobuf.ByteString getBucketIdBytes();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of access controls to this bucket.
   * Valid values are "authenticatedRead", "private", "projectPrivate",
   * "publicRead", or "publicReadWrite".
   * </pre>
   *
   * <code>string predefined_acl = 6 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The predefinedAcl.
   */
  java.lang.String getPredefinedAcl();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of access controls to this bucket.
   * Valid values are "authenticatedRead", "private", "projectPrivate",
   * "publicRead", or "publicReadWrite".
   * </pre>
   *
   * <code>string predefined_acl = 6 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for predefinedAcl.
   */
  com.google.protobuf.ByteString getPredefinedAclBytes();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of default object access controls to this
   * bucket. Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_default_object_acl = 7 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The predefinedDefaultObjectAcl.
   */
  java.lang.String getPredefinedDefaultObjectAcl();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of default object access controls to this
   * bucket. Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_default_object_acl = 7 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The bytes for predefinedDefaultObjectAcl.
   */
  com.google.protobuf.ByteString getPredefinedDefaultObjectAclBytes();

  /**
   *
   *
   * <pre>
   * Optional. If true, enable object retention on the bucket.
   * </pre>
   *
   * <code>bool enable_object_retention = 9 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The enableObjectRetention.
   */
  boolean getEnableObjectRetention();
}
