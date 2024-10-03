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

import static com.google.common.math.LongMath.saturatedAdd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.gax.rpc.OutOfRangeException;
import com.google.common.base.MoreObjects;
import com.google.storage.v2.Object;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlobDescriptorImplTest {

  @Example
  public void argValidation_begin_gtEq0() {
    BlobDescriptorState state = stateWithObjectSize(1);
    assertThrows(
        IllegalArgumentException.class,
        () -> BlobDescriptorImpl.getReadCursor(RangeSpec.of(-1, 0), state));
  }

  @Example
  public void argValidation_begin_ltSize() {
    BlobDescriptorState state = stateWithObjectSize(1);
    assertThrows(
        OutOfRangeException.class,
        () -> BlobDescriptorImpl.getReadCursor(RangeSpec.of(2, 0), state));
  }

  @Example
  public void argValidation_length_gtEq0() {
    BlobDescriptorState state = stateWithObjectSize(1);
    assertThrows(
        IllegalArgumentException.class,
        () -> BlobDescriptorImpl.getReadCursor(RangeSpec.of(0, -1L), state));
  }

  @Property(tries = 100_000)
  void getReadCursor(@ForAll("Scenarios") Scenario s) {
    RangeSpec spec = s.spec;
    long objectSize = s.objectSize;

    BlobDescriptorState state = stateWithObjectSize(objectSize);
    ReadCursor readCursor = BlobDescriptorImpl.getReadCursor(spec, state);

    assertThat(readCursor.begin()).isEqualTo(spec.begin());

    long saturatedAdd = saturatedAdd(spec.begin(), spec.limit().orElse(0L));
    long end = Math.min(saturatedAdd, objectSize);
    assertThat(readCursor.end()).isEqualTo(end);
  }

  @Provide("Scenarios")
  static Arbitrary<Scenario> arbitraryScenario() {
    //noinspection DataFlowIssue
    return Combinators.combine(
            Arbitraries.longs().greaterOrEqual(0), // objectSize
            arbitraryRangeSpec())
        .filter((objectSize, rangeSpec) -> objectSize != null && rangeSpec.begin() < objectSize)
        .as(Scenario::of);
  }

  static Arbitrary<@NonNull RangeSpec> arbitraryRangeSpec() {
    return Combinators.combine(
            // begin
            Arbitraries.oneOf(Arbitraries.longs().greaterOrEqual(0), Arbitraries.just(null)),
            // length
            Arbitraries.oneOf(Arbitraries.longs().greaterOrEqual(0), Arbitraries.just(null)))
        .as(BlobDescriptorImplTest::spec);
  }

  @NonNull
  private static RangeSpec spec(@Nullable Long begin, @Nullable Long length) {
    if (begin == null && length == null) {
      return RangeSpec.all();
    } else if (begin == null) {
      return RangeSpec.all().withLimit(length);
    } else if (length == null) {
      return RangeSpec.beginAt(begin);
    } else {
      return RangeSpec.of(begin, length);
    }
  }

  private static BlobDescriptorState stateWithObjectSize(long objectSize) {
    BlobDescriptorState state = new BlobDescriptorState(null, null);
    state.setMetadata(
        Object.newBuilder()
            .setBucket("projects/_/buckets/b")
            .setName("o")
            .setGeneration(1)
            .setSize(objectSize)
            .build());
    return state;
  }

  private static final class Scenario {

    private final long objectSize;
    private final RangeSpec spec;

    private Scenario(long objectSize, RangeSpec spec) {
      this.spec = spec;
      this.objectSize = objectSize;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("objectSize", objectSize)
          .add("spec", spec)
          .toString();
    }

    private static Scenario of(long objectSize, @NonNull RangeSpec rangeSpec) {
      assertThat(rangeSpec).isNotNull();
      return new Scenario(objectSize, rangeSpec);
    }
  }
}
