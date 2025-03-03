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
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
public final class ITAppendableUploadTest {

  @Inject public Generator generator;

  @Inject
  @StorageFixture(TransportCompatibility.Transport.GRPC)
  public Storage storage;

  @Inject public BucketInfo bucket;

  @Test
  public void testAppendableBlobUpload()
      throws IOException, ExecutionException, InterruptedException {
    AppendableBlobUpload upload =
        storage.appendableBlobUpload(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(), 2000);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);

    upload.write(ByteBuffer.wrap(a1));
    upload.write(ByteBuffer.wrap(a2));
    BlobInfo blob = upload.finalizeUpload();

    assertThat(blob.getSize()).isEqualTo(a1.length + a2.length);
  }

  @Test
  public void appendableBlobUploadWithoutFinalizing() throws IOException {
    AppendableBlobUpload upload =
        storage.appendableBlobUpload(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(), 256 * 1024);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);

    upload.write(ByteBuffer.wrap(a1));
    upload.write(ByteBuffer.wrap(a2));

    upload.close();
  }

  @Test
  @Ignore("Pending work in testbench, manually verified internally on 2025-03-03")
  public void appendableBlobUploadTakeover() throws Exception {
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());
    AppendableBlobUpload upload = storage.appendableBlobUpload(BlobInfo.newBuilder(bid).build(), 5);

    byte[] bytes = "ABCDEFGHIJ".getBytes();

    upload.write(ByteBuffer.wrap(bytes));
    upload.close();

    Blob blob = storage.get(bid);

    byte[] bytes2 = "KLMNOPQRST".getBytes();
    AppendableBlobUpload takeOver =
        storage.appendableBlobUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), 5);
    takeOver.write(ByteBuffer.wrap(bytes2));
    BlobInfo i = takeOver.finalizeUpload();
    assertThat(i.getSize()).isEqualTo(20);
  }

  @Test
  public void testUploadFileUsingAppendable() throws Exception {
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());
    try (TmpFile tmpFile =
        DataGenerator.base64Characters()
            .tempFile(Paths.get(System.getProperty("java.io.tmpdir")), 100 * 1024 * 1024)) {
      try (AppendableBlobUpload appendable =
              storage.appendableBlobUpload(BlobInfo.newBuilder(bid).build(), _2MiB);
          SeekableByteChannel r =
              Files.newByteChannel(tmpFile.getPath(), StandardOpenOption.READ)) {

        ByteStreams.copy(r, appendable);
        BlobInfo bi = appendable.finalizeUpload();
        assertThat(bi.getSize()).isEqualTo(100 * 1024 * 1024);
      }
    }
  }

  @Test
  public void finalizeAfterCloseWorks() throws Exception {
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());

    AppendableBlobUpload appendable =
        storage.appendableBlobUpload(BlobInfo.newBuilder(bid).build(), 1024);
    appendable.write(DataGenerator.base64Characters().genByteBuffer(3587));

    appendable.close();
    BlobInfo bi = appendable.finalizeUpload();
    assertThat(bi.getSize()).isEqualTo(3587);
  }

  @Test
  public void takeoverJustToFinalizeWorks() throws Exception {
    BlobId bid = BlobId.of(bucket.getName(), generator.randomObjectName());

    AppendableBlobUpload upload = storage.appendableBlobUpload(BlobInfo.newBuilder(bid).build(), 5);

    upload.write(DataGenerator.base64Characters().genByteBuffer(20));
    upload.close();

    Blob blob =
        storage.get(
            bid, BlobGetOption.fields(BlobField.BUCKET, BlobField.NAME, BlobField.GENERATION));

    AppendableBlobUpload takeOver =
        storage.appendableBlobUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), 5);
    BlobInfo i = takeOver.finalizeUpload();
    assertThat(i.getSize()).isEqualTo(20);
  }
}
