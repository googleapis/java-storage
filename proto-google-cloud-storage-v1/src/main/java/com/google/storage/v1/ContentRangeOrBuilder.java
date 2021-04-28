/*
 * Copyright 2020 Google LLC
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
// source: google/storage/v1/storage_resources.proto

package com.google.storage.v1;

public interface ContentRangeOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v1.ContentRange)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The starting offset of the object data.
   * </pre>
   *
   * <code>int64 start = 1;</code>
   *
   * @return The start.
   */
  long getStart();

  /**
   *
   *
   * <pre>
   * The ending offset of the object data.
   * </pre>
   *
   * <code>int64 end = 2;</code>
   *
   * @return The end.
   */
  long getEnd();

  /**
   *
   *
   * <pre>
   * The complete length of the object data.
   * </pre>
   *
   * <code>int64 complete_length = 3;</code>
   *
   * @return The completeLength.
   */
  long getCompleteLength();
}
