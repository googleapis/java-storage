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

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Utility methods for working by various buffers.
 *
 * <p>Several methods are copied from {@link com.google.common.base.Java8Compatibility} which is
 * package private
 */
final class Buffers {

  private Buffers() {}

  static void clear(Buffer b) {
    b.clear();
  }

  static void flip(Buffer b) {
    b.flip();
  }

  static void limit(Buffer b, int limit) {
    b.limit(limit);
  }

  static void position(Buffer b, int position) {
    b.position(position);
  }

  /**
   * attempt to drain all of {@code content} into {@code dsts} starting from {@code dsts[0]} through
   * {@code dsts[dsts.length - 1]}
   */
  static long copy(ByteBuffer content, ByteBuffer[] dsts) {
    return copy(content, dsts, 0, dsts.length);
  }

  /**
   * attempt to drain all of `content` into `dsts` starting from `dsts[offset]` through
   * `dsts[length]`
   */
  static long copy(ByteBuffer content, ByteBuffer[] dsts, int offset, int length) {
    long total = 0;
    for (int i = offset; i < length; i++) {
      int contentRemaining = content.remaining();
      if (contentRemaining <= 0) {
        break;
      }
      ByteBuffer buf = dsts[i];
      int bufRemaining = buf.remaining();
      if (bufRemaining == 0) {
        continue;
      } else if (bufRemaining < contentRemaining) {
        ByteBuffer sub = content.duplicate();
        int newLimit = sub.position() + bufRemaining;
        sub.limit(newLimit);
        buf.put(sub);
        Buffers.position(content, newLimit);
      } else {
        buf.put(content);
      }
      int written = bufRemaining - buf.remaining();
      total += written;
    }
    return total;
  }
}
