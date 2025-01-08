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

// Protobuf Java Version: 3.25.5
package com.google.storage.v2;

public interface ObjectRangeDataOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.ObjectRangeData)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   *
   * @return Whether the checksummedData field is set.
   */
  boolean hasChecksummedData();
  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   *
   * @return The checksummedData.
   */
  com.google.storage.v2.ChecksummedData getChecksummedData();
  /**
   *
   *
   * <pre>
   * A portion of the data for the object.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 1;</code>
   */
  com.google.storage.v2.ChecksummedDataOrBuilder getChecksummedDataOrBuilder();

  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   *
   * @return Whether the readRange field is set.
   */
  boolean hasReadRange();
  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   *
   * @return The readRange.
   */
  com.google.storage.v2.ReadRange getReadRange();
  /**
   *
   *
   * <pre>
   * The ReadRange describes the content being returned with read_id set to the
   * corresponding ReadObjectRequest in the stream. Multiple ObjectRangeData
   * messages may have the same read_id but increasing offsets.
   * ReadObjectResponse messages with the same read_id are guaranteed to be
   * delivered in increasing offset order.
   * </pre>
   *
   * <code>.google.storage.v2.ReadRange read_range = 2;</code>
   */
  com.google.storage.v2.ReadRangeOrBuilder getReadRangeOrBuilder();

  /**
   *
   *
   * <pre>
   * If set, indicates there are no more bytes to read for the given ReadRange.
   * </pre>
   *
   * <code>bool range_end = 3;</code>
   *
   * @return The rangeEnd.
   */
  boolean getRangeEnd();
}
