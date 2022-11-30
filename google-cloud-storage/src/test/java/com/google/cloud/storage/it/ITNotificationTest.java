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

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.common.collect.ImmutableMap;
import com.google.iam.v1.Binding;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.SetIamPolicyRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP},
    backends = {Backend.PROD})
public class ITNotificationTest {
  private static final Notification.PayloadFormat PAYLOAD_FORMAT =
      Notification.PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final Logger log = Logger.getLogger(ITNotificationTest.class.getName());

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;

  private TopicAdminClient topicAdminClient;
  private String topic;

  @Before
  public void setup() throws IOException {
    // Configure topic admin client for notification.
    topicAdminClient = TopicAdminClient.create();
    String projectId = storage.getOptions().getProjectId();
    String id = UUID.randomUUID().toString().substring(0, 8);
    topic = String.format("projects/%s/topics/test_topic_foo_%s", projectId, id).trim();

    topicAdminClient.createTopic(this.topic);

    GetIamPolicyRequest getIamPolicyRequest =
        GetIamPolicyRequest.newBuilder().setResource(this.topic).build();

    com.google.iam.v1.Policy policy = topicAdminClient.getIamPolicy(getIamPolicyRequest);

    Binding binding =
        Binding.newBuilder().setRole("roles/owner").addMembers("allAuthenticatedUsers").build();

    SetIamPolicyRequest setIamPolicyRequest =
        SetIamPolicyRequest.newBuilder()
            .setResource(this.topic)
            .setPolicy(policy.toBuilder().addBindings(binding).build())
            .build();
    topicAdminClient.setIamPolicy(setIamPolicyRequest);
  }

  @After
  public void cleanup() {
    /* Delete the Pub/Sub topic */
    if (topicAdminClient != null) {
      try {
        topicAdminClient.deleteTopic(topic);
        topicAdminClient.close();
      } catch (Exception e) {
        log.log(Level.WARNING, "Error while trying to delete topic and shutdown topic client", e);
      }
      topicAdminClient = null;
    }
  }

  @Test
  public void testNotification() {
    NotificationInfo notificationInfo =
        NotificationInfo.newBuilder(topic)
            .setCustomAttributes(CUSTOM_ATTRIBUTES)
            .setPayloadFormat(PAYLOAD_FORMAT)
            .build();
    try {
      assertThat(storage.listNotifications(bucket.getName())).isEmpty();
      Notification notification = storage.createNotification(bucket.getName(), notificationInfo);
      assertThat(notification.getNotificationId()).isNotNull();
      assertThat(CUSTOM_ATTRIBUTES).isEqualTo(notification.getCustomAttributes());
      assertThat(PAYLOAD_FORMAT.name()).isEqualTo(notification.getPayloadFormat().name());
      assertThat(notification.getTopic().contains(topic)).isTrue();

      // Gets the notification with the specified id.
      Notification actualNotification =
          storage.getNotification(bucket.getName(), notification.getNotificationId());
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
      List<Notification> notifications = storage.listNotifications(bucket.getName());
      assertThat(notifications.size()).isEqualTo(1);
      assertThat(notifications.get(0).getNotificationId())
          .isEqualTo(actualNotification.getNotificationId());

      // Deletes the notification with the specified id.
      assertThat(storage.deleteNotification(bucket.getName(), notification.getNotificationId()))
          .isTrue();
      assertThat(storage.deleteNotification(bucket.getName(), notification.getNotificationId()))
          .isFalse();
      assertThat(storage.getNotification(bucket.getName(), notification.getNotificationId()))
          .isNull();
      assertThat(storage.listNotifications(bucket.getName())).isEmpty();
    } finally {
      // delete is taken care of by BucketFixture now
    }
  }
}
