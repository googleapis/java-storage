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

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * When writing to GCS using the WriteObject rpc, there are some behavioral differences between
 * performing a "direct" upload vs a "resumable" upload.
 *
 * <p>This class provides the encapsulation of the stream and "fsync" lifecycles and possible
 * automatic retry where applicable.
 *
 * <p>In this class "fsync" is used to mean "complete the client stream to GCS and await its
 * response". We are loosely following the concept used in linux to flush data to disk <a
 * target="_blank" href="https://man7.org/linux/man-pages/man2/fdatasync.2.html">fsync(2)</a>
 */
final class WriteFlushStrategy {

  private WriteFlushStrategy() {}

  /**
   * Create a {@link BidiFlusher} which will keep a bidirectional stream open, flushing and sending
   * the appropriate signals to GCS when the buffer is full.
   */
  static BidiFlusherFactory defaultBidiFlusher(
      BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg,
      Supplier<GrpcCallContext> baseContextSupplier) {
    return (String bucketName,
        LongConsumer committedTotalBytesCallback,
        Consumer<BidiWriteObjectResponse> onSuccessCallback) ->
        new DefaultBidiFlusher(
            write,
            deps,
            alg,
            bucketName,
            committedTotalBytesCallback,
            onSuccessCallback,
            baseContextSupplier);
  }

  static GrpcCallContext contextWithBucketName(String bucketName, GrpcCallContext baseContext) {
    if (bucketName != null && !bucketName.isEmpty()) {
      return baseContext.withExtraHeaders(
          ImmutableMap.of(
              "x-goog-request-params", ImmutableList.of(String.format("bucket=%s", bucketName))));
    }
    return baseContext;
  }

  private static BidiWriteObjectRequest possiblyPairDownBidiRequest(
      BidiWriteObjectRequest message, boolean firstMessageOfStream) {
    if (firstMessageOfStream && message.getWriteOffset() == 0) {
      return message;
    }

    BidiWriteObjectRequest.Builder b = message.toBuilder();
    if (!firstMessageOfStream) {
      b.clearUploadId();
    }

    if (message.getWriteOffset() > 0) {
      b.clearWriteObjectSpec();
    }

    if (message.getWriteOffset() > 0 && !message.getFinishWrite()) {
      b.clearObjectChecksums();
    }
    return b.build();
  }

  @FunctionalInterface
  interface BidiFlusherFactory {
    /**
     * @param committedTotalBytesCallback Callback to signal the total number of bytes committed by
     *     this flusher.
     * @param onSuccessCallback Callback to signal success, and provide the final response.
     */
    BidiFlusher newFlusher(
        String bucketName,
        LongConsumer committedTotalBytesCallback,
        Consumer<BidiWriteObjectResponse> onSuccessCallback);
  }

  interface BidiFlusher {
    void flush(@NonNull List<BidiWriteObjectRequest> segments);

    void close(@Nullable BidiWriteObjectRequest req);
  }

  public static final class DefaultBidiFlusher implements BidiFlusher {

    private final BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write;
    private final RetryingDependencies deps;
    private final ResultRetryAlgorithm<?> alg;
    private final String bucketName;
    private final LongConsumer sizeCallback;
    private final Consumer<BidiWriteObjectResponse> completeCallback;
    private final Supplier<GrpcCallContext> baseContextSupplier;
    private volatile ApiStreamObserver<BidiWriteObjectRequest> stream;

    private final BidiObserver responseObserver;

    private DefaultBidiFlusher(
        BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse> write,
        RetryingDependencies deps,
        ResultRetryAlgorithm<?> alg,
        String bucketName,
        LongConsumer sizeCallback,
        Consumer<BidiWriteObjectResponse> completeCallback,
        Supplier<GrpcCallContext> baseContextSupplier) {
      this.write = write;
      this.deps = deps;
      this.alg = alg;
      this.bucketName = bucketName;
      this.sizeCallback = sizeCallback;
      this.completeCallback = completeCallback;
      this.baseContextSupplier = baseContextSupplier;
      this.responseObserver = new BidiObserver(sizeCallback, completeCallback);
    }

    public void flush(@NonNull List<BidiWriteObjectRequest> segments) {
      ensureOpen();
      Retrying.run(
          deps,
          alg,
          () -> {
            boolean first = true;
            for (BidiWriteObjectRequest message : segments) {
              message = possiblyPairDownBidiRequest(message, first);

              stream.onNext(message);
              first = false;
            }
            BidiWriteObjectRequest message =
                BidiWriteObjectRequest.newBuilder().setFlush(true).setStateLookup(true).build();
            stream.onNext(message);
            responseObserver.await();
            return null;
          },
          Decoder.identity());
    }

    public void close(@Nullable BidiWriteObjectRequest req) {
      ensureOpen();
      if (req != null) {
        flush(ImmutableList.of(req));
      }
    }

    private void ensureOpen() {
      if (stream == null) {
        synchronized (this) {
          if (stream == null) {
            GrpcCallContext internalContext =
                contextWithBucketName(bucketName, baseContextSupplier.get());
            stream =
                this.write
                    .withDefaultCallContext(internalContext)
                    .bidiStreamingCall(responseObserver);
          }
        }
      }
    }
  }

  static class BidiObserver implements ApiStreamObserver<BidiWriteObjectResponse> {

    private final LongConsumer sizeCallback;
    private final Consumer<BidiWriteObjectResponse> completeCallback;

    private final SettableApiFuture<Void> invocationHandle;
    private volatile BidiWriteObjectResponse last;

    BidiObserver(LongConsumer sizeCallback, Consumer<BidiWriteObjectResponse> completeCallback) {
      this.sizeCallback = sizeCallback;
      this.completeCallback = completeCallback;
      this.invocationHandle = SettableApiFuture.create();
    }

    @Override
    public void onNext(BidiWriteObjectResponse value) {
      // incremental update
      if (value.hasPersistedSize()) {
        sizeCallback.accept(value.getPersistedSize());
        invocationHandle.set(null);
      } else if (value.hasResource()) {
        sizeCallback.accept(value.getResource().getSize());
      }
      last = value;
    }

    /**
     * observed exceptions so far
     *
     * <ol>
     *   <li>{@link com.google.api.gax.rpc.OutOfRangeException}
     *   <li>{@link com.google.api.gax.rpc.AlreadyExistsException}
     *   <li>{@link io.grpc.StatusRuntimeException}
     * </ol>
     */
    @Override
    public void onError(Throwable t) {
      invocationHandle.setException(t);
    }

    @Override
    public void onCompleted() {

      if (last != null && last.hasResource()) {
        completeCallback.accept(last);
      }
      invocationHandle.set(null);
    }

    void await() {
      try {
        invocationHandle.get();
      } catch (InterruptedException | ExecutionException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        } else {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
