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

package com.google.cloud.storage;

import static com.google.cloud.storage.TestUtils.apiException;
import static com.google.cloud.storage.TestUtils.getChecksummedData;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.retrying.BasicResultRetryAlgorithm;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.WriteCtx.SimpleWriteObjectRequestBuilderFactory;
import com.google.cloud.storage.WriteCtx.WriteObjectRequestBuilderFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import com.google.storage.v2.WriteObjectSpec;
import io.grpc.Status.Code;
import io.grpc.stub.CallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Test;

public final class ITGapicUnbufferedWritableByteChannelTest {
  private static final Logger LOGGER =
      Logger.getLogger(ITGapicUnbufferedWritableByteChannelTest.class.getName());

  private static final Hasher HASHER = Hasher.enabled();
  private static final ChunkSegmenter segmenter =
      new ChunkSegmenter(HASHER, ByteStringStrategy.copy(), 10, 5);

  private static final String uploadId = "upload-id";

  private static final Object obj = Object.newBuilder().setBucket("buck").setName("obj").build();
  private static final WriteObjectSpec spec = WriteObjectSpec.newBuilder().setResource(obj).build();

  private static final StartResumableWriteRequest startReq =
      StartResumableWriteRequest.newBuilder().setWriteObjectSpec(spec).build();
  private static final StartResumableWriteResponse startResp =
      StartResumableWriteResponse.newBuilder().setUploadId(uploadId).build();

  private static final byte[] bytes = DataGenerator.base64Characters().genBytes(40);
  private static final WriteObjectRequest req1 =
      WriteObjectRequest.newBuilder()
          .setUploadId(uploadId)
          .setChecksummedData(getChecksummedData(ByteString.copyFrom(bytes, 0, 10), HASHER))
          .build();
  private static final WriteObjectRequest req2 =
      WriteObjectRequest.newBuilder()
          .setUploadId(uploadId)
          .setWriteOffset(10)
          .setChecksummedData(getChecksummedData(ByteString.copyFrom(bytes, 10, 10), HASHER))
          .build();
  private static final WriteObjectRequest req3 =
      WriteObjectRequest.newBuilder()
          .setUploadId(uploadId)
          .setWriteOffset(20)
          .setChecksummedData(getChecksummedData(ByteString.copyFrom(bytes, 20, 10), HASHER))
          .build();
  private static final WriteObjectRequest req4 =
      WriteObjectRequest.newBuilder()
          .setUploadId(uploadId)
          .setWriteOffset(30)
          .setChecksummedData(getChecksummedData(ByteString.copyFrom(bytes, 30, 10), HASHER))
          .build();
  private static final WriteObjectRequest req5 =
      WriteObjectRequest.newBuilder()
          .setUploadId(uploadId)
          .setWriteOffset(40)
          .setFinishWrite(true)
          .setObjectChecksums(
              ObjectChecksums.newBuilder()
                  .setCrc32C(HASHER.hash(ByteBuffer.wrap(bytes)).getValue())
                  .build())
          .build();

  private static final WriteObjectResponse resp1 =
      WriteObjectResponse.newBuilder().setPersistedSize(10).build();
  private static final WriteObjectResponse resp2 =
      WriteObjectResponse.newBuilder().setPersistedSize(20).build();
  private static final WriteObjectResponse resp3 =
      WriteObjectResponse.newBuilder().setPersistedSize(30).build();
  private static final WriteObjectResponse resp4 =
      WriteObjectResponse.newBuilder().setPersistedSize(40).build();
  private static final WriteObjectResponse resp5 =
      WriteObjectResponse.newBuilder().setResource(obj.toBuilder().setSize(40)).build();

  private static final ResumableWrite reqFactory =
      new ResumableWrite(startReq, startResp, TestUtils.onlyUploadId());

