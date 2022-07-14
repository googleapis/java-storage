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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.Bucket.Billing;
import com.google.storage.v2.Bucket.Encryption;
import com.google.storage.v2.Bucket.RetentionPolicy;
import com.google.storage.v2.Bucket.Versioning;
import com.google.storage.v2.Bucket.Website;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.CustomerEncryption;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.Owner;
import com.google.type.Date;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Tuple;
import net.jqwik.api.arbitraries.CharacterArbitrary;
import net.jqwik.api.arbitraries.ListArbitrary;
import net.jqwik.api.arbitraries.LongArbitrary;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.api.providers.TypeUsage;
import net.jqwik.time.api.DateTimes;

public final class StorageArbitraries {

  private StorageArbitraries() {}

  public static Arbitrary<Timestamp> timestamp() {
    return Combinators.combine(
            DateTimes.offsetDateTimes().offsetBetween(ZoneOffset.UTC, ZoneOffset.UTC),
            Arbitraries.integers().between(0, 999_999_999))
        .as(
            (odt, nanos) ->
                Timestamp.newBuilder().setSeconds(odt.toEpochSecond()).setNanos(nanos).build());
  }

  public static Arbitrary<Date> date() {
    return DateTimes.offsetDateTimes()
        .offsetBetween(ZoneOffset.UTC, ZoneOffset.UTC)
        .map(
            odt ->
                Date.newBuilder()
                    .setYear(odt.getYear())
                    .setMonth(odt.getMonthValue())
                    .setDay(odt.getDayOfMonth())
                    .build());
  }

  public static Arbitrary<Boolean> bool() {
    return Arbitraries.defaultFor(TypeUsage.of(Boolean.class));
  }

  public static LongArbitrary metageneration() {
    return Arbitraries.longs().greaterOrEqual(0);
  }

  public static LongArbitrary generation() {
    return Arbitraries.longs().greaterOrEqual(0);
  }

  public static StringArbitrary randomString() {
    return Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
  }

  public static CharacterArbitrary alnum() {
    return Arbitraries.chars().alpha().numeric();
  }

  public static StringArbitrary alphaString() {
    return Arbitraries.strings().alpha();
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
        .as((first, mid, last) -> new ProjectID(first + mid + last));
  }

  public static Arbitrary<String> kmsKey() {
    return Arbitraries.of("kms-key1", "kms-key2").injectNull(0.75);
  }

  public static Buckets buckets() {
    return Buckets.INSTANCE;
  }

  public static Arbitrary<String> storageClass() {
    return Arbitraries.of(
        "STANDARD",
        "NEARLINE",
        "COLDLINE",
        "ARCHIVE",
        "MULTI_REGIONAL",
        "REGIONAL",
        "DURABLE_REDUCED_AVAILABILITY");
  }

  public static Arbitrary<Owner> owner() {
    Arbitrary<String> entity = alphaString().ofMinLength(1).ofMaxLength(1024);
    return entity.map(e -> Owner.newBuilder().setEntity(e).build());
  }

  public static final class Buckets {
    private static final Buckets INSTANCE = new Buckets();

    private Buckets() {}

    /**
     * Generated bucket name based on the rules outlined in <a target="_blank" rel="noopener
     * noreferrer"
     * href="https://cloud.google.com/storage/docs/naming-buckets#requirements">https://cloud.google.com/storage/docs/naming-buckets#requirements</a>
     */
    Arbitrary<BucketName> name() {
      return Combinators.combine(
              Arbitraries.oneOf(
                  projectID(),
                  // Global buckets have prefix of projects/_
                  Arbitraries.of(new ProjectID("_"))),
              alnum(),
              alnum().with('-', '_').list().ofMinSize(1).ofMaxSize(61),
              alnum())
          .as(
              (p, first, mid, last) -> {
                final StringBuilder sb = new StringBuilder();
                sb.append(first);
                mid.forEach(sb::append);
                sb.append(last);
                return BucketName.of(p.get(), sb.toString());
              });
    }

    public Arbitrary<Bucket.Lifecycle.Rule.Action> actions() {
      return Combinators.combine(Arbitraries.of("Delete", "SetStorageClass"), storageClass())
          .as(
              (a, s) -> {
                Bucket.Lifecycle.Rule.Action.Builder actionBuilder =
                    Bucket.Lifecycle.Rule.Action.newBuilder();
                actionBuilder.setType(a);
                if (a.equals("SetStorageClass")) {
                  actionBuilder.setStorageClass(s);
                }
                return actionBuilder.build();
              });
    }

