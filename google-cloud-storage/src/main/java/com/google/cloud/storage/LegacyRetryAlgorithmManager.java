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
import com.google.cloud.BaseService;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteRequest;
import java.util.List;
import java.util.Map;

class LegacyRetryAlgorithmManager implements RetryAlgorithmManager {

  @Override
  public ExceptionHandler getForBucketAclCreate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclDelete(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclGet(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclUpdate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclList(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsCreate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsDelete(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsGet(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsUpdate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsList(Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsLockRetentionPolicy(
      Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsGetIamPolicy(
      String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsSetIamPolicy(
      String bucket, Policy pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsTestIamPermissions(
      String bucket, List<String> permissions, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclCreate(ObjectAccessControl pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclDelete(String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclGet(String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclUpdate(ObjectAccessControl pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclList(String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyCreate(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyDelete(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyGet(String accessId, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyUpdate(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyList(Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclCreate(ObjectAccessControl aclPb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclDelete(
      String bucket, String name, Long generation, String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclList(String bucket, String name, Long generation) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclGet(
      String bucket, String name, Long generation, String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclUpdate(ObjectAccessControl aclPb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsCreate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsDelete(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsGet(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsUpdate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsList(String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsRewrite(RewriteRequest pb) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsCompose(
      List<StorageObject> sources, StorageObject target, Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForResumableUploadSessionCreate(Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForResumableUploadSessionWrite(Map<StorageRpc.Option, ?> optionsMap) {
    return BaseService.EXCEPTION_HANDLER;
  }

  @Override
  public ExceptionHandler getForServiceAccountGet(String pb) {
    return BaseService.EXCEPTION_HANDLER;
  }
}
