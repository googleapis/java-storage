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
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.ReadRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

final class BlobDescriptorImpl implements BlobDescriptor {

  private final BlobDescriptorStream stream;
  private final BlobDescriptorState state;
  private final BlobInfo info;

  private BlobDescriptorImpl(BlobDescriptorStream stream, BlobDescriptorState state) {
    this.stream = stream;
    this.state = state;
    this.info = Conversions.grpc().blobInfo().decode(state.getMetadata());
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(ByteRangeSpec range) {
    long readId = state.newReadId();
    SettableApiFuture<byte[]> future = SettableApiFuture.create();
    OutstandingReadToArray value =
        new OutstandingReadToArray(readId, range.beginOffset(), range.length(), future);
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(value.makeReadRange()).build();
    state.putOutstandingRead(readId, value);
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
      BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      ResponseContentLifecycleManager<BidiReadObjectResponse> bidiResponseContentLifecycleManager,
      Executor executor) {
    BlobDescriptorState state = new BlobDescriptorState(openRequest);

    BlobDescriptorStream stream =
        BlobDescriptorStream.create(
            executor, bidiResponseContentLifecycleManager, callable, context, state);

    ApiFuture<BlobDescriptor> blobDescriptorFuture =
        ApiFutures.transform(stream, nowOpen -> new BlobDescriptorImpl(stream, state), executor);
    stream.send(openRequest);
    return StorageException.coalesceAsync(blobDescriptorFuture);
  }

  @VisibleForTesting
  static final class OutstandingReadToArray {
    private final long readId;
    private final ReadCursor readCursor;
    private final List<ChildRef> childRefs;
    private final SettableApiFuture<byte[]> complete;

    @VisibleForTesting
    OutstandingReadToArray(
        long readId, long readOffset, long readLimit, SettableApiFuture<byte[]> complete) {
      this.readId = readId;
      this.readCursor = new ReadCursor(readOffset, readOffset + readLimit);
      this.childRefs = Collections.synchronizedList(new ArrayList<>());
      this.complete = complete;
    }

    public void accept(ChildRef childRef) throws IOException {
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readCursor.advance(size);
    }

    public void eof() throws IOException {
      try {
        ByteString base = ByteString.empty();
        for (ChildRef ref : childRefs) {
          base = base.concat(ref.byteString());
        }
        complete.set(base.toByteArray());
      } finally {
        GrpcUtils.closeAll(childRefs);
      }
    }

    public ReadRange makeReadRange() {
      return ReadRange.newBuilder()
          .setReadId(readId)
          .setReadOffset(readCursor.position())
          .setReadLength(readCursor.remaining())
          .build();
    }
  }
}
