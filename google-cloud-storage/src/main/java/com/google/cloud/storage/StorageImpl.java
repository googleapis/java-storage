/*
 * Copyright 2015 Google LLC
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

import static com.google.cloud.storage.SignedUrlEncodingHelper.Rfc3986UriEncode;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.Executors.callable;

import com.google.api.core.ApiFuture;
import com.google.api.gax.paging.Page;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import com.google.auth.ServiceAccountSigner;
import com.google.cloud.BaseService;
import com.google.cloud.BatchResult;
import com.google.cloud.PageImpl;
import com.google.cloud.PageImpl.NextPageFetcher;
import com.google.cloud.Policy;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.BlobReadChannelV2.BlobReadChannelContext;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.PostPolicyV4.ConditionV4Type;
import com.google.cloud.storage.PostPolicyV4.PostConditionsV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.PostPolicyV4.PostPolicyV4Document;
import com.google.cloud.storage.UnifiedOpts.ObjectSourceOpt;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteRequest;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.CountingOutputStream;
import com.google.common.primitives.Ints;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StorageImpl extends BaseService<StorageOptions> implements Storage {

  private static final byte[] EMPTY_BYTE_ARRAY = {};
  private static final String EMPTY_BYTE_ARRAY_MD5 = "1B2M2Y8AsgTpgAmY7PhCfg==";
  private static final String EMPTY_BYTE_ARRAY_CRC32C = "AAAAAA==";
  private static final String PATH_DELIMITER = "/";
  /** Signed URLs are only supported through the GCS XML API endpoint. */
  private static final String STORAGE_XML_URI_SCHEME = "https";

  private static final String STORAGE_XML_URI_HOST_NAME = "storage.googleapis.com";

  private static final int DEFAULT_BUFFER_SIZE = 15 * 1024 * 1024;
  private static final int MIN_BUFFER_SIZE = 256 * 1024;

  private static final JsonConversions codecs = Conversions.json();

  final HttpRetryAlgorithmManager retryAlgorithmManager;
  final StorageRpc storageRpc;

  StorageImpl(HttpStorageOptions options) {
    super(options);
    this.retryAlgorithmManager = options.getRetryAlgorithmManager();
    this.storageRpc = options.getStorageRpcV1();
  }

  @Override
  public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
    final com.google.api.services.storage.model.Bucket bucketPb =
        codecs.bucketInfo().encode(bucketInfo);
    final Map<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(bucketInfo).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsCreate(bucketPb, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.create(bucketPb, optionsMap),
        (b) -> Conversions.json().bucketInfo().decode(b).asBucket(this));
  }

  @Override
  public Blob create(BlobInfo blobInfo, BlobTargetOption... options) {
    BlobInfo updatedInfo =
        blobInfo
            .toBuilder()
            .setMd5(EMPTY_BYTE_ARRAY_MD5)
            .setCrc32c(EMPTY_BYTE_ARRAY_CRC32C)
            .build();
    return internalCreate(updatedInfo, EMPTY_BYTE_ARRAY, 0, 0, options);
  }

  @Override
  public Blob create(BlobInfo blobInfo, byte[] content, BlobTargetOption... options) {
    content = firstNonNull(content, EMPTY_BYTE_ARRAY);
    BlobInfo updatedInfo =
        blobInfo
            .toBuilder()
            .setMd5(BaseEncoding.base64().encode(Hashing.md5().hashBytes(content).asBytes()))
            .setCrc32c(
                BaseEncoding.base64()
                    .encode(Ints.toByteArray(Hashing.crc32c().hashBytes(content).asInt())))
            .build();
    return internalCreate(updatedInfo, content, 0, content.length, options);
  }

  @Override
  public Blob create(
      BlobInfo blobInfo, byte[] content, int offset, int length, BlobTargetOption... options) {
    content = firstNonNull(content, EMPTY_BYTE_ARRAY);
    BlobInfo updatedInfo =
        blobInfo
            .toBuilder()
            .setMd5(
                BaseEncoding.base64()
                    .encode(Hashing.md5().hashBytes(content, offset, length).asBytes()))
            .setCrc32c(
                BaseEncoding.base64()
                    .encode(
                        Ints.toByteArray(
                            Hashing.crc32c().hashBytes(content, offset, length).asInt())))
            .build();
    return internalCreate(updatedInfo, content, offset, length, options);
  }

  @Override
  @Deprecated
  public Blob create(BlobInfo blobInfo, InputStream content, BlobWriteOption... options) {
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    BlobInfo.Builder builder = blobInfo.toBuilder().setMd5(null).setCrc32c(null);
    BlobInfo updated = opts.blobInfoMapper().apply(builder).build();
    StorageObject blobPb = codecs.blobInfo().encode(updated);
    InputStream inputStreamParam =
        firstNonNull(content, new ByteArrayInputStream(EMPTY_BYTE_ARRAY));
    // retries are not safe when the input is an InputStream, so we can't retry.
    BlobInfo info =
        Conversions.json()
            .blobInfo()
            .decode(storageRpc.create(blobPb, inputStreamParam, optionsMap));
    return info.asBlob(this);
  }

  private Blob internalCreate(
      BlobInfo info,
      final byte[] content,
      final int offset,
      final int length,
      BlobTargetOption... options) {
    Preconditions.checkNotNull(content);
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(info);
    final Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();

    BlobInfo updated = opts.blobInfoMapper().apply(info.toBuilder()).build();
    final StorageObject blobPb = codecs.blobInfo().encode(updated);
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectsCreate(blobPb, optionsMap);
    return run(
        algorithm,
        () ->
            storageRpc.create(
                blobPb, new ByteArrayInputStream(content, offset, length), optionsMap),
        (x) -> {
          BlobInfo info1 = Conversions.json().blobInfo().decode(x);
          return info1.asBlob(this);
        });
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, BlobWriteOption... options)
      throws IOException {
    return createFrom(blobInfo, path, DEFAULT_BUFFER_SIZE, options);
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    if (Files.isDirectory(path)) {
      throw new StorageException(0, path + " is a directory");
    }
    long size = Files.size(path);
    if (size == 0L) {
      return create(blobInfo, null, options);
    }
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    final Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    BlobInfo.Builder builder = blobInfo.toBuilder().setMd5(null).setCrc32c(null);
    BlobInfo updated = opts.blobInfoMapper().apply(builder).build();
    StorageObject encode = codecs.blobInfo().encode(updated);

    Supplier<String> uploadIdSupplier =
        ResumableMedia.startUploadForBlobInfo(
            getOptions(),
            updated,
            optionsMap,
            retryAlgorithmManager.getForResumableUploadSessionCreate(optionsMap));
    JsonResumableWrite jsonResumableWrite =
        JsonResumableWrite.of(encode, optionsMap, uploadIdSupplier.get(), 0);

    JsonResumableSession session =
        ResumableSession.json(
            HttpClientContext.from(storageRpc),
            getOptions().asRetryDependencies(),
            retryAlgorithmManager.idempotent(),
            jsonResumableWrite);
    HttpContentRange contentRange =
        HttpContentRange.of(ByteRangeSpec.relativeLength(0L, size), size);
    ResumableOperationResult<StorageObject> put =
        session.put(RewindableContent.of(path), contentRange);
    // all exception translation is taken care of down in the JsonResumableSession
    StorageObject object = put.getObject();
    if (object == null) {
      // if by some odd chance the put didn't get the StorageObject, query for it
      ResumableOperationResult<@Nullable StorageObject> query = session.query();
      object = query.getObject();
    }
    return codecs.blobInfo().decode(object).asBlob(this);
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    return createFrom(blobInfo, content, DEFAULT_BUFFER_SIZE, options);
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream content, int bufferSize, BlobWriteOption... options)
      throws IOException {

    ApiFuture<BlobInfo> objectFuture;
    try (StorageWriteChannel writer = writer(blobInfo, options)) {
      objectFuture = writer.getObject();
      uploadHelper(Channels.newChannel(content), writer, bufferSize);
    }
    // keep these two try blocks separate for the time being
    // leaving the above will cause the writer to close writing and finalizing the session and
    // (hopefully, on successful finalization) resolve our future
    try {
      BlobInfo info = objectFuture.get(10, TimeUnit.SECONDS);
      return info.asBlob(this);
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      throw StorageException.coalesce(e);
    }
  }

  /*
   * Uploads the given content to the storage using specified write channel and the given buffer
   * size. This method does not close any channels.
   */
  private static void uploadHelper(ReadableByteChannel reader, WriteChannel writer, int bufferSize)
      throws IOException {
    bufferSize = Math.max(bufferSize, MIN_BUFFER_SIZE);
    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
    writer.setChunkSize(bufferSize);

    while (reader.read(buffer) >= 0) {
      buffer.flip();
      writer.write(buffer);
      buffer.clear();
    }
  }

  @Override
  public Bucket get(String bucket, BucketGetOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    return internalBucketGet(bucket, optionsMap);
  }

  @Override
  public Blob get(String bucket, String blob, BlobGetOption... options) {
    return get(BlobId.of(bucket, blob), options);
  }

  @Override
  public Blob get(BlobId blob, BlobGetOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(blob).getRpcOptions();
    return internalGetBlob(blob, optionsMap);
  }

  @Override
  public Blob get(BlobId blob) {
    return get(blob, new BlobGetOption[0]);
  }

  private static class BucketPageFetcher implements NextPageFetcher<Bucket> {

    private static final long serialVersionUID = 8534413447247364038L;
    private final Map<StorageRpc.Option, ?> requestOptions;
    private final HttpStorageOptions serviceOptions;

    BucketPageFetcher(
        HttpStorageOptions serviceOptions, String cursor, Map<StorageRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(StorageRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
    }

    @Override
    public Page<Bucket> getNextPage() {
      return listBuckets(serviceOptions, requestOptions);
    }
  }

  private static class BlobPageFetcher implements NextPageFetcher<Blob> {

    private static final long serialVersionUID = -4308415167471093443L;
    private final Map<StorageRpc.Option, ?> requestOptions;
    private final HttpStorageOptions serviceOptions;
    private final String bucket;

    BlobPageFetcher(
        String bucket,
        HttpStorageOptions serviceOptions,
        String cursor,
        Map<StorageRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(StorageRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
      this.bucket = bucket;
    }

    @Override
    public Page<Blob> getNextPage() {
      return listBlobs(bucket, serviceOptions, requestOptions);
    }
  }

  private static class HmacKeyMetadataPageFetcher implements NextPageFetcher<HmacKeyMetadata> {

    private static final long serialVersionUID = -8637392485924772927L;
    private final HttpStorageOptions serviceOptions;
    private final HttpRetryAlgorithmManager retryAlgorithmManager;
    private final Map<StorageRpc.Option, ?> options;

    HmacKeyMetadataPageFetcher(
        HttpStorageOptions serviceOptions,
        HttpRetryAlgorithmManager retryAlgorithmManager,
        Map<StorageRpc.Option, ?> options) {
      this.serviceOptions = serviceOptions;
      this.retryAlgorithmManager = retryAlgorithmManager;
      this.options = options;
    }

    @Override
    public Page<HmacKeyMetadata> getNextPage() {
      return listHmacKeys(serviceOptions, retryAlgorithmManager, options);
    }
  }

  @Override
  public Page<Bucket> list(BucketListOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    return listBuckets(getOptions(), optionsMap);
  }

  @Override
  public Page<Blob> list(final String bucket, BlobListOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    return listBlobs(bucket, getOptions(), optionsMap);
  }

  private static Page<Bucket> listBuckets(
      final HttpStorageOptions serviceOptions, final Map<StorageRpc.Option, ?> optionsMap) {
    ResultRetryAlgorithm<?> algorithm =
        serviceOptions.getRetryAlgorithmManager().getForBucketsList(optionsMap);
    return Retrying.run(
        serviceOptions,
        algorithm,
        () -> serviceOptions.getStorageRpcV1().list(optionsMap),
        (result) -> {
          String cursor = result.x();
          Iterable<Bucket> buckets =
              result.y() == null
                  ? ImmutableList.of()
                  : Iterables.transform(
                      result.y(),
                      bucketPb ->
                          Conversions.json()
                              .bucketInfo()
                              .decode(bucketPb)
                              .asBucket(serviceOptions.getService()));
          return new PageImpl<>(
              new BucketPageFetcher(serviceOptions, cursor, optionsMap), cursor, buckets);
        });
  }

  private static Page<Blob> listBlobs(
      final String bucket,
      final HttpStorageOptions serviceOptions,
      final Map<StorageRpc.Option, ?> optionsMap) {
    ResultRetryAlgorithm<?> algorithm =
        serviceOptions.getRetryAlgorithmManager().getForObjectsList(bucket, optionsMap);
    return Retrying.run(
        serviceOptions,
        algorithm,
        () -> serviceOptions.getStorageRpcV1().list(bucket, optionsMap),
        (result) -> {
          String cursor = result.x();
          Iterable<Blob> blobs =
              result.y() == null
                  ? ImmutableList.of()
                  : Iterables.transform(
                      result.y(),
                      storageObject -> {
                        BlobInfo info = Conversions.json().blobInfo().decode(storageObject);
                        return info.asBlob(serviceOptions.getService());
                      });
          return new PageImpl<>(
              new BlobPageFetcher(bucket, serviceOptions, cursor, optionsMap), cursor, blobs);
        });
  }

  @Override
  public Bucket update(BucketInfo bucketInfo, BucketTargetOption... options) {
    Map<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(bucketInfo).getRpcOptions();
    if (bucketInfo.getModifiedFields().isEmpty()) {
      return internalBucketGet(bucketInfo.getName(), optionsMap);
    } else {
      com.google.api.services.storage.model.Bucket bucketPb =
          codecs.bucketInfo().encode(bucketInfo);
      ResultRetryAlgorithm<?> algorithm =
          retryAlgorithmManager.getForBucketsUpdate(bucketPb, optionsMap);
      return run(
          algorithm,
          () -> storageRpc.patch(bucketPb, optionsMap),
          (x) -> Conversions.json().bucketInfo().decode(x).asBucket(this));
    }
  }

  @Override
  public Blob update(BlobInfo blobInfo, BlobTargetOption... options) {
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    boolean unmodifiedBeforeOpts = blobInfo.getModifiedFields().isEmpty();
    BlobInfo.Builder builder = blobInfo.toBuilder();

    // This is a workaround until everything is in prod for both json and grpc.
    // We need to make sure that the retention field is only included in the
    // request if it was modified, so that we don't send a null object in a
    // grpc or json request.
    // todo: b/308194853
    if (blobInfo.getModifiedFields().contains(BlobField.RETENTION)) {
      builder.setRetention(blobInfo.getRetention());
    }
    BlobInfo updated = opts.blobInfoMapper().apply(builder).build();
    boolean unmodifiedAfterOpts = updated.getModifiedFields().isEmpty();
    if (unmodifiedBeforeOpts && unmodifiedAfterOpts) {
      return internalGetBlob(blobInfo.getBlobId(), optionsMap);
    } else {
      StorageObject pb = codecs.blobInfo().encode(updated);
      ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForObjectsUpdate(pb, optionsMap);
      return run(
          algorithm,
          () -> storageRpc.patch(pb, optionsMap),
          (x) -> {
            BlobInfo info = Conversions.json().blobInfo().decode(x);
            return info.asBlob(this);
          });
    }
  }

  @Override
  public Blob update(BlobInfo blobInfo) {
    return update(blobInfo, new BlobTargetOption[0]);
  }

  @Override
  public boolean delete(String bucket, BucketSourceOption... options) {
    final com.google.api.services.storage.model.Bucket bucketPb =
        codecs.bucketInfo().encode(BucketInfo.of(bucket));
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsDelete(bucketPb, optionsMap);
    return run(algorithm, () -> storageRpc.delete(bucketPb, optionsMap), Function.identity());
  }

  @Override
  public boolean delete(String bucket, String blob, BlobSourceOption... options) {
    return delete(BlobId.of(bucket, blob), options);
  }

  @Override
  public boolean delete(BlobId blob, BlobSourceOption... options) {
    final StorageObject storageObject = codecs.blobId().encode(blob);
    ImmutableMap<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(blob).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectsDelete(storageObject, optionsMap);
    return run(algorithm, () -> storageRpc.delete(storageObject, optionsMap), Function.identity());
  }

  @Override
  public boolean delete(BlobId blob) {
    return delete(blob, new BlobSourceOption[0]);
  }

  @Override
  public Blob compose(final ComposeRequest composeRequest) {
    final List<StorageObject> sources =
        Lists.newArrayListWithCapacity(composeRequest.getSourceBlobs().size());
    BlobInfo target = composeRequest.getTarget();
    for (ComposeRequest.SourceBlob sourceBlob : composeRequest.getSourceBlobs()) {
      sources.add(
          codecs
              .blobInfo()
              .encode(
                  BlobInfo.newBuilder(
                          BlobId.of(
                              target.getBucket(), sourceBlob.getName(), sourceBlob.getGeneration()))
                      .build()));
    }
    Opts<ObjectTargetOpt> targetOpts = composeRequest.getTargetOpts();
    StorageObject targetPb = codecs.blobInfo().encode(composeRequest.getTarget());
    Map<StorageRpc.Option, ?> targetOptions = targetOpts.getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectsCompose(sources, targetPb, targetOptions);
    return run(
        algorithm,
        () -> storageRpc.compose(sources, targetPb, targetOptions),
        (x) -> {
          BlobInfo info = Conversions.json().blobInfo().decode(x);
          return info.asBlob(this);
        });
  }

  @Override
  public CopyWriter copy(final CopyRequest copyRequest) {
    BlobId source = copyRequest.getSource();
    BlobInfo target = copyRequest.getTarget();
    Opts<ObjectSourceOpt> sourceOpts =
        Opts.unwrap(copyRequest.getSourceOptions()).resolveFrom(source).projectAsSource();
    Opts<ObjectTargetOpt> targetOpts =
        Opts.unwrap(copyRequest.getTargetOptions()).resolveFrom(target);

    StorageObject sourcePb = codecs.blobId().encode(source);
    StorageObject targetPb = codecs.blobInfo().encode(target);
    ImmutableMap<StorageRpc.Option, ?> sourceOptions = sourceOpts.getRpcOptions();
    ImmutableMap<StorageRpc.Option, ?> targetOptions = targetOpts.getRpcOptions();
    RewriteRequest rewriteRequest =
        new RewriteRequest(
            sourcePb,
            sourceOptions,
            copyRequest.overrideInfo(),
            targetPb,
            targetOptions,
            copyRequest.getMegabytesCopiedPerChunk());
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForObjectsRewrite(rewriteRequest);
    return run(
        algorithm,
        () -> storageRpc.openRewrite(rewriteRequest),
        (r) -> new HttpCopyWriter(getOptions(), r));
  }

  @Override
  public byte[] readAllBytes(String bucket, String blob, BlobSourceOption... options) {
    return readAllBytes(BlobId.of(bucket, blob), options);
  }

  @Override
  public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
    final StorageObject storageObject = codecs.blobId().encode(blob);
    Opts<ObjectSourceOpt> unwrap = Opts.unwrap(options);
    Opts<ObjectSourceOpt> resolve = unwrap.resolveFrom(blob);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = resolve.getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectsGet(storageObject, optionsMap);
    return run(algorithm, () -> storageRpc.load(storageObject, optionsMap), Function.identity());
  }

  @Override
  public StorageBatch batch() {
    return new StorageBatch(this.getOptions());
  }

  @Override
  public StorageReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
    return reader(BlobId.of(bucket, blob), options);
  }

  @Override
  public StorageReadChannel reader(BlobId blob, BlobSourceOption... options) {
    Opts<ObjectSourceOpt> opts = Opts.unwrap(options).resolveFrom(blob);
    StorageObject storageObject = Conversions.json().blobId().encode(blob);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    return new BlobReadChannelV2(storageObject, optionsMap, BlobReadChannelContext.from(this));
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {
    try (OutputStream outputStream = Files.newOutputStream(path)) {
      downloadTo(blob, outputStream, options);
    } catch (IOException e) {
      throw new StorageException(e);
    }
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {
    final CountingOutputStream countingOutputStream = new CountingOutputStream(outputStream);
    final StorageObject pb = codecs.blobId().encode(blob);
    ImmutableMap<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(blob).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForObjectsGet(pb, optionsMap);
    run(
        algorithm,
        callable(
            () -> {
              storageRpc.read(
                  pb, optionsMap, countingOutputStream.getCount(), countingOutputStream);
            }),
        Function.identity());
  }

  @Override
  public StorageWriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    Opts<ObjectTargetOpt> opts = Opts.unwrap(options).resolveFrom(blobInfo);
    final Map<StorageRpc.Option, ?> optionsMap = opts.getRpcOptions();
    BlobInfo.Builder builder = blobInfo.toBuilder().setMd5(null).setCrc32c(null);
    BlobInfo updated = opts.blobInfoMapper().apply(builder).build();

    StorageObject encode = codecs.blobInfo().encode(updated);
    // open the resumable session outside the write channel
    // the exception behavior of open is different from #write(ByteBuffer)
    Supplier<String> uploadIdSupplier =
        ResumableMedia.startUploadForBlobInfo(
            getOptions(),
            updated,
            optionsMap,
            retryAlgorithmManager.getForResumableUploadSessionCreate(optionsMap));
    JsonResumableWrite jsonResumableWrite =
        JsonResumableWrite.of(encode, optionsMap, uploadIdSupplier.get(), 0);
    return new BlobWriteChannelV2(BlobReadChannelContext.from(getOptions()), jsonResumableWrite);
  }

  @Override
  public StorageWriteChannel writer(URL signedURL) {
    // TODO: is it possible to know if a signed url is configured to have a constraint which makes
    //   it idempotent?
    ResultRetryAlgorithm<?> forResumableUploadSessionCreate =
        retryAlgorithmManager.getForResumableUploadSessionCreate(Collections.emptyMap());
    // open the resumable session outside the write channel
    // the exception behavior of open is different from #write(ByteBuffer)
    String signedUrlString = signedURL.toString();
    Supplier<String> uploadIdSupplier =
        ResumableMedia.startUploadForSignedUrl(
            getOptions(), signedURL, forResumableUploadSessionCreate);
    JsonResumableWrite jsonResumableWrite =
        JsonResumableWrite.of(signedUrlString, uploadIdSupplier.get(), 0);
    return new BlobWriteChannelV2(BlobReadChannelContext.from(getOptions()), jsonResumableWrite);
  }

  @Override
  public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
    EnumMap<SignUrlOption.Option, Object> optionMap = Maps.newEnumMap(SignUrlOption.Option.class);
    for (SignUrlOption option : options) {
      optionMap.put(option.getOption(), option.getValue());
    }

    boolean isV2 =
        getPreferredSignatureVersion(optionMap).equals(SignUrlOption.SignatureVersion.V2);
    boolean isV4 =
        getPreferredSignatureVersion(optionMap).equals(SignUrlOption.SignatureVersion.V4);

    ServiceAccountSigner credentials =
        (ServiceAccountSigner) optionMap.get(SignUrlOption.Option.SERVICE_ACCOUNT_CRED);
    if (credentials == null) {
      checkState(
          this.getOptions().getCredentials() instanceof ServiceAccountSigner,
          "Signing key was not provided and could not be derived");
      credentials = (ServiceAccountSigner) this.getOptions().getCredentials();
    }

    long expiration =
        isV4
            ? TimeUnit.SECONDS.convert(unit.toMillis(duration), TimeUnit.MILLISECONDS)
            : TimeUnit.SECONDS.convert(
                getOptions().getClock().millisTime() + unit.toMillis(duration),
                TimeUnit.MILLISECONDS);

    checkArgument(
        !(optionMap.containsKey(SignUrlOption.Option.VIRTUAL_HOSTED_STYLE)
            && optionMap.containsKey(SignUrlOption.Option.PATH_STYLE)
            && optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)),
        "Only one of VIRTUAL_HOSTED_STYLE, PATH_STYLE, or BUCKET_BOUND_HOST_NAME SignUrlOptions can be"
            + " specified.");

    String bucketName = slashlessBucketNameFromBlobInfo(blobInfo);
    String escapedBlobName = "";
    if (!Strings.isNullOrEmpty(blobInfo.getName())) {
      escapedBlobName = Rfc3986UriEncode(blobInfo.getName(), false);
    }

    boolean usePathStyle = shouldUsePathStyleForSignedUrl(optionMap);

    String storageXmlHostName =
        usePathStyle
            ? STORAGE_XML_URI_SCHEME + "://" + getBaseStorageHostName(optionMap)
            : STORAGE_XML_URI_SCHEME + "://" + bucketName + "." + getBaseStorageHostName(optionMap);

    if (optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)) {
      storageXmlHostName = (String) optionMap.get(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME);
    }

    String stPath =
        usePathStyle
            ? constructResourceUriPath(bucketName, escapedBlobName, optionMap)
            : constructResourceUriPath("", escapedBlobName, optionMap);

    URI path = URI.create(stPath);
    // For V2 signing, even if we don't specify the bucket in the URI path, we still need the
    // canonical resource string that we'll sign to include the bucket.
    URI pathForSigning =
        isV2 ? URI.create(constructResourceUriPath(bucketName, escapedBlobName, optionMap)) : path;

    try {
      SignatureInfo signatureInfo =
          buildSignatureInfo(
              optionMap, blobInfo, expiration, pathForSigning, credentials.getAccount());
      String unsignedPayload = signatureInfo.constructUnsignedPayload();
      byte[] signatureBytes = credentials.sign(unsignedPayload.getBytes(UTF_8));
      StringBuilder stBuilder = new StringBuilder();
      stBuilder.append(storageXmlHostName).append(path);

      if (isV4) {
        BaseEncoding encoding = BaseEncoding.base16().lowerCase();
        String signature = URLEncoder.encode(encoding.encode(signatureBytes), UTF_8.name());
        String v4QueryString = signatureInfo.constructV4QueryString();

        stBuilder.append('?');
        if (!Strings.isNullOrEmpty(v4QueryString)) {
          stBuilder.append(v4QueryString).append('&');
        }
        stBuilder.append("X-Goog-Signature=").append(signature);
      } else {
        BaseEncoding encoding = BaseEncoding.base64();
        String signature = URLEncoder.encode(encoding.encode(signatureBytes), UTF_8.name());
        String v2QueryString = signatureInfo.constructV2QueryString();

        stBuilder.append('?');
        if (!Strings.isNullOrEmpty(v2QueryString)) {
          stBuilder.append(v2QueryString).append('&');
        }
        stBuilder.append("GoogleAccessId=").append(credentials.getAccount());
        stBuilder.append("&Expires=").append(expiration);
        stBuilder.append("&Signature=").append(signature);
      }

      return new URL(stBuilder.toString());

    } catch (MalformedURLException | UnsupportedEncodingException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    EnumMap<SignUrlOption.Option, Object> optionMap = Maps.newEnumMap(SignUrlOption.Option.class);
    // Convert to a map of SignUrlOptions so we can re-use some utility methods
    for (PostPolicyV4Option option : options) {
      optionMap.put(SignUrlOption.Option.valueOf(option.getOption().name()), option.getValue());
    }

    optionMap.put(SignUrlOption.Option.SIGNATURE_VERSION, SignUrlOption.SignatureVersion.V4);

    ServiceAccountSigner credentials =
        (ServiceAccountSigner) optionMap.get(SignUrlOption.Option.SERVICE_ACCOUNT_CRED);
    if (credentials == null) {
      checkState(
          this.getOptions().getCredentials() instanceof ServiceAccountSigner,
          "Signing key was not provided and could not be derived");
      credentials = (ServiceAccountSigner) this.getOptions().getCredentials();
    }

    checkArgument(
        !(optionMap.containsKey(SignUrlOption.Option.VIRTUAL_HOSTED_STYLE)
            && optionMap.containsKey(SignUrlOption.Option.PATH_STYLE)
            && optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)),
        "Only one of VIRTUAL_HOSTED_STYLE, PATH_STYLE, or BUCKET_BOUND_HOST_NAME SignUrlOptions can be"
            + " specified.");

    String bucketName = slashlessBucketNameFromBlobInfo(blobInfo);

    boolean usePathStyle = shouldUsePathStyleForSignedUrl(optionMap);

    String url;

    if (usePathStyle) {
      url = STORAGE_XML_URI_SCHEME + "://" + STORAGE_XML_URI_HOST_NAME + "/" + bucketName + "/";
    } else {
      url = STORAGE_XML_URI_SCHEME + "://" + bucketName + "." + STORAGE_XML_URI_HOST_NAME + "/";
    }

    if (optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)) {
      url = optionMap.get(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME) + "/";
    }

    SimpleDateFormat googDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat expirationFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    googDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    yearMonthDayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    expirationFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    long timestamp = getOptions().getClock().millisTime();
    String date = googDateFormat.format(timestamp);
    String signingCredential =
        credentials.getAccount()
            + "/"
            + yearMonthDayFormat.format(timestamp)
            + "/auto/storage/goog4_request";

    Map<String, String> policyFields = new HashMap<>();

    PostConditionsV4.Builder conditionsBuilder = conditions.toBuilder();

    for (Map.Entry<String, String> entry : fields.getFieldsMap().entrySet()) {
      // Every field needs a corresponding policy condition, so add them if they're missing
      conditionsBuilder.addCustomCondition(
          ConditionV4Type.MATCHES, entry.getKey(), entry.getValue());

      policyFields.put(entry.getKey(), entry.getValue());
    }

    PostConditionsV4 v4Conditions =
        conditionsBuilder
            .addBucketCondition(ConditionV4Type.MATCHES, blobInfo.getBucket())
            .addKeyCondition(ConditionV4Type.MATCHES, blobInfo.getName())
            .addCustomCondition(ConditionV4Type.MATCHES, "x-goog-date", date)
            .addCustomCondition(ConditionV4Type.MATCHES, "x-goog-credential", signingCredential)
            .addCustomCondition(ConditionV4Type.MATCHES, "x-goog-algorithm", "GOOG4-RSA-SHA256")
            .build();
    PostPolicyV4Document document =
        PostPolicyV4Document.of(
            expirationFormat.format(timestamp + unit.toMillis(duration)), v4Conditions);
    String policy = BaseEncoding.base64().encode(document.toJson().getBytes());
    String signature =
        BaseEncoding.base16().encode(credentials.sign(policy.getBytes())).toLowerCase();

    for (PostPolicyV4.ConditionV4 condition : v4Conditions.getConditions()) {
      if (condition.type == ConditionV4Type.MATCHES) {
        policyFields.put(condition.operand1, condition.operand2);
      }
    }
    policyFields.put("key", blobInfo.getName());
    policyFields.put("x-goog-credential", signingCredential);
    policyFields.put("x-goog-algorithm", "GOOG4-RSA-SHA256");
    policyFields.put("x-goog-date", date);
    policyFields.put("x-goog-signature", signature);
    policyFields.put("policy", policy);

    policyFields.remove("bucket");

    return PostPolicyV4.of(url, policyFields);
  }

  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostPolicyV4Option... options) {
    return generateSignedPostPolicyV4(
        blobInfo, duration, unit, fields, PostConditionsV4.newBuilder().build(), options);
  }

  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    return generateSignedPostPolicyV4(
        blobInfo, duration, unit, PostFieldsV4.newBuilder().build(), conditions, options);
  }

  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo, long duration, TimeUnit unit, PostPolicyV4Option... options) {
    return generateSignedPostPolicyV4(
        blobInfo, duration, unit, PostFieldsV4.newBuilder().build(), options);
  }

  private String constructResourceUriPath(
      String slashlessBucketName,
      String escapedBlobName,
      EnumMap<SignUrlOption.Option, Object> optionMap) {
    if (Strings.isNullOrEmpty(slashlessBucketName)) {
      if (Strings.isNullOrEmpty(escapedBlobName)) {
        return PATH_DELIMITER;
      }
      if (escapedBlobName.startsWith(PATH_DELIMITER)) {
        return escapedBlobName;
      }
      return PATH_DELIMITER + escapedBlobName;
    }

    StringBuilder pathBuilder = new StringBuilder();
    pathBuilder.append(PATH_DELIMITER).append(slashlessBucketName);
    if (Strings.isNullOrEmpty(escapedBlobName)) {
      boolean isV2 =
          getPreferredSignatureVersion(optionMap).equals(SignUrlOption.SignatureVersion.V2);
      // If using virtual-hosted style URLs with V2 signing, the path string for a bucket resource
      // must end with a forward slash.
      if (optionMap.containsKey(SignUrlOption.Option.VIRTUAL_HOSTED_STYLE) && isV2) {
        pathBuilder.append(PATH_DELIMITER);
      }
      return pathBuilder.toString();
    }
    pathBuilder.append(PATH_DELIMITER);
    pathBuilder.append(escapedBlobName);
    return pathBuilder.toString();
  }

  private SignUrlOption.SignatureVersion getPreferredSignatureVersion(
      EnumMap<SignUrlOption.Option, Object> optionMap) {
    // Check for an explicitly specified version in the map.
    for (SignUrlOption.SignatureVersion version : SignUrlOption.SignatureVersion.values()) {
      if (version.equals(optionMap.get(SignUrlOption.Option.SIGNATURE_VERSION))) {
        return version;
      }
    }
    // TODO(#6362): V2 is the default, and thus can be specified either explicitly or implicitly
    // Change this to V4 once we make it the default.
    return SignUrlOption.SignatureVersion.V2;
  }

  private boolean shouldUsePathStyleForSignedUrl(EnumMap<SignUrlOption.Option, Object> optionMap) {
    // TODO(#6362): If we decide to change the default style used to generate URLs, switch this
    // logic to return false unless PATH_STYLE was explicitly specified.
    if (optionMap.containsKey(SignUrlOption.Option.VIRTUAL_HOSTED_STYLE)
        || optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)) {
      return false;
    }
    return true;
  }

  /**
   * Builds signature info.
   *
   * @param optionMap the option map
   * @param blobInfo the blob info
   * @param expiration the expiration in seconds
   * @param path the resource URI
   * @param accountEmail the account email
   * @return signature info
   */
  private SignatureInfo buildSignatureInfo(
      Map<SignUrlOption.Option, Object> optionMap,
      BlobInfo blobInfo,
      long expiration,
      URI path,
      String accountEmail) {

    HttpMethod httpVerb =
        optionMap.containsKey(SignUrlOption.Option.HTTP_METHOD)
            ? (HttpMethod) optionMap.get(SignUrlOption.Option.HTTP_METHOD)
            : HttpMethod.GET;

    SignatureInfo.Builder signatureInfoBuilder =
        new SignatureInfo.Builder(httpVerb, expiration, path);

    if (firstNonNull((Boolean) optionMap.get(SignUrlOption.Option.MD5), false)) {
      checkArgument(blobInfo.getMd5() != null, "Blob is missing a value for md5");
      signatureInfoBuilder.setContentMd5(blobInfo.getMd5());
    }

    if (firstNonNull((Boolean) optionMap.get(SignUrlOption.Option.CONTENT_TYPE), false)) {
      checkArgument(blobInfo.getContentType() != null, "Blob is missing a value for content-type");
      signatureInfoBuilder.setContentType(blobInfo.getContentType());
    }

    signatureInfoBuilder.setSignatureVersion(
        (SignUrlOption.SignatureVersion) optionMap.get(SignUrlOption.Option.SIGNATURE_VERSION));

    signatureInfoBuilder.setAccountEmail(accountEmail);

    signatureInfoBuilder.setTimestamp(getOptions().getClock().millisTime());

    ImmutableMap.Builder<String, String> extHeadersBuilder = new ImmutableMap.Builder<>();

    boolean isV4 =
        SignUrlOption.SignatureVersion.V4.equals(
            optionMap.get(SignUrlOption.Option.SIGNATURE_VERSION));
    if (isV4) { // We don't sign the host header for V2 signed URLs; only do this for V4.
      // Add the host here first, allowing it to be overridden in the EXT_HEADERS option below.
      if (optionMap.containsKey(SignUrlOption.Option.VIRTUAL_HOSTED_STYLE)) {
        extHeadersBuilder.put(
            "host",
            slashlessBucketNameFromBlobInfo(blobInfo) + "." + getBaseStorageHostName(optionMap));
      } else if (optionMap.containsKey(SignUrlOption.Option.HOST_NAME)
          || optionMap.containsKey(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME)) {
        extHeadersBuilder.put("host", getBaseStorageHostName(optionMap));
      }
    }

    if (optionMap.containsKey(SignUrlOption.Option.EXT_HEADERS)) {
      extHeadersBuilder.putAll(
          (Map<String, String>) optionMap.get(SignUrlOption.Option.EXT_HEADERS));
    }

    ImmutableMap.Builder<String, String> queryParamsBuilder = new ImmutableMap.Builder<>();
    if (optionMap.containsKey(SignUrlOption.Option.QUERY_PARAMS)) {
      queryParamsBuilder.putAll(
          (Map<String, String>) optionMap.get(SignUrlOption.Option.QUERY_PARAMS));
    }

    return signatureInfoBuilder
        .setCanonicalizedExtensionHeaders(extHeadersBuilder.build())
        .setCanonicalizedQueryParams(queryParamsBuilder.build())
        .build();
  }

  private String slashlessBucketNameFromBlobInfo(BlobInfo blobInfo) {
    // The bucket name itself should never contain a forward slash. However, parts already existed
    // in the code to check for this, so we remove the forward slashes to be safe here.
    return CharMatcher.anyOf(PATH_DELIMITER).trimFrom(blobInfo.getBucket());
  }

  /** Returns the hostname used to send requests to Cloud Storage, e.g. "storage.googleapis.com". */
  private String getBaseStorageHostName(Map<SignUrlOption.Option, Object> optionMap) {
    String specifiedBaseHostName = (String) optionMap.get(SignUrlOption.Option.HOST_NAME);
    String bucketBoundHostName =
        (String) optionMap.get(SignUrlOption.Option.BUCKET_BOUND_HOST_NAME);
    if (!Strings.isNullOrEmpty(specifiedBaseHostName)) {
      return specifiedBaseHostName.replaceFirst("http(s)?://", "");
    }
    if (!Strings.isNullOrEmpty(bucketBoundHostName)) {
      return bucketBoundHostName.replaceFirst("http(s)?://", "");
    }
    return STORAGE_XML_URI_HOST_NAME;
  }

  @Override
  public List<Blob> get(BlobId... blobIds) {
    return get(Arrays.asList(blobIds));
  }

  @Override
  public List<Blob> get(Iterable<BlobId> blobIds) {
    StorageBatch batch = batch();
    final List<Blob> results = Lists.newArrayList();
    for (BlobId blob : blobIds) {
      batch
          .get(blob)
          .notify(
              new BatchResult.Callback<Blob, StorageException>() {
                @Override
                public void success(Blob result) {
                  results.add(result);
                }

                @Override
                public void error(StorageException exception) {
                  results.add(null);
                }
              });
    }
    batch.submit();
    return Collections.unmodifiableList(results);
  }

  @Override
  public List<Blob> update(BlobInfo... blobInfos) {
    return update(Arrays.asList(blobInfos));
  }

  @Override
  public List<Blob> update(Iterable<BlobInfo> blobInfos) {
    StorageBatch batch = batch();
    final List<Blob> results = Lists.newArrayList();
    for (BlobInfo blobInfo : blobInfos) {
      batch
          .update(blobInfo)
          .notify(
              new BatchResult.Callback<Blob, StorageException>() {
                @Override
                public void success(Blob result) {
                  results.add(result);
                }

                @Override
                public void error(StorageException exception) {
                  results.add(null);
                }
              });
    }
    batch.submit();
    return Collections.unmodifiableList(results);
  }

  @Override
  public List<Boolean> delete(BlobId... blobIds) {
    return delete(Arrays.asList(blobIds));
  }

  @Override
  public List<Boolean> delete(Iterable<BlobId> blobIds) {
    StorageBatch batch = batch();
    final List<Boolean> results = Lists.newArrayList();
    for (BlobId blob : blobIds) {
      batch
          .delete(blob)
          .notify(
              new BatchResult.Callback<Boolean, StorageException>() {
                @Override
                public void success(Boolean result) {
                  results.add(result);
                }

                @Override
                public void error(StorageException exception) {
                  results.add(Boolean.FALSE);
                }
              });
    }
    batch.submit();
    return Collections.unmodifiableList(results);
  }

  @Override
  public Acl getAcl(final String bucket, final Entity entity, BucketSourceOption... options) {
    String pb = codecs.entity().encode(entity);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForBucketAclGet(pb, optionsMap);
    return run(
        algorithm, () -> storageRpc.getAcl(bucket, pb, optionsMap), codecs.bucketAcl()::decode);
  }

  @Override
  public Acl getAcl(final String bucket, final Entity entity) {
    return getAcl(bucket, entity, new BucketSourceOption[0]);
  }

  @Override
  public boolean deleteAcl(
      final String bucket, final Entity entity, BucketSourceOption... options) {
    final String pb = codecs.entity().encode(entity);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForBucketAclDelete(pb, optionsMap);
    return run(algorithm, () -> storageRpc.deleteAcl(bucket, pb, optionsMap), Function.identity());
  }

  @Override
  public boolean deleteAcl(final String bucket, final Entity entity) {
    return deleteAcl(bucket, entity, new BucketSourceOption[0]);
  }

  @Override
  public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
    final BucketAccessControl aclPb = codecs.bucketAcl().encode(acl).setBucket(bucket);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketAclCreate(aclPb, optionsMap);
    return run(
        algorithm, () -> storageRpc.createAcl(aclPb, optionsMap), codecs.bucketAcl()::decode);
  }

  @Override
  public Acl createAcl(String bucket, Acl acl) {
    return createAcl(bucket, acl, new BucketSourceOption[0]);
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
    final BucketAccessControl aclPb = codecs.bucketAcl().encode(acl).setBucket(bucket);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketAclUpdate(aclPb, optionsMap);
    return run(algorithm, () -> storageRpc.patchAcl(aclPb, optionsMap), codecs.bucketAcl()::decode);
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl) {
    return updateAcl(bucket, acl, new BucketSourceOption[0]);
  }

  @Override
  public List<Acl> listAcls(final String bucket, BucketSourceOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketAclList(bucket, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.listAcls(bucket, optionsMap),
        (answer) ->
            answer.stream()
                .map(
                    (com.google.common.base.Function<BucketAccessControl, Acl>)
                        codecs.bucketAcl()::decode)
                .collect(ImmutableList.toImmutableList()));
  }

  @Override
  public List<Acl> listAcls(final String bucket) {
    return listAcls(bucket, new BucketSourceOption[0]);
  }

  @Override
  public Acl getDefaultAcl(final String bucket, final Entity entity) {
    String pb = codecs.entity().encode(entity);
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForDefaultObjectAclGet(pb);
    return run(algorithm, () -> storageRpc.getDefaultAcl(bucket, pb), codecs.objectAcl()::decode);
  }

  @Override
  public boolean deleteDefaultAcl(final String bucket, final Entity entity) {
    String pb = codecs.entity().encode(entity);
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForDefaultObjectAclDelete(pb);
    return run(algorithm, () -> storageRpc.deleteDefaultAcl(bucket, pb), Function.identity());
  }

  @Override
  public Acl createDefaultAcl(String bucket, Acl acl) {
    final ObjectAccessControl aclPb = codecs.objectAcl().encode(acl).setBucket(bucket);
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForDefaultObjectAclCreate(aclPb);
    return run(algorithm, () -> storageRpc.createDefaultAcl(aclPb), codecs.objectAcl()::decode);
  }

  @Override
  public Acl updateDefaultAcl(String bucket, Acl acl) {
    final ObjectAccessControl aclPb = codecs.objectAcl().encode(acl).setBucket(bucket);
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForDefaultObjectAclUpdate(aclPb);
    return run(algorithm, () -> storageRpc.patchDefaultAcl(aclPb), codecs.objectAcl()::decode);
  }

  @Override
  public List<Acl> listDefaultAcls(final String bucket) {
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForDefaultObjectAclList(bucket);
    return run(
        algorithm,
        () -> storageRpc.listDefaultAcls(bucket),
        (answer) ->
            answer.stream()
                .map(
                    (com.google.common.base.Function<ObjectAccessControl, Acl>)
                        codecs.objectAcl()::decode)
                .collect(ImmutableList.toImmutableList()));
  }

  @Override
  public Acl getAcl(final BlobId blob, final Entity entity) {
    String bucket = blob.getBucket();
    String name = blob.getName();
    Long generation = blob.getGeneration();
    String pb = codecs.entity().encode(entity);
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectAclGet(bucket, name, generation, pb);
    return run(
        algorithm,
        () -> storageRpc.getAcl(bucket, name, generation, pb),
        codecs.objectAcl()::decode);
  }

  @Override
  public boolean deleteAcl(final BlobId blob, final Entity entity) {
    String bucket = blob.getBucket();
    String name = blob.getName();
    Long generation = blob.getGeneration();
    String pb = codecs.entity().encode(entity);
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectAclDelete(bucket, name, generation, pb);
    return run(
        algorithm, () -> storageRpc.deleteAcl(bucket, name, generation, pb), Function.identity());
  }

  @Override
  public Acl createAcl(final BlobId blob, final Acl acl) {
    final ObjectAccessControl aclPb =
        codecs
            .objectAcl()
            .encode(acl)
            .setBucket(blob.getBucket())
            .setObject(blob.getName())
            .setGeneration(blob.getGeneration());
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForObjectAclCreate(aclPb);
    return run(algorithm, () -> storageRpc.createAcl(aclPb), codecs.objectAcl()::decode);
  }

  @Override
  public Acl updateAcl(BlobId blob, Acl acl) {
    final ObjectAccessControl aclPb =
        codecs
            .objectAcl()
            .encode(acl)
            .setBucket(blob.getBucket())
            .setObject(blob.getName())
            .setGeneration(blob.getGeneration());
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForObjectAclUpdate(aclPb);
    return run(algorithm, () -> storageRpc.patchAcl(aclPb), codecs.objectAcl()::decode);
  }

  @Override
  public List<Acl> listAcls(final BlobId blob) {
    String bucket = blob.getBucket();
    String name = blob.getName();
    Long generation = blob.getGeneration();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectAclList(bucket, name, generation);
    return run(
        algorithm,
        () -> storageRpc.listAcls(bucket, name, generation),
        (answer) ->
            answer.stream()
                .map(
                    (com.google.common.base.Function<ObjectAccessControl, Acl>)
                        codecs.objectAcl()::decode)
                .collect(ImmutableList.toImmutableList()));
  }

  @Override
  public HmacKey createHmacKey(
      final ServiceAccount serviceAccount, final CreateHmacKeyOption... options) {
    String pb = serviceAccount.getEmail();
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForHmacKeyCreate(pb, optionsMap);
    return run(algorithm, () -> storageRpc.createHmacKey(pb, optionsMap), codecs.hmacKey()::decode);
  }

  @Override
  public Page<HmacKeyMetadata> listHmacKeys(ListHmacKeysOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    return listHmacKeys(getOptions(), retryAlgorithmManager, optionsMap);
  }

  @Override
  public HmacKeyMetadata getHmacKey(final String accessId, final GetHmacKeyOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForHmacKeyGet(accessId, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.getHmacKey(accessId, optionsMap),
        codecs.hmacKeyMetadata()::decode);
  }

  private HmacKeyMetadata updateHmacKey(
      final HmacKeyMetadata hmacKeyMetadata, final UpdateHmacKeyOption... options) {
    com.google.api.services.storage.model.HmacKeyMetadata pb =
        codecs.hmacKeyMetadata().encode(hmacKeyMetadata);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForHmacKeyUpdate(pb, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.updateHmacKey(pb, optionsMap),
        codecs.hmacKeyMetadata()::decode);
  }

  @Override
  public HmacKeyMetadata updateHmacKeyState(
      final HmacKeyMetadata hmacKeyMetadata,
      final HmacKey.HmacKeyState state,
      final UpdateHmacKeyOption... options) {
    HmacKeyMetadata updatedMetadata =
        HmacKeyMetadata.newBuilder(hmacKeyMetadata.getServiceAccount())
            .setProjectId(hmacKeyMetadata.getProjectId())
            .setAccessId(hmacKeyMetadata.getAccessId())
            .setState(state)
            .build();
    return updateHmacKey(updatedMetadata, options);
  }

  @Override
  public void deleteHmacKey(final HmacKeyMetadata metadata, final DeleteHmacKeyOption... options) {
    com.google.api.services.storage.model.HmacKeyMetadata pb =
        codecs.hmacKeyMetadata().encode(metadata);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForHmacKeyDelete(pb, optionsMap);
    run(
        algorithm,
        (Callable<Void>)
            () -> {
              storageRpc.deleteHmacKey(pb, optionsMap);
              return null;
            },
        Function.identity());
  }

  private static Page<HmacKeyMetadata> listHmacKeys(
      final HttpStorageOptions serviceOptions,
      final HttpRetryAlgorithmManager retryAlgorithmManager,
      final Map<StorageRpc.Option, ?> options) {
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForHmacKeyList(options);
    return Retrying.run(
        serviceOptions,
        algorithm,
        () -> serviceOptions.getStorageRpcV1().listHmacKeys(options),
        (result) -> {
          String cursor = result.x();
          final Iterable<HmacKeyMetadata> metadata =
              result.y() == null
                  ? ImmutableList.of()
                  : Iterables.transform(result.y(), codecs.hmacKeyMetadata()::decode);
          return new PageImpl<>(
              new HmacKeyMetadataPageFetcher(serviceOptions, retryAlgorithmManager, options),
              cursor,
              metadata);
        });
  }

  @Override
  public Policy getIamPolicy(final String bucket, BucketSourceOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsGetIamPolicy(bucket, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.getIamPolicy(bucket, optionsMap),
        apiPolicy -> Conversions.json().policyCodec().decode(apiPolicy));
  }

  @Override
  public Policy setIamPolicy(
      final String bucket, final Policy policy, BucketSourceOption... options) {
    com.google.api.services.storage.model.Policy pb =
        Conversions.json().policyCodec().encode(policy);
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsSetIamPolicy(bucket, pb, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.setIamPolicy(bucket, pb, optionsMap),
        apiPolicy -> Conversions.json().policyCodec().decode(apiPolicy));
  }

  @Override
  public List<Boolean> testIamPermissions(
      final String bucket, final List<String> permissions, BucketSourceOption... options) {
    ImmutableMap<StorageRpc.Option, ?> optionsMap = Opts.unwrap(options).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsTestIamPermissions(bucket, permissions, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.testIamPermissions(bucket, permissions, optionsMap),
        (response) -> {
          final Set<String> heldPermissions =
              response.getPermissions() != null
                  ? ImmutableSet.copyOf(response.getPermissions())
                  : ImmutableSet.<String>of();
          return permissions.stream()
              .map(heldPermissions::contains)
              .collect(ImmutableList.toImmutableList());
        });
  }

  @Override
  public Bucket lockRetentionPolicy(BucketInfo bucketInfo, BucketTargetOption... options) {
    final com.google.api.services.storage.model.Bucket bucketPb =
        codecs.bucketInfo().encode(bucketInfo);
    final Map<StorageRpc.Option, ?> optionsMap =
        Opts.unwrap(options).resolveFrom(bucketInfo).getRpcOptions();
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsLockRetentionPolicy(bucketPb, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.lockRetentionPolicy(bucketPb, optionsMap),
        (x) -> Conversions.json().bucketInfo().decode(x).asBucket(this));
  }

  @Override
  public ServiceAccount getServiceAccount(final String projectId) {
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForServiceAccountGet(projectId);
    return run(
        algorithm, () -> storageRpc.getServiceAccount(projectId), codecs.serviceAccount()::decode);
  }

  private <T, U> U run(ResultRetryAlgorithm<?> algorithm, Callable<T> c, Function<T, U> f) {
    return Retrying.run(getOptions(), algorithm, c, f);
  }

  @Override
  public Notification createNotification(
      final String bucket, final NotificationInfo notificationInfo) {
    final com.google.api.services.storage.model.Notification notificationPb =
        codecs.notificationInfo().encode(notificationInfo);
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForNotificationCreate(bucket, notificationPb);
    return run(
        algorithm,
        () -> storageRpc.createNotification(bucket, notificationPb),
        n -> codecs.notificationInfo().decode(n).asNotification(this));
  }

  @Override
  public Notification getNotification(final String bucket, final String notificationId) {
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForNotificationGet(bucket, notificationId);
    return run(
        algorithm,
        () -> storageRpc.getNotification(bucket, notificationId),
        n -> codecs.notificationInfo().decode(n).asNotification(this));
  }

  @Override
  public List<Notification> listNotifications(final String bucket) {
    ResultRetryAlgorithm<?> algorithm = retryAlgorithmManager.getForNotificationList(bucket);
    List<Notification> result =
        run(
            algorithm,
            () -> storageRpc.listNotifications(bucket),
            (answer) ->
                answer.stream()
                    .map(n -> codecs.notificationInfo().decode(n).asNotification(this))
                    .collect(ImmutableList.toImmutableList()));
    return result == null ? ImmutableList.of() : result;
  }

  @Override
  public boolean deleteNotification(final String bucket, final String notificationId) {
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForNotificationDelete(bucket, notificationId);
    return run(
        algorithm,
        () -> storageRpc.deleteNotification(bucket, notificationId),
        Function.identity());
  }

  @Override
  public HttpStorageOptions getOptions() {
    return (HttpStorageOptions) super.getOptions();
  }

  private Blob internalGetBlob(BlobId blob, Map<StorageRpc.Option, ?> optionsMap) {
    StorageObject storedObject = codecs.blobId().encode(blob);
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForObjectsGet(storedObject, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.get(storedObject, optionsMap),
        (x) -> {
          BlobInfo info = Conversions.json().blobInfo().decode(x);
          return info.asBlob(this);
        });
  }

  private Bucket internalBucketGet(String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    com.google.api.services.storage.model.Bucket bucketPb =
        codecs.bucketInfo().encode(BucketInfo.of(bucket));
    ResultRetryAlgorithm<?> algorithm =
        retryAlgorithmManager.getForBucketsGet(bucketPb, optionsMap);
    return run(
        algorithm,
        () -> storageRpc.get(bucketPb, optionsMap),
        (b) -> Conversions.json().bucketInfo().decode(b).asBucket(this));
  }
}
