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
import com.google.protobuf.ByteString;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;

/** Blob Descriptor is to blob, what File Descriptor is to a file */
public interface BlobDescriptor extends IOAutoCloseable {

  BlobInfo getBlobInfo();

  ApiFuture<byte[]> readRangeAsBytes(RangeSpec range);

  /**
   * Read the provided range as a non-blocking Channel.
   *
   * <p>The returned channel will be non-blocking for all read calls. If bytes have not yet
   * asynchronously been delivered from gcs the method will return rather than waiting for the bytes
   * to arrive.
   */
  ScatteringByteChannel readRangeAsChannel(RangeSpec range);

  @Override
  void close() throws IOException;

  interface ZeroCopySupport {
    interface DisposableByteString extends AutoCloseable, Closeable {
      ByteString byteString();

      @Override
      void close() throws IOException;
    }
  }
}
