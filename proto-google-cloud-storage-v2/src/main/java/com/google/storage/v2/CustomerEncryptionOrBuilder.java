// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.5
package com.google.storage.v2;

public interface CustomerEncryptionOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CustomerEncryption)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The encryption algorithm.
   * </pre>
   *
   * <code>string encryption_algorithm = 1;</code>
   *
   * @return The encryptionAlgorithm.
   */
  java.lang.String getEncryptionAlgorithm();
  /**
   *
   *
   * <pre>
   * The encryption algorithm.
   * </pre>
   *
   * <code>string encryption_algorithm = 1;</code>
   *
   * @return The bytes for encryptionAlgorithm.
   */
  com.google.protobuf.ByteString getEncryptionAlgorithmBytes();

  /**
   *
   *
   * <pre>
   * SHA256 hash value of the encryption key.
   * In raw bytes format (not base64-encoded).
   * </pre>
   *
   * <code>bytes key_sha256_bytes = 3;</code>
   *
   * @return The keySha256Bytes.
   */
  com.google.protobuf.ByteString getKeySha256Bytes();
}
