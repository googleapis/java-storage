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
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import java.io.IOException;
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
    checkState(stream.isOpen(), "stream already closed");
    long readId = state.newReadId();
    SettableApiFuture<byte[]> future = SettableApiFuture.create();
    AccumulatingRead<byte[]> read =
        BlobDescriptorStreamRead.createByteArrayAccumulatingRead(
            readId, range, retryContextProvider.create(), future);
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
    state.putOutstandingRead(readId, read);
    stream.send(request);
    return future;
  }

  public ApiFuture<DisposableByteString> readRangeAsByteString(RangeSpec range) {
    checkState(stream.isOpen(), "stream already closed");
    long readId = state.newReadId();
    SettableApiFuture<DisposableByteString> future = SettableApiFuture.create();
    AccumulatingRead<DisposableByteString> read =
        BlobDescriptorStreamRead.createZeroCopyByteStringAccumulatingRead(
            readId, range, future, retryContextProvider.create());
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
