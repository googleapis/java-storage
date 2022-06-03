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
import static com.google.cloud.storage.Utils.lift;
import static com.google.cloud.storage.Utils.toImmutableListOf;
import static com.google.cloud.storage.Utils.todo;

import com.google.cloud.storage.BlobInfo.CustomerEncryption;
import com.google.cloud.storage.Conversions.Codec;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.HmacKeyMetadata;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ObjectChecksums;
import com.google.type.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.LogRecord;

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
  private final Codec<BucketInfo.LifecycleRule, Bucket.Lifecycle.Rule> lifecycleRuleCodec =
      Codec.of(this::lifecycleRuleEncode, this::lifecycleRuleDecode);
  private final Codec<BucketInfo.DeleteRule, Bucket.Lifecycle.Rule> deleteRuleCodec =
      Codec.of(this::deleteRuleEncode, this::deleteRuleDecode);
  private final Codec<BucketInfo, Bucket> bucketInfoCodec =
      Codec.of(this::bucketInfoEncode, this::bucketInfoDecode);
  private final Codec<CustomerEncryption, com.google.storage.v2.CustomerEncryption>
      customerEncryptionCodec =
          Codec.of(this::customerEncryptionEncode, this::customerEncryptionDecode);
  private final Codec<BlobId, Object> blobIdCodec =
      Codec.of(this::blobIdEncode, this::blobIdDecode);
  private final Codec<BlobInfo, Object> blobInfoCodec =
      Codec.of(this::blobInfoEncode, this::blobInfoDecode);
  private final Codec<?, ?> notificationInfoCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<Integer, String> crc32cCodec =
      Codec.of(this::crc32cEncode, this::crc32cDecode);

  @VisibleForTesting
  final Codec<OffsetDateTime, Timestamp> timestampCodec =
      Codec.of(
          odt ->
              Timestamp.newBuilder()
                  .setSeconds(odt.toEpochSecond())
                  .setNanos(odt.getNano())
                  .build(),
          t ->
              Instant.ofEpochSecond(t.getSeconds())
                  .plusNanos(t.getNanos())
                  .atOffset(ZoneOffset.UTC));

  @VisibleForTesting
  final Codec<OffsetDateTime, Date> odtDateCodec =
      Codec.of(
          odt -> {
            OffsetDateTime utc = odt.withOffsetSameInstant(ZoneOffset.UTC);
            return Date.newBuilder()
                .setYear(utc.getYear())
                .setMonth(utc.getMonthValue())
                .setDay(utc.getDayOfMonth())
                .build();
          },
          d ->
              LocalDate.of(d.getYear(), d.getMonth(), d.getDay())
                  .atStartOfDay()
                  .atOffset(ZoneOffset.UTC));

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

  Codec<BucketInfo.LifecycleRule, Bucket.Lifecycle.Rule> lifecycleRule() {
    return lifecycleRuleCodec;
  }

  Codec<BucketInfo.DeleteRule, Bucket.Lifecycle.Rule> deleteRule() {
    return deleteRuleCodec;
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
    to.setGeneratedId(from.getBucketId());
    Bucket.RetentionPolicy retentionPolicy = from.getRetentionPolicy();
    ifNonNull(retentionPolicy, Bucket.RetentionPolicy::getIsLocked, to::setRetentionPolicyIsLocked);
    ifNonNull(
        retentionPolicy,
        lift(Bucket.RetentionPolicy::getRetentionPeriod).andThen(Utils.durationMillisCodec::decode),
        to::setRetentionPeriodDuration);
    if (from.hasRetentionPolicy() && retentionPolicy.hasEffectiveTime()) {
      to.setRetentionEffectiveTimeOffsetDateTime(
          timestampCodec.decode(retentionPolicy.getEffectiveTime()));
    }
    ifNonNull(from.getBucketId(), to::setGeneratedId);
    ifNonNull(from.getLocation(), to::setLocation);
    ifNonNull(from.getLocationType(), to::setLocationType);
    ifNonNull(from.getMetageneration(), to::setMetageneration);
    ifNonNull(from.getBilling(), Billing::getRequesterPays, to::setRequesterPays);
    ifNonNull(from.getCreateTime(), timestampCodec::decode, to::setCreateTimeOffsetDateTime);
    ifNonNull(from.getUpdateTime(), timestampCodec::decode, to::setUpdateTimeOffsetDateTime);
    ifNonNull(from.getEncryption(), Bucket.Encryption::getDefaultKmsKey, to::setDefaultKmsKeyName);
    ifNonNull(from.getRpo(), Rpo::valueOf, to::setRpo);
    ifNonNull(from.getStorageClass(), StorageClass::valueOf, to::setStorageClass);
    ifNonNull(from.getVersioning(), Bucket.Versioning::getEnabled, to::setVersioningEnabled);
    ifNonNull(from.getDefaultEventBasedHold(), to::setDefaultEventBasedHold);
    ifNonNull(from.getLabels(), to::setLabels);
    if (from.hasWebsite()) {
      to.setIndexPage(from.getWebsite().getMainPageSuffix());
      to.setNotFoundPage(from.getWebsite().getNotFoundPage());
    }
    ifNonNull(
        from.getLifecycle(),
        lift(Bucket.Lifecycle::getRuleList).andThen(toImmutableListOf(lifecycleRule()::decode)),
        to::setLifecycleRules);
    // preserve mapping to deprecated property
    ifNonNull(
        from.getLifecycle(),
        lift(Bucket.Lifecycle::getRuleList).andThen(toImmutableListOf(deleteRule()::decode)),
        to::setDeleteRules);
    // TODO(frankyn): Add logging decoder
    // TODO(frankyn): Add entity decoder
    // TODO(frankyn): Add Cors decoder support
    // TODO(frnakyn): Add DefaultObjectAcl decoder support
    // TODO(frankyn): Add lifeycle decoder
    // TODO(frankyn): Add deleteRules decoder
    // TODO(frankyn): Add SelfLink when the field is available
    // TODO(frankyn): Add decoder for iamConfig
    // TODO(frankyn): Add Etag when support is available
    return to.build();
  }

  private Bucket bucketInfoEncode(BucketInfo from) {
    Bucket.Builder to = Bucket.newBuilder();
    to.setName(from.getName());
    to.setBucketId(from.getGeneratedId());
    Bucket.RetentionPolicy.Builder retentionPolicyBuilder = to.getRetentionPolicyBuilder();
    ifNonNull(
        from.getRetentionPeriodDuration(),
        Duration::getSeconds,
        retentionPolicyBuilder::setRetentionPeriod);
    ifNonNull(
        from.getRetentionEffectiveTimeOffsetDateTime(),
        timestampCodec::encode,
        retentionPolicyBuilder::setEffectiveTime);
    ifNonNull(from.retentionPolicyIsLocked(), retentionPolicyBuilder::setIsLocked);
    to.setRetentionPolicy(retentionPolicyBuilder.build());
    ifNonNull(from.getLocation(), to::setLocation);
    ifNonNull(from.getLocationType(), to::setLocationType);
    ifNonNull(from.getMetageneration(), to::setMetageneration);
    Bucket.Billing.Builder billingBuilder = Billing.newBuilder();
    ifNonNull(from.requesterPays(), billingBuilder::setRequesterPays);
    to.setBilling(billingBuilder.build());
    ifNonNull(from.getCreateTimeOffsetDateTime(), timestampCodec::encode, to::setCreateTime);
    ifNonNull(from.getUpdateTimeOffsetDateTime(), timestampCodec::encode, to::setUpdateTime);
    Bucket.Encryption.Builder encryptionBuilder = Bucket.Encryption.newBuilder();
    ifNonNull(from.getDefaultKmsKeyName(), encryptionBuilder::setDefaultKmsKey);
    to.setEncryption(encryptionBuilder.build());
    Bucket.Website.Builder websiteBuilder = Bucket.Website.newBuilder();
    ifNonNull(from.getIndexPage(), websiteBuilder::setMainPageSuffix);
    ifNonNull(from.getNotFoundPage(), websiteBuilder::setNotFoundPage);
    to.setWebsite(websiteBuilder.build());
    ifNonNull(from.getRpo(), Rpo::toString, to::setRpo);
    ifNonNull(from.getStorageClass(), StorageClass::toString, to::setStorageClass);
    Bucket.Versioning.Builder versioningBuilder = Bucket.Versioning.newBuilder();
    ifNonNull(from.versioningEnabled(), versioningBuilder::setEnabled);
    to.setVersioning(versioningBuilder.build());
    ifNonNull(from.getDefaultEventBasedHold(), to::setDefaultEventBasedHold);
    ifNonNull(from.getLabels(), to::putAllLabels);
    // preserve mapping to deprecated property
    Bucket.Lifecycle.Builder lifecycleBuilder = Bucket.Lifecycle.newBuilder();
    ifNonNull(
        from.getLifecycleRules(),
        Utils.toImmutableListOf(lifecycleRule()::encode),
        lifecycleBuilder::addAllRule);
    //    ifNonNull(
    //        from.getDeleteRules(),
    //        Utils.toImmutableListOf(deleteRule()::encode),
    //        lifecycleBuilder::addAllRule);
    to.setLifecycle(lifecycleBuilder.build());
    // TODO(frankyn): Add logging decoder
    // TODO(frankyn): Add entity decoder
    // TODO(frankyn): Add Cors decoder support
    // TODO(frnakyn): Add DefaultObjectAcl decoder support
    // TODO(frankyn): Add lifeycle decoder
    // TODO(frankyn): Add deleteRules decoder
    // TODO(frankyn): Add SelfLink when the field is available
    // TODO(frankyn): Add decoder for iamConfig
    // TODO(frankyn): Add Etag when support is avialable
    return to.build();
  }

  @SuppressWarnings("deprecation")
  private Bucket.Lifecycle.Rule deleteRuleEncode(BucketInfo.DeleteRule from) {
    if (from instanceof BucketInfo.RawDeleteRule) {
      Bucket.Lifecycle.Rule rule =
          Bucket.Lifecycle.Rule.newBuilder()
              .setAction(Bucket.Lifecycle.Rule.Action.newBuilder().setType(from.getType().name()))
              .build();
      String msg =
          "The lifecycle condition "
              + resolveRuleActionType(rule)
              + " is not currently supported. Please update to the latest version of google-cloud-java."
              + " Also, use LifecycleRule rather than the deprecated DeleteRule.";
      // manually construct a log record, so we maintain class name and method name
      // from the old implicit values.
      LogRecord record = new LogRecord(Level.WARNING, msg);
      record.setSourceClassName(BucketInfo.RawDeleteRule.class.getName());
      record.setSourceMethodName("populateCondition");
      BucketInfo.log.log(record);
      return rule;
    }
    Bucket.Lifecycle.Rule.Builder to = Bucket.Lifecycle.Rule.newBuilder();
    to.setAction(
        Bucket.Lifecycle.Rule.Action.newBuilder().setType(BucketInfo.DeleteRule.SUPPORTED_ACTION));
    Bucket.Lifecycle.Rule.Condition.Builder condition =
        Bucket.Lifecycle.Rule.Condition.newBuilder();
    if (from instanceof BucketInfo.CreatedBeforeDeleteRule) {
      BucketInfo.CreatedBeforeDeleteRule r = (BucketInfo.CreatedBeforeDeleteRule) from;
      if (r.getTime() != null) {
        condition.setCreatedBefore(
            Date.newBuilder()
                .setYear(r.getTime().getYear())
                .setMonth(r.getTime().getMonthValue())
                .setDay(r.getTime().getDayOfMonth())
                .build());
      }
    } else if (from instanceof BucketInfo.AgeDeleteRule) {
      BucketInfo.AgeDeleteRule r = (BucketInfo.AgeDeleteRule) from;
      condition.setAgeDays(r.getDaysToLive());
    } else if (from instanceof BucketInfo.NumNewerVersionsDeleteRule) {
      BucketInfo.NumNewerVersionsDeleteRule r = (BucketInfo.NumNewerVersionsDeleteRule) from;
      condition.setNumNewerVersions(r.getNumNewerVersions());
    } else if (from instanceof BucketInfo.IsLiveDeleteRule) {
      BucketInfo.IsLiveDeleteRule r = (BucketInfo.IsLiveDeleteRule) from;
      condition.setIsLive(r.isLive());
    } // else would be RawDeleteRule which is handled above
    to.setCondition(condition);
    return to.build();
  }

  private String resolveRuleActionType(Bucket.Lifecycle.Rule rule) {
    if (rule != null && rule.getAction() != null) {
      return rule.getAction().getType();
    } else {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  private BucketInfo.DeleteRule deleteRuleDecode(Bucket.Lifecycle.Rule from) {
    if (from.getAction() != null
        && BucketInfo.DeleteRule.SUPPORTED_ACTION.endsWith(resolveRuleActionType(from))) {
      Bucket.Lifecycle.Rule.Condition condition = from.getCondition();
      Integer age = condition.getAgeDays();
      if (age != null) {
        return new BucketInfo.AgeDeleteRule(age);
      }
      Date date = condition.getCreatedBefore();
      if (date != null) {
        return new BucketInfo.CreatedBeforeDeleteRule(
            OffsetDateTime.from(LocalDate.of(date.getYear(), date.getMonth(), date.getDay())));
      }
      Integer numNewerVersions = condition.getNumNewerVersions();
      if (numNewerVersions != null) {
        return new BucketInfo.NumNewerVersionsDeleteRule(numNewerVersions);
      }
      Boolean isLive = condition.getIsLive();
      if (isLive != null) {
        return new BucketInfo.IsLiveDeleteRule(isLive);
      }
    }
    return new BucketInfo.RawDeleteRule(
        new com.google.api.services.storage.model.Bucket.Lifecycle.Rule()
            .setAction(
                new com.google.api.services.storage.model.Bucket.Lifecycle.Rule.Action()
                    .setType(resolveRuleActionType(from))));
  }

  private Bucket.Lifecycle.Rule lifecycleRuleEncode(BucketInfo.LifecycleRule from) {
    Bucket.Lifecycle.Rule.Builder to = Bucket.Lifecycle.Rule.newBuilder();
    to.setAction(ruleActionEncode(from.getLifecycleAction()));
    to.setCondition(ruleConditionEncode(from.getLifecycleCondition()));
    return to.build();
  }

  private Bucket.Lifecycle.Rule.Condition ruleConditionEncode(
      BucketInfo.LifecycleRule.LifecycleCondition from) {
    Bucket.Lifecycle.Rule.Condition.Builder to =
        Bucket.Lifecycle.Rule.Condition.newBuilder()
            .setAgeDays(from.getAge())
            .setIsLive(from.getIsLive())
            .setNumNewerVersions(from.getNumberOfNewerVersions())
            .setDaysSinceNoncurrentTime(from.getDaysSinceNoncurrentTime())
            .setDaysSinceCustomTime(from.getDaysSinceCustomTime());
    ifNonNull(from.getCreatedBeforeOffsetDateTime(), odtDateCodec::encode, to::setCreatedBefore);
    ifNonNull(
        from.getNoncurrentTimeBeforeOffsetDateTime(),
        odtDateCodec::encode,
        to::setNoncurrentTimeBefore);
    ifNonNull(
        from.getCustomTimeBeforeOffsetDateTime(), odtDateCodec::encode, to::setCustomTimeBefore);
    ifNonNull(
        from.getMatchesStorageClass(),
        toImmutableListOf(StorageClass::toString),
        to::addAllMatchesStorageClass);
    return to.build();
  }

  private Bucket.Lifecycle.Rule.Action ruleActionEncode(
      BucketInfo.LifecycleRule.LifecycleAction from) {
    Bucket.Lifecycle.Rule.Action.Builder to =
        Bucket.Lifecycle.Rule.Action.newBuilder().setType(from.getActionType());
    if (from.getActionType().equals(BucketInfo.LifecycleRule.SetStorageClassLifecycleAction.TYPE)) {
      to.setStorageClass(
          ((BucketInfo.LifecycleRule.SetStorageClassLifecycleAction) from)
              .getStorageClass()
              .toString());
    }
    return to.build();
  }

  private BucketInfo.LifecycleRule lifecycleRuleDecode(Bucket.Lifecycle.Rule from) {
    BucketInfo.LifecycleRule.LifecycleAction lifecycleAction;

    Bucket.Lifecycle.Rule.Action action = from.getAction();

    switch (action.getType()) {
      case BucketInfo.LifecycleRule.DeleteLifecycleAction.TYPE:
        lifecycleAction = BucketInfo.LifecycleRule.LifecycleAction.newDeleteAction();
        break;
      case BucketInfo.LifecycleRule.SetStorageClassLifecycleAction.TYPE:
        lifecycleAction =
            BucketInfo.LifecycleRule.LifecycleAction.newSetStorageClassAction(
                StorageClass.valueOf(action.getStorageClass()));
        break;
      default:
        BucketInfo.log.warning(
            "The lifecycle action "
                + action.getType()
                + " is not supported by this version of the library. "
                + "Attempting to update with this rule may cause errors. Please "
                + "update to the latest version of google-cloud-storage.");
        lifecycleAction =
            BucketInfo.LifecycleRule.LifecycleAction.newLifecycleAction("Unknown action");
    }

    Bucket.Lifecycle.Rule.Condition condition = from.getCondition();

    BucketInfo.LifecycleRule.LifecycleCondition.Builder conditionBuilder =
        BucketInfo.LifecycleRule.LifecycleCondition.newBuilder()
            .setAge(condition.getAgeDays())
            .setCreateBeforeOffsetDateTime(odtDateCodec.decode(condition.getCreatedBefore()))
            .setIsLive(condition.getIsLive())
            .setNumberOfNewerVersions(condition.getNumNewerVersions())
            .setDaysSinceNoncurrentTime(condition.getDaysSinceNoncurrentTime())
            .setNoncurrentTimeBeforeOffsetDateTime(
                odtDateCodec.decode(condition.getNoncurrentTimeBefore()))
            .setCustomTimeBeforeOffsetDateTime(odtDateCodec.decode(condition.getCustomTimeBefore()))
            .setDaysSinceCustomTime(condition.getDaysSinceCustomTime());
    ifNonNull(
        condition.getMatchesStorageClassList(),
        toImmutableListOf(StorageClass::valueOf),
        conditionBuilder::setMatchesStorageClass);

    return new BucketInfo.LifecycleRule(lifecycleAction, conditionBuilder.build());
  }

  private HmacKeyMetadata hmacKeyMetadataEncode(HmacKey.HmacKeyMetadata from) {
    HmacKeyMetadata.Builder to = HmacKeyMetadata.newBuilder();
    to.setAccessId(from.getAccessId());
    // TODO etag
    to.setId(from.getId());
    to.setProject(from.getProjectId());
    ifNonNull(from.getServiceAccount(), ServiceAccount::getEmail, to::setServiceAccountEmail);
    ifNonNull(from.getState(), java.lang.Object::toString, to::setState);
    ifNonNull(from.getCreateTimeOffsetDateTime(), timestampCodec::encode, to::setCreateTime);
    ifNonNull(from.getUpdateTimeOffsetDateTime(), timestampCodec::encode, to::setUpdateTime);
    return to.build();
  }

  private HmacKey.HmacKeyMetadata hmacKeyMetadataDecode(HmacKeyMetadata from) {
    return HmacKey.HmacKeyMetadata.newBuilder(ServiceAccount.of(from.getServiceAccountEmail()))
        .setAccessId(from.getAccessId())
        .setCreateTimeOffsetDateTime(timestampCodec.decode(from.getCreateTime()))
        .setId(from.getId())
        .setProjectId(from.getProject())
        .setState(HmacKey.HmacKeyState.valueOf(from.getState()))
        .setUpdateTimeOffsetDateTime(timestampCodec.decode(from.getUpdateTime()))
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
        .setKeySha256Bytes(ByteString.copyFrom(BaseEncoding.base64().decode(from.getKeySha256())))
        .build();
  }

  private CustomerEncryption customerEncryptionDecode(
      com.google.storage.v2.CustomerEncryption from) {
    return new CustomerEncryption(
        from.getEncryptionAlgorithm(),
        BaseEncoding.base64().encode(from.getKeySha256Bytes().toByteArray()));
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
        objectChecksums.setMd5Hash(
            ByteString.copyFrom(BaseEncoding.base64().decode(from.getMd5())));
      }
      if (from.getCrc32c() != null) {
        objectChecksums.setCrc32C(crc32cCodec.decode(from.getCrc32c()));
      }
      toBuilder.setChecksums(objectChecksums.build());
    }
    toBuilder.setMetageneration(from.getMetageneration());
    ifNonNull(from.getDeleteTimeOffsetDateTime(), timestampCodec::encode, toBuilder::setDeleteTime);
    ifNonNull(from.getUpdateTimeOffsetDateTime(), timestampCodec::encode, toBuilder::setUpdateTime);
    ifNonNull(from.getCreateTimeOffsetDateTime(), timestampCodec::encode, toBuilder::setCreateTime);
    ifNonNull(from.getCustomTimeOffsetDateTime(), timestampCodec::encode, toBuilder::setCustomTime);
    ifNonNull(
        from.getCustomerEncryption(),
        customerEncryptionCodec::encode,
        toBuilder::setCustomerEncryption);
    ifNonNull(from.getStorageClass(), StorageClass::toString, toBuilder::setStorageClass);
    ifNonNull(
        from.getTimeStorageClassUpdatedOffsetDateTime(),
        timestampCodec::encode,
        toBuilder::setUpdateStorageClassTime);
    ifNonNull(from.getKmsKeyName(), toBuilder::setKmsKey);
    ifNonNull(from.getEventBasedHold(), toBuilder::setEventBasedHold);
    ifNonNull(from.getTemporaryHold(), toBuilder::setTemporaryHold);
    ifNonNull(
        from.getRetentionExpirationTimeOffsetDateTime(),
        timestampCodec::encode,
        toBuilder::setRetentionExpireTime);
    // TODO(sydmunro): Add Selflink when available
    // TODO(sydmunro): Add etag when available
    // TODO(sydmunro): Add Owner
    // TODO(sydmunro): Add user metadata
    // TODO(sydmunro): Object ACL
    return toBuilder.build();
  }

  private BlobInfo blobInfoDecode(Object from) {
    BlobInfo.Builder toBuilder =
        BlobInfo.newBuilder(BlobId.of(from.getBucket(), from.getName(), from.getGeneration()));
    ifNonNull(from.getCacheControl(), toBuilder::setCacheControl);
    ifNonNull(from.getSize(), toBuilder::setSize);
    ifNonNull(from.getContentType(), toBuilder::setContentType);
    ifNonNull(from.getContentEncoding(), toBuilder::setContentEncoding);
    ifNonNull(from.getContentDisposition(), toBuilder::setContentDisposition);
    ifNonNull(from.getContentLanguage(), toBuilder::setContentLanguage);
    ifNonNull(from.getComponentCount(), toBuilder::setComponentCount);
    if (from.getChecksums() != null) {
      if (from.getChecksums().hasCrc32C()) {
        toBuilder.setCrc32c(crc32cCodec.encode(from.getChecksums().getCrc32C()));
      }
      if (from.getChecksums().getMd5Hash() != null) {
        toBuilder.setMd5(
            BaseEncoding.base64().encode(from.getChecksums().getMd5Hash().toByteArray()));
      }
    }
    ifNonNull(from.getMetageneration(), toBuilder::setMetageneration);
    ifNonNull(from.getDeleteTime(), timestampCodec::decode, toBuilder::setDeleteTimeOffsetDateTime);
    ifNonNull(from.getUpdateTime(), timestampCodec::decode, toBuilder::setUpdateTimeOffsetDateTime);
    ifNonNull(from.getCreateTime(), timestampCodec::decode, toBuilder::setCreateTimeOffsetDateTime);
    ifNonNull(from.getCustomTime(), timestampCodec::decode, toBuilder::setCustomTimeOffsetDateTime);
    ifNonNull(
        from.getCustomerEncryption(),
        customerEncryptionCodec::decode,
        toBuilder::setCustomerEncryption);
    ifNonNull(from.getStorageClass(), StorageClass::valueOf, toBuilder::setStorageClass);
    ifNonNull(
        from.getUpdateStorageClassTime(),
        timestampCodec::decode,
        toBuilder::setTimeStorageClassUpdatedOffsetDateTime);
    ifNonNull(from.getKmsKey(), toBuilder::setKmsKeyName);
    ifNonNull(from.getEventBasedHold(), toBuilder::setEventBasedHold);
    ifNonNull(from.getTemporaryHold(), toBuilder::setTemporaryHold);
    ifNonNull(
        from.getRetentionExpireTime(),
        timestampCodec::decode,
        toBuilder::setRetentionExpirationTimeOffsetDateTime);
    return toBuilder.build();
  }

  private int crc32cDecode(String from) {
    byte[] decodeCrc32c = BaseEncoding.base64().decode(from);
    return Ints.fromByteArray(decodeCrc32c);
  }

  private String crc32cEncode(int from) {
    return BaseEncoding.base64().encode(Ints.toByteArray(from));
  }
}
