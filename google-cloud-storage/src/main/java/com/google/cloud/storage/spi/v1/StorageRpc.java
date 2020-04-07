/*
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage.spi.v1;

import com.google.api.core.InternalApi;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.HmacKey;
import com.google.api.services.storage.model.HmacKeyMetadata;
import com.google.api.services.storage.model.Notification;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Policy;
import com.google.api.services.storage.model.ServiceAccount;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.services.storage.model.TestIamPermissionsResponse;
import com.google.cloud.ServiceRpc;
import com.google.cloud.Tuple;
import com.google.cloud.storage.StorageException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@InternalApi
public interface StorageRpc extends ServiceRpc {

  // These options are part of the Google Cloud storage header options
  enum Option {
    PREDEFINED_ACL("predefinedAcl"),
    PREDEFINED_DEFAULT_OBJECT_ACL("predefinedDefaultObjectAcl"),
    IF_METAGENERATION_MATCH("ifMetagenerationMatch"),
    IF_METAGENERATION_NOT_MATCH("ifMetagenerationNotMatch"),
    IF_GENERATION_MATCH("ifGenerationMatch"),
    IF_GENERATION_NOT_MATCH("ifGenerationNotMatch"),
    IF_SOURCE_METAGENERATION_MATCH("ifSourceMetagenerationMatch"),
    IF_SOURCE_METAGENERATION_NOT_MATCH("ifSourceMetagenerationNotMatch"),
    IF_SOURCE_GENERATION_MATCH("ifSourceGenerationMatch"),
    IF_SOURCE_GENERATION_NOT_MATCH("ifSourceGenerationNotMatch"),
    IF_DISABLE_GZIP_CONTENT("disableGzipContent"),
    PREFIX("prefix"),
    PROJECT_ID("projectId"),
    PROJECTION("projection"),
    MAX_RESULTS("maxResults"),
    PAGE_TOKEN("pageToken"),
    DELIMITER("delimiter"),
    VERSIONS("versions"),
    FIELDS("fields"),
    CUSTOMER_SUPPLIED_KEY("customerSuppliedKey"),
    USER_PROJECT("userProject"),
    KMS_KEY_NAME("kmsKeyName"),
    SERVICE_ACCOUNT_EMAIL("serviceAccount"),
    SHOW_DELETED_KEYS("showDeletedKeys"),
    REQUESTED_POLICY_VERSION("optionsRequestedPolicyVersion");

    private final String value;

    Option(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }

    @SuppressWarnings("unchecked")
    <T> T get(Map<Option, ?> options) {
      return (T) options.get(this);
    }

    String getString(Map<Option, ?> options) {
      return get(options);
    }

    Long getLong(Map<Option, ?> options) {
      return get(options);
    }

    Boolean getBoolean(Map<Option, ?> options) {
      return get(options);
    }
  }

  class RewriteRequest {

    public final StorageObject source;
    public final Map<StorageRpc.Option, ?> sourceOptions;
    public final boolean overrideInfo;
    public final StorageObject target;
    public final Map<StorageRpc.Option, ?> targetOptions;
    public final Long megabytesRewrittenPerCall;

    public RewriteRequest(
        StorageObject source,
        Map<StorageRpc.Option, ?> sourceOptions,
        boolean overrideInfo,
        StorageObject target,
        Map<StorageRpc.Option, ?> targetOptions,
        Long megabytesRewrittenPerCall) {
      this.source = source;
      this.sourceOptions = sourceOptions;
      this.overrideInfo = overrideInfo;
      this.target = target;
      this.targetOptions = targetOptions;
      this.megabytesRewrittenPerCall = megabytesRewrittenPerCall;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof RewriteRequest)) {
        return false;
      }
      final RewriteRequest other = (RewriteRequest) obj;
      return Objects.equals(this.source, other.source)
          && Objects.equals(this.sourceOptions, other.sourceOptions)
          && Objects.equals(this.overrideInfo, other.overrideInfo)
          && Objects.equals(this.target, other.target)
          && Objects.equals(this.targetOptions, other.targetOptions)
          && Objects.equals(this.megabytesRewrittenPerCall, other.megabytesRewrittenPerCall);
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          source, sourceOptions, overrideInfo, target, targetOptions, megabytesRewrittenPerCall);
    }
  }

  class RewriteResponse {

    public final RewriteRequest rewriteRequest;
    public final StorageObject result;
    public final long blobSize;
    public final boolean isDone;
    public final String rewriteToken;
    public final long totalBytesRewritten;

    public RewriteResponse(
        RewriteRequest rewriteRequest,
        StorageObject result,
        long blobSize,
        boolean isDone,
        String rewriteToken,
        long totalBytesRewritten) {
      this.rewriteRequest = rewriteRequest;
      this.result = result;
      this.blobSize = blobSize;
      this.isDone = isDone;
      this.rewriteToken = rewriteToken;
      this.totalBytesRewritten = totalBytesRewritten;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof RewriteResponse)) {
        return false;
      }
      final RewriteResponse other = (RewriteResponse) obj;
      return Objects.equals(this.rewriteRequest, other.rewriteRequest)
          && Objects.equals(this.result, other.result)
          && Objects.equals(this.rewriteToken, other.rewriteToken)
          && this.blobSize == other.blobSize
          && Objects.equals(this.isDone, other.isDone)
          && this.totalBytesRewritten == other.totalBytesRewritten;
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          rewriteRequest, result, blobSize, isDone, rewriteToken, totalBytesRewritten);
    }
  }

  /**
   * Creates a new bucket.
   *
   * @throws StorageException upon failure
   */
  Bucket create(Bucket bucket, Map<Option, ?> options);

  /**
   * Creates a new storage object.
   *
   * @throws StorageException upon failure
   */
  StorageObject create(StorageObject object, InputStream content, Map<Option, ?> options);

  /**
   * Lists the project's buckets.
   *
   * @throws StorageException upon failure
   */
  Tuple<String, Iterable<Bucket>> list(Map<Option, ?> options);

  /**
   * Lists the bucket's blobs.
   *
   * @throws StorageException upon failure
   */
  Tuple<String, Iterable<StorageObject>> list(String bucket, Map<Option, ?> options);

  /**
   * Returns the requested bucket or {@code null} if not found.
   *
   * @throws StorageException upon failure
   */
  Bucket get(Bucket bucket, Map<Option, ?> options);

  /**
   * Returns the requested storage object or {@code null} if not found.
   *
   * @throws StorageException upon failure
   */
  StorageObject get(StorageObject object, Map<Option, ?> options);

  /**
   * Updates bucket information.
   *
   * @throws StorageException upon failure
   */
  Bucket patch(Bucket bucket, Map<Option, ?> options);

  /**
   * Updates the storage object's information. Original metadata are merged with metadata in the
   * provided {@code storageObject}.
   *
   * @throws StorageException upon failure
   */
  StorageObject patch(StorageObject storageObject, Map<Option, ?> options);

  /**
   * Deletes the requested bucket.
   *
   * @return {@code true} if the bucket was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean delete(Bucket bucket, Map<Option, ?> options);

  /**
   * Deletes the requested storage object.
   *
   * @return {@code true} if the storage object was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean delete(StorageObject object, Map<Option, ?> options);

  /** Creates an empty batch. */
  RpcBatch createBatch();

  /**
   * Sends a compose request.
   *
   * @throws StorageException upon failure
   */
  StorageObject compose(
      Iterable<StorageObject> sources, StorageObject target, Map<Option, ?> targetOptions);

  /**
   * Reads all the bytes from a storage object.
   *
   * @throws StorageException upon failure
   */
  byte[] load(StorageObject storageObject, Map<Option, ?> options);

  /**
   * Reads the given amount of bytes from a storage object at the given position.
   *
   * @throws StorageException upon failure
   */
  Tuple<String, byte[]> read(StorageObject from, Map<Option, ?> options, long position, int bytes);

  /**
   * Reads all the bytes from a storage object at the given position in to outputstream using direct
   * download.
   *
   * @return number of bytes downloaded, returns 0 if position higher than length.
   * @throws StorageException upon failure
   */
  long read(StorageObject from, Map<Option, ?> options, long position, OutputStream outputStream);

  /**
   * Opens a resumable upload channel for a given storage object.
   *
   * @throws StorageException upon failure
   */
  String open(StorageObject object, Map<Option, ?> options);

  /**
   * Opens a resumable upload channel for a given signedURL.
   *
   * @throws StorageException upon failure
   */
  String open(String signedURL);

  /**
   * Writes the provided bytes to a storage object at the provided location.
   *
   * @throws StorageException upon failure
   */
  void write(
      String uploadId,
      byte[] toWrite,
      int toWriteOffset,
      long destOffset,
      int length,
      boolean last);

  /**
   * Sends a rewrite request to open a rewrite channel.
   *
   * @throws StorageException upon failure
   */
  RewriteResponse openRewrite(RewriteRequest rewriteRequest);

  /**
   * Continues rewriting on an already open rewrite channel.
   *
   * @throws StorageException upon failure
   */
  RewriteResponse continueRewrite(RewriteResponse previousResponse);

  /**
   * Returns the ACL entry for the specified entity on the specified bucket or {@code null} if not
   * found.
   *
   * @throws StorageException upon failure
   */
  BucketAccessControl getAcl(String bucket, String entity, Map<Option, ?> options);

  /**
   * Deletes the ACL entry for the specified entity on the specified bucket.
   *
   * @return {@code true} if the ACL was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean deleteAcl(String bucket, String entity, Map<Option, ?> options);

  /**
   * Creates a new ACL entry on the specified bucket.
   *
   * @throws StorageException upon failure
   */
  BucketAccessControl createAcl(BucketAccessControl acl, Map<Option, ?> options);

  /**
   * Updates an ACL entry on the specified bucket.
   *
   * @throws StorageException upon failure
   */
  BucketAccessControl patchAcl(BucketAccessControl acl, Map<Option, ?> options);

  /**
   * Lists the ACL entries for the provided bucket.
   *
   * @throws StorageException upon failure
   */
  List<BucketAccessControl> listAcls(String bucket, Map<Option, ?> options);

  /**
   * Returns the default object ACL entry for the specified entity on the specified bucket or {@code
   * null} if not found.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl getDefaultAcl(String bucket, String entity);

  /**
   * Deletes the default object ACL entry for the specified entity on the specified bucket.
   *
   * @return {@code true} if the ACL was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean deleteDefaultAcl(String bucket, String entity);

  /**
   * Creates a new default object ACL entry on the specified bucket.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl createDefaultAcl(ObjectAccessControl acl);

  /**
   * Updates a default object ACL entry on the specified bucket.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl patchDefaultAcl(ObjectAccessControl acl);

  /**
   * Lists the default object ACL entries for the provided bucket.
   *
   * @throws StorageException upon failure
   */
  List<ObjectAccessControl> listDefaultAcls(String bucket);

  /**
   * Returns the ACL entry for the specified entity on the specified object or {@code null} if not
   * found.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl getAcl(String bucket, String object, Long generation, String entity);

  /**
   * Deletes the ACL entry for the specified entity on the specified object.
   *
   * @return {@code true} if the ACL was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean deleteAcl(String bucket, String object, Long generation, String entity);

  /**
   * Creates a new ACL entry on the specified object.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl createAcl(ObjectAccessControl acl);

  /**
   * Updates an ACL entry on the specified object.
   *
   * @throws StorageException upon failure
   */
  ObjectAccessControl patchAcl(ObjectAccessControl acl);

  /**
   * Lists the ACL entries for the provided object.
   *
   * @throws StorageException upon failure
   */
  List<ObjectAccessControl> listAcls(String bucket, String object, Long generation);

  /**
   * Creates a new HMAC key for the provided service account email.
   *
   * @throws StorageException upon failure
   */
  HmacKey createHmacKey(String serviceAccountEmail, Map<Option, ?> options);

  /**
   * Lists the HMAC keys for the provided service account email.
   *
   * @throws StorageException upon failure
   */
  Tuple<String, Iterable<HmacKeyMetadata>> listHmacKeys(Map<Option, ?> options);

  /**
   * Updates an HMAC key for the provided metadata object and returns the updated object. Only
   * updates the State field.
   *
   * @throws StorageException upon failure
   */
  HmacKeyMetadata updateHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options);

  /**
   * Returns the HMAC key associated with the provided access id.
   *
   * @throws StorageException upon failure
   */
  HmacKeyMetadata getHmacKey(String accessId, Map<Option, ?> options);

  /**
   * Deletes the HMAC key associated with the provided metadata object.
   *
   * @throws StorageException upon failure
   */
  void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options);

  /**
   * Returns the IAM policy for the specified bucket.
   *
   * @throws StorageException upon failure
   */
  Policy getIamPolicy(String bucket, Map<Option, ?> options);

  /**
   * Updates the IAM policy for the specified bucket.
   *
   * @throws StorageException upon failure
   */
  Policy setIamPolicy(String bucket, Policy policy, Map<Option, ?> options);

  /**
   * Tests whether the caller holds the specified permissions for the specified bucket.
   *
   * @throws StorageException upon failure
   */
  TestIamPermissionsResponse testIamPermissions(
      String bucket, List<String> permissions, Map<Option, ?> options);

  /**
   * Deletes the notification with the specified name on the specified object.
   *
   * @return {@code true} if the notification was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  boolean deleteNotification(String bucket, String notification);

  /**
   * List the notifications for the provided bucket.
   *
   * @return a list of {@link Notification} objects that exist on the bucket.
   * @throws StorageException upon failure
   */
  List<Notification> listNotifications(String bucket);

  /**
   * Creates a notification with the specified entity on the specified bucket.
   *
   * @return the notification that was created.
   * @throws StorageException upon failure
   */
  Notification createNotification(String bucket, Notification notification);

  /**
   * Lock retention policy for the provided bucket.
   *
   * @return a {@code Bucket} object of the locked bucket
   * @throws StorageException upon failure
   */
  Bucket lockRetentionPolicy(Bucket bucket, Map<Option, ?> options);

  /**
   * Returns the service account associated with the given project.
   *
   * @return the ID of the project to fetch the service account for.
   * @throws StorageException upon failure
   */
  ServiceAccount getServiceAccount(String projectId);
}
