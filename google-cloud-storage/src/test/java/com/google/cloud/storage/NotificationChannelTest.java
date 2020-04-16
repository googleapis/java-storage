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

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NotificationChannelTest {

  private static final String KIND = "api#channel";
  private static final String ID = "ChannelId-" + UUID.randomUUID().toString();
  private static final String RESOURCE_ID = "ResourceId-" + UUID.randomUUID().toString();
  private static final String RESOURCE_URI =
      "https://storage.googleapis.com/storage/v1/b/BucketName/o?alt=json";
  private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
  private static final Long EXPIRATION = 30000L;
  private static final String TYPE = "WEBHOOK";
  private static final String ADDRESS = "channel address";
  private static final Map<String, String> PARAMS = ImmutableMap.of("field", "value");
  private static final Boolean PAYLOAD = Boolean.TRUE;
  private static final NotificationChannel CHANNEL =
      NotificationChannel.newBuilder()
          .setKind(KIND)
          .setId(ID)
          .setResourceId(RESOURCE_ID)
          .setResourceUri(RESOURCE_URI)
          .setToken(TOKEN)
          .setExpiration(EXPIRATION)
          .setType(TYPE)
          .setAddress(ADDRESS)
          .setParams(PARAMS)
          .setPayload(PAYLOAD)
          .build();

  @Test
  public void testBuilder() {
    assertEquals(KIND, CHANNEL.getKind());
    assertEquals(ID, CHANNEL.getId());
    assertEquals(RESOURCE_ID, CHANNEL.getResourceId());
    assertEquals(RESOURCE_URI, CHANNEL.getResourceUri());
    assertEquals(TOKEN, CHANNEL.getToken());
    assertEquals(EXPIRATION, CHANNEL.getExpiration());
    assertEquals(TYPE, CHANNEL.getType());
    assertEquals(ADDRESS, CHANNEL.getAddress());
    assertEquals(PARAMS, CHANNEL.getParams());
    assertEquals(PAYLOAD, CHANNEL.getPayload());
  }

  @Test
  public void testToBuilder() {
    assertEquals(CHANNEL, CHANNEL.toBuilder().build());
    NotificationChannel channel =
        CHANNEL
            .toBuilder()
            .setKind("kind")
            .setId("channelId-123456")
            .setResourceId("resourceId-123456")
            .setResourceUri("resourceUri")
            .setType("type")
            .setPayload(Boolean.FALSE)
            .build();
    assertEquals("kind", channel.getKind());
    assertEquals("channelId-123456", channel.getId());
    assertEquals("resourceId-123456", channel.getResourceId());
    assertEquals("resourceUri", channel.getResourceUri());
    assertEquals("type", channel.getType());
    assertEquals(Boolean.FALSE, channel.getPayload());
  }

  @Test
  public void testToAndFromProtobuf() {
    assertEquals(CHANNEL, NotificationChannel.fromProtobuf(CHANNEL.toProtobuf()));
  }
}
