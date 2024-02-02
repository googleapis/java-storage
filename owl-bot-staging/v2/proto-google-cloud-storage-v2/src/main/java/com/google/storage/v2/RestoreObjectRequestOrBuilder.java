// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface RestoreObjectRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.RestoreObjectRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. Name of the bucket in which the object resides.
   * </pre>
   *
   * <code>string bucket = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bucket.
   */
  java.lang.String getBucket();
  /**
   * <pre>
   * Required. Name of the bucket in which the object resides.
   * </pre>
   *
   * <code>string bucket = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for bucket.
   */
  com.google.protobuf.ByteString
      getBucketBytes();

  /**
   * <pre>
   * Required. The name of the object to restore.
   * </pre>
   *
   * <code>string object = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The object.
   */
  java.lang.String getObject();
  /**
   * <pre>
   * Required. The name of the object to restore.
   * </pre>
   *
   * <code>string object = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The bytes for object.
   */
  com.google.protobuf.ByteString
      getObjectBytes();

  /**
   * <pre>
   * Required. The specific revision of the object to restore.
   * </pre>
   *
   * <code>int64 generation = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The generation.
   */
  long getGeneration();

  /**
   * <pre>
   * Makes the operation conditional on whether the object's current generation
   * matches the given value. Setting to 0 makes the operation succeed only if
   * there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 4;</code>
   * @return Whether the ifGenerationMatch field is set.
   */
  boolean hasIfGenerationMatch();
  /**
   * <pre>
   * Makes the operation conditional on whether the object's current generation
   * matches the given value. Setting to 0 makes the operation succeed only if
   * there are no live versions of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_match = 4;</code>
   * @return The ifGenerationMatch.
   */
  long getIfGenerationMatch();

  /**
   * <pre>
   * Makes the operation conditional on whether the object's live generation
   * does not match the given value. If no live object exists, the precondition
   * fails. Setting to 0 makes the operation succeed only if there is a live
   * version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 5;</code>
   * @return Whether the ifGenerationNotMatch field is set.
   */
  boolean hasIfGenerationNotMatch();
  /**
   * <pre>
   * Makes the operation conditional on whether the object's live generation
   * does not match the given value. If no live object exists, the precondition
   * fails. Setting to 0 makes the operation succeed only if there is a live
   * version of the object.
   * </pre>
   *
   * <code>optional int64 if_generation_not_match = 5;</code>
   * @return The ifGenerationNotMatch.
   */
  long getIfGenerationNotMatch();

  /**
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 6;</code>
   * @return Whether the ifMetagenerationMatch field is set.
   */
  boolean hasIfMetagenerationMatch();
  /**
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration matches the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_match = 6;</code>
   * @return The ifMetagenerationMatch.
   */
  long getIfMetagenerationMatch();

  /**
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 7;</code>
   * @return Whether the ifMetagenerationNotMatch field is set.
   */
  boolean hasIfMetagenerationNotMatch();
  /**
   * <pre>
   * Makes the operation conditional on whether the object's current
   * metageneration does not match the given value.
   * </pre>
   *
   * <code>optional int64 if_metageneration_not_match = 7;</code>
   * @return The ifMetagenerationNotMatch.
   */
  long getIfMetagenerationNotMatch();

  /**
   * <pre>
   * If false or unset, the bucket's default object ACL will be used.
   * If true, copy the source object's access controls.
   * Return an error if bucket has UBLA enabled.
   * </pre>
   *
   * <code>optional bool copy_source_acl = 9;</code>
   * @return Whether the copySourceAcl field is set.
   */
  boolean hasCopySourceAcl();
  /**
   * <pre>
   * If false or unset, the bucket's default object ACL will be used.
   * If true, copy the source object's access controls.
   * Return an error if bucket has UBLA enabled.
   * </pre>
   *
   * <code>optional bool copy_source_acl = 9;</code>
   * @return The copySourceAcl.
   */
  boolean getCopySourceAcl();

  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 8;</code>
   * @return Whether the commonObjectRequestParams field is set.
   */
  boolean hasCommonObjectRequestParams();
  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 8;</code>
   * @return The commonObjectRequestParams.
   */
  com.google.storage.v2.CommonObjectRequestParams getCommonObjectRequestParams();
  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 8;</code>
   */
  com.google.storage.v2.CommonObjectRequestParamsOrBuilder getCommonObjectRequestParamsOrBuilder();
}
