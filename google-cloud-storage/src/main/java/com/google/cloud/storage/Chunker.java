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
import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import org.checkerframework.checker.nullness.qual.Nullable;

// TODO: Better name, externally we're treating a chunk to mean a set of messages for which
//   they all must commit before proceeding.
final class Chunker {

  static Data[] chunkIt(ByteBuffer[] bbs, Hasher hasher, ByteStringStrategy bss, int chunkSize) {
    return chunkIt(bbs, hasher, bss, chunkSize, 0, bbs.length);
  }

  static Data[] chunkIt(
      ByteBuffer[] bbs,
      Hasher hasher,
      ByteStringStrategy bss,
      int chunkSize,
      int offset,
      int length) {
    Deque<Data> data = new ArrayDeque<>();

    for (int i = offset; i < length; i++) {
      ByteBuffer buffer = bbs[i];
      int remaining;
      while ((remaining = buffer.remaining()) > 0) {
        // either no chunk or most recent chunk is full, start a new one
        Data peekLast = data.peekLast();
        if (peekLast == null || peekLast.b.size() == chunkSize) {
          int limit = Math.min(remaining, chunkSize);
          Data datum = getData(buffer, hasher, bss, limit);
          data.addLast(datum);
        } else {
          Data chunkSoFar = data.pollLast();
          //noinspection ConstantConditions -- covered by peekLast check above
          int limit = Math.min(remaining, chunkSize - chunkSoFar.b.size());
          Data datum = getData(buffer, hasher, bss, limit);
          Data plus = chunkSoFar.concat(datum);
          data.addLast(plus);
        }
      }
    }

    return data.toArray(new Data[0]);
  }

  private static Data getData(ByteBuffer buffer, Hasher hasher, ByteStringStrategy bss, int limit) {
    final ByteBuffer slice = buffer.slice();
    slice.limit(limit);

    Crc32cLengthKnown hash = hasher.hash(slice::duplicate);
    ByteString byteString = bss.apply(slice);
    Buffers.position(buffer, buffer.position() + limit);

    return new Data(byteString, hash);
  }

  static final class Data {
    private final ByteString b;
    @Nullable private final Crc32cLengthKnown crc32c;

    public Data(ByteString b, @Nullable Crc32cLengthKnown crc32c) {
      this.b = b;
      this.crc32c = crc32c;
    }

    public Data concat(Data other) {
      Crc32cLengthKnown newCrc = null;
      if (crc32c != null && other.crc32c != null) {
        newCrc = crc32c.concat(other.crc32c);
      }
      return new Data(b.concat(other.b), newCrc);
    }

    public ByteString getB() {
      return b;
    }

    @Nullable
    public Crc32cLengthKnown getCrc32c() {
      return crc32c;
    }
  }
}
