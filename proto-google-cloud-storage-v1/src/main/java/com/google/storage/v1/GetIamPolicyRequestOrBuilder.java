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

public interface GetIamPolicyRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v1.GetIamPolicyRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The request sent to IAM.
   * </pre>
   *
   * <code>.google.iam.v1.GetIamPolicyRequest iam_request = 1;</code>
   *
   * @return Whether the iamRequest field is set.
   */
  boolean hasIamRequest();
  /**
   *
   *
   * <pre>
   * The request sent to IAM.
   * </pre>
   *
   * <code>.google.iam.v1.GetIamPolicyRequest iam_request = 1;</code>
   *
   * @return The iamRequest.
   */
  com.google.iam.v1.GetIamPolicyRequest getIamRequest();
  /**
   *
   *
   * <pre>
   * The request sent to IAM.
   * </pre>
   *
   * <code>.google.iam.v1.GetIamPolicyRequest iam_request = 1;</code>
   */
  com.google.iam.v1.GetIamPolicyRequestOrBuilder getIamRequestOrBuilder();

  /**
   *
   *
   * <pre>
   * A set of parameters common to all Storage API requests.
   * </pre>
   *
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 2;</code>
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
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 2;</code>
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
   * <code>.google.storage.v1.CommonRequestParams common_request_params = 2;</code>
   */
  com.google.storage.v1.CommonRequestParamsOrBuilder getCommonRequestParamsOrBuilder();
}
