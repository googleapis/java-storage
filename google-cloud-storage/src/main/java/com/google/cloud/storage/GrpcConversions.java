/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage;

import static com.google.cloud.storage.Utils.ifNonNull;
import static com.google.cloud.storage.Utils.todo;

import com.google.cloud.storage.BlobInfo.CustomerEncryption;
import com.google.cloud.storage.Conversions.Codec;
import com.google.protobuf.Timestamp;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.HmacKeyMetadata;
import com.google.storage.v2.Object;
import java.util.concurrent.TimeUnit;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ObjectChecksums;

final class GrpcConversions {
  static final GrpcConversions INSTANCE = new GrpcConversions();

  private final Codec<?, ?> entityCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<Acl, ObjectAccessControl> objectAclCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> bucketAclCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<HmacKey.HmacKeyMetadata, HmacKeyMetadata> hmacKeyMetadataCodec =
      Codec.of(this::hmacKeyMetadataEncode, this::hmacKeyMetadataDecode);
  private final Codec<?, ?> hmacKeyCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<ServiceAccount, com.google.storage.v2.ServiceAccount> serviceAccountCodec =
      Codec.of(this::serviceAccountEncode, this::serviceAccountDecode);
  private final Codec<?, ?> corsCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> loggingCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> iamConfigurationCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> lifecycleRuleCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> deleteRuleCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<BucketInfo, Bucket> bucketInfoCodec =
      Codec.of(Utils::todo, this::bucketInfoDecode);
  private final Codec<CustomerEncryption, com.google.storage.v2.CustomerEncryption>
      customerEncryptionCodec =
          Codec.of(this::customerEncryptionEncode, this::customerEncryptionDecode);
  private final Codec<BlobId, Object> blobIdCodec =
      Codec.of(this::blobIdEncode, this::blobIdDecode);
  private final Codec<BlobInfo, Object> blobInfoCodec = Codec.of(this::blobInfoEncode, Utils::todo);
  private final Codec<?, ?> notificationInfoCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<Integer, String> crc32cCodec = Codec.of(Utils::todo, this::crc32cDecode);

  private GrpcConversions() {}

  Codec<?, ?> entity() {
    return todo();
  }

  Codec<Acl, ObjectAccessControl> objectAcl() {
    return objectAclCodec;
  }

  Codec<?, ?> bucketAcl() {
    return todo();
  }

  Codec<HmacKey.HmacKeyMetadata, HmacKeyMetadata> hmacKeyMetadata() {
    return hmacKeyMetadataCodec;
  }

  Codec<?, ?> hmacKey() {
    return todo();
  }

  Codec<ServiceAccount, com.google.storage.v2.ServiceAccount> serviceAccount() {
    return serviceAccountCodec;
  }

  Codec<?, ?> cors() {
    return todo();
  }

  Codec<?, ?> logging() {
    return todo();
  }

  Codec<?, ?> iamConfiguration() {
    return todo();
  }

  Codec<?, ?> lifecycleRule() {
    return todo();
  }

  Codec<?, ?> deleteRule() {
    return todo();
  }

  Codec<BucketInfo, Bucket> bucketInfo() {
    return bucketInfoCodec;
  }

  Codec<CustomerEncryption, com.google.storage.v2.CustomerEncryption> customerEncryption() {
    return customerEncryptionCodec;
  }

  Codec<BlobId, Object> blobId() {
    return blobIdCodec;
  }

  Codec<BlobInfo, Object> blobInfo() {
    return blobInfoCodec;
  }

  Codec<?, ?> notificationInfo() {
    return todo();
  }

