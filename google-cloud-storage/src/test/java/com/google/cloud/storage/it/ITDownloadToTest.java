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
import static org.junit.Assert.fail;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.TestUtils;
import com.google.cloud.storage.conformance.retry.CleanupStrategy;
import com.google.cloud.storage.conformance.retry.TestBench;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class ITDownloadToTest {
  @ClassRule(order = 0)
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("it-grpc").build();

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureGrpc =
      StorageFixture.from(
          () ->
              StorageOptions.grpc()
                  .setHost(TEST_BENCH.getGRPCBaseUri())
                  .setCredentials(NoCredentials.getInstance())
                  .setProjectId("test-project-id")
                  .build());

  @ClassRule(order = 1)
  public static final StorageFixture storageFixtureHttp = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixtureGrpc =
      BucketFixture.newBuilder()
          .setBucketNameFmtString("java-storage-grpc-%s")
          .setCleanupStrategy(CleanupStrategy.ALWAYS)
          .setHandle(storageFixtureGrpc::getInstance)
          .build();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixtureHttp =
      BucketFixture.newBuilder()
          .setBucketNameFmtString("java-storage-http-%s")
          .setCleanupStrategy(CleanupStrategy.ALWAYS)
          .setHandle(storageFixtureHttp::getInstance)
          .build();

  private static final byte[] helloWorldTextBytes = "hello world".getBytes();
  private static final byte[] helloWorldGzipBytes = TestUtils.gzipBytes(helloWorldTextBytes);

  private final StorageFixture storageFixture;
  private final BucketFixture bucketFixture;
  private final String clientName;

  public ITDownloadToTest(
      String clientName, StorageFixture storageFixture, BucketFixture bucketFixture) {
    this.storageFixture = storageFixture;
    this.bucketFixture = bucketFixture;
    this.clientName = clientName;
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(
        new Object[] {"JSON/storage.googleapis.com", storageFixtureHttp, bucketFixtureHttp},
        new Object[] {
          "GRPC/" + TEST_BENCH.getGRPCBaseUri(), storageFixtureGrpc, bucketFixtureGrpc
        });
  }

  @Before
  public void beforeClass() {
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), "zipped_blob");

    BlobInfo blobInfo =
        BlobInfo.newBuilder(blobId).setContentEncoding("gzip").setContentType("text/plain").build();
    storageFixture.getInstance().create(blobInfo, helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_yes() throws IOException {
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), "zipped_blob");
    Path helloWorldTxtGz = File.createTempFile("helloWorld", ".txt.gz").toPath();
    storageFixture
        .getInstance()
        .downloadTo(
            blobId, helloWorldTxtGz, Storage.BlobSourceOption.shouldReturnRawInputStream(true));

    byte[] actualTxtGzBytes = Files.readAllBytes(helloWorldTxtGz);
    if (Arrays.equals(actualTxtGzBytes, helloWorldTextBytes)) {
      fail("expected gzipped bytes, but got un-gzipped bytes");
    }
    assertThat(actualTxtGzBytes).isEqualTo(helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_no() throws IOException {
    BlobId blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), "zipped_blob");
    Path helloWorldTxt = File.createTempFile("helloWorld", ".txt").toPath();
    storageFixture
        .getInstance()
        .downloadTo(
            blobId, helloWorldTxt, Storage.BlobSourceOption.shouldReturnRawInputStream(false));
    byte[] actualTxtBytes = Files.readAllBytes(helloWorldTxt);
    assertThat(actualTxtBytes).isEqualTo(helloWorldTextBytes);
  }
}
