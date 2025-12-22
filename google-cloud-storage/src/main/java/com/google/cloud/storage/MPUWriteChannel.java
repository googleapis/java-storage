/*
 * Copyright 2024 Google LLC
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

// package com.google.cloud.storage;
//
// import com.google.api.core.ApiFuture;
// import com.google.api.core.SettableApiFuture;
// import com.google.cloud.RestorableState;
// import com.google.cloud.WriteChannel;
// import com.google.cloud.storage.Conversions.Decoder;
// import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
// import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
// import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
// import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
// import com.google.cloud.storage.multipartupload.model.CompletedPart;
// import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
// import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
// import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
// import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
// import com.google.storage.v2.Object;
// import com.google.storage.v2.WriteObjectResponse;
// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.Callable;
//
// final class MPUWriteChannel extends BaseStorageWriteChannel<WriteObjectResponse> {
//
//   private final MultipartUploadClient multipartUploadClient;
//   private final CreateMultipartUploadRequest createRequest;
//
//   MPUWriteChannel(
//       MultipartUploadClient multipartUploadClient, CreateMultipartUploadRequest createRequest) {
//     super(decode());
//     this.multipartUploadClient = multipartUploadClient;
//     this.createRequest = createRequest;
//   }
//
//   @Override
//   public RestorableState<WriteChannel> capture() {
//     return CrossTransportUtils.throwHttpJsonOnly(WriteChannel.class, "capture");
//   }
//
//   @Override
//   protected LazyWriteChannel<WriteObjectResponse> newLazyWriteChannel() {
//     return new LazyWriteChannel<>(
//         () ->
//             new UnbufferedWritableByteChannelSession<>(
//                 new MultipartUploadSession(multipartUploadClient, createRequest),
//                 UnbufferedWritableByteChannel.of()));
//   }
//
//   private static class MultipartUploadSession
//       implements Callable<UnbufferedWritableByteChannel> {
//
//     private final SettableApiFuture<WriteObjectResponse> result = SettableApiFuture.create();
//     private final MultipartUploadClient client;
//     private final CreateMultipartUploadRequest createRequest;
//
//     private String uploadId;
//     private final List<CompletedPart> parts = new ArrayList<>();
//     private int partNumber = 1;
//     private boolean open = true;
//
//     private MultipartUploadSession(
//         MultipartUploadClient client, CreateMultipartUploadRequest createRequest) {
//       this.client = client;
//       this.createRequest = createRequest;
//     }
//
//     @Override
//     public UnbufferedWritableByteChannel call() throws Exception {
//       CreateMultipartUploadResponse createResponse = client.createMultipartUpload(createRequest);
//       this.uploadId = createResponse.getUploadId();
//       return new UnbufferedWritableByteChannel() {
//         @Override
//         public int write(ByteBuffer src) throws IOException {
//           if (!open) {
//             throw new IOException("Channel is closed.");
//           }
//           int remaining = src.remaining();
//           UploadPartRequest partRequest =
//               UploadPartRequest.newBuilder(
//                       createRequest.getBucket(), createRequest.getObject(), uploadId, partNumber)
//                   .build();
//           UploadPartResponse partResponse =
//               client.uploadPart(partRequest, RequestBody.of(src.duplicate()));
//           parts.add(
//               CompletedPart.newBuilder(partNumber, partResponse.getEtag())
//                   .build());
//           partNumber++;
//           return remaining;
//         }
//
//         @Override
//         public boolean isOpen() {
//           return open;
//         }
//
//         @Override
//         public void close() throws IOException {
//           if (!open) {
//             return;
//           }
//           open = false;
//           if (result.isCancelled()) {
//             client.abortMultipartUpload(
//                 AbortMultipartUploadRequest.newBuilder(
//                         createRequest.getBucket(), createRequest.getObject(), uploadId)
//                     .build());
//           } else {
//             CompleteMultipartUploadRequest completeRequest =
//                 CompleteMultipartUploadRequest.newBuilder(
//                         createRequest.getBucket(), createRequest.getObject(), uploadId, parts)
//                     .build();
//             CompleteMultipartUploadResponse completeResponse =
//                 client.completeMultipartUpload(completeRequest);
//
//             Object resource =
//                 Object.newBuilder()
//                     .setBucket(completeResponse.getBucket())
//                     .setName(completeResponse.getObject())
//                     .setEtag(completeResponse.getEtag())
//                     .setSize(completeResponse.getSize())
//                     .build();
//             WriteObjectResponse responseObject =
//                 WriteObjectResponse.newBuilder().setResource(resource).build();
//             result.set(responseObject);
//           }
//         }
//       };
//     }
//
//     ApiFuture<WriteObjectResponse> getResult() {
//       return result;
//     }
//   }
//
//   private static Decoder<WriteObjectResponse, BlobInfo> decode() {
//     return Conversions.grpc().blobInfo().compose(WriteObjectResponse::getResource);
//   }
// }