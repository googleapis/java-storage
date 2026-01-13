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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MultipartUploadWriteChannel2 implements StorageWriteChannel {

  private static final int MIN_CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

  private final SettableApiFuture<BlobInfo> result = SettableApiFuture.create();
  private final BlobReadChannelContext context;
  private final String bucketName;
  private final String blobName;
  private final List<CompletedPart> completedParts = new ArrayList<>();

  private MultipartUploadClientImpl client;
  private String uploadId;
  private ByteBuffer buffer;
  private int partNumber = 1;
  private boolean open = true;

  MultipartUploadWriteChannel2(BlobReadChannelContext context, String bucketName, String blobName) {
    this.context = context;
    this.bucketName = bucketName;
    this.blobName = blobName;
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
      if (buffer != null && buffer.position() > 0) {
        int freeSpace = buffer.remaining();
        int bytesToCopy = Math.min(src.remaining(), freeSpace);

        int oldLimit = src.limit();
        src.limit(src.position() + bytesToCopy);
        buffer.put(src);
        src.limit(oldLimit);

        bytesWritten += bytesToCopy;

        if (!buffer.hasRemaining()) {
          flushBuffer();
        }
      } else {
        if (src.remaining() >= MIN_CHUNK_SIZE) {
          int oldLimit = src.limit();
          src.limit(src.position() + MIN_CHUNK_SIZE);
          uploadPart(src);
          src.limit(oldLimit);
          bytesWritten += MIN_CHUNK_SIZE;
        } else {
          if (buffer == null) {
            buffer = ByteBuffer.allocate(MIN_CHUNK_SIZE);
          }
          int bytesToCopy = src.remaining();
          buffer.put(src);
          bytesWritten += bytesToCopy;
        }
      }
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
    if (buffer == null || buffer.position() == 0) {
      return;
    }
    buffer.flip();
    uploadPart(buffer);
    buffer.clear();
  }

  private void uploadPart(ByteBuffer content) throws IOException {
    try {
      RequestBody requestBody = RequestBody.of(content);
      UploadPartRequest uploadRequest =
          UploadPartRequest.builder()
              .bucket(bucketName)
              .key(blobName)
              .uploadId(uploadId)
              .partNumber(partNumber)
              .build();
      UploadPartResponse uploadPartResponse = client.uploadPart(uploadRequest, requestBody);
      completedParts.add(
          CompletedPart.builder().partNumber(partNumber).eTag(uploadPartResponse.eTag()).build());
      partNumber++;
    } catch (Exception e) {
      throw new IOException("Failed to upload part", e);
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
      if (uploadId != null) {
        if (buffer != null && buffer.position() > 0) {
          flushBuffer();
        }

        if (!completedParts.isEmpty()) {
          completeUpload();
          BlobId blobId = BlobId.of(bucketName, blobName);
          result.set(BlobInfo.newBuilder(blobId).build());
        } else {
          abortUpload();
          result.set(null);
        }
      } else {
        result.set(null);
      }
    } catch (Exception e) {
      if (uploadId != null) {
        try {
          abortUpload();
        } catch (Exception abortEx) {
          // ignore
        }
      }
      result.setException(e);
      throw new IOException(e);
    }
  }

  private void completeUpload() {
    CompletedMultipartUpload completedMultipartUpload =
        CompletedMultipartUpload.builder().parts(completedParts).build();
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
}
