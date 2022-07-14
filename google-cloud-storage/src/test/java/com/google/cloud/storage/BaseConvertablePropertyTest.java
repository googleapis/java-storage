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

import static com.google.cloud.storage.JqwikTest.report;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Conversions.Codec;
import com.google.protobuf.Message;
import java.util.Optional;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.providers.TypeUsage;

abstract class BaseConvertablePropertyTest<ModelT, ProtoT extends Message> {

  /** Provide the codec instance used to convert between {@code ModelT} and {@code ProtoT} */
  abstract Codec<ModelT, ProtoT> codec();

  /** Report on detected edge cases for {@code ProtoT} */
  @Example
  final void edgeCases() {
    TypeUsage baseTypeUsage = findBaseTypeUsage(this.getClass());
    TypeUsage protoTType = baseTypeUsage.getTypeArgument(1);
    if (!CIUtils.isRunningOnGitHubActions() || CIUtils.isJobTypeIntegration()) {
      report(protoTType);
    }
  }

  /**
   * Ensure that {@code @ForAll ProtoT} the codec provided by {@link #codec} can round trip each
   * {@code ProtoT} such that the provided value is equal to the round tripped value.
   *
   * <p>Note: round trip means A -> B -> A, in this case ProtoT -> ModelT -> ProtoT
   */
  @Property
  final void codecRoundTrip(@ForAll ProtoT p) {
    Codec<ModelT, ProtoT> codec = codec();
    ModelT model = codec.decode(p);
    ProtoT proto = codec.encode(model);

    assertThat(p).isEqualTo(proto);
  }

  private static TypeUsage findBaseTypeUsage(
      @SuppressWarnings("rawtypes") Class<? extends BaseConvertablePropertyTest> c) {
    TypeUsage curr = TypeUsage.of(c);
    while (curr.getRawType() != BaseConvertablePropertyTest.class) {
      Optional<TypeUsage> superclass = curr.getSuperclass();
      if (!superclass.isPresent()) {
        throw new IllegalStateException(
            "Unable to locate base class" + BaseConvertablePropertyTest.class.getName());
      }
      curr = superclass.get();
    }
    return curr;
  }
}
