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

package com.google.cloud.storage.it;

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.TestUtils;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.PROD},
    transports = {Transport.HTTP, Transport.GRPC})
public final class ITAutomaticGzipDecompressionTest {

  private static final byte[] helloWorldTextBytes = "hello world".getBytes();
  private static final byte[] helloWorldGzipBytes = TestUtils.gzipBytes(helloWorldTextBytes);

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  private BlobInfo info;
  private BlobId blobId;

  @Before
  public void setUp() throws Exception {
    BlobInfo tmp =
        BlobInfo.newBuilder(bucket, generator.randomObjectName())
            // define an object with explicit content type and encoding.
            // JSON and gRPC have differing default behavior returning these values if they are
            // either undefined, or match HTTP defaults.
            .setContentType("text/plain")
            .setContentEncoding("gzip")
            .build();

    Blob gen1 = storage.create(tmp, helloWorldGzipBytes, BlobTargetOption.doesNotExist());
    info = gen1.asBlobInfo();
    blobId = info.getBlobId();
  }

  @Test
  public void readAllBytes_default_uncompressed() {
    byte[] bytes = storage.readAllBytes(blobId);
    assertThat(xxd(bytes)).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void readAllBytes_returnRawInputStream_yes() {
    byte[] bytes = storage.readAllBytes(blobId, BlobSourceOption.shouldReturnRawInputStream(true));
    assertThat(xxd(bytes)).isEqualTo(xxd(helloWorldGzipBytes));
  }

  @Test
  public void readAllBytes_returnRawInputStream_no() {
    byte[] bytes = storage.readAllBytes(blobId, BlobSourceOption.shouldReturnRawInputStream(false));
    assertThat(xxd(bytes)).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void reader_default_compressed() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ReadChannel r = storage.reader(blobId)) {
      WritableByteChannel w = Channels.newChannel(baos);
      ByteStreams.copy(r, w);
    }

    assertThat(xxd(baos.toByteArray())).isEqualTo(xxd(helloWorldGzipBytes));
  }

  @Test
  public void reader_returnRawInputStream_yes() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ReadChannel r =
        storage.reader(blobId, BlobSourceOption.shouldReturnRawInputStream(true))) {
      WritableByteChannel w = Channels.newChannel(baos);
      ByteStreams.copy(r, w);
    }

    assertThat(xxd(baos.toByteArray())).isEqualTo(xxd(helloWorldGzipBytes));
  }

  @Test
  public void reader_returnRawInputStream_no() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ReadChannel r =
        storage.reader(blobId, BlobSourceOption.shouldReturnRawInputStream(false))) {
      WritableByteChannel w = Channels.newChannel(baos);
      ByteStreams.copy(r, w);
    }

    assertThat(xxd(baos.toByteArray())).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void downloadTo_path_default_uncompressed() throws IOException {
    Path helloWorldTxtGz = File.createTempFile(blobId.getName(), ".txt.gz").toPath();
    storage.downloadTo(blobId, helloWorldTxtGz);

    byte[] actualTxtGzBytes = Files.readAllBytes(helloWorldTxtGz);
    assertThat(xxd(actualTxtGzBytes)).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void downloadTo_path_returnRawInputStream_yes() throws IOException {
    Path helloWorldTxtGz = File.createTempFile(blobId.getName(), ".txt.gz").toPath();
    storage.downloadTo(blobId, helloWorldTxtGz, BlobSourceOption.shouldReturnRawInputStream(true));

    byte[] actualTxtGzBytes = Files.readAllBytes(helloWorldTxtGz);
    assertThat(xxd(actualTxtGzBytes)).isEqualTo(xxd(helloWorldGzipBytes));
  }

  @Test
  public void downloadTo_path_returnRawInputStream_no() throws IOException {
    Path helloWorldTxt = File.createTempFile(blobId.getName(), ".txt").toPath();
    storage.downloadTo(blobId, helloWorldTxt, BlobSourceOption.shouldReturnRawInputStream(false));
    byte[] actualTxtBytes = Files.readAllBytes(helloWorldTxt);
    assertThat(xxd(actualTxtBytes)).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void downloadTo_outputStream_default_uncompressed() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    storage.downloadTo(blobId, baos);
    byte[] actual = baos.toByteArray();
    assertThat(xxd(actual)).isEqualTo(xxd(helloWorldTextBytes));
  }

  @Test
  public void downloadTo_outputStream_returnRawInputStream_yes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    storage.downloadTo(blobId, baos, BlobSourceOption.shouldReturnRawInputStream(true));
    byte[] actual = baos.toByteArray();
    assertThat(xxd(actual)).isEqualTo(xxd(helloWorldGzipBytes));
  }

  @Test
  public void downloadTo_outputStream_returnRawInputStream_no() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    storage.downloadTo(blobId, baos, BlobSourceOption.shouldReturnRawInputStream(false));
    byte[] actual = baos.toByteArray();
    assertThat(xxd(actual)).isEqualTo(xxd(helloWorldTextBytes));
  }
}
