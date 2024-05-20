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

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public final class BuffersTest {

  @Test
  public void copy() {
    SecureRandom rand = new SecureRandom();
    ByteBuffer content = DataGenerator.rand(rand).genByteBuffer(2048);

    ByteBuffer[] bufs = {
      ByteBuffer.allocate(1),
      ByteBuffer.allocate(2),
      ByteBuffer.allocate(4),
      ByteBuffer.allocate(8),
      ByteBuffer.allocate(27),
    };

    long copy = 0;
    for (long read = 0; content.hasRemaining() && (read = Buffers.copy(content, bufs)) != -1; ) {
      for (ByteBuffer buf : bufs) {
        if (!buf.hasRemaining()) {
          buf.clear();
        }
      }
      copy += read;
    }
    assertThat(copy).isEqualTo(2048);
  }

  @Test
  public void allocateAligned_nonDivisible_capacityGtAlignment() {
    ByteBuffer b1 = Buffers.allocateAligned(3, 2);
    assertThat(b1.capacity()).isEqualTo(4);
  }

  @Test
  public void allocateAligned_nonDivisible_capacityLtAlignment() {
    ByteBuffer b1 = Buffers.allocateAligned(1, 2);
    assertThat(b1.capacity()).isEqualTo(2);
  }

  @Test
  public void allocateAligned_evenlyDivisible_capacityLtAlignment() {
    ByteBuffer b1 = Buffers.allocateAligned(2, 4);
    assertThat(b1.capacity()).isEqualTo(4);
  }

  @Test
  public void allocateAligned_evenlyDivisible_capacityGtAlignment() {
    ByteBuffer b1 = Buffers.allocateAligned(8, 4);
    assertThat(b1.capacity()).isEqualTo(8);
  }

  @Test
  public void fillFrom_handles_0SizeRead_someBytesRead() throws Exception {
    byte[] bytes = new byte[14];
    ByteBuffer buf = ByteBuffer.wrap(bytes);

    byte[] expected =
        new byte[] {
          (byte) 'A',
          (byte) 'B',
          (byte) 'C',
          (byte) 'A',
          (byte) 'B',
          (byte) 'A',
          (byte) 'A',
          (byte) 'A',
          (byte) 'B',
          (byte) 'A',
          (byte) 'B',
          (byte) 'C',
          (byte) 0,
          (byte) 0
        };

    int[] acceptSequence = new int[] {3, 2, 1, 0, 0, 1, 2, 3};
    AtomicInteger readCount = new AtomicInteger(0);

    ReadableByteChannel c =
        new ReadableByteChannel() {
          @Override
          public int read(ByteBuffer dst) throws IOException {
            int i = readCount.getAndIncrement();
            if (i == acceptSequence.length) {
              return -1;
            }
            int bytesToRead = acceptSequence[i];
            if (bytesToRead > 0) {
              long copy =
                  Buffers.copy(DataGenerator.base64Characters().genByteBuffer(bytesToRead), dst);
              assertThat(copy).isEqualTo(bytesToRead);
            }

            return bytesToRead;
          }

          @Override
          public boolean isOpen() {
            return true;
          }

          @Override
          public void close() throws IOException {}
        };
    int filled = Buffers.fillFrom(buf, c);

    assertAll(
        () -> assertThat(filled).isEqualTo(12),
        () -> assertThat(xxd(bytes)).isEqualTo(xxd(expected)));
  }

  @Test
  public void fillFrom_handles_0SizeRead_noBytesRead() throws Exception {
    ByteBuffer buf = ByteBuffer.allocate(3);

    ReadableByteChannel c =
        new ReadableByteChannel() {
          @Override
          public int read(ByteBuffer dst) throws IOException {
            return -1;
          }

          @Override
          public boolean isOpen() {
            return true;
          }

          @Override
          public void close() throws IOException {}
        };
    int filled = Buffers.fillFrom(buf, c);

    assertThat(filled).isEqualTo(-1);
  }
}
