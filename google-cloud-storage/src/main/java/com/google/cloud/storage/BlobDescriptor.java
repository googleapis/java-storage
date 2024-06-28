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

/** Blob Descriptor is to blob, what File Descriptor is to a file */
public interface BlobDescriptor {

  BlobInfo getBlobInfo();

  ApiFuture<byte[]> readRangeAsBytes(ByteRangeSpec range);

  interface ZeroCopySupport {
    interface DisposableByteString extends AutoCloseable, Closeable {
      ByteString byteString();

      @Override
      void close() throws IOException;
    }
  }
}
