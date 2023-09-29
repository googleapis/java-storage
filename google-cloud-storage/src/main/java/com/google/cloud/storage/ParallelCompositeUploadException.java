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
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ErrorDetails;
import com.google.api.gax.rpc.StatusCode;
import com.google.common.collect.ImmutableList;
import io.grpc.Status.Code;

final class ParallelCompositeUploadException extends ApiException {

  private final ApiFuture<ImmutableList<BlobId>> createdObjects;

  private ParallelCompositeUploadException(
      Throwable cause,
      StatusCode statusCode,
      ErrorDetails errorDetails,
      ApiFuture<ImmutableList<BlobId>> createdObjects) {
    super(cause, statusCode, false, errorDetails);
    this.createdObjects = createdObjects;
  }

  public ApiFuture<ImmutableList<BlobId>> getCreatedObjects() {
    return createdObjects;
  }

  static ParallelCompositeUploadException of(
      Throwable t, ApiFuture<ImmutableList<BlobId>> createdObjects) {
    StatusCode statusCode;
    ErrorDetails errorDetails;

    Throwable cause = t;
    if (t instanceof StorageException && t.getCause() != null) {
      cause = t.getCause();
    }

    if (cause instanceof ApiException) {
      ApiException apiException = (ApiException) cause;
      statusCode = apiException.getStatusCode();
      errorDetails = apiException.getErrorDetails();
    } else {
      statusCode = GrpcStatusCode.of(Code.UNKNOWN);
      errorDetails = ErrorDetails.builder().build();
    }
    return new ParallelCompositeUploadException(cause, statusCode, errorDetails, createdObjects);
  }
}
