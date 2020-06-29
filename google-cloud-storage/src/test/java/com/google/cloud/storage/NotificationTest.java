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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NotificationTest {

  private static final String ETAG = "0xFF00";
  private static final String GENERATED_ID = "B/N:1";
  private static final String SELF_LINK = "http://storage/b/n";
  private static final List<String> EVENT_TYPES =
      ImmutableList.of("OBJECT_FINALIZE", "OBJECT_METADATA_UPDATE");
  private static final String OBJECT_NAME_PREFIX = "index.html";
  private static final NotificationInfo.PayloadFormat PAYLOAD_FORMAT =
      NotificationInfo.PayloadFormat.JSON_API_V1.JSON_API_V1;
  private static final String TOPIC = "projects/myProject/topics/topic1";
  private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");
  private static final NotificationInfo FULL_NOTIFICATION_INFO =
      NotificationInfo.newBuilder(TOPIC)
          .setEtag(ETAG)
          .setCustomAttributes(CUSTOM_ATTRIBUTES)
          .setSelfLink(SELF_LINK)
          .setEventTypes(EVENT_TYPES)
          .setObjectNamePrefix(OBJECT_NAME_PREFIX)
          .setPayloadFormat(PAYLOAD_FORMAT)
          .setGeneratedId(GENERATED_ID)
          .build();
  private static final NotificationInfo NOTIFICATION_INFO =
      NotificationInfo.newBuilder(TOPIC).build();

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
            .setGeneratedId(GENERATED_ID)
            .build();
    assertEquals(ETAG, notification.getEtag());
    assertEquals(GENERATED_ID, notification.getGeneratedId());
    assertEquals(SELF_LINK, notification.getSelfLink());
    assertEquals(EVENT_TYPES, notification.getEventTypes());
    assertEquals(OBJECT_NAME_PREFIX, notification.getObjectNamePrefix());
    assertEquals(PAYLOAD_FORMAT, notification.getPayloadFormat());
    assertEquals(TOPIC, notification.getTopic());
    assertEquals(CUSTOM_ATTRIBUTES, notification.getCustomAttributes());
  }

  @Test
  public void testToBuilder() {
    expect(storage.getOptions()).andReturn(mockOptions).times(2);
    replay(storage);
    Notification notification =
        new Notification(storage, new NotificationInfo.BuilderImpl(FULL_NOTIFICATION_INFO));
    compareBuckets(notification, notification.toBuilder().build());
  }

  @Test
  public void testFromPb() {
    expect(storage.getOptions()).andReturn(mockOptions).times(1);
    replay(storage);
    compareBuckets(
        FULL_NOTIFICATION_INFO, Notification.fromPb(storage, FULL_NOTIFICATION_INFO.toPb()));
  }

  private void compareBuckets(NotificationInfo expected, NotificationInfo value) {
    assertEquals(expected.getGeneratedId(), value.getGeneratedId());
    assertEquals(expected.getCustomAttributes(), value.getCustomAttributes());
    assertEquals(expected.getEtag(), value.getEtag());
    assertEquals(expected.getSelfLink(), value.getSelfLink());
    assertEquals(expected.getEventTypes(), value.getEventTypes());
    assertEquals(expected.getObjectNamePrefix(), value.getObjectNamePrefix());
    assertEquals(expected.getPayloadFormat(), value.getPayloadFormat());
    assertEquals(expected.getTopic().trim(), value.getTopic().trim());
  }
}
