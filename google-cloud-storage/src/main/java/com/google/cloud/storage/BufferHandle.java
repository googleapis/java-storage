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

import com.google.common.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Sometimes, we need a handle to create a buffer but do not want to unnecessarily allocate it. This
 * class can be though of as an enriched {@code Supplier<ByteBuffer>} that still implements the
 * common stateful methods of {@link ByteBuffer} without allocating the ByteBuffer until necessary.
 *
 * <p>{@link ByteBuffer} is a sealed class hierarchy, meaning we can't simply extend it to provide
 * laziness without this new class.
 */
abstract class BufferHandle implements Supplier<ByteBuffer> {

  @VisibleForTesting
  BufferHandle() {}

  abstract int remaining();

  abstract int capacity();

  abstract int position();

  static BufferHandle allocateAligned(int alignmentMultiple, int size) {
    int actualSize = alignSize(alignmentMultiple, size);
    return allocate(actualSize);
  }

  static BufferHandle allocate(int capacity) {
    return new LazyBufferHandle(capacity, ByteBuffer::allocate);
  }

  static BufferHandle handleOf(ByteBuffer buf) {
    return new EagerBufferHandle(buf);
  }

  /**
   * Give {@code size} "snap" it to the next {@code alignmentMultiple} that is >= {@code size}.
   *
   * <p>i.e. Given 344k size, 256k alignmentMultiple expect 512k
   */
  private static int alignSize(int alignmentMultiple, int size) {
    int actualSize = size;
    if (size < alignmentMultiple) {
      actualSize = alignmentMultiple;
    } else if (size % alignmentMultiple != 0) {
      // TODO: this mod will cause two divisions to happen
      //   * try and measure how expensive two divisions is compared to one
      //   * also measure the case where size is a multiple, and how much the
      //     following calculation costs

      // add almost another full alignmentMultiple to the size
      // then integer divide it before multiplying it by the alignmentMultiple
      actualSize = (size + alignmentMultiple - 1) / alignmentMultiple * alignmentMultiple;
    } // else size is already aligned
    return actualSize;
  }

  static final class LazyBufferHandle extends BufferHandle {

    private final int capacity;
    private final Function<Integer, ByteBuffer> factory;

    private volatile ByteBuffer buf;

    @VisibleForTesting
    LazyBufferHandle(int capacity, Function<Integer, ByteBuffer> factory) {
      this.capacity = capacity;
      this.factory = factory;
    }

    @Override
    int remaining() {
      return buf == null ? capacity() : buf.remaining();
    }

    @Override
    int capacity() {
      return buf == null ? capacity : buf.capacity();
    }

    @Override
    int position() {
      return buf == null ? 0 : buf.position();
    }

    @Override
    public ByteBuffer get() {
      if (buf == null) {
        synchronized (this) {
          if (buf == null) {
            buf = factory.apply(capacity);
          }
        }
      }
      return buf;
    }
  }

  static final class EagerBufferHandle extends BufferHandle {
    private final ByteBuffer buf;

    private EagerBufferHandle(ByteBuffer buf) {
      this.buf = buf;
    }

    @Override
    int remaining() {
      return buf.remaining();
    }

    @Override
    int capacity() {
      return buf.capacity();
    }

    @Override
    int position() {
      return buf.position();
    }

    @Override
    public ByteBuffer get() {
      return buf;
    }
  }
}
