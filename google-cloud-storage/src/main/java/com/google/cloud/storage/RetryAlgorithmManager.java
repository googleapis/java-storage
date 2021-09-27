/*
 * Copyright 2021 Google LLC
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

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.HmacKeyMetadata;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Policy;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

interface RetryAlgorithmManager extends Serializable {
  ExceptionHandler getForBucketAclCreate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketAclDelete(String pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketAclGet(String pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketAclUpdate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketAclList(String pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsCreate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsDelete(Bucket pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsGet(Bucket pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsUpdate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsList(Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsLockRetentionPolicy(
      Bucket pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsGetIamPolicy(String bucket, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsSetIamPolicy(
      String bucket, Policy pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForBucketsTestIamPermissions(
      String bucket, List<String> permissions, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForDefaultObjectAclCreate(ObjectAccessControl pb);

  ExceptionHandler getForDefaultObjectAclDelete(String pb);

  ExceptionHandler getForDefaultObjectAclGet(String pb);

  ExceptionHandler getForDefaultObjectAclUpdate(ObjectAccessControl pb);

  ExceptionHandler getForDefaultObjectAclList(String pb);

  ExceptionHandler getForHmacKeyCreate(String pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForHmacKeyDelete(HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForHmacKeyGet(String accessId, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForHmacKeyUpdate(HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForHmacKeyList(Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectAclCreate(ObjectAccessControl aclPb);

  ExceptionHandler getForObjectAclDelete(String bucket, String name, Long generation, String pb);

  ExceptionHandler getForObjectAclList(String bucket, String name, Long generation);

  ExceptionHandler getForObjectAclGet(String bucket, String name, Long generation, String pb);

  ExceptionHandler getForObjectAclUpdate(ObjectAccessControl aclPb);

  ExceptionHandler getForObjectsCreate(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectsDelete(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectsGet(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectsUpdate(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectsList(String bucket, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForObjectsRewrite(RewriteRequest pb);

  ExceptionHandler getForObjectsCompose(
      List<StorageObject> sources, StorageObject target, Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForResumableUploadSessionCreate(Map<StorageRpc.Option, ?> optionsMap);
  /** Resumable upload has differing 429 handling */
  ExceptionHandler getForResumableUploadSessionWrite(Map<StorageRpc.Option, ?> optionsMap);

  ExceptionHandler getForServiceAccountGet(String pb);
}
