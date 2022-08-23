package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageBatchResult;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageFixture;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITBatchTest {
  private static final int MAX_BATCH_SIZE = 100;
  private static final String CONTENT_TYPE = "text/plain";

  private static Storage storage;
  private static String bucketName;

  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  @BeforeClass
  public static void setUp() {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
  }

  @Test
  public void testBatchRequest() {
    String sourceBlobName1 = "test-batch-request-blob-1";
    String sourceBlobName2 = "test-batch-request-blob-2";
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucketName, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucketName, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    assertNotNull(storage.create(sourceBlob2));

    // Batch update request
    BlobInfo updatedBlob1 = sourceBlob1.toBuilder().setContentType(CONTENT_TYPE).build();
    BlobInfo updatedBlob2 = sourceBlob2.toBuilder().setContentType(CONTENT_TYPE).build();
    StorageBatch updateBatch = storage.batch();
    StorageBatchResult<Blob> updateResult1 = updateBatch.update(updatedBlob1);
    StorageBatchResult<Blob> updateResult2 = updateBatch.update(updatedBlob2);
    updateBatch.submit();
    Blob remoteUpdatedBlob1 = updateResult1.get();
    Blob remoteUpdatedBlob2 = updateResult2.get();
    assertEquals(sourceBlob1.getBucket(), remoteUpdatedBlob1.getBucket());
    assertEquals(sourceBlob1.getName(), remoteUpdatedBlob1.getName());
    assertEquals(sourceBlob2.getBucket(), remoteUpdatedBlob2.getBucket());
    assertEquals(sourceBlob2.getName(), remoteUpdatedBlob2.getName());
    assertEquals(updatedBlob1.getContentType(), remoteUpdatedBlob1.getContentType());
    assertEquals(updatedBlob2.getContentType(), remoteUpdatedBlob2.getContentType());

    // Batch get request
    StorageBatch getBatch = storage.batch();
    StorageBatchResult<Blob> getResult1 = getBatch.get(bucketName, sourceBlobName1);
    StorageBatchResult<Blob> getResult2 = getBatch.get(bucketName, sourceBlobName2);
    getBatch.submit();
    Blob remoteBlob1 = getResult1.get();
    Blob remoteBlob2 = getResult2.get();
    assertEquals(remoteUpdatedBlob1, remoteBlob1);
    assertEquals(remoteUpdatedBlob2, remoteBlob2);

    // Batch delete request
    StorageBatch deleteBatch = storage.batch();
    StorageBatchResult<Boolean> deleteResult1 = deleteBatch.delete(bucketName, sourceBlobName1);
    StorageBatchResult<Boolean> deleteResult2 = deleteBatch.delete(bucketName, sourceBlobName2);
    deleteBatch.submit();
    assertTrue(deleteResult1.get());
    assertTrue(deleteResult2.get());
  }

  @Test
  public void testBatchRequestManyOperations() {
    List<StorageBatchResult<Boolean>> deleteResults =
        Lists.newArrayListWithCapacity(MAX_BATCH_SIZE);
    List<StorageBatchResult<Blob>> getResults = Lists.newArrayListWithCapacity(MAX_BATCH_SIZE / 2);
    List<StorageBatchResult<Blob>> updateResults =
        Lists.newArrayListWithCapacity(MAX_BATCH_SIZE / 2);
    StorageBatch batch = storage.batch();
    for (int i = 0; i < MAX_BATCH_SIZE; i++) {
      BlobId blobId = BlobId.of(bucketName, "test-batch-request-many-operations-blob-" + i);
      deleteResults.add(batch.delete(blobId));
    }
    for (int i = 0; i < MAX_BATCH_SIZE / 2; i++) {
      BlobId blobId = BlobId.of(bucketName, "test-batch-request-many-operations-blob-" + i);
      getResults.add(batch.get(blobId));
    }
    for (int i = 0; i < MAX_BATCH_SIZE / 2; i++) {
      BlobInfo blob =
          BlobInfo.newBuilder(BlobId.of(bucketName, "test-batch-request-many-operations-blob-" + i))
              .build();
      updateResults.add(batch.update(blob));
    }

    String sourceBlobName1 = "test-batch-request-many-operations-source-blob-1";
    String sourceBlobName2 = "test-batch-request-many-operations-source-blob-2";
    BlobInfo sourceBlob1 = BlobInfo.newBuilder(bucketName, sourceBlobName1).build();
    BlobInfo sourceBlob2 = BlobInfo.newBuilder(bucketName, sourceBlobName2).build();
    assertNotNull(storage.create(sourceBlob1));
    assertNotNull(storage.create(sourceBlob2));
    BlobInfo updatedBlob2 = sourceBlob2.toBuilder().setContentType(CONTENT_TYPE).build();

    StorageBatchResult<Blob> getResult = batch.get(bucketName, sourceBlobName1);
    StorageBatchResult<Blob> updateResult = batch.update(updatedBlob2);

    batch.submit();

    // Check deletes
    for (StorageBatchResult<Boolean> failedDeleteResult : deleteResults) {
      assertFalse(failedDeleteResult.get());
    }

    // Check gets
    for (StorageBatchResult<Blob> failedGetResult : getResults) {
      assertNull(failedGetResult.get());
    }
    Blob remoteBlob1 = getResult.get();
    assertEquals(sourceBlob1.getBucket(), remoteBlob1.getBucket());
    assertEquals(sourceBlob1.getName(), remoteBlob1.getName());

    // Check updates
    for (StorageBatchResult<Blob> failedUpdateResult : updateResults) {
      try {
        failedUpdateResult.get();
        fail("Expected StorageException");
      } catch (StorageException ex) {
        // expected
      }
    }
    Blob remoteUpdatedBlob2 = updateResult.get();
    assertEquals(sourceBlob2.getBucket(), remoteUpdatedBlob2.getBucket());
    assertEquals(sourceBlob2.getName(), remoteUpdatedBlob2.getName());
    assertEquals(updatedBlob2.getContentType(), remoteUpdatedBlob2.getContentType());
  }

  @Test
  public void testBatchRequestFail() {
    String blobName = "test-batch-request-blob-fail";
    BlobInfo blob = BlobInfo.newBuilder(bucketName, blobName).build();
    Blob remoteBlob = storage.create(blob);
    assertNotNull(remoteBlob);
    BlobInfo updatedBlob = BlobInfo.newBuilder(bucketName, blobName, -1L).build();
    StorageBatch batch = storage.batch();
    StorageBatchResult<Blob> updateResult =
        batch.update(updatedBlob, Storage.BlobTargetOption.generationMatch());
    StorageBatchResult<Boolean> deleteResult1 =
        batch.delete(bucketName, blobName, Storage.BlobSourceOption.generationMatch(-1L));
    StorageBatchResult<Boolean> deleteResult2 = batch.delete(BlobId.of(bucketName, blobName, -1L));
    StorageBatchResult<Blob> getResult1 =
        batch.get(bucketName, blobName, Storage.BlobGetOption.generationMatch(-1L));
    StorageBatchResult<Blob> getResult2 = batch.get(BlobId.of(bucketName, blobName, -1L));
    batch.submit();
    try {
      updateResult.get();
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    try {
      deleteResult1.get();
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    try {
      deleteResult2.get();
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }
    try {
      getResult1.get();
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    try {
      getResult2.get();
      fail("Expected an 'Invalid argument' exception");
    } catch (StorageException e) {
      assertThat(e.getMessage()).contains("Invalid argument");
    }
  }
}
