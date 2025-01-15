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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.ObjectReadSessionState.OpenArguments;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

final class StorageDataClient implements IOAutoCloseable {

  private final ScheduledExecutorService executor;
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>
      bidiReadObject;
  private final RetryContextProvider retryContextProvider;
  private final IOAutoCloseable onClose;

  private StorageDataClient(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> bidiReadObject,
      RetryContextProvider retryContextProvider,
      IOAutoCloseable onClose) {
    this.executor = executor;
    this.bidiReadObject = bidiReadObject;
    this.retryContextProvider = retryContextProvider;
    this.onClose = onClose;
  }

  ApiFuture<ObjectReadSession> readSession(BidiReadObjectRequest req, GrpcCallContext ctx) {
    checkArgument(
        req.getReadRangesList().isEmpty(),
        "ranges included in the initial request are not supported");
    ObjectReadSessionState state = new ObjectReadSessionState(ctx, req);

    ObjectReadSessionStream stream =
        ObjectReadSessionStream.create(
            executor, bidiReadObject, state, retryContextProvider.create());

    ApiFuture<ObjectReadSession> objectReadSessionFuture =
        ApiFutures.transform(
            stream,
            nowOpen ->
                new ObjectReadSessionImpl(
                    executor, bidiReadObject, stream, state, retryContextProvider),
            executor);
    stream.send(req);
    return objectReadSessionFuture;
  }

  <Projection> ApiFuture<FastOpenObjectReadSession<Projection>> fastOpenReadSession(
      BidiReadObjectRequest openRequest,
      GrpcCallContext ctx,
      RangeSpec range,
      RangeProjectionConfig<Projection> config) {
    checkArgument(
        openRequest.getReadRangesList().isEmpty(),
        "ranges included in the initial request are not supported");
    ObjectReadSessionState state = new ObjectReadSessionState(ctx, openRequest);

    ObjectReadSessionStream stream =
        ObjectReadSessionStream.create(
            executor, bidiReadObject, state, retryContextProvider.create());

    long readId = state.newReadId();
    ObjectReadSessionStreamRead<Projection, ?> read =
        config.cast().newRead(readId, range, retryContextProvider.create());
    state.putOutstandingRead(readId, read);

    ApiFuture<FastOpenObjectReadSession<Projection>> objectReadSessionFuture =
        ApiFutures.transform(
            stream,
            nowOpen ->
                new FastOpenObjectReadSession<>(
                    new ObjectReadSessionImpl(
                        executor, bidiReadObject, stream, state, retryContextProvider),
                    read),
            executor);
    OpenArguments openArguments = state.getOpenArguments();
    BidiReadObjectRequest req = openArguments.getReq();
    stream.send(req);
    read.setOnCloseCallback(stream);
    return objectReadSessionFuture;
  }

  @Override
  public void close() throws IOException {
    //noinspection EmptyTryBlock
    try (IOAutoCloseable ignore = onClose) {
      // intentional
    }
  }

  static StorageDataClient create(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> read,
      RetryContextProvider retryContextProvider,
      IOAutoCloseable onClose) {
    return new StorageDataClient(executor, read, retryContextProvider, onClose);
  }

  static final class FastOpenObjectReadSession<Projection> implements IOAutoCloseable {
    private final ObjectReadSession session;
    private final ObjectReadSessionStreamRead<Projection, ?> read;

    private FastOpenObjectReadSession(
        ObjectReadSession session, ObjectReadSessionStreamRead<Projection, ?> read) {
      this.session = session;
      this.read = read;
    }

    ObjectReadSession getSession() {
      return session;
    }

    ObjectReadSessionStreamRead<Projection, ?> getRead() {
      return read;
    }

    Projection getProjection() {
      return read.project();
    }

    @Override
    public void close() throws IOException {
      //noinspection EmptyTryBlock
      try (IOAutoCloseable ignore1 = session;
          IOAutoCloseable ignore2 = read) {
        // use try-with to ensure full cleanup
      }
    }

    public static <Projection> FastOpenObjectReadSession<Projection> of(
        ObjectReadSession session, ObjectReadSessionStreamRead<Projection, ?> read) {
      return new FastOpenObjectReadSession<>(session, read);
    }
  }
}
