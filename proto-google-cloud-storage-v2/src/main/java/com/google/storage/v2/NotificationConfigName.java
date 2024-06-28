/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.v2;

import com.google.api.pathtemplate.PathTemplate;
import com.google.api.resourcenames.ResourceName;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
@Generated("by gapic-generator-java")
public class NotificationConfigName implements ResourceName {
  private static final PathTemplate PROJECT_BUCKET_NOTIFICATION_CONFIG =
      PathTemplate.createWithoutUrlEncoding(
          "projects/{project}/buckets/{bucket}/notificationConfigs/{notification_config}");
  private volatile Map<String, String> fieldValuesMap;
  private final String project;
  private final String bucket;
  private final String notificationConfig;

  @Deprecated
  protected NotificationConfigName() {
    project = null;
    bucket = null;
    notificationConfig = null;
  }

  private NotificationConfigName(Builder builder) {
    project = Preconditions.checkNotNull(builder.getProject());
    bucket = Preconditions.checkNotNull(builder.getBucket());
    notificationConfig = Preconditions.checkNotNull(builder.getNotificationConfig());
  }

  public String getProject() {
    return project;
  }

  public String getBucket() {
    return bucket;
  }

  public String getNotificationConfig() {
    return notificationConfig;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static NotificationConfigName of(
      String project, String bucket, String notificationConfig) {
    return newBuilder()
        .setProject(project)
        .setBucket(bucket)
        .setNotificationConfig(notificationConfig)
        .build();
  }

  public static String format(String project, String bucket, String notificationConfig) {
    return newBuilder()
        .setProject(project)
        .setBucket(bucket)
        .setNotificationConfig(notificationConfig)
        .build()
        .toString();
  }

  public static NotificationConfigName parse(String formattedString) {
    if (formattedString.isEmpty()) {
      return null;
    }
    Map<String, String> matchMap =
        PROJECT_BUCKET_NOTIFICATION_CONFIG.validatedMatch(
            formattedString, "NotificationConfigName.parse: formattedString not in valid format");
    return of(matchMap.get("project"), matchMap.get("bucket"), matchMap.get("notification_config"));
  }

  public static List<NotificationConfigName> parseList(List<String> formattedStrings) {
    List<NotificationConfigName> list = new ArrayList<>(formattedStrings.size());
    for (String formattedString : formattedStrings) {
      list.add(parse(formattedString));
    }
    return list;
  }

  public static List<String> toStringList(List<NotificationConfigName> values) {
    List<String> list = new ArrayList<>(values.size());
    for (NotificationConfigName value : values) {
      if (value == null) {
        list.add("");
      } else {
        list.add(value.toString());
      }
    }
    return list;
  }

  public static boolean isParsableFrom(String formattedString) {
    return PROJECT_BUCKET_NOTIFICATION_CONFIG.matches(formattedString);
  }

  @Override
  public Map<String, String> getFieldValuesMap() {
    if (fieldValuesMap == null) {
      synchronized (this) {
        if (fieldValuesMap == null) {
          ImmutableMap.Builder<String, String> fieldMapBuilder = ImmutableMap.builder();
          if (project != null) {
            fieldMapBuilder.put("project", project);
          }
          if (bucket != null) {
            fieldMapBuilder.put("bucket", bucket);
          }
          if (notificationConfig != null) {
            fieldMapBuilder.put("notification_config", notificationConfig);
          }
          fieldValuesMap = fieldMapBuilder.build();
        }
      }
    }
    return fieldValuesMap;
  }

  public String getFieldValue(String fieldName) {
    return getFieldValuesMap().get(fieldName);
  }

  @Override
  public String toString() {
    return PROJECT_BUCKET_NOTIFICATION_CONFIG.instantiate(
        "project", project, "bucket", bucket, "notification_config", notificationConfig);
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (o == this) {
      return true;
    }
    if (o != null && getClass() == o.getClass()) {
      NotificationConfigName that = ((NotificationConfigName) o);
      return Objects.equals(this.project, that.project)
          && Objects.equals(this.bucket, that.bucket)
          && Objects.equals(this.notificationConfig, that.notificationConfig);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= Objects.hashCode(project);
    h *= 1000003;
    h ^= Objects.hashCode(bucket);
    h *= 1000003;
    h ^= Objects.hashCode(notificationConfig);
    return h;
  }

  /** Builder for projects/{project}/buckets/{bucket}/notificationConfigs/{notification_config}. */
  public static class Builder {
    private String project;
    private String bucket;
    private String notificationConfig;

    protected Builder() {}

    public String getProject() {
      return project;
    }

    public String getBucket() {
      return bucket;
    }

    public String getNotificationConfig() {
      return notificationConfig;
    }

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setBucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    public Builder setNotificationConfig(String notificationConfig) {
      this.notificationConfig = notificationConfig;
      return this;
    }

    private Builder(NotificationConfigName notificationConfigName) {
      this.project = notificationConfigName.project;
      this.bucket = notificationConfigName.bucket;
      this.notificationConfig = notificationConfigName.notificationConfig;
    }

    public NotificationConfigName build() {
      return new NotificationConfigName(this);
    }
  }
}
