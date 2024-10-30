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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.storage.object.BatchSetObjectMetadata;
import com.example.storage.object.ChangeObjectCsekToKms;
import com.example.storage.object.ChangeObjectStorageClass;
import com.example.storage.object.ComposeObject;
import com.example.storage.object.CopyObject;
import com.example.storage.object.CopyOldVersionOfObject;
import com.example.storage.object.DeleteObject;
import com.example.storage.object.DeleteOldVersionOfObject;
import com.example.storage.object.DownloadEncryptedObject;
import com.example.storage.object.DownloadObject;
import com.example.storage.object.DownloadObjectIntoMemory;
import com.example.storage.object.DownloadPublicObject;
import com.example.storage.object.GenerateEncryptionKey;
import com.example.storage.object.GenerateV4GetObjectSignedUrl;
import com.example.storage.object.GenerateV4PutObjectSignedUrl;
import com.example.storage.object.GetObjectMetadata;
import com.example.storage.object.ListObjects;
import com.example.storage.object.ListObjectsWithOldVersions;
import com.example.storage.object.ListObjectsWithPrefix;
import com.example.storage.object.MakeObjectPublic;
import com.example.storage.object.MoveObject;
import com.example.storage.object.RotateObjectEncryptionKey;
import com.example.storage.object.SetObjectMetadata;
import com.example.storage.object.SetObjectRetentionPolicy;
import com.example.storage.object.StreamObjectDownload;
import com.example.storage.object.StreamObjectUpload;
import com.example.storage.object.UploadEncryptedObject;
import com.example.storage.object.UploadKmsEncryptedObject;
import com.example.storage.object.UploadObject;
import com.example.storage.object.UploadObjectFromMemory;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.it.BucketCleaner;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import com.google.cloud.testing.junit4.StdOutCaptureRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ITObjectSnippets {

  private static final Logger log = Logger.getLogger(ITObjectSnippets.class.getName());
  private static final String BUCKET = RemoteStorageHelper.generateBucketName();
  private static final String BLOB = "blob";
  private static final String STRING_CONTENT = "Hello, World!";
  private static final byte[] CONTENT = STRING_CONTENT.getBytes(UTF_8);
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String KMS_KEY_NAME =
      "projects/cloud-java-ci-sample/locations/us/keyRings/"
          + "gcs_test_kms_key_ring/cryptoKeys/gcs_kms_key_one";

  private static Storage storage;

  @Rule public final StdOutCaptureRule stdOutCaptureRule = new StdOutCaptureRule();

  @Rule public MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(5);

  @Rule public ExpectedException thrown = ExpectedException.none();

  @BeforeClass
  public static void beforeClass() {
    RemoteStorageHelper helper = RemoteStorageHelper.create();
    storage = helper.getOptions().getService();
    storage.create(BucketInfo.of(BUCKET));
    storage.create(BlobInfo.newBuilder(BUCKET, BLOB).build(), CONTENT);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    try (Storage ignore = storage) {
      BucketCleaner.doCleanup(BUCKET, storage);
    }
  }

  @Test
  public void testChangeObjectStorageClass() {
    Blob blob = storage.get(BUCKET, BLOB);
    Assert.assertNotEquals(StorageClass.COLDLINE, blob.getStorageClass());
    ChangeObjectStorageClass.changeObjectStorageClass(PROJECT_ID, BUCKET, BLOB);
    assertEquals(StorageClass.COLDLINE, storage.get(BUCKET, BLOB).getStorageClass());
    assertArrayEquals(CONTENT, storage.get(BUCKET, BLOB).getContent());
  }

  @Test
  public void testCopyObject() {
    String newBucket = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.newBuilder(newBucket).build());
    try {
      CopyObject.copyObject(PROJECT_ID, BUCKET, BLOB, newBucket);
      assertNotNull(storage.get(newBucket, BLOB));
    } finally {
      storage.delete(newBucket, BLOB);
      storage.delete(newBucket);
    }
  }

  @Test
  public void testDeleteObject() {
    String blob = "deletethisblob";
    storage.create(BlobInfo.newBuilder(BlobId.of(BUCKET, blob)).build());
    assertNotNull(storage.get(BUCKET, blob));
    DeleteObject.deleteObject(PROJECT_ID, BUCKET, blob);
    assertNull(storage.get(BUCKET, blob));
  }

  @Test
  public void testDownloadObject() throws IOException {
    File tempFile = File.createTempFile("file", ".txt");
    try {
      DownloadObject.downloadObject(PROJECT_ID, BUCKET, BLOB, tempFile.getPath());
      assertEquals("Hello, World!", new String(Files.readAllBytes(tempFile.toPath())));
    } finally {
      tempFile.delete();
    }
  }

  @Test
  public void testDownloadObjectIntoMemory() throws IOException {
    DownloadObjectIntoMemory.downloadObjectIntoMemory(PROJECT_ID, BUCKET, BLOB);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
  }

  @Test
  public void testDownloadPublicObject() throws IOException {
    String publicBlob = "publicblob" + System.currentTimeMillis();
    BlobId publicBlobId = BlobId.of(BUCKET, publicBlob);
    storage.create(BlobInfo.newBuilder(publicBlobId).build(), CONTENT);
    storage.createAcl(publicBlobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    File tempFile = File.createTempFile("file", ".txt");
    try {
      DownloadPublicObject.downloadPublicObject(BUCKET, publicBlob, tempFile.toPath());
      assertEquals("Hello, World!", new String(Files.readAllBytes(tempFile.toPath())));
    } finally {
      tempFile.delete();
    }
  }

  @Test
  public void testGetObjectMetadata() {
    String blobName = "test-create-empty-blob";
    BlobId blobId = BlobId.of(BUCKET, blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(ImmutableMap.of("k", "v")).build();
    Blob remoteBlob = storage.create(blobInfo, CONTENT);
    assertNotNull(remoteBlob);
    GetObjectMetadata.getObjectMetadata(PROJECT_ID, BUCKET, blobName);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("Bucket: " + remoteBlob.getBucket()));
    assertTrue(snippetOutput.contains("Bucket: " + remoteBlob.getBucket()));
    assertTrue(snippetOutput.contains("CacheControl: " + remoteBlob.getCacheControl()));
    assertTrue(snippetOutput.contains("ComponentCount: " + remoteBlob.getComponentCount()));
    assertTrue(snippetOutput.contains("ContentDisposition: " + remoteBlob.getContentDisposition()));
    assertTrue(snippetOutput.contains("ContentEncoding: " + remoteBlob.getContentEncoding()));
    assertTrue(snippetOutput.contains("ContentLanguage: " + remoteBlob.getContentLanguage()));
    assertTrue(snippetOutput.contains("ContentType: " + remoteBlob.getContentType()));
    assertTrue(snippetOutput.contains("CustomTime: " + remoteBlob.getCustomTime()));
    assertTrue(snippetOutput.contains("Crc32c: " + remoteBlob.getCrc32c()));
    assertTrue(snippetOutput.contains("Crc32cHexString: " + remoteBlob.getCrc32cToHexString()));
    assertTrue(snippetOutput.contains("ETag: " + remoteBlob.getEtag()));
    assertTrue(snippetOutput.contains("Generation: " + remoteBlob.getGeneration()));
    assertTrue(snippetOutput.contains("Id: " + remoteBlob.getBlobId()));
    assertTrue(snippetOutput.contains("KmsKeyName: " + remoteBlob.getKmsKeyName()));
    assertTrue(snippetOutput.contains("Md5Hash: " + remoteBlob.getMd5()));
    assertTrue(snippetOutput.contains("Md5HexString: " + remoteBlob.getMd5ToHexString()));
    assertTrue(snippetOutput.contains("MediaLink: " + remoteBlob.getMediaLink()));
    assertTrue(snippetOutput.contains("Metageneration: " + remoteBlob.getMetageneration()));
    assertTrue(snippetOutput.contains("Name: " + remoteBlob.getName()));
    assertTrue(snippetOutput.contains("Size: " + remoteBlob.getSize()));
    assertTrue(snippetOutput.contains("StorageClass: " + remoteBlob.getStorageClass()));
    assertTrue(snippetOutput.contains("TimeCreated: " + new Date(remoteBlob.getCreateTime())));
    assertTrue(
        snippetOutput.contains("Last Metadata Update: " + new Date(remoteBlob.getUpdateTime())));
    assertTrue(snippetOutput.contains("temporaryHold: disabled"));
    assertTrue(snippetOutput.contains("eventBasedHold: disabled"));
    assertTrue(snippetOutput.contains("User metadata:"));
    assertTrue(snippetOutput.contains("k=v"));
    assertTrue(snippetOutput.contains("Object Retention Policy: " + remoteBlob.getRetention()));
  }

  @Test
  public void testListObjects() {
    ListObjects.listObjects(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains(BLOB));
  }

  @Test
  public void testListObjectsWithPrefix() {
    storage.create(BlobInfo.newBuilder(BlobId.of(BUCKET, "a/1.txt")).build());
    storage.create(BlobInfo.newBuilder(BlobId.of(BUCKET, "a/b/2.txt")).build());
    storage.create(BlobInfo.newBuilder(BlobId.of(BUCKET, "a/b/3.txt")).build());
    ListObjectsWithPrefix.listObjectsWithPrefix(PROJECT_ID, BUCKET, "a/");
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertTrue(snippetOutput.contains("a/1.txt"));
    assertTrue(snippetOutput.contains("a/b/"));
    assertFalse(snippetOutput.contains("a/b/2.txt"));
  }

  @Test
  public void testMoveObject() {
    String blob = "movethisblob";
    String newBlob = "movedthisblob";

    storage.create(BlobInfo.newBuilder(BlobId.of(BUCKET, blob)).build());
    assertNotNull(storage.get(BUCKET, blob));
    String newBucket = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.newBuilder(newBucket).build());
    try {
      MoveObject.moveObject(PROJECT_ID, BUCKET, blob, newBucket, newBlob);
      assertNotNull(storage.get(newBucket, newBlob));
      assertNull(storage.get(BUCKET, blob));
    } finally {
      storage.delete(newBucket, newBlob);
      storage.delete(newBucket);
    }
  }

  @Test
  public void testSetObjectMetadata() {
    SetObjectMetadata.setObjectMetadata(PROJECT_ID, BUCKET, BLOB);
    Map<String, String> metadata = storage.get(BUCKET, BLOB).getMetadata();
    assertEquals("value", metadata.get("keyToAddOrUpdate"));
  }

  @Test
  public void testUploadObject() throws IOException {
    File tempFile = File.createTempFile("file", ".txt");
    try {
      Files.write(tempFile.toPath(), CONTENT);
      UploadObject.uploadObject(PROJECT_ID, BUCKET, "uploadobjecttest", tempFile.getPath());
      assertArrayEquals(CONTENT, storage.get(BUCKET, "uploadobjecttest").getContent());
    } finally {
      tempFile.delete();
    }
  }

  @Test
  public void testUploadObjectFromMemory() throws IOException {
    UploadObjectFromMemory.uploadObjectFromMemory(
        PROJECT_ID, BUCKET, "uploadobjectfrommemorytest", STRING_CONTENT);
    final byte[] output = storage.get(BUCKET, "uploadobjectfrommemorytest").getContent();
    assertEquals(STRING_CONTENT, new String(output, UTF_8));
  }

  @Test
  public void testObjectCSEKOperations() throws IOException {
    GenerateEncryptionKey.generateEncryptionKey();
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    String encryptionKey = snippetOutput.split(": ")[1].trim();

    File tempFile = File.createTempFile("file", ".txt");
    File downloadFile = File.createTempFile("dlfile", ".txt");
    String encryptedBlob = "uploadencryptedobjecttest";
    Files.write(tempFile.toPath(), CONTENT);

    UploadEncryptedObject.uploadEncryptedObject(
        PROJECT_ID, BUCKET, encryptedBlob, tempFile.getPath(), encryptionKey);
    DownloadEncryptedObject.downloadEncryptedObject(
        PROJECT_ID, BUCKET, encryptedBlob, downloadFile.toPath(), encryptionKey);
    assertArrayEquals(CONTENT, Files.readAllBytes(downloadFile.toPath()));

    byte[] key = new byte[32];
    new Random().nextBytes(key);
    String newEncryptionKey = BaseEncoding.base64().encode(key);
    RotateObjectEncryptionKey.rotateObjectEncryptionKey(
        PROJECT_ID, BUCKET, encryptedBlob, encryptionKey, newEncryptionKey);
    File newDownloadFile = File.createTempFile("newdownloadfile", ".txt");
    DownloadEncryptedObject.downloadEncryptedObject(
        PROJECT_ID, BUCKET, encryptedBlob, newDownloadFile.toPath(), newEncryptionKey);
    assertArrayEquals(CONTENT, Files.readAllBytes(newDownloadFile.toPath()));

    assertNull(storage.get(BUCKET, encryptedBlob).getKmsKeyName());
    ChangeObjectCsekToKms.changeObjectFromCsekToKms(
        PROJECT_ID, BUCKET, encryptedBlob, newEncryptionKey, KMS_KEY_NAME);
    assertTrue(storage.get(BUCKET, encryptedBlob).getKmsKeyName().contains(KMS_KEY_NAME));
  }

  @Test
  public void testObjectVersioningOperations() {
    storage.get(BUCKET).toBuilder().setVersioningEnabled(true).build().update();
    String versionedBlob = "versionedblob";
    final Blob originalBlob =
        storage.create(BlobInfo.newBuilder(BUCKET, versionedBlob).build(), CONTENT);
    byte[] content2 = "Hello, World 2".getBytes(UTF_8);
    storage.create(BlobInfo.newBuilder(BUCKET, versionedBlob).build(), content2);

    ListObjectsWithOldVersions.listObjectsWithOldVersions(PROJECT_ID, BUCKET);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();

    snippetOutput = snippetOutput.replaceFirst(versionedBlob, "");
    assertTrue(snippetOutput.contains(versionedBlob));

    CopyOldVersionOfObject.copyOldVersionOfObject(
        PROJECT_ID, BUCKET, versionedBlob, originalBlob.getGeneration(), "copiedblob");
    assertArrayEquals(CONTENT, storage.get(BUCKET, "copiedblob").getContent());

    DeleteOldVersionOfObject.deleteOldVersionOfObject(
        PROJECT_ID, BUCKET, versionedBlob, originalBlob.getGeneration());
    assertNull(storage.get(BlobId.of(BUCKET, versionedBlob, originalBlob.getGeneration())));
    assertNotNull(storage.get(BUCKET, versionedBlob));
  }

  @Test
  public void testV4SignedURLs() throws IOException {
    String tempObject = "test-upload-signed-url-object";
    GenerateV4PutObjectSignedUrl.generateV4PutObjectSignedUrl(PROJECT_ID, BUCKET, tempObject);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    String url = snippetOutput.split("\n")[1];
    URL uploadUrl = new URL(url);
    HttpsURLConnection connection = (HttpsURLConnection) uploadUrl.openConnection();
    connection.setRequestMethod("PUT");
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/octet-stream");
    try (OutputStream out = connection.getOutputStream()) {
      out.write(CONTENT);
      assertEquals(connection.getResponseCode(), 200);
    }
    GenerateV4GetObjectSignedUrl.generateV4GetObjectSignedUrl(PROJECT_ID, BUCKET, tempObject);
    snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    url = snippetOutput.split("\n")[5];
    URL downloadUrl = new URL(url);
    connection = (HttpsURLConnection) downloadUrl.openConnection();
    byte[] readBytes = new byte[CONTENT.length];
    try (InputStream responseStream = connection.getInputStream()) {
      assertEquals(CONTENT.length, responseStream.read(readBytes));
      assertArrayEquals(CONTENT, readBytes);
    }
  }

  @Test
  public void testMakeObjectPublic() {
    String aclBlob = "acl-test-blob";
    assertNull(
        storage.create(BlobInfo.newBuilder(BUCKET, aclBlob).build()).getAcl(Acl.User.ofAllUsers()));
    MakeObjectPublic.makeObjectPublic(PROJECT_ID, BUCKET, aclBlob);
    assertNotNull(storage.get(BUCKET, aclBlob).getAcl(Acl.User.ofAllUsers()));
  }

  @Test
  public void testComposeObject() {
    String firstObject = "firstObject";
    String secondObject = "secondObject";
    String targetObject = "targetObject";
    storage.create(BlobInfo.newBuilder(BUCKET, firstObject).build(), firstObject.getBytes(UTF_8));
    storage.create(BlobInfo.newBuilder(BUCKET, secondObject).build(), secondObject.getBytes(UTF_8));

    ComposeObject.composeObject(BUCKET, firstObject, secondObject, targetObject, PROJECT_ID);

    assertArrayEquals(
        "firstObjectsecondObject".getBytes(UTF_8), storage.get(BUCKET, targetObject).getContent());
  }

  @Test
  public void testStreamUploadDownload() throws Exception {
    StreamObjectUpload.streamObjectUpload(PROJECT_ID, BUCKET, "streamBlob", "hello world");
    File file = File.createTempFile("stream", "test");
    StreamObjectDownload.streamObjectDownload(
        PROJECT_ID, BUCKET, "streamBlob", file.getAbsolutePath());
    assertArrayEquals(Files.readAllBytes(file.toPath()), "hello world".getBytes());
    file.delete();
  }

  @Test
  public void testUploadKMSEncryptedObject() {
    String blobName = "kms-encrypted-blob";
    UploadKmsEncryptedObject.uploadKmsEncryptedObject(PROJECT_ID, BUCKET, blobName, KMS_KEY_NAME);
    assertNotNull(storage.get(BUCKET, blobName));
  }

  @Test
  public void testBatchSetObjectMetadata() {
    storage.create(BlobInfo.newBuilder(BUCKET, "b/1.txt").build());
    storage.create(BlobInfo.newBuilder(BUCKET, "b/2.txt").build());

    BatchSetObjectMetadata.batchSetObjectMetadata(PROJECT_ID, BUCKET, "b/");

    Map<String, String> firstBlobMetadata = storage.get(BUCKET, "b/1.txt").getMetadata();
    Map<String, String> secondBlobMetadata = storage.get(BUCKET, "b/2.txt").getMetadata();

    assertEquals("value", firstBlobMetadata.get("keyToAddOrUpdate"));
    assertEquals("value", secondBlobMetadata.get("keyToAddOrUpdate"));
  }

  @Test
  public void testSetObjectRetentionPolicy() {
    String tempBucket = RemoteStorageHelper.generateBucketName();
    storage.create(
        BucketInfo.of(tempBucket), Storage.BucketTargetOption.enableObjectRetention(true));
    String retentionBlob = "retentionblob";
    storage.create(BlobInfo.newBuilder(tempBucket, retentionBlob).build());
    assertNull(storage.get(tempBucket, retentionBlob).getRetention());
    try {
      SetObjectRetentionPolicy.setObjectRetentionPolicy(PROJECT_ID, tempBucket, retentionBlob);
      assertNotNull(storage.get(tempBucket, retentionBlob).getRetention());
    } finally {

      storage
          .get(tempBucket, retentionBlob)
          .toBuilder()
          .setRetention(null)
          .build()
          .update(Storage.BlobTargetOption.overrideUnlockedRetention(true));
      storage.delete(tempBucket, retentionBlob);
      storage.delete(tempBucket);
    }
  }
}
