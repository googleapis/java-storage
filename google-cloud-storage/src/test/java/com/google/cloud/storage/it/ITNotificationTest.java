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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableMap;
import com.google.iam.v1.Binding;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.SetIamPolicyRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITNotificationTest {
  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private static final String PROJECT = ServiceOptions.getDefaultProjectId();
  private static final String ID = UUID.randomUUID().toString().substring(0, 8);
  private static final String TOPIC =
      String.format("projects/%s/topics/test_topic_foo_%s", PROJECT, ID).trim();
  private static final Notification.PayloadFormat PAYLOAD_FORMAT =
      Notification.PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final Logger log = Logger.getLogger(ITNotificationTest.class.getName());

  private static Storage storage;
  private static String bucketName;
  private static TopicAdminClient topicAdminClient;

  @BeforeClass
  public static void setup() throws IOException {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
    // Configure topic admin client for notification.
    topicAdminClient = configureTopicAdminClient();
  }

  @AfterClass
  public static void cleanup() {
    /* Delete the Pub/Sub topic */
    if (topicAdminClient != null) {
      try {
        topicAdminClient.deleteTopic(TOPIC);
        topicAdminClient.close();
      } catch (Exception e) {
        log.log(Level.WARNING, "Error while trying to delete topic and shutdown topic client", e);
      }
      topicAdminClient = null;
    }
  }

  private static TopicAdminClient configureTopicAdminClient() throws IOException {
    TopicAdminClient topicAdminClient = TopicAdminClient.create();
    topicAdminClient.createTopic(TOPIC);
    GetIamPolicyRequest getIamPolicyRequest =
        GetIamPolicyRequest.newBuilder().setResource(TOPIC).build();
    com.google.iam.v1.Policy policy = topicAdminClient.getIamPolicy(getIamPolicyRequest);
    Binding binding =
        Binding.newBuilder().setRole("roles/owner").addMembers("allAuthenticatedUsers").build();
    SetIamPolicyRequest setIamPolicyRequest =
        SetIamPolicyRequest.newBuilder()
            .setResource(TOPIC)
            .setPolicy(policy.toBuilder().addBindings(binding).build())
            .build();
    topicAdminClient.setIamPolicy(setIamPolicyRequest);
    return topicAdminClient;
  }

  @Test
  public void testNotification() throws InterruptedException, ExecutionException {
    storage.create(BucketInfo.newBuilder(bucketName).setLocation("us").build());
    NotificationInfo notificationInfo =
        NotificationInfo.newBuilder(TOPIC)
            .setCustomAttributes(CUSTOM_ATTRIBUTES)
            .setPayloadFormat(PAYLOAD_FORMAT)
            .build();
    try {
      assertThat(storage.listNotifications(bucketName)).isEmpty();
      Notification notification = storage.createNotification(bucketName, notificationInfo);
      assertThat(notification.getNotificationId()).isNotNull();
      assertThat(CUSTOM_ATTRIBUTES).isEqualTo(notification.getCustomAttributes());
      assertThat(PAYLOAD_FORMAT.name()).isEqualTo(notification.getPayloadFormat().name());
      assertThat(notification.getTopic().contains(TOPIC)).isTrue();

      // Gets the notification with the specified id.
      Notification actualNotification =
          storage.getNotification(bucketName, notification.getNotificationId());
      assertThat(actualNotification.getNotificationId())
          .isEqualTo(notification.getNotificationId());
      assertThat(actualNotification.getTopic().trim()).isEqualTo(notification.getTopic().trim());
      assertThat(actualNotification.getEtag()).isEqualTo(notification.getEtag());
      assertThat(actualNotification.getEventTypes()).isEqualTo(notification.getEventTypes());
      assertThat(actualNotification.getPayloadFormat()).isEqualTo(notification.getPayloadFormat());
      assertThat(actualNotification.getSelfLink()).isEqualTo(notification.getSelfLink());
      assertThat(actualNotification.getCustomAttributes())
          .isEqualTo(notification.getCustomAttributes());

      // Retrieves the list of notifications associated with the bucket.
      List<Notification> notifications = storage.listNotifications(bucketName);
      assertThat(notifications.size()).isEqualTo(1);
      assertThat(notifications.get(0).getNotificationId())
          .isEqualTo(actualNotification.getNotificationId());

      // Deletes the notification with the specified id.
      assertThat(storage.deleteNotification(bucketName, notification.getNotificationId())).isTrue();
      assertThat(storage.deleteNotification(bucketName, notification.getNotificationId()))
          .isFalse();
      assertThat(storage.getNotification(bucketName, notification.getNotificationId())).isNull();
      assertThat(storage.listNotifications(bucketName)).isEmpty();
    } finally {
      RemoteStorageHelper.forceDelete(storage, bucketName, 5, TimeUnit.SECONDS);
    }
  }
}
