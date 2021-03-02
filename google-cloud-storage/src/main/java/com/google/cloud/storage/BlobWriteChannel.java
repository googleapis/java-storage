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
  private boolean checkingForLastChunk = false;

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

  private StorageObject getRemoteStorageObject() {
    return getOptions().getStorageRpcV1().get(getEntity().toPb(), null);
  }

  private StorageException unrecoverableState(
      int chunkOffset, int chunkLength, long localPosition, long remotePosition, boolean last) {
    StringBuilder sb = new StringBuilder();
    sb.append("Unable to recover in upload.\n");
    sb.append(
        "This may be a symptom of multiple clients uploading to the same upload session.\n\n");
    sb.append("For debugging purposes:\n");
    sb.append("uploadId: ").append(getUploadId()).append('\n');
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
                  final long remotePosition = isRetrying() ? getRemotePosition() : getPosition();
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
                    if (storageObject == null) {
                      storageObject = getRemoteStorageObject();
                    }
                    // Verify that with the final chunk we match the blob length
                    if (storageObject.getSize().longValue() != getPosition() + length) {
                      throw unrecoverableState(
                          chunkOffset, chunkLength, localPosition, remotePosition, lastChunk);
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
                        chunkOffset, chunkLength, localPosition, remotePosition, lastChunk);
                  }
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
