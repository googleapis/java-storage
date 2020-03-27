/*
 * Copyright 2020 Google LLC
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

import com.google.cloud.WriteChannel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility methods to perform various operations with the Storage such as upload.
 *
 * <p>Example of uploading files from a folder:
 *
 * <pre>{@code
 * File folder = new File("pictures/");
 * StorageUtils utils = StorageUtils.create(storage);
 * for (File file: folder.listFiles()) {
 *     if (!file.isDirectory()) {
 *         BlobInfo blobInfo = BlobInfo.newBuilder(BUCKET, file.getName()).build();
 *         try {
 *             utils.upload(blobInfo, file.toPath());
 *         } catch (IOException e) {
 *             System.err.println("Unable to upload " + file + ": " + e.getMessage());
 *         }
 *     }
 * }
 * }</pre>
 */
public final class StorageUtils {

  /** The instance of the Storage the utilities are associated with. */
  public final Storage storage;

  private static final int DEFAULT_BUFFER_SIZE = 15 * 1024 * 1024;
  private static final int MIN_BUFFER_SIZE = 256 * 1024;

  private StorageUtils(Storage storage) {
    this.storage = storage;
  }

  /**
   * Creates a new utility object associated with the given storage.
   *
   * @param storage the Storage
   * @return an instance which refers to {@code storage}
   */
  public static StorageUtils create(Storage storage) {
    return new StorageUtils(storage);
  }

  /**
   * Uploads the given {@code path} to the blob using {@link Storage#writer}. By default any MD5 and
   * CRC32C values in the given {@code blobInfo} are ignored unless requested via the {@link
   * Storage.BlobWriteOption#md5Match()} and {@link Storage.BlobWriteOption#crc32cMatch()} options.
   * Folder upload is not supported.
   *
   * <p>Example of uploading a file:
   *
   * <pre>{@code
   * String bucketName = "my-unique-bucket";
   * String fileName = "readme.txt";
   * BlobId blobId = BlobId.of(bucketName, fileName);
   * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
   * StorageUtils.create(storage).upload(blobInfo, Paths.get(fileName));
   * }</pre>
   *
   * @param blobInfo blob to create
   * @param path file to upload
   * @param options blob write options
   * @throws IOException on I/O error
   * @throws StorageException on failure
   * @see #upload(BlobInfo, Path, int, Storage.BlobWriteOption...)
   */
  public void upload(BlobInfo blobInfo, Path path, Storage.BlobWriteOption... options)
      throws IOException {
    upload(blobInfo, path, DEFAULT_BUFFER_SIZE, options);
  }

  /**
   * Uploads the given {@code path} to the blob using {@link Storage#writer} and the given {@code
   * bufferSize}. By default any MD5 and CRC32C values in the given {@code blobInfo} are ignored
   * unless requested via the {@link Storage.BlobWriteOption#md5Match()} and {@link
   * Storage.BlobWriteOption#crc32cMatch()} options. Folder upload is not supported.
   *
   * <p>{@link #upload(BlobInfo, Path, Storage.BlobWriteOption...)} invokes this one with a buffer
   * size of 15 MiB. Users can pass alternative values. Larger buffer sizes might improve the upload
   * performance but require more memory. This can cause an OutOfMemoryError or add significant
   * garbage collection overhead. Smaller buffer sizes reduce memory consumption, that is noticeable
   * when uploading many objects in parallel. Buffer sizes less than 256 KiB are treated as 256 KiB.
   *
   * <p>Example of uploading a humongous file:
   *
   * <pre>{@code
   * BlobId blobId = BlobId.of(bucketName, blobName);
   * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("video/webm").build();
   *
   * int largeBufferSize = 150 * 1024 * 1024;
   * Path file = Paths.get("humongous.file");
   * StorageUtils.create(storage).upload(blobInfo, file, largeBufferSize);
   * }</pre>
   *
   * @param blobInfo blob to create
   * @param path file to upload
   * @param bufferSize size of the buffer I/O operations
   * @param options blob write options
   * @throws IOException on I/O error
   * @throws StorageException on failure
   */
  public void upload(
      BlobInfo blobInfo, Path path, int bufferSize, Storage.BlobWriteOption... options)
      throws IOException {
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }
    try (InputStream input = Files.newInputStream(path)) {
      upload(blobInfo, input, bufferSize, options);
    }
  }

  /**
   * Uploads the given {@code content} to the blob using {@link Storage#writer}. By default any MD5
   * and CRC32C values in the given {@code blobInfo} are ignored unless requested via the {@link
   * Storage.BlobWriteOption#md5Match()} and {@link Storage.BlobWriteOption#crc32cMatch()} options.
   *
   * <p>Example of uploading data with CRC32C checksum:
   *
   * <pre>{@code
   * BlobId blobId = BlobId.of(bucketName, blobName);
   * byte[] content = "Hello, world".getBytes(UTF_8);
   * Hasher hasher = Hashing.crc32c().newHasher().putBytes(content);
   * String crc32c = BaseEncoding.base64().encode(Ints.toByteArray(hasher.hash().asInt()));
   * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(crc32c).build();
   * StorageUtils.create(storage).upload(blobInfo, new ByteArrayInputStream(content),
   *     Storage.BlobWriteOption.crc32cMatch());
   * }</pre>
   *
   * @param blobInfo blob to create
   * @param content content to upload
   * @param options blob write options
   * @throws IOException on I/O error
   * @throws StorageException on failure
   * @see #upload(BlobInfo, InputStream, int, Storage.BlobWriteOption...)
   */
  public void upload(BlobInfo blobInfo, InputStream content, Storage.BlobWriteOption... options)
      throws IOException {
    upload(blobInfo, content, DEFAULT_BUFFER_SIZE, options);
  }

  /**
   * Uploads the given {@code content} to the blob using {@link Storage#writer} and the given {@code
   * bufferSize}. By default any MD5 and CRC32C values in the given {@code blobInfo} are ignored
   * unless requested via the {@link Storage.BlobWriteOption#md5Match()} and {@link
   * Storage.BlobWriteOption#crc32cMatch()} options.
   *
   * <p>{@link #upload(BlobInfo, InputStream, Storage.BlobWriteOption...)} )} invokes this method
   * with a buffer size of 15 MiB. Users can pass alternative values. Larger buffer sizes might
   * improve the upload performance but require more memory. This can cause an OutOfMemoryError or
   * add significant garbage collection overhead. Smaller buffer sizes reduce memory consumption,
   * that is noticeable when uploading many objects in parallel. Buffer sizes less than 256 KiB are
   * treated as 256 KiB.
   *
   * @param blobInfo blob to create
   * @param content content to upload
   * @param bufferSize size of the buffer I/O operations
   * @param options blob write options
   * @throws IOException on I/O error
   * @throws StorageException on failure
   */
  public void upload(
      BlobInfo blobInfo, InputStream content, int bufferSize, Storage.BlobWriteOption... options)
      throws IOException {
    try (WriteChannel writer = storage.writer(blobInfo, options)) {
      upload(Channels.newChannel(content), writer, bufferSize);
    }
  }

  /*
   * Uploads the given content to the storage using specified write channel and the given buffer
   * size. This method does not close any channels.
   */
  private static void upload(ReadableByteChannel reader, WriteChannel writer, int bufferSize)
      throws IOException {
    bufferSize = Math.max(bufferSize, MIN_BUFFER_SIZE);
    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
    writer.setChunkSize(bufferSize);

    while (reader.read(buffer) >= 0) {
      buffer.flip();
      writer.write(buffer);
      buffer.clear();
    }
  }
}
