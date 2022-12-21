/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.kms.v1.CreateCryptoKeyRequest;
import com.google.cloud.kms.v1.CreateKeyRingRequest;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.GetCryptoKeyRequest;
import com.google.cloud.kms.v1.GetKeyRingRequest;
import com.google.cloud.kms.v1.KeyManagementServiceGrpc;
import com.google.cloud.kms.v1.KeyManagementServiceGrpc.KeyManagementServiceBlockingStub;
import com.google.cloud.kms.v1.KeyRingName;
import com.google.cloud.kms.v1.LocationName;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.DataGeneration;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.registry.Generator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.io.BaseEncoding;
import com.google.iam.v1.Binding;
import com.google.iam.v1.IAMPolicyGrpc;
import com.google.iam.v1.SetIamPolicyRequest;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.MetadataUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
    transports = {Transport.HTTP, Transport.GRPC},
    backends = Backend.PROD)
public class ITKmsTest {

  private static final long seed = -7071346537822433445L;
  @Rule public final DataGeneration dataGeneration = new DataGeneration(new Random(seed));

  // TODO: either pull the scope of these resources up higher, or make them per-test distinct
  //   to insulate them from each other
  private String kmsKeyOneResourcePath;
  private String kmsKeyTwoResourcePath;
  private static final String KMS_KEY_RING_NAME = "gcs_test_kms_key_ring";
  private static final String KMS_KEY_RING_LOCATION = "us";
  private static final String KMS_KEY_ONE_NAME = "gcs_kms_key_one";
  private static final String KMS_KEY_TWO_NAME = "gcs_kms_key_two";
  private Metadata requestParamsHeader = new Metadata();
  private static Metadata.Key<String> requestParamsKey =
      Metadata.Key.of("x-goog-request-params", Metadata.ASCII_STRING_MARSHALLER);
  private ManagedChannel kmsChannel;
  private static final Logger log = Logger.getLogger(ITKmsTest.class.getName());
  private static final byte[] BLOB_BYTE_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";
  private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";
  private static final Key KEY =
      new SecretKeySpec(BaseEncoding.base64().decode(BASE64_KEY), "AES256");
  private static final String CONTENT_TYPE = "text/plain";

  @Inject public Storage storage;
  @Inject public BucketInfo bucket;
  @Inject public Generator generator;

  @Before
  public void setup() {
    // Prepare KMS KeyRing for CMEK tests
    // https://cloud.google.com/storage/docs/encryption/using-customer-managed-keys
    // We don't care currently if we are using HTTP or gRPC because
    // these values should be the same.
    StorageOptions storageOptions = storage.getOptions();
    String projectId = storageOptions.getProjectId();

    Credentials credentials = storageOptions.getCredentials();
    // TODO: can we switch all this manual grpc stuff for
    //   com.google.cloud.kms.v1.KeyManagementServiceClient?
    kmsChannel = ManagedChannelBuilder.forTarget("cloudkms.googleapis.com:443").build();
    KeyManagementServiceBlockingStub kmsStub =
        KeyManagementServiceGrpc.newBlockingStub(kmsChannel)
            .withCallCredentials(MoreCallCredentials.from(credentials));
    IAMPolicyGrpc.IAMPolicyBlockingStub iamStub =
        IAMPolicyGrpc.newBlockingStub(kmsChannel)
            .withCallCredentials(MoreCallCredentials.from(credentials));
    ensureKmsKeyRingExistsForTests(kmsStub, projectId, KMS_KEY_RING_LOCATION, KMS_KEY_RING_NAME);
    ensureKmsKeyRingIamPermissionsForTests(
        iamStub, projectId, KMS_KEY_RING_LOCATION, KMS_KEY_RING_NAME);
    kmsKeyOneResourcePath =
        ensureKmsKeyExistsForTests(
            kmsStub, projectId, KMS_KEY_RING_LOCATION, KMS_KEY_RING_NAME, KMS_KEY_ONE_NAME);
    kmsKeyTwoResourcePath =
        ensureKmsKeyExistsForTests(
            kmsStub, projectId, KMS_KEY_RING_LOCATION, KMS_KEY_RING_NAME, KMS_KEY_TWO_NAME);
  }

