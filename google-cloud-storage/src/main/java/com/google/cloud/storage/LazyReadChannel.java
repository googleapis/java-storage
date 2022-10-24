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

import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;

final class LazyReadChannel<T> {

  private final Supplier<BufferedReadableByteChannelSession<T>> session;
  private final Supplier<BufferedReadableByteChannel> channel;

  private boolean open = false;

  LazyReadChannel(Supplier<BufferedReadableByteChannelSession<T>> session) {
    this.session = session;
    this.channel =
        Suppliers.memoize(
            () -> {
              open = true;
              return session.get().open();
            });
  }

  BufferedReadableByteChannel getChannel() {
    return channel.get();
  }

  Supplier<BufferedReadableByteChannelSession<T>> getSession() {
    return session;
  }

  boolean isOpen() {
    return open && channel.get().isOpen();
  }
}
