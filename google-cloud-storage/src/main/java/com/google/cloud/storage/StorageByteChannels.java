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
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class StorageByteChannels {

  static Readable readable() {
    return Readable.INSTANCE;
  }

  static Writable writable() {
    return Writable.INSTANCE;
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

    public UnbufferedWritableByteChannel createSynchronized(
        UnbufferedWritableByteChannel delegate) {
      return new SynchronizedUnbufferedWritableByteChannel(delegate);
    }
  }

  private static final class SynchronizedBufferedReadableByteChannel
      implements BufferedReadableByteChannel {

    private final BufferedReadableByteChannel delegate;

    public SynchronizedBufferedReadableByteChannel(BufferedReadableByteChannel delegate) {
      this.delegate = delegate;
    }

    @Override
    public synchronized int read(ByteBuffer dst) throws IOException {
      return delegate.read(dst);
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
      delegate.close();
    }
  }

  private static final class SynchronizedBufferedWritableByteChannel
      implements BufferedWritableByteChannel {

    private final BufferedWritableByteChannel delegate;

    public SynchronizedBufferedWritableByteChannel(BufferedWritableByteChannel delegate) {
      this.delegate = delegate;
    }

    @Override
    public synchronized int write(ByteBuffer src) throws IOException {
      return delegate.write(src);
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
      delegate.close();
    }

    @Override
    public synchronized void flush() throws IOException {
      delegate.flush();
    }
  }

  private static final class SynchronizedUnbufferedReadableByteChannel
      implements UnbufferedReadableByteChannel {

    private final UnbufferedReadableByteChannel delegate;

    private SynchronizedUnbufferedReadableByteChannel(UnbufferedReadableByteChannel delegate) {
      this.delegate = delegate;
    }

    @Override
    public synchronized int read(ByteBuffer src) throws IOException {
      return delegate.read(src);
    }

    @Override
    public synchronized long read(ByteBuffer[] dsts) throws IOException {
      return delegate.read(dsts);
    }

    @Override
    public synchronized long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return delegate.read(dsts, offset, length);
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
      delegate.close();
    }
  }

  private static final class SynchronizedUnbufferedWritableByteChannel
      implements UnbufferedWritableByteChannel {

    private final UnbufferedWritableByteChannel delegate;

    private SynchronizedUnbufferedWritableByteChannel(UnbufferedWritableByteChannel delegate) {
      this.delegate = delegate;
    }

    @Override
    public synchronized int write(ByteBuffer src) throws IOException {
      return delegate.write(src);
    }

    @Override
    public synchronized long write(ByteBuffer[] srcs) throws IOException {
      return delegate.write(srcs);
    }

    @Override
    public synchronized long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return delegate.write(srcs, offset, length);
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
      delegate.close();
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
        if (dst.hasRemaining()) {
          int read = c.read(dst);
          if (read == -1) {
            if (totalBytesRead == 0) {
              c.close();
              return -1;
            } else {
              break;
            }
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
}
