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
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import java.io.IOException;
import java.net.URI;

/**
 * This class is an implementation of {@link MultipartUploadClient} that uses the Google Cloud
 * Storage XML API to perform multipart uploads.
 */
@BetaApi
final class MultipartUploadClientImpl extends MultipartUploadClient {

  private final MultipartUploadHttpRequestManager httpRequestManager;
  private final Retrier retrier;
  private final URI uri;

  MultipartUploadClientImpl(
      URI uri,
      Retrier retrier,
      MultipartUploadHttpRequestManager multipartUploadHttpRequestManager) {
    this.httpRequestManager = multipartUploadHttpRequestManager;
    this.retrier = retrier;
    this.uri = uri;
  }

  @Override
  @BetaApi
  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    return httpRequestManager.sendCreateMultipartUploadRequest(uri, request);
  }

  @Override
  @BetaApi
  public ListPartsResponse listParts(ListPartsRequest request) throws IOException {

    return retrier.run(
        Retrying.alwaysRetry(),
        () -> httpRequestManager.sendListPartsRequest(uri, request),
        Decoder.identity());
  }
}
