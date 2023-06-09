/*
 * Copyright 2023 Google LLC
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

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.base.MoreObjects;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.RandomDistribution;
import org.checkerframework.checker.nullness.qual.NonNull;

final class RewindableHttpContentPropertyTest {

  @Property
  void path(@ForAll("PathScenario") PathScenario pathScenario) throws Exception {
    try (PathScenario s = pathScenario) {
      RewindableHttpContent content = RewindableHttpContent.of(s.getPath());
      assertThrows(
          IOException.class,
          () -> {
            try (ErroringOutputStream erroringOutputStream =
                new ErroringOutputStream(s.getErrorAtOffset())) {
              content.writeTo(erroringOutputStream);
            }
          });
      content.rewindTo(s.getRewindOffset());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      content.writeTo(baos);

      String actual = xxd(baos.toByteArray());

      assertThat(actual).isEqualTo(s.getExpectedXxd());
    }
  }

  @Provide("PathScenario")
  static Arbitrary<PathScenario> pathScenario() {
    return Arbitraries.lazyOf(
        () ->
            Arbitraries.oneOf(
                    bytes(1, 10),
                    bytes(10, 100),
                    bytes(100, 1_000),
                    bytes(1_000, 10_000),
                    bytes(10_000, 100_000),
                    bytes(100_000, 1_000_000),
                    bytes(1_000_000, 10_000_000))
                .flatMap(
                    bytes ->
                        Combinators.combine(
                                Arbitraries.integers().between(0, bytes.length - 1),
                                Arbitraries.integers().between(0, bytes.length - 1),
                                Arbitraries.just(bytes))
                            .as(PathScenario::of)));
  }

  @NonNull
  private static Arbitrary<byte[]> bytes(int minFileSize, int maxFileSize) {
    return Arbitraries.integers()
        .between(minFileSize, maxFileSize)
        .withDistribution(RandomDistribution.uniform())
        .map(DataGenerator.base64Characters()::genBytes);
  }

  private static final class PathScenario implements AutoCloseable {

    private static final Path TMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));

    private final int rewindOffset;
    private final int errorAtOffset;
    private final TmpFile tmpFile;
    private final byte[] expectedBytes;
    private final String expectedXxd;

    private PathScenario(
        int rewindOffset, int errorAtOffset, TmpFile tmpFile, byte[] expectedBytes) {
      this.rewindOffset = rewindOffset;
      this.errorAtOffset = errorAtOffset;
      this.tmpFile = tmpFile;
      this.expectedBytes = expectedBytes;
      this.expectedXxd = xxd(expectedBytes);
    }

    public int getRewindOffset() {
      return rewindOffset;
    }

    public int getErrorAtOffset() {
      return errorAtOffset;
    }

    public Path getPath() {
      return tmpFile.getPath();
    }

    public String getExpectedXxd() {
      return expectedXxd;
    }

    public long getFullLength() throws IOException {
      return Files.size(tmpFile.getPath());
    }

    @Override
    public void close() throws IOException {
      tmpFile.close();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("expectedXxd", "\n" + expectedXxd)
          .add("expectedBytes.length", expectedBytes.length)
          .add("rewindOffset", rewindOffset)
          .add("errorAtOffset", errorAtOffset)
          .add("tmpFile", tmpFile)
          .toString();
    }

    private static PathScenario of(int rewindOffset, int errorAtOffset, byte[] bytes) {
      try {
        TmpFile tmpFile1 = TmpFile.of(TMP_DIR, "PathScenario", ".bin");
        try (SeekableByteChannel writer = tmpFile1.writer()) {
          writer.write(ByteBuffer.wrap(bytes));
        }
        byte[] expectedBytes =
            Arrays.copyOfRange(bytes, Math.min(rewindOffset, bytes.length), bytes.length);
        return new PathScenario(rewindOffset, errorAtOffset, tmpFile1, expectedBytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static final class ErroringOutputStream extends OutputStream {
    private final long errorAt;
    private long totalWritten;

    ErroringOutputStream(long errorAt) {
      this.errorAt = errorAt;
      this.totalWritten = 0;
    }

    @Override
    public void write(int b) throws IOException {
      if (totalWritten++ >= errorAt) {
        throw new IOException("Reached errorAt limit");
      }
    }

    @Override
    public void write(byte[] b) throws IOException {
      if (totalWritten + b.length >= errorAt) {
        throw new IOException("Reached errorAt limit");
      } else {
        totalWritten += b.length;
      }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      int diff = len - off;
      if (totalWritten + diff >= errorAt) {
        throw new IOException("Reached errorAt limit");
      } else {
        totalWritten += diff;
      }
    }
  }
}
