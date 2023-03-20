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

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

public class DownloadCallable implements Callable<DownloadResult> {
  private final TransferManagerConfig transferManagerConfig;
  private final BlobInfo originalBlob;

  private final ParallelDownloadConfig parallelDownloadConfig;

  public DownloadCallable(
      TransferManagerConfig transferManagerConfig,
      BlobInfo originalBlob,
      ParallelDownloadConfig parallelDownloadConfig) {
    this.transferManagerConfig = transferManagerConfig;
    this.originalBlob = originalBlob;
    this.parallelDownloadConfig = parallelDownloadConfig;
  }

  @Override
  public DownloadResult call() throws Exception {
    // TODO: Check for chunking
    return downloadWithoutChunking();
  }

  private DownloadResult downloadWithoutChunking() {
    try (ReadChannel rc =
        transferManagerConfig
            .getStorageOptions()
            .getService()
            .reader(
                originalBlob.getBlobId(),
                parallelDownloadConfig
                    .getOptionsPerRequest()
                    .toArray(new Storage.BlobSourceOption[0]))) {
      FileChannel destFile =
          FileChannel.open(Paths.get(createDestPath()), StandardOpenOption.WRITE);
      ByteStreams.copy(rc, destFile);
    } catch (IOException e) {
      throw new StorageException(e);
    }
    DownloadResult result =
        DownloadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
            .setOutputDestination(Paths.get(createDestPath()))
            .build();
    return result;
  }

  private String createDestPath() {
    return originalBlob
        .getName()
        .replaceFirst(parallelDownloadConfig.getStripPrefix(), parallelDownloadConfig.getPrefix());
  }
}
