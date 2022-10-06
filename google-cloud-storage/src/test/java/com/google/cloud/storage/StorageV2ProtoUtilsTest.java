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

import static com.google.cloud.storage.StorageV2ProtoUtils.fmtProto;
import static com.google.cloud.storage.StorageV2ProtoUtils.seekReadObjectRequest;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.cloud.storage.jqwik.StorageArbitraries;
import com.google.storage.v2.ReadObjectRequest;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public final class StorageV2ProtoUtilsTest {

  @Example
  void validation_nullOffset_effectiveMin() {
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), null, 0L);
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), null, null);
  }

  @Example
  void validation_nullLimit_effectiveMax() {
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), 0L, null);
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), null, null);
  }

  @Example
  void validation_offset_lteq_limit() {
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), 3L, 2L);
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), 0L, 0L);
    seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), 1L, 1L);
  }

  @Example
  void validation_offset_gteq_0() {
    assertThrows(
        IllegalArgumentException.class,
        () -> seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), -1L, null));
  }

  @Example
  void validation_limit_gteq_0() {
    assertThrows(
        IllegalArgumentException.class,
        () -> seekReadObjectRequest(ReadObjectRequest.getDefaultInstance(), null, -1L));
  }

  @Property(tries = 100_000)
  void seek(@ForAll("seekCases") SeekCase srr) {
    Long offset = srr.offset;
    Long limit = srr.limit;

    // I miss pattern matching...
    if (offset == null && limit == null) {
      ReadObjectRequest seek = seekReadObjectRequest(srr.req, offset, limit);
      assertThat(seek).isSameInstanceAs(srr.req);
    } else if (offset != null && limit == null) {
      ReadObjectRequest seek = seekReadObjectRequest(srr.req, offset, limit);
      assertThat(seek.getReadOffset()).isEqualTo(offset);
    } else if (offset == null && limit != null) {
      ReadObjectRequest seek = seekReadObjectRequest(srr.req, offset, limit);
      assertThat(seek.getReadLimit()).isEqualTo(limit);
    } else {
      ReadObjectRequest seek = seekReadObjectRequest(srr.req, offset, limit);
      assertThat(seek.getReadOffset()).isEqualTo(offset);
      assertThat(seek.getReadLimit()).isEqualTo(limit);
    }
  }

  @Provide("seekCases")
  Arbitrary<SeekCase> arbitrarySeekCase() {
    return Combinators.combine(
            StorageArbitraries.objects().name(),
            Arbitraries.longs().greaterOrEqual(0).injectNull(0.6),
            Arbitraries.longs().greaterOrEqual(0).injectNull(0.6),
            Arbitraries.longs().greaterOrEqual(0).injectNull(0.3),
            Arbitraries.longs().greaterOrEqual(0).injectNull(0.3))
        .as(SeekCase::of);
  }

  private static final class SeekCase {
    private final ReadObjectRequest req;
    private final Long offset;
    private final Long limit;

    public SeekCase(ReadObjectRequest req, Long offset, Long limit) {
      this.req = req;
      this.offset = offset;
      this.limit = limit;
    }

    @Override
    public String toString() {
      return "SeekReadRequest{"
          + "req="
          + fmtProto(req)
          + ", offset="
          + offset
          + ", limit="
          + limit
          + '}';
    }

    private static SeekCase of(
        String name, Long embedOffset, Long embedLimit, Long offset, Long limit) {
      ReadObjectRequest.Builder b = ReadObjectRequest.newBuilder().setObject(name);
      if (embedOffset != null) {
        b.setReadOffset(embedOffset);
      }
      if (embedLimit != null) {
        b.setReadLimit(embedLimit);
      }
      return new SeekCase(b.build(), offset, limit);
    }
  }
}
