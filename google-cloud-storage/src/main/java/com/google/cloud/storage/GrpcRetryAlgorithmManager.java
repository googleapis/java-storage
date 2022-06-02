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

import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.storage.v2.ComposeObjectRequest;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.CreateHmacKeyRequest;
import com.google.storage.v2.CreateNotificationRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.DeleteNotificationRequest;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetHmacKeyRequest;
import com.google.storage.v2.GetNotificationRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListNotificationsRequest;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.LockBucketRetentionPolicyRequest;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.RewriteObjectRequest;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.UpdateBucketRequest;
import com.google.storage.v2.UpdateHmacKeyRequest;
import com.google.storage.v2.UpdateObjectRequest;
import com.google.storage.v2.WriteObjectRequest;
import java.io.Serializable;

final class GrpcRetryAlgorithmManager implements Serializable {

  private static final long serialVersionUID = -355073454247905645L;
  private final StorageRetryStrategy retryStrategy;

  GrpcRetryAlgorithmManager(StorageRetryStrategy retryStrategy) {
    this.retryStrategy = retryStrategy;
  }

  public ResultRetryAlgorithm<?> getFor(ComposeObjectRequest req) {
    return req.hasIfGenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(CreateBucketRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(CreateHmacKeyRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(CreateNotificationRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(DeleteBucketRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(DeleteHmacKeyRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(DeleteNotificationRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(DeleteObjectRequest req) {
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetBucketRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetHmacKeyRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetIamPolicyRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetNotificationRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetObjectRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(GetServiceAccountRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(ListBucketsRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(ListHmacKeysRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(ListNotificationsRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(ListObjectsRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(LockBucketRetentionPolicyRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(QueryWriteStatusRequest req) {
    // unique upload Id, always idempotent
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(ReadObjectRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(RewriteObjectRequest req) {
    return req.hasIfGenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(SetIamPolicyRequest req) {
    // TODO: etag
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(StartResumableWriteRequest req) {
    return req.getWriteObjectSpec().hasIfGenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(TestIamPermissionsRequest req) {
    return retryStrategy.getIdempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(UpdateBucketRequest req) {
    // TODO: account for acl "patch"
    // TODO: etag
    return req.hasIfMetagenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(UpdateHmacKeyRequest req) {
    // TODO: etag
    return retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(UpdateObjectRequest req) {
    // TODO: account for acl "patch"
    return req.hasIfMetagenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }

  public ResultRetryAlgorithm<?> getFor(WriteObjectRequest req) {
    return req.getWriteObjectSpec().hasIfGenerationMatch()
        ? retryStrategy.getIdempotentHandler()
        : retryStrategy.getNonidempotentHandler();
  }
}
