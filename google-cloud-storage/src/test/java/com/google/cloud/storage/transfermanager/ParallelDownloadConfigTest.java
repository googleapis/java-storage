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

package com.google.cloud.storage.transfermanager;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

public final class ParallelDownloadConfigTest {

  private static final String BUCKET_NAME = "test-bucket";
  private static final String STRIP_PREFIX = "prefix/";
  private static final Path DOWNLOAD_DIRECTORY = Paths.get("/tmp/downloads");
  private static final List<BlobSourceOption> OPTIONS = ImmutableList.of(BlobSourceOption.generationMatch(1L));

  @Test
  public void testBuilder() {
    ParallelDownloadConfig config = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .setStripPrefix(STRIP_PREFIX)
        .setDownloadDirectory(DOWNLOAD_DIRECTORY)
        .setOptionsPerRequest(OPTIONS)
        .setSkipIfExists(true)
        .build();

    assertThat(config.getBucketName()).isEqualTo(BUCKET_NAME);
    assertThat(config.getStripPrefix()).isEqualTo(STRIP_PREFIX);
    assertThat(config.getDownloadDirectory()).isEqualTo(DOWNLOAD_DIRECTORY.toAbsolutePath().normalize());
    assertThat(config.getOptionsPerRequest()).isEqualTo(OPTIONS);
    assertThat(config.isSkipIfExists()).isTrue();
  }

  @Test
  public void testDefaultSkipIfExists() {
    ParallelDownloadConfig config = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .build();

    assertThat(config.isSkipIfExists()).isFalse();
  }

  @Test
  public void testEqualsAndHashCode() {
    ParallelDownloadConfig config1 = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .setSkipIfExists(true)
        .build();
    ParallelDownloadConfig config2 = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .setSkipIfExists(true)
        .build();
    ParallelDownloadConfig config3 = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .setSkipIfExists(false)
        .build();

    assertThat(config1).isEqualTo(config2);
    assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    assertThat(config1).isNotEqualTo(config3);
    assertThat(config1.hashCode()).isNotEqualTo(config3.hashCode());
  }

  @Test
  public void testToString() {
    ParallelDownloadConfig config = ParallelDownloadConfig.newBuilder()
        .setBucketName(BUCKET_NAME)
        .setSkipIfExists(true)
        .build();

    assertThat(config.toString()).contains("skipIfExists=true");
    assertThat(config.toString()).contains("bucketName=" + BUCKET_NAME);
  }
}
