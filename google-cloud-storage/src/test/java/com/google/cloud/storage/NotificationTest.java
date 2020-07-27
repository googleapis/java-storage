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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.google.cloud.storage.NotificationInfo.EventType;
import com.google.cloud.storage.NotificationInfo.PayloadFormat;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NotificationTest {

  private static final String ETAG = "0xFF00";
  private static final String SELF_LINK = "http://storage/b/n";
  private static final String OBJECT_NAME_PREFIX = "index.html";
  private static final String TOPIC = "projects/myProject/topics/topic1";
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final PayloadFormat PAYLOAD_FORMAT = PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final EventType[] EVENT_TYPES = {
    EventType.OBJECT_FINALIZE, EventType.OBJECT_METADATA_UPDATE
  };
  private static final NotificationInfo NOTIFICATION_INFO =
      NotificationInfo.newBuilder(TOPIC)
          .setEtag(ETAG)
          .setCustomAttributes(CUSTOM_ATTRIBUTES)
          .setSelfLink(SELF_LINK)
          .setEventTypes(EVENT_TYPES)
          .setObjectNamePrefix(OBJECT_NAME_PREFIX)
          .setPayloadFormat(PAYLOAD_FORMAT)
          .build();

  private Storage storage;
  private StorageOptions mockOptions = createMock(StorageOptions.class);

  @Before
  public void setUp() {
    storage = createStrictMock(Storage.class);
  }

  @After
  public void tearDown() {
    verify(storage);
  }

  @Test
  public void testBuilder() {
    expect(storage.getOptions()).andReturn(mockOptions).times(2);
    replay(storage);
    Notification.Builder builder =
        new Notification.Builder(
            new Notification(storage, new NotificationInfo.BuilderImpl(NOTIFICATION_INFO)));
    Notification notification =
        builder
            .setEtag(ETAG)
            .setCustomAttributes(CUSTOM_ATTRIBUTES)
            .setSelfLink(SELF_LINK)
            .setEventTypes(EVENT_TYPES)
            .setObjectNamePrefix(OBJECT_NAME_PREFIX)
            .setPayloadFormat(PAYLOAD_FORMAT)
            .build();
    assertEquals(ETAG, notification.getEtag());
    assertEquals(SELF_LINK, notification.getSelfLink());
    assertEquals(OBJECT_NAME_PREFIX, notification.getObjectNamePrefix());
    assertEquals(PAYLOAD_FORMAT, notification.getPayloadFormat());
    assertEquals(TOPIC, notification.getTopic());
    assertEquals(CUSTOM_ATTRIBUTES, notification.getCustomAttributes());
    assertEquals(Arrays.asList(EVENT_TYPES), notification.getEventTypes());
  }

  @Test
  public void testToBuilder() {
    expect(storage.getOptions()).andReturn(mockOptions).times(2);
    replay(storage);
    Notification notification =
        new Notification(storage, new NotificationInfo.BuilderImpl(NOTIFICATION_INFO));
    compareBucketNotification(notification, notification.toBuilder().build());
  }

  @Test
  public void testFromPb() {
    expect(storage.getOptions()).andReturn(mockOptions).times(1);
    replay(storage);
    compareBucketNotification(
        NOTIFICATION_INFO, Notification.fromPb(storage, NOTIFICATION_INFO.toPb()));
  }

  private void compareBucketNotification(NotificationInfo expected, NotificationInfo actual) {
    assertEquals(expected.getNotificationId(), actual.getNotificationId());
    assertEquals(expected.getCustomAttributes(), actual.getCustomAttributes());
    assertEquals(expected.getEtag(), actual.getEtag());
    assertEquals(expected.getSelfLink(), actual.getSelfLink());
    assertEquals(expected.getEventTypes(), actual.getEventTypes());
    assertEquals(expected.getObjectNamePrefix(), actual.getObjectNamePrefix());
    assertEquals(expected.getPayloadFormat(), actual.getPayloadFormat());
    assertEquals(expected.getTopic().trim(), actual.getTopic().trim());
  }
}
