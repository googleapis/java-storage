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

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiFuture;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.it.runner.registry.ObjectsFixture;
import com.google.cloud.storage.it.runner.registry.ObjectsFixture.ObjectAndContent;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.TEST_BENCH)
@Ignore
public final class ITBlobDescriptorTest {

  private static final int _512KiB = 512 * 1024;

  @Inject
  @StorageFixture(Transport.GRPC)
  public Storage storage;

  @Inject public BucketInfo bucket;
  @Inject public Generator generator;
  @Inject public ObjectsFixture objectsFixture;

  @Test
  public void bytes() throws ExecutionException, InterruptedException, TimeoutException {
    ObjectAndContent obj512KiB = objectsFixture.getObj512KiB();
    BlobInfo info = obj512KiB.getInfo();
    BlobId blobId = info.getBlobId();

    BlobDescriptor blobDescriptor = storage.getBlobDescriptor(blobId).get(30, TimeUnit.SECONDS);

    BlobInfo info1 = blobDescriptor.getBlobInfo();

    assertThat(info1).isEqualTo(info);

    ApiFuture<byte[]> futureRead1Bytes =
        blobDescriptor.readRangeAsBytes(
            ByteRangeSpec.explicit(_512KiB - 13L, ByteRangeSpec.EFFECTIVE_INFINITY));

    byte[] read1Bytes = futureRead1Bytes.get(30, TimeUnit.SECONDS);
    byte[] expected = obj512KiB.getContent().getBytes(_512KiB - 13);
    assertThat(xxd(read1Bytes)).isEqualTo(xxd(expected));
  }

  @Test
  public void readObject() throws IOException {
    ObjectAndContent obj512KiB = objectsFixture.getObj512KiB();
    BlobInfo info = obj512KiB.getInfo();
    BlobId blobId = info.getBlobId();

    byte[] expected = obj512KiB.getContent().getBytes(_512KiB - 13);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ReadChannel r = storage.reader(blobId);
        WritableByteChannel w = Channels.newChannel(baos)) {
      r.setChunkSize(0);
      r.seek(_512KiB - 13);
      ByteStreams.copy(r, w);
    }

    assertThat(xxd(baos.toByteArray())).isEqualTo(xxd(expected));
  }
}
