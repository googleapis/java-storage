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
import com.google.cloud.storage.Cors;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Rpo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITBucketTest {

  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  @ClassRule(order = 3)
  public static final BucketFixture requesterPaysFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private static Storage storage;
  private static String bucketName;
  private static String requesterPaysBucketName;

  private static final byte[] BLOB_BYTE_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final Map<String, String> BUCKET_LABELS = ImmutableMap.of("label1", "value1");
  private static final Long RETENTION_PERIOD = 5L;

  @BeforeClass
  public static void setup() {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
    requesterPaysBucketName = requesterPaysFixture.getBucketInfo().getName();
  }

  @AfterClass
  public static void cleanup() {
    unsetRequesterPays();
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
    Bucket remoteBucket =
        storage.get(bucketName, Storage.BucketGetOption.fields(BucketField.values()));
    assertEquals(bucketName, remoteBucket.getName());
    assertNotNull(remoteBucket.getCreateTime());
    assertNotNull(remoteBucket.getSelfLink());
  }

  @Test
  public void testBucketLocationType() {
    String bucketName = RemoteStorageHelper.generateBucketName();
    Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName).setLocation("us").build());
    assertEquals("multi-region", bucket.getLocationType());
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
        storage.get(
            bucketName, Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING));
    assertNull(remoteBucket.getLabels());
    remoteBucket = remoteBucket.toBuilder().setLabels(BUCKET_LABELS).build();
    Bucket updatedBucket = storage.update(remoteBucket);
    assertEquals(BUCKET_LABELS, updatedBucket.getLabels());
    remoteBucket.toBuilder().setLabels(Collections.emptyMap()).build().update();
    assertNull(storage.get(bucketName).getLabels());
  }

  @Test
  public void testUpdateBucketRequesterPays() {
    unsetRequesterPays();
    Bucket remoteBucket =
        storage.get(
            requesterPaysBucketName,
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
            requesterPaysBucketName, blobName, Storage.BlobSourceOption.userProject(projectId));
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

  private static void unsetRequesterPays() {
    Bucket remoteBucket =
        storage.get(
            requesterPaysBucketName,
            Storage.BucketGetOption.fields(BucketField.ID, BucketField.BILLING),
            Storage.BucketGetOption.userProject(storage.getOptions().getProjectId()));
    // Disable requester pays in case a test fails to clean up.
    if (remoteBucket.requesterPays() != null && remoteBucket.requesterPays() == true) {
      remoteBucket
          .toBuilder()
          .setRequesterPays(false)
          .build()
          .update(Storage.BucketTargetOption.userProject(storage.getOptions().getProjectId()));
    }
  }
}
