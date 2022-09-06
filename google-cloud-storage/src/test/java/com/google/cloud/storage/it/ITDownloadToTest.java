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
import com.google.cloud.storage.conformance.retry.ParallelParameterized;
import com.google.cloud.storage.conformance.retry.TestBench;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(ParallelParameterized.class)
public final class ITDownloadToTest {
  @ClassRule
  public static final TestBench TEST_BENCH =
      TestBench.newBuilder().setContainerName("it-grpc").build();

  @Rule public final StorageFixture storageFixture;

  @ClassRule public final BucketFixture bucketFixture;

  private static final byte[] helloWorldTextBytes = "hello world".getBytes();
  private static final byte[] helloWorldGzipBytes = TestUtils.gzipBytes(helloWorldTextBytes);

  private static Storage storage;
  private static BlobId blobId;

  public ITDownloadToTest(String clientName, StorageFixture storageFixture) {
    this.storageFixture = storageFixture;
    this.bucketFixture =
        BucketFixture.newBuilder()
            .setBucketNameFmtString("java-storage-gcs-team-%s")
            .setCleanupStrategy(CleanupStrategy.ALWAYS)
            .setHandle(storageFixture::getInstance)
            .build();
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    StorageFixture grpcStorageFixture =
        StorageFixture.from(
            () ->
                StorageOptions.grpc()
                    .setHost(TEST_BENCH.getGRPCBaseUri())
                    .setCredentials(NoCredentials.getInstance())
                    .setProjectId("test-project-id")
                    .build());
    StorageFixture jsonStorageFixture = StorageFixture.defaultHttp();
    return Arrays.asList(
        new Object[] {"JSON/storage.googleapis.com", jsonStorageFixture},
        new Object[] {"GRPC/" + TEST_BENCH.getGRPCBaseUri(), grpcStorageFixture});
  }

  @Before
  public void beforeClass() {
    blobId = BlobId.of(bucketFixture.getBucketInfo().getName(), "zipped_blob");

    BlobInfo blobInfo =
        BlobInfo.newBuilder(blobId).setContentEncoding("gzip").setContentType("text/plain").build();
    storage = storageFixture.getInstance();
    storage.create(blobInfo, helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_yes() throws IOException {
    Path helloWorldTxtGz = File.createTempFile("helloWorld", ".txt.gz").toPath();
    storage.downloadTo(
        blobId, helloWorldTxtGz, Storage.BlobSourceOption.shouldReturnRawInputStream(true));

    byte[] actualTxtGzBytes = Files.readAllBytes(helloWorldTxtGz);
    if (Arrays.equals(actualTxtGzBytes, helloWorldTextBytes)) {
      fail("expected gzipped bytes, but got un-gzipped bytes");
    }
    assertThat(actualTxtGzBytes).isEqualTo(helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_no() throws IOException {
    Path helloWorldTxt = File.createTempFile("helloWorld", ".txt").toPath();
    storage.downloadTo(
        blobId, helloWorldTxt, Storage.BlobSourceOption.shouldReturnRawInputStream(false));
    byte[] actualTxtBytes = Files.readAllBytes(helloWorldTxt);
    assertThat(actualTxtBytes).isEqualTo(helloWorldTextBytes);
  }
}
