/*
 * Copyright 2025 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobReadChannelV2.BlobReadChannelContext;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompletedMultipartUpload;
import com.google.cloud.storage.multipartupload.model.CompletedPart;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MultipartUploadWriteChannel implements StorageWriteChannel {

  private static final int MIN_CHUNK_SIZE = 5 * 1024 * 1024; // 5MB
  private static final int POOL_SIZE = 16;

  private final SettableApiFuture<BlobInfo> result = SettableApiFuture.create();
  private final BlobReadChannelContext context;
  private final String bucketName;
  private final String blobName;
  private final ListeningExecutorService executor;
  private final BlockingQueue<ByteBuffer> bufferQueue;
  private final List<ApiFuture<CompletedPart>> completedParts = new ArrayList<>();

  private MultipartUploadClientImpl client;
  private String uploadId;
  private ByteBuffer currentBuffer;
  private int partNumber = 1;
  private boolean open = true;

  MultipartUploadWriteChannel(
      BlobReadChannelContext context, String bucketName, String blobName) {
    this.context = context;
    this.bucketName = bucketName;
    this.blobName = blobName;
    this.executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_SIZE));
    this.bufferQueue = new ArrayBlockingQueue<>(POOL_SIZE);
    for (int i = 0; i < POOL_SIZE; i++) {
      bufferQueue.offer(ByteBuffer.allocate(MIN_CHUNK_SIZE));
    }
    this.currentBuffer = bufferQueue.poll(); // Non-blocking
  }

  @Override
  public ApiFuture<BlobInfo> getObject() {
    return result;
  }

  @Override
  public void setChunkSize(int i) {
    // The chunk size is fixed at 5MB for multipart uploads.
  }

  @Override
  public RestorableState<WriteChannel> capture() {
    return null;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    if (!open) {
      throw new IOException("Channel is closed");
    }
    lazyInit();

    int bytesWritten = 0;
    while (src.hasRemaining()) {
      if (currentBuffer.remaining() == 0) {
        flushBuffer();
      }
      // The simplest way to handle incoming buffers is to copy their content into
      // our managed, fixed-size buffers.
      int bytesToCopy = Math.min(src.remaining(), currentBuffer.remaining());
      int originalLimit = src.limit();
      src.limit(src.position() + bytesToCopy);
      currentBuffer.put(src);
      src.limit(originalLimit);
      bytesWritten += bytesToCopy;
    }
    return bytesWritten;
  }

  private void lazyInit() throws IOException {
    if (client == null) {
      HttpStorageOptions options = context.getStorageOptions();
      this.client =
          new MultipartUploadClientImpl(
              context.getRetrier(),
              MultipartUploadHttpRequestManager.createFrom(options),
              options.getRetryAlgorithmManager());
    }
    if (uploadId == null) {
      try {
        CreateMultipartUploadRequest createRequest =
            CreateMultipartUploadRequest.builder().bucket(bucketName).key(blobName).build();
        CreateMultipartUploadResponse createResponse = client.createMultipartUpload(createRequest);
        this.uploadId = createResponse.uploadId();
      } catch (Exception e) {
        throw new IOException("Failed to initiate multipart upload", e);
      }
    }
  }

  private void flushBuffer() throws IOException {
    try {
      final ByteBuffer bufferToUpload = currentBuffer;
      currentBuffer = bufferQueue.take(); // Block until a buffer is available

      bufferToUpload.flip();
      final int partNum = this.partNumber++;

      Callable<CompletedPart> uploadTask =
          () -> {
            try {
              RequestBody requestBody = RequestBody.of(bufferToUpload);
              UploadPartRequest uploadRequest =
                  UploadPartRequest.builder()
                      .bucket(bucketName)
                      .key(blobName)
                      .uploadId(uploadId)
                      .partNumber(partNum)
                      .build();
              UploadPartResponse uploadPartResponse = client.uploadPart(uploadRequest, requestBody);
              return CompletedPart.builder()
                  .partNumber(partNum)
                  .eTag(uploadPartResponse.eTag())
                  .build();
            } finally {
              // Return buffer to the pool
              bufferToUpload.clear();
              bufferQueue.put(bufferToUpload);
            }
          };

      ListenableFuture<CompletedPart> future = executor.submit(uploadTask);
      completedParts.add(toApiFuture(future));

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for a buffer", e);
    }
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    if (!open) {
      return;
    }
    open = false;

    try {
      // If an upload was initiated and there's data in the current buffer, flush it.
      if (uploadId != null && currentBuffer.position() > 0) {
        flushBuffer();
      }

      // Wait for all parts to finish uploading.
      List<CompletedPart> parts = ApiFutures.allAsList(completedParts).get();

      if (!parts.isEmpty()) {
        completeUpload(parts);
        BlobId blobId = BlobId.of(bucketName, blobName);
        result.set(BlobInfo.newBuilder(blobId).build());
      } else if (uploadId != null) {
        // If no parts were successfully uploaded, but an upload was initiated, abort it.
        abortUpload();
        result.set(null);
      } else {
        // No upload was ever started.
        result.set(null);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException(e);
    } catch (ExecutionException e) {
      throw new IOException(e.getCause());
    } finally {
      // Return the current buffer to the pool
      if (currentBuffer != null) {
        currentBuffer.clear();
        bufferQueue.offer(currentBuffer);
        currentBuffer = null;
      }
      executor.shutdown();
    }
  }

  private void completeUpload(List<CompletedPart> parts) {
    CompletedMultipartUpload completedMultipartUpload =
        CompletedMultipartUpload.builder().parts(parts).build();
    CompleteMultipartUploadRequest completeRequest =
        CompleteMultipartUploadRequest.builder()
            .uploadId(uploadId)
            .bucket(bucketName)
            .key(blobName)
            .multipartUpload(completedMultipartUpload)
            .build();
    client.completeMultipartUpload(completeRequest);
  }

  private void abortUpload() {
    AbortMultipartUploadRequest abortRequest =
        AbortMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(blobName)
            .uploadId(uploadId)
            .build();
    client.abortMultipartUpload(abortRequest);
  }

  private static <T> ApiFuture<T> toApiFuture(ListenableFuture<T> listenableFuture) {
    SettableApiFuture<T> settable = SettableApiFuture.create();
    Futures.addCallback(
        listenableFuture,
        new FutureCallback<T>() {
          @Override
  public void onSuccess(T result) {
            settable.set(result);
          }

          @Override
  public void onFailure(Throwable t) {
            settable.setException(t);
          }
        },
        MoreExecutors.directExecutor());
    return settable;
  }
}