    public Arbitrary<Bucket.Lifecycle.Rule> rule() {
      Arbitrary<Boolean> conditionIsLive = bool();
      Arbitrary<Integer> conditionAgeDays = Arbitraries.integers().between(0, 100);
      Arbitrary<Integer> conditionNumberOfNewVersions = Arbitraries.integers().between(0, 10);
      Arbitrary<Date> conditionCreatedBeforeTime = date();
      Arbitrary<Integer> conditionDaysSinceNoncurrentTime = Arbitraries.integers().between(0, 10);
      Arbitrary<Date> conditionNoncurrentTime = date();
      Arbitrary<Integer> conditionDaysSinceCustomTime = Arbitraries.integers().between(0, 10);
      Arbitrary<Date> conditionCustomTime = date();
      ListArbitrary<String> storageClassMatches = storageClass().list().uniqueElements();

      return Arbitraries.oneOf(
          Arbitraries.of(
              Bucket.Lifecycle.Rule.newBuilder()
                  .setAction(Bucket.Lifecycle.Rule.Action.newBuilder().setType("Delete").build())
                  .setCondition(Bucket.Lifecycle.Rule.Condition.newBuilder().setAgeDays(10).build())
                  .build()),
          Combinators.combine(
                  actions(),
                  Combinators.combine(
                          conditionIsLive,
                          conditionAgeDays,
                          conditionNumberOfNewVersions,
                          conditionCreatedBeforeTime,
                          conditionDaysSinceNoncurrentTime,
                          conditionNoncurrentTime,
                          conditionDaysSinceCustomTime,
                          conditionCustomTime)
                      .as(Tuple::of),
                  storageClassMatches)
              .as(
                  (a, ct, s) ->
                      Bucket.Lifecycle.Rule.newBuilder()
                          .setAction(a)
                          .setCondition(
                              Bucket.Lifecycle.Rule.Condition.newBuilder()
                                  .setIsLive(ct.get1())
                                  .setAgeDays(ct.get2())
                                  .setNumNewerVersions(ct.get3())
                                  .setCreatedBefore(ct.get4())
                                  .setDaysSinceNoncurrentTime(ct.get5())
                                  .setNoncurrentTimeBefore(ct.get6())
                                  .setDaysSinceCustomTime(ct.get7())
                                  .setCustomTimeBefore(ct.get8())
                                  .addAllMatchesStorageClass(s)
                                  .build())
                          .build()));
    }

    public Arbitrary<Bucket.Lifecycle> lifecycle() {
      return rule()
          .list()
          .ofMinSize(0)
          .ofMaxSize(100)
          .uniqueElements()
          .map((r) -> Bucket.Lifecycle.newBuilder().addAllRule(r).build());
    }

    public Arbitrary<Website> website() {
      Arbitrary<String> indexPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
      Arbitrary<String> notFoundPage = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(1024);
      return Combinators.combine(indexPage, notFoundPage)
          .as((i, n) -> Website.newBuilder().setMainPageSuffix(i).setNotFoundPage(n).build());
    }

    public Arbitrary<Bucket.Logging> logging() {
      Arbitrary<BucketName> loggingBucketName = name();
      Arbitrary<String> loggingPrefix = Arbitraries.strings().all().ofMinLength(1).ofMaxLength(10);
      return Combinators.combine(loggingBucketName, loggingPrefix, bool())
          .as(
              (b, p, u) -> {
                Bucket.Logging.Builder loggingBuilder =
                    Bucket.Logging.newBuilder().setLogObjectPrefix(p);
                if (u == Boolean.TRUE) {
                  loggingBuilder.setLogBucket(b.toString());
                } else {
                  loggingBuilder.setLogBucket(b.getBucket());
                }
                return loggingBuilder.build();
              });
    }

