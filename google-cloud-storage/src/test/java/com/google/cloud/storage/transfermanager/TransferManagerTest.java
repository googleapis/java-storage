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

package com.google.cloud.storage.transfermanager;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.TmpFile;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Path;
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
public class TransferManagerTest {
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private Path baseDir;
  private static final int objectContentSize = 64;

  @Before
  public void setUp() throws Exception {
    baseDir = tmpDir.getRoot().toPath();
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
  public void downloadBlobs() {}
}
