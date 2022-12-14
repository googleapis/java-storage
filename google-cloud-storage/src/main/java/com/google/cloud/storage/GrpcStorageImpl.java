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

import static com.google.cloud.storage.ByteSizeConstants._16MiB;
import static com.google.cloud.storage.ByteSizeConstants._256KiB;
import static com.google.cloud.storage.GrpcToHttpStatusCodeTranslation.resultRetryAlgorithmToCodes;
import static com.google.cloud.storage.StorageV2ProtoUtils.bucketAclEntityOrAltEq;
import static com.google.cloud.storage.StorageV2ProtoUtils.objectAclEntityOrAltEq;
import static com.google.cloud.storage.Utils.bucketNameCodec;
import static com.google.cloud.storage.Utils.ifNonNull;
import static com.google.cloud.storage.Utils.projectNameCodec;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.Page;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.api.gax.rpc.UnimplementedException;
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
import com.google.cloud.storage.Storage.ComposeRequest.SourceBlob;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.UnifiedOpts.BucketListOpt;
import com.google.cloud.storage.UnifiedOpts.BucketSourceOpt;
import com.google.cloud.storage.UnifiedOpts.BucketTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Fields;
import com.google.cloud.storage.UnifiedOpts.HmacKeyListOpt;
import com.google.cloud.storage.UnifiedOpts.HmacKeySourceOpt;
import com.google.cloud.storage.UnifiedOpts.HmacKeyTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Mapper;
import com.google.cloud.storage.UnifiedOpts.ObjectListOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectSourceOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.cloud.storage.UnifiedOpts.ProjectId;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import com.google.storage.v2.BucketAccessControl;
import com.google.storage.v2.ComposeObjectRequest;
import com.google.storage.v2.ComposeObjectRequest.SourceObject;
import com.google.storage.v2.CreateBucketRequest;
import com.google.storage.v2.CreateHmacKeyRequest;
import com.google.storage.v2.DeleteBucketRequest;
import com.google.storage.v2.DeleteHmacKeyRequest;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.GetBucketRequest;
import com.google.storage.v2.GetHmacKeyRequest;
import com.google.storage.v2.GetObjectRequest;
import com.google.storage.v2.GetServiceAccountRequest;
import com.google.storage.v2.ListBucketsRequest;
import com.google.storage.v2.ListHmacKeysRequest;
import com.google.storage.v2.ListObjectsRequest;
import com.google.storage.v2.LockBucketRetentionPolicyRequest;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectAccessControl;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.ProjectName;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.RewriteObjectRequest;
import com.google.storage.v2.RewriteResponse;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageClient.ListBucketsPage;
import com.google.storage.v2.StorageClient.ListBucketsPagedResponse;
import com.google.storage.v2.StorageClient.ListHmacKeysPage;
import com.google.storage.v2.StorageClient.ListHmacKeysPagedResponse;
import com.google.storage.v2.StorageClient.ListObjectsPage;
import com.google.storage.v2.StorageClient.ListObjectsPagedResponse;
import com.google.storage.v2.UpdateBucketRequest;
import com.google.storage.v2.UpdateHmacKeyRequest;
import com.google.storage.v2.UpdateObjectRequest;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import com.google.storage.v2.WriteObjectSpec;
import io.grpc.Status.Code;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@BetaApi
final class GrpcStorageImpl extends BaseService<StorageOptions> implements Storage {

  private static final byte[] ZERO_BYTES = new byte[0];
  private static final Set<OpenOption> READ_OPS = ImmutableSet.of(StandardOpenOption.READ);
  private static final Set<OpenOption> WRITE_OPS =
      ImmutableSet.of(
          StandardOpenOption.WRITE,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
  private static final BucketSourceOption[] EMPTY_BUCKET_SOURCE_OPTIONS = new BucketSourceOption[0];

  final StorageClient storageClient;
  final GrpcConversions codecs;
  final GrpcRetryAlgorithmManager retryAlgorithmManager;
  final SyntaxDecoders syntaxDecoders;

  @Deprecated private final ProjectId defaultProjectId;

  GrpcStorageImpl(GrpcStorageOptions options, StorageClient storageClient) {
    super(options);
    this.storageClient = storageClient;
    this.codecs = Conversions.grpc();
    this.retryAlgorithmManager = options.getRetryAlgorithmManager();
    this.syntaxDecoders = new SyntaxDecoders();
    this.defaultProjectId = UnifiedOpts.projectId(options.getProjectId());
  }

  @Override
  public void close() throws Exception {
    try (StorageClient s = storageClient) {
      s.shutdownNow();
      org.threeten.bp.Duration terminationAwaitDuration =
          getOptions().getTerminationAwaitDuration();
      s.awaitTermination(terminationAwaitDuration.toMillis(), TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
    Opts<BucketTargetOpt> opts = Opts.unwrap(options).resolveFrom(bucketInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    com.google.storage.v2.Bucket bucket = codecs.bucketInfo().encode(bucketInfo);
    CreateBucketRequest.Builder builder =
        CreateBucketRequest.newBuilder()
            .setBucket(bucket)
            .setBucketId(bucketInfo.getName())
            .setParent(ProjectName.format(getOptions().getProjectId()));
    CreateBucketRequest req = opts.createBucketsRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.createBucketCallable().call(req, grpcCallContext),
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
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);
    Hasher hasher = getHasherForRequest(req, Hasher.enabled());
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> {
          UnbufferedWritableByteChannelSession<WriteObjectResponse> session =
              ResumableMedia.gapic()
                  .write()
                  .byteChannel(
                      storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
                  .setByteStringStrategy(ByteStringStrategy.noCopy())
                  .setHasher(hasher)
                  .direct()
                  .unbuffered()
                  .setRequest(req)
                  .build();

          try (UnbufferedWritableByteChannel c = session.open()) {
            c.write(ByteBuffer.wrap(content, offset, length));
          }
          return session.getResult();
        },
        this::getBlob);
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
    return createFrom(blobInfo, path, _16MiB, options);
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(path, "path must be non null");
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }

    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);

    Hasher hasher = getHasherForRequest(req, Hasher.enabled());
    GapicWritableByteChannelSessionBuilder channelSessionBuilder =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
            .setHasher(hasher)
            .setByteStringStrategy(ByteStringStrategy.noCopy());

    long size = Files.size(path);
    if (size < bufferSize) {
      // ignore the bufferSize argument if the file is smaller than it
      return Retrying.run(
          getOptions(),
          retryAlgorithmManager.getFor(req),
          () -> {
            BufferedWritableByteChannelSession<WriteObjectResponse> session =
                channelSessionBuilder
                    .direct()
                    .buffered(Buffers.allocate(size))
                    .setRequest(req)
                    .build();

            try (SeekableByteChannel src = Files.newByteChannel(path, READ_OPS);
                BufferedWritableByteChannel dst = session.open()) {
              ByteStreams.copy(src, dst);
            } catch (Exception e) {
              throw StorageException.coalesce(e);
            }
            return session.getResult();
          },
          this::getBlob);
    } else {
      ApiFuture<ResumableWrite> start = startResumableWrite(grpcCallContext, req);
      BufferedWritableByteChannelSession<WriteObjectResponse> session =
          channelSessionBuilder
              .resumable()
              .withRetryConfig(getOptions(), retryAlgorithmManager.idempotent())
              .buffered(Buffers.allocateAligned(bufferSize, _256KiB))
              .setStartAsync(start)
              .build();
      try (SeekableByteChannel src = Files.newByteChannel(path, READ_OPS);
          BufferedWritableByteChannel dst = session.open()) {
        ByteStreams.copy(src, dst);
      } catch (Exception e) {
        throw StorageException.coalesce(e);
      }
      return getBlob(session.getResult());
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    return createFrom(blobInfo, content, _16MiB, options);
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream in, int bufferSize, BlobWriteOption... options)
      throws IOException {
    requireNonNull(blobInfo, "blobInfo must be non null");

    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);

    ApiFuture<ResumableWrite> start = startResumableWrite(grpcCallContext, req);

    Hasher hasher = getHasherForRequest(req, Hasher.enabled());
    BufferedWritableByteChannelSession<WriteObjectResponse> session =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
            .setHasher(hasher)
            .setByteStringStrategy(ByteStringStrategy.noCopy())
            .resumable()
            .withRetryConfig(getOptions(), retryAlgorithmManager.idempotent())
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
    Opts<BucketSourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    GetBucketRequest.Builder builder =
        GetBucketRequest.newBuilder().setName(bucketNameCodec.encode(bucket));
    GetBucketRequest req = opts.getBucketsRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getBucketCallable().call(req, grpcCallContext),
        syntaxDecoders.bucket.andThen(opts.clearBucketFields()));
  }

