/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/storage/v2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.storage.v2;

public interface NotificationConfigOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.storage.v2.NotificationConfig)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The resource name of this NotificationConfig.
   * Format:
   * `projects/{project}/buckets/{bucket}/notificationConfigs/{notificationConfig}`
   * The `{project}` portion may be `_` for globally unique buckets.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The name.
   */
  java.lang.String getName();
  /**
   *
   *
   * <pre>
   * Required. The resource name of this NotificationConfig.
   * Format:
   * `projects/{project}/buckets/{bucket}/notificationConfigs/{notificationConfig}`
   * The `{project}` portion may be `_` for globally unique buckets.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * Required. The Pub/Sub topic to which this subscription publishes. Formatted
   * as:
   * '//pubsub.googleapis.com/projects/{project-identifier}/topics/{my-topic}'
   * </pre>
   *
   * <code>string topic = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The topic.
   */
  java.lang.String getTopic();
  /**
   *
   *
   * <pre>
   * Required. The Pub/Sub topic to which this subscription publishes. Formatted
   * as:
   * '//pubsub.googleapis.com/projects/{project-identifier}/topics/{my-topic}'
   * </pre>
   *
   * <code>string topic = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for topic.
   */
  com.google.protobuf.ByteString getTopicBytes();

  /**
   *
   *
   * <pre>
   * The etag of the NotificationConfig.
   * If included in the metadata of GetNotificationConfigRequest, the operation
   * will only be performed if the etag matches that of the NotificationConfig.
   * </pre>
   *
   * <code>string etag = 7;</code>
   *
   * @return The etag.
   */
  java.lang.String getEtag();
  /**
   *
   *
   * <pre>
   * The etag of the NotificationConfig.
   * If included in the metadata of GetNotificationConfigRequest, the operation
   * will only be performed if the etag matches that of the NotificationConfig.
   * </pre>
   *
   * <code>string etag = 7;</code>
   *
   * @return The bytes for etag.
   */
  com.google.protobuf.ByteString getEtagBytes();

  /**
   *
   *
   * <pre>
   * If present, only send notifications about listed event types. If
   * empty, sent notifications for all event types.
   * </pre>
   *
   * <code>repeated string event_types = 3;</code>
   *
   * @return A list containing the eventTypes.
   */
  java.util.List<java.lang.String> getEventTypesList();
  /**
   *
   *
   * <pre>
   * If present, only send notifications about listed event types. If
   * empty, sent notifications for all event types.
   * </pre>
   *
   * <code>repeated string event_types = 3;</code>
   *
   * @return The count of eventTypes.
   */
  int getEventTypesCount();
  /**
   *
   *
   * <pre>
   * If present, only send notifications about listed event types. If
   * empty, sent notifications for all event types.
   * </pre>
   *
   * <code>repeated string event_types = 3;</code>
   *
   * @param index The index of the element to return.
   * @return The eventTypes at the given index.
   */
  java.lang.String getEventTypes(int index);
  /**
   *
   *
   * <pre>
   * If present, only send notifications about listed event types. If
   * empty, sent notifications for all event types.
   * </pre>
   *
   * <code>repeated string event_types = 3;</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the eventTypes at the given index.
   */
  com.google.protobuf.ByteString getEventTypesBytes(int index);

  /**
   *
   *
   * <pre>
   * A list of additional attributes to attach to each Pub/Sub
   * message published for this NotificationConfig.
   * </pre>
   *
   * <code>map&lt;string, string&gt; custom_attributes = 4;</code>
   */
  int getCustomAttributesCount();
  /**
   *
   *
   * <pre>
   * A list of additional attributes to attach to each Pub/Sub
   * message published for this NotificationConfig.
   * </pre>
   *
   * <code>map&lt;string, string&gt; custom_attributes = 4;</code>
   */
  boolean containsCustomAttributes(java.lang.String key);
  /** Use {@link #getCustomAttributesMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getCustomAttributes();
  /**
   *
   *
   * <pre>
   * A list of additional attributes to attach to each Pub/Sub
   * message published for this NotificationConfig.
   * </pre>
   *
   * <code>map&lt;string, string&gt; custom_attributes = 4;</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getCustomAttributesMap();
  /**
   *
   *
   * <pre>
   * A list of additional attributes to attach to each Pub/Sub
   * message published for this NotificationConfig.
   * </pre>
   *
   * <code>map&lt;string, string&gt; custom_attributes = 4;</code>
   */
  /* nullable */
  java.lang.String getCustomAttributesOrDefault(
      java.lang.String key,
      /* nullable */
      java.lang.String defaultValue);
  /**
   *
   *
   * <pre>
   * A list of additional attributes to attach to each Pub/Sub
   * message published for this NotificationConfig.
   * </pre>
   *
   * <code>map&lt;string, string&gt; custom_attributes = 4;</code>
   */
  java.lang.String getCustomAttributesOrThrow(java.lang.String key);

  /**
   *
   *
   * <pre>
   * If present, only apply this NotificationConfig to object names that
   * begin with this prefix.
   * </pre>
   *
   * <code>string object_name_prefix = 5;</code>
   *
   * @return The objectNamePrefix.
   */
  java.lang.String getObjectNamePrefix();
  /**
   *
   *
   * <pre>
   * If present, only apply this NotificationConfig to object names that
   * begin with this prefix.
   * </pre>
   *
   * <code>string object_name_prefix = 5;</code>
   *
   * @return The bytes for objectNamePrefix.
   */
  com.google.protobuf.ByteString getObjectNamePrefixBytes();

  /**
   *
   *
   * <pre>
   * Required. The desired content of the Payload.
   * </pre>
   *
   * <code>string payload_format = 6 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The payloadFormat.
   */
  java.lang.String getPayloadFormat();
  /**
   *
   *
   * <pre>
   * Required. The desired content of the Payload.
   * </pre>
   *
   * <code>string payload_format = 6 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for payloadFormat.
   */
  com.google.protobuf.ByteString getPayloadFormatBytes();
}
