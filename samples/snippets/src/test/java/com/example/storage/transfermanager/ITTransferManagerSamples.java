/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.example.storage.transfermanager;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.it.BucketCleaner;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.cloud.testing.junit4.StdOutCaptureRule;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ITTransferManagerSamples {
  private static final String BUCKET = RemoteStorageHelper.generateBucketName();
  private static Storage storage;
  private static List<BlobInfo> blobs;
  private static List<BlobInfo> bigBlob;
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
  @Rule public final StdOutCaptureRule stdOutCaptureRule = new StdOutCaptureRule();
  @Rule public final TemporaryFolder tmp = new TemporaryFolder();
  @Rule public final TemporaryFolder tmpDirectory = new TemporaryFolder();

  @BeforeClass
  public static void beforeClass() {
    RemoteStorageHelper helper = RemoteStorageHelper.create();
    storage = helper.getOptions().getService();
    storage.create(BucketInfo.of(BUCKET));
    blobs =
        Arrays.asList(
            BlobInfo.newBuilder(BUCKET, "blob1").build(),
            BlobInfo.newBuilder(BUCKET, "blob2").build(),
            BlobInfo.newBuilder(BUCKET, "blob3").build());
    for (BlobInfo blob : blobs) {
      storage.create(blob);
    }
  }

  @AfterClass
  public static void afterClass() throws Exception {
    try (Storage ignore = storage) {
      BucketCleaner.doCleanup(BUCKET, storage);
    }
  }

  @Test
  public void uploadFiles() throws Exception {
    File tmpFile = File.createTempFile("file", ".txt");
    File tmpFile2 = File.createTempFile("file2", ".txt");
    File tmpFile3 = File.createTempFile("file3", ".txt");
    List<Path> files = ImmutableList.of(tmpFile.toPath(), tmpFile2.toPath(), tmpFile3.toPath());
    UploadMany.uploadManyFiles(BUCKET, files);
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("file")).isTrue();
    assertThat(snippetOutput.contains("file2")).isTrue();
    assertThat(snippetOutput.contains("file3")).isTrue();
  }

  @Test
  public void uploadDirectory() throws IOException {
    File tmpFile = tmpDirectory.newFile("fileDirUpload.txt");
    File tmpFile2 = tmpDirectory.newFile("fileDirUpload2.txt");
    File tmpFile3 = tmpDirectory.newFile("fileDirUpload3.txt");
    UploadDirectory.uploadDirectoryContents(BUCKET, tmpDirectory.getRoot().toPath());
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("fileDirUpload.txt")).isTrue();
    assertThat(snippetOutput.contains("fileDirUpload2.txt")).isTrue();
    assertThat(snippetOutput.contains("fileDirUpload3.txt")).isTrue();
  }

  @Test
  public void downloadBucket() {
    String downloadFullBucketName = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.of(downloadFullBucketName));
    List<BlobInfo> bucketBlobs =
        Arrays.asList(
            BlobInfo.newBuilder(downloadFullBucketName, "bucketb1").build(),
            BlobInfo.newBuilder(downloadFullBucketName, "bucketb2").build(),
            BlobInfo.newBuilder(downloadFullBucketName, "bucketb3").build());
    for (BlobInfo blob : bucketBlobs) {
      storage.create(blob);
    }
    DownloadBucket.downloadBucketContents(
        PROJECT_ID, downloadFullBucketName, tmp.getRoot().toPath());
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("bucketb1")).isTrue();
    assertThat(snippetOutput.contains("bucketb2")).isTrue();
    assertThat(snippetOutput.contains("bucketb3")).isTrue();
  }

  @Test
  public void downloadFiles() {
    DownloadMany.downloadManyBlobs(BUCKET, blobs, tmp.getRoot().toPath());
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("blob1")).isTrue();
    assertThat(snippetOutput.contains("blob2")).isTrue();
    assertThat(snippetOutput.contains("blob3")).isTrue();
  }

  @Test
  public void uploadAllowPCU() throws IOException {
    File tmpFile = tmpDirectory.newFile("fileDirUpload.txt");
    AllowParallelCompositeUpload.parallelCompositeUploadAllowed(
        BUCKET, Collections.singletonList(tmpFile.toPath()));
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("fileDirUpload.txt")).isTrue();
  }

  @Test
  public void downloadAllowDivideAndConquer() {
    AllowDivideAndConquerDownload.divideAndConquerDownloadAllowed(
        blobs, BUCKET, tmp.getRoot().toPath());
    String snippetOutput = stdOutCaptureRule.getCapturedOutputAsUtf8String();
    assertThat(snippetOutput.contains("blob1")).isTrue();
    assertThat(snippetOutput.contains("blob2")).isTrue();
    assertThat(snippetOutput.contains("blob3")).isTrue();
  }
}
