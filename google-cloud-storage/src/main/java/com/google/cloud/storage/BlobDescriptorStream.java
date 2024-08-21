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
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectError;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import com.google.storage.v2.ReadRange;
import com.google.storage.v2.ReadRangeError;
import io.grpc.Status.Code;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

final class BlobDescriptorStream
    implements ClientStream<BidiReadObjectRequest>, ApiFuture<Void>, AutoCloseable {

  private final SettableApiFuture<Void> blobDescriptorResolveFuture;

  private final BlobDescriptorState state;
  private final ScheduledExecutorService executor;
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>
      callable;
  private final GrpcCallContext context;
  private final int maxRedirectsAllowed;

  private volatile boolean open;
  private volatile MonitoringResponseObserver monitoringResponseObserver;
  private volatile ResponseObserver<BidiReadObjectResponse> responseObserver;
  private volatile ClientStream<BidiReadObjectRequest> requestStream;
  private volatile StreamController controller;
  private final AtomicInteger redirectCounter;

  private BlobDescriptorStream(
      BlobDescriptorState state,
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      GrpcCallContext context,
      int maxRedirectsAllowed) {
    this.state = state;
    this.executor = executor;
    this.callable = callable;
    this.context = context;
    this.blobDescriptorResolveFuture = SettableApiFuture.create();
    this.open = true;
    this.redirectCounter = new AtomicInteger();
    this.maxRedirectsAllowed = maxRedirectsAllowed;
  }

  public ClientStream<BidiReadObjectRequest> getRequestStream() {
    if (requestStream != null) {
      return requestStream;
    } else {
      synchronized (this) {
        if (requestStream == null) {
          monitoringResponseObserver =
              new MonitoringResponseObserver(new BidiReadObjectResponseObserver());
          responseObserver =
              GrpcUtils.decorateAsStateChecking(
                  new RedirectHandlingResponseObserver(monitoringResponseObserver));
          requestStream = callable.splitCall(responseObserver, context);
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
    if (requestStream == null) {
      restart();
    } else {
      getRequestStream().send(request);
    }
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
    reset();

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
    ClientStream<BidiReadObjectRequest> requestStream1 = getRequestStream();
    requestStream1.send(restartRequest);
  }

  private void reset() {
    requestStream = null;
  }

  private final class BidiReadObjectResponseObserver
      implements ResponseObserver<BidiReadObjectResponse> {

    private BidiReadObjectResponseObserver() {}

    @Override
    public void onStart(StreamController controller) {
      BlobDescriptorStream.this.controller = controller;
      controller.disableAutoInboundFlowControl();
      controller.request(2);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onResponse(BidiReadObjectResponse response) {
      controller.request(1);
      try (ResponseContentLifecycleHandle<BidiReadObjectResponse> handle =
          callable.getResponseContentLifecycleManager().get(response)) {
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
          ReadRange readRange = d.getReadRange();
          long id = readRange.getReadId();
          BlobDescriptorStreamRead read = state.getOutstandingRead(id);
          if (read == null || !read.acceptingBytes()) {
            continue;
          }
          ChecksummedData checksummedData = d.getChecksummedData();
          ByteString content = checksummedData.getContent();
          int crc32C = checksummedData.getCrc32C();

          try {
            // todo: benchmark how long it takes to compute this checksum and whether it needs to
            //   happen on a non-io thread
            Hasher.enabled().validate(Crc32cValue.of(crc32C), content);
          } catch (IOException e) {
            try {
              read.recordError(e);

              //noinspection resource
              BlobDescriptorStreamRead readWithNewId = state.assignNewReadId(id);
              BidiReadObjectRequest requestWithNewReadId =
                  BidiReadObjectRequest.newBuilder()
                      .addReadRanges(readWithNewId.makeReadRange())
                      .build();
              requestStream.send(requestWithNewReadId);
            } catch (Throwable t) {
              read.fail(t);
            }
            continue;
          }

          final int idx = i;
          long begin = readRange.getReadOffset();
          long position = read.getReadCursor().position();
          if (begin == position) {
            ChildRef childRef =
                handle.borrow(r -> r.getObjectDataRanges(idx).getChecksummedData().getContent());
            read.accept(childRef);
          } else if (begin < position) {
            int skip = Math.toIntExact(position - begin);
            ChildRef childRef =
                handle.borrow(
                    r ->
                        r.getObjectDataRanges(idx)
                            .getChecksummedData()
                            .getContent()
                            .substring(skip));
            read.accept(childRef);
            //noinspection resource
            read = state.assignNewReadId(id);
            if (read.getReadCursor().hasRemaining()) {
              BidiReadObjectRequest requestWithNewReadId =
                  BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
              requestStream.send(requestWithNewReadId);
            }
          } else {
            Status status =
                Status.newBuilder()
                    .setCode(Code.OUT_OF_RANGE.value())
                    .setMessage(
                        String.format("position = %d, readRange.read_offset = %d", position, begin))
                    .build();
            BlobDescriptorStreamRead readWithNewId = state.assignNewReadId(id);
            // todo: record failure for read
            BidiReadObjectRequest requestWithNewReadId =
                BidiReadObjectRequest.newBuilder()
                    .addReadRanges(readWithNewId.makeReadRange())
                    .build();
            requestStream.send(requestWithNewReadId);
            // todo
            continue;
          }
          if (d.getRangeEnd() && !read.getReadCursor().hasRemaining()) {
            final BlobDescriptorStreamRead finalRead = read;
            // invoke eof on exec, the resolving future could have a downstream callback
            // that we don't want to block this grpc thread
            executor.execute(
                StorageException.liftToRunnable(
                    () -> {
                      finalRead.eof();
                      // don't remove the outstanding read until the future has been resolved
                      state.removeOutstandingRead(id);
                    }));
          }
        }
      } catch (IOException e) {
        // TODO: sync this up with stream restarts when the time comes
        throw StorageException.coalesce(e);
      }
    }

    @Override
    public void onError(Throwable t) {
      BidiReadObjectError error = GrpcUtils.getBidiReadObjectError(t);
      if (error == null) {
        return;
      }

      List<ReadRangeError> rangeErrors = error.getReadRangeErrorsList();
      if (rangeErrors.isEmpty()) {
        return;
      }
      for (ReadRangeError rangeError : rangeErrors) {
        Status status = rangeError.getStatus();
        long id = rangeError.getReadId();
        BlobDescriptorStreamRead read = state.getOutstandingRead(id);
        if (read == null) {
          continue;
        }
        executor.execute(
            StorageException.liftToRunnable(
                () -> {
                  read.fail(status);
                  state.removeOutstandingRead(id);
                }));
      }
      reset();
    }

    @Override
    public void onComplete() {}
  }

  private class MonitoringResponseObserver implements ResponseObserver<BidiReadObjectResponse> {
    private final BlobDescriptorStream.BidiReadObjectResponseObserver delegate;
    private final SettableApiFuture<Void> openSignal;
    private final SettableApiFuture<Void> closeSignal;

    private MonitoringResponseObserver(BidiReadObjectResponseObserver delegate) {
      this.delegate = delegate;
      this.openSignal = SettableApiFuture.create();
      this.closeSignal = SettableApiFuture.create();
    }

    @Override
    public void onStart(StreamController controller) {
      delegate.onStart(controller);
    }

    @Override
    public void onResponse(BidiReadObjectResponse response) {
      delegate.onResponse(response);
      openSignal.set(null);
      blobDescriptorResolveFuture.set(null);
    }

    @Override
    public void onError(Throwable t) {
      delegate.onError(t);
      blobDescriptorResolveFuture.setException(t);
      openSignal.setException(t);
      closeSignal.setException(t);
    }

    @Override
    public void onComplete() {
      delegate.onComplete();
      blobDescriptorResolveFuture.set(null);
      openSignal.set(null);
      closeSignal.set(null);
    }
  }

  private final class RedirectHandlingResponseObserver
      implements ResponseObserver<BidiReadObjectResponse> {
    private final ResponseObserver<BidiReadObjectResponse> delegate;

    private RedirectHandlingResponseObserver(ResponseObserver<BidiReadObjectResponse> delegate) {
      this.delegate = delegate;
    }

    @Override
    public void onStart(StreamController controller) {
      delegate.onStart(controller);
    }

    @Override
    public void onResponse(BidiReadObjectResponse response) {
      redirectCounter.set(0);
      delegate.onResponse(response);
    }

    @Override
    public void onError(Throwable t) {
      BidiReadObjectRedirectedError error = GrpcUtils.getBidiReadObjectRedirectedError(t);
      if (error == null) {
        delegate.onError(t);
        return;
      }
      int redirectCount = redirectCounter.incrementAndGet();
      if (redirectCount > maxRedirectsAllowed) {
        // attach the fact we're ignoring the redirect to the original exception as a suppressed
        // Exception. The lower level handler can then perform its usual handling, but if things
        // bubble all the way up to the invoker we'll be able to see it in a bug report.
        t.addSuppressed(new MaxRedirectsExceededException(maxRedirectsAllowed, redirectCount));
        delegate.onError(t);
        return;
      }
      if (error.hasReadHandle()) {
        state.setBidiReadHandle(error.getReadHandle());
      }
      if (error.hasRoutingToken()) {
        state.setRoutingToken(error.getRoutingToken());
      }
      executor.execute(BlobDescriptorStream.this::restart);
    }

    @Override
    public void onComplete() {
      delegate.onComplete();
    }
  }

  static BlobDescriptorStream create(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      GrpcCallContext context,
      BlobDescriptorState state) {

    int maxRedirectsAllowed = 3; // TODO: make this configurable in the ultimate public surface
    return new BlobDescriptorStream(state, executor, callable, context, maxRedirectsAllowed);
  }

  static final class MaxRedirectsExceededException extends RuntimeException {
    private MaxRedirectsExceededException(int maxRedirectAllowed, int actualRedirects) {
      super(
          String.format(
              "max redirects exceeded (max: %d, actual: %d)", maxRedirectAllowed, actualRedirects));
    }
  }
}
