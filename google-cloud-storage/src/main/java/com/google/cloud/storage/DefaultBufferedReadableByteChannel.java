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

import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

final class DefaultBufferedReadableByteChannel implements BufferedReadableByteChannel {

  private final ByteBuffer buffer;

  private final UnbufferedReadableByteChannel channel;

  DefaultBufferedReadableByteChannel(ByteBuffer buffer, UnbufferedReadableByteChannel channel) {
    this.buffer = buffer;
    this.channel = channel;
  }

  @Override
  public boolean isComplete() {
    return channel.isComplete();
  }

  long totalRead = 0;

  @Override
  public int read(ByteBuffer dst) throws IOException {
    if (!channel.isOpen()) {
      throw new ClosedChannelException();
    }

    int bytesConsumed = 0;

    while (dst.hasRemaining()) {
      int bufferRemaining = buffer.remaining();

      int dstRemaining = dst.remaining();
      int dstPosition = dst.position();

      final int tmpBytesCopied;
      if (enqueuedBytes()) {
        long copy = Buffers.copy(buffer, new ByteBuffer[] {dst});
        if (buffer.remaining() == 0) {
          Buffers.clear(buffer);
        }
        tmpBytesCopied = Math.toIntExact(copy);
      } else {
        if (bufferRemaining < dstRemaining) {
          // no enqueued data and the available space in dst is larger than our buffer
          // rather than copying into the buffer before copying to dst, simply read a buffer size
          // worth of bytes directly into dst
          ByteBuffer slice = dst.slice();
          int sliceLimit = dstPosition + bufferRemaining;
          Buffers.limit(slice, sliceLimit);
          int read = channel.read(slice);
          if (read == -1) {
            close();
            return -1;
          }
          Buffers.position(dst, dstPosition + read);
          tmpBytesCopied = read;
        } else if (bufferRemaining == dstRemaining) {
          // no enqueued data and the available space in dst is the same as our buffer
          // rather than copying into the buffer before copying to dst, simply read directly into
          // dst
          int read = channel.read(dst);
          if (read == -1) {
            close();
            return -1;
          }
          tmpBytesCopied = read;
        } else {

          // the amount of space remaining in dst is smaller than our buffer,
          // create a slice of our buffer such that
          // dst.remaning() + bufSlice.remaning() == buffer.capacity

          ByteBuffer slice = buffer.slice();
          int sliceCapacity = buffer.capacity() - dstRemaining;
          Buffers.limit(slice, sliceCapacity);

          ByteBuffer[] dsts = {dst, slice};
          long read = channel.read(dsts);
          if (read == -1) {
            close();
            return -1;
          } else if (read < dstRemaining) {
            // we didn't read enough bytes to fill up dst, no need to advance buffer position
            tmpBytesCopied = Math.toIntExact(read);
          } else {
            // we read some bytes into slice
            // determine the position buffer needs to be set to
            long bytesReadIntoBuffer = read - dstRemaining;
            Buffers.position(buffer, Math.toIntExact(bytesReadIntoBuffer));
            tmpBytesCopied = Math.toIntExact(read);
          }
        }
      }
      bytesConsumed += tmpBytesCopied;
    }
    totalRead += bytesConsumed;
    return bytesConsumed;
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  @Override
  public void close() throws IOException {
    channel.close();
  }

  private boolean enqueuedBytes() {
    return buffer.position() > 0;
  }
}
