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
import net.jqwik.api.Arbitraries;
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

    Arbitrary<String> bucketID = Arbitraries.strings().all().ofMinLength(0).ofLength(1024);
    Arbitrary<String> bucketName = Arbitraries.strings().all().ofMinLength(0).ofLength(1024);
    Arbitrary<String> storageClass = Arbitraries.strings().all().ofMinLength(1).ofLength(1024);
    Arbitrary<String> location = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<String> locationType = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<Long> metageneration = Arbitraries.longs().between(0, 100000L);
    Arbitrary<Boolean> versioning = Arbitraries.defaultFor(TypeUsage.of(Boolean.class));
    Arbitrary<Long> createTime = Arbitraries.longs().between(100000L, 100000000L);
    Arbitrary<Tuple.Tuple8> baseMetadata =
        Combinators.combine(
                bucketID,
                bucketName,
                storageClass,
                location,
                locationType,
                metageneration,
                versioning,
                createTime)
            .as(Tuple::of);

    Arbitrary<Long> updateTime = Arbitraries.longs().between(100000L, 100000000L);
    Arbitrary<String> indexPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<String> notFoundPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<Boolean> defaultEventBasedHold = Arbitraries.defaultFor(TypeUsage.of(Boolean.class));
    Arbitrary<String> rpo = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<Boolean> requesterPays = Arbitraries.defaultFor(TypeUsage.of(Boolean.class));
    Arbitrary<String> defaultEncryptionKmsKey = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
    Arbitrary<Tuple.Tuple7> extendedMetadata =
        Combinators.combine(
                updateTime, indexPage, notFoundPage, defaultEventBasedHold, rpo, requesterPays, defaultEncryptionKmsKey)
            .as(Tuple::of);

    //    Bucket.RetentionPolicy retentionPolicy = from.getRetentionPolicy();
    //    ifNonNull(retentionPolicy, Bucket.RetentionPolicy::getIsLocked,
    // to::setRetentionPolicyIsLocked);
    //    ifNonNull(retentionPolicy, Bucket.RetentionPolicy::getRetentionPeriod,
    // to::setRetentionPeriod);
    //    if(from.hasRetentionPolicy() && retentionPolicy.hasEffectiveTime()) {
    //      to.setRetentionEffectiveTime(retentionPolicy.getEffectiveTime().getSeconds());
    //    }
    //    ifNonNull(from.getLabels(), to::setLabels);

    return Collections.singleton(
        Combinators.combine(baseMetadata, extendedMetadata).as(this::buildBucket));
  }

  private Bucket buildBucket(Tuple.Tuple8 baseMetadata, Tuple.Tuple7 extendedMetadata) {
    return Bucket.getDefaultInstance()
        .newBuilder()
        .setBucketId((String) baseMetadata.get1())
        .setName((String) baseMetadata.get2())
        .setStorageClass((String) baseMetadata.get3())
        .setLocation((String) baseMetadata.get4())
        .setLocationType((String) baseMetadata.get5())
        .setMetageneration((Long) baseMetadata.get6())
        .setVersioning(Bucket.Versioning.newBuilder().setEnabled((Boolean) baseMetadata.get7()))
        .setCreateTime(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds((long) baseMetadata.get8())
                .build())
        .setUpdateTime(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds((long) extendedMetadata.get1())
                .build())
        .setWebsite(
            Bucket.Website.newBuilder()
                .setMainPageSuffix((String) extendedMetadata.get2())
                .setNotFoundPage((String) extendedMetadata.get3())
                .build())
        .setDefaultEventBasedHold((Boolean) extendedMetadata.get4())
        .setRpo((String) extendedMetadata.get5())
        .setBilling(
            Bucket.Billing.newBuilder().setRequesterPays((Boolean) extendedMetadata.get6()).build())
        .setEncryption(Bucket.Encryption.newBuilder().setDefaultKmsKey((String) extendedMetadata.get7()).build())
        .build();
  }
}
