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
import java.nio.file.Path;
import java.util.List;

public interface TransferManager {

  class TransferManagerConfig {
    

  }
  // Separate package for  transfer manager,
  // Might be allowing public access to ChannelSessionBuilder
  // We do not want to use ReadChannel and WriteChannel. We want to use the SessionBuilder.

  UploadJob uploadFiles(List<Path> files, ParallelUploadConfig opts);

  DownloadJob downloadBlobs(List<BlobInfo> blobs, ParallelDownloadConfig opts);


}
