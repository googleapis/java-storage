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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.pathtemplate.PathTemplate;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Google Storage Notification metadata;
 *
 * @see <a href="https://cloud.google.com/storage/docs/concepts-techniques#concepts">Concepts and
 *     Terminology</a>
 */
public class Notification implements Serializable {

  private static final long serialVersionUID = 5725883368559753810L;
  private static final PathTemplate PATH_TEMPLATE =
      PathTemplate.createWithoutUrlEncoding("projects/{project}/topics/{topic}");

  public enum PayloadFormat {
    JSON_API_V1,
    NONE
  }

  static final Function<com.google.api.services.storage.model.Notification, Notification>
      FROM_PB_FUNCTION =
          new Function<com.google.api.services.storage.model.Notification, Notification>() {
            @Override
            public Notification apply(com.google.api.services.storage.model.Notification pb) {
              return Notification.fromPb(pb);
            }
          };
  static final Function<Notification, com.google.api.services.storage.model.Notification>
      TO_PB_FUNCTION =
          new Function<Notification, com.google.api.services.storage.model.Notification>() {
            @Override
            public com.google.api.services.storage.model.Notification apply(
                Notification notification) {
              return notification.toPb();
            }
          };
  private final String generatedId;
  private final String topic;
  private final List<String> eventTypes;
  private final Map<String, String> customAttributes;
  private final PayloadFormat payloadFormat;
  private final String objectNamePrefix;
  private final String etag;
  private final String selfLink;

  /** Builder for {@code NotificatioInfo}. */
  public static class Builder {

    private String generatedId;
    private String topic;
    private List<String> eventTypes;
    private Map<String, String> customAttributes;
    private PayloadFormat payloadFormat;
    private String objectNamePrefix;
    private String etag;
    private String selfLink;

    private Builder(String topic) {
      this.topic = topic;
    }

    private Builder(Notification notification) {
      generatedId = notification.generatedId;
      etag = notification.etag;
      selfLink = notification.selfLink;
      topic = notification.topic;
      eventTypes = notification.eventTypes;
      customAttributes = notification.customAttributes;
      payloadFormat = notification.payloadFormat;
      objectNamePrefix = notification.objectNamePrefix;
    }

    public Builder setGeneratedId(String generatedId) {
      this.generatedId = generatedId;
      return this;
    }

    public Builder setSelfLink(String selfLink) {
      this.selfLink = selfLink;
      return this;
    }

    /** The name of the topic. It must have the format "projects/{project}/topics/{topic}". */
    public Builder setTopic(String topic) {
      this.topic = topic;
      return this;
    }

    public Builder setPayloadFormat(PayloadFormat payloadFormat) {
      this.payloadFormat = payloadFormat;
      return this;
    }

    public Builder setObjectNamePrefix(String objectNamePrefix) {
      this.objectNamePrefix = objectNamePrefix;
      return this;
    }

    public Builder setEventTypes(Iterable<String> eventTypes) {
      this.eventTypes = eventTypes != null ? ImmutableList.copyOf(eventTypes) : null;
      return this;
    }

    public Builder setEtag(String etag) {
      this.etag = etag;
      return this;
    }

    public Builder setCustomAttributes(Map<String, String> customAttributes) {
      this.customAttributes =
          customAttributes != null ? ImmutableMap.copyOf(customAttributes) : null;
      return this;
    }

    public Notification build() {
      checkNotNull(topic);
      return new Notification(this);
    }
  }

  private Notification(Builder builder) {
    generatedId = builder.generatedId;
    etag = builder.etag;
    selfLink = builder.selfLink;
    topic = builder.topic;
    eventTypes = builder.eventTypes;
    customAttributes = builder.customAttributes;
    payloadFormat = builder.payloadFormat;
    objectNamePrefix = builder.objectNamePrefix;
  }

  /** Returns the service-generated id for the notification. */
  public String getGeneratedId() {
    return generatedId;
  }

  /** Returns the topic to which this subscription publishes. */
  public String getTopic() {
    return topic;
  }

  /** Returns the canonical URI of this topic as a string. */
  public String getSelfLink() {
    return selfLink;
  }

