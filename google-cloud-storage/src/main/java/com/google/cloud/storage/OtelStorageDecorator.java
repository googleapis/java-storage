/*
 * Copyright 2025 Google LLC
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

import static java.util.Objects.requireNonNull;

import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import com.google.api.gax.paging.Page;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.HmacKey.HmacKeyMetadata;
import com.google.cloud.storage.HmacKey.HmacKeyState;
import com.google.cloud.storage.PostPolicyV4.PostConditionsV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("DuplicatedCode")
final class OtelStorageDecorator implements Storage {

  /** Becomes the {@code otel.scope.name} attribute in a span */
  private static final String OTEL_SCOPE_NAME = "cloud.google.com/java/storage";

  @VisibleForTesting final Storage delegate;
  private final OpenTelemetry otel;
  private final Attributes baseAttributes;
  private final Tracer tracer;

  private OtelStorageDecorator(Storage delegate, OpenTelemetry otel, Attributes baseAttributes) {
    this.delegate = delegate;
    this.otel = otel;
    this.baseAttributes = baseAttributes;
    this.tracer =
        TracerDecorator.decorate(null, otel, baseAttributes, Storage.class.getName() + "/");
  }

  @Override
  public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("create")
            .setAttribute("gsutil.uri", fmtBucket(bucketInfo.getName()))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.create(bucketInfo, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob create(BlobInfo blobInfo, BlobTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("create")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.create(blobInfo, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob create(BlobInfo blobInfo, byte[] content, BlobTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("create")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.create(blobInfo, content, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob create(
      BlobInfo blobInfo, byte[] content, int offset, int length, BlobTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("create")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.create(blobInfo, content, offset, length, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  @Deprecated
  public Blob create(BlobInfo blobInfo, InputStream content, BlobWriteOption... options) {
    Span span =
        tracer
            .spanBuilder("create")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.create(blobInfo, content, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, BlobWriteOption... options)
      throws IOException {
    Span span =
        tracer
            .spanBuilder("createFrom")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createFrom(blobInfo, path, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, Path path, int bufferSize, BlobWriteOption... options)
      throws IOException {
    Span span =
        tracer
            .spanBuilder("createFrom")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createFrom(blobInfo, path, bufferSize, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob createFrom(BlobInfo blobInfo, InputStream content, BlobWriteOption... options)
      throws IOException {
    Span span =
        tracer
            .spanBuilder("createFrom")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createFrom(blobInfo, content, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob createFrom(
      BlobInfo blobInfo, InputStream content, int bufferSize, BlobWriteOption... options)
      throws IOException {
    Span span =
        tracer
            .spanBuilder("createFrom")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createFrom(blobInfo, content, bufferSize, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Bucket get(String bucket, BucketGetOption... options) {
    Span span = tracer.spanBuilder("get").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Bucket lockRetentionPolicy(BucketInfo bucket, BucketTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("lockRetentionPolicy")
            .setAttribute("gsutil.uri", fmtBucket(bucket.getName()))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.lockRetentionPolicy(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob get(String bucket, String blob, BlobGetOption... options) {
    Span span =
        tracer
            .spanBuilder("get")
            .setAttribute("gsutil.uri", String.format(Locale.US, "gs://%s/%s", bucket, blob))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(bucket, blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob get(BlobId blob, BlobGetOption... options) {
    Span span =
        tracer
            .spanBuilder("get")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob get(BlobId blob) {
    Span span =
        tracer
            .spanBuilder("get")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(blob);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob restore(BlobId blob, BlobRestoreOption... options) {
    Span span =
        tracer
            .spanBuilder("restore")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.restore(blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Page<Bucket> list(BucketListOption... options) {
    Span span = tracer.spanBuilder("list").setAttribute("gsutil.uri", "gs://").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.list(options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Page<Blob> list(String bucket, BlobListOption... options) {
    Span span =
        tracer.spanBuilder("list").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.list(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Bucket update(BucketInfo bucketInfo, BucketTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("update")
            .setAttribute("gsutil.uri", fmtBucket(bucketInfo.getName()))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.update(bucketInfo, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob update(BlobInfo blobInfo, BlobTargetOption... options) {
    Span span =
        tracer
            .spanBuilder("update")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.update(blobInfo, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob update(BlobInfo blobInfo) {
    Span span =
        tracer
            .spanBuilder("update")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.update(blobInfo);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean delete(String bucket, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("delete").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean delete(String bucket, String blob, BlobSourceOption... options) {
    Span span =
        tracer.spanBuilder("delete").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(bucket, blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean delete(BlobId blob, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("delete")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean delete(BlobId blob) {
    Span span =
        tracer
            .spanBuilder("delete")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(blob);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Blob compose(ComposeRequest composeRequest) {
    Span span =
        tracer
            .spanBuilder("compose")
            .setAttribute("gsutil.uri", composeRequest.getTarget().getBlobId().toGsUtilUri())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.compose(composeRequest);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public CopyWriter copy(CopyRequest copyRequest) {
    Span span =
        tracer
            .spanBuilder("copy")
            .setAttribute("gsutil.uri", copyRequest.getTarget().getBlobId().toGsUtilUri())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      CopyWriter copyWriter = delegate.copy(copyRequest);
      return new OtelDecoratedCopyWriter(copyWriter, span);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      span.end();
      throw t;
    }
  }

  @Override
  public byte[] readAllBytes(String bucket, String blob, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("readAllBytes")
            .setAttribute("gsutil.uri", BlobId.of(bucket, blob).toGsUtilUri())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.readAllBytes(bucket, blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("readAllBytes")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.readAllBytes(blob, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public StorageBatch batch() {
    return delegate.batch();
  }

  @Override
  public ReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("reader")
            .setAttribute("gsutil.uri", BlobId.of(bucket, blob).toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      ReadChannel reader = delegate.reader(bucket, blob, options);
      return new OtelDecoratedReadChannel(reader, span);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      span.end();
      throw t;
    }
  }

  @Override
  public ReadChannel reader(BlobId blob, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("reader")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      ReadChannel reader = delegate.reader(blob, options);
      return new OtelDecoratedReadChannel(reader, span);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      span.end();
      throw t;
    }
  }

  @Override
  public void downloadTo(BlobId blob, Path path, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("downloadTo")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      delegate.downloadTo(blob, path, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public void downloadTo(BlobId blob, OutputStream outputStream, BlobSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("downloadTo")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      delegate.downloadTo(blob, outputStream, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public WriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
    Span sessionSpan =
        tracer
            .spanBuilder("writer")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = sessionSpan.makeCurrent()) {
      WriteChannel writer = delegate.writer(blobInfo, options);
      return new OtelDecoratedWriteChannel(writer, sessionSpan);
    } catch (Throwable t) {
      sessionSpan.recordException(t);
      sessionSpan.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      sessionSpan.end();
      throw t;
    }
  }

  @Override
  public WriteChannel writer(URL signedURL) {
    Span sessionSpan = tracer.spanBuilder("writer").startSpan();
    try (Scope ignore = sessionSpan.makeCurrent()) {
      WriteChannel writer = delegate.writer(signedURL);
      return new OtelDecoratedWriteChannel(writer, sessionSpan);
    } catch (Throwable t) {
      sessionSpan.recordException(t);
      sessionSpan.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      sessionSpan.end();
      throw t;
    }
  }

  @Override
  public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
    Span span =
        tracer
            .spanBuilder("signUrl")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.signUrl(blobInfo, duration, unit, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
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
    Span span =
        tracer
            .spanBuilder("generateSignedPostPolicyV4")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.generateSignedPostPolicyV4(
          blobInfo, duration, unit, fields, conditions, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostFieldsV4 fields,
      PostPolicyV4Option... options) {
    Span span =
        tracer
            .spanBuilder("generateSignedPostPolicyV4")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.generateSignedPostPolicyV4(blobInfo, duration, unit, fields, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo,
      long duration,
      TimeUnit unit,
      PostConditionsV4 conditions,
      PostPolicyV4Option... options) {
    Span span =
        tracer
            .spanBuilder("generateSignedPostPolicyV4")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.generateSignedPostPolicyV4(blobInfo, duration, unit, conditions, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public PostPolicyV4 generateSignedPostPolicyV4(
      BlobInfo blobInfo, long duration, TimeUnit unit, PostPolicyV4Option... options) {
    Span span =
        tracer
            .spanBuilder("generateSignedPostPolicyV4")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.generateSignedPostPolicyV4(blobInfo, duration, unit, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Blob> get(BlobId... blobIds) {
    Span span = tracer.spanBuilder("get").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(blobIds);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Blob> get(Iterable<BlobId> blobIds) {
    Span span = tracer.spanBuilder("get").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.get(blobIds);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Blob> update(BlobInfo... blobInfos) {
    Span span = tracer.spanBuilder("update").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.update(blobInfos);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Blob> update(Iterable<BlobInfo> blobInfos) {
    Span span = tracer.spanBuilder("update").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.update(blobInfos);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Boolean> delete(BlobId... blobIds) {
    Span span = tracer.spanBuilder("delete").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(blobIds);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Boolean> delete(Iterable<BlobId> blobIds) {
    Span span = tracer.spanBuilder("delete").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.delete(blobIds);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl getAcl(String bucket, Entity entity, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("getAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getAcl(bucket, entity, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl getAcl(String bucket, Entity entity) {
    Span span =
        tracer.spanBuilder("getAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getAcl(bucket, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("deleteAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.deleteAcl(bucket, entity, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean deleteAcl(String bucket, Entity entity) {
    Span span =
        tracer.spanBuilder("deleteAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.deleteAcl(bucket, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("createAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createAcl(bucket, acl, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl createAcl(String bucket, Acl acl) {
    Span span =
        tracer.spanBuilder("createAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createAcl(bucket, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("updateAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.updateAcl(bucket, acl, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl updateAcl(String bucket, Acl acl) {
    Span span =
        tracer.spanBuilder("updateAcl").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.updateAcl(bucket, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Acl> listAcls(String bucket, BucketSourceOption... options) {
    Span span =
        tracer.spanBuilder("listAcls").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listAcls(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Acl> listAcls(String bucket) {
    Span span =
        tracer.spanBuilder("listAcls").setAttribute("gsutil.uri", fmtBucket(bucket)).startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listAcls(bucket);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl getDefaultAcl(String bucket, Entity entity) {
    Span span =
        tracer
            .spanBuilder("getDefaultAcl")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getDefaultAcl(bucket, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean deleteDefaultAcl(String bucket, Entity entity) {
    Span span =
        tracer
            .spanBuilder("deleteDefaultAcl")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.deleteDefaultAcl(bucket, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl createDefaultAcl(String bucket, Acl acl) {
    Span span =
        tracer
            .spanBuilder("createDefaultAcl")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createDefaultAcl(bucket, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl updateDefaultAcl(String bucket, Acl acl) {
    Span span =
        tracer
            .spanBuilder("updateDefaultAcl")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.updateDefaultAcl(bucket, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Acl> listDefaultAcls(String bucket) {
    Span span =
        tracer
            .spanBuilder("listDefaultAcls")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listDefaultAcls(bucket);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl getAcl(BlobId blob, Entity entity) {
    Span span =
        tracer
            .spanBuilder("getAcl")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getAcl(blob, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean deleteAcl(BlobId blob, Entity entity) {
    Span span =
        tracer
            .spanBuilder("deleteAcl")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.deleteAcl(blob, entity);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl createAcl(BlobId blob, Acl acl) {
    Span span =
        tracer
            .spanBuilder("createAcl")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createAcl(blob, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Acl updateAcl(BlobId blob, Acl acl) {
    Span span =
        tracer
            .spanBuilder("updateAcl")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.updateAcl(blob, acl);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Acl> listAcls(BlobId blob) {
    Span span =
        tracer
            .spanBuilder("listAcls")
            .setAttribute("gsutil.uri", blob.toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listAcls(blob);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public HmacKey createHmacKey(ServiceAccount serviceAccount, CreateHmacKeyOption... options) {
    Span span = tracer.spanBuilder("createHmacKey").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createHmacKey(serviceAccount, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Page<HmacKeyMetadata> listHmacKeys(ListHmacKeysOption... options) {
    Span span = tracer.spanBuilder("listHmacKeys").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listHmacKeys(options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public HmacKeyMetadata getHmacKey(String accessId, GetHmacKeyOption... options) {
    Span span = tracer.spanBuilder("getHmacKey").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getHmacKey(accessId, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, DeleteHmacKeyOption... options) {
    Span span = tracer.spanBuilder("deleteHmacKey").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      delegate.deleteHmacKey(hmacKeyMetadata, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public HmacKeyMetadata updateHmacKeyState(
      HmacKeyMetadata hmacKeyMetadata, HmacKeyState state, UpdateHmacKeyOption... options) {
    Span span = tracer.spanBuilder("updateHmacKeyState").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.updateHmacKeyState(hmacKeyMetadata, state, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Policy getIamPolicy(String bucket, BucketSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("getIamPolicy")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getIamPolicy(bucket, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Policy setIamPolicy(String bucket, Policy policy, BucketSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("setIamPolicy")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.setIamPolicy(bucket, policy, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Boolean> testIamPermissions(
      String bucket, List<String> permissions, BucketSourceOption... options) {
    Span span =
        tracer
            .spanBuilder("testIamPermissions")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.testIamPermissions(bucket, permissions, options);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public ServiceAccount getServiceAccount(String projectId) {
    Span span = tracer.spanBuilder("getServiceAccount").startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getServiceAccount(projectId);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Notification createNotification(String bucket, NotificationInfo notificationInfo) {
    Span span =
        tracer
            .spanBuilder("createNotification")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.createNotification(bucket, notificationInfo);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public Notification getNotification(String bucket, String notificationId) {
    Span span =
        tracer
            .spanBuilder("getNotification")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.getNotification(bucket, notificationId);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public List<Notification> listNotifications(String bucket) {
    Span span =
        tracer
            .spanBuilder("listNotifications")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.listNotifications(bucket);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public boolean deleteNotification(String bucket, String notificationId) {
    Span span =
        tracer
            .spanBuilder("deleteNotification")
            .setAttribute("gsutil.uri", fmtBucket(bucket))
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.deleteNotification(bucket, notificationId);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public void close() throws Exception {
    delegate.close();
  }

  @Override
  @BetaApi
  public BlobWriteSession blobWriteSession(BlobInfo blobInfo, BlobWriteOption... options) {
    Span sessionSpan =
        tracer
            .spanBuilder("blobWriteSession")
            .setAttribute("gsutil.uri", blobInfo.getBlobId().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = sessionSpan.makeCurrent()) {
      BlobWriteSession session = delegate.blobWriteSession(blobInfo, options);
      return new OtelDecoratedBlobWriteSession(session);
    } catch (Throwable t) {
      sessionSpan.recordException(t);
      sessionSpan.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      sessionSpan.end();
    }
  }

  @Override
  public Blob moveBlob(MoveBlobRequest request) {
    Span span =
        tracer
            .spanBuilder("moveBlob")
            .setAttribute("gsutil.uri.source", request.getSource().toGsUtilUriWithGeneration())
            .setAttribute("gsutil.uri.target", request.getTarget().toGsUtilUriWithGeneration())
            .startSpan();
    try (Scope ignore = span.makeCurrent()) {
      return delegate.moveBlob(request);
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
      throw t;
    } finally {
      span.end();
    }
  }

  @Override
  public StorageOptions getOptions() {
    return delegate.getOptions();
  }

  static Storage decorate(Storage delegate, OpenTelemetry otel, Transport transport) {
    requireNonNull(delegate, "delegate must be non null");
    requireNonNull(otel, "otel must be non null");
    if (otel == OpenTelemetry.noop()) {
      return delegate;
    }
    Attributes baseAttributes =
        Attributes.builder()
            .put("gcp.client.service", "Storage")
            .put("gcp.client.version", StorageOptions.getDefaultInstance().getLibraryVersion())
            .put("gcp.client.repo", "googleapis/java-storage")
            .put("gcp.client.artifact", "com.google.cloud:google-cloud-storage")
            .put("rpc.system", transport.toString().toLowerCase(Locale.ROOT))
            .put("service.name", "storage.googleapis.com")
            .build();
    return new OtelStorageDecorator(delegate, otel, baseAttributes);
  }

  private static @NonNull String fmtBucket(String bucket) {
    return String.format(Locale.US, "gs://%s/", bucket);
  }

  private static final class TracerDecorator implements Tracer {
    @Nullable private final Context parentContextOverride;
    private final Tracer delegate;
    private final Attributes baseAttributes;
    private final String spanNamePrefix;

    private TracerDecorator(
        @Nullable Context parentContextOverride,
        Tracer delegate,
        Attributes baseAttributes,
        String spanNamePrefix) {
      this.parentContextOverride = parentContextOverride;
      this.delegate = delegate;
      this.baseAttributes = baseAttributes;
      this.spanNamePrefix = spanNamePrefix;
    }

    private static TracerDecorator decorate(
        @Nullable Context parentContextOverride,
        OpenTelemetry otel,
        Attributes baseAttributes,
        String spanNamePrefix) {
      requireNonNull(otel, "otel must be non null");
      requireNonNull(baseAttributes, "baseAttributes must be non null");
      requireNonNull(spanNamePrefix, "spanNamePrefix must be non null");
      Tracer tracer =
          otel.getTracer(OTEL_SCOPE_NAME, StorageOptions.getDefaultInstance().getLibraryVersion());
      return new TracerDecorator(parentContextOverride, tracer, baseAttributes, spanNamePrefix);
    }

    @Override
    public SpanBuilder spanBuilder(String spanName) {
      SpanBuilder spanBuilder =
          delegate.spanBuilder(spanNamePrefix + spanName).setAllAttributes(baseAttributes);
      if (parentContextOverride != null) {
        spanBuilder.setParent(parentContextOverride);
      }
      return spanBuilder;
    }
  }

  @VisibleForTesting
  static final class OtelDecoratedReadChannel implements ReadChannel {

    @VisibleForTesting final ReadChannel reader;
    private final Span span;

    private OtelDecoratedReadChannel(ReadChannel reader, Span span) {
      this.reader = reader;
      this.span = span;
    }

    @Override
    public void seek(long position) throws IOException {
      reader.seek(position);
    }

    @Override
    public void setChunkSize(int chunkSize) {
      reader.setChunkSize(chunkSize);
    }

    @Override
    public RestorableState<ReadChannel> capture() {
      return reader.capture();
    }

    @Override
    public ReadChannel limit(long limit) {
      return reader.limit(limit);
    }

    @Override
    public long limit() {
      return reader.limit();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
      return reader.read(dst);
    }

    @Override
    public boolean isOpen() {
      return reader.isOpen();
    }

    @Override
    public void close() {
      try {
        reader.close();
      } finally {
        span.end();
      }
    }
  }

  private final class OtelDecoratedBlobWriteSession implements BlobWriteSession {

    private final BlobWriteSession delegate;
    private final Tracer tracer;

    public OtelDecoratedBlobWriteSession(BlobWriteSession delegate) {
      this.delegate = delegate;
      this.tracer =
          TracerDecorator.decorate(
              Context.current(),
              otel,
              OtelStorageDecorator.this.baseAttributes,
              BlobWriteSession.class.getName() + "/");
    }

    @Override
    public WritableByteChannel open() throws IOException {
      Span openSpan = tracer.spanBuilder("open").startSpan();
      try (Scope ignore = openSpan.makeCurrent()) {
        WritableByteChannel delegate = this.delegate.open();
        return new OtelDecoratingWritableByteChannel(delegate, openSpan);
      } catch (Throwable t) {
        openSpan.recordException(t);
        openSpan.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
        throw t;
      }
    }

    @Override
    public ApiFuture<BlobInfo> getResult() {
      return delegate.getResult();
    }

    private class OtelDecoratingWritableByteChannel implements WritableByteChannel {

      private final WritableByteChannel delegate;
      private final Span openSpan;

      private OtelDecoratingWritableByteChannel(WritableByteChannel delegate, Span openSpan) {
        this.delegate = delegate;
        this.openSpan = openSpan;
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        return delegate.write(src);
      }

      @Override
      public boolean isOpen() {
        return delegate.isOpen();
      }

      @Override
      public void close() throws IOException {
        try {
          delegate.close();
        } catch (IOException | RuntimeException e) {
          openSpan.recordException(e);
          openSpan.setStatus(StatusCode.ERROR, e.getClass().getSimpleName());
          throw e;
        } finally {
          openSpan.end();
        }
      }
    }
  }

  @VisibleForTesting
  static final class OtelDecoratedWriteChannel implements WriteChannel {
    @VisibleForTesting final WriteChannel delegate;
    private final Span openSpan;

    private OtelDecoratedWriteChannel(WriteChannel delegate, Span openSpan) {
      this.delegate = delegate;
      this.openSpan = openSpan;
    }

    @Override
    public void setChunkSize(int chunkSize) {
      delegate.setChunkSize(chunkSize);
    }

    @Override
    public RestorableState<WriteChannel> capture() {
      return delegate.capture();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      return delegate.write(src);
    }

    @Override
    public boolean isOpen() {
      return delegate.isOpen();
    }

    @Override
    public void close() throws IOException {
      try {
        delegate.close();
      } catch (IOException | RuntimeException e) {
        openSpan.recordException(e);
        openSpan.setStatus(StatusCode.ERROR, e.getClass().getSimpleName());
        throw e;
      } finally {
        openSpan.end();
      }
    }
  }

  private final class OtelDecoratedCopyWriter extends CopyWriter {

    private final CopyWriter copyWriter;
    private final Span span;
    private final Context parentContext;
    private final Tracer tracer;

    public OtelDecoratedCopyWriter(CopyWriter copyWriter, Span span) {
      this.copyWriter = copyWriter;
      this.span = span;
      this.parentContext = Context.current();
      this.tracer =
          TracerDecorator.decorate(
              Context.current(),
              otel,
              OtelStorageDecorator.this.baseAttributes,
              CopyWriter.class.getName() + "/");
    }

    @Override
    public Blob getResult() {
      try {
        return copyWriter.getResult();
      } catch (Throwable t) {
        span.recordException(t);
        span.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
        throw t;
      } finally {
        span.end();
      }
    }

    @Override
    public long getBlobSize() {
      return copyWriter.getBlobSize();
    }

    @Override
    public boolean isDone() {
      boolean done = copyWriter.isDone();
      if (done) {
        span.end();
      }
      return done;
    }

    @Override
    public long getTotalBytesCopied() {
      return copyWriter.getTotalBytesCopied();
    }

    @Override
    public RestorableState<CopyWriter> capture() {
      return copyWriter.capture();
    }

    @Override
    public void copyChunk() {
      Span copyChunkSpan = tracer.spanBuilder("copyChunk").setParent(parentContext).startSpan();
      try (Scope ignore = copyChunkSpan.makeCurrent()) {
        copyWriter.copyChunk();
      } catch (Throwable t) {
        copyChunkSpan.recordException(t);
        copyChunkSpan.setStatus(StatusCode.ERROR, t.getClass().getSimpleName());
        span.end();
        throw t;
      } finally {
        copyChunkSpan.end();
      }
    }
  }
}
