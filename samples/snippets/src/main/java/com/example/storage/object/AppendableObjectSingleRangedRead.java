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

// [START storage_read_appendable_object_single_range]

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobReadSession;
import com.google.cloud.storage.RangeSpec;
import com.google.cloud.storage.ReadAsChannel;
import com.google.cloud.storage.ReadProjectionConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppendableObjectSingleRangedRead {
  public static void appendableObjectSingleRangedRead(
      String bucketName, String objectName, long offset, int length) throws Exception {
    Storage storage = StorageOptions.grpc().build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);

    ApiFuture<BlobReadSession> futureBlobReadSession = storage.blobReadSession(blobId);

    try (BlobReadSession blobReadSession = futureBlobReadSession.get(10, TimeUnit.SECONDS)) {
      ByteBuffer buf = ByteBuffer.allocate(length);
      RangeSpec rangeSpec = RangeSpec.of(offset, offset + length);

      ReadAsChannel readAsChannelConfig =
          ReadProjectionConfigs.asChannel().withRangeSpec(rangeSpec);

      try (ScatteringByteChannel channel = blobReadSession.readAs(readAsChannelConfig)) {
        int bytesRead = 0;
        while (buf.hasRemaining() && bytesRead != -1) {
          bytesRead = channel.read(buf);
        }
      }

      buf.flip();
      System.out.printf(
          Locale.US,
          "Read %d bytes from range %s of object %s%n",
          buf.remaining(),
          rangeSpec,
          blobReadSession.getBlobInfo().getBlobId().toGsUtilUriWithGeneration());
    }
  }
}
// [END storage_read_appendable_object_single_range]
