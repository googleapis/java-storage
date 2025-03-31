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

import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

@BetaApi
final class BlobAppendableUploadImpl implements BlobAppendableUpload {
  private final AppendableObjectBufferedWritableByteChannel channel;
  private final ApiFuture<BlobInfo> result;

  private BlobAppendableUploadImpl(BlobInfo blob, BlobWriteSession session, boolean takeover)
      throws IOException {
    channel = (AppendableObjectBufferedWritableByteChannel) (session.open());
    result = session.getResult();
    if (takeover) {
      channel.startTakeoverStream();
    }
  }

  static BlobAppendableUpload createNewAppendableBlob(BlobInfo blob, BlobWriteSession session)
      throws IOException {
    return new BlobAppendableUploadImpl(blob, session, false);
  }

  static BlobAppendableUpload resumeAppendableUpload(BlobInfo blob, BlobWriteSession session)
      throws IOException {
    return new BlobAppendableUploadImpl(blob, session, true);
  }

  void startTakeoverStream() {
    channel.startTakeoverStream();
  }

  @BetaApi
  public ApiFuture<BlobInfo> finalizeUpload() throws IOException {
    channel.finalizeWrite();
    close();
    return result;
  }

  @Override
  public int write(ByteBuffer buffer) throws IOException {
    return channel.write(buffer);
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  @Override
  public void close() throws IOException {
    if (channel.isOpen()) {
      channel.close();
    }
  }

  /**
   * This class extends BufferedWritableByteChannel to handle a special case for Appendable writes,
   * namely closing the stream without finalizing the write. It adds the {@code finalizeWrite}
   * method, which must be manually called to finalize the write. This couldn't be accomplished with
   * the base BufferedWritableByteChannel class because it only has a close() method, which it
   * assumes should finalize the write before the close. It also re-implements
   * SynchronizedBufferedWritableByteChannel to avoid needing to make a decorator class for it and
   * wrap it over this one.
   */
  static final class AppendableObjectBufferedWritableByteChannel
      implements BufferedWritableByteChannel {
    private final BufferedWritableByteChannel buffered;
    private final GapicBidiUnbufferedAppendableWritableByteChannel unbuffered;
    private final ReentrantLock lock;

    AppendableObjectBufferedWritableByteChannel(
        BufferedWritableByteChannel buffered,
        GapicBidiUnbufferedAppendableWritableByteChannel unbuffered) {
      this.buffered = buffered;
      this.unbuffered = unbuffered;
      lock = new ReentrantLock();
    }

    @Override
    public void flush() throws IOException {
      lock.lock();
      try {
        buffered.flush();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      lock.lock();
      try {
        return buffered.write(src);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isOpen() {
      lock.lock();
      try {
        return buffered.isOpen();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void close() throws IOException {
      lock.lock();
      try {
        buffered.close();
      } finally {
        lock.unlock();
      }
    }

    public void finalizeWrite() throws IOException {
      lock.lock();
      try {
        buffered.flush();
        unbuffered.finalizeWrite();
      } finally {
        lock.unlock();
      }
    }

    void startTakeoverStream() {
      unbuffered.startAppendableTakeoverStream();
    }
  }
}
