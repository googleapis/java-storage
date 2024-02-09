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

package com.google.cloud.storage.benchmarking;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobWriteSession;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.it.runner.registry.Generator;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class Bidi implements Callable<String> {
  private final Storage storageClient;
  private final Bucket bucket;
  private final Generator generator;

  Bidi(Storage storageClient, Bucket bucket, Generator generator) {
    this.storageClient = storageClient;
    this.bucket = bucket;
    this.generator = generator;
  }

  @Override
  public String call() throws Exception {
    String blobName = DataGenerator.base64Characters().genBytes(20).toString();
    BlobWriteSession sess =
        storageClient.blobWriteSession(
            BlobInfo.newBuilder(bucket, blobName).build(),
            BlobWriteOption.doesNotExist());
    byte[] bytes = DataGenerator.base64Characters().genBytes(512 * 1024);
    try (WritableByteChannel w = sess.open()) {
      w.write(ByteBuffer.wrap(bytes));
    }
    BlobInfo gen1 = sess.getResult().get(10, TimeUnit.SECONDS);
    return "OK";
  }
}
