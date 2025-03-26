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

/**
 * Buffering {@link java.nio.channels.WritableByteChannel} which attempts to maximize the amount of
 * bytes written to the underlying {@link UnbufferedWritableByteChannel} while minimizing
 * unnecessary copying of said bytes.
 *
 * <p>Our flushing strategy is "eager", meaning as soon as we have enough bytes greater than or
 * equal to the capacity of our buffer we will write all bytes to the underlying channel.
 *
 * <p>A few strategies are employed to meet the stated goals.
 *
 * <ol>
 *   <li>If we do not have any bytes in our buffer and {@code src} is the same size as our buffer,
 *       simply {@link UnbufferedWritableByteChannel#write(ByteBuffer) write(src)} to the the
 *       underlying channel
 *   <li>If we do not have any bytes in our buffer and {@code src} is smaller than the size of our
 *       buffer, enqueue it in full
 *   <li>If we do have enqueued bytes and {@code src} is the size of our remaining buffer space
 *       {@link UnbufferedWritableByteChannel#write(ByteBuffer[]) write([buffer, src])} to the
 *       underlying channel
 *   <li>If we do have enqueued bytes and {@code src} is larger than the size of our remaining
 *       buffer space, take a slice of {@code src} the same size as the remaining space in our
 *       buffer and {@link UnbufferedWritableByteChannel#write(ByteBuffer[]) write([buffer, slice])}
 *       to the underlying channel before enqueuing any outstanding bytes which are smaller than our
 *       buffer.
 *   <li>If we do have enqueued bytes and {@code src} is smaller than our remaining buffer space,
 *       enqueue it in full
 * </ol>
 */
final class MinFlushBufferedWritableByteChannel implements BufferedWritableByteChannel {

  private final BufferHandle handle;

  private final UnbufferedWritableByteChannel channel;

  MinFlushBufferedWritableByteChannel(BufferHandle handle, UnbufferedWritableByteChannel channel) {
    this.handle = handle;
    this.channel = channel;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    if (!channel.isOpen()) {
      throw new ClosedChannelException();
    }
    int bytesConsumed = 0;

    while (Buffers.hasRemaining(src)) {
      int srcRemaining = Buffers.remaining(src);

      int bufferRemaining = handle.remaining();

      if (srcRemaining < bufferRemaining) {
        // srcRemaining is smaller than the remaining space in our buffer, enqueue it in full
        handle.get().put(src);
        bytesConsumed += srcRemaining;
        break;
      }

      int capacity = handle.capacity();
      int bufferPending = capacity - bufferRemaining;
      int totalPending = Math.addExact(srcRemaining, bufferPending);
      if (totalPending >= capacity) {
        ByteBuffer[] srcs;
        if (enqueuedBytes()) {
          ByteBuffer buffer = handle.get();
          Buffers.flip(buffer);
          srcs = new ByteBuffer[] {buffer, src};
        } else {
          srcs = new ByteBuffer[] {src};
        }
        long write = channel.write(srcs);
        if (enqueuedBytes()) {
          // we didn't write enough bytes to consume the whole buffer.
          Buffers.compact(handle.get());
        } else if (handle.position() == handle.capacity()) {
          // we wrote enough to consume the buffer
          Buffers.clear(handle.get());
        }
        int srcConsumed = Math.toIntExact(write) - bufferPending;
        bytesConsumed += srcConsumed;
      }
    }
    return bytesConsumed;
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  @Override
  public void close() throws IOException {
    if (enqueuedBytes()) {
      ByteBuffer buffer = handle.get();
      Buffers.flip(buffer);
      channel.writeAndClose(buffer);
      if (buffer.hasRemaining()) {
        buffer.compact();
      } else {
        Buffers.clear(buffer);
      }
    } else {
      channel.close();
    }
  }

  @Override
  public void flush() throws IOException {
    if (enqueuedBytes()) {
      ByteBuffer buffer = handle.get();
      Buffers.flip(buffer);
      channel.write(buffer);
      if (buffer.hasRemaining()) {
        buffer.compact();
      } else {
        Buffers.clear(buffer);
      }
    }
  }

  private boolean enqueuedBytes() {
    return handle.position() > 0;
  }
}
