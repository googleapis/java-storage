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
import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
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
    backends = {Backend.TEST_BENCH},
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
        BlobAppendableUploadConfig.of()
            .withFlushPolicy(FlushPolicy.maxFlushSize(2000))
            .withCloseAction(CloseAction.FINALIZE_WHEN_CLOSING);
    BlobAppendableUpload upload =
        storage.blobAppendableUpload(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(), uploadConfig);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);
    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(a1));
      channel.write(ByteBuffer.wrap(a2));
    }
    BlobInfo blob = upload.getResult().get(5, TimeUnit.SECONDS);

    assertThat(blob.getSize()).isEqualTo(a1.length + a2.length);

    BlobInfo actual = upload.getResult().get(5, TimeUnit.SECONDS);
    BlobInfo blob1 = storage.get(actual.getBlobId());
    assertThat(actual).isEqualTo(blob1);
  }

  @Test
  public void appendableBlobUploadWithoutFinalizing() throws Exception {
    BlobAppendableUploadConfig uploadConfig =
        BlobAppendableUploadConfig.of().withFlushPolicy(FlushPolicy.maxFlushSize(256 * 1024));
    BlobInfo info = BlobInfo.newBuilder(bucket, generator.randomObjectName()).build();
    BlobAppendableUpload upload = storage.blobAppendableUpload(info, uploadConfig);

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    byte[] a1 = Arrays.copyOfRange(bytes, 0, bytes.length / 2);
    byte[] a2 = Arrays.copyOfRange(bytes, bytes.length / 2 + 1, bytes.length);

    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(a1));
      channel.write(ByteBuffer.wrap(a2));
    }
    BlobInfo actual = upload.getResult().get(5, TimeUnit.SECONDS);
    assertAll(
        () -> assertThat(actual).isNotNull(),
        () -> assertThat(actual.getSize()).isEqualTo(512 * 1024 - 1),
        () -> {
          // TODO: re-enable this when crc32c behavior is better defined when multiple flushes
          //   and state lookups happen for incomplete uploads.
          if (false) {
            String crc32c = actual.getCrc32c();
            // prod is null
            boolean crc32cNull = crc32c == null;
            // testbench v0.54.0+ will have the crc32c of the first flush, regardless if more has
            // been flushed since then.
            // While the following assertion can pass for v0.54.0 and v0.55.0 it's janky, and not
            // something I want to depend upon. So, for now it's skipped, with this comment and
            // code left as a skeleton of what should be filled in.
            Crc32cLengthKnown a1hash = Hasher.enabled().hash(ByteBuffer.wrap(a1));
            boolean crc32cZero =
                Utils.crc32cCodec.encode(a1hash.getValue()).equalsIgnoreCase(crc32c);
            assertThat(crc32cNull || crc32cZero).isTrue();
          }
        });
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

    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(ByteBuffer.wrap(bytes));
    }
    BlobInfo blob = upload.getResult().get(5, TimeUnit.SECONDS);

    byte[] bytes2 = "KLMNOPQRST".getBytes();
    BlobAppendableUpload takeOver =
        storage.blobAppendableUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), uploadConfig);
    try (AppendableUploadWriteableByteChannel channel = takeOver.open()) {
      channel.write(ByteBuffer.wrap(bytes2));
    }
    BlobInfo i = takeOver.getResult().get(5, TimeUnit.SECONDS);
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

      BlobAppendableUpload appendable =
          storage.blobAppendableUpload(BlobInfo.newBuilder(bid).build(), uploadConfig);
      try (AppendableUploadWriteableByteChannel channel = appendable.open();
          SeekableByteChannel r =
              Files.newByteChannel(tmpFile.getPath(), StandardOpenOption.READ)) {
        ByteStreams.copy(r, channel);
      }
      BlobInfo bi = appendable.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(100 * 1024 * 1024);
    }
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

    try (AppendableUploadWriteableByteChannel channel = upload.open()) {
      channel.write(DataGenerator.base64Characters().genByteBuffer(20));
    }

    BlobInfo blob = upload.getResult().get(5, TimeUnit.SECONDS);

    BlobAppendableUpload takeOver =
        storage.blobAppendableUpload(BlobInfo.newBuilder(blob.getBlobId()).build(), uploadConfig);
    takeOver.open().finalizeAndClose();
    BlobInfo i = takeOver.getResult().get(5, TimeUnit.SECONDS);
    assertThat(i.getSize()).isEqualTo(20);

    BlobInfo actual = takeOver.getResult().get(5, TimeUnit.SECONDS);
    assertAll(
        () -> assertThat(actual).isNotNull(),
        () -> assertThat(actual.getSize()).isEqualTo(20),
        () -> assertThat(actual.getCrc32c()).isNotNull());
  }
}
