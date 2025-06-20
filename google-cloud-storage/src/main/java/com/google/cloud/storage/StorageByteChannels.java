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

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class StorageByteChannels {

  static Readable readable() {
    return Readable.INSTANCE;
  }

  static Writable writable() {
    return Writable.INSTANCE;
  }

  public static SeekableByteChannel seekable(SeekableByteChannel delegate) {
    return new SynchronizedSeekableByteChannel(delegate);
  }

  static final class Readable {
    private static final Readable INSTANCE = new Readable();

    private Readable() {}

    public BufferedReadableByteChannel createSynchronized(BufferedReadableByteChannel delegate) {
      return new SynchronizedBufferedReadableByteChannel(delegate);
    }

    public UnbufferedReadableByteChannel createSynchronized(
        UnbufferedReadableByteChannel delegate) {
      return new SynchronizedUnbufferedReadableByteChannel(delegate);
    }

    public ScatteringByteChannel asScatteringByteChannel(ReadableByteChannel c) {
      return new ScatteringByteChannelFacade(c);
    }
  }

  static final class Writable {
    private static final Writable INSTANCE = new Writable();

    private Writable() {}

    public BufferedWritableByteChannel createSynchronized(BufferedWritableByteChannel delegate) {
      return new SynchronizedBufferedWritableByteChannel(delegate);
    }

    public UnbufferedWritableByteChannel validateUploadCrc32c(
        UnbufferedWritableByteChannel delegate, ApiFuture<Crc32cLengthKnown> crc32cGetter) {
      return new ChecksumValidatingUnbufferedWritableByteChannel(delegate, crc32cGetter);
    }

    public UnbufferedWritableByteChannel createSynchronized(
        UnbufferedWritableByteChannel delegate) {
      return new SynchronizedUnbufferedWritableByteChannel(delegate);
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  private static final class ChecksumValidatingUnbufferedWritableByteChannel
      implements UnbufferedWritableByteChannel {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ChecksumValidatingUnbufferedWritableByteChannel.class);
    private final UnbufferedWritableByteChannel delegate;
    private final ApiFuture<Crc32cLengthKnown> crc32cGetter;

    private final Hasher cumulativeCrc32c;
    private long totalLength;

    private ChecksumValidatingUnbufferedWritableByteChannel(
        UnbufferedWritableByteChannel delegate, ApiFuture<Crc32cLengthKnown> crc32cGetter) {
      this.delegate = delegate;
      this.crc32cGetter = crc32cGetter;
      this.cumulativeCrc32c = Hashing.crc32c().newHasher();
      this.totalLength = 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      ByteBuffer dup = src.duplicate();
      int written = delegate.write(src);
      hash(dup, written);
      return written;
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
      return write(srcs, 0, srcs.length);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      ByteBuffer[] dups = Buffers.duplicate(srcs, offset, length);
      long written = delegate.write(srcs, offset, length);
      hash(dups, offset, length, written);
      return written;
    }

    @Override
    public int writeAndClose(ByteBuffer src) throws IOException {
      ByteBuffer dup = src.duplicate();
      int written = delegate.writeAndClose(src);
      hash(dup, written);
      internalClose();
      return written;
    }

    @Override
    public long writeAndClose(ByteBuffer[] srcs) throws IOException {
      return writeAndClose(srcs, 0, srcs.length);
    }

    @Override
    public long writeAndClose(ByteBuffer[] srcs, int offset, int length) throws IOException {
      ByteBuffer[] dups = Buffers.duplicate(srcs, offset, length);
      long written = delegate.writeAndClose(srcs, offset, length);
      hash(dups, offset, length, written);
      internalClose();
      return written;
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public void close() throws IOException {
      if (delegate.isOpen()) {
        delegate.close();
        internalClose();
      }
    }

    private long hash(ByteBuffer src, long written) {
      ByteBuffer buffer = src.slice();
      int remaining = buffer.remaining();
      int consumed = remaining;
      if (written < remaining) {
        int intExact = Math.toIntExact(written);
        buffer.limit(intExact);
        consumed = intExact;
      }
      totalLength += remaining;
      cumulativeCrc32c.putBytes(buffer);
      src.position(src.position() + consumed);
      return consumed;
    }

    private void hash(ByteBuffer[] srcs, int offset, int length, long written) {
      for (int i = offset; i < length; i++) {
        written -= hash(srcs[i], written);
      }
    }

    private void internalClose() throws IOException {
      try {
        Crc32cLengthKnown actual = crc32cGetter.get();
        Crc32cLengthKnown expected = Crc32cValue.of(cumulativeCrc32c.hash().asInt(), totalLength);
        LOGGER.debug("expected = {}, actual = {}", expected, actual);
        if (!expected.eqValue(actual)) {
          throw new ClientDetectedDataLossException(actual, expected);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        InterruptedIOException interruptedIOException = new InterruptedIOException();
        interruptedIOException.initCause(e);
        throw interruptedIOException;
      } catch (ExecutionException e) {
        throw new IOException(e.getCause());
      }
    }
  }

  private static final class SynchronizedBufferedReadableByteChannel
      implements BufferedReadableByteChannel {

    private final BufferedReadableByteChannel delegate;
    private final ReentrantLock lock;

    public SynchronizedBufferedReadableByteChannel(BufferedReadableByteChannel delegate) {
      this.delegate = delegate;
      this.lock = new ReentrantLock();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
      lock.lock();
      try {
        return delegate.read(dst);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return delegate.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        delegate.close();
      } finally {
        lock.unlock();
      }
    }
  }

  private static final class SynchronizedBufferedWritableByteChannel
      implements BufferedWritableByteChannel {

    private final BufferedWritableByteChannel delegate;
    private final ReentrantLock lock;

    public SynchronizedBufferedWritableByteChannel(BufferedWritableByteChannel delegate) {
      this.delegate = delegate;
      this.lock = new ReentrantLock();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return delegate.write(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return delegate.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        delegate.close();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void flush() throws IOException {
      lock.lock();
      try {
        delegate.flush();
      } finally {
        lock.unlock();
      }
    }
  }

  private static final class SynchronizedUnbufferedReadableByteChannel
      implements UnbufferedReadableByteChannel {

    private final UnbufferedReadableByteChannel delegate;
    private final ReentrantLock lock;

    private SynchronizedUnbufferedReadableByteChannel(UnbufferedReadableByteChannel delegate) {
      this.delegate = delegate;
      this.lock = new ReentrantLock();
    }

    @Override
    public int read(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return delegate.read(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long read(ByteBuffer[] dsts) throws IOException {
      lock.lock();
      try {
        return delegate.read(dsts);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      lock.lock();
      try {
        return delegate.read(dsts, offset, length);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return delegate.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        delegate.close();
      } finally {
        lock.unlock();
      }
    }
  }

  private static final class SynchronizedUnbufferedWritableByteChannel
      implements UnbufferedWritableByteChannel {

    private final UnbufferedWritableByteChannel delegate;
    private final ReentrantLock lock;

    private SynchronizedUnbufferedWritableByteChannel(UnbufferedWritableByteChannel delegate) {
      this.delegate = delegate;
      this.lock = new ReentrantLock();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return delegate.write(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
      lock.lock();
      try {
        return delegate.write(srcs);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      lock.lock();
      try {
        return delegate.write(srcs, offset, length);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public int writeAndClose(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return delegate.writeAndClose(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long writeAndClose(ByteBuffer[] srcs) throws IOException {
      lock.lock();
      try {
        return delegate.writeAndClose(srcs);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long writeAndClose(ByteBuffer[] srcs, int offset, int length) throws IOException {
      lock.lock();
      try {
        return delegate.writeAndClose(srcs, offset, length);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return delegate.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        delegate.close();
      } finally {
        lock.unlock();
      }
    }
  }

  private static final class ScatteringByteChannelFacade implements ScatteringByteChannel {
    private final ReadableByteChannel c;

    private ScatteringByteChannelFacade(final ReadableByteChannel c) {
      this.c = c;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
      return Math.toIntExact(read(new ByteBuffer[] {dst}, 0, 1));
    }

    @Override
    public long read(ByteBuffer[] dsts) throws IOException {
      return read(dsts, 0, dsts.length);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (!c.isOpen()) {
        throw new ClosedChannelException();
      }

      long totalBytesRead = 0;
      for (int i = offset; i < length; i++) {
        ByteBuffer dst = dsts[i];
        int goal = dst.remaining();
        if (dst.hasRemaining()) {
          int read = c.read(dst);
          if (read == -1) {
            if (totalBytesRead == 0) {
              c.close();
              return -1;
            } else {
              break;
            }
          } else if (read != goal) {
            // if we weren't able to fill up the current buffer with this last read, return so we
            // don't block and wait for another read call.
            return totalBytesRead + read;
          }
          totalBytesRead += read;
        }
      }
      return totalBytesRead;
    }

    @Override
    public boolean isOpen() {
      return c.isOpen();
    }

    @Override
    public void close() throws IOException {
      c.close();
    }
  }

  private static final class SynchronizedSeekableByteChannel implements SeekableByteChannel {
    private final SeekableByteChannel delegate;
    private final ReentrantLock lock;

    private SynchronizedSeekableByteChannel(SeekableByteChannel delegate) {
      this.delegate = delegate;
      this.lock = new ReentrantLock();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
      lock.lock();
      try {
        return delegate.read(dst);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return delegate.write(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long position() throws IOException {
      lock.lock();
      try {
        return delegate.position();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
      lock.lock();
      try {
        return delegate.position(newPosition);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public long size() throws IOException {
      lock.lock();
      try {
        return delegate.size();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
      lock.lock();
      try {
        return delegate.truncate(size);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return delegate.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        delegate.close();
      } finally {
        lock.unlock();
      }
    }
  }
}
