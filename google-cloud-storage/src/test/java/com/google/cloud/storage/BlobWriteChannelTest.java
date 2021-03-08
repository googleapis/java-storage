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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureLong;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BlobWriteChannelTest {

  private static final String BUCKET_NAME = "b";
  private static final String BLOB_NAME = "n";
  private static final String UPLOAD_ID = "uploadid";
  private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder(BUCKET_NAME, BLOB_NAME).build();
  private static final StorageObject UPDATED_BLOB = new StorageObject();
  private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();
  private static final int MIN_CHUNK_SIZE = 256 * 1024;
  private static final int DEFAULT_CHUNK_SIZE = 60 * MIN_CHUNK_SIZE; // 15MiB
  private static final int CUSTOM_CHUNK_SIZE = 4 * MIN_CHUNK_SIZE;
  private static final Random RANDOM = new Random();
  private static final String SIGNED_URL =
      "http://www.test.com/test-bucket/test1.txt?GoogleAccessId=testClient-test@test.com&Expires=1553839761&Signature=MJUBXAZ7";
  private static final StorageException socketClosedException =
      new StorageException(new SocketException("Socket closed"));
  private StorageOptions options;
  private StorageRpcFactory rpcFactoryMock;
  private StorageRpc storageRpcMock;
  private BlobWriteChannel writer;

  @Before
  public void setUp() {
    rpcFactoryMock = createMock(StorageRpcFactory.class);
    storageRpcMock = createMock(StorageRpc.class);
    expect(rpcFactoryMock.create(anyObject(StorageOptions.class))).andReturn(storageRpcMock);
    replay(rpcFactoryMock);
    options =
        StorageOptions.newBuilder()
            .setProjectId("projectid")
            .setServiceRpcFactory(rpcFactoryMock)
            .build();
  }

  @After
  public void tearDown() throws Exception {
    verify(rpcFactoryMock, storageRpcMock);
  }

  @Test
  public void testCreate() {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
  }

  @Test
  public void testCreateRetryableError() {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS))
        .andThrow(socketClosedException);
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
  }

  @Test
  public void testCreateNonRetryableError() {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS))
        .andThrow(new RuntimeException());
    replay(storageRpcMock);
    try {
      new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
      Assert.fail();
    } catch (RuntimeException ex) {
      assertNotNull(ex.getMessage());
    }
  }

  @Test
  public void testWriteWithoutFlush() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertEquals(MIN_CHUNK_SIZE, writer.write(ByteBuffer.allocate(MIN_CHUNK_SIZE)));
  }

  @Test
  public void testWriteWithFlushRetryChunk() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(0L);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithRetryFullChunk() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), (byte[]) anyObject(), eq(0), eq(0L), eq(MIN_CHUNK_SIZE), eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(0L);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                (byte[]) anyObject(),
                eq(0),
                eq((long) MIN_CHUNK_SIZE),
                eq(0),
                eq(true)))
        .andReturn(BLOB_INFO.toPb());
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    writer.close();
    assertFalse(writer.isOpen());
    assertNotNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithRemoteProgressMade() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    // Simulate GCS received 10 bytes but not the rest of the chunk
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(10L);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(10),
                eq(10L),
                eq(MIN_CHUNK_SIZE - 10),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithDriftRetryCase4() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn((long) MIN_CHUNK_SIZE);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq((long) MIN_CHUNK_SIZE),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
    capturedBuffer.reset();
    buffer.rewind();
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
  }

  @Test
  public void testWriteWithUnreachableRemoteOffset() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(MIN_CHUNK_SIZE + 10L);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    try {
      writer.write(buffer);
      fail("Expected StorageException");
    } catch (StorageException storageException) {
      // expected storageException
    }
    assertTrue(writer.isOpen());
    assertNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithRetryAndObjectMetadata() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(10L);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(10),
                eq(10L),
                eq(MIN_CHUNK_SIZE - 10),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    expect(storageRpcMock.get(BLOB_INFO.toPb(), Maps.newEnumMap(StorageRpc.Option.class)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    expect(storageRpcMock.get(BLOB_INFO.toPb(), Maps.newEnumMap(StorageRpc.Option.class)))
        .andReturn(BLOB_INFO.toPb().setSize(BigInteger.valueOf(MIN_CHUNK_SIZE)));
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    writer.close();
    assertFalse(writer.isOpen());
    assertNotNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithUploadCompletedByAnotherClient() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq((long) MIN_CHUNK_SIZE),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    try {
      writer.write(buffer);
      buffer.rewind();
      writer.write(buffer);
      buffer.rewind();
      writer.write(buffer);
      fail("Expected completed exception.");
    } catch (StorageException ex) {

    }
    assertTrue(writer.isOpen());
  }

  @Test
  public void testWriteWithLocalOffsetGoingBeyondRemoteOffset() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq((long) MIN_CHUNK_SIZE),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(0L);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    try {
      writer.write(buffer);
      buffer.rewind();
      writer.write(buffer);
      writer.close();
      fail("Expected completed exception.");
    } catch (StorageException ex) {
    }
    assertTrue(writer.isOpen());
  }

  @Test
  public void testGetCurrentUploadOffset() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(0L);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                (byte[]) anyObject(),
                eq(0),
                eq((long) MIN_CHUNK_SIZE),
                eq(0),
                eq(true)))
        .andReturn(BLOB_INFO.toPb());
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(MIN_CHUNK_SIZE);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    writer.close();
    assertFalse(writer.isOpen());
    assertNotNull(writer.getStorageObject());
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithLastFlushRetryChunkButCompleted() throws IOException {
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(true)))
        .andThrow(socketClosedException);
    expect(storageRpcMock.getCurrentUploadOffset(eq(UPLOAD_ID))).andReturn(-1L);
    expect(storageRpcMock.get(BLOB_INFO.toPb(), Maps.newEnumMap(StorageRpc.Option.class)))
        .andReturn(BLOB_INFO.toPb().setSize(BigInteger.valueOf(MIN_CHUNK_SIZE)));
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertEquals(MIN_CHUNK_SIZE, writer.write(buffer));
    writer.close();
    assertFalse(writer.isRetrying());
    assertFalse(writer.isOpen());
    assertNotNull(writer.getStorageObject());
    // Capture captures entire buffer of a chunk even when not completely used.
    // Making assert selective up to the size of MIN_CHUNK_SIZE
    assertArrayEquals(Arrays.copyOf(capturedBuffer.getValue(), MIN_CHUNK_SIZE), buffer.array());
  }

  @Test
  public void testWriteWithFlush() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(CUSTOM_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.setChunkSize(CUSTOM_CHUNK_SIZE);
    ByteBuffer buffer = randomBuffer(CUSTOM_CHUNK_SIZE);
    assertEquals(CUSTOM_CHUNK_SIZE, writer.write(buffer));
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
    assertNull(writer.getStorageObject());
  }

  @Test
  public void testWritesAndFlush() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(DEFAULT_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    ByteBuffer[] buffers = new ByteBuffer[DEFAULT_CHUNK_SIZE / MIN_CHUNK_SIZE];
    for (int i = 0; i < buffers.length; i++) {
      buffers[i] = randomBuffer(MIN_CHUNK_SIZE);
      assertEquals(MIN_CHUNK_SIZE, writer.write(buffers[i]));
      assertNull(writer.getStorageObject());
    }
    for (int i = 0; i < buffers.length; i++) {
      assertArrayEquals(
          buffers[i].array(),
          Arrays.copyOfRange(
              capturedBuffer.getValue(), MIN_CHUNK_SIZE * i, MIN_CHUNK_SIZE * (i + 1)));
    }
  }

  @Test
  public void testCloseWithoutFlush() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertTrue(writer.isOpen());
    writer.close();
    assertArrayEquals(new byte[0], capturedBuffer.getValue());
    assertFalse(writer.isOpen());
    assertSame(UPDATED_BLOB, writer.getStorageObject());
  }

  @Test
  public void testCloseWithFlush() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertTrue(writer.isOpen());
    writer.write(buffer);
    writer.close();
    assertEquals(DEFAULT_CHUNK_SIZE, capturedBuffer.getValue().length);
    assertArrayEquals(buffer.array(), Arrays.copyOf(capturedBuffer.getValue(), MIN_CHUNK_SIZE));
    assertFalse(writer.isOpen());
    assertSame(UPDATED_BLOB, writer.getStorageObject());
  }

  @Test
  public void testWriteClosed() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.close();
    try {
      writer.write(ByteBuffer.allocate(MIN_CHUNK_SIZE));
      fail("Expected BlobWriteChannel write to throw IOException");
    } catch (IOException ex) {
      // expected
    }
    assertSame(UPDATED_BLOB, writer.getStorageObject());
  }

  @Test
  public void testSaveAndRestore() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance(CaptureType.ALL);
    Capture<Long> capturedPosition = Capture.newInstance(CaptureType.ALL);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                captureLong(capturedPosition),
                eq(DEFAULT_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expectLastCall().times(2);
    replay(storageRpcMock);
    ByteBuffer buffer1 = randomBuffer(DEFAULT_CHUNK_SIZE);
    ByteBuffer buffer2 = randomBuffer(DEFAULT_CHUNK_SIZE);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    assertEquals(DEFAULT_CHUNK_SIZE, writer.write(buffer1));
    assertArrayEquals(buffer1.array(), capturedBuffer.getValues().get(0));
    assertEquals(new Long(0L), capturedPosition.getValues().get(0));
    RestorableState<WriteChannel> writerState = writer.capture();
    WriteChannel restoredWriter = writerState.restore();
    assertEquals(DEFAULT_CHUNK_SIZE, restoredWriter.write(buffer2));
    assertArrayEquals(buffer2.array(), capturedBuffer.getValues().get(1));
    assertEquals(new Long(DEFAULT_CHUNK_SIZE), capturedPosition.getValues().get(1));
  }

  @Test
  public void testSaveAndRestoreClosed() throws IOException {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    writer.close();
    RestorableState<WriteChannel> writerState = writer.capture();
    RestorableState<WriteChannel> expectedWriterState =
        BlobWriteChannel.StateImpl.builder(options, BLOB_INFO, UPLOAD_ID)
            .setBuffer(null)
            .setChunkSize(DEFAULT_CHUNK_SIZE)
            .setIsOpen(false)
            .setPosition(0)
            .build();
    WriteChannel restoredWriter = writerState.restore();
    assertArrayEquals(new byte[0], capturedBuffer.getValue());
    assertEquals(expectedWriterState, restoredWriter.capture());
  }

  @Test
  public void testStateEquals() {
    expect(storageRpcMock.open(BLOB_INFO.toPb(), EMPTY_RPC_OPTIONS)).andReturn(UPLOAD_ID).times(2);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    // avoid closing when you don't want partial writes to GCS upon failure
    @SuppressWarnings("resource")
    WriteChannel writer2 = new BlobWriteChannel(options, BLOB_INFO, EMPTY_RPC_OPTIONS);
    RestorableState<WriteChannel> state = writer.capture();
    RestorableState<WriteChannel> state2 = writer2.capture();
    assertEquals(state, state2);
    assertEquals(state.hashCode(), state2.hashCode());
    assertEquals(state.toString(), state2.toString());
  }

  @Test
  public void testWriteWithSignedURLAndWithoutFlush() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    assertEquals(MIN_CHUNK_SIZE, writer.write(ByteBuffer.allocate(MIN_CHUNK_SIZE)));
  }

  @Test
  public void testWriteWithSignedURLAndWithFlush() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(CUSTOM_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    writer.setChunkSize(CUSTOM_CHUNK_SIZE);
    ByteBuffer buffer = randomBuffer(CUSTOM_CHUNK_SIZE);
    assertEquals(CUSTOM_CHUNK_SIZE, writer.write(buffer));
    assertArrayEquals(buffer.array(), capturedBuffer.getValue());
  }

  @Test
  public void testWriteWithSignedURLAndFlush() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(DEFAULT_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    ByteBuffer[] buffers = new ByteBuffer[DEFAULT_CHUNK_SIZE / MIN_CHUNK_SIZE];
    for (int i = 0; i < buffers.length; i++) {
      buffers[i] = randomBuffer(MIN_CHUNK_SIZE);
      assertEquals(MIN_CHUNK_SIZE, writer.write(buffers[i]));
    }
    for (int i = 0; i < buffers.length; i++) {
      assertArrayEquals(
          buffers[i].array(),
          Arrays.copyOfRange(
              capturedBuffer.getValue(), MIN_CHUNK_SIZE * i, MIN_CHUNK_SIZE * (i + 1)));
    }
  }

  @Test
  public void testCloseWithSignedURLWithoutFlush() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    assertTrue(writer.isOpen());
    writer.close();
    assertArrayEquals(new byte[0], capturedBuffer.getValue());
    assertTrue(!writer.isOpen());
  }

  @Test
  public void testCloseWithSignedURLWithFlush() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    ByteBuffer buffer = randomBuffer(MIN_CHUNK_SIZE);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                eq(0L),
                eq(MIN_CHUNK_SIZE),
                eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    assertTrue(writer.isOpen());
    writer.write(buffer);
    writer.close();
    assertEquals(DEFAULT_CHUNK_SIZE, capturedBuffer.getValue().length);
    assertArrayEquals(buffer.array(), Arrays.copyOf(capturedBuffer.getValue(), MIN_CHUNK_SIZE));
    assertTrue(!writer.isOpen());
  }

  @Test
  public void testWriteWithSignedURLClosed() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance();
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true)))
        .andReturn(UPDATED_BLOB);
    replay(storageRpcMock);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    writer.close();
    try {
      writer.write(ByteBuffer.allocate(MIN_CHUNK_SIZE));
      fail("Expected BlobWriteChannel write to throw IOException");
    } catch (IOException ex) {
      // expected
    }
  }

  @Test
  public void testSaveAndRestoreWithSignedURL() throws IOException {
    expect(storageRpcMock.open(SIGNED_URL)).andReturn(UPLOAD_ID);
    Capture<byte[]> capturedBuffer = Capture.newInstance(CaptureType.ALL);
    Capture<Long> capturedPosition = Capture.newInstance(CaptureType.ALL);
    expect(
            storageRpcMock.writeWithResponse(
                eq(UPLOAD_ID),
                capture(capturedBuffer),
                eq(0),
                captureLong(capturedPosition),
                eq(DEFAULT_CHUNK_SIZE),
                eq(false)))
        .andReturn(null);
    expectLastCall().times(2);
    replay(storageRpcMock);
    ByteBuffer buffer1 = randomBuffer(DEFAULT_CHUNK_SIZE);
    ByteBuffer buffer2 = randomBuffer(DEFAULT_CHUNK_SIZE);
    writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    assertEquals(DEFAULT_CHUNK_SIZE, writer.write(buffer1));
    assertArrayEquals(buffer1.array(), capturedBuffer.getValues().get(0));
    assertEquals(new Long(0L), capturedPosition.getValues().get(0));
    RestorableState<WriteChannel> writerState = writer.capture();
    WriteChannel restoredWriter = writerState.restore();
    assertEquals(DEFAULT_CHUNK_SIZE, restoredWriter.write(buffer2));
    assertArrayEquals(buffer2.array(), capturedBuffer.getValues().get(1));
    assertEquals(new Long(DEFAULT_CHUNK_SIZE), capturedPosition.getValues().get(1));
  }

  @Test
  public void testRuntimeExceptionWithSignedURL() throws MalformedURLException {
    String exceptionMessage = "invalid signedURL";
    expect(new BlobWriteChannel(options, new URL(SIGNED_URL)))
        .andThrow(new RuntimeException(exceptionMessage));
    replay(storageRpcMock);
    try {
      writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
      Assert.fail();
    } catch (StorageException ex) {
      assertNotNull(ex.getMessage());
    }
  }

  private static ByteBuffer randomBuffer(int size) {
    byte[] byteArray = new byte[size];
    RANDOM.nextBytes(byteArray);
    return ByteBuffer.wrap(byteArray);
  }
}
