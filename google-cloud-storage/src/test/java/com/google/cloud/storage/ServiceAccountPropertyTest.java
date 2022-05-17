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

import static com.google.cloud.storage.JqwikTest.report;
import static com.google.common.truth.Truth.assertThat;
import static net.jqwik.api.providers.TypeUsage.of;

import com.google.cloud.storage.Conversions.Codec;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

final class ServiceAccountPropertyTest {

  @Example
  void edgeCases() {
    report(of(com.google.storage.v2.ServiceAccount.class));
  }

  @Property
  void codecCanRoundTrip(@ForAll com.google.storage.v2.ServiceAccount sa) {
    Codec<ServiceAccount, com.google.storage.v2.ServiceAccount> codec =
        Conversions.grpc().serviceAccount();
    ServiceAccount decode = codec.decode(sa);

    assertThat(decode.getEmail()).isEqualTo(sa.getEmailAddress());

    com.google.storage.v2.ServiceAccount encode = codec.encode(decode);
    assertThat(encode).isEqualTo(sa);
  }
}
