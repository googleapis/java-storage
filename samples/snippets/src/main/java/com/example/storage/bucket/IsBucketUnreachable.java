// [START storage_is_bucket_unreachable]
package com.example.storage.bucket;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class IsBucketUnreachable {
    public static void isBucketUnreachable(String projectId, String bucketName) {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The name of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Bucket bucket = storage.get(bucketName);

        if (bucket != null) {
            boolean isUnreachable = bucket.isUnreachable();
            System.out.println("Bucket " + bucketName + " is unreachable: " + isUnreachable);
        } else {
            System.out.println("Bucket " + bucketName + " not found.");
        }
    }
}
// [END storage_is_bucket_unreachable]
