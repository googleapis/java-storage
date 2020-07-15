/*
 * Copyright 2020 Google LLC
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

package com.google.cloud.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;

public class NotificationTest {

  private static final String ETAG = "0xFF00";
  private static final String SELF_LINK = "http://storage/b/n";
  private static final Notification.EventType[] EVENT_TYPES = {
    Notification.EventType.OBJECT_FINALIZE, Notification.EventType.OBJECT_METADATA_UPDATE
  };
  private static final String OBJECT_NAME_PREFIX = "index.html";
  private static final Notification.PayloadFormat PAYLOAD_FORMAT =
      Notification.PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final String TOPIC = "projects/myProject/topics/topic1";
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final Notification NOTIFICATION =
      Notification.newBuilder(TOPIC)
          .setEtag(ETAG)
          .setCustomAttributes(CUSTOM_ATTRIBUTES)
          .setSelfLink(SELF_LINK)
          .setEventTypes(EVENT_TYPES)
          .setObjectNamePrefix(OBJECT_NAME_PREFIX)
          .setPayloadFormat(PAYLOAD_FORMAT)
          .build();

  @Test
  public void testToBuilder() {
    compareBucketsNotification(NOTIFICATION, NOTIFICATION.toBuilder().build());
    Notification notification = NOTIFICATION.toBuilder().setTopic(TOPIC).build();
    assertEquals(TOPIC, notification.getTopic());
    notification = notification.toBuilder().setTopic(TOPIC).build();
    compareBucketsNotification(NOTIFICATION, notification);
  }

  @Test
  public void testToBuilderIncomplete() {
    Notification incompleteNotification = Notification.newBuilder(TOPIC).build();
    compareBucketsNotification(incompleteNotification, incompleteNotification.toBuilder().build());
  }

  @Test
  public void testOf() {
    Notification notification = Notification.of(TOPIC);
    assertEquals(TOPIC, notification.getTopic());
    assertNull(notification.getGeneratedId());
    assertNull(notification.getCustomAttributes());
    assertNull(notification.getEtag());
    assertNull(notification.getSelfLink());
    assertNull(notification.getEventTypes());
    assertNull(notification.getObjectNamePrefix());
    assertNull(notification.getPayloadFormat());
  }

  @Test
  public void testBuilder() {
    assertEquals(ETAG, NOTIFICATION.getEtag());
    assertNull(NOTIFICATION.getGeneratedId());
    assertEquals(SELF_LINK, NOTIFICATION.getSelfLink());
    assertEquals(OBJECT_NAME_PREFIX, NOTIFICATION.getObjectNamePrefix());
    assertEquals(PAYLOAD_FORMAT, NOTIFICATION.getPayloadFormat());
    assertEquals(TOPIC, NOTIFICATION.getTopic());
    assertEquals(CUSTOM_ATTRIBUTES, NOTIFICATION.getCustomAttributes());
    assertEquals(Arrays.asList(EVENT_TYPES), NOTIFICATION.getEventTypes());
  }

  @Test
  public void testToPbAndFromPb() {
    compareBucketsNotification(NOTIFICATION, Notification.fromPb(NOTIFICATION.toPb()));
    Notification notification =
        Notification.of(TOPIC)
            .toBuilder()
            .setPayloadFormat(Notification.PayloadFormat.NONE)
            .build();
    compareBucketsNotification(notification, Notification.fromPb(notification.toPb()));
  }

  private void compareBucketsNotification(Notification expected, Notification actual) {
    assertEquals(expected, actual);
    assertEquals(expected.getGeneratedId(), actual.getGeneratedId());
    assertEquals(expected.getCustomAttributes(), actual.getCustomAttributes());
    assertEquals(expected.getEtag(), actual.getEtag());
    assertEquals(expected.getSelfLink(), actual.getSelfLink());
    assertEquals(expected.getEventTypes(), actual.getEventTypes());
    assertEquals(expected.getObjectNamePrefix(), actual.getObjectNamePrefix());
    assertEquals(expected.getPayloadFormat(), actual.getPayloadFormat());
    assertEquals(expected.getTopic(), actual.getTopic());
  }
}
