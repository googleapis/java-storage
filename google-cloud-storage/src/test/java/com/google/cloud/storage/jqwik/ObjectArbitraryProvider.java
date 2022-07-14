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

package com.google.cloud.storage.jqwik;

import com.google.storage.v2.Object;
import java.util.Collections;
import java.util.Set;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Tuple;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

public final class ObjectArbitraryProvider implements ArbitraryProvider {

  @Override
  public boolean canProvideFor(TypeUsage targetType) {
    return targetType.isOfType(Object.class);
  }

  @Override
  public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
    Arbitrary<Integer> size = Arbitraries.integers().greaterOrEqual(0);
    Arbitrary<Object> objectArbitrary =
        Combinators.combine(
                Combinators.combine(
                        StorageArbitraries.objects().name(),
                        StorageArbitraries.buckets().name(),
                        StorageArbitraries.generation(),
                        StorageArbitraries.metageneration(),
                        StorageArbitraries.storageClass(),
                        size,
                        StorageArbitraries.httpHeaders().contentEncoding(),
                        StorageArbitraries.httpHeaders().contentDisposition())
                    .as(Tuple::of),
                Combinators.combine(
                        StorageArbitraries.httpHeaders().cacheControl(),
                        StorageArbitraries.httpHeaders().contentLanguage(),
                        StorageArbitraries.timestamp(), // dtime
                        StorageArbitraries.httpHeaders().contentType(),
                        StorageArbitraries.timestamp(), // ctime
                        // componentCount is populated if the object is made from compose
                        Arbitraries.integers().greaterOrEqual(0).injectNull(0.85),
                        StorageArbitraries.objects().objectChecksumsArbitrary())
                    .as(Tuple::of),
                Combinators.combine(
                        StorageArbitraries.timestamp(), // utime
                        StorageArbitraries.randomString(),
                        StorageArbitraries.timestamp(), // UpdateStorageClassTime
                        StorageArbitraries.bool(),
                        StorageArbitraries.timestamp(), // RetentionExpireTime
                        StorageArbitraries.bool(),
                        StorageArbitraries.objects().customerEncryptionArbitrary(),
                        StorageArbitraries.httpHeaders().customTime())
                    .as(Tuple::of),
                Combinators.combine(
                        StorageArbitraries.objects().customMetadata(),
                        StorageArbitraries.owner(),
                        StorageArbitraries.objects().objectAccessControl())
                    .as(Tuple::of))
            .as(
                (t1, t2, t3, t4) ->
                    Object.newBuilder()
                        .setName(t1.get1())
                        .setBucket(t1.get2().toString())
                        .setGeneration(t1.get3())
                        .setMetageneration(t1.get4())
                        .setStorageClass(t1.get5())
                        .setSize(t1.get6())
                        .setContentEncoding(t1.get7())
                        .setContentDisposition(t1.get8())
                        .setCacheControl(t2.get1())
                        .addAllAcl(t4.get3())
                        .setContentLanguage(t2.get2())
                        .setDeleteTime(t2.get3())
                        .setContentType(t2.get4())
                        .setCreateTime(t2.get5())
                        .setComponentCount(t2.get6())
                        .setChecksums(t2.get7())
                        .setUpdateTime(t3.get1())
                        .setKmsKey(t3.get2())
                        .setUpdateStorageClassTime(t3.get3())
                        .setTemporaryHold(t3.get4())
                        .setRetentionExpireTime(t3.get5())
                        .putAllMetadata(t4.get1())
                        .setEventBasedHold(t3.get6())
                        .setOwner(t4.get2())
                        .setCustomerEncryption(t3.get7())
                        .setCustomTime(t3.get8())
                        .build());
    return Collections.singleton(objectArbitrary);
  }
}
