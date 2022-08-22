package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.cloud.Policy;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.CustomPlacementConfig;
import com.google.cloud.storage.BucketInfo.LifecycleRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.AbortIncompleteMPUAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.Cors;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;
import com.google.cloud.storage.Rpo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITBucketTest {

  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private static Storage storage;
  private static String bucketName;
  private static final String BUCKET_REQUESTER_PAYS = RemoteStorageHelper.generateBucketName();
  private static final LifecycleRule LIFECYCLE_RULE_1 =
      new LifecycleRule(
          LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
          LifecycleCondition.newBuilder()
              .setAge(1)
              .setNumberOfNewerVersions(3)
              .setIsLive(false)
              .setMatchesStorageClass(ImmutableList.of(StorageClass.COLDLINE))
              .build());
  private static final LifecycleRule LIFECYCLE_RULE_2 =
      new LifecycleRule(
          LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(1).build());
  private static final ImmutableList<LifecycleRule> LIFECYCLE_RULES =
      ImmutableList.of(LIFECYCLE_RULE_1, LIFECYCLE_RULE_2);

  @BeforeClass
  public static void setUp() {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
  }

  @Test(timeout = 5000)
  public void testListBuckets() throws InterruptedException {
    Iterator<Bucket> bucketIterator =
        storage
            .list(Storage.BucketListOption.prefix(bucketName), Storage.BucketListOption.fields())
            .iterateAll()
            .iterator();
    while (!bucketIterator.hasNext()) {
      Thread.sleep(500);
      bucketIterator =
          storage
              .list(Storage.BucketListOption.prefix(bucketName), Storage.BucketListOption.fields())
              .iterateAll()
              .iterator();
    }
    while (bucketIterator.hasNext()) {
      Bucket remoteBucket = bucketIterator.next();
      assertTrue(remoteBucket.getName().startsWith(bucketName));
      assertNull(remoteBucket.getCreateTime());
      assertNull(remoteBucket.getSelfLink());
    }
  }

  @Test
  public void testGetBucketSelectedFields() {
    Bucket remoteBucket = storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.ID));
    assertEquals(bucketName, remoteBucket.getName());
    assertNull(remoteBucket.getCreateTime());
    assertNotNull(remoteBucket.getGeneratedId());
  }

  @Test
  public void testGetBucketAllSelectedFields() {
    Bucket remoteBucket = storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.values()));
    assertEquals(bucketName, remoteBucket.getName());
    assertNotNull(remoteBucket.getCreateTime());
    assertNotNull(remoteBucket.getSelfLink());
  }

  @Test
  public void testGetBucketLifecycleRules() {
    String lifecycleTestBucketName = RemoteStorageHelper.generateBucketName();
    storage.create(
        BucketInfo.newBuilder(lifecycleTestBucketName)
            .setLocation("us")
            .setLifecycleRules(
                ImmutableList.of(
                    new LifecycleRule(
                        LifecycleAction.newSetStorageClassAction(StorageClass.COLDLINE),
                        LifecycleCondition.newBuilder()
                            .setAge(1)
                            .setNumberOfNewerVersions(3)
                            .setIsLive(false)
                            .setCreatedBeforeOffsetDateTime(OffsetDateTime.now())
                            .setMatchesStorageClass(ImmutableList.of(StorageClass.COLDLINE))
                            .setDaysSinceNoncurrentTime(30)
                            .setNoncurrentTimeBeforeOffsetDateTime(OffsetDateTime.now())
                            .setCustomTimeBeforeOffsetDateTime(OffsetDateTime.now())
                            .setDaysSinceCustomTime(30)
                            .build())))
            .build());
    Bucket remoteBucket =
        storage.get(lifecycleTestBucketName, Storage.BucketGetOption.fields(BucketField.LIFECYCLE));
    LifecycleRule lifecycleRule = remoteBucket.getLifecycleRules().get(0);
    try {
      assertTrue(
          lifecycleRule
              .getAction()
              .getActionType()
              .equals(LifecycleRule.SetStorageClassLifecycleAction.TYPE));
      assertEquals(3, lifecycleRule.getCondition().getNumberOfNewerVersions().intValue());
      assertNotNull(lifecycleRule.getCondition().getCreatedBeforeOffsetDateTime());
      assertFalse(lifecycleRule.getCondition().getIsLive());
      assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
      assertEquals(1, lifecycleRule.getCondition().getMatchesStorageClass().size());
      assertEquals(30, lifecycleRule.getCondition().getDaysSinceNoncurrentTime().intValue());
      assertNotNull(lifecycleRule.getCondition().getNoncurrentTimeBeforeOffsetDateTime());
      assertEquals(30, lifecycleRule.getCondition().getDaysSinceCustomTime().intValue());
      assertNotNull(lifecycleRule.getCondition().getCustomTimeBeforeOffsetDateTime());
    } finally {
      storage.delete(lifecycleTestBucketName);
    }
  }

  @Test
  public void testGetBucketAbortMPULifecycle() {
    String lifecycleTestBucketName = RemoteStorageHelper.generateBucketName();
    storage.create(
        BucketInfo.newBuilder(lifecycleTestBucketName)
            .setLocation("us")
            .setLifecycleRules(
                ImmutableList.of(
                    new LifecycleRule(
                        LifecycleAction.newAbortIncompleteMPUploadAction(),
                        LifecycleCondition.newBuilder().setAge(1).build())))
            .build());
    Bucket remoteBucket =
        storage.get(lifecycleTestBucketName, Storage.BucketGetOption.fields(BucketField.LIFECYCLE));
    LifecycleRule lifecycleRule = remoteBucket.getLifecycleRules().get(0);
    try {
      assertEquals(AbortIncompleteMPUAction.TYPE, lifecycleRule.getAction().getActionType());
      assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
    } finally {
      storage.delete(lifecycleTestBucketName);
    }
  }

  @Test
  public void testBucketLocationType() throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    storage.create(
        BucketInfo.newBuilder(bucketName)
            .setLocation("us")
            .build());
    Bucket bucket = storage.get(bucketName);
    assertEquals("multi-region", bucket.getLocationType());
    RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
  }

  @Test
  public void testBucketCustomPlacmentConfigDualRegion() {
    String bucketName = RemoteStorageHelper.generateBucketName();
    List<String> locations = new ArrayList<>();
    locations.add("US-EAST1");
    locations.add("US-WEST1");
    CustomPlacementConfig customPlacementConfig =
        CustomPlacementConfig.newBuilder().setDataLocations(locations).build();
    Bucket bucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setCustomPlacementConfig(customPlacementConfig)
                .setLocation("us")
                .build());
    assertTrue(bucket.getCustomPlacementConfig().getDataLocations().contains("US-EAST1"));
    assertTrue(bucket.getCustomPlacementConfig().getDataLocations().contains("US-WEST1"));
    assertTrue(bucket.getLocation().equalsIgnoreCase("us"));
  }

  @Test
  public void testBucketLogging() throws ExecutionException, InterruptedException {
    String logsBucket = RemoteStorageHelper.generateBucketName();
    String loggingBucket = RemoteStorageHelper.generateBucketName();
    try {
      assertNotNull(storage.create(BucketInfo.newBuilder(logsBucket).setLocation("us").build()));
      Policy policy = storage.getIamPolicy(logsBucket);
      assertNotNull(policy);
      BucketInfo.Logging logging =
          BucketInfo.Logging.newBuilder()
              .setLogBucket(logsBucket)
              .setLogObjectPrefix("test-logs")
              .build();
      Bucket bucket =
          storage.create(
              BucketInfo.newBuilder(loggingBucket).setLocation("us").setLogging(logging).build());
      assertEquals(logsBucket, bucket.getLogging().getLogBucket());
      assertEquals("test-logs", bucket.getLogging().getLogObjectPrefix());

      // Disable bucket logging.
      Bucket updatedBucket = bucket.toBuilder().setLogging(null).build().update();
      assertNull(updatedBucket.getLogging());

    } finally {
      RemoteStorageHelper.forceDelete(storage, logsBucket, 5, TimeUnit.SECONDS);
      RemoteStorageHelper.forceDelete(storage, loggingBucket, 5, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testDeleteLifecycleRules() throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket bucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setLocation("us")
                .setLifecycleRules(LIFECYCLE_RULES)
                .build());
    assertThat(bucket.getLifecycleRules()).isNotNull();
    assertThat(bucket.getLifecycleRules()).hasSize(2);
    try {
      Bucket updatedBucket = bucket.toBuilder().deleteLifecycleRules().build().update();
      assertThat(updatedBucket.getLifecycleRules()).hasSize(0);
    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testRemoveBucketCORS() throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    List<Cors.Origin> origins = ImmutableList.of(Cors.Origin.of("http://cloud.google.com"));
    List<HttpMethod> httpMethods = ImmutableList.of(HttpMethod.GET);
    List<String> responseHeaders = ImmutableList.of("Content-Type");
    try {
      Cors cors =
          Cors.newBuilder()
              .setOrigins(origins)
              .setMethods(httpMethods)
              .setResponseHeaders(responseHeaders)
              .setMaxAgeSeconds(100)
              .build();
      storage.create(BucketInfo.newBuilder(bucketName).setCors(ImmutableList.of(cors)).build());

      // case-1 : Cors are set and field selector is selected then returns not-null.
      Bucket remoteBucket =
          storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.CORS));
      assertThat(remoteBucket.getCors()).isNotNull();
      assertThat(remoteBucket.getCors().get(0).getMaxAgeSeconds()).isEqualTo(100);
      assertThat(remoteBucket.getCors().get(0).getMethods()).isEqualTo(httpMethods);
      assertThat(remoteBucket.getCors().get(0).getOrigins()).isEqualTo(origins);
      assertThat(remoteBucket.getCors().get(0).getResponseHeaders()).isEqualTo(responseHeaders);

      // case-2 : Cors are set but field selector isn't selected then returns not-null.
      remoteBucket = storage.get(bucketName);
      assertThat(remoteBucket.getCors()).isNotNull();

      // Remove CORS configuration from the bucket.
      Bucket updatedBucket = remoteBucket.toBuilder().setCors(null).build().update();
      assertThat(updatedBucket.getCors()).isNull();

      // case-3 : Cors are not set and field selector is selected then returns null.
      updatedBucket = storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.CORS));
      assertThat(updatedBucket.getCors()).isNull();

      // case-4 : Cors are not set and field selector isn't selected then returns null.
      updatedBucket = storage.get(bucketName);
      assertThat(updatedBucket.getCors()).isNull();

    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testNotification() throws InterruptedException, ExecutionException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.newBuilder(bucketName).setLocation("us").build());
    NotificationInfo notificationInfo =
        NotificationInfo.newBuilder(TOPIC)
            .setCustomAttributes(CUSTOM_ATTRIBUTES)
            .setPayloadFormat(PAYLOAD_FORMAT)
            .build();
    try {
      assertThat(storage.listNotifications(bucketName)).isEmpty();
      Notification notification = storage.createNotification(bucketName, notificationInfo);
      assertThat(notification.getNotificationId()).isNotNull();
      assertThat(CUSTOM_ATTRIBUTES).isEqualTo(notification.getCustomAttributes());
      assertThat(PAYLOAD_FORMAT.name()).isEqualTo(notification.getPayloadFormat().name());
      assertThat(notification.getTopic().contains(TOPIC)).isTrue();

      // Gets the notification with the specified id.
      Notification actualNotification =
          storage.getNotification(bucketName, notification.getNotificationId());
      assertThat(actualNotification.getNotificationId())
          .isEqualTo(notification.getNotificationId());
      assertThat(actualNotification.getTopic().trim()).isEqualTo(notification.getTopic().trim());
      assertThat(actualNotification.getEtag()).isEqualTo(notification.getEtag());
      assertThat(actualNotification.getEventTypes()).isEqualTo(notification.getEventTypes());
      assertThat(actualNotification.getPayloadFormat()).isEqualTo(notification.getPayloadFormat());
      assertThat(actualNotification.getSelfLink()).isEqualTo(notification.getSelfLink());
      assertThat(actualNotification.getCustomAttributes())
          .isEqualTo(notification.getCustomAttributes());

      // Retrieves the list of notifications associated with the bucket.
      List<Notification> notifications = storage.listNotifications(bucketName);
      assertThat(notifications.size()).isEqualTo(1);
      assertThat(notifications.get(0).getNotificationId())
          .isEqualTo(actualNotification.getNotificationId());

      // Deletes the notification with the specified id.
      assertThat(storage.deleteNotification(bucketName, notification.getNotificationId())).isTrue();
      assertThat(storage.deleteNotification(bucketName, notification.getNotificationId()))
          .isFalse();
      assertThat(storage.getNotification(bucketName, notification.getNotificationId())).isNull();
      assertThat(storage.listNotifications(bucketName)).isEmpty();
    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testRpoConfig() {
    String rpoBucket = RemoteStorageHelper.generateBucketName();
    try {
      Bucket bucket =
          storage.create(
              BucketInfo.newBuilder(rpoBucket).setLocation("NAM4").setRpo(Rpo.ASYNC_TURBO).build());
      assertEquals("ASYNC_TURBO", bucket.getRpo().toString());

      bucket.toBuilder().setRpo(Rpo.DEFAULT).build().update();

      assertEquals("DEFAULT", storage.get(rpoBucket).getRpo().toString());
    } finally {
      storage.delete(rpoBucket);
    }
  }

  @Test
  public void testRetentionPolicyLock() throws ExecutionException, InterruptedException {
    retentionPolicyLockRequesterPays(true);
    retentionPolicyLockRequesterPays(false);
  }

  private void retentionPolicyLockRequesterPays(boolean requesterPays)
      throws ExecutionException, InterruptedException {
    String projectId = storage.getOptions().getProjectId();
    String bucketName = RemoteStorageHelper.generateBucketName();
    BucketInfo bucketInfo;
    if (requesterPays) {
      bucketInfo =
          BucketInfo.newBuilder(bucketName)
              .setRetentionPeriod(RETENTION_PERIOD)
              .setRequesterPays(true)
              .build();
    } else {
      bucketInfo = BucketInfo.newBuilder(bucketName).setRetentionPeriod(RETENTION_PERIOD).build();
    }
    Bucket remoteBucket = storage.create(bucketInfo);
    try {
      assertNull(remoteBucket.retentionPolicyIsLocked());
      assertNotNull(remoteBucket.getRetentionEffectiveTime());
      assertNotNull(remoteBucket.getMetageneration());
      if (requesterPays) {
        remoteBucket =
            storage.lockRetentionPolicy(
                remoteBucket,
                Storage.BucketTargetOption.metagenerationMatch(),
                Storage.BucketTargetOption.userProject(projectId));
      } else {
        remoteBucket =
            storage.lockRetentionPolicy(
                remoteBucket, Storage.BucketTargetOption.metagenerationMatch());
      }
      assertTrue(remoteBucket.retentionPolicyIsLocked());
      assertNotNull(remoteBucket.getRetentionEffectiveTime());
    } finally {
      if (requesterPays) {
        bucketInfo = bucketInfo.toBuilder().setRequesterPays(false).build();
        Bucket updateBucket =
            storage.update(bucketInfo, Storage.BucketTargetOption.userProject(projectId));
        assertFalse(updateBucket.requesterPays());
      }
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS, projectId);
    }
  }

  @Test
  public void testUpdateBucketLabel() {
    Bucket remoteBucket =
        storage.get(BUCKET, Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING));
    assertNull(remoteBucket.getLabels());
    remoteBucket = remoteBucket.toBuilder().setLabels(BUCKET_LABELS).build();
    Bucket updatedBucket = storage.update(remoteBucket);
    assertEquals(BUCKET_LABELS, updatedBucket.getLabels());
    remoteBucket.toBuilder().setLabels(REMOVE_BUCKET_LABELS).build().update();
    assertNull(storage.get(BUCKET).getLabels());
  }

  @Test
  public void testUpdateBucketRequesterPays() {
    unsetRequesterPays();
    Bucket remoteBucket =
        storage.get(
            BUCKET_REQUESTER_PAYS,
            Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING));
    assertTrue(remoteBucket.requesterPays() == null || !remoteBucket.requesterPays());
    remoteBucket = remoteBucket.toBuilder().setRequesterPays(true).build();
    Bucket updatedBucket = storage.update(remoteBucket);
    assertTrue(updatedBucket.requesterPays());

    String projectId = storage.getOptions().getProjectId();
    Bucket.BlobTargetOption option = Bucket.BlobTargetOption.userProject(projectId);
    String blobName = "test-create-empty-blob-requester-pays";
    Blob remoteBlob = updatedBucket.create(blobName, BLOB_BYTE_CONTENT, option);
    assertNotNull(remoteBlob);
    byte[] readBytes =
        storage.readAllBytes(
            BUCKET_REQUESTER_PAYS, blobName, Storage.BlobSourceOption.userProject(projectId));
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
    remoteBucket = remoteBucket.toBuilder().setRequesterPays(false).build();
    updatedBucket = storage.update(remoteBucket, Storage.BucketTargetOption.userProject(projectId));
    assertFalse(updatedBucket.requesterPays());
  }

  @Test
  public void testEnableDisableBucketDefaultEventBasedHold()
      throws ExecutionException, InterruptedException {
    String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket remoteBucket =
        storage.create(BucketInfo.newBuilder(bucketName).setDefaultEventBasedHold(true).build());
    try {
      assertTrue(remoteBucket.getDefaultEventBasedHold());
      remoteBucket =
          storage.get(
              bucketName, Storage.BucketGetOption.fields(BucketField.DEFAULT_EVENT_BASED_HOLD));
      assertTrue(remoteBucket.getDefaultEventBasedHold());
      String blobName = "test-create-with-event-based-hold";
      BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
      Blob remoteBlob = storage.create(blobInfo);
      assertTrue(remoteBlob.getEventBasedHold());
      remoteBlob =
          storage.get(
              blobInfo.getBlobId(), Storage.BlobGetOption.fields(BlobField.EVENT_BASED_HOLD));
      assertTrue(remoteBlob.getEventBasedHold());
      remoteBlob = remoteBlob.toBuilder().setEventBasedHold(false).build().update();
      assertFalse(remoteBlob.getEventBasedHold());
      remoteBucket = remoteBucket.toBuilder().setDefaultEventBasedHold(false).build().update();
      assertFalse(remoteBucket.getDefaultEventBasedHold());
    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }

}
