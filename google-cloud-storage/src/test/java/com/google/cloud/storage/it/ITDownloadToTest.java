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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;

public final class ITDownloadToTest {

  private static final String BUCKET = RemoteStorageHelper.generateBucketName();
  private static final byte[] helloWorldTextBytes = "hello world".getBytes();
  private static final byte[] helloWorldGzipBytes = gzipBytes(helloWorldTextBytes);

  private static Storage storage;
  private static BlobId blobId;

  @BeforeClass
  public static void beforeClass() {
    BucketInfo bucketInfo = BucketInfo.of(BUCKET);
    blobId = BlobId.of(BUCKET, "zipped_blob");

    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentEncoding("gzip")
            .setContentType("text/plain")
            .build();

    storage = StorageOptions.newBuilder().build().getService();
    storage.create(bucketInfo);
    storage.create(blobInfo, helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_yes() throws IOException {
    Path helloWorldTxtGz = File.createTempFile("helloWorld", ".txt.gz").toPath();
    storage.downloadTo(blobId, helloWorldTxtGz, Storage.BlobSourceOption.shouldReturnRawInputStream(true));

    byte[] actualTxtGzBytes = Files.readAllBytes(helloWorldTxtGz);
    if (Arrays.equals(actualTxtGzBytes, helloWorldTextBytes)) {
      fail("expected gzipped bytes, but got un-gzipped bytes");
    }
    assertThat(actualTxtGzBytes).isEqualTo(helloWorldGzipBytes);
  }

  @Test
  public void downloadTo_returnRawInputStream_no() throws IOException {
    Path helloWorldTxt = File.createTempFile("helloWorld", ".txt").toPath();
    storage.downloadTo(blobId, helloWorldTxt, Storage.BlobSourceOption.shouldReturnRawInputStream(false));
    byte[] actualTxtBytes = Files.readAllBytes(helloWorldTxt);
    assertThat(actualTxtBytes).isEqualTo(helloWorldTextBytes);
  }

  private static byte[] gzipBytes(byte[] bytes) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (OutputStream out = new GZIPOutputStream(byteArrayOutputStream)) {
      out.write(bytes);
    } catch (IOException ignore) {}

    return byteArrayOutputStream.toByteArray();
  }

}
