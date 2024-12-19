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
import com.google.api.core.ApiFutures;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;

final class BlobReadSessionAdapter implements BlobReadSession {

  @VisibleForTesting final ObjectReadSession session;

  BlobReadSessionAdapter(ObjectReadSession session) {
    this.session = session;
  }

  @Override
  public BlobInfo getBlobInfo() {
    return Conversions.grpc().blobInfo().decode(session.getResource());
  }

  @Override
  public ApiFuture<byte[]> readRangeAsBytes(RangeSpec range) {
    return StorageException.coalesceAsync(session.readRangeAsBytes(range));
  }

  @Override
  public ScatteringByteChannel readRangeAsChannel(RangeSpec range) {
    return session.readRangeAsChannel(range);
  }

  @Override
  public void close() throws IOException {
    session.close();
  }

  static ApiFuture<BlobReadSession> wrap(ApiFuture<ObjectReadSession> session) {
    return ApiFutures.transform(
        StorageException.coalesceAsync(session),
        BlobReadSessionAdapter::new,
        MoreExecutors.directExecutor());
  }
}
