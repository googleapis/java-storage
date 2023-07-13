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

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.RestorableState;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.storage.v2.RewriteObjectRequest;
import com.google.storage.v2.RewriteResponse;

final class GapicCopyWriter extends CopyWriter {

  // needed for #getResult
  private final transient GrpcStorageImpl storage;
  private final GrpcStorageOptions options;
  private final UnaryCallable<RewriteObjectRequest, RewriteResponse> callable;
  private final ResultRetryAlgorithm<?> alg;
  private final RewriteResponse initialResponse;

  private RewriteResponse mostRecentResponse;

  GapicCopyWriter(
      GrpcStorageImpl storage,
      UnaryCallable<RewriteObjectRequest, RewriteResponse> callable,
      ResultRetryAlgorithm<?> alg,
      RewriteResponse initialResponse) {
    this.storage = storage;
    this.options = storage.getOptions();
    this.callable = callable;
    this.alg = alg;
    this.initialResponse = initialResponse;
    this.mostRecentResponse = initialResponse;
  }

  @Override
  public Blob getResult() {
    while (!isDone()) {
      copyChunk();
    }
    BlobInfo info = Conversions.grpc().blobInfo().decode(mostRecentResponse.getResource());
    return info.asBlob(storage);
  }

  @Override
  public long getBlobSize() {
    return initialResponse.getObjectSize();
  }

  @Override
  public boolean isDone() {
    return mostRecentResponse.getDone();
  }

  @Override
  public long getTotalBytesCopied() {
    return mostRecentResponse.getTotalBytesRewritten();
  }

  @Override
  public void copyChunk() {
    if (!isDone()) {
      RewriteObjectRequest req =
          RewriteObjectRequest.newBuilder()
              .setRewriteToken(mostRecentResponse.getRewriteToken())
              .build();
      GrpcCallContext retryContext = Retrying.newCallContext();
      mostRecentResponse =
          Retrying.run(options, alg, () -> callable.call(req, retryContext), Decoder.identity());
    }
  }

  @Override
  public RestorableState<CopyWriter> capture() {
    return CrossTransportUtils.throwHttpJsonOnly(CopyWriter.class, "capture");
  }
}
