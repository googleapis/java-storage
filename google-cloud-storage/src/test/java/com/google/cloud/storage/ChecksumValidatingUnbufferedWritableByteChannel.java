/*
 * Copyright 2025 Google LLC
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
import static org.junit.Assert.assertThrows;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;

public final class ChecksumValidatingUnbufferedWritableByteChannel {

  private static final ChecksummedTestContent ALL_CONTENT =
      ChecksummedTestContent.of(DataGenerator.base64Characters().genBytes(64));
  private static final List<ChecksummedTestContent> CHUNKS = ALL_CONTENT.chunkup(8);

  static {
    assertThat(CHUNKS).hasSize(8);
  }

  @Test
  public void dataLossErrorRaised() throws IOException {
    SettableApiFuture<Crc32cLengthKnown> crc32cGetter = SettableApiFuture.create();
    UnbufferedWritableByteChannel channel =
        StorageByteChannels.writable()
            .validateUploadCrc32c(
                new UnbufferedWritableByteChannel() {
                  long totalBytes = 0;

                  @Override
                  public long write(ByteBuffer[] srcs, int offset, int length) {
                    long total = 0;
                    for (ByteBuffer buffer : srcs) {
                      int remaining = buffer.remaining();
                      total += remaining;
                      buffer.position(buffer.position() + remaining);
                    }
                    totalBytes += total;
                    return total;
                  }

                  @Override
                  public boolean isOpen() {
                    return true;
                  }

                  @Override
                  public void close() {
                    crc32cGetter.set(Crc32cValue.of(373, totalBytes));
                  }
                },
                crc32cGetter);

    ChecksummedTestContent allContent =
        ChecksummedTestContent.of(DataGenerator.base64Characters().genBytes(64));
    List<ChecksummedTestContent> chunks = allContent.chunkup(8);
    assertThat(chunks).hasSize(8);
    channel.write(ByteBuffer.wrap(chunks.get(0).getBytes()));
    channel.write(
        new ByteBuffer[] {
          ByteBuffer.wrap(chunks.get(1).getBytes()), ByteBuffer.wrap(chunks.get(2).getBytes()),
        });

    ClientDetectedDataLossException error =
        assertThrows(
            ClientDetectedDataLossException.class,
            () ->
                channel.writeAndClose(
                    new ByteBuffer[] {
                      ByteBuffer.wrap(chunks.get(3).getBytes()),
                      ByteBuffer.wrap(chunks.get(4).getBytes()),
                    }));

    assertThat(error).hasMessageThat().contains("expected");
    assertThat(error).hasMessageThat().contains("actual");
  }

  @Test
  @SuppressWarnings("UnstableApiUsage")
  public void validChecksumClosesCleanly()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    SettableApiFuture<Crc32cLengthKnown> crc32cGetter = SettableApiFuture.create();
    UnbufferedWritableByteChannel channel =
        StorageByteChannels.writable()
            .validateUploadCrc32c(
                new UnbufferedWritableByteChannel() {
                  long totalBytes = 0;
                  final Hasher crc32c = Hashing.crc32c().newHasher();

                  @Override
                  public long write(ByteBuffer[] srcs, int offset, int length) {
                    long total = 0;
                    for (ByteBuffer buffer : srcs) {
                      int remaining = buffer.remaining();
                      total += remaining;
                      crc32c.putBytes(buffer);
                    }
                    totalBytes += total;
                    return total;
                  }

                  @Override
                  public boolean isOpen() {
                    return true;
                  }

                  @Override
                  public void close() {
                    crc32cGetter.set(Crc32cValue.of(crc32c.hash().asInt(), totalBytes));
                  }
                },
                crc32cGetter);

    channel.write(ByteBuffer.wrap(CHUNKS.get(0).getBytes()));
    channel.write(
        new ByteBuffer[] {
          ByteBuffer.wrap(CHUNKS.get(1).getBytes()),
          ByteBuffer.wrap(CHUNKS.get(2).getBytes()),
          ByteBuffer.wrap(CHUNKS.get(3).getBytes()),
          ByteBuffer.wrap(CHUNKS.get(4).getBytes()),
          ByteBuffer.wrap(CHUNKS.get(5).getBytes()),
          ByteBuffer.wrap(CHUNKS.get(6).getBytes()),
        });

    channel.writeAndClose(ByteBuffer.wrap(CHUNKS.get(7).getBytes()));

    Crc32cLengthKnown actual = crc32cGetter.get(2, TimeUnit.SECONDS);
    Crc32cLengthKnown expected =
        Crc32cValue.of(ALL_CONTENT.getCrc32c(), ALL_CONTENT.getBytes().length);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void partiallyConsumedBufferIsAccurate_write_thenClose()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    doTestPartialConsume(
        (buffer, channel) -> {
          int written = channel.write(buffer);
          assertThat(written).isEqualTo(3);
          channel.close();
        });
  }

  @Test
  public void partiallyConsumedBufferIsAccurate_writeAndClose()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    doTestPartialConsume(
        (buffer, channel) -> {
          int written = channel.writeAndClose(buffer);
          assertThat(written).isEqualTo(3);
        });
  }

  @Test
  public void partiallyConsumedBufferIsAccurate_writeArrayAndClose()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    doTestPartialConsume(
        (buffer, channel) -> {
          long written = channel.writeAndClose(new ByteBuffer[] {buffer});
          assertThat(written).isEqualTo(3);
        });
  }

  @Test
  public void partiallyConsumedBufferIsAccurate_writeArray_thenClose()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    doTestPartialConsume(
        (buffer, channel) -> {
          long written = channel.write(new ByteBuffer[] {buffer});
          assertThat(written).isEqualTo(3);
          channel.close();
        });
  }

  private static void doTestPartialConsume(IOConsumer<UnbufferedWritableByteChannel> c)
      throws IOException, InterruptedException, ExecutionException, TimeoutException {
    SettableApiFuture<Crc32cLengthKnown> crc32cGetter = SettableApiFuture.create();
    UnbufferedWritableByteChannel channel =
        StorageByteChannels.writable()
            .validateUploadCrc32c(
                new UnbufferedWritableByteChannel() {
                  Hasher crc32c = Hashing.crc32c().newHasher();

                  @Override
                  public long write(ByteBuffer[] srcs, int offset, int length) {
                    ByteBuffer buffer = srcs[0];
                    ByteBuffer slice = buffer.slice();
                    slice.limit(3);
                    crc32c.putBytes(slice);
                    buffer.position(3);
                    return 3;
                  }

                  @Override
                  public boolean isOpen() {
                    return true;
                  }

                  @Override
                  public void close() {
                    crc32cGetter.set(Crc32cValue.of(crc32c.hash().asInt(), 3));
                  }
                },
                crc32cGetter);

    c.accept(ByteBuffer.wrap(CHUNKS.get(0).getBytes()), channel);

    Crc32cLengthKnown actual = crc32cGetter.get(2, TimeUnit.SECONDS);
    ChecksummedTestContent testContent = ALL_CONTENT.slice(0, 3);
    Crc32cLengthKnown expected =
        Crc32cValue.of(testContent.getCrc32c(), testContent.getBytes().length);
    assertThat(actual).isEqualTo(expected);
  }

  @FunctionalInterface
  interface IOConsumer<T> {
    void accept(ByteBuffer buffer, T t) throws IOException;
  }
}
