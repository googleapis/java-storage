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

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.function.BiFunction;

final class StorageByteChannels {

  private StorageByteChannels() {}

  static Readable readable() {
    return Readable.INSTANCE;
  }

  static Writable writable() {
    return Writable.INSTANCE;
  }

  interface BufferedWritableByteChannel extends WritableByteChannel {
    void flush() throws IOException;
  }

  interface BufferedReadableByteChannel extends ReadableByteChannel {}

  interface UnbufferedWritableByteChannel extends GatheringByteChannel {}

  interface UnbufferedReadableByteChannel extends ScatteringByteChannel {}

  static final class Sessions {

    interface UnbufferedReadableByteChannelSession<ResultT>
        extends ReadableByteChannelSession<UnbufferedReadableByteChannel, ResultT> {}

    interface UnbufferedWritableByteChannelSession<ResultT>
        extends WritableByteChannelSession<UnbufferedWritableByteChannel, ResultT> {}

    interface BufferedReadableByteChannelSession<ResultT>
        extends ReadableByteChannelSession<BufferedReadableByteChannel, ResultT> {}

    interface BufferedWritableByteChannelSession<ResultT>
        extends WritableByteChannelSession<BufferedWritableByteChannel, ResultT> {}

    interface ReadableByteChannelSession<RBC extends ReadableByteChannel, ResultT> {

      default RBC open() {
        return ApiExceptions.callAndTranslateApiException(openAsync());
      }

      ApiFuture<RBC> openAsync();

      ApiFuture<ResultT> getResult();
    }

    interface WritableByteChannelSession<WBC extends WritableByteChannel, ResultT> {

      default WBC open() {
        return ApiExceptions.callAndTranslateApiException(openAsync());
      }

      ApiFuture<WBC> openAsync();

      ApiFuture<ResultT> getResult();
    }

    static final class UnbufferedReadSession<S, R>
        extends DefaultSession<S, R, UnbufferedReadableByteChannel>
        implements UnbufferedReadableByteChannelSession<R> {

      UnbufferedReadSession(
          ApiFuture<S> startFuture,
          BiFunction<S, SettableApiFuture<R>, UnbufferedReadableByteChannel> f) {
        super(startFuture, f);
      }
    }

    static final class BufferedReadSession<S, R>
        extends DefaultSession<S, R, BufferedReadableByteChannel>
        implements BufferedReadableByteChannelSession<R> {

      BufferedReadSession(
          ApiFuture<S> startFuture,
          BiFunction<S, SettableApiFuture<R>, BufferedReadableByteChannel> f) {
        super(startFuture, f);
      }
    }

    static final class UnbufferedWriteSession<S, R>
        extends DefaultSession<S, R, UnbufferedWritableByteChannel>
        implements UnbufferedWritableByteChannelSession<R> {

      UnbufferedWriteSession(
          ApiFuture<S> startFuture,
          BiFunction<S, SettableApiFuture<R>, UnbufferedWritableByteChannel> f) {
        super(startFuture, f);
      }
    }

    static final class BufferedWriteSession<S, R>
        extends DefaultSession<S, R, BufferedWritableByteChannel>
        implements BufferedWritableByteChannelSession<R> {

      BufferedWriteSession(
          ApiFuture<S> startFuture,
          BiFunction<S, SettableApiFuture<R>, BufferedWritableByteChannel> f) {
        super(startFuture, f);
      }
    }

    private abstract static class DefaultSession<StartT, ResultT, ChannelT> {
      private final Object channelInitSyncObj = new Object();

      private final ApiFuture<StartT> startFuture;
      private final ApiFunction<StartT, ChannelT> f;
      private final SettableApiFuture<ResultT> resultFuture;

      private volatile ApiFuture<ChannelT> channel;

      private DefaultSession(
          ApiFuture<StartT> startFuture,
          BiFunction<StartT, SettableApiFuture<ResultT>, ChannelT> f) {
        this.startFuture = startFuture;
        this.resultFuture = SettableApiFuture.create();
        this.f = (s) -> f.apply(s, resultFuture);
      }

      public ApiFuture<ChannelT> openAsync() {
        if (channel == null) {
          synchronized (channelInitSyncObj) {
            if (channel == null) {
              channel = ApiFutures.transform(startFuture, f, MoreExecutors.directExecutor());
            }
          }
        }
        return channel;
      }

      public ApiFuture<ResultT> getResult() {
        return resultFuture;
      }
    }
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

    private SynchronizedBufferedReadableByteChannel(BufferedReadableByteChannel delegate) {
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
    public void close() throws IOException {
      delegate.close();
    }
  }

  private static final class SynchronizedBufferedWritableByteChannel
      implements BufferedWritableByteChannel {

    private final BufferedWritableByteChannel delegate;

    private SynchronizedBufferedWritableByteChannel(BufferedWritableByteChannel delegate) {
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
    public void close() throws IOException {
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
    public void close() throws IOException {
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
    public void close() throws IOException {
      delegate.close();
    }
  }
}
