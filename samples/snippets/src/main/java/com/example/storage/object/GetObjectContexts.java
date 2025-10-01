package com.example.storage.object;

// [START storage_get_object_contexts]

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo.ObjectCustomContextPayload;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.Map;

public class GetObjectContexts {
  public static void getObjectMetadata(String projectId, String bucketName, String blobName)
      throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    try (Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {

      Blob blob = storage.get(bucketName, blobName);
      Map<String, ObjectCustomContextPayload> customContexts = blob.getContexts().getCustom();
      // Print blob's object contexts
      System.out.println("\nCustom Contexts:");
      for (Map.Entry<String, ObjectCustomContextPayload> custom : customContexts.entrySet()) {
        System.out.println(custom.getKey() + "=" + custom.getValue());
      }
    }
  }
}
// [END storage_get_object_contexts]
