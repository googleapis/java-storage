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
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
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
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * gRPC stub implementation for Cloud Storage API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator")
@BetaApi("A restructuring of stub classes is planned, so this may break in the future")
public class GrpcStorageStub extends StorageStub {

  private static final MethodDescriptor<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlMethodDescriptor =
          MethodDescriptor.<DeleteBucketAccessControlRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/DeleteBucketAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(DeleteBucketAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlMethodDescriptor =
          MethodDescriptor.<GetBucketAccessControlRequest, BucketAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetBucketAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetBucketAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(BucketAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlMethodDescriptor =
          MethodDescriptor.<InsertBucketAccessControlRequest, BucketAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/InsertBucketAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(InsertBucketAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(BucketAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<
          ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsMethodDescriptor =
          MethodDescriptor
              .<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListBucketAccessControls")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListBucketAccessControlsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListBucketAccessControlsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlMethodDescriptor =
          MethodDescriptor.<UpdateBucketAccessControlRequest, BucketAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/UpdateBucketAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(UpdateBucketAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(BucketAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlMethodDescriptor =
          MethodDescriptor.<PatchBucketAccessControlRequest, BucketAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/PatchBucketAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(PatchBucketAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(BucketAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<DeleteBucketRequest, Empty> deleteBucketMethodDescriptor =
      MethodDescriptor.<DeleteBucketRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/DeleteBucket")
          .setRequestMarshaller(ProtoUtils.marshaller(DeleteBucketRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<GetBucketRequest, Bucket> getBucketMethodDescriptor =
      MethodDescriptor.<GetBucketRequest, Bucket>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/GetBucket")
          .setRequestMarshaller(ProtoUtils.marshaller(GetBucketRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Bucket.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<InsertBucketRequest, Bucket> insertBucketMethodDescriptor =
      MethodDescriptor.<InsertBucketRequest, Bucket>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/InsertBucket")
          .setRequestMarshaller(ProtoUtils.marshaller(InsertBucketRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Bucket.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<ListChannelsRequest, ListChannelsResponse>
      listChannelsMethodDescriptor =
          MethodDescriptor.<ListChannelsRequest, ListChannelsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListChannels")
              .setRequestMarshaller(ProtoUtils.marshaller(ListChannelsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListChannelsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ListBucketsRequest, ListBucketsResponse>
      listBucketsMethodDescriptor =
          MethodDescriptor.<ListBucketsRequest, ListBucketsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListBuckets")
              .setRequestMarshaller(ProtoUtils.marshaller(ListBucketsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListBucketsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<LockRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicyMethodDescriptor =
          MethodDescriptor.<LockRetentionPolicyRequest, Bucket>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/LockBucketRetentionPolicy")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(LockRetentionPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Bucket.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetIamPolicyRequest, Policy>
      getBucketIamPolicyMethodDescriptor =
          MethodDescriptor.<GetIamPolicyRequest, Policy>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetBucketIamPolicy")
              .setRequestMarshaller(ProtoUtils.marshaller(GetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Policy.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<SetIamPolicyRequest, Policy>
      setBucketIamPolicyMethodDescriptor =
          MethodDescriptor.<SetIamPolicyRequest, Policy>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/SetBucketIamPolicy")
              .setRequestMarshaller(ProtoUtils.marshaller(SetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Policy.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsMethodDescriptor =
          MethodDescriptor.<TestIamPermissionsRequest, TestIamPermissionsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/TestBucketIamPermissions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(TestIamPermissionsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(TestIamPermissionsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<PatchBucketRequest, Bucket> patchBucketMethodDescriptor =
      MethodDescriptor.<PatchBucketRequest, Bucket>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/PatchBucket")
          .setRequestMarshaller(ProtoUtils.marshaller(PatchBucketRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Bucket.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<UpdateBucketRequest, Bucket> updateBucketMethodDescriptor =
      MethodDescriptor.<UpdateBucketRequest, Bucket>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/UpdateBucket")
          .setRequestMarshaller(ProtoUtils.marshaller(UpdateBucketRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Bucket.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<StopChannelRequest, Empty> stopChannelMethodDescriptor =
      MethodDescriptor.<StopChannelRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/StopChannel")
          .setRequestMarshaller(ProtoUtils.marshaller(StopChannelRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlMethodDescriptor =
          MethodDescriptor.<DeleteDefaultObjectAccessControlRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/DeleteDefaultObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(
                      DeleteDefaultObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlMethodDescriptor =
          MethodDescriptor.<GetDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetDefaultObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetDefaultObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<
          InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlMethodDescriptor =
          MethodDescriptor
              .<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/InsertDefaultObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(
                      InsertDefaultObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<
          ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsMethodDescriptor =
          MethodDescriptor
              .<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
                  newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListDefaultObjectAccessControls")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(
                      ListDefaultObjectAccessControlsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListObjectAccessControlsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlMethodDescriptor =
          MethodDescriptor.<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/PatchDefaultObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(
                      PatchDefaultObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<
          UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlMethodDescriptor =
          MethodDescriptor
              .<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/UpdateDefaultObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(
                      UpdateDefaultObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<DeleteNotificationRequest, Empty>
      deleteNotificationMethodDescriptor =
          MethodDescriptor.<DeleteNotificationRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/DeleteNotification")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(DeleteNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetNotificationRequest, Notification>
      getNotificationMethodDescriptor =
          MethodDescriptor.<GetNotificationRequest, Notification>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetNotification")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Notification.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<InsertNotificationRequest, Notification>
      insertNotificationMethodDescriptor =
          MethodDescriptor.<InsertNotificationRequest, Notification>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/InsertNotification")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(InsertNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Notification.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsMethodDescriptor =
          MethodDescriptor.<ListNotificationsRequest, ListNotificationsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListNotifications")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListNotificationsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlMethodDescriptor =
          MethodDescriptor.<DeleteObjectAccessControlRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/DeleteObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(DeleteObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlMethodDescriptor =
          MethodDescriptor.<GetObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlMethodDescriptor =
          MethodDescriptor.<InsertObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/InsertObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(InsertObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<
          ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsMethodDescriptor =
          MethodDescriptor
              .<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListObjectAccessControls")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListObjectAccessControlsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListObjectAccessControlsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlMethodDescriptor =
          MethodDescriptor.<PatchObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/PatchObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(PatchObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlMethodDescriptor =
          MethodDescriptor.<UpdateObjectAccessControlRequest, ObjectAccessControl>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/UpdateObjectAccessControl")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(UpdateObjectAccessControlRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ObjectAccessControl.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ComposeObjectRequest, com.google.storage.v1.Object>
      composeObjectMethodDescriptor =
          MethodDescriptor.<ComposeObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ComposeObject")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ComposeObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<CopyObjectRequest, com.google.storage.v1.Object>
      copyObjectMethodDescriptor =
          MethodDescriptor.<CopyObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/CopyObject")
              .setRequestMarshaller(ProtoUtils.marshaller(CopyObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<DeleteObjectRequest, Empty> deleteObjectMethodDescriptor =
      MethodDescriptor.<DeleteObjectRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/DeleteObject")
          .setRequestMarshaller(ProtoUtils.marshaller(DeleteObjectRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<GetObjectRequest, com.google.storage.v1.Object>
      getObjectMethodDescriptor =
          MethodDescriptor.<GetObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetObject")
              .setRequestMarshaller(ProtoUtils.marshaller(GetObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaMethodDescriptor =
          MethodDescriptor.<GetObjectMediaRequest, GetObjectMediaResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName("google.storage.v1.Storage/GetObjectMedia")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetObjectMediaRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(GetObjectMediaResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectMethodDescriptor =
          MethodDescriptor.<InsertObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName("google.storage.v1.Storage/InsertObject")
              .setRequestMarshaller(ProtoUtils.marshaller(InsertObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ListObjectsRequest, ListObjectsResponse>
      listObjectsMethodDescriptor =
          MethodDescriptor.<ListObjectsRequest, ListObjectsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListObjects")
              .setRequestMarshaller(ProtoUtils.marshaller(ListObjectsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListObjectsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<RewriteObjectRequest, RewriteResponse>
      rewriteObjectMethodDescriptor =
          MethodDescriptor.<RewriteObjectRequest, RewriteResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/RewriteObject")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(RewriteObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(RewriteResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteMethodDescriptor =
          MethodDescriptor.<StartResumableWriteRequest, StartResumableWriteResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/StartResumableWrite")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(StartResumableWriteRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(StartResumableWriteResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusMethodDescriptor =
          MethodDescriptor.<QueryWriteStatusRequest, QueryWriteStatusResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/QueryWriteStatus")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(QueryWriteStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(QueryWriteStatusResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<PatchObjectRequest, com.google.storage.v1.Object>
      patchObjectMethodDescriptor =
          MethodDescriptor.<PatchObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/PatchObject")
              .setRequestMarshaller(ProtoUtils.marshaller(PatchObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<UpdateObjectRequest, com.google.storage.v1.Object>
      updateObjectMethodDescriptor =
          MethodDescriptor.<UpdateObjectRequest, com.google.storage.v1.Object>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/UpdateObject")
              .setRequestMarshaller(ProtoUtils.marshaller(UpdateObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(com.google.storage.v1.Object.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetIamPolicyRequest, Policy>
      getObjectIamPolicyMethodDescriptor =
          MethodDescriptor.<GetIamPolicyRequest, Policy>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetObjectIamPolicy")
              .setRequestMarshaller(ProtoUtils.marshaller(GetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Policy.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<SetIamPolicyRequest, Policy>
      setObjectIamPolicyMethodDescriptor =
          MethodDescriptor.<SetIamPolicyRequest, Policy>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/SetObjectIamPolicy")
              .setRequestMarshaller(ProtoUtils.marshaller(SetIamPolicyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Policy.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsMethodDescriptor =
          MethodDescriptor.<TestIamPermissionsRequest, TestIamPermissionsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/TestObjectIamPermissions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(TestIamPermissionsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(TestIamPermissionsResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<WatchAllObjectsRequest, Channel>
      watchAllObjectsMethodDescriptor =
          MethodDescriptor.<WatchAllObjectsRequest, Channel>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/WatchAllObjects")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(WatchAllObjectsRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Channel.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountMethodDescriptor =
          MethodDescriptor.<GetProjectServiceAccountRequest, ServiceAccount>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetServiceAccount")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetProjectServiceAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(ServiceAccount.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<CreateHmacKeyRequest, CreateHmacKeyResponse>
      createHmacKeyMethodDescriptor =
          MethodDescriptor.<CreateHmacKeyRequest, CreateHmacKeyResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/CreateHmacKey")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(CreateHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(CreateHmacKeyResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<DeleteHmacKeyRequest, Empty> deleteHmacKeyMethodDescriptor =
      MethodDescriptor.<DeleteHmacKeyRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.v1.Storage/DeleteHmacKey")
          .setRequestMarshaller(ProtoUtils.marshaller(DeleteHmacKeyRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .build();
  private static final MethodDescriptor<GetHmacKeyRequest, HmacKeyMetadata>
      getHmacKeyMethodDescriptor =
          MethodDescriptor.<GetHmacKeyRequest, HmacKeyMetadata>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/GetHmacKey")
              .setRequestMarshaller(ProtoUtils.marshaller(GetHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(HmacKeyMetadata.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ListHmacKeysRequest, ListHmacKeysResponse>
      listHmacKeysMethodDescriptor =
          MethodDescriptor.<ListHmacKeysRequest, ListHmacKeysResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/ListHmacKeys")
              .setRequestMarshaller(ProtoUtils.marshaller(ListHmacKeysRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListHmacKeysResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<UpdateHmacKeyRequest, HmacKeyMetadata>
      updateHmacKeyMethodDescriptor =
          MethodDescriptor.<UpdateHmacKeyRequest, HmacKeyMetadata>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v1.Storage/UpdateHmacKey")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(UpdateHmacKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(HmacKeyMetadata.getDefaultInstance()))
              .build();

  private final BackgroundResource backgroundResources;

  private final UnaryCallable<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlCallable;
  private final UnaryCallable<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlCallable;
  private final UnaryCallable<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlCallable;
  private final UnaryCallable<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsCallable;
  private final UnaryCallable<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlCallable;
  private final UnaryCallable<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlCallable;
  private final UnaryCallable<DeleteBucketRequest, Empty> deleteBucketCallable;
  private final UnaryCallable<GetBucketRequest, Bucket> getBucketCallable;
  private final UnaryCallable<InsertBucketRequest, Bucket> insertBucketCallable;
  private final UnaryCallable<ListChannelsRequest, ListChannelsResponse> listChannelsCallable;
  private final UnaryCallable<ListBucketsRequest, ListBucketsResponse> listBucketsCallable;
  private final UnaryCallable<LockRetentionPolicyRequest, Bucket> lockBucketRetentionPolicyCallable;
  private final UnaryCallable<GetIamPolicyRequest, Policy> getBucketIamPolicyCallable;
  private final UnaryCallable<SetIamPolicyRequest, Policy> setBucketIamPolicyCallable;
  private final UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsCallable;
  private final UnaryCallable<PatchBucketRequest, Bucket> patchBucketCallable;
  private final UnaryCallable<UpdateBucketRequest, Bucket> updateBucketCallable;
  private final UnaryCallable<StopChannelRequest, Empty> stopChannelCallable;
  private final UnaryCallable<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlCallable;
  private final UnaryCallable<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlCallable;
  private final UnaryCallable<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlCallable;
  private final UnaryCallable<
          ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsCallable;
  private final UnaryCallable<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlCallable;
  private final UnaryCallable<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlCallable;
  private final UnaryCallable<DeleteNotificationRequest, Empty> deleteNotificationCallable;
  private final UnaryCallable<GetNotificationRequest, Notification> getNotificationCallable;
  private final UnaryCallable<InsertNotificationRequest, Notification> insertNotificationCallable;
  private final UnaryCallable<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsCallable;
  private final UnaryCallable<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlCallable;
  private final UnaryCallable<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlCallable;
  private final UnaryCallable<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlCallable;
  private final UnaryCallable<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsCallable;
  private final UnaryCallable<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlCallable;
  private final UnaryCallable<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlCallable;
  private final UnaryCallable<ComposeObjectRequest, com.google.storage.v1.Object>
      composeObjectCallable;
  private final UnaryCallable<CopyObjectRequest, com.google.storage.v1.Object> copyObjectCallable;
  private final UnaryCallable<DeleteObjectRequest, Empty> deleteObjectCallable;
  private final UnaryCallable<GetObjectRequest, com.google.storage.v1.Object> getObjectCallable;
  private final ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaCallable;
  private final ClientStreamingCallable<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectCallable;
  private final UnaryCallable<ListObjectsRequest, ListObjectsResponse> listObjectsCallable;
  private final UnaryCallable<RewriteObjectRequest, RewriteResponse> rewriteObjectCallable;
  private final UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable;
  private final UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable;
  private final UnaryCallable<PatchObjectRequest, com.google.storage.v1.Object> patchObjectCallable;
  private final UnaryCallable<UpdateObjectRequest, com.google.storage.v1.Object>
      updateObjectCallable;
  private final UnaryCallable<GetIamPolicyRequest, Policy> getObjectIamPolicyCallable;
  private final UnaryCallable<SetIamPolicyRequest, Policy> setObjectIamPolicyCallable;
  private final UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsCallable;
  private final UnaryCallable<WatchAllObjectsRequest, Channel> watchAllObjectsCallable;
  private final UnaryCallable<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountCallable;
  private final UnaryCallable<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeyCallable;
  private final UnaryCallable<DeleteHmacKeyRequest, Empty> deleteHmacKeyCallable;
  private final UnaryCallable<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeyCallable;
  private final UnaryCallable<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysCallable;
  private final UnaryCallable<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyCallable;

  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcStorageStub create(StorageStubSettings settings) throws IOException {
    return new GrpcStorageStub(settings, ClientContext.create(settings));
  }

  public static final GrpcStorageStub create(ClientContext clientContext) throws IOException {
    return new GrpcStorageStub(StorageStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcStorageStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcStorageStub(
        StorageStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcStorageStub, using the given settings. This is protected so that
   * it is easy to make a subclass, but otherwise, the static factory methods should be preferred.
   */
  protected GrpcStorageStub(StorageStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcStorageCallableFactory());
  }

  /**
   * Constructs an instance of GrpcStorageStub, using the given settings. This is protected so that
   * it is easy to make a subclass, but otherwise, the static factory methods should be preferred.
   */
  protected GrpcStorageStub(
      StorageStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    GrpcCallSettings<DeleteBucketAccessControlRequest, Empty>
        deleteBucketAccessControlTransportSettings =
            GrpcCallSettings.<DeleteBucketAccessControlRequest, Empty>newBuilder()
                .setMethodDescriptor(deleteBucketAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<GetBucketAccessControlRequest, BucketAccessControl>
        getBucketAccessControlTransportSettings =
            GrpcCallSettings.<GetBucketAccessControlRequest, BucketAccessControl>newBuilder()
                .setMethodDescriptor(getBucketAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<InsertBucketAccessControlRequest, BucketAccessControl>
        insertBucketAccessControlTransportSettings =
            GrpcCallSettings.<InsertBucketAccessControlRequest, BucketAccessControl>newBuilder()
                .setMethodDescriptor(insertBucketAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
        listBucketAccessControlsTransportSettings =
            GrpcCallSettings
                .<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>newBuilder()
                .setMethodDescriptor(listBucketAccessControlsMethodDescriptor)
                .build();
    GrpcCallSettings<UpdateBucketAccessControlRequest, BucketAccessControl>
        updateBucketAccessControlTransportSettings =
            GrpcCallSettings.<UpdateBucketAccessControlRequest, BucketAccessControl>newBuilder()
                .setMethodDescriptor(updateBucketAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<PatchBucketAccessControlRequest, BucketAccessControl>
        patchBucketAccessControlTransportSettings =
            GrpcCallSettings.<PatchBucketAccessControlRequest, BucketAccessControl>newBuilder()
                .setMethodDescriptor(patchBucketAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<DeleteBucketRequest, Empty> deleteBucketTransportSettings =
        GrpcCallSettings.<DeleteBucketRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteBucketMethodDescriptor)
            .build();
    GrpcCallSettings<GetBucketRequest, Bucket> getBucketTransportSettings =
        GrpcCallSettings.<GetBucketRequest, Bucket>newBuilder()
            .setMethodDescriptor(getBucketMethodDescriptor)
            .build();
    GrpcCallSettings<InsertBucketRequest, Bucket> insertBucketTransportSettings =
        GrpcCallSettings.<InsertBucketRequest, Bucket>newBuilder()
            .setMethodDescriptor(insertBucketMethodDescriptor)
            .build();
    GrpcCallSettings<ListChannelsRequest, ListChannelsResponse> listChannelsTransportSettings =
        GrpcCallSettings.<ListChannelsRequest, ListChannelsResponse>newBuilder()
            .setMethodDescriptor(listChannelsMethodDescriptor)
            .build();
    GrpcCallSettings<ListBucketsRequest, ListBucketsResponse> listBucketsTransportSettings =
        GrpcCallSettings.<ListBucketsRequest, ListBucketsResponse>newBuilder()
            .setMethodDescriptor(listBucketsMethodDescriptor)
            .build();
    GrpcCallSettings<LockRetentionPolicyRequest, Bucket>
        lockBucketRetentionPolicyTransportSettings =
            GrpcCallSettings.<LockRetentionPolicyRequest, Bucket>newBuilder()
                .setMethodDescriptor(lockBucketRetentionPolicyMethodDescriptor)
                .build();
    GrpcCallSettings<GetIamPolicyRequest, Policy> getBucketIamPolicyTransportSettings =
        GrpcCallSettings.<GetIamPolicyRequest, Policy>newBuilder()
            .setMethodDescriptor(getBucketIamPolicyMethodDescriptor)
            .build();
    GrpcCallSettings<SetIamPolicyRequest, Policy> setBucketIamPolicyTransportSettings =
        GrpcCallSettings.<SetIamPolicyRequest, Policy>newBuilder()
            .setMethodDescriptor(setBucketIamPolicyMethodDescriptor)
            .build();
    GrpcCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testBucketIamPermissionsTransportSettings =
            GrpcCallSettings.<TestIamPermissionsRequest, TestIamPermissionsResponse>newBuilder()
                .setMethodDescriptor(testBucketIamPermissionsMethodDescriptor)
                .build();
    GrpcCallSettings<PatchBucketRequest, Bucket> patchBucketTransportSettings =
        GrpcCallSettings.<PatchBucketRequest, Bucket>newBuilder()
            .setMethodDescriptor(patchBucketMethodDescriptor)
            .build();
    GrpcCallSettings<UpdateBucketRequest, Bucket> updateBucketTransportSettings =
        GrpcCallSettings.<UpdateBucketRequest, Bucket>newBuilder()
            .setMethodDescriptor(updateBucketMethodDescriptor)
            .build();
    GrpcCallSettings<StopChannelRequest, Empty> stopChannelTransportSettings =
        GrpcCallSettings.<StopChannelRequest, Empty>newBuilder()
            .setMethodDescriptor(stopChannelMethodDescriptor)
            .build();
    GrpcCallSettings<DeleteDefaultObjectAccessControlRequest, Empty>
        deleteDefaultObjectAccessControlTransportSettings =
            GrpcCallSettings.<DeleteDefaultObjectAccessControlRequest, Empty>newBuilder()
                .setMethodDescriptor(deleteDefaultObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
        getDefaultObjectAccessControlTransportSettings =
            GrpcCallSettings.<GetDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(getDefaultObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
        insertDefaultObjectAccessControlTransportSettings =
            GrpcCallSettings
                .<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(insertDefaultObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listDefaultObjectAccessControlsTransportSettings =
            GrpcCallSettings
                .<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
                    newBuilder()
                .setMethodDescriptor(listDefaultObjectAccessControlsMethodDescriptor)
                .build();
    GrpcCallSettings<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
        patchDefaultObjectAccessControlTransportSettings =
            GrpcCallSettings
                .<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(patchDefaultObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
        updateDefaultObjectAccessControlTransportSettings =
            GrpcCallSettings
                .<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(updateDefaultObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<DeleteNotificationRequest, Empty> deleteNotificationTransportSettings =
        GrpcCallSettings.<DeleteNotificationRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteNotificationMethodDescriptor)
            .build();
    GrpcCallSettings<GetNotificationRequest, Notification> getNotificationTransportSettings =
        GrpcCallSettings.<GetNotificationRequest, Notification>newBuilder()
            .setMethodDescriptor(getNotificationMethodDescriptor)
            .build();
    GrpcCallSettings<InsertNotificationRequest, Notification> insertNotificationTransportSettings =
        GrpcCallSettings.<InsertNotificationRequest, Notification>newBuilder()
            .setMethodDescriptor(insertNotificationMethodDescriptor)
            .build();
    GrpcCallSettings<ListNotificationsRequest, ListNotificationsResponse>
        listNotificationsTransportSettings =
            GrpcCallSettings.<ListNotificationsRequest, ListNotificationsResponse>newBuilder()
                .setMethodDescriptor(listNotificationsMethodDescriptor)
                .build();
    GrpcCallSettings<DeleteObjectAccessControlRequest, Empty>
        deleteObjectAccessControlTransportSettings =
            GrpcCallSettings.<DeleteObjectAccessControlRequest, Empty>newBuilder()
                .setMethodDescriptor(deleteObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<GetObjectAccessControlRequest, ObjectAccessControl>
        getObjectAccessControlTransportSettings =
            GrpcCallSettings.<GetObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(getObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<InsertObjectAccessControlRequest, ObjectAccessControl>
        insertObjectAccessControlTransportSettings =
            GrpcCallSettings.<InsertObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(insertObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
        listObjectAccessControlsTransportSettings =
            GrpcCallSettings
                .<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>newBuilder()
                .setMethodDescriptor(listObjectAccessControlsMethodDescriptor)
                .build();
    GrpcCallSettings<PatchObjectAccessControlRequest, ObjectAccessControl>
        patchObjectAccessControlTransportSettings =
            GrpcCallSettings.<PatchObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(patchObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<UpdateObjectAccessControlRequest, ObjectAccessControl>
        updateObjectAccessControlTransportSettings =
            GrpcCallSettings.<UpdateObjectAccessControlRequest, ObjectAccessControl>newBuilder()
                .setMethodDescriptor(updateObjectAccessControlMethodDescriptor)
                .build();
    GrpcCallSettings<ComposeObjectRequest, com.google.storage.v1.Object>
        composeObjectTransportSettings =
            GrpcCallSettings.<ComposeObjectRequest, com.google.storage.v1.Object>newBuilder()
                .setMethodDescriptor(composeObjectMethodDescriptor)
                .build();
    GrpcCallSettings<CopyObjectRequest, com.google.storage.v1.Object> copyObjectTransportSettings =
        GrpcCallSettings.<CopyObjectRequest, com.google.storage.v1.Object>newBuilder()
            .setMethodDescriptor(copyObjectMethodDescriptor)
            .build();
    GrpcCallSettings<DeleteObjectRequest, Empty> deleteObjectTransportSettings =
        GrpcCallSettings.<DeleteObjectRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteObjectMethodDescriptor)
            .build();
    GrpcCallSettings<GetObjectRequest, com.google.storage.v1.Object> getObjectTransportSettings =
        GrpcCallSettings.<GetObjectRequest, com.google.storage.v1.Object>newBuilder()
            .setMethodDescriptor(getObjectMethodDescriptor)
            .build();
    GrpcCallSettings<GetObjectMediaRequest, GetObjectMediaResponse>
        getObjectMediaTransportSettings =
            GrpcCallSettings.<GetObjectMediaRequest, GetObjectMediaResponse>newBuilder()
                .setMethodDescriptor(getObjectMediaMethodDescriptor)
                .build();
    GrpcCallSettings<InsertObjectRequest, com.google.storage.v1.Object>
        insertObjectTransportSettings =
            GrpcCallSettings.<InsertObjectRequest, com.google.storage.v1.Object>newBuilder()
                .setMethodDescriptor(insertObjectMethodDescriptor)
                .build();
    GrpcCallSettings<ListObjectsRequest, ListObjectsResponse> listObjectsTransportSettings =
        GrpcCallSettings.<ListObjectsRequest, ListObjectsResponse>newBuilder()
            .setMethodDescriptor(listObjectsMethodDescriptor)
            .build();
    GrpcCallSettings<RewriteObjectRequest, RewriteResponse> rewriteObjectTransportSettings =
        GrpcCallSettings.<RewriteObjectRequest, RewriteResponse>newBuilder()
            .setMethodDescriptor(rewriteObjectMethodDescriptor)
            .build();
    GrpcCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteTransportSettings =
            GrpcCallSettings.<StartResumableWriteRequest, StartResumableWriteResponse>newBuilder()
                .setMethodDescriptor(startResumableWriteMethodDescriptor)
                .build();
    GrpcCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusTransportSettings =
            GrpcCallSettings.<QueryWriteStatusRequest, QueryWriteStatusResponse>newBuilder()
                .setMethodDescriptor(queryWriteStatusMethodDescriptor)
                .build();
    GrpcCallSettings<PatchObjectRequest, com.google.storage.v1.Object>
        patchObjectTransportSettings =
            GrpcCallSettings.<PatchObjectRequest, com.google.storage.v1.Object>newBuilder()
                .setMethodDescriptor(patchObjectMethodDescriptor)
                .build();
    GrpcCallSettings<UpdateObjectRequest, com.google.storage.v1.Object>
        updateObjectTransportSettings =
            GrpcCallSettings.<UpdateObjectRequest, com.google.storage.v1.Object>newBuilder()
                .setMethodDescriptor(updateObjectMethodDescriptor)
                .build();
    GrpcCallSettings<GetIamPolicyRequest, Policy> getObjectIamPolicyTransportSettings =
        GrpcCallSettings.<GetIamPolicyRequest, Policy>newBuilder()
            .setMethodDescriptor(getObjectIamPolicyMethodDescriptor)
            .build();
    GrpcCallSettings<SetIamPolicyRequest, Policy> setObjectIamPolicyTransportSettings =
        GrpcCallSettings.<SetIamPolicyRequest, Policy>newBuilder()
            .setMethodDescriptor(setObjectIamPolicyMethodDescriptor)
            .build();
    GrpcCallSettings<TestIamPermissionsRequest, TestIamPermissionsResponse>
        testObjectIamPermissionsTransportSettings =
            GrpcCallSettings.<TestIamPermissionsRequest, TestIamPermissionsResponse>newBuilder()
                .setMethodDescriptor(testObjectIamPermissionsMethodDescriptor)
                .build();
    GrpcCallSettings<WatchAllObjectsRequest, Channel> watchAllObjectsTransportSettings =
        GrpcCallSettings.<WatchAllObjectsRequest, Channel>newBuilder()
            .setMethodDescriptor(watchAllObjectsMethodDescriptor)
            .build();
    GrpcCallSettings<GetProjectServiceAccountRequest, ServiceAccount>
        getServiceAccountTransportSettings =
            GrpcCallSettings.<GetProjectServiceAccountRequest, ServiceAccount>newBuilder()
                .setMethodDescriptor(getServiceAccountMethodDescriptor)
                .build();
    GrpcCallSettings<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeyTransportSettings =
        GrpcCallSettings.<CreateHmacKeyRequest, CreateHmacKeyResponse>newBuilder()
            .setMethodDescriptor(createHmacKeyMethodDescriptor)
            .build();
    GrpcCallSettings<DeleteHmacKeyRequest, Empty> deleteHmacKeyTransportSettings =
        GrpcCallSettings.<DeleteHmacKeyRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteHmacKeyMethodDescriptor)
            .build();
    GrpcCallSettings<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeyTransportSettings =
        GrpcCallSettings.<GetHmacKeyRequest, HmacKeyMetadata>newBuilder()
            .setMethodDescriptor(getHmacKeyMethodDescriptor)
            .build();
    GrpcCallSettings<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysTransportSettings =
        GrpcCallSettings.<ListHmacKeysRequest, ListHmacKeysResponse>newBuilder()
            .setMethodDescriptor(listHmacKeysMethodDescriptor)
            .build();
    GrpcCallSettings<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyTransportSettings =
        GrpcCallSettings.<UpdateHmacKeyRequest, HmacKeyMetadata>newBuilder()
            .setMethodDescriptor(updateHmacKeyMethodDescriptor)
            .build();

    this.deleteBucketAccessControlCallable =
        callableFactory.createUnaryCallable(
            deleteBucketAccessControlTransportSettings,
            settings.deleteBucketAccessControlSettings(),
            clientContext);
    this.getBucketAccessControlCallable =
        callableFactory.createUnaryCallable(
            getBucketAccessControlTransportSettings,
            settings.getBucketAccessControlSettings(),
            clientContext);
    this.insertBucketAccessControlCallable =
        callableFactory.createUnaryCallable(
            insertBucketAccessControlTransportSettings,
            settings.insertBucketAccessControlSettings(),
            clientContext);
    this.listBucketAccessControlsCallable =
        callableFactory.createUnaryCallable(
            listBucketAccessControlsTransportSettings,
            settings.listBucketAccessControlsSettings(),
            clientContext);
    this.updateBucketAccessControlCallable =
        callableFactory.createUnaryCallable(
            updateBucketAccessControlTransportSettings,
            settings.updateBucketAccessControlSettings(),
            clientContext);
    this.patchBucketAccessControlCallable =
        callableFactory.createUnaryCallable(
            patchBucketAccessControlTransportSettings,
            settings.patchBucketAccessControlSettings(),
            clientContext);
    this.deleteBucketCallable =
        callableFactory.createUnaryCallable(
            deleteBucketTransportSettings, settings.deleteBucketSettings(), clientContext);
    this.getBucketCallable =
        callableFactory.createUnaryCallable(
            getBucketTransportSettings, settings.getBucketSettings(), clientContext);
    this.insertBucketCallable =
        callableFactory.createUnaryCallable(
            insertBucketTransportSettings, settings.insertBucketSettings(), clientContext);
    this.listChannelsCallable =
        callableFactory.createUnaryCallable(
            listChannelsTransportSettings, settings.listChannelsSettings(), clientContext);
    this.listBucketsCallable =
        callableFactory.createUnaryCallable(
            listBucketsTransportSettings, settings.listBucketsSettings(), clientContext);
    this.lockBucketRetentionPolicyCallable =
        callableFactory.createUnaryCallable(
            lockBucketRetentionPolicyTransportSettings,
            settings.lockBucketRetentionPolicySettings(),
            clientContext);
    this.getBucketIamPolicyCallable =
        callableFactory.createUnaryCallable(
            getBucketIamPolicyTransportSettings,
            settings.getBucketIamPolicySettings(),
            clientContext);
    this.setBucketIamPolicyCallable =
        callableFactory.createUnaryCallable(
            setBucketIamPolicyTransportSettings,
            settings.setBucketIamPolicySettings(),
            clientContext);
    this.testBucketIamPermissionsCallable =
        callableFactory.createUnaryCallable(
            testBucketIamPermissionsTransportSettings,
            settings.testBucketIamPermissionsSettings(),
            clientContext);
    this.patchBucketCallable =
        callableFactory.createUnaryCallable(
            patchBucketTransportSettings, settings.patchBucketSettings(), clientContext);
    this.updateBucketCallable =
        callableFactory.createUnaryCallable(
            updateBucketTransportSettings, settings.updateBucketSettings(), clientContext);
    this.stopChannelCallable =
        callableFactory.createUnaryCallable(
            stopChannelTransportSettings, settings.stopChannelSettings(), clientContext);
    this.deleteDefaultObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            deleteDefaultObjectAccessControlTransportSettings,
            settings.deleteDefaultObjectAccessControlSettings(),
            clientContext);
    this.getDefaultObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            getDefaultObjectAccessControlTransportSettings,
            settings.getDefaultObjectAccessControlSettings(),
            clientContext);
    this.insertDefaultObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            insertDefaultObjectAccessControlTransportSettings,
            settings.insertDefaultObjectAccessControlSettings(),
            clientContext);
    this.listDefaultObjectAccessControlsCallable =
        callableFactory.createUnaryCallable(
            listDefaultObjectAccessControlsTransportSettings,
            settings.listDefaultObjectAccessControlsSettings(),
            clientContext);
    this.patchDefaultObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            patchDefaultObjectAccessControlTransportSettings,
            settings.patchDefaultObjectAccessControlSettings(),
            clientContext);
    this.updateDefaultObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            updateDefaultObjectAccessControlTransportSettings,
            settings.updateDefaultObjectAccessControlSettings(),
            clientContext);
    this.deleteNotificationCallable =
        callableFactory.createUnaryCallable(
            deleteNotificationTransportSettings,
            settings.deleteNotificationSettings(),
            clientContext);
    this.getNotificationCallable =
        callableFactory.createUnaryCallable(
            getNotificationTransportSettings, settings.getNotificationSettings(), clientContext);
    this.insertNotificationCallable =
        callableFactory.createUnaryCallable(
            insertNotificationTransportSettings,
            settings.insertNotificationSettings(),
            clientContext);
    this.listNotificationsCallable =
        callableFactory.createUnaryCallable(
            listNotificationsTransportSettings,
            settings.listNotificationsSettings(),
            clientContext);
    this.deleteObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            deleteObjectAccessControlTransportSettings,
            settings.deleteObjectAccessControlSettings(),
            clientContext);
    this.getObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            getObjectAccessControlTransportSettings,
            settings.getObjectAccessControlSettings(),
            clientContext);
    this.insertObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            insertObjectAccessControlTransportSettings,
            settings.insertObjectAccessControlSettings(),
            clientContext);
    this.listObjectAccessControlsCallable =
        callableFactory.createUnaryCallable(
            listObjectAccessControlsTransportSettings,
            settings.listObjectAccessControlsSettings(),
            clientContext);
    this.patchObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            patchObjectAccessControlTransportSettings,
            settings.patchObjectAccessControlSettings(),
            clientContext);
    this.updateObjectAccessControlCallable =
        callableFactory.createUnaryCallable(
            updateObjectAccessControlTransportSettings,
            settings.updateObjectAccessControlSettings(),
            clientContext);
    this.composeObjectCallable =
        callableFactory.createUnaryCallable(
            composeObjectTransportSettings, settings.composeObjectSettings(), clientContext);
    this.copyObjectCallable =
        callableFactory.createUnaryCallable(
            copyObjectTransportSettings, settings.copyObjectSettings(), clientContext);
    this.deleteObjectCallable =
        callableFactory.createUnaryCallable(
            deleteObjectTransportSettings, settings.deleteObjectSettings(), clientContext);
    this.getObjectCallable =
        callableFactory.createUnaryCallable(
            getObjectTransportSettings, settings.getObjectSettings(), clientContext);
    this.getObjectMediaCallable =
        callableFactory.createServerStreamingCallable(
            getObjectMediaTransportSettings, settings.getObjectMediaSettings(), clientContext);
    this.insertObjectCallable =
        callableFactory.createClientStreamingCallable(
            insertObjectTransportSettings, settings.insertObjectSettings(), clientContext);
    this.listObjectsCallable =
        callableFactory.createUnaryCallable(
            listObjectsTransportSettings, settings.listObjectsSettings(), clientContext);
    this.rewriteObjectCallable =
        callableFactory.createUnaryCallable(
            rewriteObjectTransportSettings, settings.rewriteObjectSettings(), clientContext);
    this.startResumableWriteCallable =
        callableFactory.createUnaryCallable(
            startResumableWriteTransportSettings,
            settings.startResumableWriteSettings(),
            clientContext);
    this.queryWriteStatusCallable =
        callableFactory.createUnaryCallable(
            queryWriteStatusTransportSettings, settings.queryWriteStatusSettings(), clientContext);
    this.patchObjectCallable =
        callableFactory.createUnaryCallable(
            patchObjectTransportSettings, settings.patchObjectSettings(), clientContext);
    this.updateObjectCallable =
        callableFactory.createUnaryCallable(
            updateObjectTransportSettings, settings.updateObjectSettings(), clientContext);
    this.getObjectIamPolicyCallable =
        callableFactory.createUnaryCallable(
            getObjectIamPolicyTransportSettings,
            settings.getObjectIamPolicySettings(),
            clientContext);
    this.setObjectIamPolicyCallable =
        callableFactory.createUnaryCallable(
            setObjectIamPolicyTransportSettings,
            settings.setObjectIamPolicySettings(),
            clientContext);
    this.testObjectIamPermissionsCallable =
        callableFactory.createUnaryCallable(
            testObjectIamPermissionsTransportSettings,
            settings.testObjectIamPermissionsSettings(),
            clientContext);
    this.watchAllObjectsCallable =
        callableFactory.createUnaryCallable(
            watchAllObjectsTransportSettings, settings.watchAllObjectsSettings(), clientContext);
    this.getServiceAccountCallable =
        callableFactory.createUnaryCallable(
            getServiceAccountTransportSettings,
            settings.getServiceAccountSettings(),
            clientContext);
    this.createHmacKeyCallable =
        callableFactory.createUnaryCallable(
            createHmacKeyTransportSettings, settings.createHmacKeySettings(), clientContext);
    this.deleteHmacKeyCallable =
        callableFactory.createUnaryCallable(
            deleteHmacKeyTransportSettings, settings.deleteHmacKeySettings(), clientContext);
    this.getHmacKeyCallable =
        callableFactory.createUnaryCallable(
            getHmacKeyTransportSettings, settings.getHmacKeySettings(), clientContext);
    this.listHmacKeysCallable =
        callableFactory.createUnaryCallable(
            listHmacKeysTransportSettings, settings.listHmacKeysSettings(), clientContext);
    this.updateHmacKeyCallable =
        callableFactory.createUnaryCallable(
            updateHmacKeyTransportSettings, settings.updateHmacKeySettings(), clientContext);

    backgroundResources = new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public UnaryCallable<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlCallable() {
    return deleteBucketAccessControlCallable;
  }

  public UnaryCallable<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlCallable() {
    return getBucketAccessControlCallable;
  }

  public UnaryCallable<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlCallable() {
    return insertBucketAccessControlCallable;
  }

  public UnaryCallable<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsCallable() {
    return listBucketAccessControlsCallable;
  }

  public UnaryCallable<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlCallable() {
    return updateBucketAccessControlCallable;
  }

  public UnaryCallable<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlCallable() {
    return patchBucketAccessControlCallable;
  }

  public UnaryCallable<DeleteBucketRequest, Empty> deleteBucketCallable() {
    return deleteBucketCallable;
  }

  public UnaryCallable<GetBucketRequest, Bucket> getBucketCallable() {
    return getBucketCallable;
  }

  public UnaryCallable<InsertBucketRequest, Bucket> insertBucketCallable() {
    return insertBucketCallable;
  }

  public UnaryCallable<ListChannelsRequest, ListChannelsResponse> listChannelsCallable() {
    return listChannelsCallable;
  }

  public UnaryCallable<ListBucketsRequest, ListBucketsResponse> listBucketsCallable() {
    return listBucketsCallable;
  }

  public UnaryCallable<LockRetentionPolicyRequest, Bucket> lockBucketRetentionPolicyCallable() {
    return lockBucketRetentionPolicyCallable;
  }

  public UnaryCallable<GetIamPolicyRequest, Policy> getBucketIamPolicyCallable() {
    return getBucketIamPolicyCallable;
  }

  public UnaryCallable<SetIamPolicyRequest, Policy> setBucketIamPolicyCallable() {
    return setBucketIamPolicyCallable;
  }

  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsCallable() {
    return testBucketIamPermissionsCallable;
  }

  public UnaryCallable<PatchBucketRequest, Bucket> patchBucketCallable() {
    return patchBucketCallable;
  }

  public UnaryCallable<UpdateBucketRequest, Bucket> updateBucketCallable() {
    return updateBucketCallable;
  }

  public UnaryCallable<StopChannelRequest, Empty> stopChannelCallable() {
    return stopChannelCallable;
  }

  public UnaryCallable<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlCallable() {
    return deleteDefaultObjectAccessControlCallable;
  }

  public UnaryCallable<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlCallable() {
    return getDefaultObjectAccessControlCallable;
  }

  public UnaryCallable<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlCallable() {
    return insertDefaultObjectAccessControlCallable;
  }

  public UnaryCallable<ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsCallable() {
    return listDefaultObjectAccessControlsCallable;
  }

  public UnaryCallable<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlCallable() {
    return patchDefaultObjectAccessControlCallable;
  }

  public UnaryCallable<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlCallable() {
    return updateDefaultObjectAccessControlCallable;
  }

  public UnaryCallable<DeleteNotificationRequest, Empty> deleteNotificationCallable() {
    return deleteNotificationCallable;
  }

  public UnaryCallable<GetNotificationRequest, Notification> getNotificationCallable() {
    return getNotificationCallable;
  }

  public UnaryCallable<InsertNotificationRequest, Notification> insertNotificationCallable() {
    return insertNotificationCallable;
  }

  public UnaryCallable<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsCallable() {
    return listNotificationsCallable;
  }

  public UnaryCallable<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlCallable() {
    return deleteObjectAccessControlCallable;
  }

  public UnaryCallable<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlCallable() {
    return getObjectAccessControlCallable;
  }

  public UnaryCallable<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlCallable() {
    return insertObjectAccessControlCallable;
  }

  public UnaryCallable<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsCallable() {
    return listObjectAccessControlsCallable;
  }

  public UnaryCallable<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlCallable() {
    return patchObjectAccessControlCallable;
  }

  public UnaryCallable<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlCallable() {
    return updateObjectAccessControlCallable;
  }

  public UnaryCallable<ComposeObjectRequest, com.google.storage.v1.Object> composeObjectCallable() {
    return composeObjectCallable;
  }

  public UnaryCallable<CopyObjectRequest, com.google.storage.v1.Object> copyObjectCallable() {
    return copyObjectCallable;
  }

  public UnaryCallable<DeleteObjectRequest, Empty> deleteObjectCallable() {
    return deleteObjectCallable;
  }

  public UnaryCallable<GetObjectRequest, com.google.storage.v1.Object> getObjectCallable() {
    return getObjectCallable;
  }

  public ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaCallable() {
    return getObjectMediaCallable;
  }

  public ClientStreamingCallable<InsertObjectRequest, com.google.storage.v1.Object>
      insertObjectCallable() {
    return insertObjectCallable;
  }

  public UnaryCallable<ListObjectsRequest, ListObjectsResponse> listObjectsCallable() {
    return listObjectsCallable;
  }

  public UnaryCallable<RewriteObjectRequest, RewriteResponse> rewriteObjectCallable() {
    return rewriteObjectCallable;
  }

  public UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable() {
    return startResumableWriteCallable;
  }

  public UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable() {
    return queryWriteStatusCallable;
  }

  public UnaryCallable<PatchObjectRequest, com.google.storage.v1.Object> patchObjectCallable() {
    return patchObjectCallable;
  }

  public UnaryCallable<UpdateObjectRequest, com.google.storage.v1.Object> updateObjectCallable() {
    return updateObjectCallable;
  }

  public UnaryCallable<GetIamPolicyRequest, Policy> getObjectIamPolicyCallable() {
    return getObjectIamPolicyCallable;
  }

  public UnaryCallable<SetIamPolicyRequest, Policy> setObjectIamPolicyCallable() {
    return setObjectIamPolicyCallable;
  }

  public UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsCallable() {
    return testObjectIamPermissionsCallable;
  }

  public UnaryCallable<WatchAllObjectsRequest, Channel> watchAllObjectsCallable() {
    return watchAllObjectsCallable;
  }

  public UnaryCallable<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountCallable() {
    return getServiceAccountCallable;
  }

  public UnaryCallable<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeyCallable() {
    return createHmacKeyCallable;
  }

  public UnaryCallable<DeleteHmacKeyRequest, Empty> deleteHmacKeyCallable() {
    return deleteHmacKeyCallable;
  }

  public UnaryCallable<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeyCallable() {
    return getHmacKeyCallable;
  }

  public UnaryCallable<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysCallable() {
    return listHmacKeysCallable;
  }

  public UnaryCallable<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyCallable() {
    return updateHmacKeyCallable;
  }

  @Override
  public final void close() {
    shutdown();
  }

  @Override
  public void shutdown() {
    backgroundResources.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return backgroundResources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return backgroundResources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    backgroundResources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }
}
