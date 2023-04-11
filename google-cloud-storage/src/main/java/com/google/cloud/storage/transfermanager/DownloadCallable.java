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
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.StorageException;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

final class DownloadCallable implements Callable<DownloadResult> {
  private final BlobInfo originalBlob;

  private final ParallelDownloadConfig parallelDownloadConfig;
  private final Storage storage;

  private final Storage.BlobSourceOption[] opts;

  DownloadCallable(
      Storage storage,
      BlobInfo originalBlob,
      ParallelDownloadConfig parallelDownloadConfig,
      BlobSourceOption[] opts) {
    this.originalBlob = originalBlob;
    this.parallelDownloadConfig = parallelDownloadConfig;
    this.storage = storage;
    this.opts = opts;
  }

  @Override
  public DownloadResult call() throws Exception {
    // TODO: Check for chunking
    return downloadWithoutChunking();
  }

  private DownloadResult downloadWithoutChunking() {
    Path path = createDestPath();
    try (ReadChannel rc = storage.reader(originalBlob.getBlobId(), opts)) {
      FileChannel destFile =
          FileChannel.open(
              path,
              StandardOpenOption.WRITE,
              StandardOpenOption.CREATE,
              StandardOpenOption.TRUNCATE_EXISTING);
      ByteStreams.copy(rc, destFile);
    } catch (IOException e) {
      throw new StorageException(e);
    }
    DownloadResult result =
        DownloadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
            .setOutputDestination(path)
            .build();
    return result;
  }

  private Path createDestPath() {
    Path newPath =
        parallelDownloadConfig
            .getDownloadDirectory()
            .resolve(
                originalBlob.getName().replaceFirst(parallelDownloadConfig.getStripPrefix(), ""));
    // Check to make sure the parent directories exist
    if (Files.exists(newPath.getParent())) {
      return newPath;
    } else {
      // Make parent directories if they do not exist
      try {
        Files.createDirectories(newPath.getParent());
        return newPath;
      } catch (IOException e) {
        throw new StorageException(e);
      }
    }
  }
}
