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

public interface OwnerOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.Owner)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The entity, in the form `user-`*userId*.
   * </pre>
   *
   * <code>string entity = 1;</code>
   *
   * @return The entity.
   */
  java.lang.String getEntity();
  /**
   *
   *
   * <pre>
   * The entity, in the form `user-`*userId*.
   * </pre>
   *
   * <code>string entity = 1;</code>
   *
   * @return The bytes for entity.
   */
  com.google.protobuf.ByteString getEntityBytes();

  /**
   *
   *
   * <pre>
   * The ID for the entity.
   * </pre>
   *
   * <code>string entity_id = 2;</code>
   *
   * @return The entityId.
   */
  java.lang.String getEntityId();
  /**
   *
   *
   * <pre>
   * The ID for the entity.
   * </pre>
   *
   * <code>string entity_id = 2;</code>
   *
   * @return The bytes for entityId.
   */
  com.google.protobuf.ByteString getEntityIdBytes();
}
