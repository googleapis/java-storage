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

package com.google.storage.v2.stub;

import static com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import static com.google.storage.v2.StorageClient.ListHmacKeysPagedResponse;
import static com.google.storage.v2.StorageClient.ListNotificationConfigsPagedResponse;
import static com.google.storage.v2.StorageClient.ListObjectsPagedResponse;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.Empty;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.BidiWriteObjectResponse;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.CancelResumableWriteRequest;
import com.google.storage.v2.CancelResumableWriteResponse;
import com.google.storage.v2.ComposeObjectRequest;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.CreateHmacKeyRequest;
import com.google.storage.v2.CreateHmacKeyResponse;
import com.google.storage.v2.CreateNotificationConfigRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.DeleteNotificationConfigRequest;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetHmacKeyRequest;
import com.google.storage.v2.GetNotificationConfigRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.HmacKeyMetadata;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListBucketsResponse;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListHmacKeysResponse;
import com.google.storage.v2.ListNotificationConfigsRequest;
import com.google.storage.v2.ListNotificationConfigsResponse;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.ListObjectsResponse;
import com.google.storage.v2.LockBucketRetentionPolicyRequest;
import com.google.storage.v2.NotificationConfig;
import com.google.storage.v2.Object;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.RestoreObjectRequest;
import com.google.storage.v2.RewriteObjectRequest;
import com.google.storage.v2.RewriteResponse;
import com.google.storage.v2.ServiceAccount;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.UpdateBucketRequest;
import com.google.storage.v2.UpdateHmacKeyRequest;
import com.google.storage.v2.UpdateObjectRequest;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the Storage service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class StorageStub implements BackgroundResource {

  public UnaryCallable<DeleteBucketRequest, Empty> deleteBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteBucketCallable()");
  }

  public UnaryCallable<GetBucketRequest, Bucket> getBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: getBucketCallable()");
  }

  public UnaryCallable<CreateBucketRequest, Bucket> createBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: createBucketCallable()");
  }

  public UnaryCallable<ListBucketsRequest, ListBucketsPagedResponse> listBucketsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listBucketsPagedCallable()");
  }

  public UnaryCallable<ListBucketsRequest, ListBucketsResponse> listBucketsCallable() {
    throw new UnsupportedOperationException("Not implemented: listBucketsCallable()");
  }

  public UnaryCallable<LockBucketRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: lockBucketRetentionPolicyCallable()");
  }

  public UnaryCallable<GetIamPolicyRequest, Policy> getIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: getIamPolicyCallable()");
  }

  public UnaryCallable<SetIamPolicyRequest, Policy> setIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: setIamPolicyCallable()");
  }

  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testIamPermissionsCallable() {
    throw new UnsupportedOperationException("Not implemented: testIamPermissionsCallable()");
  }

  public UnaryCallable<UpdateBucketRequest, Bucket> updateBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: updateBucketCallable()");
  }

  public UnaryCallable<DeleteNotificationConfigRequest, Empty> deleteNotificationConfigCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteNotificationConfigCallable()");
  }

  public UnaryCallable<GetNotificationConfigRequest, NotificationConfig>
      getNotificationConfigCallable() {
    throw new UnsupportedOperationException("Not implemented: getNotificationConfigCallable()");
  }

  public UnaryCallable<CreateNotificationConfigRequest, NotificationConfig>
      createNotificationConfigCallable() {
    throw new UnsupportedOperationException("Not implemented: createNotificationConfigCallable()");
  }

  public UnaryCallable<ListNotificationConfigsRequest, ListNotificationConfigsPagedResponse>
      listNotificationConfigsPagedCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: listNotificationConfigsPagedCallable()");
  }

  public UnaryCallable<ListNotificationConfigsRequest, ListNotificationConfigsResponse>
      listNotificationConfigsCallable() {
    throw new UnsupportedOperationException("Not implemented: listNotificationConfigsCallable()");
  }

  public UnaryCallable<ComposeObjectRequest, Object> composeObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: composeObjectCallable()");
  }

  public UnaryCallable<DeleteObjectRequest, Empty> deleteObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteObjectCallable()");
  }

  public UnaryCallable<RestoreObjectRequest, Object> restoreObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: restoreObjectCallable()");
  }

  public UnaryCallable<CancelResumableWriteRequest, CancelResumableWriteResponse>
      cancelResumableWriteCallable() {
    throw new UnsupportedOperationException("Not implemented: cancelResumableWriteCallable()");
  }

  public UnaryCallable<GetObjectRequest, Object> getObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: getObjectCallable()");
  }

  public ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> readObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: readObjectCallable()");
  }

  public UnaryCallable<UpdateObjectRequest, Object> updateObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: updateObjectCallable()");
  }

  public ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> writeObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: writeObjectCallable()");
  }

  public BidiStreamingCallable<BidiWriteObjectRequest, BidiWriteObjectResponse>
      bidiWriteObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: bidiWriteObjectCallable()");
  }

  public UnaryCallable<ListObjectsRequest, ListObjectsPagedResponse> listObjectsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listObjectsPagedCallable()");
  }

  public UnaryCallable<ListObjectsRequest, ListObjectsResponse> listObjectsCallable() {
    throw new UnsupportedOperationException("Not implemented: listObjectsCallable()");
  }

  public UnaryCallable<RewriteObjectRequest, RewriteResponse> rewriteObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: rewriteObjectCallable()");
  }

  public UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable() {
    throw new UnsupportedOperationException("Not implemented: startResumableWriteCallable()");
  }

  public UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable() {
    throw new UnsupportedOperationException("Not implemented: queryWriteStatusCallable()");
  }

  public UnaryCallable<GetServiceAccountRequest, ServiceAccount> getServiceAccountCallable() {
    throw new UnsupportedOperationException("Not implemented: getServiceAccountCallable()");
  }

  public UnaryCallable<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeyCallable() {
    throw new UnsupportedOperationException("Not implemented: createHmacKeyCallable()");
  }

  public UnaryCallable<DeleteHmacKeyRequest, Empty> deleteHmacKeyCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteHmacKeyCallable()");
  }

  public UnaryCallable<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeyCallable() {
    throw new UnsupportedOperationException("Not implemented: getHmacKeyCallable()");
  }

  public UnaryCallable<ListHmacKeysRequest, ListHmacKeysPagedResponse> listHmacKeysPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listHmacKeysPagedCallable()");
  }

  public UnaryCallable<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysCallable() {
    throw new UnsupportedOperationException("Not implemented: listHmacKeysCallable()");
  }

  public UnaryCallable<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyCallable() {
    throw new UnsupportedOperationException("Not implemented: updateHmacKeyCallable()");
  }

  @Override
  public abstract void close();
}