  private BucketInfo bucketInfoDecode(Bucket from) {
    BucketInfo.Builder to = BucketInfo.newBuilder(from.getName());
    Bucket.RetentionPolicy retentionPolicy = from.getRetentionPolicy();
    ifNonNull(retentionPolicy, Bucket.RetentionPolicy::getIsLocked, to::setRetentionPolicyIsLocked);
    ifNonNull(retentionPolicy, Bucket.RetentionPolicy::getRetentionPeriod, to::setRetentionPeriod);
    if (from.hasRetentionPolicy() && retentionPolicy.hasEffectiveTime()) {
      to.setRetentionEffectiveTime(retentionPolicy.getEffectiveTime().getSeconds());
    }
    ifNonNull(from.getBucketId(), to::setGeneratedId);
    ifNonNull(from.getLocation(), to::setLocation);
    ifNonNull(from.getLocationType(), to::setLocationType);
    ifNonNull(from.getMetageneration(), to::setMetageneration);
    ifNonNull(from.getBilling(), Billing::getRequesterPays, to::setRequesterPays);
    ifNonNull(from.getCreateTime(), Timestamps::toMillis, to::setCreateTime);
    ifNonNull(from.getUpdateTime(), Timestamps::toMillis, to::setUpdateTime);
    ifNonNull(from.getEncryption(), Bucket.Encryption::getDefaultKmsKey, to::setDefaultKmsKeyName);
    ifNonNull(from.getWebsite(), Bucket.Website::getMainPageSuffix, to::setIndexPage);
    ifNonNull(from.getWebsite(), Bucket.Website::getNotFoundPage, to::setNotFoundPage);
    ifNonNull(from.getRpo(), Rpo::valueOf, to::setRpo);
    ifNonNull(from.getStorageClass(), StorageClass::valueOf, to::setStorageClass);
    ifNonNull(from.getVersioning(), Bucket.Versioning::getEnabled, to::setVersioningEnabled);
    ifNonNull(from.getDefaultEventBasedHold(), to::setDefaultEventBasedHold);
    ifNonNull(from.getLabels(), to::setLabels);
    ifNonNull(from.getBilling(), Bucket.Billing::getRequesterPays, to::setRequesterPays);
    // TODO(frankyn): Add logging decoder
    // TODO(frankyn): Add entity decoder
    // TODO(frankyn): Add lifeycle decoder
    // TODO(frankyn): Add deleteRules decoder
    // TODO(frankyn): Add SelfLink when the field is available
    // TODO(frankyn): Add decoder for iamConfig
    // TODO(frankyn): Add Etag when support is avialable
    // TODO(frankyn): Add Cors decoder support
    // TODO(frnakyn): Add DefaultObjectAcl decoder support
    return to.build();
  }

  private HmacKeyMetadata hmacKeyMetadataEncode(HmacKey.HmacKeyMetadata from) {
    HmacKeyMetadata.Builder to = HmacKeyMetadata.newBuilder();
    to.setAccessId(from.getAccessId());
    // TODO etag
    to.setId(from.getId());
    to.setProject(from.getProjectId());
    ifNonNull(from.getServiceAccount(), ServiceAccount::getEmail, to::setServiceAccountEmail);
    ifNonNull(from.getState(), java.lang.Object::toString, to::setState);
    if (from.getCreateTime() != null) {
      to.setCreateTime(
          Timestamp.newBuilder().setSeconds(TimeUnit.MILLISECONDS.toSeconds(from.getCreateTime())));
    }
    if (from.getUpdateTime() != null) {
      to.setUpdateTime(
          Timestamp.newBuilder().setSeconds(TimeUnit.MILLISECONDS.toSeconds(from.getCreateTime())));
    }
    return to.build();
  }

  private HmacKey.HmacKeyMetadata hmacKeyMetadataDecode(HmacKeyMetadata from) {
    return HmacKey.HmacKeyMetadata.newBuilder(ServiceAccount.of(from.getServiceAccountEmail()))
        .setAccessId(from.getAccessId())
        .setCreateTime(TimeUnit.SECONDS.toMillis(from.getCreateTime().getSeconds()))
        .setId(from.getId())
        .setProjectId(from.getProject())
        .setState(HmacKey.HmacKeyState.valueOf(from.getState()))
        .setUpdateTime(TimeUnit.SECONDS.toMillis(from.getUpdateTime().getSeconds()))
        .build();
  }

  private com.google.storage.v2.ServiceAccount serviceAccountEncode(ServiceAccount from) {
    return com.google.storage.v2.ServiceAccount.newBuilder()
        .setEmailAddress(from.getEmail())
        .build();
  }

  private ServiceAccount serviceAccountDecode(com.google.storage.v2.ServiceAccount from) {
    return ServiceAccount.of(from.getEmailAddress());
  }

