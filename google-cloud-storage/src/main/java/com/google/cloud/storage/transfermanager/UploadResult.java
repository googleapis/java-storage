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
import static com.google.common.base.Preconditions.checkState;

import com.google.api.core.BetaApi;
import com.google.cloud.storage.BlobInfo;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@BetaApi
public final class UploadResult {

  @NonNull private final BlobInfo input;
  @NonNull private final TransferStatus status;
  @MonotonicNonNull private final BlobInfo uploadedBlob;
  @MonotonicNonNull private final Exception exception;

  private UploadResult(
      @NonNull BlobInfo input,
      @NonNull TransferStatus status,
      BlobInfo uploadedBlob,
      Exception exception) {
    this.input = input;
    this.status = status;
    this.uploadedBlob = uploadedBlob;
    this.exception = exception;
  }

  @BetaApi
  public @NonNull BlobInfo getInput() {
    return input;
  }

  @BetaApi
  public @NonNull TransferStatus getStatus() {
    return status;
  }

  @BetaApi
  public @NonNull BlobInfo getUploadedBlob() {
    checkState(
        status == TransferStatus.SUCCESS,
        "getUploadedBlob() only valid when status is SUCCESS but status was %s",
        status);
    return uploadedBlob;
  }

  @BetaApi
  public @NonNull Exception getException() {
    checkState(
        status == TransferStatus.FAILED_TO_START || status == TransferStatus.FAILED_TO_FINISH,
        "getException() is only valid when an unexpected error has occurred but status was %s",
        status);
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
    return input.equals(that.input)
        && status == that.status
        && uploadedBlob.equals(that.uploadedBlob)
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

  @BetaApi
  public static Builder newBuilder(@NonNull BlobInfo input, @NonNull TransferStatus status) {
    return new Builder(input, status);
  }

  @BetaApi
  public static final class Builder {

    private @NonNull BlobInfo input;
    private @NonNull TransferStatus status;
    private @MonotonicNonNull BlobInfo uploadedBlob;
    private @MonotonicNonNull Exception exception;

    private Builder(@NonNull BlobInfo input, @NonNull TransferStatus status) {
      this.input = input;
      this.status = status;
    }

    @BetaApi
    public Builder setInput(@NonNull BlobInfo input) {
      this.input = input;
      return this;
    }

    @BetaApi
    public Builder setStatus(@NonNull TransferStatus status) {
      this.status = status;
      return this;
    }

    @BetaApi
    public Builder setUploadedBlob(@NonNull BlobInfo uploadedBlob) {
      this.uploadedBlob = uploadedBlob;
      return this;
    }

    @BetaApi
    public Builder setException(@NonNull Exception exception) {
      this.exception = exception;
      return this;
    }

    @BetaApi
    public UploadResult build() {
      checkNotNull(input);
      checkNotNull(status);
      if (status == TransferStatus.SUCCESS) {
        checkNotNull(uploadedBlob);
      } else if (status == TransferStatus.FAILED_TO_START
          || status == TransferStatus.FAILED_TO_FINISH) {
        checkNotNull(exception);
      }
      return new UploadResult(input, status, uploadedBlob, exception);
    }
  }
}
