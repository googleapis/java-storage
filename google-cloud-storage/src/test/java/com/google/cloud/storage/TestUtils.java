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

import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ErrorDetails;
import com.google.cloud.RetryHelper;
import com.google.cloud.RetryHelper.RetryHelperException;
import com.google.cloud.http.BaseHttpServiceException;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.rpc.DebugInfo;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.WriteObjectRequest;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.buffer.ByteBufUtil;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.function.ThrowingRunnable;
import org.junit.runners.model.MultipleFailureException;

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
    StatusRuntimeException statusRuntimeException =
        code.toStatus().withDescription(message).asRuntimeException();
    DebugInfo debugInfo = DebugInfo.newBuilder().setDetail(message).build();
    ErrorDetails errorDetails =
        ErrorDetails.builder().setRawErrorMessages(ImmutableList.of(Any.pack(debugInfo))).build();
    return ApiExceptionFactory.createException(
        statusRuntimeException, GrpcStatusCode.of(code), true, errorDetails);
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

  static RetryingDependencies defaultRetryingDeps() {
    return new RetryingDependencies() {
      @Override
      public RetrySettings getRetrySettings() {
        return StorageOptions.getDefaultRetrySettings();
      }

      @Override
      public ApiClock getClock() {
        return NanoClock.getDefaultClock();
      }
    };
  }

  /**
   * Search {@code t} for an instance of {@code T} either directly or via a cause
   *
   * @return The found instance of T or null if not found.
   */
  @Nullable
  public static <T extends Throwable> T findThrowable(Class<T> c, Throwable t) {
    T found = null;
    Throwable tmp = t;
    while (tmp != null) {
      if (c.isInstance(tmp)) {
        found = c.cast(tmp);
        break;
      } else {
        tmp = tmp.getCause();
      }
    }
    return found;
  }

  public static <T> T retry429s(Callable<T> c, Storage storage) {
    try {
      return RetryHelper.runWithRetries(
          c,
          storage.getOptions().getRetrySettings(),
          new BasicResultRetryAlgorithm<Object>() {
            @Override
            public boolean shouldRetry(Throwable previousThrowable, Object previousResponse) {
              if (previousThrowable instanceof BaseHttpServiceException) {
                BaseHttpServiceException httpException =
                    (BaseHttpServiceException) previousThrowable;
                return httpException.getCode() == 429;
              }
              return false;
            }
          },
          storage.getOptions().getClock());
    } catch (RetryHelperException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      } else {
        throw e;
      }
    }
  }

  /**
   * Return a function which when provided an {@code uploadId} will create a {@link
   * WriteObjectRequest} with that {@code uploadId}
   */
  @NonNull
  public static Function<String, WriteObjectRequest> onlyUploadId() {
    return uId -> WriteObjectRequest.newBuilder().setUploadId(uId).build();
  }

  public static byte[] snapshotData(ByteBuffer buf) {
    ByteBuffer dup = buf.duplicate();
    dup.flip();
    byte[] bytes = new byte[dup.remaining()];
    dup.get(bytes);
    return bytes;
  }

  public static byte[] slice(byte[] bs, int begin, int end) {
    int len = end - begin;
    byte[] dst = new byte[len];
    System.arraycopy(bs, begin, dst, 0, len);
    return dst;
  }

  public static String xxd(byte[] bytes) {
    return ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(bytes));
  }

  public static String xxd(ByteBuffer bytes) {
    return xxd(true, bytes);
  }

  public static String xxd(ByteString bytes) {
    return xxd(false, bytes.asReadOnlyByteBuffer());
  }

  public static String xxd(boolean flip, ByteBuffer bytes) {
    ByteBuffer dup = bytes.duplicate();
    if (flip) dup.flip();
    return ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(dup));
  }

  public static String xxd(boolean flip, ByteBuffer[] buffers) {
    ByteBuffer[] dups =
        Arrays.stream(buffers)
            .map(ByteBuffer::duplicate)
            .peek(
                byteBuffer -> {
                  if (flip) byteBuffer.flip();
                })
            .toArray(ByteBuffer[]::new);
    return ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(dups));
  }

  public static void assertAll(ThrowingRunnable... trs) throws Exception {
    List<Throwable> x =
        Arrays.stream(trs)
            .map(
                tr -> {
                  try {
                    tr.run();
                    return null;
                  } catch (Throwable e) {
                    return e;
                  }
                })
            .filter(Objects::nonNull)
            .collect(ImmutableList.toImmutableList());
    MultipleFailureException.assertEmpty(x);
  }

  /** ImmutableMap does not allow null values, this method does */
  public static Map<@NonNull String, @Nullable String> hashMapOf(
      @NonNull String k1, @Nullable String v1) {
    requireNonNull(k1, "k1 must be non null");
    HashMap<String, String> map = new HashMap<>();
    map.put(k1, v1);
    return Collections.unmodifiableMap(map);
  }

  /** ImmutableMap does not allow null values, this method does */
  public static Map<@NonNull String, @Nullable String> hashMapOf(
      @NonNull String k1, @Nullable String v1, @NonNull String k2, @Nullable String v2) {
    requireNonNull(k1, "k1 must be non null");
    requireNonNull(k2, "k2 must be non null");
    HashMap<String, String> map = new HashMap<>();
    map.put(k1, v1);
    map.put(k2, v2);
    return Collections.unmodifiableMap(map);
  }

  // copied with minor modification from
  // com.google.api.gax.grpc.InstantiatingGrpcChannelProvider#isOnComputeEngine
  public static boolean isOnComputeEngine() {
    String osName = System.getProperty("os.name");
    if ("Linux".equals(osName)) {
      try {
        String result =
            Files.asCharSource(new File("/sys/class/dmi/id/product_name"), StandardCharsets.UTF_8)
                .readFirstLine();
        return result != null && (result.contains("Google") || result.contains("Compute Engine"));
      } catch (IOException ignored) {
        return false;
      }
    }
    return false;
  }

  /**
   * GRPC test methods will sometimes use all bucket fields, which can cause an error if any aren't
   * supported by GRPC. todo: b/308194853
   */
  public static Storage.BucketField[] filterOutHttpOnlyBucketFields(Storage.BucketField[] fields) {
    return Arrays.stream(fields)
        .filter(f -> !f.equals(Storage.BucketField.OBJECT_RETENTION))
        .collect(ImmutableSet.toImmutableSet())
        .toArray(new Storage.BucketField[0]);
  }

  public static <T> Optional<T> last(List<T> l) {
    if (l.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(l.get(l.size() - 1));
    }
  }
}