  @Test
  public void directUpload() throws IOException, InterruptedException, ExecutionException {

    byte[] bytes = DataGenerator.base64Characters().genBytes(40);
    WriteObjectRequest req1 =
        ITGapicUnbufferedWritableByteChannelTest.req1
            .toBuilder()
            .clearUploadId()
            .setWriteObjectSpec(spec)
            .build();
    WriteObjectRequest req2 =
        ITGapicUnbufferedWritableByteChannelTest.req2.toBuilder().clearUploadId().build();
    WriteObjectRequest req3 =
        ITGapicUnbufferedWritableByteChannelTest.req3.toBuilder().clearUploadId().build();
    WriteObjectRequest req4 =
        ITGapicUnbufferedWritableByteChannelTest.req4.toBuilder().clearUploadId().build();
    WriteObjectRequest req5 =
        ITGapicUnbufferedWritableByteChannelTest.req5.toBuilder().clearUploadId().build();

    WriteObjectResponse resp = resp5;

    WriteObjectRequest base = WriteObjectRequest.newBuilder().setWriteObjectSpec(spec).build();
    SimpleWriteObjectRequestBuilderFactory reqFactory =
        WriteObjectRequestBuilderFactory.simple(base);

    StorageImplBase service =
        new DirectWriteService(
            ImmutableMap.of(ImmutableList.of(req1, req2, req3, req4, req5), resp));
    try (FakeServer fake = FakeServer.of(service);
        StorageClient sc =
            PackagePrivateMethodWorkarounds.maybeGetStorageClient(
                fake.getGrpcStorageOptions().getService())) {
      assertThat(sc).isNotNull();
      SettableApiFuture<WriteObjectResponse> result = SettableApiFuture.create();
      try (GapicUnbufferedDirectWritableByteChannel c =
          new GapicUnbufferedDirectWritableByteChannel(
              result, segmenter, sc.writeObjectCallable(), new WriteCtx<>(reqFactory))) {
        c.write(ByteBuffer.wrap(bytes));
      }
      assertThat(result.get()).isEqualTo(resp);
    }
  }

  @Test
  public void resumableUpload() throws IOException, InterruptedException, ExecutionException {
    ImmutableMap<List<WriteObjectRequest>, WriteObjectResponse> writes =
        ImmutableMap.<List<WriteObjectRequest>, WriteObjectResponse>builder()
            .put(ImmutableList.of(req1), resp1)
            .put(ImmutableList.of(req2), resp2)
            .put(ImmutableList.of(req3), resp3)
            .put(ImmutableList.of(req4), resp4)
            .put(ImmutableList.of(req5), resp5)
            .build();
    StorageImplBase service = new DirectWriteService(writes);
    try (FakeServer fake = FakeServer.of(service);
        StorageClient sc = StorageClient.create(fake.storageSettings())) {
      SettableApiFuture<WriteObjectResponse> result = SettableApiFuture.create();
      GapicUnbufferedChunkedResumableWritableByteChannel c =
          new GapicUnbufferedChunkedResumableWritableByteChannel(
              result,
              segmenter,
              sc.writeObjectCallable(),
              new WriteCtx<>(reqFactory),
              RetryingDependencies.attemptOnce(),
              Retrying.neverRetry(),
              Retrying::newCallContext);
      ArrayList<String> debugMessages = new ArrayList<>();
      try {
        ImmutableList<ByteBuffer> buffers = TestUtils.subDivide(bytes, 10);
        for (ByteBuffer buf : buffers) {
          debugMessages.add(String.format("Writing buffer. buf = %s", buf));
          int written = c.write(buf);
          debugMessages.add(String.format("Wrote bytes. written = %2d", written));
        }
        // explicitly only close on success so we can trap the original error that maybe have
        // happened before we reach here.
        // Realistically, calling close here isn't strictly necessary because once we leave the
        // try block for FakeServer the server will shut down.
        c.close();
      } catch (PermissionDeniedException ignore) {
        for (String debugMessage : debugMessages) {
          LOGGER.warning(debugMessage);
        }
      }
      assertThat(result.get()).isEqualTo(resp5);
    }
  }

