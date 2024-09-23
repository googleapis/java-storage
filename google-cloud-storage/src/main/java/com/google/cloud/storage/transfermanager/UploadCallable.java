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

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.StorageException;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

final class UploadCallable<T> implements Callable<UploadResult> {
  private final Storage storage;

  private final BlobInfo originalBlob;

  private final T source;

  private final ParallelUploadConfig parallelUploadConfig;

  private final Storage.BlobWriteOption[] opts;

  public UploadCallable(
      Storage storage,
      BlobInfo originalBlob,
      T source,
      ParallelUploadConfig parallelUploadConfig,
      BlobWriteOption[] opts) {
    this.storage = storage;
    this.originalBlob = originalBlob;
    this.source = source;
    this.parallelUploadConfig = parallelUploadConfig;
    this.opts = opts;
  }

  public UploadResult call() {
    // TODO: Check for chunking
    return uploadWithoutChunking();
  }

  private UploadResult uploadWithoutChunking() {
    try {
      Blob from;
      if (source instanceof Path) {
        from = storage.createFrom(originalBlob, (Path) source, opts);
      } else if (source instanceof InputStream) {
        from = storage.createFrom(originalBlob, (InputStream) source, opts);
      } else {
        throw new IllegalArgumentException("Unsupported source type: " + source.getClass().getName());
      }
      return UploadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
          .setUploadedBlob(from.asBlobInfo())
          .build();
    } catch (StorageException e) {
      if (parallelUploadConfig.isSkipIfExists() && e.getCode() == 412) {
        return UploadResult.newBuilder(originalBlob, TransferStatus.SKIPPED)
            .setException(e)
            .build();
      } else {
        // TODO: check for FAILED_TO_START conditions
        return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_FINISH)
            .setException(e)
            .build();
      }
    } catch (Exception e) {
      return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_FINISH)
          .setException(e)
          .build();
    }
  }
}
