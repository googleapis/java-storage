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

import static com.google.cloud.storage.BidiUploadState.appendableNew;
import static com.google.cloud.storage.BidiUploadTestUtils.finishAt;
import static com.google.cloud.storage.BidiUploadTestUtils.timestampNow;
import static com.google.cloud.storage.StorageV2ProtoUtils.fmtProto;
import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.lang.String.format;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.ClientStream;
import com.google.cloud.storage.BidiUploadState.AppendableUploadState;
import com.google.cloud.storage.BidiUploadState.State;
import com.google.cloud.storage.ChunkSegmenter.ChunkSegment;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import com.google.storage.v2.AppendObjectSpec;
import com.google.storage.v2.BidiWriteHandle;
import com.google.storage.v2.BidiWriteObjectRedirectedError;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.WriteObjectSpec;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
@SuppressWarnings({"unused", "UnnecessaryLocalVariable", "SameParameterValue"})
public final class BidiUploadTest {
  static final Collector<CharSequence, ?, String> joiner =
      // Collectors.joining(",\n  ", "[\n  ", "\n]");
      joiner(1);

  private static Collector<CharSequence, ?, String> joiner(int indentation) {
    String i0 = "  ";
    String i_1 = IntStream.range(0, indentation - 1).mapToObj(x -> i0).reduce("", String::concat);
    String i = IntStream.range(0, indentation).mapToObj(x -> i0).reduce("", String::concat);

    return Collectors.joining(",\n" + i, "[\n" + i, "\n" + i_1 + "]");
  }

  public static final ChecksummedTestContent content =
      ChecksummedTestContent.of(DataGenerator.base64Characters().genBytes(64));

  static final BidiWriteObjectRequest appendRequestNew =
      BidiWriteObjectRequest.newBuilder()
          .setWriteObjectSpec(
              WriteObjectSpec.newBuilder()
                  .setResource(
                      Object.newBuilder().setBucket("projects/_/buckets/b").setName("o").build())
                  .setAppendable(true)
                  .build())
          .build();
  static final BidiWriteObjectRequest appendRequestTakeover =
      BidiWriteObjectRequest.newBuilder()
          .setAppendObjectSpec(
              AppendObjectSpec.newBuilder()
                  .setBucket("projects/_/buckets/b")
                  .setObject("o")
                  .setGeneration(1)
                  .build())
          .build();

  static final BidiWriteObjectRequest uploadId =
      BidiWriteObjectRequest.newBuilder().setUploadId("uploadId").build();
  static final BidiWriteObjectRequest writeObjectSpec =
      BidiWriteObjectRequest.newBuilder()
          .setWriteObjectSpec(WriteObjectSpec.newBuilder().setIfGenerationMatch(0).build())
          .build();
  static final BidiWriteObjectRequest appendableObjectSpec =
      BidiWriteObjectRequest.newBuilder()
          .setAppendObjectSpec(AppendObjectSpec.newBuilder().setObject("obj").build())
          .build();

  static final BidiWriteObjectRequest onlyBytes_00 =
      BidiWriteObjectRequest.newBuilder()
          .setChecksummedData(content.slice(0, 10).asChecksummedData())
          .setWriteOffset(0)
          .build();
  static final BidiWriteObjectRequest onlyBytes_10 =
      BidiWriteObjectRequest.newBuilder()
          .setChecksummedData(content.slice(10, 10).asChecksummedData())
          .setWriteOffset(10)
          .build();
  static final BidiWriteObjectRequest onlyBytes_20 =
      BidiWriteObjectRequest.newBuilder()
          .setChecksummedData(content.slice(20, 10).asChecksummedData())
          .setWriteOffset(20)
          .build();
  static final BidiWriteObjectRequest onlyBytes_30 =
      BidiWriteObjectRequest.newBuilder()
          .setChecksummedData(content.slice(30, 10).asChecksummedData())
          .setWriteOffset(30)
          .build();

  static final BidiWriteObjectRequest onlyFlush =
      BidiWriteObjectRequest.newBuilder().setFlush(true).build();
  static final BidiWriteObjectRequest onlyFinishWrite =
      BidiWriteObjectRequest.newBuilder().setFinishWrite(true).build();

  @SuppressWarnings("ClassEscapesDefinedScope")
  @RunWith(Parameterized.class)
  public static final class BidiUploadStateCommonTest {

    private static final Function<BidiWriteObjectRequest, String> fmt =
        message -> message == null ? "null" : TextFormat.printer().shortDebugString(message);

    private final BidiUploadStateFactory factory;

    public BidiUploadStateCommonTest(BidiUploadStateFactory factory) {
      this.factory = factory;
    }

