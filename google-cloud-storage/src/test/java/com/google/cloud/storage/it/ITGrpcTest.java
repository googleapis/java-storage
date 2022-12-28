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

import com.google.api.gax.paging.Page;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.HmacKey;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.cloud.storage.Storage.CreateHmacKeyOption;
import com.google.cloud.storage.Storage.ListHmacKeysOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.TEST_BENCH},
    transports = {Transport.HTTP, Transport.GRPC})
public final class ITGrpcTest {

  @Inject public Storage storage;

  @Inject public BucketInfo bucketInfo;

  @Inject public Generator generator;

  @Test
  public void testCreateBucket() {
    String bucketName = generator.randomBucketName();
    Bucket bucket = storage.create(BucketInfo.of(bucketName));
    assertThat(bucket.getName()).isEqualTo(bucketName);
  }

  @Test
  public void listBlobs() {
    byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    String prefix = generator.randomObjectName();
    List<Blob> blobs =
        IntStream.rangeClosed(1, 10)
            .mapToObj(i -> String.format("%s/%02d", prefix, i))
            .map(n -> BlobInfo.newBuilder(bucketInfo, n).build())
            .map(info -> storage.create(info, content, BlobTargetOption.doesNotExist()))
            .collect(ImmutableList.toImmutableList());

    List<String> expected =
        blobs.stream().map(Blob::getName).collect(ImmutableList.toImmutableList());

    Page<Blob> list = storage.list(bucketInfo.getName(), BlobListOption.prefix(prefix));
    ImmutableList<String> actual =
        StreamSupport.stream(list.iterateAll().spliterator(), false)
            .map(Blob::getName)
            .collect(ImmutableList.toImmutableList());

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void listBuckets() {
    Page<Bucket> list = storage.list();
    ImmutableList<String> bucketNames =
        StreamSupport.stream(list.iterateAll().spliterator(), false)
            .map(Bucket::getName)
            .collect(ImmutableList.toImmutableList());

    assertThat(bucketNames).contains(bucketInfo.getName());
  }

  @Test
  public void createHmacKey() {
    ServiceAccount serviceAccount = ServiceAccount.of("x@y.z");
    HmacKey hmacKey = storage.createHmacKey(serviceAccount);
    assertThat(hmacKey).isNotNull();
    assertThat(hmacKey.getSecretKey()).isNotNull();
    assertThat(hmacKey.getMetadata().getServiceAccount()).isEqualTo(serviceAccount);
  }

  @Test
  public void getHmacKey() {
    ServiceAccount serviceAccount = ServiceAccount.of("x@y.z");
    HmacKey hmacKey = storage.createHmacKey(serviceAccount);
    HmacKeyMetadata actual = storage.getHmacKey(hmacKey.getMetadata().getAccessId());
    assertThat(actual).isEqualTo(hmacKey.getMetadata());
  }

  @Test
  public void listHmacKeys() {
    ImmutableList<HmacKey> keys =
        IntStream.rangeClosed(1, 4)
            .mapToObj(i -> ServiceAccount.of(String.format("x-%d@y.z", i)))
            .map(sa -> storage.createHmacKey(sa, CreateHmacKeyOption.projectId("proj")))
            .collect(ImmutableList.toImmutableList());

    ImmutableList<HmacKeyMetadata> expected =
        keys.stream().map(HmacKey::getMetadata).collect(ImmutableList.toImmutableList());

    Page<HmacKeyMetadata> page = storage.listHmacKeys(ListHmacKeysOption.projectId("proj"));

    ImmutableList<HmacKeyMetadata> actual =
        StreamSupport.stream(page.iterateAll().spliterator(), false)
            .collect(ImmutableList.toImmutableList());

    assertThat(actual).containsAtLeastElementsIn(expected);
  }

  @Test
  public void updateHmacKey() {
    ServiceAccount serviceAccount = ServiceAccount.of("x@y.z");
    HmacKey hmacKey = storage.createHmacKey(serviceAccount);
    HmacKeyMetadata updated =
        storage.updateHmacKeyState(hmacKey.getMetadata(), HmacKeyState.INACTIVE);
    assertThat(updated.getServiceAccount()).isEqualTo(serviceAccount);
    assertThat(updated.getState()).isEqualTo(HmacKeyState.INACTIVE);
  }

  @Test
  public void deleteHmacKey() {
    ServiceAccount serviceAccount = ServiceAccount.of("x@y.z");
    HmacKey hmacKey = storage.createHmacKey(serviceAccount);
    storage.updateHmacKeyState(hmacKey.getMetadata(), HmacKeyState.INACTIVE);
    storage.deleteHmacKey(hmacKey.getMetadata());
  }

  @Test
  public void object_writeGetRead() {
    Storage s = storage;
    BlobInfo info = BlobInfo.newBuilder(bucketInfo, "writeGetRead").build();
    byte[] content = "hello, world".getBytes(StandardCharsets.UTF_8);
    s.create(info, content, BlobTargetOption.doesNotExist());

    Blob blob = s.get(info.getBlobId());

    byte[] actualContent = blob.getContent();
    assertThat(actualContent).isEqualTo(content);
  }

  @Test
  public void objectWrite_storage_create() {
    BlobInfo info = BlobInfo.newBuilder(bucketInfo, generator.randomObjectName()).build();
    byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    Blob blob = storage.create(info, content, BlobTargetOption.doesNotExist());
    byte[] actual = blob.getContent();
    assertThat(actual).isEqualTo(content);
  }

  @Test
  public void objectWrite_storage_create_stream() {
    BlobInfo info = BlobInfo.newBuilder(bucketInfo, generator.randomObjectName()).build();
    byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    Blob blob =
        storage.create(info, new ByteArrayInputStream(content), BlobWriteOption.doesNotExist());
    byte[] actual = blob.getContent();
    assertThat(actual).isEqualTo(content);
  }

  @Test
  public void objectWrite_storage_writer() throws IOException {
    BlobInfo info = BlobInfo.newBuilder(bucketInfo, generator.randomObjectName()).build();
    byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    try (WriteChannel c = storage.writer(info, BlobWriteOption.doesNotExist())) {
      c.write(ByteBuffer.wrap(content));
    }
    byte[] actual = storage.readAllBytes(info.getBlobId());
    assertThat(actual).isEqualTo(content);
  }

  @Test
  public void storageCopy() {
    Storage s = storage;

    byte[] expected = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    BlobInfo info =
        BlobInfo.newBuilder(bucketInfo, generator.randomObjectName() + "copy/src").build();
    Blob cpySrc = s.create(info, expected, BlobTargetOption.doesNotExist());

    BlobInfo dst =
        BlobInfo.newBuilder(bucketInfo, generator.randomObjectName() + "copy/dst").build();

    CopyRequest copyRequest =
        CopyRequest.newBuilder()
            .setSource(cpySrc.getBlobId())
            .setSourceOptions(BlobSourceOption.generationMatch(cpySrc.getGeneration()))
            .setTarget(dst, BlobTargetOption.doesNotExist())
            .build();

    CopyWriter copyWriter = s.copy(copyRequest);
    Blob result = copyWriter.getResult();

    byte[] actualBytes = s.readAllBytes(result.getBlobId());
    assertThat(actualBytes).isEqualTo(expected);
  }

  @Test
  public void lockBucketRetentionPolicy() {
    Storage s = storage;

    Bucket bucket = s.create(BucketInfo.of(generator.randomBucketName()));

    Bucket locked = bucket.lockRetentionPolicy(BucketTargetOption.metagenerationMatch());
    try {
      assertThat(locked.retentionPolicyIsLocked()).isTrue();
    } finally {
      s.delete(bucket.getName());
    }
  }
}
