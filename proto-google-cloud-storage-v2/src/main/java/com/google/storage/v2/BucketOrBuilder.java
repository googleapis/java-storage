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
// source: google/storage/v2/storage.proto

package com.google.storage.v2;

public interface BucketOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.Bucket)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Immutable. The name of the bucket.
   * Global buckets will be of the format `projects/{project}/buckets/{bucket}`.
   * Other sorts of buckets in the future are not guaranteed to follow this
   * pattern.
   * For globally unique bucket names, a `_` may be substituted for the project
   * ID.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return The name.
   */
  java.lang.String getName();
  /**
   *
   *
   * <pre>
   * Immutable. The name of the bucket.
   * Global buckets will be of the format `projects/{project}/buckets/{bucket}`.
   * Other sorts of buckets in the future are not guaranteed to follow this
   * pattern.
   * For globally unique bucket names, a `_` may be substituted for the project
   * ID.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * Output only. The user-chosen part of the bucket name. The `{bucket}` portion of the
   * `name` field. For globally unique buckets, this is equal to the "bucket
   * name" of other Cloud Storage APIs. Example: "pub".
   * </pre>
   *
   * <code>string bucket_id = 2 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The bucketId.
   */
  java.lang.String getBucketId();
  /**
   *
   *
   * <pre>
   * Output only. The user-chosen part of the bucket name. The `{bucket}` portion of the
   * `name` field. For globally unique buckets, this is equal to the "bucket
   * name" of other Cloud Storage APIs. Example: "pub".
   * </pre>
   *
   * <code>string bucket_id = 2 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The bytes for bucketId.
   */
  com.google.protobuf.ByteString getBucketIdBytes();

  /**
   *
   *
   * <pre>
   * Immutable. The project which owns this bucket.
   * Format: projects/{project_number}
   * Example: `projects/123456`.
   * </pre>
   *
   * <code>
   * string project = 3 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The project.
   */
  java.lang.String getProject();
  /**
   *
   *
   * <pre>
   * Immutable. The project which owns this bucket.
   * Format: projects/{project_number}
   * Example: `projects/123456`.
   * </pre>
   *
   * <code>
   * string project = 3 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for project.
   */
  com.google.protobuf.ByteString getProjectBytes();

  /**
   *
   *
   * <pre>
   * Output only. The metadata generation of this bucket.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>int64 metageneration = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The metageneration.
   */
  long getMetageneration();

  /**
   *
   *
   * <pre>
   * Immutable. The location of the bucket. Object data for objects in the bucket resides
   * in physical storage within this region.  Defaults to `US`. See the
   * [https://developers.google.com/storage/docs/concepts-techniques#specifyinglocations"][developer's
   * guide] for the authoritative list. Attempting to update this field after
   * the bucket is created will result in an error.
   * </pre>
   *
   * <code>string location = 5 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return The location.
   */
  java.lang.String getLocation();
  /**
   *
   *
   * <pre>
   * Immutable. The location of the bucket. Object data for objects in the bucket resides
   * in physical storage within this region.  Defaults to `US`. See the
   * [https://developers.google.com/storage/docs/concepts-techniques#specifyinglocations"][developer's
   * guide] for the authoritative list. Attempting to update this field after
   * the bucket is created will result in an error.
   * </pre>
   *
   * <code>string location = 5 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return The bytes for location.
   */
  com.google.protobuf.ByteString getLocationBytes();

  /**
   *
   *
   * <pre>
   * Output only. The location type of the bucket (region, dual-region, multi-region, etc).
   * </pre>
   *
   * <code>string location_type = 6 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The locationType.
   */
  java.lang.String getLocationType();
  /**
   *
   *
   * <pre>
   * Output only. The location type of the bucket (region, dual-region, multi-region, etc).
   * </pre>
   *
   * <code>string location_type = 6 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The bytes for locationType.
   */
  com.google.protobuf.ByteString getLocationTypeBytes();

  /**
   *
   *
   * <pre>
   * The bucket's default storage class, used whenever no storageClass is
   * specified for a newly-created object. This defines how objects in the
   * bucket are stored and determines the SLA and the cost of storage.
   * If this value is not specified when the bucket is created, it will default
   * to `STANDARD`. For more information, see
   * https://developers.google.com/storage/docs/storage-classes.
   * </pre>
   *
   * <code>string storage_class = 7;</code>
   *
   * @return The storageClass.
   */
  java.lang.String getStorageClass();
  /**
   *
   *
   * <pre>
   * The bucket's default storage class, used whenever no storageClass is
   * specified for a newly-created object. This defines how objects in the
   * bucket are stored and determines the SLA and the cost of storage.
   * If this value is not specified when the bucket is created, it will default
   * to `STANDARD`. For more information, see
   * https://developers.google.com/storage/docs/storage-classes.
   * </pre>
   *
   * <code>string storage_class = 7;</code>
   *
   * @return The bytes for storageClass.
   */
  com.google.protobuf.ByteString getStorageClassBytes();

  /**
   *
   *
   * <pre>
   * Access controls on the bucket.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.BucketAccessControl acl = 8;</code>
   */
  java.util.List<com.google.storage.v2.BucketAccessControl> getAclList();
  /**
   *
   *
   * <pre>
   * Access controls on the bucket.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.BucketAccessControl acl = 8;</code>
   */
  com.google.storage.v2.BucketAccessControl getAcl(int index);
  /**
   *
   *
   * <pre>
   * Access controls on the bucket.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.BucketAccessControl acl = 8;</code>
   */
  int getAclCount();
  /**
   *
   *
   * <pre>
   * Access controls on the bucket.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.BucketAccessControl acl = 8;</code>
   */
  java.util.List<? extends com.google.storage.v2.BucketAccessControlOrBuilder>
      getAclOrBuilderList();
  /**
   *
   *
   * <pre>
   * Access controls on the bucket.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.BucketAccessControl acl = 8;</code>
   */
  com.google.storage.v2.BucketAccessControlOrBuilder getAclOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * Default access controls to apply to new objects when no ACL is provided.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl default_object_acl = 9;</code>
   */
  java.util.List<com.google.storage.v2.ObjectAccessControl> getDefaultObjectAclList();
  /**
   *
   *
   * <pre>
   * Default access controls to apply to new objects when no ACL is provided.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl default_object_acl = 9;</code>
   */
  com.google.storage.v2.ObjectAccessControl getDefaultObjectAcl(int index);
  /**
   *
   *
   * <pre>
   * Default access controls to apply to new objects when no ACL is provided.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl default_object_acl = 9;</code>
   */
  int getDefaultObjectAclCount();
  /**
   *
   *
   * <pre>
   * Default access controls to apply to new objects when no ACL is provided.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl default_object_acl = 9;</code>
   */
  java.util.List<? extends com.google.storage.v2.ObjectAccessControlOrBuilder>
      getDefaultObjectAclOrBuilderList();
  /**
   *
   *
   * <pre>
   * Default access controls to apply to new objects when no ACL is provided.
   * If iamConfig.uniformBucketLevelAccess is enabled on this bucket,
   * requests to set, read, or modify acl is an error.
   * </pre>
   *
   * <code>repeated .google.storage.v2.ObjectAccessControl default_object_acl = 9;</code>
   */
  com.google.storage.v2.ObjectAccessControlOrBuilder getDefaultObjectAclOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * The bucket's lifecycle config. See
   * [https://developers.google.com/storage/docs/lifecycle]Lifecycle Management]
   * for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Lifecycle lifecycle = 10;</code>
   *
   * @return Whether the lifecycle field is set.
   */
  boolean hasLifecycle();
  /**
   *
   *
   * <pre>
   * The bucket's lifecycle config. See
   * [https://developers.google.com/storage/docs/lifecycle]Lifecycle Management]
   * for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Lifecycle lifecycle = 10;</code>
   *
   * @return The lifecycle.
   */
  com.google.storage.v2.Bucket.Lifecycle getLifecycle();
  /**
   *
   *
   * <pre>
   * The bucket's lifecycle config. See
   * [https://developers.google.com/storage/docs/lifecycle]Lifecycle Management]
   * for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Lifecycle lifecycle = 10;</code>
   */
  com.google.storage.v2.Bucket.LifecycleOrBuilder getLifecycleOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. The creation time of the bucket in
   * [https://tools.ietf.org/html/rfc3339][RFC 3339] format.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 11 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the createTime field is set.
   */
  boolean hasCreateTime();
  /**
   *
   *
   * <pre>
   * Output only. The creation time of the bucket in
   * [https://tools.ietf.org/html/rfc3339][RFC 3339] format.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 11 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The createTime.
   */
  com.google.protobuf.Timestamp getCreateTime();
  /**
   *
   *
   * <pre>
   * Output only. The creation time of the bucket in
   * [https://tools.ietf.org/html/rfc3339][RFC 3339] format.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 11 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.protobuf.TimestampOrBuilder getCreateTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's [https://www.w3.org/TR/cors/][Cross-Origin Resource Sharing]
   * (CORS) config.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Bucket.Cors cors = 12;</code>
   */
  java.util.List<com.google.storage.v2.Bucket.Cors> getCorsList();
  /**
   *
   *
   * <pre>
   * The bucket's [https://www.w3.org/TR/cors/][Cross-Origin Resource Sharing]
   * (CORS) config.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Bucket.Cors cors = 12;</code>
   */
  com.google.storage.v2.Bucket.Cors getCors(int index);
  /**
   *
   *
   * <pre>
   * The bucket's [https://www.w3.org/TR/cors/][Cross-Origin Resource Sharing]
   * (CORS) config.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Bucket.Cors cors = 12;</code>
   */
  int getCorsCount();
  /**
   *
   *
   * <pre>
   * The bucket's [https://www.w3.org/TR/cors/][Cross-Origin Resource Sharing]
   * (CORS) config.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Bucket.Cors cors = 12;</code>
   */
  java.util.List<? extends com.google.storage.v2.Bucket.CorsOrBuilder> getCorsOrBuilderList();
  /**
   *
   *
   * <pre>
   * The bucket's [https://www.w3.org/TR/cors/][Cross-Origin Resource Sharing]
   * (CORS) config.
   * </pre>
   *
   * <code>repeated .google.storage.v2.Bucket.Cors cors = 12;</code>
   */
  com.google.storage.v2.Bucket.CorsOrBuilder getCorsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * Output only. The modification time of the bucket.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 13 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the updateTime field is set.
   */
  boolean hasUpdateTime();
  /**
   *
   *
   * <pre>
   * Output only. The modification time of the bucket.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 13 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The updateTime.
   */
  com.google.protobuf.Timestamp getUpdateTime();
  /**
   *
   *
   * <pre>
   * Output only. The modification time of the bucket.
   * Attempting to set or update this field will result in a
   * [FieldViolation][google.rpc.BadRequest.FieldViolation].
   * </pre>
   *
   * <code>.google.protobuf.Timestamp update_time = 13 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.protobuf.TimestampOrBuilder getUpdateTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * The default value for event-based hold on newly created objects in this
   * bucket.  Event-based hold is a way to retain objects indefinitely until an
   * event occurs, signified by the
   * hold's release. After being released, such objects will be subject to
   * bucket-level retention (if any).  One sample use case of this flag is for
   * banks to hold loan documents for at least 3 years after loan is paid in
   * full. Here, bucket-level retention is 3 years and the event is loan being
   * paid in full. In this example, these objects will be held intact for any
   * number of years until the event has occurred (event-based hold on the
   * object is released) and then 3 more years after that. That means retention
   * duration of the objects begins from the moment event-based hold
   * transitioned from true to false.  Objects under event-based hold cannot be
   * deleted, overwritten or archived until the hold is removed.
   * </pre>
   *
   * <code>bool default_event_based_hold = 14;</code>
   *
   * @return The defaultEventBasedHold.
   */
  boolean getDefaultEventBasedHold();

  /**
   *
   *
   * <pre>
   * User-provided labels, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; labels = 15;</code>
   */
  int getLabelsCount();
  /**
   *
   *
   * <pre>
   * User-provided labels, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; labels = 15;</code>
   */
  boolean containsLabels(java.lang.String key);
  /** Use {@link #getLabelsMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getLabels();
  /**
   *
   *
   * <pre>
   * User-provided labels, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; labels = 15;</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getLabelsMap();
  /**
   *
   *
   * <pre>
   * User-provided labels, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; labels = 15;</code>
   */
  java.lang.String getLabelsOrDefault(java.lang.String key, java.lang.String defaultValue);
  /**
   *
   *
   * <pre>
   * User-provided labels, in key/value pairs.
   * </pre>
   *
   * <code>map&lt;string, string&gt; labels = 15;</code>
   */
  java.lang.String getLabelsOrThrow(java.lang.String key);

  /**
   *
   *
   * <pre>
   * The bucket's website config, controlling how the service behaves
   * when accessing bucket contents as a web site. See the
   * [https://cloud.google.com/storage/docs/static-website][Static Website
   * Examples] for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Website website = 16;</code>
   *
   * @return Whether the website field is set.
   */
  boolean hasWebsite();
  /**
   *
   *
   * <pre>
   * The bucket's website config, controlling how the service behaves
   * when accessing bucket contents as a web site. See the
   * [https://cloud.google.com/storage/docs/static-website][Static Website
   * Examples] for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Website website = 16;</code>
   *
   * @return The website.
   */
  com.google.storage.v2.Bucket.Website getWebsite();
  /**
   *
   *
   * <pre>
   * The bucket's website config, controlling how the service behaves
   * when accessing bucket contents as a web site. See the
   * [https://cloud.google.com/storage/docs/static-website][Static Website
   * Examples] for more information.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Website website = 16;</code>
   */
  com.google.storage.v2.Bucket.WebsiteOrBuilder getWebsiteOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's versioning config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Versioning versioning = 17;</code>
   *
   * @return Whether the versioning field is set.
   */
  boolean hasVersioning();
  /**
   *
   *
   * <pre>
   * The bucket's versioning config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Versioning versioning = 17;</code>
   *
   * @return The versioning.
   */
  com.google.storage.v2.Bucket.Versioning getVersioning();
  /**
   *
   *
   * <pre>
   * The bucket's versioning config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Versioning versioning = 17;</code>
   */
  com.google.storage.v2.Bucket.VersioningOrBuilder getVersioningOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's logging config, which defines the destination bucket
   * and name prefix (if any) for the current bucket's logs.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Logging logging = 18;</code>
   *
   * @return Whether the logging field is set.
   */
  boolean hasLogging();
  /**
   *
   *
   * <pre>
   * The bucket's logging config, which defines the destination bucket
   * and name prefix (if any) for the current bucket's logs.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Logging logging = 18;</code>
   *
   * @return The logging.
   */
  com.google.storage.v2.Bucket.Logging getLogging();
  /**
   *
   *
   * <pre>
   * The bucket's logging config, which defines the destination bucket
   * and name prefix (if any) for the current bucket's logs.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Logging logging = 18;</code>
   */
  com.google.storage.v2.Bucket.LoggingOrBuilder getLoggingOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. The owner of the bucket. This is always the project team's owner group.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return Whether the owner field is set.
   */
  boolean hasOwner();
  /**
   *
   *
   * <pre>
   * Output only. The owner of the bucket. This is always the project team's owner group.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The owner.
   */
  com.google.storage.v2.Owner getOwner();
  /**
   *
   *
   * <pre>
   * Output only. The owner of the bucket. This is always the project team's owner group.
   * </pre>
   *
   * <code>.google.storage.v2.Owner owner = 19 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   */
  com.google.storage.v2.OwnerOrBuilder getOwnerOrBuilder();

  /**
   *
   *
   * <pre>
   * Encryption config for a bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Encryption encryption = 20;</code>
   *
   * @return Whether the encryption field is set.
   */
  boolean hasEncryption();
  /**
   *
   *
   * <pre>
   * Encryption config for a bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Encryption encryption = 20;</code>
   *
   * @return The encryption.
   */
  com.google.storage.v2.Bucket.Encryption getEncryption();
  /**
   *
   *
   * <pre>
   * Encryption config for a bucket.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Encryption encryption = 20;</code>
   */
  com.google.storage.v2.Bucket.EncryptionOrBuilder getEncryptionOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's billing config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Billing billing = 21;</code>
   *
   * @return Whether the billing field is set.
   */
  boolean hasBilling();
  /**
   *
   *
   * <pre>
   * The bucket's billing config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Billing billing = 21;</code>
   *
   * @return The billing.
   */
  com.google.storage.v2.Bucket.Billing getBilling();
  /**
   *
   *
   * <pre>
   * The bucket's billing config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.Billing billing = 21;</code>
   */
  com.google.storage.v2.Bucket.BillingOrBuilder getBillingOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's retention policy. The retention policy enforces a minimum
   * retention time for all objects contained in the bucket, based on their
   * creation time. Any attempt to overwrite or delete objects younger than the
   * retention period will result in a PERMISSION_DENIED error.  An unlocked
   * retention policy can be modified or removed from the bucket via a
   * storage.buckets.update operation. A locked retention policy cannot be
   * removed or shortened in duration for the lifetime of the bucket.
   * Attempting to remove or decrease period of a locked retention policy will
   * result in a PERMISSION_DENIED error.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.RetentionPolicy retention_policy = 22;</code>
   *
   * @return Whether the retentionPolicy field is set.
   */
  boolean hasRetentionPolicy();
  /**
   *
   *
   * <pre>
   * The bucket's retention policy. The retention policy enforces a minimum
   * retention time for all objects contained in the bucket, based on their
   * creation time. Any attempt to overwrite or delete objects younger than the
   * retention period will result in a PERMISSION_DENIED error.  An unlocked
   * retention policy can be modified or removed from the bucket via a
   * storage.buckets.update operation. A locked retention policy cannot be
   * removed or shortened in duration for the lifetime of the bucket.
   * Attempting to remove or decrease period of a locked retention policy will
   * result in a PERMISSION_DENIED error.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.RetentionPolicy retention_policy = 22;</code>
   *
   * @return The retentionPolicy.
   */
  com.google.storage.v2.Bucket.RetentionPolicy getRetentionPolicy();
  /**
   *
   *
   * <pre>
   * The bucket's retention policy. The retention policy enforces a minimum
   * retention time for all objects contained in the bucket, based on their
   * creation time. Any attempt to overwrite or delete objects younger than the
   * retention period will result in a PERMISSION_DENIED error.  An unlocked
   * retention policy can be modified or removed from the bucket via a
   * storage.buckets.update operation. A locked retention policy cannot be
   * removed or shortened in duration for the lifetime of the bucket.
   * Attempting to remove or decrease period of a locked retention policy will
   * result in a PERMISSION_DENIED error.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.RetentionPolicy retention_policy = 22;</code>
   */
  com.google.storage.v2.Bucket.RetentionPolicyOrBuilder getRetentionPolicyOrBuilder();

  /**
   *
   *
   * <pre>
   * The bucket's IAM config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.IamConfig iam_config = 23;</code>
   *
   * @return Whether the iamConfig field is set.
   */
  boolean hasIamConfig();
  /**
   *
   *
   * <pre>
   * The bucket's IAM config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.IamConfig iam_config = 23;</code>
   *
   * @return The iamConfig.
   */
  com.google.storage.v2.Bucket.IamConfig getIamConfig();
  /**
   *
   *
   * <pre>
   * The bucket's IAM config.
   * </pre>
   *
   * <code>.google.storage.v2.Bucket.IamConfig iam_config = 23;</code>
   */
  com.google.storage.v2.Bucket.IamConfigOrBuilder getIamConfigOrBuilder();

  /**
   *
   *
   * <pre>
   * Immutable. The zone or zones from which the bucket is intended to use zonal quota.
   * Requests for data from outside the specified affinities are still allowed
   * but won't be able to use zonal quota. The values are case-insensitive.
   * Attempting to update this field after bucket is created will result in an
   * error.
   * </pre>
   *
   * <code>repeated string zone_affinity = 24 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return A list containing the zoneAffinity.
   */
  java.util.List<java.lang.String> getZoneAffinityList();
  /**
   *
   *
   * <pre>
   * Immutable. The zone or zones from which the bucket is intended to use zonal quota.
   * Requests for data from outside the specified affinities are still allowed
   * but won't be able to use zonal quota. The values are case-insensitive.
   * Attempting to update this field after bucket is created will result in an
   * error.
   * </pre>
   *
   * <code>repeated string zone_affinity = 24 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @return The count of zoneAffinity.
   */
  int getZoneAffinityCount();
  /**
   *
   *
   * <pre>
   * Immutable. The zone or zones from which the bucket is intended to use zonal quota.
   * Requests for data from outside the specified affinities are still allowed
   * but won't be able to use zonal quota. The values are case-insensitive.
   * Attempting to update this field after bucket is created will result in an
   * error.
   * </pre>
   *
   * <code>repeated string zone_affinity = 24 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @param index The index of the element to return.
   * @return The zoneAffinity at the given index.
   */
  java.lang.String getZoneAffinity(int index);
  /**
   *
   *
   * <pre>
   * Immutable. The zone or zones from which the bucket is intended to use zonal quota.
   * Requests for data from outside the specified affinities are still allowed
   * but won't be able to use zonal quota. The values are case-insensitive.
   * Attempting to update this field after bucket is created will result in an
   * error.
   * </pre>
   *
   * <code>repeated string zone_affinity = 24 [(.google.api.field_behavior) = IMMUTABLE];</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the zoneAffinity at the given index.
   */
  com.google.protobuf.ByteString getZoneAffinityBytes(int index);

  /**
   *
   *
   * <pre>
   * Reserved for future use.
   * </pre>
   *
   * <code>bool satisfies_pzs = 25;</code>
   *
   * @return The satisfiesPzs.
   */
  boolean getSatisfiesPzs();
}
