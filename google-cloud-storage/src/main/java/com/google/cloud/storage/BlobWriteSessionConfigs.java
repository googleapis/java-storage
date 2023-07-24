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

package com.google.cloud.storage;

import com.google.api.core.BetaApi;
import com.google.cloud.storage.GrpcStorageOptions.GrpcStorageDefaults;
import com.google.cloud.storage.Storage.BlobWriteOption;

/**
 * Factory class to select and construct {@link BlobWriteSessionConfig}s.
 *
 * @see BlobWriteSessionConfig
 * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
 * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
 * @since 2.26.0 This new api is in preview and is subject to breaking changes.
 */
@BetaApi
public final class BlobWriteSessionConfigs {

  private BlobWriteSessionConfigs() {}

  /**
   * Factory to produce the default configuration for uploading an object to Cloud Storage.
   *
   * <p>Configuration of the chunk size can be performed via {@link
   * DefaultBlobWriteSessionConfig#withChunkSize(int)}.
   *
   * @see GrpcStorageDefaults#getDefaultStorageWriterConfig()
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static DefaultBlobWriteSessionConfig getDefault() {
    return new DefaultBlobWriteSessionConfig(ByteSizeConstants._16MiB);
  }
}
