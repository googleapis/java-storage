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

import com.google.cloud.NoCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGeneration;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.TestBench;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public final class ITBlobReadChannelTest {

  private static final int _16MiB = 16 * 1024 * 1024;
  private static final int _256KiB = 256 * 1024;

  @ClassRule
  public static final TestBench testBench =
      TestBench.newBuilder().setContainerName("blob-read-channel-test").build();

  @Rule public final TestName testName = new TestName();

  @Rule public final DataGeneration dataGeneration = new DataGeneration(new Random(872364872));

  @Test
  public void testLimit_smallerThanOneChunk() throws IOException {
    int srcContentSize = _256KiB;
    int rangeBegin = 57;
    int rangeEnd = 2384;
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

  private void doLimitTest(int srcContentSize, int rangeBegin, int rangeEnd, int chunkSize)
      throws IOException {
    Storage s =
        StorageOptions.newBuilder()
            .setProjectId("blob-read-channel-test")
            .setHost(testBench.getBaseUri())
            .setCredentials(NoCredentials.getInstance())
            .build()
            .getService();

    String testNameMethodName = testName.getMethodName();
    String bucketName = String.format("bucket-%s", testNameMethodName.toLowerCase());
    String blobName = String.format("%s/src", testNameMethodName);

    Bucket bucket = s.create(BucketInfo.of(bucketName));
    BlobInfo src = BlobInfo.newBuilder(bucket, blobName).build();
    ByteBuffer content = dataGeneration.randByteBuffer(srcContentSize);
    ByteBuffer expectedSubContent = content.duplicate();
    expectedSubContent.position(rangeBegin);
    expectedSubContent.limit(rangeEnd);
    try (WriteChannel writer = s.writer(src)) {
      writer.write(content);
    }

    ByteBuffer actual = ByteBuffer.allocate(rangeEnd - rangeBegin);

    try (ReadChannel reader = s.reader(src.getBlobId())) {
      reader.setChunkSize(chunkSize);
      reader.seek(rangeBegin);
      reader.limit(rangeEnd);
      reader.read(actual);
      actual.flip();
    }

    assertThat(actual).isEqualTo(expectedSubContent);
  }
}
