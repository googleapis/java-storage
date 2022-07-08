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

abstract class Crc32cValue<Res extends Crc32cValue<Res>> {

  private Crc32cValue() {}

  public abstract int getValue();

  /**
   * Concatenate {@code other} to {@code this} value.
   *
   * <p>The concat operation satisfies the Left <a target="_blank" rel="noopener noreferrer"
   * href="https://en.wikipedia.org/wiki/Distributive_property">Distributive property</a>.
   *
   * <p>This means, given the following instances:
   *
   * <pre>{@code
   * var A = Crc32cValue.of(a);
   * var B = Crc32cValue.of(b, 4);
   * var C = Crc32cValue.of(c, 4);
   * var D = Crc32cValue.of(d, 4);
   * }</pre>
   *
   * Each of the following lines will all produce the same value:
   *
   * <pre>{@code
   * var ABCD1 = A.concat(B).concat(C).concat(D);
   * var ABCD2 = A.concat(B.concat(C.concat(D)));
   * var ABCD3 = A.concat(B.concat(C)).concat(D);
   * }</pre>
   */
  public abstract Res concat(Crc32cLengthKnown other);

  public abstract String debugString();

  static Crc32cLengthUnknown of(int value) {
    return new Crc32cLengthUnknown(value);
  }

  static Crc32cLengthKnown of(int value, long length) {
    return new Crc32cLengthKnown(value, length);
  }

  public static <Res extends Crc32cValue<Res>> Res nullSafeConcat(Res r1, Crc32cLengthKnown r2) {
    if (r1 == null) {
      return null;
    } else {
      return r1.concat(r2);
    }
  }

  static final class Crc32cLengthUnknown extends Crc32cValue<Crc32cLengthUnknown> {
    private final int value;

    public Crc32cLengthUnknown(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    @Override
    public Crc32cLengthUnknown concat(Crc32cLengthKnown other) {
      int combined = Crc32cUtility.crc32cCombineGoogle(value, other.value, other.length);
      return new Crc32cLengthUnknown(combined);
    }

    @Override
    public String toString() {
      return String.format("crc32c{0x%08x}", value);
    }

    @Override
    public String debugString() {
      return toString();
    }

    public Crc32cLengthKnown withLength(long length) {
      return new Crc32cLengthKnown(value, length);
    }
  }

  static final class Crc32cLengthKnown extends Crc32cValue<Crc32cLengthKnown> {
    private final int value;
    private final long length;

    private Crc32cLengthKnown(int value, long length) {
      this.value = value;
      this.length = length;
    }

    @Override
    public int getValue() {
      return value;
    }

    public long getLength() {
      return length;
    }

    @Override
    public Crc32cLengthKnown concat(Crc32cLengthKnown other) {
      int combined = Crc32cUtility.crc32cCombineGoogle(value, other.value, other.length);
      return new Crc32cLengthKnown(combined, length + other.length);
    }

    @Override
    public String toString() {
      return String.format("crc32c{0x%08x (length = %d)}", value, length);
    }

    @Override
    public String debugString() {
      return String.format("crc32c{0x%08x}", value);
    }
  }
}
