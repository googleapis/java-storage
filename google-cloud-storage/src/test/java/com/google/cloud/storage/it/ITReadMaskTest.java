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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Rpo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.conformance.retry.ParallelParameterized;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
public final class ITReadMaskTest {

  @ClassRule(order = 1)
  public static final StorageFixture sfH = StorageFixture.defaultHttp();

  @ClassRule(order = 1)
  public static final StorageFixture sfG = StorageFixture.defaultGrpc();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder()
          .setHandle(sfH::getInstance)
          .setBucketNameFmtString("java-storage-grpc-%s")
          .build();

  private static Storage sh;
  private static Storage sg;
  private static BlobId blobId;

  @BeforeClass
  public static void beforeClass() {
    sh = sfH.getInstance();
    sg = sfG.getInstance();

    String bucket = bucketFixture.getBucketInfo().getName();

    BlobId blobId1 = BlobId.of(bucket, objName("001"));
    BlobId blobId2 = BlobId.of(bucket, objName("002"));
    BlobId blobId3 = BlobId.of(bucket, objName("003"));
    BlobId blobId4 = BlobId.of(bucket, objName("004"));

    BlobInfo info1 = BlobInfo.newBuilder(blobId1).setMetadata(ImmutableMap.of("pow", "1")).build();
    BlobInfo info2 = BlobInfo.newBuilder(blobId2).setMetadata(ImmutableMap.of("pow", "2")).build();
    BlobInfo info3 = BlobInfo.newBuilder(blobId3).setMetadata(ImmutableMap.of("pow", "3")).build();
    BlobInfo info4 = BlobInfo.newBuilder(blobId4).setMetadata(ImmutableMap.of("pow", "4")).build();
    sh.create(info1, "A".getBytes(StandardCharsets.UTF_8), BlobTargetOption.doesNotExist());

    ComposeRequest c2 =
        ComposeRequest.newBuilder()
            .addSource(blobId1.getName(), blobId1.getName())
            .setTarget(info2)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    ComposeRequest c3 =
        ComposeRequest.newBuilder()
            .addSource(blobId2.getName(), blobId2.getName())
            .setTarget(info3)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    ComposeRequest c4 =
        ComposeRequest.newBuilder()
            .addSource(blobId3.getName(), blobId3.getName())
            .setTarget(info4)
            .setTargetOptions(BlobTargetOption.doesNotExist())
            .build();
    sh.compose(c2);
    sh.compose(c3);
    sh.compose(c4);

    blobId = blobId1;
  }

  @RunWith(ParallelParameterized.class)
  public static final class BucketReadMask {

    private final BucketField field;
    private final LazyAssertion<BucketInfo> assertion;

    public BucketReadMask(Args<BucketField, BucketInfo> arg) {
      this.field = arg.f;
      this.assertion = arg.assertion;
    }

    @Test
    public void get() {
      BucketInfo bucketJson = getBucket(sh);
      BucketInfo bucketGrpc = getBucket(sg);

      assertion.validate(bucketJson, bucketGrpc);
    }

    @Test
    public void list() {
      List<BucketInfo> bucketsJson = listBuckets(sh);
      List<BucketInfo> bucketsGrpc = listBuckets(sg);

      assertion.pairwiseList().validate(bucketsJson, bucketsGrpc);
    }

