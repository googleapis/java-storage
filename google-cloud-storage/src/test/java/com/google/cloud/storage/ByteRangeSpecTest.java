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

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
public final class ByteRangeSpecTest {

  public static final class Behavior {

    @Test
    public void beginNonNullZero_endNonNullNonInfinity() throws Exception {
      ByteRangeSpec rel = ByteRangeSpec.relativeLength(0L, 52L);
      ByteRangeSpec exO = ByteRangeSpec.explicit(0L, 52L);
      ByteRangeSpec exC = ByteRangeSpec.explicitClosed(0L, 51L);

      threeWayEqual(exO, exC, rel);
    }

    @Test
    public void beginNonNullNonZero_endNonNullNonInfinity() throws Exception {
      ByteRangeSpec rel = ByteRangeSpec.relativeLength(10L, 10L);
      ByteRangeSpec exO = ByteRangeSpec.explicit(10L, 20L);
      ByteRangeSpec exC = ByteRangeSpec.explicitClosed(10L, 19L);

      threeWayEqual(exO, exC, rel);
    }

    @Test
    public void beginNull_endNonNullNonInfinity() throws Exception {
      ByteRangeSpec rel = ByteRangeSpec.relativeLength(null, 10L);
      ByteRangeSpec exO = ByteRangeSpec.explicit(null, 10L);
      ByteRangeSpec exC = ByteRangeSpec.explicitClosed(null, 9L);

      threeWayEqual(exO, exC, rel);
    }

    @Test
    public void beginNonNullNonZero_endNull() throws Exception {
      ByteRangeSpec rel = ByteRangeSpec.relativeLength(10L, null);
      ByteRangeSpec exO = ByteRangeSpec.explicit(10L, null);
      ByteRangeSpec exC = ByteRangeSpec.explicitClosed(10L, null);

      threeWayEqual(exO, exC, rel);
    }

    @Test
    public void bothNull_relative() {
      assertThat(ByteRangeSpec.relativeLength(null, null))
          .isSameInstanceAs(ByteRangeSpec.nullRange());
    }

    @Test
    public void bothNull_explicit() {
      assertThat(ByteRangeSpec.explicit(null, null)).isSameInstanceAs(ByteRangeSpec.nullRange());
    }

    @Test
    public void bothNull_explicitClosed() {
      assertThat(ByteRangeSpec.explicitClosed(null, null))
          .isSameInstanceAs(ByteRangeSpec.nullRange());
    }

    @Test
    public void httpRangeHeaderIsCached() {
      ByteRangeSpec relative = ByteRangeSpec.relativeLength(5L, null);

      String header1 = relative.getHttpRangeHeader();
      String header2 = relative.getHttpRangeHeader();

      assertThat(header1).isSameInstanceAs(header2);
    }

