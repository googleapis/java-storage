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

import static com.google.cloud.storage.ByteSizeConstants._15MiB;
import static com.google.cloud.storage.ByteSizeConstants._256KiB;
import static com.google.cloud.storage.Utils.bucketNameCodec;
import static com.google.cloud.storage.Utils.ifNonNull;
import static com.google.cloud.storage.Utils.projectNameCodec;
import static com.google.cloud.storage.Utils.todo;
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
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.ApiExceptions;
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
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.cloud.storage.UnbufferedWritableByteChannelSession.UnbufferedWritableByteChannel;
import com.google.cloud.storage.UnifiedOpts.BucketListOpt;
import com.google.cloud.storage.UnifiedOpts.BucketSourceOpt;
import com.google.cloud.storage.UnifiedOpts.BucketTargetOpt;
import com.google.cloud.storage.UnifiedOpts.HmacKeyListOpt;
import com.google.cloud.storage.UnifiedOpts.HmacKeySourceOpt;
import com.google.cloud.storage.UnifiedOpts.HmacKeyTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Mapper;
import com.google.cloud.storage.UnifiedOpts.ObjectListOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectSourceOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.cloud.storage.UnifiedOpts.ProjectId;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Message;
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
import com.google.storage.v2.Object;
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
  /**
   * For use in {@link #resultRetryAlgorithmToCodes(ResultRetryAlgorithm)}. Resolve all codes and
   * construct corresponding ApiExceptions.
   *
   * <p>Constructing the exceptions will walk the stack for each one. In order to avoid the stack
   * walking overhead for every Code for every invocation of read, construct the set of exceptions
   * only once and keep in this value.
   */
  private static final Set<StorageException> CODE_API_EXCEPTIONS =
      Arrays.stream(StatusCode.Code.values())
          .map(GrpcStorageImpl::statusCodeFor)
          .map(c -> ApiExceptionFactory.createException(null, c, false))
          .map(StorageException::asStorageException)
          .collect(Collectors.toSet());

  private final StorageClient storageClient;
  private final GrpcConversions codecs;
  private final GrpcRetryAlgorithmManager retryAlgorithmManager;
  private final SyntaxDecoders syntaxDecoders;

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
    try {
      UnbufferedWritableByteChannelSession<WriteObjectResponse> session =
          ResumableMedia.gapic()
              .write()
              .byteChannel(
                  storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
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

    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);

    GapicWritableByteChannelSessionBuilder channelSessionBuilder =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
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
                  storageClient
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

    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    WriteObjectRequest req = getWriteObjectRequest(blobInfo, opts);

    ApiFuture<ResumableWrite> start =
        ResumableMedia.gapic()
            .write()
            .resumableWrite(
                storageClient.startResumableWriteCallable().withDefaultCallContext(grpcCallContext),
                req);

    BufferedWritableByteChannelSession<WriteObjectResponse> session =
        ResumableMedia.gapic()
            .write()
            .byteChannel(
                storageClient.writeObjectCallable().withDefaultCallContext(grpcCallContext))
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
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    GrpcCallContext grpcCallContext =
        opts.grpcMetadataMapper().apply(GrpcCallContext.createDefault());
    GetObjectRequest.Builder builder =
        GetObjectRequest.newBuilder()
            .setBucket(bucketNameCodec.encode(blob.getBucket()))
            .setObject(blob.getName());
    GetObjectRequest req = opts.getObjectsRequest().apply(builder).build();
    return Retrying.run(
        getOptions(),
        retryAlgorithmManager.getFor(req),
        () -> storageClient.getObjectCallable().call(req, grpcCallContext),
        syntaxDecoders.blob);
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
          page, syntaxDecoders.bucket, getOptions(), retryAlgorithmManager.getFor(request));
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
          page, syntaxDecoders.blob, getOptions(), retryAlgorithmManager.getFor(req));
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
    UpdateBucketRequest.Builder builder = UpdateBucketRequest.newBuilder().setBucket(bucket);
    UpdateBucketRequest req =
        opts.updateBucketsRequest()
            .apply(builder)
            .setUpdateMask(fieldMaskGenerator(bucket))
            .build();

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
    UpdateObjectRequest.Builder builder = UpdateObjectRequest.newBuilder().setObject(object);
    UpdateObjectRequest req =
        opts.updateObjectsRequest()
            .apply(builder)
            .setUpdateMask(fieldMaskGenerator(object))
            .build();
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
        DeleteObjectRequest.newBuilder().setBucket(blob.getBucket()).setObject(blob.getName());
    ifNonNull(blob.getGeneration(), builder::setGeneration);
    DeleteObjectRequest req = opts.deleteObjectsRequest().apply(builder).build();
    try {
      Retrying.run(
          getOptions(),
          retryAlgorithmManager.getFor(req),
          () -> storageClient.deleteObjectCallable().call(req, grpcCallContext),
          Decoder.identity());
      return true;
    } catch (StorageException e) {
      return false;
    }
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
        .map(
            src ->
                SourceObject.newBuilder()
                    .setName(src.getName())
                    .setGeneration(src.getGeneration())
                    .build())
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
    Set<StatusCode.Code> codes =
        GrpcStorageImpl.resultRetryAlgorithmToCodes(retryAlgorithmManager.getFor(request));
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
    return new GrpcBlobWriteChannel(
        storageClient.writeObjectCallable(),
        () ->
            ResumableMedia.gapic()
                .write()
                .resumableWrite(
                    storageClient
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
    return throwNotYetImplemented(
        fmtMethodName("getAcl", String.class, Entity.class, BucketSourceOption[].class));
  }

  @Override
  public Acl getAcl(String bucket, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("getAcl", String.class, Entity.class));
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("deleteAcl", String.class, Entity.class, BucketSourceOption[].class));
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("deleteAcl", String.class, Entity.class));
  }

  @Override
  public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("createAcl", String.class, Acl.class, BucketSourceOption[].class));
  }

  @Override
  public Acl createAcl(String bucket, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("createAcl", String.class, Acl.class));
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("updateAcl", String.class, Acl.class, BucketSourceOption[].class));
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("updateAcl", String.class, Acl.class));
  }

  @Override
  public List<Acl> listAcls(String bucket, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("listAcls", String.class, BucketSourceOption[].class));
  }

  @Override
  public List<Acl> listAcls(String bucket) {
    return throwNotYetImplemented(fmtMethodName("listAcls", String.class));
  }

  @Override
  public Acl getDefaultAcl(String bucket, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("getDefaultAcl", String.class, Entity.class));
  }

  @Override
  public boolean deleteDefaultAcl(String bucket, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("deleteDefaultAcl", String.class, Entity.class));
  }

  @Override
  public Acl createDefaultAcl(String bucket, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("createDefaultAcl", String.class, Acl.class));
  }

  @Override
  public Acl updateDefaultAcl(String bucket, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("updateDefaultAcl", String.class, Acl.class));
  }

  @Override
  public List<Acl> listDefaultAcls(String bucket) {
    return throwNotYetImplemented(fmtMethodName("listDefaultAcls", String.class));
  }

  @Override
  public Acl getAcl(BlobId blob, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("getAcl", BlobId.class, Entity.class));
  }

  @Override
  public boolean deleteAcl(BlobId blob, Entity entity) {
    return throwNotYetImplemented(fmtMethodName("deleteAcl", BlobId.class, Entity.class));
  }

  @Override
  public Acl createAcl(BlobId blob, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("createAcl", BlobId.class, Acl.class));
  }

  @Override
  public Acl updateAcl(BlobId blob, Acl acl) {
    return throwNotYetImplemented(fmtMethodName("updateAcl", BlobId.class, Acl.class));
  }

  @Override
  public List<Acl> listAcls(BlobId blob) {
    return throwNotYetImplemented(fmtMethodName("listAcls", BlobId.class));
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
    return throwNotYetImplemented(
        fmtMethodName("getIamPolicy", String.class, BucketSourceOption[].class));
  }

  @Override
  public Policy setIamPolicy(String bucket, Policy policy, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("setIamPolicy", String.class, Policy.class, BucketSourceOption[].class));
  }

  @Override
  public List<Boolean> testIamPermissions(
      String bucket, List<String> permissions, BucketSourceOption... options) {
    return throwNotYetImplemented(
        fmtMethodName("testIamPermissions", String.class, List.class, BucketSourceOption.class));
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

  private <T> T throwHttpJsonOnly(String methodName) {
    String message =
        String.format(
            "%s#%s is only supported for HTTP_JSON transport. Please use StorageOptions.http() to construct a compatible instance.",
            Storage.class.getName(), methodName);
    throw new UnsupportedOperationException(message);
  }

  private <T> T throwNotYetImplemented(String methodName) {
    String message =
        String.format(
            "%s#%s is not yet implemented for GRPC transport. Please use StorageOptions.http() to construct a compatible instance in the interim.",
            Storage.class.getName(), methodName);
    throw new UnimplementedException(
        message, null, statusCodeFor(StatusCode.Code.UNIMPLEMENTED), false);
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
        GrpcStorageImpl.resultRetryAlgorithmToCodes(
            retryAlgorithmManager.getFor(readObjectRequest));
    GrpcCallContext grpcCallContext = GrpcCallContext.createDefault().withRetryableCodes(codes);
    return ResumableMedia.gapic()
        .read()
        .byteChannel(storageClient.readObjectCallable().withDefaultCallContext(grpcCallContext))
        .setAutoGzipDecompression(!opts.autoGzipDecompression())
        .unbuffered()
        .setReadObjectRequest(readObjectRequest)
        .build();
  }

  private FieldMask fieldMaskGenerator(Message message) {
    return FieldMask.newBuilder()
        .addAllPaths(
            message.getAllFields().entrySet().stream()
                .filter(x -> x.getValue() != null)
                .map(e -> e.getKey().getName())
                .collect(Collectors.toList()))
        .build();
  }

  /**
   * When using the retry features of the Gapic client, we are only allowed to provide a {@link
   * Set}{@code <}{@link StatusCode.Code}{@code >}. Given {@link StatusCode.Code} is an enum, we can
   * resolve the set of values from a given {@link ResultRetryAlgorithm} by evaluating each one as
   * an {@link ApiException}.
   */
  static Set<StatusCode.Code> resultRetryAlgorithmToCodes(ResultRetryAlgorithm<?> alg) {
    return CODE_API_EXCEPTIONS.stream()
        .filter(e -> alg.shouldRetry(e, null))
        .map(e -> e.apiExceptionCause.getStatusCode().getCode())
        .collect(Collectors.toSet());
  }

  private static GrpcStatusCode statusCodeFor(StatusCode.Code code) {
    switch (code) {
      case OK:
        return GrpcStatusCode.of(Code.OK);
      case CANCELLED:
        return GrpcStatusCode.of(Code.CANCELLED);
      case UNKNOWN:
        return GrpcStatusCode.of(Code.UNKNOWN);
      case INVALID_ARGUMENT:
        return GrpcStatusCode.of(Code.INVALID_ARGUMENT);
      case DEADLINE_EXCEEDED:
        return GrpcStatusCode.of(Code.DEADLINE_EXCEEDED);
      case NOT_FOUND:
        return GrpcStatusCode.of(Code.NOT_FOUND);
      case ALREADY_EXISTS:
        return GrpcStatusCode.of(Code.ALREADY_EXISTS);
      case PERMISSION_DENIED:
        return GrpcStatusCode.of(Code.PERMISSION_DENIED);
      case RESOURCE_EXHAUSTED:
        return GrpcStatusCode.of(Code.RESOURCE_EXHAUSTED);
      case FAILED_PRECONDITION:
        return GrpcStatusCode.of(Code.FAILED_PRECONDITION);
      case ABORTED:
        return GrpcStatusCode.of(Code.ABORTED);
      case OUT_OF_RANGE:
        return GrpcStatusCode.of(Code.OUT_OF_RANGE);
      case UNIMPLEMENTED:
        return GrpcStatusCode.of(Code.UNIMPLEMENTED);
      case INTERNAL:
        return GrpcStatusCode.of(Code.INTERNAL);
      case UNAVAILABLE:
        return GrpcStatusCode.of(Code.UNAVAILABLE);
      case DATA_LOSS:
        return GrpcStatusCode.of(Code.DATA_LOSS);
      case UNAUTHENTICATED:
        return GrpcStatusCode.of(Code.UNAUTHENTICATED);
      default:
        throw new IllegalStateException("Unrecognized status code: " + code);
    }
  }
}
