/*
 * Copyright 2024 Google LLC
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

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.cloud.storage.BlobDescriptor.ZeroCopySupport.DisposableByteString;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.cloud.storage.RetryContext.OnSuccess;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import com.google.storage.v2.ReadRange;
import io.grpc.StatusRuntimeException;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class BlobDescriptorStreamRead implements AutoCloseable, Closeable {

  protected final long readId;
  protected final ReadCursor readCursor;
  protected final List<ChildRef> childRefs;
  protected final RetryContext retryContext;
  protected boolean closed;

  private BlobDescriptorStreamRead(long readId, ReadCursor readCursor, RetryContext retryContext) {
    this(readId, readCursor, Collections.synchronizedList(new ArrayList<>()), retryContext, false);
  }

  private BlobDescriptorStreamRead(
      long readId,
      ReadCursor readCursor,
      List<ChildRef> childRefs,
      RetryContext retryContext,
      boolean closed) {
    this.readId = readId;
    this.readCursor = readCursor;
    this.childRefs = childRefs;
    this.retryContext = retryContext;
    this.closed = closed;
  }

  ReadCursor getReadCursor() {
    return readCursor;
  }

  abstract boolean acceptingBytes();

  abstract void accept(ChildRef childRef) throws IOException;

  abstract void eof() throws IOException;

  final void fail(Status status) throws IOException {
    io.grpc.Status grpcStatus = io.grpc.Status.fromCodeValue(status.getCode());
    if (!status.getMessage().isEmpty()) {
      grpcStatus = grpcStatus.withDescription(status.getMessage());
    }
    StatusRuntimeException cause = grpcStatus.asRuntimeException();
    ApiException apiException =
        ApiExceptionFactory.createException(cause, GrpcStatusCode.of(grpcStatus.getCode()), false);
    fail(apiException);
  }

  final void unsafeFail(Throwable t) {
    try {
      fail(t);
    } catch (IOException e) {
      // todo: better exception than this
      throw new RuntimeException(e);
    }
  }

  abstract void fail(Throwable t) throws IOException;

  abstract BlobDescriptorStreamRead withNewReadId(long newReadId);

  final ReadRange makeReadRange() {
    return ReadRange.newBuilder()
        .setReadId(readId)
        .setReadOffset(readCursor.position())
        .setReadLength(readCursor.remaining())
        .build();
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      retryContext.reset();
      closed = true;
      GrpcUtils.closeAll(childRefs);
    }
  }

  abstract void recordError(Throwable t, OnSuccess onSuccess, OnFailure onFailure);

  static AccumulatingRead<byte[]> createByteArrayAccumulatingRead(
      long readId,
      ReadCursor readCursor,
      RetryContext retryContext,
      SettableApiFuture<byte[]> complete) {
    return new ByteArrayAccumulatingRead(readId, readCursor, retryContext, complete);
  }

  static ZeroCopyByteStringAccumulatingRead createZeroCopyByteStringAccumulatingRead(
      long readId,
      ReadCursor readCursor,
      SettableApiFuture<DisposableByteString> complete,
      RetryContext retryContext) {
    return new ZeroCopyByteStringAccumulatingRead(readId, readCursor, retryContext, complete);
  }

  /** Base class of a read that will accumulate before completing by resolving a future */
  abstract static class AccumulatingRead<Result> extends BlobDescriptorStreamRead {
    protected final SettableApiFuture<Result> complete;

    private AccumulatingRead(
        long readId,
        ReadCursor readCursor,
        RetryContext retryContext,
        SettableApiFuture<Result> complete) {
      super(readId, readCursor, retryContext);
      this.complete = complete;
    }

    private AccumulatingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<Result> complete) {
      super(readId, readCursor, childRefs, retryContext, closed);
      this.complete = complete;
    }

    @Override
    boolean acceptingBytes() {
      return !complete.isDone() && readCursor.hasRemaining();
    }

    @Override
    void fail(Throwable t) throws IOException {
      try {
        complete.setException(t);
      } finally {
        close();
      }
    }

    @Override
    void recordError(Throwable t, OnSuccess onSuccess, OnFailure onFailure) {
      retryContext.recordError(t, onSuccess, onFailure);
    }
  }

  /**
   * Base class of a read that will be processed in a streaming manner (e.g. {@link
   * java.nio.channels.ReadableByteChannel})
   */
  abstract static class StreamingRead extends BlobDescriptorStreamRead {
    private StreamingRead(long readId, long readOffset, long readLimit, RetryContext retryContext) {
      super(readId, new ReadCursor(readOffset, readOffset + readLimit), retryContext);
    }

    private StreamingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        boolean closed) {
      super(readId, readCursor, childRefs, retryContext, closed);
    }
  }

  static final class ByteArrayAccumulatingRead extends AccumulatingRead<byte[]> {

    private ByteArrayAccumulatingRead(
        long readId,
        ReadCursor readCursor,
        RetryContext retryContext,
        SettableApiFuture<byte[]> complete) {
      super(readId, readCursor, retryContext, complete);
    }

    private ByteArrayAccumulatingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<byte[]> complete) {
      super(readId, readCursor, childRefs, retryContext, closed, complete);
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      retryContext.reset();
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readCursor.advance(size);
    }

    @Override
    void eof() throws IOException {
      retryContext.reset();
      try {
        ByteString base = ByteString.empty();
        for (ChildRef ref : childRefs) {
          base = base.concat(ref.byteString());
        }
        complete.set(base.toByteArray());
      } finally {
        close();
      }
    }

    @Override
    ByteArrayAccumulatingRead withNewReadId(long newReadId) {
      return new ByteArrayAccumulatingRead(
          newReadId, readCursor, childRefs, retryContext, closed, complete);
    }
  }

  static final class ZeroCopyByteStringAccumulatingRead
      extends AccumulatingRead<DisposableByteString> implements DisposableByteString {

    private volatile ByteString byteString;

    private ZeroCopyByteStringAccumulatingRead(
        long readId,
        ReadCursor readCursor,
        RetryContext retryContext,
        SettableApiFuture<DisposableByteString> complete) {
      super(readId, readCursor, retryContext, complete);
    }

    public ZeroCopyByteStringAccumulatingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<DisposableByteString> complete,
        ByteString byteString) {
      super(readId, readCursor, childRefs, retryContext, closed, complete);
      this.byteString = byteString;
    }

    @Override
    public ByteString byteString() {
      return byteString;
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      retryContext.reset();
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readCursor.advance(size);
    }

    @Override
    void eof() throws IOException {
      retryContext.reset();
      ByteString base = ByteString.empty();
      for (ChildRef ref : childRefs) {
        base = base.concat(ref.byteString());
      }
      byteString = base;
      complete.set(this);
    }

    @Override
    ZeroCopyByteStringAccumulatingRead withNewReadId(long newReadId) {
      return new ZeroCopyByteStringAccumulatingRead(
          newReadId, readCursor, childRefs, retryContext, closed, complete, byteString);
    }
  }
}
