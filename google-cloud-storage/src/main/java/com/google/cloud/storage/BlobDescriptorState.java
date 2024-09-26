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

import com.google.common.collect.ImmutableList;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadRange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BlobDescriptorState {

  private final BidiReadObjectRequest openRequest;
  private final AtomicReference<@Nullable BidiReadHandle> bidiReadHandle;
  private final AtomicReference<@Nullable String> routingToken;
  private final AtomicReference<@MonotonicNonNull Object> metadata;
  private final AtomicLong readIdSeq;
  private final Map<Long, BlobDescriptorStreamRead> outstandingReads;

  BlobDescriptorState(BidiReadObjectRequest openRequest) {
    this.openRequest = openRequest;
    this.bidiReadHandle = new AtomicReference<>();
    this.routingToken = new AtomicReference<>();
    this.metadata = new AtomicReference<>();
    this.readIdSeq = new AtomicLong(1);
    this.outstandingReads = new HashMap<>();
  }

  BidiReadObjectRequest getOpenRequest() {
    return openRequest;
  }

  @Nullable
  BidiReadHandle getBidiReadHandle() {
    return bidiReadHandle.get();
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
    synchronized (this) {
      return outstandingReads.get(key);
    }
  }

  void putOutstandingRead(long key, BlobDescriptorStreamRead value) {
    synchronized (this) {
      outstandingReads.put(key, value);
    }
  }

  void removeOutstandingRead(long key) {
    synchronized (this) {
      outstandingReads.remove(key);
    }
  }

  void setRoutingToken(String routingToken) {
    this.routingToken.set(routingToken);
  }

  @Nullable
  String getRoutingToken() {
    return this.routingToken.get();
  }

  BlobDescriptorStreamRead assignNewReadId(long oldReadId) {
    synchronized (this) {
      BlobDescriptorStreamRead remove = outstandingReads.remove(oldReadId);
      checkState(remove != null, "unable to locate old");
      long newReadId = newReadId();
      BlobDescriptorStreamRead withNewReadId = remove.withNewReadId(newReadId);
      outstandingReads.put(newReadId, withNewReadId);
      return withNewReadId;
    }
  }

  List<ReadRange> getOutstandingReads() {
    synchronized (this) {
      return outstandingReads.values().stream()
          .filter(BlobDescriptorStreamRead::readyToSend)
          .map(BlobDescriptorStreamRead::makeReadRange)
          .collect(ImmutableList.toImmutableList());
    }
  }
}
