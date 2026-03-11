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

package com.google.cloud.storage.transfermanager;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.transfermanager.ParallelUploadConfig.UploadBlobInfoFactory;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class TransferManagerTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private Storage storage;
  private StorageOptions storageOptions;
  private TransferManager transferManager;
  private Path baseDir;

  @Before
  public void setUp() {
    storage = mock(Storage.class);
    storageOptions = mock(StorageOptions.class);
    when(storageOptions.getService()).thenReturn(storage);
    when(storageOptions.toBuilder()).thenReturn(StorageOptions.newBuilder());

    TransferManagerConfig config =
        TransferManagerConfig.newBuilder().setStorageOptions(storageOptions).build();
    transferManager = config.getService();
    baseDir = tmpDir.getRoot().toPath();
  }

  @Test
  public void uploadBlobInfoFactory_prefixObjectNames_leadingSlash() {
    UploadBlobInfoFactory factory = UploadBlobInfoFactory.prefixObjectNames("asdf");

    BlobInfo info = factory.apply("bucket", "/f/i/l/e/n/a/m/e.txt");
    assertThat(info.getBucket()).isEqualTo("bucket");
    assertThat(info.getName()).isEqualTo("asdf/f/i/l/e/n/a/m/e.txt");
  }

  @Test
  public void uploadBlobInfoFactory_prefixObjectNames() {
    UploadBlobInfoFactory factory = UploadBlobInfoFactory.prefixObjectNames("asdf");

    BlobInfo info = factory.apply("bucket", "n/a/m/e.txt");
    assertThat(info.getBucket()).isEqualTo("bucket");
    assertThat(info.getName()).isEqualTo("asdf/n/a/m/e.txt");
  }

  @Test
  public void uploadBlobInfoFactory_transformFileName() {
    UploadBlobInfoFactory factory =
        UploadBlobInfoFactory.transformFileName(
            Function.<String>identity().andThen(s -> s + "|").compose(s -> "|" + s));

    BlobInfo info = factory.apply("bucket", "/e.txt");
    assertThat(info.getBucket()).isEqualTo("bucket");
    assertThat(info.getName()).isEqualTo("|/e.txt|");
  }

  @Test
  public void uploadBlobInfoFactory_default_doesNotModify() {
    UploadBlobInfoFactory factory = UploadBlobInfoFactory.defaultInstance();

    BlobInfo info = factory.apply("bucket", "/e.txt");
    assertThat(info.getBucket()).isEqualTo("bucket");
    assertThat(info.getName()).isEqualTo("/e.txt");
  }

  @Test
  public void downloadBlobs_skipIfExists() throws IOException {
    String bucketName = "test-bucket";
    String blobName = "test-blob.txt";
    BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();

    Path destPath = baseDir.resolve(blobName);
    Files.createFile(destPath); // Create the file locally

    ParallelDownloadConfig config =
        ParallelDownloadConfig.newBuilder()
            .setBucketName(bucketName)
            .setDownloadDirectory(baseDir)
            .setSkipIfExists(true)
            .build();

    DownloadJob job = transferManager.downloadBlobs(ImmutableList.of(blobInfo), config);
    List<DownloadResult> results = job.getDownloadResults();

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getStatus()).isEqualTo(TransferStatus.SKIPPED);
  }
}
