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
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.StateCheckingResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.common.base.Preconditions;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class BlobDescriptorStream
    implements ClientStream<BidiReadObjectRequest>, ApiFuture<Void>, AutoCloseable {

  private final SettableApiFuture<Void> blobDescriptorResolveFuture;

  private final BlobDescriptorState state;
  private final ResponseContentLifecycleManager<BidiReadObjectResponse>
      bidiResponseContentLifecycleManager;
  private final Executor executor;
  private final BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable;
  private final GrpcCallContext context;

  private volatile boolean open;
  private volatile MonitoringResponseObserver monitoringResponseObserver;
  private volatile ClientStream<BidiReadObjectRequest> requestStream;
  private volatile StreamController controller;

  private BlobDescriptorStream(
      BlobDescriptorState state,
      Executor executor,
      ResponseContentLifecycleManager<BidiReadObjectResponse> bidiResponseContentLifecycleManager,
      BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      GrpcCallContext context) {
    this.state = state;
    this.executor = executor;
    this.bidiResponseContentLifecycleManager = bidiResponseContentLifecycleManager;
    this.callable = callable;
    this.context = context;
    this.blobDescriptorResolveFuture = SettableApiFuture.create();
    this.open = true;
  }

  public ClientStream<BidiReadObjectRequest> getRequestStream() {
    if (requestStream != null) {
      return requestStream;
    } else {
      synchronized (this) {
        if (requestStream == null) {
          monitoringResponseObserver =
              new MonitoringResponseObserver(new BidiReadObjectResponseObserver());
          requestStream = callable.splitCall(monitoringResponseObserver, context);
        }
        return requestStream;
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (!open) {
      return;
    }

    try {
      cancel(true);
      if (requestStream != null) {
        requestStream.closeSend();
        ApiFutureUtils.await(monitoringResponseObserver.closeSignal);
        requestStream = null;
      }
    } finally {
      open = false;
    }
  }

  @Override
  public void send(BidiReadObjectRequest request) {
    checkOpen();
    getRequestStream().send(request);
  }

  @Override
  public void closeSendWithError(Throwable t) {
    checkOpen();
    getRequestStream().closeSendWithError(t);
  }

  @Override
  public void closeSend() {
    checkOpen();
    getRequestStream().closeSend();
  }

  @Override
  public boolean isSendReady() {
    checkOpen();
    return getRequestStream().isSendReady();
  }

  @Override
  public void addListener(Runnable listener, Executor executor) {
    blobDescriptorResolveFuture.addListener(listener, executor);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return blobDescriptorResolveFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public Void get() throws InterruptedException, ExecutionException {
    return blobDescriptorResolveFuture.get();
  }

  @Override
  public Void get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return blobDescriptorResolveFuture.get(timeout, unit);
  }

  @Override
  public boolean isCancelled() {
    return blobDescriptorResolveFuture.isCancelled();
  }

  @Override
  public boolean isDone() {
    return blobDescriptorResolveFuture.isDone();
  }

  private void checkOpen() {
    Preconditions.checkState(open, "not open");
  }

  private void restart() {
    requestStream = null;

    BidiReadObjectRequest openRequest = state.getOpenRequest();
    BidiReadObjectRequest.Builder b = openRequest.toBuilder().clearReadRanges();

    String routingToken = state.getRoutingToken();
    if (routingToken != null) {
      b.getReadObjectSpecBuilder().setRoutingToken(routingToken);
    }

    BidiReadHandle bidiReadHandle = state.getBidiReadHandle();
    if (bidiReadHandle != null) {
      b.getReadObjectSpecBuilder().setReadHandle(bidiReadHandle);
    }

    b.addAllReadRanges(state.getOutstandingReads());
    if (openRequest.getReadObjectSpec().getGeneration() <= 0) {
      Object metadata = state.getMetadata();
      if (metadata != null) {
        b.getReadObjectSpecBuilder().setGeneration(metadata.getGeneration());
      }
    }

    BidiReadObjectRequest restartRequest = b.build();
    synchronized (this) {
      ClientStream<BidiReadObjectRequest> requestStream1 = getRequestStream();
      requestStream1.send(restartRequest);
      // todo: put this in a retry loop
      ApiFutureUtils.await(monitoringResponseObserver.openSignal);
    }
  }

  private final class BidiReadObjectResponseObserver
      extends StateCheckingResponseObserver<BidiReadObjectResponse> {

    private BidiReadObjectResponseObserver() {}

    @Override
    public void onStartImpl(StreamController controller) {
      BlobDescriptorStream.this.controller = controller;
      controller.disableAutoInboundFlowControl();
      controller.request(2);
    }

    @Override
    protected void onResponseImpl(BidiReadObjectResponse response) {
      controller.request(1);
      try (ResponseContentLifecycleHandle<BidiReadObjectResponse> handle =
          bidiResponseContentLifecycleManager.get(response)) {
        if (response.hasMetadata()) {
          state.setMetadata(response.getMetadata());
        }
        if (response.hasReadHandle()) {
          state.setBidiReadHandle(response.getReadHandle());
        }
        List<ObjectRangeData> rangeData = response.getObjectDataRangesList();
        if (rangeData.isEmpty()) {
          return;
        }
        for (int i = 0; i < rangeData.size(); i++) {
          ObjectRangeData d = rangeData.get(i);
          long id = d.getReadRange().getReadId();
          BlobDescriptorStreamRead read = state.getOutstandingRead(id);
          if (read == null) {
            continue;
          }
          final int idx = i;
          //noinspection rawtypes
          ResponseContentLifecycleHandle.ChildRef childRef =
              handle.borrow(r -> r.getObjectDataRanges(idx).getChecksummedData().getContent());
          read.accept(childRef);
          if (d.getRangeEnd()) {
            // invoke eof on exec, the resolving future could have a downstream callback
            // that we don't want to block this grpc thread
            executor.execute(
                () -> {
                  try {
                    read.eof();
                    // don't remove the outstanding read until the future has been resolved
                    state.removeOutstandingRead(id);
                  } catch (IOException e) {
                    // TODO: sync this up with stream restarts when the time comes
                    throw StorageException.coalesce(e);
                  }
                });
          }
        }
      } catch (IOException e) {
        // TODO: sync this up with stream restarts when the time comes
        throw StorageException.coalesce(e);
      }
    }

    @Override
    protected void onErrorImpl(Throwable t) {
      BidiReadObjectRedirectedError bidiReadObjectRedirectedError =
          GrpcUtils.getBidiReadObjectRedirectedError(t);
      if (bidiReadObjectRedirectedError != null) {
        if (bidiReadObjectRedirectedError.hasReadHandle()) {
          state.setBidiReadHandle(bidiReadObjectRedirectedError.getReadHandle());
        }
        if (bidiReadObjectRedirectedError.hasRoutingToken()) {
          state.setRoutingToken(bidiReadObjectRedirectedError.getRoutingToken());
        }
        executor.execute(BlobDescriptorStream.this::restart);
      }
    }

    @Override
    protected void onCompleteImpl() {}
  }

  private class MonitoringResponseObserver
      extends StateCheckingResponseObserver<BidiReadObjectResponse> {
    private final BidiReadObjectResponseObserver delegate;
    private final SettableApiFuture<Void> openSignal;
    private final SettableApiFuture<Void> closeSignal;

    private MonitoringResponseObserver(BidiReadObjectResponseObserver delegate) {
      this.delegate = delegate;
      this.openSignal = SettableApiFuture.create();
      this.closeSignal = SettableApiFuture.create();
    }

    @Override
    protected void onStartImpl(StreamController controller) {
      delegate.onStart(controller);
    }

    @Override
    protected void onResponseImpl(BidiReadObjectResponse response) {
      delegate.onResponse(response);
      openSignal.set(null);
      blobDescriptorResolveFuture.set(null);
    }

    @Override
    protected void onErrorImpl(Throwable t) {
      if (GrpcUtils.isBidiReadObjectRedirect(t)) {
        delegate.onError(t);
      } else {
        delegate.onError(t);
        blobDescriptorResolveFuture.setException(t);
        openSignal.setException(t);
        closeSignal.setException(t);
      }
    }

    @Override
    protected void onCompleteImpl() {
      delegate.onComplete();
      blobDescriptorResolveFuture.set(null);
      openSignal.set(null);
      closeSignal.set(null);
    }
  }

  static BlobDescriptorStream create(
      Executor executor,
      ResponseContentLifecycleManager<BidiReadObjectResponse> bidiResponseContentLifecycleManager,
      BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      GrpcCallContext context,
      BlobDescriptorState state) {

    return new BlobDescriptorStream(
        state, executor, bidiResponseContentLifecycleManager, callable, context);
  }
}
