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

import com.google.common.hash.Hashing;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;

public class Crc32cUtilityPropertyTest {
  @Example
  public void testCrc32cCombinePropertyTest(
      @ForAll("randomData") String firstObject, @ForAll("randomData") String secondObject) {
    int firstPartHash = Hashing.crc32c().hashBytes(firstObject.getBytes()).asInt();
    int secondPartHash = Hashing.crc32c().hashBytes(secondObject.getBytes()).asInt();
    String mergedParts = firstObject + secondObject;
    int combined = Hashing.crc32c().hashBytes(mergedParts.getBytes()).asInt();
    assertThat(combined)
        .isEqualTo(
            Crc32cUtility.crc32cCombineGoogle(
                firstPartHash, secondPartHash, secondObject.getBytes().length));
  }

  @Provide("randomData")
  Arbitrary<String> stringArrays() {
    return Arbitraries.strings().ofMinLength(0).ofMaxLength(1024 * 1024);
  }
}
