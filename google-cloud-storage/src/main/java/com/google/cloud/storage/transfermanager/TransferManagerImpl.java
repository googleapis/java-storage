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

import com.google.cloud.storage.BlobInfo;
import com.google.common.collect.ImmutableList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class TransferManagerImpl implements TransferManager {

  private final TransferManagerConfig transferManagerConfig;
  private final ExecutorService executor;

  public TransferManagerImpl(TransferManagerConfig transferManagerConfig) {
    this.transferManagerConfig = transferManagerConfig;
    this.executor = Executors.newFixedThreadPool(transferManagerConfig.getMaxWorkers());
  }

  @Override
  public @NonNull UploadJob uploadFiles(List<Path> files, ParallelUploadConfig opts) {
    List<Future<UploadResult>> uploadTasks = new ArrayList<>();
    for (Path file : files) {
      if (Files.isDirectory(file)) throw new IllegalStateException("Directories are not supported");
      String blobName = TransferManagerUtils.createBlobName(opts, file);
      BlobInfo blobInfo = BlobInfo.newBuilder(opts.getBucketName(), blobName).build();
      UploadCallable callable = new UploadCallable(transferManagerConfig, blobInfo, file, opts);
      uploadTasks.add(executor.submit(callable));
    }
    return UploadJob.newBuilder()
        .setParallelUploadConfig(opts)
        .setUploadResponses(ImmutableList.copyOf(uploadTasks))
        .build();
  }

  @Override
  public @NonNull DownloadJob downloadBlobs(List<BlobInfo> blobs, ParallelDownloadConfig opts) {
    List<Future<DownloadResult>> downloadTasks = new ArrayList<>();
    for (BlobInfo blob : blobs) {
      DownloadCallable callable = new DownloadCallable(transferManagerConfig, blob, opts);
      downloadTasks.add(executor.submit(callable));
    }
    return DownloadJob.newBuilder()
        .setDownloadResults(downloadTasks)
        .setParallelDownloadConfig(opts)
        .build();
  }
}
