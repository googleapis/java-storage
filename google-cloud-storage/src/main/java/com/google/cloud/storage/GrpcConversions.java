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

import static com.google.cloud.storage.Utils.bucketNameCodec;
import static com.google.cloud.storage.Utils.durationMillisCodec;
import static com.google.cloud.storage.Utils.ifNonNull;
import static com.google.cloud.storage.Utils.lift;
import static com.google.cloud.storage.Utils.projectNameCodec;
import static com.google.cloud.storage.Utils.toImmutableListOf;
import static com.google.cloud.storage.Utils.todo;

import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.BlobInfo.CustomerEncryption;
import com.google.cloud.storage.BucketInfo.CustomPlacementConfig;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.PublicAccessPrevention;
import com.google.cloud.storage.Conversions.Codec;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.BucketAccessControl;
import com.google.storage.v2.HmacKeyMetadata;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.Owner;
import com.google.type.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

final class GrpcConversions {
  static final GrpcConversions INSTANCE = new GrpcConversions();

  private final Codec<Acl.Entity, String> entityCodec =
      Codec.of(this::entityEncode, this::entityDecode);
  private final Codec<Acl, ObjectAccessControl> objectAclCodec =
      Codec.of(this::objectAclEncode, this::objectAclDecode);
  private final Codec<Acl, BucketAccessControl> bucketAclCodec =
      Codec.of(this::bucketAclEncode, this::bucketAclDecode);
  private final Codec<HmacKey.HmacKeyMetadata, HmacKeyMetadata> hmacKeyMetadataCodec =
      Codec.of(this::hmacKeyMetadataEncode, this::hmacKeyMetadataDecode);
  private final Codec<?, ?> hmacKeyCodec = Codec.of(Utils::todo, Utils::todo);
  private final Codec<ServiceAccount, com.google.storage.v2.ServiceAccount> serviceAccountCodec =
      Codec.of(this::serviceAccountEncode, this::serviceAccountDecode);
  private final Codec<Cors, Bucket.Cors> corsCodec = Codec.of(this::corsEncode, this::corsDecode);
  private final Codec<BucketInfo.Logging, Bucket.Logging> loggingCodec =
      Codec.of(this::loggingEncode, this::loggingDecode);
  private final Codec<BucketInfo.IamConfiguration, Bucket.IamConfig> iamConfigurationCodec =
      Codec.of(this::iamConfigEncode, this::iamConfigDecode);
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

  Codec<Acl.Entity, String> entity() {
    return entityCodec;
  }

  Codec<Acl, ObjectAccessControl> objectAcl() {
    return objectAclCodec;
  }

