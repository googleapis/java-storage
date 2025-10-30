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
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is an implementation of {@link MultipartUploadClient} that uses the Google Cloud
 * Storage XML API to perform multipart uploads.
 */
@BetaApi
final class MultipartUploadClientImpl extends MultipartUploadClient {

  private final MultipartUploadHttpRequestManager httpRequestManager;
  private final Retrier retrier;
  private final URI uri;
  private final HttpRetryAlgorithmManager retryAlgorithmManager;

  MultipartUploadClientImpl(
      URI uri,
      Retrier retrier,
      MultipartUploadHttpRequestManager multipartUploadHttpRequestManager,
      HttpRetryAlgorithmManager retryAlgorithmManager) {
    this.httpRequestManager = multipartUploadHttpRequestManager;
    this.retrier = retrier;
    this.uri = uri;
    this.retryAlgorithmManager = retryAlgorithmManager;
  }

  @Override
  @BetaApi
  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    return httpRequestManager.sendCreateMultipartUploadRequest(uri, request);
  }

  @Override
  @BetaApi
  public ListPartsResponse listParts(ListPartsRequest request) {

    return retrier.run(
        retryAlgorithmManager.idempotent(),
        () -> httpRequestManager.sendListPartsRequest(uri, request),
        Decoder.identity());
  }

  @Override
  @BetaApi
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request) {
    return retrier.run(
        retryAlgorithmManager.idempotent(),
        () -> httpRequestManager.sendAbortMultipartUploadRequest(uri, request),
        Decoder.identity());
  }

  @Override
  public CompleteMultipartUploadResponse completeMultipartUpload(
      CompleteMultipartUploadRequest request) {
    return retrier.run(
        retryAlgorithmManager.idempotent(),
        () -> httpRequestManager.sendCompleteMultipartUploadRequest(uri, request),
        Decoder.identity());
  }

  @Override
  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody) {
    AtomicBoolean dirty = new AtomicBoolean(false);
    return retrier.run(
        retryAlgorithmManager.idempotent(),
        () -> {
          if (dirty.getAndSet(true)) {
            requestBody.getContent().rewindTo(0);
          }
          return httpRequestManager.sendUploadPartRequest(uri, request, requestBody.getContent());
        },
        Decoder.identity());
  }
}
