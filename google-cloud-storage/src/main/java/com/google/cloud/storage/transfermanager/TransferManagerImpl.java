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
import com.google.api.core.ApiFutures;
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
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;

final class TransferManagerImpl implements TransferManager {

  private final TransferManagerConfig transferManagerConfig;
  private final ListeningExecutorService executor;
  private final Qos qos;
  private final Storage storage;

  TransferManagerImpl(TransferManagerConfig transferManagerConfig) {
    this.transferManagerConfig = transferManagerConfig;
    this.executor =
        MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(transferManagerConfig.getMaxWorkers()));
    this.qos = transferManagerConfig.getQos();
    this.storage = transferManagerConfig.getStorageOptions().getService();
  }

  @Override
  public void close() throws Exception {
    // We only want to shutdown the executor service not the provided storage instance
    executor.shutdownNow();
    executor.awaitTermination(5, TimeUnit.MINUTES);
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
    if (!transferManagerConfig.isAllowDivideAndConquer()) {
      for (BlobInfo blob : blobs) {
        DirectDownloadCallable callable = new DirectDownloadCallable(storage, blob, config, opts);
        downloadTasks.add(convert(executor.submit(callable)));
      }
    } else {
      for (BlobInfo blob : blobs) {
        BlobInfo validatedBlob = retrieveSizeAndGeneration(storage, blob, config.getBucketName());
        Path destPath = TransferManagerUtils.createDestPath(config, blob);
        if (validatedBlob != null && qos.divideAndConquer(validatedBlob.getSize())) {
          DownloadResult optimisticResult =
              DownloadResult.newBuilder(validatedBlob, TransferStatus.SUCCESS)
                  .setOutputDestination(destPath)
                  .build();

          List<ApiFuture<DownloadSegment>> downloadSegmentTasks =
              computeRanges(validatedBlob.getSize(), transferManagerConfig.getPerWorkerBufferSize()).stream()
                  .map(
                      r ->
                          new ChunkedDownloadCallable(
                              storage, validatedBlob, opts, destPath, r.begin, r.end))
                  .map(executor::submit)
                  .map(TransferManagerImpl::convert)
                  .collect(ImmutableList.toImmutableList());

          downloadTasks.add(
              ApiFutures.transform(
                  ApiFutures.allAsList(downloadSegmentTasks),
                  segments ->
                      segments.stream()
                          .reduce(
                              optimisticResult,
                              DownloadSegment::reduce,
                              BinaryOperator.minBy(DownloadResult.COMPARATOR)),
                  MoreExecutors.directExecutor()));
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

  private static BlobInfo retrieveSizeAndGeneration(
      Storage storage, BlobInfo blobInfo, String bucketName) {
    if (blobInfo.getGeneration() == null) {
      return storage.get(BlobId.of(bucketName, blobInfo.getName()));
    } else if (blobInfo.getSize() == null) {
      return storage.get(BlobId.of(bucketName, blobInfo.getName(), blobInfo.getGeneration()));
    }
    return blobInfo;
  }

  private static ImmutableList<Range> computeRanges(long end, long segmentSize) {
    ImmutableList.Builder<Range> b = ImmutableList.builder();

    if (end <= segmentSize) {
      b.add(Range.of(0, end));
    } else {
      for (long i = 0; i < end; i += segmentSize) {
        b.add(Range.of(i, Math.min(i + segmentSize, end)));
      }
    }
    return b.build();
  }

  private static final class Range {
    private final long begin;
    private final long end;

    private Range(long begin, long end) {
      this.begin = begin;
      this.end = end;
    }

    public static Range of(long begin, long end) {
      return new Range(begin, end);
    }
  }
}
