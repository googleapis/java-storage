/*
 * Copyright 2025 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import com.google.api.core.InternalExtensionOnly;
import com.google.cloud.storage.Storage.BlobWriteOption;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Interface representing those methods which can be used to write to and interact with an
 * appendable upload.
 *
 * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
 */
@BetaApi
@InternalExtensionOnly
public interface BlobAppendableUpload extends AutoCloseable, WritableByteChannel {

  /**
   * Write some bytes to the appendable session. Whether a flush happens will depend on how many
   * bytes have been written prior, how many bytes are being written now and what {@link
   * BlobAppendableUploadConfig} was provided when creating the {@link BlobAppendableUpload}.
   *
   * <p>This method can block the invoking thread in order to ensure written bytes are acknowledged
   * by Google Cloud Storage.
   *
   * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
   */
  @Override
  int write(ByteBuffer src) throws IOException;

  /**
   * Close this instance to further {@link #write(ByteBuffer)}ing. This will close any underlying
   * stream and release any releasable resources once out of scope.
   *
   * <p>{@link #finalizeUpload()} can be called after this method, but it will not carry any bytes
   * with it.
   */
  @Override
  void close() throws IOException;

  /**
   * Finalize the appendable upload, close any underlying stream and release any releasable
   * resources once out of scope.
   *
   * <p>Once this method is called, and returns no more writes to the object will be allowed by GCS.
   */
  @BetaApi
  ApiFuture<BlobInfo> finalizeUpload() throws IOException;
}
