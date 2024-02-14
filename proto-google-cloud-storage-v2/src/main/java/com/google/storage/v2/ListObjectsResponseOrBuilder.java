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

public interface ListObjectsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.ListObjectsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Object objects = 1;</code>
   */
  java.util.List<com.google.storage.v2.Object> getObjectsList();
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Object objects = 1;</code>
   */
  com.google.storage.v2.Object getObjects(int index);
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Object objects = 1;</code>
   */
  int getObjectsCount();
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Object objects = 1;</code>
   */
  java.util.List<? extends com.google.storage.v2.ObjectOrBuilder> getObjectsOrBuilderList();
  /**
   *
   *
   * <pre>
   * The list of items.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Object objects = 1;</code>
   */
  com.google.storage.v2.ObjectOrBuilder getObjectsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * The list of prefixes of objects matching-but-not-listed up to and including
   * the requested delimiter.
   * </pre>
   *
   * <code>repeated string prefixes = 2;</code>
   *
   * @return A list containing the prefixes.
   */
  java.util.List<java.lang.String> getPrefixesList();
  /**
   *
   *
   * <pre>
   * The list of prefixes of objects matching-but-not-listed up to and including
   * the requested delimiter.
   * </pre>
   *
   * <code>repeated string prefixes = 2;</code>
   *
   * @return The count of prefixes.
   */
  int getPrefixesCount();
  /**
   *
   *
   * <pre>
   * The list of prefixes of objects matching-but-not-listed up to and including
   * the requested delimiter.
   * </pre>
   *
   * <code>repeated string prefixes = 2;</code>
   *
   * @param index The index of the element to return.
   * @return The prefixes at the given index.
   */
  java.lang.String getPrefixes(int index);
  /**
   *
   *
   * <pre>
   * The list of prefixes of objects matching-but-not-listed up to and including
   * the requested delimiter.
   * </pre>
   *
   * <code>repeated string prefixes = 2;</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the prefixes at the given index.
   */
  com.google.protobuf.ByteString getPrefixesBytes(int index);

  /**
   *
   *
   * <pre>
   * The continuation token, used to page through large result sets. Provide
   * this value in a subsequent request to return the next page of results.
   * </pre>
   *
   * <code>string next_page_token = 3;</code>
   *
   * @return The nextPageToken.
   */
  java.lang.String getNextPageToken();
  /**
   *
   *
   * <pre>
   * The continuation token, used to page through large result sets. Provide
   * this value in a subsequent request to return the next page of results.
   * </pre>
   *
   * <code>string next_page_token = 3;</code>
   *
   * @return The bytes for nextPageToken.
   */
  com.google.protobuf.ByteString getNextPageTokenBytes();
}
