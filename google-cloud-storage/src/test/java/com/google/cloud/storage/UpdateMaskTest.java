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

package com.google.cloud.storage;

import static com.google.cloud.storage.Utils.bucketNameCodec;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo.CustomerEncryption;
import com.google.cloud.storage.Storage.BlobField;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.FieldMask;
import com.google.storage.v2.Object;
import com.google.storage.v2.StorageGrpc.StorageImplBase;
import com.google.storage.v2.UpdateObjectRequest;
import io.grpc.stub.StreamObserver;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public final class UpdateMaskTest {

  public static final class BlobInfoUpdateMask {

    @Test
    public void updateObjectRequest() throws Exception {
      Object expectedObject =
          Object.newBuilder()
              .setBucket(bucketNameCodec.encode("bucket"))
              .setName("obj-name")
              .putMetadata("x", "X")
              .build();
      UpdateObjectRequest expected =
          UpdateObjectRequest.newBuilder()
              .setObject(expectedObject)
              .setUpdateMask(FieldMask.newBuilder().addPaths("metadata").build())
              .build();

      AtomicReference<UpdateObjectRequest> actualRequest = new AtomicReference<>();
      StorageImplBase service =
          new StorageImplBase() {
            @Override
            public void updateObject(UpdateObjectRequest request, StreamObserver<Object> obs) {
              try {
                actualRequest.compareAndSet(null, request);
                obs.onNext(expectedObject);
                obs.onCompleted();
              } catch (Exception e) {
                obs.onError(e);
              }
            }
          };

      try (FakeServer fake = FakeServer.of(service);
          Storage s = fake.getGrpcStorageOptions().getService()) {
        BlobInfo base = base();
        s.update(base.toBuilder().setMetadata(ImmutableMap.of("x", "X")).build());
      }

      UpdateObjectRequest actual = actualRequest.get();
      assertThat(actual).isNotNull();
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void blobInfo_field_metadata() {
      testBlobField(b -> b.setMetadata(ImmutableMap.of("x", "X")), BlobField.METADATA);
    }

    @Test
    public void blobInfo_field_acl() {
      testBlobField(b -> b.setAcl(ImmutableList.of()), BlobField.ACL);
    }

    @Test
    public void blobInfo_field_cacheControl() {
      testBlobField(b -> b.setCacheControl("cc"), BlobField.CACHE_CONTROL);
    }

    @Test
    public void blobInfo_field_contentDisposition() {
      testBlobField(b -> b.setContentDisposition("cd"), BlobField.CONTENT_DISPOSITION);
    }

    @Test
    public void blobInfo_field_contentEncoding() {
      testBlobField(b -> b.setContentEncoding("ce"), BlobField.CONTENT_ENCODING);
    }

    @Test
    public void blobInfo_field_contentLanguage() {
      testBlobField(b -> b.setContentLanguage("cl"), BlobField.CONTENT_LANGUAGE);
    }

    @Test
    public void blobInfo_field_contentType() {
      testBlobField(b -> b.setContentType("ct"), BlobField.CONTENT_TYPE);
    }

    @Test
    public void blobInfo_field_crc32c() {
      testBlobField(b -> b.setCrc32c("c"), BlobField.CRC32C);
    }

    @Test
    public void blobInfo_field_crc32cFromHexString() {
      testBlobField(b -> b.setCrc32cFromHexString("145d34"), BlobField.CRC32C);
    }

    @Test
    public void blobInfo_field_etag() {
      testBlobField(b -> b.setEtag("e"), BlobField.ETAG);
    }

    @Test
    public void blobInfo_field_md5() {
      testBlobField(b -> b.setMd5("m"), BlobField.MD5HASH);
    }

    @Test
    public void blobInfo_field_md5FromHexString() {
      testBlobField(b -> b.setMd5FromHexString("145d34"), BlobField.MD5HASH);
    }

    @Test
    public void blobInfo_field_owner() {
      testBlobField(b -> b.setOwner(new User("x@y.z")), BlobField.OWNER);
    }

    @Test
    public void blobInfo_field_storageClass() {
      testBlobField(b -> b.setStorageClass(StorageClass.COLDLINE), BlobField.STORAGE_CLASS);
    }

    @Test
    public void blobInfo_field_timeDeleted() {
      testBlobField(b -> b.setDeleteTimeOffsetDateTime(OffsetDateTime.MAX), BlobField.TIME_DELETED);
    }

    @Test
    public void blobInfo_field_timeCreated() {
      testBlobField(b -> b.setCreateTimeOffsetDateTime(OffsetDateTime.MAX), BlobField.TIME_CREATED);
    }

    @Test
    public void blobInfo_field_kmsKeyName() {
      testBlobField(b -> b.setKmsKeyName("key"), BlobField.KMS_KEY_NAME);
    }

    @Test
    public void blobInfo_field_eventBasedHold() {
      testBlobField(b -> b.setEventBasedHold(true), BlobField.EVENT_BASED_HOLD);
    }

    @Test
    public void blobInfo_field_temporaryHold() {
      testBlobField(b -> b.setTemporaryHold(true), BlobField.TEMPORARY_HOLD);
    }

    @Test
    public void blobInfo_field_retentionExpirationTime() {
      testBlobField(
          b -> b.setRetentionExpirationTimeOffsetDateTime(OffsetDateTime.MAX),
          BlobField.RETENTION_EXPIRATION_TIME);
    }

    @Test
    public void blobInfo_field_updated() {
      testBlobField(b -> b.setUpdateTimeOffsetDateTime(OffsetDateTime.MAX), BlobField.UPDATED);
    }

    @Test
    public void blobInfo_field_customTime() {
      testBlobField(b -> b.setCustomTimeOffsetDateTime(OffsetDateTime.MAX), BlobField.CUSTOM_TIME);
    }

    @Test
    public void blobInfo_field_timeStorageClassUpdated() {
      testBlobField(
          b -> b.setTimeStorageClassUpdatedOffsetDateTime(OffsetDateTime.MAX),
          BlobField.TIME_STORAGE_CLASS_UPDATED);
    }

    @Test
    public void blobInfo_field_customerEncryption() {
      testBlobField(
          b -> b.setCustomerEncryption(new CustomerEncryption("alg", "sha")),
          BlobField.CUSTOMER_ENCRYPTION);
    }

    @Test
    public void blobInfo_field_blobId_changeBucketNameGeneration() {
      testBlobField(
          b -> b.setBlobId(BlobId.of("bucket2", "obj2", 3L)),
          BlobField.BUCKET,
          BlobField.NAME,
          BlobField.GENERATION);
    }

    @Test
    public void blobInfo_field_blobId_changeName() {
      testBlobField(b -> b.setBlobId(BlobId.of("bucket", "obj2")), BlobField.NAME);
    }

    @Test
    public void blobInfo_field_blobId_changeGeneration() {
      testBlobField(b -> b.setBlobId(BlobId.of("bucket", "obj-name", 3L)), BlobField.GENERATION);
    }

    private static void testBlobField(
        UnaryOperator<BlobInfo.Builder> f, BlobField... expectedModified) {
      BlobInfo actual1 = f.apply(base().toBuilder()).build();
      assertThat(actual1.getModifiedFields()).isEqualTo(ImmutableSet.copyOf(expectedModified));
      // verify that nothing is carried through from a previous state, and that setting the same
      // value does not mark it as modified.
      BlobInfo actual2 = f.apply(actual1.toBuilder()).build();
      assertThat(actual2.getModifiedFields()).isEqualTo(ImmutableSet.of());
    }

    private static BlobInfo base() {
      return BlobInfo.newBuilder("bucket", "obj-name").build();
    }
  }
}
