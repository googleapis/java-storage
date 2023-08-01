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

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ssbcli")
public class StorageSharedBenchmarkingCli implements Runnable {
  // TODO: check what input validation is needed for option values.
  @Option(names = "-project", description = "GCP Project Identifier")
  String project;

  @Option(names = "-bucket", description = "Name of the bucket to use")
  String bucket;

  @Option(names = "-samples", defaultValue = "8000", description = "Number of samples to report")
  int samples;

  @Option(
      names = "-workers",
      defaultValue = "16",
      description = "Number of workers to run in parallel for the workload")
  int workers;

  @Option(names = "-api", defaultValue = "Mixed", description = "API to use")
  String api;

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

  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(StorageSharedBenchmarkingCli.class).setUsageHelpWidth(100);
    System.exit(cmd.execute(args));
  }

  @Override
  public void run() {
    Storage storageClient = StorageOptions.newBuilder().setProjectId(project).build().getService();
    
  }
}
