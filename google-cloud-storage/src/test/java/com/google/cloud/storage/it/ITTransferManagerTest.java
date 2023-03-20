/*
 * Copyright 2023 Google LLC
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

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TmpFile;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.transfermanager.DownloadJob;
import com.google.cloud.storage.transfermanager.ParallelDownloadConfig;
import com.google.cloud.storage.transfermanager.ParallelUploadConfig;
import com.google.cloud.storage.transfermanager.TransferManager;
import com.google.cloud.storage.transfermanager.TransferManagerConfig;
import com.google.cloud.storage.transfermanager.TransferManagerImpl;
import com.google.cloud.storage.transfermanager.UploadJob;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP},
    backends = {Backend.PROD})
public class ITTransferManagerTest {
  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private Path baseDir;
  private static final int objectContentSize = 64;
  private List<BlobInfo> blobs = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    baseDir = tmpDir.getRoot().toPath();
    BlobInfo blobInfo1 =
        BlobInfo.newBuilder(
                BlobId.of(bucket.getName(), String.format("%s/src", generator.randomObjectName())))
            .build();
    BlobInfo blobInfo2 =
        BlobInfo.newBuilder(
                BlobId.of(bucket.getName(), String.format("%s/src", generator.randomObjectName())))
            .build();
    BlobInfo blobInfo3 =
        BlobInfo.newBuilder(
                BlobId.of(bucket.getName(), String.format("%s/src", generator.randomObjectName())))
            .build();
    Collections.addAll(blobs, blobInfo1, blobInfo2, blobInfo3);
    ByteBuffer content = DataGenerator.base64Characters().genByteBuffer(108);
    for (BlobInfo blob : blobs) {
      try (WriteChannel writeChannel = storage.writer(blob)) {
        writeChannel.write(content);
      }
    }
  }

  @Test
  public void uploadFiles() throws IOException {
    TransferManagerConfig config =
        TransferManagerConfig.newBuilder().setAllowChunking(false).setMaxWorkers(1).build();
    TransferManager transferManager = new TransferManagerImpl(config);
    try (TmpFile tmpFile = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        TmpFile tmpFile1 = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        TmpFile tmpFile2 = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize)) {
      List<Path> files =
          ImmutableList.of(tmpFile.getPath(), tmpFile1.getPath(), tmpFile2.getPath());
      String bucketName = bucket.getName();
      ParallelUploadConfig parallelUploadConfig =
          ParallelUploadConfig.newBuilder().setBucketName(bucketName).build();
      UploadJob job = transferManager.uploadFiles(files, parallelUploadConfig);
      assertThat(job.getUploadResponses()).hasSize(3);
    }
  }

  @Test
  public void downloadBlobs() throws IOException {
    TransferManagerConfig config =
        TransferManagerConfig.newBuilder().setAllowChunking(false).setMaxWorkers(1).build();
    TransferManager transferManager = new TransferManagerImpl(config);
    String bucketName = bucket.getName();
    ParallelDownloadConfig parallelDownloadConfig =
        ParallelDownloadConfig.newBuilder().setBucketName(bucketName).build();
    DownloadJob job = transferManager.downloadBlobs(blobs, parallelDownloadConfig);
    assertThat(job.getDownloadResults()).hasSize(3);
  }
}
