/*
 * Copyright 2024 Google LLC
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
import com.google.api.core.SettableApiFuture;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobWriteSession;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.StorageException;
import com.google.common.io.ByteStreams;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

final class ParallelCompositeUploadCallable implements Callable<UploadResult> {
  private final Storage storage;

  private final BlobInfo originalBlob;

  private final Path sourceFile;

  private final ParallelUploadConfig parallelUploadConfig;

  private final Storage.BlobWriteOption[] opts;

  private final SettableApiFuture<UploadResult> result;

  public ParallelCompositeUploadCallable(
      Storage storage,
      BlobInfo originalBlob,
      Path sourceFile,
      ParallelUploadConfig parallelUploadConfig,
      BlobWriteOption[] opts) {
    this.storage = storage;
    this.originalBlob = originalBlob;
    this.sourceFile = sourceFile;
    this.parallelUploadConfig = parallelUploadConfig;
    this.opts = opts;
    this.result = SettableApiFuture.create();
  }

  public ApiFuture<UploadResult> getResult() {
    return result;
  }

  public UploadResult call() throws ExecutionException, InterruptedException {
    try {
      UploadResult uploadResult = uploadPCU();
      result.set(uploadResult);
      return uploadResult;
    } catch (Exception e) {
      result.setException(e);
      throw e;
    }
  }

  private UploadResult uploadPCU() throws ExecutionException, InterruptedException {
    BlobWriteSession session = storage.blobWriteSession(originalBlob, opts);
    try (WritableByteChannel writableByteChannel = session.open();
        FileChannel fc = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      ByteStreams.copy(fc, writableByteChannel);
    } catch (StorageException e) {
      if (parallelUploadConfig.isSkipIfExists() && e.getCode() == 412) {
        return UploadResult.newBuilder(originalBlob, TransferStatus.SKIPPED)
            .setException(e)
            .build();
      } else {
        return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_FINISH)
            .setException(e)
            .build();
      }
    } catch (Exception e) {
      return UploadResult.newBuilder(originalBlob, TransferStatus.FAILED_TO_FINISH)
          .setException(e)
          .build();
    }
    BlobInfo newBlob = session.getResult().get();
    return UploadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
        .setUploadedBlob(newBlob)
        .build();
  }
}
