/*
 * Copyright 2025 Google LLC
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
import static org.junit.Assert.assertNotNull;

import com.example.storage.object.AppendableObjectMultipleRangedRead;
import com.example.storage.object.AppendableObjectReadFullObject;
import com.example.storage.object.AppendableObjectSingleRangedRead;
import com.example.storage.object.FinalizeAppendableObjectUpload;
import com.example.storage.object.ResumeAppendableObjectUpload;
import com.example.storage.object.StartAppendableObjectUpload;
import com.google.api.core.ApiFuture;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobAppendableUpload;
import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadConfig;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobReadSession;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.ReadProjectionConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.cloud.storage.it.runner.registry.Generator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.GRPC},
    backends = {Backend.PROD})
public class ITAppendableObjectSnippets {

  @Inject public Generator generator;

  @Inject
  @StorageFixture(Transport.GRPC)
  public Storage storage;

  @Inject
  @BucketFixture(BucketType.RAPID)
  public BucketInfo bucket;

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    outContent.reset();
  }

  @Test
  public void testAppendableObjectSingleRangedRead() throws Exception {
    String objectName = generator.randomObjectName();
    String bucketName = bucket.getName();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    byte[] content = DataGenerator.base64Characters().genBytes(2048);
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(blobInfo, BlobAppendableUploadConfig.of());
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(content));
    }
    blobInfo = upload.getResult().get();
    Blob blob = storage.get(blobId);
    assertNotNull("Blob should be created", blob);

    int offset = 512;
    int length = 1024;

    AppendableObjectSingleRangedRead.appendableObjectSingleRangedRead(
        bucketName, objectName, offset, length);

    String output = outContent.toString(StandardCharsets.UTF_8.name());
    assertThat(output).contains(String.format("Read %d bytes", length));

    blob.delete();
  }

  @Test
  public void testAppendableObjectMultipleRangedRead() throws Exception {
    String objectName = generator.randomObjectName();
    String bucketName = bucket.getName();

    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    byte[] content = DataGenerator.base64Characters().genBytes(4096);
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(blobInfo, BlobAppendableUploadConfig.of());
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(content));
    }
    blobInfo = upload.getResult().get();
    Blob blob = storage.get(blobId);
    assertNotNull("Blob should be created", blob);

    int offset1 = 512;
    int length1 = 1024;
    int offset2 = 2048;
    int length2 = 512;

    AppendableObjectMultipleRangedRead.appendableObjectMultipleRangedRead(
        bucketName, objectName, offset1, length1, offset2, length2);

    String output = outContent.toString(StandardCharsets.UTF_8.name());
    assertThat(output).contains(String.format("Read %d bytes from range", length1));
    assertThat(output).contains(String.format("and %d bytes from range", length2));

    blob.delete();
  }

  @Test
  public void testAppendableObjectReadFullObject() throws Exception {
    String objectName = generator.randomObjectName();
    String bucketName = bucket.getName();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    String stringContent = "line 1\nline 2\nline 3\n";
    byte[] content = stringContent.getBytes(StandardCharsets.UTF_8);

    BlobAppendableUpload upload =
        storage.blobAppendableUpload(blobInfo, BlobAppendableUploadConfig.of());
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(content));
    }
    blobInfo = upload.getResult().get();
    Blob blob = storage.get(blobId);
    assertNotNull("Blob should be created", blob);

    AppendableObjectReadFullObject.appendableObjectReadFullObject(bucketName, objectName);

    long expectedNewlineCount = 3;
    String output = outContent.toString(StandardCharsets.UTF_8.name());
    assertThat(output).contains(String.format("Found %d newline characters", expectedNewlineCount));

    blob.delete();
  }

  @Test
  public void testStartAppendableObjectUpload() throws Exception {
    String objectName = generator.randomObjectName();
    byte[] content = DataGenerator.base64Characters().genBytes(1024);
    Path tempFile = createTempFile(content);
    String localFilePath = tempFile.toString();
    String bucketName = bucket.getName();

    StartAppendableObjectUpload.startAppendableObjectUpload(bucketName, objectName, localFilePath);

    Blob blob = storage.get(bucketName, objectName);
    assertNotNull("Blob should be created", blob);
    assertThat(blob.getSize()).isEqualTo(content.length);

    ApiFuture<BlobReadSession> blobReadSessionFuture = storage.blobReadSession(blob.getBlobId());
    try (BlobReadSession read = blobReadSessionFuture.get()) {
      ApiFuture<byte[]> futureBytes = read.readAs(ReadProjectionConfigs.asFutureBytes());
      assertThat(futureBytes.get()).isEqualTo(content);
    }
    blob.delete();
  }

  @Test
  public void testResumeAppendableObjectUpload() throws Exception {
    String objectName = generator.randomObjectName();
    String bucketName = bucket.getName();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    byte[] initialContent = DataGenerator.base64Characters().genBytes(512);
    byte[] appendContent = DataGenerator.base64Characters().genBytes(512);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    os.write(initialContent);
    os.write(appendContent);
    byte[] fullContent = os.toByteArray();
    Path tempFile = createTempFile(fullContent);
    String localFilePath = tempFile.toString();

    BlobAppendableUploadConfig config =
        BlobAppendableUploadConfig.of().withCloseAction(CloseAction.CLOSE_WITHOUT_FINALIZING);
    BlobAppendableUpload upload = storage.blobAppendableUpload(blobInfo, config);
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(initialContent));
    }
    upload.getResult().get();

    ResumeAppendableObjectUpload.resumeAppendableObjectUpload(
        bucketName, objectName, localFilePath);

    Blob blob = storage.get(bucketName, objectName);
    assertNotNull("Blob should exist", blob);
    assertThat(blob.getSize()).isEqualTo(fullContent.length);

    ApiFuture<BlobReadSession> blobReadSessionFuture = storage.blobReadSession(blob.getBlobId());
    try (BlobReadSession read = blobReadSessionFuture.get()) {
      ApiFuture<byte[]> futureBytes = read.readAs(ReadProjectionConfigs.asFutureBytes());
      assertThat(futureBytes.get()).isEqualTo(fullContent);
    }
    blob.delete();
  }

  @Test
  public void testFinalizeAppendableObjectUpload() throws Exception {
    String objectName = generator.randomObjectName();
    String bucketName = bucket.getName();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    byte[] initialContent = DataGenerator.base64Characters().genBytes(512);
    BlobAppendableUploadConfig config =
        BlobAppendableUploadConfig.of().withCloseAction(CloseAction.CLOSE_WITHOUT_FINALIZING);
    BlobAppendableUpload upload = storage.blobAppendableUpload(blobInfo, config);
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(initialContent));
    }
    upload.getResult().get();

    FinalizeAppendableObjectUpload.finalizeAppendableObjectUpload(bucketName, objectName);

    Blob blob = storage.get(bucketName, objectName);
    assertNotNull("Blob should exist after finalization", blob);
    assertThat(blob.getSize()).isEqualTo(initialContent.length);

    ApiFuture<BlobReadSession> blobReadSessionFuture = storage.blobReadSession(blob.getBlobId());
    try (BlobReadSession read = blobReadSessionFuture.get()) {
      ApiFuture<byte[]> futureBytes = read.readAs(ReadProjectionConfigs.asFutureBytes());
      assertThat(futureBytes.get()).isEqualTo(initialContent);
    }
    blob.delete();
  }

  private Path createTempFile(byte[] content) throws IOException {
    Path tempFile = tmpDir.newFile(UUID.randomUUID().toString()).toPath();
    Files.write(tempFile, content);
    return tempFile;
  }
}
