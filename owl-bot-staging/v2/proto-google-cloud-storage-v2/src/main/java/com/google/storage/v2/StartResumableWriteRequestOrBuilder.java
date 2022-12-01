// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

public interface StartResumableWriteRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.StartResumableWriteRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. The destination bucket, object, and metadata, as well as any
   * preconditions.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return Whether the writeObjectSpec field is set.
   */
  boolean hasWriteObjectSpec();
  /**
   * <pre>
   * Required. The destination bucket, object, and metadata, as well as any
   * preconditions.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   * @return The writeObjectSpec.
   */
  com.google.storage.v2.WriteObjectSpec getWriteObjectSpec();
  /**
   * <pre>
   * Required. The destination bucket, object, and metadata, as well as any
   * preconditions.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   */
  com.google.storage.v2.WriteObjectSpecOrBuilder getWriteObjectSpecOrBuilder();

  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 3;</code>
   * @return Whether the commonObjectRequestParams field is set.
   */
  boolean hasCommonObjectRequestParams();
  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 3;</code>
   * @return The commonObjectRequestParams.
   */
  com.google.storage.v2.CommonObjectRequestParams getCommonObjectRequestParams();
  /**
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 3;</code>
   */
  com.google.storage.v2.CommonObjectRequestParamsOrBuilder getCommonObjectRequestParamsOrBuilder();

  /**
   * <pre>
   * The checksums of the complete object. This will be used to validate the
   * uploaded object. For each upload, object_checksums can be provided with
   * either StartResumableWriteRequest or the WriteObjectRequest with
   * finish_write set to `true`.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 5;</code>
   * @return Whether the objectChecksums field is set.
   */
  boolean hasObjectChecksums();
  /**
   * <pre>
   * The checksums of the complete object. This will be used to validate the
   * uploaded object. For each upload, object_checksums can be provided with
   * either StartResumableWriteRequest or the WriteObjectRequest with
   * finish_write set to `true`.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 5;</code>
   * @return The objectChecksums.
   */
  com.google.storage.v2.ObjectChecksums getObjectChecksums();
  /**
   * <pre>
   * The checksums of the complete object. This will be used to validate the
   * uploaded object. For each upload, object_checksums can be provided with
   * either StartResumableWriteRequest or the WriteObjectRequest with
   * finish_write set to `true`.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 5;</code>
   */
  com.google.storage.v2.ObjectChecksumsOrBuilder getObjectChecksumsOrBuilder();
}
