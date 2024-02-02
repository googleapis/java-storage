// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface CreateHmacKeyResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CreateHmacKeyResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   * @return Whether the metadata field is set.
   */
  boolean hasMetadata();
  /**
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   * @return The metadata.
   */
  com.google.storage.v2.HmacKeyMetadata getMetadata();
  /**
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   */
  com.google.storage.v2.HmacKeyMetadataOrBuilder getMetadataOrBuilder();

  /**
   * <pre>
   * HMAC key secret material.
   * In raw bytes format (not base64-encoded).
   * </pre>
   *
   * <code>bytes secret_key_bytes = 3;</code>
   * @return The secretKeyBytes.
   */
  com.google.protobuf.ByteString getSecretKeyBytes();
}
