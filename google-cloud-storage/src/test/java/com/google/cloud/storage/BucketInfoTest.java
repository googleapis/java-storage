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

package com.google.cloud.storage;

import static com.google.cloud.storage.Acl.Project.ProjectRole.VIEWERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Bucket.Lifecycle;
import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import com.google.cloud.storage.Acl.Project;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BucketInfo.AgeDeleteRule;
import com.google.cloud.storage.BucketInfo.CreatedBeforeDeleteRule;
import com.google.cloud.storage.BucketInfo.DeleteRule;
import com.google.cloud.storage.BucketInfo.DeleteRule.Type;
import com.google.cloud.storage.BucketInfo.IamConfiguration;
import com.google.cloud.storage.BucketInfo.IsLiveDeleteRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.BucketInfo.NumNewerVersionsDeleteRule;
import com.google.cloud.storage.BucketInfo.PublicAccessPrevention;
import com.google.cloud.storage.BucketInfo.RawDeleteRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class BucketInfoTest {

  private static final List<Acl> ACL =
      ImmutableList.of(
          Acl.of(User.ofAllAuthenticatedUsers(), Role.READER),
          Acl.of(new Project(VIEWERS, "p1"), Role.WRITER));
  private static final String ETAG = "0xFF00";
  private static final String GENERATED_ID = "B/N:1";
  private static final Long META_GENERATION = 10L;
  private static final User OWNER = new User("user@gmail.com");
  private static final String SELF_LINK = "http://storage/b/n";
  private static final Long CREATE_TIME = System.currentTimeMillis();
  private static final Long UPDATE_TIME = CREATE_TIME;
  private static final List<Cors> CORS = Collections.singletonList(Cors.newBuilder().build());
  private static final List<Acl> DEFAULT_ACL =
      Collections.singletonList(Acl.of(User.ofAllAuthenticatedUsers(), Role.WRITER));

  @SuppressWarnings({"unchecked", "deprecation"})
  private static final List<? extends DeleteRule> DELETE_RULES =
      Collections.singletonList(new AgeDeleteRule(5));

  private static final List<? extends BucketInfo.LifecycleRule> LIFECYCLE_RULES =
      Collections.singletonList(
          new BucketInfo.LifecycleRule(
              LifecycleAction.newDeleteAction(),
              LifecycleCondition.newBuilder().setAge(5).build()));
  private static final String INDEX_PAGE = "index.html";
  private static final BucketInfo.IamConfiguration IAM_CONFIGURATION =
      BucketInfo.IamConfiguration.newBuilder()
          .setIsUniformBucketLevelAccessEnabled(true)
          .setUniformBucketLevelAccessLockedTime(System.currentTimeMillis())
          .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.ENFORCED)
          .build();
  private static final BucketInfo.Logging LOGGING =
      BucketInfo.Logging.newBuilder()
          .setLogBucket("test-bucket")
          .setLogObjectPrefix("test-")
          .build();
  private static final String NOT_FOUND_PAGE = "error.html";
  private static final String LOCATION = "ASIA";
  private static final StorageClass STORAGE_CLASS = StorageClass.STANDARD;
  private static final StorageClass ARCHIVE_STORAGE_CLASS = StorageClass.ARCHIVE;
  private static final String DEFAULT_KMS_KEY_NAME =
      "projects/p/locations/kr-loc/keyRings/kr/cryptoKeys/key";
  private static final Boolean VERSIONING_ENABLED = true;
  private static final Map<String, String> BUCKET_LABELS;

  static {
    BUCKET_LABELS = new HashMap<>();
    BUCKET_LABELS.put("label1", "value1");
    BUCKET_LABELS.put("label2", null);
  }

  private static final Map<String, String> BUCKET_LABELS_TARGET =
      ImmutableMap.of("label1", "value1", "label2", "");
  private static final Boolean REQUESTER_PAYS = true;
  private static final Boolean DEFAULT_EVENT_BASED_HOLD = true;
  private static final Long RETENTION_EFFECTIVE_TIME = 10L;
  private static final Long RETENTION_PERIOD = 10L;
  private static final Boolean RETENTION_POLICY_IS_LOCKED = false;
  private static final List<String> LOCATION_TYPES =
      ImmutableList.of("multi-region", "region", "dual-region");
  private static final String LOCATION_TYPE = "multi-region";

  @SuppressWarnings({"unchecked", "deprecation"})
  private static final BucketInfo BUCKET_INFO =
      BucketInfo.newBuilder("b")
          .setAcl(ACL)
          .setEtag(ETAG)
          .setGeneratedId(GENERATED_ID)
          .setMetageneration(META_GENERATION)
          .setOwner(OWNER)
          .setSelfLink(SELF_LINK)
          .setCors(CORS)
          .setCreateTime(CREATE_TIME)
          .setUpdateTime(UPDATE_TIME)
          .setDefaultAcl(DEFAULT_ACL)
          .setDeleteRules(DELETE_RULES)
          .setLifecycleRules(LIFECYCLE_RULES)
          .setIndexPage(INDEX_PAGE)
          .setIamConfiguration(IAM_CONFIGURATION)
          .setNotFoundPage(NOT_FOUND_PAGE)
          .setLocation(LOCATION)
          .setLocationType(LOCATION_TYPE)
          .setStorageClass(STORAGE_CLASS)
          .setVersioningEnabled(VERSIONING_ENABLED)
          .setLabels(BUCKET_LABELS)
          .setRequesterPays(REQUESTER_PAYS)
          .setDefaultKmsKeyName(DEFAULT_KMS_KEY_NAME)
          .setDefaultEventBasedHold(DEFAULT_EVENT_BASED_HOLD)
          .setRetentionEffectiveTime(RETENTION_EFFECTIVE_TIME)
          .setRetentionPeriod(RETENTION_PERIOD)
          .setRetentionPolicyIsLocked(RETENTION_POLICY_IS_LOCKED)
          .setLogging(LOGGING)
          .build();

  @SuppressWarnings({"unchecked", "deprecation"})
  private static final BucketInfo BUCKET_INFO_ARCHIVE =
      BucketInfo.newBuilder("b")
          .setAcl(ACL)
          .setEtag(ETAG)
          .setGeneratedId(GENERATED_ID)
          .setMetageneration(META_GENERATION)
          .setOwner(OWNER)
          .setSelfLink(SELF_LINK)
          .setCors(CORS)
          .setCreateTime(CREATE_TIME)
          .setUpdateTime(UPDATE_TIME)
          .setDefaultAcl(DEFAULT_ACL)
          .setDeleteRules(DELETE_RULES)
          .setLifecycleRules(LIFECYCLE_RULES)
          .setIndexPage(INDEX_PAGE)
          .setIamConfiguration(IAM_CONFIGURATION)
          .setNotFoundPage(NOT_FOUND_PAGE)
          .setLocation(LOCATION)
          .setLocationType(LOCATION_TYPE)
          .setStorageClass(ARCHIVE_STORAGE_CLASS)
          .setVersioningEnabled(VERSIONING_ENABLED)
          .setLabels(BUCKET_LABELS)
          .setRequesterPays(REQUESTER_PAYS)
          .setDefaultKmsKeyName(DEFAULT_KMS_KEY_NAME)
          .setDefaultEventBasedHold(DEFAULT_EVENT_BASED_HOLD)
          .setRetentionEffectiveTime(RETENTION_EFFECTIVE_TIME)
          .setRetentionPeriod(RETENTION_PERIOD)
          .setRetentionPolicyIsLocked(RETENTION_POLICY_IS_LOCKED)
          .setLogging(LOGGING)
          .build();

  private static final Lifecycle EMPTY_LIFECYCLE = lifecycle(Collections.<Rule>emptyList());

  @Test
  public void testToBuilder() {
    compareBuckets(BUCKET_INFO, BUCKET_INFO.toBuilder().build());
    BucketInfo bucketInfo = BUCKET_INFO.toBuilder().setName("B").setGeneratedId("id").build();
    assertEquals("B", bucketInfo.getName());
    assertEquals("id", bucketInfo.getGeneratedId());
    bucketInfo = bucketInfo.toBuilder().setName("b").setGeneratedId(GENERATED_ID).build();
    compareBuckets(BUCKET_INFO, bucketInfo);
    assertEquals(ARCHIVE_STORAGE_CLASS, BUCKET_INFO_ARCHIVE.getStorageClass());
  }

  @Test
  public void testToBuilderIncomplete() {
    BucketInfo incompleteBucketInfo = BucketInfo.newBuilder("b").build();
    compareBuckets(incompleteBucketInfo, incompleteBucketInfo.toBuilder().build());
  }

  @Test
  public void testOf() {
    BucketInfo bucketInfo = BucketInfo.of("bucket");
    assertEquals("bucket", bucketInfo.getName());
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testBuilder() {
    assertEquals("b", BUCKET_INFO.getName());
    assertEquals(ACL, BUCKET_INFO.getAcl());
    assertEquals(ETAG, BUCKET_INFO.getEtag());
    assertEquals(GENERATED_ID, BUCKET_INFO.getGeneratedId());
    assertEquals(META_GENERATION, BUCKET_INFO.getMetageneration());
    assertEquals(OWNER, BUCKET_INFO.getOwner());
    assertEquals(SELF_LINK, BUCKET_INFO.getSelfLink());
    assertEquals(CREATE_TIME, BUCKET_INFO.getCreateTime());
    assertEquals(UPDATE_TIME, BUCKET_INFO.getUpdateTime());
    assertEquals(CORS, BUCKET_INFO.getCors());
    assertEquals(DEFAULT_ACL, BUCKET_INFO.getDefaultAcl());
    assertEquals(DELETE_RULES, BUCKET_INFO.getDeleteRules());
    assertEquals(INDEX_PAGE, BUCKET_INFO.getIndexPage());
    assertEquals(IAM_CONFIGURATION, BUCKET_INFO.getIamConfiguration());
    assertEquals(NOT_FOUND_PAGE, BUCKET_INFO.getNotFoundPage());
    assertEquals(LOCATION, BUCKET_INFO.getLocation());
    assertEquals(STORAGE_CLASS, BUCKET_INFO.getStorageClass());
    assertEquals(DEFAULT_KMS_KEY_NAME, BUCKET_INFO.getDefaultKmsKeyName());
    assertEquals(VERSIONING_ENABLED, BUCKET_INFO.versioningEnabled());
    assertEquals(BUCKET_LABELS_TARGET, BUCKET_INFO.getLabels());
    assertEquals(REQUESTER_PAYS, BUCKET_INFO.requesterPays());
    assertEquals(DEFAULT_EVENT_BASED_HOLD, BUCKET_INFO.getDefaultEventBasedHold());
    assertEquals(RETENTION_EFFECTIVE_TIME, BUCKET_INFO.getRetentionEffectiveTime());
    assertEquals(RETENTION_PERIOD, BUCKET_INFO.getRetentionPeriod());
    assertEquals(RETENTION_POLICY_IS_LOCKED, BUCKET_INFO.retentionPolicyIsLocked());
    assertTrue(LOCATION_TYPES.contains(BUCKET_INFO.getLocationType()));
    assertEquals(LOGGING, BUCKET_INFO.getLogging());
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testToPbAndFromPb() {
    compareBuckets(
        BUCKET_INFO,
        Conversions.apiary()
            .bucketInfo()
            .decode(Conversions.apiary().bucketInfo().encode(BUCKET_INFO)));
    BucketInfo bucketInfo =
        BucketInfo.newBuilder("b")
            .setDeleteRules(DELETE_RULES)
            .setLifecycleRules(LIFECYCLE_RULES)
            .setLogging(LOGGING)
            .build();
    compareBuckets(
        bucketInfo,
        Conversions.apiary()
            .bucketInfo()
            .decode(Conversions.apiary().bucketInfo().encode(bucketInfo)));
  }

  @SuppressWarnings({"unchecked", "deprecation"})
  private void compareBuckets(BucketInfo expected, BucketInfo value) {
    assertEquals(expected, value);
    assertEquals(expected.getName(), value.getName());
    assertEquals(expected.getAcl(), value.getAcl());
    assertEquals(expected.getEtag(), value.getEtag());
    assertEquals(expected.getGeneratedId(), value.getGeneratedId());
    assertEquals(expected.getMetageneration(), value.getMetageneration());
    assertEquals(expected.getOwner(), value.getOwner());
    assertEquals(expected.getSelfLink(), value.getSelfLink());
    assertEquals(expected.getCreateTime(), value.getCreateTime());
    assertEquals(expected.getUpdateTime(), value.getUpdateTime());
    assertEquals(expected.getCors(), value.getCors());
    assertEquals(expected.getDefaultAcl(), value.getDefaultAcl());
    assertEquals(expected.getDeleteRules(), value.getDeleteRules());
    assertEquals(expected.getLifecycleRules(), value.getLifecycleRules());
    assertEquals(expected.getIndexPage(), value.getIndexPage());
    assertEquals(expected.getIamConfiguration(), value.getIamConfiguration());
    assertEquals(expected.getNotFoundPage(), value.getNotFoundPage());
    assertEquals(expected.getLocation(), value.getLocation());
    assertEquals(expected.getStorageClass(), value.getStorageClass());
    assertEquals(expected.getDefaultKmsKeyName(), value.getDefaultKmsKeyName());
    assertEquals(expected.versioningEnabled(), value.versioningEnabled());
    assertEquals(expected.getLabels(), value.getLabels());
    assertEquals(expected.requesterPays(), value.requesterPays());
    assertEquals(expected.getDefaultEventBasedHold(), value.getDefaultEventBasedHold());
    assertEquals(expected.getRetentionEffectiveTime(), value.getRetentionEffectiveTime());
    assertEquals(expected.getRetentionPeriod(), value.getRetentionPeriod());
    assertEquals(expected.retentionPolicyIsLocked(), value.retentionPolicyIsLocked());
    assertEquals(expected.getLogging(), value.getLogging());
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testDeleteRules() {
    AgeDeleteRule ageRule = new AgeDeleteRule(10);
    assertEquals(10, ageRule.getDaysToLive());
    assertEquals(10, ageRule.getDaysToLive());
    assertEquals(Type.AGE, ageRule.getType());
    assertEquals(Type.AGE, ageRule.getType());
    CreatedBeforeDeleteRule createBeforeRule = new CreatedBeforeDeleteRule(1);
    assertEquals(1, createBeforeRule.getTimeMillis());
    assertEquals(1, createBeforeRule.getTimeMillis());
    assertEquals(Type.CREATE_BEFORE, createBeforeRule.getType());
    NumNewerVersionsDeleteRule versionsRule = new NumNewerVersionsDeleteRule(2);
    assertEquals(2, versionsRule.getNumNewerVersions());
    assertEquals(2, versionsRule.getNumNewerVersions());
    assertEquals(Type.NUM_NEWER_VERSIONS, versionsRule.getType());
    IsLiveDeleteRule isLiveRule = new IsLiveDeleteRule(true);
    assertTrue(isLiveRule.isLive());
    assertEquals(Type.IS_LIVE, isLiveRule.getType());
    assertEquals(Type.IS_LIVE, isLiveRule.getType());
    Rule rule = new Rule().set("a", "b");
    RawDeleteRule rawRule = new RawDeleteRule(rule);
    assertEquals(Type.IS_LIVE, isLiveRule.getType());
    assertEquals(Type.IS_LIVE, isLiveRule.getType());
    ImmutableList<DeleteRule> rules =
        ImmutableList.of(ageRule, createBeforeRule, versionsRule, isLiveRule, rawRule);
    for (DeleteRule delRule : rules) {
      assertEquals(
          delRule,
          Conversions.apiary()
              .deleteRule()
              .decode(Conversions.apiary().deleteRule().encode(delRule)));
    }
    Rule unsupportedRule =
        new Rule().setAction(new Rule.Action().setType("This action doesn't exist"));
    Conversions.apiary()
        .deleteRule()
        .decode(unsupportedRule); // if this doesn't throw an exception, unsupported rules work
  }

  @Test
  public void testLifecycleRules() {
    Rule deleteLifecycleRule =
        Conversions.apiary()
            .lifecycleRule()
            .encode(
                new LifecycleRule(
                    LifecycleAction.newDeleteAction(),
                    LifecycleCondition.newBuilder().setAge(10).build()));

    assertEquals(
        LifecycleRule.DeleteLifecycleAction.TYPE, deleteLifecycleRule.getAction().getType());
    assertEquals(10, deleteLifecycleRule.getCondition().getAge().intValue());

    Rule setStorageClassLifecycleRule =
        Conversions.apiary()
            .lifecycleRule()
            .encode(
                new LifecycleRule(
                    LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
                    LifecycleCondition.newBuilder()
                        .setIsLive(true)
                        .setNumberOfNewerVersions(10)
                        .build()));

    assertEquals(
        StorageClass.COLDLINE.toString(),
        setStorageClassLifecycleRule.getAction().getStorageClass());
    assertTrue(setStorageClassLifecycleRule.getCondition().getIsLive());
    assertEquals(10, setStorageClassLifecycleRule.getCondition().getNumNewerVersions().intValue());

    Rule lifecycleRule =
        Conversions.apiary()
            .lifecycleRule()
            .encode(
                new LifecycleRule(
                    LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
                    LifecycleCondition.newBuilder()
                        .setIsLive(true)
                        .setNumberOfNewerVersions(10)
                        .setDaysSinceNoncurrentTime(30)
                        .setNoncurrentTimeBefore(new DateTime(System.currentTimeMillis()))
                        .setCustomTimeBefore(new DateTime(System.currentTimeMillis()))
                        .setDaysSinceCustomTime(30)
                        .build()));
    assertEquals(StorageClass.COLDLINE.toString(), lifecycleRule.getAction().getStorageClass());
    assertTrue(lifecycleRule.getCondition().getIsLive());
    assertEquals(10, lifecycleRule.getCondition().getNumNewerVersions().intValue());
    assertEquals(30, lifecycleRule.getCondition().getDaysSinceNoncurrentTime().intValue());
    assertNotNull(lifecycleRule.getCondition().getNoncurrentTimeBefore());
    assertEquals(StorageClass.COLDLINE.toString(), lifecycleRule.getAction().getStorageClass());
    assertEquals(30, lifecycleRule.getCondition().getDaysSinceCustomTime().intValue());
    assertNotNull(lifecycleRule.getCondition().getCustomTimeBefore());

    Rule unsupportedRule =
        Conversions.apiary()
            .lifecycleRule()
            .encode(
                new LifecycleRule(
                    LifecycleAction.newLifecycleAction("This action type doesn't exist"),
                    LifecycleCondition.newBuilder().setAge(10).build()));
    unsupportedRule.setAction(
        unsupportedRule.getAction().setType("This action type also doesn't exist"));

    Conversions.apiary()
        .lifecycleRule()
        .decode(
            unsupportedRule); // If this doesn't throw an exception, unsupported rules are working
  }

  @Test
  public void testIamConfiguration() {
    Bucket.IamConfiguration iamConfiguration =
        Conversions.apiary()
            .iamConfiguration()
            .encode(
                IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(true)
                    .setUniformBucketLevelAccessLockedTime(System.currentTimeMillis())
                    .setPublicAccessPrevention(PublicAccessPrevention.ENFORCED)
                    .build());

    assertEquals(Boolean.TRUE, iamConfiguration.getUniformBucketLevelAccess().getEnabled());
    assertNotNull(iamConfiguration.getUniformBucketLevelAccess().getLockedTime());
    assertEquals(
        BucketInfo.PublicAccessPrevention.ENFORCED.getValue(),
        iamConfiguration.getPublicAccessPrevention());
  }

  @Test
  public void testPublicAccessPrevention_ensureAbsentWhenUnknown() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonGenerator jsonGenerator =
        JacksonFactory.getDefaultInstance().createJsonGenerator(stringWriter);

    jsonGenerator.serialize(
        Conversions.apiary()
            .iamConfiguration()
            .encode(
                IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(true)
                    .setUniformBucketLevelAccessLockedTime(System.currentTimeMillis())
                    .setPublicAccessPrevention(PublicAccessPrevention.UNKNOWN)
                    .build()));
    jsonGenerator.flush();

    assertFalse(stringWriter.getBuffer().toString().contains("publicAccessPrevention"));
  }

  @Test
  public void testPapValueOfIamConfiguration() {
    Bucket.IamConfiguration iamConfiguration = new Bucket.IamConfiguration();
    Bucket.IamConfiguration.UniformBucketLevelAccess uniformBucketLevelAccess =
        new Bucket.IamConfiguration.UniformBucketLevelAccess();
    iamConfiguration.setUniformBucketLevelAccess(uniformBucketLevelAccess);
    iamConfiguration.setPublicAccessPrevention("random-string");
    IamConfiguration fromPb = Conversions.apiary().iamConfiguration().decode(iamConfiguration);

    assertEquals(PublicAccessPrevention.UNKNOWN, fromPb.getPublicAccessPrevention());
  }

  @Test
  public void testLogging() {
    Bucket.Logging logging =
        Conversions.apiary()
            .logging()
            .encode(
                BucketInfo.Logging.newBuilder()
                    .setLogBucket("test-bucket")
                    .setLogObjectPrefix("test-")
                    .build());
    assertEquals("test-bucket", logging.getLogBucket());
    assertEquals("test-", logging.getLogObjectPrefix());
  }

  @Test
  public void testRuleMappingIsCorrect_noMutations() {
    Bucket bucket = Conversions.apiary().bucketInfo().encode(bi().build());
    assertNull(bucket.getLifecycle());
  }

  @Test
  public void testRuleMappingIsCorrect_deleteLifecycleRules() {
    Bucket bucket = Conversions.apiary().bucketInfo().encode(bi().deleteLifecycleRules().build());
    assertEquals(EMPTY_LIFECYCLE, bucket.getLifecycle());
  }

  @Test
  @SuppressWarnings({"deprecation"})
  public void testRuleMappingIsCorrect_setDeleteRules_null() {
    Bucket bucket = Conversions.apiary().bucketInfo().encode(bi().setDeleteRules(null).build());
    assertNull(bucket.getLifecycle());
  }

  @Test
  @SuppressWarnings({"deprecation"})
  public void testRuleMappingIsCorrect_setDeleteRules_empty() {
    Bucket bucket =
        Conversions.apiary()
            .bucketInfo()
            .encode(bi().setDeleteRules(Collections.<DeleteRule>emptyList()).build());
    assertEquals(EMPTY_LIFECYCLE, bucket.getLifecycle());
  }

  @Test
  public void testRuleMappingIsCorrect_setLifecycleRules_empty() {
    Bucket bucket =
        Conversions.apiary()
            .bucketInfo()
            .encode(bi().setLifecycleRules(Collections.<LifecycleRule>emptyList()).build());
    assertEquals(EMPTY_LIFECYCLE, bucket.getLifecycle());
  }

  @Test
  public void testRuleMappingIsCorrect_setLifeCycleRules_nonEmpty() {
    LifecycleRule lifecycleRule =
        new LifecycleRule(
            LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(10).build());
    Rule lifecycleDeleteAfter10 = Conversions.apiary().lifecycleRule().encode(lifecycleRule);
    Bucket bucket =
        Conversions.apiary()
            .bucketInfo()
            .encode(bi().setLifecycleRules(ImmutableList.of(lifecycleRule)).build());
    assertEquals(lifecycle(lifecycleDeleteAfter10), bucket.getLifecycle());
  }

  @Test
  @SuppressWarnings({"deprecation"})
  public void testRuleMappingIsCorrect_setDeleteRules_nonEmpty() {
    DeleteRule deleteRule = DELETE_RULES.get(0);
    Rule deleteRuleAge5 = Conversions.apiary().deleteRule().encode(deleteRule);
    Bucket bucket =
        Conversions.apiary()
            .bucketInfo()
            .encode(bi().setDeleteRules(ImmutableList.of(deleteRule)).build());
    assertEquals(lifecycle(deleteRuleAge5), bucket.getLifecycle());
  }

  private static Lifecycle lifecycle(Rule... rules) {
    return lifecycle(Arrays.asList(rules));
  }

  private static Lifecycle lifecycle(List<Rule> rules) {
    Lifecycle emptyLifecycle = new Lifecycle();
    emptyLifecycle.setRule(rules);
    return emptyLifecycle;
  }

  private static BucketInfo.Builder bi() {
    String bucketId = "bucketId";
    return BucketInfo.newBuilder(bucketId);
  }
}
