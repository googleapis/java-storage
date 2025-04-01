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

import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
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
      new BlobAppendableUploadConfig(FlushPolicy.minFlushSize(_256KiB), Hasher.enabled());

  private final FlushPolicy flushPolicy;
  private final Hasher hasher;

  private BlobAppendableUploadConfig(FlushPolicy flushPolicy, Hasher hasher) {
    this.flushPolicy = flushPolicy;
    this.hasher = hasher;
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
    return new BlobAppendableUploadConfig(flushPolicy, hasher);
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
    return new BlobAppendableUploadConfig(flushPolicy, enabled ? Hasher.enabled() : Hasher.noop());
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
   * }</pre>
   *
   * @since 2.51.0 This new api is in preview and is subject to breaking changes.
   * @see FlushPolicy#minFlushSize(int)
   */
  @BetaApi
  public static BlobAppendableUploadConfig of() {
    return INSTANCE;
  }
}