    @Parameters(name = "{0}")
    public static Iterable<Args<BucketField, BucketInfo>> parameters() {
      ImmutableList<Args<BucketField, BucketInfo>> args =
          ImmutableList.of(
              new Args<>(BucketField.ACL, LazyAssertion.equal()),
              new Args<>(BucketField.AUTOCLASS, LazyAssertion.equal()),
              new Args<>(BucketField.BILLING, LazyAssertion.equal()),
              new Args<>(BucketField.CORS, LazyAssertion.equal()),
              new Args<>(BucketField.CUSTOM_PLACEMENT_CONFIG, LazyAssertion.equal()),
              new Args<>(
                  BucketField.DEFAULT_EVENT_BASED_HOLD,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getDefaultEventBasedHold()).isNull();
                    assertThat(grpcT.getDefaultEventBasedHold()).isFalse();
                  }),
              new Args<>(
                  BucketField.DEFAULT_OBJECT_ACL,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getDefaultAcl()).isNotEmpty();
                    assertThat(grpcT.getDefaultAcl()).isNull();
                  }),
              new Args<>(BucketField.ENCRYPTION, LazyAssertion.equal()),
              new Args<>(BucketField.ETAG, LazyAssertion.equal()),
              new Args<>(BucketField.IAMCONFIGURATION, LazyAssertion.equal()),
              new Args<>(BucketField.ID, LazyAssertion.equal()),
              new Args<>(BucketField.LABELS, LazyAssertion.equal()),
              new Args<>(BucketField.LIFECYCLE, LazyAssertion.equal()),
              new Args<>(BucketField.LOCATION, LazyAssertion.equal()),
              new Args<>(BucketField.LOCATION_TYPE, LazyAssertion.equal()),
              new Args<>(BucketField.LOGGING, LazyAssertion.equal()),
              new Args<>(BucketField.METAGENERATION, LazyAssertion.equal()),
              new Args<>(BucketField.NAME, LazyAssertion.equal()),
              new Args<>(BucketField.OWNER, LazyAssertion.equal()),
              new Args<>(BucketField.RETENTION_POLICY, LazyAssertion.equal()),
              new Args<>(
                  BucketField.RPO,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getRpo()).isEqualTo(Rpo.DEFAULT);
                    assertThat(grpcT.getRpo()).isNull();
                  }),
              new Args<>(
                  BucketField.SELF_LINK,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getSelfLink()).isNotEmpty();
                    assertThat(grpcT.getSelfLink()).isNull();
                  }),
              new Args<>(BucketField.STORAGE_CLASS, LazyAssertion.equal()),
              new Args<>(BucketField.TIME_CREATED, LazyAssertion.equal()),
              new Args<>(BucketField.UPDATED, LazyAssertion.equal()),
              new Args<>(BucketField.VERSIONING, LazyAssertion.equal()),
              new Args<>(BucketField.WEBSITE, LazyAssertion.equal()));

      List<String> argsDefined =
          args.stream().map(Args::getF).map(Enum::name).sorted().collect(Collectors.toList());

      List<String> definedFields =
          Arrays.stream(BucketField.values()).map(Enum::name).sorted().collect(Collectors.toList());

      assertThat(argsDefined).containsExactlyElementsIn(definedFields);
      return args;
    }

    private BucketInfo getBucket(Storage s) {
      return s.get(blobId.getBucket(), BucketGetOption.fields(field)).asBucketInfo();
    }

    private List<BucketInfo> listBuckets(Storage s) {
      Page<Bucket> p =
          s.list(BucketListOption.prefix(blobId.getBucket()), BucketListOption.fields(field));
      return StreamSupport.stream(p.iterateAll().spliterator(), false)
          .map(Bucket::asBucketInfo)
          .collect(Collectors.toList());
    }
  }

  @RunWith(ParallelParameterized.class)
  public static final class BlobReadMask {

    private final BlobField field;
    private final LazyAssertion<BlobInfo> assertion;

    public BlobReadMask(Args<BlobField, BlobInfo> args) {
      this.field = args.f;
      this.assertion = args.assertion;
    }

    @Test
    public void get() {
      BlobInfo blobJson = getBlob(sh);
      BlobInfo blobGrpc = getBlob(sg);

      assertion.validate(blobJson, blobGrpc);
    }

    @Test
    public void list() {
      List<BlobInfo> blobsJson = listBlobs(sh);
      List<BlobInfo> blobsGrpc = listBlobs(sg);

      assertion.pairwiseList().validate(blobsJson, blobsGrpc);
    }

    @Parameters(name = "{0}")
    public static Iterable<Args<BlobField, BlobInfo>> parameters() {
      ImmutableList<Args<BlobField, BlobInfo>> args =
          ImmutableList.of(
              new Args<>(BlobField.ACL, LazyAssertion.equal()),
              new Args<>(BlobField.BUCKET, LazyAssertion.equal()),
              new Args<>(
                  BlobField.CACHE_CONTROL,
                  LazyAssertion.apiaryNullGrpcDefault("", BlobInfo::getCacheControl)),
              // for non-composed objects, json and grpc differ in their resulting values. For json,
              // a null will be returned whereas for grpc we will get the type default value which
              // in this case is 0. The only possible way we could guard against this would be if
              // the proto changed component_count to proto3_optional forcing it to generate a
              // hasComponentCount.
              new Args<>(
                  BlobField.COMPONENT_COUNT,
                  (jsonT, grpcT) -> {
                    if (grpcT.getComponentCount() == 0) {
                      assertThat(jsonT.getComponentCount()).isNull();
                    } else {
                      assertThat(grpcT.getComponentCount()).isEqualTo(jsonT.getComponentCount());
                    }
                  }),
              new Args<>(
                  BlobField.CONTENT_DISPOSITION,
                  LazyAssertion.apiaryNullGrpcDefault("", BlobInfo::getContentDisposition)),
              new Args<>(
                  BlobField.CONTENT_ENCODING,
                  LazyAssertion.apiaryNullGrpcDefault("", BlobInfo::getContentEncoding)),
              new Args<>(
                  BlobField.CONTENT_LANGUAGE,
                  LazyAssertion.apiaryNullGrpcDefault("", BlobInfo::getContentLanguage)),
              // we'd expect this to follow the patter of the other Content-* headers, however via
              // the json api GCS will default null contentType to application/octet-stream. Note,
              // however it doesn't carry this forward to composed objects so a composed object can
              // have a null/empty content-type.
              new Args<>(
                  BlobField.CONTENT_TYPE,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getContentType()).isAnyOf("application/octet-stream", null);
                    assertThat(grpcT.getContentType()).isAnyOf("application/octet-stream", "");
                  }),
              new Args<>(BlobField.CRC32C, LazyAssertion.equal()),
              new Args<>(BlobField.CUSTOMER_ENCRYPTION, LazyAssertion.equal()),
              new Args<>(BlobField.CUSTOM_TIME, LazyAssertion.equal()),
              new Args<>(BlobField.ETAG, LazyAssertion.equal()),
              new Args<>(
                  BlobField.EVENT_BASED_HOLD,
                  LazyAssertion.apiaryNullGrpcDefault(false, BlobInfo::getEventBasedHold)),
              new Args<>(BlobField.GENERATION, LazyAssertion.equal()),
              new Args<>(
                  BlobField.ID,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getGeneratedId()).isNotEmpty();
                    assertThat(grpcT.getGeneratedId()).isNull();
                  }),
              new Args<>(
                  BlobField.KIND,
                  (jsonT, grpcT) -> {
                    // pass - we don't expose kind in the public surface
                  }),
              new Args<>(BlobField.KMS_KEY_NAME, LazyAssertion.equal()),
              new Args<>(BlobField.MD5HASH, LazyAssertion.equal()),
              new Args<>(
                  BlobField.MEDIA_LINK,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getMediaLink()).isNotEmpty();
                    assertThat(grpcT.getMediaLink()).isNull();
                  }),
              new Args<>(BlobField.METADATA, LazyAssertion.equal()),
              new Args<>(BlobField.METAGENERATION, LazyAssertion.equal()),
              new Args<>(BlobField.NAME, LazyAssertion.equal()),
              new Args<>(BlobField.OWNER, LazyAssertion.equal()),
              new Args<>(BlobField.RETENTION_EXPIRATION_TIME, LazyAssertion.equal()),
              new Args<>(
                  BlobField.SELF_LINK,
                  (jsonT, grpcT) -> {
                    assertThat(jsonT.getSelfLink()).isNotEmpty();
                    assertThat(grpcT.getSelfLink()).isNull();
                  }),
              new Args<>(BlobField.SIZE, LazyAssertion.equal()),
              new Args<>(BlobField.STORAGE_CLASS, LazyAssertion.equal()),
              new Args<>(
                  BlobField.TEMPORARY_HOLD,
                  LazyAssertion.apiaryNullGrpcDefault(false, BlobInfo::getTemporaryHold)),
              new Args<>(BlobField.TIME_CREATED, LazyAssertion.equal()),
              new Args<>(BlobField.TIME_DELETED, LazyAssertion.equal()),
              new Args<>(BlobField.TIME_STORAGE_CLASS_UPDATED, LazyAssertion.equal()),
              new Args<>(BlobField.UPDATED, LazyAssertion.equal()));
      List<String> argsDefined =
          args.stream().map(Args::getF).map(Enum::name).sorted().collect(Collectors.toList());

      List<String> definedFields =
          Arrays.stream(BlobField.values()).map(Enum::name).sorted().collect(Collectors.toList());

      assertThat(argsDefined).containsExactlyElementsIn(definedFields);
      return args;
    }

    private BlobInfo getBlob(Storage s) {
      return s.get(blobId, BlobGetOption.fields(field)).asBlobInfo();
    }

    private List<BlobInfo> listBlobs(Storage s) {
      Page<Blob> p =
          s.list(
              blobId.getBucket(),
              BlobListOption.prefix(ITReadMaskTest.class.getSimpleName()),
              BlobListOption.fields(field));
      return StreamSupport.stream(p.iterateAll().spliterator(), false)
          .map(Blob::asBlobInfo)
          .collect(Collectors.toList());
    }
  }

  private static String objName(String name) {
    return String.format("%s/%s", ITReadMaskTest.class.getSimpleName(), name);
  }

  private static final class Args<F, T> {
    private final F f;
    private final LazyAssertion<T> assertion;

    Args(F f, LazyAssertion<T> assertion) {
      this.f = f;
      this.assertion = assertion;
    }

    F getF() {
      return f;
    }

    @Override
    public String toString() {
      return f.toString();
    }
  }

  @FunctionalInterface
  private interface LazyAssertion<T> {
    void validate(T jsonT, T grpcT) throws AssertionError;

    default LazyAssertion<List<T>> pairwiseList() {
      LazyAssertion<T> self = this;
      return (jsonTs, grpcTs) -> {
        final int length = Math.min(jsonTs.size(), grpcTs.size());
        int idx = 0;
        for (; idx < length; idx++) {
          T jT = jsonTs.get(idx);
          T gT = grpcTs.get(idx);
          self.validate(jT, gT);
        }

        assertThat(idx).isEqualTo(jsonTs.size());
        assertThat(idx).isEqualTo(grpcTs.size());

        assertThat(jsonTs.size()).isEqualTo(length);
        assertThat(grpcTs.size()).isEqualTo(length);
      };
    }

    static <X> LazyAssertion<X> equal() {
      return (a, g) -> assertThat(g).isEqualTo(a);
    }

    static <X, F> LazyAssertion<X> apiaryNullGrpcDefault(F def, Function<X, F> extractor) {
      return (jsonT, grpcT) -> {
        assertThat(extractor.apply(jsonT)).isNull();
        assertThat(extractor.apply(grpcT)).isEqualTo(def);
      };
    }
  }
}
