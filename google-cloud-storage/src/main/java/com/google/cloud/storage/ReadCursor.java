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

/**
 * Shrink wraps a beginning, offset and limit for tracking state of an individual invocation of
 * {@link #read}
 */
final class ReadCursor {
  private final long beginning;
  private long offset;
  private final long limit;

  ReadCursor(long beginning, long limit) {
    this.limit = limit;
    this.beginning = beginning;
    this.offset = beginning;
  }

  public boolean hasRemaining() {
    return limit - offset > 0;
  }

  public void advance(long incr) {
    checkArgument(incr >= 0);
    offset += incr;
  }

  public long read() {
    return offset - beginning;
  }

  @Override
  public String toString() {
    return String.format("ReadCursor{begin=%d, offset=%d, limit=%d}", beginning, offset, limit);
  }
}
