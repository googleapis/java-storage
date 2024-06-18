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
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.StateCheckingResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import com.google.storage.v2.ReadRange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class BlobDescriptorImpl implements BlobDescriptor {

  private final BlobDescriptorStreamPair stream;
  private final BlobDescriptorState state;
  private final BlobInfo info;

  private BlobDescriptorImpl(BlobDescriptorStreamPair stream, BlobDescriptorState state) {
    this.stream = stream;
    this.state = state;
    this.info = Conversions.grpc().blobInfo().decode(state.metadata.get());
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(ByteRangeSpec range) {
    long readId = state.readIdSeq.getAndIncrement();
    SettableApiFuture<byte[]> future = SettableApiFuture.create();
    OutstandingReadToArray value =
        new OutstandingReadToArray(
            readId, range.beginOffset(), range.length(), new ByteArrayOutputStream(), future);
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder()
            .addReadRanges(
                ReadRange.newBuilder()
                    .setReadId(readId)
                    .setReadOffset(range.beginOffset())
                    .setReadLength(range.length())
                    .build())
            .build();
    state.outstandingReads.put(readId, value);
    stream.requestStream.send(request);
    return future;
  }

  @Override
  public BlobInfo getBlobInfo() {
    return info;
  }

  static ApiFuture<BlobDescriptor> create(
      BidiReadObjectRequest openRequest,
      GrpcCallContext context,
      BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      Executor executor) {
    SettableApiFuture<Void> pendingOpen = SettableApiFuture.create();
    BlobDescriptorState state = new BlobDescriptorState(openRequest);
    BlobDescriptorResponseObserver responseObserver =
        new BlobDescriptorResponseObserver(pendingOpen, state, executor);
    ClientStream<BidiReadObjectRequest> requestStream =
        callable.splitCall(responseObserver, context);
    BlobDescriptorStreamPair stream = new BlobDescriptorStreamPair(requestStream, responseObserver);
    ApiFuture<BlobDescriptor> blobDescriptorFuture =
        ApiFutures.transform(
            pendingOpen, nowOpen -> new BlobDescriptorImpl(stream, state), executor);
    stream.getRequestStream().send(openRequest);
    return StorageException.coalesceAsync(blobDescriptorFuture);
  }

  private static final class BlobDescriptorStreamPair {
    private final ClientStream<BidiReadObjectRequest> requestStream;
    private final BlobDescriptorResponseObserver responseObserver;

    BlobDescriptorStreamPair(
        ClientStream<BidiReadObjectRequest> requestStream,
        BlobDescriptorResponseObserver responseObserver) {
      this.requestStream = requestStream;
      this.responseObserver = responseObserver;
    }

    public ClientStream<BidiReadObjectRequest> getRequestStream() {
      return requestStream;
    }
  }

  private static final class BlobDescriptorResponseObserver
      extends StateCheckingResponseObserver<BidiReadObjectResponse> {

    private StreamController controller;
    private final BlobDescriptorState state;
    private final Executor exec;

    private final SettableApiFuture<Void> openSignal;

    public BlobDescriptorResponseObserver(
        SettableApiFuture<Void> openSignal, BlobDescriptorState state, Executor exec) {
      this.openSignal = openSignal;
      this.state = state;
      this.exec = exec;
    }

    @Override
    protected void onStartImpl(StreamController controller) {
      this.controller = controller;
      controller.disableAutoInboundFlowControl();
      controller.request(2);
    }

    @Override
    protected void onResponseImpl(BidiReadObjectResponse response) {
      controller.request(1);
      if (response.hasMetadata()) {
        state.metadata.set(response.getMetadata());
        openSignal.set(null);
      }
      if (response.hasReadHandle()) {
        state.ref.set(response.getReadHandle());
        openSignal.set(null);
      }
      List<ObjectRangeData> rangeData = response.getObjectDataRangesList();
      if (rangeData.isEmpty()) {
        return;
      }
      for (ObjectRangeData d : rangeData) {
        long id = d.getReadRange().getReadId();
        OutstandingReadToArray read = state.outstandingReads.get(id);
        if (read == null) {
          continue;
        }
        ByteString content = d.getChecksummedData().getContent();
        read.accept(content);
        if (d.getRangeEnd()) {
          state.outstandingReads.remove(id);
          // invoke eof on exec, the resolving future could have a downstream callback
          // that we don't want to block this grpc thread
          exec.execute(read::eof);
        }
      }
    }

    @Override
    protected void onErrorImpl(Throwable t) {
      openSignal.setException(t);
    }

    @Override
    protected void onCompleteImpl() {
      openSignal.set(null);
    }
  }

  // todo: handle zero-copy lifecycle integration
  private static final class OutstandingReadToArray {
    private final long readId;
    private final long readOffset;
    private final long readLimit;
    private final ByteArrayOutputStream bytes;
    private final SettableApiFuture<byte[]> complete;

    private OutstandingReadToArray(
        long readId,
        long readOffset,
        long readLimit,
        ByteArrayOutputStream bytes,
        SettableApiFuture<byte[]> complete) {
      this.readId = readId;
      this.readOffset = readOffset;
      this.readLimit = readLimit;
      this.bytes = bytes;
      this.complete = complete;
    }

    public void accept(ByteString bytes) {
      try {
        bytes.writeTo(this.bytes);
      } catch (IOException e) {
        // ByteArrayOutputStream doesn't throw IOException
        throw new RuntimeException(e);
      }
    }

    public void eof() {
      complete.set(bytes.toByteArray());
    }
  }

  private static final class BlobDescriptorState {
    private final BidiReadObjectRequest openRequest;
    private final AtomicReference<BidiReadHandle> ref;
    private final AtomicReference<Object> metadata;
    private final AtomicLong readIdSeq;
    private final Map<Long, OutstandingReadToArray> outstandingReads;

    public BlobDescriptorState(BidiReadObjectRequest openRequest) {
      this.openRequest = openRequest;
      this.ref = new AtomicReference<>();
      this.metadata = new AtomicReference<>();
      this.readIdSeq = new AtomicLong(1);
      this.outstandingReads = new ConcurrentHashMap<>();
    }
  }
}
