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
package com.google.cloud.google.storage.v1;

import com.google.api.core.BetaApi;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.AbstractMessage;
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
import com.google.storage.v1.StorageGrpc.StorageImplBase;
import com.google.storage.v1.TestIamPermissionsRequest;
import com.google.storage.v1.UpdateBucketAccessControlRequest;
import com.google.storage.v1.UpdateBucketRequest;
import com.google.storage.v1.UpdateDefaultObjectAccessControlRequest;
import com.google.storage.v1.UpdateHmacKeyRequest;
import com.google.storage.v1.UpdateObjectAccessControlRequest;
import com.google.storage.v1.UpdateObjectRequest;
import com.google.storage.v1.WatchAllObjectsRequest;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@javax.annotation.Generated("by GAPIC")
@BetaApi
public class MockStorageImpl extends StorageImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockStorageImpl() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  public List<AbstractMessage> getRequests() {
    return requests;
  }

  public void addResponse(AbstractMessage response) {
    responses.add(response);
  }

  public void setResponses(List<AbstractMessage> responses) {
    this.responses = new LinkedList<Object>(responses);
  }

  public void addException(Exception exception) {
    responses.add(exception);
  }

  public void reset() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  @Override
  public void deleteBucketAccessControl(
      DeleteBucketAccessControlRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getBucketAccessControl(
      GetBucketAccessControlRequest request, StreamObserver<BucketAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext((BucketAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void insertBucketAccessControl(
      InsertBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext((BucketAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listBucketAccessControls(
      ListBucketAccessControlsRequest request,
      StreamObserver<ListBucketAccessControlsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListBucketAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext((ListBucketAccessControlsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateBucketAccessControl(
      UpdateBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext((BucketAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void patchBucketAccessControl(
      PatchBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext((BucketAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteBucket(DeleteBucketRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getBucket(GetBucketRequest request, StreamObserver<Bucket> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext((Bucket) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void insertBucket(InsertBucketRequest request, StreamObserver<Bucket> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext((Bucket) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listChannels(
      ListChannelsRequest request, StreamObserver<ListChannelsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListChannelsResponse) {
      requests.add(request);
      responseObserver.onNext((ListChannelsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listBuckets(
      ListBucketsRequest request, StreamObserver<ListBucketsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListBucketsResponse) {
      requests.add(request);
      responseObserver.onNext((ListBucketsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void lockBucketRetentionPolicy(
      LockRetentionPolicyRequest request, StreamObserver<Bucket> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext((Bucket) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getBucketIamPolicy(
      GetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext((Policy) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void setBucketIamPolicy(
      SetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext((Policy) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void testBucketIamPermissions(
      TestIamPermissionsRequest request,
      StreamObserver<TestIamPermissionsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof TestIamPermissionsResponse) {
      requests.add(request);
      responseObserver.onNext((TestIamPermissionsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void patchBucket(PatchBucketRequest request, StreamObserver<Bucket> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext((Bucket) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateBucket(UpdateBucketRequest request, StreamObserver<Bucket> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext((Bucket) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void stopChannel(StopChannelRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteDefaultObjectAccessControl(
      DeleteDefaultObjectAccessControlRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getDefaultObjectAccessControl(
      GetDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void insertDefaultObjectAccessControl(
      InsertDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listDefaultObjectAccessControls(
      ListDefaultObjectAccessControlsRequest request,
      StreamObserver<ListObjectAccessControlsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListObjectAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext((ListObjectAccessControlsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void patchDefaultObjectAccessControl(
      PatchDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateDefaultObjectAccessControl(
      UpdateDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteNotification(
      DeleteNotificationRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getNotification(
      GetNotificationRequest request, StreamObserver<Notification> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Notification) {
      requests.add(request);
      responseObserver.onNext((Notification) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void insertNotification(
      InsertNotificationRequest request, StreamObserver<Notification> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Notification) {
      requests.add(request);
      responseObserver.onNext((Notification) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listNotifications(
      ListNotificationsRequest request,
      StreamObserver<ListNotificationsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListNotificationsResponse) {
      requests.add(request);
      responseObserver.onNext((ListNotificationsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteObjectAccessControl(
      DeleteObjectAccessControlRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getObjectAccessControl(
      GetObjectAccessControlRequest request, StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void insertObjectAccessControl(
      InsertObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listObjectAccessControls(
      ListObjectAccessControlsRequest request,
      StreamObserver<ListObjectAccessControlsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListObjectAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext((ListObjectAccessControlsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void patchObjectAccessControl(
      PatchObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateObjectAccessControl(
      UpdateObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext((ObjectAccessControl) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void composeObject(
      ComposeObjectRequest request, StreamObserver<com.google.storage.v1.Object> responseObserver) {
    Object response = responses.remove();
    if (response instanceof com.google.storage.v1.Object) {
      requests.add(request);
      responseObserver.onNext((com.google.storage.v1.Object) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void copyObject(
      CopyObjectRequest request, StreamObserver<com.google.storage.v1.Object> responseObserver) {
    Object response = responses.remove();
    if (response instanceof com.google.storage.v1.Object) {
      requests.add(request);
      responseObserver.onNext((com.google.storage.v1.Object) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteObject(DeleteObjectRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getObject(
      GetObjectRequest request, StreamObserver<com.google.storage.v1.Object> responseObserver) {
    Object response = responses.remove();
    if (response instanceof com.google.storage.v1.Object) {
      requests.add(request);
      responseObserver.onNext((com.google.storage.v1.Object) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getObjectMedia(
      GetObjectMediaRequest request, StreamObserver<GetObjectMediaResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof GetObjectMediaResponse) {
      requests.add(request);
      responseObserver.onNext((GetObjectMediaResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public StreamObserver<InsertObjectRequest> insertObject(
      final StreamObserver<com.google.storage.v1.Object> responseObserver) {
    final Object response = responses.remove();
    StreamObserver<InsertObjectRequest> requestObserver =
        new StreamObserver<InsertObjectRequest>() {
          @Override
          public void onNext(InsertObjectRequest value) {
            if (response instanceof com.google.storage.v1.Object) {
              responseObserver.onNext((com.google.storage.v1.Object) response);
            } else if (response instanceof Exception) {
              responseObserver.onError((Exception) response);
            } else {
              responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
            }
          }

          @Override
          public void onError(Throwable t) {
            responseObserver.onError(t);
          }

          @Override
          public void onCompleted() {
            responseObserver.onCompleted();
          }
        };
    return requestObserver;
  }

  @Override
  public void listObjects(
      ListObjectsRequest request, StreamObserver<ListObjectsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListObjectsResponse) {
      requests.add(request);
      responseObserver.onNext((ListObjectsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void rewriteObject(
      RewriteObjectRequest request, StreamObserver<RewriteResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof RewriteResponse) {
      requests.add(request);
      responseObserver.onNext((RewriteResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void startResumableWrite(
      StartResumableWriteRequest request,
      StreamObserver<StartResumableWriteResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof StartResumableWriteResponse) {
      requests.add(request);
      responseObserver.onNext((StartResumableWriteResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void queryWriteStatus(
      QueryWriteStatusRequest request, StreamObserver<QueryWriteStatusResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof QueryWriteStatusResponse) {
      requests.add(request);
      responseObserver.onNext((QueryWriteStatusResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void patchObject(
      PatchObjectRequest request, StreamObserver<com.google.storage.v1.Object> responseObserver) {
    Object response = responses.remove();
    if (response instanceof com.google.storage.v1.Object) {
      requests.add(request);
      responseObserver.onNext((com.google.storage.v1.Object) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateObject(
      UpdateObjectRequest request, StreamObserver<com.google.storage.v1.Object> responseObserver) {
    Object response = responses.remove();
    if (response instanceof com.google.storage.v1.Object) {
      requests.add(request);
      responseObserver.onNext((com.google.storage.v1.Object) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getObjectIamPolicy(
      GetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext((Policy) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void setObjectIamPolicy(
      SetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext((Policy) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void testObjectIamPermissions(
      TestIamPermissionsRequest request,
      StreamObserver<TestIamPermissionsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof TestIamPermissionsResponse) {
      requests.add(request);
      responseObserver.onNext((TestIamPermissionsResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void watchAllObjects(
      WatchAllObjectsRequest request, StreamObserver<Channel> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Channel) {
      requests.add(request);
      responseObserver.onNext((Channel) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getServiceAccount(
      GetProjectServiceAccountRequest request, StreamObserver<ServiceAccount> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ServiceAccount) {
      requests.add(request);
      responseObserver.onNext((ServiceAccount) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void createHmacKey(
      CreateHmacKeyRequest request, StreamObserver<CreateHmacKeyResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof CreateHmacKeyResponse) {
      requests.add(request);
      responseObserver.onNext((CreateHmacKeyResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void deleteHmacKey(DeleteHmacKeyRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.remove();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext((Empty) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void getHmacKey(
      GetHmacKeyRequest request, StreamObserver<HmacKeyMetadata> responseObserver) {
    Object response = responses.remove();
    if (response instanceof HmacKeyMetadata) {
      requests.add(request);
      responseObserver.onNext((HmacKeyMetadata) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void listHmacKeys(
      ListHmacKeysRequest request, StreamObserver<ListHmacKeysResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ListHmacKeysResponse) {
      requests.add(request);
      responseObserver.onNext((ListHmacKeysResponse) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }

  @Override
  public void updateHmacKey(
      UpdateHmacKeyRequest request, StreamObserver<HmacKeyMetadata> responseObserver) {
    Object response = responses.remove();
    if (response instanceof HmacKeyMetadata) {
      requests.add(request);
      responseObserver.onNext((HmacKeyMetadata) response);
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError((Exception) response);
    } else {
      responseObserver.onError(new IllegalArgumentException("Unrecognized response type"));
    }
  }
}
