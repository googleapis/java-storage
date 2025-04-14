/*
 * Copyright 2024 Google LLC
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

import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.PROD, Backend.TEST_BENCH},
    transports = Transport.GRPC)
public final class ITAppendableUploadTest {

  @Inject public Generator generator;

  @Inject public Storage storage;

  @Inject
  @BucketFixture(BucketType.RAPID)
  public BucketInfo bucket;

  @Test
  public void testAppendableBlobUpload()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(2000));
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(), uploadConfig);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);

    upload.write(ByteBuffer.wrap(a1));
    upload.write(ByteBuffer.wrap(a2));
    BlobInfo blob = upload.finalizeUpload().get(5, TimeUnit.SECONDS);

    assertThat(blob.getSize()).isEqualTo(a1.length + a2.length);
  }

  @Test
  public void appendableBlobUploadWithoutFinalizing() throws IOException {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(256 * 1024));
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(), uploadConfig);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);

    upload.write(ByteBuffer.wrap(a1));
    upload.write(ByteBuffer.wrap(a2));

    upload.close();
  }

  @Test
  // Pending work in testbench, manually verified internally on 2025-03-25
  @CrossRun.Ignore(backends = {Backend.TEST_BENCH})
  public void appendableBlobUploadTakeover() throws Exception {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(5));
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(BlobInfo.newBuilder(bid).build(), uploadConfig);

    byte[] bytes = "ABCDEFGHIJ".getBytes();

    upload.write(ByteBuffer.wrap(bytes));
    upload.close();

    Blob blob = storage.get(bid);

    byte[] bytes2 = "KLMNOPQRST".getBytes();
    BlobAppendableUpload takeOver =
        storage.blobAppendableUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), uploadConfig);
    takeOver.write(ByteBuffer.wrap(bytes2));
    BlobInfo i = takeOver.finalizeUpload().get(5, TimeUnit.SECONDS);
    assertThat(i.getSize()).isEqualTo(20);
  }

  @Test
  public void testUploadFileUsingAppendable() throws Exception {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.minFlushSize(_2MiB));

    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());
    try (TmpFile tmpFile =
        DataGenerator.base64Characters()
            .tempFile(Paths.get(System.getProperty("java.io.tmpdir")), 100 * 1024 * 1024)) {
      try (BlobAppendableUpload appendable =
              storage.blobAppendableUpload(BlobInfo.newBuilder(bid).build(), uploadConfig);
          SeekableByteChannel r =
              Files.newByteChannel(tmpFile.getPath(), StandardOpenOption.READ)) {

        ByteStreams.copy(r, appendable);
        BlobInfo bi = appendable.finalizeUpload().get(5, TimeUnit.SECONDS);
        assertThat(bi.getSize()).isEqualTo(100 * 1024 * 1024);
      }
    }
  }

  @Test
  // Pending work in testbench, manually verified internally on 2025-03-25
  @CrossRun.Ignore(backends = {Backend.TEST_BENCH})
  public void finalizeAfterCloseWorks() throws Exception {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(1024));
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());

    BlobAppendableUpload appendable =
        storage.blobAppendableUpload(BlobInfo.newBuilder(bid).build(), uploadConfig);
    appendable.write(DataGenerator.base64Characters().genByteBuffer(3587));

    appendable.close();
    BlobInfo bi = appendable.finalizeUpload().get(5, TimeUnit.SECONDS);
    assertThat(bi.getSize()).isEqualTo(3587);
  }

  @Test
  // Pending work in testbench, manually verified internally on 2025-03-25
  @CrossRun.Ignore(backends = {Backend.TEST_BENCH})
  public void takeoverJustToFinalizeWorks() throws Exception {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(5));
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());

    BlobAppendableUpload upload =
        storage.blobAppendableUpload(BlobInfo.newBuilder(bid).build(), uploadConfig);

    upload.write(DataGenerator.base64Characters().genByteBuffer(20));
    upload.close();

    Blob blob =
        storage.get(
            bid, BlobGetOption.fields(BlobField.BUCKET, BlobField.NAME, BlobField.GENERATION));

    BlobAppendableUpload takeOver =
        storage.blobAppendableUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), uploadConfig);
    BlobInfo i = takeOver.finalizeUpload().get(5, TimeUnit.SECONDS);
    assertThat(i.getSize()).isEqualTo(20);
  }
}
