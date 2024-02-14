/*
 * Copyright 2016 Google LLC
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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.spi.v1.HttpStorageRpc;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageBatchTest {

  private static final BlobId BLOB_ID = BlobId.of("b1", "n1");
  private static final BlobId BLOB_ID_COMPLETE = BlobId.of("b1", "n1", 42L);
  private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder(BLOB_ID).build();
  private static final BlobInfo BLOB_INFO_COMPLETE =
      BlobInfo.newBuilder(BLOB_ID_COMPLETE).setMetageneration(42L).build();
  private static final BlobGetOption[] BLOB_GET_OPTIONS = {
    BlobGetOption.generationMatch(42L), BlobGetOption.metagenerationMatch(42L)
  };
  private static final BlobSourceOption[] BLOB_SOURCE_OPTIONS = {
    BlobSourceOption.generationMatch(42L), BlobSourceOption.metagenerationMatch(42L)
  };
  private static final GoogleJsonError GOOGLE_JSON_ERROR = new GoogleJsonError();
  private final HttpRetryAlgorithmManager retryAlgorithmManager =
      HttpStorageOptions.getDefaultInstance().getRetryAlgorithmManager();

  private HttpStorageOptions optionsMock;
  private HttpStorageRpc storageRpcMock;
  private RpcBatch batchMock;
  private StorageBatch storageBatch;
  private final Storage storage = EasyMock.createStrictMock(Storage.class);

  @Before
  public void setUp() {
    optionsMock = EasyMock.createMock(HttpStorageOptions.class);
    storageRpcMock = EasyMock.createMock(HttpStorageRpc.class);
    batchMock = EasyMock.createMock(RpcBatch.class);
    EasyMock.expect(optionsMock.getStorageRpcV1()).andReturn(storageRpcMock);
    EasyMock.expect(optionsMock.getRetryAlgorithmManager())
        .andReturn(retryAlgorithmManager)
        .anyTimes();
    EasyMock.expect(storageRpcMock.createBatch()).andReturn(batchMock);
    EasyMock.replay(optionsMock, storageRpcMock, batchMock, storage);
    storageBatch = new StorageBatch(optionsMock);
  }

  @After
  public void tearDown() {
    EasyMock.verify(batchMock, storageRpcMock, optionsMock, storage);
  }

  @Test
  public void testConstructor() {
    assertSame(batchMock, storageBatch.getBatch());
    assertSame(optionsMock, storageBatch.getOptions());
    assertSame(storageRpcMock, storageBatch.getStorageRpc());
  }

  @Test
  public void testDelete() {
    EasyMock.reset(batchMock);
    Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
    batchMock.addDelete(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO)),
        EasyMock.capture(callback),
        EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    EasyMock.replay(batchMock);
    StorageBatchResult<Boolean> batchResult =
        storageBatch.delete(BLOB_ID.getBucket(), BLOB_ID.getName());
    assertNotNull(callback.getValue());
    try {
      batchResult.get();
      fail("No result available yet.");
    } catch (IllegalStateException ex) {
      // expected
    }
    // testing error here, success is tested with options
    RpcBatch.Callback<Void> capturedCallback = callback.getValue();
    capturedCallback.onFailure(GOOGLE_JSON_ERROR);
    try {
      batchResult.get();
      fail("Should throw a StorageExcetion on error.");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testDeleteWithOptions() {
    EasyMock.reset(batchMock);
    Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
    Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
    batchMock.addDelete(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO)),
        EasyMock.capture(callback),
        EasyMock.capture(capturedOptions));
    EasyMock.replay(batchMock);
    StorageBatchResult<Boolean> batchResult = storageBatch.delete(BLOB_ID, BLOB_SOURCE_OPTIONS);
    assertNotNull(callback.getValue());
    Map<StorageRpc.Option, Object> options = capturedOptions.getValue();
    assertEquals(2, options.size());
    assertThat(options).containsEntry(StorageRpc.Option.IF_GENERATION_MATCH, 42L);
    assertThat(options).containsEntry(StorageRpc.Option.IF_METAGENERATION_MATCH, 42L);
    RpcBatch.Callback<Void> capturedCallback = callback.getValue();
    capturedCallback.onSuccess(null);
    assertTrue(batchResult.get());
  }

  @Test
  public void testUpdate() {
    EasyMock.reset(batchMock);
    Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
    batchMock.addPatch(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO)),
        EasyMock.capture(callback),
        EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    EasyMock.replay(batchMock);
    StorageBatchResult<Blob> batchResult = storageBatch.update(BLOB_INFO);
    assertNotNull(callback.getValue());
    try {
      batchResult.get();
      fail("No result available yet.");
    } catch (IllegalStateException ex) {
      // expected
    }
    // testing error here, success is tested with options
    RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
    capturedCallback.onFailure(GOOGLE_JSON_ERROR);
    try {
      batchResult.get();
      fail("Should throw a StorageExcetion on error.");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testUpdateWithOptions() {
    EasyMock.reset(storage, batchMock, optionsMock);
    EasyMock.expect(storage.getOptions()).andReturn(optionsMock).anyTimes();
    EasyMock.expect(optionsMock.getService()).andReturn(storage).anyTimes();
    EasyMock.expect(optionsMock.getRetryAlgorithmManager())
        .andReturn(retryAlgorithmManager)
        .anyTimes();
    Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
    Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
    batchMock.addPatch(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO_COMPLETE)),
        EasyMock.capture(callback),
        EasyMock.capture(capturedOptions));
    EasyMock.replay(batchMock, storage, optionsMock);
    StorageBatchResult<Blob> batchResult =
        storageBatch.update(
            BLOB_INFO_COMPLETE,
            BlobTargetOption.generationMatch(),
            BlobTargetOption.metagenerationMatch());
    assertNotNull(callback.getValue());
    assertEquals(2, capturedOptions.getValue().size());
    assertEquals(42L, capturedOptions.getValue().get(StorageRpc.Option.IF_GENERATION_MATCH));
    assertEquals(42L, capturedOptions.getValue().get(StorageRpc.Option.IF_METAGENERATION_MATCH));
    RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
    capturedCallback.onSuccess(Conversions.json().blobInfo().encode(BLOB_INFO));
    assertEquals(new Blob(storage, new Blob.BuilderImpl(BLOB_INFO)), batchResult.get());
  }

  @Test
  public void testGet() {
    EasyMock.reset(batchMock);
    Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
    batchMock.addGet(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO)),
        EasyMock.capture(callback),
        EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    EasyMock.replay(batchMock);
    StorageBatchResult<Blob> batchResult = storageBatch.get(BLOB_ID.getBucket(), BLOB_ID.getName());
    assertNotNull(callback.getValue());
    try {
      batchResult.get();
      fail("No result available yet.");
    } catch (IllegalStateException ex) {
      // expected
    }
    // testing error here, success is tested with options
    RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
    capturedCallback.onFailure(GOOGLE_JSON_ERROR);
    try {
      batchResult.get();
      fail("Should throw a StorageExcetion on error.");
    } catch (StorageException ex) {
      // expected
    }
  }

  @Test
  public void testGetWithOptions() {
    EasyMock.reset(storage, batchMock, optionsMock);
    EasyMock.expect(storage.getOptions()).andReturn(optionsMock).anyTimes();
    EasyMock.expect(optionsMock.getService()).andReturn(storage).anyTimes();
    EasyMock.expect(optionsMock.getRetryAlgorithmManager())
        .andReturn(retryAlgorithmManager)
        .anyTimes();
    Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
    Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
    batchMock.addGet(
        EasyMock.eq(Conversions.json().blobInfo().encode(BLOB_INFO)),
        EasyMock.capture(callback),
        EasyMock.capture(capturedOptions));
    EasyMock.replay(storage, batchMock, optionsMock);
    StorageBatchResult<Blob> batchResult = storageBatch.get(BLOB_ID, BLOB_GET_OPTIONS);
    assertNotNull(callback.getValue());
    Map<StorageRpc.Option, Object> options = capturedOptions.getValue();
    assertEquals(2, options.size());
    assertThat(options).containsEntry(StorageRpc.Option.IF_GENERATION_MATCH, 42L);
    assertThat(options).containsEntry(StorageRpc.Option.IF_METAGENERATION_MATCH, 42L);
    RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
    capturedCallback.onSuccess(Conversions.json().blobInfo().encode(BLOB_INFO));
    assertEquals(new Blob(storage, new Blob.BuilderImpl(BLOB_INFO)), batchResult.get());
  }
}
