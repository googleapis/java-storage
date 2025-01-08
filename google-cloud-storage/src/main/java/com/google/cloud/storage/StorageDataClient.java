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
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

final class StorageDataClient implements IOAutoCloseable {

  private final ScheduledExecutorService executor;
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> read;
  private final RetryContextProvider retryContextProvider;
  private final IOAutoCloseable onClose;

  private StorageDataClient(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> read,
      RetryContextProvider retryContextProvider,
      IOAutoCloseable onClose) {
    this.executor = executor;
    this.read = read;
    this.retryContextProvider = retryContextProvider;
    this.onClose = onClose;
  }

  ApiFuture<ObjectReadSession> readSession(BidiReadObjectRequest req, GrpcCallContext ctx) {
    checkArgument(
        req.getReadRangesList().isEmpty(),
        "ranged included in the initial request are not supported");
    return ObjectReadSessionImpl.create(req, ctx, read, executor, retryContextProvider);
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
}
