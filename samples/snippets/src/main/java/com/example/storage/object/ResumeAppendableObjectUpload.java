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

// [START storage_resume_appendable_object_upload]

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobAppendableUpload;
import com.google.cloud.storage.BlobAppendableUpload.AppendableUploadWriteableByteChannel;
import com.google.cloud.storage.BlobAppendableUploadConfig;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Locale;

public class ResumeAppendableObjectUpload {
  public static void resumeAppendableObjectUpload(
      String bucketName, String objectName, String filePath) throws Exception {
    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS unfinalized appendable object
    // String objectName = "your-object-name";

    // The path to the file to upload
    // String filePath = "path/to/your/file";

    Storage storage = StorageOptions.grpc().build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    Blob existingBlob = storage.get(blobId);
    BlobInfo blobInfoForTakeover = BlobInfo.newBuilder(existingBlob.getBlobId()).build();
    FileChannel fileChannel = FileChannel.open(Paths.get(filePath));

    long currentObjectSize = existingBlob.getSize();
    System.out.printf(
        Locale.US,
        "Resuming upload for %s. Currently uploaded size: %d bytes\n",
        blobId.toGsUtilUri(),
        currentObjectSize);

    BlobAppendableUploadConfig config =
        BlobAppendableUploadConfig.of().withCloseAction(CloseAction.CLOSE_WITHOUT_FINALIZING);
    BlobAppendableUpload resumeUploadSession =
        storage.blobAppendableUpload(blobInfoForTakeover, config);
    try (AppendableUploadWriteableByteChannel channel = resumeUploadSession.open()) {

      if (fileChannel.size() < currentObjectSize) {
        throw new IOException(
            "Local file is smaller than the already uploaded data. File size: "
                + fileChannel.size()
                + ", Uploaded size: "
                + currentObjectSize);
      } else if (fileChannel.size() == currentObjectSize) {
        System.out.println("No more data to upload.");
      } else {
        fileChannel.position(currentObjectSize);
        System.out.printf(
            Locale.US, "Appending %d bytes\n", fileChannel.size() - currentObjectSize);
        ByteStreams.copy(fileChannel, channel);
      }
    }
    BlobInfo result = storage.get(blobId);
    System.out.printf(
        Locale.US,
        "Object %s successfully resumed. Total size: %d\n",
        result.getBlobId().toGsUtilUriWithGeneration(),
        result.getSize());
  }
}
// [END storage_resume_appendable_object_upload]
