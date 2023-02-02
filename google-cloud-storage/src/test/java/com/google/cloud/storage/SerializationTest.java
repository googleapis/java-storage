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

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.junit.Assert.assertEquals;

import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.BaseSerializationTest;
import com.google.cloud.NoCredentials;
import com.google.cloud.PageImpl;
import com.google.cloud.Restorable;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.BlobReadChannelV2.BlobReadChannelContext;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.UnifiedOpts.Opt;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SerializationTest extends BaseSerializationTest {

  private static final Acl.Domain ACL_DOMAIN = new Acl.Domain("domain");
  private static final Acl.Group ACL_GROUP = new Acl.Group("group");
  private static final Acl.Project ACL_PROJECT_ = new Acl.Project(ProjectRole.VIEWERS, "pid");
  private static final Acl.User ACL_USER = new Acl.User("user");
  private static final Acl.RawEntity ACL_RAW = new Acl.RawEntity("raw");
  private static final Acl ACL = Acl.of(ACL_DOMAIN, Acl.Role.OWNER);
  private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder("b", "n").build();
  private static final BucketInfo BUCKET_INFO = BucketInfo.of("b");
  private static final Cors.Origin ORIGIN = Cors.Origin.any();
  private static final Cors CORS =
      Cors.newBuilder().setMaxAgeSeconds(1).setOrigins(Collections.singleton(ORIGIN)).build();
  private static final StorageException STORAGE_EXCEPTION = new StorageException(42, "message");
  private static final Storage.BlobListOption BLOB_LIST_OPTIONS =
      Storage.BlobListOption.pageSize(100);
  private static final Storage.BlobSourceOption BLOB_SOURCE_OPTIONS =
      Storage.BlobSourceOption.generationMatch(1);
  private static final Storage.BlobTargetOption BLOB_TARGET_OPTIONS =
      Storage.BlobTargetOption.generationMatch();
  private static final Storage.BucketListOption BUCKET_LIST_OPTIONS =
      Storage.BucketListOption.prefix("bla");
  private static final Storage.BucketSourceOption BUCKET_SOURCE_OPTIONS =
      Storage.BucketSourceOption.metagenerationMatch(1);
  private static final Storage.BucketTargetOption BUCKET_TARGET_OPTIONS =
      Storage.BucketTargetOption.metagenerationNotMatch();
  private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

  private static Storage STORAGE;
  private static Blob BLOB;
  private static Bucket BUCKET;
  private static PageImpl<Blob> PAGE_RESULT;

  @BeforeClass
  public static void beforeClass() {
    StorageOptions storageOptions =
        StorageOptions.newBuilder()
            .setProjectId("p")
            .setCredentials(NoCredentials.getInstance())
            .build();
    STORAGE = storageOptions.getService();
    BLOB = BLOB_INFO.asBlob(STORAGE);
    BUCKET = BUCKET_INFO.asBucket(STORAGE);
    PAGE_RESULT = new PageImpl<>(null, "c", Collections.singletonList(BLOB));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    if (STORAGE != null) {
      STORAGE.close();
    }
  }

  @Override
  protected Serializable[] serializableObjects() {
    StorageOptions optionsDefault1 =
        StorageOptions.newBuilder()
            .setProjectId("p1")
            .setCredentials(NoCredentials.getInstance())
            .build();
    StorageOptions optionsDefault2 = optionsDefault1.toBuilder().setProjectId("p2").build();
    StorageOptions optionsHttp1 =
        StorageOptions.http()
            .setProjectId("http1")
            .setCredentials(NoCredentials.getInstance())
            .build();
    StorageOptions optionsHttp2 = optionsHttp1.toBuilder().setProjectId("http2").build();
    StorageOptions optionsGrpc1 =
        StorageOptions.grpc()
            .setProjectId("grpc1")
            .setCredentials(NoCredentials.getInstance())
            .build();
    StorageOptions optionsGrpc2 = optionsGrpc1.toBuilder().setProjectId("grpc2").build();

    // echo -n "key" | base64
    String keyBase64 = "a2V5";

    ImmutableList<Opt> serializableOpts =
        ImmutableList.<Opt>builder()
            .add(UnifiedOpts.crc32cMatch("crc32c"))
            .add(UnifiedOpts.currentDirectory())
            .add(UnifiedOpts.decryptionKey(keyBase64))
            .add(UnifiedOpts.delimiter("/"))
            .add(UnifiedOpts.detectContentType())
            .add(UnifiedOpts.disableGzipContent())
            .add(UnifiedOpts.doesNotExist())
            .add(UnifiedOpts.encryptionKey(keyBase64))
            .add(UnifiedOpts.endOffset("end"))
            .add(UnifiedOpts.fields(ImmutableSet.of(BucketField.LOCATION)))
            .add(UnifiedOpts.generationMatch(0))
            .add(UnifiedOpts.generationNotMatch(0))
            .add(UnifiedOpts.kmsKeyName("key"))
            .add(UnifiedOpts.md5Match("md5"))
            .add(UnifiedOpts.metagenerationMatch(1))
            .add(UnifiedOpts.metagenerationNotMatch(1))
            .add(UnifiedOpts.pageSize(3))
            .add(UnifiedOpts.pageToken("token"))
            .add(UnifiedOpts.predefinedAcl(PredefinedAcl.PRIVATE))
            .add(UnifiedOpts.predefinedDefaultObjectAcl(PredefinedAcl.PRIVATE))
            .add(UnifiedOpts.prefix("prefix"))
            .add(UnifiedOpts.projectId("proj"))
            .add(UnifiedOpts.projection("full"))
            .add(UnifiedOpts.requestedPolicyVersion(2))
            .add(UnifiedOpts.returnRawInputStream(false))
            .add(UnifiedOpts.serviceAccount(ServiceAccount.of("x@y.z")))
            .add(UnifiedOpts.setContentType("text/plain"))
            .add(UnifiedOpts.showDeletedKeys(false))
            .add(UnifiedOpts.startOffset("start"))
            .add(UnifiedOpts.userProject("user-proj"))
            .add(UnifiedOpts.versionsFilter(false))
            .add(UnifiedOpts.generationMatchExtractor())
            .add(UnifiedOpts.generationNotMatchExtractor())
            .add(UnifiedOpts.metagenerationMatchExtractor())
            .add(UnifiedOpts.metagenerationNotMatchExtractor())
            .add(UnifiedOpts.crc32cMatchExtractor())
            .add(UnifiedOpts.md5MatchExtractor())
            .build();

    return new Serializable[] {
      ACL_DOMAIN,
      ACL_GROUP,
      ACL_PROJECT_,
      ACL_USER,
      ACL_RAW,
      ACL,
      BLOB_INFO,
      BLOB,
      BUCKET_INFO,
      BUCKET,
      ORIGIN,
      CORS,
      PAGE_RESULT,
      BLOB_LIST_OPTIONS,
      BLOB_SOURCE_OPTIONS,
      BLOB_TARGET_OPTIONS,
      BUCKET_LIST_OPTIONS,
      BUCKET_SOURCE_OPTIONS,
      BUCKET_TARGET_OPTIONS,
      STORAGE_EXCEPTION,
      optionsDefault1,
      optionsDefault2,
      optionsHttp1,
      optionsHttp2,
      optionsGrpc1,
      optionsGrpc2,
      serializableOpts
    };
  }

  @Override
  @SuppressWarnings("resource")
  protected Restorable<?>[] restorableObjects() {
    HttpStorageOptions options = HttpStorageOptions.newBuilder().setProjectId("p2").build();
    ResultRetryAlgorithm<?> algorithm =
        options.getRetryAlgorithmManager().getForResumableUploadSessionWrite(EMPTY_RPC_OPTIONS);
    ReadChannel readerV2 =
        new BlobReadChannelV2(
            new StorageObject().setBucket("b").setName("n"),
            EMPTY_RPC_OPTIONS,
            BlobReadChannelContext.from(options));
    BlobWriteChannel writer =
        new BlobWriteChannel(
            options, BlobInfo.newBuilder(BlobId.of("b", "n")).build(), "upload-id", algorithm);
    return new Restorable<?>[] {readerV2, writer};
  }

  /**
   * Here we override the super classes implementation to remove the "assertNotSame".
   *
   * <p>We should not enforce that two instances are not the same. As long as they're equal and have
   * the same hashCode that should be sufficient.
   */
  @Test
  @Override
  public void testSerializableObjects() throws Exception {
    for (Serializable obj : firstNonNull(serializableObjects(), new Serializable[0])) {
      Object copy = serializeAndDeserialize(obj);
      assertEquals(obj, obj);
      assertEquals(obj, copy);
      assertEquals(obj.hashCode(), copy.hashCode());
      assertEquals(obj.toString(), copy.toString());
      assertEquals(copy, copy);
    }
  }
}
