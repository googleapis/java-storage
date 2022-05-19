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

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.Bucket.Encryption;
import com.google.storage.v2.Bucket.RetentionPolicy;
import com.google.storage.v2.Bucket.Versioning;
import com.google.storage.v2.Bucket.Website;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.providers.TypeUsage;

public final class StorageArbitraries {

  private StorageArbitraries() {}

  public static Arbitrary<Timestamp> timestamp() {
    return Arbitraries.longs().between(0, 100000000L).map(s -> Timestamps.fromMillis(s));
  }

  public static Arbitrary<Boolean> bool() {
    return Arbitraries.defaultFor(TypeUsage.of(Boolean.class));
  }

  public static Arbitrary<Long> metageneration() {
    return Arbitraries.longs().greaterOrEqual(0);
  }

  /**
   * Generated bucket name based on the rules outlined in <a target="_blank" rel="noopener
   * noreferrer"
   * href="https://cloud.google.com/storage/docs/naming-buckets#requirements">https://cloud.google.com/storage/docs/naming-buckets#requirements</a>
   */
  public static Arbitrary<BucketName> bucketName() {
    return Combinators.combine(
            Arbitraries.oneOf(Arbitraries.chars().alpha(), Arbitraries.chars().numeric()),
            Arbitraries.oneOf(
                    Arbitraries.chars().alpha(),
                    Arbitraries.chars().numeric(),
                    Arbitraries.chars().with('-', '_'))
                .list()
                .ofMinSize(1)
                .ofMaxSize(61),
            Arbitraries.oneOf(Arbitraries.chars().alpha(), Arbitraries.chars().numeric()))
        .as(
            (first, mid, last) -> {
              final StringBuilder sb = new StringBuilder();
              sb.append(first);
              mid.forEach(sb::append);
              sb.append(last);

              return new BucketName(sb.toString());
            });
  }

  public static Arbitrary<ProjectID> projectID() {
    return Combinators.combine(
            // must start with a letter
            Arbitraries.chars().range('a', 'z'),
            // can only contain numbers, lowercase letters, and hyphens, and must be 6-30 chars
            Arbitraries.strings()
                .withCharRange('a', 'z')
                .numeric()
                .withChars('-')
                .ofMinLength(4)
                .ofMaxLength(28),
            // must not end with a hyphen
            Arbitraries.chars().range('a', 'z').numeric())
        .as(
            (first, mid, last) -> {
              final StringBuilder sb = new StringBuilder();
              sb.append(first).append(mid).append(last);
              return new ProjectID(sb.toString());
            });
  }

  public static Buckets buckets() {
    return Buckets.INSTANCE;
  }

  public static final class Buckets {
    private static final Buckets INSTANCE = new Buckets();

    private Buckets() {}

    public Arbitrary<Website> website() {
      Arbitrary<String> indexPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
      Arbitrary<String> notFoundPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
      return Combinators.combine(indexPage, notFoundPage)
          .as((i, n) -> Website.newBuilder().setMainPageSuffix(i).setNotFoundPage(n).build());
    }

    public Arbitrary<Bucket.Billing> billing() {
      return bool().map(b -> Billing.newBuilder().setRequesterPays(b).build());
    }

    public Arbitrary<Bucket.Encryption> encryption() {
      return Arbitraries.strings()
          .all()
          .ofMinLength(1)
          .ofMaxLength(1024)
          .map(s -> Encryption.newBuilder().setDefaultKmsKey(s).build());
    }

    public Arbitrary<Bucket.RetentionPolicy> retentionPolicy() {
      return Combinators.combine(bool(), Arbitraries.longs().greaterOrEqual(0), timestamp())
          .as(
              (locked, period, effectiveTime) ->
                  RetentionPolicy.newBuilder()
                      .setIsLocked(locked)
                      .setRetentionPeriod(period)
                      .setEffectiveTime(effectiveTime)
                      .build());
    }

    public Arbitrary<Bucket.Versioning> versioning() {
      return bool().map(b -> Versioning.newBuilder().setEnabled(b).build());
    }

    public Arbitrary<String> storageClass() {
      // TODO: return each of the real values and edge cases (including invalid values)
      return Arbitraries.strings().all().ofMinLength(1).ofLength(1024);
    }

    public Arbitrary<String> rpo() {
      // TODO: return each of the real values and edge cases (including invalid values)
      return Arbitraries.strings().all().ofMinLength(1).ofLength(1024);
    }
  }

  public static final class BucketName {
    private final String value;

    private BucketName(String value) {
      this.value = value;
    }

    public String get() {
      return value;
    }
  }

  public static final class ProjectID {
    private final String value;

    private ProjectID(String value) {
      this.value = value;
    }

    public String get() {
      return value;
    }
  }
}
