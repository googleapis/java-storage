package com.google.cloud.storage;

import static com.google.cloud.storage.TestUtils.getChecksummedData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.cloud.storage.GrpcStorageOptions.ReadObjectResponseZeroCopyMessageMarshaller;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class ZeroCopyMarshallerTest {
  private final byte[] bytes = DataGenerator.base64Characters().genBytes(40);
  private final ByteString data = ByteString.copyFrom(bytes, 0, 10);
  private final ReadObjectResponse RESPONSE =
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
    InputStream stream = createInputStream(RESPONSE.toByteArray(), true);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    ReadObjectResponse response = marshaller.parse(stream);
    assertEquals(response, RESPONSE);
    ResponseContentLifecycleHandle stream2 = marshaller.get(response);
    assertNotNull(stream2);
    stream2.close();
    ResponseContentLifecycleHandle stream3 = marshaller.get(response);
    assertNotNull(stream3);
    stream3.close();
  }

  @Test
  public void testParseOnSlowPath() throws IOException {
    InputStream stream = createInputStream(RESPONSE.toByteArray(), false);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    ReadObjectResponse response = marshaller.parse(stream);
    assertEquals(response, RESPONSE);
    ResponseContentLifecycleHandle stream2 = marshaller.get(response);
    assertNotNull(stream2);
    stream2.close();
  }

  @Test
  public void testParseBrokenMessageOnFastPath() {
    InputStream stream = createInputStream(dropLastOneByte(RESPONSE.toByteArray()), true);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    assertThrows(
        StatusRuntimeException.class,
        () -> {
          marshaller.parse(stream);
        });
  }

  @Test
  public void testParseBrokenMessageOnSlowPath() {
    InputStream stream = createInputStream(dropLastOneByte(RESPONSE.toByteArray()), false);
    ReadObjectResponseZeroCopyMessageMarshaller marshaller = createMarshaller();
    assertThrows(
        StatusRuntimeException.class,
        () -> {
          marshaller.parse(stream);
        });
  }

  @Test
  public void testResponseContentLifecycleHandle() throws IOException {
    System.out.println(GrpcStorageOptions.ZeroCopyReadinessChecker.isReady());
    AtomicBoolean wasClosedCalled = new AtomicBoolean(false);
    Closeable verifyClosed = () -> wasClosedCalled.set(true);

    ResponseContentLifecycleHandle handle =
        new ResponseContentLifecycleHandle(RESPONSE, verifyClosed);
    handle.close();

    assertTrue(wasClosedCalled.get());

    ResponseContentLifecycleHandle nullHandle = new ResponseContentLifecycleHandle(RESPONSE, null);
    nullHandle.close();
    // No NullPointerException means test passes
  }
}
