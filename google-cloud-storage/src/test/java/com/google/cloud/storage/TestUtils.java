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
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.rpc.DebugInfo;
import com.google.storage.v2.ChecksummedData;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

public final class TestUtils {

  private TestUtils() {}

  public static byte[] gzipBytes(byte[] bytes) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (OutputStream out = new GZIPOutputStream(byteArrayOutputStream)) {
      out.write(bytes);
    } catch (IOException ignore) {
      // GZIPOutputStream will only throw if the underlying stream throws.
      // ByteArrayOutputStream does not throw on write
    }

    return byteArrayOutputStream.toByteArray();
  }

  public static ChecksummedData getChecksummedData(ByteString content) {
    return ChecksummedData.newBuilder().setContent(content).build();
  }

  public static ChecksummedData getChecksummedData(ByteString content, Hasher hasher) {
    ChecksummedData.Builder b = ChecksummedData.newBuilder().setContent(content);
    Crc32cLengthKnown hash = hasher.hash(content.asReadOnlyByteBuffer());
    if (hash != null) {
      int crc32c = hash.getValue();
      b.setCrc32C(crc32c);
    }
    return b.build();
  }

  public static ApiException apiException(Code code) {
    return apiException(code, "");
  }

  public static ApiException apiException(Code code, String message) {
    StatusRuntimeException statusRuntimeException = code.toStatus().asRuntimeException();
    DebugInfo debugInfo =
        DebugInfo.newBuilder().setDetail("forced failure |~| " + code.name() + message).build();
    ErrorDetails errorDetails =
        ErrorDetails.builder().setRawErrorMessages(ImmutableList.of(Any.pack(debugInfo))).build();
    return ApiExceptionFactory.createException(
        statusRuntimeException, GrpcStatusCode.of(code), true, errorDetails);
  }

  public static GrpcCallContext contextWithRetryForCodes(StatusCode.Code... code) {
    return GrpcCallContext.createDefault().withRetryableCodes(ImmutableSet.copyOf(code));
  }

  public static ImmutableList<ByteBuffer> subDivide(byte[] bytes, int division) {
    int length = bytes.length;
    int fullDivisions = length / division;
    int x = division * fullDivisions;
    int remaining = length - x;

    if ((fullDivisions == 1 && remaining == 0) || (fullDivisions == 0 && remaining == 1)) {
      return ImmutableList.of(ByteBuffer.wrap(bytes));
    } else {
      return Stream.of(
              IntStream.iterate(0, i -> i + division)
                  .limit(fullDivisions)
                  .mapToObj(i -> ByteBuffer.wrap(bytes, i, division)),
              Stream.of(ByteBuffer.wrap(bytes, x, remaining)))
          .flatMap(Function.identity())
          .filter(Buffer::hasRemaining)
          .collect(ImmutableList.toImmutableList());
    }
  }
}
