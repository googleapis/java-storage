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
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.google.storage.v1.stub.StorageStub;
import com.google.cloud.google.storage.v1.stub.StorageStubSettings;
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
import com.google.storage.v1.TestIamPermissionsRequest;
import com.google.storage.v1.UpdateBucketAccessControlRequest;
import com.google.storage.v1.UpdateBucketRequest;
import com.google.storage.v1.UpdateDefaultObjectAccessControlRequest;
import com.google.storage.v1.UpdateHmacKeyRequest;
import com.google.storage.v1.UpdateObjectAccessControlRequest;
import com.google.storage.v1.UpdateObjectRequest;
import com.google.storage.v1.WatchAllObjectsRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: Manages Google Cloud Storage resources.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
 * try (StorageClient storageClient = StorageClient.create()) {
 *   DeleteBucketAccessControlRequest request =
 *       DeleteBucketAccessControlRequest.newBuilder()
 *           .setBucket("bucket-1378203158")
 *           .setEntity("entity-1298275357")
 *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
 *           .build();
 *   storageClient.deleteBucketAccessControl(request);
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the StorageClient object to clean up resources such as
 * threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li>A "flattened" method. With this type of method, the fields of the request type have been
 *       converted into function parameters. It may be the case that not all fields are available as
 *       parameters, and not every API method will have a flattened method entry point.
 *   <li>A "request object" method. This type of method only takes one parameter, a request object,
 *       which must be constructed before the call. Not every API method will have a request object
 *       method.
 *   <li>A "callable" method. This type of method takes no parameters and returns an immutable API
 *       callable object, which can be used to initiate calls to the service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of StorageSettings to create().
 * For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * StorageSettings storageSettings =
 *     StorageSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * StorageClient storageClient = StorageClient.create(storageSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * StorageSettings storageSettings = StorageSettings.newBuilder().setEndpoint(myEndpoint).build();
 * StorageClient storageClient = StorageClient.create(storageSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class StorageClient implements BackgroundResource {
  private final StorageSettings settings;
  private final StorageStub stub;

  /** Constructs an instance of StorageClient with default settings. */
  public static final StorageClient create() throws IOException {
    return create(StorageSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of StorageClient, using the given settings. The channels are created
   * based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final StorageClient create(StorageSettings settings) throws IOException {
    return new StorageClient(settings);
  }

  /**
   * Constructs an instance of StorageClient, using the given stub for making calls. This is for
   * advanced usage - prefer using create(StorageSettings).
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final StorageClient create(StorageStub stub) {
    return new StorageClient(stub);
  }

  /**
   * Constructs an instance of StorageClient, using the given settings. This is protected so that it
   * is easy to make a subclass, but otherwise, the static factory methods should be preferred.
   */
  protected StorageClient(StorageSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((StorageStubSettings) settings.getStubSettings()).createStub();
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected StorageClient(StorageStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final StorageSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public StorageStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteBucketAccessControlRequest request =
   *       DeleteBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteBucketAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteBucketAccessControl(DeleteBucketAccessControlRequest request) {
    deleteBucketAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteBucketAccessControlRequest request =
   *       DeleteBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future =
   *       storageClient.deleteBucketAccessControlCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteBucketAccessControlRequest, Empty>
      deleteBucketAccessControlCallable() {
    return stub.deleteBucketAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetBucketAccessControlRequest request =
   *       GetBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   BucketAccessControl response = storageClient.getBucketAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BucketAccessControl getBucketAccessControl(GetBucketAccessControlRequest request) {
    return getBucketAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetBucketAccessControlRequest request =
   *       GetBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<BucketAccessControl> future =
   *       storageClient.getBucketAccessControlCallable().futureCall(request);
   *   // Do something.
   *   BucketAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetBucketAccessControlRequest, BucketAccessControl>
      getBucketAccessControlCallable() {
    return stub.getBucketAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertBucketAccessControlRequest request =
   *       InsertBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   BucketAccessControl response = storageClient.insertBucketAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BucketAccessControl insertBucketAccessControl(
      InsertBucketAccessControlRequest request) {
    return insertBucketAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertBucketAccessControlRequest request =
   *       InsertBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<BucketAccessControl> future =
   *       storageClient.insertBucketAccessControlCallable().futureCall(request);
   *   // Do something.
   *   BucketAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<InsertBucketAccessControlRequest, BucketAccessControl>
      insertBucketAccessControlCallable() {
    return stub.insertBucketAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves ACL entries on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListBucketAccessControlsRequest request =
   *       ListBucketAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListBucketAccessControlsResponse response = storageClient.listBucketAccessControls(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListBucketAccessControlsResponse listBucketAccessControls(
      ListBucketAccessControlsRequest request) {
    return listBucketAccessControlsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves ACL entries on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListBucketAccessControlsRequest request =
   *       ListBucketAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListBucketAccessControlsResponse> future =
   *       storageClient.listBucketAccessControlsCallable().futureCall(request);
   *   // Do something.
   *   ListBucketAccessControlsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListBucketAccessControlsRequest, ListBucketAccessControlsResponse>
      listBucketAccessControlsCallable() {
    return stub.listBucketAccessControlsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified bucket. Equivalent to PatchBucketAccessControl, but all
   * unspecified fields will be reset to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateBucketAccessControlRequest request =
   *       UpdateBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   BucketAccessControl response = storageClient.updateBucketAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BucketAccessControl updateBucketAccessControl(
      UpdateBucketAccessControlRequest request) {
    return updateBucketAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified bucket. Equivalent to PatchBucketAccessControl, but all
   * unspecified fields will be reset to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateBucketAccessControlRequest request =
   *       UpdateBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<BucketAccessControl> future =
   *       storageClient.updateBucketAccessControlCallable().futureCall(request);
   *   // Do something.
   *   BucketAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateBucketAccessControlRequest, BucketAccessControl>
      updateBucketAccessControlCallable() {
    return stub.updateBucketAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchBucketAccessControlRequest request =
   *       PatchBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   BucketAccessControl response = storageClient.patchBucketAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BucketAccessControl patchBucketAccessControl(
      PatchBucketAccessControlRequest request) {
    return patchBucketAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchBucketAccessControlRequest request =
   *       PatchBucketAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setBucketAccessControl(BucketAccessControl.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<BucketAccessControl> future =
   *       storageClient.patchBucketAccessControlCallable().futureCall(request);
   *   // Do something.
   *   BucketAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<PatchBucketAccessControlRequest, BucketAccessControl>
      patchBucketAccessControlCallable() {
    return stub.patchBucketAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteBucketRequest request =
   *       DeleteBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteBucket(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteBucket(DeleteBucketRequest request) {
    deleteBucketCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteBucketRequest request =
   *       DeleteBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future = storageClient.deleteBucketCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteBucketRequest, Empty> deleteBucketCallable() {
    return stub.deleteBucketCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetBucketRequest request =
   *       GetBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Bucket response = storageClient.getBucket(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Bucket getBucket(GetBucketRequest request) {
    return getBucketCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetBucketRequest request =
   *       GetBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Bucket> future = storageClient.getBucketCallable().futureCall(request);
   *   // Do something.
   *   Bucket response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetBucketRequest, Bucket> getBucketCallable() {
    return stub.getBucketCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertBucketRequest request =
   *       InsertBucketRequest.newBuilder()
   *           .setProject("project-309310695")
   *           .setBucket(Bucket.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Bucket response = storageClient.insertBucket(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Bucket insertBucket(InsertBucketRequest request) {
    return insertBucketCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertBucketRequest request =
   *       InsertBucketRequest.newBuilder()
   *           .setProject("project-309310695")
   *           .setBucket(Bucket.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Bucket> future = storageClient.insertBucketCallable().futureCall(request);
   *   // Do something.
   *   Bucket response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<InsertBucketRequest, Bucket> insertBucketCallable() {
    return stub.insertBucketCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * List active object change notification channels for this bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListChannelsRequest request =
   *       ListChannelsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListChannelsResponse response = storageClient.listChannels(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListChannelsResponse listChannels(ListChannelsRequest request) {
    return listChannelsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * List active object change notification channels for this bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListChannelsRequest request =
   *       ListChannelsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListChannelsResponse> future =
   *       storageClient.listChannelsCallable().futureCall(request);
   *   // Do something.
   *   ListChannelsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListChannelsRequest, ListChannelsResponse> listChannelsCallable() {
    return stub.listChannelsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of buckets for a given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListBucketsRequest request =
   *       ListBucketsRequest.newBuilder()
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setProject("project-309310695")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListBucketsResponse response = storageClient.listBuckets(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListBucketsResponse listBuckets(ListBucketsRequest request) {
    return listBucketsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of buckets for a given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListBucketsRequest request =
   *       ListBucketsRequest.newBuilder()
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setProject("project-309310695")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListBucketsResponse> future =
   *       storageClient.listBucketsCallable().futureCall(request);
   *   // Do something.
   *   ListBucketsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListBucketsRequest, ListBucketsResponse> listBucketsCallable() {
    return stub.listBucketsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Locks retention policy on a bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   LockRetentionPolicyRequest request =
   *       LockRetentionPolicyRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(1043427781)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Bucket response = storageClient.lockBucketRetentionPolicy(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Bucket lockBucketRetentionPolicy(LockRetentionPolicyRequest request) {
    return lockBucketRetentionPolicyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Locks retention policy on a bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   LockRetentionPolicyRequest request =
   *       LockRetentionPolicyRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(1043427781)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Bucket> future =
   *       storageClient.lockBucketRetentionPolicyCallable().futureCall(request);
   *   // Do something.
   *   Bucket response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<LockRetentionPolicyRequest, Bucket>
      lockBucketRetentionPolicyCallable() {
    return stub.lockBucketRetentionPolicyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets the IAM policy for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetIamPolicyRequest request =
   *       GetIamPolicyRequest.newBuilder()
   *           .setIamRequest(GetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Policy response = storageClient.getBucketIamPolicy(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Policy getBucketIamPolicy(GetIamPolicyRequest request) {
    return getBucketIamPolicyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets the IAM policy for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetIamPolicyRequest request =
   *       GetIamPolicyRequest.newBuilder()
   *           .setIamRequest(GetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Policy> future = storageClient.getBucketIamPolicyCallable().futureCall(request);
   *   // Do something.
   *   Policy response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetIamPolicyRequest, Policy> getBucketIamPolicyCallable() {
    return stub.getBucketIamPolicyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an IAM policy for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   SetIamPolicyRequest request =
   *       SetIamPolicyRequest.newBuilder()
   *           .setIamRequest(SetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Policy response = storageClient.setBucketIamPolicy(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Policy setBucketIamPolicy(SetIamPolicyRequest request) {
    return setBucketIamPolicyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an IAM policy for the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   SetIamPolicyRequest request =
   *       SetIamPolicyRequest.newBuilder()
   *           .setIamRequest(SetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Policy> future = storageClient.setBucketIamPolicyCallable().futureCall(request);
   *   // Do something.
   *   Policy response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<SetIamPolicyRequest, Policy> setBucketIamPolicyCallable() {
    return stub.setBucketIamPolicyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Tests a set of permissions on the given bucket to see which, if any, are held by the caller.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   TestIamPermissionsRequest request =
   *       TestIamPermissionsRequest.newBuilder()
   *           .setIamRequest(TestIamPermissionsRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   TestIamPermissionsResponse response = storageClient.testBucketIamPermissions(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final TestIamPermissionsResponse testBucketIamPermissions(
      TestIamPermissionsRequest request) {
    return testBucketIamPermissionsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Tests a set of permissions on the given bucket to see which, if any, are held by the caller.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   TestIamPermissionsRequest request =
   *       TestIamPermissionsRequest.newBuilder()
   *           .setIamRequest(TestIamPermissionsRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<TestIamPermissionsResponse> future =
   *       storageClient.testBucketIamPermissionsCallable().futureCall(request);
   *   // Do something.
   *   TestIamPermissionsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testBucketIamPermissionsCallable() {
    return stub.testBucketIamPermissionsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a bucket. Changes to the bucket will be readable immediately after writing, but
   * configuration changes may take time to propagate.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchBucketRequest request =
   *       PatchBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Bucket.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Bucket response = storageClient.patchBucket(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Bucket patchBucket(PatchBucketRequest request) {
    return patchBucketCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a bucket. Changes to the bucket will be readable immediately after writing, but
   * configuration changes may take time to propagate.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchBucketRequest request =
   *       PatchBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Bucket.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Bucket> future = storageClient.patchBucketCallable().futureCall(request);
   *   // Do something.
   *   Bucket response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<PatchBucketRequest, Bucket> patchBucketCallable() {
    return stub.patchBucketCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a bucket. Equivalent to PatchBucket, but always replaces all mutatable fields of the
   * bucket with new values, reverting all unspecified fields to their default values. Like
   * PatchBucket, Changes to the bucket will be readable immediately after writing, but
   * configuration changes may take time to propagate.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateBucketRequest request =
   *       UpdateBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Bucket.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Bucket response = storageClient.updateBucket(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Bucket updateBucket(UpdateBucketRequest request) {
    return updateBucketCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a bucket. Equivalent to PatchBucket, but always replaces all mutatable fields of the
   * bucket with new values, reverting all unspecified fields to their default values. Like
   * PatchBucket, Changes to the bucket will be readable immediately after writing, but
   * configuration changes may take time to propagate.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateBucketRequest request =
   *       UpdateBucketRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Bucket.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Bucket> future = storageClient.updateBucketCallable().futureCall(request);
   *   // Do something.
   *   Bucket response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateBucketRequest, Bucket> updateBucketCallable() {
    return stub.updateBucketCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Halts "Object Change Notification" push messagages. See
   * https://cloud.google.com/storage/docs/object-change-notification Note: this is not related to
   * the newer "Notifications" resource, which are stopped using DeleteNotification.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   StopChannelRequest request =
   *       StopChannelRequest.newBuilder()
   *           .setChannel(Channel.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.stopChannel(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void stopChannel(StopChannelRequest request) {
    stopChannelCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Halts "Object Change Notification" push messagages. See
   * https://cloud.google.com/storage/docs/object-change-notification Note: this is not related to
   * the newer "Notifications" resource, which are stopped using DeleteNotification.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   StopChannelRequest request =
   *       StopChannelRequest.newBuilder()
   *           .setChannel(Channel.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future = storageClient.stopChannelCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<StopChannelRequest, Empty> stopChannelCallable() {
    return stub.stopChannelCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the default object ACL entry for the specified entity on the specified
   * bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteDefaultObjectAccessControlRequest request =
   *       DeleteDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteDefaultObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteDefaultObjectAccessControl(
      DeleteDefaultObjectAccessControlRequest request) {
    deleteDefaultObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the default object ACL entry for the specified entity on the specified
   * bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteDefaultObjectAccessControlRequest request =
   *       DeleteDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future =
   *       storageClient.deleteDefaultObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteDefaultObjectAccessControlRequest, Empty>
      deleteDefaultObjectAccessControlCallable() {
    return stub.deleteDefaultObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the default object ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetDefaultObjectAccessControlRequest request =
   *       GetDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.getDefaultObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl getDefaultObjectAccessControl(
      GetDefaultObjectAccessControlRequest request) {
    return getDefaultObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the default object ACL entry for the specified entity on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetDefaultObjectAccessControlRequest request =
   *       GetDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.getDefaultObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetDefaultObjectAccessControlRequest, ObjectAccessControl>
      getDefaultObjectAccessControlCallable() {
    return stub.getDefaultObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new default object ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertDefaultObjectAccessControlRequest request =
   *       InsertDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.insertDefaultObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl insertDefaultObjectAccessControl(
      InsertDefaultObjectAccessControlRequest request) {
    return insertDefaultObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new default object ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertDefaultObjectAccessControlRequest request =
   *       InsertDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.insertDefaultObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<InsertDefaultObjectAccessControlRequest, ObjectAccessControl>
      insertDefaultObjectAccessControlCallable() {
    return stub.insertDefaultObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves default object ACL entries on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListDefaultObjectAccessControlsRequest request =
   *       ListDefaultObjectAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListObjectAccessControlsResponse response =
   *       storageClient.listDefaultObjectAccessControls(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListObjectAccessControlsResponse listDefaultObjectAccessControls(
      ListDefaultObjectAccessControlsRequest request) {
    return listDefaultObjectAccessControlsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves default object ACL entries on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListDefaultObjectAccessControlsRequest request =
   *       ListDefaultObjectAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListObjectAccessControlsResponse> future =
   *       storageClient.listDefaultObjectAccessControlsCallable().futureCall(request);
   *   // Do something.
   *   ListObjectAccessControlsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<
          ListDefaultObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listDefaultObjectAccessControlsCallable() {
    return stub.listDefaultObjectAccessControlsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a default object ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchDefaultObjectAccessControlRequest request =
   *       PatchDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.patchDefaultObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl patchDefaultObjectAccessControl(
      PatchDefaultObjectAccessControlRequest request) {
    return patchDefaultObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a default object ACL entry on the specified bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchDefaultObjectAccessControlRequest request =
   *       PatchDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.patchDefaultObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<PatchDefaultObjectAccessControlRequest, ObjectAccessControl>
      patchDefaultObjectAccessControlCallable() {
    return stub.patchDefaultObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a default object ACL entry on the specified bucket. Equivalent to
   * PatchDefaultObjectAccessControl, but modifies all unspecified fields to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateDefaultObjectAccessControlRequest request =
   *       UpdateDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.updateDefaultObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl updateDefaultObjectAccessControl(
      UpdateDefaultObjectAccessControlRequest request) {
    return updateDefaultObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a default object ACL entry on the specified bucket. Equivalent to
   * PatchDefaultObjectAccessControl, but modifies all unspecified fields to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateDefaultObjectAccessControlRequest request =
   *       UpdateDefaultObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.updateDefaultObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateDefaultObjectAccessControlRequest, ObjectAccessControl>
      updateDefaultObjectAccessControlCallable() {
    return stub.updateDefaultObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes a notification subscription. Note: Older, "Object Change Notification" push
   * subscriptions should be deleted using StopChannel instead.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteNotificationRequest request =
   *       DeleteNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification("notification595233003")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteNotification(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteNotification(DeleteNotificationRequest request) {
    deleteNotificationCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes a notification subscription. Note: Older, "Object Change Notification" push
   * subscriptions should be deleted using StopChannel instead.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteNotificationRequest request =
   *       DeleteNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification("notification595233003")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future = storageClient.deleteNotificationCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteNotificationRequest, Empty> deleteNotificationCallable() {
    return stub.deleteNotificationCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * View a notification configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetNotificationRequest request =
   *       GetNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification("notification595233003")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Notification response = storageClient.getNotification(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Notification getNotification(GetNotificationRequest request) {
    return getNotificationCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * View a notification configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetNotificationRequest request =
   *       GetNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification("notification595233003")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Notification> future = storageClient.getNotificationCallable().futureCall(request);
   *   // Do something.
   *   Notification response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetNotificationRequest, Notification> getNotificationCallable() {
    return stub.getNotificationCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a notification subscription for a given bucket. These notifications, when triggered,
   * publish messages to the specified Cloud Pub/Sub topics. See
   * https://cloud.google.com/storage/docs/pubsub-notifications.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertNotificationRequest request =
   *       InsertNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification(Notification.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Notification response = storageClient.insertNotification(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Notification insertNotification(InsertNotificationRequest request) {
    return insertNotificationCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a notification subscription for a given bucket. These notifications, when triggered,
   * publish messages to the specified Cloud Pub/Sub topics. See
   * https://cloud.google.com/storage/docs/pubsub-notifications.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertNotificationRequest request =
   *       InsertNotificationRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setNotification(Notification.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Notification> future =
   *       storageClient.insertNotificationCallable().futureCall(request);
   *   // Do something.
   *   Notification response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<InsertNotificationRequest, Notification> insertNotificationCallable() {
    return stub.insertNotificationCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of notification subscriptions for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListNotificationsRequest request =
   *       ListNotificationsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListNotificationsResponse response = storageClient.listNotifications(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListNotificationsResponse listNotifications(ListNotificationsRequest request) {
    return listNotificationsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of notification subscriptions for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListNotificationsRequest request =
   *       ListNotificationsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListNotificationsResponse> future =
   *       storageClient.listNotificationsCallable().futureCall(request);
   *   // Do something.
   *   ListNotificationsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListNotificationsRequest, ListNotificationsResponse>
      listNotificationsCallable() {
    return stub.listNotificationsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the ACL entry for the specified entity on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteObjectAccessControlRequest request =
   *       DeleteObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteObjectAccessControl(DeleteObjectAccessControlRequest request) {
    deleteObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes the ACL entry for the specified entity on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteObjectAccessControlRequest request =
   *       DeleteObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future =
   *       storageClient.deleteObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteObjectAccessControlRequest, Empty>
      deleteObjectAccessControlCallable() {
    return stub.deleteObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the ACL entry for the specified entity on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetObjectAccessControlRequest request =
   *       GetObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.getObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl getObjectAccessControl(GetObjectAccessControlRequest request) {
    return getObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the ACL entry for the specified entity on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetObjectAccessControlRequest request =
   *       GetObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.getObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetObjectAccessControlRequest, ObjectAccessControl>
      getObjectAccessControlCallable() {
    return stub.getObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new ACL entry on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertObjectAccessControlRequest request =
   *       InsertObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.insertObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl insertObjectAccessControl(
      InsertObjectAccessControlRequest request) {
    return insertObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new ACL entry on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   InsertObjectAccessControlRequest request =
   *       InsertObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.insertObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<InsertObjectAccessControlRequest, ObjectAccessControl>
      insertObjectAccessControlCallable() {
    return stub.insertObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves ACL entries on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListObjectAccessControlsRequest request =
   *       ListObjectAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListObjectAccessControlsResponse response = storageClient.listObjectAccessControls(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListObjectAccessControlsResponse listObjectAccessControls(
      ListObjectAccessControlsRequest request) {
    return listObjectAccessControlsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves ACL entries on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListObjectAccessControlsRequest request =
   *       ListObjectAccessControlsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListObjectAccessControlsResponse> future =
   *       storageClient.listObjectAccessControlsCallable().futureCall(request);
   *   // Do something.
   *   ListObjectAccessControlsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListObjectAccessControlsRequest, ListObjectAccessControlsResponse>
      listObjectAccessControlsCallable() {
    return stub.listObjectAccessControlsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Patches an ACL entry on the specified object. Patch is similar to update, but only applies or
   * appends the specified fields in the object_access_control object. Other fields are unaffected.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchObjectAccessControlRequest request =
   *       PatchObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.patchObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl patchObjectAccessControl(
      PatchObjectAccessControlRequest request) {
    return patchObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Patches an ACL entry on the specified object. Patch is similar to update, but only applies or
   * appends the specified fields in the object_access_control object. Other fields are unaffected.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchObjectAccessControlRequest request =
   *       PatchObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.patchObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<PatchObjectAccessControlRequest, ObjectAccessControl>
      patchObjectAccessControlCallable() {
    return stub.patchObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateObjectAccessControlRequest request =
   *       UpdateObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ObjectAccessControl response = storageClient.updateObjectAccessControl(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ObjectAccessControl updateObjectAccessControl(
      UpdateObjectAccessControlRequest request) {
    return updateObjectAccessControlCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an ACL entry on the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateObjectAccessControlRequest request =
   *       UpdateObjectAccessControlRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setEntity("entity-1298275357")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setObjectAccessControl(ObjectAccessControl.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ApiFuture<ObjectAccessControl> future =
   *       storageClient.updateObjectAccessControlCallable().futureCall(request);
   *   // Do something.
   *   ObjectAccessControl response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateObjectAccessControlRequest, ObjectAccessControl>
      updateObjectAccessControlCallable() {
    return stub.updateObjectAccessControlCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Concatenates a list of existing objects into a new object in the same bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ComposeObjectRequest request =
   *       ComposeObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setDestination(Object.newBuilder().build())
   *           .addAllSourceObjects(new ArrayList<ComposeObjectRequest.SourceObjects>())
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setKmsKeyName("kmsKeyName412586233")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Object response = storageClient.composeObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Object composeObject(ComposeObjectRequest request) {
    return composeObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Concatenates a list of existing objects into a new object in the same bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ComposeObjectRequest request =
   *       ComposeObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setDestination(Object.newBuilder().build())
   *           .addAllSourceObjects(new ArrayList<ComposeObjectRequest.SourceObjects>())
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setKmsKeyName("kmsKeyName412586233")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Object> future = storageClient.composeObjectCallable().futureCall(request);
   *   // Do something.
   *   Object response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ComposeObjectRequest, Object> composeObjectCallable() {
    return stub.composeObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Copies a source object to a destination object. Optionally overrides metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   CopyObjectRequest request =
   *       CopyObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setSourceBucket("sourceBucket841604581")
   *           .setSourceObject("sourceObject1196439354")
   *           .setSourceGeneration(1232209852)
   *           .setDestination(Object.newBuilder().build())
   *           .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Object response = storageClient.copyObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Object copyObject(CopyObjectRequest request) {
    return copyObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Copies a source object to a destination object. Optionally overrides metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   CopyObjectRequest request =
   *       CopyObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setSourceBucket("sourceBucket841604581")
   *           .setSourceObject("sourceObject1196439354")
   *           .setSourceGeneration(1232209852)
   *           .setDestination(Object.newBuilder().build())
   *           .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Object> future = storageClient.copyObjectCallable().futureCall(request);
   *   // Do something.
   *   Object response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CopyObjectRequest, Object> copyObjectCallable() {
    return stub.copyObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes an object and its metadata. Deletions are permanent if versioning is not enabled for
   * the bucket, or if the `generation` parameter is used.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteObjectRequest request =
   *       DeleteObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setUploadId("uploadId1563990780")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteObject(DeleteObjectRequest request) {
    deleteObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes an object and its metadata. Deletions are permanent if versioning is not enabled for
   * the bucket, or if the `generation` parameter is used.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteObjectRequest request =
   *       DeleteObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setUploadId("uploadId1563990780")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future = storageClient.deleteObjectCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteObjectRequest, Empty> deleteObjectCallable() {
    return stub.deleteObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves an object's metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetObjectRequest request =
   *       GetObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Object response = storageClient.getObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Object getObject(GetObjectRequest request) {
    return getObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves an object's metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetObjectRequest request =
   *       GetObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Object> future = storageClient.getObjectCallable().futureCall(request);
   *   // Do something.
   *   Object response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetObjectRequest, Object> getObjectCallable() {
    return stub.getObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Reads an object's data.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetObjectMediaRequest request =
   *       GetObjectMediaRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setReadOffset(-715377828)
   *           .setReadLimit(-164298798)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ServerStream<GetObjectMediaResponse> stream =
   *       storageClient.getObjectMediaCallable().call(request);
   *   for (GetObjectMediaResponse response : stream) {
   *     // Do something when a response is received.
   *   }
   * }
   * }</pre>
   */
  public final ServerStreamingCallable<GetObjectMediaRequest, GetObjectMediaResponse>
      getObjectMediaCallable() {
    return stub.getObjectMediaCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Stores a new object and metadata.
   *
   * <p>An object can be written either in a single message stream or in a resumable sequence of
   * message streams. To write using a single stream, the client should include in the first message
   * of the stream an `InsertObjectSpec` describing the destination bucket, object, and any
   * preconditions. Additionally, the final message must set 'finish_write' to true, or else it is
   * an error.
   *
   * <p>For a resumable write, the client should instead call `StartResumableWrite()` and provide
   * that method an `InsertObjectSpec.` They should then attach the returned `upload_id` to the
   * first message of each following call to `Insert`. If there is an error or the connection is
   * broken during the resumable `Insert()`, the client should check the status of the `Insert()` by
   * calling `QueryWriteStatus()` and continue writing from the returned `committed_size`. This may
   * be less than the amount of data the client previously sent.
   *
   * <p>The service will not view the object as complete until the client has sent an `Insert` with
   * `finish_write` set to `true`. Sending any requests on a stream after sending a request with
   * `finish_write` set to `true` will cause an error. The client &#42;&#42;should&#42;&#42; check
   * the `Object` it receives to determine how much data the service was able to commit and whether
   * the service views the object as complete.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ApiStreamObserver<InsertObjectRequest> responseObserver =
   *       new ApiStreamObserver<InsertObjectRequest>() {
   *         {@literal @}Override
   *         public void onNext(Object response) {
   *           // Do something when a response is received.
   *         }
   *
   *         {@literal @}Override
   *         public void onError(Throwable t) {
   *           // Add error-handling
   *         }
   *
   *         {@literal @}Override
   *         public void onCompleted() {
   *           // Do something when complete.
   *         }
   *       };
   *   ApiStreamObserver<InsertObjectRequest> requestObserver =
   *       storageClient.insertObject().clientStreamingCall(responseObserver);
   *   InsertObjectRequest request =
   *       InsertObjectRequest.newBuilder()
   *           .setWriteOffset(-1559543565)
   *           .setObjectChecksums(ObjectChecksums.newBuilder().build())
   *           .setFinishWrite(true)
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   requestObserver.onNext(request);
   * }
   * }</pre>
   */
  public final ClientStreamingCallable<InsertObjectRequest, Object> insertObjectCallable() {
    return stub.insertObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of objects matching the criteria.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListObjectsRequest request =
   *       ListObjectsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setDelimiter("delimiter-250518009")
   *           .setIncludeTrailingDelimiter(true)
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setVersions(true)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListObjectsResponse response = storageClient.listObjects(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListObjectsResponse listObjects(ListObjectsRequest request) {
    return listObjectsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of objects matching the criteria.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListObjectsRequest request =
   *       ListObjectsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setDelimiter("delimiter-250518009")
   *           .setIncludeTrailingDelimiter(true)
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setVersions(true)
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListObjectsResponse> future =
   *       storageClient.listObjectsCallable().futureCall(request);
   *   // Do something.
   *   ListObjectsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListObjectsRequest, ListObjectsResponse> listObjectsCallable() {
    return stub.listObjectsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Rewrites a source object to a destination object. Optionally overrides metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   RewriteObjectRequest request =
   *       RewriteObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMaxBytesRewrittenPerCall(1178170730)
   *           .setRewriteToken("rewriteToken80654285")
   *           .setSourceBucket("sourceBucket841604581")
   *           .setSourceObject("sourceObject1196439354")
   *           .setSourceGeneration(1232209852)
   *           .setObject(Object.newBuilder().build())
   *           .setCopySourceEncryptionAlgorithm("copySourceEncryptionAlgorithm-1524952548")
   *           .setCopySourceEncryptionKey("copySourceEncryptionKey1199243724")
   *           .setCopySourceEncryptionKeySha256("copySourceEncryptionKeySha256544611091")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   RewriteResponse response = storageClient.rewriteObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final RewriteResponse rewriteObject(RewriteObjectRequest request) {
    return rewriteObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Rewrites a source object to a destination object. Optionally overrides metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   RewriteObjectRequest request =
   *       RewriteObjectRequest.newBuilder()
   *           .setDestinationBucket("destinationBucket-2116438120")
   *           .setDestinationObject("destinationObject-1761603347")
   *           .setDestinationKmsKeyName("destinationKmsKeyName-559122521")
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfSourceMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMaxBytesRewrittenPerCall(1178170730)
   *           .setRewriteToken("rewriteToken80654285")
   *           .setSourceBucket("sourceBucket841604581")
   *           .setSourceObject("sourceObject1196439354")
   *           .setSourceGeneration(1232209852)
   *           .setObject(Object.newBuilder().build())
   *           .setCopySourceEncryptionAlgorithm("copySourceEncryptionAlgorithm-1524952548")
   *           .setCopySourceEncryptionKey("copySourceEncryptionKey1199243724")
   *           .setCopySourceEncryptionKeySha256("copySourceEncryptionKeySha256544611091")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<RewriteResponse> future = storageClient.rewriteObjectCallable().futureCall(request);
   *   // Do something.
   *   RewriteResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<RewriteObjectRequest, RewriteResponse> rewriteObjectCallable() {
    return stub.rewriteObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Starts a resumable write. How long the write operation remains valid, and what happens when the
   * write operation becomes invalid, are service-dependent.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   StartResumableWriteRequest request =
   *       StartResumableWriteRequest.newBuilder()
   *           .setInsertObjectSpec(InsertObjectSpec.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   StartResumableWriteResponse response = storageClient.startResumableWrite(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final StartResumableWriteResponse startResumableWrite(StartResumableWriteRequest request) {
    return startResumableWriteCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Starts a resumable write. How long the write operation remains valid, and what happens when the
   * write operation becomes invalid, are service-dependent.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   StartResumableWriteRequest request =
   *       StartResumableWriteRequest.newBuilder()
   *           .setInsertObjectSpec(InsertObjectSpec.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<StartResumableWriteResponse> future =
   *       storageClient.startResumableWriteCallable().futureCall(request);
   *   // Do something.
   *   StartResumableWriteResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable() {
    return stub.startResumableWriteCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Determines the `committed_size` for an object that is being written, which can then be used as
   * the `write_offset` for the next `Write()` call.
   *
   * <p>If the object does not exist (i.e., the object has been deleted, or the first `Write()` has
   * not yet reached the service), this method returns the error `NOT_FOUND`.
   *
   * <p>The client &#42;&#42;may&#42;&#42; call `QueryWriteStatus()` at any time to determine how
   * much data has been processed for this object. This is useful if the client is buffering data
   * and needs to know which data can be safely evicted. For any sequence of `QueryWriteStatus()`
   * calls for a given object name, the sequence of returned `committed_size` values will be
   * non-decreasing.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   QueryWriteStatusRequest request =
   *       QueryWriteStatusRequest.newBuilder()
   *           .setUploadId("uploadId1563990780")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   QueryWriteStatusResponse response = storageClient.queryWriteStatus(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final QueryWriteStatusResponse queryWriteStatus(QueryWriteStatusRequest request) {
    return queryWriteStatusCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Determines the `committed_size` for an object that is being written, which can then be used as
   * the `write_offset` for the next `Write()` call.
   *
   * <p>If the object does not exist (i.e., the object has been deleted, or the first `Write()` has
   * not yet reached the service), this method returns the error `NOT_FOUND`.
   *
   * <p>The client &#42;&#42;may&#42;&#42; call `QueryWriteStatus()` at any time to determine how
   * much data has been processed for this object. This is useful if the client is buffering data
   * and needs to know which data can be safely evicted. For any sequence of `QueryWriteStatus()`
   * calls for a given object name, the sequence of returned `committed_size` values will be
   * non-decreasing.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   QueryWriteStatusRequest request =
   *       QueryWriteStatusRequest.newBuilder()
   *           .setUploadId("uploadId1563990780")
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<QueryWriteStatusResponse> future =
   *       storageClient.queryWriteStatusCallable().futureCall(request);
   *   // Do something.
   *   QueryWriteStatusResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable() {
    return stub.queryWriteStatusCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an object's metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchObjectRequest request =
   *       PatchObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Object.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Object response = storageClient.patchObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Object patchObject(PatchObjectRequest request) {
    return patchObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an object's metadata.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   PatchObjectRequest request =
   *       PatchObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Object.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Object> future = storageClient.patchObjectCallable().futureCall(request);
   *   // Do something.
   *   Object response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<PatchObjectRequest, Object> patchObjectCallable() {
    return stub.patchObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an object's metadata. Equivalent to PatchObject, but always replaces all mutatable
   * fields of the bucket with new values, reverting all unspecified fields to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateObjectRequest request =
   *       UpdateObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Object.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Object response = storageClient.updateObject(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Object updateObject(UpdateObjectRequest request) {
    return updateObjectCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an object's metadata. Equivalent to PatchObject, but always replaces all mutatable
   * fields of the bucket with new values, reverting all unspecified fields to their default values.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateObjectRequest request =
   *       UpdateObjectRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setObject("object-1023368385")
   *           .setGeneration(305703192)
   *           .setIfGenerationMatch(Int64Value.newBuilder().build())
   *           .setIfGenerationNotMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationMatch(Int64Value.newBuilder().build())
   *           .setIfMetagenerationNotMatch(Int64Value.newBuilder().build())
   *           .setMetadata(Object.newBuilder().build())
   *           .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Object> future = storageClient.updateObjectCallable().futureCall(request);
   *   // Do something.
   *   Object response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateObjectRequest, Object> updateObjectCallable() {
    return stub.updateObjectCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets the IAM policy for the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetIamPolicyRequest request =
   *       GetIamPolicyRequest.newBuilder()
   *           .setIamRequest(GetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Policy response = storageClient.getObjectIamPolicy(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Policy getObjectIamPolicy(GetIamPolicyRequest request) {
    return getObjectIamPolicyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets the IAM policy for the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetIamPolicyRequest request =
   *       GetIamPolicyRequest.newBuilder()
   *           .setIamRequest(GetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Policy> future = storageClient.getObjectIamPolicyCallable().futureCall(request);
   *   // Do something.
   *   Policy response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetIamPolicyRequest, Policy> getObjectIamPolicyCallable() {
    return stub.getObjectIamPolicyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an IAM policy for the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   SetIamPolicyRequest request =
   *       SetIamPolicyRequest.newBuilder()
   *           .setIamRequest(SetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Policy response = storageClient.setObjectIamPolicy(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Policy setObjectIamPolicy(SetIamPolicyRequest request) {
    return setObjectIamPolicyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates an IAM policy for the specified object.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   SetIamPolicyRequest request =
   *       SetIamPolicyRequest.newBuilder()
   *           .setIamRequest(SetIamPolicyRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Policy> future = storageClient.setObjectIamPolicyCallable().futureCall(request);
   *   // Do something.
   *   Policy response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<SetIamPolicyRequest, Policy> setObjectIamPolicyCallable() {
    return stub.setObjectIamPolicyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Tests a set of permissions on the given object to see which, if any, are held by the caller.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   TestIamPermissionsRequest request =
   *       TestIamPermissionsRequest.newBuilder()
   *           .setIamRequest(TestIamPermissionsRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   TestIamPermissionsResponse response = storageClient.testObjectIamPermissions(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final TestIamPermissionsResponse testObjectIamPermissions(
      TestIamPermissionsRequest request) {
    return testObjectIamPermissionsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Tests a set of permissions on the given object to see which, if any, are held by the caller.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   TestIamPermissionsRequest request =
   *       TestIamPermissionsRequest.newBuilder()
   *           .setIamRequest(TestIamPermissionsRequest.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<TestIamPermissionsResponse> future =
   *       storageClient.testObjectIamPermissionsCallable().futureCall(request);
   *   // Do something.
   *   TestIamPermissionsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse>
      testObjectIamPermissionsCallable() {
    return stub.testObjectIamPermissionsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Watch for changes on all objects in a bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   WatchAllObjectsRequest request =
   *       WatchAllObjectsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setVersions(true)
   *           .setDelimiter("delimiter-250518009")
   *           .setMaxResults(1128457243)
   *           .setPrefix("prefix-980110702")
   *           .setIncludeTrailingDelimiter(true)
   *           .setPageToken("pageToken873572522")
   *           .setChannel(Channel.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   Channel response = storageClient.watchAllObjects(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Channel watchAllObjects(WatchAllObjectsRequest request) {
    return watchAllObjectsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Watch for changes on all objects in a bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   WatchAllObjectsRequest request =
   *       WatchAllObjectsRequest.newBuilder()
   *           .setBucket("bucket-1378203158")
   *           .setVersions(true)
   *           .setDelimiter("delimiter-250518009")
   *           .setMaxResults(1128457243)
   *           .setPrefix("prefix-980110702")
   *           .setIncludeTrailingDelimiter(true)
   *           .setPageToken("pageToken873572522")
   *           .setChannel(Channel.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Channel> future = storageClient.watchAllObjectsCallable().futureCall(request);
   *   // Do something.
   *   Channel response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<WatchAllObjectsRequest, Channel> watchAllObjectsCallable() {
    return stub.watchAllObjectsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves the name of a project's Google Cloud Storage service account.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetProjectServiceAccountRequest request =
   *       GetProjectServiceAccountRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ServiceAccount response = storageClient.getServiceAccount(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ServiceAccount getServiceAccount(GetProjectServiceAccountRequest request) {
    return getServiceAccountCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves the name of a project's Google Cloud Storage service account.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetProjectServiceAccountRequest request =
   *       GetProjectServiceAccountRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ServiceAccount> future =
   *       storageClient.getServiceAccountCallable().futureCall(request);
   *   // Do something.
   *   ServiceAccount response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetProjectServiceAccountRequest, ServiceAccount>
      getServiceAccountCallable() {
    return stub.getServiceAccountCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new HMAC key for the given service account.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   CreateHmacKeyRequest request =
   *       CreateHmacKeyRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setServiceAccountEmail("serviceAccountEmail1825953988")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   CreateHmacKeyResponse response = storageClient.createHmacKey(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final CreateHmacKeyResponse createHmacKey(CreateHmacKeyRequest request) {
    return createHmacKeyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new HMAC key for the given service account.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   CreateHmacKeyRequest request =
   *       CreateHmacKeyRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setServiceAccountEmail("serviceAccountEmail1825953988")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<CreateHmacKeyResponse> future =
   *       storageClient.createHmacKeyCallable().futureCall(request);
   *   // Do something.
   *   CreateHmacKeyResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CreateHmacKeyRequest, CreateHmacKeyResponse> createHmacKeyCallable() {
    return stub.createHmacKeyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes a given HMAC key. Key must be in an INACTIVE state.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteHmacKeyRequest request =
   *       DeleteHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   storageClient.deleteHmacKey(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteHmacKey(DeleteHmacKeyRequest request) {
    deleteHmacKeyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes a given HMAC key. Key must be in an INACTIVE state.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   DeleteHmacKeyRequest request =
   *       DeleteHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<Empty> future = storageClient.deleteHmacKeyCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteHmacKeyRequest, Empty> deleteHmacKeyCallable() {
    return stub.deleteHmacKeyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets an existing HMAC key metadata for the given id.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetHmacKeyRequest request =
   *       GetHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   HmacKeyMetadata response = storageClient.getHmacKey(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final HmacKeyMetadata getHmacKey(GetHmacKeyRequest request) {
    return getHmacKeyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Gets an existing HMAC key metadata for the given id.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   GetHmacKeyRequest request =
   *       GetHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<HmacKeyMetadata> future = storageClient.getHmacKeyCallable().futureCall(request);
   *   // Do something.
   *   HmacKeyMetadata response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetHmacKeyRequest, HmacKeyMetadata> getHmacKeyCallable() {
    return stub.getHmacKeyCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists HMAC keys under a given project with the additional filters provided.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListHmacKeysRequest request =
   *       ListHmacKeysRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setServiceAccountEmail("serviceAccountEmail1825953988")
   *           .setShowDeletedKeys(true)
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ListHmacKeysResponse response = storageClient.listHmacKeys(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListHmacKeysResponse listHmacKeys(ListHmacKeysRequest request) {
    return listHmacKeysCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists HMAC keys under a given project with the additional filters provided.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   ListHmacKeysRequest request =
   *       ListHmacKeysRequest.newBuilder()
   *           .setProjectId("projectId-894832108")
   *           .setServiceAccountEmail("serviceAccountEmail1825953988")
   *           .setShowDeletedKeys(true)
   *           .setMaxResults(1128457243)
   *           .setPageToken("pageToken873572522")
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<ListHmacKeysResponse> future =
   *       storageClient.listHmacKeysCallable().futureCall(request);
   *   // Do something.
   *   ListHmacKeysResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ListHmacKeysRequest, ListHmacKeysResponse> listHmacKeysCallable() {
    return stub.listHmacKeysCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a given HMAC key state between ACTIVE and INACTIVE.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateHmacKeyRequest request =
   *       UpdateHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setMetadata(HmacKeyMetadata.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   HmacKeyMetadata response = storageClient.updateHmacKey(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final HmacKeyMetadata updateHmacKey(UpdateHmacKeyRequest request) {
    return updateHmacKeyCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates a given HMAC key state between ACTIVE and INACTIVE.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (StorageClient storageClient = StorageClient.create()) {
   *   UpdateHmacKeyRequest request =
   *       UpdateHmacKeyRequest.newBuilder()
   *           .setAccessId("accessId-2146437729")
   *           .setProjectId("projectId-894832108")
   *           .setMetadata(HmacKeyMetadata.newBuilder().build())
   *           .setCommonRequestParams(CommonRequestParams.newBuilder().build())
   *           .build();
   *   ApiFuture<HmacKeyMetadata> future = storageClient.updateHmacKeyCallable().futureCall(request);
   *   // Do something.
   *   HmacKeyMetadata response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateHmacKeyRequest, HmacKeyMetadata> updateHmacKeyCallable() {
    return stub.updateHmacKeyCallable();
  }

  @Override
  public final void close() {
    stub.close();
  }

  @Override
  public void shutdown() {
    stub.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return stub.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return stub.isTerminated();
  }

  @Override
  public void shutdownNow() {
    stub.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return stub.awaitTermination(duration, unit);
  }
}
