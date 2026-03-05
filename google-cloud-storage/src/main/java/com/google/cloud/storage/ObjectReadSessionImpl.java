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
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.ReadProjectionConfig.ProjectionType;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.Object;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

final class ObjectReadSessionImpl implements ObjectReadSession {

  private final ScheduledExecutorService executor;
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>
      callable;
  private final ObjectReadSessionStream stream;
  @VisibleForTesting final ObjectReadSessionState state;
  private final Object resource;
  private final RetryContextProvider retryContextProvider;

  private final ConcurrentIdentityMap<ObjectReadSessionStream, ObjectReadSessionState> children;

  private volatile boolean open;

  ObjectReadSessionImpl(
      ScheduledExecutorService executor,
      ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse> callable,
      ObjectReadSessionStream stream,
      ObjectReadSessionState state,
      RetryContextProvider retryContextProvider) {
    this.executor = executor;
    this.callable = callable;
    this.stream = stream;
    this.state = state;
    this.resource = state.getMetadata();
    this.retryContextProvider = retryContextProvider;
    this.children = new ConcurrentIdentityMap<>();
    this.open = true;
  }

  @Override
  public Object getResource() {
    return resource;
  }

  @Override
  public <Projection> Projection readAs(ReadProjectionConfig<Projection> config) {
    checkState(open, "Session already closed");
    switch (config.getType()) {
      case STREAM_READ:
        long readId = state.newReadId();
        ObjectReadSessionStreamRead<Projection> read =
            config.cast().newRead(readId, retryContextProvider.create());
        registerReadInState(readId, read);
        return read.project();
      case SESSION_USER:
        return config.project(this, IOAutoCloseable.noOp());
      default:
        throw new IllegalStateException(
            String.format(
                Locale.US,
                "Broken java enum %s value=%s",
                ProjectionType.class.getName(),
                config.getType().name()));
    }
  }

  @Override
  public <Projection> java.util.List<Projection> readAllAs(
      java.util.List<ReadProjectionConfig<Projection>> configs) {
    checkState(open, "Session already closed");
    java.util.List<Projection> results = new ArrayList<>(configs.size());

    // Filter for STREAM_READ configs to batch
    List<ObjectReadSessionStreamRead<Projection>> batchedReads = new ArrayList<>();
    List<Long> batchedReadIds = new ArrayList<>();

    for (ReadProjectionConfig<Projection> config : configs) {
      switch (config.getType()) {
        case STREAM_READ:
          long readId = state.newReadId();
          ObjectReadSessionStreamRead<Projection> read =
              config.cast().newRead(readId, retryContextProvider.create());
          batchedReads.add(read);
          batchedReadIds.add(readId);
          results.add(read.project());
          break;
        case SESSION_USER:
          results.add(config.project(this, IOAutoCloseable.noOp()));
          break;
        default:
          throw new IllegalStateException(
              String.format(
                  Locale.US,
                  "Broken java enum %s value=%s",
                  ProjectionType.class.getName(),
                  config.getType().name()));
      }
    }

    if (!batchedReads.isEmpty()) {
      registerBatchReadsInState(batchedReadIds, batchedReads);
    }

    return results;
  }

  @Override
  public void close() throws IOException {
    try {
      if (!open) {
        return;
      }
      open = false;
      List<ApiFuture<Void>> closing =
          children.drainEntries((subStream, subStreamState) -> subStream.closeAsync());
      stream.close();
      ApiFutures.allAsList(closing).get();
    } catch (ExecutionException e) {
      throw new IOException(e.getCause());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException();
    }
  }

  private void registerReadInState(long readId, ObjectReadSessionStreamRead<?> read) {
    BidiReadObjectRequest request =
        BidiReadObjectRequest.newBuilder().addReadRanges(read.makeReadRange()).build();
    if (state.canHandleNewRead(read)) {
      state.putOutstandingRead(readId, read);
      stream.send(request);
    } else {
      ObjectReadSessionState child = state.forkChild();
      ObjectReadSessionStream newStream =
          ObjectReadSessionStream.create(executor, callable, child, retryContextProvider.create());
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

  private void registerBatchReadsInState(
      List<Long> readIds, List<? extends ObjectReadSessionStreamRead<?>> reads) {
    // 1. Check if the current state can handle ALL new reads
    //    We assume if it can handle one valid read of the batch, it can handle all,
    //    BUT we must ensure transactionality or consistency if they differ.
    //    However, usually they differ only by offset/length.
    //    Let's check all or check if they are compatible with each other + state.
    //    For now, we check if state can handle the first one?
    //    Correctness: we should check all or rely on the fact they come from same session/context.
    //    If state implies "same generation/metadata", then all reads should be fine if one is fine.
    //    Let's verify strict correctness:
    boolean allCompatible = true;
    for (ObjectReadSessionStreamRead<?> read : reads) {
      if (!state.canHandleNewRead(read)) {
        allCompatible = false;
        break;
      }
    }

    // Prepare Request Builder
    BidiReadObjectRequest.Builder requestBuilder = BidiReadObjectRequest.newBuilder();
    for (ObjectReadSessionStreamRead<?> read : reads) {
      requestBuilder.addReadRanges(read.makeReadRange());
    }
    BidiReadObjectRequest request = requestBuilder.build();

    if (allCompatible) {
      for (int i = 0; i < readIds.size(); i++) {
        state.putOutstandingRead(readIds.get(i), reads.get(i));
      }
      stream.send(request);
    } else {
      // Fork a new child for this BATCH
      ObjectReadSessionState child = state.forkChild();
      ObjectReadSessionStream newStream =
          ObjectReadSessionStream.create(executor, callable, child, retryContextProvider.create());
      children.put(newStream, child);

      // We only need one close callback for the stream closure, so we can attach it to the first read
      // or all? If we attach to all, we might try to close multiple times, which is fine (concurrent map remove).
      IOAutoCloseable closeCallback =
          () -> {
            children.remove(newStream);
            newStream.close();
          };

      for (int i = 0; i < readIds.size(); i++) {
        ObjectReadSessionStreamRead<?> read = reads.get(i);
        read.setOnCloseCallback(closeCallback);
        child.putOutstandingRead(readIds.get(i), read);
      }
      newStream.send(request);
    }
  }

  @VisibleForTesting
  static final class ConcurrentIdentityMap<K, V> {
    private final ReentrantLock lock;
    private final IdentityHashMap<K, V> children;

    @VisibleForTesting
    ConcurrentIdentityMap() {
      lock = new ReentrantLock();
      children = new IdentityHashMap<>();
    }

    public void put(K key, V value) {
      lock.lock();
      try {
        children.put(key, value);
      } finally {
        lock.unlock();
      }
    }

    public void remove(K key) {
      lock.lock();
      try {
        children.remove(key);
      } finally {
        lock.unlock();
      }
    }

    public <R> ArrayList<R> drainEntries(BiFunction<K, V, R> f) {
      lock.lock();
      try {
        Iterator<Entry<K, V>> it = children.entrySet().iterator();
        ArrayList<R> results = new ArrayList<>(children.size());
        while (it.hasNext()) {
          Entry<K, V> entry = it.next();
          K key = entry.getKey();
          V value = entry.getValue();
          it.remove();
          R r = f.apply(key, value);
          results.add(r);
        }
        return results;
      } finally {
        lock.unlock();
      }
    }
  }
}
