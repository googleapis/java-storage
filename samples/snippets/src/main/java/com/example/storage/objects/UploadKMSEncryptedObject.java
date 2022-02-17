/*
 * Copyright 2020 Google LLC
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
package com.example.storage.objects;

// [START storage_upload_with_kms_key]

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UploadKMSEncryptedObject {
    public static void uploadKMSEncryptedObject(String projectId, String bucketName, String objectName, String kmsKeyName) {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The name of the KMS key to encrypt with
        // String kmsKeyName = "projects/my-project/locations/us/keyRings/my_key_ring/cryptoKeys/my_key"

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        byte[] data = "Hello, World!".getBytes(UTF_8);

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        storage.create(blobInfo, data, Storage.BlobTargetOption.kmsKeyName(kmsKeyName));

        System.out.println("Uploaded object " + objectName + " in bucket " + bucketName + " encrypted with " + kmsKeyName);
    }
}
// [END storage_upload_with_kms_key]