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

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.PackagePrivateMethodWorkarounds;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.common.io.ByteStreams;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.Callable;

final class UploadCallable implements Callable<UploadResult> {
  private final TransferManagerConfig transferManagerConfig;
  private final Storage storage;

  private final BlobInfo originalBlob;

  private final Path sourceFile;

  private final ParallelUploadConfig parallelUploadConfig;

  private final Storage.BlobWriteOption[] opts;

  public UploadCallable(
      TransferManagerConfig transferManagerConfig,
      Storage storage,
      BlobInfo originalBlob,
      Path sourceFile,
      ParallelUploadConfig parallelUploadConfig,
      BlobWriteOption[] opts) {
    this.transferManagerConfig = transferManagerConfig;
    this.storage = storage;
    this.originalBlob = originalBlob;
    this.sourceFile = sourceFile;
    this.parallelUploadConfig = parallelUploadConfig;
    this.opts = opts;
  }

  public UploadResult call() throws Exception {
    // TODO: Check for chunking
    return uploadWithoutChunking();
  }

  private UploadResult uploadWithoutChunking() {
    long bytesCopied = -1L;
    try {
      Optional<BlobInfo> newBlob;
      WriteChannel w = storage.writer(originalBlob, opts);
      try (FileChannel r = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
        w.setChunkSize(transferManagerConfig.getPerWorkerBufferSize());
        bytesCopied = ByteStreams.copy(r, w);
      } finally {
        w.close();
      }
      newBlob = PackagePrivateMethodWorkarounds.maybeGetBlobInfoFunction().apply(w);
      return UploadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
          .setUploadedBlob(newBlob.get())
          .build();
    } catch (Exception e) {
      if (bytesCopied == -1) {
        return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_START)
            .setException(e)
            .build();
      }
      return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_FINISH)
          .setException(e)
          .build();
    }
  }
}
