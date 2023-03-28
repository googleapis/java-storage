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
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

public class UploadCallable implements Callable<UploadResult> {
  private final TransferManagerConfig transferManagerConfig;

  private final BlobInfo originalBlob;

  private final Path sourceFile;

  private final ParallelUploadConfig parallelUploadConfig;

  public UploadCallable(
      TransferManagerConfig transferManagerConfig,
      BlobInfo originalBlob,
      Path sourceFile,
      ParallelUploadConfig parallelUploadConfig) {
    this.transferManagerConfig = transferManagerConfig;
    this.originalBlob = originalBlob;
    this.sourceFile = sourceFile;
    this.parallelUploadConfig = parallelUploadConfig;
  }

  public UploadResult call() throws Exception {
    // TODO: Check for chunking
    return uploadWithoutChunking();
  }

  private UploadResult uploadWithoutChunking() throws IOException {
    Optional<BlobInfo> newBlob;
    WriteChannel wc =
        transferManagerConfig
            .getStorageOptions()
            .getService()
            .writer(
                originalBlob,
                parallelUploadConfig
                    .getWriteOptsPerRequest()
                    .toArray(new Storage.BlobWriteOption[0]));
    try {
      InputStream inputStream = Files.newInputStream(sourceFile);
      uploadHelper(Channels.newChannel(inputStream), wc);
    } catch (IOException e) {
      throw new StorageException(e);
    } finally {
      wc.close();
    }
    newBlob = PackagePrivateMethodWorkarounds.maybeGetBlobInfoFunction().apply(wc);
    UploadResult result =
        UploadResult.newBuilder(originalBlob, TransferStatus.SUCCESS)
            .setUploadedBlob(newBlob.get())
            .build();
    return result;
  }

  private void uploadHelper(ReadableByteChannel reader, WriteChannel writer) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(transferManagerConfig.getPerWorkerBufferSize());
    writer.setChunkSize(transferManagerConfig.getPerWorkerBufferSize());

    while (reader.read(buffer) >= 0) {
      buffer.flip();
      writer.write(buffer);
      buffer.clear();
    }
  }
}
