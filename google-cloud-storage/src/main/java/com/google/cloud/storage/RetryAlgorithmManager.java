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

final class RetryAlgorithmManager implements Serializable {

  private final StorageExceptionHandlerFactory sehf;

  RetryAlgorithmManager(StorageExceptionHandlerFactory sehf) {
    this.sehf = sehf;
  }

  public ExceptionHandler getForBucketAclCreate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForBucketAclDelete(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForBucketAclGet(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketAclUpdate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForBucketAclList(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsCreate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsDelete(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsGet(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsUpdate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    // TODO: Include etag when it is supported by the library
    return optionsMap.containsKey(StorageRpc.Option.IF_METAGENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForBucketsList(Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsLockRetentionPolicy(
      Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    // Always idempotent because IfMetagenerationMatch is required
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsGetIamPolicy(
      String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForBucketsSetIamPolicy(
      String bucket, Policy pb, Map<StorageRpc.Option, ?> optionsMap) {
    return pb.getEtag() != null ? sehf.getIdempotentHandler() : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForBucketsTestIamPermissions(
      String bucket, List<String> permissions, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForDefaultObjectAclCreate(ObjectAccessControl pb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForDefaultObjectAclDelete(String pb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForDefaultObjectAclGet(String pb) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForDefaultObjectAclUpdate(ObjectAccessControl pb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForDefaultObjectAclList(String pb) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForHmacKeyCreate(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForHmacKeyDelete(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForHmacKeyGet(String accessId, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForHmacKeyUpdate(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    // TODO: Include etag when it is supported by the library
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForHmacKeyList(Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForObjectAclCreate(ObjectAccessControl aclPb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectAclDelete(
      String bucket, String name, Long generation, String pb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectAclList(String bucket, String name, Long generation) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForObjectAclGet(
      String bucket, String name, Long generation, String pb) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForObjectAclUpdate(ObjectAccessControl aclPb) {
    return sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectsCreate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    if (pb.getGeneration() != null && pb.getGeneration() == 0) {
      return sehf.getIdempotentHandler();
    }
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectsDelete(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectsGet(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForObjectsUpdate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_METAGENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectsList(String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return sehf.getIdempotentHandler();
  }

  public ExceptionHandler getForObjectsRewrite(RewriteRequest pb) {
    return pb.targetOptions.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForObjectsCompose(
      List<StorageObject> sources, StorageObject target, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForResumableUploadSessionCreate(Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForResumableUploadSessionWrite(Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? sehf.getIdempotentHandler()
        : sehf.getNonidempotentHandler();
  }

  public ExceptionHandler getForServiceAccountGet(String pb) {
    return sehf.getIdempotentHandler();
  }
}
