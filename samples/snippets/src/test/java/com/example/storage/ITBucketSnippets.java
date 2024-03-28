/*
 * Copyright 2016 Google LLC
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

package com.example.storage;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.storage.bucket.AddBucketIamConditionalBinding;
import com.example.storage.bucket.AddBucketIamMember;
import com.example.storage.bucket.AddBucketLabel;
import com.example.storage.bucket.ChangeDefaultStorageClass;
import com.example.storage.bucket.ConfigureBucketCors;
import com.example.storage.bucket.CreateBucket;
import com.example.storage.bucket.CreateBucketWithObjectRetention;
import com.example.storage.bucket.CreateBucketWithStorageClassAndLocation;
import com.example.storage.bucket.CreateBucketWithTurboReplication;
import com.example.storage.bucket.DeleteBucket;
import com.example.storage.bucket.DisableBucketVersioning;
import com.example.storage.bucket.DisableDefaultEventBasedHold;
import com.example.storage.bucket.DisableLifecycleManagement;
import com.example.storage.bucket.DisableRequesterPays;
import com.example.storage.bucket.DisableUniformBucketLevelAccess;
import com.example.storage.bucket.EnableBucketVersioning;
import com.example.storage.bucket.EnableDefaultEventBasedHold;
import com.example.storage.bucket.EnableLifecycleManagement;
import com.example.storage.bucket.EnableRequesterPays;
import com.example.storage.bucket.EnableUniformBucketLevelAccess;
import com.example.storage.bucket.GetBucketMetadata;
import com.example.storage.bucket.GetBucketRpo;
import com.example.storage.bucket.GetDefaultEventBasedHold;
import com.example.storage.bucket.GetPublicAccessPrevention;
import com.example.storage.bucket.GetRetentionPolicy;
import com.example.storage.bucket.GetUniformBucketLevelAccess;
import com.example.storage.bucket.ListBucketIamMembers;
import com.example.storage.bucket.ListBuckets;
import com.example.storage.bucket.LockRetentionPolicy;
import com.example.storage.bucket.MakeBucketPublic;
import com.example.storage.bucket.RemoveBucketCors;
import com.example.storage.bucket.RemoveBucketDefaultKmsKey;
import com.example.storage.bucket.RemoveBucketIamConditionalBinding;
import com.example.storage.bucket.RemoveBucketIamMember;
import com.example.storage.bucket.RemoveBucketLabel;
import com.example.storage.bucket.RemoveRetentionPolicy;
import com.example.storage.bucket.SetAsyncTurboRpo;
import com.example.storage.bucket.SetBucketDefaultKmsKey;
import com.example.storage.bucket.SetBucketWebsiteInfo;
import com.example.storage.bucket.SetClientEndpoint;
import com.example.storage.bucket.SetDefaultRpo;
import com.example.storage.bucket.SetPublicAccessPreventionEnforced;
import com.example.storage.bucket.SetPublicAccessPreventionInherited;
import com.example.storage.bucket.SetRetentionPolicy;
import com.example.storage.object.DownloadRequesterPaysObject;
import com.example.storage.object.ReleaseEventBasedHold;
import com.example.storage.object.ReleaseTemporaryHold;
import com.example.storage.object.SetEventBasedHold;
import com.example.storage.object.SetTemporaryHold;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.Identity;
import com.google.cloud.ServiceOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.PublicAccessPrevention;
import com.google.cloud.storage.Cors;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.StorageRoles;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.cloud.testing.junit4.StdOutCaptureRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.threeten.bp.Duration;

public class ITBucketSnippets {

  private static final Logger log = Logger.getLogger(ITBucketSnippets.class.getName());
  private static final String BUCKET = RemoteStorageHelper.generateBucketName();
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String KMS_KEY_NAME =
      "projects/cloud-java-ci-sample/locations/us/keyRings/"
          + "gcs_test_kms_key_ring/cryptoKeys/gcs_kms_key_one";
  private static final RetrySettings RETRY_SETTINGS =
      RetrySettings.newBuilder()
          .setInitialRetryDelay(Duration.ofSeconds(2))
          .setRetryDelayMultiplier(1.75)
          .setTotalTimeout(Duration.ofSeconds(90))
          .setMaxRetryDelay(Duration.ofSeconds(10))
          .build();

  private static Storage storage;

  @Rule public final StdOutCaptureRule stdOutCaptureRule = new StdOutCaptureRule();

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Rule public Timeout globalTimeout = Timeout.seconds(300);

  @BeforeClass
  public static void beforeClass() {
    RemoteStorageHelper helper = RemoteStorageHelper.create();
    storage =
        helper
            .getOptions()
            .toBuilder()
            .setRetrySettings(
                helper
                    .getOptions()
                    .getRetrySettings()
                    .toBuilder()
                    .setRetryDelayMultiplier(3.0)
                    .build())
            .build()
            .getService();
    storage.create(BucketInfo.of(BUCKET));
  }

  @AfterClass
  public static void afterClass() throws ExecutionException, InterruptedException {
    if (storage != null) {
      boolean wasDeleted = RemoteStorageHelper.forceDelete(storage, BUCKET, 5, TimeUnit.SECONDS);
      if (!wasDeleted && log.isLoggable(Level.WARNING)) {
        log.log(Level.WARNING, "Deletion of bucket {0} timed out, bucket is not empty", BUCKET);
      }
    }
  }

  @After
  public void after() throws Exception {
    // This avoids 429 errors
    Thread.sleep(3000);
  }

  @Test
  public void testAddBucketLabel() {
    int oldSize = storage.get(BUCKET).getLabels().size();
    AddBucketLabel.addBucketLabel(PROJECT_ID, BUCKET, "key", "value");
    assertEquals(oldSize + 1, storage.get(BUCKET).getLabels().size());
  }

  @Test
  public void testChangeDefaultStorageClass() throws Throwable {
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> assertEquals("STANDARD", storage.get(BUCKET).getStorageClass().name()));
    ChangeDefaultStorageClass.changeDefaultStorageClass(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> assertEquals("COLDLINE", storage.get(BUCKET).getStorageClass().name()));
  }

  @Test
  public void testCreateBucket() {
    String newBucket = RemoteStorageHelper.generateBucketName();
    CreateBucket.createBucket(PROJECT_ID, newBucket);
    try {
      Bucket remoteBucket = storage.get(newBucket);
      assertNotNull(remoteBucket);
    } finally {
      storage.delete(newBucket);
    }
  }

  @Test
  public void testCreateBucketWithStorageClassAndLocation() {
    String newBucket = RemoteStorageHelper.generateBucketName();
    CreateBucketWithStorageClassAndLocation.createBucketWithStorageClassAndLocation(
        PROJECT_ID, newBucket);
    try {
      Bucket remoteBucket = storage.get(newBucket);
      assertNotNull(remoteBucket);
      assertEquals("COLDLINE", remoteBucket.getStorageClass().name());
      assertEquals("ASIA", remoteBucket.getLocation());
    } finally {
      storage.delete(newBucket);
    }
  }

  @Test
  public void testDeleteBucket() {
    String newBucket = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.newBuilder(newBucket).build());
    assertNotNull(storage.get(newBucket));
    try {
      DeleteBucket.deleteBucket(PROJECT_ID, newBucket);
      assertNull(storage.get(newBucket));
    } finally {
      storage.delete(newBucket);
    }
  }

  @Test
  public void testGetBucketMetadata() {
    Bucket bucket =
        storage.get(BUCKET, Storage.BucketGetOption.fields(Storage.BucketField.values()));
    bucket =
        bucket
            .toBuilder()
            .setLabels(ImmutableMap.of("k", "v"))
            .setLifecycleRules(
                ImmutableList.of(
                    new BucketInfo.LifecycleRule(
                        BucketInfo.LifecycleRule.LifecycleAction.newDeleteAction(),
                        BucketInfo.LifecycleRule.LifecycleCondition.newBuilder()
                            .setAge(5)
                            .build())))
            .build()
            .update();

    GetBucketMetadata.getBucketMetadata(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains(("BucketName: " + bucket.getName())));
    assertTrue(
        snippetOutput.contains(("DefaultEventBasedHold: " + bucket.getDefaultEventBasedHold())));
    assertTrue(snippetOutput.contains(("DefaultKmsKeyName: " + bucket.getDefaultKmsKeyName())));
    assertTrue(snippetOutput.contains(("Id: " + bucket.getGeneratedId())));
    assertTrue(snippetOutput.contains(("IndexPage: " + bucket.getIndexPage())));
    assertTrue(snippetOutput.contains(("Location: " + bucket.getLocation())));
    assertTrue(snippetOutput.contains(("LocationType: " + bucket.getLocationType())));
    assertTrue(snippetOutput.contains(("Metageneration: " + bucket.getMetageneration())));
    assertTrue(snippetOutput.contains(("NotFoundPage: " + bucket.getNotFoundPage())));
    assertTrue(
        snippetOutput.contains(("RetentionEffectiveTime: " + bucket.getRetentionEffectiveTime())));
    assertTrue(snippetOutput.contains(("RetentionPeriod: " + bucket.getRetentionPeriod())));
    assertTrue(
        snippetOutput.contains(("RetentionPolicyIsLocked: " + bucket.retentionPolicyIsLocked())));
    assertTrue(snippetOutput.contains(("RequesterPays: " + bucket.requesterPays())));
    assertTrue(snippetOutput.contains(("SelfLink: " + bucket.getSelfLink())));
    assertTrue(snippetOutput.contains(("StorageClass: " + bucket.getStorageClass().name())));
    assertTrue(snippetOutput.contains(("TimeCreated: " + bucket.getCreateTime())));
    assertTrue(snippetOutput.contains(("VersioningEnabled: " + bucket.versioningEnabled())));
    assertTrue(snippetOutput.contains("Labels:"));
    assertTrue(snippetOutput.contains("k=v"));
    assertTrue(snippetOutput.contains("Lifecycle Rules:"));
    assertTrue(snippetOutput.contains("ObjectRetention: " + bucket.getObjectRetention()));
  }

  @Test
  public void testListBuckets() {
    ListBuckets.listBuckets(PROJECT_ID);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains(BUCKET));
  }

  @Test
  public void testRemoveBucketLabel() {
    storage.get(BUCKET).toBuilder().setLabels(ImmutableMap.of("k", "v")).build().update();
    int oldSize = storage.get(BUCKET).getLabels().size();
    RemoveBucketLabel.removeBucketLabel(PROJECT_ID, BUCKET, "k");
    Map<String, String> labels = storage.get(BUCKET).getLabels();
    if (labels != null) {
      assertEquals(oldSize - 1, labels.size());
    }
  }

  @Test
  public void testEnableLifecycleManagement() throws Throwable {
    EnableLifecycleManagement.enableLifecycleManagement(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertEquals(1, storage.get(BUCKET).getLifecycleRules().size()));
  }

  @Test
  public void testDisableLifecycleManagement() throws Throwable {
    storage
        .get(BUCKET)
        .toBuilder()
        .setLifecycleRules(
            ImmutableList.of(
                new BucketInfo.LifecycleRule(
                    BucketInfo.LifecycleRule.LifecycleAction.newDeleteAction(),
                    BucketInfo.LifecycleRule.LifecycleCondition.newBuilder().setAge(5).build())))
        .build()
        .update();
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertEquals(1, storage.get(BUCKET).getLifecycleRules().size()));
    DisableLifecycleManagement.disableLifecycleManagement(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertEquals(0, storage.get(BUCKET).getLifecycleRules().size()));
  }

  @Test
  public void testGetPublicAccessPrevention() throws Throwable {
    try {
      // By default a bucket PAP state is INHERITED and we are changing the state to validate
      // non-default state.
      storage
          .get(BUCKET)
          .toBuilder()
          .setIamConfiguration(
              BucketInfo.IamConfiguration.newBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.ENFORCED)
                  .build())
          .build()
          .update();
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () ->
              assertThat(storage.get(BUCKET).getIamConfiguration().getPublicAccessPrevention())
                  .isEqualTo(PublicAccessPrevention.ENFORCED));
      GetPublicAccessPrevention.getPublicAccessPrevention(PROJECT_ID, BUCKET);
      String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
      assertTrue(snippetOutput.contains("enforced"));
    } finally {
      // No matter what happens make sure test set bucket back to INHERITED
      storage
          .get(BUCKET)
          .toBuilder()
          .setIamConfiguration(
              BucketInfo.IamConfiguration.newBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.INHERITED)
                  .build())
          .build()
          .update();
    }
  }

  @Test
  public void testSetPublicAccessPreventionEnforced() throws Throwable {
    try {
      SetPublicAccessPreventionEnforced.setPublicAccessPreventionEnforced(PROJECT_ID, BUCKET);
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () ->
              assertEquals(
                  storage.get(BUCKET).getIamConfiguration().getPublicAccessPrevention(),
                  BucketInfo.PublicAccessPrevention.ENFORCED));
    } finally {
      // No matter what happens make sure test set bucket back to INHERITED
      storage
          .get(BUCKET)
          .toBuilder()
          .setIamConfiguration(
              BucketInfo.IamConfiguration.newBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.INHERITED)
                  .build())
          .build()
          .update();
    }
  }

  @Test
  public void testSetPublicAccessPreventionInherited() throws Throwable {
    try {
      storage
          .get(BUCKET)
          .toBuilder()
          .setIamConfiguration(
              BucketInfo.IamConfiguration.newBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.ENFORCED)
                  .build())
          .build()
          .update();
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () ->
              assertThat(storage.get(BUCKET).getIamConfiguration().getPublicAccessPrevention())
                  .isEqualTo(PublicAccessPrevention.ENFORCED));

      SetPublicAccessPreventionInherited.setPublicAccessPreventionInherited(PROJECT_ID, BUCKET);
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () ->
              assertEquals(
                  storage.get(BUCKET).getIamConfiguration().getPublicAccessPrevention(),
                  BucketInfo.PublicAccessPrevention.INHERITED));
    } finally {
      // No matter what happens make sure test set bucket back to INHERITED
      storage
          .get(BUCKET)
          .toBuilder()
          .setIamConfiguration(
              BucketInfo.IamConfiguration.newBuilder()
                  .setPublicAccessPrevention(BucketInfo.PublicAccessPrevention.INHERITED)
                  .build())
          .build()
          .update();
    }
  }

  @Test
  public void testAddListRemoveBucketIamMembers() throws Throwable {
    storage.update(
        BucketInfo.newBuilder(BUCKET)
            .setIamConfiguration(
                BucketInfo.IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(true)
                    .build())
            .build());
    // todo:
    int originalSize = storage.getIamPolicy(BUCKET).getBindingsList().size();
    AddBucketIamMember.addBucketIamMember(PROJECT_ID, BUCKET);
    assertEquals(originalSize + 1, storage.getIamPolicy(BUCKET).getBindingsList().size());
    ListBucketIamMembers.listBucketIamMembers(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("example@google.com"));
    RemoveBucketIamMember.removeBucketIamMember(PROJECT_ID, BUCKET);
    assertEquals(originalSize, storage.getIamPolicy(BUCKET).getBindingsList().size());
    AddBucketIamConditionalBinding.addBucketIamConditionalBinding(PROJECT_ID, BUCKET);
    assertEquals(originalSize + 1, storage.getIamPolicy(BUCKET).getBindingsList().size());
    RemoveBucketIamConditionalBinding.removeBucketIamConditionalBinding(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> assertEquals(originalSize, storage.getIamPolicy(BUCKET).getBindingsList().size()));
    storage.update(
        BucketInfo.newBuilder(BUCKET)
            .setIamConfiguration(
                BucketInfo.IamConfiguration.newBuilder()
                    .setIsUniformBucketLevelAccessEnabled(false)
                    .build())
            .build());
  }

  @Test
  public void testMakeBucketPublic() throws Throwable {
    MakeBucketPublic.makeBucketPublic(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () ->
            assertTrue(
                storage
                    .getIamPolicy(BUCKET)
                    .getBindings()
                    .get(StorageRoles.objectViewer())
                    .contains(Identity.allUsers())));
  }

  @Test
  public void deleteBucketDefaultKmsKey() throws Throwable {
    storage
        .get(BUCKET)
        .toBuilder()
        .setDefaultKmsKeyName(
            "projects/cloud-java-ci-sample/locations/us/keyRings/"
                + "gcs_test_kms_key_ring/cryptoKeys/gcs_kms_key_one")
        .build()
        .update();
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertNotNull(storage.get(BUCKET).getDefaultKmsKeyName()));
    RemoveBucketDefaultKmsKey.removeBucketDefaultKmsKey(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertNull(storage.get(BUCKET).getDefaultKmsKeyName()));
  }

  @Test
  public void testEnableDisableVersioning() throws Throwable {
    EnableBucketVersioning.enableBucketVersioning(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertTrue(storage.get(BUCKET).versioningEnabled()));
    DisableBucketVersioning.disableBucketVersioning(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertFalse(storage.get(BUCKET).versioningEnabled()));
  }

  @Test
  public void testSetBucketWebsiteInfo() throws Throwable {
    SetBucketWebsiteInfo.setBucketWesbiteInfo(PROJECT_ID, BUCKET, "index.html", "404.html");
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> {
          Bucket bucket = storage.get(BUCKET);
          assertEquals("index.html", bucket.getIndexPage());
          assertEquals("404.html", bucket.getNotFoundPage());
        });
  }

  @Test
  public void testSetClientEndpoint() {
    SetClientEndpoint.setClientEndpoint(PROJECT_ID, "https://storage.googleapis.com");
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("https://storage.googleapis.com"));
  }

  @Test
  public void testConfigureBucketCors() throws Throwable {
    ConfigureBucketCors.configureBucketCors(
        PROJECT_ID, BUCKET, "http://example.appspot.com", "Content-Type", 3600);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> {
          Cors cors = storage.get(BUCKET).getCors().get(0);
          assertTrue(cors.getOrigins().get(0).toString().contains("example.appspot.com"));
          assertTrue(cors.getResponseHeaders().contains("Content-Type"));
          assertEquals(3600, cors.getMaxAgeSeconds().intValue());
          assertTrue(cors.getMethods().get(0).toString().equalsIgnoreCase("GET"));
        });
  }

  @Test
  public void testRemoveBucketCors() throws Throwable {
    storage
        .get(BUCKET)
        .toBuilder()
        .setCors(
            ImmutableList.of(
                Cors.newBuilder()
                    .setOrigins(ImmutableList.of(Cors.Origin.of("http://example.appspot.com")))
                    .setMethods(ImmutableList.of(HttpMethod.GET))
                    .setResponseHeaders(ImmutableList.of("Content-Type"))
                    .setMaxAgeSeconds(3600)
                    .build()))
        .build()
        .update();
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> {
          Cors cors = storage.get(BUCKET).getCors().get(0);
          assertNotNull(cors);
          assertTrue(cors.getOrigins().get(0).toString().contains("example.appspot.com"));
          assertTrue(cors.getResponseHeaders().contains("Content-Type"));
          assertEquals(3600, cors.getMaxAgeSeconds().intValue());
          assertTrue(cors.getMethods().get(0).toString().equalsIgnoreCase("GET"));
        });
    RemoveBucketCors.removeBucketCors(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(RETRY_SETTINGS, () -> assertNull(storage.get(BUCKET).getCors()));
  }

  @Test
  public void testRequesterPays() throws Throwable {
    EnableRequesterPays.enableRequesterPays(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () ->
            assertTrue(
                storage
                    .get(BUCKET, Storage.BucketGetOption.userProject(PROJECT_ID))
                    .requesterPays()));
    String projectId = ServiceOptions.getDefaultProjectId();
    String blobName = "test-create-empty-blob-requester-pays";
    byte[] content = {0xD, 0xE, 0xA, 0xD};
    Blob remoteBlob =
        storage.create(
            BlobInfo.newBuilder(BUCKET, blobName).build(),
            content,
            BlobTargetOption.userProject(projectId));
    assertNotNull(remoteBlob);
    DownloadRequesterPaysObject.downloadRequesterPaysObject(
        projectId, BUCKET, blobName, Paths.get(blobName));
    byte[] readBytes = Files.readAllBytes(Paths.get(blobName));
    assertArrayEquals(content, readBytes);
    DisableRequesterPays.disableRequesterPays(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(RETRY_SETTINGS, () -> assertFalse(storage.get(BUCKET).requesterPays()));
  }

  @Test
  public void testRpo() throws Throwable {
    String rpoBucket = RemoteStorageHelper.generateBucketName();
    try {
      CreateBucketWithTurboReplication.createBucketWithTurboReplication(
          PROJECT_ID, rpoBucket, "NAM4");
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () -> assertEquals("ASYNC_TURBO", storage.get(rpoBucket).getRpo().toString()));

      SetDefaultRpo.setDefaultRpo(PROJECT_ID, rpoBucket);
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () -> assertEquals("DEFAULT", storage.get(rpoBucket).getRpo().toString()));

      SetAsyncTurboRpo.setAsyncTurboRpo(PROJECT_ID, rpoBucket);
      TestUtils.retryAssert(
          RETRY_SETTINGS,
          () -> assertEquals("ASYNC_TURBO", storage.get(rpoBucket).getRpo().toString()));

      GetBucketRpo.getBucketRpo(PROJECT_ID, rpoBucket);
      String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
      assertTrue(snippetOutput.contains("ASYNC_TURBO"));
    } finally {
      storage.delete(rpoBucket);
    }
  }

  @Test
  public void testDefaultKMSKey() throws Throwable {
    SetBucketDefaultKmsKey.setBucketDefaultKmsKey(PROJECT_ID, BUCKET, KMS_KEY_NAME);
    TestUtils.retryAssert(
        RETRY_SETTINGS,
        () -> assertEquals(KMS_KEY_NAME, storage.get(BUCKET).getDefaultKmsKeyName()));

    RemoveBucketDefaultKmsKey.removeBucketDefaultKmsKey(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertNull(storage.get(BUCKET).getDefaultKmsKeyName()));
  }

  @Test
  public void testBucketRetention() throws Throwable {
    Long retention = 5L;
    SetRetentionPolicy.setRetentionPolicy(PROJECT_ID, BUCKET, retention);
    Bucket bucket = storage.get(BUCKET);
    assertEquals(retention, bucket.getRetentionPeriod());
    assertNotNull(bucket.getRetentionEffectiveTime());

    GetRetentionPolicy.getRetentionPolicy(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("5"));

    EnableDefaultEventBasedHold.enableDefaultEventBasedHold(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertTrue(storage.get(BUCKET).getDefaultEventBasedHold()));

    GetDefaultEventBasedHold.getDefaultEventBasedHold(PROJECT_ID, BUCKET);
    snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("enabled"));

    byte[] content = {0xD, 0xE, 0xA, 0xD};
    String blobName = "test-create-empty-blob-retention-policy";
    bucket.create(blobName, content);
    SetEventBasedHold.setEventBasedHold(PROJECT_ID, BUCKET, blobName);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertTrue(storage.get(BUCKET, blobName).getEventBasedHold()));
    ReleaseEventBasedHold.releaseEventBasedHold(PROJECT_ID, BUCKET, blobName);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertFalse(storage.get(BUCKET, blobName).getEventBasedHold()));
    RemoveRetentionPolicy.removeRetentionPolicy(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertNull(storage.get(BUCKET).getRetentionPeriod()));
    DisableDefaultEventBasedHold.disableDefaultEventBasedHold(PROJECT_ID, BUCKET);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertFalse(storage.get(BUCKET).getDefaultEventBasedHold()));
    SetTemporaryHold.setTemporaryHold(PROJECT_ID, BUCKET, blobName);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertTrue(storage.get(BUCKET, blobName).getTemporaryHold()));
    ReleaseTemporaryHold.releaseTemporaryHold(PROJECT_ID, BUCKET, blobName);
    TestUtils.retryAssert(
        RETRY_SETTINGS, () -> assertFalse(storage.get(BUCKET, blobName).getTemporaryHold()));
  }

  @Test
  public void testLockRetentionPolicy() {
    String tempBucket = RemoteStorageHelper.generateBucketName();
    Bucket bucket = storage.create(BucketInfo.of(tempBucket));
    assertNotNull(bucket);
    try {
      SetRetentionPolicy.setRetentionPolicy(PROJECT_ID, tempBucket, 5L);
      assertEquals(5L, (long) storage.get(tempBucket).getRetentionPeriod());
      LockRetentionPolicy.lockRetentionPolicy(PROJECT_ID, tempBucket);
      assertTrue(storage.get(tempBucket).retentionPolicyIsLocked());
    } finally {
      storage.delete(tempBucket);
    }
  }

  @Test
  public void testUniformBucketLevelAccess() {
    EnableUniformBucketLevelAccess.enableUniformBucketLevelAccess(PROJECT_ID, BUCKET);
    Bucket bucket = storage.get(BUCKET);
    assertTrue(bucket.getIamConfiguration().isUniformBucketLevelAccessEnabled());
    assertNotNull(bucket.getIamConfiguration().getUniformBucketLevelAccessLockedTime());

    GetUniformBucketLevelAccess.getUniformBucketLevelAccess(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("enabled"));

    DisableUniformBucketLevelAccess.disableUniformBucketLevelAccess(PROJECT_ID, BUCKET);
    assertFalse(storage.get(BUCKET).getIamConfiguration().isUniformBucketLevelAccessEnabled());
  }

  @Test
  public void testCreateBucketWithObjectRetention() {
    String tempBucket = RemoteStorageHelper.generateBucketName();

    try {
      CreateBucketWithObjectRetention.createBucketWithObjectRetention(PROJECT_ID, tempBucket);
      assertNotNull(storage.get(tempBucket).getObjectRetention());
      String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
      assertTrue(snippetOutput.contains("Enabled"));
    } finally {
      storage.delete(tempBucket);
    }
  }
}
