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

// Protobuf Java Version: 3.25.3
package com.google.storage.v2;

public interface CreateHmacKeyResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.CreateHmacKeyResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   *
   * @return Whether the metadata field is set.
   */
  boolean hasMetadata();
  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   *
   * @return The metadata.
   */
  com.google.storage.v2.HmacKeyMetadata getMetadata();
  /**
   *
   *
   * <pre>
   * Key metadata.
   * </pre>
   *
   * <code>.google.storage.v2.HmacKeyMetadata metadata = 1;</code>
   */
  com.google.storage.v2.HmacKeyMetadataOrBuilder getMetadataOrBuilder();

  /**
   *
   *
   * <pre>
   * HMAC key secret material.
   * In raw bytes format (not base64-encoded).
   * </pre>
   *
   * <code>bytes secret_key_bytes = 3;</code>
   *
   * @return The secretKeyBytes.
   */
  com.google.protobuf.ByteString getSecretKeyBytes();
}
