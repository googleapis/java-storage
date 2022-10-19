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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageFixture;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class ITObjectChecksumSupportTest {

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureHttp = StorageFixture.defaultHttp();

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureGrpc = StorageFixture.defaultGrpc();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixtureHttp =
      BucketFixture.newBuilder()
          .setBucketNameFmtString("java-storage-http-%s")
          .setHandle(storageFixtureHttp::getInstance)
          .build();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixtureGrpc =
      BucketFixture.newBuilder()
          .setBucketNameFmtString("java-storage-grpc-%s")
          .setHandle(storageFixtureHttp::getInstance)
          .build();

  @Rule public final TestName testName = new TestName();

  private final String clientName;
  private final Storage storage;
  private final BucketFixture bucketFixture;
  private final ChecksummedTestContent content;

  public ITObjectChecksumSupportTest(
      String clientName,
      StorageFixture storageFixture,
      BucketFixture bucketFixture,
      ChecksummedTestContent content) {
    this.clientName = clientName;
    this.storage = storageFixture.getInstance();
    this.bucketFixture = bucketFixture;
    this.content = content;
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> parameters() {
    ImmutableList<Object[]> clientsAndBuckets =
        ImmutableList.of(
            new Object[] {"JSON/Prod", storageFixtureHttp, bucketFixtureHttp},
            new Object[] {"GRPC/Prod", storageFixtureGrpc, bucketFixtureGrpc});

    DataGenerator gen = DataGenerator.base64Characters();
    int _2MiB = 2 * 1024 * 1024;
    int _24MiB = 24 * 1024 * 1024;
    ImmutableList<ChecksummedTestContent> contents =
        ImmutableList.of(
            // small, single message single stream when resumable
            ChecksummedTestContent.of(gen.genBytes(15)),
            // med, multiple messages single stream when resumable
            ChecksummedTestContent.of(gen.genBytes(_2MiB + 3)),
            // large, multiple messages and multiple streams when resumable
            ChecksummedTestContent.of(gen.genBytes(_24MiB + 5)));

    ImmutableList.Builder<Object[]> cross = ImmutableList.builder();
    for (Object[] cab : clientsAndBuckets) {
      for (ChecksummedTestContent content : contents) {
        cross.add(new Object[] {cab[0] + "/" + content.getBytes().length, cab[1], cab[2], content});
      }
    }
    return cross.build();
  }

  @Test
  public void testCrc32cValidated_createFrom_expectFailure() {
    String blobName = testName.getMethodName();
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(content.getCrc32cBase64()).build();

    byte[] bytes = content.concat('x');
    StorageException expected =
        assertThrows(
            StorageException.class,
            () ->
                storage.createFrom(
                    blobInfo,
                    new ByteArrayInputStream(bytes),
                    BlobWriteOption.doesNotExist(),
                    BlobWriteOption.crc32cMatch()));
    assertThat(expected.getCode()).isEqualTo(400);
  }

  @Test
  public void testCrc32cValidated_createFrom_expectSuccess() throws IOException {
    String blobName = testName.getMethodName();
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(content.getCrc32cBase64()).build();

    byte[] bytes = content.getBytes();
    Blob blob =
        storage.createFrom(
            blobInfo,
            new ByteArrayInputStream(bytes),
            BlobWriteOption.doesNotExist(),
            BlobWriteOption.crc32cMatch());
    assertThat(blob.getCrc32c()).isEqualTo(content.getCrc32cBase64());
  }

  @Test
  public void testCrc32cValidated_writer_expectFailure() {
    String blobName = testName.getMethodName();
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(content.getCrc32cBase64()).build();

    byte[] bytes = content.concat('x');
    StorageException expected =
        assertThrows(
            StorageException.class,
            () -> {
              try (ReadableByteChannel src = Channels.newChannel(new ByteArrayInputStream(bytes));
                  WriteChannel dst =
                      storage.writer(
                          blobInfo,
                          BlobWriteOption.doesNotExist(),
                          BlobWriteOption.crc32cMatch())) {
                ByteStreams.copy(src, dst);
              }
            });
    assertThat(expected.getCode()).isEqualTo(400);
  }

  @Test
  public void testCrc32cValidated_writer_expectSuccess() throws IOException {
    String blobName = testName.getMethodName();
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(content.getCrc32cBase64()).build();

    byte[] bytes = content.getBytes();

    try (ReadableByteChannel src = Channels.newChannel(new ByteArrayInputStream(bytes));
        WriteChannel dst =
            storage.writer(
                blobInfo, BlobWriteOption.doesNotExist(), BlobWriteOption.crc32cMatch())) {
      ByteStreams.copy(src, dst);
    }

    Blob blob = storage.get(blobId);
    assertThat(blob.getCrc32c()).isEqualTo(content.getCrc32cBase64());
  }

  @Test
  @SuppressWarnings({"deprecation"})
  public void testCreateBlobMd5Fail() {
    // Error Handling for GRPC not complete
    // b/247621346
    assumeTrue(clientName.startsWith("JSON"));

    String blobName = testName.getMethodName();
    BlobInfo blob =
        BlobInfo.newBuilder(bucketFixture.getBucketInfo(), blobName)
            .setMd5("O1R4G1HJSDUISJjoIYmVhQ==")
            .build();
    ByteArrayInputStream stream = content.bytesAsInputStream();
    try {
      storage.create(blob, stream, Storage.BlobWriteOption.md5Match());
      fail("StorageException was expected");
    } catch (StorageException ex) {
      // expected
    }
  }
}
