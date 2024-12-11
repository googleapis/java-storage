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

import static com.google.common.base.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.BlobDescriptor.ZeroCopySupport.DisposableByteString;
import com.google.cloud.storage.BlobDescriptorStreamRead.AccumulatingRead;
import com.google.cloud.storage.BlobDescriptorStreamRead.StreamingRead;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import org.checkerframework.checker.lock.qual.GuardedBy;

final class BlobDescriptorImpl implements BlobDescriptor {

  private final ScheduledExecutorService executor;
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>
      callable;
  private final BlobDescriptorStream stream;
  @VisibleForTesting final BlobDescriptorState state;
  private final BlobInfo info;
  private final RetryContextProvider retryContextProvider;

  @GuardedBy("this.lock")
  private final IdentityHashMap<BlobDescriptorStream, BlobDescriptorState> children;

  private final ReentrantLock lock;

  @GuardedBy("this.lock")
  private volatile boolean open;

  private BlobDescriptorImpl(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      BlobDescriptorStream stream,
      BlobDescriptorState state,
      RetryContextProvider retryContextProvider) {
    this.executor = executor;
    this.callable = callable;
    this.stream = stream;
    this.state = state;
    this.info = Conversions.grpc().blobInfo().decode(state.getMetadata());
    this.retryContextProvider = retryContextProvider;
    this.children = new IdentityHashMap<>();
    this.lock = new ReentrantLock();
    this.open = true;
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(RangeSpec range) {
    lock.lock();
    try {
      checkState(open, "stream already closed");
      long readId = state.newReadId();
      SettableApiFuture<byte[]> future = SettableApiFuture.create();
      AccumulatingRead<byte[]> read =
          BlobDescriptorStreamRead.createByteArrayAccumulatingRead(
              readId, range, retryContextProvider.create(), future);
      registerReadInState(readId, read);
      return future;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public ScatteringByteChannel readRangeAsChannel(RangeSpec range) {
    lock.lock();
    try {
      checkState(open, "stream already closed");
      long readId = state.newReadId();
      StreamingRead read =
          BlobDescriptorStreamRead.streamingRead(readId, range, retryContextProvider.create());
      registerReadInState(readId, read);
      return read;
    } finally {
      lock.unlock();
    }
  }

  public ApiFuture<DisposableByteString> readRangeAsByteString(RangeSpec range) {
    lock.lock();
    try {
      checkState(open, "stream already closed");
      long readId = state.newReadId();
      SettableApiFuture<DisposableByteString> future = SettableApiFuture.create();
      AccumulatingRead<DisposableByteString> read =
          BlobDescriptorStreamRead.createZeroCopyByteStringAccumulatingRead(
              readId, range, retryContextProvider.create(), future);
      registerReadInState(readId, read);
      return future;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public BlobInfo getBlobInfo() {
    return info;
  }

  @Override
  public void close() throws IOException {
    open = false;
    lock.lock();
    try {
      Iterator<Entry<BlobDescriptorStream, BlobDescriptorState>> it =
          children.entrySet().iterator();
      ArrayList<ApiFuture<Void>> closing = new ArrayList<>(children.size());
      while (it.hasNext()) {
        Entry<BlobDescriptorStream, BlobDescriptorState> next = it.next();
        BlobDescriptorStream subStream = next.getKey();
        it.remove();
        closing.add(subStream.closeAsync());
      }
      stream.close();
      ApiFutures.allAsList(closing).get();
    } catch (ExecutionException e) {
      throw new IOException(e.getCause());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException();
    } finally {
      lock.unlock();
    }
  }

  private void registerReadInState(long readId, BlobDescriptorStreamRead read) {
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
    if (state.canHandleNewRead(read)) {
      state.putOutstandingRead(readId, read);
      stream.send(request);
    } else {
      BlobDescriptorState child = state.forkChild();
      BlobDescriptorStream newStream =
          BlobDescriptorStream.create(executor, callable, child, retryContextProvider.create());
      children.put(newStream, child);
      read.setOnCloseCallback(
          () -> {
            children.remove(newStream);
            newStream.close();
          });
      child.putOutstandingRead(readId, read);
      newStream.send(request);
    }
  }

  static ApiFuture<BlobDescriptor> create(
      BidiReadObjectRequest openRequest,
      GrpcCallContext context,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      ScheduledExecutorService executor,
      RetryContextProvider retryContextProvider) {
    BlobDescriptorState state = new BlobDescriptorState(context, openRequest);

    BlobDescriptorStream stream =
        BlobDescriptorStream.create(executor, callable, state, retryContextProvider.create());

    ApiFuture<BlobDescriptor> blobDescriptorFuture =
        ApiFutures.transform(
            stream,
            nowOpen ->
                new BlobDescriptorImpl(executor, callable, stream, state, retryContextProvider),
            executor);
    stream.send(openRequest);
    return StorageException.coalesceAsync(blobDescriptorFuture);
  }

  private static final class EmptyDisposableByteString implements DisposableByteString {
    private static final EmptyDisposableByteString INSTANCE = new EmptyDisposableByteString();

    private EmptyDisposableByteString() {}

    @Override
    public ByteString byteString() {
      return ByteString.empty();
    }

    @Override
    public void close() throws IOException {
      // no-op
    }
  }
}
