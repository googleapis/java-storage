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

// [START storage_finalize_appendable_object_upload]

import com.google.cloud.storage.BlobAppendableUpload;
import com.google.cloud.storage.BlobAppendableUploadConfig;
import com.google.cloud.storage.BlobAppendableUploadConfig.CloseAction;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FinalizeAppendableObjectUpload {
  public static void finalizeAppendableObjectUpload(String bucketName, String objectName)
      throws Exception {
    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS GCS unfinalized appendable object
    // String objectName = "your-object-name";

    Storage storage = StorageOptions.grpc().build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    BlobAppendableUploadConfig config =
        BlobAppendableUploadConfig.of().withCloseAction(CloseAction.FINALIZE_WHEN_CLOSING);
    BlobAppendableUpload finalizingSession = storage.blobAppendableUpload(blobInfo, config);

    finalizingSession.open().close();
    BlobInfo finalizedBlob = finalizingSession.getResult().get(5, TimeUnit.SECONDS);

    System.out.printf(
        Locale.US,
        "Appendable object %s successfully finalized with size %d.\n",
        finalizedBlob.getBlobId().toGsUtilUriWithGeneration(),
        finalizedBlob.getSize());
  }
}
// [END storage_finalize_appendable_object_upload]
