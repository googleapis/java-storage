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

import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

final class ResponseContentLifecycleHandle implements Closeable {
  @Nullable private final Closeable dispose;

  private final List<ByteBuffer> buffers;
  private final AtomicBoolean open;
  private final AtomicInteger refs;

  private ResponseContentLifecycleHandle(List<ByteBuffer> buffers, @Nullable Closeable dispose) {
    this.dispose = dispose;
    this.buffers = buffers;
    this.open = new AtomicBoolean(true);
    this.refs = new AtomicInteger(1);
  }

  static <Response> ResponseContentLifecycleHandle create(
      Response response,
      Function<Response, List<ByteBuffer>> toBuffersFunction,
      @Nullable Closeable dispose) {
    List<ByteBuffer> buffers = toBuffersFunction.apply(response);
    return new ResponseContentLifecycleHandle(buffers, dispose);
  }

  ChildRef borrow() {
    Preconditions.checkState(open.get(), "only able to borrow when open");
    ChildRef childRef = new ChildRef();
    refs.incrementAndGet();
    return childRef;
  }

  void copy(ReadCursor c, ByteBuffer[] dsts, int offset, int length) {
    for (ByteBuffer b : buffers) {
      long copiedBytes = Buffers.copy(b, dsts, offset, length);
      c.advance(copiedBytes);
      if (b.hasRemaining()) break;
    }
  }

  boolean hasRemaining() {
    for (ByteBuffer b : buffers) {
      if (b.hasRemaining()) return true;
    }
    return false;
  }

  @Override
  public void close() throws IOException {
    if (open.getAndSet(false)) {
      int newCount = refs.decrementAndGet();
      if (newCount == 0) {
        dispose();
      }
    }
  }

  private void dispose() throws IOException {
    if (dispose != null) {
      dispose.close();
    }
  }

  final class ChildRef implements Closeable {

    @Override
    public void close() throws IOException {
      int newCount = refs.decrementAndGet();
      if (newCount == 0) {
        ResponseContentLifecycleHandle.this.dispose();
      }
    }
  }
}
