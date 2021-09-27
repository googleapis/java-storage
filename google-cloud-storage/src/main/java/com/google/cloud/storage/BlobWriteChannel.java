/*
 * Copyright 2015 Google LLC
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

import static com.google.cloud.RetryHelper.runWithRetries;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.callable;

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.BaseWriteChannel;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.RestorableState;
import com.google.cloud.RetryHelper;
import com.google.cloud.WriteChannel;
import java.math.BigInteger;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Write channel implementation to upload Google Cloud Storage blobs. */
class BlobWriteChannel extends BaseWriteChannel<StorageOptions, BlobInfo> {

  private final ExceptionHandler exceptionHandlerForWrite;
  // Detect if flushBuffer() is being retried or not.
  // TODO: I don't think this is thread safe, and there's probably a better way to detect a retry
  // occuring.
  private boolean retrying = false;
  private boolean checkingForLastChunk = false;

  // Contains metadata of the updated object or null if upload is not completed.
  private StorageObject storageObject;

  BlobWriteChannel(
      StorageOptions storageOptions,
      BlobInfo blobInfo,
      String uploadId,
      ExceptionHandler exceptionHandlerForWrite) {
    super(storageOptions, blobInfo, uploadId);
    this.exceptionHandlerForWrite = exceptionHandlerForWrite;
  }

  boolean isRetrying() {
    return retrying;
  }

  StorageObject getStorageObject() {
    return storageObject;
  }

  private StorageObject transmitChunk(
      int chunkOffset, int chunkLength, long position, boolean last) {
    return getOptions()
        .getStorageRpcV1()
        .writeWithResponse(getUploadId(), getBuffer(), chunkOffset, position, chunkLength, last);
  }

  private long getRemotePosition() {
    return getOptions().getStorageRpcV1().getCurrentUploadOffset(getUploadId());
  }

  private static StorageException unrecoverableState(
      String uploadId,
      int chunkOffset,
      int chunkLength,
      long localPosition,
      long remotePosition,
      boolean last) {
    return unrecoverableState(
        uploadId,
        chunkOffset,
        chunkLength,
        localPosition,
        remotePosition,
        last,
        "Unable to recover in upload.\nThis may be a symptom of multiple clients uploading to the same upload session.");
  }

  private static StorageException errorResolvingMetadataLastChunk(
      String uploadId,
      int chunkOffset,
      int chunkLength,
      long localPosition,
      long remotePosition,
      boolean last) {
    return unrecoverableState(
        uploadId,
        chunkOffset,
        chunkLength,
        localPosition,
        remotePosition,
        last,
        "Unable to load object metadata to determine if last chunk was successfully written");
  }

  private static StorageException unrecoverableState(
      String uploadId,
      int chunkOffset,
      int chunkLength,
      long localPosition,
      long remotePosition,
      boolean last,
      String message) {
    StringBuilder sb = new StringBuilder();
    sb.append(message).append("\n\n");
    sb.append("For debugging purposes:\n");
    sb.append("uploadId: ").append(uploadId).append('\n');
    sb.append("chunkOffset: ").append(chunkOffset).append('\n');
    sb.append("chunkLength: ").append(chunkLength).append('\n');
    sb.append("localOffset: ").append(localPosition).append('\n');
    sb.append("remoteOffset: ").append(remotePosition).append('\n');
    sb.append("lastChunk: ").append(last).append("\n\n");
    return new StorageException(0, sb.toString());
  }

