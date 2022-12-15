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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGeneration;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP, Transport.GRPC},
    backends = Backend.PROD)
public final class ITBlobReadChannelTest {

  private static final int _16MiB = 16 * 1024 * 1024;
  private static final int _256KiB = 256 * 1024;
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";
  private static final byte[] COMPRESSED_CONTENT =
      BaseEncoding.base64()
          .decode("H4sIAAAAAAAAAPNIzcnJV3DPz0/PSVVwzskvTVEILskvSkxPVQQA/LySchsAAAA=");

  @Rule public final TestName testName = new TestName();

  @Rule public final DataGeneration dataGeneration = new DataGeneration(new Random(872364872));

  @Rule public final TemporaryFolder tmp = new TemporaryFolder();

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;

  @Test
  public void testLimit_smallerThanOneChunk() throws IOException {
    int srcContentSize = _256KiB;
    int rangeBegin = 57;
    int rangeEnd = 2384;
    int chunkSize = _16MiB;
    doLimitTest(srcContentSize, rangeBegin, rangeEnd, chunkSize);
  }

  @Test
  public void testLimit_pastEndOfBlob() throws IOException {
    int srcContentSize = _256KiB;
    int rangeBegin = _256KiB - 20;
    int rangeEnd = _256KiB + 20;
    int chunkSize = _16MiB;
    doLimitTest(srcContentSize, rangeBegin, rangeEnd, chunkSize);
  }

  @Test
  public void testLimit_endBeforeBegin() throws IOException {
    int srcContentSize = _256KiB;
    int rangeBegin = 4;
    int rangeEnd = 3;
    int chunkSize = _16MiB;
    doLimitTest(srcContentSize, rangeBegin, rangeEnd, chunkSize);
  }

  @Test
  public void testLimit_largerThanOneChunk() throws IOException {
    int srcContentSize = _16MiB + (_256KiB * 3);
    int rangeBegin = 384;
    int rangeEnd = rangeBegin + _16MiB;
    int chunkSize = _16MiB;

    doLimitTest(srcContentSize, rangeBegin, rangeEnd, chunkSize);
  }

  @Test
  public void testLimit_downloadToFile() throws IOException {
    String blobName = String.format("%s/src", testName.getMethodName());
    BlobId blobId = BlobId.of(bucket.getName(), blobName);
    ByteBuffer content = dataGeneration.randByteBuffer(108);
    try (WriteChannel writer = storage.writer(BlobInfo.newBuilder(blobId).build())) {
      writer.write(content);
    }

    File file = tmp.newFile();
    String destFileName = file.getAbsolutePath();
    byte[] expectedBytes = new byte[37 - 14];
    ByteBuffer duplicate = content.duplicate();
    duplicate.position(14);
    duplicate.limit(37);
    duplicate.get(expectedBytes);

    try {
      try (ReadChannel from = storage.reader(blobId);
          FileChannel to = FileChannel.open(Paths.get(destFileName), StandardOpenOption.WRITE)) {
        from.seek(14);
        from.limit(37);

        ByteStreams.copy(from, to);
      }

      byte[] readBytes = Files.readAllBytes(Paths.get(destFileName));
      assertThat(readBytes).isEqualTo(expectedBytes);
    } finally {
      file.delete();
    }
  }

  @Test
  public void
      testReadChannel_preconditionFailureResultsInIOException_metagenerationMatch_specified() {
    String blobName = testName.getMethodName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    try (ReadChannel reader =
        storage.reader(blob.getBlobId(), Storage.BlobSourceOption.metagenerationMatch(-1L))) {
      reader.read(ByteBuffer.allocate(42));
      fail("IOException was expected");
    } catch (IOException ex) {
      // expected
    }
  }

  @Test
  public void testReadChannel_preconditionFailureResultsInIOException_generationMatch_specified() {
    String blobName = testName.getMethodName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    try (ReadChannel reader =
        storage.reader(blob.getBlobId(), Storage.BlobSourceOption.generationMatch(-1L))) {
      reader.read(ByteBuffer.allocate(42));
      fail("IOException was expected");
    } catch (IOException ex) {
      // expected
    }
  }

  @Test
  public void testReadChannel_preconditionFailureResultsInIOException_generationMatch_extractor() {
    String blobName = testName.getMethodName();
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobId blobIdWrongGeneration = BlobId.of(bucket.getName(), blobName, -1L);
    try (ReadChannel reader =
        storage.reader(blobIdWrongGeneration, Storage.BlobSourceOption.generationMatch())) {
      reader.read(ByteBuffer.allocate(42));
      fail("IOException was expected");
    } catch (IOException ex) {
      // expected
    }
  }

