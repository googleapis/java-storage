package com.example.storage.object;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo.Retention;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;


import static java.time.OffsetDateTime.now;

// [START storage_set_object_retention_policy]
public class SetObjectRetentionPolicy {
    public static void setObjectRetentionPolicy(String projectId, String bucketName, String objectName)
            throws StorageException {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket that has object retention enabled
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("The object " + objectName + " was not found in " + bucketName);
            return;
        }

        Blob updated = blob.toBuilder().setRetention(Retention.newBuilder()
                .setMode(Retention.Mode.UNLOCKED)
                .setRetainUntilTime(now().plusDays(10))
                .build())
                .build()
                .update();

        System.out.println("Retention policy for object " + objectName + " was set to:");
        System.out.println(updated.getRetention().toString());

        // To modify an existing policy on an Unlocked object, pass in the override parameter
        blob.toBuilder().setRetention(updated.getRetention()
                .toBuilder().setRetainUntilTime(now().plusDays(9)).build())
                .build()
                .update(Storage.BlobTargetOption.overrideUnlockedRetention(true));


        System.out.println("Retention policy for object " + objectName + " was updated to:");
        System.out.println(storage.get(blobId).getRetention().toString());
    }
}

// [START storage_set_object_retention_policy]
