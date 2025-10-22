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

import com.google.api.core.BetaApi;
import com.google.api.core.InternalExtensionOnly;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import java.io.IOException;
import java.net.URI;

/**
 * A client for interacting with Google Cloud Storage's Multipart Upload API.
 *
 * <p>This class is for internal use only and is not intended for public consumption. It provides a
 * low-level interface for creating and managing multipart uploads.
 *
 * @see <a href="https://cloud.google.com/storage/docs/multipart-uploads">Multipart Uploads</a>
 */
@BetaApi
@InternalExtensionOnly
public abstract class MultipartUploadClient {

  MultipartUploadClient() {}

  /**
   * Creates a new multipart upload.
   *
   * @param request The request object containing the details for creating the multipart upload.
   * @return A {@link CreateMultipartUploadResponse} object containing the upload ID.
   * @throws IOException if an I/O error occurs.
   */
  @BetaApi
  public abstract CreateMultipartUploadResponse createMultipartUpload(
      CreateMultipartUploadRequest request) throws IOException;

  /**
   * Lists the parts that have been uploaded for a specific multipart upload.
   *
   * @param listPartsRequest The request object containing the details for listing the parts.
   * @return A {@link ListPartsResponse} object containing the list of parts.
   * @throws IOException if an I/O error occurs.
   */
  @BetaApi
  public abstract ListPartsResponse listParts(ListPartsRequest listPartsRequest) throws IOException;

  /**
   * Creates a new instance of {@link MultipartUploadClient}.
   *
   * @param config The configuration for the client.
   * @return A new {@link MultipartUploadClient} instance.
   */
  @BetaApi
  public static MultipartUploadClient create(MultipartUploadSettings config) {
    HttpStorageOptions options = config.getOptions();
    return new MultipartUploadClientImpl(
        URI.create(options.getHost()),
        options.getStorageRpcV1().getStorage().getRequestFactory(),
        options.createRetrier(),
        options);
  }
}
