/*
 * Copyright 2025 Google LLC
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
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.AbortedException;
import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import com.google.rpc.Code;
import com.google.storage.v2.AppendObjectSpec;
import com.google.storage.v2.BidiWriteHandle;
import com.google.storage.v2.BidiWriteObjectRedirectedError;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageGrpc;
import com.google.storage.v2.WriteObjectSpec;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.Test;

public class ITAppendableUploadFakeTest {
  private static final byte[] ALL_OBJECT_BYTES = DataGenerator.base64Characters().genBytes(64);

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
  private static final BidiWriteObjectRequest REQ_OPEN =
      BidiWriteObjectRequest.newBuilder()
          .setWriteObjectSpec(
              WriteObjectSpec.newBuilder()
                  .setResource(
                      Object.newBuilder()
                          .setBucket(METADATA.getBucket())
                          .setName(METADATA.getName()))
                  .setAppendable(true)
                  .build())
          .setChecksummedData(
              ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABCDE")).build())
          .build();

  private static final BlobAppendableUploadConfig UPLOAD_CONFIG =
      BlobAppendableUploadConfig.of()
          .withFlushPolicy(FlushPolicy.maxFlushSize(5))
          .withCrc32cValidationEnabled(false)
          .withCloseAction(CloseAction.FINALIZE_WHEN_CLOSING);

  /**
   *
   *
   * <ol>
   *   <li>Create a new appendable object
   *   <li>First results give redirect error
   *   <li>Retry using a new AppendObjectSpec with routing token, generation, write handle specified
   *       -- retry succeeds
   *   <li>Finish writing the data as normal on the new stream
   * </ol>
   */
  @Test
  public void bidiWriteObjectRedirectedError() throws Exception {

    String routingToken = UUID.randomUUID().toString();
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setRoutingToken(routingToken)
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req3 =
        BidiWriteObjectRequest.newBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABCDE")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectRequest req4 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(5)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("FGHIJ")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();
    BidiWriteObjectRequest req5 =
        BidiWriteObjectRequest.newBuilder().setWriteOffset(10).setFinishWrite(true).build();

    ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
    BidiWriteObjectResponse res2 = BidiWriteObjectResponse.newBuilder().setPersistedSize(0).build();
    BidiWriteObjectResponse res3 = BidiWriteObjectResponse.newBuilder().setPersistedSize(5).build();

    BidiWriteObjectResponse res4 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(10).build();

    BidiWriteObjectResponse res5 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN.toBuilder().setFlush(true).setStateLookup(true).build(),
                respond -> {
                  BidiWriteObjectRedirectedError redirect =
                      BidiWriteObjectRedirectedError.newBuilder()
                          .setWriteHandle(writeHandle)
                          .setRoutingToken(routingToken)
                          .setGeneration(METADATA.getGeneration())
                          .build();

                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(Code.ABORTED_VALUE)
                          .setMessage("redirect")
                          .addDetails(Any.pack(redirect))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.ABORTED.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                },
                req2,
                respond -> respond.onNext(res2),
                req3,
                respond -> respond.onNext(res3),
                req4,
                respond -> respond.onNext(res4),
                req5,
                respond -> respond.onNext(res5)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      try (AppendableUploadWriteableByteChannel channel = b.open()) {
        channel.write(ByteBuffer.wrap(content.getBytes()));
      }
      BlobInfo bi = b.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(10);
    }
  }

  @Test
  public void bidiWriteObjectRedirectedError_maxAttempts() throws Exception {
    // todo: This test fails currently
    String routingToken = UUID.randomUUID().toString();

    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setRoutingToken(routingToken)
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRedirectedError redirect =
        BidiWriteObjectRedirectedError.newBuilder()
            .setWriteHandle(writeHandle)
            .setRoutingToken(routingToken)
            .setGeneration(METADATA.getGeneration())
            .build();

    com.google.rpc.Status grpcStatusDetails =
        com.google.rpc.Status.newBuilder()
            .setCode(Code.ABORTED_VALUE)
            .setMessage("redirect")
            .addDetails(Any.pack(redirect))
            .build();

    Metadata trailers = new Metadata();
    trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
    StatusRuntimeException statusRuntimeException =
        Status.ABORTED.withDescription("redirect").asRuntimeException(trailers);

    // TODO: assert number of redirects returned
    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN.toBuilder().setFlush(true).setStateLookup(true).build(),
                respond -> respond.onError(statusRuntimeException),
                req2,
                respond -> respond.onError(statusRuntimeException)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage =
            fakeServer
                .getGrpcStorageOptions()
                .toBuilder()
                .setRetrySettings(
                    fakeServer
                        .getGrpcStorageOptions()
                        .getRetrySettings()
                        .toBuilder()
                        .setRetryDelayMultiplier(1.0)
                        .setInitialRetryDelayDuration(Duration.ofMillis(10))
                        .build())
                .build()
                .getService()) {

      BlobId id = BlobId.of("b", "o");
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      AppendableUploadWriteableByteChannel channel = b.open();
      try {
        StorageException e =
            assertThrows(
                StorageException.class,
                () -> {
                  channel.write(ByteBuffer.wrap("ABCDE".getBytes()));
                });
        assertThat(e).hasCauseThat().isInstanceOf(AbortedException.class);
      } finally {
        channel.close();
      }
    }
  }

  /**
   *
   *
   * <ol>
   *   <li>Create a new appendable object, write 5 bytes, first result succeeds
   *   <li>Write 5 more bytes--server responds with a retryable error
   *   <li>Retry using a new AppendObjectSpec with generation, write handle specified -- retry
   *       succeeds
   *   <li>Finish writing the data as normal on the new stream
   * </ol>
   */
  @Test
  public void bidiWriteObjectRetryableError() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(5)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(5)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("FGHIJ")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectRequest req3 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req5 =
        BidiWriteObjectRequest.newBuilder().setWriteOffset(10).setFinishWrite(true).build();

    BidiWriteObjectResponse res3 = BidiWriteObjectResponse.newBuilder().setPersistedSize(5).build();

    BidiWriteObjectResponse res4 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(10).build();

    BidiWriteObjectResponse res5 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    final AtomicBoolean retried = new AtomicBoolean(false);

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN.toBuilder().setFlush(true).setStateLookup(true).build(),
                respond -> respond.onNext(res1),
                req2,
                respond -> {
                  // This same request gets run twice, the first time (as the second request),
                  // it gets an error. The second time (as the fourth request) it succeeds.
                  if (!retried.get()) {
                    respond.onError(Status.INTERNAL.asRuntimeException());
                    retried.set(true);
                  } else {
                    respond.onNext(res4);
                  }
                },
                req3,
                respond -> respond.onNext(res3),
                req5,
                respond -> respond.onNext(res5)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      try (AppendableUploadWriteableByteChannel channel = b.open()) {
        channel.write(ByteBuffer.wrap(content.getBytes()));
      }
      BlobInfo bi = b.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(10);
    }
  }

  /**
   *
   *
   * <ol>
   *   <li>Create a new appendable object, write 5 bytes, first result succeeds
   *   <li>Write 5 more bytes--server responds with a retryable error
   *   <li>Retry using a new AppendObjectSpec with generation, write handle specified
   *   <li>GCS responds with a persisted size indicating a partial write
   *   <li>Client responds by taking the partial success into account and skipping some bytes on the
   *       retry
   *   <li>Finish writing the data as normal on the new stream
   * </ol>
   */
  @Test
  public void retryableErrorIncompleteFlush() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(5)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(5)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("FGHIJ")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectRequest req3 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req5 =
        BidiWriteObjectRequest.newBuilder().setWriteOffset(10).setFinishWrite(true).build();

    BidiWriteObjectResponse res3 = BidiWriteObjectResponse.newBuilder().setPersistedSize(7).build();

    BidiWriteObjectRequest req4 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(7)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("HIJ")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectResponse res4 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(10).build();

    BidiWriteObjectResponse res5 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                REQ_OPEN.toBuilder().setFlush(true).setStateLookup(true).build(),
                respond -> respond.onNext(res1),
                req2,
                respond -> respond.onError(Status.INTERNAL.asRuntimeException()),
                req3,
                respond -> respond.onNext(res3),
                req4,
                respond -> respond.onNext(res4),
                req5,
                respond -> respond.onNext(res5)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      try (AppendableUploadWriteableByteChannel channel = b.open()) {
        channel.write(ByteBuffer.wrap(content.getBytes()));
      }
      BlobInfo bi = b.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(10);
    }
  }

  /**
   * We use a small segmenter (3 byte segments) and flush "ABCDEFGHIJ". We make sure that this
   * resolves to segments of "ABC"/"DEF"/"GHI"/"J".
   */
  @Test
  public void testFlushMultipleSegments() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                respond -> {},
                incrementalRequest(3, "DEF"),
                respond -> {},
                incrementalRequest(6, "GHI"),
                respond -> {},
                incrementalRequest(9, "J", true),
                respond -> respond.onNext(res1),
                finishMessage(10),
                respond -> respond.onNext(last)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      channel.write(ByteBuffer.wrap(content.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(10);
    }
  }

  /**
   * We use a small segmenter and flush "ABCDEFGHIJ", which will resolve to "ABC"/"DEF"/"GHI"/"J".
   * While flushing "GHI" we get a retryable error. We make sure that the retry loop handles
   * skipping the already-ack'd messages (i.e. "ABC" and "DEF") by using a map to count how many
   * times the fake server sees those messages, and throwing an error if it sees them more than
   * once.
   */
  @Test
  public void testFlushMultipleSegments_failsHalfway() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(3)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 = incrementalRequest(3, "DEF");
    BidiWriteObjectRequest req3 = incrementalRequest(6, "GHI");

    BidiWriteObjectRequest reconnect =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req4 = incrementalRequest(9, "J", true);
    BidiWriteObjectRequest req5 = finishMessage(10);

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();
    Map<BidiWriteObjectRequest, Integer> map = new ConcurrentHashMap<>();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                maxRetries(req1, null, map, 1),
                req2,
                maxRetries(req2, null, map, 1),
                req3,
                retryableErrorOnce(req3, null, map, 2),
                reconnect,
                maxRetries(reconnect, incrementalResponse(6), map, 2),
                req4,
                maxRetries(req4, incrementalResponse(10), map, 1),
                req5,
                maxRetries(req5, last, map, 1)),
            ImmutableMap.of(
                GetObjectRequest.newBuilder()
                    .setObject(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setReadMask(
                        (FieldMask.newBuilder()
                            .addPaths(Storage.BlobField.GENERATION.getGrpcName())
                            .build()))
                    .build(),
                Object.newBuilder().setGeneration(METADATA.getGeneration()).build()));

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      channel.write(ByteBuffer.wrap(content.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(10);

      assertThat(map.get(req1)).isEqualTo(1);
      assertThat(map.get(req2)).isEqualTo(1);
      assertThat(map.get(req3)).isEqualTo(2);
      assertThat(map.get(req4)).isEqualTo(1);
      assertThat(map.get(req5)).isEqualTo(1);
    }
  }

  /**
   * We use a small segmenter and flush "ABCDEFGHIJ", which will resolve to "ABC"/"DEF"/"GHI"/"J"
   * While flushing "GHI" we get a retryable error, and the response on the reconnect indicates that
   * there was a partial flush (i.e. only "G" got flushed). The retry loop handles skipping the "G"
   * and only sending "HI", and updating the offsets accordingly.
   */
  @Test
  public void testFlushMultipleSegments_failsHalfway_partialFlush() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(3)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 = incrementalRequest(3, "DEF");
    BidiWriteObjectRequest req3 = incrementalRequest(6, "GHI");

    BidiWriteObjectRequest reconnect =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req4 = incrementalRequest(7, "HI");

    BidiWriteObjectRequest req5 = incrementalRequest(9, "J", true);
    BidiWriteObjectRequest req6 = finishMessage(10);

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();
    Map<BidiWriteObjectRequest, Integer> map = new HashMap<>();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                maxRetries(req1, null, map, 1),
                req2,
                maxRetries(req2, null, map, 1),
                req3,
                retryableErrorOnce(req3, null, map, 1),
                reconnect,
                maxRetries(reconnect, incrementalResponse(7), map, 1),
                req4,
                maxRetries(req4, null, map, 1),
                req5,
                maxRetries(req5, incrementalResponse(10), map, 1),
                req6,
                maxRetries(req6, last, map, 1)),
            ImmutableMap.of(
                GetObjectRequest.newBuilder()
                    .setObject(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setReadMask(
                        (FieldMask.newBuilder()
                            .addPaths(Storage.BlobField.GENERATION.getGrpcName())
                            .build()))
                    .build(),
                Object.newBuilder().setGeneration(METADATA.getGeneration()).build()));

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      channel.write(ByteBuffer.wrap(content.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(10);

      assertThat(map.get(req1)).isEqualTo(1);
      assertThat(map.get(req2)).isEqualTo(1);
      assertThat(map.get(req3)).isEqualTo(1);
      assertThat(map.get(req4)).isEqualTo(1);
      assertThat(map.get(req5)).isEqualTo(1);
    }
  }

  /**
   * In this test, we use a small chunk segmenter that makes 3 byte segments, and do two flushes of
   * multiple segments (one with "ABC"/"DEF"/"GHI"/"J" and one with "KLM"/"NOP"/"QRS"/T"). The first
   * one flushes normally, but the second one gets a retryable error halfway through, and the result
   * of that retryable error indicates that a partial flush occurred. The retry loop handles
   * skipping the partially ack'd bytes. This test is just to assure that the {@code begin} variable
   * in the channel works properly
   */
  @Test
  public void testFlushMultipleSegmentsTwice_firstSucceeds_secondFailsHalfway_partialFlush()
      throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 = incrementalRequest(10, "KLM");
    BidiWriteObjectRequest req3 = incrementalRequest(13, "NOP");

    BidiWriteObjectRequest reconnect =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req4 = incrementalRequest(14, "OP");

    BidiWriteObjectRequest req5 = incrementalRequest(16, "QRS");
    BidiWriteObjectRequest req6 = incrementalRequest(19, "T", true);
    BidiWriteObjectRequest req7 = finishMessage(20);

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(20)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();
    Map<BidiWriteObjectRequest, Integer> map = new HashMap<>();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap
                .<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>>
                    builder()
                .putAll(
                    ImmutableMap.of(
                        req1,
                        respond -> {},
                        incrementalRequest(3, "DEF"),
                        respond -> {},
                        incrementalRequest(6, "GHI"),
                        respond -> {},
                        incrementalRequest(9, "J", true),
                        respond -> respond.onNext(res1),
                        req2,
                        maxRetries(req2, null, map, 1),
                        req3,
                        retryableErrorOnce(req3, null, map, 1),
                        reconnect,
                        maxRetries(reconnect, incrementalResponse(14), map, 1),
                        req4,
                        maxRetries(req4, null, map, 1),
                        req5,
                        maxRetries(req5, null, map, 1),
                        req6,
                        maxRetries(req6, incrementalResponse(20), map, 1)))
                .putAll(ImmutableMap.of(req7, maxRetries(req7, last, map, 1)))
                .build());

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content1 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      ChecksummedTestContent content2 = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 10);
      channel.write(ByteBuffer.wrap(content1.getBytes()));
      channel.write(ByteBuffer.wrap(content2.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(20);

      assertThat(map.get(reconnect)).isEqualTo(1);
      assertThat(map.get(req2)).isEqualTo(1);
      assertThat(map.get(req3)).isEqualTo(1);
      assertThat(map.get(req4)).isEqualTo(1);
      assertThat(map.get(req5)).isEqualTo(1);
      assertThat(map.get(req6)).isEqualTo(1);
      assertThat(map.get(req7)).isEqualTo(1);
    }
  }

  /**
   * If we get a 200 response with a partial success halfway through a flush of multiple segments,
   * the next segment after the partial success will hit a server-side error due to having a larger
   * write offset than the current persisted size. We retry this error and the retry loop handles
   * skipping the partially ack'd bytes
   */
  @Test
  public void testFlushMultipleSegments_200ResponsePartialFlushHalfway() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(8)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 = incrementalRequest(3, "DEF");
    BidiWriteObjectRequest req3 = incrementalRequest(6, "GHI");

    BidiWriteObjectRequest reconnect =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req4 = incrementalRequest(9, "J", true);

    BidiWriteObjectRequest req5 = incrementalRequest(8, "I");
    BidiWriteObjectRequest req6 = finishMessage(10);

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(10)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();
    Map<BidiWriteObjectRequest, Integer> map = new HashMap<>();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                maxRetries(req1, null, map, 1),
                req2,
                maxRetries(req2, null, map, 1),
                req3,
                maxRetries(req3, null, map, 1),
                req4,
                respond -> {
                  map.putIfAbsent(req4, 0);
                  int attempts = map.get(req4) + 1;
                  map.put(req4, attempts);
                  if (attempts == 1) {
                    respond.onNext(res1);
                  } else if (attempts == 2) {
                    respond.onNext(incrementalResponse(10));
                  }
                },
                reconnect,
                maxRetries(reconnect, incrementalResponse(8), map, 1),
                req5,
                maxRetries(req5, null, map, 1),
                req6,
                maxRetries(req6, last, map, 1)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 10);
      channel.write(ByteBuffer.wrap(content.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(10);

      assertThat(map.get(req1)).isEqualTo(1);
      assertThat(map.get(req2)).isEqualTo(1);
      assertThat(map.get(req3)).isEqualTo(1);
      assertThat(map.get(req4)).isEqualTo(2);
      assertThat(map.get(req5)).isEqualTo(1);
      assertThat(map.get(req6)).isEqualTo(1);
      assertThat(map.get(reconnect)).isEqualTo(1);
    }
  }

  /**
   * If the last message in a flush of multiple segments (or the only message in a flush with just
   * one segment) returns a 200 response but does a partial flush, we won't get a server side error
   * like in the previous test, because we won't try to do a write with a larger offset than the
   * persisted size. Instead, the channel keeps a manual count for this case, and throws an error if
   * it happens, which triggers a retry, and the retry loop handles flushing the last request again
   * while skipping the partially ack'd bytes
   */
  @Test
  public void testFlushMultipleSegments_200ResponsePartialFlushOnLastMessage() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();

    ChunkSegmenter smallSegmenter =
        new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), 3, 3);

    BidiWriteObjectRequest req1 =
        REQ_OPEN
            .toBuilder()
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("ABC")))
            .build();

    BidiWriteObjectResponse res1 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(7)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    BidiWriteObjectRequest req2 = incrementalRequest(3, "DEF");
    BidiWriteObjectRequest req3 = incrementalRequest(6, "GHI", true);

    BidiWriteObjectRequest reconnect =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req4 = incrementalRequest(7, "HI", true);

    BidiWriteObjectRequest req5 = finishMessage(9);

    BidiWriteObjectResponse last =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(9)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();
    Map<BidiWriteObjectRequest, Integer> map = new HashMap<>();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                maxRetries(req1, null, map, 1),
                req2,
                maxRetries(req2, null, map, 1),
                req3,
                maxRetries(req3, res1, map, 1),
                reconnect,
                maxRetries(reconnect, incrementalResponse(7), map, 1),
                req4,
                maxRetries(req4, incrementalResponse(9), map, 1),
                req5,
                maxRetries(req5, last, map, 1)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        GrpcStorageImpl storage =
            (GrpcStorageImpl) fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      StorageClient storageClient = storage.storageClient;
      BidiWriteCtx<BidiAppendableWrite> writeCtx =
          new BidiWriteCtx<>(
              new BidiAppendableWrite(
                  BidiWriteObjectRequest.newBuilder()
                      .setWriteObjectSpec(
                          WriteObjectSpec.newBuilder()
                              .setResource(
                                  Object.newBuilder()
                                      .setBucket(METADATA.getBucket())
                                      .setName(METADATA.getName()))
                              .setAppendable(true)
                              .build())
                      .build()));
      SettableApiFuture<BidiWriteObjectResponse> done = SettableApiFuture.create();

      GapicBidiUnbufferedAppendableWritableByteChannel channel =
          new GapicBidiUnbufferedAppendableWritableByteChannel(
              storageClient.bidiWriteObjectCallable(),
              storageClient.getObjectCallable(),
              TestUtils.retrierFromStorageOptions(fakeServer.getGrpcStorageOptions())
                  .withAlg(
                      fakeServer.getGrpcStorageOptions().getRetryAlgorithmManager().idempotent()),
              done,
              smallSegmenter,
              writeCtx,
              GrpcCallContext::createDefault);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 9);
      channel.write(ByteBuffer.wrap(content.getBytes()));
      channel.finalizeWrite();
      assertThat(done.get().getResource().getSize()).isEqualTo(9);

      assertThat(map.get(req1)).isEqualTo(1);
      assertThat(map.get(req2)).isEqualTo(1);
      assertThat(map.get(req3)).isEqualTo(1);
      assertThat(map.get(req4)).isEqualTo(1);
      assertThat(map.get(req5)).isEqualTo(1);
      assertThat(map.get(reconnect)).isEqualTo(1);
    }
  }

  @Test
  public void takeoverRedirectError() throws Exception {
    BidiWriteHandle writeHandle =
        BidiWriteHandle.newBuilder()
            .setHandle(ByteString.copyFromUtf8(UUID.randomUUID().toString()))
            .build();
    String routingToken = UUID.randomUUID().toString();

    BidiWriteObjectRequest req1 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setAppendObjectSpec(
                AppendObjectSpec.newBuilder()
                    .setBucket(METADATA.getBucket())
                    .setObject(METADATA.getName())
                    .setGeneration(METADATA.getGeneration())
                    .setWriteHandle(writeHandle)
                    .setRoutingToken(routingToken)
                    .build())
            .setFlush(true)
            .setStateLookup(true)
            .build();

    BidiWriteObjectRequest req3 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(10)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("KLMNO")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectRequest req4 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(15)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8("PQRST")).build())
            .setStateLookup(true)
            .setFlush(true)
            .build();

    BidiWriteObjectRequest req5 =
        BidiWriteObjectRequest.newBuilder().setWriteOffset(20).setFinishWrite(true).build();

    BidiWriteObjectResponse res2 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(10).build();

    BidiWriteObjectResponse res3 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(15).build();

    BidiWriteObjectResponse res4 =
        BidiWriteObjectResponse.newBuilder().setPersistedSize(20).build();

    BidiWriteObjectResponse res5 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(20)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .setWriteHandle(writeHandle)
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1,
                respond -> {
                  BidiWriteObjectRedirectedError redirect =
                      BidiWriteObjectRedirectedError.newBuilder()
                          .setWriteHandle(writeHandle)
                          .setRoutingToken(routingToken)
                          .setGeneration(METADATA.getGeneration())
                          .build();

                  com.google.rpc.Status grpcStatusDetails =
                      com.google.rpc.Status.newBuilder()
                          .setCode(Code.ABORTED_VALUE)
                          .setMessage("redirect")
                          .addDetails(Any.pack(redirect))
                          .build();

                  Metadata trailers = new Metadata();
                  trailers.put(GRPC_STATUS_DETAILS_KEY, grpcStatusDetails);
                  StatusRuntimeException statusRuntimeException =
                      Status.ABORTED.withDescription("redirect").asRuntimeException(trailers);
                  respond.onError(statusRuntimeException);
                },
                req2,
                respond -> respond.onNext(res2),
                req3,
                respond -> respond.onNext(res3),
                req4,
                respond -> respond.onNext(res4),
                req5,
                respond -> respond.onNext(res5)));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o", METADATA.getGeneration());
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 10, 10);
      try (AppendableUploadWriteableByteChannel channel = b.open()) {
        channel.write(ByteBuffer.wrap(content.getBytes()));
      }
      BlobInfo bi = b.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(20);
    }
  }

  /**
   * We get a retryable error in our first flush. We don't have a generation so we do a metadata
   * lookup, but we get an ObjectNotFound, which means that GCS never received the WriteObjectSpec
   * and never created the object. Thus, we just send the WriteObjectSpec again
   */
  @Test
  public void retryableError_ObjectNotFound() throws Exception {
    BidiWriteObjectRequest req1 = REQ_OPEN.toBuilder().setFlush(true).setStateLookup(true).build();

    Map<BidiWriteObjectRequest, Integer> map = new ConcurrentHashMap<>();
    BidiWriteObjectResponse res =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(5)
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();

    BidiWriteObjectRequest req2 = finishMessage(5);

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1, retryableErrorOnce(req1, res, map, 2), req2, maxRetries(req2, res, map, 1)),
            ImmutableMap.of(
                GetObjectRequest.newBuilder()
                    .setObject(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setReadMask(
                        (FieldMask.newBuilder()
                            .addPaths(Storage.BlobField.GENERATION.getGrpcName())
                            .build()))
                    .build(),
                Object.getDefaultInstance()));

    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {

      BlobId id = BlobId.of("b", "o");
      BlobAppendableUpload b =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), UPLOAD_CONFIG);
      ChecksummedTestContent content = ChecksummedTestContent.of(ALL_OBJECT_BYTES, 0, 5);
      try (AppendableUploadWriteableByteChannel channel = b.open()) {
        channel.write(ByteBuffer.wrap(content.getBytes()));
      }
      BlobInfo bi = b.getResult().get(5, TimeUnit.SECONDS);
      assertThat(bi.getSize()).isEqualTo(5);

      assertThat(map.get(req1)).isEqualTo(2);
      assertThat(map.get(req2)).isEqualTo(1);
    }
  }

  @Test
  public void crc32cWorks() throws Exception {
    byte[] b = new byte[25];
    DataGenerator.base64Characters().fill(b, 0, 20);
    DataGenerator.base64Characters().fill(b, 20, 5);
    ChecksummedTestContent abcde = ChecksummedTestContent.of(b, 0, 5);
    ChecksummedTestContent fghij = ChecksummedTestContent.of(b, 5, 5);
    ChecksummedTestContent klmno = ChecksummedTestContent.of(b, 10, 5);
    ChecksummedTestContent pqrst = ChecksummedTestContent.of(b, 15, 5);
    ChecksummedTestContent all = ChecksummedTestContent.of(b);

    BidiWriteObjectRequest req1 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(0)
            .setWriteObjectSpec(REQ_OPEN.getWriteObjectSpec())
            .setChecksummedData(abcde.asChecksummedData())
            .setFlush(true)
            .setStateLookup(true)
            .build();
    BidiWriteObjectResponse res1 = incrementalResponse(5);

    BidiWriteObjectRequest req2 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(5)
            .setChecksummedData(fghij.asChecksummedData())
            .setFlush(true)
            .setStateLookup(true)
            .build();
    BidiWriteObjectResponse res2 = incrementalResponse(10);
    BidiWriteObjectRequest req3 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(10)
            .setChecksummedData(klmno.asChecksummedData())
            .setFlush(true)
            .setStateLookup(true)
            .build();
    BidiWriteObjectResponse res3 = incrementalResponse(15);
    BidiWriteObjectRequest req4 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(15)
            .setChecksummedData(pqrst.asChecksummedData())
            .setFlush(true)
            .setStateLookup(true)
            .build();
    BidiWriteObjectResponse res4 = incrementalResponse(20);
    BidiWriteObjectRequest req5 =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(20)
            .setChecksummedData(abcde.asChecksummedData())
            .setFlush(true)
            .setStateLookup(true)
            .build();
    BidiWriteObjectResponse res5 = incrementalResponse(25);
    BidiWriteObjectRequest req6 =
        BidiWriteObjectRequest.newBuilder().setWriteOffset(25).setFinishWrite(true).build();
    BidiWriteObjectResponse res6 =
        BidiWriteObjectResponse.newBuilder()
            .setResource(
                Object.newBuilder()
                    .setName(METADATA.getName())
                    .setBucket(METADATA.getBucket())
                    .setGeneration(METADATA.getGeneration())
                    .setSize(25)
                    .setChecksums(ObjectChecksums.newBuilder().setCrc32C(all.getCrc32c()).build())
                    // real object would have some extra fields like metageneration and storage
                    // class
                    .build())
            .build();

    FakeStorage fake =
        FakeStorage.of(
            ImmutableMap.of(
                req1, respond -> respond.onNext(res1),
                req2, respond -> respond.onNext(res2),
                req3, respond -> respond.onNext(res3),
                req4, respond -> respond.onNext(res4),
                req5, respond -> respond.onNext(res5),
                req6, respond -> respond.onNext(res6)));
    try (FakeServer fakeServer = FakeServer.of(fake);
        Storage storage = fakeServer.getGrpcStorageOptions().toBuilder().build().getService()) {
      BlobId id = BlobId.of("b", "o");

      BlobAppendableUploadConfig uploadConfig = UPLOAD_CONFIG.withCrc32cValidationEnabled(true);
      BlobAppendableUpload upload =
          storage.blobAppendableUpload(BlobInfo.newBuilder(id).build(), uploadConfig);
      try (AppendableUploadWriteableByteChannel channel = upload.open()) {
        channel.write(ByteBuffer.wrap(b));
      }
      upload.getResult().get(5, TimeUnit.SECONDS);
    }
  }

  private Consumer<StreamObserver<BidiWriteObjectResponse>> maxRetries(
      BidiWriteObjectRequest req,
      BidiWriteObjectResponse res,
      Map<BidiWriteObjectRequest, Integer> retryMap,
      int maxAttempts) {
    return respond -> {
      retryMap.putIfAbsent(req, 0);
      int attempts = retryMap.get(req) + 1;
      retryMap.put(req, attempts);
      if (attempts > maxAttempts) {
        respond.onError(
            Status.ABORTED
                .withDescription("maxRetriesMethod exceed maxAttempts in fake")
                .asRuntimeException());
      } else {
        if (res != null) {
          respond.onNext(res);
        }
      }
    };
  }

  private Consumer<StreamObserver<BidiWriteObjectResponse>> retryableErrorOnce(
      BidiWriteObjectRequest req,
      BidiWriteObjectResponse res,
      Map<BidiWriteObjectRequest, Integer> retryMap,
      int maxAttempts) {
    return respond -> {
      retryMap.putIfAbsent(req, 0);
      int attempts = retryMap.get(req) + 1;
      retryMap.put(req, attempts);
      if (attempts == 1) {
        respond.onError(Status.INTERNAL.asRuntimeException());
      } else if (attempts > maxAttempts) {
        respond.onError(
            Status.ABORTED
                .withDescription("retryableErrorOnce method exceeded max retries in fake")
                .asRuntimeException());
      } else {
        if (res != null) {
          respond.onNext(res);
        }
      }
    };
  }

  private BidiWriteObjectRequest incrementalRequest(long offset, String content, boolean flush) {
    BidiWriteObjectRequest.Builder builder =
        BidiWriteObjectRequest.newBuilder()
            .setWriteOffset(offset)
            .setChecksummedData(
                ChecksummedData.newBuilder().setContent(ByteString.copyFromUtf8(content)));

    if (flush) {
      builder.setFlush(true).setStateLookup(true);
    }
    return builder.build();
  }

  private BidiWriteObjectRequest incrementalRequest(long offset, String content) {
    return incrementalRequest(offset, content, false);
  }

  private BidiWriteObjectResponse incrementalResponse(long perSize) {
    return BidiWriteObjectResponse.newBuilder().setPersistedSize(perSize).build();
  }

  private BidiWriteObjectRequest finishMessage(long offset) {
    return BidiWriteObjectRequest.newBuilder().setWriteOffset(offset).setFinishWrite(true).build();
  }

  static final class FakeStorage extends StorageGrpc.StorageImplBase {

    private final Map<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>> db;
    private final Map<GetObjectRequest, Object> getdb;

    private FakeStorage(
        Map<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>> db) {
      this(db, ImmutableMap.of());
    }

    private FakeStorage(
        Map<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>> db,
        Map<GetObjectRequest, Object> getdb) {
      this.db = db;
      this.getdb = getdb;
    }

    @Override
    public void getObject(GetObjectRequest request, StreamObserver<Object> responseObserver) {
      if (getdb.containsKey(request)) {
        Object resp = getdb.get(request);
        if (resp.getGeneration() == 0) {
          responseObserver.onError(TestUtils.apiException(Status.Code.NOT_FOUND, "not found"));
        } else {
          responseObserver.onNext(getdb.get(request));
          responseObserver.onCompleted();
        }
      } else {
        responseObserver.onError(
            TestUtils.apiException(Status.Code.UNIMPLEMENTED, "Unexpected request"));
      }
    }

    @Override
    public StreamObserver<BidiWriteObjectRequest> bidiWriteObject(
        StreamObserver<BidiWriteObjectResponse> respond) {
      return new AbstractObserver(respond) {
        @Override
        public void onNext(BidiWriteObjectRequest req) {
          if (db.containsKey(req)) {
            db.get(req).accept(respond);
          } else {
            respond.onError(
                TestUtils.apiException(Status.Code.UNIMPLEMENTED, "Unexpected request"));
          }
        }
      };
    }

    static FakeStorage of(
        Map<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>> db) {
      return new FakeStorage(db);
    }

    static FakeStorage of(
        Map<BidiWriteObjectRequest, Consumer<StreamObserver<BidiWriteObjectResponse>>> db,
        Map<GetObjectRequest, Object> getdb) {
      return new FakeStorage(db, getdb);
    }

    static FakeStorage from(Map<BidiWriteObjectRequest, BidiWriteObjectResponse> db) {
      return new FakeStorage(Maps.transformValues(db, resp -> (respond) -> respond.onNext(resp)));
    }
  }

  abstract static class AbstractObserver implements StreamObserver<BidiWriteObjectRequest> {

    protected final StreamObserver<BidiWriteObjectResponse> respond;

    private AbstractObserver(StreamObserver<BidiWriteObjectResponse> respond) {
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
