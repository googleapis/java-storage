/*
 * Copyright 2022 Google LLC
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

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ErrorDetails;
import com.google.cloud.BaseServiceException;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.rpc.DebugInfo;
import com.google.rpc.ErrorInfo;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import org.junit.Test;

public final class StorageExceptionGrpcCompatibilityTest {

  @Test
  public void testCoalesce_CANCELLED() {
    doTestCoalesce(0, Code.CANCELLED);
  }

  @Test
  public void testCoalesce_UNKNOWN() {
    doTestCoalesce(0, Code.UNKNOWN);
  }

  @Test
  public void testCoalesce_INVALID_ARGUMENT() {
    doTestCoalesce(400, Code.INVALID_ARGUMENT);
  }

  @Test
  public void testCoalesce_DEADLINE_EXCEEDED() {
    doTestCoalesce(504, Code.DEADLINE_EXCEEDED);
  }

  @Test
  public void testCoalesce_NOT_FOUND() {
    doTestCoalesce(404, Code.NOT_FOUND);
  }

  @Test
  public void testCoalesce_ALREADY_EXISTS() {
    doTestCoalesce(409, Code.ALREADY_EXISTS);
  }

  @Test
  public void testCoalesce_PERMISSION_DENIED() {
    doTestCoalesce(403, Code.PERMISSION_DENIED);
  }

  @Test
  public void testCoalesce_RESOURCE_EXHAUSTED() {
    doTestCoalesce(429, Code.RESOURCE_EXHAUSTED);
  }

  @Test
  public void testCoalesce_FAILED_PRECONDITION() {
    doTestCoalesce(412, Code.FAILED_PRECONDITION);
  }

  @Test
  public void testCoalesce_ABORTED() {
    doTestCoalesce(409, Code.ABORTED);
  }

  @Test
  public void testCoalesce_OUT_OF_RANGE() {
    doTestCoalesce(400, Code.OUT_OF_RANGE);
  }

  @Test
  public void testCoalesce_UNIMPLEMENTED() {
    doTestCoalesce(501, Code.UNIMPLEMENTED);
  }

  @Test
  public void testCoalesce_INTERNAL() {
    doTestCoalesce(500, Code.INTERNAL);
  }

  @Test
  public void testCoalesce_UNAVAILABLE() {
    doTestCoalesce(503, Code.UNAVAILABLE);
  }

  @Test
  public void testCoalesce_DATA_LOSS() {
    doTestCoalesce(400, Code.DATA_LOSS);
  }

  @Test
  public void testCoalesce_UNAUTHENTICATED() {
    doTestCoalesce(401, Code.UNAUTHENTICATED);
  }

  private void doTestCoalesce(int expectedCode, Code code) {
    Status status = code.toStatus();
    GrpcStatusCode statusCode = GrpcStatusCode.of(code);
    ErrorInfo errorInfo =
        ErrorInfo.newBuilder()
            .setReason("reason")
            .setDomain("global")
            .putMetadata("errors", "x")
            .build();

    DebugInfo debugInfo =
        DebugInfo.newBuilder()
            .setDetail(
                "bw-storage-dev-region-fine@default-223119.iam.gserviceaccount.com does not have storage.hmacKeys.list access to the Google Cloud project.")
            .build();

    ImmutableList<Any> anys = ImmutableList.of(Any.pack(errorInfo), Any.pack(debugInfo));
    ErrorDetails errorDetails = ErrorDetails.builder().setRawErrorMessages(anys).build();

    StatusRuntimeException cause =
        new StatusRuntimeException(status.withDescription(debugInfo.getDetail()));
    ApiException x = ApiExceptionFactory.createException(cause, statusCode, false, errorDetails);

    BaseServiceException ex = StorageException.coalesce(x);
    assertThat(ex.getCode()).isEqualTo(expectedCode);
    assertThat(ex.getReason()).isEqualTo(x.getReason());
    assertThat(ex.getMessage()).contains(x.getErrorDetails().getDebugInfo().getDetail());
    assertThat(ex).hasCauseThat().isEqualTo(x);
  }
}
