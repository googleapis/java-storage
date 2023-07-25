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

import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * A session to write an object to Google Cloud Storage.
 *
 * <p>A session can only write a single version of an object. If writing multiple versions of an
 * object a new session must be created each time.
 *
 * <p>Provides an api that allows writing to and retrieving the resulting {@link BlobInfo} after
 * write finalization.
 *
 * <p>The underlying implementation is dictated based upon the specified {@link
 * BlobWriteSessionConfig} provided at {@link StorageOptions} creation time.
 *
 * @see GrpcStorageOptions.Builder#setBlobWriteSessionConfig(BlobWriteSessionConfig)
 * @see BlobWriteSessionConfig
 * @see BlobWriteSessionConfigs
 * @since 2.26.0 This new api is in preview and is subject to breaking changes.
 */
@BetaApi
public interface BlobWriteSession {

  /**
   * Open the {@link WritableByteChannel} for this session.
   *
   * <p>A session may only be {@code open}ed once. If multiple calls to open are made, an illegal
   * state exception will be thrown
   *
   * <p>Upon calling {@link WritableByteChannel#close()} the object creation will be finalized, and
   * {@link #getResult()}s future should resolve.
   *
   * @throws IOException When creating the {@link WritableByteChannel} if an unrecoverable
   *     underlying IOException occurs it can be rethrown
   * @throws IllegalStateException if open is called more than once
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  WritableByteChannel open() throws IOException;

  /**
   * Return an {@link ApiFuture}{@code <BlobInfo>} which will represent the state of the object upon
   * finalization and success response from Google Cloud Storage.
   *
   * <p>This future will not resolve until: 1. The object is successfully finalized and created in
   * Google Cloud Storage 2. A terminal failure occurs, the terminal failure will become the
   * exception result
   *
   * @since 2.26.0 This new api is in preview and is subject to breaking changes.
   */
  @BetaApi
  ApiFuture<BlobInfo> getResult();
}
