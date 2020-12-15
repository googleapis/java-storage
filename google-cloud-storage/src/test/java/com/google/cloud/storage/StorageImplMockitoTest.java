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

import static com.google.cloud.storage.SignedUrlEncodingHelper.Rfc3986UriEncode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.util.DateTime;
import com.google.api.core.ApiClock;
import com.google.api.gax.paging.Page;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.services.storage.model.TestIamPermissionsResponse;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Tuple;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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

  private static final BlobInfo BLOB_INFO_WITH_HASHES =
      BLOB_INFO1.toBuilder().setMd5(CONTENT_MD5).setCrc32c(CONTENT_CRC32C).build();
  private static final BlobInfo BLOB_INFO_WITHOUT_HASHES =
      BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();

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

  @Test
  public void testGetBucket() {
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(BucketInfo.of(BUCKET_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    Bucket bucket = storage.get(BUCKET_NAME1);
    assertEquals(expectedBucket1, bucket);
  }

  @Test
  public void testGetBucketWithOptions() {
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(BucketInfo.of(BUCKET_NAME1).toPb(), BUCKET_GET_OPTIONS);
    initializeService();
    Bucket bucket = storage.get(BUCKET_NAME1, BUCKET_GET_METAGENERATION);
    assertEquals(expectedBucket1, bucket);
  }

  @Test
  public void testGetBucketWithSelectedFields() {
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(Mockito.eq(BucketInfo.of(BUCKET_NAME1).toPb()), capturedOptions.capture());
    initializeService();
    Bucket bucket = storage.get(BUCKET_NAME1, BUCKET_GET_METAGENERATION, BUCKET_GET_FIELDS);
    assertEquals(
        BUCKET_GET_METAGENERATION.getValue(),
        capturedOptions.getValue().get(BUCKET_GET_METAGENERATION.getRpcOption()));
    String selector = (String) capturedOptions.getValue().get(BLOB_GET_FIELDS.getRpcOption());
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("location"));
    assertTrue(selector.contains("acl"));
    assertEquals(17, selector.length());
    assertEquals(BUCKET_INFO1.getName(), bucket.getName());
  }

  @Test
  public void testGetBucketWithEmptyFields() {
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    doReturn(BUCKET_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(Mockito.eq(BucketInfo.of(BUCKET_NAME1).toPb()), capturedOptions.capture());
    initializeService();
    Bucket bucket = storage.get(BUCKET_NAME1, BUCKET_GET_METAGENERATION, BUCKET_GET_EMPTY_FIELDS);
    assertEquals(
        BUCKET_GET_METAGENERATION.getValue(),
        capturedOptions.getValue().get(BUCKET_GET_METAGENERATION.getRpcOption()));
    String selector = (String) capturedOptions.getValue().get(BLOB_GET_FIELDS.getRpcOption());
    assertTrue(selector.contains("name"));
    assertEquals(4, selector.length());
    assertEquals(BUCKET_INFO1.getName(), bucket.getName());
  }

  @Test
  public void testGetBucketFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .get(BucketInfo.of(BUCKET_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.get(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertEquals(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testGetBlob() {
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    Blob blob = storage.get(BUCKET_NAME1, BLOB_NAME1);
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testGetBlobWithOptions() {
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), BLOB_GET_OPTIONS);
    initializeService();
    Blob blob = storage.get(BUCKET_NAME1, BLOB_NAME1, BLOB_GET_METAGENERATION, BLOB_GET_GENERATION);
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testGetBlobWithOptionsFromBlobId() {
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(BLOB_INFO1.getBlobId().toPb(), BLOB_GET_OPTIONS);
    initializeService();
    Blob blob =
        storage.get(
            BLOB_INFO1.getBlobId(), BLOB_GET_METAGENERATION, BLOB_GET_GENERATION_FROM_BLOB_ID);
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testGetBlobWithSelectedFields() {
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(Mockito.eq(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb()), capturedOptions.capture());
    initializeService();
    Blob blob =
        storage.get(
            BUCKET_NAME1,
            BLOB_NAME1,
            BLOB_GET_METAGENERATION,
            BLOB_GET_GENERATION,
            BLOB_GET_FIELDS);
    assertEquals(
        BLOB_GET_METAGENERATION.getValue(),
        capturedOptions.getValue().get(BLOB_GET_METAGENERATION.getRpcOption()));
    assertEquals(
        BLOB_GET_GENERATION.getValue(),
        capturedOptions.getValue().get(BLOB_GET_GENERATION.getRpcOption()));
    String selector = (String) capturedOptions.getValue().get(BLOB_GET_FIELDS.getRpcOption());
    assertTrue(selector.contains("bucket"));
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("contentType"));
    assertTrue(selector.contains("crc32c"));
    assertEquals(30, selector.length());
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testGetBlobWithEmptyFields() {
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(Mockito.eq(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb()), capturedOptions.capture());
    initializeService();
    Blob blob =
        storage.get(
            BUCKET_NAME1,
            BLOB_NAME1,
            BLOB_GET_METAGENERATION,
            BLOB_GET_GENERATION,
            BLOB_GET_EMPTY_FIELDS);
    assertEquals(
        BLOB_GET_METAGENERATION.getValue(),
        capturedOptions.getValue().get(BLOB_GET_METAGENERATION.getRpcOption()));
    assertEquals(
        BLOB_GET_GENERATION.getValue(),
        capturedOptions.getValue().get(BLOB_GET_GENERATION.getRpcOption()));
    String selector = (String) capturedOptions.getValue().get(BLOB_GET_FIELDS.getRpcOption());
    assertTrue(selector.contains("bucket"));
    assertTrue(selector.contains("name"));
    assertEquals(11, selector.length());
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testGetBlobFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .get(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.get(BUCKET_NAME1, BLOB_NAME1);
      fail();
    } catch (StorageException e) {
      assertEquals(STORAGE_FAILURE, e.getCause());
    }
  }

  private void verifyCreateBlobCapturedStream(ArgumentCaptor<ByteArrayInputStream> capturedStream)
      throws IOException {
    ByteArrayInputStream byteStream = capturedStream.getValue();
    byte[] streamBytes = new byte[BLOB_CONTENT.length];
    assertEquals(BLOB_CONTENT.length, byteStream.read(streamBytes));
    assertArrayEquals(BLOB_CONTENT, streamBytes);
    assertEquals(-1, byteStream.read(streamBytes));
  }

  @Test
  public void testCreateBlob() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITH_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(EMPTY_RPC_OPTIONS));
    initializeService();

    Blob blob = storage.create(BLOB_INFO1, BLOB_CONTENT);

    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  public void testCreateBlobWithSubArrayFromByteArray() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(
                BLOB_INFO1
                    .toBuilder()
                    .setMd5(SUB_CONTENT_MD5)
                    .setCrc32c(SUB_CONTENT_CRC32C)
                    .build()
                    .toPb()),
            capturedStream.capture(),
            Mockito.eq(EMPTY_RPC_OPTIONS));
    initializeService();

    Blob blob = storage.create(BLOB_INFO1, BLOB_CONTENT, 1, 2);

    assertEquals(expectedBlob1, blob);
    ByteArrayInputStream byteStream = capturedStream.getValue();
    byte[] streamBytes = new byte[BLOB_SUB_CONTENT.length];
    assertEquals(BLOB_SUB_CONTENT.length, byteStream.read(streamBytes));
    assertArrayEquals(BLOB_SUB_CONTENT, streamBytes);
    assertEquals(-1, byteStream.read(streamBytes));
  }

  @Test
  public void testCreateBlobRetry() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    StorageObject storageObject = BLOB_INFO_WITH_HASHES.toPb();

    doThrow(new StorageException(500, "internalError"))
        .doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(Mockito.eq(storageObject), capturedStream.capture(), Mockito.eq(EMPTY_RPC_OPTIONS));

    storage =
        options
            .toBuilder()
            .setRetrySettings(ServiceOptions.getDefaultRetrySettings())
            .build()
            .getService();
    initializeServiceDependentObjects();

    Blob blob = storage.create(BLOB_INFO1, BLOB_CONTENT);

    assertEquals(expectedBlob1, blob);

    byte[] streamBytes = new byte[BLOB_CONTENT.length];
    for (ByteArrayInputStream byteStream : capturedStream.getAllValues()) {
      assertEquals(BLOB_CONTENT.length, byteStream.read(streamBytes));
      assertArrayEquals(BLOB_CONTENT, streamBytes);
      assertEquals(-1, byteStream.read(streamBytes));
    }
  }

  @Test
  public void testCreateEmptyBlob() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(
                BLOB_INFO1
                    .toBuilder()
                    .setMd5("1B2M2Y8AsgTpgAmY7PhCfg==")
                    .setCrc32c("AAAAAA==")
                    .build()
                    .toPb()),
            capturedStream.capture(),
            Mockito.eq(EMPTY_RPC_OPTIONS));
    initializeService();

    Blob blob = storage.create(BLOB_INFO1);
    assertEquals(expectedBlob1, blob);
    ByteArrayInputStream byteStream = capturedStream.getValue();
    byte[] streamBytes = new byte[BLOB_CONTENT.length];
    assertEquals(-1, byteStream.read(streamBytes));
  }

  @Test
  public void testCreateBlobWithOptions() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITH_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(BLOB_TARGET_OPTIONS_CREATE));
    initializeService();

    Blob blob =
        storage.create(
            BLOB_INFO1,
            BLOB_CONTENT,
            BLOB_TARGET_METAGENERATION,
            BLOB_TARGET_NOT_EXIST,
            BLOB_TARGET_PREDEFINED_ACL);
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  public void testCreateBlobWithDisabledGzipContent() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITH_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(BLOB_TARGET_OPTIONS_CREATE_DISABLE_GZIP_CONTENT));
    initializeService();

    Blob blob = storage.create(BLOB_INFO1, BLOB_CONTENT, BLOB_TARGET_DISABLE_GZIP_CONTENT);
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  public void testCreateBlobWithEncryptionKey() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    doReturn(BLOB_INFO1.toPb())
        .doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITH_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(ENCRYPTION_KEY_OPTIONS));
    initializeService();

    Blob blob =
        storage.create(BLOB_INFO1, BLOB_CONTENT, Storage.BlobTargetOption.encryptionKey(KEY));
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
    blob =
        storage.create(
            BLOB_INFO1, BLOB_CONTENT, Storage.BlobTargetOption.encryptionKey(BASE64_KEY));
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  public void testCreateBlobWithKmsKeyName() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    doReturn(BLOB_INFO1.toPb())
        .doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITH_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(KMS_KEY_NAME_OPTIONS));
    initializeService();

    Blob blob =
        storage.create(BLOB_INFO1, BLOB_CONTENT, Storage.BlobTargetOption.kmsKeyName(KMS_KEY_NAME));
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
    blob =
        storage.create(BLOB_INFO1, BLOB_CONTENT, Storage.BlobTargetOption.kmsKeyName(KMS_KEY_NAME));
    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobFromStream() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    ByteArrayInputStream fileStream = new ByteArrayInputStream(BLOB_CONTENT);

    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITHOUT_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(EMPTY_RPC_OPTIONS));
    initializeService();

    Blob blob = storage.create(BLOB_INFO_WITH_HASHES, fileStream);

    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobFromStreamDisableGzipContent() throws IOException {
    ArgumentCaptor<ByteArrayInputStream> capturedStream =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);

    ByteArrayInputStream fileStream = new ByteArrayInputStream(BLOB_CONTENT);
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(
            Mockito.eq(BLOB_INFO_WITHOUT_HASHES.toPb()),
            capturedStream.capture(),
            Mockito.eq(BLOB_TARGET_OPTIONS_CREATE_DISABLE_GZIP_CONTENT));
    initializeService();

    Blob blob =
        storage.create(
            BLOB_INFO_WITH_HASHES, fileStream, Storage.BlobWriteOption.disableGzipContent());

    assertEquals(expectedBlob1, blob);
    verifyCreateBlobCapturedStream(capturedStream);
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobFromStreamWithEncryptionKey() throws IOException {
    ByteArrayInputStream fileStream = new ByteArrayInputStream(BLOB_CONTENT);

    doReturn(BLOB_INFO1.toPb())
        .doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(BLOB_INFO_WITHOUT_HASHES.toPb(), fileStream, ENCRYPTION_KEY_OPTIONS);
    initializeService();
    Blob blob =
        storage.create(
            BLOB_INFO_WITH_HASHES, fileStream, Storage.BlobWriteOption.encryptionKey(BASE64_KEY));
    assertEquals(expectedBlob1, blob);
    blob =
        storage.create(
            BLOB_INFO_WITH_HASHES, fileStream, Storage.BlobWriteOption.encryptionKey(BASE64_KEY));
    assertEquals(expectedBlob1, blob);
  }

  @Test
  @SuppressWarnings({"unchecked", "deprecation"})
  public void testCreateBlobFromStreamRetryableException() throws IOException {

    ByteArrayInputStream fileStream = new ByteArrayInputStream(BLOB_CONTENT);

    Exception internalErrorException = new StorageException(500, "internalError");
    doThrow(internalErrorException)
        .when(storageRpcMock)
        .create(BLOB_INFO_WITHOUT_HASHES.toPb(), fileStream, EMPTY_RPC_OPTIONS);

    storage =
        options
            .toBuilder()
            .setRetrySettings(ServiceOptions.getDefaultRetrySettings())
            .build()
            .getService();

    // Even though this exception is retryable, storage.create(BlobInfo, InputStream)
    // shouldn't retry.
    try {
      storage.create(BLOB_INFO_WITH_HASHES, fileStream);
      fail();
    } catch (StorageException ex) {
      assertSame(internalErrorException, ex);
    }
  }

  @Test
  public void testCreateFromDirectory() throws IOException {
    initializeService();
    Path dir = Files.createTempDirectory("unit_");
    try {
      storage.createFrom(BLOB_INFO1, dir);
      fail();
    } catch (StorageException e) {
      assertEquals(dir + " is a directory", e.getMessage());
    }
  }

  private BlobInfo initializeUpload(byte[] bytes) {
    return initializeUpload(bytes, DEFAULT_BUFFER_SIZE, EMPTY_RPC_OPTIONS);
  }

  private BlobInfo initializeUpload(byte[] bytes, int bufferSize) {
    return initializeUpload(bytes, bufferSize, EMPTY_RPC_OPTIONS);
  }

  private BlobInfo initializeUpload(
      byte[] bytes, int bufferSize, Map<StorageRpc.Option, ?> rpcOptions) {
    String uploadId = "upload-id";
    byte[] buffer = new byte[bufferSize];
    System.arraycopy(bytes, 0, buffer, 0, bytes.length);
    BlobInfo blobInfo = BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
    StorageObject storageObject = new StorageObject();
    storageObject.setBucket(BLOB_INFO1.getBucket());
    storageObject.setName(BLOB_INFO1.getName());
    storageObject.setSize(BigInteger.valueOf(bytes.length));
    doReturn(uploadId)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(blobInfo.toPb(), rpcOptions);

    doReturn(storageObject)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .writeWithResponse(uploadId, buffer, 0, 0L, bytes.length, true);

    initializeService();
    expectedUpdated = Blob.fromPb(storage, storageObject);
    return blobInfo;
  }

  @Test
  public void testCreateFromFile() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4};
    Path tempFile = Files.createTempFile("testCreateFrom", ".tmp");
    Files.write(tempFile, dataToSend);

    BlobInfo blobInfo = initializeUpload(dataToSend);
    Blob blob = storage.createFrom(blobInfo, tempFile);
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromStream() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4, 5};
    ByteArrayInputStream stream = new ByteArrayInputStream(dataToSend);

    BlobInfo blobInfo = initializeUpload(dataToSend);
    Blob blob = storage.createFrom(blobInfo, stream);
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromWithOptions() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4, 5, 6};
    ByteArrayInputStream stream = new ByteArrayInputStream(dataToSend);

    BlobInfo blobInfo = initializeUpload(dataToSend, DEFAULT_BUFFER_SIZE, KMS_KEY_NAME_OPTIONS);
    Blob blob =
        storage.createFrom(blobInfo, stream, Storage.BlobWriteOption.kmsKeyName(KMS_KEY_NAME));
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromWithBufferSize() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4, 5, 6};
    ByteArrayInputStream stream = new ByteArrayInputStream(dataToSend);
    int bufferSize = MIN_BUFFER_SIZE * 2;

    BlobInfo blobInfo = initializeUpload(dataToSend, bufferSize);
    Blob blob = storage.createFrom(blobInfo, stream, bufferSize);
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromWithBufferSizeAndOptions() throws Exception {
    byte[] dataToSend = {1, 2, 3, 4, 5, 6};
    ByteArrayInputStream stream = new ByteArrayInputStream(dataToSend);
    int bufferSize = MIN_BUFFER_SIZE * 2;

    BlobInfo blobInfo = initializeUpload(dataToSend, bufferSize, KMS_KEY_NAME_OPTIONS);
    Blob blob =
        storage.createFrom(
            blobInfo, stream, bufferSize, Storage.BlobWriteOption.kmsKeyName(KMS_KEY_NAME));
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromWithSmallBufferSize() throws Exception {
    byte[] dataToSend = new byte[100_000];
    ByteArrayInputStream stream = new ByteArrayInputStream(dataToSend);
    int smallBufferSize = 100;

    BlobInfo blobInfo = initializeUpload(dataToSend, MIN_BUFFER_SIZE);
    Blob blob = storage.createFrom(blobInfo, stream, smallBufferSize);
    assertEquals(expectedUpdated, blob);
  }

  @Test
  public void testCreateFromWithException() throws Exception {
    initializeService();
    String uploadId = "id-exception";
    byte[] bytes = new byte[10];
    byte[] buffer = new byte[MIN_BUFFER_SIZE];
    System.arraycopy(bytes, 0, buffer, 0, bytes.length);
    BlobInfo info = BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
    doReturn(uploadId)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(info.toPb(), EMPTY_RPC_OPTIONS);

    Exception runtimeException = new RuntimeException("message");
    doThrow(runtimeException)
        .when(storageRpcMock)
        .writeWithResponse(uploadId, buffer, 0, 0L, bytes.length, true);

    InputStream input = new ByteArrayInputStream(bytes);
    try {
      storage.createFrom(info, input, MIN_BUFFER_SIZE);
      fail();
    } catch (StorageException e) {
      assertSame(runtimeException, e.getCause());
    }
  }

  @Test
  public void testCreateFromMultipleParts() throws Exception {
    initializeService();
    String uploadId = "id-multiple-parts";
    int extraBytes = 10;
    int totalSize = MIN_BUFFER_SIZE + extraBytes;
    byte[] dataToSend = new byte[totalSize];
    dataToSend[0] = 42;
    dataToSend[MIN_BUFFER_SIZE + 1] = 43;

    StorageObject storageObject = new StorageObject();
    storageObject.setBucket(BLOB_INFO1.getBucket());
    storageObject.setName(BLOB_INFO1.getName());
    storageObject.setSize(BigInteger.valueOf(totalSize));

    BlobInfo info = BLOB_INFO1.toBuilder().setMd5(null).setCrc32c(null).build();
    doReturn(uploadId)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(info.toPb(), EMPTY_RPC_OPTIONS);

    byte[] buffer1 = new byte[MIN_BUFFER_SIZE];
    System.arraycopy(dataToSend, 0, buffer1, 0, MIN_BUFFER_SIZE);
    doReturn(null)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .writeWithResponse(uploadId, buffer1, 0, 0L, MIN_BUFFER_SIZE, false);

    byte[] buffer2 = new byte[MIN_BUFFER_SIZE];
    System.arraycopy(dataToSend, MIN_BUFFER_SIZE, buffer2, 0, extraBytes);
    doReturn(storageObject)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .writeWithResponse(uploadId, buffer2, 0, (long) MIN_BUFFER_SIZE, extraBytes, true);

    InputStream input = new ByteArrayInputStream(dataToSend);
    Blob blob = storage.createFrom(info, input, MIN_BUFFER_SIZE);
    assertEquals(Blob.fromPb(storage, storageObject), blob);
  }

  @Test
  public void testListBuckets() {
    String cursor = "cursor";
    ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(BUCKET_INFO1, BUCKET_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result =
        Tuple.of(cursor, Iterables.transform(bucketInfoList, BucketInfo.TO_PB_FUNCTION));

    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(EMPTY_RPC_OPTIONS);

    initializeService();
    ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
    Page<Bucket> page = storage.list();
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
  }

  @Test
  public void testListBucketsEmpty() {
    doReturn(Tuple.<String, Iterable<com.google.api.services.storage.model.Bucket>>of(null, null))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(EMPTY_RPC_OPTIONS);

    initializeService();
    Page<Bucket> page = storage.list();
    assertNull(page.getNextPageToken());
    assertArrayEquals(
        ImmutableList.of().toArray(), Iterables.toArray(page.getValues(), Bucket.class));
  }

  @Test
  public void testListBucketsWithOptions() {
    String cursor = "cursor";
    ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(BUCKET_INFO1, BUCKET_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result =
        Tuple.of(cursor, Iterables.transform(bucketInfoList, BucketInfo.TO_PB_FUNCTION));

    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_LIST_OPTIONS);

    initializeService();
    ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
    Page<Bucket> page = storage.list(BUCKET_LIST_PAGE_SIZE, BUCKET_LIST_PREFIX);
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
  }

  @Test
  public void testListBucketsWithSelectedFields() {
    String cursor = "cursor";
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);

    ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(BUCKET_INFO1, BUCKET_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result =
        Tuple.of(cursor, Iterables.transform(bucketInfoList, BucketInfo.TO_PB_FUNCTION));

    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(capturedOptions.capture());
    initializeService();
    ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
    Page<Bucket> page = storage.list(BUCKET_LIST_FIELDS);
    String selector = (String) capturedOptions.getValue().get(BUCKET_LIST_FIELDS.getRpcOption());
    assertTrue(selector.contains("items("));
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("acl"));
    assertTrue(selector.contains("location"));
    assertTrue(selector.contains("nextPageToken"));
    assertTrue(selector.endsWith(")"));
    assertEquals(38, selector.length());
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
  }

  @Test
  public void testListBucketsWithEmptyFields() {
    String cursor = "cursor";
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    ImmutableList<BucketInfo> bucketInfoList = ImmutableList.of(BUCKET_INFO1, BUCKET_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.Bucket>> result =
        Tuple.of(cursor, Iterables.transform(bucketInfoList, BucketInfo.TO_PB_FUNCTION));

    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(capturedOptions.capture());
    initializeService();
    ImmutableList<Bucket> bucketList = ImmutableList.of(expectedBucket1, expectedBucket2);
    Page<Bucket> page = storage.list(BUCKET_LIST_EMPTY_FIELDS);
    String selector =
        (String) capturedOptions.getValue().get(BUCKET_LIST_EMPTY_FIELDS.getRpcOption());
    assertTrue(selector.contains("items("));
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("nextPageToken"));
    assertTrue(selector.endsWith(")"));
    assertEquals(25, selector.length());
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(bucketList.toArray(), Iterables.toArray(page.getValues(), Bucket.class));
  }

  @Test
  public void testListBucketsWithException() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).list(EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.list();
      fail();
    } catch (StorageException e) {
      assertEquals(STORAGE_FAILURE.toString(), e.getMessage());
    }
  }

  @Test
  public void testListBlobs() {
    String cursor = "cursor";
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));

    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, EMPTY_RPC_OPTIONS);

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page = storage.list(BUCKET_NAME1);
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsEmpty() {
    doReturn(
            Tuple.<String, Iterable<com.google.api.services.storage.model.StorageObject>>of(
                null, null))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, EMPTY_RPC_OPTIONS);

    initializeService();
    Page<Blob> page = storage.list(BUCKET_NAME1);
    assertNull(page.getNextPageToken());
    assertArrayEquals(
        ImmutableList.of().toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsWithOptions() {
    String cursor = "cursor";
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, BLOB_LIST_OPTIONS);
    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page =
        storage.list(BUCKET_NAME1, BLOB_LIST_PAGE_SIZE, BLOB_LIST_PREFIX, BLOB_LIST_VERSIONS);
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsWithSelectedFields() {
    String cursor = "cursor";
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(Mockito.eq(BUCKET_NAME1), capturedOptions.capture());

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page =
        storage.list(BUCKET_NAME1, BLOB_LIST_PAGE_SIZE, BLOB_LIST_PREFIX, BLOB_LIST_FIELDS);
    assertEquals(
        BLOB_LIST_PAGE_SIZE.getValue(),
        capturedOptions.getValue().get(BLOB_LIST_PAGE_SIZE.getRpcOption()));
    assertEquals(
        BLOB_LIST_PREFIX.getValue(),
        capturedOptions.getValue().get(BLOB_LIST_PREFIX.getRpcOption()));
    String selector = (String) capturedOptions.getValue().get(BLOB_LIST_FIELDS.getRpcOption());
    assertTrue(selector.contains("prefixes"));
    assertTrue(selector.contains("items("));
    assertTrue(selector.contains("bucket"));
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("contentType"));
    assertTrue(selector.contains("md5Hash"));
    assertTrue(selector.contains("nextPageToken"));
    assertTrue(selector.endsWith(")"));
    assertEquals(61, selector.length());
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsWithEmptyFields() {
    String cursor = "cursor";
    ArgumentCaptor<Map<StorageRpc.Option, Object>> capturedOptions =
        ArgumentCaptor.forClass(Map.class);
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(Mockito.eq(BUCKET_NAME1), capturedOptions.capture());

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page =
        storage.list(BUCKET_NAME1, BLOB_LIST_PAGE_SIZE, BLOB_LIST_PREFIX, BLOB_LIST_EMPTY_FIELDS);
    assertEquals(
        BLOB_LIST_PAGE_SIZE.getValue(),
        capturedOptions.getValue().get(BLOB_LIST_PAGE_SIZE.getRpcOption()));
    assertEquals(
        BLOB_LIST_PREFIX.getValue(),
        capturedOptions.getValue().get(BLOB_LIST_PREFIX.getRpcOption()));
    String selector =
        (String) capturedOptions.getValue().get(BLOB_LIST_EMPTY_FIELDS.getRpcOption());
    assertTrue(selector.contains("prefixes"));
    assertTrue(selector.contains("items("));
    assertTrue(selector.contains("bucket"));
    assertTrue(selector.contains("name"));
    assertTrue(selector.contains("nextPageToken"));
    assertTrue(selector.endsWith(")"));
    assertEquals(41, selector.length());
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsCurrentDirectory() {
    String cursor = "cursor";
    Map<StorageRpc.Option, ?> options = ImmutableMap.of(StorageRpc.Option.DELIMITER, "/");
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, options);

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page = storage.list(BUCKET_NAME1, Storage.BlobListOption.currentDirectory());
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsDelimiter() {
    String cursor = "cursor";
    String delimiter = "/";
    Map<StorageRpc.Option, ?> options = ImmutableMap.of(StorageRpc.Option.DELIMITER, delimiter);
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, options);

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page = storage.list(BUCKET_NAME1, Storage.BlobListOption.delimiter(delimiter));
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsWithOffset() {
    String cursor = "cursor";
    String startOffset = "startOffset";
    String endOffset = "endOffset";
    Map<StorageRpc.Option, ?> options =
        ImmutableMap.of(
            StorageRpc.Option.START_OFF_SET, startOffset, StorageRpc.Option.END_OFF_SET, endOffset);
    ImmutableList<BlobInfo> blobInfoList = ImmutableList.of(BLOB_INFO1, BLOB_INFO2);
    Tuple<String, Iterable<com.google.api.services.storage.model.StorageObject>> result =
        Tuple.of(cursor, Iterables.transform(blobInfoList, BlobInfo.INFO_TO_PB_FUNCTION));
    doReturn(result)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .list(BUCKET_NAME1, options);

    initializeService();
    ImmutableList<Blob> blobList = ImmutableList.of(expectedBlob1, expectedBlob2);
    Page<Blob> page =
        storage.list(
            BUCKET_NAME1,
            Storage.BlobListOption.startOffset(startOffset),
            Storage.BlobListOption.endOffset(endOffset));
    assertEquals(cursor, page.getNextPageToken());
    assertArrayEquals(blobList.toArray(), Iterables.toArray(page.getValues(), Blob.class));
  }

  @Test
  public void testListBlobsWithException() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).list(BUCKET_NAME1, EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.list(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertEquals(STORAGE_FAILURE.toString(), e.getMessage());
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
    doReturn("upload-id")
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .open(BLOB_INFO_WITHOUT_HASHES.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    WriteChannel channel = storage.writer(BLOB_INFO_WITH_HASHES);
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
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .open(BLOB_INFO_WITHOUT_HASHES.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.writer(BLOB_INFO_WITH_HASHES);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testUpdateBucket() {
    BucketInfo updatedBucketInfo = BUCKET_INFO1.toBuilder().setIndexPage("some-page").build();
    doReturn(updatedBucketInfo.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patch(updatedBucketInfo.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    Bucket bucket = storage.update(updatedBucketInfo);
    assertEquals(new Bucket(storage, new BucketInfo.BuilderImpl(updatedBucketInfo)), bucket);
  }

  @Test
  public void testUpdateBucketWithOptions() {
    BucketInfo updatedBucketInfo = BUCKET_INFO1.toBuilder().setIndexPage("some-page").build();
    doReturn(updatedBucketInfo.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patch(updatedBucketInfo.toPb(), BUCKET_TARGET_OPTIONS);
    initializeService();
    Bucket bucket =
        storage.update(
            updatedBucketInfo, BUCKET_TARGET_METAGENERATION, BUCKET_TARGET_PREDEFINED_ACL);
    assertEquals(new Bucket(storage, new BucketInfo.BuilderImpl(updatedBucketInfo)), bucket);
  }

  @Test
  public void testUpdateBucketFailure() {
    BucketInfo updatedBucketInfo = BUCKET_INFO1.toBuilder().setIndexPage("some-page").build();
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .patch(updatedBucketInfo.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.update(updatedBucketInfo);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testUpdateBlob() {
    BlobInfo updatedBlobInfo = BLOB_INFO1.toBuilder().setContentType("some-content-type").build();
    doReturn(updatedBlobInfo.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patch(updatedBlobInfo.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    Blob blob = storage.update(updatedBlobInfo);
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(updatedBlobInfo)), blob);
  }

  @Test
  public void testUpdateBlobWithOptions() {
    BlobInfo updatedBlobInfo = BLOB_INFO1.toBuilder().setContentType("some-content-type").build();
    doReturn(updatedBlobInfo.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patch(updatedBlobInfo.toPb(), BLOB_TARGET_OPTIONS_UPDATE);
    initializeService();
    Blob blob =
        storage.update(updatedBlobInfo, BLOB_TARGET_METAGENERATION, BLOB_TARGET_PREDEFINED_ACL);
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(updatedBlobInfo)), blob);
  }

  @Test
  public void testUpdateBlobFailure() {
    BlobInfo updatedBlobInfo = BLOB_INFO1.toBuilder().setContentType("some-content-type").build();
    doThrow(STORAGE_FAILURE).when(storageRpcMock).patch(updatedBlobInfo.toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.update(updatedBlobInfo);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testDeleteBucket() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .delete(BucketInfo.of(BUCKET_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    assertTrue(storage.delete(BUCKET_NAME1));
  }

  @Test
  public void testDeleteBucketWithOptions() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .delete(BucketInfo.of(BUCKET_NAME1).toPb(), BUCKET_SOURCE_OPTIONS);
    initializeService();
    assertTrue(storage.delete(BUCKET_NAME1, BUCKET_SOURCE_METAGENERATION));
  }

  @Test
  public void testDeleteBucketFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .delete(BucketInfo.of(BUCKET_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.delete(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testDeleteBlob() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .delete(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    assertTrue(storage.delete(BUCKET_NAME1, BLOB_NAME1));
  }

  @Test
  public void testDeleteBlobWithOptions() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .delete(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), BLOB_SOURCE_OPTIONS);
    initializeService();
    assertTrue(
        storage.delete(
            BUCKET_NAME1, BLOB_NAME1, BLOB_SOURCE_GENERATION, BLOB_SOURCE_METAGENERATION));
  }

  @Test
  public void testDeleteBlobWithOptionsFromBlobId() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .delete(BLOB_INFO1.getBlobId().toPb(), BLOB_SOURCE_OPTIONS);
    initializeService();
    assertTrue(
        storage.delete(
            BLOB_INFO1.getBlobId(),
            BLOB_SOURCE_GENERATION_FROM_BLOB_ID,
            BLOB_SOURCE_METAGENERATION));
  }

  @Test
  public void testDeleteBlobFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .delete(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.delete(BUCKET_NAME1, BLOB_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testCompose() {
    Storage.ComposeRequest req =
        Storage.ComposeRequest.newBuilder()
            .addSource(BLOB_NAME2, BLOB_NAME3)
            .setTarget(BLOB_INFO1)
            .build();
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .compose(
            ImmutableList.of(BLOB_INFO2.toPb(), BLOB_INFO3.toPb()),
            BLOB_INFO1.toPb(),
            EMPTY_RPC_OPTIONS);
    initializeService();
    Blob blob = storage.compose(req);
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testComposeWithOptions() {
    Storage.ComposeRequest req =
        Storage.ComposeRequest.newBuilder()
            .addSource(BLOB_NAME2, BLOB_NAME3)
            .setTarget(BLOB_INFO1)
            .setTargetOptions(BLOB_TARGET_GENERATION, BLOB_TARGET_METAGENERATION)
            .build();
    doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .compose(
            ImmutableList.of(BLOB_INFO2.toPb(), BLOB_INFO3.toPb()),
            BLOB_INFO1.toPb(),
            BLOB_TARGET_OPTIONS_COMPOSE);
    initializeService();
    Blob blob = storage.compose(req);
    assertEquals(expectedBlob1, blob);
  }

  @Test
  public void testComposeFailure() {
    Storage.ComposeRequest req =
        Storage.ComposeRequest.newBuilder()
            .addSource(BLOB_NAME2, BLOB_NAME3)
            .setTarget(BLOB_INFO1)
            .build();
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .compose(
            ImmutableList.of(BLOB_INFO2.toPb(), BLOB_INFO3.toPb()),
            BLOB_INFO1.toPb(),
            EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.compose(req);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testCopy() {
    Storage.CopyRequest request =
        Storage.CopyRequest.of(BLOB_INFO1.getBlobId(), BLOB_INFO2.getBlobId());
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            EMPTY_RPC_OPTIONS,
            false,
            BLOB_INFO2.toPb(),
            EMPTY_RPC_OPTIONS,
            null);
    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testCopyWithOptions() {
    Storage.CopyRequest request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO2.getBlobId())
            .setSourceOptions(BLOB_SOURCE_GENERATION, BLOB_SOURCE_METAGENERATION)
            .setTarget(BLOB_INFO1, BLOB_TARGET_GENERATION, BLOB_TARGET_METAGENERATION)
            .build();
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            BLOB_SOURCE_OPTIONS_COPY,
            true,
            request.getTarget().toPb(),
            BLOB_TARGET_OPTIONS_COMPOSE,
            null);
    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testCopyWithEncryptionKey() {
    Storage.CopyRequest request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO2.getBlobId())
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(KEY))
            .setTarget(BLOB_INFO1, Storage.BlobTargetOption.encryptionKey(BASE64_KEY))
            .build();
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            ENCRYPTION_KEY_OPTIONS,
            true,
            request.getTarget().toPb(),
            ENCRYPTION_KEY_OPTIONS,
            null);
    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse, rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
    request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO2.getBlobId())
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(BASE64_KEY))
            .setTarget(BLOB_INFO1, Storage.BlobTargetOption.encryptionKey(KEY))
            .build();
    writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testCopyFromEncryptionKeyToKmsKeyName() {
    Storage.CopyRequest request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO2.getBlobId())
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(KEY))
            .setTarget(BLOB_INFO1, Storage.BlobTargetOption.kmsKeyName(KMS_KEY_NAME))
            .build();
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            ENCRYPTION_KEY_OPTIONS,
            true,
            request.getTarget().toPb(),
            KMS_KEY_NAME_OPTIONS,
            null);
    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse, rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
    request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO2.getBlobId())
            .setSourceOptions(Storage.BlobSourceOption.decryptionKey(BASE64_KEY))
            .setTarget(BLOB_INFO1, Storage.BlobTargetOption.kmsKeyName(KMS_KEY_NAME))
            .build();
    writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testCopyWithOptionsFromBlobId() {
    Storage.CopyRequest request =
        Storage.CopyRequest.newBuilder()
            .setSource(BLOB_INFO1.getBlobId())
            .setSourceOptions(BLOB_SOURCE_GENERATION_FROM_BLOB_ID, BLOB_SOURCE_METAGENERATION)
            .setTarget(BLOB_INFO1, BLOB_TARGET_GENERATION, BLOB_TARGET_METAGENERATION)
            .build();
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            BLOB_SOURCE_OPTIONS_COPY,
            true,
            request.getTarget().toPb(),
            BLOB_TARGET_OPTIONS_COMPOSE,
            null);
    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testCopyMultipleRequests() {
    Storage.CopyRequest request =
        Storage.CopyRequest.of(BLOB_INFO1.getBlobId(), BLOB_INFO2.getBlobId());
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            EMPTY_RPC_OPTIONS,
            false,
            BLOB_INFO2.toPb(),
            EMPTY_RPC_OPTIONS,
            null);
    StorageRpc.RewriteResponse rpcResponse1 =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    StorageRpc.RewriteResponse rpcResponse2 =
        new StorageRpc.RewriteResponse(rpcRequest, BLOB_INFO1.toPb(), 42L, true, "token", 42L);
    doReturn(rpcResponse1)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);

    doReturn(rpcResponse2)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .continueRewrite(rpcResponse1);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
    assertEquals(expectedBlob1, writer.getResult());
    assertTrue(writer.isDone());
    assertEquals(42L, writer.getTotalBytesCopied());
    assertEquals(42L, writer.getBlobSize());
  }

  @Test
  public void testCopyFailure() {
    Storage.CopyRequest request =
        Storage.CopyRequest.of(BLOB_INFO1.getBlobId(), BLOB_INFO2.getBlobId());
    StorageRpc.RewriteRequest rpcRequest =
        new StorageRpc.RewriteRequest(
            request.getSource().toPb(),
            EMPTY_RPC_OPTIONS,
            false,
            BLOB_INFO2.toPb(),
            EMPTY_RPC_OPTIONS,
            null);
    doThrow(STORAGE_FAILURE).when(storageRpcMock).openRewrite(rpcRequest);
    initializeService();
    try {
      storage.copy(request);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }

    StorageRpc.RewriteResponse rpcResponse =
        new StorageRpc.RewriteResponse(rpcRequest, null, 42L, false, "token", 21L);
    doReturn(rpcResponse)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .openRewrite(rpcRequest);
    initializeService();
    CopyWriter writer = storage.copy(request);
    assertEquals(42L, writer.getBlobSize());
    assertEquals(21L, writer.getTotalBytesCopied());
    assertFalse(writer.isDone());
  }

  @Test
  public void testReadAllBytes() {
    doReturn(BLOB_CONTENT)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .load(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    byte[] readBytes = storage.readAllBytes(BUCKET_NAME1, BLOB_NAME1);
    assertArrayEquals(BLOB_CONTENT, readBytes);
  }

  @Test
  public void testReadAllBytesWithOptions() {
    doReturn(BLOB_CONTENT)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .load(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), BLOB_SOURCE_OPTIONS);
    initializeService();
    byte[] readBytes =
        storage.readAllBytes(
            BUCKET_NAME1, BLOB_NAME1, BLOB_SOURCE_GENERATION, BLOB_SOURCE_METAGENERATION);
    assertArrayEquals(BLOB_CONTENT, readBytes);
  }

  @Test
  public void testReadAllBytesWithDecryptionKey() {
    doReturn(BLOB_CONTENT, BLOB_CONTENT)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .load(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), ENCRYPTION_KEY_OPTIONS);
    initializeService();
    byte[] readBytes =
        storage.readAllBytes(BUCKET_NAME1, BLOB_NAME1, Storage.BlobSourceOption.decryptionKey(KEY));
    assertArrayEquals(BLOB_CONTENT, readBytes);
    readBytes =
        storage.readAllBytes(
            BUCKET_NAME1, BLOB_NAME1, Storage.BlobSourceOption.decryptionKey(BASE64_KEY));
    assertArrayEquals(BLOB_CONTENT, readBytes);
  }

  @Test
  public void testReadAllBytesFromBlobIdWithOptions() {
    doReturn(BLOB_CONTENT)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .load(BLOB_INFO1.getBlobId().toPb(), BLOB_SOURCE_OPTIONS);
    initializeService();
    byte[] readBytes =
        storage.readAllBytes(
            BLOB_INFO1.getBlobId(),
            BLOB_SOURCE_GENERATION_FROM_BLOB_ID,
            BLOB_SOURCE_METAGENERATION);
    assertArrayEquals(BLOB_CONTENT, readBytes);
  }

  @Test
  public void testReadAllBytesFromBlobIdWithDecryptionKey() {
    doReturn(BLOB_CONTENT, BLOB_CONTENT)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .load(BLOB_INFO1.getBlobId().toPb(), ENCRYPTION_KEY_OPTIONS);

    initializeService();
    byte[] readBytes =
        storage.readAllBytes(BLOB_INFO1.getBlobId(), Storage.BlobSourceOption.decryptionKey(KEY));
    assertArrayEquals(BLOB_CONTENT, readBytes);
    readBytes =
        storage.readAllBytes(
            BLOB_INFO1.getBlobId(), Storage.BlobSourceOption.decryptionKey(BASE64_KEY));
    assertArrayEquals(BLOB_CONTENT, readBytes);
  }

  @Test
  public void testReadAllBytesFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .load(BlobId.of(BUCKET_NAME1, BLOB_NAME1).toPb(), EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.readAllBytes(BUCKET_NAME1, BLOB_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testBatch() {
    RpcBatch batchMock = mock(RpcBatch.class);
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();
    StorageBatch batch = storage.batch();
    assertSame(options, batch.getOptions());
    assertSame(storageRpcMock, batch.getStorageRpc());
    assertSame(batchMock, batch.getBatch());
  }

  @Test
  public void testBatchFailure() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).createBatch();
    initializeService();
    try {
      storage.batch();
      fail();
    } catch (RuntimeException e) {
      assertSame(STORAGE_FAILURE, e);
    }
  }

  @Test
  public void testSignUrl()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url = storage.signUrl(BLOB_INFO1, 14, TimeUnit.DAYS);
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlWithHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://example.com"));
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://example.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlLeadingSlash()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    String blobName = "/b1";
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url =
        storage.signUrl(BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
    String expectedResourcePath = "/b1";
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append("/")
            .append(expectedResourcePath)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append("/")
        .append(expectedResourcePath);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlLeadingSlashWithHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    String blobName = "/b1";
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url =
        storage.signUrl(
            BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(),
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://example.com"));
    String escapedBlobName = Rfc3986UriEncode(blobName, false);
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://example.com/")
            .append(BUCKET_NAME1)
            .append("/")
            .append(escapedBlobName)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append("/")
        .append(escapedBlobName);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlWithOptions()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.POST),
            Storage.SignUrlOption.withContentType(),
            Storage.SignUrlOption.withMd5());
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.POST)
        .append('\n')
        .append(BLOB_INFO1.getMd5())
        .append('\n')
        .append(BLOB_INFO1.getContentType())
        .append('\n')
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlWithOptionsAndHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.POST),
            Storage.SignUrlOption.withContentType(),
            Storage.SignUrlOption.withMd5(),
            Storage.SignUrlOption.withHostName("https://example.com"));
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://example.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.POST)
        .append('\n')
        .append(BLOB_INFO1.getMd5())
        .append('\n')
        .append(BLOB_INFO1.getContentType())
        .append('\n')
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlForBlobWithSpecialChars()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    Map<Character, String> encodingCharsToTest =
        new HashMap<Character, String>(RFC3986_URI_ENCODING_MAP);
    // Signed URL specs say that '/' is not encoded in the resource name (path segment of the URI).
    encodingCharsToTest.put('/', "/");
    for (Map.Entry<Character, String> entry : encodingCharsToTest.entrySet()) {
      String blobName = "/a" + entry.getKey() + "b";
      URL url =
          storage.signUrl(BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
      String expectedBlobName = "/a" + entry.getValue() + "b";
      String stringUrl = url.toString();
      String expectedUrl =
          new StringBuilder("https://storage.googleapis.com/")
              .append(BUCKET_NAME1)
              .append("/")
              .append(expectedBlobName)
              .append("?GoogleAccessId=")
              .append(ACCOUNT)
              .append("&Expires=")
              .append(42L + 1209600)
              .append("&Signature=")
              .toString();
      assertTrue(stringUrl.startsWith(expectedUrl));
      String signature = stringUrl.substring(expectedUrl.length());

      StringBuilder signedMessageBuilder = new StringBuilder();
      signedMessageBuilder
          .append(HttpMethod.GET)
          .append("\n\n\n")
          .append(42L + 1209600)
          .append("\n/")
          .append(BUCKET_NAME1)
          .append("/")
          .append(expectedBlobName);

      Signature signer = Signature.getInstance("SHA256withRSA");
      signer.initVerify(publicKey);
      signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
      assertTrue(
          signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
    }
  }

  @Test
  public void testSignUrlForBlobWithSpecialCharsAndHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    Map<Character, String> encodingCharsToTest =
        new HashMap<Character, String>(RFC3986_URI_ENCODING_MAP);
    // Signed URL specs say that '/' is not encoded in the resource name (path segment of the URI).
    encodingCharsToTest.put('/', "/");
    for (Map.Entry<Character, String> entry : encodingCharsToTest.entrySet()) {
      String blobName = "/a" + entry.getKey() + "b";
      URL url =
          storage.signUrl(
              BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(),
              14,
              TimeUnit.DAYS,
              Storage.SignUrlOption.withHostName("https://example.com"));
      String expectedBlobName = "/a" + entry.getValue() + "b";
      String stringUrl = url.toString();
      String expectedUrl =
          new StringBuilder("https://example.com/")
              .append(BUCKET_NAME1)
              .append("/")
              .append(expectedBlobName)
              .append("?GoogleAccessId=")
              .append(ACCOUNT)
              .append("&Expires=")
              .append(42L + 1209600)
              .append("&Signature=")
              .toString();
      assertTrue(stringUrl.startsWith(expectedUrl));
      String signature = stringUrl.substring(expectedUrl.length());

      StringBuilder signedMessageBuilder = new StringBuilder();
      signedMessageBuilder
          .append(HttpMethod.GET)
          .append("\n\n\n")
          .append(42L + 1209600)
          .append("\n/")
          .append(BUCKET_NAME1)
          .append("/")
          .append(expectedBlobName);

      Signature signer = Signature.getInstance("SHA256withRSA");
      signer.initVerify(publicKey);
      signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
      assertTrue(
          signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
    }
  }

  @Test
  public void testSignUrlWithExtHeaders()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    Map<String, String> extHeaders = new HashMap<String, String>();
    extHeaders.put("x-goog-acl", "public-read");
    extHeaders.put("x-goog-meta-owner", "myself");
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withContentType(),
            Storage.SignUrlOption.withExtHeaders(extHeaders));
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.PUT)
        .append('\n')
        .append('\n')
        .append(BLOB_INFO1.getContentType())
        .append('\n')
        .append(42L + 1209600)
        .append('\n')
        .append("x-goog-acl:public-read\n")
        .append("x-goog-meta-owner:myself\n")
        .append('/')
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlWithExtHeadersAndHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();
    Map<String, String> extHeaders = new HashMap<String, String>();
    extHeaders.put("x-goog-acl", "public-read");
    extHeaders.put("x-goog-meta-owner", "myself");
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withContentType(),
            Storage.SignUrlOption.withExtHeaders(extHeaders),
            Storage.SignUrlOption.withHostName("https://example.com"));
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://example.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.PUT)
        .append('\n')
        .append('\n')
        .append(BLOB_INFO1.getContentType())
        .append('\n')
        .append(42L + 1209600)
        .append('\n')
        .append("x-goog-acl:public-read\n")
        .append("x-goog-meta-owner:myself\n")
        .append('/')
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlForBlobWithSlashes()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    String blobName = "/foo/bar/baz #%20other cool stuff.txt";
    URL url =
        storage.signUrl(BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(), 14, TimeUnit.DAYS);
    String escapedBlobName = Rfc3986UriEncode(blobName, false);
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append("/")
            .append(escapedBlobName)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append("/")
        .append(escapedBlobName);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testSignUrlForBlobWithSlashesAndHostName()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    String blobName = "/foo/bar/baz #%20other cool stuff.txt";
    URL url =
        storage.signUrl(
            BlobInfo.newBuilder(BUCKET_NAME1, blobName).build(),
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withHostName("https://example.com"));
    String escapedBlobName = Rfc3986UriEncode(blobName, false);
    String stringUrl = url.toString();
    String expectedUrl =
        new StringBuilder("https://example.com/")
            .append(BUCKET_NAME1)
            .append("/")
            .append(escapedBlobName)
            .append("?GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedUrl));
    String signature = stringUrl.substring(expectedUrl.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append("\n\n\n")
        .append(42L + 1209600)
        .append("\n/")
        .append(BUCKET_NAME1)
        .append("/")
        .append(escapedBlobName);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  @Test
  public void testV2SignUrlWithQueryParams()
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
          UnsupportedEncodingException {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    String dispositionNotEncoded = "attachment; filename=\"" + BLOB_NAME1 + "\"";
    String dispositionEncoded = "attachment%3B%20filename%3D%22" + BLOB_NAME1 + "%22";
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            14,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withV2Signature(),
            Storage.SignUrlOption.withQueryParams(
                ImmutableMap.<String, String>of(
                    "response-content-disposition", dispositionNotEncoded)));

    String stringUrl = url.toString();

    String expectedPrefix =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            // Query params aren't sorted for V2 signatures; user-supplied params are inserted at
            // the start of the query string, before the required auth params.
            .append("?response-content-disposition=")
            .append(dispositionEncoded)
            .append("&GoogleAccessId=")
            .append(ACCOUNT)
            .append("&Expires=")
            .append(42L + 1209600)
            .append("&Signature=")
            .toString();
    assertTrue(stringUrl.startsWith(expectedPrefix));
    String signature = stringUrl.substring(expectedPrefix.length());

    StringBuilder signedMessageBuilder = new StringBuilder();
    signedMessageBuilder
        .append(HttpMethod.GET)
        .append('\n')
        // No value for Content-MD5, blank
        .append('\n')
        // No value for Content-Type, blank
        .append('\n')
        // Expiration line:
        .append(42L + 1209600)
        .append('\n')
        // Resource line:
        .append('/')
        .append(BUCKET_NAME1)
        .append('/')
        .append(BLOB_NAME1);

    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(signedMessageBuilder.toString().getBytes(UTF_8));
    assertTrue(
        signer.verify(BaseEncoding.base64().decode(URLDecoder.decode(signature, UTF_8.name()))));
  }

  // TODO(b/144304815): Remove this test once all conformance tests contain query param test cases.
  @Test
  public void testV4SignUrlWithQueryParams() {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    String dispositionNotEncoded = "attachment; filename=\"" + BLOB_NAME1 + "\"";
    String dispositionEncoded = "attachment%3B%20filename%3D%22" + BLOB_NAME1 + "%22";
    URL url =
        storage.signUrl(
            BLOB_INFO1,
            6,
            TimeUnit.DAYS,
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withV4Signature(),
            Storage.SignUrlOption.withQueryParams(
                ImmutableMap.<String, String>of(
                    "response-content-disposition", dispositionNotEncoded)));
    String stringUrl = url.toString();
    String expectedPrefix =
        new StringBuilder("https://storage.googleapis.com/")
            .append(BUCKET_NAME1)
            .append('/')
            .append(BLOB_NAME1)
            .append('?')
            .toString();
    assertTrue(stringUrl.startsWith(expectedPrefix));
    String restOfUrl = stringUrl.substring(expectedPrefix.length());

    Pattern pattern =
        Pattern.compile(
            // We use the same code to construct the canonical request query string as we do to
            // construct the query string used in the final URL, so this query string should also be
            // sorted correctly, except for the trailing x-goog-signature param.
            new StringBuilder("X-Goog-Algorithm=GOOG4-RSA-SHA256")
                .append("&X-Goog-Credential=[^&]+")
                .append("&X-Goog-Date=[^&]+")
                .append("&X-Goog-Expires=[^&]+")
                .append("&X-Goog-SignedHeaders=[^&]+")
                .append("&response-content-disposition=[^&]+")
                // Signature is always tacked onto the end of the final URL; it's not sorted w/ the
                // other params above, since the signature is not known when you're constructing the
                // query string line of the canonical request string.
                .append("&X-Goog-Signature=.*")
                .toString());
    Matcher matcher = pattern.matcher(restOfUrl);
    assertTrue(restOfUrl, matcher.matches());

    // Make sure query param was encoded properly.
    assertNotEquals(-1, restOfUrl.indexOf("&response-content-disposition=" + dispositionEncoded));
  }

  @Test
  public void testGetAllArray() {
    BlobId blobId1 = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    BlobId blobId2 = BlobId.of(BUCKET_NAME1, BLOB_NAME2);
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<StorageObject>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addGet(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Blob> resultBlobs = storage.get(blobId1, blobId2);

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, resultBlobs.size());
    callback.getAllValues().get(0).onFailure(new GoogleJsonError());
    callback.getAllValues().get(1).onSuccess(BLOB_INFO2.toPb());
    assertEquals(2, resultBlobs.size());
    assertNull(resultBlobs.get(0));
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO2)), resultBlobs.get(1));
  }

  @Test
  public void testGetAllArrayIterable() {
    BlobId blobId1 = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    BlobId blobId2 = BlobId.of(BUCKET_NAME1, BLOB_NAME2);
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<StorageObject>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addGet(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Blob> resultBlobs = storage.get(ImmutableList.of(blobId1, blobId2));

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, resultBlobs.size());
    callback.getAllValues().get(0).onFailure(new GoogleJsonError());
    callback.getAllValues().get(1).onSuccess(BLOB_INFO2.toPb());
    assertEquals(2, resultBlobs.size());
    assertNull(resultBlobs.get(0));
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO2)), resultBlobs.get(1));
  }

  @Test
  public void testDeleteAllArray() {
    BlobId blobId1 = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    BlobId blobId2 = BlobId.of(BUCKET_NAME1, BLOB_NAME2);
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<Void>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addDelete(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Boolean> result = storage.delete(blobId1, blobId2);

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, result.size());
    callback.getAllValues().get(0).onFailure(new GoogleJsonError());
    callback.getAllValues().get(1).onSuccess(null);
    assertEquals(2, result.size());
    assertFalse(result.get(0));
    assertTrue(result.get(1));
  }

  @Test
  public void testDeleteAllIterable() {
    BlobId blobId1 = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    BlobId blobId2 = BlobId.of(BUCKET_NAME1, BLOB_NAME2);
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<Void>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addDelete(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Boolean> result = storage.delete(ImmutableList.of(blobId1, blobId2));

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, result.size());
    callback.getAllValues().get(0).onSuccess(null);
    callback.getAllValues().get(1).onFailure(new GoogleJsonError());
    assertEquals(2, result.size());
    assertTrue(result.get(0));
    assertFalse(result.get(1));
  }

  @Test
  public void testUpdateAllArray() {
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<StorageObject>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addPatch(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Blob> resultBlobs = storage.update(BLOB_INFO1, BLOB_INFO2);

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, resultBlobs.size());
    callback.getAllValues().get(0).onSuccess(BLOB_INFO1.toPb());
    callback.getAllValues().get(1).onFailure(new GoogleJsonError());
    assertEquals(2, resultBlobs.size());
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO1)), resultBlobs.get(0));
    assertNull(resultBlobs.get(1));
  }

  @Test
  public void testUpdateAllIterable() {
    RpcBatch batchMock = mock(RpcBatch.class);
    ArgumentCaptor<StorageObject> storageObject = ArgumentCaptor.forClass(StorageObject.class);
    ArgumentCaptor<RpcBatch.Callback<StorageObject>> callback =
        ArgumentCaptor.forClass(RpcBatch.Callback.class);
    doNothing()
        .doNothing()
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(batchMock)
        .addPatch(
            storageObject.capture(),
            callback.capture(),
            Mockito.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
    doReturn(batchMock).doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).createBatch();
    initializeService();

    List<Blob> resultBlobs = storage.update(ImmutableList.of(BLOB_INFO1, BLOB_INFO2));

    assertEquals(BLOB_NAME1, storageObject.getAllValues().get(0).getName());
    assertEquals(BLOB_NAME2, storageObject.getAllValues().get(1).getName());
    assertEquals(0, resultBlobs.size());
    callback.getAllValues().get(0).onSuccess(BLOB_INFO1.toPb());
    callback.getAllValues().get(1).onFailure(new GoogleJsonError());
    assertEquals(2, resultBlobs.size());
    assertEquals(new Blob(storage, new BlobInfo.BuilderImpl(BLOB_INFO1)), resultBlobs.get(0));
    assertNull(resultBlobs.get(1));
  }

  @Test
  public void testGetDeleteUpdateAllFailure() {
    BlobId blobId1 = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    BlobId blobId2 = BlobId.of(BUCKET_NAME1, BLOB_NAME2);

    Answer answer =
        new Answer<Object>() {
          @Override
          public Object answer(InvocationOnMock invocation) {
            throw STORAGE_FAILURE;
          };
        };

    RpcBatch batchMock = mock(RpcBatch.class, answer);
    doReturn(batchMock, batchMock, batchMock, batchMock, batchMock, batchMock)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .createBatch();

    initializeService();

    for (int i = 0; i < 6; i++) {
      try {
        if (i == 0) {
          storage.get(blobId1, blobId2);
        } else if (i == 1) {
          storage.get(ImmutableList.of(blobId1, blobId2));
        } else if (i == 2) {
          storage.delete(blobId1, blobId2);
        } else if (i == 3) {
          storage.delete(ImmutableList.of(blobId1, blobId2));
        } else if (i == 4) {
          storage.update(BLOB_INFO1, BLOB_INFO2);
        } else if (i == 5) {
          storage.update(ImmutableList.of(BLOB_INFO1, BLOB_INFO2));
        }
        fail();
      } catch (RuntimeException e) {
        assertSame(STORAGE_FAILURE, e);
      }
    }
  }

  @Test
  public void testGetBucketAcl() {
    doReturn(ACL.toBucketPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>());

    initializeService();
    Acl acl = storage.getAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
    assertEquals(ACL, acl);
  }

  @Test
  public void testGetBucketAclNull() {
    doReturn(null)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>());

    initializeService();
    assertNull(storage.getAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testGetBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>());
    initializeService();
    try {
      storage.getAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testDeleteBucketAcl() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .deleteAcl(BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>());
    initializeService();
    assertTrue(storage.deleteAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testDeleteBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .deleteAcl(BUCKET_NAME1, "allAuthenticatedUsers", new HashMap<StorageRpc.Option, Object>());
    initializeService();
    try {
      storage.deleteAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testCreateBucketAcl() {
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toBucketPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .createAcl(
            ACL.toBucketPb().setBucket(BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>());
    initializeService();
    Acl acl = storage.createAcl(BUCKET_NAME1, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testCreateBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .createAcl(
            ACL.toBucketPb().setBucket(BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>());
    initializeService();
    try {
      storage.createAcl(BUCKET_NAME1, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testUpdateBucketAcl() {
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toBucketPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patchAcl(
            ACL.toBucketPb().setBucket(BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>());
    initializeService();
    Acl acl = storage.updateAcl(BUCKET_NAME1, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testUpdateBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .patchAcl(
            ACL.toBucketPb().setBucket(BUCKET_NAME1), new HashMap<StorageRpc.Option, Object>());
    initializeService();
    try {
      storage.updateAcl(BUCKET_NAME1, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testListBucketAcl() {
    doReturn(ImmutableList.of(ACL.toBucketPb(), OTHER_ACL.toBucketPb()))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .listAcls(BUCKET_NAME1, new HashMap<StorageRpc.Option, Object>());
    initializeService();
    List<Acl> acls = storage.listAcls(BUCKET_NAME1);
    assertEquals(ImmutableList.of(ACL, OTHER_ACL), acls);
  }

  @Test
  public void testListBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .listAcls(BUCKET_NAME1, new HashMap<StorageRpc.Option, Object>());
    initializeService();
    try {
      storage.listAcls(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testGetDefaultBucketAcl() {
    doReturn(ACL.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getDefaultAcl(BUCKET_NAME1, "allAuthenticatedUsers");
    initializeService();
    Acl acl = storage.getDefaultAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
    assertEquals(ACL, acl);
  }

  @Test
  public void testGetDefaultBucketAclNull() {
    doReturn(null)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getDefaultAcl(BUCKET_NAME1, "allAuthenticatedUsers");
    initializeService();
    assertNull(storage.getDefaultAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void tesGetDefaultBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .getDefaultAcl(BUCKET_NAME1, "allAuthenticatedUsers");
    initializeService();
    try {
      storage.getDefaultAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testDeleteDefaultBucketAcl() {
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .deleteDefaultAcl(BUCKET_NAME1, "allAuthenticatedUsers");
    initializeService();
    assertTrue(storage.deleteDefaultAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testDeleteDefaultBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .deleteDefaultAcl(BUCKET_NAME1, "allAuthenticatedUsers");
    initializeService();
    try {
      storage.deleteDefaultAcl(BUCKET_NAME1, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testCreateDefaultBucketAcl() {
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .createDefaultAcl(ACL.toObjectPb().setBucket(BUCKET_NAME1));
    initializeService();
    Acl acl = storage.createDefaultAcl(BUCKET_NAME1, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testCreateDefaultBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .createDefaultAcl(ACL.toObjectPb().setBucket(BUCKET_NAME1));
    initializeService();
    try {
      storage.createDefaultAcl(BUCKET_NAME1, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testUpdateDefaultBucketAcl() {
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patchDefaultAcl(ACL.toObjectPb().setBucket(BUCKET_NAME1));
    initializeService();
    Acl acl = storage.updateDefaultAcl(BUCKET_NAME1, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testUpdateDefaultBucketAclFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .patchDefaultAcl(ACL.toObjectPb().setBucket(BUCKET_NAME1));
    initializeService();
    try {
      storage.updateDefaultAcl(BUCKET_NAME1, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testListDefaultBucketAcl() {
    doReturn(ImmutableList.of(ACL.toObjectPb(), OTHER_ACL.toObjectPb()))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .listDefaultAcls(BUCKET_NAME1);
    initializeService();
    List<Acl> acls = storage.listDefaultAcls(BUCKET_NAME1);
    assertEquals(ImmutableList.of(ACL, OTHER_ACL), acls);
  }

  @Test
  public void testListDefaultBucketAclFailure() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).listDefaultAcls(BUCKET_NAME1);
    initializeService();
    try {
      storage.listDefaultAcls(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testGetBlobAcl() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doReturn(ACL.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, BLOB_NAME1, 42L, "allAuthenticatedUsers");
    initializeService();
    Acl acl = storage.getAcl(blobId, Acl.User.ofAllAuthenticatedUsers());
    assertEquals(ACL, acl);
  }

  @Test
  public void testGetBlobAclNull() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doReturn(null)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, BLOB_NAME1, 42L, "allAuthenticatedUsers");
    initializeService();
    assertNull(storage.getAcl(blobId, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testGetBlobAclFailure() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .getAcl(BUCKET_NAME1, BLOB_NAME1, 42L, "allAuthenticatedUsers");
    initializeService();
    try {
      storage.getAcl(blobId, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testDeleteBlobAcl() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doReturn(true)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .deleteAcl(BUCKET_NAME1, BLOB_NAME1, 42L, "allAuthenticatedUsers");
    initializeService();
    assertTrue(storage.deleteAcl(blobId, Acl.User.ofAllAuthenticatedUsers()));
  }

  @Test
  public void testDeleteBlobAclFailure() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .deleteAcl(BUCKET_NAME1, BLOB_NAME1, 42L, "allAuthenticatedUsers");
    initializeService();
    try {
      storage.deleteAcl(blobId, Acl.User.ofAllAuthenticatedUsers());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testCreateBlobAcl() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .createAcl(
            ACL.toObjectPb().setBucket(BUCKET_NAME1).setObject(BLOB_NAME1).setGeneration(42L));
    initializeService();
    Acl acl = storage.createAcl(blobId, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testCreateBlobAclFailure() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .createAcl(
            ACL.toObjectPb().setBucket(BUCKET_NAME1).setObject(BLOB_NAME1).setGeneration(42L));
    initializeService();
    try {
      storage.createAcl(blobId, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testUpdateBlobAcl() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    Acl returnedAcl = ACL.toBuilder().setEtag("ETAG").setId("ID").build();
    doReturn(returnedAcl.toObjectPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .patchAcl(
            ACL.toObjectPb().setBucket(BUCKET_NAME1).setObject(BLOB_NAME1).setGeneration(42L));
    initializeService();
    Acl acl = storage.updateAcl(blobId, ACL);
    assertEquals(returnedAcl, acl);
  }

  @Test
  public void testUpdateBlobAclFailure() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .patchAcl(
            ACL.toObjectPb().setBucket(BUCKET_NAME1).setObject(BLOB_NAME1).setGeneration(42L));
    initializeService();
    try {
      storage.updateAcl(blobId, ACL);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testListBlobAcl() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doReturn(ImmutableList.of(ACL.toObjectPb(), OTHER_ACL.toObjectPb()))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .listAcls(BUCKET_NAME1, BLOB_NAME1, 42L);
    initializeService();
    List<Acl> acls = storage.listAcls(blobId);
    assertEquals(ImmutableList.of(ACL, OTHER_ACL), acls);
  }

  @Test
  public void testListBlobAclFailure() {
    BlobId blobId = BlobId.of(BUCKET_NAME1, BLOB_NAME1, 42L);
    doThrow(STORAGE_FAILURE).when(storageRpcMock).listAcls(BUCKET_NAME1, BLOB_NAME1, 42L);
    initializeService();
    try {
      storage.listAcls(blobId);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testGetIamPolicy() {
    doReturn(API_POLICY1)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getIamPolicy(BUCKET_NAME1, EMPTY_RPC_OPTIONS);
    initializeService();
    assertEquals(LIB_POLICY1, storage.getIamPolicy(BUCKET_NAME1));
  }

  @Test
  public void testGetIamPolicyFailure() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).getIamPolicy(BUCKET_NAME1, EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.getIamPolicy(BUCKET_NAME1);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testSetIamPolicy() {
    com.google.api.services.storage.model.Policy preCommitApiPolicy =
        new com.google.api.services.storage.model.Policy()
            .setBindings(
                ImmutableList.of(
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(ImmutableList.of("allUsers"))
                        .setRole("roles/storage.objectViewer"),
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(
                            ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com"))
                        .setRole("roles/storage.objectAdmin"),
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(ImmutableList.of("group:test-group@gmail.com"))
                        .setRole("roles/storage.admin")))
            .setEtag(POLICY_ETAG1)
            .setVersion(1);
    // postCommitApiPolicy is identical but for the etag, which has been updated.
    com.google.api.services.storage.model.Policy postCommitApiPolicy =
        new com.google.api.services.storage.model.Policy()
            .setBindings(
                ImmutableList.of(
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(ImmutableList.of("allUsers"))
                        .setRole("roles/storage.objectViewer"),
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(
                            ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com"))
                        .setRole("roles/storage.objectAdmin"),
                    new com.google.api.services.storage.model.Policy.Bindings()
                        .setMembers(ImmutableList.of("group:test-group@gmail.com"))
                        .setRole("roles/storage.admin")))
            .setEtag(POLICY_ETAG2)
            .setVersion(1);
    Policy postCommitLibPolicy =
        Policy.newBuilder()
            .addIdentity(StorageRoles.objectViewer(), Identity.allUsers())
            .addIdentity(
                StorageRoles.objectAdmin(),
                Identity.user("test1@gmail.com"),
                Identity.user("test2@gmail.com"))
            .addIdentity(StorageRoles.admin(), Identity.group("test-group@gmail.com"))
            .setEtag(POLICY_ETAG2)
            .setVersion(1)
            .build();

    doReturn(API_POLICY1)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getIamPolicy(BUCKET_NAME1, EMPTY_RPC_OPTIONS);

    doReturn(postCommitApiPolicy)
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .setIamPolicy(BUCKET_NAME1, preCommitApiPolicy, EMPTY_RPC_OPTIONS);

    initializeService();

    Policy currentPolicy = storage.getIamPolicy(BUCKET_NAME1);
    Policy updatedPolicy =
        storage.setIamPolicy(
            BUCKET_NAME1,
            currentPolicy
                .toBuilder()
                .addIdentity(StorageRoles.admin(), Identity.group("test-group@gmail.com"))
                .build());
    assertEquals(updatedPolicy, postCommitLibPolicy);
  }

  @Test
  public void testSetIamPolicyFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .setIamPolicy(
            any(String.class),
            any(com.google.api.services.storage.model.Policy.class),
            any(Map.class));
    initializeService();
    try {
      storage.setIamPolicy(BUCKET_NAME1, Policy.newBuilder().build());
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testTestIamPermissionsNull() {
    List<Boolean> expectedPermissions = ImmutableList.of(false, false, false);
    List<String> checkedPermissions =
        ImmutableList.of(
            "storage.buckets.get", "storage.buckets.getIamPolicy", "storage.objects.list");
    doReturn(new TestIamPermissionsResponse())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .testIamPermissions(BUCKET_NAME1, checkedPermissions, EMPTY_RPC_OPTIONS);

    initializeService();
    assertEquals(expectedPermissions, storage.testIamPermissions(BUCKET_NAME1, checkedPermissions));
  }

  @Test
  public void testTestIamPermissionsNonNull() {
    List<Boolean> expectedPermissions = ImmutableList.of(true, false, true);
    List<String> checkedPermissions =
        ImmutableList.of(
            "storage.buckets.get", "storage.buckets.getIamPolicy", "storage.objects.list");

    doReturn(
            new TestIamPermissionsResponse()
                .setPermissions(ImmutableList.of("storage.objects.list", "storage.buckets.get")))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .testIamPermissions(BUCKET_NAME1, checkedPermissions, EMPTY_RPC_OPTIONS);
    initializeService();
    assertEquals(expectedPermissions, storage.testIamPermissions(BUCKET_NAME1, checkedPermissions));
  }

  @Test
  public void testTestIamPolicyFailure() {
    List<String> permissions = ImmutableList.of("test");
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .testIamPermissions(BUCKET_NAME1, permissions, EMPTY_RPC_OPTIONS);
    initializeService();
    try {
      storage.testIamPermissions(BUCKET_NAME1, permissions);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testLockRetentionPolicy() {
    doReturn(BUCKET_INFO3.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .lockRetentionPolicy(BUCKET_INFO3.toPb(), BUCKET_TARGET_OPTIONS_LOCK_RETENTION_POLICY);

    initializeService();
    Bucket bucket =
        storage.lockRetentionPolicy(
            BUCKET_INFO3, BUCKET_TARGET_METAGENERATION, BUCKET_TARGET_USER_PROJECT);
    assertEquals(expectedBucket3, bucket);
  }

  @Test
  public void testLockRetentionPolicyFailure() {
    doThrow(STORAGE_FAILURE)
        .when(storageRpcMock)
        .lockRetentionPolicy(BUCKET_INFO3.toPb(), BUCKET_TARGET_OPTIONS_LOCK_RETENTION_POLICY);
    initializeService();
    try {
      storage.lockRetentionPolicy(
          BUCKET_INFO3, BUCKET_TARGET_METAGENERATION, BUCKET_TARGET_USER_PROJECT);
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testGetServiceAccount() {
    doReturn(SERVICE_ACCOUNT.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .getServiceAccount("projectId");
    initializeService();
    ServiceAccount serviceAccount = storage.getServiceAccount("projectId");
    assertEquals(SERVICE_ACCOUNT, serviceAccount);
  }

  @Test
  public void testGetServiceAccountFailure() {
    doThrow(STORAGE_FAILURE).when(storageRpcMock).getServiceAccount("projectId");
    initializeService();
    try {
      storage.getServiceAccount("projectId");
      fail();
    } catch (StorageException e) {
      assertSame(STORAGE_FAILURE, e.getCause());
    }
  }

  @Test
  public void testRetryableException() {
    BlobId blob = BlobId.of(BUCKET_NAME1, BLOB_NAME1);

    doThrow(new StorageException(500, "internalError"))
        .doReturn(BLOB_INFO1.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(blob.toPb(), EMPTY_RPC_OPTIONS);

    storage =
        options
            .toBuilder()
            .setRetrySettings(ServiceOptions.getDefaultRetrySettings())
            .build()
            .getService();
    initializeServiceDependentObjects();
    Blob readBlob = storage.get(blob);
    assertEquals(expectedBlob1, readBlob);
  }

  @Test
  public void testNonRetryableException() {
    BlobId blob = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    String exceptionMessage = "Not Implemented";
    doThrow(new StorageException(501, exceptionMessage))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(blob.toPb(), EMPTY_RPC_OPTIONS);
    storage =
        options
            .toBuilder()
            .setRetrySettings(ServiceOptions.getDefaultRetrySettings())
            .build()
            .getService();
    initializeServiceDependentObjects();
    try {
      storage.get(blob);
      Assert.fail();
    } catch (StorageException ex) {
      Assert.assertNotNull(ex.getMessage());
    }
  }

  @Test
  public void testRuntimeException() {
    BlobId blob = BlobId.of(BUCKET_NAME1, BLOB_NAME1);
    String exceptionMessage = "Artificial runtime exception";
    doThrow(new RuntimeException(exceptionMessage))
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .get(blob.toPb(), EMPTY_RPC_OPTIONS);

    storage =
        options
            .toBuilder()
            .setRetrySettings(ServiceOptions.getDefaultRetrySettings())
            .build()
            .getService();
    try {
      storage.get(blob);
      Assert.fail();
    } catch (StorageException ex) {
      Assert.assertNotNull(ex.getMessage());
    }
  }

  @Test
  public void testWriterWithSignedURL() throws MalformedURLException {
    doReturn("upload-id").doThrow(UNEXPECTED_CALL_EXCEPTION).when(storageRpcMock).open(SIGNED_URL);
    initializeService();
    WriteChannel writer = new BlobWriteChannel(options, new URL(SIGNED_URL));
    assertNotNull(writer);
    assertTrue(writer.isOpen());
  }

  @Test
  public void testV4PostPolicy() {
    ServiceAccountCredentials credentials =
        ServiceAccountCredentials.newBuilder()
            .setClientEmail(ACCOUNT)
            .setPrivateKey(privateKey)
            .build();
    storage = options.toBuilder().setCredentials(credentials).build().getService();

    PostPolicyV4.PostFieldsV4 fields =
        PostPolicyV4.PostFieldsV4.newBuilder().setAcl("public-read").build();
    PostPolicyV4.PostConditionsV4 conditions =
        PostPolicyV4.PostConditionsV4.newBuilder()
            .addContentTypeCondition(PostPolicyV4.ConditionV4Type.MATCHES, "image/jpeg")
            .build();

    // test fields and conditions
    PostPolicyV4 policy =
        storage.generateSignedPostPolicyV4(
            BlobInfo.newBuilder("my-bucket", "my-object").build(),
            7,
            TimeUnit.DAYS,
            fields,
            conditions);

    Map<String, String> outputFields = policy.getFields();

    assertTrue(outputFields.containsKey("x-goog-date"));
    assertTrue(outputFields.containsKey("x-goog-credential"));
    assertTrue(outputFields.containsKey("x-goog-signature"));
    assertEquals(outputFields.get("x-goog-algorithm"), "GOOG4-RSA-SHA256");
    assertEquals(outputFields.get("content-type"), "image/jpeg");
    assertEquals(outputFields.get("acl"), "public-read");
    assertEquals(outputFields.get("key"), "my-object");
    assertEquals("https://storage.googleapis.com/my-bucket/", policy.getUrl());

    // test fields, no conditions
    policy =
        storage.generateSignedPostPolicyV4(
            BlobInfo.newBuilder("my-bucket", "my-object").build(), 7, TimeUnit.DAYS, conditions);
    outputFields = policy.getFields();

    assertTrue(outputFields.containsKey("x-goog-date"));
    assertTrue(outputFields.containsKey("x-goog-credential"));
    assertTrue(outputFields.containsKey("x-goog-signature"));
    assertEquals(outputFields.get("x-goog-algorithm"), "GOOG4-RSA-SHA256");
    assertEquals(outputFields.get("content-type"), "image/jpeg");
    assertEquals(outputFields.get("key"), "my-object");
    assertEquals("https://storage.googleapis.com/my-bucket/", policy.getUrl());

    // test conditions, no fields
    policy =
        storage.generateSignedPostPolicyV4(
            BlobInfo.newBuilder("my-bucket", "my-object").build(), 7, TimeUnit.DAYS, fields);
    outputFields = policy.getFields();
    assertTrue(outputFields.containsKey("x-goog-date"));
    assertTrue(outputFields.containsKey("x-goog-credential"));
    assertTrue(outputFields.containsKey("x-goog-signature"));
    assertEquals(outputFields.get("x-goog-algorithm"), "GOOG4-RSA-SHA256");
    assertEquals(outputFields.get("acl"), "public-read");
    assertEquals(outputFields.get("key"), "my-object");

    // test no conditions no fields
    policy =
        storage.generateSignedPostPolicyV4(
            BlobInfo.newBuilder("my-bucket", "my-object").build(), 7, TimeUnit.DAYS);
    outputFields = policy.getFields();
    assertTrue(outputFields.containsKey("x-goog-date"));
    assertTrue(outputFields.containsKey("x-goog-credential"));
    assertTrue(outputFields.containsKey("x-goog-signature"));
    assertEquals(outputFields.get("x-goog-algorithm"), "GOOG4-RSA-SHA256");
    assertEquals(outputFields.get("key"), "my-object");
    assertEquals("https://storage.googleapis.com/my-bucket/", policy.getUrl());
  }

  @Test
  public void testBucketLifecycleRules() {
    BucketInfo bucketInfo =
        BucketInfo.newBuilder("b")
            .setLocation("us")
            .setLifecycleRules(
                ImmutableList.of(
                    new BucketInfo.LifecycleRule(
                        BucketInfo.LifecycleRule.LifecycleAction.newSetStorageClassAction(
                            StorageClass.COLDLINE),
                        BucketInfo.LifecycleRule.LifecycleCondition.newBuilder()
                            .setAge(1)
                            .setNumberOfNewerVersions(3)
                            .setIsLive(false)
                            .setCreatedBefore(new DateTime(System.currentTimeMillis()))
                            .setMatchesStorageClass(ImmutableList.of(StorageClass.COLDLINE))
                            .setDaysSinceNoncurrentTime(30)
                            .setNoncurrentTimeBefore(new DateTime(System.currentTimeMillis()))
                            .setCustomTimeBefore(new DateTime(System.currentTimeMillis()))
                            .setDaysSinceCustomTime(30)
                            .build())))
            .build();
    doReturn(bucketInfo.toPb())
        .doThrow(UNEXPECTED_CALL_EXCEPTION)
        .when(storageRpcMock)
        .create(bucketInfo.toPb(), new HashMap<StorageRpc.Option, Object>());
    initializeService();
    Bucket bucket = storage.create(bucketInfo);
    BucketInfo.LifecycleRule lifecycleRule = bucket.getLifecycleRules().get(0);
    assertEquals(3, lifecycleRule.getCondition().getNumberOfNewerVersions().intValue());
    assertNotNull(lifecycleRule.getCondition().getCreatedBefore());
    assertFalse(lifecycleRule.getCondition().getIsLive());
    assertEquals(1, lifecycleRule.getCondition().getAge().intValue());
    assertEquals(1, lifecycleRule.getCondition().getMatchesStorageClass().size());
    assertEquals(30, lifecycleRule.getCondition().getDaysSinceNoncurrentTime().intValue());
    assertNotNull(lifecycleRule.getCondition().getNoncurrentTimeBefore());
    assertEquals(30, lifecycleRule.getCondition().getDaysSinceCustomTime().intValue());
    assertNotNull(lifecycleRule.getCondition().getCustomTimeBefore());
  }
}
