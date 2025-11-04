package com.example.storage.multipartupload;

import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.RequestBody;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadPartFromFile {
  public static void uploadPartFromFile(
      String projectId, String bucketName, String objectName, String uploadId, String filePath)
      throws IOException {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The ID of the multipart upload
    // String uploadId = "your-upload-id";

    // The path to the file to upload
    // String filePath = "/path/to/your/file.txt";

    HttpStorageOptions storageOptions =
        HttpStorageOptions.newBuilder().setProjectId(projectId).build();
    MultipartUploadSettings mpuSettings = MultipartUploadSettings.of(storageOptions);
    MultipartUploadClient mpuClient = MultipartUploadClient.create(mpuSettings);

    // The minimum part size for a multipart upload is 5 MiB, except for the last part.
    // We are using 8MiB as our part size. You can change this to a different value
    // to optimize for performance.
    int partSize = 5 * 1024 * 1024;

    Path path = Paths.get(filePath);
    long fileSize = Files.size(path);
    long partCount = (long) Math.ceil((double) fileSize / partSize);
    System.out.println("File will be uploaded in " + partCount + " parts.");

    try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
      ByteBuffer buffer = ByteBuffer.allocate(partSize);
      for (int partNumber = 1; partNumber <= partCount; partNumber++) {
        buffer.clear();
        int bytesRead = channel.read(buffer);
        buffer.flip();

        // The RewindableContent is important for retrying failed uploads.
        RequestBody requestBody = RequestBody.of(buffer.slice(0, bytesRead));

        System.out.println("Uploading part " + partNumber);
        UploadPartRequest uploadPartRequest =
            UploadPartRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .partNumber(partNumber)
                .uploadId(uploadId)
                .build();

        UploadPartResponse uploadPartResponse =
            mpuClient.uploadPart(uploadPartRequest, requestBody);

        System.out.println(
            "Part " + partNumber + " uploaded with ETag: " + uploadPartResponse.eTag());
      }
    }

    System.out.println("All parts uploaded.");
  }
}
