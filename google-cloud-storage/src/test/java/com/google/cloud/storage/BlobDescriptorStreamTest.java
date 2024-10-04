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
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.NanoClock;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ClientStreamReadyObserver;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.storage.Backoff.Jitterer;
import com.google.cloud.storage.GrpcUtils.ZeroCopyBidiStreamingCallable;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.cloud.storage.RetryContext.OnSuccess;
import com.google.cloud.storage.RetryContext.RetryContextProvider;
import com.google.cloud.storage.RetryContextTest.BlockingOnSuccess;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiReadObjectSpec;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public final class BlobDescriptorStreamTest {

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

  private static ScheduledExecutorService exec;

  private final RetrySettings retrySettings = RetrySettings.newBuilder().build();
  private final BlobDescriptorState state =
      new BlobDescriptorState(GrpcCallContext.createDefault(), REQ_OPEN);
  private final RetryContextProvider retryContextProvider =
      () ->
          RetryContext.of(
              exec,
              RetryingDependencies.simple(NanoClock.getDefaultClock(), retrySettings),
              Retrying.alwaysRetry(),
              Jitterer.noJitter());
  private final ZeroCopyBidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>
      callable =
          new ZeroCopyBidiStreamingCallable<>(
              new BidiStreamingCallable<BidiReadObjectRequest, BidiReadObjectResponse>() {
                @Override
                public ClientStream<BidiReadObjectRequest> internalCall(
                    ResponseObserver<BidiReadObjectResponse> responseObserver,
                    ClientStreamReadyObserver<BidiReadObjectRequest> onReady,
                    ApiCallContext context) {
                  return new ClientStream<BidiReadObjectRequest>() {
                    @Override
                    public void send(BidiReadObjectRequest request) {}

                    @Override
                    public void closeSendWithError(Throwable t) {}

                    @Override
                    public void closeSend() {
                      responseObserver.onComplete();
                    }

                    @Override
                    public boolean isSendReady() {
                      return true;
                    }
                  };
                }
              },
              ResponseContentLifecycleManager.noopBidiReadObjectResponse());

  @BeforeClass
  public static void beforeClass() {
    exec = Executors.newSingleThreadScheduledExecutor();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    if (exec != null) {
      exec.shutdownNow();
      assertThat(exec.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
    }
  }

  @Test
  public void streamRestartShouldNotSendARequestIfAllReadsAreInBackoff() {
    RetryContext read1RetryContext = retryContextProvider.create();
    TestBlobDescriptorStreamRead read1 =
        new TestBlobDescriptorStreamRead(
            1, new ReadCursor(1, 2), new ArrayList<>(), read1RetryContext, false);
    state.putOutstandingRead(1, read1);

    RetryContext streamRetryContext = retryContextProvider.create();
    try (BlobDescriptorStream stream =
        BlobDescriptorStream.create(exec, callable, state, streamRetryContext)) {
      BlockingOnSuccess blockingOnSuccess = new BlockingOnSuccess();
      read1RetryContext.recordError(
          new RuntimeException("read1err"), blockingOnSuccess, RetryContextTest.failOnFailure());

      stream.restart();
      blockingOnSuccess.release();
    }
  }

  @Test
  public void streamRestartShouldSendARequestIfReadsAreNotInBackoff() {
    RetryContext read1RetryContext = retryContextProvider.create();
    TestBlobDescriptorStreamRead read1 =
        new TestBlobDescriptorStreamRead(
            1, new ReadCursor(1, 2), new ArrayList<>(), read1RetryContext, false);
    read1.readyToSend = true;
    state.putOutstandingRead(1, read1);

    RetryContext streamRetryContext = retryContextProvider.create();
    try (BlobDescriptorStream stream =
        BlobDescriptorStream.create(exec, callable, state, streamRetryContext)) {
      stream.restart();
    }
  }

  @Test
  public void attemptingToRestartStreamThatIsAlreadyActiveThrows() {

    RetryContext streamRetryContext = retryContextProvider.create();
    try (BlobDescriptorStream stream =
        BlobDescriptorStream.create(exec, callable, state, streamRetryContext)) {
      stream.send(REQ_OPEN);

      IllegalStateException ise = assertThrows(IllegalStateException.class, stream::restart);
      assertThat(ise).hasMessageThat().contains("already active");
    }
  }

  @Test
  public void sendErrorsIfNotOpen() throws Exception {

    RetryContext streamRetryContext = retryContextProvider.create();
    BlobDescriptorStream stream =
        BlobDescriptorStream.create(exec, callable, state, streamRetryContext);
    assertThat(stream.isOpen()).isTrue();
    stream.close();

    assertAll(
        () -> {
          IllegalStateException ise =
              assertThrows(IllegalStateException.class, () -> stream.send(REQ_OPEN));
          assertThat(ise).hasMessageThat().isEqualTo("Stream closed");
        },
        () -> assertThat(stream.isOpen()).isFalse());
  }

  private static class TestBlobDescriptorStreamRead extends BlobDescriptorStreamRead {

    private boolean readyToSend = false;

    TestBlobDescriptorStreamRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        boolean closed) {
      super(readId, readCursor, childRefs, retryContext, closed);
    }

    @Override
    boolean acceptingBytes() {
      return false;
    }

    @Override
    void accept(ChildRef childRef) {}

    @Override
    void eof() {}

    @Override
    void fail(Throwable t) {}

    @Override
    BlobDescriptorStreamRead withNewReadId(long newReadId) {
      return null;
    }

    @Override
    <T extends Throwable> void recordError(T t, OnSuccess onSuccess, OnFailure<T> onFailure) {}

    @Override
    public boolean readyToSend() {
      return readyToSend;
    }
  }
}
