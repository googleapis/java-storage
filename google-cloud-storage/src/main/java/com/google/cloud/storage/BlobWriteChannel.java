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
import static java.util.concurrent.Executors.callable;

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.BaseWriteChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.RetryHelper;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.spi.v1.StorageRpc;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

/** Write channel implementation to upload Google Cloud Storage blobs. */
class BlobWriteChannel extends BaseWriteChannel<StorageOptions, BlobInfo> {

  BlobWriteChannel(StorageOptions options, BlobInfo blob, Map<StorageRpc.Option, ?> optionsMap) {
    this(options, blob, open(options, blob, optionsMap));
  }

  BlobWriteChannel(StorageOptions options, URL signedURL) {
    this(options, open(signedURL, options));
  }

  BlobWriteChannel(StorageOptions options, BlobInfo blobInfo, String uploadId) {
    super(options, blobInfo, uploadId);
  }

  BlobWriteChannel(StorageOptions options, String uploadId) {
    super(options, null, uploadId);
  }

  // Contains metadata of the updated object or null if upload is not completed.
  private StorageObject storageObject;

  // Detect if flushBuffer() is being retried or not.
  // TODO: I don't think this is thread safe, and there's probably a better way to detect a retry
  // occuring.
  private boolean retrying = false;

  boolean isRetrying() {
    return retrying;
  }

  StorageObject getStorageObject() {
    return storageObject;
  }

  @Override
  protected void flushBuffer(final int length, final boolean last) {
    try {
      runWithRetries(
          callable(
              new Runnable() {
                @Override
                public void run() {
                  if (!isRetrying()) {
                    // Enable isRetrying state to reduce number of calls to getCurrentUploadOffset()
                    retrying = true;
                    storageObject =
                        getOptions()
                            .getStorageRpcV1()
                            .writeWithResponse(
                                getUploadId(), getBuffer(), 0, getPosition(), length, last);
                  } else {
                    // Retriable interruption occurred.
                    // Variables:
                    // chunk = getBuffer()
                    // localNextByteOffset == getPosition()
                    // chunkSize = getChunkSize()
                    //
                    // Case 1: localNextByteOffset == 0 && remoteNextByteOffset == 0:
                    // we are retrying from first chunk start from 0 offset.
                    //
                    // Case 2: localNextByteOffset == remoteNextByteOffset:
                    // Special case of Case 1 when a chunk is retried.
                    //
                    // Case 3: localNextByteOffset < remoteNextByteOffset
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
                    // Case 4: localNextByteOffset < remoteNextByteOffset
                    //            && driftOffset == chunkSize:
                    // Special case of Case 3.
                    // If chunkSize is equal to driftOffset then remoteNextByteOffset has moved on
                    // to the next chunk.
                    //
                    // Case 5: localNextByteOffset < remoteNextByteOffset
                    //            && driftOffset > chunkSize:
                    // Throw exception as remoteNextByteOffset has drifted beyond the retriable
                    // chunk maintained in memory. This is not possible unless there's multiple
                    // clients uploading to the same resumable upload session.
                    //
                    // Case 6: localNextByteOffset > remoteNextByteOffset:
                    // For completeness, this case is not possible because it would require retrying
                    // a 400 status code which is not allowed.
                    //
                    // Get remote offset from API
                    long remoteNextByteOffset =
                        getOptions().getStorageRpcV1().getCurrentUploadOffset(getUploadId());
                    long localNextByteOffset = getPosition();
                    int driftOffset = (int) (remoteNextByteOffset - localNextByteOffset);
                    int retryChunkLength = length - driftOffset;

                    if (localNextByteOffset == 0 && remoteNextByteOffset == 0
                        || localNextByteOffset == remoteNextByteOffset) {
                      // Case 1 and 2
                      storageObject =
                          getOptions()
                              .getStorageRpcV1()
                              .writeWithResponse(
                                  getUploadId(), getBuffer(), 0, getPosition(), length, last);
                    } else if (localNextByteOffset < remoteNextByteOffset
                        && driftOffset < getChunkSize()) {
                      // Case 3
                      storageObject =
                          getOptions()
                              .getStorageRpcV1()
                              .writeWithResponse(
                                  getUploadId(),
                                  getBuffer(),
                                  driftOffset,
                                  remoteNextByteOffset,
                                  retryChunkLength,
                                  last);
                    } else if (localNextByteOffset < remoteNextByteOffset
                        && driftOffset == getChunkSize()) {
                      // Case 4
                      // Continue to next chunk
                      retrying = false;
                      return;
                    } else {
                      // Case 5
                      StringBuilder sb = new StringBuilder();
                      sb.append(
                          "Remote offset has progressed beyond starting byte offset of next chunk.");
                      sb.append(
                          "This may be a symptom of multiple clients uploading to the same upload session.\n\n");
                      sb.append("For debugging purposes:\n");
                      sb.append("uploadId: ").append(getUploadId()).append('\n');
                      sb.append("localNextByteOffset: ").append(localNextByteOffset).append('\n');
                      sb.append("remoteNextByteOffset: ").append(remoteNextByteOffset).append('\n');
                      sb.append("driftOffset: ").append(driftOffset).append("\n\n");
                      throw new StorageException(0, sb.toString());
                    }
                  }
                  // Request was successful and retrying state is now disabled.
                  retrying = false;
                }
              }),
          getOptions().getRetrySettings(),
          StorageImpl.EXCEPTION_HANDLER,
          getOptions().getClock());
    } catch (RetryHelper.RetryHelperException e) {
      throw StorageException.translateAndThrow(e);
    }
  }

