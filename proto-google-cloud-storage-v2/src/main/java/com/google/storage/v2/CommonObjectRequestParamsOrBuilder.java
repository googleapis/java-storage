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

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface CommonObjectRequestParamsOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CommonObjectRequestParams)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Encryption algorithm used with the Customer-Supplied Encryption Keys
   * feature.
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
   * Encryption algorithm used with the Customer-Supplied Encryption Keys
   * feature.
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
   * Encryption key used with the Customer-Supplied Encryption Keys feature.
   * In raw bytes format (not base64-encoded).
   * </pre>
   *
   * <code>bytes encryption_key_bytes = 4;</code>
   *
   * @return The encryptionKeyBytes.
   */
  com.google.protobuf.ByteString getEncryptionKeyBytes();

  /**
   *
   *
   * <pre>
   * SHA256 hash of encryption key used with the Customer-Supplied Encryption
   * Keys feature.
   * </pre>
   *
   * <code>bytes encryption_key_sha256_bytes = 5;</code>
   *
   * @return The encryptionKeySha256Bytes.
   */
  com.google.protobuf.ByteString getEncryptionKeySha256Bytes();
}
