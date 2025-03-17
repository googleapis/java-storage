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

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.AccumulatingRead;
import com.google.cloud.storage.BaseObjectReadSessionStreamRead.StreamingRead;
import com.google.cloud.storage.ZeroCopySupport.DisposableByteString;
import java.nio.channels.ScatteringByteChannel;

public final class RangeProjectionConfigs {

  abstract static class BaseConfig<Projection, Read extends ObjectReadSessionStreamRead<Projection>>
      extends RangeProjectionConfig<Projection> {
    abstract Read newRead(long readId, RangeSpec range, RetryContext retryContext);

    @Override
    ProjectionType getType() {
      return ProjectionType.STREAM_READ;
    }
  }

  /**
   * Read a range as a non-blocking Channel.
   *
   * <p>The returned channel will be non-blocking for all read calls. If bytes have not yet
   * asynchronously been delivered from gcs the method will return rather than waiting for the bytes
   * to arrive.
   */
  public static RangeAsChannel asChannel() {
    return RangeAsChannel.INSTANCE;
  }

  public static RangeAsFutureBytes asFutureBytes() {
    return RangeAsFutureBytes.INSTANCE;
  }

  static RangeAsFutureByteString asFutureByteString() {
    return RangeAsFutureByteString.INSTANCE;
  }

  public static final class RangeAsChannel
      extends BaseConfig<ScatteringByteChannel, StreamingRead> {
    private static final RangeAsChannel INSTANCE = new RangeAsChannel();

    private RangeAsChannel() {
      super();
    }

    @Override
    BaseConfig<ScatteringByteChannel, ?> cast() {
      return this;
    }

    @Override
    StreamingRead newRead(long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.streamingRead(readId, range, retryContext);
    }
  }

  public static final class RangeAsFutureBytes
      extends BaseConfig<ApiFuture<byte[]>, AccumulatingRead<byte[]>> {
    private static final RangeAsFutureBytes INSTANCE = new RangeAsFutureBytes();

    private RangeAsFutureBytes() {
      super();
    }

    @Override
    BaseConfig<ApiFuture<byte[]>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<byte[]> newRead(long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createByteArrayAccumulatingRead(
          readId, range, retryContext);
    }
  }

  static final class RangeAsFutureByteString
      extends BaseConfig<ApiFuture<DisposableByteString>, AccumulatingRead<DisposableByteString>> {
    private static final RangeAsFutureByteString INSTANCE = new RangeAsFutureByteString();

    private RangeAsFutureByteString() {
      super();
    }

    @Override
    BaseConfig<ApiFuture<DisposableByteString>, ?> cast() {
      return this;
    }

    @Override
    AccumulatingRead<DisposableByteString> newRead(
        long readId, RangeSpec range, RetryContext retryContext) {
      return ObjectReadSessionStreamRead.createZeroCopyByteStringAccumulatingRead(
          readId, range, retryContext);
    }
  }
}
