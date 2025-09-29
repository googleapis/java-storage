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
import java.security.NoSuchAlgorithmException;

@BetaApi
@InternalExtensionOnly
public abstract class MultipartUploadClient {

  MultipartUploadClient() {}

  public abstract CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException;

  public abstract UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException, NoSuchAlgorithmException;

  public abstract CompleteMultipartUploadResponse completeMultipartUpload(
      CompleteMultipartUploadRequest request)
      throws NoSuchAlgorithmException, IOException;

  public abstract AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request)
      throws IOException, NoSuchAlgorithmException;

  public abstract ListPartsResponse listParts(ListPartsRequest listPartsRequest) throws IOException;

  public static MultipartUploadClient create(MultipartUploadSettings config) {
    HttpStorageOptions options = config.getOptions();
    return new MultipartUploadClientImpl(
        URI.create(options.getHost()),
        options.getStorageRpcV1().getStorage().getRequestFactory(),
        options.createRetrier(),
        options);
  }
}
