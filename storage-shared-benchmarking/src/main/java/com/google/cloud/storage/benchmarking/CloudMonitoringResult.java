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

package com.google.cloud.storage.benchmarking;

import com.google.common.base.MoreObjects;
import java.util.Objects;

final class CloudMonitoringResult {
  private final String library;
  private final String api;
  private final String op;

  private final int workers;
  private final int object_size;
  private final int app_buffer_size;
  private final int chunksize;
  private final boolean crc32c_enabled;
  private final boolean md5_enabled;
  private final int cpu_time_us;
  private final String bucket_name;
  private final String status;
  private final String transfer_size;
  private final String transfer_offset;
  private final String failure_msg;
  private final double throughput;

  CloudMonitoringResult(
      String library,
      String api,
      String op,
      int workers,
      int objectSize,
      int appBufferSize,
      int chunksize,
      boolean crc32cEnabled,
      boolean md5Enabled,
      int cpuTimeUs,
      String bucketName,
      String status,
      String transferSize,
      String transferOffset,
      String failureMsg,
      double throughput) {
    this.library = library;
    this.api = api;
    this.op = op;
    this.workers = workers;
    this.object_size = objectSize;
    this.app_buffer_size = appBufferSize;
    this.chunksize = chunksize;
    this.crc32c_enabled = crc32cEnabled;
    this.md5_enabled = md5Enabled;
    this.cpu_time_us = cpuTimeUs;
    this.bucket_name = bucketName;
    this.status = status;
    this.transfer_size = transferSize;
    this.transfer_offset = transferOffset;
    this.failure_msg = failureMsg;
    this.throughput = throughput;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("library", library)
        .add("api", api)
        .add("op", op)
        .add("workers", workers)
        .add("object_size", object_size)
        .add("app_buffer_size", app_buffer_size)
        .add("chunksize", chunksize)
        .add("crc32c_enabled", crc32c_enabled)
        .add("md5_enabled", md5_enabled)
        .add("cpu_time_us", cpu_time_us)
        .add("bucket_name", bucket_name)
        .add("status", status)
        .add("transfer_size", transfer_size)
        .add("transfer_offset", transfer_offset)
        .add("failure_msg", failure_msg)
        .add("throughput", throughput)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CloudMonitoringResult)) {
      return false;
    }
    CloudMonitoringResult result = (CloudMonitoringResult) o;
    return workers == result.workers
        && object_size == result.object_size
        && app_buffer_size == result.app_buffer_size
        && chunksize == result.chunksize
        && crc32c_enabled == result.crc32c_enabled
        && md5_enabled == result.md5_enabled
        && cpu_time_us == result.cpu_time_us
        && Double.compare(result.throughput, throughput) == 0
        && Objects.equals(library, result.library)
        && Objects.equals(api, result.api)
        && Objects.equals(op, result.op)
        && Objects.equals(bucket_name, result.bucket_name)
        && Objects.equals(status, result.status)
        && Objects.equals(transfer_size, result.transfer_size)
        && Objects.equals(transfer_offset, result.transfer_offset)
        && Objects.equals(failure_msg, result.failure_msg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        library,
        api,
        op,
        workers,
        object_size,
        app_buffer_size,
        chunksize,
        crc32c_enabled,
        md5_enabled,
        cpu_time_us,
        bucket_name,
        status,
        transfer_size,
        transfer_offset,
        failure_msg,
        throughput);
  }

  public String generateCustomMetric() {
    return String.format(
        "throughput{library=%s,api=%s,op=%s,object_size=%d,chunksize=%d,workers=%d,crc32c_enabled=%b,md5_enabled=%b,bucket_name=%s,status=%s,app_buffer_size=%d}%.1f",
        library,
        api,
        op,
        object_size,
        chunksize,
        workers,
        crc32c_enabled,
        md5_enabled,
        bucket_name,
        status,
        app_buffer_size,
        throughput);
  }

  public static class Builder {

    private String library;
    private String api;
    private String op;
    private int workers;
    private int objectSize;
    private int appBufferSize;
    private int chunksize;
    private boolean crc32cEnabled;
    private boolean md5Enabled;
    private int cpuTimeUs;
    private String bucketName;
    private String status;
    private String transferSize;
    private String transferOffset;
    private String failureMsg;
    private double throughput;

    public Builder setLibrary(String library) {
      this.library = library;
      return this;
    }

    public Builder setApi(String api) {
      this.api = api;
      return this;
    }

    public Builder setOp(String op) {
      this.op = op;
      return this;
    }

    public Builder setWorkers(int workers) {
      this.workers = workers;
      return this;
    }

    public Builder setObjectSize(int objectSize) {
      this.objectSize = objectSize;
      return this;
    }

    public Builder setAppBufferSize(int appBufferSize) {
      this.appBufferSize = appBufferSize;
      return this;
    }

    public Builder setChunksize(int chunksize) {
      this.chunksize = chunksize;
      return this;
    }

    public Builder setCrc32cEnabled(boolean crc32cEnabled) {
      this.crc32cEnabled = crc32cEnabled;
      return this;
    }

    public Builder setMd5Enabled(boolean md5Enabled) {
      this.md5Enabled = md5Enabled;
      return this;
    }

    public Builder setCpuTimeUs(int cpuTimeUs) {
      this.cpuTimeUs = cpuTimeUs;
      return this;
    }

    public Builder setBucketName(String bucketName) {
      this.bucketName = bucketName;
      return this;
    }

    public Builder setStatus(String status) {
      this.status = status;
      return this;
    }

    public Builder setTransferSize(String transferSize) {
      this.transferSize = transferSize;
      return this;
    }

    public Builder setTransferOffset(String transferOffset) {
      this.transferOffset = transferOffset;
      return this;
    }

    public Builder setFailureMsg(String failureMsg) {
      this.failureMsg = failureMsg;
      return this;
    }

    public Builder setThroughput(double throughput) {
      this.throughput = throughput;
      return this;
    }

    public CloudMonitoringResult build() {
      return new CloudMonitoringResult(
          library,
          api,
          op,
          workers,
          objectSize,
          appBufferSize,
          chunksize,
          crc32cEnabled,
          md5Enabled,
          cpuTimeUs,
          bucketName,
          status,
          transferSize,
          transferOffset,
          failureMsg,
          throughput);
    }
  }
}
