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

public interface RewriteObjectRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.RewriteObjectRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Immutable. The name of the destination object.
   * See the
   * [Naming Guidelines](https://cloud.google.com/storage/docs/objects#naming).
   * Example: `test.txt`
   * The `name` field by itself does not uniquely identify a Cloud Storage
   * object. A Cloud Storage object is uniquely identified by the tuple of
   * (bucket, object, generation).
   * </pre>
   *
   * <code>
   * string destination_name = 24 [(.google.api.field_behavior) = REQUIRED, (.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The destinationName.
   */
  java.lang.String getDestinationName();

  /**
   *
   *
   * <pre>
   * Required. Immutable. The name of the destination object.
   * See the
   * [Naming Guidelines](https://cloud.google.com/storage/docs/objects#naming).
   * Example: `test.txt`
   * The `name` field by itself does not uniquely identify a Cloud Storage
   * object. A Cloud Storage object is uniquely identified by the tuple of
   * (bucket, object, generation).
   * </pre>
   *
   * <code>
   * string destination_name = 24 [(.google.api.field_behavior) = REQUIRED, (.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The bytes for destinationName.
   */
  com.google.protobuf.ByteString getDestinationNameBytes();

  /**
   *
   *
   * <pre>
   * Required. Immutable. The name of the bucket containing the destination
   * object.
   * </pre>
   *
   * <code>
   * string destination_bucket = 25 [(.google.api.field_behavior) = REQUIRED, (.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The destinationBucket.
   */
  java.lang.String getDestinationBucket();

  /**
   *
   *
   * <pre>
   * Required. Immutable. The name of the bucket containing the destination
   * object.
   * </pre>
   *
   * <code>
   * string destination_bucket = 25 [(.google.api.field_behavior) = REQUIRED, (.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for destinationBucket.
   */
  com.google.protobuf.ByteString getDestinationBucketBytes();

  /**
   *
   *
   * <pre>
   * Optional. The name of the Cloud KMS key that will be used to encrypt the
   * destination object. The Cloud KMS key must be located in same location as
   * the object. If the parameter is not specified, the request uses the
   * destination bucket's default encryption key, if any, or else the
   * Google-managed encryption key.
   * </pre>
   *
   * <code>
   * string destination_kms_key = 27 [(.google.api.field_behavior) = OPTIONAL, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The destinationKmsKey.
   */
  java.lang.String getDestinationKmsKey();

  /**
   *
   *
   * <pre>
   * Optional. The name of the Cloud KMS key that will be used to encrypt the
   * destination object. The Cloud KMS key must be located in same location as
   * the object. If the parameter is not specified, the request uses the
   * destination bucket's default encryption key, if any, or else the
   * Google-managed encryption key.
   * </pre>
   *
   * <code>
   * string destination_kms_key = 27 [(.google.api.field_behavior) = OPTIONAL, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for destinationKmsKey.
   */
  com.google.protobuf.ByteString getDestinationKmsKeyBytes();

  /**
   *
   *
   * <pre>
   * Optional. Properties of the destination, post-rewrite object.
   * The `name`, `bucket` and `kms_key` fields must not be populated (these
   * values are specified in the `destination_name`, `destination_bucket`, and
   * `destination_kms_key` fields).
   * If `destination` is present it will be used to construct the destination
   * object's metadata; otherwise the destination object's metadata will be
   * copied from the source object.
   * </pre>
   *
   * <code>.google.storage.v2.Object destination = 1 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the destination field is set.
   */
  boolean hasDestination();

  /**
   *
   *
   * <pre>
   * Optional. Properties of the destination, post-rewrite object.
   * The `name`, `bucket` and `kms_key` fields must not be populated (these
   * values are specified in the `destination_name`, `destination_bucket`, and
   * `destination_kms_key` fields).
   * If `destination` is present it will be used to construct the destination
   * object's metadata; otherwise the destination object's metadata will be
   * copied from the source object.
   * </pre>
   *
   * <code>.google.storage.v2.Object destination = 1 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The destination.
   */
  com.google.storage.v2.Object getDestination();

  /**
   *
   *
   * <pre>
   * Optional. Properties of the destination, post-rewrite object.
   * The `name`, `bucket` and `kms_key` fields must not be populated (these
   * values are specified in the `destination_name`, `destination_bucket`, and
   * `destination_kms_key` fields).
   * If `destination` is present it will be used to construct the destination
   * object's metadata; otherwise the destination object's metadata will be
   * copied from the source object.
   * </pre>
   *
   * <code>.google.storage.v2.Object destination = 1 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.storage.v2.ObjectOrBuilder getDestinationOrBuilder();

  /**
   *
   *
   * <pre>
   * Required. Name of the bucket in which to find the source object.
   * </pre>
   *
   * <code>
   * string source_bucket = 2 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The sourceBucket.
   */
  java.lang.String getSourceBucket();

  /**
   *
   *
   * <pre>
   * Required. Name of the bucket in which to find the source object.
   * </pre>
   *
   * <code>
   * string source_bucket = 2 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for sourceBucket.
   */
  com.google.protobuf.ByteString getSourceBucketBytes();

  /**
   *
   *
   * <pre>
   * Required. Name of the source object.
   * </pre>
   *
   * <code>string source_object = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The sourceObject.
   */
  java.lang.String getSourceObject();

  /**
   *
   *
   * <pre>
   * Required. Name of the source object.
   * </pre>
   *
   * <code>string source_object = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for sourceObject.
   */
  com.google.protobuf.ByteString getSourceObjectBytes();

  /**
   *
   *
   * <pre>
   * Optional. If present, selects a specific revision of the source object (as
   * opposed to the latest version, the default).
   * </pre>
   *
   * <code>int64 source_generation = 4 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The sourceGeneration.
   */
  long getSourceGeneration();

  /**
   *
   *
   * <pre>
   * Optional. Include this field (from the previous rewrite response) on each
   * rewrite request after the first one, until the rewrite response 'done' flag
   * is true. Calls that provide a rewriteToken can omit all other request
   * fields, but if included those fields must match the values provided in the
   * first rewrite request.
   * </pre>
   *
   * <code>string rewrite_token = 5 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The rewriteToken.
   */
  java.lang.String getRewriteToken();

  /**
   *
   *
   * <pre>
   * Optional. Include this field (from the previous rewrite response) on each
   * rewrite request after the first one, until the rewrite response 'done' flag
   * is true. Calls that provide a rewriteToken can omit all other request
   * fields, but if included those fields must match the values provided in the
   * first rewrite request.
   * </pre>
   *
   * <code>string rewrite_token = 5 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for rewriteToken.
   */
  com.google.protobuf.ByteString getRewriteTokenBytes();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of access controls to the destination
   * object. Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string destination_predefined_acl = 28 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The destinationPredefinedAcl.
   */
  java.lang.String getDestinationPredefinedAcl();

  /**
   *
   *
   * <pre>
   * Optional. Apply a predefined set of access controls to the destination
   * object. Valid values are "authenticatedRead", "bucketOwnerFullControl",
   * "bucketOwnerRead", "private", "projectPrivate", or "publicRead".
   * </pre>
   *
   * <code>string destination_predefined_acl = 28 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for destinationPredefinedAcl.
   */
  com.google.protobuf.ByteString getDestinationPredefinedAclBytes();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current generation
   * matches the given value. Setting to 0 makes the operation succeed only if
   * there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 7;</code>
   *
   * @return Whether the ifGenerationMatch field is set.
   */
  boolean hasIfGenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's current generation
   * matches the given value. Setting to 0 makes the operation succeed only if
   * there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 7;</code>
   *
   * @return The ifGenerationMatch.
   */
  long getIfGenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's live generation
   * does not match the given value. If no live object exists, the precondition
   * fails. Setting to 0 makes the operation succeed only if there is a live
   * version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 8;</code>
   *
   * @return Whether the ifGenerationNotMatch field is set.
   */
  boolean hasIfGenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the object's live generation
   * does not match the given value. If no live object exists, the precondition
   * fails. Setting to 0 makes the operation succeed only if there is a live
   * version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 8;</code>
   *
   * @return The ifGenerationNotMatch.
   */
  long getIfGenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the destination object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 9;</code>
   *
   * @return Whether the ifMetagenerationMatch field is set.
   */
  boolean hasIfMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the destination object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 9;</code>
   *
   * @return The ifMetagenerationMatch.
   */
  long getIfMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the destination object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 10;</code>
   *
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  boolean hasIfMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the destination object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 10;</code>
   *
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's live
   * generation matches the given value.
   * </pre>
   *
   * <code>optional int64 if_source_generation_match = 11;</code>
   *
   * @return Whether the ifSourceGenerationMatch field is set.
   */
  boolean hasIfSourceGenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's live
   * generation matches the given value.
   * </pre>
   *
   * <code>optional int64 if_source_generation_match = 11;</code>
   *
   * @return The ifSourceGenerationMatch.
   */
  long getIfSourceGenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's live
   * generation does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_source_generation_not_match = 12;</code>
   *
   * @return Whether the ifSourceGenerationNotMatch field is set.
   */
  boolean hasIfSourceGenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's live
   * generation does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_source_generation_not_match = 12;</code>
   *
   * @return The ifSourceGenerationNotMatch.
   */
  long getIfSourceGenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_source_metageneration_match = 13;</code>
   *
   * @return Whether the ifSourceMetagenerationMatch field is set.
   */
  boolean hasIfSourceMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_source_metageneration_match = 13;</code>
   *
   * @return The ifSourceMetagenerationMatch.
   */
  long getIfSourceMetagenerationMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_source_metageneration_not_match = 14;</code>
   *
   * @return Whether the ifSourceMetagenerationNotMatch field is set.
   */
  boolean hasIfSourceMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Makes the operation conditional on whether the source object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_source_metageneration_not_match = 14;</code>
   *
   * @return The ifSourceMetagenerationNotMatch.
   */
  long getIfSourceMetagenerationNotMatch();

  /**
   *
   *
   * <pre>
   * Optional. The maximum number of bytes that will be rewritten per rewrite
   * request. Most callers shouldn't need to specify this parameter - it is
   * primarily in place to support testing. If specified the value must be an
   * integral multiple of 1 MiB (1048576). Also, this only applies to requests
   * where the source and destination span locations and/or storage classes.
   * Finally, this value must not change across rewrite calls else you'll get an
   * error that the `rewriteToken` is invalid.
   * </pre>
   *
   * <code>int64 max_bytes_rewritten_per_call = 15 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The maxBytesRewrittenPerCall.
   */
  long getMaxBytesRewrittenPerCall();

  /**
   *
   *
   * <pre>
   * Optional. The algorithm used to encrypt the source object, if any. Used if
   * the source object was encrypted with a Customer-Supplied Encryption Key.
   * </pre>
   *
   * <code>string copy_source_encryption_algorithm = 16 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The copySourceEncryptionAlgorithm.
   */
  java.lang.String getCopySourceEncryptionAlgorithm();

  /**
   *
   *
   * <pre>
   * Optional. The algorithm used to encrypt the source object, if any. Used if
   * the source object was encrypted with a Customer-Supplied Encryption Key.
   * </pre>
   *
   * <code>string copy_source_encryption_algorithm = 16 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The bytes for copySourceEncryptionAlgorithm.
   */
  com.google.protobuf.ByteString getCopySourceEncryptionAlgorithmBytes();

  /**
   *
   *
   * <pre>
   * Optional. The raw bytes (not base64-encoded) AES-256 encryption key used to
   * encrypt the source object, if it was encrypted with a Customer-Supplied
   * Encryption Key.
   * </pre>
   *
   * <code>bytes copy_source_encryption_key_bytes = 21 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The copySourceEncryptionKeyBytes.
   */
  com.google.protobuf.ByteString getCopySourceEncryptionKeyBytes();

  /**
   *
   *
   * <pre>
   * Optional. The raw bytes (not base64-encoded) SHA256 hash of the encryption
   * key used to encrypt the source object, if it was encrypted with a
   * Customer-Supplied Encryption Key.
   * </pre>
   *
   * <code>
   * bytes copy_source_encryption_key_sha256_bytes = 22 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The copySourceEncryptionKeySha256Bytes.
   */
  com.google.protobuf.ByteString getCopySourceEncryptionKeySha256Bytes();

  /**
   *
   *
   * <pre>
   * Optional. A set of parameters common to Storage API requests concerning an
   * object.
   * </pre>
   *
   * <code>
   * .google.storage.v2.CommonObjectRequestParams common_object_request_params = 19 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the commonObjectRequestParams field is set.
   */
  boolean hasCommonObjectRequestParams();

  /**
   *
   *
   * <pre>
   * Optional. A set of parameters common to Storage API requests concerning an
   * object.
   * </pre>
   *
   * <code>
   * .google.storage.v2.CommonObjectRequestParams common_object_request_params = 19 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The commonObjectRequestParams.
   */
  com.google.storage.v2.CommonObjectRequestParams getCommonObjectRequestParams();

  /**
   *
   *
   * <pre>
   * Optional. A set of parameters common to Storage API requests concerning an
   * object.
   * </pre>
   *
   * <code>
   * .google.storage.v2.CommonObjectRequestParams common_object_request_params = 19 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.storage.v2.CommonObjectRequestParamsOrBuilder getCommonObjectRequestParamsOrBuilder();

  /**
   *
   *
   * <pre>
   * Optional. The checksums of the complete object. This will be used to
   * validate the destination object after rewriting.
   * </pre>
   *
   * <code>
   * .google.storage.v2.ObjectChecksums object_checksums = 29 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the objectChecksums field is set.
   */
  boolean hasObjectChecksums();

  /**
   *
   *
   * <pre>
   * Optional. The checksums of the complete object. This will be used to
   * validate the destination object after rewriting.
   * </pre>
   *
   * <code>
   * .google.storage.v2.ObjectChecksums object_checksums = 29 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The objectChecksums.
   */
  com.google.storage.v2.ObjectChecksums getObjectChecksums();

  /**
   *
   *
   * <pre>
   * Optional. The checksums of the complete object. This will be used to
   * validate the destination object after rewriting.
   * </pre>
   *
   * <code>
   * .google.storage.v2.ObjectChecksums object_checksums = 29 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.storage.v2.ObjectChecksumsOrBuilder getObjectChecksumsOrBuilder();
}
