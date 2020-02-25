/*
 * Copyright 2020 Google LLC
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
// source: google/storage/v1/storage.proto

package com.google.storage.v1;

public interface UpdateObjectAccessControlRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v1.UpdateObjectAccessControlRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Name of a bucket.
   * </pre>
   *
   * <code>string bucket = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bucket.
   */
  java.lang.String getBucket();
  /**
   *
   *
   * <pre>
   * Required. Name of a bucket.
   * </pre>
   *
   * <code>string bucket = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for bucket.
   */
  com.google.protobuf.ByteString getBucketBytes();

  /**
   *
   *
   * <pre>
   * Required. The entity holding the permission. Can be one of:
   * * `user-`*userId*
   * * `user-`*emailAddress*
   * * `group-`*groupId*
   * * `group-`*emailAddress*
   * * `allUsers`
   * * `allAuthenticatedUsers`
   * </pre>
   *
   * <code>string entity = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The entity.
   */
  java.lang.String getEntity();
  /**
   *
   *
   * <pre>
   * Required. The entity holding the permission. Can be one of:
   * * `user-`*userId*
   * * `user-`*emailAddress*
   * * `group-`*groupId*
   * * `group-`*emailAddress*
   * * `allUsers`
   * * `allAuthenticatedUsers`
   * </pre>
   *
   * <code>string entity = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for entity.
   */
  com.google.protobuf.ByteString getEntityBytes();

  /**
   *
   *
   * <pre>
   * Required. Name of the object.
   * Required.
   * </pre>
   *
   * <code>string object = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The object.
   */
  java.lang.String getObject();
  /**
   *
   *
   * <pre>
   * Required. Name of the object.
   * Required.
   * </pre>
   *
   * <code>string object = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for object.
   */
  com.google.protobuf.ByteString getObjectBytes();

  /**
   *
   *
   * <pre>
   * If present, selects a specific revision of this object (as opposed to the
   * latest version, the default).
   * </pre>
   *
   * <code>int64 generation = 4;</code>
   *
   * @return The generation.
   */
  long getGeneration();

  /**
   *
   *
   * <pre>
   * The ObjectAccessControl for updating.
   * </pre>
   *
   * <code>.google.storage.v1.ObjectAccessControl object_access_control = 6;</code>
   *
   * @return Whether the objectAccessControl field is set.
   */
  boolean hasObjectAccessControl();
  /**
   *
   *
   * <pre>
   * The ObjectAccessControl for updating.
   * </pre>
   *
   * <code>.google.storage.v1.ObjectAccessControl object_access_control = 6;</code>
   *
   * @return The objectAccessControl.
   */
  com.google.storage.v1.ObjectAccessControl getObjectAccessControl();
  /**
   *
   *
   * <pre>
   * The ObjectAccessControl for updating.
   * </pre>
   *
   * <code>.google.storage.v1.ObjectAccessControl object_access_control = 6;</code>
   */
  com.google.storage.v1.ObjectAccessControlOrBuilder getObjectAccessControlOrBuilder();

  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 7;</code>
   *
   * @return Whether the commonRequestParams field is set.
   */
  boolean hasCommonRequestParams();
  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 7;</code>
   *
   * @return The commonRequestParams.
   */
  com.google.storage.v1.CommonRequestParams getCommonRequestParams();
  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 7;</code>
   */
  com.google.storage.v1.CommonRequestParamsOrBuilder getCommonRequestParamsOrBuilder();

  /**
   *
   *
   * <pre>
   * List of fields to be updated.
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   * Not specifying any fields is an error.
   * Not specifying a field while setting that field to a non-default value is
   * an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 8;</code>
   *
   * @return Whether the updateMask field is set.
   */
  boolean hasUpdateMask();
  /**
   *
   *
   * <pre>
   * List of fields to be updated.
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   * Not specifying any fields is an error.
   * Not specifying a field while setting that field to a non-default value is
   * an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 8;</code>
   *
   * @return The updateMask.
   */
  com.google.protobuf.FieldMask getUpdateMask();
  /**
   *
   *
   * <pre>
   * List of fields to be updated.
   * To specify ALL fields, equivalent to the JSON API's "update" function,
   * specify a single field with the value `*`. Note: not recommended. If a new
   * field is introduced at a later time, an older client updating with the `*`
   * may accidentally reset the new field's value.
   * Not specifying any fields is an error.
   * Not specifying a field while setting that field to a non-default value is
   * an error.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 8;</code>
   */
  com.google.protobuf.FieldMaskOrBuilder getUpdateMaskOrBuilder();
}
