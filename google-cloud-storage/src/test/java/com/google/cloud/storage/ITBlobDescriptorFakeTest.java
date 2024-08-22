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

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.AbortedException;
import com.google.api.gax.rpc.UnavailableException;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.cloud.storage.it.GrpcPlainRequestLoggingInterceptor;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectError;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import com.google.storage.v2.ReadRange;
import com.google.storage.v2.ReadRangeError;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public final class ITBlobDescriptorFakeTest {

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
    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .build())
            .build();
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(1, 10, 10)).build();
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .setGeneration(1)
                    .setReadHandle(readHandle)
                    .setRoutingToken(routingToken)
                    .build())
            .addReadRanges(getReadRange(1, 10, 10))
            .build();

    BidiReadObjectResponse res1 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .build();

    ChecksummedTestContent content =
        ChecksummedTestContent.of(
            Arrays.copyOfRange(DataGenerator.base64Characters().genBytes(64), 10, 20));
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content.asChecksummedData())
                    .setReadRange(getReadRange(1, 10, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
              @Override
              public void onNext(BidiReadObjectRequest value) {
                if (req1.equals(value)) {
                  respond.onNext(res1);
                } else if (req2.equals(value)) {
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
                  trailers.put(GrpcUtils.GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                } else if (req3.equals(value)) {
                  respond.onNext(res2);
                } else {
                  respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
                }
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

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
        byte[] actual =
            bd.readRangeAsBytes(ByteRangeSpec.relativeLength(10L, 10L)).get(1, TimeUnit.SECONDS);

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
    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .build())
            .build();
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .setReadHandle(readHandle)
                    .setRoutingToken(routingToken)
                    .build())
            .build();

    BidiReadObjectResponse res1 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .build();

    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
              @Override
              public void onNext(BidiReadObjectRequest value) {
                if (req1.equals(value)) {
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
                  trailers.put(GrpcUtils.GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                } else if (req2.equals(value)) {
                  respond.onNext(res1);
                } else {
                  respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
                }
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

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
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
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
                trailers.put(GrpcUtils.GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                StatusRuntimeException statusRuntimeException =
                    Status.UNAVAILABLE
                        .withDescription(String.format("redirect %03d", requestCount))
                        .asRuntimeException(trailers);
                respond.onError(statusRuntimeException);
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

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

    String routingToken = UUID.randomUUID().toString();
    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .build())
            .build();
    BidiReadObjectResponse res1 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .build();

    ChecksummedTestContent content2 =
        ChecksummedTestContent.of(
            Arrays.copyOfRange(DataGenerator.base64Characters().genBytes(64), 10, 20));
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(1, 10, 10)).build();
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
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

    ChecksummedTestContent content3 =
        ChecksummedTestContent.of(
            Arrays.copyOfRange(DataGenerator.base64Characters().genBytes(64), 15, 20));
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket("projects/_/buckets/b")
                    .setObject("o")
                    .setGeneration(1)
                    .build())
            .addReadRanges(getReadRange(2, 15, 5))
            .build();
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(Object.newBuilder().setBucket("b").setName("o").setGeneration(1).build())
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 15, 5))
                    .setRangeEnd(true)
                    .build())
            .build();

    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
              @Override
              public void onNext(BidiReadObjectRequest value) {
                if (req1.equals(value)) {
                  respond.onNext(res1);
                } else if (req2.equals(value)) {
                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(com.google.rpc.Code.UNAVAILABLE_VALUE)
                          .setMessage("redirect")
                          .addDetails(Any.pack(err2))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GrpcUtils.GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.UNAVAILABLE.withDescription("redirect").asRuntimeException(trailers);
                  respond.onNext(res2);
                  respond.onError(statusRuntimeException);
                } else if (req3.equals(value)) {
                  respond.onNext(res3);
                } else {
                  respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
                }
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

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
        assertThrows(
            AbortedException.class,
            () -> {
              try {
                ApiFuture<byte[]> future =
                    bd.readRangeAsBytes(ByteRangeSpec.relativeLength(10L, 10L));
                future.get(5, TimeUnit.SECONDS);
              } catch (ExecutionException e) {
                throw e.getCause();
              }
            });
        byte[] actual =
            bd.readRangeAsBytes(ByteRangeSpec.relativeLength(15L, 5L)).get(2, TimeUnit.SECONDS);
        assertThat(actual).hasLength(5);
        assertThat(xxd(actual)).isEqualTo(xxd(content3.getBytes()));
      }
    }
  }

  @Test
  public void objectRangeData_checksumFailure() throws Exception {

    Object metadata =
        Object.newBuilder()
            .setBucket(BucketName.format("_", "b"))
            .setName("o")
            .setGeneration(1)
            .build();
    byte[] b64bytes = DataGenerator.base64Characters().genBytes(64);
    ChecksummedTestContent expected =
        ChecksummedTestContent.of(Arrays.copyOfRange(b64bytes, 10, 30));

    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(metadata.getBucket())
                    .setObject(metadata.getName())
                    .build())
            .build();
    BidiReadObjectResponse res1 = BidiReadObjectResponse.newBuilder().setMetadata(metadata).build();

    ChecksummedTestContent content2_1 =
        ChecksummedTestContent.of(Arrays.copyOfRange(b64bytes, 10, 20));
    ChecksummedTestContent content2_2 =
        ChecksummedTestContent.of(Arrays.copyOfRange(b64bytes, 20, 30));
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(1, 10, 20)).build();
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
            .setMetadata(metadata)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2_2.asChecksummedData().toBuilder().setCrc32C(1))
                    .setReadRange(getReadRange(1, 20, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(2, 20, 10)).build();
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(metadata)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2_2.asChecksummedData())
                    .setReadRange(getReadRange(2, 20, 10))
                    .setRangeEnd(true)
                    .build())
            .build();

    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
              @Override
              public void onNext(BidiReadObjectRequest value) {
                if (req1.equals(value)) {
                  respond.onNext(res1);
                } else if (req2.equals(value)) {
                  respond.onNext(res2_1);
                  respond.onNext(res2_2);
                } else if (req3.equals(value)) {
                  respond.onNext(res3);
                } else {
                  respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
                }
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().getService()) {

      BlobId id = BlobId.of("b", "o");
      ApiFuture<BlobDescriptor> futureObjectDescriptor = storage.getBlobDescriptor(id);

      try (BlobDescriptor bd = futureObjectDescriptor.get(5, TimeUnit.SECONDS)) {
        ApiFuture<byte[]> future = bd.readRangeAsBytes(ByteRangeSpec.relativeLength(10L, 20L));

        byte[] actual = future.get(5, TimeUnit.SECONDS);
        Crc32cLengthKnown actualCrc32c = Hasher.enabled().hash(ByteBuffer.wrap(actual));

        byte[] expectedBytes = expected.getBytes();
        Crc32cLengthKnown expectedCrc32c =
            Crc32cValue.of(expected.getCrc32c(), expectedBytes.length);

        assertAll(
            () -> assertThat(actual).hasLength(20),
            () -> assertThat(xxd(actual)).isEqualTo(xxd(expectedBytes)),
            () -> assertThat(actualCrc32c).isEqualTo(expectedCrc32c));
      }
    }
  }

  @Test
  public void objectRangeData_offset_notAligned_lt() throws Exception {

    Object metadata =
        Object.newBuilder()
            .setBucket(BucketName.format("_", "b"))
            .setName("o")
            .setGeneration(1)
            .build();
    byte[] b64bytes = DataGenerator.base64Characters().genBytes(64);
    ChecksummedTestContent expected = ChecksummedTestContent.of(b64bytes, 10, 20);

    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(metadata.getBucket())
                    .setObject(metadata.getName())
                    .build())
            .build();
    BidiReadObjectResponse res1 = BidiReadObjectResponse.newBuilder().setMetadata(metadata).build();

    ChecksummedTestContent content2 = ChecksummedTestContent.of(b64bytes, 9, 20);
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(1, 10, 20)).build();
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 9, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(b64bytes, 29, 1);
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(2, 29, 1)).build();
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(metadata)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 29, content3))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(req1, res1)
            .put(req2, res2)
            .put(req3, res3)
            .buildOrThrow();

    runTestAgainstFakeServer(expected, db, ByteRangeSpec.relativeLength(10L, 20L));
  }

  @Test
  public void objectRangeData_offset_notAligned_gt() throws Exception {

    Object metadata =
        Object.newBuilder()
            .setBucket(BucketName.format("_", "b"))
            .setName("o")
            .setGeneration(1)
            .build();
    byte[] b64bytes = DataGenerator.base64Characters().genBytes(64);
    ChecksummedTestContent expected = ChecksummedTestContent.of(b64bytes, 10, 20);

    BidiReadObjectRequest req1 =
        BidiReadObjectRequest.newBuilder()
            .setReadObjectSpec(
                BidiReadObjectSpec.newBuilder()
                    .setBucket(metadata.getBucket())
                    .setObject(metadata.getName())
                    .build())
            .build();
    BidiReadObjectResponse res1 = BidiReadObjectResponse.newBuilder().setMetadata(metadata).build();

    ChecksummedTestContent content2 = ChecksummedTestContent.of(b64bytes, 11, 20);
    BidiReadObjectRequest req2 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(1, 10, 20)).build();
    BidiReadObjectResponse res2 =
        BidiReadObjectResponse.newBuilder()
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content2.asChecksummedData())
                    .setReadRange(getReadRange(1, 11, content2))
                    .setRangeEnd(true)
                    .build())
            .build();

    ChecksummedTestContent content3 = ChecksummedTestContent.of(b64bytes, 10, 20);
    BidiReadObjectRequest req3 =
        BidiReadObjectRequest.newBuilder().addReadRanges(getReadRange(2, 10, 20)).build();
    BidiReadObjectResponse res3 =
        BidiReadObjectResponse.newBuilder()
            .setMetadata(metadata)
            .addObjectDataRanges(
                ObjectRangeData.newBuilder()
                    .setChecksummedData(content3.asChecksummedData())
                    .setReadRange(getReadRange(2, 10, content3))
                    .setRangeEnd(true)
                    .build())
            .build();

    ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db =
        ImmutableMap.<BidiReadObjectRequest, BidiReadObjectResponse>builder()
            .put(req1, res1)
            .put(req2, res2)
            .put(req3, res3)
            .buildOrThrow();

    runTestAgainstFakeServer(expected, db, ByteRangeSpec.relativeLength(10L, 20L));
  }

  private void runTestAgainstFakeServer(
      ChecksummedTestContent expected,
      ImmutableMap<BidiReadObjectRequest, BidiReadObjectResponse> db,
      ByteRangeSpec range)
      throws Exception {

    StorageImplBase fake =
        new StorageImplBase() {
          @Override
          public StreamObserver<BidiReadObjectRequest> bidiReadObject(
              StreamObserver<BidiReadObjectResponse> respond) {
            return new StreamObserver<BidiReadObjectRequest>() {
              @Override
              public void onNext(BidiReadObjectRequest req) {
                if (db.containsKey(req)) {
                  respond.onNext(db.get(req));
                } else {
                  respond.onError(TestUtils.apiException(Code.UNIMPLEMENTED, "Unexpected request"));
                }
              }

              @Override
              public void onError(Throwable t) {
                respond.onError(t);
              }

              @Override
              public void onCompleted() {
                respond.onCompleted();
              }
            };
          }
        };

    try (FakeServer fakeServer = FakeServer.of(fake);
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

  private ReadRange getReadRange(int readId, int readOffset, ChecksummedTestContent content) {
    return getReadRange(readId, readOffset, content.asChecksummedData().getContent().size());
  }

  private static ReadRange getReadRange(int readId, int readOffset, int readLimit) {
    return ReadRange.newBuilder()
        .setReadId(readId)
        .setReadOffset(readOffset)
        .setReadLength(readLimit)
        .build();
  }
}
