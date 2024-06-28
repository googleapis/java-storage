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

import com.google.cloud.storage.BlobDescriptorImpl.OutstandingReadToArray;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.Object;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class BlobDescriptorState {

  private final BidiReadObjectRequest openRequest;
  private final AtomicReference<BidiReadHandle> bidiReadHandle;
  private final AtomicReference<Object> metadata;
  private final AtomicLong readIdSeq;
  private final Map<Long, OutstandingReadToArray> outstandingReads;

  BlobDescriptorState(BidiReadObjectRequest openRequest) {
    this.openRequest = openRequest;
    this.bidiReadHandle = new AtomicReference<>();
    this.metadata = new AtomicReference<>();
    this.readIdSeq = new AtomicLong(1);
    this.outstandingReads = new ConcurrentHashMap<>();
  }

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

  OutstandingReadToArray getOutstandingRead(long key) {
    return outstandingReads.get(key);
  }

  void putOutstandingRead(long key, OutstandingReadToArray value) {
    outstandingReads.put(key, value);
  }

  void removeOutstandingRead(long key) {
    outstandingReads.remove(key);
  }
}