  Codec<Acl, BucketAccessControl> bucketAcl() {
    return bucketAclCodec;
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

  Codec<Cors, Bucket.Cors> cors() {
    return corsCodec;
  }

  Codec<BucketInfo.Logging, Bucket.Logging> logging() {
    return loggingCodec;
  }

  Codec<BucketInfo.IamConfiguration, Bucket.IamConfig> iamConfiguration() {
    return iamConfigurationCodec;
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
    BucketInfo.Builder to = new BucketInfo.BuilderImpl(bucketNameCodec.decode(from.getName()));
    to.setProject(from.getProject());
    to.setGeneratedId(from.getBucketId());
    if (from.hasRetentionPolicy()) {
      Bucket.RetentionPolicy retentionPolicy = from.getRetentionPolicy();
      ifNonNull(retentionPolicy.getIsLocked(), to::setRetentionPolicyIsLocked);
      ifNonNull(
          retentionPolicy.getRetentionPeriod(),
          Utils.durationMillisCodec::decode,
          to::setRetentionPeriodDuration);
      ifNonNull(
          retentionPolicy.getEffectiveTime(),
          timestampCodec::decode,
          to::setRetentionEffectiveTimeOffsetDateTime);
    }
    ifNonNull(from.getLocation(), to::setLocation);
    ifNonNull(from.getLocationType(), to::setLocationType);
    ifNonNull(from.getMetageneration(), to::setMetageneration);
    if (from.hasBilling()) {
      Billing billing = from.getBilling();
      to.setRequesterPays(billing.getRequesterPays());
    }
    if (from.hasCreateTime()) {
      to.setCreateTimeOffsetDateTime(timestampCodec.decode(from.getCreateTime()));
    }
    if (from.hasUpdateTime()) {
      to.setUpdateTimeOffsetDateTime(timestampCodec.decode(from.getUpdateTime()));
    }
    if (from.hasEncryption()) {
      to.setDefaultKmsKeyName(from.getEncryption().getDefaultKmsKey());
    }
    if (!from.getRpo().isEmpty()) {
      to.setRpo(Rpo.valueOf(from.getRpo()));
    }
    if (!from.getStorageClass().isEmpty()) {
      to.setStorageClass(StorageClass.valueOf(from.getStorageClass()));
    }
    if (from.hasVersioning()) {
      to.setVersioningEnabled(from.getVersioning().getEnabled());
    }
    ifNonNull(from.getDefaultEventBasedHold(), to::setDefaultEventBasedHold);
    Map<String, String> labelsMap = from.getLabelsMap();
    if (!labelsMap.isEmpty()) {
      to.setLabels(labelsMap);
    }
    if (from.hasWebsite()) {
      to.setIndexPage(from.getWebsite().getMainPageSuffix());
      to.setNotFoundPage(from.getWebsite().getNotFoundPage());
    }
    if (from.hasLifecycle()) {
      to.setLifecycleRules(
          toImmutableListOf(lifecycleRuleCodec::decode).apply(from.getLifecycle().getRuleList()));
    }
    List<Bucket.Cors> corsList = from.getCorsList();
    if (!corsList.isEmpty()) {
      to.setCors(toImmutableListOf(corsCodec::decode).apply(corsList));
    }
    if (from.hasLogging()) {
      to.setLogging(loggingCodec.decode(from.getLogging()));
    }
    if (from.hasOwner()) {
      to.setOwner(entityCodec.decode(from.getOwner().getEntity()));
    }

    List<ObjectAccessControl> defaultObjectAclList = from.getDefaultObjectAclList();
    if (!defaultObjectAclList.isEmpty()) {
      to.setDefaultAcl(toImmutableListOf(objectAclCodec::decode).apply(defaultObjectAclList));
    }
    List<BucketAccessControl> bucketAclList = from.getAclList();
    if (!bucketAclList.isEmpty()) {
      to.setAcl(toImmutableListOf(bucketAclCodec::decode).apply(bucketAclList));
    }
    if (from.hasIamConfig()) {
      to.setIamConfiguration(iamConfigurationCodec.decode(from.getIamConfig()));
    }
    if (from.hasCustomPlacementConfig()) {
      Bucket.CustomPlacementConfig customPlacementConfig = from.getCustomPlacementConfig();
      to.setCustomPlacementConfig(
          CustomPlacementConfig.newBuilder()
              .setDataLocations(customPlacementConfig.getDataLocationsList())
              .build());
    }
    // TODO(frankyn): Add SelfLink when the field is available
    if (!from.getEtag().isEmpty()) {
      to.setEtag(from.getEtag());
    }
    return to.build();
  }

  private Bucket bucketInfoEncode(BucketInfo from) {
    Bucket.Builder to = Bucket.newBuilder();
    to.setName(bucketNameCodec.encode(from.getName()));
    ifNonNull(from.getGeneratedId(), to::setBucketId);
    if (from.getRetentionPeriodDuration() != null) {
      Bucket.RetentionPolicy.Builder retentionPolicyBuilder = to.getRetentionPolicyBuilder();
      ifNonNull(
          from.getRetentionPeriodDuration(),
          durationMillisCodec::encode,
          retentionPolicyBuilder::setRetentionPeriod);
      ifNonNull(from.retentionPolicyIsLocked(), retentionPolicyBuilder::setIsLocked);
      if (from.retentionPolicyIsLocked() == Boolean.TRUE) {
        ifNonNull(
            from.getRetentionEffectiveTimeOffsetDateTime(),
            timestampCodec::encode,
            retentionPolicyBuilder::setEffectiveTime);
      }
      to.setRetentionPolicy(retentionPolicyBuilder.build());
    }
    ifNonNull(from.getLocation(), to::setLocation);
    ifNonNull(from.getLocationType(), to::setLocationType);
    ifNonNull(from.getMetageneration(), to::setMetageneration);
    if (from.requesterPays() != null) {
      Bucket.Billing.Builder billingBuilder = Billing.newBuilder();
      ifNonNull(from.requesterPays(), billingBuilder::setRequesterPays);
      to.setBilling(billingBuilder.build());
    }
    ifNonNull(from.getCreateTimeOffsetDateTime(), timestampCodec::encode, to::setCreateTime);
    ifNonNull(from.getUpdateTimeOffsetDateTime(), timestampCodec::encode, to::setUpdateTime);
    if (from.getDefaultKmsKeyName() != null) {
      Bucket.Encryption.Builder encryptionBuilder = Bucket.Encryption.newBuilder();
      ifNonNull(from.getDefaultKmsKeyName(), encryptionBuilder::setDefaultKmsKey);
      to.setEncryption(encryptionBuilder.build());
    }
    if (from.getIndexPage() != null || from.getNotFoundPage() != null) {
      Bucket.Website.Builder websiteBuilder = Bucket.Website.newBuilder();
      ifNonNull(from.getIndexPage(), websiteBuilder::setMainPageSuffix);
      ifNonNull(from.getNotFoundPage(), websiteBuilder::setNotFoundPage);
      to.setWebsite(websiteBuilder.build());
    }
    ifNonNull(from.getRpo(), Rpo::toString, to::setRpo);
    ifNonNull(from.getStorageClass(), StorageClass::toString, to::setStorageClass);
    if (from.versioningEnabled() != null) {
      Bucket.Versioning.Builder versioningBuilder = Bucket.Versioning.newBuilder();
      ifNonNull(from.versioningEnabled(), versioningBuilder::setEnabled);
      to.setVersioning(versioningBuilder.build());
    }
    ifNonNull(from.getDefaultEventBasedHold(), to::setDefaultEventBasedHold);
    ifNonNull(from.getLabels(), to::putAllLabels);
    // Do not use, #getLifecycleRules, it can not return null, which is important to our logic here
    List<? extends LifecycleRule> lifecycleRules = from.lifecycleRules;
    if (lifecycleRules != null) {
      Bucket.Lifecycle.Builder lifecycleBuilder = Bucket.Lifecycle.newBuilder();
      if (!lifecycleRules.isEmpty()) {
        ImmutableSet<Bucket.Lifecycle.Rule> set =
            from.getLifecycleRules().stream()
                .map(lifecycleRuleCodec::encode)
                .collect(ImmutableSet.toImmutableSet());
        lifecycleBuilder.addAllRule(ImmutableList.copyOf(set));
      }
      to.setLifecycle(lifecycleBuilder.build());
    }
    ifNonNull(from.getLogging(), loggingCodec::encode, to::setLogging);
    ifNonNull(from.getCors(), toImmutableListOf(corsCodec::encode), to::addAllCors);
    ifNonNull(
        from.getOwner(),
        lift(entity()::encode).andThen(o -> Owner.newBuilder().setEntity(o).build()),
        to::setOwner);
    ifNonNull(
        from.getDefaultAcl(),
        toImmutableListOf(objectAclCodec::encode),
        to::addAllDefaultObjectAcl);
    ifNonNull(from.getAcl(), toImmutableListOf(bucketAclCodec::encode), to::addAllAcl);
    ifNonNull(from.getIamConfiguration(), iamConfigurationCodec::encode, to::setIamConfig);
    CustomPlacementConfig customPlacementConfig = from.getCustomPlacementConfig();
    if (customPlacementConfig != null && customPlacementConfig.getDataLocations() != null) {
      to.setCustomPlacementConfig(
          Bucket.CustomPlacementConfig.newBuilder()
              .addAllDataLocations(customPlacementConfig.getDataLocations())
              .build());
    }
    // TODO(frankyn): Add SelfLink when the field is available
    ifNonNull(from.getEtag(), to::setEtag);
    return to.build();
  }

  private Bucket.Logging loggingEncode(BucketInfo.Logging from) {
    Bucket.Logging.Builder to = Bucket.Logging.newBuilder();
    if (!from.getLogObjectPrefix().isEmpty()) {
      to.setLogObjectPrefix(from.getLogObjectPrefix());
    }
    ifNonNull(from.getLogBucket(), bucketNameCodec::encode, to::setLogBucket);
    return to.build();
  }

  private BucketInfo.Logging loggingDecode(Bucket.Logging from) {
    BucketInfo.Logging.Builder to = BucketInfo.Logging.newBuilder();
    String logObjectPrefix = from.getLogObjectPrefix();
    if (!logObjectPrefix.isEmpty()) {
      to.setLogObjectPrefix(logObjectPrefix);
    }
    String logBucket = from.getLogBucket();
    if (!logBucket.isEmpty()) {
      to.setLogBucket(bucketNameCodec.decode(logBucket));
    }
    return to.build();
  }

  private Bucket.Cors corsEncode(Cors from) {
    Bucket.Cors.Builder to = Bucket.Cors.newBuilder();
    to.setMaxAgeSeconds(from.getMaxAgeSeconds());
    to.addAllResponseHeader(from.getResponseHeaders());
    ifNonNull(from.getMethods(), toImmutableListOf(java.lang.Object::toString), to::addAllMethod);
    ifNonNull(from.getOrigins(), toImmutableListOf(java.lang.Object::toString), to::addAllOrigin);
    return to.build();
  }

  private Cors corsDecode(Bucket.Cors from) {
    Cors.Builder to = Cors.newBuilder().setMaxAgeSeconds(from.getMaxAgeSeconds());
    ifNonNull(
        from.getMethodList(),
        m ->
            m.stream()
                .map(String::toUpperCase)
                .map(HttpMethod::valueOf)
                .collect(ImmutableList.toImmutableList()),
        to::setMethods);
    ifNonNull(from.getOriginList(), toImmutableListOf(Cors.Origin::of), to::setOrigins);
    to.setResponseHeaders(from.getResponseHeaderList());
    return to.build();
  }

  private String entityEncode(Acl.Entity from) {
    if (from instanceof Acl.RawEntity) {
      return from.getValue();
    } else if (from instanceof Acl.User) {
      switch (from.getValue()) {
        case Acl.User.ALL_AUTHENTICATED_USERS:
          return Acl.User.ALL_AUTHENTICATED_USERS;
        case Acl.User.ALL_USERS:
          return Acl.User.ALL_USERS;
        default:
          break;
      }
    }
    // intentionally not an else so that if the default is hit above it will fall through to here
    return from.getType().name().toLowerCase() + "-" + from.getValue();
  }

  private Acl.Entity entityDecode(String from) {
    if (from.startsWith("user-")) {
      return new Acl.User(from.substring(5));
    }
    if (from.equals(Acl.User.ALL_USERS)) {
      return Acl.User.ofAllUsers();
    }
    if (from.equals(Acl.User.ALL_AUTHENTICATED_USERS)) {
      return Acl.User.ofAllAuthenticatedUsers();
    }
    if (from.startsWith("group-")) {
      return new Acl.Group(from.substring(6));
    }
    if (from.startsWith("domain-")) {
      return new Acl.Domain(from.substring(7));
    }
    if (from.startsWith("project-")) {
      int idx = from.indexOf('-', 8);
      String team = from.substring(8, idx);
      String projectId = from.substring(idx + 1);
      return new Acl.Project(Acl.Project.ProjectRole.valueOf(team), projectId);
    }
    return new Acl.RawEntity(from);
  }

  private Acl objectAclDecode(ObjectAccessControl from) {
    Acl.Role role = Acl.Role.valueOf(from.getRole());
    Acl.Entity entity = entityDecode(from.getEntity());
    Acl.Builder to = Acl.newBuilder(entity, role).setId(from.getId());
    if (!from.getEtag().isEmpty()) {
      to.setEtag(from.getEtag());
    }
    return to.build();
  }

  private ObjectAccessControl objectAclEncode(Acl from) {
    ObjectAccessControl.Builder to =
        ObjectAccessControl.newBuilder()
            .setEntity(entityEncode(from.getEntity()))
            .setRole(from.getRole().name())
            .setId(from.getId());
    ifNonNull(from.getEtag(), to::setEtag);
    return to.build();
  }

  private Acl bucketAclDecode(com.google.storage.v2.BucketAccessControl from) {
    Role role = Role.valueOf(from.getRole());
    Entity entity = entityDecode(from.getEntity());
    Acl.Builder to = Acl.newBuilder(entity, role).setId(from.getId());
    if (!from.getEtag().isEmpty()) {
      to.setEtag(from.getEtag());
    }
    return to.build();
  }

  private com.google.storage.v2.BucketAccessControl bucketAclEncode(Acl from) {
    BucketAccessControl.Builder to =
        BucketAccessControl.newBuilder()
            .setEntity(from.getEntity().toString())
            .setRole(from.getRole().toString())
            .setId(from.getId());
    ifNonNull(from.getEtag(), to::setEtag);
    return to.build();
  }

  private Bucket.IamConfig.UniformBucketLevelAccess ublaEncode(BucketInfo.IamConfiguration from) {
    Bucket.IamConfig.UniformBucketLevelAccess.Builder to =
        Bucket.IamConfig.UniformBucketLevelAccess.newBuilder();
    to.setEnabled(from.isUniformBucketLevelAccessEnabled());
    if (from.isUniformBucketLevelAccessEnabled() == Boolean.TRUE) {
      ifNonNull(
          from.getUniformBucketLevelAccessLockedTimeOffsetDateTime(),
          timestampCodec::encode,
          to::setLockTime);
    }
    return to.build();
  }

  private Bucket.IamConfig iamConfigEncode(BucketInfo.IamConfiguration from) {
    Bucket.IamConfig.Builder to = Bucket.IamConfig.newBuilder();
    to.setUniformBucketLevelAccess(ublaEncode(from));
    if (from.getPublicAccessPrevention() != null) {
      ifNonNull(from.getPublicAccessPrevention().getValue(), to::setPublicAccessPrevention);
    }
    return to.build();
  }

  private BucketInfo.IamConfiguration iamConfigDecode(Bucket.IamConfig from) {
    Bucket.IamConfig.UniformBucketLevelAccess ubla = from.getUniformBucketLevelAccess();

    BucketInfo.IamConfiguration.Builder to = BucketInfo.IamConfiguration.newBuilder();
    ifNonNull(ubla.getEnabled(), to::setIsUniformBucketLevelAccessEnabled);
    ifNonNull(
        ubla.getLockTime(),
        timestampCodec::decode,
        to::setUniformBucketLevelAccessLockedTimeOffsetDateTime);
    if (!from.getPublicAccessPrevention().isEmpty()) {
      to.setPublicAccessPrevention(PublicAccessPrevention.parse(from.getPublicAccessPrevention()));
    }
    return to.build();
  }

  @SuppressWarnings("deprecation")
  private Bucket.Lifecycle.Rule deleteRuleEncode(BucketInfo.DeleteRule from) {
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

  static final List<Descriptors.FieldDescriptor> SUPPORTED_CONDITIONS_DELETE_RULE =
      ImmutableList.of(
          Bucket.Lifecycle.Rule.Condition.getDescriptor()
              .findFieldByNumber(Bucket.Lifecycle.Rule.Condition.AGE_DAYS_FIELD_NUMBER),
          Bucket.Lifecycle.Rule.Condition.getDescriptor()
              .findFieldByNumber(Bucket.Lifecycle.Rule.Condition.CREATED_BEFORE_FIELD_NUMBER),
          Bucket.Lifecycle.Rule.Condition.getDescriptor()
              .findFieldByNumber(Bucket.Lifecycle.Rule.Condition.NUM_NEWER_VERSIONS_FIELD_NUMBER),
          Bucket.Lifecycle.Rule.Condition.getDescriptor()
              .findFieldByNumber(Bucket.Lifecycle.Rule.Condition.IS_LIVE_FIELD_NUMBER));

  private boolean isValidDeleteRule(Bucket.Lifecycle.Rule rule) {
    return rule.hasAction()
        && rule.getAction().getType().equals(BucketInfo.LifecycleRule.DeleteLifecycleAction.TYPE)
        && rule.getCondition().getAllFields().keySet().size() == 1
        && rule.getCondition().getAllFields().keySet().stream()
            .anyMatch(SUPPORTED_CONDITIONS_DELETE_RULE::contains);
  }

  @SuppressWarnings("deprecation")
  private BucketInfo.DeleteRule deleteRuleDecode(Bucket.Lifecycle.Rule from) {
    if (!isValidDeleteRule(from)) {
      throw new IllegalArgumentException("Rule is not a valid DeleteRule" + from);
    }
    Bucket.Lifecycle.Rule.Condition condition = from.getCondition();
    if (condition.hasAgeDays()) {
      return new BucketInfo.AgeDeleteRule(condition.getAgeDays());
    } else if (condition.hasCreatedBefore()) {
      Date date = condition.getCreatedBefore();
      return new BucketInfo.CreatedBeforeDeleteRule(
          OffsetDateTime.from(LocalDate.of(date.getYear(), date.getMonth(), date.getDay())));
    } else if (condition.hasNumNewerVersions()) {
      return new BucketInfo.NumNewerVersionsDeleteRule(condition.getNumNewerVersions());
    } else {
      return new BucketInfo.IsLiveDeleteRule(condition.getIsLive());
    }
  }

  private Bucket.Lifecycle.Rule lifecycleRuleEncode(BucketInfo.LifecycleRule from) {
    Bucket.Lifecycle.Rule.Builder to = Bucket.Lifecycle.Rule.newBuilder();
    to.setAction(ruleActionEncode(from.getAction()));
    to.setCondition(ruleConditionEncode(from.getCondition()));
    return to.build();
  }

  private Bucket.Lifecycle.Rule.Condition ruleConditionEncode(
      BucketInfo.LifecycleRule.LifecycleCondition from) {
    Bucket.Lifecycle.Rule.Condition.Builder to = Bucket.Lifecycle.Rule.Condition.newBuilder();
    if (from.getAge() != null) {
      to.setAgeDays(from.getAge());
    }
    if (from.getIsLive() != null) {
      to.setIsLive(from.getIsLive());
    }
    if (from.getNumberOfNewerVersions() != null) {
      to.setNumNewerVersions(from.getNumberOfNewerVersions());
    }
    if (from.getDaysSinceNoncurrentTime() != null) {
      to.setDaysSinceNoncurrentTime(from.getDaysSinceNoncurrentTime());
    }
    if (from.getDaysSinceCustomTime() != null) {
      to.setDaysSinceCustomTime(from.getDaysSinceCustomTime());
    }
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
    ifNonNull(from.getMatchesPrefix(), to::addAllMatchesPrefix);
    ifNonNull(from.getMatchesSuffix(), to::addAllMatchesSuffix);
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
        BucketInfo.LifecycleRule.LifecycleCondition.newBuilder();
    if (condition.hasAgeDays()) {
      conditionBuilder.setAge(condition.getAgeDays());
    }
    if (condition.hasCreatedBefore()) {
      conditionBuilder.setCreatedBeforeOffsetDateTime(
          odtDateCodec.nullable().decode(condition.getCreatedBefore()));
    }
    if (condition.hasIsLive()) {
      conditionBuilder.setIsLive(condition.getIsLive());
    }
    if (condition.hasNumNewerVersions()) {
      conditionBuilder.setNumberOfNewerVersions(condition.getNumNewerVersions());
    }
    if (condition.hasDaysSinceNoncurrentTime()) {
      conditionBuilder.setDaysSinceNoncurrentTime(condition.getDaysSinceNoncurrentTime());
    }
    if (condition.hasNoncurrentTimeBefore()) {
      conditionBuilder.setNoncurrentTimeBeforeOffsetDateTime(
          odtDateCodec.decode(condition.getNoncurrentTimeBefore()));
    }
    if (condition.hasCustomTimeBefore()) {
      conditionBuilder.setCustomTimeBeforeOffsetDateTime(
          odtDateCodec.decode(condition.getCustomTimeBefore()));
    }
    if (condition.hasDaysSinceCustomTime()) {
      conditionBuilder.setDaysSinceCustomTime(condition.getDaysSinceCustomTime());
    }
    ifNonNull(
        condition.getMatchesStorageClassList(),
        toImmutableListOf(StorageClass::valueOf),
        conditionBuilder::setMatchesStorageClass);
    conditionBuilder.setMatchesPrefix(condition.getMatchesPrefixList());
    conditionBuilder.setMatchesSuffix(condition.getMatchesSuffixList());
    return new BucketInfo.LifecycleRule(lifecycleAction, conditionBuilder.build());
  }

  private HmacKeyMetadata hmacKeyMetadataEncode(HmacKey.HmacKeyMetadata from) {
    HmacKeyMetadata.Builder to = HmacKeyMetadata.newBuilder();
    ifNonNull(from.getEtag(), to::setEtag);
    ifNonNull(from.getId(), to::setId);
    ifNonNull(from.getAccessId(), to::setAccessId);
    ifNonNull(from.getProjectId(), projectNameCodec::encode, to::setProject);
    ifNonNull(from.getServiceAccount(), ServiceAccount::getEmail, to::setServiceAccountEmail);
    ifNonNull(from.getState(), Enum::name, to::setState);
    ifNonNull(from.getCreateTimeOffsetDateTime(), timestampCodec::encode, to::setCreateTime);
    ifNonNull(from.getUpdateTimeOffsetDateTime(), timestampCodec::encode, to::setUpdateTime);
    return to.build();
  }

  private HmacKey.HmacKeyMetadata hmacKeyMetadataDecode(HmacKeyMetadata from) {
    HmacKey.HmacKeyMetadata.Builder to =
        HmacKey.HmacKeyMetadata.newBuilder(ServiceAccount.of(from.getServiceAccountEmail()))
            .setAccessId(from.getAccessId())
            .setCreateTimeOffsetDateTime(timestampCodec.decode(from.getCreateTime()))
            .setId(from.getId())
            .setProjectId(projectNameCodec.decode(from.getProject()))
            .setState(HmacKeyState.valueOf(from.getState()))
            .setUpdateTimeOffsetDateTime(timestampCodec.decode(from.getUpdateTime()));
    if (!from.getEtag().isEmpty()) {
      to.setEtag(from.getEtag());
    }
    return to.build();
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
    Object.Builder to = Object.newBuilder();
    ifNonNull(from.getBucket(), bucketNameCodec::encode, to::setBucket);
    ifNonNull(from.getName(), to::setName);
    ifNonNull(from.getGeneration(), to::setGeneration);
    return to.build();
  }

  private BlobId blobIdDecode(Object from) {
    return BlobId.of(from.getBucket(), from.getName(), from.getGeneration());
  }

  private Object blobInfoEncode(BlobInfo from) {
    Object.Builder toBuilder = Object.newBuilder();
    ifNonNull(from.getBucket(), bucketNameCodec::encode, toBuilder::setBucket);
    ifNonNull(from.getName(), toBuilder::setName);
    ifNonNull(from.getGeneration(), toBuilder::setGeneration);
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
    ifNonNull(from.getMetageneration(), toBuilder::setMetageneration);
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
    ifNonNull(from.getEtag(), toBuilder::setEtag);
    Entity entity = from.getOwner();
    if (entity != null) {
      toBuilder.setOwner(Owner.newBuilder().setEntity(entityEncode(entity)).build());
    }
    ifNonNull(from.getMetadata(), toBuilder::putAllMetadata);
    ifNonNull(from.getAcl(), toImmutableListOf(objectAcl()::encode), toBuilder::addAllAcl);
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
    if (from.hasChecksums()) {
      ObjectChecksums checksums = from.getChecksums();
      if (checksums.hasCrc32C()) {
        toBuilder.setCrc32c(crc32cCodec.encode(checksums.getCrc32C()));
      }
      if (!checksums.getMd5Hash().equals(ByteString.empty())) {
        toBuilder.setMd5(BaseEncoding.base64().encode(checksums.getMd5Hash().toByteArray()));
      }
    }
    ifNonNull(from.getMetageneration(), toBuilder::setMetageneration);
    if (from.hasDeleteTime()) {
      toBuilder.setDeleteTimeOffsetDateTime(timestampCodec.decode(from.getDeleteTime()));
    }
    if (from.hasUpdateTime()) {
      toBuilder.setUpdateTimeOffsetDateTime(timestampCodec.decode(from.getUpdateTime()));
    }
    if (from.hasCreateTime()) {
      toBuilder.setCreateTimeOffsetDateTime(timestampCodec.decode(from.getCreateTime()));
    }
    if (from.hasCustomTime()) {
      toBuilder.setCustomTimeOffsetDateTime(timestampCodec.decode(from.getCustomTime()));
    }
    if (from.hasCustomerEncryption()) {
      toBuilder.setCustomerEncryption(customerEncryptionCodec.decode(from.getCustomerEncryption()));
    }
    String storageClass = from.getStorageClass();
    if (!storageClass.isEmpty()) {
      toBuilder.setStorageClass(StorageClass.valueOf(storageClass));
    }
    if (from.hasUpdateStorageClassTime()) {
      toBuilder.setTimeStorageClassUpdatedOffsetDateTime(
          timestampCodec.decode(from.getUpdateStorageClassTime()));
    }
    if (!from.getKmsKey().isEmpty()) {
      toBuilder.setKmsKeyName(from.getKmsKey());
    }
    if (from.hasEventBasedHold()) {
      toBuilder.setEventBasedHold(from.getEventBasedHold());
    }
    toBuilder.setTemporaryHold(from.getTemporaryHold());
    if (from.hasRetentionExpireTime()) {
      toBuilder.setRetentionExpirationTimeOffsetDateTime(
          timestampCodec.decode(from.getRetentionExpireTime()));
    }
    if (!from.getMetadataMap().isEmpty()) {
      toBuilder.setMetadata(from.getMetadataMap());
    }
    if (from.hasOwner()) {
      Owner owner = from.getOwner();
      if (!owner.getEntity().isEmpty()) {
        toBuilder.setOwner(entityDecode(owner.getEntity()));
      }
    }
    if (!from.getEtag().isEmpty()) {
      toBuilder.setEtag(from.getEtag());
    }
    ifNonNull(from.getAclList(), toImmutableListOf(objectAcl()::decode), toBuilder::setAcl);
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
