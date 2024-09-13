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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public final class AppendableBlobUpload implements AutoCloseable {
  private final BufferedWritableByteChannelSession.BufferedWritableByteChannel channel;
  private final ApiFuture<BlobInfo> result;

  private AppendableBlobUpload(BlobInfo blob, BlobWriteSession session) throws IOException {
    channel = (BufferedWritableByteChannelSession.BufferedWritableByteChannel) (session.open());
    result = session.getResult();
  }

  static AppendableBlobUpload createNewAppendableBlob(BlobInfo blob, BlobWriteSession session)
      throws IOException {
    return new AppendableBlobUpload(blob, session);
  }

  public BlobInfo finalizeUpload() throws IOException, ExecutionException, InterruptedException {
    ((GapicBidiUnbufferedWritableByteChannel) channel.getChannel()).setFinalFlush();
    channel.flush();
    ((GapicBidiUnbufferedWritableByteChannel) channel.getChannel()).finalizeWrite();
    close();
    return result.get();
  }

  public void write(ByteBuffer buffer) throws IOException {
    channel.write(buffer);
  }

  @Override
  public void close() throws IOException {
    if (channel.isOpen()) {
      channel.close();
    }
  }
}
