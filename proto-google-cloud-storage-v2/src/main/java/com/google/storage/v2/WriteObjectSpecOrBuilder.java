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
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

public interface WriteObjectSpecOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.WriteObjectSpec)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Destination object, including its name and its metadata.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 1;</code>
   *
   * @return Whether the resource field is set.
   */
  boolean hasResource();
  /**
   *
   *
   * <pre>
   * Destination object, including its name and its metadata.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 1;</code>
   *
   * @return The resource.
   */
  com.google.storage.v2.Object getResource();
  /**
   *
   *
   * <pre>
   * Destination object, including its name and its metadata.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 1;</code>
   */
  com.google.storage.v2.ObjectOrBuilder getResourceOrBuilder();

  /**
   *
   *
   * <pre>
   * Apply a predefined set of access controls to this object.
   * Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_acl = 7;</code>
   *
   * @return The predefinedAcl.
   */
  java.lang.String getPredefinedAcl();
  /**
   *
   *
   * <pre>
   * Apply a predefined set of access controls to this object.
   * Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string predefined_acl = 7;</code>
   *
   * @return The bytes for predefinedAcl.
   */
  com.google.protobuf.ByteString getPredefinedAclBytes();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current
   * generation matches the given value. Setting to 0 makes the operation
   * succeed only if there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 3;</code>
   *
   * @return Whether the ifGenerationMatch field is set.
   */
  boolean hasIfGenerationMatch();
  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current
   * generation matches the given value. Setting to 0 makes the operation
   * succeed only if there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 3;</code>
   *
   * @return The ifGenerationMatch.
   */
  long getIfGenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's live
   * generation does not match the given value. If no live object exists, the
   * precondition fails. Setting to 0 makes the operation succeed only if
   * there is a live version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 4;</code>
   *
   * @return Whether the ifGenerationNotMatch field is set.
   */
  boolean hasIfGenerationNotMatch();
  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's live
   * generation does not match the given value. If no live object exists, the
   * precondition fails. Setting to 0 makes the operation succeed only if
   * there is a live version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 4;</code>
   *
   * @return The ifGenerationNotMatch.
   */
  long getIfGenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 5;</code>
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
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 5;</code>
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
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 6;</code>
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
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 6;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();
}
