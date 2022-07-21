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

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.DataGeneration;
import com.google.cloud.storage.StorageFixture;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

public final class ITBlobReadChannelTest {

  private static final int _16MiB = 16 * 1024 * 1024;
  private static final int _256KiB = 256 * 1024;

  @Rule public final TestName testName = new TestName();

  @Rule public final DataGeneration dataGeneration = new DataGeneration(new Random(872364872));

  @Rule public final TemporaryFolder tmp = new TemporaryFolder();

  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private String bucketName;
  private String blobName;

  @Before
  public void setUp() throws Exception {
    bucketName = bucketFixture.getBucketInfo().getName();

    blobName = String.format("%s/src", testName.getMethodName());
  }

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
    BlobId blobId = BlobId.of(bucketName, blobName);
    ByteBuffer content = dataGeneration.randByteBuffer(108);
    try (WriteChannel writer =
        storageFixture.getInstance().writer(BlobInfo.newBuilder(blobId).build())) {
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
      try (ReadChannel from = storageFixture.getInstance().reader(blobId);
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

  private void doLimitTest(int srcContentSize, int rangeBegin, int rangeEnd, int chunkSize)
      throws IOException {
    BlobInfo src = BlobInfo.newBuilder(bucketName, blobName).build();
    ByteBuffer content = dataGeneration.randByteBuffer(srcContentSize);
    ByteBuffer dup = content.duplicate();
    dup.position(rangeBegin);
    dup.limit(Math.min(dup.capacity(), rangeEnd));
    byte[] expectedSubContent = new byte[dup.remaining()];
    dup.get(expectedSubContent);

    try (WriteChannel writer = storageFixture.getInstance().writer(src)) {
      writer.write(content);
    }

    ByteBuffer buffer = ByteBuffer.allocate(srcContentSize);

    try (ReadChannel reader = storageFixture.getInstance().reader(src.getBlobId())) {
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
