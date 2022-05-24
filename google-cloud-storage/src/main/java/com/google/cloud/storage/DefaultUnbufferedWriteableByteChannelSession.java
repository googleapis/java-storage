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

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.function.BiFunction;

final class DefaultUnbufferedWriteableByteChannelSession<R, S>
    implements UnbufferedWritableByteChannelSession<R> {

  private final Object channelInitSyncObj = new Object();

  private final ApiFuture<S> uploadIdFuture;
  private final ApiFunction<S, UnbufferedWritableByteChannel> f;
  private final SettableApiFuture<R> resultFuture;

  private volatile ApiFuture<UnbufferedWritableByteChannel> channel;

  public DefaultUnbufferedWriteableByteChannelSession(
      ApiFuture<S> uploadIdFuture,
      BiFunction<S, SettableApiFuture<R>, UnbufferedWritableByteChannel> f) {
    this.uploadIdFuture = uploadIdFuture;
    this.resultFuture = SettableApiFuture.create();
    this.f = (s) -> f.apply(s, resultFuture);
  }

  @Override
  public ApiFuture<UnbufferedWritableByteChannel> openAsync() {
    if (channel == null) {
      synchronized (channelInitSyncObj) {
        if (channel == null) {
          channel = ApiFutures.transform(uploadIdFuture, f, MoreExecutors.directExecutor());
        }
      }
    }
    return channel;
  }

  @Override
  public ApiFuture<R> getResult() {
    return resultFuture;
  }
}
