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

package com.google.cloud.storage.multipartuploader;

import com.google.cloud.storage.multipartuploader.data.CompleteMultipartRequest;
import com.google.cloud.storage.multipartuploader.data.CompleteMultipartResponse;
import com.google.cloud.storage.multipartuploader.data.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartuploader.data.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartuploader.data.RequestBody;
import com.google.cloud.storage.multipartuploader.data.UploadPartRequest;
import com.google.cloud.storage.multipartuploader.data.UploadPartResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.security.NoSuchAlgorithmException;

public interface MultipartUploader {

  CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException;

  UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException;

  CompleteMultipartResponse completeMultipartUpload(CompleteMultipartRequest request)
      throws NoSuchAlgorithmException;
}
