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

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ErrorDetails;
import com.google.api.gax.rpc.StatusCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.rpc.DebugInfo;
import com.google.storage.v2.ChecksummedData;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public final class TestUtils {

  private TestUtils() {}

  public static byte[] gzipBytes(byte[] bytes) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (OutputStream out = new GZIPOutputStream(byteArrayOutputStream)) {
      out.write(bytes);
    } catch (IOException ignore) {
    }

    return byteArrayOutputStream.toByteArray();
  }

  public static ChecksummedData getChecksummedData(ByteString content) {
    int crc32c = Hashing.crc32c().hashBytes(content.asReadOnlyByteBuffer()).asInt();
    return ChecksummedData.newBuilder().setContent(content).setCrc32C(crc32c).build();
  }

  public static ApiException apiException(Code code) {
    StatusRuntimeException statusRuntimeException = code.toStatus().asRuntimeException();
    DebugInfo debugInfo =
        DebugInfo.newBuilder().setDetail("forced failure |~| " + code.name()).build();
    ErrorDetails errorDetails =
        ErrorDetails.builder().setRawErrorMessages(ImmutableList.of(Any.pack(debugInfo))).build();
    return ApiExceptionFactory.createException(
        statusRuntimeException, GrpcStatusCode.of(code), true, errorDetails);
  }

  public static GrpcCallContext contextWithRetryForCodes(StatusCode.Code... code) {
    return GrpcCallContext.createDefault().withRetryableCodes(ImmutableSet.copyOf(code));
  }
}
