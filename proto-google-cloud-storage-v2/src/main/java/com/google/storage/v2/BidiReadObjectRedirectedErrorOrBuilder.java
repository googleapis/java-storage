/*
 * Copyright 2025 Google LLC
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

// Protobuf Java Version: 3.25.8
package com.google.storage.v2;

public interface BidiReadObjectRedirectedErrorOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.BidiReadObjectRedirectedError)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The read handle for the redirected read. If set, the client may use this in
   * the BidiReadObjectSpec when retrying the read stream.
   * </pre>
   *
   * <code>.google.storage.v2.BidiReadHandle read_handle = 1;</code>
   *
   * @return Whether the readHandle field is set.
   */
  boolean hasReadHandle();

  /**
   *
   *
   * <pre>
   * The read handle for the redirected read. If set, the client may use this in
   * the BidiReadObjectSpec when retrying the read stream.
   * </pre>
   *
   * <code>.google.storage.v2.BidiReadHandle read_handle = 1;</code>
   *
   * @return The readHandle.
   */
  com.google.storage.v2.BidiReadHandle getReadHandle();

  /**
   *
   *
   * <pre>
   * The read handle for the redirected read. If set, the client may use this in
   * the BidiReadObjectSpec when retrying the read stream.
   * </pre>
   *
   * <code>.google.storage.v2.BidiReadHandle read_handle = 1;</code>
   */
  com.google.storage.v2.BidiReadHandleOrBuilder getReadHandleOrBuilder();

  /**
   *
   *
   * <pre>
   * The routing token the client must use when retrying the read stream.
   * This value must be provided in the header `x-goog-request-params`, with key
   * `routing_token` and this string verbatim as the value.
   * </pre>
   *
   * <code>optional string routing_token = 2;</code>
   *
   * @return Whether the routingToken field is set.
   */
  boolean hasRoutingToken();

  /**
   *
   *
   * <pre>
   * The routing token the client must use when retrying the read stream.
   * This value must be provided in the header `x-goog-request-params`, with key
   * `routing_token` and this string verbatim as the value.
   * </pre>
   *
   * <code>optional string routing_token = 2;</code>
   *
   * @return The routingToken.
   */
  java.lang.String getRoutingToken();

  /**
   *
   *
   * <pre>
   * The routing token the client must use when retrying the read stream.
   * This value must be provided in the header `x-goog-request-params`, with key
   * `routing_token` and this string verbatim as the value.
   * </pre>
   *
   * <code>optional string routing_token = 2;</code>
   *
   * @return The bytes for routingToken.
   */
  com.google.protobuf.ByteString getRoutingTokenBytes();
}
