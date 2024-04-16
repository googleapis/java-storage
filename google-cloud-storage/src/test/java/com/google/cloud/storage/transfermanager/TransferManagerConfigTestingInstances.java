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

import com.google.cloud.storage.StorageOptions;

public final class TransferManagerConfigTestingInstances {
  private TransferManagerConfigTestingInstances() {}

  public static TransferManagerConfig defaults() {
    return defaults(StorageOptions.newBuilder().build());
  }

  public static TransferManagerConfig defaults(StorageOptions options) {
    return TransferManagerConfig.newBuilder()
        .setAllowDivideAndConquerDownload(false)
        .setMaxWorkers(5)
        .setPerWorkerBufferSize(512 * 1024)
        .setStorageOptions(options)
        .build();
  }
}
