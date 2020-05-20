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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.google.api.core.ApiClock;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Tuple;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StorageImplMockitoTest {

  private static final String BUCKET_NAME1 = "b1";
  private static final String BUCKET_NAME2 = "b2";
  private static final String BUCKET_NAME3 = "b3";
  private static final String BLOB_NAME1 = "n1";
  private static final String BLOB_NAME2 = "n2";
  private static final String BLOB_NAME3 = "n3";
  private static final byte[] BLOB_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final byte[] BLOB_SUB_CONTENT = {0xE, 0xA};
  private static final String CONTENT_MD5 = "O1R4G1HJSDUISJjoIYmVhQ==";
  private static final String CONTENT_CRC32C = "9N3EPQ==";
  private static final String SUB_CONTENT_MD5 = "5e7c7CdasUiOn3BO560jPg==";
  private static final String SUB_CONTENT_CRC32C = "bljNYA==";
  private static final int DEFAULT_CHUNK_SIZE = 2 * 1024 * 1024;
  private static final String BASE64_KEY = "JVzfVl8NLD9FjedFuStegjRfES5ll5zc59CIXw572OA=";
  private static final Key KEY =
      new SecretKeySpec(BaseEncoding.base64().decode(BASE64_KEY), "AES256");
  private static final String KMS_KEY_NAME =
      "projects/gcloud-devel/locations/us/keyRings/gcs_kms_key_ring_us/cryptoKeys/key";
  private static final Long RETENTION_PERIOD = 10L;
  private static final String USER_PROJECT = "test-project";
  private static final int DEFAULT_BUFFER_SIZE = 15 * 1024 * 1024;
  private static final int MIN_BUFFER_SIZE = 256 * 1024;
  // BucketInfo objects
  private static final BucketInfo BUCKET_INFO1 =
      BucketInfo.newBuilder(BUCKET_NAME1).setMetageneration(42L).build();
  private static final BucketInfo BUCKET_INFO2 = BucketInfo.newBuilder(BUCKET_NAME2).build();
  private static final BucketInfo BUCKET_INFO3 =
      BucketInfo.newBuilder(BUCKET_NAME3)
          .setRetentionPeriod(RETENTION_PERIOD)
          .setRetentionPolicyIsLocked(true)
          .setMetageneration(42L)
          .build();

  // BlobInfo objects
  private static final BlobInfo BLOB_INFO1 =
      BlobInfo.newBuilder(BUCKET_NAME1, BLOB_NAME1, 24L)
          .setMetageneration(42L)
          .setContentType("application/json")
          .setMd5("md5string")
          .build();
  private static final BlobInfo BLOB_INFO2 = BlobInfo.newBuilder(BUCKET_NAME1, BLOB_NAME2).build();
  private static final BlobInfo BLOB_INFO3 = BlobInfo.newBuilder(BUCKET_NAME1, BLOB_NAME3).build();

  // Empty StorageRpc options
  private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

  // Bucket target options
  private static final Storage.BucketTargetOption BUCKET_TARGET_METAGENERATION =
      Storage.BucketTargetOption.metagenerationMatch();
  private static final Storage.BucketTargetOption BUCKET_TARGET_PREDEFINED_ACL =
      Storage.BucketTargetOption.predefinedAcl(Storage.PredefinedAcl.PRIVATE);
  private static final Storage.BucketTargetOption BUCKET_TARGET_USER_PROJECT =
      Storage.BucketTargetOption.userProject(USER_PROJECT);
  private static final Map<StorageRpc.Option, ?> BUCKET_TARGET_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BUCKET_INFO1.getMetageneration(),
          StorageRpc.Option.PREDEFINED_ACL, BUCKET_TARGET_PREDEFINED_ACL.getValue());
  private static final Map<StorageRpc.Option, ?> BUCKET_TARGET_OPTIONS_LOCK_RETENTION_POLICY =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH,
          BUCKET_INFO3.getMetageneration(),
          StorageRpc.Option.USER_PROJECT,
          USER_PROJECT);

  // Blob target options (create, update, compose)
  private static final Storage.BlobTargetOption BLOB_TARGET_GENERATION =
      Storage.BlobTargetOption.generationMatch();
  private static final Storage.BlobTargetOption BLOB_TARGET_METAGENERATION =
      Storage.BlobTargetOption.metagenerationMatch();
  private static final Storage.BlobTargetOption BLOB_TARGET_DISABLE_GZIP_CONTENT =
      Storage.BlobTargetOption.disableGzipContent();
  private static final Storage.BlobTargetOption BLOB_TARGET_NOT_EXIST =
      Storage.BlobTargetOption.doesNotExist();
  private static final Storage.BlobTargetOption BLOB_TARGET_PREDEFINED_ACL =
      Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PRIVATE);
  private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_CREATE =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BLOB_INFO1.getMetageneration(),
          StorageRpc.Option.IF_GENERATION_MATCH, 0L,
          StorageRpc.Option.PREDEFINED_ACL, BUCKET_TARGET_PREDEFINED_ACL.getValue());
  private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_CREATE_DISABLE_GZIP_CONTENT =
      ImmutableMap.of(StorageRpc.Option.IF_DISABLE_GZIP_CONTENT, true);
  private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_UPDATE =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BLOB_INFO1.getMetageneration(),
          StorageRpc.Option.PREDEFINED_ACL, BUCKET_TARGET_PREDEFINED_ACL.getValue());
  private static final Map<StorageRpc.Option, ?> BLOB_TARGET_OPTIONS_COMPOSE =
      ImmutableMap.of(
          StorageRpc.Option.IF_GENERATION_MATCH, BLOB_INFO1.getGeneration(),
          StorageRpc.Option.IF_METAGENERATION_MATCH, BLOB_INFO1.getMetageneration());

  // Blob write options (create, writer)
  private static final Storage.BlobWriteOption BLOB_WRITE_METAGENERATION =
      Storage.BlobWriteOption.metagenerationMatch();
  private static final Storage.BlobWriteOption BLOB_WRITE_NOT_EXIST =
      Storage.BlobWriteOption.doesNotExist();
  private static final Storage.BlobWriteOption BLOB_WRITE_PREDEFINED_ACL =
      Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PRIVATE);
  private static final Storage.BlobWriteOption BLOB_WRITE_MD5_HASH =
      Storage.BlobWriteOption.md5Match();
  private static final Storage.BlobWriteOption BLOB_WRITE_CRC2C =
      Storage.BlobWriteOption.crc32cMatch();

  // Bucket get/source options
  private static final Storage.BucketSourceOption BUCKET_SOURCE_METAGENERATION =
      Storage.BucketSourceOption.metagenerationMatch(BUCKET_INFO1.getMetageneration());
  private static final Map<StorageRpc.Option, ?> BUCKET_SOURCE_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BUCKET_SOURCE_METAGENERATION.getValue());
  private static final Storage.BucketGetOption BUCKET_GET_METAGENERATION =
      Storage.BucketGetOption.metagenerationMatch(BUCKET_INFO1.getMetageneration());
  private static final Storage.BucketGetOption BUCKET_GET_FIELDS =
      Storage.BucketGetOption.fields(Storage.BucketField.LOCATION, Storage.BucketField.ACL);
  private static final Storage.BucketGetOption BUCKET_GET_EMPTY_FIELDS =
      Storage.BucketGetOption.fields();
  private static final Map<StorageRpc.Option, ?> BUCKET_GET_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BUCKET_SOURCE_METAGENERATION.getValue());

  // Blob get/source options
  private static final Storage.BlobGetOption BLOB_GET_METAGENERATION =
      Storage.BlobGetOption.metagenerationMatch(BLOB_INFO1.getMetageneration());
  private static final Storage.BlobGetOption BLOB_GET_GENERATION =
      Storage.BlobGetOption.generationMatch(BLOB_INFO1.getGeneration());
  private static final Storage.BlobGetOption BLOB_GET_GENERATION_FROM_BLOB_ID =
      Storage.BlobGetOption.generationMatch();
  private static final Storage.BlobGetOption BLOB_GET_FIELDS =
      Storage.BlobGetOption.fields(Storage.BlobField.CONTENT_TYPE, Storage.BlobField.CRC32C);
  private static final Storage.BlobGetOption BLOB_GET_EMPTY_FIELDS = Storage.BlobGetOption.fields();
  private static final Map<StorageRpc.Option, ?> BLOB_GET_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BLOB_GET_METAGENERATION.getValue(),
          StorageRpc.Option.IF_GENERATION_MATCH, BLOB_GET_GENERATION.getValue());
  private static final Storage.BlobSourceOption BLOB_SOURCE_METAGENERATION =
      Storage.BlobSourceOption.metagenerationMatch(BLOB_INFO1.getMetageneration());
  private static final Storage.BlobSourceOption BLOB_SOURCE_GENERATION =
      Storage.BlobSourceOption.generationMatch(BLOB_INFO1.getGeneration());
  private static final Storage.BlobSourceOption BLOB_SOURCE_GENERATION_FROM_BLOB_ID =
      Storage.BlobSourceOption.generationMatch();
  private static final Map<StorageRpc.Option, ?> BLOB_SOURCE_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.IF_METAGENERATION_MATCH, BLOB_SOURCE_METAGENERATION.getValue(),
          StorageRpc.Option.IF_GENERATION_MATCH, BLOB_SOURCE_GENERATION.getValue());
  private static final Map<StorageRpc.Option, ?> BLOB_SOURCE_OPTIONS_COPY =
      ImmutableMap.of(
          StorageRpc.Option.IF_SOURCE_METAGENERATION_MATCH, BLOB_SOURCE_METAGENERATION.getValue(),
          StorageRpc.Option.IF_SOURCE_GENERATION_MATCH, BLOB_SOURCE_GENERATION.getValue());

  // Bucket list options
  private static final Storage.BucketListOption BUCKET_LIST_PAGE_SIZE =
      Storage.BucketListOption.pageSize(42L);
  private static final Storage.BucketListOption BUCKET_LIST_PREFIX =
      Storage.BucketListOption.prefix("prefix");
  private static final Storage.BucketListOption BUCKET_LIST_FIELDS =
      Storage.BucketListOption.fields(Storage.BucketField.LOCATION, Storage.BucketField.ACL);
  private static final Storage.BucketListOption BUCKET_LIST_EMPTY_FIELDS =
      Storage.BucketListOption.fields();
  private static final Map<StorageRpc.Option, ?> BUCKET_LIST_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.MAX_RESULTS, BUCKET_LIST_PAGE_SIZE.getValue(),
          StorageRpc.Option.PREFIX, BUCKET_LIST_PREFIX.getValue());

  // Blob list options
  private static final Storage.BlobListOption BLOB_LIST_PAGE_SIZE =
      Storage.BlobListOption.pageSize(42L);
  private static final Storage.BlobListOption BLOB_LIST_PREFIX =
      Storage.BlobListOption.prefix("prefix");
  private static final Storage.BlobListOption BLOB_LIST_FIELDS =
      Storage.BlobListOption.fields(Storage.BlobField.CONTENT_TYPE, Storage.BlobField.MD5HASH);
  private static final Storage.BlobListOption BLOB_LIST_VERSIONS =
      Storage.BlobListOption.versions(false);
  private static final Storage.BlobListOption BLOB_LIST_EMPTY_FIELDS =
      Storage.BlobListOption.fields();
  private static final Map<StorageRpc.Option, ?> BLOB_LIST_OPTIONS =
      ImmutableMap.of(
          StorageRpc.Option.MAX_RESULTS, BLOB_LIST_PAGE_SIZE.getValue(),
          StorageRpc.Option.PREFIX, BLOB_LIST_PREFIX.getValue(),
          StorageRpc.Option.VERSIONS, BLOB_LIST_VERSIONS.getValue());

  // ACLs
  private static final Acl ACL = Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.OWNER);
  private static final Acl OTHER_ACL =
      Acl.of(new Acl.Project(Acl.Project.ProjectRole.OWNERS, "p"), Acl.Role.READER);

  // Customer supplied encryption key options
  private static final Map<StorageRpc.Option, ?> ENCRYPTION_KEY_OPTIONS =
      ImmutableMap.of(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, BASE64_KEY);

  // Customer managed encryption key options
  private static final Map<StorageRpc.Option, ?> KMS_KEY_NAME_OPTIONS =
      ImmutableMap.of(StorageRpc.Option.KMS_KEY_NAME, KMS_KEY_NAME);
  // IAM policies
  private static final String POLICY_ETAG1 = "CAE=";
  private static final String POLICY_ETAG2 = "CAI=";
  private static final Policy LIB_POLICY1 =
      Policy.newBuilder()
          .addIdentity(StorageRoles.objectViewer(), Identity.allUsers())
          .addIdentity(
              StorageRoles.objectAdmin(),
              Identity.user("test1@gmail.com"),
              Identity.user("test2@gmail.com"))
          .setEtag(POLICY_ETAG1)
          .setVersion(1)
          .build();

  private static final ServiceAccount SERVICE_ACCOUNT = ServiceAccount.of("test@google.com");

  private static final com.google.api.services.storage.model.Policy API_POLICY1 =
      new com.google.api.services.storage.model.Policy()
          .setBindings(
              ImmutableList.of(
                  new com.google.api.services.storage.model.Policy.Bindings()
                      .setMembers(ImmutableList.of("allUsers"))
                      .setRole("roles/storage.objectViewer"),
                  new com.google.api.services.storage.model.Policy.Bindings()
                      .setMembers(ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com"))
                      .setRole("roles/storage.objectAdmin")))
          .setEtag(POLICY_ETAG1)
          .setVersion(1);

  private static final String PRIVATE_KEY_STRING =
      "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoG"
          + "BAL2xolH1zrISQ8+GzOV29BNjjzq4/HIP8Psd1+cZb81vDklSF+95wB250MSE0BDc81pvIMwj5OmIfLg1NY6uB"
          + "1xavOPpVdx1z664AGc/BEJ1zInXGXaQ6s+SxGenVq40Yws57gikQGMZjttpf1Qbz4DjkxsbRoeaRHn06n9pH1e"
          + "jAgMBAAECgYEAkWcm0AJF5LMhbWKbjkxm/LG06UNApkHX6vTOOOODkonM/qDBnhvKCj8Tan+PaU2j7679Cd19q"
          + "xCm4SBQJET7eBhqLD9L2j9y0h2YUQnLbISaqUS1/EXcr2C1Lf9VCEn1y/GYuDYqs85rGoQ4ZYfM9ClROSq86fH"
          + "+cbIIssqJqukCQQD18LjfJz/ichFeli5/l1jaFid2XoCH3T6TVuuysszVx68fh60gSIxEF/0X2xB+wuPxTP4IQ"
          + "+t8tD/ktd232oWXAkEAxXPych2QBHePk9/lek4tOkKBgfnDzex7S/pI0G1vpB3VmzBbCsokn9lpOv7JV8071GD"
          + "lW/7R6jlLfpQy3hN31QJAE10osSk99m5Uv8XDU3hvHnywDrnSFOBulNs7I47AYfSe7TSZhPkxUgsxejddTR27J"
          + "LyTI8N1PxRSE4feNSOXcQJAMMKJRJT4U6IS2rmXubREhvaVdLtxFxEnAYQ1JwNfZm/XqBMw6GEy2iaeTetNXVl"
          + "ZRQEIoscyn1y2v/No/F5iYQJBAKBOGASoQcBjGTOg/H/SfcE8QVNsKEpthRrs6CkpT80aZ/AV+ksfoIf2zw2M3"
          + "mAHfrO+TBLdz4sicuFQvlN9SEc=";

  private static final String PUBLIC_KEY_STRING =
      "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9saJR9c6y"
          + "EkPPhszldvQTY486uPxyD/D7HdfnGW/Nbw5JUhfvecAdudDEhNAQ3PNabyDMI+TpiHy4NTWOrgdcWrzj6VXcdc"
          + "+uuABnPwRCdcyJ1xl2kOrPksRnp1auNGMLOe4IpEBjGY7baX9UG8+A45MbG0aHmkR59Op/aR9XowIDAQAB";

  private static final String SIGNED_URL =
      "http://www.test.com/test-bucket/test1.txt?GoogleAccessId=testClient-test@test.com&Expires=1553839761&Signature=MJUBXAZ7";

  private static final ApiClock TIME_SOURCE =
      new ApiClock() {
        @Override
        public long nanoTime() {
          return 42_000_000_000L;
        }

        @Override
        public long millisTime() {
          return 42_000L;
        }
      };

  // List of chars under test were taken from
  // https://en.wikipedia.org/wiki/Percent-encoding#Percent-encoding_reserved_characters
  private static final Map<Character, String> RFC3986_URI_ENCODING_MAP =
      ImmutableMap.<Character, String>builder()
          .put('!', "%21")
          .put('#', "%23")
          .put('$', "%24")
          .put('&', "%26")
          .put('\'', "%27")
          .put('(', "%28")
          .put(')', "%29")
          .put('*', "%2A")
          .put('+', "%2B")
          .put(',', "%2C")
          // NOTE: Whether the forward slash character should be encoded depends on the URI segment
          // being encoded. The path segment should not encode forward slashes, but others (e.g.
          // query parameter keys and values) should encode them. Tests verifying encoding behavior
          // in path segments should make a copy of this map and replace the mapping for '/' to "/".
          .put('/', "%2F")
          .put(':', "%3A")
          .put(';', "%3B")
          .put('=', "%3D")
          .put('?', "%3F")
          .put('@', "%40")
          .put('[', "%5B")
          .put(']', "%5D")
          // In addition to [a-zA-Z0-9], these chars should not be URI-encoded:
          .put('-', "-")
          .put('_', "_")
          .put('.', ".")
          .put('~', "~")
          .build();

  private static final String ACCOUNT = "account";
  private static PrivateKey privateKey;
  private static PublicKey publicKey;

  private StorageOptions options;
  private StorageRpcFactory rpcFactoryMock;
  private StorageRpc storageRpcMock;
  private Storage storage;

  private Blob expectedBlob1, expectedBlob2, expectedBlob3, expectedUpdated;
  private Bucket expectedBucket1, expectedBucket2, expectedBucket3;

  @BeforeClass
  public static void beforeClass() throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    EncodedKeySpec privateKeySpec =
        new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(PRIVATE_KEY_STRING));
    privateKey = keyFactory.generatePrivate(privateKeySpec);
    EncodedKeySpec publicKeySpec =
        new X509EncodedKeySpec(BaseEncoding.base64().decode(PUBLIC_KEY_STRING));
    publicKey = keyFactory.generatePublic(publicKeySpec);
  }

  private static final RuntimeException STORAGE_FAILURE =
      new RuntimeException("Something went wrong");

  private static final RuntimeException UNEXPECTED_CALL_EXCEPTION =
      new RuntimeException("Unexpected call");
  private static final Answer UNEXPECTED_CALL_ANSWER =
      new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) {
          throw new IllegalArgumentException(
              "Unexpected call of "
                  + invocation.getMethod()
                  + " with "
                  + Arrays.toString(invocation.getArguments()));
        };
      };

  @Before
  public void setUp() {
    rpcFactoryMock = mock(StorageRpcFactory.class, UNEXPECTED_CALL_ANSWER);
    storageRpcMock = mock(StorageRpc.class, UNEXPECTED_CALL_ANSWER);
    doReturn(storageRpcMock).when(rpcFactoryMock).create(Mockito.any(StorageOptions.class));
    options =
        StorageOptions.newBuilder()
            .setProjectId("projectId")
            .setClock(TIME_SOURCE)
            .setServiceRpcFactory(rpcFactoryMock)
            .setRetrySettings(ServiceOptions.getNoRetrySettings())
            .build();
  }

  private void initializeService() {
    storage = options.getService();
    initializeServiceDependentObjects();
  }

  private void initializeServiceDependentObjects() {
    expectedBlob1 = new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO1));
    expectedBlob2 = new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO2));
    expectedBlob3 = new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO3));
    expectedBucket1 = new Bucket(storage, new BucketInfo.BuilderImpl(BUCKET_INFO1));
    expectedBucket2 = new Bucket(storage, new BucketInfo.BuilderImpl(BUCKET_INFO2));
    expectedBucket3 = new Bucket(storage, new BucketInfo.BuilderImpl(BUCKET_INFO3));
    expectedUpdated = null;
  }

  @Test
  public void testGetOptions() {
    initializeService();
    assertSame(options, storage.getOptions());
  }

  @Test
  public void testCreateBucket() {
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(BUCKET_INFO1.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    Bucket bucket = storage.create(BUCKET_INFO1);
    assertEquals(expectedBucket1, bucket);
  }

  @Test
  public void testCreateBucketWithOptions() {
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(BUCKET_INFO1.toPb(), BUCKET_TARGET_OPTIONS);
    initializeService();
    Bucket bucket =
        storage.create(BUCKET_INFO1, BUCKET_TARGET_METAGENERATION, BUCKET_TARGET_PREDEFINED_ACL);
    assertEquals(expectedBucket1, bucket);
  }

  @Test
  public void testCreateBucketFailure() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).create(BUCKET_INFO1.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.create(BUCKET_INFO1);
      fail();
    } catch (StorageException e) {
      assertEquals(STORAGE_FAILURE, e.getCause());
    }
  }

  private void verifyChannelRead(ReadChannel channel, byte[] bytes) throws IOException {
    assertNotNull(channel);
    assertTrue(channel.isOpen());

    ByteBuffer buffer = ByteBuffer.allocate(42);
    byte[] expectedBytes = new byte[buffer.capacity()];
    System.arraycopy(bytes, 0, expectedBytes, 0, bytes.length);

    int size = channel.read(buffer);
    assertEquals(bytes.length, size);
    assertEquals(bytes.length, buffer.position());
    assertArrayEquals(expectedBytes, buffer.array());
  }

  @Test
  public void testReader() {
    initializeService();
    ReadChannel channel = storage.reader(BUCKET_NAME1, BLOB_NAME1);
    assertNotNull(channel);
    assertTrue(channel.isOpen());
    // Storage.reader() does not issue any RPC, channel.read() does
    try {
      channel.read(ByteBuffer.allocate(100));
      fail();
    } catch (IOException e) {
      assertTrue(e.getMessage().contains("java.lang.IllegalArgumentException: Unexpected call"));
    }
  }

  @Test
  public void testReaderWithOptions() throws IOException {
    doReturn(Tuple.of("etag", BLOB_CONTENT))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .read(BLOB_INFO2.toPb(), BLOB_SOURCE_OPTIONS, 0, DEFAULT_CHUNK_SIZE);
    initializeService();
    ReadChannel channel =
        storage.reader(
            BUCKET_NAME1, BLOB_NAME2, BLOB_SOURCE_GENERATION, BLOB_SOURCE_METAGENERATION);
    verifyChannelRead(channel, BLOB_CONTENT);
  }

  @Test
  public void testReaderWithDecryptionKey() throws IOException {
    doReturn(Tuple.of("a", BLOB_CONTENT), Tuple.of("b", BLOB_SUB_CONTENT))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .read(BLOB_INFO2.toPb(), ENCRYPTION_KEY_OPTIONS, 0, DEFAULT_CHUNK_SIZE);
    initializeService();
    ReadChannel channel =
        storage.reader(BUCKET_NAME1, BLOB_NAME2, Storage.BlobSourceOption.decryptionKey(KEY));

    verifyChannelRead(channel, BLOB_CONTENT);
    channel =
        storage.reader(
            BUCKET_NAME1, BLOB_NAME2, Storage.BlobSourceOption.decryptionKey(BASE64_KEY));
    verifyChannelRead(channel, BLOB_SUB_CONTENT);
  }

  @Test
  public void testReaderWithOptionsFromBlobId() throws IOException {
    doReturn(Tuple.of("etag", BLOB_CONTENT))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .read(BLOB_INFO1.getBlobId().toPb(), BLOB_SOURCE_OPTIONS, 0, DEFAULT_CHUNK_SIZE);
    initializeService();
    ReadChannel channel =
        storage.reader(
            BLOB_INFO1.getBlobId(),
            BLOB_SOURCE_GENERATION_FROM_BLOB_ID,
            BLOB_SOURCE_METAGENERATION);
    verifyChannelRead(channel, BLOB_CONTENT);
  }

  @Test
  public void testReaderFailure() throws IOException {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .read(BLOB_INFO2.getBlobId().toPb(), EMPTY_RPC_OPTIONS, 0, DEFAULT_CHUNK_SIZE);
    initializeService();
    ReadChannel channel = storage.reader(BUCKET_NAME1, BLOB_NAME2);
    assertNotNull(channel);
    assertTrue(channel.isOpen());
    try {
      channel.read(ByteBuffer.allocate(42));
      fail();
    } catch (IOException e) {
      assertTrue(e.getMessage().contains(STORAGE_FAILURE.toString()));
    }
  }

  @Test
  public void testWriter() {
    BlobInfo.Builder infoBuilder = BLOB_INFO1.toBuilder();
    BlobInfo infoWithHashes = infoBuilder.setMd5(CONTENT_MD5).setCrc32c(CONTENT_CRC32C).build();
    BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
    doReturn("upload-id")
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(infoWithoutHashes.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    WriteChannel channel = storage.writer(infoWithHashes);
    assertNotNull(channel);
    assertTrue(channel.isOpen());
  }

  @Test
  public void testWriterWithOptions() {
    BlobInfo info = BLOB_INFO1.toBuilder().setMd5(CONTENT_MD5).setCrc32c(CONTENT_CRC32C).build();
    doReturn("upload-id")
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(info.toPb(), BLOB_TARGET_OPTIONS_CREATE);
    initializeService();
    WriteChannel channel =
        storage.writer(
            info,
            BLOB_WRITE_METAGENERATION,
            BLOB_WRITE_NOT_EXIST,
            BLOB_WRITE_PREDEFINED_ACL,
            BLOB_WRITE_CRC2C,
            BLOB_WRITE_MD5_HASH);
    assertNotNull(channel);
    assertTrue(channel.isOpen());
  }

  @Test
  public void testWriterWithEncryptionKey() {
    BlobInfo info = BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
    doReturn("upload-id-1", "upload-id-2")
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(info.toPb(), ENCRYPTION_KEY_OPTIONS);
    initializeService();
    WriteChannel channel = storage.writer(info, Storage.BlobWriteOption.encryptionKey(KEY));
    assertNotNull(channel);
    assertTrue(channel.isOpen());
    channel = storage.writer(info, Storage.BlobWriteOption.encryptionKey(BASE64_KEY));
    assertNotNull(channel);
    assertTrue(channel.isOpen());
  }

  @Test
  public void testWriterWithKmsKeyName() {
    BlobInfo info = BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
    doReturn("upload-id-1", "upload-id-2")
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(info.toPb(), KMS_KEY_NAME_OPTIONS);
    initializeService();
    WriteChannel channel = storage.writer(info, Storage.BlobWriteOption.kmsKeyName(KMS_KEY_NAME));
    assertNotNull(channel);
    assertTrue(channel.isOpen());
    channel = storage.writer(info, Storage.BlobWriteOption.kmsKeyName(KMS_KEY_NAME));
    assertNotNull(channel);
    assertTrue(channel.isOpen());
  }

  @Test
  public void testWriterFailure() {
    BlobInfo.Builder infoBuilder = BLOB_INFO1.toBuilder();
    BlobInfo infoWithHashes = infoBuilder.setMd5(CONTENT_MD5).setCrc32c(CONTENT_CRC32C).build();
    BlobInfo infoWithoutHashes = infoBuilder.setMd5(null).setCrc32c(null).build();
    doThrow(STORAGE_FAILURE).when(storageRpcMock).open(infoWithoutHashes.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.writer(infoWithHashes);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }
}
