/*
 * Copyright 2023 Google LLC
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
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.TimedAttemptSettings;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.api.gax.rpc.UnavailableException;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.SyncAndUploadUnbufferedWritableByteChannel.Alg;
import com.google.cloud.storage.SyncAndUploadUnbufferedWritableByteChannel.RequestStream;
import com.google.cloud.storage.SyncAndUploadUnbufferedWritableByteChannel.ResponseStream;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import com.google.storage.v2.Object;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.RandomDistribution;
import net.jqwik.api.Tuple;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import net.jqwik.api.lifecycle.AfterContainer;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.api.lifecycle.BeforeContainer;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.api.lifecycle.BeforeTry;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ITSyncAndUploadUnbufferedWritableByteChannelPropertyTest {

  private static Path tmpFolder;
  private static RecoveryFileManager recoveryFileManager;
  private FailureInducingStorageImpl failureInducingStorage;
  private FakeServer server;
  private GrpcStorageImpl storage;

  @BeforeContainer
  static void beforeContainer() throws IOException {
    tmpFolder =
        Files.createTempDirectory(
            ITSyncAndUploadUnbufferedWritableByteChannelPropertyTest.class.getSimpleName());
    recoveryFileManager = RecoveryFileManager.of(ImmutableList.of(tmpFolder));
  }

  @AfterContainer
  static void afterContainer() throws IOException {
    if (tmpFolder != null) {
      Files.walkFileTree(
          tmpFolder,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.deleteIfExists(file);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.deleteIfExists(dir);
              return FileVisitResult.CONTINUE;
            }
          });
    }
  }

  @BeforeProperty
  void beforeProperty() throws IOException {
    failureInducingStorage = new FailureInducingStorageImpl();
    server = FakeServer.of(failureInducingStorage);
    storage = (GrpcStorageImpl) server.getGrpcStorageOptions().getService();
  }

  @AfterProperty
  void afterProperty() throws Exception {
    // use try-with-resources to do the close dance
    try (AutoCloseable ignore1 = server;
        AutoCloseable ignore2 = storage) {
      storage = null;
      server = null;
    }
  }

  @BeforeTry
  void beforeTry() {
    failureInducingStorage.reset();
  }

  @Example
  void emptyObject() throws Exception {
    Scenario scenario = Scenario.of("empty", 0, 0, 256, 256, FailuresQueue.empty());
    testUploads(scenario);
  }

  @Example
  void requestStream_halfClosedToUnavailable_positive() {
    UnavailableException unavailableException =
        assertThrows(
            UnavailableException.class,
            () ->
                RequestStream.halfClosedToUnavailable(
                    () -> {
                      throw new IllegalStateException("asdf half-closed fdsa");
                    }));
    assertThat(unavailableException)
        .hasCauseThat()
        .hasMessageThat()
        .isEqualTo("asdf half-closed fdsa");
  }

  @Example
  void requestStream_halfClosedToUnavailable_negative() {
    IllegalStateException illegalStateException =
        assertThrows(
            IllegalStateException.class,
            () ->
                RequestStream.halfClosedToUnavailable(
                    () -> {
                      throw new IllegalStateException("blah");
                    }));
    assertThat(illegalStateException).hasMessageThat().isEqualTo("blah");
  }

  @Example
  void alg_shouldSetResultFutureIfNotRetryable() {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();
    Alg alg =
        new Alg((ResultRetryAlgorithmAdapter) (prevThrowable, prevResponse) -> false, resultFuture);

    ForcedFailure ff = new ForcedFailure("should not be retried");
    boolean shouldRetry = alg.shouldRetry(ff, null);
    assertThat(shouldRetry).isFalse();
    assertThat(resultFuture.isDone()).isTrue();
    ExecutionException runtimeException = assertThrows(ExecutionException.class, resultFuture::get);
    assertThat(runtimeException).hasCauseThat().hasMessageThat().isEqualTo("should not be retried");
  }

  @Example
  void alg_shouldNotSetResultFutureIfRetryable() {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();
    Alg alg =
        new Alg((ResultRetryAlgorithmAdapter) (prevThrowable, prevResponse) -> true, resultFuture);

    ForcedFailure ff = new ForcedFailure("can be retried");
    boolean shouldRetry = alg.shouldRetry(ff, null);
    assertThat(shouldRetry).isTrue();
    assertThat(resultFuture.isDone()).isFalse();
  }

  @Example
  void responseStream_onComplete_lastMessageWithResourceMustResolveResultFuture()
      throws ExecutionException, InterruptedException {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();

    ResponseStream responseStream = new ResponseStream(resultFuture);

    Object fake = Object.newBuilder().setName("fake").build();
    WriteObjectResponse response = WriteObjectResponse.newBuilder().setResource(fake).build();
    responseStream.onNext(response);

    assertThat(resultFuture.isDone()).isFalse();
    responseStream.onCompleted();
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isEqualTo(response);
  }

  @Example
  void responseStream_onComplete_lastMessageWithoutResourceDoesNotResolveResultFuture() {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();

    ResponseStream responseStream = new ResponseStream(resultFuture);

    WriteObjectResponse response = WriteObjectResponse.newBuilder().setPersistedSize(3).build();
    responseStream.onNext(response);

    assertThat(resultFuture.isDone()).isFalse();
    responseStream.onCompleted();
    assertThat(resultFuture.isDone()).isFalse();
  }

  @Example
  void responseStream_await_yields_onComplete() throws ExecutionException, InterruptedException {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();

    ResponseStream responseStream = new ResponseStream(resultFuture);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    try {
      Future<String> submit =
          executorService.submit(
              () -> {
                responseStream.await();
                return "Success";
              });

      Object fake = Object.newBuilder().setName("fake").build();
      WriteObjectResponse response = WriteObjectResponse.newBuilder().setResource(fake).build();
      responseStream.onNext(response);
      responseStream.onCompleted();

      assertThat(submit.get()).isEqualTo("Success");
    } finally {
      executorService.shutdownNow();
    }
  }

  @Example
  void responseStream_await_yields_onError() throws ExecutionException, InterruptedException {
    SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();

    ResponseStream responseStream = new ResponseStream(resultFuture);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    try {
      Future<String> submit =
          executorService.submit(
              () -> {
                try {
                  responseStream.await();
                } catch (ForcedFailure ff) {
                  return "Success";
                }
                return "Fail";
              });

      RuntimeException re = new ForcedFailure("error");
      responseStream.onError(re);

      assertThat(submit.get()).isEqualTo("Success");
    } finally {
      executorService.shutdownNow();
    }
  }

  //  25 tries leads to ~0m:30s of runtime
  // 250 tries leads to ~6m:00s of runtime
  @Property(tries = 25)
  void testUploads(@ForAll("scenario") Scenario s) throws Exception {

    StorageClient storageClient = storage.storageClient;
    BlobInfo info = BlobInfo.newBuilder("buck", s.objectName).build();
    try (RecoveryFile rf = s.recoveryFileManager.newRecoveryFile(info)) {
      SettableApiFuture<WriteObjectResponse> resultFuture = SettableApiFuture.create();

      ApiFuture<ResumableWrite> f =
          storage.startResumableWrite(
              GrpcCallContext.createDefault(),
              storage.getWriteObjectRequest(info, Opts.empty()),
              Opts.empty());
      ResumableWrite resumableWrite = ApiExceptions.callAndTranslateApiException(f);

      UploadCtx uploadCtx =
          failureInducingStorage.data.get(UploadId.of(resumableWrite.getRes().getUploadId()));

      uploadCtx.failuresQueue = s.failuresQueue;

      SyncAndUploadUnbufferedWritableByteChannel syncAndUpload =
          new SyncAndUploadUnbufferedWritableByteChannel(
              storageClient.writeObjectCallable(),
              storageClient.queryWriteStatusCallable(),
              resultFuture,
              s.chunkSegmenter,
              TestUtils.defaultRetryingDeps(),
              StorageRetryStrategy.getDefaultStorageRetryStrategy().getIdempotentHandler(),
              new WriteCtx<>(resumableWrite),
              rf,
              s.copyBuffer);
      try (BufferedWritableByteChannel w = s.buffered(syncAndUpload)) {
        for (ByteString dataFrame : s.dataFrames) {
          w.write(dataFrame.asReadOnlyByteBuffer());
        }
      }

      WriteObjectResponse response = resultFuture.get(1, TimeUnit.SECONDS);
      assertThat(response.hasResource()).isTrue();
      Object resource = response.getResource();

      ByteString actual =
          uploadCtx.parts.stream()
              .filter(WriteObjectRequest::hasChecksummedData)
              .map(wor -> wor.getChecksummedData().getContent())
              .reduce(ByteString.empty(), ByteString::concat);
      ByteString expected = s.dataFrames.stream().reduce(ByteString.empty(), ByteString::concat);
      assertAll(
          () -> assertThat(uploadCtx.getLength()).isEqualTo(s.objectSize),
          () -> assertThat(resource.getSize()).isEqualTo(s.objectSize),
          () -> assertThat(xxd(actual)).isEqualTo(xxd(expected)));
    }
  }

  static List<ByteString> dataFrames(long length, int segmentLength) {
    // todo: rethink this
    Random rand = new Random(length);
    ArrayList<ByteString> segments = new ArrayList<>();

    int i = 0;
    for (; i < length; i += segmentLength) {
      long remaining = length - i;
      int size = Math.toIntExact(Math.min(remaining, segmentLength));
      byte[] bytes = DataGenerator.rand(rand).genBytes(size);
      if (size > 4) {
        byte[] byteArray = Ints.toByteArray(i);
        ByteString offset = ByteString.copyFrom(byteArray);
        ByteString concat = offset.concat(ByteString.copyFrom(bytes, 4, bytes.length - 4));
        segments.add(concat);
      } else {
        segments.add(ByteString.copyFrom(bytes));
      }
    }

    return ImmutableList.copyOf(segments);
  }

  @Provide("scenario")
  static Arbitrary<Scenario> scenarioArbitrary() {
    // 1. choose an alignment quantum
    return alignmentQuantumArbitrary()
        .flatMap(
            quantum ->
                Combinators.combine(
                        Arbitraries.just(quantum),
                        // 2. choose a segment size between 1 and 8 times the quantum
                        ints().between(1, 8).map(mult -> quantum * mult))
                    .as(Tuple::of))
        .flatMap(
            t -> {
              int segmentSize = t.get2();
              return Combinators.combine(
                      Arbitraries.just(t.get1()),
                      Arbitraries.just(segmentSize),
                      // 3. choose an object size between 0 and 32 time segment size
                      //   this helps keep the maximum number of rights relatively low and
                      //   proportional with the size of the object
                      ints().between(0, 32 * segmentSize))
                  .as(Tuple::of);
            })
        .flatMap(
            t -> {
              int quantum = t.get1();
              int objectSize = t.get3();
              // if the object isn't 0 bytes, set our min write size to be 1
              int minWriteSize = Math.min(1, objectSize);

              // determine how many quantum will make up the full object
              // we want to align failures to quantum boundaries like GCS does
              int quantumCount = objectSize / quantum;
              return Combinators.combine(
                      Arbitraries.just(quantum),
                      Arbitraries.just(t.get2()),
                      Arbitraries.just(objectSize),
                      ints().between(minWriteSize, objectSize),
                      // 4. generate between 0 and 3 failure offsets
                      ints()
                          .between(0, quantumCount)
                          .map(i -> FailureOffset.of((long) i * quantum))
                          .list()
                          .ofMinSize(0)
                          .ofMaxSize(3)
                          .map(FailuresQueue::new))
                  .as(Tuple::of);
            })
        .map(
            t -> {
              // 5. Construct our scenario from the generated values
              int quantum = t.get1();
              int segmentSize = t.get2();
              int objectSize = t.get3();
              int writeSize = t.get4();
              return Scenario.of(
                  String.format("object-%d", t.hashCode()),
                  objectSize,
                  writeSize,
                  segmentSize,
                  quantum,
                  t.get5());
            })
        // The way we're defining things there aren't critical edge cases. Let jqwik know, so it
        // can be smarter about generation, evaluation and shrinking
        .withoutEdgeCases();
  }

  static Arbitrary<Integer> alignmentQuantumArbitrary() {
    // 16..256KiB
    return ints().between(4, 18).map(i -> Math.toIntExact((long) Math.pow(2, i)));
  }

  @NonNull
  private static IntegerArbitrary ints() {
    return Arbitraries.integers().withDistribution(RandomDistribution.uniform());
  }

  private static String fmt(int i) {
    return String.format("% 10d (0x%08x)", i, i);
  }

  private static final class Scenario {
    private final String toString;
    private final String objectName;
    private final long objectSize;
    private final ChunkSegmenter chunkSegmenter;
    private final BufferHandle bufferHandle;
    private final BufferHandle copyBuffer;
    private final FailuresQueue failuresQueue;
    private final RecoveryFileManager recoveryFileManager;
    private final List<ByteString> dataFrames;

    private Scenario(
        String toString,
        String objectName,
        long objectSize,
        ChunkSegmenter chunkSegmenter,
        BufferHandle bufferHandle,
        BufferHandle copyBuffer,
        FailuresQueue failuresQueue,
        RecoveryFileManager recoveryFileManager,
        List<ByteString> dataFrames) {
      this.toString = toString;
      this.objectName = objectName;
      this.objectSize = objectSize;
      this.chunkSegmenter = chunkSegmenter;
      this.bufferHandle = bufferHandle;
      this.copyBuffer = copyBuffer;
      this.failuresQueue = failuresQueue;
      this.recoveryFileManager = recoveryFileManager;
      this.dataFrames = dataFrames;
    }

    BufferedWritableByteChannel buffered(UnbufferedWritableByteChannel c) {
      return StorageByteChannels.writable()
          .createSynchronized(new DefaultBufferedWritableByteChannel(bufferHandle, c));
    }

    public static Scenario of(
        String objectName,
        long objectSize,
        int writeSize,
        int segmentSize,
        int quantum,
        FailuresQueue failuresQueue) {

      List<FailureOffset> nonQuantumAligned =
          failuresQueue.statuses.stream()
              .filter(f -> f.getOffset() % quantum != 0)
              .collect(Collectors.toList());
      assertWithMessage("Failure offsets not quantum aligned (quantum=%s)", fmt(quantum))
          .that(nonQuantumAligned)
          .isEmpty();
      List<ByteString> dataFrames = dataFrames(objectSize, writeSize);
      return new Scenario(
          MoreObjects.toStringHelper(Scenario.class)
              .add("\n  objectName", objectName)
              .add("\n  objectSize", objectSize)
              .add("\n  writeSize", writeSize)
              .add("\n  segmentSize", segmentSize)
              .add("\n  quantum", quantum)
              .add("\n  dataFrames.size()", dataFrames.size())
              .add("\n  failuresQueue", failuresQueue)
              .addValue("\n")
              .toString(),
          objectName,
          objectSize,
          new ChunkSegmenter(Hasher.noop(), ByteStringStrategy.copy(), segmentSize, quantum),
          BufferHandle.allocate(segmentSize),
          BufferHandle.allocate(segmentSize),
          failuresQueue,
          ITSyncAndUploadUnbufferedWritableByteChannelPropertyTest.recoveryFileManager,
          dataFrames);
    }

    @Override
    public String toString() {
      return toString;
    }
  }

  private static final class UploadId {
    private final String id;

    private UploadId(String id) {
      this.id = id;
    }

    private static UploadId of(String id) {
      return new UploadId(id);
    }

    @Override
    public boolean equals(java.lang.Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof UploadId)) {
        return false;
      }
      UploadId uploadId = (UploadId) o;
      return Objects.equals(id, uploadId.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).toString();
    }
  }

  private static final class UploadCtx {
    private final UploadId id;
    private final StartResumableWriteRequest req;

    private final List<WriteObjectRequest> parts;

    private FailuresQueue failuresQueue;
    private long length;

    private UploadCtx(UploadId id, StartResumableWriteRequest req) {
      this.id = id;
      this.req = req;
      this.parts = Collections.synchronizedList(new ArrayList<>());
    }

    public static UploadCtx of(UploadId id, StartResumableWriteRequest req) {
      return new UploadCtx(id, req);
    }

    UploadId getId() {
      return id;
    }

    StartResumableWriteRequest getReq() {
      return req;
    }

    void addPart(WriteObjectRequest req) {
      length += req.getChecksummedData().getContent().size();
      parts.add(req);
    }

    long getLength() {
      return length;
    }

    boolean finishWrite() {
      if (!parts.isEmpty()) {
        return parts.get(parts.size() - 1).getFinishWrite();
      } else {
        return false;
      }
    }

    public Code consume(WriteObjectRequest req) {
      if (failuresQueue != null) {
        FailureOffset peek = failuresQueue.pending.peekFirst();
        if (peek != null) {
          if (req.hasChecksummedData()) {
            long writeOffset = req.getWriteOffset();
            ByteString content = req.getChecksummedData().getContent();
            int size = content.size();
            boolean applies = writeOffset <= peek.offset && peek.offset < writeOffset + size;
            if (applies) {
              int subLength = Math.toIntExact(Math.subtractExact(peek.offset, writeOffset));
              ByteString substring = content.substring(0, subLength);
              WriteObjectRequest.Builder b = req.toBuilder();
              b.getChecksummedDataBuilder().setContent(substring);
              b.clearFinishWrite();
              failuresQueue.pending.pop();
              length += substring.size();
              parts.add(b.build());
              return peek.getStatus();
            }
          }
        }
      }
      addPart(req);
      return Code.OK;
    }
  }

  private static final class FailuresQueue {

    private final List<FailureOffset> statuses;

    private final Deque<FailureOffset> pending;

    private FailuresQueue(List<FailureOffset> statuses) {
      this.statuses = ImmutableList.sortedCopyOf(FailureOffset.COMP, statuses);
      ArrayDeque<FailureOffset> tmp = new ArrayDeque<>();
      this.statuses.forEach(tmp::addLast);
      this.pending = tmp;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("statuses", statuses).toString();
    }

    static FailuresQueue empty() {
      return new FailuresQueue(ImmutableList.of());
    }
  }

  private static final class FailureOffset implements Comparable<FailureOffset> {
    private static final Comparator<FailureOffset> COMP =
        Comparator.comparing(FailureOffset::getOffset);
    private final long offset;
    private final Status.Code status;

    private FailureOffset(long offset, Code status) {
      this.offset = offset;
      this.status = status;
    }

    public long getOffset() {
      return offset;
    }

    public Code getStatus() {
      return status;
    }

    @Override
    public int compareTo(FailureOffset o) {
      return COMP.compare(this, o);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("offset", offset)
          .add("status", status)
          .toString();
    }

    private static FailureOffset of(long offset) {
      return new FailureOffset(offset, Code.INTERNAL);
    }
  }

  private static final class FailureInducingStorageImpl extends StorageImplBase {

    private final Map<UploadId, UploadCtx> data;

    public FailureInducingStorageImpl() {
      this.data = Collections.synchronizedMap(new HashMap<>());
    }

    void reset() {
      data.clear();
    }

    @Override
    public void startResumableWrite(
        StartResumableWriteRequest request,
        StreamObserver<StartResumableWriteResponse> responseObserver) {
      UploadId id = UploadId.of(UUID.randomUUID().toString());
      data.put(id, UploadCtx.of(id, request));
      StartResumableWriteResponse startResumableWriteResponse =
          StartResumableWriteResponse.newBuilder().setUploadId(id.id).build();
      responseObserver.onNext(startResumableWriteResponse);
      responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<WriteObjectRequest> writeObject(
        StreamObserver<WriteObjectResponse> responseObserver) {
      return new FailureInducingWriteObjectRequestObserver(responseObserver, data);
    }

    @Override
    public void queryWriteStatus(
        QueryWriteStatusRequest queryWriteStatusRequest,
        StreamObserver<QueryWriteStatusResponse> responseObserver) {
      UploadId uploadId = UploadId.of(queryWriteStatusRequest.getUploadId());
      UploadCtx ctx;
      if (data.containsKey(uploadId)) {
        ctx = data.get(uploadId);
      } else {
        responseObserver.onError(Code.NOT_FOUND.toStatus().asRuntimeException());
        return;
      }
      QueryWriteStatusResponse.Builder b = QueryWriteStatusResponse.newBuilder();
      if (ctx.finishWrite()) {
        b.setResource(
            ctx.getReq()
                .getWriteObjectSpec()
                .getResource()
                .toBuilder()
                .setSize(ctx.getLength())
                .setGeneration(1)
                .setMetageneration(1)
                .build());
      } else {
        b.setPersistedSize(ctx.getLength());
      }
      QueryWriteStatusResponse queryWriteStatusResponse = b.build();
      responseObserver.onNext(queryWriteStatusResponse);
      responseObserver.onCompleted();
    }
  }

  private static final class FailureInducingWriteObjectRequestObserver
      implements StreamObserver<WriteObjectRequest> {
    private final StreamObserver<WriteObjectResponse> responseObserver;
    private final Map<UploadId, UploadCtx> data;

    private UploadCtx ctx;
    private boolean errored;

    public FailureInducingWriteObjectRequestObserver(
        StreamObserver<WriteObjectResponse> responseObserver, Map<UploadId, UploadCtx> data) {
      this.data = data;
      this.responseObserver = responseObserver;
      this.ctx = null;
      this.errored = false;
    }

    @Override
    public void onNext(WriteObjectRequest writeObjectRequest) {
      if (ctx == null) {
        UploadId uploadId = UploadId.of(writeObjectRequest.getUploadId());
        if (data.containsKey(uploadId)) {
          ctx = data.get(uploadId);
        } else {
          errored = true;
          responseObserver.onError(Code.NOT_FOUND.toStatus().asRuntimeException());
          return;
        }
      }
      Status.Code ret = ctx.consume(writeObjectRequest);
      if (ret != Code.OK) {
        errored = true;
        responseObserver.onError(ret.toStatus().asRuntimeException());
      }
    }

    @Override
    public void onError(Throwable throwable) {
      if (errored) {
        return;
      }
      responseObserver.onError(throwable);
    }

    @Override
    public void onCompleted() {
      if (errored) {
        return;
      }
      WriteObjectResponse resp =
          WriteObjectResponse.newBuilder()
              .setResource(
                  ctx.getReq()
                      .getWriteObjectSpec()
                      .getResource()
                      .toBuilder()
                      .setSize(ctx.getLength())
                      .setGeneration(1)
                      .setMetageneration(1)
                      .build())
              .build();
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    }
  }

  @FunctionalInterface
  private interface ResultRetryAlgorithmAdapter extends ResultRetryAlgorithm<java.lang.Object> {

    @Override
    default TimedAttemptSettings createNextAttempt(
        Throwable prevThrowable, java.lang.Object prevResponse, TimedAttemptSettings prevSettings) {
      return null;
    }
  }

  private static final class ForcedFailure extends RuntimeException {
    public ForcedFailure(String message) {
      super(message);
    }
  }
}
