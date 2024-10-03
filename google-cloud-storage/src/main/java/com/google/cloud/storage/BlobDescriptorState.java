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

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.Object;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BlobDescriptorState {

  private final GrpcCallContext baseContext;
  private final BidiReadObjectRequest openRequest;
  private final AtomicReference<@Nullable BidiReadHandle> bidiReadHandle;
  private final AtomicReference<@Nullable String> routingToken;
  private final AtomicReference<@MonotonicNonNull Object> metadata;
  private final AtomicLong readIdSeq;

  @GuardedBy("this.lock") // https://errorprone.info/bugpattern/GuardedBy
  private final Map<Long, BlobDescriptorStreamRead> outstandingReads;

  private final ReentrantLock lock;

  BlobDescriptorState(
      @NonNull GrpcCallContext baseContext, @NonNull BidiReadObjectRequest openRequest) {
    this.baseContext = baseContext;
    this.openRequest = openRequest;
    this.bidiReadHandle = new AtomicReference<>();
    this.routingToken = new AtomicReference<>();
    this.metadata = new AtomicReference<>();
    this.readIdSeq = new AtomicLong(1);
    this.outstandingReads = new HashMap<>();
    this.lock = new ReentrantLock();
  }

  OpenArguments getOpenArguments() {
    lock.lock();
    try {
      BidiReadObjectRequest.Builder b = openRequest.toBuilder().clearReadRanges();

      Object obj = metadata.get();
      if (obj != null && obj.getGeneration() != openRequest.getReadObjectSpec().getGeneration()) {
        b.getReadObjectSpecBuilder().setGeneration(obj.getGeneration());
      }

      String routingToken = this.routingToken.get();
      if (routingToken != null) {
        b.getReadObjectSpecBuilder().setRoutingToken(routingToken);
      }

      BidiReadHandle bidiReadHandle = this.bidiReadHandle.get();
      if (bidiReadHandle != null) {
        b.getReadObjectSpecBuilder().setReadHandle(bidiReadHandle);
      }

      outstandingReads.values().stream()
          .filter(BlobDescriptorStreamRead::readyToSend)
          .map(BlobDescriptorStreamRead::makeReadRange)
          .forEach(b::addReadRanges);

      return OpenArguments.of(baseContext, b.build());
    } finally {
      lock.unlock();
    }
  }

  void setBidiReadHandle(BidiReadHandle newValue) {
    bidiReadHandle.set(newValue);
  }

  Object getMetadata() {
    return metadata.get();
  }

  void setMetadata(Object metadata) {
    this.metadata.set(metadata);
  }

  long newReadId() {
    return readIdSeq.getAndIncrement();
  }

  @Nullable
  BlobDescriptorStreamRead getOutstandingRead(long key) {
    lock.lock();
    try {
      return outstandingReads.get(key);
    } finally {
      lock.unlock();
    }
  }

  void putOutstandingRead(long key, BlobDescriptorStreamRead value) {
    lock.lock();
    try {
      outstandingReads.put(key, value);
    } finally {
      lock.unlock();
    }
  }

  void removeOutstandingRead(long key) {
    lock.lock();
    try {
      outstandingReads.remove(key);
    } finally {
      lock.unlock();
    }
  }

  <T extends Throwable> OnFailure<T> removeOutstandingReadOnFailure(long key, OnFailure<T> onFail) {
    return t -> {
      removeOutstandingRead(key);
      onFail.onFailure(t);
    };
  }

  void setRoutingToken(String routingToken) {
    this.routingToken.set(routingToken);
  }

  BlobDescriptorStreamRead assignNewReadId(long oldReadId) {
    lock.lock();
    try {
      BlobDescriptorStreamRead remove = outstandingReads.remove(oldReadId);
      checkState(remove != null, "unable to locate old read");
      long newReadId = newReadId();
      BlobDescriptorStreamRead withNewReadId = remove.withNewReadId(newReadId);
      outstandingReads.put(newReadId, withNewReadId);
      return withNewReadId;
    } finally {
      lock.unlock();
    }
  }

  static final class OpenArguments {
    private final GrpcCallContext ctx;
    private final BidiReadObjectRequest req;

    private OpenArguments(GrpcCallContext ctx, BidiReadObjectRequest req) {
      this.ctx = ctx;
      this.req = req;
    }

    public GrpcCallContext getCtx() {
      return ctx;
    }

    public BidiReadObjectRequest getReq() {
      return req;
    }

    public static OpenArguments of(GrpcCallContext ctx, BidiReadObjectRequest req) {
      return new OpenArguments(ctx, req);
    }
  }
}
