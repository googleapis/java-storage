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

import com.google.api.core.ApiFuture;
import com.google.api.core.ListenableFutureToApiFuture;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.checkerframework.checker.nullness.qual.NonNull;

final class TransferManagerImpl implements TransferManager {

  private final Long DOWNLOAD_IN_CHUNKS_FILE_SIZE_THRESHOLD = Long.valueOf(32 * 1024 * 1024);

  private final TransferManagerConfig transferManagerConfig;
  private final ListeningExecutorService executor;
  private final Storage storage;

  TransferManagerImpl(TransferManagerConfig transferManagerConfig) {
    this.transferManagerConfig = transferManagerConfig;
    this.executor =
        MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(transferManagerConfig.getMaxWorkers()));

    this.storage = transferManagerConfig.getStorageOptions().getService();
  }

  @Override
  public @NonNull UploadJob uploadFiles(List<Path> files, ParallelUploadConfig config) {
    Storage.BlobWriteOption[] opts =
        config.getWriteOptsPerRequest().toArray(new BlobWriteOption[0]);
    List<ApiFuture<UploadResult>> uploadTasks = new ArrayList<>();
    for (Path file : files) {
      if (Files.isDirectory(file)) throw new IllegalStateException("Directories are not supported");
      String blobName = TransferManagerUtils.createBlobName(config, file);
      BlobInfo blobInfo = BlobInfo.newBuilder(config.getBucketName(), blobName).build();
      UploadCallable callable =
          new UploadCallable(transferManagerConfig, storage, blobInfo, file, config, opts);
      uploadTasks.add(convert(executor.submit(callable)));
    }
    return UploadJob.newBuilder()
        .setParallelUploadConfig(config)
        .setUploadResponses(ImmutableList.copyOf(uploadTasks))
        .build();
  }

  @Override
  public @NonNull DownloadJob downloadBlobs(List<BlobInfo> blobs, ParallelDownloadConfig config) {
    Storage.BlobSourceOption[] opts =
        config.getOptionsPerRequest().toArray(new Storage.BlobSourceOption[0]);
    List<ApiFuture<DownloadResult>> downloadTasks = new ArrayList<>();
    if (!transferManagerConfig.isAllowChunking()) {
      for (BlobInfo blob : blobs) {
        DirectDownloadCallable callable = new DirectDownloadCallable(storage, blob, config, opts);
        downloadTasks.add(convert(executor.submit(callable)));
      }
    } else {
      for (BlobInfo blob : blobs) {
        BlobInfo validatedBlob = retrieveSizeAndGeneration(storage, blob, config.getBucketName());
        long size = validatedBlob.getSize();
        if (size > DOWNLOAD_IN_CHUNKS_FILE_SIZE_THRESHOLD) {
          List<ApiFuture<DownloadSegment>> downloadSegmentTasks = new ArrayList<>();
          // Perform chunked download
          long start = 0;
          while (start < size) {
            long chunkStart = start;
            long chunkEnd = start + transferManagerConfig.getPerWorkerBufferSize() - 1;
            chunkEnd = chunkEnd > size ? size : chunkEnd;
            ChunkedDownloadCallable callable =
                new ChunkedDownloadCallable(storage, blob, config, opts, chunkStart, chunkEnd);
            downloadSegmentTasks.add(convert(executor.submit(callable)));
          }
        } else {
          DirectDownloadCallable callable = new DirectDownloadCallable(storage, blob, config, opts);
          downloadTasks.add(convert(executor.submit(callable)));
        }
      }
    }
    return DownloadJob.newBuilder()
        .setDownloadResults(downloadTasks)
        .setParallelDownloadConfig(config)
        .build();
  }

  private static <T> ApiFuture<T> convert(ListenableFuture<T> lf) {
    return new ListenableFutureToApiFuture<>(lf);
  }

  private BlobInfo retrieveSizeAndGeneration(
      Storage storage, BlobInfo blobInfo, String bucketName) {
    if (blobInfo.getGeneration() == null) {
      return storage.get(BlobId.of(bucketName, blobInfo.getName()));
    } else if (blobInfo.getSize() == null) {
      return storage.get(BlobId.of(bucketName, blobInfo.getName(), blobInfo.getGeneration()));
    }
    return blobInfo;
  }
}
