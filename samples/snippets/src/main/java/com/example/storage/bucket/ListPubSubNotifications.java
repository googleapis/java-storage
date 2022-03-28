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

// [START storage_list_bucket_notifications]

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.List;


public class ListPubSubNotifications {

  public static void listPubSubNotifications(String bucketName) {
    // The ID to give your GCS bucket
    // String bucketName = "your-unique-bucket-name";
    Storage storage = StorageOptions.newBuilder().build().getService();
    List<Notification> notificationList = storage.listNotifications(bucketName);
    for (Notification notification : notificationList) {
      System.out.println(
          "Found notification " + notification.getTopic() + " for bucket " + bucketName);
    }
  }
}
// [END storage_list_bucket_notifications]