    public ListArbitrary<Bucket.Cors> cors() {
      Arbitrary<Integer> maxAgeSeconds =
          Arbitraries.integers().between(0, OffsetDateTime.MAX.getSecond());
      ListArbitrary<String> methods =
          Arbitraries.of("GET", "DELETE", "UPDATE", "PATCH").list().uniqueElements();
      ListArbitrary<String> responseHeaders =
          Arbitraries.of("Content-Type", "Origin").list().uniqueElements();
      ListArbitrary<String> origins = Arbitraries.of("*", "google.com").list().uniqueElements();
      return Combinators.combine(methods, responseHeaders, origins, maxAgeSeconds)
          .as(
              (m, r, o, a) ->
                  Bucket.Cors.newBuilder()
                      .addAllMethod(m)
                      .addAllResponseHeader(r)
                      .addAllOrigin(o)
                      .setMaxAgeSeconds(a)
                      .build())
          .list()
          .ofMinSize(0)
          .ofMaxSize(10);
    }

    public Arbitrary<Bucket.Billing> billing() {
      return bool().map(b -> Billing.newBuilder().setRequesterPays(b).build());
    }

    public ListArbitrary<ObjectAccessControl> objectAccessControl() {
      Arbitrary<String> entity = alphaString().ofMinLength(1).ofMaxLength(1024);
      Arbitrary<String> role = alphaString().ofMinLength(1).ofMaxLength(1024);
      return Combinators.combine(entity, role)
          .as((e, r) -> ObjectAccessControl.newBuilder().setEntity(e).setRole(r).build())
          .list();
    }

    public Arbitrary<Bucket.IamConfig.UniformBucketLevelAccess> uniformBucketLevelAccess() {
      return Combinators.combine(bool(), timestamp())
          .as(
              (e, l) -> {
                Bucket.IamConfig.UniformBucketLevelAccess.Builder ublaBuilder =
                    Bucket.IamConfig.UniformBucketLevelAccess.newBuilder();
                ublaBuilder.setEnabled(e);
                if (e) {
                  ublaBuilder.setLockTime(l);
                }
                return ublaBuilder.build();
              });
    }

    public Arbitrary<Bucket.IamConfig> iamConfig() {
      Arbitrary<Bucket.IamConfig.UniformBucketLevelAccess> uniformBucketLevelAccess =
          uniformBucketLevelAccess();
      Arbitrary<String> pap = Arbitraries.of("enforced", "inherited");
      return Combinators.combine(pap, uniformBucketLevelAccess())
          .as(
              (p, u) -> {
                Bucket.IamConfig.Builder iamConfigBuilder = Bucket.IamConfig.newBuilder();
                iamConfigBuilder.setUniformBucketLevelAccess(u);
                if (u.getEnabled()) {
                  iamConfigBuilder.setPublicAccessPrevention(p);
                }
                return iamConfigBuilder.build();
              });
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
              (locked, period, effectiveTime) -> {
                RetentionPolicy.Builder retentionBuilder = RetentionPolicy.newBuilder();
                retentionBuilder.setRetentionPeriod(period);
                retentionBuilder.setIsLocked(locked);
                if (locked) {
                  retentionBuilder.setEffectiveTime(effectiveTime);
                }
                return retentionBuilder.build();
              });
    }

    public Arbitrary<Bucket.Versioning> versioning() {
      return bool().map(b -> Versioning.newBuilder().setEnabled(b).build());
    }

    public Arbitrary<String> rpo() {
      return Arbitraries.of("DEFAULT", "ASYNC_TURBO");
    }

    public Arbitrary<String> location() {
      return Arbitraries.of(
          "US", "US-CENTRAL1", "US-EAST1", "EUROPE-CENTRAL2", "SOUTHAMERICA-EAST1");
    }

