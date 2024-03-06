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
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.concurrent.Immutable;
import org.checkerframework.checker.nullness.qual.Nullable;

interface Hasher {

  @Nullable
  default Crc32cLengthKnown hash(Supplier<ByteBuffer> b) {
    return hash(b.get());
  }

  @Nullable
  Crc32cLengthKnown hash(ByteBuffer b);

  void validate(Crc32cValue<?> expected, Supplier<ByteBuffer> b) throws IOException;

  void validate(Crc32cValue<?> expected, List<ByteBuffer> buffers) throws IOException;

  @Nullable
  Crc32cLengthKnown nullSafeConcat(Crc32cLengthKnown r1, Crc32cLengthKnown r2);

  static Hasher noop() {
    return NoOpHasher.INSTANCE;
  }

  static Hasher enabled() {
    return GuavaHasher.INSTANCE;
  }

  @Immutable
  class NoOpHasher implements Hasher {
    private static final NoOpHasher INSTANCE = new NoOpHasher();

    private NoOpHasher() {}

    @Override
    public Crc32cLengthKnown hash(ByteBuffer b) {
      return null;
    }

    @Override
    public void validate(Crc32cValue<?> expected, Supplier<ByteBuffer> b) {}

    @Override
    public void validate(Crc32cValue<?> expected, List<ByteBuffer> b) {}

    @Override
    public @Nullable Crc32cLengthKnown nullSafeConcat(Crc32cLengthKnown r1, Crc32cLengthKnown r2) {
      return null;
    }
  }

  @Immutable
  class GuavaHasher implements Hasher {
    private static final GuavaHasher INSTANCE = new GuavaHasher();

    private GuavaHasher() {}

    @Override
    public Crc32cLengthKnown hash(ByteBuffer b) {
      int remaining = b.remaining();
      return Crc32cValue.of(Hashing.crc32c().hashBytes(b).asInt(), remaining);
    }

    @SuppressWarnings({"ConstantConditions", "UnstableApiUsage"})
    @Override
    public void validate(Crc32cValue<?> expected, List<ByteBuffer> b) throws IOException {
      long remaining = 0;
      com.google.common.hash.Hasher crc32c = Hashing.crc32c().newHasher();
      for (ByteBuffer tmp : b) {
        remaining += tmp.remaining();
        crc32c.putBytes(tmp);
      }
      Crc32cLengthKnown actual = Crc32cValue.of(crc32c.hash().asInt(), remaining);
      if (!actual.eqValue(expected)) {
        throw new IOException(
            String.format(
                "Mismatch checksum value. Expected %s actual %s",
                expected.debugString(), actual.debugString()));
      }
    }

    @Override
    public void validate(Crc32cValue<?> expected, Supplier<ByteBuffer> b) throws IOException {
      Crc32cLengthKnown actual = hash(b);
      if (!actual.eqValue(expected)) {
        throw new IOException(
            String.format(
                "Mismatch checksum value. Expected %s actual %s",
                expected.debugString(), actual.debugString()));
      }
    }

    @Override
    @Nullable
    public Crc32cLengthKnown nullSafeConcat(Crc32cLengthKnown r1, Crc32cLengthKnown r2) {
      if (r1 == null) {
        return r2;
      } else {
        return r1.concat(r2);
      }
    }
  }
}