  @Override
  public Bucket lockRetentionPolicy(BucketInfo bucket, BucketTargetOption... options) {
    Opts<BucketTargetOpt> opts = Opts.unwrap(options).resolveFrom(bucket);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    LockBucketRetentionPolicyRequest.Builder builder =
        LockBucketRetentionPolicyRequest.newBuilder()
            .setBucket(bucketNameCodec.encode(bucket.getName()));
    LockBucketRetentionPolicyRequest req =
        opts.lockBucketRetentionPolicyRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.lockBucketRetentionPolicyCallable().call(req, grpcCallContext),
        syntaxDecoders.bucket);
  }

  @Override
  public Blob get(String bucket, String blob, BlobGetOption... options) {
    return get(BlobId.of(bucket, blob), options);
  }

  @Override
  public Blob get(BlobId blob, BlobGetOption... options) {
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    GetObjectRequest.Builder builder =
        GetObjectRequest.newBuilder()
            .setBucket(bucketNameCodec.encode(blob.getBucket()))
            .setObject(blob.getName());
    ifNonNull(blob.getGeneration(), builder::setGeneration);
    GetObjectRequest req = opts.getObjectsRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> {
          try {
            return storageClient.getObjectCallable().call(req, grpcCallContext);
          } catch (NotFoundException ignore) {
            return null;
          }
        },
        syntaxDecoders.blob.andThen(opts.clearBlobFields()));
  }

  @Override
  public Blob get(BlobId blob) {
    return get(blob, new BlobGetOption[0]);
  }

  @Override
  public Page<Bucket> list(BucketListOption... options) {
    UnaryCallable<ListBucketsRequest, ListBucketsPagedResponse> listBucketsCallable =
        storageClient.listBucketsPagedCallable();
    Opts<BucketListOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    ListBucketsRequest request =
        defaultProjectId
            .listBuckets()
            .andThen(opts.listBucketsRequest())
            .apply(ListBucketsRequest.newBuilder())
            .build();
    ListBucketsPagedResponse call = listBucketsCallable.call(request, grpcCallContext);
    try {
      ListBucketsPage page = call.getPage();
      return new TransformingPageDecorator<>(
          page,
          syntaxDecoders.bucket.andThen(opts.clearBucketFields()),
          getOptions(),
          retryAlgorithmManager.getFor(request));
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Page<Blob> list(String bucket, BlobListOption... options) {
    UnaryCallable<ListObjectsRequest, ListObjectsPagedResponse> listObjectsCallable =
        storageClient.listObjectsPagedCallable();
    Opts<ObjectListOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    ListObjectsRequest.Builder builder =
        ListObjectsRequest.newBuilder().setParent(bucketNameCodec.encode(bucket));
    ListObjectsRequest req = opts.listObjectsRequest().apply(builder).build();
    try {
      ListObjectsPagedResponse call = listObjectsCallable.call(req, grpcCallContext);
      ListObjectsPage page = call.getPage();
      return new TransformingPageDecorator<>(
          page,
          syntaxDecoders.blob.andThen(opts.clearBlobFields()),
          getOptions(),
          retryAlgorithmManager.getFor(req));
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Bucket update(BucketInfo bucketInfo, BucketTargetOption... options) {
    Opts<BucketTargetOpt> opts = Opts.unwrap(options).resolveFrom(bucketInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    com.google.storage.v2.Bucket bucket = codecs.bucketInfo().encode(bucketInfo);
    UpdateBucketRequest.Builder builder =
        opts.updateBucketsRequest().apply(UpdateBucketRequest.newBuilder().setBucket(bucket));
    builder
        .getUpdateMaskBuilder()
        .addAllPaths(
            bucketInfo.getModifiedFields().stream()
                .map(BucketField::getGrpcName)
                .collect(ImmutableList.toImmutableList()));
    UpdateBucketRequest req = builder.build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.updateBucketCallable().call(req, grpcCallContext),
        syntaxDecoders.bucket);
  }

  @Override
  public Blob update(BlobInfo blobInfo, BlobTargetOption... options) {
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    Object object = codecs.blobInfo().encode(blobInfo);
    UpdateObjectRequest.Builder builder =
        opts.updateObjectsRequest().apply(UpdateObjectRequest.newBuilder().setObject(object));
    builder
        .getUpdateMaskBuilder()
        .addAllPaths(
            blobInfo.getModifiedFields().stream()
                .map(BlobField::getGrpcName)
                .collect(ImmutableList.toImmutableList()));
    UpdateObjectRequest req = builder.build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.updateObjectCallable().call(req, grpcCallContext),
        syntaxDecoders.blob);
  }

  @Override
  public Blob update(BlobInfo blobInfo) {
    return update(blobInfo, new BlobTargetOption[0]);
  }

  @Override
  public boolean delete(String bucket, BucketSourceOption... options) {
    Opts<BucketSourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    DeleteBucketRequest.Builder builder =
        DeleteBucketRequest.newBuilder().setName(bucketNameCodec.encode(bucket));
    DeleteBucketRequest req = opts.deleteBucketsRequest().apply(builder).build();
    try {
      Retrying.run(
          getOptions(),
          retryAlgorithmManager.getFor(req),
          () -> storageClient.deleteBucketCallable().call(req, grpcCallContext),
          Decoder.identity());
      return true;
    } catch (StorageException e) {
      return false;
    }
  }

  @Override
  public boolean delete(String bucket, String blob, BlobSourceOption... options) {
    return delete(BlobId.of(bucket, blob), options);
  }

  @Override
  public boolean delete(BlobId blob, BlobSourceOption... options) {
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    DeleteObjectRequest.Builder builder =
        DeleteObjectRequest.newBuilder()
            .setBucket(bucketNameCodec.encode(blob.getBucket()))
            .setObject(blob.getName());
    ifNonNull(blob.getGeneration(), builder::setGeneration);
    DeleteObjectRequest req = opts.deleteObjectsRequest().apply(builder).build();
    return Boolean.TRUE.equals(
        Retrying.run(
            getOptions(),
            retryAlgorithmManager.getFor(req),
            () -> {
              try {
                storageClient.deleteObjectCallable().call(req, grpcCallContext);
                return true;
              } catch (NotFoundException e) {
                return false;
              }
            },
            Decoder.identity()));
  }

  @Override
  public boolean delete(BlobId blob) {
    return delete(blob, new BlobSourceOption[0]);
  }

  @Override
  public Blob compose(ComposeRequest composeRequest) {
    Opts<ObjectTargetOpt> opts =
        Opts.unwrap(composeRequest.getTargetOptions()).resolveFrom(composeRequest.getTarget());
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    ComposeObjectRequest.Builder builder = ComposeObjectRequest.newBuilder();
    composeRequest.getSourceBlobs().stream()
        .map(src -> sourceObjectEncode(src))
        .forEach(builder::addSourceObjects);
    final Object target = codecs.blobInfo().encode(composeRequest.getTarget());
    builder.setDestination(target);
    ComposeObjectRequest req = opts.composeObjectsRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.composeObjectCallable().call(req, grpcCallContext),
        syntaxDecoders.blob);
  }

  @Override
  public CopyWriter copy(CopyRequest copyRequest) {
    BlobId src = copyRequest.getSource();
    BlobInfo dst = copyRequest.getTarget();
    Opts<ObjectSourceOpt> srcOpts =
        Opts.unwrap(copyRequest.getSourceOptions()).projectAsSource().resolveFrom(src);
    Opts<ObjectTargetOpt> dstOpts = Opts.unwrap(copyRequest.getTargetOptions()).resolveFrom(dst);

    Mapper<RewriteObjectRequest.Builder> mapper =
        srcOpts.rewriteObjectsRequest().andThen(dstOpts.rewriteObjectsRequest());

    Object srcProto = codecs.blobId().encode(src);
    Object dstProto = codecs.blobInfo().encode(dst);

    RewriteObjectRequest.Builder b =
        RewriteObjectRequest.newBuilder()
            .setDestinationName(dstProto.getName())
            .setDestinationBucket(dstProto.getBucket())
            // destination_kms_key comes from dstOpts
            // according to the docs in the protos, it is illegal to populate the following fields,
            // clear them out if they are set
            // destination_predefined_acl comes from dstOpts
            // if_*_match come from srcOpts and dstOpts
            // copy_source_encryption_* come from srcOpts
            // common_object_request_params come from dstOpts
            .setDestination(dstProto.toBuilder().clearName().clearBucket().clearKmsKey().build())
            .setSourceBucket(srcProto.getBucket())
            .setSourceObject(srcProto.getName());

    if (src.getGeneration() != null) {
      b.setSourceGeneration(src.getGeneration());
    }

    if (copyRequest.getMegabytesCopiedPerChunk() != null) {
      b.setMaxBytesRewrittenPerCall(copyRequest.getMegabytesCopiedPerChunk());
    }

    RewriteObjectRequest req = mapper.apply(b).build();
    GrpcCallContext grpcCallContext =
        srcOpts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    UnaryCallable<RewriteObjectRequest, RewriteResponse> callable =
        storageClient.rewriteObjectCallable().withDefaultCallContext(grpcCallContext);
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> callable.call(req),
        (resp) -> new GapicCopyWriter(this, callable, retryAlgorithmManager.idempotent(), resp));
  }

  @Override
  public byte[] readAllBytes(String bucket, String blob, BlobSourceOption... options) {
    return readAllBytes(BlobId.of(bucket, blob), options);
  }

  @Override
  public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
    UnbufferedReadableByteChannelSession<Object> session = unbufferedReadSession(blob, options);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Channels.newChannel(baos)) {
      ByteStreams.copy(r, w);
    } catch (ApiException | IOException e) {
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
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    ReadObjectRequest request = getReadObjectRequest(blob, opts);
    Set<StatusCode.Code> codes = resultRetryAlgorithmToCodes(retryAlgorithmManager.getFor(request));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault().withRetryableCodes(codes);
    return new GrpcBlobReadChannel(
        storageClient.readObjectCallable().withDefaultCallContext(grpcCallContext),
        request,
        !opts.autoGzipDecompression());
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {

    UnbufferedReadableByteChannelSession<Object> session = unbufferedReadSession(blob, options);

    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Files.newByteChannel(path, WRITE_OPS)) {
      ByteStreams.copy(r, w);
    } catch (ApiException | IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {

    UnbufferedReadableByteChannelSession<Object> session = unbufferedReadSession(blob, options);

    try (UnbufferedReadableByteChannel r = session.open();
        WritableByteChannel w = Channels.newChannel(outputStream)) {
      ByteStreams.copy(r, w);
    } catch (ApiException | IOException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public GrpcBlobWriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);
    Hasher hasher = getHasherForRequest(req, Hasher.enabled());
    return new GrpcBlobWriteChannel(
        storageClient.writeObjectCallable(),
        getOptions(),
        retryAlgorithmManager.idempotent(),
        () -> startResumableWrite(grpcCallContext, req),
        hasher);
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
    return throwHttpJsonOnly(fmtMethodName("get", BlobId[].class));
  }

  @Override
  public List<Blob> get(Iterable<BlobId> blobIds) {
    return throwHttpJsonOnly(fmtMethodName("get", Iterable.class));
  }

  @Override
  public List<Blob> update(BlobInfo... blobInfos) {
    return throwHttpJsonOnly(fmtMethodName("update", BlobInfo[].class));
  }

  @Override
  public List<Blob> update(Iterable<BlobInfo> blobInfos) {
    return throwHttpJsonOnly(fmtMethodName("update", Iterable.class));
  }

  @Override
  public List<Boolean> delete(BlobId... blobIds) {
    return throwHttpJsonOnly(fmtMethodName("delete", BlobId[].class));
  }

  @Override
  public List<Boolean> delete(Iterable<BlobId> blobIds) {
    return throwHttpJsonOnly(fmtMethodName("delete", Iterable.class));
  }

  @Override
  public Acl getAcl(String bucket, Entity entity, BucketSourceOption... options) {
    try {
      Opts<BucketSourceOpt> opts = Opts.unwrap(options);
      com.google.storage.v2.Bucket resp = getBucketWithAcls(bucket, opts);

      Predicate<BucketAccessControl> entityPredicate =
          bucketAclEntityOrAltEq(codecs.entity().encode(entity));

      Optional<BucketAccessControl> first =
          resp.getAclList().stream().filter(entityPredicate).findFirst();

      // HttpStorageRpc defaults to null if Not Found
      return first.map(codecs.bucketAcl()::decode).orElse(null);
    } catch (NotFoundException e) {
      return null;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return null;
      } else {
        throw se;
      }
    }
  }

  @Override
  public Acl getAcl(String bucket, Entity entity) {
    return getAcl(bucket, entity, EMPTY_BUCKET_SOURCE_OPTIONS);
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity, BucketSourceOption... options) {
    try {
      Opts<BucketSourceOpt> opts = Opts.unwrap(options);
      com.google.storage.v2.Bucket resp = getBucketWithAcls(bucket, opts);
      String encode = codecs.entity().encode(entity);

      Predicate<BucketAccessControl> entityPredicate = bucketAclEntityOrAltEq(encode);

      List<BucketAccessControl> currentAcls = resp.getAclList();
      ImmutableList<BucketAccessControl> newAcls =
          currentAcls.stream()
              .filter(entityPredicate.negate())
              .collect(ImmutableList.toImmutableList());
      if (newAcls.equals(currentAcls)) {
        // we didn't actually filter anything out, no need to send an RPC, simply return false
        return false;
      }
      long metageneration = resp.getMetageneration();

      UpdateBucketRequest req = createUpdateBucketAclRequest(bucket, newAcls, metageneration);

      com.google.storage.v2.Bucket updateResult = updateBucket(req);
      // read the response to ensure there is no longer an acl for the specified entity
      Optional<BucketAccessControl> first =
          updateResult.getAclList().stream().filter(entityPredicate).findFirst();
      return !first.isPresent();
    } catch (NotFoundException e) {
      // HttpStorageRpc returns false if the bucket doesn't exist :(
      return false;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return false;
      } else {
        throw se;
      }
    }
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity) {
    return deleteAcl(bucket, entity, EMPTY_BUCKET_SOURCE_OPTIONS);
  }

  @Override
  public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
    return updateAcl(bucket, acl, options);
  }

  @Override
  public Acl createAcl(String bucket, Acl acl) {
    return createAcl(bucket, acl, EMPTY_BUCKET_SOURCE_OPTIONS);
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
    try {
      Opts<BucketSourceOpt> opts = Opts.unwrap(options);
      com.google.storage.v2.Bucket resp = getBucketWithAcls(bucket, opts);
      BucketAccessControl encode = codecs.bucketAcl().encode(acl);
      String entity = encode.getEntity();

      Predicate<BucketAccessControl> entityPredicate = bucketAclEntityOrAltEq(entity);

      ImmutableList<BucketAccessControl> newDefaultAcls =
          Streams.concat(
                  resp.getAclList().stream().filter(entityPredicate.negate()), Stream.of(encode))
              .collect(ImmutableList.toImmutableList());

      UpdateBucketRequest req =
          createUpdateBucketAclRequest(bucket, newDefaultAcls, resp.getMetageneration());

      com.google.storage.v2.Bucket updateResult = updateBucket(req);

      Optional<Acl> first =
          updateResult.getAclList().stream()
              .filter(entityPredicate)
              .findFirst()
              .map(codecs.bucketAcl()::decode);

      return first.orElseThrow(
          () -> new StorageException(0, "Acl update call success, but not in response"));
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl) {
    return updateAcl(bucket, acl, EMPTY_BUCKET_SOURCE_OPTIONS);
  }

  @Override
  public List<Acl> listAcls(String bucket, BucketSourceOption... options) {
    try {
      Opts<BucketSourceOpt> opts = Opts.unwrap(options);
      com.google.storage.v2.Bucket resp = getBucketWithAcls(bucket, opts);
      return resp.getAclList().stream()
          .map(codecs.bucketAcl()::decode)
          .collect(ImmutableList.toImmutableList());
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public List<Acl> listAcls(String bucket) {
    return listAcls(bucket, EMPTY_BUCKET_SOURCE_OPTIONS);
  }

  @Override
  public Acl getDefaultAcl(String bucket, Entity entity) {
    try {
      com.google.storage.v2.Bucket resp = getBucketWithDefaultAcls(bucket);

      Predicate<ObjectAccessControl> entityPredicate =
          objectAclEntityOrAltEq(codecs.entity().encode(entity));

      Optional<ObjectAccessControl> first =
          resp.getDefaultObjectAclList().stream().filter(entityPredicate).findFirst();

      // HttpStorageRpc defaults to null if Not Found
      return first.map(codecs.objectAcl()::decode).orElse(null);
    } catch (NotFoundException e) {
      return null;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return null;
      } else {
        throw se;
      }
    }
  }

  @Override
  public boolean deleteDefaultAcl(String bucket, Entity entity) {
    try {
      com.google.storage.v2.Bucket resp = getBucketWithDefaultAcls(bucket);
      String encode = codecs.entity().encode(entity);

      Predicate<ObjectAccessControl> entityPredicate = objectAclEntityOrAltEq(encode);

      List<ObjectAccessControl> currentDefaultAcls = resp.getDefaultObjectAclList();
      ImmutableList<ObjectAccessControl> newDefaultAcls =
          currentDefaultAcls.stream()
              .filter(entityPredicate.negate())
              .collect(ImmutableList.toImmutableList());
      if (newDefaultAcls.equals(currentDefaultAcls)) {
        // we didn't actually filter anything out, no need to send an RPC, simply return false
        return false;
      }
      long metageneration = resp.getMetageneration();

      UpdateBucketRequest req =
          createUpdateDefaultAclRequest(bucket, newDefaultAcls, metageneration);

      com.google.storage.v2.Bucket updateResult = updateBucket(req);
      // read the response to ensure there is no longer an acl for the specified entity
      Optional<ObjectAccessControl> first =
          updateResult.getDefaultObjectAclList().stream().filter(entityPredicate).findFirst();
      return !first.isPresent();
    } catch (NotFoundException e) {
      // HttpStorageRpc returns false if the bucket doesn't exist :(
      return false;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return false;
      } else {
        throw se;
      }
    }
  }

  @Override
  public Acl createDefaultAcl(String bucket, Acl acl) {
    return updateDefaultAcl(bucket, acl);
  }

  @Override
  public Acl updateDefaultAcl(String bucket, Acl acl) {
    try {
      com.google.storage.v2.Bucket resp = getBucketWithDefaultAcls(bucket);
      ObjectAccessControl encode = codecs.objectAcl().encode(acl);
      String entity = encode.getEntity();

      Predicate<ObjectAccessControl> entityPredicate = objectAclEntityOrAltEq(entity);

      ImmutableList<ObjectAccessControl> newDefaultAcls =
          Streams.concat(
                  resp.getDefaultObjectAclList().stream().filter(entityPredicate.negate()),
                  Stream.of(encode))
              .collect(ImmutableList.toImmutableList());

      UpdateBucketRequest req =
          createUpdateDefaultAclRequest(bucket, newDefaultAcls, resp.getMetageneration());

      com.google.storage.v2.Bucket updateResult = updateBucket(req);

      Optional<Acl> first =
          updateResult.getDefaultObjectAclList().stream()
              .filter(entityPredicate)
              .findFirst()
              .map(codecs.objectAcl()::decode);

      return first.orElseThrow(
          () -> new StorageException(0, "Acl update call success, but not in response"));
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public List<Acl> listDefaultAcls(String bucket) {
    try {
      com.google.storage.v2.Bucket resp = getBucketWithDefaultAcls(bucket);
      return resp.getDefaultObjectAclList().stream()
          .map(codecs.objectAcl()::decode)
          .collect(ImmutableList.toImmutableList());
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public Acl getAcl(BlobId blob, Entity entity) {
    try {
      Object req = codecs.blobId().encode(blob);
      Object resp = getObjectWithAcls(req);

      Predicate<ObjectAccessControl> entityPredicate =
          objectAclEntityOrAltEq(codecs.entity().encode(entity));

      Optional<ObjectAccessControl> first =
          resp.getAclList().stream().filter(entityPredicate).findFirst();

      // HttpStorageRpc defaults to null if Not Found
      return first.map(codecs.objectAcl()::decode).orElse(null);
    } catch (NotFoundException e) {
      return null;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return null;
      } else {
        throw se;
      }
    }
  }

  @Override
  public boolean deleteAcl(BlobId blob, Entity entity) {
    try {
      Object obj = codecs.blobId().encode(blob);
      Object resp = getObjectWithAcls(obj);
      String encode = codecs.entity().encode(entity);

      Predicate<ObjectAccessControl> entityPredicate = objectAclEntityOrAltEq(encode);

      List<ObjectAccessControl> currentDefaultAcls = resp.getAclList();
      ImmutableList<ObjectAccessControl> newDefaultAcls =
          currentDefaultAcls.stream()
              .filter(entityPredicate.negate())
              .collect(ImmutableList.toImmutableList());
      if (newDefaultAcls.equals(currentDefaultAcls)) {
        // we didn't actually filter anything out, no need to send an RPC, simply return false
        return false;
      }
      long metageneration = resp.getMetageneration();

      UpdateObjectRequest req = createUpdateObjectAclRequest(obj, newDefaultAcls, metageneration);

      Object updateResult = updateObject(req);
      // read the response to ensure there is no longer an acl for the specified entity
      Optional<ObjectAccessControl> first =
          updateResult.getAclList().stream().filter(entityPredicate).findFirst();
      return !first.isPresent();
    } catch (NotFoundException e) {
      // HttpStorageRpc returns false if the bucket doesn't exist :(
      return false;
    } catch (StorageException se) {
      if (se.getCode() == 404) {
        return false;
      } else {
        throw se;
      }
    }
  }

  @Override
  public Acl createAcl(BlobId blob, Acl acl) {
    return updateAcl(blob, acl);
  }

  @Override
  public Acl updateAcl(BlobId blob, Acl acl) {
    try {
      Object obj = codecs.blobId().encode(blob);
      Object resp = getObjectWithAcls(obj);
      ObjectAccessControl encode = codecs.objectAcl().encode(acl);
      String entity = encode.getEntity();

      Predicate<ObjectAccessControl> entityPredicate = objectAclEntityOrAltEq(entity);

      ImmutableList<ObjectAccessControl> newDefaultAcls =
          Streams.concat(
                  resp.getAclList().stream().filter(entityPredicate.negate()), Stream.of(encode))
              .collect(ImmutableList.toImmutableList());

      UpdateObjectRequest req =
          createUpdateObjectAclRequest(obj, newDefaultAcls, resp.getMetageneration());

      Object updateResult = updateObject(req);

      Optional<Acl> first =
          updateResult.getAclList().stream()
              .filter(entityPredicate)
              .findFirst()
              .map(codecs.objectAcl()::decode);

      return first.orElseThrow(
          () -> new StorageException(0, "Acl update call success, but not in response"));
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public List<Acl> listAcls(BlobId blob) {
    try {
      Object req = codecs.blobId().encode(blob);
      Object resp = getObjectWithAcls(req);
      return resp.getAclList().stream()
          .map(codecs.objectAcl()::decode)
          .collect(ImmutableList.toImmutableList());
    } catch (NotFoundException e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public HmacKey createHmacKey(ServiceAccount serviceAccount, CreateHmacKeyOption... options) {
    Opts<HmacKeyTargetOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    CreateHmacKeyRequest request =
        defaultProjectId
            .createHmacKey()
            .andThen(opts.createHmacKeysRequest())
            .apply(CreateHmacKeyRequest.newBuilder())
            .setServiceAccountEmail(serviceAccount.getEmail())
            .build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(request),
        () -> storageClient.createHmacKeyCallable().call(request, grpcCallContext),
        resp -> {
          ByteString secretKeyBytes = resp.getSecretKeyBytes();
          String b64SecretKey = BaseEncoding.base64().encode(secretKeyBytes.toByteArray());
          return HmacKey.newBuilder(b64SecretKey)
              .setMetadata(codecs.hmacKeyMetadata().decode(resp.getMetadata()))
              .build();
        });
  }

  @Override
  public Page<HmacKeyMetadata> listHmacKeys(ListHmacKeysOption... options) {
    UnaryCallable<ListHmacKeysRequest, ListHmacKeysPagedResponse> listHmacKeysCallable =
        storageClient.listHmacKeysPagedCallable();
    Opts<HmacKeyListOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());

    ListHmacKeysRequest request =
        defaultProjectId
            .listHmacKeys()
            .andThen(opts.listHmacKeysRequest())
            .apply(ListHmacKeysRequest.newBuilder())
            .build();
    try {
      ListHmacKeysPagedResponse call = listHmacKeysCallable.call(request, grpcCallContext);
      ListHmacKeysPage page = call.getPage();
      return new TransformingPageDecorator<>(
          page, codecs.hmacKeyMetadata(), getOptions(), retryAlgorithmManager.getFor(request));
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
  }

  @Override
  public HmacKeyMetadata getHmacKey(String accessId, GetHmacKeyOption... options) {
    Opts<HmacKeySourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    GetHmacKeyRequest request =
        defaultProjectId
            .getHmacKey()
            .andThen(opts.getHmacKeysRequest())
            .apply(GetHmacKeyRequest.newBuilder())
            .setAccessId(accessId)
            .build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(request),
        () -> storageClient.getHmacKeyCallable().call(request, grpcCallContext),
        codecs.hmacKeyMetadata());
  }

  @Override
  public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, DeleteHmacKeyOption... options) {
    Opts<HmacKeyTargetOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    DeleteHmacKeyRequest req =
        DeleteHmacKeyRequest.newBuilder()
            .setAccessId(hmacKeyMetadata.getAccessId())
            .setProject(projectNameCodec.encode(hmacKeyMetadata.getProjectId()))
            .build();
    Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> {
          storageClient.deleteHmacKeyCallable().call(req, grpcCallContext);
          return null;
        },
        Decoder.identity());
  }

  @Override
  public HmacKeyMetadata updateHmacKeyState(
      HmacKeyMetadata hmacKeyMetadata, HmacKeyState state, UpdateHmacKeyOption... options) {
    Opts<HmacKeyTargetOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    com.google.storage.v2.HmacKeyMetadata encode =
        codecs.hmacKeyMetadata().encode(hmacKeyMetadata).toBuilder().setState(state.name()).build();

    UpdateHmacKeyRequest.Builder builder =
        opts.updateHmacKeysRequest().apply(UpdateHmacKeyRequest.newBuilder()).setHmacKey(encode);
    UpdateHmacKeyRequest request =
        builder.setUpdateMask(FieldMask.newBuilder().addPaths("state").build()).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(request),
        () -> storageClient.updateHmacKeyCallable().call(request, grpcCallContext),
        codecs.hmacKeyMetadata());
  }

  @Override
  public Policy getIamPolicy(String bucket, BucketSourceOption... options) {
    Opts<BucketSourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    GetIamPolicyRequest.Builder builder =
        GetIamPolicyRequest.newBuilder().setResource(bucketNameCodec.encode(bucket));
    GetIamPolicyRequest req = opts.getIamPolicyRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getIamPolicyCallable().call(req, grpcCallContext),
        codecs.policyCodec());
  }

  @Override
  public Policy setIamPolicy(String bucket, Policy policy, BucketSourceOption... options) {
    Opts<BucketSourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    SetIamPolicyRequest req =
        SetIamPolicyRequest.newBuilder()
            .setResource(bucketNameCodec.encode(bucket))
            .setPolicy(codecs.policyCodec().encode(policy))
            .build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.setIamPolicyCallable().call(req, grpcCallContext),
        codecs.policyCodec());
  }

  @Override
  public List<Boolean> testIamPermissions(
      String bucket, List<String> permissions, BucketSourceOption... options) {
    Opts<BucketSourceOpt> opts = Opts.unwrap(options);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    TestIamPermissionsRequest req =
        TestIamPermissionsRequest.newBuilder()
            .setResource(bucketNameCodec.encode(bucket))
            .addAllPermissions(permissions)
            .build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.testIamPermissionsCallable().call(req, grpcCallContext),
        resp -> {
          Set<String> heldPermissions = ImmutableSet.copyOf(resp.getPermissionsList());
          return permissions.stream()
              .map(heldPermissions::contains)
              .collect(ImmutableList.toImmutableList());
        });
  }

  @Override
  public ServiceAccount getServiceAccount(String projectId) {
    GetServiceAccountRequest req =
        GetServiceAccountRequest.newBuilder()
            .setProject(projectNameCodec.encode(projectId))
            .build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getServiceAccountCallable().call(req),
        codecs.serviceAccount());
  }

  @Override
  public Notification createNotification(String bucket, NotificationInfo notificationInfo) {
    return throwNotYetImplemented(
        fmtMethodName("createNotification", String.class, NotificationInfo.class));
  }

  @Override
  public Notification getNotification(String bucket, String notificationId) {
    return throwNotYetImplemented(fmtMethodName("getNotification", String.class, String.class));
  }

  @Override
  public List<Notification> listNotifications(String bucket) {
    return throwNotYetImplemented(fmtMethodName("listNotifications", String.class));
  }

  @Override
  public boolean deleteNotification(String bucket, String notificationId) {
    return throwNotYetImplemented(fmtMethodName("deleteNotification", String.class, String.class));
  }

  @Override
  public GrpcStorageOptions getOptions() {
    return (GrpcStorageOptions) super.getOptions();
  }

  boolean isClosed() {
    return storageClient.isShutdown();
  }

  private Blob getBlob(ApiFuture<WriteObjectResponse> result) {
    try {
      WriteObjectResponse response = ApiExceptions.callAndTranslateApiException(result);
      return syntaxDecoders.blob.decode(response.getResource());
    } catch (Exception e) {
      throw StorageException.coalesce(e);
    }
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
    private final Retrying.RetryingDependencies deps;
    private final ResultRetryAlgorithm<?> resultRetryAlgorithm;

    TransformingPageDecorator(
        PageT page,
        Decoder<ResourceT, ModelT> translator,
        Retrying.RetryingDependencies deps,
        ResultRetryAlgorithm<?> resultRetryAlgorithm) {
      this.page = page;
      this.translator = translator;
      this.deps = deps;
      this.resultRetryAlgorithm = resultRetryAlgorithm;
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
      return new TransformingPageDecorator<>(
          page.getNextPage(), translator, deps, resultRetryAlgorithm);
    }

    @SuppressWarnings({"Convert2MethodRef"})
    @Override
    public Iterable<ModelT> iterateAll() {
      // iterateAll on AbstractPage isn't very friendly to decoration, as getNextPage isn't actually
      // ever called. This means we aren't able to apply our retry wrapping there.
      // Instead, what we do is create a stream which will attempt to call getNextPage repeatedly
      // until we meet some condition of exhaustion. At that point we can apply our retry logic.
      return () ->
          streamIterate(
                  page,
                  p -> p != null && p.hasNextPage(),
                  prev -> {
                    // explicitly define this callable rather than using the method reference to
                    // prevent a javac 1.8 exception
                    // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8056984
                    Callable<PageT> c = () -> prev.getNextPage();
                    return Retrying.run(deps, resultRetryAlgorithm, c, Decoder.identity());
                  })
              .filter(Objects::nonNull)
              .flatMap(p -> StreamSupport.stream(p.getValues().spliterator(), false))
              .map(translator::decode)
              .iterator();
    }

    @Override
    public Iterable<ModelT> getValues() {
      return () ->
          StreamSupport.stream(page.getValues().spliterator(), false)
              .map(translator::decode)
              .iterator();
    }

    private static <T> Stream<T> streamIterate(
        T seed, Predicate<? super T> shouldComputeNext, UnaryOperator<T> computeNext) {
      requireNonNull(seed, "seed must be non null");
      requireNonNull(shouldComputeNext, "shouldComputeNext must be non null");
      requireNonNull(computeNext, "computeNext must be non null");
      Spliterator<T> spliterator =
          new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            T prev;
            boolean started = false;
            boolean done = false;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
              // if we haven't started, emit our seed and return
              if (!started) {
                started = true;
                action.accept(seed);
                prev = seed;
                return true;
              }
              // if we've previously finished quickly return
              if (done) {
                return false;
              }
              // test whether we should try and compute the next value
              if (shouldComputeNext.test(prev)) {
                // compute the next value and figure out if we can use it
                T next = computeNext.apply(prev);
                if (next != null) {
                  action.accept(next);
                  prev = next;
                  return true;
                }
              }

              // fallthrough, if we haven't taken an action by now consider the stream done and
              // return
              done = true;
              return false;
            }
          };
      return StreamSupport.stream(spliterator, false);
    }
  }

  static <T> T throwHttpJsonOnly(String methodName) {
    return throwHttpJsonOnly(Storage.class, methodName);
  }

  static <T> T throwHttpJsonOnly(Class<?> clazz, String methodName) {
    String message =
        String.format(
            "%s#%s is only supported for HTTP_JSON transport. Please use StorageOptions.http() to construct a compatible instance.",
            clazz.getName(), methodName);
    throw new UnsupportedOperationException(message);
  }

  static <T> T throwNotYetImplemented(String methodName) {
    String message =
        String.format(
            "%s#%s is not yet implemented for GRPC transport. Please use StorageOptions.http() to construct a compatible instance in the interim.",
            Storage.class.getName(), methodName);
    throw new UnimplementedException(message, null, GrpcStatusCode.of(Code.UNIMPLEMENTED), false);
  }

  private static String fmtMethodName(String name, Class<?>... args) {
    return name
        + "("
        + Arrays.stream(args).map(Class::getName).collect(Collectors.joining(", "))
        + ")";
  }

  private ReadObjectRequest getReadObjectRequest(BlobId blob, Opts<ObjectSourceOpt> opts) {
    Object object = codecs.blobId().encode(blob);

    ReadObjectRequest.Builder builder =
        ReadObjectRequest.newBuilder().setBucket(object.getBucket()).setObject(object.getName());

    long generation = object.getGeneration();
    if (generation > 0) {
      builder.setGeneration(generation);
    }
    return opts.readObjectRequest().apply(builder).build();
  }

  private WriteObjectRequest getWriteObjectRequest(BlobInfo info, Opts<ObjectTargetOpt> opts) {
    Object object = codecs.blobInfo().encode(info);
    Object.Builder objectBuilder =
        object
            .toBuilder()
            // required if the data is changing
            .clearChecksums()
            // trimmed to shave payload size
            .clearGeneration()
            .clearMetageneration()
            .clearSize()
            .clearCreateTime()
            .clearUpdateTime();
    WriteObjectSpec.Builder specBuilder = WriteObjectSpec.newBuilder().setResource(objectBuilder);

    WriteObjectRequest.Builder requestBuilder =
        WriteObjectRequest.newBuilder().setWriteObjectSpec(specBuilder);

    return opts.writeObjectRequest().apply(requestBuilder).build();
  }

  private UnbufferedReadableByteChannelSession<Object> unbufferedReadSession(
      BlobId blob, BlobSourceOption[] options) {
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    ReadObjectRequest readObjectRequest = getReadObjectRequest(blob, opts);
    Set<StatusCode.Code> codes =
        resultRetryAlgorithmToCodes(retryAlgorithmManager.getFor(readObjectRequest));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault().withRetryableCodes(codes);
    return ResumableMedia.gapic()
        .read()
        .byteChannel(storageClient.readObjectCallable().withDefaultCallContext(grpcCallContext))
        .setAutoGzipDecompression(!opts.autoGzipDecompression())
        .unbuffered()
        .setReadObjectRequest(readObjectRequest)
        .build();
  }

  @VisibleForTesting
  ApiFuture<ResumableWrite> startResumableWrite(
      GrpcCallContext grpcCallContext, WriteObjectRequest req) {
    Set<StatusCode.Code> codes = resultRetryAlgorithmToCodes(retryAlgorithmManager.getFor(req));
    return ResumableMedia.gapic()
        .write()
        .resumableWrite(
            storageClient
                .startResumableWriteCallable()
                .withDefaultCallContext(grpcCallContext.withRetryableCodes(codes)),
            req);
  }

  private SourceObject sourceObjectEncode(SourceBlob from) {
    SourceObject.Builder to = SourceObject.newBuilder();
    to.setName(from.getName());
    ifNonNull(from.getGeneration(), to::setGeneration);
    return to.build();
  }

  private com.google.storage.v2.Bucket getBucketWithDefaultAcls(String bucketName) {
    Fields fields =
        UnifiedOpts.fields(
            ImmutableSet.of(
                BucketField.ACL, // workaround for b/261771961
                BucketField.DEFAULT_OBJECT_ACL,
                BucketField.METAGENERATION));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault();
    GetBucketRequest req =
        fields
            .getBucket()
            .apply(GetBucketRequest.newBuilder())
            .setName(bucketNameCodec.encode(bucketName))
            .build();

    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getBucketCallable().call(req, grpcCallContext),
        Decoder.identity());
  }

  private com.google.storage.v2.Bucket getBucketWithAcls(
      String bucketName, Opts<BucketSourceOpt> opts) {
    Fields fields =
        UnifiedOpts.fields(ImmutableSet.of(BucketField.ACL, BucketField.METAGENERATION));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault();
    Mapper<GetBucketRequest.Builder> mapper = opts.getBucketsRequest().andThen(fields.getBucket());
    GetBucketRequest req =
        mapper
            .apply(GetBucketRequest.newBuilder())
            .setName(bucketNameCodec.encode(bucketName))
            .build();

    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getBucketCallable().call(req, grpcCallContext),
        Decoder.identity());
  }

  private com.google.storage.v2.Bucket updateBucket(UpdateBucketRequest req) {
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.updateBucketCallable().call(req, grpcCallContext),
        Decoder.identity());
  }

  private static UpdateBucketRequest createUpdateDefaultAclRequest(
      String bucket, ImmutableList<ObjectAccessControl> newDefaultAcls, long metageneration) {
    com.google.storage.v2.Bucket update =
        com.google.storage.v2.Bucket.newBuilder()
            .setName(bucketNameCodec.encode(bucket))
            .addAllDefaultObjectAcl(newDefaultAcls)
            .build();
    Opts<BucketTargetOpt> opts =
        Opts.from(
            UnifiedOpts.fields(ImmutableSet.of(BucketField.DEFAULT_OBJECT_ACL)),
            UnifiedOpts.metagenerationMatch(metageneration));
    return opts.updateBucketsRequest()
        .apply(UpdateBucketRequest.newBuilder())
        .setBucket(update)
        .build();
  }

  private static UpdateBucketRequest createUpdateBucketAclRequest(
      String bucket, ImmutableList<BucketAccessControl> newDefaultAcls, long metageneration) {
    com.google.storage.v2.Bucket update =
        com.google.storage.v2.Bucket.newBuilder()
            .setName(bucketNameCodec.encode(bucket))
            .addAllAcl(newDefaultAcls)
            .build();
    Opts<BucketTargetOpt> opts =
        Opts.from(
            UnifiedOpts.fields(ImmutableSet.of(BucketField.ACL)),
            UnifiedOpts.metagenerationMatch(metageneration));
    return opts.updateBucketsRequest()
        .apply(UpdateBucketRequest.newBuilder())
        .setBucket(update)
        .build();
  }

  private Object getObjectWithAcls(Object obj) {
    Fields fields =
        UnifiedOpts.fields(ImmutableSet.of(BucketField.ACL, BucketField.METAGENERATION));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault();
    GetObjectRequest req =
        fields
            .getObject()
            .apply(GetObjectRequest.newBuilder())
            .setBucket(obj.getBucket())
            .setObject(obj.getName())
            .build();

    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getObjectCallable().call(req, grpcCallContext),
        Decoder.identity());
  }

  private static UpdateObjectRequest createUpdateObjectAclRequest(
      Object obj, ImmutableList<ObjectAccessControl> newAcls, long metageneration) {
    Object update =
        Object.newBuilder()
            .setBucket(obj.getBucket())
            .setName(obj.getName())
            .addAllAcl(newAcls)
            .build();
    Opts<BucketTargetOpt> opts =
        Opts.from(
            UnifiedOpts.fields(ImmutableSet.of(BlobField.ACL)),
            UnifiedOpts.metagenerationMatch(metageneration));
    return opts.updateObjectsRequest()
        .apply(UpdateObjectRequest.newBuilder())
        .setObject(update)
        .build();
  }

  private Object updateObject(UpdateObjectRequest req) {
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.updateObjectCallable().call(req, grpcCallContext),
        Decoder.identity());
  }

  private static Hasher getHasherForRequest(WriteObjectRequest req, Hasher defaultHasher) {
    if (!req.hasObjectChecksums()) {
      return defaultHasher;
    } else {
      ObjectChecksums checksums = req.getObjectChecksums();
      if (!checksums.hasCrc32C() && checksums.getMd5Hash().isEmpty()) {
        return defaultHasher;
      } else {
        return Hasher.noop();
      }
    }
  }
}
