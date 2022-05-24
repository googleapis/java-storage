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

import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

final class DefaultBufferedWritableByteChannel implements BufferedWritableByteChannel {

  private final ByteBuffer buffer;

  private final UnbufferedWritableByteChannel channel;

  DefaultBufferedWritableByteChannel(ByteBuffer buffer, UnbufferedWritableByteChannel channel) {
    this.buffer = buffer;
    this.channel = channel;
  }

  @Override
  public boolean isComplete() {
    return channel.isComplete();
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    if (!channel.isOpen()) {
      throw new ClosedChannelException();
    }
    int bytesConsumed = 0;

    while (src.hasRemaining()) {
      int bufferRemaining = buffer.remaining();
      int srcRemaining = src.remaining();

      final int tmpBytesConsumed;
      if (!enqueuedBytes() && bufferRemaining == srcRemaining) {
        channel.write(src);
        tmpBytesConsumed = bufferRemaining;
      } else if (!enqueuedBytes() && bufferRemaining <= srcRemaining) {
        // no enqueued data and the src provided is larger than our buffer
        // rather than copying into the buffer, simply flush a slice from the source
        ByteBuffer slice = src.slice();
        Buffers.limit(slice, bufferRemaining);
        int write = channel.write(slice);
        Buffers.position(src, src.position() + write);
        tmpBytesConsumed = bufferRemaining;
      } else {
        if (bufferRemaining <= srcRemaining) {
          // some enqueued bytes in the buffer, fill the remaining capacity of buffer
          // before yielding
          ByteBuffer slice = src.slice();
          Buffers.limit(slice, bufferRemaining);
          buffer.put(slice);
          Buffers.position(src, src.position() + bufferRemaining);
          tmpBytesConsumed = bufferRemaining;
        } else {
          // the available in src is less than buffers remaining capacity, enqueue it in full
          buffer.put(src);
          tmpBytesConsumed = srcRemaining;
        }
        maybeFlushBuffer();
      }

      bytesConsumed += tmpBytesConsumed;
    }

    return bytesConsumed;
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  @Override
  public void close() throws IOException {
    try (UnbufferedWritableByteChannel ignored = channel) {
      flush();
    }
  }

  @Override
  public void flush() throws IOException {
    if (enqueuedBytes()) {
      ByteBuffer b = buffer.duplicate();
      Buffers.position(b, 0);
      Buffers.limit(b, buffer.position());
      channel.write(b);
      Buffers.position(buffer, 0);
    }
  }

  private void maybeFlushBuffer() throws IOException {
    if (enqueuedBytes() && !buffer.hasRemaining()) {
      flush();
    }
  }

  private boolean enqueuedBytes() {
    return buffer.position() > 0;
  }
}