    @Parameters(name = "{0}")
    public static ImmutableList<BidiUploadStateFactory> factories() {
      return ImmutableList.of(new AppendableNewFactory(), new AppendableTakeoverFactory());
    }

    @Test
    public void offer() {
      BidiUploadState state = factory.createInitialized();
      assertThat(state.offer(uploadId)).isTrue();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.offer(onlyBytes_20)).isFalse();

      assertThat(state.peekFirst()).isSameInstanceAs(uploadId);
      assertThat(state.peekLast()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);
    }

    @Test
    public void setConfirmedBytesOffset_oneFullMessage() {
      BidiUploadState state = factory.createInitialized();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);

      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(10));

      assertThat(state.peekFirst()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.peekLast()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(10);
    }

    @Test
    public void setConfirmedBytesOffset_anyFirstMessageEvicted() {
      BidiUploadState state = factory.createInitialized();
      assertThat(state.offer(uploadId)).isTrue();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);
      assertThat(state.peekFirst()).isSameInstanceAs(uploadId);

      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(10));

      assertThat(state.peekFirst()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.peekLast()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(10);
    }

    @Test
    public void setConfirmedBytesOffset_onlyFullMessagesAreEvicted() {
      BidiUploadState state = factory.createInitialized();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);

      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(11));

      assertThat(state.peekFirst()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.peekLast()).isSameInstanceAs(onlyBytes_10);
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(10);
    }

    @Test
    public void setConfirmedBytesOffset_allMessagesAreEvicted() {
      BidiUploadState state = factory.createInitialized();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);

      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(20));

      assertThat(state.peekFirst()).isNull();
      assertThat(state.peekLast()).isNull();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      assertThat(state.getConfirmedBytes()).isEqualTo(20);
    }

    @Test
    public void multipleOfferAckCycles() {
      BidiUploadState state = factory.createInitialized();

      assertThat(state.getConfirmedBytes()).isEqualTo(0);

      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(10);
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(10));
      assertThat(state.getConfirmedBytes()).isEqualTo(10);

      assertThat(state.offer(onlyBytes_10)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(20);
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(20));
      assertThat(state.getConfirmedBytes()).isEqualTo(20);

      assertThat(state.offer(onlyBytes_20)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(30);
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(30));
      assertThat(state.getConfirmedBytes()).isEqualTo(30);

      assertThat(state.peekFirst()).isNull();
      assertThat(state.peekLast()).isNull();
    }

    @Test
    public void ackOfferLessThanSent() {
      BidiUploadState state = factory.createInitialized();

      assertThat(state.offer(uploadId)).isTrue();
      assertThat(state.offer(onlyBytes_00)).isTrue();
      assertThat(state.getTotalSentBytes()).isEqualTo(10);
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(0));
      assertThat(state.getTotalSentBytes()).isEqualTo(10);
      assertThat(state.getConfirmedBytes()).isEqualTo(0);

      assertThat(state.peekFirst()).isSameInstanceAs(onlyBytes_00);
      assertThat(state.peekLast()).isSameInstanceAs(onlyBytes_00);
    }

    @Test
    public void offerWithBytesRejectedIfNoAvailableCapacity() {
      BidiUploadState state = factory.createInitialized(4);

      assertThat(state.availableCapacity()).isEqualTo(4);
      assertThat(
              state.offer(
                  BidiUploadTestUtils.createSegment(content.slice(0, 5).asChecksummedData())))
          .isFalse();
    }

    @Test
    public void initialRequestMessage_shouldNotMergeIntoDataRequest() {
      BidiUploadState state = factory.createInitialized(17);

      assertThat(state.offer(BidiUploadTestUtils.createSegment(onlyBytes_00.getChecksummedData())))
          .isTrue();

      assertThat(state.peekFirst()).isEqualTo(onlyBytes_00);
    }

    @Test
    public void redirectToken_appendable_previousSuccessfulFlush() throws Exception {
      BidiWriteObjectRequest req = appendRequestNew;
      GrpcCallContext baseContext =
          GrpcCallContext.createDefault()
              .withExtraHeaders(ImmutableMap.of("something", ImmutableList.of("or", "other")));
      BidiUploadState state = factory.createInitialized(() -> baseContext, 17);

      state.updateStateFromResponse(BidiUploadTestUtils.incremental(10));
      state.pendingRetry();
      state.updateFromRedirect(
          BidiWriteObjectRedirectedError.newBuilder()
              .setGeneration(1)
              .setRoutingToken("routing-token")
              .setWriteHandle(
                  BidiWriteHandle.newBuilder().setHandle(ByteString.copyFromUtf8("handle")).build())
              .build());
      state.retrying();

      GrpcCallContext actualCtx = state.enqueueFirstMessageAndGetGrpcCallContext();
      List<BidiWriteObjectRequest> requests = BidiUploadTestUtils.sinkToList(state);

      BidiWriteObjectRequest expectedRequest =
          appendRequestNew.toBuilder()
              .setAppendObjectSpec(
                  AppendObjectSpec.newBuilder()
                      .setBucket("projects/_/buckets/b")
                      .setObject("o")
                      .setGeneration(1)
                      .setWriteHandle(
                          BidiWriteHandle.newBuilder()
                              .setHandle(ByteString.copyFromUtf8("handle"))
                              .build())
                      .setRoutingToken("routing-token")
                      .build())
              .setStateLookup(true)
              .build();
      ImmutableMap<String, ImmutableList<String>> expectedHeaders =
          ImmutableMap.of(
              "something",
              ImmutableList.of("or", "other"),
              "x-goog-request-params",
              ImmutableList.of(
                  "bucket=projects/_/buckets/b&appendable=true&routing_token=routing-token"));
      assertAll(
          () -> assertThat(requests).isEqualTo(ImmutableList.of(expectedRequest)),
          () -> assertThat(actualCtx).isNotEqualTo(baseContext),
          () -> assertThat(actualCtx.getExtraHeaders()).isEqualTo(expectedHeaders));
    }

    @Test
    public void sendVia_onlySendsFirstMessageWhenRetrying() {
      BidiUploadState state = factory.create(20);
      assertThat(state.enqueueFirstMessageAndGetGrpcCallContext()).isNotNull();
      List<BidiWriteObjectRequest> requests1 = BidiUploadTestUtils.sinkToList(state);
      assertThat(requests1).hasSize(1);
      assertThat(state.onResponse(resourceWithSize(0))).isNull();
      assertThat(state.getState()).isEqualTo(State.RUNNING);

      ChecksummedTestContent.gen(20).chunkup(5).stream()
          .map(ChecksummedTestContent::asChecksummedData)
          .map(BidiUploadTestUtils::createSegment)
          .forEach(s -> assertThat(state.offer(s)).isTrue());

      List<BidiWriteObjectRequest> requests2 = BidiUploadTestUtils.sinkToList(state);
      assertThat(requests2).hasSize(4);
      assertThat(requests2).containsNoneIn(requests1);

      assertThat(state.onResponse(BidiUploadTestUtils.incremental(4))).isNull();

      state.pendingRetry();
      state.retrying();
      assertThat(state.enqueueFirstMessageAndGetGrpcCallContext()).isNotNull();

      List<BidiWriteObjectRequest> requests3 = BidiUploadTestUtils.sinkToList(state);

      assertThat(requests3)
          .isEqualTo(
              ImmutableList.of(
                  BidiWriteObjectRequest.newBuilder()
                      .setStateLookup(true)
                      .setAppendObjectSpec(
                          AppendObjectSpec.newBuilder()
                              .setBucket("projects/_/buckets/b")
                              .setObject("o")
                              .setGeneration(1)
                              .build())
                      .build()));
    }

    @Test
    public void redirectToken_appendable_noPreviousSuccessfulFlush() throws Exception {
      GrpcCallContext baseContext =
          GrpcCallContext.createDefault()
              .withExtraHeaders(ImmutableMap.of("something", ImmutableList.of("or", "other")));
      BidiUploadState state = factory.create(() -> baseContext, 17);

      GrpcCallContext actualCtx = state.enqueueFirstMessageAndGetGrpcCallContext();
      List<BidiWriteObjectRequest> requests = BidiUploadTestUtils.sinkToList(state);

      ImmutableMap<String, ImmutableList<String>> expectedHeaders =
          ImmutableMap.of(
              "something",
              ImmutableList.of("or", "other"),
              "x-goog-request-params",
              ImmutableList.of("bucket=projects/_/buckets/b&appendable=true"));
      assertAll(
          () -> {
            // because we're running parameterized, we don't know which initial request is
            // specifically needed for this assertion. But we do know all the valid request chains.
            // Enumerate them here and validate that one of them is matched.
            List<List<BidiWriteObjectRequest>> all =
                ImmutableList.of(
                    ImmutableList.of(appendRequestNew), ImmutableList.of(appendRequestTakeover));
            boolean contains = all.contains(requests);
            String msg =
                format(
                    "Requests does not match a valid list of expected requests.%n"
                        + "expected: %s"
                        + "%n"
                        + "%n"
                        + "but was: %s",
                    all.stream()
                        .map(l -> l.stream().map(StorageV2ProtoUtils::fmtProto).collect(joiner(2)))
                        .collect(joiner),
                    requests.stream().map(StorageV2ProtoUtils::fmtProto).collect(joiner));
            assertWithMessage(msg).that(contains).isTrue();
          },
          () -> assertThat(actualCtx).isNotEqualTo(baseContext),
          () -> assertThat(actualCtx.getExtraHeaders()).isEqualTo(expectedHeaders));
    }

    private abstract static class BidiUploadStateFactory {
      final BidiUploadState createInitialized() {
        return createInitialized(25);
      }

      final BidiUploadState createInitialized(long maxBytes) {
        return createInitialized(
            GrpcCallContext::createDefault,
            maxBytes,
            SettableApiFuture.create(),
            Crc32cValue.zero());
      }

      final BidiUploadState createInitialized(
          Supplier<GrpcCallContext> grpcContextSupplier, long maxBytes) {
        return createInitialized(
            grpcContextSupplier, maxBytes, SettableApiFuture.create(), Crc32cValue.zero());
      }

      final BidiUploadState create() {
        return create(25);
      }

      final BidiUploadState create(long maxBytes) {
        return create(
            GrpcCallContext::createDefault,
            maxBytes,
            SettableApiFuture.create(),
            Crc32cValue.zero());
      }

      final BidiUploadState create(Supplier<GrpcCallContext> grpcContextSupplier, long maxBytes) {
        return create(
            grpcContextSupplier, maxBytes, SettableApiFuture.create(), Crc32cValue.zero());
      }

      abstract BidiUploadState createInitialized(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c);

      abstract BidiUploadState create(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c);

      @Override
      public final String toString() {
        return this.getClass().getSimpleName();
      }
    }

    private static final class AppendableNewFactory extends BidiUploadStateFactory {
      @Override
      BidiUploadState createInitialized(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c) {
        BidiUploadState state = create(baseCallContext, maxBytes, resultFuture, initialCrc32c);
        state.enqueueFirstMessageAndGetGrpcCallContext();
        state.updateStateFromResponse(BidiUploadTestUtils.incremental(0));
        return state;
      }

      @Override
      BidiUploadState create(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c) {
        return appendableNew(
            appendRequestNew, baseCallContext, maxBytes, resultFuture, initialCrc32c);
      }
    }

    private static final class AppendableTakeoverFactory extends BidiUploadStateFactory {
      @Override
      BidiUploadState createInitialized(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c) {
        BidiUploadState state = create(baseCallContext, maxBytes, resultFuture, initialCrc32c);
        state.awaitTakeoverStateReconciliation(
            () -> {
              state.retrying();
              assertThat(state.onResponse(BidiUploadTest.resourceFor(appendRequestTakeover, 0)))
                  .isNull();
            });
        return state;
      }

      @Override
      BidiUploadState create(
          Supplier<GrpcCallContext> baseCallContext,
          long maxBytes,
          SettableApiFuture<BidiWriteObjectResponse> resultFuture,
          @Nullable Crc32cLengthKnown initialCrc32c) {
        return BidiUploadState.appendableTakeover(
            appendRequestTakeover, baseCallContext, maxBytes, resultFuture, initialCrc32c);
      }
    }
  }

  public static final class BidiUploadStateConcatenateTest {

    @Test
    public void concatenate_bothChecksummedData_resultsInIllegalArgument() {
      IllegalArgumentException iae =
          assertThrows(
              IllegalArgumentException.class,
              () -> BidiUploadState.concatenate(onlyBytes_00, onlyBytes_10));
    }

    @Test
    public void concatenate_writeOffsetLesserWhenBothSpecified() {
      BidiWriteObjectRequest expected =
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(10)
              .setFinishWrite(true)
              .setChecksummedData(onlyBytes_10.getChecksummedData())
              .build();
      BidiWriteObjectRequest finish_20 = onlyFinishWrite.toBuilder().setWriteOffset(20).build();
      BidiWriteObjectRequest concatenated = BidiUploadState.concatenate(onlyBytes_10, finish_20);
      assertThat(concatenated).isEqualTo(expected);
    }

    @Test
    public void concatenate_left0_right10_shouldBe0() {
      BidiWriteObjectRequest expected =
          BidiWriteObjectRequest.newBuilder()
              .setFinishWrite(true)
              .setChecksummedData(onlyBytes_00.getChecksummedData())
              .build();
      BidiWriteObjectRequest finish_10 = onlyFinishWrite.toBuilder().setWriteOffset(10).build();
      BidiWriteObjectRequest concatenated = BidiUploadState.concatenate(onlyBytes_00, finish_10);
      assertThat(concatenated).isEqualTo(expected);
    }

    @Test
    public void concatenate_leftFirst_rightFinish10_shouldHaveWriteOffset10() {
      BidiWriteObjectRequest expected =
          BidiWriteObjectRequest.newBuilder()
              .setWriteObjectSpec(appendRequestNew.getWriteObjectSpec())
              .setWriteOffset(10)
              .setFinishWrite(true)
              .build();
      BidiWriteObjectRequest finish_10 = onlyFinishWrite.toBuilder().setWriteOffset(10).build();
      BidiWriteObjectRequest concatenated =
          BidiUploadState.concatenate(appendRequestNew, finish_10);
      assertThat(concatenated).isEqualTo(expected);
    }

    @Test
    public void concatenate_leftFirstWith00_rightFinish10_shouldHaveWriteOffset0() {
      BidiWriteObjectRequest expected =
          BidiWriteObjectRequest.newBuilder()
              .setWriteObjectSpec(appendRequestNew.getWriteObjectSpec())
              .setChecksummedData(onlyBytes_00.getChecksummedData())
              .setWriteOffset(0)
              .setFinishWrite(true)
              .build();
      BidiWriteObjectRequest first_00 =
          appendRequestNew.toBuilder().mergeFrom(onlyBytes_00).build();
      BidiWriteObjectRequest finish_10 = onlyFinishWrite.toBuilder().setWriteOffset(10).build();
      BidiWriteObjectRequest concatenated = BidiUploadState.concatenate(first_00, finish_10);
      assertThat(concatenated).isEqualTo(expected);
    }

    @Test
    public void concatenate_left00_rightFinish10_shouldHaveWriteOffset0() {
      BidiWriteObjectRequest expected =
          BidiWriteObjectRequest.newBuilder()
              .setChecksummedData(onlyBytes_00.getChecksummedData())
              .setWriteOffset(0)
              .setFinishWrite(true)
              .build();
      BidiWriteObjectRequest first_00 = onlyBytes_00;
      BidiWriteObjectRequest finish_10 = finishAt(10);
      BidiWriteObjectRequest concatenated = BidiUploadState.concatenate(first_00, finish_10);
      assertThat(concatenated).isEqualTo(expected);
    }

    @Test
    public void concatenate_left10_rightFinish21_shouldThrowIllegalArgumentException() {
      IllegalArgumentException iae =
          assertThrows(
              IllegalArgumentException.class,
              () -> BidiUploadState.concatenate(onlyBytes_10, finishAt(21)));
    }
  }

  public static final class AppendableUploadStateTest {
    private static @NonNull AppendableUploadState getAppendable() {
      AppendableUploadState uploadState =
          appendableNew(
              appendRequestNew,
              GrpcCallContext::createDefault,
              50_000,
              SettableApiFuture.create(),
              Crc32cValue.zero());
      uploadState.enqueueFirstMessageAndGetGrpcCallContext();
      BidiUploadTestUtils.sinkToList(uploadState);
      assertThat(uploadState.onResponse(resourceWithSize(0))).isNull();
      ChecksummedTestContent.gen(10).chunkup(1).stream()
          .map(ChecksummedTestContent::asChecksummedData)
          .map(BidiUploadTestUtils::createSegment)
          .forEach(c -> assertThat(uploadState.offer(c)).isTrue());
      return uploadState;
    }

    private static void runExpectNoException(
        AppendableUploadState state, @NonNull BidiWriteObjectResponse response) {
      StorageException se = state.onResponse(response);
      assertThat(se).isNull();
    }

    private static void runExpectException(
        AppendableUploadState state,
        BidiWriteObjectResponse response,
        UploadFailureScenario scenario) {
      StorageException se = state.onResponse(response);
      assertThat(se).isNotNull();
      assertThat(se).hasMessageThat().contains(scenario.getMessage());
    }

    @Test
    public void onResponse_responseWithoutPersistedSizeAndWithoutResource_IllegalStateException() {
      AppendableUploadState state = getAppendable();

      IllegalStateException ise =
          assertThrows(
              IllegalStateException.class,
              () -> {
                BidiWriteObjectResponse response = BidiWriteObjectResponse.getDefaultInstance();
                StorageException se = state.onResponse(response);
                if (se != null) {
                  throw se;
                }
              });

      assertThat(ise).hasMessageThat().contains("persistedSize > -1");
    }

    @Test
    public void onResponse_writeHandleUpdated() {
      BidiWriteHandle handle =
          BidiWriteHandle.newBuilder().setHandle(ByteString.copyFromUtf8("new-handle")).build();
      AppendableUploadState state = getAppendable();
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(
          state, BidiUploadTestUtils.incremental(10).toBuilder().setWriteHandle(handle).build());
      assertThat(state.writeHandle).isEqualTo(handle);
    }

    @Test
    public void onResponse_notFinalizing_incremental_ackEq() {
      AppendableUploadState state = getAppendable();
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(state, BidiUploadTestUtils.incremental(10));
      assertThat(state.confirmedBytes).isEqualTo(10);
    }

    @Test
    public void onResponse_notFinalizing_incremental_ackLt() {
      AppendableUploadState state = getAppendable();
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(state, BidiUploadTestUtils.incremental(9));
      assertThat(state.confirmedBytes).isEqualTo(9);
    }

    @Test
    public void onResponse_notFinalizing_incremental_ackGt() {
      AppendableUploadState state = getAppendable();
      BidiUploadTestUtils.sinkToList(state);
      runExpectException(
          state, BidiUploadTestUtils.incremental(11), UploadFailureScenario.SCENARIO_7);
    }

    @Test
    public void onResponse_notFinalizing_notIncremental_ackEq() {
      AppendableUploadState state = getAppendable();
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(state, resourceWithSize(10));
    }

    @Test
    public void onResponse_finalizing_notIncremental_ackLt_inRangeOfBufferedBytes() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      // runExpectException(state, resourceWithSize(9), UploadFailureScenario.SCENARIO_9);
      runExpectNoException(state, resourceWithSize(9));
      assertThat(state.confirmedBytes).isEqualTo(9);
    }

    @Test
    public void onResponse_finalizing_notIncremental_ackLt_outOfRangeOfBufferedBytes() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(3));
      runExpectException(state, resourceWithSize(2), UploadFailureScenario.SCENARIO_4_1);
    }

    @Test
    public void onResponse_finalizing_notIncremental_ackGt() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      runExpectException(state, resourceWithSize(11), UploadFailureScenario.SCENARIO_4_2);
    }

    @Test
    public void onResponse_finalizing_incremental_ackLt_inRangeOfBufferedBytes() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      // runExpectException(state, incremental(9), UploadFailureScenario.SCENARIO_9);
      runExpectNoException(state, BidiUploadTestUtils.incremental(9));
      assertThat(state.confirmedBytes).isEqualTo(9);
    }

    @Test
    public void onResponse_finalizing_incremental_ackLt_outOfRangeOfBufferedBytes() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      state.updateStateFromResponse(BidiUploadTestUtils.incremental(9));
      runExpectException(
          state, BidiUploadTestUtils.incremental(8), UploadFailureScenario.SCENARIO_3);
    }

    @Test
    public void onResponse_finalizing_incremental_ackEq() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(state, BidiUploadTestUtils.incremental(10));
      assertThat(state.confirmedBytes).isEqualTo(10);
    }

    @Test
    public void onResponse_finalizing_incremental_ackGt() {
      // BidiUploadState state = new TestState(Flag.FINALIZING);
      AppendableUploadState state = getAppendable();
      // updateBasedOnResponseRunExpectFailure(state, UploadFailureScenario.SCENARIO_2,
      // incremental(11));
    }

    @Test
    public void onResponse_finalizing_notIncremental_ackEq() {
      AppendableUploadState state = getAppendable();
      state.offer(
          BidiWriteObjectRequest.newBuilder()
              .setWriteOffset(state.totalSentBytes)
              .setFinishWrite(true)
              .build());
      BidiUploadTestUtils.sinkToList(state);
      runExpectNoException(state, resourceWithSize(10));
      assertThat(state.confirmedBytes).isEqualTo(10);
    }

    @Test
    public void complexSequence_1() throws Exception {
      AppendableUploadState state =
          appendableNew(
              appendRequestNew,
              GrpcCallContext::createDefault,
              50_000,
              SettableApiFuture.create(),
              Crc32cValue.zero());
      GrpcCallContext actualCtx = state.enqueueFirstMessageAndGetGrpcCallContext();
      assertThat(actualCtx).isNotNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(0),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(-1),
          () -> assertThat(state.confirmedBytes).isEqualTo(-1),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(Crc32cValue.zero()));

      ChecksummedTestContent b_10 = ChecksummedTestContent.gen(10);
      Crc32cLengthKnown cumulative0 = Crc32cValue.of(b_10.getCrc32c(), b_10.length());
      b_10.chunkup(1).stream()
          .map(ChecksummedTestContent::asChecksummedData)
          .map(BidiUploadTestUtils::createSegment)
          .forEach(c -> assertThat(state.offer(c)).isTrue());
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(10),
          () -> assertThat(state.lastSentRequestIndex).isIn(range(-1, state.queue.size())),
          () -> assertThat(state.confirmedBytes).isEqualTo(-1),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative0));

      BidiUploadTestUtils.sinkToList(state);
      assertThat(state.onResponse(resourceWithSize(1))).isNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(10),
          () -> assertThat(state.lastSentRequestIndex).isIn(range(-1, state.queue.size())),
          () -> assertThat(state.confirmedBytes).isEqualTo(1),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative0));

      BidiUploadTestUtils.sinkToList(state);
      assertThat(state.onResponse(BidiUploadTestUtils.incremental(10))).isNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(10),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(-1),
          () -> assertThat(state.confirmedBytes).isEqualTo(10),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative0));

      ChunkSegment segment1 = BidiUploadTestUtils.createSegment(1);
      Crc32cLengthKnown cumulative1 = cumulative0.concat(segment1.getCrc32c());
      assertThat(state.offer(segment1)).isTrue();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(11),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(-1),
          () -> assertThat(state.confirmedBytes).isEqualTo(10),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative1));
      BidiUploadTestUtils.sinkToList(state);
      assertThat(state.lastSentRequestIndex).isEqualTo(0);

      ChunkSegment segment2 = BidiUploadTestUtils.createSegment(2);
      Crc32cLengthKnown cumulative2 = cumulative1.concat(segment2.getCrc32c());
      assertThat(state.offer(segment2)).isTrue();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(13),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(0),
          () -> assertThat(state.confirmedBytes).isEqualTo(10),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative2));
      BidiUploadTestUtils.sinkToList(state);
      assertThat(state.lastSentRequestIndex).isEqualTo(1);

      BidiUploadTestUtils.sinkToList(state);
      assertThat(state.onResponse(BidiUploadTestUtils.incremental(11))).isNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(13),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(0),
          () -> assertThat(state.confirmedBytes).isEqualTo(11),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(cumulative2));
    }

    @Test
    public void complexScenario_2_retries() throws Exception {
      ChecksummedTestContent b_10 = ChecksummedTestContent.of("ABCDEFGHIJ");
      Crc32cLengthKnown cumulative0 = Crc32cValue.of(b_10.getCrc32c(), b_10.length());
      ChecksummedTestContent abc = b_10.slice(0, 3);
      ChecksummedTestContent def = b_10.slice(3, 3);
      ChecksummedTestContent ghi = b_10.slice(6, 3);
      ChecksummedTestContent j = b_10.slice(9, 1);

      AppendableUploadState state =
          appendableNew(
              appendRequestNew,
              GrpcCallContext::createDefault,
              50_000,
              SettableApiFuture.create(),
              Crc32cValue.zero());
      GrpcCallContext actualCtx = state.enqueueFirstMessageAndGetGrpcCallContext();
      assertThat(actualCtx).isNotNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(0),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(-1),
          () -> assertThat(state.confirmedBytes).isEqualTo(-1),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(Crc32cValue.zero()));

      assertThat(state.offer(BidiUploadTestUtils.createSegment(abc.asChecksummedData()))).isTrue();
      assertThat(BidiUploadTestUtils.sinkToList(state)).hasSize(1);
      assertThat(state.lastSentRequestIndex).isEqualTo(1);

      assertThat(state.offer(BidiUploadTestUtils.createSegment(def.asChecksummedData()))).isTrue();
      assertThat(BidiUploadTestUtils.sinkToList(state)).hasSize(1);
      assertThat(state.lastSentRequestIndex).isEqualTo(2);

      assertThat(state.offer(BidiUploadTestUtils.createSegment(ghi.asChecksummedData()))).isTrue();
      assertThat(BidiUploadTestUtils.sinkToList(state)).hasSize(1);
      assertThat(state.lastSentRequestIndex).isEqualTo(3);

      assertThat(state.offer(BidiUploadTestUtils.createSegment(j.asChecksummedData()))).isTrue();
      assertThat(state.offer(flushOffset(10))).isTrue();
      assertThat(BidiUploadTestUtils.sinkToList(state)).hasSize(1);
      assertThat(state.lastSentRequestIndex).isEqualTo(5);

      // send incremental response, ack'ing 3 bytes
      assertThat(state.onResponse(resourceFor(abc))).isNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(10),
          () -> assertThat(state.confirmedBytes).isEqualTo(3),
          () -> assertThat(state.isFinalizing()).isFalse());

      // error returned, transition to pending retry
      state.pendingRetry();
      // error is retryable, and backoff has elapsed, transition to retrying
      state.retrying();
      // resolve the opening request and call context
      // todo: better method name
      state.enqueueFirstMessageAndGetGrpcCallContext();

      BidiWriteObjectRequest reconnect =
          BidiWriteObjectRequest.newBuilder()
              .setStateLookup(true)
              .setAppendObjectSpec(
                  AppendObjectSpec.newBuilder()
                      .setBucket(appendRequestNew.getWriteObjectSpec().getResource().getBucket())
                      .setObject(appendRequestNew.getWriteObjectSpec().getResource().getName())
                      .setGeneration(1)
                      .build())
              .build();
      assertThat(state.lastSentRequestIndex).isEqualTo(-1);
      ApiFuture<Void> reconciliation = state.beginReconciliation();
      assertThat(BidiUploadTestUtils.sinkToList(state)).isEqualTo(ImmutableList.of(reconnect));

      assertThat(state.onResponse(BidiUploadTestUtils.incremental(6))).isNull();
      reconciliation.get(137, TimeUnit.MILLISECONDS);

      assertThat(BidiUploadTestUtils.sinkToList(state)).hasSize(2);
    }

    @Test
    public void resultFutureNotResolvedForResourceWithoutFinalizeTime() throws Exception {
      AppendableUploadState state =
          appendableNew(
              appendRequestNew,
              GrpcCallContext::createDefault,
              50_000,
              SettableApiFuture.create(),
              Crc32cValue.zero());
      GrpcCallContext actualCtx = state.enqueueFirstMessageAndGetGrpcCallContext();
      assertThat(actualCtx).isNotNull();
      assertAll(
          () -> assertThat(state.totalSentBytes).isEqualTo(0),
          () -> assertThat(state.lastSentRequestIndex).isEqualTo(-1),
          () -> assertThat(state.confirmedBytes).isEqualTo(-1),
          () -> assertThat(state.isFinalizing()).isFalse(),
          () -> assertThat(state.cumulativeCrc32c).isEqualTo(Crc32cValue.zero()));

      // simulate a scenario where multiple messages are sent to gcs before we receive any
      // resource response. Even if this resource is the expected size, we should not resolved the
      // result future when it doesn't have finalize_time set.
      ChecksummedTestContent b_10 = ChecksummedTestContent.gen(10);
      Crc32cLengthKnown cumulative0 = Crc32cValue.of(b_10.getCrc32c(), b_10.length());
      b_10.chunkup(1).stream()
          .map(ChecksummedTestContent::asChecksummedData)
          .map(BidiUploadTestUtils::createSegment)
          .forEach(c -> assertThat(state.offer(c)).isTrue());
      assertThat(state.offer(finishAt(10))).isTrue();
      assertThat(state.onResponse(resourceFor(appendRequestNew, 10))).isNull();
      assertThat(state.getResultFuture().isDone()).isFalse();

      BidiWriteObjectResponse response =
          resourceFor(
              appendRequestNew,
              b ->
                  b.setSize(10)
                      .setFinalizeTime(timestampNow())
                      .setChecksums(
                          ObjectChecksums.newBuilder().setCrc32C(b_10.getCrc32c()).build()));
      assertThat(state.onResponse(response)).isNull();
      assertThat(state.getResultFuture().isDone()).isTrue();
      assertThat(state.getResultFuture().get()).isEqualTo(response);
    }

    private Range<Integer> range(int min, int maxExclusive) {
      return Range.range(min, BoundType.CLOSED, maxExclusive, BoundType.OPEN);
    }
  }

  static BidiWriteObjectRequest flushOffset(long offset) {
    return onlyFlush.toBuilder().setWriteOffset(offset).setStateLookup(true).build();
  }

  static @NonNull BidiWriteObjectResponse resourceWithSize(int size) {
    return resourceFor(appendRequestNew, size);
  }

  static @NonNull BidiWriteObjectResponse resourceFor(ChecksummedTestContent ctc) {
    return resourceFor(
        appendRequestNew,
        b ->
            b.setSize(ctc.length())
                .setChecksums(ObjectChecksums.newBuilder().setCrc32C(ctc.getCrc32c()).build()));
  }

  static @NonNull BidiWriteObjectResponse resourceFor(BidiWriteObjectRequest req, long size) {
    return resourceFor(req, b -> b.setSize(size));
  }

  static @NonNull BidiWriteObjectResponse resourceFor(
      BidiWriteObjectRequest req, UnaryOperator<Object.Builder> f) {
    Object.Builder b = Object.newBuilder();
    if (req.hasWriteObjectSpec()) {
      WriteObjectSpec spec = req.getWriteObjectSpec();
      b.setBucket(spec.getResource().getBucket())
          .setName(spec.getResource().getName())
          .setGeneration(1);
    } else if (req.hasAppendObjectSpec()) {
      AppendObjectSpec spec = req.getAppendObjectSpec();
      b.setBucket(spec.getBucket()).setName(spec.getObject()).setGeneration(spec.getGeneration());
    } else {
      fail("Unhandled request shape: " + fmtProto(req));
    }
    return BidiWriteObjectResponse.newBuilder().setResource(f.apply(b)).build();
  }

  @FunctionalInterface
  private interface OnlySendClientStream<ReqT> extends ClientStream<ReqT> {
    @Override
    default void closeSendWithError(Throwable t) {}

    @Override
    default void closeSend() {}

    @Override
    default boolean isSendReady() {
      return true;
    }
  }
}
