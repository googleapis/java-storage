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

/**
 * A client for Cloud Storage - Unified object storage.
 *
 * <p>Here's a simple usage example the Java Storage client. This example shows how to create a
 * Storage object.
 *
 * <pre>{@code
 * Storage storage = StorageOptions.getDefaultInstance().getService();
 * BlobId blobId = BlobId.of("bucket", "blob_name");
 * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
 * Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
 * }</pre>
 *
 * <p>This second example shows how to update an object's content if the object exists.
 *
 * <pre>{@code
 * Storage storage = StorageOptions.getDefaultInstance().getService();
 * BlobId blobId = BlobId.of("bucket", "blob_name");
 * Blob blob = storage.get(blobId);
 * if (blob != null) {
 *   byte[] prevContent = blob.getContent();
 *   System.out.println(new String(prevContent, UTF_8));
 *   WritableByteChannel channel = blob.writer();
 *   channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
 *   channel.close();
 * }
 * }</pre>
 *
 * <p>For more detailed code examples, see the <a
 * href="https://cloud.google.com/storage/docs/samples">sample library</a>.
 *
 * <p>When using google-cloud from outside of App/Compute Engine, you have to <a
 * href="https://github.com/googleapis/google-cloud-java#specifying-a-project-id">specify a project
 * ID</a> and <a href="https://github.com/googleapis/google-cloud-java#authentication">provide
 * credentials</a>.
 *
 * @see <a href="https://cloud.google.com/storage/">Google Cloud Storage</a>
 */
package com.google.cloud.storage;
