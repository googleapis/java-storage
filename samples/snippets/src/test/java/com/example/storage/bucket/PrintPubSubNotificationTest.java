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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import com.example.storage.TestBase;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;
import com.google.common.collect.ImmutableMap;
import com.google.iam.v1.Binding;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.SetIamPolicyRequest;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PrintPubSubNotificationTest extends TestBase {

  private static final Notification.PayloadFormat PAYLOAD_FORMAT =
      Notification.PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final String PROJECT = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String PROJECT_NUMBER = System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER");

  private static final String ID = UUID.randomUUID().toString().substring(0, 8);
  private static final String TOPIC =
      String.format("projects/%s/topics/new-topic-print-%s", PROJECT, ID);
  private static TopicAdminClient topicAdminClient;

  @BeforeClass
  public static void configureTopicAdminClient() throws IOException {
    if (PROJECT != null) {
      topicAdminClient = TopicAdminClient.create();
      topicAdminClient.createTopic(TOPIC);
      GetIamPolicyRequest getIamPolicyRequest =
          GetIamPolicyRequest.newBuilder().setResource(TOPIC).build();
      com.google.iam.v1.Policy policy = topicAdminClient.getIamPolicy(getIamPolicyRequest);
      // For available bindings identities, see
      // https://cloud.google.com/iam/docs/overview#concepts_related_identity
      String member =
          PROJECT_NUMBER != null
              ? "serviceAccount:service-"
                  + PROJECT_NUMBER
                  + "@gs-project-accounts.iam.gserviceaccount.com"
              : "allAuthenticatedUsers";
      Binding binding = Binding.newBuilder().setRole("roles/owner").addMembers(member).build();
      SetIamPolicyRequest setIamPolicyRequest =
          SetIamPolicyRequest.newBuilder()
              .setResource(TOPIC)
              .setPolicy(policy.toBuilder().addBindings(binding).build())
              .build();
      topicAdminClient.setIamPolicy(setIamPolicyRequest);
    }
  }

  @AfterClass
  public static void deleteTopicAndClient() {
    /* Delete the Pub/Sub topic */
    if (topicAdminClient != null) {
      topicAdminClient.deleteTopic(TOPIC);
      topicAdminClient.close();
    }
  }

  @Test
  public void testPrintBucketPubSubNotification() {
    // Check that we can access project value and that topic admin client came up successfully
    assertNotNull("Unable to determine project", PROJECT);
    assertNotNull("Topic Admin Client did not start up", topicAdminClient);

    NotificationInfo notificationInfo =
        NotificationInfo.newBuilder(TOPIC)
            .setCustomAttributes(CUSTOM_ATTRIBUTES)
            .setPayloadFormat(PAYLOAD_FORMAT)
            .build();
    Notification notification = storage.createNotification(bucketName, notificationInfo);
    PrintPubSubNotification.printPubSubNotification(bucketName, notification.getNotificationId());
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains(TOPIC);
  }
}