  @Test
  public void resumableUpload_chunkAutomaticRetry()
      throws IOException, InterruptedException, ExecutionException {
    AtomicBoolean req2SendErr = new AtomicBoolean(true);
    AtomicBoolean req4SendErr = new AtomicBoolean(true);
    AtomicInteger writeCount = new AtomicInteger(0);
    StorageImplBase service =
        new DirectWriteService(
            (obs, requests) -> {
              writeCount.getAndIncrement();
              if (requests.equals(ImmutableList.of(req1))) {
                obs.onNext(resp1);
                obs.onCompleted();
              } else if (requests.equals(ImmutableList.of(req2))) {
                obs.onNext(resp2);
                if (req2SendErr.get()) {
                  req2SendErr.set(false);
                  obs.onError(apiException(Code.DATA_LOSS));
                } else {
                  obs.onCompleted();
                }
              } else if (requests.equals(ImmutableList.of(req3))) {
                obs.onNext(resp3);
                obs.onCompleted();
              } else if (requests.equals(ImmutableList.of(req4))) {
                obs.onNext(resp4);
                if (req4SendErr.get()) {
                  req4SendErr.set(false);
                  obs.onError(apiException(Code.DATA_LOSS));
                } else {
                  obs.onCompleted();
                }
              } else if (requests.equals(ImmutableList.of(req5))) {
                obs.onNext(resp5);
                obs.onCompleted();
              } else {
                DirectWriteService.logUnexpectedRequest(
                    ImmutableSet.of(
                        ImmutableList.of(req1),
                        ImmutableList.of(req2),
                        ImmutableList.of(req3),
                        ImmutableList.of(req4),
                        ImmutableList.of(req5)),
                    requests);
                obs.onError(
                    TestUtils.apiException(Code.PERMISSION_DENIED, "Unexpected request chain."));
              }
            });
    WriteCtx<?> writeCtx;
    try (FakeServer fake = FakeServer.of(service);
        StorageClient sc = StorageClient.create(fake.storageSettings())) {
      SettableApiFuture<WriteObjectResponse> result = SettableApiFuture.create();
      try (GapicUnbufferedChunkedResumableWritableByteChannel c =
          new GapicUnbufferedChunkedResumableWritableByteChannel(
              result,
              segmenter,
              sc.writeObjectCallable(),
              new WriteCtx<>(reqFactory),
              TestUtils.defaultRetryingDeps(),
              new BasicResultRetryAlgorithm<Object>() {
                @Override
                public boolean shouldRetry(Throwable t, Object ignore) {
                  return TestUtils.findThrowable(DataLossException.class, t) != null;
                }
              },
              Retrying::newCallContext)) {
        writeCtx = c.getWriteCtx();
        ImmutableList<ByteBuffer> buffers = TestUtils.subDivide(bytes, 10);
        c.write(buffers.get(0));
        assertThat(writeCtx.getTotalSentBytes().get()).isEqualTo(10);
        assertThat(writeCtx.getConfirmedBytes().get()).isEqualTo(10);
        c.write(buffers.get(1));
        assertThat(writeCtx.getTotalSentBytes().get()).isEqualTo(20);
        assertThat(writeCtx.getConfirmedBytes().get()).isEqualTo(20);
        c.write(buffers.get(2));
        assertThat(writeCtx.getTotalSentBytes().get()).isEqualTo(30);
        assertThat(writeCtx.getConfirmedBytes().get()).isEqualTo(30);
        c.write(buffers.get(3));
        assertThat(writeCtx.getTotalSentBytes().get()).isEqualTo(40);
        assertThat(writeCtx.getConfirmedBytes().get()).isEqualTo(40);
      }
      assertThat(result.get()).isEqualTo(resp5);
    }
    assertThat(req2SendErr.get()).isFalse();
    assertThat(req4SendErr.get()).isFalse();
    assertThat(writeCount.get()).isEqualTo(7);

    assertThat(writeCtx.getTotalSentBytes().get()).isEqualTo(40);
    assertThat(writeCtx.getConfirmedBytes().get()).isEqualTo(40);
  }

