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

import com.google.cloud.storage.BucketInfo.BucketWithProject;
import com.google.cloud.storage.Conversions.Codec;
import com.google.cloud.storage.jqwik.StorageArbitraries;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

final class BucketWithProjectPropertyTest {

  static final Codec<BucketWithProject, String> codec = Utils.bucketWithProjectCodec;

  @Example
  void debugging() {
    x("xyz");
  }

  @Property
  void x(@ForAll("x") String s) {
    BucketWithProject decode = codec.decode(s);
    String encode = codec.encode(decode);

    assertThat(encode).isEqualTo(s);
  }

  @Provide("x")
  Arbitrary<String> xx() {
    return Arbitraries.oneOf(
        Arbitraries.of("xyz"),
        Combinators.combine(StorageArbitraries.buckets().name(), StorageArbitraries.bool())
            .as(
                (name, stripsResourcePath) -> {
                  if (stripsResourcePath) {
                    return name.getBucket();
                  } else {
                    return name.toString();
                  }
                }));
  }
}
