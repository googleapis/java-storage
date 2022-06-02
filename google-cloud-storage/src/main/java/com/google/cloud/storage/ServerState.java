/*
 * Copyright 2022 Google LLC
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

import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class ServerState {

  private final ResumableWrite resumableWrite;
  private final AtomicLong totalSentBytes;
  private final AtomicLong confirmedBytes;
  private final AtomicReference<Crc32cLengthKnown> cumulativeCrc32c;

  public ServerState(ResumableWrite resumableWrite) {
    this.resumableWrite = resumableWrite;
    totalSentBytes = new AtomicLong(0);
    confirmedBytes = new AtomicLong(0);
    cumulativeCrc32c = new AtomicReference<>();
  }

  public ResumableWrite getStart() {
    return resumableWrite;
  }

  public AtomicLong getTotalSentBytes() {
    return totalSentBytes;
  }

  public AtomicLong getConfirmedBytes() {
    return confirmedBytes;
  }

  public AtomicReference<Crc32cLengthKnown> getCumulativeCrc32c() {
    return cumulativeCrc32c;
  }

  // TODO: flush this out more
  boolean isDirty() {
    return confirmedBytes.get() == totalSentBytes.get();
  }

  @Override
  public String toString() {
    return "ServerState{"
        + "start="
        + resumableWrite
        + ", totalSentBytes="
        + totalSentBytes
        + ", confirmedBytes="
        + confirmedBytes
        + ", totalSentCrc32c="
        + cumulativeCrc32c
        + '}';
  }
}
