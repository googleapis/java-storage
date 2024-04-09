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

import static com.google.common.truth.Truth.assertThat;

import com.google.api.core.ApiFutures;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.ITUnbufferedResumableUploadTest.ObjectSizes;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.CrossRun.Exclude;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.Parameterized;
import com.google.cloud.storage.it.runner.annotations.Parameterized.Parameter;
import com.google.cloud.storage.it.runner.annotations.Parameterized.ParametersProvider;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    backends = {Backend.PROD},
    transports = {Transport.HTTP, Transport.GRPC})
@Parameterized(ObjectSizes.class)
public final class ITUnbufferedResumableUploadTest {

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Parameter public int objectSize;

  public static final class ObjectSizes implements ParametersProvider {

    @Override
    public ImmutableList<Integer> parameters() {
      return ImmutableList.of(256 * 1024, 2 * 1024 * 1024);
    }
  }

  @Test
  @Exclude(transports = Transport.GRPC)
  public void json()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    BlobInfo blobInfo = BlobInfo.newBuilder(bucket, generator.randomObjectName()).build();
    Opts<ObjectTargetOpt> opts = Opts.empty();
    final Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    BlobInfo.Builder builder = blobInfo.toBuilder().setMd5(null).setCrc32c(null);
    BlobInfo updated = opts.blobInfoMapper().apply(builder).build();

    StorageObject encode = Conversions.json().blobInfo().encode(updated);
    HttpStorageOptions options = (HttpStorageOptions) storage.getOptions();
    Supplier<String> uploadIdSupplier =
        ResumableMedia.startUploadForBlobInfo(
            options,
            updated,
            optionsMap,
            StorageRetryStrategy.getUniformStorageRetryStrategy().getIdempotentHandler());
    JsonResumableWrite jsonResumableWrite =
        JsonResumableWrite.of(encode, optionsMap, uploadIdSupplier.get(), 0);

    UnbufferedWritableByteChannelSession<StorageObject> session =
        ResumableMedia.http()
            .write()
            .byteChannel(HttpClientContext.from(options.getStorageRpcV1()))
            .resumable()
            .unbuffered()
            .setStartAsync(ApiFutures.immediateFuture(jsonResumableWrite))
            .build();

    int additional = 13;
    long size = objectSize + additional;
    ByteBuffer b = DataGenerator.base64Characters().genByteBuffer(size);

    UnbufferedWritableByteChannel open = session.open();
    int written = open.write(b);
    assertThat(written).isEqualTo(objectSize);
    assertThat(b.remaining()).isEqualTo(additional);

    int writtenAndClose = open.writeAndClose(b);
    assertThat(writtenAndClose).isEqualTo(additional);
    open.close();

    StorageObject storageObject = session.getResult().get(2, TimeUnit.SECONDS);
    assertThat(storageObject.getSize()).isEqualTo(BigInteger.valueOf(size));
  }
}
