/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.control.v2;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.StorageOptions;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class ITFoldersTest {
  @Test
  public void createFolderTest() throws Exception {
    // TODO: Update this with manual client implmentation of HNS enabled when published.
    // TODO: Refactor to use Storage test infrastructure if extended.
    StorageOptions option = StorageOptions.http().build();
    HttpTransportOptions httpTransportOptions = (HttpTransportOptions) option.getTransportOptions();
    HttpTransport transport = httpTransportOptions.getHttpTransportFactory().create();
    HttpRequestInitializer initializer = httpTransportOptions.getHttpRequestInitializer(option);
    Storage storage =
        new Storage.Builder(transport, new JacksonFactory(), initializer)
            .setApplicationName("test")
            .build();
    String bucketName = "hns-b-gcs-grpc-team-test-" + UUID.randomUUID();
    Bucket createdBucket = null;
    try {
      Bucket bucketConfig =
          new Bucket()
              .setName(bucketName)
              .setHierarchicalNamespace(new Bucket.HierarchicalNamespace().setEnabled(true))
              .setIamConfiguration(
                  new Bucket.IamConfiguration()
                      .setUniformBucketLevelAccess(
                          new Bucket.IamConfiguration.UniformBucketLevelAccess().setEnabled(true)));
      createdBucket = storage.buckets().insert(option.getProjectId(), bucketConfig).execute();
      StorageControlClient storageControlClient = StorageControlClient.create();
      String folderId = "foldername/";
      Folder folder =
          storageControlClient.createFolder(
              CreateFolderRequest.newBuilder()
                  .setParent(BucketName.format("_", bucketName))
                  .setFolderId(folderId)
                  .build());
      Assert.assertEquals(folder.getName(), FolderName.format("_", bucketName, folderId));
    } finally {
      if (createdBucket != null) {
        storage.buckets().delete(bucketName);
      }
    }
  }
}
