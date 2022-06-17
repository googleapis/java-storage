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
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.BaseService;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.PostPolicyV4.PostConditionsV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StorageClient.ListBucketsPage;
import com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import com.google.storage.v2.StorageClient.ListHmacKeysPage;
import com.google.storage.v2.StorageClient.ListHmacKeysPagedResponse;
import com.google.storage.v2.StorageClient.ListObjectsPage;
import com.google.storage.v2.StorageClient.ListObjectsPagedResponse;
import com.google.storage.v2.WriteObjectResponse;
import com.google.storage.v2.WriteObjectSpec;
import com.google.storage.v2.stub.GrpcStorageStub;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

final class GrpcStorageImpl extends BaseService<StorageOptions> implements Storage {

  private static final byte[] ZERO_BYTES = new byte[0];
  private static final Set<OpenOption> READ_OPS = ImmutableSet.of(StandardOpenOption.READ);
  private static final Set<OpenOption> WRITE_OPS =
      ImmutableSet.of(
          StandardOpenOption.WRITE,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);

  private final GrpcStorageStub grpcStorageStub;
  private final GrpcConversions codecs;
  private final GrpcRetryAlgorithmManager retryAlgorithmManager;
  private final SyntaxDecoders syntaxDecoders;

  GrpcStorageImpl(GrpcStorageOptions options, GrpcStorageStub grpcStorageStub) {
    super(options);
    this.grpcStorageStub = grpcStorageStub;
    this.codecs = Conversions.grpc();
    this.retryAlgorithmManager = options.getRetryAlgorithmManager();
    this.syntaxDecoders = new SyntaxDecoders();
  }

  @Override
  public void close() throws Exception {
    try (GrpcStorageStub s = grpcStorageStub) {
      s.shutdownNow();
    }
  }

