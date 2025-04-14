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

import static com.google.cloud.storage.ByteSizeConstants._256KiB;
import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiFutures;
import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.rpc.AbortedException;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadImpl.AppendableObjectBufferedWritableByteChannel;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.Object;
import javax.annotation.concurrent.Immutable;

/**
 * Configuration parameters for an appendable uploads channel.
 *
 * <p>Instances of this class are immutable and thread safe.
 *
 * @see Storage#blobAppendableUpload(BlobInfo, BlobAppendableUploadConfig, BlobWriteOption...)
 * @since 2.51.0 This new api is in preview and is subject to breaking changes.
 */
@Immutable
@BetaApi
@TransportCompatibility({Transport.GRPC})
public final class BlobAppendableUploadConfig {

  private static final BlobAppendableUploadConfig INSTANCE =
      new BlobAppendableUploadConfig(
          FlushPolicy.minFlushSize(_256KiB),
          Hasher.enabled(),
          CloseAction.CLOSE_WITHOUT_FINALIZING);

  private final FlushPolicy flushPolicy;
  private final Hasher hasher;
  private final CloseAction closeAction;

  private BlobAppendableUploadConfig(
      FlushPolicy flushPolicy, Hasher hasher, CloseAction closeAction) {
    this.flushPolicy = flushPolicy;
    this.hasher = hasher;
    this.closeAction = closeAction;
  }

  /**
   * The {@link FlushPolicy} which will be used to determine when and how many bytes to flush to
   * GCS.
   *
   * <p><i>Default:</i> {@link FlushPolicy#minFlushSize(int) FlushPolicy.minFlushSize(256 * 1024)}
   *
   * @see #withFlushPolicy(FlushPolicy)
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public FlushPolicy getFlushPolicy() {
    return flushPolicy;
  }

  /**
   * Return an instance with the {@code FlushPolicy} set to be the specified value.
   *
   * <p><i>Default:</i> {@link FlushPolicy#minFlushSize(int) FlushPolicy.minFlushSize(256 * 1024)}
   *
   * @see #getFlushPolicy()
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public BlobAppendableUploadConfig withFlushPolicy(FlushPolicy flushPolicy) {
    requireNonNull(flushPolicy, "flushPolicy must be non null");
    if (this.flushPolicy.equals(flushPolicy)) {
      return this;
    }
    return new BlobAppendableUploadConfig(flushPolicy, hasher, closeAction);
  }

  /**
   * The {@link CloseAction} which will dictate the behavior of {@link
   * AppendableUploadWriteableByteChannel#close()}.
   *
   * <p><i>Default:</i> {@link CloseAction#CLOSE_WITHOUT_FINALIZING}
   *
   * @see #withCloseAction(CloseAction)
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public CloseAction getCloseAction() {
    return closeAction;
  }

  /**
   * Return an instance with the {@code CloseAction} set to be the specified value. <i>Default:</i>
   * {@link CloseAction#CLOSE_WITHOUT_FINALIZING}
   *
   * @see #getCloseAction()
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public BlobAppendableUploadConfig withCloseAction(CloseAction closeAction) {
    requireNonNull(closeAction, "closeAction must be non null");
    if (this.closeAction == closeAction) {
      return this;
    }
    return new BlobAppendableUploadConfig(flushPolicy, hasher, closeAction);
  }

  /**
   * Whether crc32c validation will be performed for bytes returned by Google Cloud Storage
   *
   * <p><i>Default:</i> {@code true}
   *
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  boolean getCrc32cValidationEnabled() {
    return Hasher.enabled().equals(hasher);
  }

  /**
   * Return an instance with crc32c validation enabled based on {@code enabled}.
   *
   * <p><i>Default:</i> {@code true}
   *
   * @param enabled Whether crc32c validation will be performed for bytes returned by Google Cloud
   *     Storage
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  BlobAppendableUploadConfig withCrc32cValidationEnabled(boolean enabled) {
    if (enabled && Hasher.enabled().equals(hasher)) {
      return this;
    } else if (!enabled && Hasher.noop().equals(hasher)) {
      return this;
    }
    return new BlobAppendableUploadConfig(
        flushPolicy, enabled ? Hasher.enabled() : Hasher.noop(), closeAction);
  }

  /** Never to be made public until {@link Hasher} is public */
  @InternalApi
  Hasher getHasher() {
    return hasher;
  }

