// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

public interface BidiWriteObjectResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.BidiWriteObjectResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The total number of bytes that have been processed for the given object
   * from all `WriteObject` calls. Only set if the upload has not finalized.
   * </pre>
   *
   * <code>int64 persisted_size = 1;</code>
   * @return Whether the persistedSize field is set.
   */
  boolean hasPersistedSize();
  /**
   * <pre>
   * The total number of bytes that have been processed for the given object
   * from all `WriteObject` calls. Only set if the upload has not finalized.
   * </pre>
   *
   * <code>int64 persisted_size = 1;</code>
   * @return The persistedSize.
   */
  long getPersistedSize();

  /**
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   * @return Whether the resource field is set.
   */
  boolean hasResource();
  /**
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   * @return The resource.
   */
  com.google.storage.v2.Object getResource();
  /**
   * <pre>
   * A resource containing the metadata for the uploaded object. Only set if
   * the upload has finalized.
   * </pre>
   *
   * <code>.google.storage.v2.Object resource = 2;</code>
   */
  com.google.storage.v2.ObjectOrBuilder getResourceOrBuilder();

  com.google.storage.v2.BidiWriteObjectResponse.WriteStatusCase getWriteStatusCase();
}
