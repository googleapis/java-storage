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

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.function.LongConsumer;
import javax.annotation.ParametersAreNonnullByDefault;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
final class ApiaryUnbufferedWritableByteChannel implements UnbufferedWritableByteChannel {

  private final JsonResumableSession session;

  private final SettableApiFuture<StorageObject> result;
  private final LongConsumer committedBytesCallback;

  private boolean open;
  private long cumulativeByteCount;
  private boolean finished;

  ApiaryUnbufferedWritableByteChannel(
      HttpClientContext httpClientContext,
      RetryingDependencies deps,
      ResultRetryAlgorithm<?> alg,
      JsonResumableWrite resumableWrite,
      SettableApiFuture<StorageObject> result,
      LongConsumer committedBytesCallback) {
    this.session = ResumableSession.json(httpClientContext, deps, alg, resumableWrite);
    this.result = result;
    this.committedBytesCallback = committedBytesCallback;
    this.open = true;
    this.cumulativeByteCount = resumableWrite.getBeginOffset();
    this.finished = false;
  }

  @Override
  public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
    if (!open) {
      throw new ClosedChannelException();
    }
    RewindableContent content = RewindableContent.of(Utils.subArray(srcs, offset, length));
    long available = content.getLength();
    long newFinalByteOffset = cumulativeByteCount + available;
    final HttpContentRange header;
    ByteRangeSpec rangeSpec = ByteRangeSpec.explicit(cumulativeByteCount, newFinalByteOffset);
    if (available % ByteSizeConstants._256KiB == 0) {
      header = HttpContentRange.of(rangeSpec);
    } else {
      header = HttpContentRange.of(rangeSpec, newFinalByteOffset);
      finished = true;
    }
    try {
      ResumableOperationResult<@Nullable StorageObject> operationResult =
          session.put(content, header);
      long persistedSize = operationResult.getPersistedSize();
      committedBytesCallback.accept(persistedSize);
      this.cumulativeByteCount = persistedSize;
      if (finished) {
        StorageObject storageObject = operationResult.getObject();
        result.set(storageObject);
      }
      return available;
    } catch (Exception e) {
      result.setException(e);
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    open = false;
    if (!finished) {
      try {
        ResumableOperationResult<@Nullable StorageObject> operationResult =
            session.put(RewindableContent.empty(), HttpContentRange.of(cumulativeByteCount));
        long persistedSize = operationResult.getPersistedSize();
        committedBytesCallback.accept(persistedSize);
        result.set(operationResult.getObject());
      } catch (Exception e) {
        result.setException(e);
        throw StorageException.coalesce(e);
      }
    }
  }
}
