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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.RandomDistribution;

final class ChunkSegmenterTest {
  private static final int _2MiB = 2 * 1024 * 1024;

  @Property
  void chunkIt(@ForAll("TestData") TestData td) {
    System.out.println("td = " + td);

    ChunkSegment[] data =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.noCopy(), td.chunkSize)
            .segmentBuffers(td.buffers);

    long dataTotalSize = Arrays.stream(data).mapToLong(d -> d.getB().size()).sum();
    Optional<Crc32cLengthKnown> reduce =
        Arrays.stream(data)
            .map(ChunkSegment::getCrc32c)
            .filter(Objects::nonNull)
            .reduce(Crc32cValue::concat);

    assertThat(dataTotalSize).isEqualTo(td.totalSize);
    assertThat(data).hasLength(td.expectedChunkCount);
    assertThat(reduce).isAnyOf(Optional.empty(), Optional.of(Crc32cValue.of(td.allCrc32c.asInt())));
  }

  @Provide("TestData")
  static Arbitrary<TestData> arbitraryTestData() {
    return Arbitraries.lazyOf(
            () ->
                Arbitraries.lazyOf(
                        () ->
                            Arbitraries.integers()
                                .greaterOrEqual(1)
                                .lessOrEqual(8 * 1024 * 1024)
                                .withDistribution(RandomDistribution.uniform()))
                    .map(DataGenerator.base64Characters()::genBytes)
                    .array(byte[][].class)
                    .ofMinSize(0)
                    .ofMaxSize(10)
                    .withSizeDistribution(RandomDistribution.uniform()))
        .map(TestData::create);
  }

  static final class TestData {
    private final int chunkSize;
    private final long totalSize;
    private final int expectedChunkCount;
    private final byte[][] originalData;
    private final ByteBuffer[] buffers;
    private final HashCode allCrc32c;

    private TestData(
        long totalSize,
        int expectedChunkCount,
        byte[][] originalData,
        ByteBuffer[] buffers,
        HashCode allCrc32c,
        int chunkSize) {
      this.totalSize = totalSize;
      this.expectedChunkCount = expectedChunkCount;
      this.originalData = originalData;
      this.buffers = buffers;
      this.allCrc32c = allCrc32c;
      this.chunkSize = chunkSize;
    }

    @Override
    public String toString() {
      return "TestData{"
          + "chunkSize="
          + fmt(chunkSize)
          + ", totalSize="
          + fmt(totalSize)
          + ", expectedChunkCount="
          + fmt(expectedChunkCount)
          + ", allCrc32c="
          + allCrc32c
          + ", originalDataLengths="
          + Arrays.toString(
              Arrays.stream(originalData).mapToInt(x -> x.length).mapToObj(TestData::fmt).toArray())
          + '}';
    }

    @SuppressWarnings("UnstableApiUsage")
    static TestData create(byte[][] bs) {
      long totalSize = 0;
      HashCode allCrc32c;
      int expectedChunkCount;
      com.google.common.hash.Hasher hasher = Hashing.crc32c().newHasher();
      for (byte[] bb : bs) {
        totalSize += bb.length;
        hasher.putBytes(bb);
      }
      allCrc32c = hasher.hash();

      int chunkSize = _2MiB;
      expectedChunkCount = Math.toIntExact(totalSize / chunkSize);
      if (totalSize % chunkSize != 0) {
        expectedChunkCount++;
      }

      ByteBuffer[] bbs = Arrays.stream(bs).map(ByteBuffer::wrap).toArray(ByteBuffer[]::new);

      return new TestData(totalSize, expectedChunkCount, bs, bbs, allCrc32c, chunkSize);
    }

    static String fmt(int i) {
      return String.format("0x%08x", i);
    }

    static String fmt(long i) {
      return String.format("0x%016x", i);
    }
  }
}
