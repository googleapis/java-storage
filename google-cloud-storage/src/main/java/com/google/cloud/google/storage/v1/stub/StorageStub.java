/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.google.storage.v1.stub;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.Empty;
import com.google.storage.v1.Bucket;
import com.google.storage.v1.BucketAccessControl;
import com.google.storage.v1.Channel;
import com.google.storage.v1.ComposeObjectRequest;
import com.google.storage.v1.CopyObjectRequest;
import com.google.storage.v1.CreateHmacKeyRequest;
import com.google.storage.v1.CreateHmacKeyResponse;
import com.google.storage.v1.DeleteBucketAccessControlRequest;
import com.google.storage.v1.DeleteBucketRequest;
import com.google.storage.v1.DeleteDefaultObjectAccessControlRequest;
import com.google.storage.v1.DeleteHmacKeyRequest;
import com.google.storage.v1.DeleteNotificationRequest;
import com.google.storage.v1.DeleteObjectAccessControlRequest;
import com.google.storage.v1.DeleteObjectRequest;
import com.google.storage.v1.GetBucketAccessControlRequest;
import com.google.storage.v1.GetBucketRequest;
import com.google.storage.v1.GetDefaultObjectAccessControlRequest;
import com.google.storage.v1.GetHmacKeyRequest;
import com.google.storage.v1.GetIamPolicyRequest;
import com.google.storage.v1.GetNotificationRequest;
import com.google.storage.v1.GetObjectAccessControlRequest;
import com.google.storage.v1.GetObjectMediaRequest;
import com.google.storage.v1.GetObjectMediaResponse;
import com.google.storage.v1.GetObjectRequest;
import com.google.storage.v1.GetProjectServiceAccountRequest;
import com.google.storage.v1.HmacKeyMetadata;
import com.google.storage.v1.InsertBucketAccessControlRequest;
import com.google.storage.v1.InsertBucketRequest;
import com.google.storage.v1.InsertDefaultObjectAccessControlRequest;
import com.google.storage.v1.InsertNotificationRequest;
import com.google.storage.v1.InsertObjectAccessControlRequest;
import com.google.storage.v1.InsertObjectRequest;
import com.google.storage.v1.ListBucketAccessControlsRequest;
import com.google.storage.v1.ListBucketAccessControlsResponse;
import com.google.storage.v1.ListBucketsRequest;
import com.google.storage.v1.ListBucketsResponse;
import com.google.storage.v1.ListChannelsRequest;
import com.google.storage.v1.ListChannelsResponse;
import com.google.storage.v1.ListDefaultObjectAccessControlsRequest;
import com.google.storage.v1.ListHmacKeysRequest;
import com.google.storage.v1.ListHmacKeysResponse;
import com.google.storage.v1.ListNotificationsRequest;
import com.google.storage.v1.ListNotificationsResponse;
import com.google.storage.v1.ListObjectAccessControlsRequest;
import com.google.storage.v1.ListObjectAccessControlsResponse;
import com.google.storage.v1.ListObjectsRequest;
import com.google.storage.v1.ListObjectsResponse;
import com.google.storage.v1.LockRetentionPolicyRequest;
import com.google.storage.v1.Notification;
import com.google.storage.v1.ObjectAccessControl;
import com.google.storage.v1.PatchBucketAccessControlRequest;
import com.google.storage.v1.PatchBucketRequest;
import com.google.storage.v1.PatchDefaultObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectAccessControlRequest;
import com.google.storage.v1.PatchObjectRequest;
import com.google.storage.v1.QueryWriteStatusRequest;
import com.google.storage.v1.QueryWriteStatusResponse;
import com.google.storage.v1.RewriteObjectRequest;
import com.google.storage.v1.RewriteResponse;
import com.google.storage.v1.ServiceAccount;
import com.google.storage.v1.SetIamPolicyRequest;
import com.google.storage.v1.StartResumableWriteRequest;
import com.google.storage.v1.StartResumableWriteResponse;
import com.google.storage.v1.StopChannelRequest;
import com.google.storage.v1.TestIamPermissionsRequest;
import com.google.storage.v1.UpdateBucketAccessControlRequest;
import com.google.storage.v1.UpdateBucketRequest;
import com.google.storage.v1.UpdateDefaultObjectAccessControlRequest;
import com.google.storage.v1.UpdateHmacKeyRequest;
import com.google.storage.v1.UpdateObjectAccessControlRequest;
import com.google.storage.v1.UpdateObjectRequest;
import com.google.storage.v1.WatchAllObjectsRequest;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Base stub class for Cloud Storage API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator")
@BetaApi("A restructuring of stub classes is planned, so this may break in the future")
public abstract class StorageStub implements BackgroundResource {

