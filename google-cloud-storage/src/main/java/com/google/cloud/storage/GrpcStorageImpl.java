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

import static com.google.cloud.storage.Utils.ifNonNull;
import static com.google.cloud.storage.Utils.todo;

import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.BaseService;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.PostPolicyV4.PostConditionsV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.StorageClient.ListBucketsPage;
import com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import com.google.storage.v2.StorageClient.ListHmacKeysPage;
import com.google.storage.v2.StorageClient.ListHmacKeysPagedResponse;
import com.google.storage.v2.StorageClient.ListObjectsPage;
import com.google.storage.v2.StorageClient.ListObjectsPagedResponse;
import com.google.storage.v2.stub.GrpcStorageStub;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

final class GrpcStorageImpl extends BaseService<StorageOptions> implements Storage {

  private final GrpcStorageStub grpcStorageStub;
  private final GrpcConversions codecs;
  private final RetryAlgorithmManager retryAlgorithmManager;

  GrpcStorageImpl(GrpcStorageOptions options, GrpcStorageStub grpcStorageStub) {
    super(options);
    this.grpcStorageStub = grpcStorageStub;
    this.codecs = Conversions.grpc();
    this.retryAlgorithmManager = options.getRetryAlgorithmManager();
  }

  @Override
  public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
    return todo();
  }

  @Override
  public Blob create(BlobInfo blobInfo, BlobTargetOption... options) {
    return todo();
  }

  @Override
  public Blob create(BlobInfo blobInfo, byte[] content, BlobTargetOption... options) {
    return todo();
  }

  @Override
  public Blob create(
      BlobInfo blobInfo, byte[] content, int offset, int length, BlobTargetOption... options) {
    return todo();
  }

  @Override
  public Blob create(BlobInfo blobInfo, InputStream content, BlobWriteOption... options) {
    return todo();
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, BlobWriteOption... options)
      throws IOException {
    return todo();
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    return todo();
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    return todo();
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream content, int bufferSize, BlobWriteOption... options)
      throws IOException {
    return todo();
  }

  @Override
  public Bucket get(String bucket, BucketGetOption... options) {
    UnaryCallable<GetBucketRequest, com.google.storage.v2.Bucket> bucketCallable =
        grpcStorageStub.getBucketCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GetBucketRequest.Builder bucketRequestBuilder = GetBucketRequest.newBuilder().setName(bucket);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        bucketRequestBuilder::setIfMetagenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        bucketRequestBuilder::setIfMetagenerationNotMatch);
    // TODO(frankyn): Do we care about projection because Apiary uses FULL for projection? Missing
    // projection=full
    com.google.storage.v2.Bucket protoBucket = bucketCallable.call(bucketRequestBuilder.build());
    return Conversions.grpc().bucketInfo().decode(protoBucket).asBucket(this);
  }

  @Override
  public Bucket lockRetentionPolicy(BucketInfo bucket, BucketTargetOption... options) {
    return todo();
  }

  @Override
  public Blob get(String bucket, String blob, BlobGetOption... options) {
    return get(BlobId.of(bucket, blob), options);
  }

  @Override
  public Blob get(BlobId blob, BlobGetOption... options) {
    UnaryCallable<GetObjectRequest, com.google.storage.v2.Object> unaryCallable =
        grpcStorageStub.getObjectCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GetObjectRequest.Builder getObjectRequestBuilder =
        GetObjectRequest.newBuilder().setBucket(blob.getBucket()).setObject(blob.getName());
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        getObjectRequestBuilder::setIfMetagenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        getObjectRequestBuilder::setIfMetagenerationNotMatch);
    // TODO(sydmunro) StorageRpc.Option.Fields
    com.google.storage.v2.Object object = unaryCallable.call(getObjectRequestBuilder.build());
    BlobInfo blobInfo = codecs.blobInfo().decode(object);
    return blobInfo.asBlob(this);
  }

  @Override
  public Blob get(BlobId blob) {
    return get(blob, new BlobGetOption[0]);
  }

  @Override
  public Page<Bucket> list(BucketListOption... options) {
    UnaryCallable<ListBucketsRequest, ListBucketsPagedResponse>
        listBucketsRequestListBucketsPagedResponseUnaryCallable =
            grpcStorageStub.listBucketsPagedCallable();
    // TODO: Actually construct request
    ListBucketsRequest req = ListBucketsRequest.getDefaultInstance();
    ListBucketsPagedResponse call =
        listBucketsRequestListBucketsPagedResponseUnaryCallable.call(req);
    ListBucketsPage page = call.getPage();
    Function<com.google.storage.v2.Bucket, BucketInfo> decode = codecs.bucketInfo()::decode;

    Function<com.google.storage.v2.Bucket, Bucket> translator =
        decode.andThen(bucketInfo -> bucketInfo.asBucket(this));
    return new TransformPageDecorator<>(page, translator);
  }

  @Override
  public Page<Blob> list(String bucket, BlobListOption... options) {
    UnaryCallable<ListObjectsRequest, ListObjectsPagedResponse>
        listObjectsPagedResponseUnaryCallable = grpcStorageStub.listObjectsPagedCallable();
    // TODO: Actually construct request
    ListObjectsRequest req = ListObjectsRequest.getDefaultInstance();
    ListObjectsPagedResponse call = listObjectsPagedResponseUnaryCallable.call(req);
    ListObjectsPage page = call.getPage();
    Function<com.google.storage.v2.Object, BlobInfo> decode = codecs.blobInfo()::decode;
    Function<com.google.storage.v2.Object, Blob> translator =
        decode.andThen(blobInfo -> blobInfo.asBlob(this));
    return new TransformPageDecorator<>(page, translator);
  }

  @Override
  public Bucket update(BucketInfo bucketInfo, BucketTargetOption... options) {
    return todo();
  }

  @Override
  public Blob update(BlobInfo blobInfo, BlobTargetOption... options) {
    return todo();
  }

  @Override
  public Blob update(BlobInfo blobInfo) {
    return todo();
  }

  @Override
  public boolean delete(String bucket, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public boolean delete(String bucket, String blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public boolean delete(BlobId blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public boolean delete(BlobId blob) {
    return todo();
  }

  @Override
  public Blob compose(ComposeRequest composeRequest) {
    return todo();
  }

  @Override
  public CopyWriter copy(CopyRequest copyRequest) {
    return todo();
  }

  @Override
  public byte[] readAllBytes(String bucket, String blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public StorageBatch batch() {
    return todo();
  }

  @Override
  public ReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public ReadChannel reader(BlobId blob, BlobSourceOption... options) {
    return todo();
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {
    todo();
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {
    todo();
  }

  @Override
  public WriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    return todo();
  }

  @Override
  public WriteChannel writer(URL signedURL) {
    return todo();
  }

  @Override
  public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
    return todo();
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    return todo();
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostPolicyV4Option... options) {
    return todo();
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    return todo();
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo, long duration, TimeUnit unit, PostPolicyV4Option... options) {
    return todo();
  }

  @Override
  public List<Blob> get(BlobId... blobIds) {
    return todo();
  }

  @Override
  public List<Blob> get(Iterable<BlobId> blobIds) {
    return todo();
  }

  @Override
  public List<Blob> update(BlobInfo... blobInfos) {
    return todo();
  }

  @Override
  public List<Blob> update(Iterable<BlobInfo> blobInfos) {
    return todo();
  }

  @Override
  public List<Boolean> delete(BlobId... blobIds) {
    return todo();
  }

  @Override
  public List<Boolean> delete(Iterable<BlobId> blobIds) {
    return todo();
  }

  @Override
  public Acl getAcl(String bucket, Entity entity, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public Acl getAcl(String bucket, Entity entity) {
    return todo();
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity) {
    return todo();
  }

  @Override
  public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public Acl createAcl(String bucket, Acl acl) {
    return todo();
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl) {
    return todo();
  }

  @Override
  public List<Acl> listAcls(String bucket, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public List<Acl> listAcls(String bucket) {
    return todo();
  }

  @Override
  public Acl getDefaultAcl(String bucket, Entity entity) {
    return todo();
  }

  @Override
  public boolean deleteDefaultAcl(String bucket, Entity entity) {
    return todo();
  }

  @Override
  public Acl createDefaultAcl(String bucket, Acl acl) {
    return todo();
  }

  @Override
  public Acl updateDefaultAcl(String bucket, Acl acl) {
    return todo();
  }

  @Override
  public List<Acl> listDefaultAcls(String bucket) {
    return todo();
  }

  @Override
  public Acl getAcl(BlobId blob, Entity entity) {
    return todo();
  }

  @Override
  public boolean deleteAcl(BlobId blob, Entity entity) {
    return todo();
  }

  @Override
  public Acl createAcl(BlobId blob, Acl acl) {
    return todo();
  }

  @Override
  public Acl updateAcl(BlobId blob, Acl acl) {
    return todo();
  }

  @Override
  public List<Acl> listAcls(BlobId blob) {
    return todo();
  }

  @Override
  public HmacKey createHmacKey(ServiceAccount serviceAccount, CreateHmacKeyOption... options) {
    return todo();
  }

  @Override
  public Page<HmacKeyMetadata> listHmacKeys(ListHmacKeysOption... options) {
    UnaryCallable<ListHmacKeysRequest, ListHmacKeysPagedResponse>
        listBucketsRequestListHmacKeysPagedResponseUnaryCallable =
            grpcStorageStub.listHmacKeysPagedCallable();
    // TODO: Actually construct request
    ListHmacKeysRequest req = ListHmacKeysRequest.getDefaultInstance();
    ListHmacKeysPagedResponse call =
        listBucketsRequestListHmacKeysPagedResponseUnaryCallable.call(req);
    ListHmacKeysPage page = call.getPage();
    Function<com.google.storage.v2.HmacKeyMetadata, HmacKey.HmacKeyMetadata> decode =
        codecs.hmacKeyMetadata()::decode;
    return new TransformPageDecorator<>(page, decode);
  }

  @Override
  public HmacKeyMetadata getHmacKey(String accessId, GetHmacKeyOption... options) {
    return todo();
  }

  @Override
  public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, DeleteHmacKeyOption... options) {
    DeleteHmacKeyRequest req =
        DeleteHmacKeyRequest.newBuilder()
            .setAccessId(hmacKeyMetadata.getAccessId())
            .setProject(hmacKeyMetadata.getProjectId())
            .build();
    // TODO retries
    grpcStorageStub.deleteHmacKeyCallable().call(req);
  }

  @Override
  public HmacKeyMetadata updateHmacKeyState(
      HmacKeyMetadata hmacKeyMetadata, HmacKeyState state, UpdateHmacKeyOption... options) {
    return todo();
  }

  @Override
  public Policy getIamPolicy(String bucket, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public Policy setIamPolicy(String bucket, Policy policy, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public List<Boolean> testIamPermissions(
      String bucket, List<String> permissions, BucketSourceOption... options) {
    return todo();
  }

  @Override
  public ServiceAccount getServiceAccount(String projectId) {
    GetServiceAccountRequest req =
        GetServiceAccountRequest.newBuilder().setProject(projectId).build();
    com.google.storage.v2.ServiceAccount resp =
        grpcStorageStub.getServiceAccountCallable().call(req);
    return codecs.serviceAccount().decode(resp);
  }

  @Override
  public Notification createNotification(String bucket, NotificationInfo notificationInfo) {
    return todo();
  }

  @Override
  public Notification getNotification(String bucket, String notificationId) {
    return todo();
  }

  @Override
  public List<Notification> listNotifications(String bucket) {
    return todo();
  }

  @Override
  public boolean deleteNotification(String bucket, String notificationId) {
    return todo();
  }

  @Override
  public StorageOptions getOptions() {
    return todo();
  }

  private static class TransformPageDecorator<
          RequestT,
          ResponseT,
          ResourceT,
          PageT extends AbstractPage<RequestT, ResponseT, ResourceT, PageT>,
          ModelT>
      implements Page<ModelT> {

    private final PageT page;
    private final Function<ResourceT, ModelT> translator;

    public TransformPageDecorator(PageT page, Function<ResourceT, ModelT> translator) {
      this.page = page;
      this.translator = translator;
    }

    @Override
    public boolean hasNextPage() {
      return page.hasNextPage();
    }

    @Override
    public String getNextPageToken() {
      return page.getNextPageToken();
    }

    @Override
    public Page<ModelT> getNextPage() {
      return new TransformPageDecorator<>(page.getNextPage(), translator);
    }

    @Override
    public Iterable<ModelT> iterateAll() {
      return () -> {
        final Iterator<ResourceT> iter = page.iterateAll().iterator();
        return new ResourceIterator(iter);
      };
    }

    @Override
    public Iterable<ModelT> getValues() {
      return () -> {
        final Iterator<ResourceT> inter = page.getValues().iterator();
        return new ResourceIterator(inter);
      };
    }

    private class ResourceIterator implements Iterator<ModelT> {

      private final Iterator<ResourceT> iter;

      public ResourceIterator(Iterator<ResourceT> iter) {
        this.iter = iter;
      }

      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public ModelT next() {
        ResourceT next = iter.next();
        return translator.apply(next);
      }
    }
  }
}
