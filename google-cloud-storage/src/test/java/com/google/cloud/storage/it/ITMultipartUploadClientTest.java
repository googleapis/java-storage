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

package com.google.cloud.storage.it;

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.Thread.sleep;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.MultipartUploadClient;
import com.google.cloud.storage.MultipartUploadSettings;
import com.google.cloud.storage.RequestBody;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.BucketFixture;
import com.google.cloud.storage.it.runner.annotations.BucketType;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompletedMultipartUpload;
import com.google.cloud.storage.multipartupload.model.CompletedPart;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP},
    backends = {Backend.PROD})
public final class ITMultipartUploadClientTest {

  private static final int _5MiB = 5 * 1024 * 1024;

  @Inject public BucketInfo bucket;

  @Inject
  @BucketFixture(BucketType.REQUESTER_PAYS)
  public BucketInfo rpBucket;

  @Inject public Storage injectedStorage;

  @Inject public Transport transport;

  @Inject public Generator generator;

  private MultipartUploadClient multipartUploadClient;
  private Random rand;

  @Before
  public void setUp() throws Exception {
    rand = new Random();
    multipartUploadClient =
        MultipartUploadClient.create(
            MultipartUploadSettings.of((HttpStorageOptions) injectedStorage.getOptions()));
  }

  @Test
  public void testMultipartUpload() throws IOException {
    doTest(bucket, 12 * _5MiB + 37, "");
  }

  @Test
  public void testMultipartUpload_requesterPays() throws IOException {
    String projectId = injectedStorage.getOptions().getProjectId();
    doTest(rpBucket, 12 * _5MiB + 37, projectId);
  }

  @Test
  public void testAbort() throws IOException {
    BlobInfo info = BlobInfo.newBuilder(bucket, generator.randomObjectName()).build();

    CreateMultipartUploadRequest.Builder createRequestBuilder =
        CreateMultipartUploadRequest.builder().bucket(info.getBucket()).key(info.getName());
    CreateMultipartUploadResponse createResponse =
        multipartUploadClient.createMultipartUpload(createRequestBuilder.build());
    String uploadId = createResponse.uploadId();

    byte[] bytes = DataGenerator.rand(rand).genBytes(_5MiB);

    RequestBody partBody = RequestBody.of(ByteBuffer.wrap(bytes));
    UploadPartRequest.Builder uploadPartRequestBuilder =
        UploadPartRequest.builder()
            .partNumber(1)
            .uploadId(uploadId)
            .bucket(info.getBucket())
            .key(info.getName());
    multipartUploadClient.uploadPart(uploadPartRequestBuilder.build(), partBody);
    AbortMultipartUploadRequest abortRequest =
        AbortMultipartUploadRequest.builder()
            .bucket(info.getBucket())
            .key(info.getName())
            .uploadId(uploadId)
            .build();
    multipartUploadClient.abortMultipartUpload(abortRequest);
    Blob blob = injectedStorage.get(info.getBlobId());
    assertThat(blob).isNull();
  }

  private void doTest(BucketInfo bucket, int objectSizeBytes, String userProject)
      throws IOException {
    BlobInfo info = BlobInfo.newBuilder(bucket, generator.randomObjectName()).build();

    CreateMultipartUploadRequest.Builder createRequestBuilder =
        CreateMultipartUploadRequest.builder().bucket(info.getBucket()).key(info.getName());
    CreateMultipartUploadResponse createResponse =
        multipartUploadClient.createMultipartUpload(createRequestBuilder.build());
    String uploadId = createResponse.uploadId();
    byte[] bytes = DataGenerator.rand(rand).genBytes(objectSizeBytes);

    List<CompletedPart> completedParts = new ArrayList<>();
    int partNumber = 1;
    for (int i = 0; i < objectSizeBytes; i += _5MiB) {
      int len = Math.min(_5MiB, objectSizeBytes - i);
      byte[] partBuffer = Arrays.copyOfRange(bytes, i, i + len);
      RequestBody partBody = RequestBody.of(ByteBuffer.wrap(partBuffer));
      UploadPartRequest.Builder uploadPartRequestBuilder =
          UploadPartRequest.builder()
              .partNumber(partNumber)
              .uploadId(uploadId)
              .bucket(info.getBucket())
              .key(info.getName());
      UploadPartResponse uploadPartResponse =
          multipartUploadClient.uploadPart(uploadPartRequestBuilder.build(), partBody);
      completedParts.add(
          CompletedPart.builder().partNumber(partNumber).eTag(uploadPartResponse.eTag()).build());
      partNumber++;
    }
    completedParts.sort(Comparator.comparingInt(CompletedPart::partNumber));

    ListPartsRequest.Builder listPartsBuilder =
        ListPartsRequest.builder().bucket(info.getBucket()).key(info.getName()).uploadId(uploadId);
    ListPartsResponse listPartsResponse = multipartUploadClient.listParts(listPartsBuilder.build());
    assertThat(listPartsResponse.getParts()).hasSize(completedParts.size());
    CompletedMultipartUpload completedMultipartUpload =
        CompletedMultipartUpload.builder().parts(completedParts).build();
    CompleteMultipartUploadRequest completeMultipartUploadRequest =
        CompleteMultipartUploadRequest.builder()
            .bucket(info.getBucket())
            .key(info.getName())
            .uploadId(uploadId)
            .multipartUpload(completedMultipartUpload)
            .build();
    multipartUploadClient.completeMultipartUpload(completeMultipartUploadRequest);
    Blob result;
    byte[] actual;
    if (!userProject.isEmpty()) {
      result =
          injectedStorage.get(
              info.getBucket(), info.getName(), BlobGetOption.userProject(userProject));
      actual =
          injectedStorage.readAllBytes(info.getBlobId(), BlobSourceOption.userProject(userProject));
    } else {
      result = injectedStorage.get(info.getBlobId());
      actual = injectedStorage.readAllBytes(info.getBlobId());
    }
    assertThat(result).isNotNull();

    assertThat(actual).isEqualTo(bytes);
    assertThat(xxd(actual)).isEqualTo(xxd(bytes));
  }
}
