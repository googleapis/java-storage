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

import com.google.storage.v2.Bucket;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

final class BucketPropertyTest {

  @Property
  void allBucketsDecode(@ForAll Bucket b) {
    BucketInfo decoded = Conversions.grpc().bucketInfo().decode(b);
    assertThat(decoded.getGeneratedId()).isEqualTo(b.getBucketId());
    assertThat(decoded.getName()).isEqualTo(b.getName());
    assertThat(decoded.getMetageneration()).isEqualTo(b.getMetageneration());
    assertThat(decoded.getStorageClass().toString()).isEqualTo(b.getStorageClass());
    assertThat(decoded.getLocation()).isEqualTo(b.getLocation());
    assertThat(decoded.getLocationType()).isEqualTo(b.getLocationType());
    assertThat(decoded.getCreateTime()).isEqualTo(b.getCreateTime().getSeconds());
    assertThat(decoded.getUpdateTime()).isEqualTo(b.getUpdateTime().getSeconds());
    assertThat(decoded.versioningEnabled()).isEqualTo(b.getVersioning().getEnabled());
    assertThat(decoded.getRpo().toString()).isEqualTo(b.getRpo());
    assertThat(decoded.requesterPays()).isEqualTo(b.getBilling().getRequesterPays());
    assertThat(decoded.getDefaultKmsKeyName()).isEqualTo(b.getEncryption().getDefaultKmsKey());
    if (b.getWebsite() != null) {
      assertThat(decoded.getIndexPage()).isEqualTo(b.getWebsite().getMainPageSuffix());
      assertThat(decoded.getNotFoundPage()).isEqualTo(b.getWebsite().getNotFoundPage());
    }
  }
}
