package com.example.storage.bucket;

// [START storage_get_soft_delete_policy]
import com.google.cloud.storage.BucketInfo.SoftDeletePolicy;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Duration;

public class GetSoftDeletePolicy {
  public static void getSoftDeletePolicy(String projectId, String bucketName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    SoftDeletePolicy policy = storage.get(bucketName).getSoftDeletePolicy();

    if (Duration.ofSeconds(0).equals(policy.getRetentionDuration())) {
      System.out.println("Soft delete is disabled for " + bucketName);
    } else {
      System.out.println("The soft delete policy for " + bucketName + " is:");
      System.out.println(policy);
    }
  }
}
// [END storage_get_soft_delete_policy]
