/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.storage.object;

// [START storage_appendable_object_tail_read]

import com.google.api.core.ApiFuture;
import com.google.cloud.storage.*;
import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppendableObjectTailRead {
  public static void appendableObjectTailRead(String bucketName, String objectName)
      throws Exception {
    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to the file to upload
    // String filePath = "path/to/your/file";

    try (Storage storage = StorageOptions.grpc().build().getService()) {
      BlobId blobId = BlobId.of(bucketName, objectName);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      long lastKnownReadPosition = 0;

      for (int i = 0; i < 2; i++) {
        String content =
            String.format("--- NEW ENTRY %d ---\nThis is the content for iteration %d.\n", i, i);
        writeToObject(storage, blobId, blobInfo, content);

        // Perform tail read
        Blob result = storage.get(blobId);
        blobInfo = result.toBuilder().build();
        long currentObjectSize = result.getSize();
        long bytesToRead = currentObjectSize - lastKnownReadPosition;
        System.out.println("Current object size is " + currentObjectSize);
        System.out.println("lastknownreadposition is " + lastKnownReadPosition);
        System.out.println("Current bytes to read is " + bytesToRead);
        if (bytesToRead <= 0) {
          continue;
        }
        ApiFuture<BlobReadSession> futureBlobReadSession = storage.blobReadSession(blobId);
        try (BlobReadSession blobReadSession = futureBlobReadSession.get(10, TimeUnit.SECONDS)) {
          RangeSpec rangeSpec = RangeSpec.of(lastKnownReadPosition, currentObjectSize);
          ApiFuture<byte[]> future =
              blobReadSession.readAs(
                  ReadProjectionConfigs.asFutureBytes().withRangeSpec(rangeSpec));
          byte[] bytes = future.get();
          System.out.println(
              "Successfully read "
                  + bytes.length
                  + " bytes from object "
                  + objectName
                  + " in bucket "
                  + bucketName);
        }
        lastKnownReadPosition = currentObjectSize;
      }
    }
  }

  public static void writeToObject(Storage storage, BlobId blobId, BlobInfo blobInfo, String content)
      throws IOException {

    BlobAppendableUploadConfig config =
        BlobAppendableUploadConfig.of().withCloseAction(CloseAction.CLOSE_WITHOUT_FINALIZING);
    BlobAppendableUpload uploadSession = storage.blobAppendableUpload(blobInfo, config);

    try (AppendableUploadWriteableByteChannel channel = uploadSession.open()) {

      ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
      long totalBytesWritten = channel.write(buffer);
      channel.flush();
      System.out.printf(Locale.US, "Wrote %d bytes.\n", totalBytesWritten);
    } catch (IOException ex) {
      throw new IOException("Failed to write to object " + blobId.toGsUtilUri(), ex);
    }
  }
}
// [END storage_appendable_object_tail_read]
