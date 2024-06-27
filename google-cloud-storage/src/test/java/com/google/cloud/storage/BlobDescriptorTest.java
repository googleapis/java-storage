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

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.storage.BlobDescriptorImpl.OutstandingReadToArray;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;
import com.google.storage.v2.ReadRange;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.Test;

public final class BlobDescriptorTest {

  @Test
  public void outstandingReadToArray_happyPath()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    byte[] genBytes = DataGenerator.base64Characters().genBytes(137);
    ByteString byteString = UnsafeByteOperations.unsafeWrap(genBytes);
    AtomicBoolean closed = new AtomicBoolean(false);
    Closeable close = () -> closed.set(true);
    ResponseContentLifecycleHandle handle =
        ResponseContentLifecycleHandle.create(
            byteString, ByteString::asReadOnlyByteBufferList, close);
    ChildRef childRef = handle.borrow();
    handle.close();

    SettableApiFuture<byte[]> complete = SettableApiFuture.create();
    OutstandingReadToArray outstandingReadToArray = new OutstandingReadToArray(1, 0, 137, complete);

    outstandingReadToArray.accept(childRef, byteString);
    outstandingReadToArray.eof();

    String expectedBytes = xxd(genBytes);

    byte[] actualFutureBytes = complete.get(1, TimeUnit.SECONDS);
    assertThat(xxd(actualFutureBytes)).isEqualTo(expectedBytes);
    assertThat(closed.get()).isTrue();
  }

  @Test
  public void outstandingReadToArray_childRef_close_ioException_propagated() throws IOException {
    byte[] genBytes = DataGenerator.base64Characters().genBytes(137);
    ByteString byteString = UnsafeByteOperations.unsafeWrap(genBytes);
    Closeable throwOnClose =
        () -> {
          throw new IOException(new Kaboom());
        };
    ResponseContentLifecycleHandle handle =
        ResponseContentLifecycleHandle.create(
            byteString, ByteString::asReadOnlyByteBufferList, throwOnClose);
    ChildRef childRef = handle.borrow();
    handle.close();

    SettableApiFuture<byte[]> complete = SettableApiFuture.create();
    OutstandingReadToArray outstandingReadToArray = new OutstandingReadToArray(1, 0, 137, complete);

    IOException ioException =
        assertThrows(IOException.class, () -> outstandingReadToArray.accept(childRef, byteString));
    assertThat(ioException).hasCauseThat().isInstanceOf(Kaboom.class);
  }

  @Test
  public void outstandingReadToArray_producesAnAccurateReadRange()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    SettableApiFuture<byte[]> complete = SettableApiFuture.create();
    int readId = 1;
    OutstandingReadToArray read = new OutstandingReadToArray(readId, 0, 137, complete);

    ReadRange readRange1 = read.makeReadRange();
    ReadRange expectedReadRange1 =
        ReadRange.newBuilder().setReadId(readId).setReadOffset(0).setReadLength(137).build();
    assertThat(readRange1).isEqualTo(expectedReadRange1);

    try (ResponseContentLifecycleHandle handle = noopContentHandle()) {
      ByteString bytes = ByteString.copyFrom(DataGenerator.base64Characters().genBytes(64));
      read.accept(handle.borrow(), bytes);
    }

    ReadRange readRange2 = read.makeReadRange();
    ReadRange expectedReadRange2 =
        ReadRange.newBuilder().setReadId(readId).setReadOffset(64).setReadLength(73).build();
    assertThat(readRange2).isEqualTo(expectedReadRange2);

    try (ResponseContentLifecycleHandle handle = noopContentHandle()) {
      ByteString bytes = ByteString.copyFrom(DataGenerator.base64Characters().genBytes(64));
      read.accept(handle.borrow(), bytes);
    }

    ReadRange readRange3 = read.makeReadRange();
    ReadRange expectedReadRange3 =
        ReadRange.newBuilder().setReadId(readId).setReadOffset(128).setReadLength(9).build();
    assertThat(readRange3).isEqualTo(expectedReadRange3);

    try (ResponseContentLifecycleHandle handle = noopContentHandle()) {
      ByteString bytes = ByteString.copyFrom(DataGenerator.base64Characters().genBytes(9));
      read.accept(handle.borrow(), bytes);
      read.eof();
    }

    ReadRange readRange4 = read.makeReadRange();
    ReadRange expectedReadRange4 =
        ReadRange.newBuilder().setReadId(readId).setReadOffset(137).setReadLength(0).build();
    assertThat(readRange4).isEqualTo(expectedReadRange4);

    byte[] actualBytes = complete.get(1, TimeUnit.SECONDS);
    assertThat(xxd(actualBytes)).isEqualTo(xxd(DataGenerator.base64Characters().genBytes(137)));
  }

  private static ResponseContentLifecycleHandle noopContentHandle() {
    return ResponseContentLifecycleHandle.create(ImmutableList.of(), Function.identity(), () -> {});
  }

  private static final class Kaboom extends RuntimeException {
    private Kaboom() {
      super("Kaboom!!!");
    }
  }
}
