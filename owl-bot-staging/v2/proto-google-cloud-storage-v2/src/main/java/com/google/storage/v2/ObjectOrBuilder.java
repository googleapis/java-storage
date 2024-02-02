// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface ObjectOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.Object)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Immutable. The name of this object. Nearly any sequence of unicode
   * characters is valid. See
   * [Guidelines](https://cloud.google.com/storage/docs/objects#naming).
   * Example: `test.txt`
   * The `name` field by itself does not uniquely identify a Cloud Storage
   * object. A Cloud Storage object is uniquely identified by the tuple of
   * (bucket, object, generation).
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = IMMUTABLE];</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * Immutable. The name of this object. Nearly any sequence of unicode
   * characters is valid. See
   * [Guidelines](https://cloud.google.com/storage/docs/objects#naming).
   * Example: `test.txt`
   * The `name` field by itself does not uniquely identify a Cloud Storage
   * object. A Cloud Storage object is uniquely identified by the tuple of
   * (bucket, object, generation).
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = IMMUTABLE];</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * Immutable. The name of the bucket containing this object.
   * </pre>
   *
   * <code>string bucket = 2 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }</code>
   * @return The bucket.
   */
  java.lang.String getBucket();
  /**
   * <pre>
   * Immutable. The name of the bucket containing this object.
   * </pre>
   *
   * <code>string bucket = 2 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for bucket.
   */
  com.google.protobuf.ByteString
      getBucketBytes();

  /**
   * <pre>
   * The etag of the object.
   * If included in the metadata of an update or delete request message, the
   * operation will only be performed if the etag matches that of the live
   * object.
   * </pre>
   *
   * <code>string etag = 27;</code>
   * @return The etag.
   */
  java.lang.String getEtag();
  /**
   * <pre>
   * The etag of the object.
   * If included in the metadata of an update or delete request message, the
   * operation will only be performed if the etag matches that of the live
   * object.
   * </pre>
   *
   * <code>string etag = 27;</code>
   * @return The bytes for etag.
   */
  com.google.protobuf.ByteString
      getEtagBytes();

  /**
   * <pre>
   * Immutable. The content generation of this object. Used for object
   * versioning.
   * </pre>
   *
   * <code>int64 generation = 3 [(.google.api.field_behavior) = IMMUTABLE];</code>
   * @return The generation.
   */
  long getGeneration();

  /**
   * <pre>
   * Output only. The version of the metadata for this generation of this
   * object. Used for preconditions and for detecting changes in metadata. A
   * metageneration number is only meaningful in the context of a particular
   * generation of a particular object.
   * </pre>
   *
   * <code>int64 metageneration = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The metageneration.
   */
  long getMetageneration();

  /**
   * <pre>
   * Storage class of the object.
   * </pre>
   *
   * <code>string storage_class = 5;</code>
   * @return The storageClass.
   */
  java.lang.String getStorageClass();
  /**
   * <pre>
   * Storage class of the object.
   * </pre>
   *
   * <code>string storage_class = 5;</code>
   * @return The bytes for storageClass.
   */
  com.google.protobuf.ByteString
      getStorageClassBytes();

  /**
   * <pre>
   * Output only. Content-Length of the object data in bytes, matching
   * [https://tools.ietf.org/html/rfc7230#section-3.3.2][RFC 7230 §3.3.2].
   * </pre>
   *
   * <code>int64 size = 6 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The size.
   */
  long getSize();

  /**
   * <pre>
   * Content-Encoding of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.2.2][RFC 7231 §3.1.2.2]
   * </pre>
   *
   * <code>string content_encoding = 7;</code>
   * @return The contentEncoding.
   */
  java.lang.String getContentEncoding();
  /**
   * <pre>
   * Content-Encoding of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.2.2][RFC 7231 §3.1.2.2]
   * </pre>
   *
   * <code>string content_encoding = 7;</code>
   * @return The bytes for contentEncoding.
   */
  com.google.protobuf.ByteString
      getContentEncodingBytes();

  /**
   * <pre>
   * Content-Disposition of the object data, matching
   * [https://tools.ietf.org/html/rfc6266][RFC 6266].
   * </pre>
   *
   * <code>string content_disposition = 8;</code>
   * @return The contentDisposition.
   */
  java.lang.String getContentDisposition();
  /**
   * <pre>
   * Content-Disposition of the object data, matching
   * [https://tools.ietf.org/html/rfc6266][RFC 6266].
   * </pre>
   *
   * <code>string content_disposition = 8;</code>
   * @return The bytes for contentDisposition.
   */
  com.google.protobuf.ByteString
      getContentDispositionBytes();

  /**
   * <pre>
   * Cache-Control directive for the object data, matching
   * [https://tools.ietf.org/html/rfc7234#section-5.2"][RFC 7234 §5.2].
   * If omitted, and the object is accessible to all anonymous users, the
   * default will be `public, max-age=3600`.
   * </pre>
   *
   * <code>string cache_control = 9;</code>
   * @return The cacheControl.
   */
  java.lang.String getCacheControl();
  /**
   * <pre>
   * Cache-Control directive for the object data, matching
   * [https://tools.ietf.org/html/rfc7234#section-5.2"][RFC 7234 §5.2].
   * If omitted, and the object is accessible to all anonymous users, the
   * default will be `public, max-age=3600`.
   * </pre>
   *
   * <code>string cache_control = 9;</code>
   * @return The bytes for cacheControl.
   */
  com.google.protobuf.ByteString
      getCacheControlBytes();

  /**
   * <pre>
   * Access controls on the object.
   * If iam_config.uniform_bucket_level_access is enabled on the parent
   * bucket, requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl acl = 10;</code>
   */
  java.util.List<com.google.storage.v2.ObjectAccessControl> 
      getAclList();
  /**
   * <pre>
   * Access controls on the object.
   * If iam_config.uniform_bucket_level_access is enabled on the parent
   * bucket, requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl acl = 10;</code>
   */
  com.google.storage.v2.ObjectAccessControl getAcl(int index);
  /**
   * <pre>
   * Access controls on the object.
   * If iam_config.uniform_bucket_level_access is enabled on the parent
   * bucket, requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl acl = 10;</code>
   */
  int getAclCount();
  /**
   * <pre>
   * Access controls on the object.
   * If iam_config.uniform_bucket_level_access is enabled on the parent
   * bucket, requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl acl = 10;</code>
   */
  java.util.List<? extends com.google.storage.v2.ObjectAccessControlOrBuilder> 
      getAclOrBuilderList();
  /**
   * <pre>
   * Access controls on the object.
   * If iam_config.uniform_bucket_level_access is enabled on the parent
   * bucket, requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl acl = 10;</code>
   */
  com.google.storage.v2.ObjectAccessControlOrBuilder getAclOrBuilder(
      int index);

  /**
   * <pre>
   * Content-Language of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.3.2][RFC 7231 §3.1.3.2].
   * </pre>
   *
   * <code>string content_language = 11;</code>
   * @return The contentLanguage.
   */
  java.lang.String getContentLanguage();
  /**
   * <pre>
   * Content-Language of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.3.2][RFC 7231 §3.1.3.2].
   * </pre>
   *
   * <code>string content_language = 11;</code>
   * @return The bytes for contentLanguage.
   */
  com.google.protobuf.ByteString
      getContentLanguageBytes();

  /**
   * <pre>
   * Output only. If this object is noncurrent, this is the time when the object
   * became noncurrent.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp delete_time = 12 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the deleteTime field is set.
   */
  boolean hasDeleteTime();
  /**
   * <pre>
   * Output only. If this object is noncurrent, this is the time when the object
   * became noncurrent.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp delete_time = 12 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The deleteTime.
   */
  com.google.protobuf.Timestamp getDeleteTime();
  /**
   * <pre>
   * Output only. If this object is noncurrent, this is the time when the object
   * became noncurrent.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp delete_time = 12 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.protobuf.TimestampOrBuilder getDeleteTimeOrBuilder();

  /**
   * <pre>
   * Content-Type of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.1.5][RFC 7231 §3.1.1.5].
   * If an object is stored without a Content-Type, it is served as
   * `application/octet-stream`.
   * </pre>
   *
   * <code>string content_type = 13;</code>
   * @return The contentType.
   */
  java.lang.String getContentType();
  /**
   * <pre>
   * Content-Type of the object data, matching
   * [https://tools.ietf.org/html/rfc7231#section-3.1.1.5][RFC 7231 §3.1.1.5].
   * If an object is stored without a Content-Type, it is served as
   * `application/octet-stream`.
   * </pre>
   *
   * <code>string content_type = 13;</code>
   * @return The bytes for contentType.
   */
  com.google.protobuf.ByteString
      getContentTypeBytes();

  /**
   * <pre>
   * Output only. The creation time of the object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 14 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the createTime field is set.
   */
  boolean hasCreateTime();
  /**
   * <pre>
   * Output only. The creation time of the object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 14 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The createTime.
   */
  com.google.protobuf.Timestamp getCreateTime();
  /**
   * <pre>
   * Output only. The creation time of the object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 14 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.protobuf.TimestampOrBuilder getCreateTimeOrBuilder();

  /**
   * <pre>
   * Output only. Number of underlying components that make up this object.
   * Components are accumulated by compose operations.
   * </pre>
   *
   * <code>int32 component_count = 15 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The componentCount.
   */
  int getComponentCount();

  /**
   * <pre>
   * Output only. Hashes for the data part of this object. This field is used
   * for output only and will be silently ignored if provided in requests.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums checksums = 16 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the checksums field is set.
   */
  boolean hasChecksums();
  /**
   * <pre>
   * Output only. Hashes for the data part of this object. This field is used
   * for output only and will be silently ignored if provided in requests.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums checksums = 16 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The checksums.
   */
  com.google.storage.v2.ObjectChecksums getChecksums();
  /**
   * <pre>
   * Output only. Hashes for the data part of this object. This field is used
   * for output only and will be silently ignored if provided in requests.
   * </pre>
   *
   * <code>.google.storage.v2.ObjectChecksums checksums = 16 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.storage.v2.ObjectChecksumsOrBuilder getChecksumsOrBuilder();

  /**
   * <pre>
   * Output only. The modification time of the object metadata.
   * Set initially to object creation time and then updated whenever any
   * metadata of the object changes. This includes changes made by a requester,
   * such as modifying custom metadata, as well as changes made by Cloud Storage
   * on behalf of a requester, such as changing the storage class based on an
   * Object Lifecycle Configuration.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 17 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the updateTime field is set.
   */
  boolean hasUpdateTime();
  /**
   * <pre>
   * Output only. The modification time of the object metadata.
   * Set initially to object creation time and then updated whenever any
   * metadata of the object changes. This includes changes made by a requester,
   * such as modifying custom metadata, as well as changes made by Cloud Storage
   * on behalf of a requester, such as changing the storage class based on an
   * Object Lifecycle Configuration.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 17 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The updateTime.
   */
  com.google.protobuf.Timestamp getUpdateTime();
  /**
   * <pre>
   * Output only. The modification time of the object metadata.
   * Set initially to object creation time and then updated whenever any
   * metadata of the object changes. This includes changes made by a requester,
   * such as modifying custom metadata, as well as changes made by Cloud Storage
   * on behalf of a requester, such as changing the storage class based on an
   * Object Lifecycle Configuration.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 17 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.protobuf.TimestampOrBuilder getUpdateTimeOrBuilder();

  /**
   * <pre>
   * Cloud KMS Key used to encrypt this object, if the object is encrypted by
   * such a key.
   * </pre>
   *
   * <code>string kms_key = 18 [(.google.api.resource_reference) = { ... }</code>
   * @return The kmsKey.
   */
  java.lang.String getKmsKey();
  /**
   * <pre>
   * Cloud KMS Key used to encrypt this object, if the object is encrypted by
   * such a key.
   * </pre>
   *
   * <code>string kms_key = 18 [(.google.api.resource_reference) = { ... }</code>
   * @return The bytes for kmsKey.
   */
  com.google.protobuf.ByteString
      getKmsKeyBytes();

  /**
   * <pre>
   * Output only. The time at which the object's storage class was last changed.
   * When the object is initially created, it will be set to time_created.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_storage_class_time = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the updateStorageClassTime field is set.
   */
  boolean hasUpdateStorageClassTime();
  /**
   * <pre>
   * Output only. The time at which the object's storage class was last changed.
   * When the object is initially created, it will be set to time_created.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_storage_class_time = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The updateStorageClassTime.
   */
  com.google.protobuf.Timestamp getUpdateStorageClassTime();
  /**
   * <pre>
   * Output only. The time at which the object's storage class was last changed.
   * When the object is initially created, it will be set to time_created.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_storage_class_time = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.protobuf.TimestampOrBuilder getUpdateStorageClassTimeOrBuilder();

  /**
   * <pre>
   * Whether an object is under temporary hold. While this flag is set to true,
   * the object is protected against deletion and overwrites.  A common use case
   * of this flag is regulatory investigations where objects need to be retained
   * while the investigation is ongoing. Note that unlike event-based hold,
   * temporary hold does not impact retention expiration time of an object.
   * </pre>
   *
   * <code>bool temporary_hold = 20;</code>
   * @return The temporaryHold.
   */
  boolean getTemporaryHold();

  /**
   * <pre>
   * A server-determined value that specifies the earliest time that the
   * object's retention period expires.
   * Note 1: This field is not provided for objects with an active event-based
   * hold, since retention expiration is unknown until the hold is removed.
   * Note 2: This value can be provided even when temporary hold is set (so that
   * the user can reason about policy without having to first unset the
   * temporary hold).
   * </pre>
   *
   * <code>.google.protobuf.Timestamp retention_expire_time = 21;</code>
   * @return Whether the retentionExpireTime field is set.
   */
  boolean hasRetentionExpireTime();
  /**
   * <pre>
   * A server-determined value that specifies the earliest time that the
   * object's retention period expires.
   * Note 1: This field is not provided for objects with an active event-based
   * hold, since retention expiration is unknown until the hold is removed.
   * Note 2: This value can be provided even when temporary hold is set (so that
   * the user can reason about policy without having to first unset the
   * temporary hold).
   * </pre>
   *
   * <code>.google.protobuf.Timestamp retention_expire_time = 21;</code>
   * @return The retentionExpireTime.
   */
  com.google.protobuf.Timestamp getRetentionExpireTime();
  /**
   * <pre>
   * A server-determined value that specifies the earliest time that the
   * object's retention period expires.
   * Note 1: This field is not provided for objects with an active event-based
   * hold, since retention expiration is unknown until the hold is removed.
   * Note 2: This value can be provided even when temporary hold is set (so that
   * the user can reason about policy without having to first unset the
   * temporary hold).
   * </pre>
   *
   * <code>.google.protobuf.Timestamp retention_expire_time = 21;</code>
   */
  com.google.protobuf.TimestampOrBuilder getRetentionExpireTimeOrBuilder();

  /**
   * <pre>
   * User-provided metadata, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; metadata = 22;</code>
   */
  int getMetadataCount();
  /**
   * <pre>
   * User-provided metadata, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; metadata = 22;</code>
   */
  boolean containsMetadata(
      java.lang.String key);
  /**
   * Use {@link #getMetadataMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getMetadata();
  /**
   * <pre>
   * User-provided metadata, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; metadata = 22;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getMetadataMap();
  /**
   * <pre>
   * User-provided metadata, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; metadata = 22;</code>
   */
  /* nullable */
