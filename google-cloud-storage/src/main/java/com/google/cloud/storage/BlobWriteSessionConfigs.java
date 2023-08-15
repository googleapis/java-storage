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
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

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

  /**
   * Create a new {@link BlobWriteSessionConfig} which will first buffer the content of the object
   * to a temporary file under {@code java.io.tmpdir}.
   *
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Google Cloud
   * Storage.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   */
  @BetaApi
  public static BlobWriteSessionConfig bufferToTempDirThenUpload() throws IOException {
    return bufferToDiskThenUpload(
        Paths.get(System.getProperty("java.io.tmpdir"), "google-cloud-storage"));
  }

  /**
   * Create a new {@link BlobWriteSessionConfig} which will first buffer the content of the object
   * to a temporary file under the specified {@code path}.
   *
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Google Cloud
   * Storage.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   */
  @BetaApi
  public static BufferToDiskThenUpload bufferToDiskThenUpload(Path path) throws IOException {
    return bufferToDiskThenUpload(ImmutableList.of(path));
  }

  /**
   * Create a new {@link BlobWriteSessionConfig} which will first buffer the content of the object
   * to a temporary file under one of the specified {@code paths}.
   *
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Google Cloud
   * Storage.
   *
   * <p>The specifics of how the work is spread across multiple paths is undefined and subject to
   * change.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   */
  @BetaApi
  public static BufferToDiskThenUpload bufferToDiskThenUpload(Collection<Path> paths)
      throws IOException {
    return new BufferToDiskThenUpload(ImmutableList.copyOf(paths), false);
  }
}
