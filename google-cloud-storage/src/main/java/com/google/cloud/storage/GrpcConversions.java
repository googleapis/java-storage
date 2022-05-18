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

import com.google.cloud.storage.Conversions.Codec;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.HmacKeyMetadata;
import com.google.storage.v2.Object;
import java.util.concurrent.TimeUnit;

final class GrpcConversions {
  static final GrpcConversions INSTANCE = new GrpcConversions();

  private final Codec<?, ?> entityCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> objectAclCodec = Codec.of(Utils::todo, Utils::todo);
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
  private final Codec<?, ?> customerEncryptionCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<BlobId, Object> blobIdCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> blobInfoCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<?, ?> notificationInfoCodec = Codec.of(Utils::todo, Utils::todo);

  private GrpcConversions() {}

  Codec<?, ?> entity() {
    return todo();
  }

  Codec<?, ?> objectAcl() {
    return todo();
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

  Codec<?, ?> customerEncryption() {
    return todo();
  }

  Codec<BlobId, Object> blobId() {
    return blobIdCodec;
  }

  Codec<?, ?> blobInfo() {
    return todo();
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
}
