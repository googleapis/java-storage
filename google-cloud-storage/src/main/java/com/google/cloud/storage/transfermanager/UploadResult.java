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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageException;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UploadResult {

  @NonNull private final BlobInfo input;
  @NonNull private final TransferStatus status;
  @NonNull private final BlobInfo uploadedBlob;
  @NonNull private final StorageException exception;

  public UploadResult(
      @NonNull BlobInfo input,
      @NonNull TransferStatus status,
      @NonNull BlobInfo uploadedBlob,
      @NonNull StorageException exception) {
    this.input = input;
    this.status = status;
    this.uploadedBlob = uploadedBlob;
    this.exception = exception;
  }

  public @NonNull BlobInfo getInput() {
    return input;
  }

  public @NonNull TransferStatus getStatus() {
    return status;
  }

  public @NonNull BlobInfo getUploadedBlob() {
    return uploadedBlob;
  }

  public @NonNull StorageException getException() {
    return exception;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadResult that = (UploadResult) o;
    return input.equals(that.input) && status == that.status && uploadedBlob.equals(
        that.uploadedBlob)
        && exception.equals(that.exception);
  }

  @Override
  public int hashCode() {
    return Objects.hash(input, status, uploadedBlob, exception);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("input", input)
        .add("status", status)
        .add("uploadedBlob", uploadedBlob)
        .add("exception", exception)
        .toString();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private @NonNull BlobInfo input;
    private @NonNull TransferStatus status;
    private @NonNull BlobInfo uploadedBlob;
    private @NonNull StorageException exception;

    private Builder() {
      //TODO: set "NULL" values
    }

    public Builder setInput(@NonNull BlobInfo input) {
      this.input = input;
      return this;
    }

    public Builder setStatus(@NonNull TransferStatus status) {
      this.status = status;
      return this;
    }

    public Builder setUploadedBlob(@NonNull BlobInfo uploadedBlob) {
      this.uploadedBlob = uploadedBlob;
      return this;
    }

    public Builder setException(@NonNull StorageException exception) {
      this.exception = exception;
      return this;
    }

    public UploadResult build() {
      checkNotNull(input);
      checkNotNull(status);
      checkNotNull(uploadedBlob);
      checkNotNull(exception);
      return new UploadResult(input, status, uploadedBlob, exception);
    }
  }
}