java.lang.String getMetadataOrDefault(
      java.lang.String key,
      /* nullable */
java.lang.String defaultValue);
  /**
   * <pre>
   * User-provided metadata, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; metadata = 22;</code>
   */
  java.lang.String getMetadataOrThrow(
      java.lang.String key);

  /**
   * <pre>
   * Whether an object is under event-based hold.
   * An event-based hold is a way to force the retention of an object until
   * after some event occurs. Once the hold is released by explicitly setting
   * this field to false, the object will become subject to any bucket-level
   * retention policy, except that the retention duration will be calculated
   * from the time the event based hold was lifted, rather than the time the
   * object was created.
   *
   * In a WriteObject request, not setting this field implies that the value
   * should be taken from the parent bucket's "default_event_based_hold" field.
   * In a response, this field will always be set to true or false.
   * </pre>
   *
   * <code>optional bool event_based_hold = 23;</code>
   * @return Whether the eventBasedHold field is set.
   */
  boolean hasEventBasedHold();
  /**
   * <pre>
   * Whether an object is under event-based hold.
   * An event-based hold is a way to force the retention of an object until
   * after some event occurs. Once the hold is released by explicitly setting
   * this field to false, the object will become subject to any bucket-level
   * retention policy, except that the retention duration will be calculated
   * from the time the event based hold was lifted, rather than the time the
   * object was created.
   *
   * In a WriteObject request, not setting this field implies that the value
   * should be taken from the parent bucket's "default_event_based_hold" field.
   * In a response, this field will always be set to true or false.
   * </pre>
   *
   * <code>optional bool event_based_hold = 23;</code>
   * @return The eventBasedHold.
   */
  boolean getEventBasedHold();

  /**
   * <pre>
   * Output only. The owner of the object. This will always be the uploader of
   * the object.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 24 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return Whether the owner field is set.
   */
  boolean hasOwner();
  /**
   * <pre>
   * Output only. The owner of the object. This will always be the uploader of
   * the object.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 24 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   * @return The owner.
   */
  com.google.storage.v2.Owner getOwner();
  /**
   * <pre>
   * Output only. The owner of the object. This will always be the uploader of
   * the object.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 24 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.storage.v2.OwnerOrBuilder getOwnerOrBuilder();

  /**
   * <pre>
   * Metadata of Customer-Supplied Encryption Key, if the object is encrypted by
   * such a key.
   * </pre>
   *
   * <code>.google.storage.v2.CustomerEncryption customer_encryption = 25;</code>
   * @return Whether the customerEncryption field is set.
   */
  boolean hasCustomerEncryption();
  /**
   * <pre>
   * Metadata of Customer-Supplied Encryption Key, if the object is encrypted by
   * such a key.
   * </pre>
   *
   * <code>.google.storage.v2.CustomerEncryption customer_encryption = 25;</code>
   * @return The customerEncryption.
   */
  com.google.storage.v2.CustomerEncryption getCustomerEncryption();
  /**
   * <pre>
   * Metadata of Customer-Supplied Encryption Key, if the object is encrypted by
   * such a key.
   * </pre>
   *
   * <code>.google.storage.v2.CustomerEncryption customer_encryption = 25;</code>
   */
  com.google.storage.v2.CustomerEncryptionOrBuilder getCustomerEncryptionOrBuilder();

  /**
   * <pre>
   * A user-specified timestamp set on an object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp custom_time = 26;</code>
   * @return Whether the customTime field is set.
   */
  boolean hasCustomTime();
  /**
   * <pre>
   * A user-specified timestamp set on an object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp custom_time = 26;</code>
   * @return The customTime.
   */
  com.google.protobuf.Timestamp getCustomTime();
  /**
   * <pre>
   * A user-specified timestamp set on an object.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp custom_time = 26;</code>
   */
  com.google.protobuf.TimestampOrBuilder getCustomTimeOrBuilder();
}
