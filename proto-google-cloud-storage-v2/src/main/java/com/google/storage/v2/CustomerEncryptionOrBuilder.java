/*
 * Copyright 2024 Google LLC
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

// Protobuf Java Version: 3.25.4
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
