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
package com.google.cloud.storage.jqwik;

import com.google.cloud.storage.HmacKey;
import com.google.storage.v2.HmacKeyMetadata;
import java.util.Collections;
import java.util.Set;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;
import net.jqwik.web.api.Web;

public class HmacKeyMetadataArbitraryProvider implements ArbitraryProvider {
  @Override
  public boolean canProvideFor(TypeUsage targetType) {
    return targetType.isOfType(HmacKeyMetadata.class);
  }

  @Override
  public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {

    Arbitrary<String> accessID =
        Arbitraries.strings().withCharRange('a', 'z').numeric().ofLength(61);

    Arbitrary<HmacKeyMetadata> as =
        Combinators.combine(
                accessID,
                StorageArbitraries.projectID(),
                Arbitraries.of(HmacKey.HmacKeyState.class),
                StorageArbitraries.timestamp(),
                StorageArbitraries.timestamp(),
                Web.emails())
            .as(
                (accessId, projectID, state, createTime, updateTime, email) ->
                    HmacKeyMetadata.newBuilder()
                        .setAccessId(accessId)
                        .setProject(projectID.get())
                        .setId(projectID.get() + "/" + accessId)
                        .setState(state.toString())
                        .setCreateTime(createTime)
                        .setUpdateTime(updateTime)
                        .setServiceAccountEmail(email)
                        .build());
    return Collections.singleton(as);
  }
}
