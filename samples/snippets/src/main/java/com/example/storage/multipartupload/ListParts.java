package com.example.storage.multipartupload;

import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.Part;

public class ListParts {
  public static void listParts(
      String projectId, String bucketName, String objectName, String uploadId) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The ID of the multipart upload
    // String uploadId = "your-upload-id";

    HttpStorageOptions storageOptions =
        HttpStorageOptions.newBuilder().setProjectId(projectId).build();
    MultipartUploadSettings mpuSettings = MultipartUploadSettings.of(storageOptions);
    MultipartUploadClient mpuClient = MultipartUploadClient.create(mpuSettings);

    System.out.println("Listing parts for upload ID: " + uploadId);

    ListPartsRequest listPartsRequest =
        ListPartsRequest.builder().bucket(bucketName).key(objectName).uploadId(uploadId).build();

    ListPartsResponse listPartsResponse = mpuClient.listParts(listPartsRequest);

    if (listPartsResponse.getParts() == null || listPartsResponse.getParts().isEmpty()) {
      System.out.println("No parts have been uploaded yet.");
      return;
    }

    System.out.println("Uploaded Parts:");
    for (Part part : listPartsResponse.getParts()) {
      System.out.println("  - Part Number: " + part.partNumber());
      System.out.println("    ETag: " + part.eTag());
      System.out.println("    Size: " + part.size() + " bytes");
      System.out.println("    Last Modified: " + part.lastModified());
    }
  }
}
