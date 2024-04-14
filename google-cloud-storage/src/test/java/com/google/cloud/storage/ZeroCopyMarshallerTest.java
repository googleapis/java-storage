/*
 * Copyright 2024 Google LLC
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

import static com.google.cloud.storage.TestUtils.getChecksummedData;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.cloud.storage.GrpcStorageOptions.ReadObjectResponseZeroCopyMessageMarshaller;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ContentRange;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.ReadObjectResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.ReadableBuffer;
import io.grpc.internal.ReadableBuffers;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.junit.Test;

public class ZeroCopyMarshallerTest {
  private final byte[] bytes = DataGenerator.base64Characters().genBytes(40);
  private final ByteString data = ByteString.copyFrom(bytes, 0, 10);
  private final ReadObjectResponse response =
      ReadObjectResponse.newBuilder()
          .setMetadata(
              Object.newBuilder()
                  .setName("name")
                  .setGeneration(3L)
                  .setContentType("application/octet-stream")
                  .build())
          .setContentRange(ContentRange.newBuilder().setStart(0).build())
          .setObjectChecksums(
              ObjectChecksums.newBuilder().setCrc32C(Hashing.crc32c().hashBytes(bytes).asInt()))
          .setChecksummedData(getChecksummedData(data, Hasher.enabled()))
          .build();

  private ReadObjectResponseZeroCopyMessageMarshaller createMarshaller() {
    return new ReadObjectResponseZeroCopyMessageMarshaller(ReadObjectResponse.getDefaultInstance());
  }

  private byte[] dropLastOneByte(byte[] bytes) {
    return Arrays.copyOfRange(bytes, 0, bytes.length - 1);
  }

  private InputStream createInputStream(byte[] bytes, boolean isZeroCopyable) {
    ReadableBuffer buffer =
        isZeroCopyable ? ReadableBuffers.wrap(ByteBuffer.wrap(bytes)) : ReadableBuffers.wrap(bytes);
    return ReadableBuffers.openStream(buffer, true);
  }

  @Test
  public void testParseOnFastPath() throws IOException {
    InputStream stream = createInputStream(response.toByteArray(), true);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    ReadObjectResponse response = marshaller.parse(stream);
    assertEquals(response, this.response);
    ResponseContentLifecycleHandle stream2 = marshaller.get(response);
    assertNotNull(stream2);
    stream2.close();
    ResponseContentLifecycleHandle stream3 = marshaller.get(response);
    assertNotNull(stream3);
    stream3.close();
  }

  @Test
  public void testParseOnSlowPath() throws IOException {
    InputStream stream = createInputStream(response.toByteArray(), false);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    ReadObjectResponse response = marshaller.parse(stream);
    assertEquals(response, this.response);
    ResponseContentLifecycleHandle stream2 = marshaller.get(response);
    assertNotNull(stream2);
    stream2.close();
  }

  @Test
  public void testParseBrokenMessageOnFastPath() {
    InputStream stream = createInputStream(dropLastOneByte(response.toByteArray()), true);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    assertThrows(
        StatusRuntimeException.class,
        () -> {
          marshaller.parse(stream);
        });
  }

  @Test
  public void testParseBrokenMessageOnSlowPath() {
    InputStream stream = createInputStream(dropLastOneByte(response.toByteArray()), false);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    assertThrows(
        StatusRuntimeException.class,
        () -> {
          marshaller.parse(stream);
        });
  }

  @Test
  public void testResponseContentLifecycleHandle() throws IOException {
    AtomicBoolean wasClosedCalled = new AtomicBoolean(false);
    Closeable verifyClosed = () -> wasClosedCalled.set(true);

    ResponseContentLifecycleHandle handle =
        new ResponseContentLifecycleHandle(response, verifyClosed);
    handle.close();

    assertTrue(wasClosedCalled.get());

    ResponseContentLifecycleHandle nullHandle = new ResponseContentLifecycleHandle(response, null);
    nullHandle.close();
    // No NullPointerException means test passes
  }

  @Test
  public void testMarshallerClose_clean() throws IOException {
    CloseAuditingInputStream stream1 =
        CloseAuditingInputStream.of(createInputStream(response.toByteArray(), true));
    CloseAuditingInputStream stream2 =
        CloseAuditingInputStream.of(createInputStream(response.toByteArray(), true));
    CloseAuditingInputStream stream3 =
        CloseAuditingInputStream.of(createInputStream(response.toByteArray(), true));

    ReadObjectResponseZeroCopyMessageMarshaller.closeAllStreams(
        ImmutableList.of(stream1, stream2, stream3));

    assertThat(stream1.closed).isTrue();
    assertThat(stream2.closed).isTrue();
    assertThat(stream3.closed).isTrue();
  }

  @SuppressWarnings("resource")
  @Test
  public void testMarshallerClose_multipleIoExceptions() {
    CloseAuditingInputStream stream1 =
        new CloseAuditingInputStream(null) {
          @Override
          void onClose() throws IOException {
            throw new IOException("Kaboom stream1");
          }
        };
    CloseAuditingInputStream stream2 =
        new CloseAuditingInputStream(null) {
          @Override
          void onClose() throws IOException {
            throw new IOException("Kaboom stream2");
          }
        };
    CloseAuditingInputStream stream3 =
        new CloseAuditingInputStream(null) {
          @Override
          void onClose() throws IOException {
            throw new IOException("Kaboom stream3");
          }
        };

    IOException ioException =
        assertThrows(
            IOException.class,
            () ->
                ReadObjectResponseZeroCopyMessageMarshaller.closeAllStreams(
                    ImmutableList.of(stream1, stream2, stream3)));

    assertThat(stream1.closed).isTrue();
    assertThat(stream2.closed).isTrue();
    assertThat(stream3.closed).isTrue();

    assertThat(ioException).hasMessageThat().isEqualTo("Kaboom stream1");
    List<String> messages =
        Arrays.stream(ioException.getSuppressed())
            .map(Throwable::getMessage)
            .collect(Collectors.toList());
    assertThat(messages).isEqualTo(ImmutableList.of("Kaboom stream2", "Kaboom stream3"));
  }

  private static class CloseAuditingInputStream extends FilterInputStream {

    private boolean closed = false;

    private CloseAuditingInputStream(InputStream in) {
      super(in);
    }

    public static CloseAuditingInputStream of(InputStream in) {
      return new CloseAuditingInputStream(in);
    }

    @Override
    public void close() throws IOException {
      closed = true;
      onClose();
      super.close();
    }

    void onClose() throws IOException {}
  }
}
