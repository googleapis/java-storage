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

import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.storage.GapicDownloadSessionBuilder.ReadableByteChannelSessionBuilder;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GrpcBlobReadChannel extends BaseStorageReadChannel<Object> {

  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
  private final RetryingDependencies retryingDependencies;
  private final ResultRetryAlgorithm<?> resultRetryAlgorithm;
  private final ResponseContentLifecycleManager responseContentLifecycleManager;
  private final ReadObjectRequest request;
  private final boolean autoGzipDecompression;

  GrpcBlobReadChannel(
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      RetryingDependencies retryingDependencies,
      ResultRetryAlgorithm<?> resultRetryAlgorithm,
      ResponseContentLifecycleManager responseContentLifecycleManager,
      ReadObjectRequest request,
      boolean autoGzipDecompression) {
    super(Conversions.grpc().blobInfo());
    this.read = read;
    this.retryingDependencies = retryingDependencies;
    this.resultRetryAlgorithm = resultRetryAlgorithm;
    this.responseContentLifecycleManager = responseContentLifecycleManager;
    this.request = request;
    this.autoGzipDecompression = autoGzipDecompression;
  }

  @Override
  public RestorableState<ReadChannel> capture() {
    return CrossTransportUtils.throwHttpJsonOnly(ReadChannel.class, "capture");
  }

  @Override
  protected LazyReadChannel<?, Object> newLazyReadChannel() {
    return new LazyReadChannel<>(
        () -> {
          ReadableByteChannelSessionBuilder b =
              ResumableMedia.gapic()
                  .read()
                  .byteChannel(
                      read,
                      retryingDependencies,
                      resultRetryAlgorithm,
                      responseContentLifecycleManager)
                  .setHasher(Hasher.noop())
                  .setAutoGzipDecompression(autoGzipDecompression);
          BufferHandle bufferHandle = getBufferHandle();
          // because we're erasing the specific type of channel, we need to declare it here.
          // If we don't, the compiler complains we're not returning a compliant type.
          ReadableByteChannelSession<?, Object> session;
          if (bufferHandle.capacity() > 0) {
            session =
                b.buffered(getBufferHandle()).setReadObjectRequest(getReadObjectRequest()).build();
          } else {
            session = b.unbuffered().setReadObjectRequest(getReadObjectRequest()).build();
          }
          return session;
        });
  }

  @NonNull
  private ReadObjectRequest getReadObjectRequest() {
    ByteRangeSpec rangeSpec = getByteRangeSpec();
    ReadObjectRequest.Builder b = request.toBuilder();
    if (request.getGeneration() == 0) {
      Object resolvedObject = getResolvedObject();
      if (resolvedObject != null) {
        b.setGeneration(resolvedObject.getGeneration());
      }
    }
    return rangeSpec.seekReadObjectRequest(b).build();
  }
}
