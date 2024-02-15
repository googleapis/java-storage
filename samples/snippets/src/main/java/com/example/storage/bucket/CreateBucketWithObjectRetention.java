package com.example.storage.bucket;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

// [START storage_create_bucket_with_object_retention]
public class CreateBucketWithObjectRetention {
    public static void createBucketWithObjectRetention(String projectId, String bucketName) {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID to give your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Bucket bucket = storage.create(BucketInfo.of(bucketName), Storage.BucketTargetOption.enableObjectRetention(true));

        System.out.println(
                "Created bucket "
                        + bucket.getName()
                        + " with object retention enabled setting: "
                        + bucket.getObjectRetention().getMode().toString());
    }
}

// [END storage_create_bucket_with_object_retention]
