// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/control/v2/storage_control.proto

package com.google.storage.control.v2;

public interface GetStorageLayoutRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.control.v2.GetStorageLayoutRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. The name of the StorageLayout resource.
   * Format: `projects/{project}/buckets/{bucket}/storageLayout`
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * Required. The name of the StorageLayout resource.
   * Format: `projects/{project}/buckets/{bucket}/storageLayout`
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * An optional prefix used for permission check. It is useful when the caller
   * only has limited permissions under a specific prefix.
   * </pre>
   *
   * <code>string prefix = 2;</code>
   * @return The prefix.
   */
  java.lang.String getPrefix();
  /**
   * <pre>
   * An optional prefix used for permission check. It is useful when the caller
   * only has limited permissions under a specific prefix.
   * </pre>
   *
   * <code>string prefix = 2;</code>
   * @return The bytes for prefix.
   */
  com.google.protobuf.ByteString
      getPrefixBytes();

  /**
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted.
   * </pre>
   *
   * <code>string request_id = 3 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }</code>
   * @return The requestId.
   */
  java.lang.String getRequestId();
  /**
   * <pre>
   * Optional. A unique identifier for this request. UUID is the recommended
   * format, but other formats are still accepted.
   * </pre>
   *
   * <code>string request_id = 3 [(.google.api.field_behavior) = OPTIONAL, (.google.api.field_info) = { ... }</code>
   * @return The bytes for requestId.
   */
  com.google.protobuf.ByteString
      getRequestIdBytes();
}
