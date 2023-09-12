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
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Factory class to select and construct {@link BlobWriteSessionConfig}s.
 *
 * <p>There are several strategies which can be used to upload a {@link Blob} to Google Cloud
 * Storage. This class provides factories which allow you to select the appropriate strategy for
 * your workload.
 *
 * <table>
 *   <caption>Comparison of Strategies</caption>
 *   <tr>
 *     <th>Strategy</th>
 *     <th>Factory Method(s)</th>
 *     <th>Description</th>
 *     <th>Retry Support</th>
 *     <th>Transports Supported</th>
 *     <th>Cloud Storage API used</th>
 *     <th>Considerations</th>
 *   </tr>
 *   <tr>
 *     <td>Default (Chunk based upload)</td>
 *     <td>{@link #getDefault()}</td>
 *     <td>
 *       Buffer up to a configurable amount of bytes in memory, write to Cloud Storage when
 *       full or close. Buffer size is configurable via
 *       {@link DefaultBlobWriteSessionConfig#withChunkSize(int)}
 *     </td>
 *     <td>
 *       Each chunk is retried up to the limitations specified in
 *       {@link StorageOptions#getRetrySettings()}
 *     </td>
 *     <td>gRPC</td>
 *     <td><a href="https://cloud.google.com/storage/docs/resumable-uploads">Resumable Upload</a></td>
 *     <td>The network will only be used for the following operations:
 *     <ol>
 *       <li>Creating the Resumable Upload Session</li>
 *       <li>Transmitting zero or more incremental chunks</li>
 *       <li>Transmitting the final chunk and finalizing the Resumable Upload Session</li>
 *       <li>
 *         If any of the above are interrupted with a retryable error, the Resumable Upload Session
 *         will be queried to reconcile client side state with Cloud Storage
 *       </li>
 *     </ol>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>Buffer to disk then upload</td>
 *     <td>
 *       <ul>
 *         <li>{@link #bufferToDiskThenUpload(Path)}</li>
 *         <li>{@link #bufferToDiskThenUpload(Collection) bufferToDiskThenUpload(Collection&lt;Path>)}</li>
 *         <li>{@link #bufferToTempDirThenUpload()}</li>
 *       </ul>
 *     </td>
 *     <td>
 *       Buffer bytes to a temporary file on disk. On {@link WritableByteChannel#close() close()}
 *       upload the entire files contents to Cloud Storage. Delete the temporary file.
 *     </td>
 *     <td>
 *       Upload the file in the fewest number of RPC possible retrying within the limitations
 *       specified in {@link StorageOptions#getRetrySettings()}
 *     </td>
 *     <td>gRPC</td>
 *     <td><a href="https://cloud.google.com/storage/docs/resumable-uploads">Resumable Upload</a></td>
 *     <td>
 *       <ol>
 *         <li>A Resumable Upload Session will be used to upload the file on disk.</li>
 *         <li>
 *           If the upload is interrupted with a retryable error, the Resumable Upload Session will
 *           be queried to restart the upload from Cloud Storage's last received byte
 *         </li>
 *       </ol>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>Journal to disk while uploading</td>
 *     <td>{@link #journaling(Collection) journaling(Collection&lt;Path>)}</td>
 *     <td>
 *       Create a Resumable Upload Session, before transmitting bytes to Cloud Storage write
 *       to a recovery file on disk. If the stream to Cloud Storage is interrupted with a
 *       retryable error query the offset of the Resumable Upload Session, then open the recovery
 *       file from the offset and transmit the bytes to Cloud Storage.
 *     </td>
 *     <td>gRPC</td>
 *     <td><a href="https://cloud.google.com/storage/docs/resumable-uploads">Resumable Upload</a></td>
 *     <td>
 *       <ol>
 *         <li>
 *           The stream to Cloud Storage will be held open until a) the write is complete
 *           b) the stream is interrupted
 *         </li>
 *         <li>
 *           Because the bytes are journaled to disk, the upload to Cloud Storage can only
 *           be as fast as the disk.
 *         </li>
 *         <li>
 *           The use of <a href="https://cloud.google.com/compute/docs/disks/local-ssd#nvme">Compute
 *           Engine Local NVMe SSD</a> is strongly encouraged compared to Compute Engine Persistent
 *           Disk.
 *         </li>
 *       </ol>
 *     </td>
 *   </tr>
 * </table>
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
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Cloud Storage.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
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
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Cloud Storage.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static BufferToDiskThenUpload bufferToDiskThenUpload(Path path) throws IOException {
    return bufferToDiskThenUpload(ImmutableList.of(path));
  }

  /**
   * Create a new {@link BlobWriteSessionConfig} which will first buffer the content of the object
   * to a temporary file under one of the specified {@code paths}.
   *
   * <p>Once the file on disk is closed, the entire file will then be uploaded to Cloud Storage.
   *
   * <p>The specifics of how the work is spread across multiple paths is undefined and subject to
   * change.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static BufferToDiskThenUpload bufferToDiskThenUpload(Collection<Path> paths)
      throws IOException {
    return new BufferToDiskThenUpload(ImmutableList.copyOf(paths), false);
  }

  /**
   * Create a new {@link BlobWriteSessionConfig} which will journal writes to a temporary file under
   * one of the specified {@code paths} before transmitting the bytes to Cloud Storage.
   *
   * <p>The specifics of how the work is spread across multiple paths is undefined and subject to
   * change.
   *
   * @see Storage#blobWriteSession(BlobInfo, BlobWriteOption...)
   * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
   * @since 2.27.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public static JournalingBlobWriteSessionConfig journaling(Collection<Path> paths) {
    return new JournalingBlobWriteSessionConfig(ImmutableList.copyOf(paths), false);
  }
}
