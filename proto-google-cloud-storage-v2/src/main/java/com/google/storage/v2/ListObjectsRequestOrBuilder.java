// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.5
package com.google.storage.v2;

public interface ListObjectsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.ListObjectsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Name of the bucket in which to look for objects.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The parent.
   */
  java.lang.String getParent();
  /**
   *
   *
   * <pre>
   * Required. Name of the bucket in which to look for objects.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for parent.
   */
  com.google.protobuf.ByteString getParentBytes();

  /**
   *
   *
   * <pre>
   * Maximum number of `items` plus `prefixes` to return
   * in a single page of responses. As duplicate `prefixes` are
   * omitted, fewer total results may be returned than requested. The service
   * will use this parameter or 1,000 items, whichever is smaller.
   * </pre>
   *
   * <code>int32 page_size = 2;</code>
   *
   * @return The pageSize.
   */
  int getPageSize();

  /**
   *
   *
   * <pre>
   * A previously-returned page token representing part of the larger set of
   * results to view.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The pageToken.
   */
  java.lang.String getPageToken();
  /**
   *
   *
   * <pre>
   * A previously-returned page token representing part of the larger set of
   * results to view.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The bytes for pageToken.
   */
  com.google.protobuf.ByteString getPageTokenBytes();

  /**
   *
   *
   * <pre>
   * If set, returns results in a directory-like mode. `items` will contain
   * only objects whose names, aside from the `prefix`, do not
   * contain `delimiter`. Objects whose names, aside from the
   * `prefix`, contain `delimiter` will have their name,
   * truncated after the `delimiter`, returned in
   * `prefixes`. Duplicate `prefixes` are omitted.
   * </pre>
   *
   * <code>string delimiter = 4;</code>
   *
   * @return The delimiter.
   */
  java.lang.String getDelimiter();
  /**
   *
   *
   * <pre>
   * If set, returns results in a directory-like mode. `items` will contain
   * only objects whose names, aside from the `prefix`, do not
   * contain `delimiter`. Objects whose names, aside from the
   * `prefix`, contain `delimiter` will have their name,
   * truncated after the `delimiter`, returned in
   * `prefixes`. Duplicate `prefixes` are omitted.
   * </pre>
   *
   * <code>string delimiter = 4;</code>
   *
   * @return The bytes for delimiter.
   */
  com.google.protobuf.ByteString getDelimiterBytes();

  /**
   *
   *
   * <pre>
   * If true, objects that end in exactly one instance of `delimiter`
   * will have their metadata included in `items` in addition to
   * `prefixes`.
   * </pre>
   *
   * <code>bool include_trailing_delimiter = 5;</code>
   *
   * @return The includeTrailingDelimiter.
   */
  boolean getIncludeTrailingDelimiter();

  /**
   *
   *
   * <pre>
   * Filter results to objects whose names begin with this prefix.
   * </pre>
   *
   * <code>string prefix = 6;</code>
   *
   * @return The prefix.
   */
  java.lang.String getPrefix();
  /**
   *
   *
   * <pre>
   * Filter results to objects whose names begin with this prefix.
   * </pre>
   *
   * <code>string prefix = 6;</code>
   *
   * @return The bytes for prefix.
   */
  com.google.protobuf.ByteString getPrefixBytes();

  /**
   *
   *
   * <pre>
   * If `true`, lists all versions of an object as distinct results.
   * For more information, see
   * [Object
   * Versioning](https://cloud.google.com/storage/docs/object-versioning).
   * </pre>
   *
   * <code>bool versions = 7;</code>
   *
   * @return The versions.
   */
  boolean getVersions();

  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.acl and
   * items.owner.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 8;</code>
   *
   * @return Whether the readMask field is set.
   */
  boolean hasReadMask();
  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.acl and
   * items.owner.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 8;</code>
   *
   * @return The readMask.
   */
  com.google.protobuf.FieldMask getReadMask();
  /**
   *
   *
   * <pre>
   * Mask specifying which fields to read from each result.
   * If no mask is specified, will default to all fields except items.acl and
   * items.owner.
   * * may be used to mean "all fields".
   * </pre>
   *
   * <code>optional .google.protobuf.FieldMask read_mask = 8;</code>
   */
  com.google.protobuf.FieldMaskOrBuilder getReadMaskOrBuilder();

  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects whose names are lexicographically equal
   * to or after lexicographic_start. If lexicographic_end is also set, the
   * objects listed have names between lexicographic_start (inclusive) and
   * lexicographic_end (exclusive).
   * </pre>
   *
   * <code>string lexicographic_start = 10 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The lexicographicStart.
   */
  java.lang.String getLexicographicStart();
  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects whose names are lexicographically equal
   * to or after lexicographic_start. If lexicographic_end is also set, the
   * objects listed have names between lexicographic_start (inclusive) and
   * lexicographic_end (exclusive).
   * </pre>
   *
   * <code>string lexicographic_start = 10 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for lexicographicStart.
   */
  com.google.protobuf.ByteString getLexicographicStartBytes();

  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects whose names are lexicographically
   * before lexicographic_end. If lexicographic_start is also set, the objects
   * listed have names between lexicographic_start (inclusive) and
   * lexicographic_end (exclusive).
   * </pre>
   *
   * <code>string lexicographic_end = 11 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The lexicographicEnd.
   */
  java.lang.String getLexicographicEnd();
  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects whose names are lexicographically
   * before lexicographic_end. If lexicographic_start is also set, the objects
   * listed have names between lexicographic_start (inclusive) and
   * lexicographic_end (exclusive).
   * </pre>
   *
   * <code>string lexicographic_end = 11 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for lexicographicEnd.
   */
  com.google.protobuf.ByteString getLexicographicEndBytes();

  /**
   *
   *
   * <pre>
   * Optional. If true, only list all soft-deleted versions of the object.
   * Soft delete policy is required to set this option.
   * </pre>
   *
   * <code>bool soft_deleted = 12 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The softDeleted.
   */
  boolean getSoftDeleted();

  /**
   *
   *
   * <pre>
   * Optional. If true, will also include folders and managed folders (besides
   * objects) in the returned `prefixes`. Requires `delimiter` to be set to '/'.
   * </pre>
   *
   * <code>bool include_folders_as_prefixes = 13 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The includeFoldersAsPrefixes.
   */
  boolean getIncludeFoldersAsPrefixes();

  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects and prefixes that match this glob
   * pattern. See [List Objects Using
   * Glob](https://cloud.google.com/storage/docs/json_api/v1/objects/list#list-objects-and-prefixes-using-glob)
   * for the full syntax.
   * </pre>
   *
   * <code>string match_glob = 14 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The matchGlob.
   */
  java.lang.String getMatchGlob();
  /**
   *
   *
   * <pre>
   * Optional. Filter results to objects and prefixes that match this glob
   * pattern. See [List Objects Using
   * Glob](https://cloud.google.com/storage/docs/json_api/v1/objects/list#list-objects-and-prefixes-using-glob)
   * for the full syntax.
   * </pre>
   *
   * <code>string match_glob = 14 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for matchGlob.
   */
  com.google.protobuf.ByteString getMatchGlobBytes();
}