  /** Returns the desired content of the Payload. */
  public PayloadFormat getPayloadFormat() {
    return payloadFormat;
  }

  /** Returns the object name prefix for which this notification configuration applies. */
  public String getObjectNamePrefix() {
    return objectNamePrefix;
  }

  /**
   * Returns HTTP 1.1 Entity tag for the notification.
   *
   * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.11">Entity Tags</a>
   */
  public String getEtag() {
    return etag;
  }

  /**
   * Returns the list of event types that this notification will apply to. If empty, notifications
   * will be sent on all event types.
   *
   * @see <a href="https://cloud.google.com/storage/docs/cross-origin">Cross-Origin Resource Sharing
   *     (CORS)</a>
   */
  public List<String> getEventTypes() {
    return eventTypes;
  }

  /**
   * Returns the list of additional attributes to attach to each Cloud PubSub message published for
   * this notification subscription.
   *
   * @see <a href="https://cloud.google.com/storage/docs/access-control#About-Access-Control-Lists">
   *     About Access Control Lists</a>
   */
  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTopic());
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this
        || obj != null
            && obj.getClass().equals(Notification.class)
            && Objects.equals(toPb(), ((Notification) obj).toPb());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("topic", getTopic()).toString();
  }

  com.google.api.services.storage.model.Notification toPb() {
    com.google.api.services.storage.model.Notification notificationPb =
        new com.google.api.services.storage.model.Notification();
    notificationPb.setId(generatedId);
    notificationPb.setEtag(etag);
    if (customAttributes != null) {
      notificationPb.setCustomAttributes(customAttributes);
    }
    if (eventTypes != null) {
      notificationPb.setEventTypes(eventTypes);
    }
    if (objectNamePrefix != null) {
      notificationPb.setObjectNamePrefix(objectNamePrefix);
    }
    if (payloadFormat != null) {
      notificationPb.setPayloadFormat(payloadFormat.toString());
    } else {
      notificationPb.setPayloadFormat(PayloadFormat.NONE.toString());
    }
    notificationPb.setSelfLink(selfLink);
    notificationPb.setTopic(topic);

    return notificationPb;
  }

  /**
   * Creates a {@code Notification} object for the provided topic name.
   *
   * @param topic The name of the topic. It must have the format
   *     "projects/{project}/topics/{topic}".
   */
  public static Notification of(String topic) {
    PATH_TEMPLATE.validatedMatch(topic, "topic name must be in valid format");
    return newBuilder(topic).build();
  }

  /** Returns a builder for the current notification. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  /**
   * Returns a {@code Notification} builder where the topic's name is set to the provided name.
   *
   * @param topic The name of the topic. It must have the format
   *     "projects/{project}/topics/{topic}".
   */
  public static Builder newBuilder(String topic) {
    PATH_TEMPLATE.validatedMatch(topic, "topic name must be in valid format");
    return new Builder(topic);
  }

  static Notification fromPb(com.google.api.services.storage.model.Notification notificationPb) {
    Builder builder = newBuilder(notificationPb.getTopic());
    if (notificationPb.getId() != null) {
      builder.setGeneratedId(notificationPb.getId());
    }
    if (notificationPb.getEtag() != null) {
      builder.setEtag(notificationPb.getEtag());
    }
    if (notificationPb.getCustomAttributes() != null) {
      builder.setCustomAttributes(notificationPb.getCustomAttributes());
    }
    if (notificationPb.getSelfLink() != null) {
      builder.setSelfLink(notificationPb.getSelfLink());
    }
    if (notificationPb.getObjectNamePrefix() != null) {
      builder.setObjectNamePrefix(notificationPb.getObjectNamePrefix());
    }
    if (notificationPb.getTopic() != null) {
      builder.setTopic(notificationPb.getTopic());
    }
    if (notificationPb.getEventTypes() != null) {
      builder.setEventTypes(notificationPb.getEventTypes());
    }
    if (notificationPb.getPayloadFormat() != null) {
      builder.setPayloadFormat(PayloadFormat.valueOf(notificationPb.getPayloadFormat()));
    }
    return builder.build();
  }
}
