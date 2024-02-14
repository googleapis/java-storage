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

package com.google.cloud.storage.it;

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.PackagePrivateMethodWorkarounds;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.CrossRun.Exclude;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP, Transport.GRPC},
    backends = {Backend.PROD})
public class ITObjectTest {

  private static final String CONTENT_TYPE = "text/plain";
  private static final byte[] BLOB_BYTE_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";
  private static final String BLOB_STRING_CONTENT_CRC32C =
      BaseEncoding.base64()
          .encode(
              Ints.toByteArray(
                  Hashing.crc32c().hashBytes(BLOB_STRING_CONTENT.getBytes(UTF_8)).asInt()));
  private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";
  private static final String OTHER_BASE64_KEY = "IcOIQGlliNr5pr3vJb63l+XMqc7NjXqjfw/deBoNxPA=";
  private static final Key KEY =
      new SecretKeySpec(BaseEncoding.base64().decode(BASE64_KEY), "AES256");

  private static final Long RETENTION_PERIOD = 5L;
  private static final Long RETENTION_PERIOD_IN_MILLISECONDS = RETENTION_PERIOD * 1000;

  @Inject public Generator generator;

  @Inject
  @BucketFixture(BucketType.DEFAULT)
  public BucketInfo bucket;

  @Inject
  @BucketFixture(BucketType.REQUESTER_PAYS)
  public BucketInfo requesterPaysBucket;

  @Inject public Storage storage;

