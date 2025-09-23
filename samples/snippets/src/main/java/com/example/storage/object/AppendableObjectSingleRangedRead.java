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
import com.google.cloud.storage.ReadProjectionConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.concurrent.TimeUnit;

public class AppendableObjectSingleRangedRead {
  public static void appendableObjectSingleRangedRead(
      String bucketName, String objectName, long offset, int length) throws Exception {

    try (Storage storage = StorageOptions.grpc().build().getService()) {
      BlobId blobId = BlobId.of(bucketName, objectName);
      ApiFuture<BlobReadSession> futureBlobReadSession = storage.blobReadSession(blobId);

      try (BlobReadSession blobReadSession = futureBlobReadSession.get(10, TimeUnit.SECONDS)) {
        // Define the range of bytes to read.
        RangeSpec rangeSpec = RangeSpec.of(offset, length);
        ApiFuture<byte[]> future =
            blobReadSession.readAs(ReadProjectionConfigs.asFutureBytes().withRangeSpec(rangeSpec));

        // Wait for the read to complete.
        byte[] bytes = future.get();

        System.out.println(
            "Successfully read "
                + bytes.length
                + " bytes from object "
                + objectName
                + " in bucket "
                + bucketName);
      }
    }
  }
}
// [END storage_read_appendable_object_single_range]
