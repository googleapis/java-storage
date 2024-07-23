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

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.cloud.storage.it.GrpcPlainRequestLoggingInterceptor;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BidiReadHandle;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectRangeData;
import com.google.storage.v2.ReadRange;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

  private static ReadRange getReadRange(int readId, int readOffset, int readLimit) {
    return ReadRange.newBuilder()
        .setReadId(readId)
        .setReadOffset(readOffset)
        .setReadLength(readLimit)
        .build();
  }
}
