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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.google.cloud.WriteChannel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageOperationsTest {
  private Storage storage;
  private StorageOperations storageOperations;

  private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder("b", "n").build();
  private static final int DEFAULT_BUFFER_SIZE = 15 * 1024 * 1024;
  private static final int MIN_BUFFER_SIZE = 256 * 1024;

  @Before
  public void setUp() {
    storage = createStrictMock(Storage.class);
    storageOperations = new StorageOperations(storage);
  }

  @After
  public void tearDown() throws Exception {
    verify(storage);
  }

  @Test
  public void testUploadFromNonExistentFile() {
    replay(storage);
    String fileName = "non_existing_file.txt";
    try {
      storageOperations.upload(BLOB_INFO, Paths.get(fileName));
      storageOperations.upload(BLOB_INFO, Paths.get(fileName), -1);
      fail();
    } catch (IOException e) {
      assertEquals(NoSuchFileException.class, e.getClass());
      assertEquals(fileName, e.getMessage());
    }
  }

  @Test
  public void testUploadFromDirectory() throws IOException {
    replay(storage);
    Path dir = Files.createTempDirectory("unit_");
    try {
      storageOperations.upload(BLOB_INFO, dir);
      storageOperations.upload(BLOB_INFO, dir, -2);
      fail();
    } catch (StorageException e) {
      assertEquals(dir + " is a directory", e.getMessage());
    }
  }

  private void prepareForUpload(BlobInfo blobInfo, byte[] bytes, Storage.BlobWriteOption... options)
      throws Exception {
    prepareForUpload(blobInfo, bytes, DEFAULT_BUFFER_SIZE, options);
  }

  private void prepareForUpload(
      BlobInfo blobInfo, byte[] bytes, int bufferSize, Storage.BlobWriteOption... options)
      throws Exception {
    WriteChannel channel = createStrictMock(WriteChannel.class);
    ByteBuffer expectedByteBuffer = ByteBuffer.wrap(bytes, 0, bytes.length);
    channel.setChunkSize(bufferSize);
    expect(channel.write(expectedByteBuffer)).andReturn(bytes.length);
    channel.close();
    replay(channel);
    expect(storage.writer(blobInfo, options)).andReturn(channel);
    replay(storage);
  }

  @Test
  public void testUploadFromFile() throws Exception {
    byte[] dataToSend = {1, 2, 3};
    prepareForUpload(BLOB_INFO, dataToSend);
    Path tempFile = Files.createTempFile("testUpload", ".tmp");
    Files.write(tempFile, dataToSend);
    storageOperations.upload(BLOB_INFO, tempFile);
  }

  @Test
  public void testUploadFromStream() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4, 5};
    Storage.BlobWriteOption[] options =
        new Storage.BlobWriteOption[] {Storage.BlobWriteOption.crc32cMatch()};
    prepareForUpload(BLOB_INFO, dataToSend, options);
    InputStream input = new ByteArrayInputStream(dataToSend);
    storageOperations.upload(BLOB_INFO, input, options);
  }

  @Test
  public void testUploadSmallBufferSize() throws Exception {
    byte[] dataToSend = new byte[100_000];
    prepareForUpload(BLOB_INFO, dataToSend, MIN_BUFFER_SIZE);
    InputStream input = new ByteArrayInputStream(dataToSend);
    int smallBufferSize = 100;
    storageOperations.upload(BLOB_INFO, input, smallBufferSize);
  }

  @Test
  public void testUploadFromIOException() throws Exception {
    IOException ioException = new IOException("message");
    WriteChannel channel = createStrictMock(WriteChannel.class);
    channel.setChunkSize(DEFAULT_BUFFER_SIZE);
    expect(channel.write((ByteBuffer) anyObject())).andThrow(ioException);
    replay(channel);
    expect(storage.writer(eq(BLOB_INFO))).andReturn(channel);
    replay(storage);
    InputStream input = new ByteArrayInputStream(new byte[10]);
    try {
      storageOperations.upload(BLOB_INFO, input);
      fail();
    } catch (IOException e) {
      assertSame(e, ioException);
    }
  }

  @Test
  public void testUploadMultiplePortions() throws Exception {
    int totalSize = 400_000;
    int bufferSize = 300_000;
    byte[] dataToSend = new byte[totalSize];
    dataToSend[0] = 42;
    dataToSend[bufferSize] = 43;

    WriteChannel channel = createStrictMock(WriteChannel.class);
    channel.setChunkSize(bufferSize);
    expect(channel.write(ByteBuffer.wrap(dataToSend, 0, bufferSize))).andReturn(1);
    expect(channel.write(ByteBuffer.wrap(dataToSend, bufferSize, totalSize - bufferSize)))
        .andReturn(2);
    channel.close();
    replay(channel);
    expect(storage.writer(BLOB_INFO)).andReturn(channel);
    replay(storage);

    InputStream input = new ByteArrayInputStream(dataToSend);
    storageOperations.upload(BLOB_INFO, input, bufferSize);
  }
}
