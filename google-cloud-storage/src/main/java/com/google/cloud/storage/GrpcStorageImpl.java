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
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.BaseService;
import com.google.cloud.Policy;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.BufferedWritableByteChannelSession.BufferedWritableByteChannel;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.PostPolicyV4.PostConditionsV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import com.google.storage.v2.BucketName;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.ComposeObjectRequest;
import com.google.storage.v2.ComposeObjectRequest.SourceObject;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.StorageClient.ListBucketsPage;
import com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import com.google.storage.v2.StorageClient.ListHmacKeysPage;
import com.google.storage.v2.StorageClient.ListHmacKeysPagedResponse;
import com.google.storage.v2.StorageClient.ListObjectsPage;
import com.google.storage.v2.StorageClient.ListObjectsPagedResponse;
import com.google.storage.v2.WriteObjectRequest;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GrpcStorageImpl extends BaseService<StorageOptions> implements Storage {

  private static final byte[] ZERO_BYTES = new byte[0];
  private static final Set<OpenOption> READ_OPS = ImmutableSet.of(StandardOpenOption.READ);
  private static final Set<OpenOption> WRITE_OPS =
      ImmutableSet.of(
          StandardOpenOption.WRITE,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
  static final int _256KiB = 256 * 1024;
  static final int _15MiB = 15 * 1024 * 1024;

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
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
        () -> grpcStorageStub.createBucketCallable().call(req, grpcCallContext),
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
    Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    WriteObjectRequest req = getWriteObjectRequestBuilder(blobInfo, optionsMap).build();
    try {
      UnbufferedWritableByteChannelSession<WriteObjectResponse> session =
          ResumableMedia.gapic()
              .write()
              .byteChannel(
                  grpcStorageStub.writeObjectCallable().withDefaultCallContext(grpcCallContext))
              .setByteStringStrategy(ByteStringStrategy.noCopy())
              .setHasher(Hasher.enabled())
              .direct()
              .unbuffered()
              .setRequest(req)
              .build();

      try (UnbufferedWritableByteChannel c = session.open()) {
        c.write(ByteBuffer.wrap(content, offset, length));
      }
      return getBlob(session.getResult());
    } catch (Exception e) {
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
    return createFrom(blobInfo, path, _15MiB, options);
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(path, "path must be non null");
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }

    BlobTargetOption[] translate = translate(options);
    // TODO: Why does optionMap not accept BlobWriteOption?
    Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(translate);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    WriteObjectRequest req = getWriteObjectRequestBuilder(blobInfo, optionsMap).build();

    GapicWritableByteChannelSessionBuilder channelSessionBuilder =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                grpcStorageStub.writeObjectCallable().withDefaultCallContext(grpcCallContext))
            .setHasher(Hasher.enabled())
            .setByteStringStrategy(ByteStringStrategy.noCopy());

    BufferedWritableByteChannelSession<WriteObjectResponse> session;
    long size = Files.size(path);
    if (size < bufferSize) {
      // ignore the bufferSize argument if the file is smaller than it
      session =
          channelSessionBuilder.direct().buffered(Buffers.allocate(size)).setRequest(req).build();
    } else {
      ApiFuture<ResumableWrite> start =
          ResumableMedia.gapic()
              .write()
              .resumableWrite(
                  grpcStorageStub
                      .startResumableWriteCallable()
                      .withDefaultCallContext(grpcCallContext),
                  req);
      session =
          channelSessionBuilder
              .resumable()
              .buffered(Buffers.allocateAligned(bufferSize, _256KiB))
              .setStartAsync(start)
              .build();
    }

    try (SeekableByteChannel src = Files.newByteChannel(path, READ_OPS);
        BufferedWritableByteChannel dst = session.open()) {
      ByteStreams.copy(src, dst);
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
    return getBlob(session.getResult());
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    return createFrom(blobInfo, content, _15MiB, options);
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream in, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(blobInfo, "blobInfo must be non null");

    BlobTargetOption[] translate = translate(options);
    // TODO: Why does optionMap not accept BlobWriteOption?
    Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(translate);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    WriteObjectRequest req = getWriteObjectRequestBuilder(blobInfo, optionsMap).build();

    ApiFuture<ResumableWrite> start =
        ResumableMedia.gapic()
            .write()
            .resumableWrite(
                grpcStorageStub
                    .startResumableWriteCallable()
                    .withDefaultCallContext(grpcCallContext),
                req);

    BufferedWritableByteChannelSession<WriteObjectResponse> session =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                grpcStorageStub.writeObjectCallable().withDefaultCallContext(grpcCallContext))
            .setHasher(Hasher.enabled())
            .setByteStringStrategy(ByteStringStrategy.noCopy())
            .resumable()
            .buffered(Buffers.allocateAligned(bufferSize, _256KiB))
            .setStartAsync(start)
            .build();

    // Specifically not in the try-with, so we don't close the provided stream
    ReadableByteChannel src =
        Channels.newChannel(firstNonNull(in, new ByteArrayInputStream(ZERO_BYTES)));
    try (BufferedWritableByteChannel dst = session.open()) {
      ByteStreams.copy(src, dst);
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
    return getBlob(session.getResult());
  }

  @Override
  public Bucket get(String bucket, BucketGetOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
        () -> grpcStorageStub.getBucketCallable().call(req, grpcCallContext),
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
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
        () -> grpcStorageStub.getObjectCallable().call(req, grpcCallContext),
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
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
    ListBucketsPagedResponse call = listBucketsCallable.call(builder.build(), grpcCallContext);
    ListBucketsPage page = call.getPage();
    return new TransformingPageDecorator<>(page, syntaxDecoders.bucket);
  }

  @Override
  public Page<Blob> list(String bucket, BlobListOption... options) {
    UnaryCallable<ListObjectsRequest, ListObjectsPagedResponse> listObjectsCallable =
        grpcStorageStub.listObjectsPagedCallable();
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
    ListObjectsPagedResponse call = listObjectsCallable.call(builder.build(), grpcCallContext);
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
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
          () -> grpcStorageStub.deleteBucketCallable().call(req, grpcCallContext),
          Decoder.identity());
      return true;
    } catch (ApiException e) {
      // TODO: We should throw a StorageException instead of ApiException when making the
      return false;
    }
  }

  @Override
  public boolean delete(String bucket, String blob, BlobSourceOption... options) {
    return delete(BlobId.of(bucket, blob), options);
  }

  @Override
  public boolean delete(BlobId blob, BlobSourceOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    DeleteObjectRequest.Builder objectRequestBuilder =
        DeleteObjectRequest.newBuilder().setBucket(blob.getBucket()).setObject(blob.getName());
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        objectRequestBuilder::setIfMetagenerationNotMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        objectRequestBuilder::setIfMetagenerationNotMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_MATCH),
        objectRequestBuilder::setIfGenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_NOT_MATCH),
        objectRequestBuilder::setIfGenerationNotMatch);
    ifNonNull(blob.getGeneration(), objectRequestBuilder::setGeneration);
    DeleteObjectRequest req = objectRequestBuilder.build();
    try {
      Retrying.run(
          getOptions(),
          retryAlgorithmManager.getFor(req),
          () -> grpcStorageStub.deleteObjectCallable().call(req, grpcCallContext),
          Decoder.identity());
      return true;
    } catch (ApiException e) {
      // TODO: We should throw a StorageException instead of ApiException when making the
      return false;
    }
  }

  @Override
  public boolean delete(BlobId blob) {
    return delete(blob);
  }

  @Override
  public Blob compose(ComposeRequest composeRequest) {
    final Map<StorageRpc.Option, ?> optionsMap =
        StorageImpl.optionMap(Iterables.toArray(composeRequest.getTargetOptions(), Option.class));
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    ComposeObjectRequest.Builder composeObjectReqBuilder = ComposeObjectRequest.newBuilder();
    final List<SourceObject> sources =
        Lists.newArrayListWithCapacity(composeRequest.getSourceBlobs().size());
    for (ComposeRequest.SourceBlob sourceBlob : composeRequest.getSourceBlobs()) {
      sources.add(
          SourceObject.newBuilder()
              .setName(sourceBlob.getName())
              .setGeneration(sourceBlob.getGeneration())
              .build());
    }
    composeObjectReqBuilder.addAllSourceObjects(sources);
    final Object target = codecs.blobInfo().encode(composeRequest.getTarget());
    composeObjectReqBuilder.setDestination(target);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_MATCH),
        composeObjectReqBuilder::setIfGenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        composeObjectReqBuilder::setIfMetagenerationMatch);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.PREDEFINED_ACL),
        composeObjectReqBuilder::setDestinationPredefinedAcl);
    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.KMS_KEY_NAME),
        composeObjectReqBuilder::setKmsKey);
    ComposeObjectRequest req = composeObjectReqBuilder.build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> grpcStorageStub.composeObjectCallable().call(req, grpcCallContext),
        syntaxDecoders.blob);
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

    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);

    ReadObjectRequest readObjectRequest = getReadObjectRequest(blob, optionsMap);
    UnbufferedReadableByteChannelSession<Object> session =
        ResumableMedia.gapic()
            .read()
            .byteChannel(grpcStorageStub.readObjectCallable())
            .unbuffered()
            .setReadObjectRequest(readObjectRequest)
            .build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Channels.newChannel(baos)) {
      ByteStreams.copy(r, w);
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
    return baos.toByteArray();
  }

  @Override
  public StorageBatch batch() {
    return throwHttpJsonOnly("batch()");
  }

  @Override
  public GrpcBlobReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
    return reader(BlobId.of(bucket, blob), options);
  }

  @Override
  public GrpcBlobReadChannel reader(BlobId blob, BlobSourceOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    ReadObjectRequest request = getReadObjectRequest(blob, optionsMap);
    return new GrpcBlobReadChannel(grpcStorageStub.readObjectCallable(), request);
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {

    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);

    // TODO: handle StorageRpc.Option.RETURN_RAW_INPUT_STREAM impacts

    ReadObjectRequest readObjectRequest = getReadObjectRequest(blob, optionsMap);
    UnbufferedReadableByteChannelSession<Object> session =
        ResumableMedia.gapic()
            .read()
            .byteChannel(grpcStorageStub.readObjectCallable())
            .unbuffered()
            .setReadObjectRequest(readObjectRequest)
            .build();

    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Files.newByteChannel(path, WRITE_OPS)) {
      ByteStreams.copy(r, w);
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {

    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);

    // TODO: handle StorageRpc.Option.RETURN_RAW_INPUT_STREAM impacts

    ReadObjectRequest readObjectRequest = getReadObjectRequest(blob, optionsMap);
    UnbufferedReadableByteChannelSession<Object> session =
        ResumableMedia.gapic()
            .read()
            .byteChannel(grpcStorageStub.readObjectCallable())
            .unbuffered()
            .setReadObjectRequest(readObjectRequest)
            .build();

    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Channels.newChannel(outputStream)) {
      ByteStreams.copy(r, w);
    } catch (IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public GrpcBlobWriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    BlobTargetOption[] translate = translate(options);
    // TODO: Why does optionMap not accept BlobWriteOption?
    Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(translate);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    WriteObjectRequest req = getWriteObjectRequestBuilder(blobInfo, optionsMap).build();
    return new GrpcBlobWriteChannel(
        grpcStorageStub.writeObjectCallable(),
        () ->
            ResumableMedia.gapic()
                .write()
                .resumableWrite(
                    grpcStorageStub
                        .startResumableWriteCallable()
                        .withDefaultCallContext(grpcCallContext),
                    req));
  }

  @Override
  public WriteChannel writer(URL signedURL) {
    return throwHttpJsonOnly(fmtMethodName("writer", URL.class));
  }

  @Override
  public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
    return throwHttpJsonOnly(
        fmtMethodName("signUrl", BlobInfo.class, long.class, TimeUnit.class, SignUrlOption.class));
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    return throwHttpJsonOnly(
        fmtMethodName(
            "generateSignedPostPolicyV4",
            BlobInfo.class,
            long.class,
            TimeUnit.class,
            PostFieldsV4.class,
            PostConditionsV4.class,
            PostPolicyV4Option.class));
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostPolicyV4Option... options) {
    return throwHttpJsonOnly(
        fmtMethodName(
            "generateSignedPostPolicyV4",
            BlobInfo.class,
            long.class,
            TimeUnit.class,
            PostFieldsV4.class,
            PostPolicyV4Option.class));
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    return throwHttpJsonOnly(
        fmtMethodName(
            "generateSignedPostPolicyV4",
            BlobInfo.class,
            long.class,
            TimeUnit.class,
            PostConditionsV4.class,
            PostPolicyV4Option.class));
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo, long duration, TimeUnit unit, PostPolicyV4Option... options) {
    return throwHttpJsonOnly(
        fmtMethodName(
            "generateSignedPostPolicyV4",
            BlobInfo.class,
            long.class,
            TimeUnit.class,
            PostPolicyV4Option.class));
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
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
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
    ListHmacKeysPagedResponse call = listHmacKeysCallable.call(builder.build(), grpcCallContext);
    ListHmacKeysPage page = call.getPage();
    return new TransformingPageDecorator<>(page, codecs.hmacKeyMetadata());
  }

  @Override
  public HmacKeyMetadata getHmacKey(String accessId, GetHmacKeyOption... options) {
    return todo();
  }

  @Override
  public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, DeleteHmacKeyOption... options) {
    final Map<StorageRpc.Option, ?> optionsMap = StorageImpl.optionMap(options);
    GrpcCallContext grpcCallContext = GrpcRequestMetadataSupport.create(optionsMap);
    DeleteHmacKeyRequest req =
        DeleteHmacKeyRequest.newBuilder()
            .setAccessId(hmacKeyMetadata.getAccessId())
            .setProject(hmacKeyMetadata.getProjectId())
            .build();
    Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> {
          grpcStorageStub.deleteHmacKeyCallable().call(req, grpcCallContext);
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

  private BlobTargetOption[] translate(BlobWriteOption[] options) {
    return todo();
  }

  private Blob getBlob(ApiFuture<WriteObjectResponse> result) {
    // TODO: investigate if we need to unnest any exception
    WriteObjectResponse response = ApiExceptions.callAndTranslateApiException(result);
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

  private <T> T throwHttpJsonOnly(String methodName) {
    String message =
        String.format(
            "%s#%s is only supported for HTTP_JSON transport. Please use StorageOptions.http() to construct a compatible instance.",
            Storage.class.getName(), methodName);
    throw new UnsupportedOperationException(message);
  }

  private static String fmtMethodName(String name, Class<?>... args) {
    return name
        + "("
        + Arrays.stream(args).map(Class::getName).collect(Collectors.joining(", "))
        + ")";
  }

  private ReadObjectRequest getReadObjectRequest(
      BlobId blob, Map<StorageRpc.Option, ?> optionsMap) {
    Object object = codecs.blobId().encode(blob);

    ReadObjectRequest.Builder builder =
        ReadObjectRequest.newBuilder().setBucket(object.getBucket()).setObject(object.getName());

    long generation = object.getGeneration();
    if (generation > 0) {
      builder.setGeneration(generation);
    }
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        builder::setIfMetagenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        builder::setIfMetagenerationNotMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_MATCH),
        builder::setIfGenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_NOT_MATCH),
        builder::setIfGenerationNotMatch);
    String key = (String) optionsMap.get(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY);
    if (key != null) {
      builder.setCommonObjectRequestParams(commonRequestParams(key));
    }
    return builder.build();
  }

  private static final class GrpcRequestMetadataSupport {
    @NonNull
    static GrpcCallContext create(Map<StorageRpc.Option, ?> optionsMap) {
      // GrpcCallContext is immutable, any modification we perform needs to be assigned back to
      // our variable otherwise it will effectively be lost.
      GrpcCallContext ctx = GrpcCallContext.createDefault();
      String userProject = (String) optionsMap.get(StorageRpc.Option.USER_PROJECT);
      if (userProject != null) {
        ctx =
            ctx.withExtraHeaders(
                ImmutableMap.of("X-Goog-User-Project", ImmutableList.of(userProject)));
      }
      return ctx;
    }
  }

  private WriteObjectRequest.Builder getWriteObjectRequestBuilder(
      BlobInfo info, Map<StorageRpc.Option, ?> optionsMap) {
    Object object = codecs.blobInfo().encode(info);
    Object.Builder objectBuilder =
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
            .clearUpdateTime();
    WriteObjectSpec.Builder specBuilder = WriteObjectSpec.newBuilder().setResource(objectBuilder);

    WriteObjectRequest.Builder requestBuilder =
        WriteObjectRequest.newBuilder().setWriteObjectSpec(specBuilder);

    // TODO: Projection: Do we care?

    ifNonNull(
        (String) optionsMap.get(StorageRpc.Option.PREDEFINED_ACL), specBuilder::setPredefinedAcl);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_MATCH),
        specBuilder::setIfMetagenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH),
        specBuilder::setIfMetagenerationNotMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_MATCH),
        specBuilder::setIfGenerationMatch);
    ifNonNull(
        (Long) optionsMap.get(StorageRpc.Option.IF_GENERATION_NOT_MATCH),
        specBuilder::setIfGenerationNotMatch);
    String userProject = (String) optionsMap.get(StorageRpc.Option.USER_PROJECT);
    if (userProject != null) {
      // TODO: user project
    }
    String encryptionKey = (String) optionsMap.get(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY);
    if (encryptionKey != null) {
      requestBuilder.setCommonObjectRequestParams(commonRequestParams(encryptionKey));
    }
    String key = (String) optionsMap.get(StorageRpc.Option.KMS_KEY_NAME);
    if (key != null) {
      objectBuilder.setKmsKey(key);
    }

    return requestBuilder;
  }

  private CommonObjectRequestParams commonRequestParams(String key) {
    byte[] keyBytes = Base64.getDecoder().decode(key);
    HashCode keySha256 = Hashing.sha256().hashBytes(keyBytes);

    return CommonObjectRequestParams.newBuilder()
        .setEncryptionAlgorithm("AES256")
        .setEncryptionKeyBytes(ByteString.copyFromUtf8(key))
        .setEncryptionKeySha256Bytes(ByteString.copyFrom(keySha256.asBytes()))
        .build();
  }
}