  /**
   * Default instance factory method.
   *
   * <p>The {@link FlushPolicy} of this instance is equivalent to the following:
   *
   * <pre>{@code
   * BlobAppendableUploadConfig.of()
   *   .withFlushPolicy(FlushPolicy.minFlushSize(256 * 1024))
   *   .withCloseAction(CloseAction.CLOSE_WITHOUT_FINALIZING)
   * }</pre>
   *
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   * @see FlushPolicy#minFlushSize(int)
   */
  @BetaApi
  public static BlobAppendableUploadConfig of() {
    return INSTANCE;
  }

  /**
   * Enum providing the possible actions which can be taken during the {@link
   * AppendableUploadWriteableByteChannel#close()} call.
   *
   * @see AppendableUploadWriteableByteChannel#close()
   * @see BlobAppendableUploadConfig#withCloseAction(CloseAction)
   * @see BlobAppendableUploadConfig#getCloseAction()
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  public enum CloseAction {
    /**
     * Designate that when {@link AppendableUploadWriteableByteChannel#close()} is called, the
     * appendable upload should be finalized.
     *
     * @since 2.51.0 This new api is in preview and is subject to breaking changes.
     * @see AppendableUploadWriteableByteChannel#finalizeAndClose()
     */
    @BetaApi
    FINALIZE_WHEN_CLOSING,
    /**
     * Designate that when {@link AppendableUploadWriteableByteChannel#close()} is called, the
     * appendable upload should NOT be finalized, allowing for takeover by another session or
     * client.
     *
     * @since 2.51.0 This new api is in preview and is subject to breaking changes.
     * @see AppendableUploadWriteableByteChannel#closeWithoutFinalizing()
     */
    @BetaApi
    CLOSE_WITHOUT_FINALIZING
  }

  BlobAppendableUpload create(GrpcStorageImpl storage, BlobInfo info, Opts<ObjectTargetOpt> opts) {
    boolean takeOver = info.getGeneration() != null;
    BidiWriteObjectRequest req =
        takeOver
            ? storage.getBidiWriteObjectRequestForTakeover(info, opts)
            : storage.getBidiWriteObjectRequest(info, opts);

    BidiAppendableWrite baw = new BidiAppendableWrite(req, takeOver);

    WritableByteChannelSession<AppendableObjectBufferedWritableByteChannel, BidiWriteObjectResponse>
        build =
            ResumableMedia.gapic()
                .write()
                .bidiByteChannel(storage.storageClient.bidiWriteObjectCallable())
                .setHasher(this.getHasher())
                .setByteStringStrategy(ByteStringStrategy.copy())
                .appendable()
                .withRetryConfig(
                    storage.retrier.withAlg(
                        new BasicResultRetryAlgorithm<Object>() {
                          @Override
                          public boolean shouldRetry(
                              Throwable previousThrowable, Object previousResponse) {
                            // TODO: remove this later once the redirects are not handled by the
                            // retry loop
                            ApiException apiEx = null;
                            if (previousThrowable instanceof StorageException) {
                              StorageException se = (StorageException) previousThrowable;
                              Throwable cause = se.getCause();
                              if (cause instanceof ApiException) {
                                apiEx = (ApiException) cause;
                              }
                            }
                            if (apiEx instanceof AbortedException) {
                              return true;
                            }
                            return storage
                                .retryAlgorithmManager
                                .idempotent()
                                .shouldRetry(previousThrowable, null);
                          }
                        }))
                .buffered(this.getFlushPolicy())
                .setStartAsync(ApiFutures.immediateFuture(baw))
                .setGetCallable(storage.storageClient.getObjectCallable())
                .setFinalizeOnClose(this.closeAction == CloseAction.FINALIZE_WHEN_CLOSING)
                .build();

    return new BlobAppendableUploadImpl(
        new DefaultBlobWriteSessionConfig.DecoratedWritableByteChannelSession<>(
            build, BidiBlobWriteSessionConfig.Factory.WRITE_OBJECT_RESPONSE_BLOB_INFO_DECODER));
  }
}
