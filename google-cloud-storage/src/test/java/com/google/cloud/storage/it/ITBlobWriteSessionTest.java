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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobWriteSession;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.GrpcStorageOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageWriterConfigs;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.cloud.storage.it.runner.registry.Generator;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.PROD)
public final class ITBlobWriteSessionTest {

  @Inject
  @StorageFixture(Transport.GRPC)
  public Storage storage;

  @Inject public BucketInfo bucket;

  @Inject public Generator generator;

  @Test
  public void allDefaults() throws Exception {
    doTest(storage);
  }

  @Test
  public void overrideDefaultBufferSize() throws Exception {
    GrpcStorageOptions options =
        ((GrpcStorageOptions) storage.getOptions())
            .toBuilder()
            .setStorageWriterConfig(StorageWriterConfigs.getDefault().withChunkSize(256 * 1024))
            .build();
    try (Storage s = options.getService()) {
      doTest(s);
    }
  }

  private void doTest(Storage underTest) throws Exception {
    BlobWriteSession sess =
        underTest.blobWriteSession(
            BlobInfo.newBuilder(bucket, generator.randomObjectName()).build());

    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    try (WritableByteChannel w = sess.open()) {
      w.write(ByteBuffer.wrap(bytes));
    }

    BlobInfo gen1 = sess.getResult().get(10, TimeUnit.SECONDS);

    byte[] allBytes = storage.readAllBytes(gen1.getBlobId());

    assertThat(allBytes).isEqualTo(bytes);
  }
}
