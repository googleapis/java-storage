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

package com.google.cloud.storage;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public final class ZOptTest {

  @Test
  public void happyPath() {
    AtomicReference<String> ref1 = new AtomicReference<>();
    AtomicLong ref2 = new AtomicLong(Long.MAX_VALUE);

    ImmutableMap<StorageRpc.Option, ?> map =
        ImmutableMap.of(StorageRpc.Option.PREDEFINED_ACL, "acl");
    ZOpt.PREDEFINED_ACL.consumeVia(ref1::set).apply(map);
    ZOpt.IF_GENERATION_MATCH.consumeVia(ref2::set).apply(map);

    assertThat(ref1.get()).isEqualTo("acl");
    assertThat(ref2.get()).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  public void applyAll() {
    AtomicReference<String> ref1 = new AtomicReference<>();
    AtomicLong ref2 = new AtomicLong(Long.MAX_VALUE);

    ImmutableMap<StorageRpc.Option, ?> map =
        ImmutableMap.of(StorageRpc.Option.PREDEFINED_ACL, "acl");
    ZOpt.applyAll(
        map,
        ZOpt.PREDEFINED_ACL.consumeVia(ref1::set),
        ZOpt.IF_GENERATION_MATCH.consumeVia(ref2::set));

    assertThat(ref1.get()).isEqualTo("acl");
    assertThat(ref2.get()).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  public void aNullValueShouldNotBeProvidedToTheConsumer() {
    ZOpt.PREDEFINED_ACL
        .consumeVia((v) -> fail("null should not be provided"))
        .apply(Collections.emptyMap());
  }

  @Test
  public void ifMapperProducesNullItShouldNotBeProvidedToTheConsumer() {
    ImmutableMap<StorageRpc.Option, ?> map =
        ImmutableMap.of(StorageRpc.Option.PREDEFINED_ACL, "acl");
    ZOpt.PREDEFINED_ACL
        .mapThenConsumeVia((t) -> null, (v) -> fail("null should not be provided"))
        .apply(map);
  }

  @Test
  public void ensureEachTypeWorks() {
    AtomicReference<String> ref1 = new AtomicReference<>();
    AtomicLong ref2 = new AtomicLong(Long.MAX_VALUE);
    AtomicBoolean ref3 = new AtomicBoolean(false);

    ImmutableMap<StorageRpc.Option, ?> map =
        ImmutableMap.of(
            StorageRpc.Option.PREDEFINED_ACL,
            "acl",
            StorageRpc.Option.IF_GENERATION_MATCH,
            0L,
            StorageRpc.Option.IF_DISABLE_GZIP_CONTENT,
            true);
    ZOpt.applyAll(
        map,
        ZOpt.PREDEFINED_ACL.consumeVia(ref1::set),
        ZOpt.IF_GENERATION_MATCH.consumeVia(ref2::set),
        ZOpt.IF_DISABLE_GZIP_CONTENT.consumeVia(ref3::set));

    assertThat(ref1.get()).isEqualTo("acl");
    assertThat(ref2.get()).isEqualTo(0L);
    assertThat(ref3.get()).isTrue();
  }
}
