package com.example.storage.multipartupload;

import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;

public class AbortMultipartUpload {
  public static void abortMultipartUpload(String projectId, String bucketName, String objectName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    HttpStorageOptions storageOptions =
        HttpStorageOptions.newBuilder().setProjectId(projectId).build();
    MultipartUploadSettings mpuSettings = MultipartUploadSettings.of(storageOptions);
    MultipartUploadClient mpuClient = MultipartUploadClient.create(mpuSettings);

    System.out.println("Initiating a multipart upload to abort for " + objectName);
    CreateMultipartUploadRequest createRequest =
        CreateMultipartUploadRequest.builder().bucket(bucketName).key(objectName).build();
    CreateMultipartUploadResponse createResponse = mpuClient.createMultipartUpload(createRequest);
    String uploadId = createResponse.uploadId().trim();
    System.out.println("Upload ID to be aborted: " + uploadId);

    System.out.println("Aborting multipart upload: " + uploadId);
    AbortMultipartUploadRequest abortRequest =
        AbortMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .uploadId(uploadId)
            .build();

    mpuClient.abortMultipartUpload(abortRequest);

    System.out.println("Multipart upload with ID " + uploadId + " has been successfully aborted.");
  }
}
