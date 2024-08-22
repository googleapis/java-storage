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

package com.google.cloud.storage;

import static com.google.cloud.storage.ByteSizeConstants._1MiB;
import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.storage.BlobDescriptor.ZeroCopySupport.DisposableByteString;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.ChecksummedTestContent;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.it.runner.registry.ObjectsFixture;
import com.google.cloud.storage.it.runner.registry.ObjectsFixture.ObjectAndContent;
import com.google.common.base.Stopwatch;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
public final class ITBlobDescriptorTest {

  private static final int _512KiB = 512 * 1024;

  @Inject
  @StorageFixture(Transport.GRPC)
  public Storage storage;

  @Inject public BucketInfo bucket;

  @Inject public Generator generator;

  @Inject public ObjectsFixture objectsFixture;

  @Test
  public void bytes()
      throws ExecutionException, InterruptedException, TimeoutException, IOException {
    ObjectAndContent obj512KiB = objectsFixture.getObj512KiB();
    byte[] expected = obj512KiB.getContent().getBytes(_512KiB - 13);
    BlobId blobId = obj512KiB.getInfo().getBlobId();

    try (BlobDescriptor blobDescriptor =
        storage.getBlobDescriptor(blobId).get(30, TimeUnit.SECONDS)) {

      BlobInfo info1 = blobDescriptor.getBlobInfo();
      assertThat(info1).isNotNull();

      ApiFuture<byte[]> futureRead1Bytes =
          blobDescriptor.readRangeAsBytes(RangeSpec.of(_512KiB - 13L, 13L));

      byte[] read1Bytes = futureRead1Bytes.get(30, TimeUnit.SECONDS);
      assertThat(read1Bytes.length).isEqualTo(13);

      assertThat(xxd(read1Bytes)).isEqualTo(xxd(expected));
    }
  }

  @Test
  public void lotsOfBytes() throws Exception {
    ChecksummedTestContent testContent =
        ChecksummedTestContent.of(DataGenerator.base64Characters().genBytes(512 * _1MiB));

    BlobInfo gen1 =
        storage.create(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(),
            testContent.getBytes(),
            BlobTargetOption.doesNotExist());
    BlobId blobId = gen1.getBlobId();
    for (int j = 0; j < 2; j++) {

      Stopwatch sw = Stopwatch.createStarted();
      try (BlobDescriptor blobDescriptor =
          storage.getBlobDescriptor(blobId).get(30, TimeUnit.SECONDS)) {

        int numRangesToRead = 256;
        List<ApiFuture<byte[]>> futures =
            LongStream.range(0, numRangesToRead)
                .mapToObj(i -> RangeSpec.of(i * _2MiB, (long) _2MiB))
                .map(blobDescriptor::readRangeAsBytes)
                .collect(Collectors.toList());

        ApiFuture<List<byte[]>> listApiFuture = ApiFutures.allAsList(futures);

        List<byte[]> ranges = listApiFuture.get(5, TimeUnit.SECONDS);
        Stopwatch stop = sw.stop();
        System.out.println(stop.elapsed(TimeUnit.MILLISECONDS));
        Hasher hasher = Hashing.crc32c().newHasher();
        long length = 0;
        for (byte[] range : ranges) {
          hasher.putBytes(range);
          length += range.length;
        }
        final long finalLength = length;

        assertAll(
            () -> {
              Crc32cLengthKnown expectedCrc32c =
                  Crc32cValue.of(testContent.getCrc32c(), testContent.getBytes().length);
              Crc32cLengthKnown actualCrc32c = Crc32cValue.of(hasher.hash().asInt(), finalLength);

              assertThat(actualCrc32c).isEqualTo(expectedCrc32c);
            },
            () -> assertThat(finalLength).isEqualTo(numRangesToRead * _2MiB));
      }
    }
  }

  @Test
  public void readRangeAsByteString() throws Exception {
    ChecksummedTestContent testContent =
        ChecksummedTestContent.of(DataGenerator.base64Characters().genBytes(512 * _1MiB));

    BlobInfo gen1 =
        storage.create(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build(),
            testContent.getBytes(),
            BlobTargetOption.doesNotExist());
    BlobId blobId = gen1.getBlobId();
    for (int j = 0; j < 2; j++) {

      Stopwatch sw = Stopwatch.createStarted();
      try (BlobDescriptorImpl blobDescriptor =
          (BlobDescriptorImpl) storage.getBlobDescriptor(blobId).get(2, TimeUnit.SECONDS)) {

        int numRangesToRead = 256;
        List<ApiFuture<DisposableByteString>> futures =
            LongStream.range(0, numRangesToRead)
                .mapToObj(i -> RangeSpec.of(i * _2MiB, _2MiB))
                .map(blobDescriptor::readRangeAsByteString)
                .collect(Collectors.toList());

        ApiFuture<List<DisposableByteString>> listApiFuture = ApiFutures.allAsList(futures);

        List<DisposableByteString> ranges = listApiFuture.get(5, TimeUnit.SECONDS);
        Stopwatch stop = sw.stop();
        System.out.println(stop.elapsed(TimeUnit.MILLISECONDS));
        Hasher hasher = Hashing.crc32c().newHasher();
        long length = 0;
        for (DisposableByteString range : ranges) {
          try (DisposableByteString disposable = range) {
            ByteString byteString = disposable.byteString();
            for (ByteBuffer byteBuffer : byteString.asReadOnlyByteBufferList()) {
              hasher.putBytes(byteBuffer);
            }
            length += byteString.size();
          }
        }
        final long finalLength = length;

        assertAll(
            () -> {
              Crc32cLengthKnown expectedCrc32c =
                  Crc32cValue.of(testContent.getCrc32c(), testContent.getBytes().length);
              Crc32cLengthKnown actualCrc32c = Crc32cValue.of(hasher.hash().asInt(), finalLength);

              assertThat(actualCrc32c).isEqualTo(expectedCrc32c);
            },
            () -> assertThat(finalLength).isEqualTo(numRangesToRead * _2MiB));
      }
    }
  }

  @Test
  public void readFromBucketThatDoesNotExistShouldRaiseStorageExceptionWith404() {
    BlobId blobId = BlobId.of("gcs-grpc-team-bucket-that-does-not-exist", "someobject");

    ApiFuture<BlobDescriptor> futureBlobDescriptor = storage.getBlobDescriptor(blobId);

    ExecutionException ee =
        assertThrows(ExecutionException.class, () -> futureBlobDescriptor.get(5, TimeUnit.SECONDS));

    assertThat(ee).hasCauseThat().isInstanceOf(StorageException.class);
    StorageException cause = (StorageException) ee.getCause();
    assertThat(cause.getCode()).isEqualTo(404);
  }
}