  @Override
  public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    com.google.storage.v2.Bucket bucket =
        com.google.storage.v2.Bucket.newBuilder()
            .setName(BucketName.format(this.getOptions().getProjectId(), bucketInfo.getName()))
            .setProject(this.getOptions().getProjectId())
            .build();
    CreateBucketRequest.Builder bucketRequestBuilder =
        CreateBucketRequest.newBuilder().setBucket(bucket);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.PREDEFINED_ACL),
        bucketRequestBuilder::setPredefinedAcl);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.PREDEFINED_DEFAULT_OBJECT_ACL),
        bucketRequestBuilder::setPredefinedDefaultObjectAcl);
    CreateBucketRequest req = bucketRequestBuilder.build();
    // TODO(frankyn): Do we care about projection because Apiary uses FULL for projection? Missing
    // projection=full
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> grpcStorageStub.createBucketCallable().call(req),
        syntaxDecoders.bucket);
  }

  @Override
  public Blob create(BlobInfo blobInfo, BlobTargetOption... options) {
    return create(blobInfo, null, options);
  }

  @Override
  public Blob create(BlobInfo blobInfo, byte[] content, BlobTargetOption... options) {
    content = firstNonNull(content, ZERO_BYTES);
    return create(blobInfo, content, 0, content.length, options);
  }

  @Override
  public Blob create(
      BlobInfo blobInfo, byte[] content, int offset, int length, BlobTargetOption... options) {
    requireNonNull(blobInfo, "blobInfo must be non null");
    requireNonNull(content, "content must be non null");
    BlobWriteOption[] translate = translate(options);
    try {
      ApiFuture<WriteObjectResponse> f;
      try (GrpcBlobWriteChannel c = writer(blobInfo, translate)) {
        c.write(ByteBuffer.wrap(content, offset, length));
        f = c.getResults();
      }
      WriteObjectResponse response = ApiExceptions.callAndTranslateApiException(f);
      return syntaxDecoders.blob.decode(response.getResource());
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Blob create(BlobInfo blobInfo, InputStream content, BlobWriteOption... options) {
    try {
      return createFrom(blobInfo, content, options);
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, BlobWriteOption... options)
      throws IOException {
    requireNonNull(path, "path must be non null");
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }
    try (SeekableByteChannel src = Files.newByteChannel(path, READ_OPS)) {
      return internalCreate(blobInfo, src, options);
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(path, "path must be non null");
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }
    try (SeekableByteChannel src = Files.newByteChannel(path, READ_OPS)) {
      return internalCreate(blobInfo, src, bufferSize, options);
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    ReadableByteChannel src =
        Channels.newChannel(firstNonNull(content, new ByteArrayInputStream(ZERO_BYTES)));
    return internalCreate(blobInfo, src, options);
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream content, int bufferSize, BlobWriteOption... options)
      throws IOException {
    ReadableByteChannel src =
        Channels.newChannel(firstNonNull(content, new ByteArrayInputStream(ZERO_BYTES)));
    return internalCreate(blobInfo, src, bufferSize, options);
  }

  @Override
  public Bucket get(String bucket, BucketGetOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GetBucketRequest.Builder bucketRequestBuilder = GetBucketRequest.newBuilder().setName(bucket);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        bucketRequestBuilder::setIfMetagenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        bucketRequestBuilder::setIfMetagenerationNotMatch);
    GetBucketRequest req = bucketRequestBuilder.build();
    // TODO(frankyn): Do we care about projection because Apiary uses FULL for projection? Missing
    // projection=full
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> grpcStorageStub.getBucketCallable().call(req),
        syntaxDecoders.bucket);
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
    GetObjectRequest req = getObjectRequestBuilder.build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> grpcStorageStub.getObjectCallable().call(req),
        syntaxDecoders.blob);
  }

  @Override
  public Blob get(BlobId blob) {
    return get(blob, new BlobGetOption[0]);
  }

  @Override
  public Page<Bucket> list(BucketListOption... options) {
    UnaryCallable<ListBucketsRequest, ListBucketsPagedResponse> listBucketsCallable =
        grpcStorageStub.listBucketsPagedCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    String projectId = (String) optionsMap.get(StorageRpc.Option.PROJECT_ID);
    if (projectId == null) {
      projectId = this.getOptions().getProjectId();
    }
    ListBucketsRequest.Builder builder = ListBucketsRequest.newBuilder().setParent(projectId);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.MAX_RESULTS), Long::intValue, builder::setPageSize);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.PAGE_TOKEN), builder::setPageToken);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.PREFIX), builder::setPrefix);
    // TODO(sydmunro): StorageRpc.Option.Fields
    // TODO(sydmunro): User Project
    ListBucketsPagedResponse call = listBucketsCallable.call(builder.build());
    ListBucketsPage page = call.getPage();
    return new TransformingPageDecorator<>(page, syntaxDecoders.bucket);
  }

  @Override
  public Page<Blob> list(String bucket, BlobListOption... options) {
    UnaryCallable<ListObjectsRequest, ListObjectsPagedResponse> listObjectsCallable =
        grpcStorageStub.listObjectsPagedCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    ListObjectsRequest.Builder builder = ListObjectsRequest.newBuilder().setParent(bucket);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.MAX_RESULTS), Long::intValue, builder::setPageSize);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.PAGE_TOKEN), builder::setPageToken);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.PREFIX), builder::setPrefix);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.DELIMITER), builder::setDelimiter);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.START_OFF_SET), builder::setLexicographicStart);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.END_OFF_SET), builder::setLexicographicEnd);
    // TODO(sydmunro) StorageRpc.Option.Fields
    ListObjectsPagedResponse call = listObjectsCallable.call(builder.build());
    ListObjectsPage page = call.getPage();
    return new TransformingPageDecorator<>(page, syntaxDecoders.blob);
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
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    DeleteBucketRequest.Builder bucketRequestBuilder =
        DeleteBucketRequest.newBuilder()
            .setName(BucketName.format(this.getOptions().getProjectId(), bucket));
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        bucketRequestBuilder::setIfMetagenerationNotMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        bucketRequestBuilder::setIfMetagenerationNotMatch);
    DeleteBucketRequest req = bucketRequestBuilder.build();
    try {
      Retrying.run(
          getOptions(),
          retryAlgorithmManager.getFor(req),
          () -> grpcStorageStub.deleteBucketCallable().call(req),
          Decoder.identity());
      return true;
    } catch (ApiException e) {
      // TODO: We should throw a StorageException instead of ApiException when making the
      // deleteBucketCallable().call(req)
      return false;
    }
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
    return readAllBytes(BlobId.of(bucket, blob), options);
  }

  @Override
  public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ReadChannel r = reader(blob, options);
        WritableByteChannel w = Channels.newChannel(baos)) {
      ByteStreams.copy(r, w);
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
    return baos.toByteArray();
  }

  @Override
  public StorageBatch batch() {
    return todo();
  }

  @Override
  public GrpcBlobReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
    return reader(BlobId.of(bucket, blob), options);
  }

  @Override
  public GrpcBlobReadChannel reader(BlobId blob, BlobSourceOption... options) {
    Object pb = codecs.blobId().encode(blob);
    return new GrpcBlobReadChannel(grpcStorageStub.readObjectCallable(), () -> pb);
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {
    try (WritableByteChannel out = Files.newByteChannel(path, WRITE_OPS);
        GrpcBlobReadChannel in = reader(blob, options)) {
      ByteStreams.copy(in, out);
    } catch (IOException e) {
      throw new StorageException(e);
    }
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {
    try (WritableByteChannel out = Channels.newChannel(outputStream);
        GrpcBlobReadChannel in = reader(blob, options)) {
      ByteStreams.copy(in, out);
    } catch (IOException e) {
      throw new StorageException(e);
    }
  }

  @Override
  public GrpcBlobWriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    Object object = codecs.blobInfo().encode(blobInfo);
    WriteObjectSpec writeObjectSpec =
        WriteObjectSpec.newBuilder()
            .setResource(
                object
                    .toBuilder()
                    // required if the data is changing
                    .clearChecksums()
                    // trimmed to shave payload size
                    .clearAcl()
                    .clearGeneration()
                    .clearMetageneration()
                    .clearSize()
                    .clearCreateTime()
                    .clearUpdateTime()
                    .build())
            .build();
    StartResumableWriteRequest startResumableWriteRequest =
        StartResumableWriteRequest.newBuilder().setWriteObjectSpec(writeObjectSpec).build();
    return new GrpcBlobWriteChannel(
        grpcStorageStub.writeObjectCallable(),
        () ->
            ApiFutures.transform(
                grpcStorageStub
                    .startResumableWriteCallable()
                    .futureCall(startResumableWriteRequest),
                (resp) -> new ResumableWrite(startResumableWriteRequest, resp),
                MoreExecutors.directExecutor()));
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
    UnaryCallable<ListHmacKeysRequest, ListHmacKeysPagedResponse> listHmacKeysCallable =
        grpcStorageStub.listHmacKeysPagedCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    String projectId = (String) optionsMap.get(StorageRpc.Option.PROJECT_ID);
    if (projectId == null) {
      projectId = this.getOptions().getProjectId();
    }
    ListHmacKeysRequest.Builder builder = ListHmacKeysRequest.newBuilder().setProject(projectId);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.SERVICE_ACCOUNT_EMAIL),
        builder::setServiceAccountEmail);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.MAX_RESULTS), Long::intValue, builder::setPageSize);
    ifNonNull((String) optionsMap.get(StorageRpc.Option.PAGE_TOKEN), builder::setPageToken);
    ifNonNull(
        (Boolean) optionsMap.get(StorageRpc.Option.SHOW_DELETED_KEYS), builder::setShowDeletedKeys);
    ListHmacKeysPagedResponse call = listHmacKeysCallable.call(builder.build());
    ListHmacKeysPage page = call.getPage();
    return new TransformingPageDecorator<>(page, codecs.hmacKeyMetadata());
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
    Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> {
          grpcStorageStub.deleteHmacKeyCallable().call(req);
          return null;
        },
        Decoder.identity());
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
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> grpcStorageStub.getServiceAccountCallable().call(req),
        codecs.serviceAccount());
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
  public GrpcStorageOptions getOptions() {
    return (GrpcStorageOptions) super.getOptions();
  }

  private BlobWriteOption[] translate(BlobTargetOption[] options) {
    return todo();
  }

  private Blob internalCreate(
      BlobInfo blobInfo, ReadableByteChannel src, BlobWriteOption... options) throws IOException {
    requireNonNull(blobInfo, "blobInfo must be non null");
    ApiFuture<WriteObjectResponse> f;
    try (GrpcBlobWriteChannel c = writer(blobInfo, options)) {
      ByteStreams.copy(src, c);
      f = c.getResults();
    }
    WriteObjectResponse response = ApiExceptions.callAndTranslateApiException(f);
    return syntaxDecoders.blob.decode(response.getResource());
  }

  private Blob internalCreate(
      BlobInfo blobInfo, ReadableByteChannel src, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(blobInfo, "blobInfo must be non null");
    ApiFuture<WriteObjectResponse> f;
    try (GrpcBlobWriteChannel c = writer(blobInfo, options)) {
      c.setChunkSize(bufferSize);
      ByteStreams.copy(src, c);
      f = c.getResults();
    }
    WriteObjectResponse response = ApiExceptions.callAndTranslateApiException(f);
    return syntaxDecoders.blob.decode(response.getResource());
  }

  /** Bind some decoders for our "Syntax" classes to this instance of GrpcStorageImpl */
  private final class SyntaxDecoders {

    final Decoder<Object, Blob> blob =
        o -> codecs.blobInfo().decode(o).asBlob(GrpcStorageImpl.this);
    final Decoder<com.google.storage.v2.Bucket, Bucket> bucket =
        b -> codecs.bucketInfo().decode(b).asBucket(GrpcStorageImpl.this);
  }

  static final class TransformingPageDecorator<
          RequestT,
          ResponseT,
          ResourceT,
          PageT extends AbstractPage<RequestT, ResponseT, ResourceT, PageT>,
          ModelT>
      implements Page<ModelT> {

    private final PageT page;
    private final Decoder<ResourceT, ModelT> translator;

    public TransformingPageDecorator(PageT page, Decoder<ResourceT, ModelT> translator) {
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
      return new TransformingPageDecorator<>(page.getNextPage(), translator);
    }

    @Override
    public Iterable<ModelT> iterateAll() {
      return () -> {
        final Iterator<ResourceT> iter = page.iterateAll().iterator();
        return new TransformingIterator(iter);
      };
    }

    @Override
    public Iterable<ModelT> getValues() {
      return () -> {
        final Iterator<ResourceT> inter = page.getValues().iterator();
        return new TransformingIterator(inter);
      };
    }

    private class TransformingIterator implements Iterator<ModelT> {

      private final Iterator<ResourceT> iter;

      public TransformingIterator(Iterator<ResourceT> iter) {
        this.iter = iter;
      }

      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public ModelT next() {
        ResourceT next = iter.next();
        return translator.decode(next);
      }
    }
  }
}