    public Arbitrary<String> locationType() {
      return Arbitraries.of("region", "dual-region", "multi-region");
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

  public static Objects objects() {
    return Objects.INSTANCE;
  }

  public static final class Objects {
    private static final Objects INSTANCE = new Objects();

    private Objects() {}

    /**
     * Generated object name based on the rules outlined in <a target="_blank" rel="noopener
     * noreferrer"
     * href="https://cloud.google.com/storage/docs/naming-objects#objectnames">https://cloud.google.com/storage/docs/naming-objects#objectnames</a>
     */
    public Arbitrary<String> name() {
      return Arbitraries.strings()
          .all()
          .excludeChars('#', '[', ']', '*', '?')
          .excludeChars(enumerate(0x7f, 0x84))
          .excludeChars(enumerate(0x86, 0x9f))
          .ofMinLength(1)
          .ofMaxLength(1024)
          .filter(s -> !s.equals("."))
          .filter(s -> !s.equals(".."))
          .filter(s -> !s.startsWith(".well-known/acme-challenge/"));
    }

    public Arbitrary<ObjectChecksums> objectChecksumsArbitrary() {
      return Combinators.combine(
              Arbitraries.integers().greaterOrEqual(1),
              Arbitraries.strings()
                  .map(
                      s ->
                          BaseEncoding.base64()
                              .encode(Hashing.md5().hashBytes(s.getBytes()).asBytes())))
          .as(
              (crc32c, md5) ->
                  ObjectChecksums.newBuilder()
                      .setCrc32C(crc32c)
                      .setMd5Hash(ByteString.copyFrom(md5.getBytes()))
                      .build());
    }

    public Arbitrary<CustomerEncryption> customerEncryptionArbitrary() {
      return Combinators.combine(
              Arbitraries.strings().ofMinLength(1).ofMaxLength(1024),
              Arbitraries.strings()
                  .map(s -> Hashing.sha256().hashString(s, StandardCharsets.UTF_8).asBytes())
                  .map(ByteString::copyFrom))
          .as(
              (algorithm, key) ->
                  CustomerEncryption.newBuilder()
                      .setEncryptionAlgorithm(algorithm)
                      .setKeySha256Bytes(key)
                      .build());
    }

    /**
     * Custom metadata from <a target="_blank" rel="noopener noreferrer"
     * href="https://cloud.google.com/storage/docs/metadata">https://cloud.google.com/storage/docs/metadata</a>
     */
    public Arbitrary<Map<String, String>> customMetadata() {
      // TODO: are we going to need to care about non-url encoded characters?
      //   Not for grpc itself, but possibly for compatibility tests.
      return Arbitraries.maps(
              alphaString().ofMinLength(1).ofMaxLength(32),
              alphaString().ofMinLength(1).ofMaxLength(128))
          .ofMinSize(0)
          .ofMaxSize(15)
          .injectNull(0.5);
    }

    public ListArbitrary<ObjectAccessControl> objectAccessControl() {
      return Arbitraries.of(ObjectAccessControl.getDefaultInstance())
          .list()
          .ofMaxSize(0) /*.ofMinSize(0).ofMaxSize(10)*/;
    }
  }

  public static HttpHeaders httpHeaders() {
    return HttpHeaders.INSTANCE;
  }

  /**
   * Fixed-key metadata from <a target="_blank" rel="noopener noreferrer"
   * href="https://cloud.google.com/storage/docs/metadata">https://cloud.google.com/storage/docs/metadata</a>
   */
  public static final class HttpHeaders {
    private static final HttpHeaders INSTANCE = new HttpHeaders();

    public Arbitrary<String> cacheControl() {
      return Combinators.combine(
              Arbitraries.of("public", "private", "no-cache", "no-store"),
              // bound to 10K to ease exhaustion processing
              Arbitraries.integers().between(0, 10_000).injectNull(0.5),
              Arbitraries.of("no-transform").injectNull(0.5))
          .as(
              (visibility, maxAge, transform) -> {
                //noinspection ConstantConditions
                if (maxAge == null && transform == null) {
                  return visibility;
                } else {
                  //noinspection ConstantConditions
                  if (maxAge != null) {
                    return String.format("%s, max-age=%d", visibility, maxAge);
                  } else if (transform != null) {
                    return String.format("%s, %s", visibility, transform);
                  } else {
                    return String.format("%s, max-age=%d, %s", visibility, maxAge, transform);
                  }
                }
              });
    }

    public Arbitrary<String> contentDisposition() {
      return Arbitraries.of("inline", "attachment;filename=blob.bin").injectNull(0.75);
    }

    public Arbitrary<String> contentEncoding() {
      return Arbitraries.of("gzip").injectNull(0.5);
    }

    public Arbitrary<String> contentLanguage() {
      return Arbitraries.of("en", "es", "zh").injectNull(0.75);
    }

    public Arbitrary<String> contentType() {
      return Arbitraries.of(
              "text/plain",
              "application/json",
              "application/octet-stream",
              "application/x-www-form-urlencoded")
          .injectNull(0.33);
    }

    public Arbitrary<Timestamp> customTime() {
      return timestamp().injectNull(0.75);
    }
  }

  private static char[] enumerate(int lower, int upperInclusive) {
    checkArgument(lower <= upperInclusive, "lower <= upperInclusive");
    int length = upperInclusive - lower + 1;
    char[] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = (char) (i + lower);
    }
    return chars;
  }
}