  protected StateImpl.Builder stateBuilder() {
    return StateImpl.builder(getOptions(), getEntity(), getUploadId());
  }

  private static String open(
      final StorageOptions options,
      final BlobInfo blob,
      final Map<StorageRpc.Option, ?> optionsMap) {
    try {
      return runWithRetries(
          new Callable<String>() {
            @Override
            public String call() {
              return options.getStorageRpcV1().open(blob.toPb(), optionsMap);
            }
          },
          options.getRetrySettings(),
          StorageImpl.EXCEPTION_HANDLER,
          options.getClock());
    } catch (RetryHelper.RetryHelperException e) {
      throw StorageException.translateAndThrow(e);
    }
  }

  private static String open(final URL signedURL, final StorageOptions options) {
    try {
      return runWithRetries(
          new Callable<String>() {
            @Override
            public String call() {
              if (!isValidSignedURL(signedURL.getQuery())) {
                throw new StorageException(2, "invalid signedURL");
              }
              return options.getStorageRpcV1().open(signedURL.toString());
            }
          },
          options.getRetrySettings(),
          StorageImpl.EXCEPTION_HANDLER,
          options.getClock());
    } catch (RetryHelper.RetryHelperException e) {
      throw StorageException.translateAndThrow(e);
    }
  }

  private static boolean isValidSignedURL(String signedURLQuery) {
    boolean isValid = true;
    if (signedURLQuery.startsWith("X-Goog-Algorithm=")) {
      if (!signedURLQuery.contains("&X-Goog-Credential=")
          || !signedURLQuery.contains("&X-Goog-Date=")
          || !signedURLQuery.contains("&X-Goog-Expires=")
          || !signedURLQuery.contains("&X-Goog-SignedHeaders=")
          || !signedURLQuery.contains("&X-Goog-Signature=")) {
        isValid = false;
      }
    } else if (signedURLQuery.startsWith("GoogleAccessId=")) {
      if (!signedURLQuery.contains("&Expires=") || !signedURLQuery.contains("&Signature=")) {
        isValid = false;
      }
    } else {
      isValid = false;
    }
    return isValid;
  }

  static class StateImpl extends BaseWriteChannel.BaseState<StorageOptions, BlobInfo> {

    private static final long serialVersionUID = -9028324143780151286L;

    StateImpl(Builder builder) {
      super(builder);
    }

    static class Builder extends BaseWriteChannel.BaseState.Builder<StorageOptions, BlobInfo> {

      private Builder(StorageOptions options, BlobInfo blobInfo, String uploadId) {
        super(options, blobInfo, uploadId);
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
      BlobWriteChannel channel = new BlobWriteChannel(serviceOptions, entity, uploadId);
      channel.restore(this);
      return channel;
    }
  }
}