  @Test
  @CrossRun.Exclude(transports = Transport.GRPC)
  public void testReadChannelFailUpdatedGeneration() throws IOException {
    // this test scenario is valid for both grpc and json, however the current semantics of actual
    // request interleaving are very different, so this specific test is only applicable to json.
    String blobName = "test-read-blob-fail-updated-generation";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    Random random = new Random();
    int chunkSize = 1024;
    int blobSize = 2 * chunkSize;
    byte[] content = new byte[blobSize];
    random.nextBytes(content);
    Blob remoteBlob = storage.create(blob, content);
    assertNotNull(remoteBlob);
    assertEquals(blobSize, (long) remoteBlob.getSize());
    try (ReadChannel reader = storage.reader(blob.getBlobId())) {
      reader.setChunkSize(chunkSize);
      ByteBuffer readBytes = ByteBuffer.allocate(chunkSize);
      int numReadBytes = reader.read(readBytes);
      assertEquals(chunkSize, numReadBytes);
      assertArrayEquals(Arrays.copyOf(content, chunkSize), readBytes.array());
      try (WriteChannel writer = storage.writer(blob)) {
        byte[] newContent = new byte[blobSize];
        random.nextBytes(newContent);
        int numWrittenBytes = writer.write(ByteBuffer.wrap(newContent));
        assertEquals(blobSize, numWrittenBytes);
      }
      readBytes = ByteBuffer.allocate(chunkSize);
      reader.read(readBytes);
      fail("StorageException was expected");
    } catch (IOException ex) {
      StringBuilder messageBuilder = new StringBuilder();
      messageBuilder.append("Blob ").append(blob.getBlobId()).append(" was updated while reading");
      assertEquals(messageBuilder.toString(), ex.getMessage());
    }
  }

  @Test
  public void ensureReaderReturnsCompressedBytesByDefault() throws IOException {
    String blobName = testName.getMethodName();
    BlobInfo blobInfo =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType("text/plain")
            .setContentEncoding("gzip")
            .build();
    Blob blob = storage.create(blobInfo, COMPRESSED_CONTENT);
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      try (ReadChannel reader = storage.reader(BlobId.of(bucket.getName(), blobName))) {
        reader.setChunkSize(8);
        ByteStreams.copy(reader, Channels.newChannel(output));
      }
      assertArrayEquals(
          BLOB_STRING_CONTENT.getBytes(UTF_8),
          storage.readAllBytes(
              bucket.getName(), blobName, BlobSourceOption.shouldReturnRawInputStream(false)));
      assertArrayEquals(COMPRESSED_CONTENT, output.toByteArray());
      try (GZIPInputStream zipInput =
          new GZIPInputStream(new ByteArrayInputStream(output.toByteArray()))) {
        assertArrayEquals(BLOB_STRING_CONTENT.getBytes(UTF_8), ByteStreams.toByteArray(zipInput));
      }
    }
  }

  @Test
  public void ensureReaderCanAutoDecompressWhenReturnRawInputStream_false() throws IOException {
    String blobName = testName.getMethodName();
    BlobInfo blobInfo =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType("text/plain")
            .setContentEncoding("gzip")
            .build();
    Blob blob = storage.create(blobInfo, COMPRESSED_CONTENT);
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      try (ReadChannel reader =
          storage.reader(
              BlobId.of(bucket.getName(), blobName),
              BlobSourceOption.shouldReturnRawInputStream(false))) {
        reader.setChunkSize(8);
        ByteStreams.copy(reader, Channels.newChannel(output));
      }
      assertArrayEquals(BLOB_STRING_CONTENT.getBytes(UTF_8), output.toByteArray());
    }
  }

  @Test
  public void returnRawInputStream_true() throws IOException {
    String blobName = testName.getMethodName();
    BlobInfo blobInfo =
        BlobInfo.newBuilder(bucket, blobName)
            .setContentType("text/plain")
            .setContentEncoding("gzip")
            .build();
    Blob blob = storage.create(blobInfo, COMPRESSED_CONTENT);
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      try (ReadChannel reader =
          storage.reader(
              BlobId.of(bucket.getName(), blobName),
              BlobSourceOption.shouldReturnRawInputStream(true))) {
        reader.setChunkSize(8);
        ByteStreams.copy(reader, Channels.newChannel(output));
      }
      assertArrayEquals(
          BLOB_STRING_CONTENT.getBytes(UTF_8),
          storage.readAllBytes(
              bucket.getName(), blobName, BlobSourceOption.shouldReturnRawInputStream(false)));
      assertArrayEquals(COMPRESSED_CONTENT, output.toByteArray());
      try (GZIPInputStream zipInput =
          new GZIPInputStream(new ByteArrayInputStream(output.toByteArray()))) {
        assertArrayEquals(BLOB_STRING_CONTENT.getBytes(UTF_8), ByteStreams.toByteArray(zipInput));
      }
    }
  }

  private void doLimitTest(int srcContentSize, int rangeBegin, int rangeEnd, int chunkSize)
      throws IOException {
    String blobName = String.format("%s/src", testName.getMethodName());
    BlobInfo src = BlobInfo.newBuilder(bucket, blobName).build();
    ByteBuffer content = dataGeneration.randByteBuffer(srcContentSize);
    ByteBuffer dup = content.duplicate();
    dup.position(rangeBegin);
    dup.limit(Math.min(dup.capacity(), rangeEnd));
    byte[] expectedSubContent = new byte[dup.remaining()];
    dup.get(expectedSubContent);

    try (WriteChannel writer = storage.writer(src)) {
      writer.write(content);
    }

    ByteBuffer buffer = ByteBuffer.allocate(srcContentSize);

    try (ReadChannel reader = storage.reader(src.getBlobId())) {
      reader.setChunkSize(chunkSize);
      reader.seek(rangeBegin);
      reader.limit(rangeEnd);
      reader.read(buffer);
      buffer.flip();
    }

    byte[] actual = new byte[buffer.limit()];
    buffer.get(actual);

    assertThat(actual).isEqualTo(expectedSubContent);
  }
}
