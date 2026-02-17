/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.storage.transfermanager;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.common.collect.ImmutableList;
import java.nio.file.Paths;
import org.junit.Test;

public final class ParallelDownloadConfigTest {

  @Test
  public void testBuilder() {
    ParallelDownloadConfig config =
        ParallelDownloadConfig.newBuilder()
            .setBucketName("bucket")
            .setDownloadDirectory(Paths.get("dir"))
            .setStripPrefix("prefix")
            .setSkipIfExists(true)
            .setOptionsPerRequest(ImmutableList.of(BlobSourceOption.generationMatch(1L)))
            .build();

    assertThat(config.getBucketName()).isEqualTo("bucket");
    assertThat(config.getDownloadDirectory()).isEqualTo(Paths.get("dir"));
    assertThat(config.getStripPrefix()).isEqualTo("prefix");
    assertThat(config.isSkipIfExists()).isTrue();
    assertThat(config.getOptionsPerRequest())
        .containsExactly(BlobSourceOption.generationMatch(1L));
  }

  @Test
  public void testDefaultValues() {
    ParallelDownloadConfig config = ParallelDownloadConfig.newBuilder().setBucketName("bucket").build();

    assertThat(config.isSkipIfExists()).isFalse();
    assertThat(config.getDownloadDirectory()).isEqualTo(Paths.get(""));
    assertThat(config.getStripPrefix()).isEqualTo("");
    assertThat(config.getOptionsPerRequest()).isEmpty();
  }

  @Test
  public void testEqualsAndHashCode() {
    ParallelDownloadConfig config1 =
        ParallelDownloadConfig.newBuilder()
            .setBucketName("bucket")
            .setSkipIfExists(true)
            .build();
    ParallelDownloadConfig config2 =
        ParallelDownloadConfig.newBuilder()
            .setBucketName("bucket")
            .setSkipIfExists(true)
            .build();
    ParallelDownloadConfig config3 =
        ParallelDownloadConfig.newBuilder()
            .setBucketName("bucket")
            .setSkipIfExists(false)
            .build();

    assertThat(config1).isEqualTo(config2);
    assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    assertThat(config1).isNotEqualTo(config3);
    assertThat(config1.hashCode()).isNotEqualTo(config3.hashCode());
  }
}
