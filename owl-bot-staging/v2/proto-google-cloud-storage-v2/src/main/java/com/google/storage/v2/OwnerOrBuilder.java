// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface OwnerOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.Owner)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The entity, in the form `user-`*userId*.
   * </pre>
   *
   * <code>string entity = 1;</code>
   * @return The entity.
   */
  java.lang.String getEntity();
  /**
   * <pre>
   * The entity, in the form `user-`*userId*.
   * </pre>
   *
   * <code>string entity = 1;</code>
   * @return The bytes for entity.
   */
  com.google.protobuf.ByteString
      getEntityBytes();

  /**
   * <pre>
   * The ID for the entity.
   * </pre>
   *
   * <code>string entity_id = 2;</code>
   * @return The entityId.
   */
  java.lang.String getEntityId();
  /**
   * <pre>
   * The ID for the entity.
   * </pre>
   *
   * <code>string entity_id = 2;</code>
   * @return The bytes for entityId.
   */
  com.google.protobuf.ByteString
      getEntityIdBytes();
}
