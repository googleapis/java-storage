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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.core.ApiFuture;
import com.google.cloud.Restorable;
import com.google.cloud.RestorableState;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public interface ReadChannel extends ReadableByteChannel, AutoCloseable, Restorable<ReadChannel> {

  void close();

  void setChunkSize(int chunkSize);

  ReadChannel setByteRangeSpec(ByteRangeSpec byteRangeSpec);

  ApiFuture<BlobInfo> getObject();

  default ByteRangeSpec getByteRangeSpec() {
    return ByteRangeSpec.nullRange();
  }

  /** @deprecated Use {@link #setByteRangeSpec(ByteRangeSpec)} */
  @Deprecated
  @SuppressWarnings("resource")
  default void seek(long position) throws IOException {
    checkArgument(position >= 0, "position must be >= 0");
    try {
      setByteRangeSpec(getByteRangeSpec().withNewBeginOffset(position));
    } catch (StorageException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      } else {
        throw e;
      }
    }
  }

  /** @deprecated Use {@link #setByteRangeSpec(ByteRangeSpec)} */
  @SuppressWarnings("resource")
  @Deprecated
  default ReadChannel limit(long limit) {
    checkArgument(limit >= 0, "limit must be >= 0");
    setByteRangeSpec(getByteRangeSpec().withNewEndOffset(limit));
    return this;
  }

  /** @deprecated Use {@link #getByteRangeSpec()} */
  @Deprecated
  default long limit() {
    return getByteRangeSpec().endOffset();
  }

  ReadChannelState capture();

  interface ReadChannelState extends RestorableState<ReadChannel> {}
}
