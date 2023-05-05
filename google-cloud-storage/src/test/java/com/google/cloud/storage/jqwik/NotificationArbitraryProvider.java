/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.storage.jqwik;

import static com.google.cloud.storage.PackagePrivateMethodWorkarounds.ifNonNull;

import com.google.pubsub.v1.TopicName;
import com.google.storage.v2.NotificationConfig;
import com.google.storage.v2.NotificationConfigName;
import java.util.Collections;
import java.util.Set;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NotificationArbitraryProvider implements ArbitraryProvider {

  @Override
  public boolean canProvideFor(TypeUsage targetType) {
    return targetType.isOfType(NotificationConfig.class);
  }

  @NonNull
  @Override
  public Set<Arbitrary<?>> provideFor(
      @NonNull TypeUsage targetType, @NonNull SubtypeProvider subtypeProvider) {
    Arbitrary<NotificationConfig> as =
        Combinators.combine(
                notificationName(),
                topic(),
                StorageArbitraries.etag().injectNull(0.5),
                eventTypes(),
                StorageArbitraries.buckets().labels(),
                StorageArbitraries.objects().name().injectNull(0.5),
                Arbitraries.of("JSON_API_V1", "NONE"))
            .as(
                (name, topic, etag, types, customAttributes, prefix, payloadFormat) -> {
                  NotificationConfig.Builder b =
                      NotificationConfig.newBuilder()
                          .setName(name.toString())
                          .setTopic(topic)
                          .setPayloadFormat(payloadFormat);
                  ifNonNull(types, b::addAllEventTypes);
                  ifNonNull(customAttributes, b::putAllCustomAttributes);
                  ifNonNull(etag, b::setEtag);
                  ifNonNull(prefix, b::setObjectNamePrefix);
                  return b.build();
                });
    return Collections.singleton(as);
  }

  private static Arbitrary<Set<String>> eventTypes() {
    return Arbitraries.of(
            "OBJECT_FINALIZE", "OBJECT_METADATA_UPDATE", "OBJECT_DELETE", "OBJECT_ARCHIVE")
        .set()
        .ofMinSize(0)
        .ofMaxSize(4);
  }

  @NonNull
  private static Arbitrary<String> topic() {
    return Combinators.combine(
            StorageArbitraries.projectID(),
            StorageArbitraries.alphaString().ofMinLength(1).ofMaxLength(10))
        .as((p, t) -> TopicName.of(p.get(), t))
        .map(tn -> "//pubsub.googleapis.com/" + tn.toString());
  }

  @NonNull
  private static Arbitrary<NotificationConfigName> notificationName() {
    return Combinators.combine(
            StorageArbitraries.buckets().name(), StorageArbitraries.alphaString().ofMinLength(1))
        .as(
            (bucket, notification) ->
                NotificationConfigName.of(bucket.getProject(), bucket.getBucket(), notification));
  }
}