  @After
  public void after() {
    if (kmsChannel != null) {
      try {
        kmsChannel.shutdownNow();
      } catch (Exception e) {
        log.log(Level.WARNING, "Error while trying to shutdown kms channel", e);
      }
      kmsChannel = null;
    }
  }

  private String ensureKmsKeyRingExistsForTests(
      KeyManagementServiceBlockingStub kmsStub,
      String projectId,
      String location,
      String keyRingName)
      throws StatusRuntimeException {
    String kmsKeyRingResourcePath = KeyRingName.of(projectId, location, keyRingName).toString();
    try {
      // Attempt to Get KeyRing
      GetKeyRingRequest getKeyRingRequest =
          GetKeyRingRequest.newBuilder().setName(kmsKeyRingResourcePath).build();
      requestParamsHeader.put(requestParamsKey, "name=" + kmsKeyRingResourcePath);
      ClientInterceptor headersInterceptor =
          MetadataUtils.newAttachHeadersInterceptor(requestParamsHeader);
      KeyManagementServiceBlockingStub stubForGetKeyRing =
          kmsStub.withInterceptors(headersInterceptor);
      stubForGetKeyRing.getKeyRing(getKeyRingRequest);
    } catch (StatusRuntimeException ex) {
      if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
        // Create KmsKeyRing
        String keyRingParent = LocationName.of(projectId, location).toString();
        CreateKeyRingRequest createKeyRingRequest =
            CreateKeyRingRequest.newBuilder()
                .setParent(keyRingParent)
                .setKeyRingId(keyRingName)
                .build();
        requestParamsHeader.put(requestParamsKey, "parent=" + keyRingParent);
        ClientInterceptor headersInterceptor =
            MetadataUtils.newAttachHeadersInterceptor(requestParamsHeader);
        KeyManagementServiceBlockingStub stubForCreateKeyRing =
            kmsStub.withInterceptors(headersInterceptor);
        stubForCreateKeyRing.createKeyRing(createKeyRingRequest);
      } else {
        throw ex;
      }
    }

