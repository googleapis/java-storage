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

// [START storage_read_appendable_object_multiple_ranges]

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

public class AppendableObjectMultipleRangedRead {
  public static void appendableObjectMultipleRangedRead(
      String bucketName, String objectName, long offset1, int length1, long offset2, int length2)
      throws Exception {
    Storage storage = StorageOptions.grpc().build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);

    ApiFuture<BlobReadSession> futureBlobReadSession = storage.blobReadSession(blobId);

    try (BlobReadSession blobReadSession = futureBlobReadSession.get(10, TimeUnit.SECONDS)) {
      RangeSpec rangeSpec1 = RangeSpec.of(offset1, offset1 + length1);
      ReadAsChannel readAsChannelConfig1 =
          ReadProjectionConfigs.asChannel().withRangeSpec(rangeSpec1);

      RangeSpec rangeSpec2 = RangeSpec.of(offset2, offset2 + length2);
      ReadAsChannel readAsChannelConfig2 =
          ReadProjectionConfigs.asChannel().withRangeSpec(rangeSpec2);

      ByteBuffer buf1 = ByteBuffer.allocate(length1);
      ByteBuffer buf2 = ByteBuffer.allocate(length2);
      int bytesRead1 = 0;
      int bytesRead2 = 0;

      try (ScatteringByteChannel channel1 = blobReadSession.readAs(readAsChannelConfig1)) {
        int currentRead = 0;
        while (buf1.hasRemaining() && currentRead != -1) {
          currentRead = channel1.read(buf1);
          if (currentRead > 0) {
            bytesRead1 += currentRead;
          }
        }
      }
      try (ScatteringByteChannel channel2 = blobReadSession.readAs(readAsChannelConfig2)) {
        int currentRead = 0;
        while (buf2.hasRemaining() && currentRead != -1) {
          currentRead = channel2.read(buf2);
          if (currentRead > 0) {
            bytesRead2 += currentRead;
          }
        }
      }

      System.out.printf(
          Locale.US,
          "Read %d bytes from range %s and %d bytes from range %s%n",
          bytesRead1,
          rangeSpec1,
          bytesRead2,
          rangeSpec2);
    }
  }
}
// [END storage_read_appendable_object_multiple_ranges]
