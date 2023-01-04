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

import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GrpcBlobReadChannel extends BaseStorageReadChannel<Object> {

  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
  private final ReadObjectRequest request;
  private final boolean autoGzipDecompression;

  GrpcBlobReadChannel(
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      ReadObjectRequest request,
      boolean autoGzipDecompression) {
    super(Conversions.grpc().blobInfo());
    this.read = read;
    this.request = request;
    this.autoGzipDecompression = autoGzipDecompression;
  }

  @Override
  public RestorableState<ReadChannel> capture() {
    return GrpcStorageImpl.throwHttpJsonOnly(ReadChannel.class, "capture");
  }

  @Override
  protected LazyReadChannel<Object> newLazyReadChannel() {
    return new LazyReadChannel<>(
        () ->
            ResumableMedia.gapic()
                .read()
                .byteChannel(read)
                .setHasher(Hasher.noop())
                .setAutoGzipDecompression(autoGzipDecompression)
                .buffered(getBufferHandle())
                .setReadObjectRequest(getReadObjectRequest())
                .build());
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
