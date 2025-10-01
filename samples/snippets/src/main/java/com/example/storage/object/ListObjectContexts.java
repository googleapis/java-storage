package com.example.storage.object;

// [START storage_list_object_contexts]

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class ListObjectContexts {
  public static void listObjectContexts(String projectId, String bucketName) throws Exception {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    try (Storage storage =
        StorageOptions.newBuilder().setProjectId(projectId).build().getService()) {
      /**
       * List any object that has a context with the specified key attached String filter =
       * "contexts.\"KEY\":*";
       *
       * <p>List any object that that does not have a context with the specified key attached String
       * filter = "NOT contexts.\"KEY\":*";
       *
       * <p>List any object that has a context with the specified key and value attached String
       * filter = "contexts.\"KEY\"=\"VALUE\"";
       *
       * <p>List any object that does not have a context with the specified key and value attached
       * String filter = "NOT contexts.\"KEY\"=\"VALUE\"";
       */
      String key = "your-context-key";
      String filter = "contexts.\"" + key + "\":*";

      System.out.println("Listing objects for bucket: " + bucketName + "with context key: " + key);
      Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.filter(filter));
      for (Blob blob : blobs.iterateAll()) {
        System.out.println(blob.getName());
      }
    }
  }
}
// [END storage_list_object_contexts]
