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

import static com.google.cloud.storage.BackwardCompatibilityUtils.grpcCodeToHttpStatusCode;
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
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class StorageExceptionGrpcCompatibilityTest {

  private final ApiException x;

  public StorageExceptionGrpcCompatibilityTest(Code ignore, ApiException x) {
    this.x = x;
  }

  @Test
  public void testCoalesce() {
    GrpcStatusCode grpcStatusCode = (GrpcStatusCode) x.getStatusCode();
    int expectedCode = grpcCodeToHttpStatusCode(grpcStatusCode.getTransportCode());

    BaseServiceException ex = StorageException.coalesce(x);
    assertThat(ex.getCode()).isEqualTo(expectedCode);
    assertThat(ex.getReason()).isEqualTo(x.getReason());
    assertThat(ex.getMessage()).contains(x.getErrorDetails().getDebugInfo().getDetail());
    assertThat(ex).hasCauseThat().isEqualTo(x);
  }

  @Parameters(name = "{0}")
  public static List<Object[]> args() {
    return Arrays.stream(Code.values())
        .map(Code::toStatus)
        .filter(s -> !s.isOk())
        .map(
            status -> {
              Code code = status.getCode();

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
              return new Object[] {
                code, ApiExceptionFactory.createException(cause, statusCode, false, errorDetails)
              };
            })
        .collect(ImmutableList.toImmutableList());
  }
}
