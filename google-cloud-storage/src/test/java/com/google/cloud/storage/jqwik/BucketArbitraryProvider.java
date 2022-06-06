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

import com.google.storage.v2.Bucket;
import java.util.Collections;
import java.util.Set;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Tuple;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

public final class BucketArbitraryProvider implements ArbitraryProvider {

  @Override
  public boolean canProvideFor(TypeUsage targetType) {
    return targetType.isOfType(Bucket.class);
  }

  @Override
  public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
    Arbitrary<Bucket> as =
        Combinators.combine(
                Combinators.combine(
                        StorageArbitraries.bucketName(),
                        StorageArbitraries.bucketName(),
                        StorageArbitraries.buckets().storageClass(),
                        StorageArbitraries.buckets().location(),
                        StorageArbitraries.buckets().locationType(),
                        StorageArbitraries.metageneration(),
                        StorageArbitraries.buckets().versioning(),
                        StorageArbitraries.timestamp())
                    .as(Tuple::of),
                Combinators.combine(
                        StorageArbitraries.timestamp(),
                        StorageArbitraries.buckets().website(),
                        StorageArbitraries.bool(),
                        StorageArbitraries.buckets().rpo(),
                        StorageArbitraries.buckets().billing(),
                        StorageArbitraries.buckets().encryption(),
                        StorageArbitraries.buckets().retentionPolicy(),
                        StorageArbitraries.buckets().lifecycle())
                    .as(Tuple::of),
                Combinators.combine(
                        StorageArbitraries.buckets().logging(),
                        StorageArbitraries.buckets().cors(),
                        StorageArbitraries.buckets().objectAccessControl(),
                        StorageArbitraries.buckets().owner(),
                        StorageArbitraries.buckets().iamConfig())
                    .as(Tuple::of))
            .as(
                (t1, t2, t3) ->
                    Bucket.newBuilder()
                        .setBucketId(t1.get1().get())
                        .setName(t1.get2().get())
                        .setStorageClass(t1.get3())
                        .setLocation(t1.get4())
                        .setLocationType(t1.get5())
                        .setMetageneration(t1.get6())
                        .setVersioning(t1.get7())
                        .setCreateTime(t1.get8())
                        .setUpdateTime(t2.get1())
                        .setWebsite(t2.get2())
                        .setDefaultEventBasedHold(t2.get3())
                        .setRpo(t2.get4())
                        .setBilling(t2.get5())
                        .setEncryption(t2.get6())
                        .setRetentionPolicy(t2.get7())
                        .setLifecycle(t2.get8())
                        .setLogging(t3.get1())
                        .addAllCors(t3.get2())
                        .addAllDefaultObjectAcl(t3.get3())
                        .setOwner(t3.get4())
                        .setIamConfig(t3.get5())
                        .build());
    return Collections.singleton(as);
  }
}
