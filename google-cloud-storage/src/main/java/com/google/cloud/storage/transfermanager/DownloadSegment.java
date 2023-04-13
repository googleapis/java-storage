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

package com.google.cloud.storage.transfermanager;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageException;
import java.nio.file.Path;

public final class DownloadSegment {
  private final BlobInfo input;

  private final Path outputDestination;

  private final TransferStatus status;

  private final StorageException exception;

  private final Long generation;

  private DownloadSegment(
      BlobInfo input,
      Path outputDestination,
      TransferStatus status,
      StorageException exception,
      Long generation) {
    this.input = input;
    this.outputDestination = outputDestination;
    this.status = status;
    this.exception = exception;
    this.generation = generation;
  }

  public BlobInfo getInput() {
    return input;
  }

  public Path getOutputDestination() {
    return outputDestination;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public StorageException getException() {
    return exception;
  }

  public Long getGeneration() {
    return generation;
  }

  public static class Builder {

    private BlobInfo input;
    private Path outputDestination;
    private TransferStatus status;
    private StorageException exception;
    private Long generation;

    public Builder setInput(BlobInfo input) {
      this.input = input;
      return this;
    }

    public Builder setOutputDestination(Path outputDestination) {
      this.outputDestination = outputDestination;
      return this;
    }

    public Builder setStatus(TransferStatus status) {
      this.status = status;
      return this;
    }

    public Builder setException(StorageException exception) {
      this.exception = exception;
      return this;
    }

    public Builder setGeneration(Long generation) {
      this.generation = generation;
      return this;
    }

    public DownloadSegment createDownloadSegment() {
      return new DownloadSegment(input, outputDestination, status, exception, generation);
    }
  }
}