  private com.google.storage.v2.CustomerEncryption customerEncryptionEncode(
      CustomerEncryption from) {
    return com.google.storage.v2.CustomerEncryption.newBuilder()
        .setEncryptionAlgorithm(from.getEncryptionAlgorithm())
        .setKeySha256Bytes(ByteString.copyFrom(from.getKeySha256().getBytes()))
        .build();
  }

  private CustomerEncryption customerEncryptionDecode(
      com.google.storage.v2.CustomerEncryption from) {
    return new CustomerEncryption(
        from.getEncryptionAlgorithm(), from.getKeySha256Bytes().toString());
  }

  private Object blobIdEncode(BlobId from) {
    return Object.newBuilder()
        .setName(from.getName())
        .setBucket(from.getBucket())
        .setGeneration(from.getGeneration())
        .build();
  }

  private BlobId blobIdDecode(Object from) {
    return BlobId.of(from.getBucket(), from.getName(), from.getGeneration());
  }

  private Object blobInfoEncode(BlobInfo from) {
    Object.Builder toBuilder =
        Object.newBuilder()
            .setBucket(from.getBucket())
            .setName(from.getName())
            .setGeneration(from.getGeneration());
    ifNonNull(from.getCacheControl(), toBuilder::setCacheControl);
    ifNonNull(from.getSize(), toBuilder::setSize);
    ifNonNull(from.getContentType(), toBuilder::setContentType);
    ifNonNull(from.getContentEncoding(), toBuilder::setContentEncoding);
    ifNonNull(from.getContentDisposition(), toBuilder::setContentDisposition);
    ifNonNull(from.getContentLanguage(), toBuilder::setContentLanguage);
    ifNonNull(from.getComponentCount(), toBuilder::setComponentCount);
    if (from.getMd5() != null || from.getCrc32c() != null) {
      ObjectChecksums.Builder objectChecksums = ObjectChecksums.newBuilder();
      if (from.getMd5() != null) {
        objectChecksums.setMd5Hash(ByteString.copyFrom(from.getMd5().getBytes()));
      } else if (from.getCrc32c() != null) {
        objectChecksums.setCrc32C(crc32cDecode(from.getCrc32c()));
      }
      toBuilder.setChecksums(objectChecksums.build());
    }
    toBuilder.setMetageneration(from.getMetageneration());
    ifNonNull(from.getDeleteTime(), Timestamps::fromMillis, toBuilder::setDeleteTime);
    ifNonNull(from.getUpdateTime(), Timestamps::fromMillis, toBuilder::setUpdateTime);
    ifNonNull(from.getCreateTime(), Timestamps::fromMillis, toBuilder::setCreateTime);
    ifNonNull(from.getCustomTime(), Timestamps::fromMillis, toBuilder::setCustomTime);
    ifNonNull(
        from.getCustomerEncryption(),
        this::customerEncryptionEncode,
        toBuilder::setCustomerEncryption);
    ifNonNull(from.getStorageClass(), StorageClass::toString, toBuilder::setStorageClass);
    ifNonNull(
        from.getTimeStorageClassUpdated(),
        Timestamps::fromMillis,
        toBuilder::setUpdateStorageClassTime);
    ifNonNull(from.getKmsKeyName(), toBuilder::setKmsKey);
    ifNonNull(from.getEventBasedHold(), toBuilder::setEventBasedHold);
    ifNonNull(from.getTemporaryHold(), toBuilder::setTemporaryHold);
    ifNonNull(
        from.getRetentionExpirationTime(),
        Timestamps::fromMillis,
        toBuilder::setRetentionExpireTime);
    // TODO(sydmunro): Add Selflink when available
    // TODO(sydmunro): Add etag when available
    // TODO(sydmunro): Add Owner
    // TODO(sydmunro): Add user metadata
    // TODO(sydmunro): Object ACL
    return toBuilder.build();
  }

  private BlobInfo blobInfoDecode(Object from) {
    return todo();
  }

  private int crc32cDecode(String from) {
    byte[] decodeCrc32c = BaseEncoding.base64().decode(from);
    return Ints.fromByteArray(decodeCrc32c);
  }
}
