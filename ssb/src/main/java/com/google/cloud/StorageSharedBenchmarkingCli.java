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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ListenableFutureToApiFuture;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.DataGenerator;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.TmpFile;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ssb")
public class StorageSharedBenchmarkingCli implements Runnable {
  public static long SSB_SIZE_THRESHOLD_BYTES = 1048576;
  // TODO: check what input validation is needed for option values.
  @Option(names = "-project", description = "GCP Project Identifier", required = true)
  String project;

  @Option(names = "-bucket", description = "Name of the bucket to use", required = true)
  String bucket;

  @Option(names = "-samples", defaultValue = "8000", description = "Number of samples to report")
  int samples;

  @Option(
      names = "-workers",
      defaultValue = "16",
      description = "Number of workers to run in parallel for the workload")
  static int workers;

  @Option(names = "-api", defaultValue = "JSON", description = "API to use")
  static String api;

  @Option(
      names = "-object_size",
      defaultValue = "1048576",
      description = "Object size in bytes to use for the workload")
  int objectSize;

  @Option(
      names = "-output_type",
      defaultValue = "cloud-monitoring",
      description = "Output results format")
  String outputType;

  @Option(
      names = "-test_type",
      description = "Specify which workload the cli should run",
      required = true)
  String testType;

  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(StorageSharedBenchmarkingCli.class);
    System.exit(cmd.execute(args));
  }

  @Override
  public void run() {
    // TODO: Make this a switch once we add more workloads
    runWorkload1();
  }

  private void runWorkload1() {
    RetrySettings retrySettings = StorageOptions.getDefaultRetrySettings().toBuilder().build();

    StorageOptions alwaysRetryStorageOptions =
        StorageOptions.newBuilder().setProjectId(project).setRetrySettings(retrySettings).build();
    Storage storageClient = alwaysRetryStorageOptions.getService();
    Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
    try {
      ListeningExecutorService executorService =
          MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(workers));
      List<ApiFuture<String>> workloadRuns = new ArrayList<>();
      for (int i = 0; i < samples; i++) {
        TmpFile file = DataGenerator.base64Characters().tempFile(tempDir, objectSize);
        BlobInfo blob = BlobInfo.newBuilder(bucket, file.toString()).build();
        workloadRuns.add(convert(executorService.submit(new Workload1(file, blob, storageClient))));
      }
      ApiExceptions.callAndTranslateApiException(ApiFutures.allAsList(workloadRuns));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> ApiFuture<T> convert(ListenableFuture<T> lf) {
    return new ListenableFutureToApiFuture<>(lf);
  }
}
