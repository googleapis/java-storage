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

public interface GetBucketRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v1.GetBucketRequest)
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
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration matches the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_match = 2;</code>
   *
   * @return Whether the ifMetagenerationMatch field is set.
   */
  boolean hasIfMetagenerationMatch();
  /**
   *
   *
   * <pre>
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration matches the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_match = 2;</code>
   *
   * @return The ifMetagenerationMatch.
   */
  com.google.protobuf.Int64Value getIfMetagenerationMatch();
  /**
   *
   *
   * <pre>
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration matches the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_match = 2;</code>
   */
  com.google.protobuf.Int64ValueOrBuilder getIfMetagenerationMatchOrBuilder();

  /**
   *
   *
   * <pre>
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration does not match the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_not_match = 3;</code>
   *
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  boolean hasIfMetagenerationNotMatch();
  /**
   *
   *
   * <pre>
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration does not match the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_not_match = 3;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  com.google.protobuf.Int64Value getIfMetagenerationNotMatch();
  /**
   *
   *
   * <pre>
   * Makes the return of the bucket metadata conditional on whether the bucket's
   * current metageneration does not match the given value.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value if_metageneration_not_match = 3;</code>
   */
  com.google.protobuf.Int64ValueOrBuilder getIfMetagenerationNotMatchOrBuilder();

  /**
   *
   *
   * <pre>
   * Set of properties to return. Defaults to `NO_ACL`.
   * </pre>
   *
   * <code>.google.storage.v1.CommonEnums.Projection projection = 4;</code>
   *
   * @return The enum numeric value on the wire for projection.
   */
  int getProjectionValue();
  /**
   *
   *
   * <pre>
   * Set of properties to return. Defaults to `NO_ACL`.
   * </pre>
   *
   * <code>.google.storage.v1.CommonEnums.Projection projection = 4;</code>
   *
   * @return The projection.
   */
  com.google.storage.v1.CommonEnums.Projection getProjection();

  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 6;</code>
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
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 6;</code>
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
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 6;</code>
   */
  com.google.storage.v1.CommonRequestParamsOrBuilder getCommonRequestParamsOrBuilder();
}
