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
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.Storage.BlobWriteOption;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;

/**
 * Interface representing those methods which can be used to write to and interact with an
 * appendable upload.
 *
 * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
 */
@BetaApi
@InternalExtensionOnly
public interface BlobAppendableUpload extends BlobWriteSession {

  /**
   * Open the {@link AppendableUploadWriteableByteChannel AppendableUploadWriteableByteChannel} for
   * this session.
   *
   * <p>A session may only be {@code open}ed once. If multiple calls to open are made, an illegal
   * state exception will be thrown
   *
   * <p>The returned {@code AppendableUploadWriteableByteChannel} can throw IOExceptions from any of
   * its usual methods. Any {@link IOException} thrown can have a cause of a {@link
   * StorageException}. However, not all {@code IOExceptions} will have {@code StorageException}s.
   *
   * @throws IOException When creating the {@link AppendableUploadWriteableByteChannel} if an
   *     unrecoverable underlying IOException occurs it can be rethrown
   * @throws IllegalStateException if open is called more than once
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @Override
  AppendableUploadWriteableByteChannel open() throws IOException;

  /**
   * Return an {@link ApiFuture}{@code <BlobInfo>} which will represent the state of the object in
   * Google Cloud Storage.
   *
   * <p>This future will not resolve until:
   *
   * <ol>
   *   <li>The object is successfully finalized in Google Cloud Storage by calling {@link
   *       AppendableUploadWriteableByteChannel#finalizeAndClose()
   *       AppendableUploadWriteableByteChannel#finalizeAndClose()}
   *   <li>This session is detached from the upload without finalizing by calling {@link
   *       AppendableUploadWriteableByteChannel#closeWithoutFinalizing()
   *       AppendableUploadWriteableByteChannel#closeWithoutFinalizing()}
   *   <li>The session is closed by calling {@link AppendableUploadWriteableByteChannel#close()
   *       AppendableUploadWriteableByteChannel#close()}
   *   <li>A terminal failure occurs, the terminal failure will become the exception result
   * </ol>
   *
   * <p><i>NOTICE:</i> Some fields may not be populated unless finalization has completed.
   *
   * <p>If a terminal failure is encountered, calling either {@link ApiFuture#get()} or {@link
   * ApiFuture#get(long, TimeUnit)} will result in an {@link
   * java.util.concurrent.ExecutionException} with the cause.
   *
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @Override
  ApiFuture<BlobInfo> getResult();

  /**
   * The {@link WritableByteChannel} returned from {@link BlobAppendableUpload#open()}.
   *
   * <p>This interface allows writing bytes to an Appendable Upload, and provides methods to close
   * this channel -- optionally finalizing the upload.
   *
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  @InternalExtensionOnly
  interface AppendableUploadWriteableByteChannel extends WritableByteChannel {

    /**
     * Finalize the upload and close this instance to further {@link #write(ByteBuffer)}ing. This
     * will close any underlying stream and release any releasable resources once out of scope.
     *
     * <p>Once this method is called, and returns no more writes to the object will be allowed by
     * GCS.
     *
     * <p>This method and {@link #close()} are mutually exclusive. If one of the other methods are
     * called before this method, this method will be a no-op.
     *
     * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
     * @see BlobAppendableUploadConfig.CloseAction#FINALIZE_WHEN_CLOSING
     * @see BlobAppendableUploadConfig#getCloseAction()
     * @see BlobAppendableUploadConfig#withCloseAction(CloseAction)
     * @since 2.51.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    void finalizeAndClose() throws IOException;

    /**
     * Close this instance to further {@link #write(ByteBuffer)}ing without finalizing the upload.
     * This will close any underlying stream and release any releasable resources once out of scope.
     *
     * <p>This method, {@link AppendableUploadWriteableByteChannel#finalizeAndClose()} and {@link
     * AppendableUploadWriteableByteChannel#close()} are mutually exclusive. If one of the other
     * methods are called before this method, this method will be a no-op.
     *
     * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
     * @see BlobAppendableUploadConfig.CloseAction#CLOSE_WITHOUT_FINALIZING
     * @see BlobAppendableUploadConfig#getCloseAction()
     * @see BlobAppendableUploadConfig#withCloseAction(CloseAction)
     * @since 2.51.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    void closeWithoutFinalizing() throws IOException;

    /**
     * Close this instance to further {@link #write(ByteBuffer)}ing.
     *
     * <p>Whether the upload is finalized during this depends on the {@link
     * BlobAppendableUploadConfig#getCloseAction()} provided to create the {@link
     * BlobAppendableUpload}. If {@link BlobAppendableUploadConfig#getCloseAction()}{@code ==
     * }{@link CloseAction#FINALIZE_WHEN_CLOSING}, {@link #finalizeAndClose()} will be called. If
     * {@link BlobAppendableUploadConfig#getCloseAction()}{@code == }{@link
     * CloseAction#CLOSE_WITHOUT_FINALIZING}, {@link #closeWithoutFinalizing()} will be called.
     *
     * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
     * @see BlobAppendableUploadConfig#getCloseAction()
     * @see BlobAppendableUploadConfig#withCloseAction(CloseAction)
     * @since 2.51.0 This new api is in preview and is subject to breaking changes.
     */
    @BetaApi
    void close() throws IOException;
  }
}