    return kmsKeyRingResourcePath;
  }

  private void ensureKmsKeyRingIamPermissionsForTests(
      IAMPolicyGrpc.IAMPolicyBlockingStub iamStub,
      String projectId,
      String location,
      String keyRingName)
      throws StatusRuntimeException {
    ServiceAccount serviceAccount = storage.getServiceAccount(projectId);
    String kmsKeyRingResourcePath = KeyRingName.of(projectId, location, keyRingName).toString();
    Binding binding =
        Binding.newBuilder()
            .setRole("roles/cloudkms.cryptoKeyEncrypterDecrypter")
            .addMembers("serviceAccount:" + serviceAccount.getEmail())
            .build();
    com.google.iam.v1.Policy policy =
        com.google.iam.v1.Policy.newBuilder().addBindings(binding).build();
    SetIamPolicyRequest setIamPolicyRequest =
        SetIamPolicyRequest.newBuilder()
            .setResource(kmsKeyRingResourcePath)
            .setPolicy(policy)
            .build();
    requestParamsHeader.put(requestParamsKey, "parent=" + kmsKeyRingResourcePath);
    ClientInterceptor headersInterceptor =
        MetadataUtils.newAttachHeadersInterceptor(requestParamsHeader);
    iamStub = iamStub.withInterceptors(headersInterceptor);
    try {
      iamStub.setIamPolicy(setIamPolicyRequest);
    } catch (StatusRuntimeException e) {
      if (log.isLoggable(Level.WARNING)) {
        log.log(Level.WARNING, "Unable to set IAM policy: {0}", e.getMessage());
      }
    }
  }

  private String ensureKmsKeyExistsForTests(
      KeyManagementServiceBlockingStub kmsStub,
      String projectId,
      String location,
      String keyRingName,
      String keyName)
      throws StatusRuntimeException {
    String kmsKeyResourcePath =
        CryptoKeyName.of(projectId, location, keyRingName, keyName).toString();
    try {
      // Attempt to Get CryptoKey
      requestParamsHeader.put(requestParamsKey, "name=" + kmsKeyResourcePath);
      GetCryptoKeyRequest getCryptoKeyRequest =
          GetCryptoKeyRequest.newBuilder().setName(kmsKeyResourcePath).build();
      ClientInterceptor headersInterceptor =
          MetadataUtils.newAttachHeadersInterceptor(requestParamsHeader);
      KeyManagementServiceGrpc.KeyManagementServiceBlockingStub stubForGetCryptoKey =
          kmsStub.withInterceptors(headersInterceptor);
      stubForGetCryptoKey.getCryptoKey(getCryptoKeyRequest);
    } catch (StatusRuntimeException ex) {
      if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
        String kmsKeyRingResourcePath = KeyRingName.of(projectId, location, keyRingName).toString();
        CryptoKey cryptoKey =
            CryptoKey.newBuilder().setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT).build();
        CreateCryptoKeyRequest createCryptoKeyRequest =
            CreateCryptoKeyRequest.newBuilder()
                .setCryptoKeyId(keyName)
                .setParent(kmsKeyRingResourcePath)
                .setCryptoKey(cryptoKey)
                .build();

        requestParamsHeader.put(requestParamsKey, "parent=" + kmsKeyRingResourcePath);
        ClientInterceptor headersInterceptor =
            MetadataUtils.newAttachHeadersInterceptor(requestParamsHeader);
        KeyManagementServiceGrpc.KeyManagementServiceBlockingStub stubForCreateCryptoKey =
            kmsStub.withInterceptors(headersInterceptor);
        stubForCreateCryptoKey.createCryptoKey(createCryptoKeyRequest);
      } else {
        throw ex;
      }
    }
    return kmsKeyResourcePath;
  }

  @Test
  public void testClearBucketDefaultKmsKeyName() {
    String bucketName = generator.randomBucketName();
    // TODO: replace with storage
    // b/246634709
    Bucket remoteBucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setDefaultKmsKeyName(kmsKeyOneResourcePath)
                .setLocation(KMS_KEY_RING_LOCATION)
                .build());
    try {
      assertEquals(kmsKeyOneResourcePath, remoteBucket.getDefaultKmsKeyName());
      Bucket updatedBucket = remoteBucket.toBuilder().setDefaultKmsKeyName(null).build().update();
      assertNull(updatedBucket.getDefaultKmsKeyName());
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testUpdateBucketDefaultKmsKeyName() {
    // TODO: replace with storage
    // b/246634709
    String bucketName = generator.randomBucketName();
    Bucket remoteBucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setDefaultKmsKeyName(kmsKeyOneResourcePath)
                .setLocation(KMS_KEY_RING_LOCATION)
                .build());

    try {
      assertEquals(kmsKeyOneResourcePath, remoteBucket.getDefaultKmsKeyName());
      Bucket updatedBucket =
          remoteBucket.toBuilder().setDefaultKmsKeyName(kmsKeyTwoResourcePath).build().update();
      assertEquals(kmsKeyTwoResourcePath, updatedBucket.getDefaultKmsKeyName());
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testCreateBlobWithKmsKeyName() {
    String blobName = "test-create-with-kms-key-name-blob";
    String bucketName = bucket.getName();
    BlobInfo blob = BlobInfo.newBuilder(bucketName, blobName).build();
    Blob remoteBlob =
        storage.create(
            blob, BLOB_BYTE_CONTENT, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath));
    assertNotNull(remoteBlob);
    assertEquals(blob.getBucket(), remoteBlob.getBucket());
    assertEquals(blob.getName(), remoteBlob.getName());
    assertNotNull(remoteBlob.getKmsKeyName());
    assertTrue(remoteBlob.getKmsKeyName().startsWith(kmsKeyOneResourcePath));
    byte[] readBytes = storage.readAllBytes(bucketName, blobName);
    assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
  }

  @Test(expected = StorageException.class)
  public void testCreateBlobWithKmsKeyNameAndCustomerSuppliedKeyFails() {
    String blobName = "test-create-with-kms-key-name-blob";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
    storage.create(
        blob,
        BLOB_BYTE_CONTENT,
        Storage.BlobTargetOption.encryptionKey(KEY),
        Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath));
  }

  @Test
  public void testCreateBlobWithDefaultKmsKeyName() {
    String bucketName = generator.randomBucketName();
    // TODO: replace with storage
    // b/246634709
    Bucket bucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setDefaultKmsKeyName(kmsKeyOneResourcePath)
                .setLocation(KMS_KEY_RING_LOCATION)
                .build());
    assertEquals(bucket.getDefaultKmsKeyName(), kmsKeyOneResourcePath);

    try {
      String blobName = "test-create-with-default-kms-key-name-blob";
      BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).build();
      Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
      assertNotNull(remoteBlob);
      assertEquals(blob.getBucket(), remoteBlob.getBucket());
      assertEquals(blob.getName(), remoteBlob.getName());
      assertNotNull(remoteBlob.getKmsKeyName());
      assertTrue(remoteBlob.getKmsKeyName().startsWith(kmsKeyOneResourcePath));
      byte[] readBytes = storage.readAllBytes(bucketName, blobName);
      assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testGetBlobKmsKeyNameField() {
    String blobName = "test-get-selected-kms-key-name-field-blob";
    BlobInfo blob = BlobInfo.newBuilder(bucket, blobName).setContentType(CONTENT_TYPE).build();
    assertNotNull(storage.create(blob, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath)));
    Blob remoteBlob =
        storage.get(blob.getBlobId(), Storage.BlobGetOption.fields(BlobField.KMS_KEY_NAME));
    assertEquals(blob.getBlobId(), remoteBlob.getBlobId());
    assertTrue(remoteBlob.getKmsKeyName().startsWith(kmsKeyOneResourcePath));
    assertNull(remoteBlob.getContentType());
  }

  @Test(timeout = 5000)
  public void testListBlobsKmsKeySelectedFields() throws InterruptedException {
    String[] blobNames = {
      "test-list-blobs-selected-field-kms-key-name-blob1",
      "test-list-blobs-selected-field-kms-key-name-blob2"
    };
    BlobInfo blob1 = BlobInfo.newBuilder(bucket, blobNames[0]).setContentType(CONTENT_TYPE).build();
    BlobInfo blob2 = BlobInfo.newBuilder(bucket, blobNames[1]).setContentType(CONTENT_TYPE).build();
    Blob remoteBlob1 =
        storage.create(blob1, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath));
    Blob remoteBlob2 =
        storage.create(blob2, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath));
    assertNotNull(remoteBlob1);
    assertNotNull(remoteBlob2);
    Page<Blob> page =
        storage.list(
            bucket.getName(),
            Storage.BlobListOption.prefix("test-list-blobs-selected-field-kms-key-name-blob"),
            Storage.BlobListOption.fields(BlobField.KMS_KEY_NAME));
    // Listing blobs is eventually consistent, we loop until the list is of the expected size. The
    // test fails if timeout is reached.
    while (Iterators.size(page.iterateAll().iterator()) != 2) {
      Thread.sleep(500);
      page =
          storage.list(
              bucket.getName(),
              Storage.BlobListOption.prefix("test-list-blobs-selected-field-kms-key-name-blob"),
              Storage.BlobListOption.fields(BlobField.KMS_KEY_NAME));
    }
    Set<String> blobSet = ImmutableSet.of(blobNames[0], blobNames[1]);
    Iterator<Blob> iterator = page.iterateAll().iterator();
    while (iterator.hasNext()) {
      Blob remoteBlob = iterator.next();
      assertEquals(bucket.getName(), remoteBlob.getBucket());
      assertTrue(blobSet.contains(remoteBlob.getName()));
      assertTrue(remoteBlob.getKmsKeyName().startsWith(kmsKeyOneResourcePath));
      assertNull(remoteBlob.getContentType());
    }
  }

  @Test
  public void testRotateFromCustomerEncryptionToKmsKey() {
    String sourceBlobName = "test-copy-blob-encryption-key-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    Blob remoteBlob =
        storage.create(
            BlobInfo.newBuilder(source).build(),
            BLOB_BYTE_CONTENT,
            Storage.BlobTargetOption.encryptionKey(KEY));
    assertNotNull(remoteBlob);
    String targetBlobName = "test-copy-blob-kms-key-target";
    BlobInfo target =
        BlobInfo.newBuilder(bucket, targetBlobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    Storage.CopyRequest req =
        Storage.CopyRequest.newBuilder()
            .setSource(source)
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(BASE64_KEY))
            .setTarget(target, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath))
            .build();
    CopyWriter copyWriter = storage.copy(req);
    assertEquals(bucket.getName(), copyWriter.getResult().getBucket());
    assertEquals(targetBlobName, copyWriter.getResult().getName());
    assertEquals(CONTENT_TYPE, copyWriter.getResult().getContentType());
    assertNotNull(copyWriter.getResult().getKmsKeyName());
    assertTrue(copyWriter.getResult().getKmsKeyName().startsWith(kmsKeyOneResourcePath));
    assertArrayEquals(BLOB_BYTE_CONTENT, copyWriter.getResult().getContent());
    assertEquals(metadata, copyWriter.getResult().getMetadata());
    assertTrue(copyWriter.isDone());
    assertTrue(storage.delete(bucket.getName(), targetBlobName));
  }

  @Test(expected = StorageException.class)
  public void testRotateFromCustomerEncryptionToKmsKeyWithCustomerEncryption() {
    String sourceBlobName = "test-copy-blob-encryption-key-source";
    BlobId source = BlobId.of(bucket.getName(), sourceBlobName);
    ImmutableMap<String, String> metadata = ImmutableMap.of("k", "v");
    Blob remoteBlob =
        storage.create(
            BlobInfo.newBuilder(source).build(),
            BLOB_BYTE_CONTENT,
            Storage.BlobTargetOption.encryptionKey(KEY));
    assertNotNull(remoteBlob);
    String targetBlobName = "test-copy-blob-kms-key-target";
    BlobInfo target =
        BlobInfo.newBuilder(bucket, targetBlobName)
            .setContentType(CONTENT_TYPE)
            .setMetadata(metadata)
            .build();
    Storage.CopyRequest req =
        Storage.CopyRequest.newBuilder()
            .setSource(source)
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(BASE64_KEY))
            .setTarget(
                target,
                Storage.BlobTargetOption.encryptionKey(KEY),
                Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath))
            .build();
    storage.copy(req);
  }

  @Test
  public void testListBucketDefaultKmsKeyName() throws InterruptedException {
    String bucketName = generator.randomBucketName();
    // TODO: replace with storage
    // b/246634709
    Bucket remoteBucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setDefaultKmsKeyName(kmsKeyOneResourcePath)
                .setLocation(KMS_KEY_RING_LOCATION)
                .build());
    assertNotNull(remoteBucket);
    assertTrue(remoteBucket.getDefaultKmsKeyName().startsWith(kmsKeyOneResourcePath));
    try {
      Iterator<Bucket> bucketIterator =
          storage
              .list(
                  Storage.BucketListOption.prefix(bucketName),
                  Storage.BucketListOption.fields(BucketField.ENCRYPTION))
              .iterateAll()
              .iterator();
      while (!bucketIterator.hasNext()) {
        Thread.sleep(500);
        bucketIterator =
            storage
                .list(
                    Storage.BucketListOption.prefix(bucketName),
                    Storage.BucketListOption.fields(BucketField.ENCRYPTION))
                .iterateAll()
                .iterator();
      }
      while (bucketIterator.hasNext()) {
        Bucket bucket = bucketIterator.next();
        assertTrue(bucket.getName().startsWith(bucketName));
        assertNotNull(bucket.getDefaultKmsKeyName());
        assertTrue(bucket.getDefaultKmsKeyName().startsWith(kmsKeyOneResourcePath));
      }
    } finally {
      BucketCleaner.doCleanup(bucketName, storage);
    }
  }

  @Test
  public void testWriterWithKmsKeyName() throws IOException {
    // Write an empty object with a kmsKeyName.
    String blobName = "test-empty-blob";
    BlobInfo blobInfo = BlobInfo.newBuilder(bucket, blobName).build();
    Blob blob =
        storage.create(blobInfo, Storage.BlobTargetOption.kmsKeyName(kmsKeyOneResourcePath));

    // Create a writer using blob that already has metadata received from Storage API.
    int numberOfBytes;
    byte[] content = BLOB_STRING_CONTENT.getBytes(UTF_8);
    try (WriteChannel writer = blob.writer()) {
      numberOfBytes = writer.write(ByteBuffer.wrap(content, 0, content.length));
    }
    assertThat(numberOfBytes).isEqualTo(content.length);
    assertThat(blob.getKmsKeyName()).isNotNull();
    assertThat(storage.delete(bucket.getName(), blobName)).isTrue();
  }
}
