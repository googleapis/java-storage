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

import com.google.cloud.storage.multipartupload.model.CompleteMultipartRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface MultipartUpload {

  CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException;

  UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException;

  CompleteMultipartResponse completeMultipartUpload(CompleteMultipartRequest request)
      throws NoSuchAlgorithmException, IOException;
}
