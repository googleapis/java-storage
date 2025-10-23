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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.core.BetaApi;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * This class is an implementation of {@link MultipartUploadClient} that uses the Google Cloud
 * Storage XML API to perform multipart uploads.
 */
@BetaApi
final class MultipartUploadClientImpl extends MultipartUploadClient {

  private final MultipartUploadHttpRequestManager httpRequestManager;
  private final HttpStorageOptions options;
  private final Retrier retrier;
  private final URI uri;

  MultipartUploadClientImpl(
      URI uri, HttpRequestFactory requestFactory, Retrier retrier, HttpStorageOptions options) {
    this.httpRequestManager =
        new MultipartUploadHttpRequestManager(requestFactory, new XmlObjectParser(new XmlMapper()));
    this.options = options;
    this.retrier = retrier;
    this.uri = uri;
  }

  @Override
  @BetaApi
  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    return httpRequestManager.sendCreateMultipartUploadRequest(uri, request, options);
  }

  @Override
  @BetaApi
  public ListPartsResponse listParts(ListPartsRequest request) throws IOException {

    return retrier.run(
        Retrying.alwaysRetry(),
        () -> httpRequestManager.sendListPartsRequest(uri, request, options),
        Decoder.identity());
  }

  @Override
  @BetaApi
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request)
      throws IOException, NoSuchAlgorithmException {

    return retrier.run(
        Retrying.alwaysRetry(),
        () -> httpRequestManager.sendAbortMultipartUploadRequest(uri, request, options),
        Decoder.identity());
  }
}
