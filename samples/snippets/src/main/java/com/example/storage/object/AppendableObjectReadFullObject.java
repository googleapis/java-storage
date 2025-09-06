/*
 * Copyright 2025 Google LLC
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

package com.example.storage.object;

// [START storage_read_appendable_object_full]

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobReadSession;
import com.google.cloud.storage.ReadAsChannel;
import com.google.cloud.storage.ReadProjectionConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppendableObjectReadFullObject {
  public static void appendableObjectReadFullObject(String bucketName, String objectName)
      throws Exception {

    Storage storage = StorageOptions.grpc().build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    ApiFuture<BlobReadSession> futureBlobReadSession = storage.blobReadSession(blobId);

    try (BlobReadSession blobReadSession = futureBlobReadSession.get(10, TimeUnit.SECONDS)) {
      ReadAsChannel readAsChannelConfig = ReadProjectionConfigs.asChannel();

      long newlineCount = 0;
      try (ScatteringByteChannel channel = blobReadSession.readAs(readAsChannelConfig)) {
        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
        int bytesRead = 0;
        while ((bytesRead = channel.read(buffer)) != -1) {
          if (bytesRead > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
              if (buffer.get() == '\n') {
                newlineCount++;
              }
            }
            buffer.clear();
          }
        }
      }
      System.out.printf(
          Locale.US,
          "Found %d newline characters in object %s %n",
          newlineCount,
          blobId.toGsUtilUri());
    }
  }
}
// [END storage_read_appendable_object_full]
