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

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A Google cloud storage notification channel object which could be used to watch the changes on
 * objects present in the {@code Bucket}.
 */
public class NotificationChannel implements Serializable {

  private static final long serialVersionUID = -4712013629621638459L;

  private String kind;
  private String id;
  private String resourceId;
  private String resourceUri;
  private String token;
  private Long expiration;
  private String type;
  private String address;
  private Map<String, String> params;
  private Boolean payload;

  static class Builder {

    private String kind;
    private String id;
    private String resourceId;
    private String resourceUri;
    private String token;
    private Long expiration;
    private String type;
    private String address;
    private Map<String, String> params;
    private Boolean payload;

    Builder() {}

    Builder(NotificationChannel channel) {
      this.kind = channel.kind;
      this.id = channel.id;
      this.resourceId = channel.resourceId;
      this.resourceUri = channel.resourceUri;
      this.token = channel.token;
      this.expiration = channel.expiration;
      this.type = channel.type;
      this.address = channel.address;
      this.params = channel.params;
      this.payload = channel.payload;
    }

    Builder setKind(String kind) {
      this.kind = kind;
      return this;
    }

    Builder setId(String id) {
      this.id = id;
      return this;
    }

    Builder setResourceId(String resourceId) {
      this.resourceId = resourceId;
      return this;
    }

    Builder setResourceUri(String resourceUri) {
      this.resourceUri = resourceUri;
      return this;
    }

    Builder setToken(String token) {
      this.token = token;
      return this;
    }

    Builder setExpiration(Long expiration) {
      this.expiration = expiration;
      return this;
    }

    Builder setType(String type) {
      this.type = type;
      return this;
    }

    Builder setAddress(String address) {
      this.address = address;
      return this;
    }

    Builder setParams(Map<String, String> params) {
      this.params = params;
      return this;
    }

    Builder setPayload(Boolean payload) {
      this.payload = payload;
      return this;
    }

    NotificationChannel build() {
      return new NotificationChannel(this);
    }
  }

  NotificationChannel(Builder builder) {
    this.kind = builder.kind;
    this.id = builder.id;
    this.resourceId = builder.resourceId;
    this.resourceUri = builder.resourceUri;
    this.token = builder.token;
    this.expiration = builder.expiration;
    this.type = builder.type;
    this.address = builder.address;
    this.params = builder.params;
    this.payload = builder.payload;
  }

  /* Identifies this as a notification channel used to watch for changes to a resource*/
  public String getKind() {
    return kind;
  }

  /* Returns a UUID or similar unique string that identifies this channel.*/
  public String getId() {
    return id;
  }

  /* Returns an opaque ID that identifies the resource being watched on this channel.*/
  public String getResourceId() {
    return resourceId;
  }

  /* Returns a version-specific identifier for the watched resource.*/
  public String getResourceUri() {
    return resourceUri;
  }

  /* Returns an arbitrary string delivered to the target address with each notification delivered over the channel.*/
  public String getToken() {
    return token;
  }

  /* Returns the notification channel expiration date and time.*/
  public Long getExpiration() {
    return expiration;
  }

  /* Returns the type of delivery mechanism used for this channel.*/
  public String getType() {
    return type;
  }

  /* Returns the address where notifications are delivered.*/
  public String getAddress() {
    return address;
  }

  /* Returns the newly declared parameter by name.*/
  public Map<String, String> getParams() {
    return params;
  }

  /* Returns a Boolean value to indicate whether payload is wanted.*/
  public Boolean getPayload() {
    return payload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationChannel channel = (NotificationChannel) o;
    return Objects.equals(kind, channel.kind)
        && Objects.equals(id, channel.id)
        && Objects.equals(resourceId, channel.resourceId)
        && Objects.equals(resourceUri, channel.resourceUri)
        && Objects.equals(token, channel.token)
        && Objects.equals(expiration, channel.expiration)
        && Objects.equals(type, channel.type)
        && Objects.equals(address, channel.address)
        && Objects.equals(params, channel.params)
        && Objects.equals(payload, channel.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        kind, id, resourceId, resourceUri, token, expiration, type, address, params, payload);
  }

  /** Returns a builder for the {@link NotificationChannel} object. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Returns a builder for the {@link NotificationChannel} object. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  com.google.api.services.storage.model.Channel toProtobuf() {
    com.google.api.services.storage.model.Channel channel =
        new com.google.api.services.storage.model.Channel();
    if (kind != null) {
      channel.setKind(kind);
    }
    if (id != null) {
      channel.setId(id);
    }
    if (resourceId != null) {
      channel.setResourceId(resourceId);
    }
    if (resourceUri != null) {
      channel.setResourceUri(resourceUri);
    }
    if (token != null) {
      channel.setToken(token);
    }
    if (expiration != null) {
      channel.setExpiration(expiration);
    }
    if (type != null) {
      channel.setType(type);
    }
    if (address != null) {
      channel.setAddress(address);
    }
    if (params != null) {
      channel.setParams(params);
    }
    if (payload != null) {
      channel.setPayload(payload);
    }
    return channel;
  }

  static NotificationChannel fromProtobuf(com.google.api.services.storage.model.Channel channel) {
    Builder builder = newBuilder();
    if (channel.getKind() != null) {
      builder.setKind(channel.getKind());
    }
    if (channel.getId() != null) {
      builder.setId(channel.getId());
    }
    if (channel.getResourceId() != null) {
      builder.setResourceId(channel.getResourceId());
    }
    if (channel.getResourceUri() != null) {
      builder.setResourceUri(channel.getResourceUri());
    }
    if (channel.getToken() != null) {
      builder.setToken(channel.getToken());
    }
    if (channel.getExpiration() != null) {
      builder.setExpiration(channel.getExpiration());
    }
    if (channel.getType() != null) {
      builder.setType(channel.getType());
    }
    if (channel.getAddress() != null) {
      builder.setAddress(channel.getAddress());
    }
    if (channel.getParams() != null) {
      builder.setParams(channel.getParams());
    }
    if (channel.getPayload() != null) {
      builder.setPayload(channel.getPayload());
    }
    return builder.build();
  }
}