  // Retriable interruption occurred.
  // Variables:
  // chunk = getBuffer()
  // localNextByteOffset == getPosition()
  // chunkSize = getChunkSize()
  //
  // Case 1: localNextByteOffset == remoteNextByteOffset:
  // Retrying the entire chunk
  //
  // Case 2: localNextByteOffset < remoteNextByteOffset
  //             && driftOffset < chunkSize:
  // Upload progressed and localNextByteOffset is not in-sync with
  // remoteNextByteOffset and driftOffset is less than chunkSize.
  // driftOffset must be less than chunkSize for it to retry using
  // chunk maintained in memory.
  // Find the driftOffset by subtracting localNextByteOffset from
  // remoteNextByteOffset.
  // Use driftOffset to determine where to restart from using the chunk in
  // memory.
  //
  // Case 3: localNextByteOffset < remoteNextByteOffset
  //            && driftOffset == chunkSize:
  // Special case of Case 2.
  // If chunkSize is equal to driftOffset then remoteNextByteOffset has moved on
  // to the next chunk.
  //
  // Case 4: localNextByteOffset < remoteNextByteOffset
  //            && driftOffset > chunkSize:
  // Throw exception as remoteNextByteOffset has drifted beyond the retriable
  // chunk maintained in memory. This is not possible unless there's multiple
  // clients uploading to the same resumable upload session.
  //
  // Case 5: localNextByteOffset > remoteNextByteOffset:
  // For completeness, this case is not possible because it would require retrying
  // a 400 status code which is not allowed.
  //
  // Case 6: remoteNextByteOffset==-1 && last == true
  // Upload is complete and retry occurred in the "last" chunk. Data sent was
  // received by the service.
  //
  // Case 7: remoteNextByteOffset==-1 && last == false && !checkingForLastChunk
  // Not last chunk and are not checkingForLastChunk, allow for the client to
  // catch up to final chunk which meets
  // Case 6.
  //
  // Case 8: remoteNextByteOffset==-1 && last == false && checkingForLastChunk
  // Not last chunk and checkingForLastChunk means this is the second time we
  // hit this case, meaning the upload was completed by a different client.
  //
  // Case 9: Only possible if the client local offset continues beyond the remote
  // offset which is not possible.
  //
  @Override
  protected void flushBuffer(final int length, final boolean lastChunk) {
    try {
      runWithRetries(
          callable(
              new Runnable() {
                @Override
                public void run() {
                  // Get remote offset from API
                  final long localPosition = getPosition();
                  // For each request it should be possible to retry from its location in this code
                  final long remotePosition = isRetrying() ? getRemotePosition() : localPosition;
                  final int chunkOffset = (int) (remotePosition - localPosition);
                  final int chunkLength = length - chunkOffset;
                  final boolean uploadAlreadyComplete = remotePosition == -1;
                  // Enable isRetrying state to reduce number of calls to getRemotePosition()
                  if (!isRetrying()) {
                    retrying = true;
                  }
                  if (uploadAlreadyComplete && lastChunk) {
                    // Case 6
                    // Request object metadata if not available
                    long totalBytes = getPosition() + length;
                    if (storageObject == null) {
                      storageObject =
                          getOptions()
                              .getStorageRpcV1()
                              .queryCompletedResumableUpload(getUploadId(), totalBytes);
                    }
                    // the following checks are defined here explicitly to provide a more
                    // informative if either storageObject is unable to be resolved or it's size is
                    // unable to be determined. This scenario is a very rare case of failure that
                    // can arise when packets are lost.
                    if (storageObject == null) {
                      throw errorResolvingMetadataLastChunk(
                          getUploadId(),
                          chunkOffset,
                          chunkLength,
                          localPosition,
                          remotePosition,
                          lastChunk);
                    }
                    // Verify that with the final chunk we match the blob length
                    BigInteger size = storageObject.getSize();
                    if (size == null) {
                      throw errorResolvingMetadataLastChunk(
                          getUploadId(),
                          chunkOffset,
                          chunkLength,
                          localPosition,
                          remotePosition,
                          lastChunk);
                    }
                    if (size.longValue() != totalBytes) {
                      throw unrecoverableState(
                          getUploadId(),
                          chunkOffset,
                          chunkLength,
                          localPosition,
                          remotePosition,
                          lastChunk);
                    }
                    retrying = false;
                  } else if (uploadAlreadyComplete && !lastChunk && !checkingForLastChunk) {
                    // Case 7
                    // Make sure this is the second to last chunk.
                    checkingForLastChunk = true;
                    // Continue onto next chunk in case this is the last chunk
                  } else if (localPosition <= remotePosition && chunkOffset < getChunkSize()) {
                    // Case 1 && Case 2
                    // We are in a position to send a chunk
                    storageObject =
                        transmitChunk(chunkOffset, chunkLength, remotePosition, lastChunk);
                    retrying = false;
                  } else if (localPosition < remotePosition && chunkOffset == getChunkSize()) {
                    // Case 3
                    // Continue to next chunk to catch up with remotePosition we are one chunk
                    // behind
                    retrying = false;
                  } else {
                    // Case 4 && Case 8 && Case 9
                    throw unrecoverableState(
                        getUploadId(),
                        chunkOffset,
                        chunkLength,
                        localPosition,
                        remotePosition,
                        lastChunk);
                  }
                }
              }),
          getOptions().getRetrySettings(),
          exceptionHandlerForWrite,
          getOptions().getClock());
    } catch (RetryHelper.RetryHelperException e) {
      throw StorageException.translateAndThrow(e);
    }
  }

