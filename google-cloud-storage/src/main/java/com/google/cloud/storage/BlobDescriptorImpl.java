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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.cloud.storage.BlobDescriptor.ZeroCopySupport.DisposableByteString;
import com.google.cloud.storage.BlobDescriptorStreamRead.AccumulatingRead;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.LongMath;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import io.grpc.Status.Code;
import java.io.IOException;
import java.util.OptionalLong;
import java.util.concurrent.ScheduledExecutorService;

final class BlobDescriptorImpl implements BlobDescriptor {

  private final BlobDescriptorStream stream;
  @VisibleForTesting final BlobDescriptorState state;
  private final BlobInfo info;
  private final RetryContextProvider retryContextProvider;

  private BlobDescriptorImpl(
      BlobDescriptorStream stream,
      BlobDescriptorState state,
      RetryContextProvider retryContextProvider) {
    this.stream = stream;
    this.state = state;
    this.info = Conversions.grpc().blobInfo().decode(state.getMetadata());
    this.retryContextProvider = retryContextProvider;
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(RangeSpec range) {
    long readId = state.newReadId();
    ReadCursor readCursor = getReadCursor(range, state);
    if (!readCursor.hasRemaining()) {
      return ApiFutures.immediateFuture(new byte[0]);
    }
    SettableApiFuture<byte[]> future = SettableApiFuture.create();
    AccumulatingRead<byte[]> read =
        BlobDescriptorStreamRead.createByteArrayAccumulatingRead(
            readId, readCursor, retryContextProvider.create(), future);
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
    state.putOutstandingRead(readId, read);
    stream.send(request);
    return future;
  }

  public ApiFuture<DisposableByteString> readRangeAsByteString(RangeSpec range) {
    long readId = state.newReadId();
    ReadCursor readCursor = getReadCursor(range, state);
    if (!readCursor.hasRemaining()) {
      return ApiFutures.immediateFuture(EmptyDisposableByteString.INSTANCE);
    }
    SettableApiFuture<DisposableByteString> future = SettableApiFuture.create();
    AccumulatingRead<DisposableByteString> read =
        BlobDescriptorStreamRead.createZeroCopyByteStringAccumulatingRead(
            readId, readCursor, future, retryContextProvider.create());
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
    state.putOutstandingRead(readId, read);
    stream.send(request);
    return future;
  }

  @Override
  public BlobInfo getBlobInfo() {
    return info;
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

  @VisibleForTesting
  static ReadCursor getReadCursor(RangeSpec range, BlobDescriptorState state) {
    long begin = range.begin();
    long objectSize = state.getMetadata().getSize();
    if (begin > objectSize) {
      throw ApiExceptionFactory.createException(
          String.format(
              "range begin must be < objectSize (range begin = %d, object size = %d",
              begin, objectSize),
          null,
          GrpcStatusCode.of(Code.OUT_OF_RANGE),
          false);
    }
    final long end;
    OptionalLong limit = range.limit();
    long saturatedAdd = LongMath.saturatedAdd(begin, limit.orElse(0L));
    end = Math.min(saturatedAdd, objectSize);
    return new ReadCursor(begin, end);
  }

  static ApiFuture<BlobDescriptor> create(
      BidiReadObjectRequest openRequest,
      GrpcCallContext context,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      ScheduledExecutorService executor,
      RetryContextProvider retryContextProvider) {
    BlobDescriptorState state = new BlobDescriptorState(context, openRequest);

    BlobDescriptorStream stream = BlobDescriptorStream.create(executor, callable, state);

    ApiFuture<BlobDescriptor> blobDescriptorFuture =
        ApiFutures.transform(
            stream,
            nowOpen -> new BlobDescriptorImpl(stream, state, retryContextProvider),
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
