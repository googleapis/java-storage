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
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TransferManagerUtils {

  private TransferManagerUtils() {}

  static String createBlobName(ParallelUploadConfig config, Path file) {
    if (config.getPrefix().isEmpty()) {
      return file.toString();
    } else {
      return config.getPrefix().concat(file.toString());
    }
  }

  static Path createDestPath(ParallelDownloadConfig config, BlobInfo originalBlob) {
    Path newPath =
        config
            .getDownloadDirectory()
            .resolve(originalBlob.getName().replaceFirst(config.getStripPrefix(), ""));
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