  @Test
  public void testCreateBlob() {
    String blobName = generator.randomObjectName();
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName).setCustomTime(System.currentTimeMillis()).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    assertNotNull(remoteBlob.getCustomTime());
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
    assertTrue(remoteBlob.delete());
  }

  @Test
  public void testCreateBlobMd5Crc32cFromHexString() {
    String blobName = generator.randomObjectName();
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMd5FromHexString("3b54781b51c94835084898e821899585")
            .setCrc32cFromHexString("f4ddc43d")
            .build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    assertEquals(blob.getMd5ToHexString(), remoteBlob.getMd5ToHexString());
    assertEquals(blob.getCrc32cToHexString(), remoteBlob.getCrc32cToHexString());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
    assertTrue(remoteBlob.delete());
  }

  @Test
  public void testCreateGetBlobWithEncryptionKey() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT, BlobTargetOption.encryptionKey(KEY));
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    byte[] readBytes =
        storage.readAllBytes(
            bucket.getName(), blobName, BlobSourceOption.decryptionKey(BASE64_KEY));
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
    remoteBlob =
        storage.get(
            blob.getBlobId(),
            BlobGetOption.decryptionKey(BASE64_KEY),
            BlobGetOption.fields(BlobField.CRC32C, BlobField.MD5HASH));
    assertNotNull(remoteBlob.getCrc32c());
    assertNotNull(remoteBlob.getMd5());
  }

  @Test
  public void testCreateEmptyBlob() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertArrayEquals(new byte[0], readBytes);
  }

  @Test
  public void testZeroByteFileUpload() throws Exception {
    String blobName = generator.randomObjectName();
    BlobId blobId = BlobId.of(bucket.getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    File zeroByteFile = File.createTempFile("zerobyte", null);
    zeroByteFile.deleteOnExit();

    storage.createFrom(blobInfo, Paths.get(zeroByteFile.getAbsolutePath()));

    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertArrayEquals(new byte[0], readBytes);
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobStream() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).setContentType(CONTENT_TYPE).build();
    ByteArrayInputStream stream = new ByteArrayInputStream(BLOB_STRING_CONTENT.getBytes(UTF_8));
    Blob remoteBlob = storage.create(blob, stream);
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    assertEquals(blob.getContentType(), remoteBlob.getContentType());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertEquals(BLOB_STRING_CONTENT, new String(readBytes, UTF_8));
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobStreamDisableGzipContent() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).setContentType(CONTENT_TYPE).build();
    ByteArrayInputStream stream = new ByteArrayInputStream(BLOB_STRING_CONTENT.getBytes(UTF_8));
    Blob remoteBlob = storage.create(blob, stream, BlobWriteOption.disableGzipContent());
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    assertEquals(blob.getContentType(), remoteBlob.getContentType());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), blobName);
    assertEquals(BLOB_STRING_CONTENT, new String(readBytes, UTF_8));
  }

  @Test
  public void testCreateBlobFail() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobInfo wrongGenerationBlob = BlobInfo.newBuilder(bucket, blobName, -1L).build();
    try {
      storage.create(wrongGenerationBlob, BLOB_BYTE_CONTENT, BlobTargetOption.generationMatch());
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testGetBlobEmptySelectedFields() {

    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).setContentType(CONTENT_TYPE).build();
    assertNotNull(storage.create(blob));
    Blob remoteBlob = storage.get(blob.getBlobId(), BlobGetOption.fields());
    assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
    assertNull(remoteBlob.getContentType());
  }

  @Test
  public void testGetBlobSelectedFields() {

    String blobName = generator.randomObjectName();
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(ImmutableMap.of("k", "v"))
            .build();
    assertNotNull(storage.create(blob));
    Blob remoteBlob = storage.get(blob.getBlobId(), BlobGetOption.fields(BlobField.METADATA));
    assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
    assertEquals(ImmutableMap.of("k", "v"), remoteBlob.getMetadata());
    assertNull(remoteBlob.getContentType());
  }

  @Test
  public void testGetBlobAllSelectedFields() {

    String blobName = generator.randomObjectName();
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(ImmutableMap.of("k", "v"))
            .build();
    assertNotNull(storage.create(blob));
    Blob remoteBlob = storage.get(blob.getBlobId(), BlobGetOption.fields(BlobField.values()));
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    assertEquals(ImmutableMap.of("k", "v"), remoteBlob.getMetadata());
  }

  @Test
  public void testGetBlobFail() {
    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobId wrongGenerationBlob = BlobId.of(bucket.getName(), blobName);
    try {
      storage.get(wrongGenerationBlob, BlobGetOption.generationMatch(-1));
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testGetBlobFailNonExistingGeneration() {
    String blobName = "test-get-blob-fail-non-existing-generation";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobId wrongGenerationBlob = BlobId.of(bucket.getName(), blobName, -1L);
    try {
      assertNull(storage.get(wrongGenerationBlob));
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }
  }

  @Test
  public void testListBlobsSelectedFields() {
    String baseName = generator.randomObjectName();

    String name1 = baseName + "1";
    String name2 = baseName + "2";

    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    BlobInfo blob1 = BlobInfo.newBuilder(bucket, name1).setMetadata(metadata).build();
    BlobInfo blob2 = BlobInfo.newBuilder(bucket, name2).setMetadata(metadata).build();
    Blob remoteBlob1 = storage.create(blob1);
    Blob remoteBlob2 = storage.create(blob2);

    ImmutableSet<Map<String, String>> expected =
        Stream.of(remoteBlob1, remoteBlob2)
            .map(BlobInfo::getMetadata)
            .collect(ImmutableSet.toImmutableSet());

    Page<Blob> page =
        storage.list(
            bucket.getName(),
            Storage.BlobListOption.prefix(baseName),
            Storage.BlobListOption.fields(BlobField.METADATA));

    ImmutableSet<Blob> blobs = ImmutableSet.copyOf(page.iterateAll());

    ImmutableSet<Map<String, String>> actual =
        blobs.stream().map(BlobInfo::getMetadata).collect(ImmutableSet.toImmutableSet());

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void getBlobReturnNullOn404() {
    String bucketName = bucket.getName();
    String objectName = generator.randomObjectName() + "__d_o_e_s__n_o_t__e_x_i_s_t__";
    BlobId id = BlobId.of(bucketName, objectName);
    Blob blob = storage.get(id);
    assertThat(blob).isNull();
  }

  @Test
  public void testListBlobRequesterPays() throws InterruptedException {
    String projectId = storage.getOptions().getProjectId();

    String prefix = generator.randomObjectName();
    BlobInfo blobInfo1 =
        BlobInfo.newBuilder(requesterPaysBucket.getName(), prefix + "1")
            .setContentType(CONTENT_TYPE)
            .build();
    Blob blob1 = storage.create(blobInfo1, BlobTargetOption.userProject(projectId));
    assertNotNull(blob1);

    // Test listing a Requester Pays bucket.
    Bucket remoteBucket =
        storage.get(
            requesterPaysBucket.getName(),
            BucketGetOption.fields(BucketField.ID, BucketField.BILLING),
            BucketGetOption.userProject(projectId));

    assertTrue(remoteBucket.requesterPays());
    // TODO: split to own test which modifies a temp bucket
    // Bucket updatedBucket = storage.update(remoteBucket);
    // assertTrue(updatedBucket.requesterPays());
    try {
      storage.list(
          requesterPaysBucket.getName(),
          BlobListOption.prefix(prefix),
          BlobListOption.fields(),
          BlobListOption.userProject("fakeBillingProjectId"));
      fail("Expected bad user project error.");
    } catch (StorageException e) {
      assertTrue(e.getMessage().contains("User project specified in the request is invalid"));
    }

    Page<Blob> page =
        storage.list(
            requesterPaysBucket.getName(),
            BlobListOption.prefix(prefix),
            BlobListOption.userProject(projectId));
    List<BlobInfo> blobs =
        StreamSupport.stream(page.iterateAll().spliterator(), false)
            .map(PackagePrivateMethodWorkarounds::noAcl)
            .collect(ImmutableList.toImmutableList());
    // gRPC and json have differing defaults on projections b/258835631
    assertThat(blobs).contains(PackagePrivateMethodWorkarounds.noAcl(blob1));
  }

  @Test
  public void testListBlobsVersioned() throws ExecutionException, InterruptedException {
    String bucketName = generator.randomBucketName();
    Bucket bucket =
        storage.create(BucketInfo.newBuilder(bucketName).setVersioningEnabled(true).build());
    try {
      String[] blobNames = {"test-list-blobs-versioned-blob1", "test-list-blobs-versioned-blob2"};
      BlobInfo blob1 =
          BlobInfo.newBuilder(bucket, blobNames[0]).setContentType(CONTENT_TYPE).build();
      BlobInfo blob2 =
          BlobInfo.newBuilder(bucket, blobNames[1]).setContentType(CONTENT_TYPE).build();
      Blob remoteBlob1 = storage.create(blob1);
      Blob remoteBlob2 = storage.create(blob2);
      Blob remoteBlob3 = storage.create(blob2);
      assertNotNull(remoteBlob1);
      assertNotNull(remoteBlob2);
      assertNotNull(remoteBlob3);
      Page<Blob> page =
          storage.list(
              bucketName,
              BlobListOption.prefix("test-list-blobs-versioned-blob"),
              BlobListOption.versions(true));
      // https://cloud.google.com/storage/docs/consistency#strongly_consistent_operations
      // enabling versioning on an existing bucket seems to have some backpressure on when new
      // versions can safely be made, but listing is not eventually consistent.

      // TODO: make hermetic
      // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
      // test fails if timeout is reached.
      while (Iterators.size(page.iterateAll().iterator()) != 3) {
        Thread.sleep(500);
        page =
            storage.list(
                bucketName,
                BlobListOption.prefix("test-list-blobs-versioned-blob"),
                BlobListOption.versions(true));
      }
      Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
      Iterator<Blob> iterator = page.iterateAll().iterator();
      while (iterator.hasNext()) {
        Blob remoteBlob = iterator.next();
        assertEquals(bucketName, remoteBlob.getBucket());
        assertTrue(blobSet.contains(remoteBlob.getName()));
        assertNotNull(remoteBlob.getGeneration());
      }
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testListBlobsWithOffset() throws ExecutionException, InterruptedException {
    String bucketName = generator.randomBucketName();
    Bucket bucket =
        storage.create(BucketInfo.newBuilder(bucketName).setVersioningEnabled(true).build());
    try {
      List<String> blobNames =
          ImmutableList.of("startOffset_blob1", "startOffset_blob2", "blob3_endOffset");
      BlobInfo blob1 =
          BlobInfo.newBuilder(bucket, blobNames.get(0)).setContentType(CONTENT_TYPE).build();
      BlobInfo blob2 =
          BlobInfo.newBuilder(bucket, blobNames.get(1)).setContentType(CONTENT_TYPE).build();
      BlobInfo blob3 =
          BlobInfo.newBuilder(bucket, blobNames.get(2)).setContentType(CONTENT_TYPE).build();

      Blob remoteBlob1 = storage.create(blob1);
      Blob remoteBlob2 = storage.create(blob2);
      Blob remoteBlob3 = storage.create(blob3);
      assertNotNull(remoteBlob1);
      assertNotNull(remoteBlob2);
      assertNotNull(remoteBlob3);

      // Listing blobs without BlobListOptions.
      Page<Blob> page1 = storage.list(bucketName);
      assertEquals(3, Iterators.size(page1.iterateAll().iterator()));

      // Listing blobs with startOffset.
      Page<Blob> page2 = storage.list(bucketName, BlobListOption.startOffset("startOffset"));
      assertEquals(2, Iterators.size(page2.iterateAll().iterator()));

      // Listing blobs with endOffset.
      Page<Blob> page3 = storage.list(bucketName, BlobListOption.endOffset("endOffset"));
      assertEquals(1, Iterators.size(page3.iterateAll().iterator()));

      // Listing blobs with startOffset and endOffset.
      Page<Blob> page4 =
          storage.list(
              bucketName,
              BlobListOption.startOffset("startOffset"),
              BlobListOption.endOffset("endOffset"));
      assertEquals(0, Iterators.size(page4.iterateAll().iterator()));
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testListBlobsCurrentDirectoryIncludesBothObjectsAndSyntheticDirectories() {
    String bucketName = bucket.getName();
    String directoryName = generator.randomObjectName();
    String subdirectoryName = "subdirectory";

    String uriSubDir = String.format("gs://%s/%s/%s/", bucketName, directoryName, subdirectoryName);
    String uri1 = String.format("gs://%s/%s/%s/blob1", bucketName, directoryName, subdirectoryName);
    String uri2 = String.format("gs://%s/%s/blob2", bucketName, directoryName);

    BlobId id1 = BlobId.fromGsUtilUri(uri1);
    BlobId id2 = BlobId.fromGsUtilUri(uri2);
    BlobId idSubDir = BlobId.fromGsUtilUri(uriSubDir);

    BlobInfo blob1 = BlobInfo.newBuilder(id1).build();
    BlobInfo blob2 = BlobInfo.newBuilder(id2).build();
    BlobInfo obj1Gen1 = storage.create(blob1, BLOB_BYTE_CONTENT).asBlobInfo();
    BlobInfo obj2Gen1 = storage.create(blob2, BLOB_BYTE_CONTENT).asBlobInfo();

    Page<Blob> page1 =
        storage.list(
            bucketName,
            BlobListOption.prefix(directoryName + "/"),
            BlobListOption.currentDirectory());

    ImmutableList<Blob> blobs = ImmutableList.copyOf(page1.iterateAll());

    ImmutableSet<BlobInfo> actual =
        blobs.stream()
            .map(Blob::asBlobInfo)
            .map(info -> PackagePrivateMethodWorkarounds.noAcl(info))
            .collect(ImmutableSet.toImmutableSet());

    // obj1Gen1 is "in subdirectory" and we don't expect to receive it as a result when listing
    // object in "the current directory"
    assertThat(actual).doesNotContain(obj1Gen1);

    // make sure one of the results we received is the "subdirectory" blob1 is "in"
    Optional<BlobInfo> first = actual.stream().filter(BlobInfo::isDirectory).findFirst();
    assertThat(first.isPresent()).isTrue();
    assertThat(first.get().getBlobId()).isEqualTo(idSubDir);

    assertThat(actual).contains(PackagePrivateMethodWorkarounds.noAcl(obj2Gen1));
  }

  @Test
  // When gRPC support is added for matchGlob, enable this test for gRPC.
  @Exclude(transports = Transport.GRPC)
  public void testListBlobsWithMatchGlob() throws Exception {
    BucketInfo bucketInfo = BucketInfo.newBuilder(generator.randomBucketName()).build();
    try (TemporaryBucket tempBucket =
        TemporaryBucket.newBuilder().setBucketInfo(bucketInfo).setStorage(storage).build()) {
      BucketInfo bucket = tempBucket.getBucket();
      assertNotNull(storage.create(BlobInfo.newBuilder(bucket, "foo/bar").build()));
      assertNotNull(storage.create(BlobInfo.newBuilder(bucket, "foo/baz").build()));
      assertNotNull(storage.create(BlobInfo.newBuilder(bucket, "foo/foobar").build()));
      assertNotNull(storage.create(BlobInfo.newBuilder(bucket, "foobar").build()));

      Page<Blob> page1 = storage.list(bucket.getName(), BlobListOption.matchGlob("foo*bar"));
      Page<Blob> page2 = storage.list(bucket.getName(), BlobListOption.matchGlob("foo**bar"));
      Page<Blob> page3 = storage.list(bucket.getName(), BlobListOption.matchGlob("**/foobar"));
      Page<Blob> page4 = storage.list(bucket.getName(), BlobListOption.matchGlob("*/ba[rz]"));
      Page<Blob> page5 = storage.list(bucket.getName(), BlobListOption.matchGlob("*/ba[!a-y]"));
      Page<Blob> page6 =
          storage.list(bucket.getName(), BlobListOption.matchGlob("**/{foobar,baz}"));
      Page<Blob> page7 =
          storage.list(bucket.getName(), BlobListOption.matchGlob("foo/{foo*,*baz}"));
      assertAll(
          () ->
              assertThat(Iterables.transform(page1.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foobar")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page2.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/bar", "foo/foobar", "foobar")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page3.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/foobar", "foobar")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page4.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/bar", "foo/baz")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page5.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/baz")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page6.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/baz", "foo/foobar", "foobar")
                  .inOrder(),
          () ->
              assertThat(Iterables.transform(page7.iterateAll(), blob -> blob.getName()))
                  .containsExactly("foo/baz", "foo/foobar")
                  .inOrder());
    }
  }

  @Test
  public void testListBlobsMultiplePages() {
    String basePath = generator.randomObjectName();

    ImmutableList<BlobInfo> expected =
        IntStream.rangeClosed(1, 10)
            .mapToObj(i -> String.format("%s/%2d", basePath, i))
            .map(name -> BlobInfo.newBuilder(bucket, name).build())
            .map(info -> storage.create(info, BlobTargetOption.doesNotExist()))
            .map(info1 -> PackagePrivateMethodWorkarounds.noAcl(info1))
            .collect(ImmutableList.toImmutableList());

    Page<Blob> page =
        storage.list(bucket.getName(), BlobListOption.prefix(basePath), BlobListOption.pageSize(3));

    ImmutableList<BlobInfo> actual =
        ImmutableList.copyOf(page.iterateAll()).stream()
            .map(info -> PackagePrivateMethodWorkarounds.noAcl(info))
            .collect(ImmutableList.toImmutableList());

    try {
      assertThat(actual).isEqualTo(expected);
    } finally {
      // delete all the objects we created
      expected.stream().map(BlobInfo::getBlobId).forEach(storage::delete);
    }
  }

  @Test
  public void testUpdateBlob() {
    String blobName = "test-update-blob";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    Blob updatedBlob = remoteBlob.toBuilder().setContentType(CONTENT_TYPE).build().update();
    assertNotNull(updatedBlob);
    assertEquals(blob.getName(), updatedBlob.getName());
    assertEquals(blob.getBucket(), updatedBlob.getBucket());
    assertEquals(CONTENT_TYPE, updatedBlob.getContentType());
  }

  @Test
  public void testUpdateBlobReplaceMetadata() {
    String blobName = "test-update-blob-replace-metadata";
    ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a");
    ImmutableMap<String, String> newMetadata = ImmutableMap.of("k2", "b");
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    Blob updatedBlob = remoteBlob.toBuilder().setMetadata(null).build().update();
    assertNotNull(updatedBlob);
    assertNull(updatedBlob.getMetadata());
    updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
    assertEquals(blob.getName(), updatedBlob.getName());
    assertEquals(blob.getBucket(), updatedBlob.getBucket());
    assertEquals(newMetadata, updatedBlob.getMetadata());
  }

  @Test
  public void testUpdateBlobMergeMetadata() {
    String blobName = "test-update-blob-merge-metadata";
    ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a");
    ImmutableMap<String, String> newMetadata = ImmutableMap.of("k2", "b");
    ImmutableMap<String, String> expectedMetadata = ImmutableMap.of("k1", "a", "k2", "b");
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    Blob updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
    assertNotNull(updatedBlob);
    assertEquals(blob.getName(), updatedBlob.getName());
    assertEquals(blob.getBucket(), updatedBlob.getBucket());
    assertEquals(expectedMetadata, updatedBlob.getMetadata());
  }

  @Test
  public void testUpdateBlobUnsetMetadata() {

    String blobName = "test-update-blob-unset-metadata";
    ImmutableMap<String, String> metadata = ImmutableMap.of("k1", "a", "k2", "b");
    Map<String, String> newMetadata = new HashMap<>();
    newMetadata.put("k1", "a");
    newMetadata.put("k2", null);
    ImmutableMap<String, String> expectedMetadata = ImmutableMap.of("k1", "a");
    BlobInfo blob =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    Blob updatedBlob = remoteBlob.toBuilder().setMetadata(newMetadata).build().update();
    assertNotNull(updatedBlob);
    assertEquals(blob.getName(), updatedBlob.getName());
    assertEquals(blob.getBucket(), updatedBlob.getBucket());
    assertEquals(expectedMetadata, updatedBlob.getMetadata());
  }

  @Test
  public void testUpdateBlobFail() {
    String blobName = "test-update-blob-fail";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobInfo wrongGenerationBlob =
        BlobInfo.newBuilder(bucket, blobName, -1L).setContentType(CONTENT_TYPE).build();
    try {
      storage.update(wrongGenerationBlob, BlobTargetOption.generationMatch());
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testDeleteNonExistingBlob() {
    String blobName = "test-delete-non-existing-blob";
    assertFalse(storage.delete(bucket.getName(), blobName));
  }

  @Test
  public void testDeleteBlobNonExistingGeneration() {
    String blobName = "test-delete-blob-non-existing-generation";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    assertNotNull(storage.create(blob));
    try {
      assertFalse(storage.delete(BlobId.of(bucket.getName(), blobName, -1L)));
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }
  }

  @Test
  public void testDeleteBlobFail() {
    String blobName = "test-delete-blob-fail";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    try {
      storage.delete(bucket.getName(), blob.getName(), BlobSourceOption.generationMatch(-1L));
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
    assertTrue(remoteBlob.delete());
  }

  @Test
  public void testComposeBlob() {
    String sourceBlobName1 = "test-compose-blob-source-1";
    String sourceBlobName2 = "test-compose-blob-source-2";
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    Blob remoteSourceBlob1 = storage.create(sourceBlob1, BLOB_BYTE_CONTENT);
    Blob remoteSourceBlob2 = storage.create(sourceBlob2, BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob1);
    assertNotNull(remoteSourceBlob2);
    String targetBlobName = "test-compose-blob-target";
    BlobInfo targetBlob = BlobInfo.newBuilder(bucket, targetBlobName).build();
    ComposeRequest req =
        ComposeRequest.of(ImmutableList.of(sourceBlobName1, sourceBlobName2), targetBlob);
    Blob remoteTargetBlob = storage.compose(req);
    assertNotNull(remoteTargetBlob);
    assertEquals(targetBlob.getName(), remoteTargetBlob.getName());
    assertEquals(targetBlob.getBucket(), remoteTargetBlob.getBucket());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), targetBlobName);
    byte[] composedBytes = Arrays.copyOf(BLOB_BYTE_CONTENT, BLOB_BYTE_CONTENT.length * 2);
    System.arraycopy(
        BLOB_BYTE_CONTENT, 0, composedBytes, BLOB_BYTE_CONTENT.length, BLOB_BYTE_CONTENT.length);
    assertArrayEquals(composedBytes, readBytes);
  }

  @Test
  public void testComposeBlobWithContentType() {
    String sourceBlobName1 = "test-compose-blob-with-content-type-source-1";
    String sourceBlobName2 = "test-compose-blob-with-content-type-source-2";
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    Blob remoteSourceBlob1 = storage.create(sourceBlob1, BLOB_BYTE_CONTENT);
    Blob remoteSourceBlob2 = storage.create(sourceBlob2, BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob1);
    assertNotNull(remoteSourceBlob2);
    String targetBlobName = "test-compose-blob-with-content-type-target";
    BlobInfo targetBlob =
        BlobInfo.newBuilder(bucket, targetBlobName).setContentType(CONTENT_TYPE).build();
    ComposeRequest req =
        ComposeRequest.of(ImmutableList.of(sourceBlobName1, sourceBlobName2), targetBlob);
    Blob remoteTargetBlob = storage.compose(req);
    assertNotNull(remoteTargetBlob);
    assertEquals(targetBlob.getName(), remoteTargetBlob.getName());
    assertEquals(targetBlob.getBucket(), remoteTargetBlob.getBucket());
    assertEquals(CONTENT_TYPE, remoteTargetBlob.getContentType());
    byte[] readBytes = storage.readAllBytes(bucket.getName(), targetBlobName);
    byte[] composedBytes = Arrays.copyOf(BLOB_BYTE_CONTENT, BLOB_BYTE_CONTENT.length * 2);
    System.arraycopy(
        BLOB_BYTE_CONTENT, 0, composedBytes, BLOB_BYTE_CONTENT.length, BLOB_BYTE_CONTENT.length);
    assertArrayEquals(composedBytes, readBytes);
  }

  @Test
  public void testComposeBlobFail() {
    String sourceBlobName1 = "test-compose-blob-fail-source-1";
    String sourceBlobName2 = "test-compose-blob-fail-source-2";
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    Blob remoteSourceBlob1 = storage.create(sourceBlob1);
    Blob remoteSourceBlob2 = storage.create(sourceBlob2);
    assertNotNull(remoteSourceBlob1);
    assertNotNull(remoteSourceBlob2);
    String targetBlobName = "test-compose-blob-fail-target";
    BlobInfo targetBlob = BlobInfo.newBuilder(bucket, targetBlobName).build();
    ComposeRequest req =
        ComposeRequest.newBuilder()
            .addSource(sourceBlobName1, -1L)
            .addSource(sourceBlobName2, -1L)
            .setTarget(targetBlob)
            .build();
    try {
      storage.compose(req);
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlob() {

    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    BlobInfo blob =
        BlobInfo.newBuilder(source).setContentType(CONTENT_TYPE).setMetadata(metadata).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    String targetBlobName = generator.randomObjectName() + "-target";
    CopyRequest req = CopyRequest.of(source, BlobId.of(bucket.getName(), targetBlobName));
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    assertTrue(remoteBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobWithPredefinedAcl() {

    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    BlobInfo blob =
        BlobInfo.newBuilder(source).setContentType(CONTENT_TYPE).setMetadata(metadata).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    String targetBlobName = generator.randomObjectName() + "-target";
    CopyRequest req =
        CopyRequest.newBuilder()
            .setSource(source)
            .setTarget(
                BlobId.of(bucket.getName(), targetBlobName),
                BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ))
            .build();
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertNotNull(copyWriter.getResult().getAcl(User.ofAllUsers()));
    assertTrue(copyWriter.isDone());
    assertTrue(remoteBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobWithEncryptionKeys() {

    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    Blob remoteBlob =
        storage.create(
            BlobInfo.newBuilder(source).build(),
            BLOB_BYTE_CONTENT,
            BlobTargetOption.encryptionKey(KEY));
    assertNotNull(remoteBlob);
    String targetBlobName = generator.randomObjectName() + "-target";
    BlobInfo target =
        BlobInfo.newBuilder(bucket, targetBlobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    CopyRequest req =
        CopyRequest.newBuilder()
            .setSource(source)
            .setTarget(target, BlobTargetOption.encryptionKey(OTHER_BASE64_KEY))
            .setSourceOptions(BlobSourceOption.decryptionKey(BASE64_KEY))
            .build();
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertArrayEquals(
        BLOB_BYTE_CONTENT,
        copyWriter.getResult().getContent(Blob.BlobSourceOption.decryptionKey(OTHER_BASE64_KEY)));
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    req =
        CopyRequest.newBuilder()
            .setSource(source)
            .setTarget(target)
            .setSourceOptions(BlobSourceOption.decryptionKey(BASE64_KEY))
            .build();
    copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertArrayEquals(BLOB_BYTE_CONTENT, copyWriter.getResult().getContent());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    assertTrue(remoteBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobUpdateMetadata() {

    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    Blob remoteSourceBlob = storage.create(BlobInfo.newBuilder(source).build(), BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob);
    String targetBlobName = generator.randomObjectName() + "-target";
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    BlobInfo target =
        BlobInfo.newBuilder(bucket, targetBlobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    CopyRequest req = CopyRequest.of(source, target);
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    assertTrue(remoteSourceBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  // Re-enable this test when it stops failing
  // @Test
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobUpdateStorageClass() {
    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    BlobInfo sourceInfo =
        BlobInfo.newBuilder(source).setStorageClass(StorageClass.STANDARD).build();
    Blob remoteSourceBlob = storage.create(sourceInfo, BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob);
    assertEquals(StorageClass.STANDARD, remoteSourceBlob.getStorageClass());

    String targetBlobName = generator.randomObjectName() + "-target";
    BlobInfo targetInfo =
        BlobInfo.newBuilder(bucket, targetBlobName).setStorageClass(StorageClass.COLDLINE).build();
    CopyRequest req = CopyRequest.of(source, targetInfo);
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(StorageClass.COLDLINE, copyWriter.getResult().getStorageClass());
    assertTrue(copyWriter.isDone());
    assertTrue(remoteSourceBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobNoContentType() {

    String sourceBlobName = generator.randomObjectName() + "-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    Blob remoteSourceBlob = storage.create(BlobInfo.newBuilder(source).build(), BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob);
    String targetBlobName = generator.randomObjectName() + "-target";
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    BlobInfo target = BlobInfo.newBuilder(bucket, targetBlobName).setMetadata(metadata).build();
    CopyRequest req = CopyRequest.of(source, target);
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertNull(copyWriter.getResult().getContentType());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    assertTrue(remoteSourceBlob.delete());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test
  // Verified against testbench
  // Bucket attribute extration on allowlist bug b/246634709
  @Exclude(transports = Transport.GRPC)
  public void testCopyBlobFail() {

    String sourceBlobName = "test-copy-blob-source-fail";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName, -1L);
    Blob remoteSourceBlob = storage.create(BlobInfo.newBuilder(source).build(), BLOB_BYTE_CONTENT);
    assertNotNull(remoteSourceBlob);
    String targetBlobName = "test-copy-blob-target-fail";
    BlobInfo target =
        BlobInfo.newBuilder(bucket, targetBlobName).setContentType(CONTENT_TYPE).build();
    CopyRequest req =
        CopyRequest.newBuilder()
            .setSource(bucket.getName(), sourceBlobName)
            .setSourceOptions(BlobSourceOption.generationMatch(-1L))
            .setTarget(target)
            .build();
    try {
      storage.copy(req);
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
    CopyRequest req2 =
        CopyRequest.newBuilder()
            .setSource(source)
            .setSourceOptions(BlobSourceOption.generationMatch())
            .setTarget(target)
            .build();
    try {
      storage.copy(req2);
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testReadAndWriteChannelWithEncryptionKey() throws IOException {
    String blobName = "test-read-write-channel-with-customer-key-blob";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    byte[] stringBytes;
    try (WriteChannel writer = storage.writer(blob, BlobWriteOption.encryptionKey(BASE64_KEY))) {
      stringBytes = BLOB_STRING_CONTENT.getBytes(UTF_8);
      writer.write(ByteBuffer.wrap(BLOB_BYTE_CONTENT));
      writer.write(ByteBuffer.wrap(stringBytes));
    }
    ByteBuffer readBytes;
    ByteBuffer readStringBytes;
    try (ReadChannel reader =
        storage.reader(blob.getBlobId(), BlobSourceOption.decryptionKey(KEY))) {
      readBytes = ByteBuffer.allocate(BLOB_BYTE_CONTENT.length);
      readStringBytes = ByteBuffer.allocate(stringBytes.length);
      reader.read(readBytes);
      reader.read(readStringBytes);
    }
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes.array());
    assertEquals(BLOB_STRING_CONTENT, new String(readStringBytes.array(), UTF_8));
    assertTrue(storage.delete(bucket.getName(), blobName));
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_0B() throws IOException {
    doTestReadAndWriteChannelsWithSize(0);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_700B() throws IOException {
    doTestReadAndWriteChannelsWithSize(700);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_8193B() throws IOException {
    doTestReadAndWriteChannelsWithSize(4 * 1024);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_256KiB() throws IOException {
    doTestReadAndWriteChannelsWithSize(256 * 1024);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_2MiB() throws IOException {
    doTestReadAndWriteChannelsWithSize(2 * 1024 * 1024);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_4MiB() throws IOException {
    doTestReadAndWriteChannelsWithSize(4 * 1024 * 1024);
  }

  @Test
  public void testReadAndWriteChannelsWithDifferentFileSize_4MiB_plus1() throws IOException {
    doTestReadAndWriteChannelsWithSize((4 * 1024 * 1024) + 1);
  }

  private void doTestReadAndWriteChannelsWithSize(int blobSize) throws IOException {
    String blobName = String.format("%s-%d", generator.randomObjectName(), blobSize);
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Random rnd = new Random();
    byte[] bytes = new byte[blobSize];
    rnd.nextBytes(bytes);
    try (WriteChannel writer = storage.writer(blob)) {
      writer.write(ByteBuffer.wrap(bytes));
    }
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (ReadChannel reader = storage.reader(blob.getBlobId())) {
      ByteStreams.copy(reader, Channels.newChannel(output));
    }
    byte[] actual = output.toByteArray();
    assertThat(actual).isEqualTo(bytes);
    assertTrue(storage.delete(bucket.getName(), blobName));
  }

  @Test
  // Capture not implemented yet
  @Exclude(transports = Transport.GRPC)
  public void testReadAndWriteCaptureChannels() throws IOException {

    String blobName = generator.randomObjectName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    byte[] stringBytes;
    WriteChannel writer = storage.writer(blob);
    stringBytes = BLOB_STRING_CONTENT.getBytes(UTF_8);
    writer.write(ByteBuffer.wrap(BLOB_BYTE_CONTENT));
    RestorableState<WriteChannel> writerState = writer.capture();
    WriteChannel secondWriter = writerState.restore();
    secondWriter.write(ByteBuffer.wrap(stringBytes));
    secondWriter.close();
    ByteBuffer readBytes;
    ByteBuffer readStringBytes;
    ReadChannel reader = storage.reader(blob.getBlobId());
    reader.setChunkSize(BLOB_BYTE_CONTENT.length);
    readBytes = ByteBuffer.allocate(BLOB_BYTE_CONTENT.length);
    reader.read(readBytes);
    RestorableState<ReadChannel> readerState = reader.capture();
    ReadChannel secondReader = readerState.restore();
    readStringBytes = ByteBuffer.allocate(stringBytes.length);
    secondReader.read(readStringBytes);
    reader.close();
    secondReader.close();
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes.array());
    assertEquals(BLOB_STRING_CONTENT, new String(readStringBytes.array(), UTF_8));
    assertTrue(storage.delete(bucket.getName(), blobName));
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testGetBlobs() {
    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    assertNotNull(storage.create(sourceBlob2));
    List<Blob> remoteBlobs = storage.get(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
    assertEquals(sourceBlob1.getBucket(), remoteBlobs.get(0).getBucket());
    assertEquals(sourceBlob1.getName(), remoteBlobs.get(0).getName());
    assertEquals(sourceBlob2.getBucket(), remoteBlobs.get(1).getBucket());
    assertEquals(sourceBlob2.getName(), remoteBlobs.get(1).getName());
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testGetBlobsFail() {

    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    List<Blob> remoteBlobs = storage.get(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
    assertEquals(sourceBlob1.getBucket(), remoteBlobs.get(0).getBucket());
    assertEquals(sourceBlob1.getName(), remoteBlobs.get(0).getName());
    assertNull(remoteBlobs.get(1));
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testDeleteBlobs() {

    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    assertNotNull(storage.create(sourceBlob2));
    List<Boolean> deleteStatus = storage.delete(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
    assertTrue(deleteStatus.get(0));
    assertTrue(deleteStatus.get(1));
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testDeleteBlobsFail() {
    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    List<Boolean> deleteStatus = storage.delete(sourceBlob1.getBlobId(), sourceBlob2.getBlobId());
    assertTrue(deleteStatus.get(0));
    assertFalse(deleteStatus.get(1));
  }

  @Test
  public void testDeleteBlob() {
    String sourceBlobName = generator.randomObjectName();
    BlobInfo sourceBlob = BlobInfo.newBuilder(bucket, sourceBlobName).build();
    assertNotNull(storage.create(sourceBlob));
    boolean result = storage.delete(sourceBlob.getBlobId());
    assertTrue(result);
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testUpdateBlobs() {

    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    Blob remoteBlob1 = storage.create(sourceBlob1);
    Blob remoteBlob2 = storage.create(sourceBlob2);
    assertNotNull(remoteBlob1);
    assertNotNull(remoteBlob2);
    List<Blob> updatedBlobs =
        storage.update(
            remoteBlob1.toBuilder().setContentType(CONTENT_TYPE).build(),
            remoteBlob2.toBuilder().setContentType(CONTENT_TYPE).build());
    assertEquals(sourceBlob1.getBucket(), updatedBlobs.get(0).getBucket());
    assertEquals(sourceBlob1.getName(), updatedBlobs.get(0).getName());
    assertEquals(CONTENT_TYPE, updatedBlobs.get(0).getContentType());
    assertEquals(sourceBlob2.getBucket(), updatedBlobs.get(1).getBucket());
    assertEquals(sourceBlob2.getName(), updatedBlobs.get(1).getName());
    assertEquals(CONTENT_TYPE, updatedBlobs.get(1).getContentType());
  }

  @Test
  // Only supported in JSON right now
  @Exclude(transports = Transport.GRPC)
  public void testUpdateBlobsFail() {

    String sourceBlobName1 = generator.randomObjectName();
    String sourceBlobName2 = generator.randomObjectName();
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucket, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucket, sourceBlobName2).build();
    BlobInfo remoteBlob1 = storage.create(sourceBlob1);
    assertNotNull(remoteBlob1);
    List<Blob> updatedBlobs =
        storage.update(
            remoteBlob1.toBuilder().setContentType(CONTENT_TYPE).build(),
            sourceBlob2.toBuilder().setContentType(CONTENT_TYPE).build());
    assertEquals(sourceBlob1.getBucket(), updatedBlobs.get(0).getBucket());
    assertEquals(sourceBlob1.getName(), updatedBlobs.get(0).getName());
    assertEquals(CONTENT_TYPE, updatedBlobs.get(0).getContentType());
    assertNull(updatedBlobs.get(1));
  }

  @Test
  public void testAttemptObjectDeleteWithRetentionPolicy()
      throws ExecutionException, InterruptedException {
    String bucketName = generator.randomBucketName();
    Bucket remoteBucket =
        storage.create(
            BucketInfo.newBuilder(bucketName).setRetentionPeriod(RETENTION_PERIOD).build());
    assertEquals(RETENTION_PERIOD, remoteBucket.getRetentionPeriod());
    String blobName = generator.randomObjectName();
    BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
    Blob remoteBlob = storage.create(blobInfo);
    assertNotNull(remoteBlob.getRetentionExpirationTime());
    try {
      remoteBlob.delete();
      fail("Expected failure on delete from retentionPolicy");
    } catch (StorageException ex) {
      // expected
    } finally {
      Thread.sleep(RETENTION_PERIOD_IN_MILLISECONDS);
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testEnableDisableTemporaryHold() {
    String blobName = generator.randomObjectName();
    BlobInfo blobInfo = BlobInfo.newBuilder(bucket, blobName).setTemporaryHold(true).build();
    Blob remoteBlob = storage.create(blobInfo);
    assertTrue(remoteBlob.getTemporaryHold());
    remoteBlob =
        storage.get(remoteBlob.getBlobId(), BlobGetOption.fields(BlobField.TEMPORARY_HOLD));
    assertTrue(remoteBlob.getTemporaryHold());
    remoteBlob = remoteBlob.toBuilder().setTemporaryHold(false).build().update();
    assertFalse(remoteBlob.getTemporaryHold());
  }

  @Test
  public void testAttemptObjectDeleteWithEventBasedHold() {
    String blobName = generator.randomObjectName();
    BlobInfo blobInfo = BlobInfo.newBuilder(bucket, blobName).setEventBasedHold(true).build();
    Blob remoteBlob = storage.create(blobInfo);
    assertTrue(remoteBlob.getEventBasedHold());
    try {
      remoteBlob.delete();
      fail("Expected failure on delete from eventBasedHold");
    } catch (StorageException ex) {
      // expected
    } finally {
      remoteBlob.toBuilder().setEventBasedHold(false).build().update();
    }
  }

  @Test
  public void testAttemptDeletionObjectTemporaryHold() {
    String blobName = generator.randomObjectName();
    BlobInfo blobInfo = BlobInfo.newBuilder(bucket, blobName).setTemporaryHold(true).build();
    Blob remoteBlob = storage.create(blobInfo);
    assertTrue(remoteBlob.getTemporaryHold());
    try {
      remoteBlob.delete();
      fail("Expected failure on delete from temporaryHold");
    } catch (StorageException ex) {
      // expected
    } finally {
      remoteBlob.toBuilder().setTemporaryHold(false).build().update();
    }
  }

  @Test
  public void testBlobReload() throws Exception {
    String blobName = generator.randomObjectName();
    BlobId blobId = BlobId.of(bucket.getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    Blob blob = storage.create(blobInfo, new byte[] {0, 1, 2});

    Blob blobUnchanged = blob.reload();
    // gRPC and json have differing defaults on projections b/258835631
    assertThat(blobUnchanged).isAnyOf(blob, PackagePrivateMethodWorkarounds.noAcl(blob));

    blob.writer().close();
    try {
      blob.reload(Blob.BlobSourceOption.generationMatch());
      fail("StorageException was expected");
    } catch (StorageException e) {
      assertEquals(412, e.getCode());
    }

    Blob updated = blob.reload();
    assertEquals(blob.getBucket(), updated.getBucket());
    assertEquals(blob.getName(), updated.getName());
    assertNotEquals(blob.getGeneration(), updated.getGeneration());
    assertEquals(new Long(0), updated.getSize());

    updated.delete();
    assertNull(updated.reload());
  }

  @Test
  public void testUploadWithEncryption() throws Exception {
    String blobName = generator.randomObjectName();
    BlobId blobId = BlobId.of(bucket.getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    ByteArrayInputStream content = new ByteArrayInputStream(BLOB_BYTE_CONTENT);
    Blob blob = storage.createFrom(blobInfo, content, BlobWriteOption.encryptionKey(KEY));

    try {
      blob.getContent();
      fail("StorageException was expected");
    } catch (StorageException e) {
      String expectedMessage =
          "The target object is encrypted by a customer-supplied encryption key.";
      assertTrue(e.getMessage().contains(expectedMessage));
      assertEquals(400, e.getCode());
    }
    byte[] readBytes = blob.getContent(Blob.BlobSourceOption.decryptionKey(KEY));
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
  }

  private Blob createBlob(String method, BlobInfo blobInfo, boolean detectType) throws IOException {
    switch (method) {
      case "create":
        return detectType
            ? storage.create(blobInfo, BlobTargetOption.detectContentType())
            : storage.create(blobInfo);
      case "createFrom":
        InputStream inputStream = new ByteArrayInputStream(BLOB_BYTE_CONTENT);
        return detectType
            ? storage.createFrom(blobInfo, inputStream, BlobWriteOption.detectContentType())
            : storage.createFrom(blobInfo, inputStream);
      case "writer":
        if (detectType) {
          storage.writer(blobInfo, BlobWriteOption.detectContentType()).close();
        } else {
          storage.writer(blobInfo).close();
        }
        return storage.get(BlobId.of(blobInfo.getBucket(), blobInfo.getName()));
      default:
        throw new IllegalArgumentException("Unknown method " + method);
    }
  }

  private void testAutoContentType(String method) throws IOException {
    String[] names = {
      generator.randomObjectName() + ".txt",
      generator.randomObjectName() + "with space/Pic.Jpg",
      generator.randomObjectName() + "no_extension"
    };
    String[] types = {"text/plain", "image/jpeg", "application/octet-stream"};
    for (int i = 0; i < names.length; i++) {
      BlobId blobId = BlobId.of(bucket.getName(), names[i]);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      Blob blob_true = createBlob(method, blobInfo, true);
      assertEquals(types[i], blob_true.getContentType());

      Blob blob_false = createBlob(method, blobInfo, false);
      assertThat(blob_false.getContentType()).isAnyOf("application/octet-stream", "");
    }
    String customType = "custom/type";
    BlobId blobId = BlobId.of(bucket.getName(), names[0]);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(customType).build();
    Blob blob = createBlob(method, blobInfo, true);
    assertEquals(customType, blob.getContentType());
  }

  @Test
  public void testAutoContentTypeCreate() throws IOException {
    testAutoContentType("create");
  }

  @Test
  public void testAutoContentTypeCreateFrom() throws IOException {
    testAutoContentType("createFrom");
  }

  @Test
  public void testAutoContentTypeWriter() throws IOException {
    testAutoContentType("writer");
  }

  @Test
  public void testBlobTimeStorageClassUpdated() {

    String blobName = generator.randomObjectName();
    StorageClass storageClass = StorageClass.COLDLINE;
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).setStorageClass(storageClass).build();
    Blob remoteBlob = storage.create(blob);
    assertThat(remoteBlob).isNotNull();
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertThat(remoteBlob.getName()).isEqualTo(blob.getName());
    assertThat(remoteBlob.getCreateTime()).isNotNull();
    assertThat(remoteBlob.getUpdateTime()).isEqualTo(remoteBlob.getCreateTime());
    assertThat(remoteBlob.getTimeStorageClassUpdated()).isEqualTo(remoteBlob.getCreateTime());

    // We can't change an object's storage class directly, the only way is to rewrite the object
    // with the desired storage class.
    BlobId blobId = BlobId.of(bucket.getName(), blobName);
    CopyRequest request =
        CopyRequest.newBuilder()
            .setSource(blobId)
            .setTarget(BlobInfo.newBuilder(blobId).setStorageClass(StorageClass.STANDARD).build())
            .build();
    Blob updatedBlob1 = storage.copy(request).getResult();
    assertThat(updatedBlob1.getTimeStorageClassUpdated()).isNotNull();
    assertThat(updatedBlob1.getCreateTime()).isGreaterThan(remoteBlob.getCreateTime());
    assertThat(updatedBlob1.getUpdateTime()).isGreaterThan(remoteBlob.getCreateTime());
    assertThat(updatedBlob1.getTimeStorageClassUpdated())
        .isGreaterThan(remoteBlob.getTimeStorageClassUpdated());

    // Updates the other properties of the blob's to check the difference between blob updateTime
    // and timeStorageClassUpdated.
    Blob updatedBlob2 = updatedBlob1.toBuilder().setContentType(CONTENT_TYPE).build().update();
    assertThat(updatedBlob2.getUpdateTime())
        .isGreaterThan(updatedBlob2.getTimeStorageClassUpdated());
    assertThat(updatedBlob2.getTimeStorageClassUpdated())
        .isEqualTo(updatedBlob1.getTimeStorageClassUpdated());
    assertThat(updatedBlob2.delete()).isTrue();
  }

  @Test
  public void testUpdateBlob_noModification() {
    BlobInfo info = BlobInfo.newBuilder(bucket, generator.randomObjectName()).build();

    // in grpc, create will return acls but update does not. re-get the metadata with default fields
    Blob gen1 = storage.create(info);
    gen1 = storage.get(gen1.getBlobId());
    Blob gen2 = storage.update(gen1);
    assertThat(gen2).isEqualTo(gen1);
  }
}
