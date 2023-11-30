/*
 * Copyright 2023 Google LLC
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

package com.google.storage.v2;

public interface BidiWriteObjectRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.BidiWriteObjectRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * For resumable uploads. This should be the `upload_id` returned from a
   * call to `StartResumableWriteResponse`.
   * </pre>
   *
   * <code>string upload_id = 1;</code>
   *
   * @return Whether the uploadId field is set.
   */
  boolean hasUploadId();
  /**
   *
   *
   * <pre>
   * For resumable uploads. This should be the `upload_id` returned from a
   * call to `StartResumableWriteResponse`.
   * </pre>
   *
   * <code>string upload_id = 1;</code>
   *
   * @return The uploadId.
   */
  java.lang.String getUploadId();
  /**
   *
   *
   * <pre>
   * For resumable uploads. This should be the `upload_id` returned from a
   * call to `StartResumableWriteResponse`.
   * </pre>
   *
   * <code>string upload_id = 1;</code>
   *
   * @return The bytes for uploadId.
   */
  com.google.protobuf.ByteString getUploadIdBytes();

  /**
   *
   *
   * <pre>
   * For non-resumable uploads. Describes the overall upload, including the
   * destination bucket and object name, preconditions, etc.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 2;</code>
   *
   * @return Whether the writeObjectSpec field is set.
   */
  boolean hasWriteObjectSpec();
  /**
   *
   *
   * <pre>
   * For non-resumable uploads. Describes the overall upload, including the
   * destination bucket and object name, preconditions, etc.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 2;</code>
   *
   * @return The writeObjectSpec.
   */
  com.google.storage.v2.WriteObjectSpec getWriteObjectSpec();
  /**
   *
   *
   * <pre>
   * For non-resumable uploads. Describes the overall upload, including the
   * destination bucket and object name, preconditions, etc.
   * </pre>
   *
   * <code>.google.storage.v2.WriteObjectSpec write_object_spec = 2;</code>
   */
  com.google.storage.v2.WriteObjectSpecOrBuilder getWriteObjectSpecOrBuilder();

  /**
   *
   *
   * <pre>
   * Required. The offset from the beginning of the object at which the data
   * should be written.
   *
   * In the first `WriteObjectRequest` of a `WriteObject()` action, it
   * indicates the initial offset for the `Write()` call. The value **must** be
   * equal to the `persisted_size` that a call to `QueryWriteStatus()` would
   * return (0 if this is the first write to the object).
   *
   * On subsequent calls, this value **must** be no larger than the sum of the
   * first `write_offset` and the sizes of all `data` chunks sent previously on
   * this stream.
   *
   * An invalid value will cause an error.
   * </pre>
   *
   * <code>int64 write_offset = 3 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The writeOffset.
   */
  long getWriteOffset();

  /**
   *
   *
   * <pre>
   * The data to insert. If a crc32c checksum is provided that doesn't match
   * the checksum computed by the service, the request will fail.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 4;</code>
   *
   * @return Whether the checksummedData field is set.
   */
  boolean hasChecksummedData();
  /**
   *
   *
   * <pre>
   * The data to insert. If a crc32c checksum is provided that doesn't match
   * the checksum computed by the service, the request will fail.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 4;</code>
   *
   * @return The checksummedData.
   */
  com.google.storage.v2.ChecksummedData getChecksummedData();
  /**
   *
   *
   * <pre>
   * The data to insert. If a crc32c checksum is provided that doesn't match
   * the checksum computed by the service, the request will fail.
   * </pre>
   *
   * <code>.google.storage.v2.ChecksummedData checksummed_data = 4;</code>
   */
  com.google.storage.v2.ChecksummedDataOrBuilder getChecksummedDataOrBuilder();

  /**
   *
   *
   * <pre>
   * Checksums for the complete object. If the checksums computed by the service
   * don't match the specified checksums the call will fail. May only be
   * provided in the first or last request (either with first_message, or
   * finish_write set).
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 6;</code>
   *
   * @return Whether the objectChecksums field is set.
   */
  boolean hasObjectChecksums();
  /**
   *
   *
   * <pre>
   * Checksums for the complete object. If the checksums computed by the service
   * don't match the specified checksums the call will fail. May only be
   * provided in the first or last request (either with first_message, or
   * finish_write set).
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 6;</code>
   *
   * @return The objectChecksums.
   */
  com.google.storage.v2.ObjectChecksums getObjectChecksums();
  /**
   *
   *
   * <pre>
   * Checksums for the complete object. If the checksums computed by the service
   * don't match the specified checksums the call will fail. May only be
   * provided in the first or last request (either with first_message, or
   * finish_write set).
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums object_checksums = 6;</code>
   */
  com.google.storage.v2.ObjectChecksumsOrBuilder getObjectChecksumsOrBuilder();

  /**
   *
   *
   * <pre>
   * For each BidiWriteObjectRequest where state_lookup is `true` or the client
   * closes the stream, the service will send a BidiWriteObjectResponse
   * containing the current persisted size. The persisted size sent in responses
   * covers all the bytes the server has persisted thus far and can be used to
   * decide what data is safe for the client to drop. Note that the object's
   * current size reported by the BidiWriteObjectResponse may lag behind the
   * number of bytes written by the client.
   * </pre>
   *
   * <code>bool state_lookup = 7;</code>
   *
   * @return The stateLookup.
   */
  boolean getStateLookup();

  /**
   *
   *
   * <pre>
   * Persists data written on the stream, up to and including the current
   * message, to permanent storage. This option should be used sparingly as it
   * may reduce performance. Ongoing writes will periodically be persisted on
   * the server even when `flush` is not set.
   * </pre>
   *
   * <code>bool flush = 8;</code>
   *
   * @return The flush.
   */
  boolean getFlush();

  /**
   *
   *
   * <pre>
   * If `true`, this indicates that the write is complete. Sending any
   * `WriteObjectRequest`s subsequent to one in which `finish_write` is `true`
   * will cause an error.
   * For a non-resumable write (where the upload_id was not set in the first
   * message), it is an error not to set this field in the final message of the
   * stream.
   * </pre>
   *
   * <code>bool finish_write = 9;</code>
   *
   * @return The finishWrite.
   */
  boolean getFinishWrite();

  /**
   *
   *
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 10;</code>
   *
   * @return Whether the commonObjectRequestParams field is set.
   */
  boolean hasCommonObjectRequestParams();
  /**
   *
   *
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 10;</code>
   *
   * @return The commonObjectRequestParams.
   */
  com.google.storage.v2.CommonObjectRequestParams getCommonObjectRequestParams();
  /**
   *
   *
   * <pre>
   * A set of parameters common to Storage API requests concerning an object.
   * </pre>
   *
   * <code>.google.storage.v2.CommonObjectRequestParams common_object_request_params = 10;</code>
   */
  com.google.storage.v2.CommonObjectRequestParamsOrBuilder getCommonObjectRequestParamsOrBuilder();

  com.google.storage.v2.BidiWriteObjectRequest.FirstMessageCase getFirstMessageCase();

  com.google.storage.v2.BidiWriteObjectRequest.DataCase getDataCase();
}
