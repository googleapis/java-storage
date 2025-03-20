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


import com.google.api.core.BetaApi;

@BetaApi
public final class ReadProjectionConfigs {

  private ReadProjectionConfigs() {}

  abstract static class BaseConfig<Projection, Read extends ObjectReadSessionStreamRead<Projection>>
      extends ReadProjectionConfig<Projection> {

    BaseConfig() {}

    abstract Read newRead(long readId, RetryContext retryContext);

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
  @BetaApi
  public static ReadAsChannel asChannel() {
    return ReadAsChannel.INSTANCE;
  }

  @BetaApi
  static ReadAsFutureBytes asFutureBytes() {
    return ReadAsFutureBytes.INSTANCE;
  }

  @BetaApi
  static ReadAsFutureByteString asFutureByteString() {
    return ReadAsFutureByteString.INSTANCE;
  }

  @BetaApi
  public static ReadAsSeekableChannel asSeekableChannel() {
    return ReadAsSeekableChannel.INSTANCE;
  }
}
