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
import java.util.concurrent.Executors;

public class MultipartUploadWriteChannel implements StorageWriteChannel {

  private static final int MIN_CHUNK_SIZE = 5 * 1024 * 1024; // 5MB
  private static final int POOL_SIZE = 16;

  private final SettableApiFuture<BlobInfo> result;
  private final String bucketName;
  private final String blobName;
  private final String uploadId;
  private final MultipartUploadClientImpl client;
  private final ListeningExecutorService executor =
      MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_SIZE));
  private final BlockingQueue<ByteBuffer> bufferQueue;
  private ByteBuffer currentBuffer;
  List<ApiFuture<CompletedPart>> completedParts = new ArrayList<>();
  private int partNumber = 1;
  private boolean open = true;

  MultipartUploadWriteChannel(
      BlobReadChannelContext context, String bucketName, String blobName) {
    this.result = SettableApiFuture.create();
    this.bucketName = bucketName;
    this.blobName = blobName;

    this.bufferQueue = new ArrayBlockingQueue<>(POOL_SIZE);
    for (int i = 0; i < POOL_SIZE; i++) {
      //noinspection ResultOfMethodCallIgnored
      this.bufferQueue.offer(ByteBuffer.allocate(MIN_CHUNK_SIZE));
    }
    try {
      this.currentBuffer = bufferQueue.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }

    HttpStorageOptions options = context.getStorageOptions();
    this.client =
        new MultipartUploadClientImpl(
            context.getRetrier(),
            MultipartUploadHttpRequestManager.createFrom(options),
            options.getRetryAlgorithmManager());

    CreateMultipartUploadRequest createRequest =
        CreateMultipartUploadRequest.builder().bucket(bucketName).key(blobName).build();
    CreateMultipartUploadResponse createResponse = client.createMultipartUpload(createRequest);
    this.uploadId = createResponse.uploadId();
  }

  @Override
  public ApiFuture<BlobInfo> getObject() {
    return result;
  }

  @Override
  public void setChunkSize(int i) {
    // The chunk size is fixed at 5MB
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
    int bytesWritten = 0;
    while (src.hasRemaining()) {
      if (currentBuffer.remaining() == 0) {
        uploadPart();
      }
      int bytesToCopy = Math.min(src.remaining(), currentBuffer.remaining());
      int originalLimit = src.limit();
      src.limit(src.position() + bytesToCopy);
      currentBuffer.put(src);
      src.limit(originalLimit);
      bytesWritten += bytesToCopy;
    }
    return bytesWritten;
  }

  private void uploadPart() throws IOException {
    try {
      final ByteBuffer bufferToUpload = currentBuffer;
      currentBuffer = bufferQueue.take();

      bufferToUpload.flip();
      final int partNumber = this.partNumber++;
      Callable<CompletedPart> uploadTask =
          () -> {
            try {
              RequestBody requestBody = RequestBody.of(bufferToUpload);
              UploadPartRequest uploadRequest =
                  UploadPartRequest.builder()
                      .bucket(bucketName)
                      .key(blobName)
                      .uploadId(uploadId)
                      .partNumber(partNumber)
                      .build();
              UploadPartResponse uploadPartResponse = client.uploadPart(uploadRequest, requestBody);
              return CompletedPart.builder()
                  .partNumber(partNumber)
                  .eTag(uploadPartResponse.eTag())
                  .build();
            } finally {
              bufferToUpload.clear();
              bufferQueue.put(bufferToUpload);
            }
          };
      ListenableFuture<CompletedPart> listenableFuture = executor.submit(uploadTask);
      SettableApiFuture<CompletedPart> settableApiFuture = SettableApiFuture.create();
      Futures.addCallback(
          listenableFuture,
          new FutureCallback<CompletedPart>() {
            @Override
            public void onFailure(Throwable t) {
              settableApiFuture.setException(t);
            }

            @Override
            public void onSuccess(CompletedPart result) {
              settableApiFuture.set(result);
            }
          },
          executor);
      completedParts.add(settableApiFuture);
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
    if (currentBuffer.position() > 0) {
      uploadPart();
    }

    if (currentBuffer != null) {
      currentBuffer.clear();
      //noinspection ResultOfMethodCallIgnored
      bufferQueue.offer(currentBuffer);
      currentBuffer = null;
    }

    try {
      List<CompletedPart> parts = ApiFutures.allAsList(completedParts).get();
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
      result.set(null); // Or the actual BlobInfo if available
    } catch (Exception e) {
      throw new IOException(e);
    } finally {
      open = false;
      executor.shutdown();
    }
  }
}
