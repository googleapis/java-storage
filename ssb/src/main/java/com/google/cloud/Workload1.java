/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.TmpFile;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

final class Workload1 implements Callable<String> {
  private final TmpFile file;
  private final BlobInfo blob;
  private final Storage storage;

  Workload1(TmpFile file, BlobInfo blob, Storage storage) {
    this.file = file;
    this.blob = blob;
    this.storage = storage;
  }

  @Override
  public String call() throws Exception {
    Clock clock = Clock.systemDefaultZone();

    // Get the start time
    Instant startTime = clock.instant();
    Blob created = storage.createFrom(blob, file.getPath());
    Instant endTime = clock.instant();
    Duration elapsedTime = Duration.between(startTime, endTime);
    double throughput =
        created.getSize() >= StorageSharedBenchmarkingCli.SSB_SIZE_THRESHOLD_BYTES
            ? created.getSize() / 1024 / 1024 / (elapsedTime.toNanos())
            : created.getSize() / 1024 / 1024 / (elapsedTime.toNanos());
    System.out.println(generateCloudMonitoringResult("WRITE", throughput, created).toString());
    return "OK";
  }

  private CloudMonitoringResult generateCloudMonitoringResult(
      String op, double throughput, Blob created) {
    CloudMonitoringResult result =
        CloudMonitoringResult.newBuilder()
            .setLibrary("java")
            .setApi(StorageSharedBenchmarkingCli.api)
            .setOp(op)
            .setWorkers(StorageSharedBenchmarkingCli.workers)
            .setObjectSize(created.getSize().intValue())
            .setChunksize(created.getSize().intValue())
            .setCrc32cEnabled(false)
            .setMd5Enabled(false)
            .setCpuTimeUs(-1)
            .setBucketName(created.getBucket())
            .setStatus("OK")
            .setTransferSize(created.getSize().toString())
            .setThroughput(throughput)
            .build();
    return result;
  }
}
