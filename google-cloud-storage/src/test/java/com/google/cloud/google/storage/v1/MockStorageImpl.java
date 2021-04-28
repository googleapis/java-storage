/*
 * Copyright 2021 Google LLC
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
import com.google.storage.v1.Object;
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
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockStorageImpl extends StorageImplBase {
  private List<AbstractMessage> requests;
  private Queue<java.lang.Object> responses;

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
    this.responses = new LinkedList<java.lang.Object>(responses);
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
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteBucketAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getBucketAccessControl(
      GetBucketAccessControlRequest request, StreamObserver<BucketAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext(((BucketAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetBucketAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  BucketAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void insertBucketAccessControl(
      InsertBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext(((BucketAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method InsertBucketAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  BucketAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listBucketAccessControls(
      ListBucketAccessControlsRequest request,
      StreamObserver<ListBucketAccessControlsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListBucketAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListBucketAccessControlsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListBucketAccessControls, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListBucketAccessControlsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateBucketAccessControl(
      UpdateBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext(((BucketAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateBucketAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  BucketAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void patchBucketAccessControl(
      PatchBucketAccessControlRequest request,
      StreamObserver<BucketAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof BucketAccessControl) {
      requests.add(request);
      responseObserver.onNext(((BucketAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method PatchBucketAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  BucketAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteBucket(DeleteBucketRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteBucket, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getBucket(GetBucketRequest request, StreamObserver<Bucket> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext(((Bucket) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetBucket, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Bucket.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void insertBucket(InsertBucketRequest request, StreamObserver<Bucket> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext(((Bucket) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method InsertBucket, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Bucket.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listChannels(
      ListChannelsRequest request, StreamObserver<ListChannelsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListChannelsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListChannelsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListChannels, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListChannelsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listBuckets(
      ListBucketsRequest request, StreamObserver<ListBucketsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListBucketsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListBucketsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListBuckets, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListBucketsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void lockBucketRetentionPolicy(
      LockRetentionPolicyRequest request, StreamObserver<Bucket> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext(((Bucket) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method LockBucketRetentionPolicy, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Bucket.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getBucketIamPolicy(
      GetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext(((Policy) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetBucketIamPolicy, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Policy.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void setBucketIamPolicy(
      SetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext(((Policy) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method SetBucketIamPolicy, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Policy.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void testBucketIamPermissions(
      TestIamPermissionsRequest request,
      StreamObserver<TestIamPermissionsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof TestIamPermissionsResponse) {
      requests.add(request);
      responseObserver.onNext(((TestIamPermissionsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method TestBucketIamPermissions, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  TestIamPermissionsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void patchBucket(PatchBucketRequest request, StreamObserver<Bucket> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext(((Bucket) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method PatchBucket, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Bucket.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateBucket(UpdateBucketRequest request, StreamObserver<Bucket> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Bucket) {
      requests.add(request);
      responseObserver.onNext(((Bucket) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateBucket, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Bucket.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void stopChannel(StopChannelRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method StopChannel, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteDefaultObjectAccessControl(
      DeleteDefaultObjectAccessControlRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteDefaultObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getDefaultObjectAccessControl(
      GetDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetDefaultObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void insertDefaultObjectAccessControl(
      InsertDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method InsertDefaultObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listDefaultObjectAccessControls(
      ListDefaultObjectAccessControlsRequest request,
      StreamObserver<ListObjectAccessControlsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListObjectAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListObjectAccessControlsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListDefaultObjectAccessControls, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListObjectAccessControlsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void patchDefaultObjectAccessControl(
      PatchDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method PatchDefaultObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateDefaultObjectAccessControl(
      UpdateDefaultObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateDefaultObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteNotification(
      DeleteNotificationRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteNotification, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getNotification(
      GetNotificationRequest request, StreamObserver<Notification> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Notification) {
      requests.add(request);
      responseObserver.onNext(((Notification) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetNotification, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Notification.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void insertNotification(
      InsertNotificationRequest request, StreamObserver<Notification> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Notification) {
      requests.add(request);
      responseObserver.onNext(((Notification) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method InsertNotification, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Notification.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listNotifications(
      ListNotificationsRequest request,
      StreamObserver<ListNotificationsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListNotificationsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListNotificationsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListNotifications, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListNotificationsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteObjectAccessControl(
      DeleteObjectAccessControlRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getObjectAccessControl(
      GetObjectAccessControlRequest request, StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void insertObjectAccessControl(
      InsertObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method InsertObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listObjectAccessControls(
      ListObjectAccessControlsRequest request,
      StreamObserver<ListObjectAccessControlsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListObjectAccessControlsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListObjectAccessControlsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListObjectAccessControls, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListObjectAccessControlsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void patchObjectAccessControl(
      PatchObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method PatchObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateObjectAccessControl(
      UpdateObjectAccessControlRequest request,
      StreamObserver<ObjectAccessControl> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ObjectAccessControl) {
      requests.add(request);
      responseObserver.onNext(((ObjectAccessControl) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateObjectAccessControl, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ObjectAccessControl.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void composeObject(ComposeObjectRequest request, StreamObserver<Object> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Object) {
      requests.add(request);
      responseObserver.onNext(((Object) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ComposeObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Object.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void copyObject(CopyObjectRequest request, StreamObserver<Object> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Object) {
      requests.add(request);
      responseObserver.onNext(((Object) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CopyObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Object.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteObject(DeleteObjectRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getObject(GetObjectRequest request, StreamObserver<Object> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Object) {
      requests.add(request);
      responseObserver.onNext(((Object) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Object.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getObjectMedia(
      GetObjectMediaRequest request, StreamObserver<GetObjectMediaResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof GetObjectMediaResponse) {
      requests.add(request);
      responseObserver.onNext(((GetObjectMediaResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetObjectMedia, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  GetObjectMediaResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public StreamObserver<InsertObjectRequest> insertObject(
      final StreamObserver<Object> responseObserver) {
    StreamObserver<InsertObjectRequest> requestObserver =
        new StreamObserver<InsertObjectRequest>() {
          @Override
          public void onNext(InsertObjectRequest value) {
            requests.add(value);
            final java.lang.Object response = responses.remove();
            if (response instanceof Object) {
              responseObserver.onNext(((Object) response));
            } else if (response instanceof Exception) {
              responseObserver.onError(((Exception) response));
            } else {
              responseObserver.onError(
                  new IllegalArgumentException(
                      String.format(
                          "Unrecognized response type %s for method InsertObject, expected %s or %s",
                          response == null ? "null" : response.getClass().getName(),
                          Object.class.getName(),
                          Exception.class.getName())));
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
    java.lang.Object response = responses.poll();
    if (response instanceof ListObjectsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListObjectsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListObjects, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListObjectsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void rewriteObject(
      RewriteObjectRequest request, StreamObserver<RewriteResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof RewriteResponse) {
      requests.add(request);
      responseObserver.onNext(((RewriteResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method RewriteObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  RewriteResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void startResumableWrite(
      StartResumableWriteRequest request,
      StreamObserver<StartResumableWriteResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof StartResumableWriteResponse) {
      requests.add(request);
      responseObserver.onNext(((StartResumableWriteResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method StartResumableWrite, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  StartResumableWriteResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void queryWriteStatus(
      QueryWriteStatusRequest request, StreamObserver<QueryWriteStatusResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof QueryWriteStatusResponse) {
      requests.add(request);
      responseObserver.onNext(((QueryWriteStatusResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method QueryWriteStatus, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  QueryWriteStatusResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void patchObject(PatchObjectRequest request, StreamObserver<Object> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Object) {
      requests.add(request);
      responseObserver.onNext(((Object) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method PatchObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Object.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateObject(UpdateObjectRequest request, StreamObserver<Object> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Object) {
      requests.add(request);
      responseObserver.onNext(((Object) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateObject, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Object.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getObjectIamPolicy(
      GetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext(((Policy) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetObjectIamPolicy, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Policy.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void setObjectIamPolicy(
      SetIamPolicyRequest request, StreamObserver<Policy> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Policy) {
      requests.add(request);
      responseObserver.onNext(((Policy) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method SetObjectIamPolicy, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Policy.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void testObjectIamPermissions(
      TestIamPermissionsRequest request,
      StreamObserver<TestIamPermissionsResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof TestIamPermissionsResponse) {
      requests.add(request);
      responseObserver.onNext(((TestIamPermissionsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method TestObjectIamPermissions, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  TestIamPermissionsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void watchAllObjects(
      WatchAllObjectsRequest request, StreamObserver<Channel> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Channel) {
      requests.add(request);
      responseObserver.onNext(((Channel) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method WatchAllObjects, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Channel.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getServiceAccount(
      GetProjectServiceAccountRequest request, StreamObserver<ServiceAccount> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ServiceAccount) {
      requests.add(request);
      responseObserver.onNext(((ServiceAccount) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetServiceAccount, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ServiceAccount.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void createHmacKey(
      CreateHmacKeyRequest request, StreamObserver<CreateHmacKeyResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof CreateHmacKeyResponse) {
      requests.add(request);
      responseObserver.onNext(((CreateHmacKeyResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateHmacKey, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  CreateHmacKeyResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteHmacKey(DeleteHmacKeyRequest request, StreamObserver<Empty> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteHmacKey, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getHmacKey(
      GetHmacKeyRequest request, StreamObserver<HmacKeyMetadata> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof HmacKeyMetadata) {
      requests.add(request);
      responseObserver.onNext(((HmacKeyMetadata) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetHmacKey, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  HmacKeyMetadata.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listHmacKeys(
      ListHmacKeysRequest request, StreamObserver<ListHmacKeysResponse> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof ListHmacKeysResponse) {
      requests.add(request);
      responseObserver.onNext(((ListHmacKeysResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListHmacKeys, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListHmacKeysResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateHmacKey(
      UpdateHmacKeyRequest request, StreamObserver<HmacKeyMetadata> responseObserver) {
    java.lang.Object response = responses.poll();
    if (response instanceof HmacKeyMetadata) {
      requests.add(request);
      responseObserver.onNext(((HmacKeyMetadata) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateHmacKey, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  HmacKeyMetadata.class.getName(),
                  Exception.class.getName())));
    }
  }
}
