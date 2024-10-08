/*
 * Copyright 2024 Google LLC
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

import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.cloud.storage.TestUtils.getChecksummedData;
import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.AbortedException;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.OutOfRangeException;
import com.google.api.gax.rpc.UnavailableException;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Hasher.UncheckedChecksumMismatchException;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.cloud.storage.it.GrpcPlainRequestLoggingInterceptor;
import com.google.cloud.storage.it.GrpcRequestAuditing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectError;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import com.google.storage.v2.ReadRange;
import com.google.storage.v2.ReadRangeError;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class ITBlobDescriptorFakeTest {

  private static final Metadata.Key<com.google.rpc.Status> GRPC_STATUS_DETAILS_KEY =
      Metadata.Key.of(
          "grpc-status-details-bin",
          ProtoUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance()));

  private static final Object METADATA =
      Object.newBuilder()
          .setBucket(BucketName.format("_", "b"))
          .setName("o")
          .setGeneration(1)
          .setSize(_2MiB)
          .build();
  private static final BidiReadObjectRequest REQ_OPEN =
      BidiReadObjectRequest.newBuilder()
          .setReadObjectSpec(
              BidiReadObjectSpec.newBuilder()
                  .setBucket(METADATA.getBucket())
                  .setObject(METADATA.getName())
                  .build())
          .build();
  private static final BidiReadObjectResponse RES_OPEN =
      BidiReadObjectResponse.newBuilder().setMetadata(METADATA).build();
  private static final byte[] ALL_OBJECT_BYTES = DataGenerator.base64Characters().genBytes(64);
  private static final Metadata.Key<String> X_GOOG_REQUEST_PARAMS =
      Metadata.Key.of("x-goog-request-params", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> X_GOOG_USER_PROJECT =
      Metadata.Key.of("x-goog-user-project", Metadata.ASCII_STRING_MARSHALLER);

  /**
   *
   *
   * <ol>
   *   <li>Open blob descriptor
   *   <li>attempt to read bytes 10-20
   *   <li>server responds with a redirect
   *   <li>expect a new stream open with the specified redirect token, read handle and pending read
   *       of bytes 10-20
   * </ol>
   */
  @Test
  public void bidiReadObjectRedirectedError() throws Exception {

    String routingToken = UUID.randomUUID().toString();
    BidiReadHandle readHandle =
        BidiReadHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    BidiReadObjectRequest req2 = read(1, 10, 10);
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(1)
                    .setReadHandle(readHandle)
                    .setRoutingToken(routingToken)
                    .build())
            .addReadRanges(getReadRange(1, 10, 10))
            .build();

    ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 10);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content.asChecksummedData())
                    .setReadRange(getReadRange(1, 10, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN,
                respond -> respond.onNext(RES_OPEN),
                req2,
                respond -> {
                  BidiReadObjectRedirectedError redirect =
                      BidiReadObjectRedirectedError.newBuilder()
                          .setReadHandle(readHandle)
                          .setRoutingToken(routingToken)
                          .build();

                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(com.google.rpc.Code.UNAVAILABLE_VALUE)
                          .setMessage("redirect")
                          .addDetails(Any.pack(redirect))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                },
                req3,
                respond -> respond.onNext(res2)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureBlobDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureBlobDescriptor.get(5, TimeUnit.SECONDS)) {
        byte[] actual = bd.readRangeAsBytes(RangeSpec.of(10L, 10L)).get(1, TimeUnit.SECONDS);

        assertThat(xxd(actual)).isEqualTo(xxd(content.getBytes()));
      }
    }
  }

  /**
   *
   *
   * <ol>
   *   <li>Attempt to open blob descriptor
   *   <li>server responds with a redirect
   *   <li>expect a new stream open with the specified redirect token
   * </ol>
   */
  @Test
  public void bidiReadObjectRedirectedError_onOpen() throws Exception {
    String routingToken = UUID.randomUUID().toString();
    BidiReadHandle readHandle =
        BidiReadHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setReadHandle(readHandle)
                    .setRoutingToken(routingToken)
                    .build())
            .build();

    BidiReadObjectResponse res1 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN,
                respond -> {
                  BidiReadObjectRedirectedError redirect =
                      BidiReadObjectRedirectedError.newBuilder()
                          .setReadHandle(readHandle)
                          .setRoutingToken(routingToken)
                          .build();

                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(com.google.rpc.Code.UNAVAILABLE_VALUE)
                          .setMessage("redirect")
                          .addDetails(Any.pack(redirect))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                },
                req2,
                respond -> respond.onNext(res1)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setGrpcInterceptorProvider(
                    GrpcPlainRequestLoggingInterceptor.getInterceptorProvider())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureBlobDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureBlobDescriptor.get(5, TimeUnit.SECONDS)) {
        assertThat(bd).isNotNull();
      }
    }
  }

  @Test
  public void bidiReadObjectRedirectedError_maxRedirectAttempts() throws Exception {
    AtomicInteger reqCounter = new AtomicInteger(0);
    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> responseObserver) {
            return new AbstractObserver(responseObserver) {
              @Override
              public void onNext(BidiReadObjectRequest value) {
                int requestCount = reqCounter.incrementAndGet();
                BidiReadObjectRedirectedError redirect =
                    BidiReadObjectRedirectedError.newBuilder()
                        .setReadHandle(
                            BidiReadHandle.newBuilder()
                                .setHandle(
                                    ByteString.copyFromUtf8(
                                        String.format("handle-%03d", requestCount)))
                                .build())
                        .setRoutingToken(String.format("token-%03d", requestCount))
                        .build();

                com.google.rpc.Status grpcStatusDetails =
                    com.google.rpc.Status.newBuilder()
                        .setCode(com.google.rpc.Code.UNAVAILABLE_VALUE)
                        .setMessage(String.format("redirect %03d", requestCount))
                        .addDetails(Any.pack(redirect))
                        .build();

                Metadata trailers = new Metadata();
                trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                StatusRuntimeException statusRuntimeException =
                    Status.UNAVAILABLE
                        .withDescription(String.format("redirect %03d", requestCount))
                        .asRuntimeException(trailers);
                respond.onError(statusRuntimeException);
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureBlobDescriptor =
          storage.getBlobDescriptor(id, BlobSourceOption.userProject("user-project"));

      StorageException se =
          assertThrows(
              StorageException.class,
              () -> {
                try {
                  futureBlobDescriptor.get(5, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                  throw e.getCause();
                }
              });

      assertThat(se.getCode()).isEqualTo(503);
      assertThat(se).hasCauseThat().isInstanceOf(UnavailableException.class);
      assertThat(reqCounter.get()).isEqualTo(4);
    }
  }

  @Test
  public void bidiReadObjectError() throws Exception {

    ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 5);
    BidiReadObjectRequest req2 = read(1, 10, 10);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 10, 5))
                    .build())
            .build();
    BidiReadObjectError err2 =
        BidiReadObjectError.newBuilder()
            .addReadRangeErrors(
                ReadRangeError.newBuilder()
                    .setReadId(1)
                    .setStatus(
                        com.google.rpc.Status.newBuilder()
                            .setCode(com.google.rpc.Code.ABORTED_VALUE)
                            .build())
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 15, 5);
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(1)
                    .build())
            .addReadRanges(getReadRange(2, 15, 5))
            .build();
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 15, 5))
                    .setRangeEnd(true)
                    .build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN,
                respond -> respond.onNext(RES_OPEN),
                req2,
                respond -> {
                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(com.google.rpc.Code.UNAVAILABLE_VALUE)
                          .setMessage("fail read_id: 1")
                          .addDetails(Any.pack(err2))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onNext(res2);
                  respond.onError(statusRuntimeException);
                },
                req3,
                respond -> respond.onNext(res3)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureBlobDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureBlobDescriptor.get(5, TimeUnit.SECONDS)) {
        assertThrows(
            AbortedException.class,
            () -> {
              try {
                ApiFuture<byte[]> future = bd.readRangeAsBytes(RangeSpec.of(10L, 10L));
                future.get(5, TimeUnit.SECONDS);
              } catch (ExecutionException e) {
                throw e.getCause();
              }
            });
        byte[] actual = bd.readRangeAsBytes(RangeSpec.of(15L, 5L)).get(2, TimeUnit.SECONDS);
        assertThat(actual).hasLength(5);
        assertThat(xxd(actual)).isEqualTo(xxd(content3.getBytes()));
      }
    }
  }

  @Test
  public void expectRetryForRangeWithFailedChecksumValidation() throws Exception {

    ChecksummedTestContent expected = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 20);

    ChecksummedTestContent content2_1 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 10);
    ChecksummedTestContent content2_2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 20, 10);
    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2_1 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2_1.asChecksummedData())
                    .setReadRange(getReadRange(1, 10, 10))
                    .build())
            .build();
    BidiReadObjectResponse res2_2 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2_2.asChecksummedData().toBuilder().setCrc32C(1))
                    .setReadRange(getReadRange(1, 20, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    BidiReadObjectRequest req3 = read(2, 20, 10);
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2_2.asChecksummedData())
                    .setReadRange(getReadRange(2, 20, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN,
                respond -> respond.onNext(RES_OPEN),
                req2,
                respond -> {
                  respond.onNext(res2_1);
                  respond.onNext(res2_2);
                },
                req3,
                respond -> respond.onNext(res3)));

    runTestAgainstFakeServer(fake, RangeSpec.of(10L, 20L), expected);
  }

  @Test
  public void objectRangeData_offset_notAligned_lt() throws Exception {

    ChecksummedTestContent expected = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 20);

    ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 9, 20);
    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 9, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 29, 1);
    BidiReadObjectRequest req3 = read(2, 29, 1);
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 29, content3))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(REQ_OPEN, RES_OPEN)
            .put(req2, res2)
            .put(req3, res3)
            .buildOrThrow();

    runTestAgainstFakeServer(FakeStorage.from(db), RangeSpec.of(10L, 20L), expected);
  }

  @Test
  public void objectRangeData_offset_notAligned_gt() throws Exception {

    ChecksummedTestContent expected = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 20);

    ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 11, 20);
    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 11, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 20);
    BidiReadObjectRequest req3 = read(2, 10, 20);
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(METADATA)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 10, content3))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(REQ_OPEN, RES_OPEN)
            .put(req2, res2)
            .put(req3, res3)
            .buildOrThrow();

    runTestAgainstFakeServer(FakeStorage.from(db), RangeSpec.of(10L, 20L), expected);
  }

  @Test
  public void readRangeDoesNotSendARequestIfTheRangeWouldResultInZeroBytes() throws Exception {

    ChecksummedTestContent expected = ChecksummedTestContent.of(new byte[0]);

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(REQ_OPEN, RES_OPEN)
            .buildOrThrow();

    runTestAgainstFakeServer(FakeStorage.from(db), RangeSpec.of(_2MiB, 8192), expected);
  }

  @Test
  public void readRange_retrySettingsApplicable_attempt() throws Exception {

    AtomicInteger reqCounter = new AtomicInteger(0);
    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> responseObserver) {
            return new AbstractObserver(responseObserver) {
              @Override
              public void onNext(BidiReadObjectRequest request) {
                int reqCount = reqCounter.getAndIncrement();
                if (request.equals(REQ_OPEN)) {
                  respond.onNext(RES_OPEN);
                } else {

                  BidiReadObjectResponse.Builder b = BidiReadObjectResponse.newBuilder();
                  request.getReadRangesList().stream()
                      .map(r -> r.toBuilder().setReadLength(1).build())
                      .map(
                          r ->
                              ObjectRangeData.newBuilder()
                                  .setReadRange(r)
                                  .setChecksummedData(
                                      ChecksummedData.newBuilder()
                                          .setContent(ByteString.copyFrom(new byte[] {'A'}))
                                          // explicitly send a bad checksum to induce failure
                                          .setCrc32C(reqCount)
                                          .build())
                                  .build())
                      .forEach(b::addObjectDataRanges);

                  respond.onNext(b.build());
                }
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(3).build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureBlobDescriptor = storage.getBlobDescriptor(id);
      try (BlobDescriptor bd = futureBlobDescriptor.get(5, TimeUnit.SECONDS)) {
        ApiFuture<byte[]> future = bd.readRangeAsBytes(RangeSpec.of(10, 10));

        DataLossException dataLossException =
            assertThrows(
                DataLossException.class, () -> TestUtils.await(future, 5, TimeUnit.SECONDS));

        assertThat(dataLossException).isInstanceOf(UncheckedChecksumMismatchException.class);
        Throwable[] suppressed = dataLossException.getSuppressed();
        String suppressedMessages =
            Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.joining("\n"));
        assertAll(
            () ->
                assertThat(suppressedMessages)
                    .contains("Operation failed to complete within attempt budget"),
            () ->
                assertThat(suppressedMessages)
                    .contains(
                        "Mismatch checksum value. Expected crc32c{0x00000001} actual crc32c{0xe16dcdee}"),
            () ->
                assertThat(suppressedMessages)
                    .contains(
                        "Mismatch checksum value. Expected crc32c{0x00000002} actual crc32c{0xe16dcdee}"),
            () -> assertThat(suppressedMessages).contains("Asynchronous task failed"));
      }
    }
  }

  @Test
  public void retrySettingsApplicable_objectRangeData_offset_notAligned_gt() throws Exception {

    ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 11, 20);
    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 11, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 12, 20);
    BidiReadObjectRequest req3 = read(2, 10, 20);
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 12, content3))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(REQ_OPEN, RES_OPEN)
            .put(req2, res2)
            .put(req3, res3)
            .buildOrThrow();

    try (FakeServer fakeServer = FakeServer.of(FakeStorage.from(db));
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(2).build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        ApiFuture<byte[]> future = bd.readRangeAsBytes(RangeSpec.of(10L, 20L));

        OutOfRangeException outOfRangeException =
            assertThrows(
                OutOfRangeException.class, () -> TestUtils.await(future, 5, TimeUnit.SECONDS));

        assertThat(outOfRangeException).isInstanceOf(OutOfRangeException.class);
        Throwable[] suppressed = outOfRangeException.getSuppressed();
        String suppressedMessages =
            Arrays.stream(suppressed).map(Throwable::getMessage).collect(Collectors.joining("\n"));
        assertAll(
            () ->
                assertThat(suppressedMessages)
                    .contains("Operation failed to complete within attempt budget"),
            () ->
                assertThat(suppressedMessages)
                    .contains("position = 10, readRange.read_offset = 11"),
            () -> assertThat(suppressedMessages).contains("Asynchronous task failed"));
      }
    }
  }

  @Test
  public void moreBytesReturnedThanRequested_onlyForwardsRequestedBytes() throws Exception {

    ChecksummedTestContent expected = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 20);
    ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 21);
    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 10, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(REQ_OPEN, RES_OPEN)
            .put(req2, res2)
            .buildOrThrow();

    runTestAgainstFakeServer(FakeStorage.from(db), RangeSpec.of(10, 20), expected);
  }

  @Test
  public void validateReadRemovedFromStateWhenFailed() throws Exception {

    BidiReadObjectRequest req2 = read(1, 10, 20);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setReadRange(req2.getReadRangesList().get(0))
                    .setChecksummedData(
                        ChecksummedData.newBuilder()
                            .setContent(ByteString.copyFrom(new byte[] {'A'}))
                            // explicitly send a bad checksum to induce failure
                            .setCrc32C(1)
                            .build())
                    .build())
            .build();

    FakeStorage fake = FakeStorage.from(ImmutableMap.of(REQ_OPEN, RES_OPEN, req2, res2));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        BlobDescriptorImpl bdi = null;
        if (bd instanceof BlobDescriptorImpl) {
          bdi = (BlobDescriptorImpl) bd;
        } else {
          fail("unable to locate state for validation");
        }

        ApiFuture<byte[]> future = bd.readRangeAsBytes(RangeSpec.of(10, 20));
        ExecutionException ee =
            assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));

        assertThat(ee).hasCauseThat().isInstanceOf(UncheckedChecksumMismatchException.class);

        BlobDescriptorStreamRead outstandingRead = bdi.state.getOutstandingRead(1L);
        assertThat(outstandingRead).isNull();
      }
    }
  }

  @Test
  public void requestOptionsShouldBePresentInRequest() throws Exception {

    String keyB64 = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";
    Key key = new SecretKeySpec(BaseEncoding.base64().decode(keyB64), "AES256");
    byte[] keySha256 = Hashing.sha256().hashBytes(key.getEncoded()).asBytes();
    BidiReadObjectRequest reqOpen =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setIfGenerationMatch(1)
                    .setIfGenerationNotMatch(2)
                    .setIfMetagenerationMatch(3)
                    .setIfMetagenerationNotMatch(4)
                    .setCommonObjectRequestParams(
                        CommonObjectRequestParams.newBuilder()
                            .setEncryptionAlgorithm("AES256")
                            .setEncryptionKeyBytes(ByteString.copyFrom(key.getEncoded()))
                            .setEncryptionKeySha256Bytes(ByteString.copyFrom(keySha256))))
            .build();
    BidiReadObjectResponse resOpen =
        BidiReadObjectResponse.newBuilder().setMetadata(METADATA).build();

    FakeStorage fake = FakeStorage.from(ImmutableMap.of(reqOpen, resOpen));

    GrpcRequestAuditing requestAuditing = new GrpcRequestAuditing();
    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build())
                .setGrpcInterceptorProvider(
                    () ->
                        ImmutableList.of(
                            requestAuditing, GrpcPlainRequestLoggingInterceptor.getInstance()))
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor =
          storage.getBlobDescriptor(
              id,
              BlobSourceOption.generationMatch(1),
              BlobSourceOption.generationNotMatch(2),
              BlobSourceOption.metagenerationMatch(3),
              BlobSourceOption.metagenerationNotMatch(4),
              BlobSourceOption.decryptionKey(key),
              BlobSourceOption.userProject("my-awesome-project"));

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        // by the time we reach here the test has already passed/failed
        assertAll(
            () -> assertThat(bd).isNotNull(),
            () ->
                requestAuditing
                    .assertRequestHeader(X_GOOG_REQUEST_PARAMS)
                    .contains("bucket=" + METADATA.getBucket()),
            () ->
                requestAuditing
                    .assertRequestHeader(X_GOOG_USER_PROJECT)
                    .contains("my-awesome-project"));
      }
    }
  }

  @Test
  public void failedStreamRestartShouldFailAllPendingReads() throws Exception {
    final Set<BidiReadObjectRequest> reads = Collections.synchronizedSet(new HashSet<>());
    StorageImplBase fakeStorage =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> responseObserver) {
            return new AbstractObserver(responseObserver) {
              @Override
              public void onNext(BidiReadObjectRequest request) {
                if (request.equals(REQ_OPEN)) {
                  respond.onNext(RES_OPEN);
                  return;
                }

                reads.add(request);

                if (reads.size() == 3) {
                  respond.onError(Status.UNAVAILABLE.asRuntimeException());
                }
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fakeStorage);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        ApiFuture<byte[]> f1 = bd.readRangeAsBytes(RangeSpec.of(1, 1));
        ApiFuture<byte[]> f2 = bd.readRangeAsBytes(RangeSpec.of(2, 2));
        ApiFuture<byte[]> f3 = bd.readRangeAsBytes(RangeSpec.of(3, 3));

        List<byte[]> successful =
            ApiFutures.successfulAsList(ImmutableList.of(f1, f2, f3)).get(5, TimeUnit.SECONDS);
        assertThat(successful).isEqualTo(Lists.newArrayList(null, null, null));

        assertAll(
            () -> {
              Set<String> readRanges =
                  reads.stream()
                      .map(BidiReadObjectRequest::getReadRangesList)
                      .flatMap(Collection::stream)
                      .map(ITBlobDescriptorFakeTest::fmt)
                      .collect(Collectors.toSet());
              Set<String> expected =
                  Stream.of(getReadRange(1, 1, 1), getReadRange(2, 2, 2), getReadRange(3, 3, 3))
                      .map(ITBlobDescriptorFakeTest::fmt)
                      .collect(Collectors.toSet());
              assertThat(readRanges).isEqualTo(expected);
            },
            assert503(f1),
            assert503(f2),
            assert503(f3));
      }
    }
  }

  // todo: in the future this should also interrupt and fail any child streams.
  //   for example, when an individual range is streamed and we don't want backpressure
  //   from the consumer to slow down the network stream of all reads.
  @Test
  public void closingBlobDescriptorShouldFailAllPendingReads() throws Exception {
    BidiReadObjectRequest req2 = read(1, 1, 1);
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setReadRange(req2.getReadRangesList().get(0))
                    .setChecksummedData(
                        getChecksummedData(ByteString.copyFromUtf8("A"), Hasher.enabled()))
                    .setRangeEnd(true))
            .build();
    final Set<BidiReadObjectRequest> reads = Collections.synchronizedSet(new HashSet<>());
    StorageImplBase fakeStorage =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> responseObserver) {
            return new AbstractObserver(responseObserver) {
              @Override
              public void onNext(BidiReadObjectRequest request) {
                if (request.equals(REQ_OPEN)) {
                  respond.onNext(RES_OPEN);
                  return;
                } else if (request.equals(req2)) {
                  respond.onNext(res2);
                }
                reads.add(request);
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fakeStorage);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        // issue three different range reads
        ApiFuture<byte[]> f1 = bd.readRangeAsBytes(RangeSpec.of(1, 1));
        ApiFuture<byte[]> f2 = bd.readRangeAsBytes(RangeSpec.of(2, 2));
        ApiFuture<byte[]> f3 = bd.readRangeAsBytes(RangeSpec.of(3, 3));

        // close the "parent"
        bd.close();

        assertAll(
            () -> {
              // make sure all three ranges were sent to the server
              Set<String> readRanges =
                  reads.stream()
                      .map(BidiReadObjectRequest::getReadRangesList)
                      .flatMap(Collection::stream)
                      .map(ITBlobDescriptorFakeTest::fmt)
                      .collect(Collectors.toSet());
              Set<String> expected =
                  Stream.of(getReadRange(1, 1, 1), getReadRange(2, 2, 2), getReadRange(3, 3, 3))
                      .map(ITBlobDescriptorFakeTest::fmt)
                      .collect(Collectors.toSet());
              assertThat(readRanges).isEqualTo(expected);
            },
            () -> {
              // make sure the first read succeeded
              byte[] actual = TestUtils.await(f1, 5, TimeUnit.SECONDS);
              assertThat(ByteString.copyFrom(actual)).isEqualTo(ByteString.copyFromUtf8("A"));
            },
            // make sure the other two pending reads fail
            assertStatusCodeIs(f2, 0),
            assertStatusCodeIs(f3, 0),
            () -> {
              // the futures are already verified to be resolved based on the two previous
              // assertions get them again for our additional assertions
              ExecutionException ee2 = assertThrows(ExecutionException.class, f2::get);
              ExecutionException ee3 = assertThrows(ExecutionException.class, f3::get);
              StorageException se2 = (StorageException) ee2.getCause();
              StorageException se3 = (StorageException) ee3.getCause();

              assertAll(
                  () -> assertThat(se2).isNotSameInstanceAs(se3),
                  () ->
                      assertThat(se2).hasCauseThat().isInstanceOf(AsynchronousCloseException.class),
                  () ->
                      assertThat(se3)
                          .hasCauseThat()
                          .isInstanceOf(AsynchronousCloseException.class));
            });
      }
    }
  }

  private static void runTestAgainstFakeServer(
      FakeStorage fakeStorage, RangeSpec range, ChecksummedTestContent expected) throws Exception {

    try (FakeServer fakeServer = FakeServer.of(fakeStorage);
        Storage storage = fakeServer.getGrpcStorageOptions().getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        ApiFuture<byte[]> future = bd.readRangeAsBytes(range);

        byte[] actual = future.get(5, TimeUnit.SECONDS);
        Crc32cLengthKnown actualCrc32c = Hasher.enabled().hash(ByteBuffer.wrap(actual));

        byte[] expectedBytes = expected.getBytes();
        Crc32cLengthKnown expectedCrc32c =
            Crc32cValue.of(expected.getCrc32c(), expectedBytes.length);

        assertAll(
            () -> assertThat(actual).hasLength(expectedBytes.length),
            () -> assertThat(xxd(actual)).isEqualTo(xxd(expectedBytes)),
            () -> assertThat(actualCrc32c).isEqualTo(expectedCrc32c));
      }
    }
  }

  private static BidiReadObjectRequest read(int readId, int readOffset, int readLimit) {
    return BidiReadObjectRequest.newBuilder()
        .addReadRanges(getReadRange(readId, readOffset, readLimit))
        .build();
  }

  private static ReadRange getReadRange(
      int readId, int readOffset, ChecksummedTestContent content) {
    return getReadRange(readId, readOffset, content.asChecksummedData().getContent().size());
  }

  private static ReadRange getReadRange(int readId, int readOffset, int readLimit) {
    return ReadRange.newBuilder()
        .setReadId(readId)
        .setReadOffset(readOffset)
        .setReadLength(readLimit)
        .build();
  }

  private static ThrowingRunnable assert503(ApiFuture<?> f) {
    return assertStatusCodeIs(f, 503);
  }

  private static ThrowingRunnable assertStatusCodeIs(ApiFuture<?> f, int expected) {
    return () -> {
      StorageException se =
          assertThrows(StorageException.class, () -> TestUtils.await(f, 5, TimeUnit.SECONDS));
      assertThat(se.getCode()).isEqualTo(expected);
    };
  }

  private static String fmt(ReadRange r) {
    return String.format(
        "ReadRange{id: %d, offset: %d, length: %d}",
        r.getReadId(), r.getReadOffset(), r.getReadLength());
  }

  private static final class FakeStorage extends StorageImplBase {

    private final Map<BidiReadObjectRequest, Consumer<StreamObserver<BidiReadObjectResponse>>> db;

    private FakeStorage(
        Map<BidiReadObjectRequest, Consumer<StreamObserver<BidiReadObjectResponse>>> db) {
      this.db = db;
    }

    @Override
    public StreamObserver<BidiReadObjectRequest> bidiReadObject(
        StreamObserver<BidiReadObjectResponse> respond) {
      return new AbstractObserver(respond) {
        @Override
        public void onNext(BidiReadObjectRequest req) {
          if (db.containsKey(req)) {
            db.get(req).accept(respond);
          } else {
            respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
          }
        }
      };
    }

    private static FakeStorage of(
        Map<BidiReadObjectRequest, Consumer<StreamObserver<BidiReadObjectResponse>>> db) {
      return new FakeStorage(db);
    }

    private static FakeStorage from(Map<BidiReadObjectRequest, BidiReadObjectResponse> db) {
      return new FakeStorage(Maps.transformValues(db, resp -> (respond) -> respond.onNext(resp)));
    }
  }

  private abstract static class AbstractObserver implements StreamObserver<BidiReadObjectRequest> {

    protected final StreamObserver<BidiReadObjectResponse> respond;

    private AbstractObserver(StreamObserver<BidiReadObjectResponse> respond) {
      this.respond = respond;
    }

    @Override
    public void onError(Throwable t) {
      respond.onError(t);
    }

    @Override
    public void onCompleted() {
      respond.onCompleted();
    }
  }
}