  protected StateImpl.Builder stateBuilder() {
    return StateImpl.builder(getOptions(), getEntity(), getUploadId())
        .setResultRetryAlgorithm(exceptionHandlerForWrite);
  }

  static Builder newBuilder() {
    return new Builder();
  }

  static final class Builder {
    private StorageOptions storageOptions;
    private BlobInfo blobInfo;
    private Supplier<@NonNull String> uploadIdSupplier;
    private ExceptionHandler putExceptionHandler;

    public Builder setStorageOptions(StorageOptions storageOptions) {
      this.storageOptions = storageOptions;
      return this;
    }

    public Builder setBlobInfo(BlobInfo blobInfo) {
      this.blobInfo = blobInfo;
      return this;
    }

    public Builder setUploadIdSupplier(Supplier<String> uploadIdSupplier) {
      this.uploadIdSupplier = uploadIdSupplier;
      return this;
    }

    public Builder setPutExceptionHandler(ExceptionHandler putExceptionHandler) {
      this.putExceptionHandler = putExceptionHandler;
      return this;
    }

    BlobWriteChannel build() {
      String uploadId = requireNonNull(uploadIdSupplier, "uploadId must be non null").get();
      return new BlobWriteChannel(
          requireNonNull(storageOptions, "storageOptions must be non null"),
          blobInfo,
          requireNonNull(uploadId, "uploadId must be non null"),
          requireNonNull(putExceptionHandler, "putExceptionHandler must be non null"));
    }
  }

  static class StateImpl extends BaseWriteChannel.BaseState<StorageOptions, BlobInfo> {

    private static final long serialVersionUID = -9028324143780151286L;

    private final ExceptionHandler exceptionHandler;

    StateImpl(Builder builder) {
      super(builder);
      this.exceptionHandler = builder.exceptionHandler;
    }

    static class Builder extends BaseWriteChannel.BaseState.Builder<StorageOptions, BlobInfo> {
      private ExceptionHandler exceptionHandler;

      private Builder(StorageOptions options, BlobInfo blobInfo, String uploadId) {
        super(options, blobInfo, uploadId);
      }

      public Builder setResultRetryAlgorithm(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
      }

      @Override
      public RestorableState<WriteChannel> build() {
        return new StateImpl(this);
      }
    }

    static Builder builder(StorageOptions options, BlobInfo blobInfo, String uploadId) {
      return new Builder(options, blobInfo, uploadId);
    }

    @Override
    public WriteChannel restore() {
      try {
        BlobWriteChannel channel =
            BlobWriteChannel.newBuilder()
                .setStorageOptions(serviceOptions)
                .setBlobInfo(entity)
                .setUploadIdSupplier(() -> uploadId)
                .setPutExceptionHandler(exceptionHandler)
                .build();
        channel.restore(this);
        return channel;
      } catch (Exception e) {
        throw StorageException.coalesce(e);
      }
    }
  }
}