    @Test
    public void withNewBeginOffset_sameInstanceIfNotDifferent_relative() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      assertThat(spec.withNewBeginOffset(3L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewBeginOffset_sameInstanceIfNotDifferent_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      assertThat(spec.withNewBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewBeginOffset_sameInstanceIfNotDifferent_leftClosed() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      assertThat(spec.withNewBeginOffset(3L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewBeginOffset_sameInstanceIfNotDifferent_leftClosedRightOpen() {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      assertThat(spec.withNewBeginOffset(3L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewBeginOffset_sameInstanceIfNotDifferent_leftClosedRightClosed() {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      assertThat(spec.withNewBeginOffset(3L)).isSameInstanceAs(spec);
    }

    @Test
    public void withShiftBeginOffset_sameInstanceIfNotDifferent_relative() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      assertThat(spec.withShiftBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withShiftBeginOffset_sameInstanceIfNotDifferent_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      assertThat(spec.withShiftBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withShiftBeginOffset_sameInstanceIfNotDifferent_leftClosed() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      assertThat(spec.withShiftBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withShiftBeginOffset_sameInstanceIfNotDifferent_leftClosedRightOpen() {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      assertThat(spec.withShiftBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withShiftBeginOffset_sameInstanceIfNotDifferent_leftClosedRightClosed() {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      assertThat(spec.withShiftBeginOffset(0)).isSameInstanceAs(spec);
    }

    @Test
    public void withRelativeLength_sameInstanceIfNotDifferent_relative() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      assertThat(spec.withNewRelativeLength(10L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewEndOffset_sameInstanceIfNotDifferent_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      assertThat(spec.withNewEndOffset(RangeScenarios.INF)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewEndOffsetClosed_sameInstanceIfNotDifferent_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      assertThat(spec.withNewEndOffsetClosed(RangeScenarios.INF)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewRelativeLength_sameInstanceIfNotDifferent_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      assertThat(spec.withNewRelativeLength(RangeScenarios.INF)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewEndOffset_sameInstanceIfNotDifferent_leftClosedRightOpen() {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 41L);
      assertThat(spec.withNewEndOffset(41L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewEndOffsetClosed_sameInstanceIfNotDifferent_leftClosedRightClosed() {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 41L);
      assertThat(spec.withNewEndOffsetClosed(41L)).isSameInstanceAs(spec);
    }

    @Test
    public void withNewBeginOffset_relative() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      ByteRangeSpec actual = spec.withNewBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withNewBeginOffset_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      ByteRangeSpec actual = spec.withNewBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withNewBeginOffset_leftClosed() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      ByteRangeSpec actual = spec.withNewBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withNewBeginOffset_leftClosedRightOpen() {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      ByteRangeSpec actual = spec.withNewBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withNewBeginOffset_leftClosedRightClosed() {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      ByteRangeSpec actual = spec.withNewBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withShiftBeginOffset_relative() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      ByteRangeSpec actual = spec.withShiftBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(7);
    }

    @Test
    public void withShiftBeginOffset_null() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      ByteRangeSpec actual = spec.withShiftBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(4);
    }

    @Test
    public void withShiftBeginOffset_leftClosed() {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      ByteRangeSpec actual = spec.withShiftBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(7);
    }

    @Test
    public void withShiftBeginOffset_leftClosedRightOpen() {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      ByteRangeSpec actual = spec.withShiftBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(7);
    }

    @Test
    public void withShiftBeginOffset_leftClosedRightClosed() {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      ByteRangeSpec actual = spec.withShiftBeginOffset(4L);
      assertThat(actual.beginOffset()).isEqualTo(7);
    }

    @Test
    public void withNewEndOffset_relative() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffset(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffset_null() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      ByteRangeSpec actual = spec.withNewEndOffset(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(0),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffset_leftClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      ByteRangeSpec actual = spec.withNewEndOffset(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffset_leftClosedRightOpen() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffset(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffset_leftClosedRightClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffset(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffsetClosed_relative() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffsetClosed(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffsetClosed_null() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      ByteRangeSpec actual = spec.withNewEndOffsetClosed(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(0),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffsetClosed_leftClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      ByteRangeSpec actual = spec.withNewEndOffsetClosed(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffsetClosed_leftClosedRightOpen() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffsetClosed(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewEndOffsetClosed_leftClosedRightClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      ByteRangeSpec actual = spec.withNewEndOffsetClosed(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.endOffset()).isEqualTo(4));
    }

    @Test
    public void withNewRelativeLength_relative() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, 10L);
      ByteRangeSpec actual = spec.withNewRelativeLength(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.length()).isEqualTo(4));
    }

    @Test
    public void withNewRelativeLength_null() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(null, null);
      ByteRangeSpec actual = spec.withNewRelativeLength(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(0),
          () -> assertThat(actual.length()).isEqualTo(4));
    }

    @Test
    public void withNewRelativeLength_leftClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.relativeLength(3L, null);
      ByteRangeSpec actual = spec.withNewRelativeLength(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.length()).isEqualTo(4));
    }

    @Test
    public void withNewRelativeLength_leftClosedRightOpen() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicit(3L, 10L);
      ByteRangeSpec actual = spec.withNewRelativeLength(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.length()).isEqualTo(4));
    }

    @Test
    public void withNewRelativeLength_leftClosedRightClosed() throws Exception {
      ByteRangeSpec spec = ByteRangeSpec.explicitClosed(3L, 10L);
      ByteRangeSpec actual = spec.withNewRelativeLength(4L);
      assertAll(
          () -> assertThat(actual.beginOffset()).isEqualTo(3),
          () -> assertThat(actual.length()).isEqualTo(4));
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void negativeEquals() {
      assertThat(ByteRangeSpec.nullRange().equals("")).isFalse();
    }

    @Test
    public void nullRangeShouldBeASingletonAcrossJavaSerialization()
        throws IOException, ClassNotFoundException {
      ByteRangeSpec orig = ByteRangeSpec.nullRange();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
        oos.writeObject(orig);
      }

      byte[] serializedBytes = baos.toByteArray();
      ByteRangeSpec deserialized;
      try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
          ObjectInputStream ois = new ObjectInputStream(bais)) {
        deserialized = (ByteRangeSpec) ois.readObject();
      }
      assertThat(deserialized).isSameInstanceAs(orig);
    }
  }

  private static void threeWayEqual(
      ByteRangeSpec explicitO, ByteRangeSpec explicitC, ByteRangeSpec relative) throws Exception {

    assertAll(
        () -> assertThat(explicitO).isEqualTo(relative),
        () -> assertThat(explicitO).isEqualTo(explicitC),
        () -> assertThat(explicitC).isEqualTo(relative));
  }

  @RunWith(Parameterized.class)
  public static final class RangeScenarios {

    private static final long INF = Long.MAX_VALUE;
    private final RangeScenario rs;

    public RangeScenarios(RangeScenario rs) {
      this.rs = rs;
    }

    @Test
    public void httpRangeHeader() {
      assertThat(rs.getSpec().getHttpRangeHeader()).isEqualTo(rs.getExpectedHttpRange());
    }

    @Test
    public void beginOffset() {
      assertThat(rs.getSpec().beginOffset()).isEqualTo(rs.getExpectedBeginOffset());
    }

    @Test
    public void endOffset() {
      assertThat(rs.getSpec().endOffset()).isEqualTo(rs.getExpectedEndOffset());
    }

    @Test
    public void length() {
      assertThat(rs.getSpec().length()).isEqualTo(rs.getExpectedLength());
    }

    @Parameters(name = "{0}")
    public static Iterable<Object[]> testCases() {
      Stream<RangeScenario> bothNullOrEmpty =
          Stream.of(
                  ByteRangeSpec.relativeLength(null, null),
                  ByteRangeSpec.explicit(null, null),
                  ByteRangeSpec.explicitClosed(null, null),
                  ByteRangeSpec.relativeLength(0L, null),
                  ByteRangeSpec.explicit(0L, null),
                  ByteRangeSpec.explicitClosed(0L, null),
                  ByteRangeSpec.relativeLength(null, INF),
                  ByteRangeSpec.explicit(null, INF),
                  ByteRangeSpec.explicitClosed(null, INF),
                  ByteRangeSpec.relativeLength(0L, INF),
                  ByteRangeSpec.explicit(0L, INF),
                  ByteRangeSpec.explicitClosed(0L, INF))
              .map(brs -> RangeScenario.of(brs, 0, INF, INF, null));
      Stream<RangeScenario> effectivelyOnlyBegin =
          Stream.of(
                  ByteRangeSpec.relativeLength(3L, null),
                  ByteRangeSpec.explicit(3L, null),
                  ByteRangeSpec.explicitClosed(3L, null),
                  // effective infinity means it should not impact things
                  ByteRangeSpec.relativeLength(3L, INF),
                  ByteRangeSpec.explicit(3L, INF),
                  ByteRangeSpec.explicitClosed(3L, INF))
              .map(brs -> RangeScenario.of(brs, 3, INF, INF, rangeOpen(3)));
      Stream<RangeScenario> effectivelyOnlyEnd =
          Stream.of(
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(null, 31L), 0L, 30L, 31L, rangeClosed(0, 30)),
              RangeScenario.of(ByteRangeSpec.explicit(null, 31L), 0L, 31L, 31L, rangeClosed(0, 30)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(null, 31L), 0L, 31L, 31L, rangeClosed(0, 31)),
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(0L, 31L), 0L, 30L, 31L, rangeClosed(0, 30)),
              RangeScenario.of(ByteRangeSpec.explicit(0L, 31L), 0L, 31L, 31L, rangeClosed(0, 30)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(0L, 31L), 0L, 31L, 31L, rangeClosed(0, 31)));

      Stream<RangeScenario> bothSpecified =
          Stream.of(
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(3L, 15L), 3L, 17L, 15L, rangeClosed(3, 17)),
              RangeScenario.of(ByteRangeSpec.explicit(3L, 15L), 3L, 15L, 12L, rangeClosed(3, 14)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(3L, 15L), 3L, 15L, 12L, rangeClosed(3, 15)));

      long effectiveMax = INF - 1;
      Stream<RangeScenario> edgeCases =
          Stream.of(
              // edge cases near default values
              RangeScenario.of(ByteRangeSpec.relativeLength(1L, null), 1L, INF, INF, rangeOpen(1)),
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(null, effectiveMax),
                  0,
                  effectiveMax - 1,
                  effectiveMax,
                  rangeClosed(0, effectiveMax - 1)),
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(INF, null), INF, INF, INF, rangeOpen(INF)),
              RangeScenario.of(
                  ByteRangeSpec.relativeLength(1L, effectiveMax),
                  1L,
                  effectiveMax,
                  effectiveMax,
                  rangeClosed(1L, effectiveMax)),
              RangeScenario.of(ByteRangeSpec.explicit(1L, null), 1L, INF, INF, rangeOpen(1)),
              RangeScenario.of(
                  ByteRangeSpec.explicit(null, effectiveMax),
                  0,
                  effectiveMax,
                  effectiveMax,
                  rangeClosed(0, effectiveMax - 1)),
              RangeScenario.of(ByteRangeSpec.explicit(INF, null), INF, INF, INF, rangeOpen(INF)),
              RangeScenario.of(
                  ByteRangeSpec.explicit(1L, effectiveMax),
                  1L,
                  effectiveMax,
                  effectiveMax - 1,
                  rangeClosed(1L, effectiveMax - 1)),
              RangeScenario.of(ByteRangeSpec.explicitClosed(1L, null), 1L, INF, INF, rangeOpen(1)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(null, effectiveMax),
                  0,
                  effectiveMax,
                  effectiveMax,
                  rangeClosed(0, effectiveMax)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(INF, null), INF, INF, INF, rangeOpen(INF)),
              RangeScenario.of(
                  ByteRangeSpec.explicitClosed(1L, effectiveMax),
                  1L,
                  effectiveMax,
                  effectiveMax - 1,
                  rangeClosed(1L, effectiveMax)));

      return Streams.concat(
              bothNullOrEmpty, effectivelyOnlyBegin, effectivelyOnlyEnd, bothSpecified, edgeCases)
          .map(rs -> new Object[] {rs})
          .collect(ImmutableList.toImmutableList());
    }
  }

  private static String rangeOpen(long min) {
    return String.format("bytes=%d-", min);
  }

  private static String rangeClosed(long min, long max) {
    return String.format("bytes=%d-%d", min, max);
  }

  private static final class RangeScenario {
    private final ByteRangeSpec spec;
    private final long expectedBeginOffset;
    private final long expectedEndOffset;
    private final long expectedLength;
    @Nullable private final String expectedHttpRange;

    private RangeScenario(
        ByteRangeSpec spec,
        long expectedBeginOffset,
        long expectedEndOffset,
        long expectedLength,
        @Nullable String expectedHttpRange) {
      this.spec = spec;
      this.expectedBeginOffset = expectedBeginOffset;
      this.expectedEndOffset = expectedEndOffset;
      this.expectedLength = expectedLength;
      this.expectedHttpRange = expectedHttpRange;
    }

    public ByteRangeSpec getSpec() {
      return spec;
    }

    public long getExpectedBeginOffset() {
      return expectedBeginOffset;
    }

    public long getExpectedEndOffset() {
      return expectedEndOffset;
    }

    public long getExpectedLength() {
      return expectedLength;
    }

    public @Nullable String getExpectedHttpRange() {
      return expectedHttpRange;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("spec", spec)
          .add("expectedBeginOffset", fmt(expectedBeginOffset))
          .add("expectedEndOffset", fmt(expectedEndOffset))
          .add("expectedLength", fmt(expectedLength))
          .add("expectedHttpRange", expectedHttpRange)
          .toString();
    }

    static RangeScenario of(
        ByteRangeSpec spec,
        long expectedBeginOffset,
        long expectedEndOffset,
        long expectedLength,
        @Nullable String expectedHttpRange) {
      return new RangeScenario(
          spec, expectedBeginOffset, expectedEndOffset, expectedLength, expectedHttpRange);
    }

    private static String fmt(@Nullable Long l) {
      if (l == null) {
        return null;
      }
      return l == Long.MAX_VALUE ? "Long.MAX_VALUE" : l.toString();
    }
  }
}