  public UnaryCallable<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteBucketAccessControlCallable()");
  }

  public UnaryCallable<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: getBucketAccessControlCallable()");
  }

  public UnaryCallable<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: insertBucketAccessControlCallable()");
  }

  public UnaryCallable<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsCallable() {
    throw new UnsupportedOperationException("Not implemented: listBucketAccessControlsCallable()");
  }

  public UnaryCallable<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: updateBucketAccessControlCallable()");
  }

  public UnaryCallable<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: patchBucketAccessControlCallable()");
  }

  public UnaryCallable<DeleteBucketRequest, Empty> deleteBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteBucketCallable()");
  }

  public UnaryCallable<GetBucketRequest, Bucket> getBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: getBucketCallable()");
  }

  public UnaryCallable<InsertBucketRequest, Bucket> insertBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: insertBucketCallable()");
  }

  public UnaryCallable<ListChannelsRequest, ListChannelsResponse> listChannelsCallable() {
    throw new UnsupportedOperationException("Not implemented: listChannelsCallable()");
  }

  public UnaryCallable<ListBucketsRequest, ListBucketsResponse> listBucketsCallable() {
    throw new UnsupportedOperationException("Not implemented: listBucketsCallable()");
  }

  public UnaryCallable<LockRetentionPolicyRequest, Bucket> lockBucketRetentionPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: lockBucketRetentionPolicyCallable()");
  }

  public UnaryCallable<GetIamPolicyRequest, Policy> getBucketIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: getBucketIamPolicyCallable()");
  }

  public UnaryCallable<SetIamPolicyRequest, Policy> setBucketIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: setBucketIamPolicyCallable()");
  }

  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsCallable() {
    throw new UnsupportedOperationException("Not implemented: testBucketIamPermissionsCallable()");
  }

  public UnaryCallable<PatchBucketRequest, Bucket> patchBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: patchBucketCallable()");
  }

  public UnaryCallable<UpdateBucketRequest, Bucket> updateBucketCallable() {
    throw new UnsupportedOperationException("Not implemented: updateBucketCallable()");
  }

  public UnaryCallable<StopChannelRequest, Empty> stopChannelCallable() {
    throw new UnsupportedOperationException("Not implemented: stopChannelCallable()");
  }

  public UnaryCallable<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: deleteDefaultObjectAccessControlCallable()");
  }

  public UnaryCallable<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: getDefaultObjectAccessControlCallable()");
  }

  public UnaryCallable<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: insertDefaultObjectAccessControlCallable()");
  }

  public UnaryCallable<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: listDefaultObjectAccessControlsCallable()");
  }

  public UnaryCallable<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: patchDefaultObjectAccessControlCallable()");
  }

  public UnaryCallable<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: updateDefaultObjectAccessControlCallable()");
  }

  public UnaryCallable<DeleteNotificationRequest, Empty> deleteNotificationCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteNotificationCallable()");
  }

  public UnaryCallable<GetNotificationRequest, Notification> getNotificationCallable() {
    throw new UnsupportedOperationException("Not implemented: getNotificationCallable()");
  }

  public UnaryCallable<InsertNotificationRequest, Notification> insertNotificationCallable() {
    throw new UnsupportedOperationException("Not implemented: insertNotificationCallable()");
  }

  public UnaryCallable<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsCallable() {
    throw new UnsupportedOperationException("Not implemented: listNotificationsCallable()");
  }

  public UnaryCallable<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteObjectAccessControlCallable()");
  }

  public UnaryCallable<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: getObjectAccessControlCallable()");
  }

  public UnaryCallable<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: insertObjectAccessControlCallable()");
  }

  public UnaryCallable<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsCallable() {
    throw new UnsupportedOperationException("Not implemented: listObjectAccessControlsCallable()");
  }

  public UnaryCallable<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: patchObjectAccessControlCallable()");
  }

  public UnaryCallable<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlCallable() {
    throw new UnsupportedOperationException("Not implemented: updateObjectAccessControlCallable()");
  }

  public UnaryCallable<ComposeObjectRequest, com.google.storage.v1.Object> composeObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: composeObjectCallable()");
  }

  public UnaryCallable<CopyObjectRequest, com.google.storage.v1.Object> copyObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: copyObjectCallable()");
  }

  public UnaryCallable<DeleteObjectRequest, Empty> deleteObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteObjectCallable()");
  }

  public UnaryCallable<GetObjectRequest, com.google.storage.v1.Object> getObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: getObjectCallable()");
  }

  public ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaCallable() {
    throw new UnsupportedOperationException("Not implemented: getObjectMediaCallable()");
  }

  public ClientStreamingCallable<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: insertObjectCallable()");
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

  public UnaryCallable<PatchObjectRequest, com.google.storage.v1.Object> patchObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: patchObjectCallable()");
  }

  public UnaryCallable<UpdateObjectRequest, com.google.storage.v1.Object> updateObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: updateObjectCallable()");
  }

  public UnaryCallable<GetIamPolicyRequest, Policy> getObjectIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: getObjectIamPolicyCallable()");
  }

  public UnaryCallable<SetIamPolicyRequest, Policy> setObjectIamPolicyCallable() {
    throw new UnsupportedOperationException("Not implemented: setObjectIamPolicyCallable()");
  }

  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsCallable() {
    throw new UnsupportedOperationException("Not implemented: testObjectIamPermissionsCallable()");
  }

  public UnaryCallable<WatchAllObjectsRequest, Channel> watchAllObjectsCallable() {
    throw new UnsupportedOperationException("Not implemented: watchAllObjectsCallable()");
  }

  public UnaryCallable<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountCallable() {
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

  public UnaryCallable<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysCallable() {
    throw new UnsupportedOperationException("Not implemented: listHmacKeysCallable()");
  }

  public UnaryCallable<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyCallable() {
    throw new UnsupportedOperationException("Not implemented: updateHmacKeyCallable()");
  }

  @Override
  public abstract void close();
}
