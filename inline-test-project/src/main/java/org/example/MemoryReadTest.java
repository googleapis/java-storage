/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MemoryReadTest {

  public static void main(String[] args) throws Exception {
    final int numberOfReads = Integer.parseInt(args[0]);
    final String bucketName = args[1];
    final String objectName = args[2];
    final String transport = args[3];
    final int appBuffer = Integer.parseInt(args[4]);
    final int threads = Integer.parseInt(args[5]);
    Storage storage;
    if (transport.equals("grpc")) {
      System.out.println("Using gRPC over DP-with zero copy");
      storage = StorageOptions.grpc().setAttemptDirectPath(true).build().getService();
    } else if (transport.equals("no-dp-grpc")) {
      System.out.println("Using gRPC over Cloud Path-with zero copy");
      storage = StorageOptions.grpc().build().getService();
    } else {
      System.out.println("Using JSON");
      storage = StorageOptions.http().build().getService();
    }

    final BlobId blobId = BlobId.of(bucketName, objectName);
    System.out.println("Starting...");

    final HashFunction hashFunction = Hashing.crc32c();
    final Blob blob = storage.get(blobId);
    byte[] decoded = Base64.getDecoder().decode(blob.getCrc32c());
    // flip order; hashFunction provides hashes in opposite order.
    ByteBuffer b = ByteBuffer.allocate(4);
    for (int i = 3; i >= 0; i--) {
      b.put(decoded[i]);
    }
    final byte[] expectedHash = b.array();
    if (threads > 1) {
      List<Future<Result>> results = new ArrayList<>();
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      for (int j = 0; j < threads; j++) {
        results.add(executor.submit(() -> {
          System.out.println("Starting thread: " + Thread.currentThread());
          return readWorkload(numberOfReads, appBuffer, storage, blobId, hashFunction, expectedHash, blob.getSize());
        }));
      }
      executor.shutdown();
      for(Future<Result> r : results) {
          Result t =  r.get();
          System.out.println(t);
      }
    } else {
      Result t = readWorkload(numberOfReads, appBuffer, storage, blobId, hashFunction, expectedHash, blob.getSize());
      System.out.println(t);
    }
    System.out.println("Workload Finished");
  }

  private static class Result {
      public long elapsedTime;
      public long totalSize;
      public int failures;
      public double throughput;

      @Override
      public String toString() {
          return new Formatter().format("elapsedTime: %d\ntotalSize: %d\nthroughput: %f bps\nfailures: %d", elapsedTime, totalSize, throughput, failures).toString();
      }
  }

  private static Result readWorkload(int numberOfReads, int appBuffer, Storage storage, BlobId blobId, HashFunction hashFunction, byte[] expectedHash, Long objectSize) {
    int failures = 0;
    ByteBuffer buffer = ByteBuffer.allocate(appBuffer);
    Clock clock = Clock.systemDefaultZone();
    Instant startTime = clock.instant();
    for (int i = 0; i < numberOfReads; i++) {
      Hasher h = hashFunction.newHasher();
      ReadChannel r = storage.reader(blobId);
      try {
        while (r.isOpen()) {
          int bytesRead = r.read(buffer);
          if (bytesRead == -1) {
            break;
          }
          buffer.flip();
          h.putBytes(buffer);
          buffer.clear();
        }
        byte[] resultHash = h.hash().asBytes();
        failures += Arrays.equals(resultHash, expectedHash) ? 0 : 1;
      } catch (IOException e) {
        System.err.println("failure: " + e);
        failures++;
      }
    }
    Instant endTime = clock.instant();
    Duration elapsedTime = Duration.between(startTime, endTime);
    Result r = new Result();
    r.elapsedTime = elapsedTime.getSeconds();
    r.totalSize = numberOfReads * objectSize;
    r.throughput = ((double)r.totalSize/(double)r.elapsedTime);
    r.failures = failures;
    return r;
  }
}