  @Test
  public void resumableUpload_finalizeWhenWriteAndCloseCalledEvenWhenQuantumAligned()
      throws IOException, InterruptedException, ExecutionException {
    ImmutableMap<List<WriteObjectRequest>, WriteObjectResponse> writes =
        ImmutableMap.<List<WriteObjectRequest>, WriteObjectResponse>builder()
            .put(
                ImmutableList.of(
                    req1,
                    req2.toBuilder().clearUploadId().build(),
                    req3.toBuilder().clearUploadId().build(),
                    req4.toBuilder().clearUploadId().build(),
                    req5.toBuilder().clearUploadId().build()),
                resp5)
            .build();
    StorageImplBase service = new DirectWriteService(writes);
    try (FakeServer fake = FakeServer.of(service);
        StorageClient sc = StorageClient.create(fake.storageSettings())) {
      SettableApiFuture<WriteObjectResponse> result = SettableApiFuture.create();
      GapicUnbufferedChunkedResumableWritableByteChannel c =
          new GapicUnbufferedChunkedResumableWritableByteChannel(
              result,
              segmenter,
              sc.writeObjectCallable(),
              new WriteCtx<>(reqFactory),
              RetryingDependencies.attemptOnce(),
              Retrying.neverRetry(),
              Retrying::newCallContext);
      try {
        int written = c.writeAndClose(ByteBuffer.wrap(bytes));
        assertThat(written).isEqualTo(bytes.length);
      } catch (PermissionDeniedException ignore) {
      }
      assertThat(result.get()).isEqualTo(resp5);
    }
  }

  static class DirectWriteService extends StorageImplBase {
    private static final Logger LOGGER = Logger.getLogger(DirectWriteService.class.getName());
    private final BiConsumer<StreamObserver<WriteObjectResponse>, List<WriteObjectRequest>> c;

    private ImmutableList.Builder<WriteObjectRequest> requests;

    DirectWriteService(
        BiConsumer<StreamObserver<WriteObjectResponse>, List<WriteObjectRequest>> c) {
      this.c = c;
      this.requests = new ImmutableList.Builder<>();
    }

    DirectWriteService(ImmutableMap<List<WriteObjectRequest>, WriteObjectResponse> writes) {
      this(
          (obs, build) -> {
            if (writes.containsKey(build)) {
              obs.onNext(writes.get(build));
              obs.onCompleted();
            } else {
              logUnexpectedRequest(writes.keySet(), build);
              obs.onError(
                  TestUtils.apiException(Code.PERMISSION_DENIED, "Unexpected request chain."));
            }
          });
    }

    private static void logUnexpectedRequest(
        Set<List<WriteObjectRequest>> writes, List<WriteObjectRequest> build) {
      Collector<CharSequence, ?, String> joining = Collectors.joining(",\n\t", "[\n\t", "\n]");
      Collector<CharSequence, ?, String> oneLine = Collectors.joining(",", "[", "]");
      String msg =
          String.format(
              "Unexpected Request Chain.%nexpected one of: %s%n        but was: %s",
              writes.stream()
                  .map(l -> l.stream().map(StorageV2ProtoUtils::fmtProto).collect(oneLine))
                  .collect(joining),
              build.stream().map(StorageV2ProtoUtils::fmtProto).collect(oneLine));
      LOGGER.warning(msg);
    }

    @Override
    public StreamObserver<WriteObjectRequest> writeObject(StreamObserver<WriteObjectResponse> obs) {
      return new Adapter() {
        @Override
        public void onNext(WriteObjectRequest value) {
          requests.add(value);
        }

        @Override
        public void onError(Throwable t) {}

        @Override
        public void onCompleted() {
          ImmutableList<WriteObjectRequest> build = requests.build();
          c.accept(obs, build);
          requests = new ImmutableList.Builder<>();
        }
      };
    }
  }

  private abstract static class Adapter extends CallStreamObserver<WriteObjectRequest> {

    private Adapter() {}

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setOnReadyHandler(Runnable onReadyHandler) {}

    @Override
    public void disableAutoInboundFlowControl() {}

    @Override
    public void request(int count) {}

    @Override
    public void setMessageCompression(boolean enable) {}
  }
}
