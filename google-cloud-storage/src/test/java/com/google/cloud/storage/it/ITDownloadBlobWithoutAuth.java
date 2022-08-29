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
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketFixture;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import java.util.Iterator;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITDownloadBlobWithoutAuth {
  @ClassRule(order = 1)
  public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  @ClassRule(order = 2)
  public static final BucketFixture bucketFixture =
      BucketFixture.newBuilder().setHandle(storageFixture::getInstance).build();

  private static final boolean IS_VPC_TEST =
      System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC") != null
          && System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC").equalsIgnoreCase("true");

  private static Storage storage;
  private static String bucketName;

  @BeforeClass
  public static void setup() {
    storage = storageFixture.getInstance();
    bucketName = bucketFixture.getBucketInfo().getName();
  }

  @Test
  public void testDownloadPublicBlobWithoutAuthentication() {
    assumeFalse(IS_VPC_TEST);
    // create an unauthorized user
    Storage unauthorizedStorage = StorageOptions.getUnauthenticatedInstance().getService();

    // try to download blobs from a public bucket
    String landsatBucket = "gcp-public-data-landsat";
    String landsatPrefix = "LC08/01/001/002/LC08_L1GT_001002_20160817_20170322_01_T2/";
    String landsatBlob = landsatPrefix + "LC08_L1GT_001002_20160817_20170322_01_T2_ANG.txt";
    byte[] bytes = unauthorizedStorage.readAllBytes(landsatBucket, landsatBlob);

    assertThat(bytes.length).isEqualTo(117255);
    int numBlobs = 0;
    Iterator<Blob> blobIterator =
        unauthorizedStorage
            .list(landsatBucket, Storage.BlobListOption.prefix(landsatPrefix))
            .iterateAll()
            .iterator();
    while (blobIterator.hasNext()) {
      numBlobs++;
      blobIterator.next();
    }
    assertThat(numBlobs).isEqualTo(14);

    // try to download blobs from a bucket that requires authentication
    // authenticated client will succeed
    // unauthenticated client will receive an exception
    String sourceBlobName = "source-blob-name";
    BlobInfo sourceBlob = BlobInfo.newBuilder(bucketName, sourceBlobName).build();
    assertThat(storage.create(sourceBlob)).isNotNull();
    assertThat(storage.readAllBytes(bucketName, sourceBlobName)).isNotNull();
    try {
      unauthorizedStorage.readAllBytes(bucketName, sourceBlobName);
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    assertThat(storage.get(sourceBlob.getBlobId()).delete()).isTrue();

    // try to upload blobs to a bucket that requires authentication
    // authenticated client will succeed
    // unauthenticated client will receive an exception
    assertThat(storage.create(sourceBlob)).isNotNull();
    try {
      unauthorizedStorage.create(sourceBlob);
      fail("Expected StorageException");
    } catch (StorageException ex) {
      // expected
    }
    assertThat(storage.get(sourceBlob.getBlobId()).delete()).isTrue();
  }
}
