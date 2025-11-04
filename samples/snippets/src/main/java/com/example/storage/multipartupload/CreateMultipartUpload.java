package com.example.storage.multipartupload;

import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;

public class CreateMultipartUpload {
  public static void createMultipartUpload(String projectId, String bucketName, String objectName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String sourceBucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String sourceObjectName = "your-object-name";

    HttpStorageOptions storageOptions =
        HttpStorageOptions.newBuilder().setProjectId(projectId).build();
    MultipartUploadSettings mpuSettings = MultipartUploadSettings.of(storageOptions);
    MultipartUploadClient mpuClient = MultipartUploadClient.create(mpuSettings);

    System.out.println("Initiating multipart upload for " + objectName);
    CreateMultipartUploadRequest createRequest =
        CreateMultipartUploadRequest.builder().bucket(bucketName).key(objectName).build();
    CreateMultipartUploadResponse createResponse = mpuClient.createMultipartUpload(createRequest);
    String uploadId = createResponse.uploadId();
    System.out.println("Upload ID: " + uploadId);
  }
}
