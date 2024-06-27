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
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StateCheckingResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.common.annotations.VisibleForTesting;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class BlobDescriptorImpl implements BlobDescriptor {

  private final BlobDescriptorStream stream;
  private final BlobDescriptorState state;
  private final BlobInfo info;

  private BlobDescriptorImpl(BlobDescriptorStream stream, BlobDescriptorState state) {
    this.stream = stream;
    this.state = state;
    this.info = Conversions.grpc().blobInfo().decode(state.metadata.get());
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(ByteRangeSpec range) {
    long readId = state.readIdSeq.getAndIncrement();
    SettableApiFuture<byte[]> future = SettableApiFuture.create();
    OutstandingReadToArray value =
        new OutstandingReadToArray(readId, range.beginOffset(), range.length(), future);
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(value.makeReadRange()).build();
    state.outstandingReads.put(readId, value);
    stream.send(request);
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
      ResponseContentLifecycleManager<BidiReadObjectResponse> bidiResponseContentLifecycleManager,
      Executor executor) {
    BlobDescriptorState state = new BlobDescriptorState(openRequest);

    BlobDescriptorResponseObserver responseObserver =
        new BlobDescriptorResponseObserver(state, executor, bidiResponseContentLifecycleManager);

    BlobDescriptorStream stream = new BlobDescriptorStream(callable, context, responseObserver);

    ApiFuture<BlobDescriptor> blobDescriptorFuture =
        ApiFutures.transform(stream, nowOpen -> new BlobDescriptorImpl(stream, state), executor);
    stream.send(openRequest);
    return StorageException.coalesceAsync(blobDescriptorFuture);
  }

  private static final class BlobDescriptorStream
      implements ClientStream<BidiReadObjectRequest>, ApiFuture<Void> {
    private final SettableApiFuture<Void> openSignal;

    private final BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable;
    private final GrpcCallContext context;
    private final ResponseObserver<BidiReadObjectResponse> responseObserver;
    private final OpenMonitorResponseObserver openMonitorResponseObserver;

    private volatile ClientStream<BidiReadObjectRequest> requestStream;

    public BlobDescriptorStream(
        BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
        GrpcCallContext context,
        BlobDescriptorResponseObserver responseObserver) {
      this.callable = callable;
      this.context = context;
      this.responseObserver = responseObserver;
      this.openMonitorResponseObserver = new OpenMonitorResponseObserver(responseObserver);
      this.openSignal = SettableApiFuture.create();
    }

    public ClientStream<BidiReadObjectRequest> getRequestStream() {
      if (requestStream != null) {
        return requestStream;
      } else {
        synchronized (this) {
          if (requestStream == null) {
            requestStream = callable.splitCall(openMonitorResponseObserver, context);
          }
          return requestStream;
        }
      }
    }

    @Override
    public void send(BidiReadObjectRequest request) {
      getRequestStream().send(request);
    }

    @Override
    public void closeSendWithError(Throwable t) {
      getRequestStream().closeSendWithError(t);
    }

    @Override
    public void closeSend() {
      getRequestStream().closeSend();
    }

    @Override
    public boolean isSendReady() {
      return getRequestStream().isSendReady();
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
      openSignal.addListener(listener, executor);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return openSignal.cancel(mayInterruptIfRunning);
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
      return openSignal.get();
    }

    @Override
    public Void get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return openSignal.get(timeout, unit);
    }

    @Override
    public boolean isCancelled() {
      return openSignal.isCancelled();
    }

    @Override
    public boolean isDone() {
      return openSignal.isDone();
    }

    private class OpenMonitorResponseObserver
        extends StateCheckingResponseObserver<BidiReadObjectResponse> {

      private final BlobDescriptorResponseObserver responseObserver;

      private OpenMonitorResponseObserver(BlobDescriptorResponseObserver responseObserver) {
        this.responseObserver = responseObserver;
      }

      @Override
      protected void onStartImpl(StreamController controller) {
        responseObserver.onStartImpl(controller);
      }

      @Override
      protected void onResponseImpl(BidiReadObjectResponse response) {
        responseObserver.onResponseImpl(response);
        openSignal.set(null);
      }

      @Override
      protected void onErrorImpl(Throwable t) {
        responseObserver.onErrorImpl(t);
        openSignal.setException(t);
      }

      @Override
      protected void onCompleteImpl() {
        responseObserver.onCompleteImpl();
        openSignal.set(null);
      }
    }
  }

  private static final class BlobDescriptorResponseObserver
      extends StateCheckingResponseObserver<BidiReadObjectResponse> {

    private StreamController controller;
    private final BlobDescriptorState state;
    private final Executor exec;
    private final ResponseContentLifecycleManager<BidiReadObjectResponse>
        bidiResponseContentLifecycleManager;

    private BlobDescriptorResponseObserver(
        BlobDescriptorState state,
        Executor exec,
        ResponseContentLifecycleManager<BidiReadObjectResponse>
            bidiResponseContentLifecycleManager) {
      this.state = state;
      this.exec = exec;
      this.bidiResponseContentLifecycleManager = bidiResponseContentLifecycleManager;
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
      try (ResponseContentLifecycleHandle handle =
          bidiResponseContentLifecycleManager.get(response)) {
        if (response.hasMetadata()) {
          state.metadata.set(response.getMetadata());
        }
        if (response.hasReadHandle()) {
          state.ref.set(response.getReadHandle());
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
          ChildRef childRef = handle.borrow();
          read.accept(childRef, content);
          if (d.getRangeEnd()) {
            // invoke eof on exec, the resolving future could have a downstream callback
            // that we don't want to block this grpc thread
            exec.execute(read::eof);
            // don't remove the outstanding read until the future has been resolved
            state.outstandingReads.remove(id);
          }
        }
      } catch (IOException e) {
        // TODO: sync this up with stream restarts when the time comes
        throw StorageException.coalesce(e);
      }
    }

    @Override
    protected void onErrorImpl(Throwable t) {}

    @Override
    protected void onCompleteImpl() {}
  }

  @VisibleForTesting
  static final class OutstandingReadToArray {
    private final long readId;
    private final ReadCursor readCursor;
    private final ByteArrayOutputStream bytes;
    private final SettableApiFuture<byte[]> complete;

    @VisibleForTesting
    OutstandingReadToArray(
        long readId, long readOffset, long readLimit, SettableApiFuture<byte[]> complete) {
      this.readId = readId;
      this.readCursor = new ReadCursor(readOffset, readOffset + readLimit);
      this.bytes = new ByteArrayOutputStream();
      this.complete = complete;
    }

    public void accept(ChildRef childRef, ByteString bytes) throws IOException {
      try (ChildRef autoclose = childRef) {
        int size = bytes.size();
        bytes.writeTo(this.bytes);
        readCursor.advance(size);
      }
    }

    public void eof() {
      complete.set(bytes.toByteArray());
    }

    public ReadRange makeReadRange() {
      return ReadRange.newBuilder()
          .setReadId(readId)
          .setReadOffset(readCursor.position())
          .setReadLength(readCursor.remaining())
          .build();
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
