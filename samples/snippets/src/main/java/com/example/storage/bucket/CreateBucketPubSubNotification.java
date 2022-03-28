/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.storage.bucket;

// [START storage_create_bucket_notifications]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;


public class CreateBucketPubSubNotification {

  public static void createBucketPubSubNotification(String bucketName, NotificationInfo notificationInfo) {
    // The ID to give your GCS bucket
    // String bucketName = "your-unique-bucket-name";
    // The NotificationInfo for the notification you would like to create
    // See: https://cloud.google.com/java/docs/reference/google-cloud-notification/latest/com.google.cloud.notification.NotificationInfo
    Storage storage = StorageOptions.newBuilder().build().getService();
    Notification notification = storage.createNotification(bucketName, notificationInfo);
    System.out.println("Successfully created notification for topic " + notification.getTopic());
  }
}
// [END storage_create_bucket_notifications]