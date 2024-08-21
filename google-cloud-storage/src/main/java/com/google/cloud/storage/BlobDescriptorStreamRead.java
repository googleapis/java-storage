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
  protected boolean closed;

  private BlobDescriptorStreamRead(long readId, ReadCursor readCursor) {
    this(readId, readCursor, Collections.synchronizedList(new ArrayList<>()), false);
  }

  private BlobDescriptorStreamRead(
      long readId, ReadCursor readCursor, List<ChildRef> childRefs, boolean closed) {
    this.readId = readId;
    this.readCursor = readCursor;
    this.childRefs = childRefs;
    this.closed = closed;
  }

  abstract void accept(ChildRef childRef) throws IOException;

  abstract void eof() throws IOException;

  abstract void fail(Status status) throws IOException;

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
      closed = true;
      GrpcUtils.closeAll(childRefs);
    }
  }

  static AccumulatingRead<byte[]> createByteArrayAccumulatingRead(
      long readId, ByteRangeSpec range, SettableApiFuture<byte[]> complete) {
    return new ByteArrayAccumulatingRead(
        readId,
        new ReadCursor(range.beginOffset(), range.beginOffset() + range.length()),
        complete);
  }

  static AccumulatingRead<DisposableByteString> createZeroCopyByteStringAccumulatingRead(
      long readId, ByteRangeSpec range, SettableApiFuture<DisposableByteString> complete) {
    return new ZeroCopyByteStringAccumulatingRead(
        readId,
        new ReadCursor(range.beginOffset(), range.beginOffset() + range.length()),
        complete);
  }

  /** Base class of a read that will accumulate before completing by resolving a future */
  abstract static class AccumulatingRead<Result> extends BlobDescriptorStreamRead {
    protected final SettableApiFuture<Result> complete;

    private AccumulatingRead(
        long readId, ReadCursor readCursor, SettableApiFuture<Result> complete) {
      super(readId, readCursor);
      this.complete = complete;
    }

    private AccumulatingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        boolean closed,
        SettableApiFuture<Result> complete) {
      super(readId, readCursor, childRefs, closed);
      this.complete = complete;
    }

    @Override
    void fail(Status status) throws IOException {
      io.grpc.Status grpcStatus = io.grpc.Status.fromCodeValue(status.getCode());
      StatusRuntimeException cause = grpcStatus.asRuntimeException();
      ApiException apiException =
          ApiExceptionFactory.createException(
              cause, GrpcStatusCode.of(grpcStatus.getCode()), false);
      complete.setException(apiException);
    }
  }

  /**
   * Base class of a read that will be processed in a streaming manner (e.g. {@link
   * java.nio.channels.ReadableByteChannel})
   */
  abstract static class StreamingRead extends BlobDescriptorStreamRead {
    private StreamingRead(long readId, long readOffset, long readLimit) {
      super(readId, new ReadCursor(readOffset, readOffset + readLimit));
    }

    public StreamingRead(
        long readId, ReadCursor readCursor, List<ChildRef> childRefs, boolean closed) {
      super(readId, readCursor, childRefs, closed);
    }
  }

  static final class ByteArrayAccumulatingRead extends AccumulatingRead<byte[]> {

    private ByteArrayAccumulatingRead(
        long readId, ReadCursor readCursor, SettableApiFuture<byte[]> complete) {
      super(readId, readCursor, complete);
    }

    private ByteArrayAccumulatingRead(
        long readId,
        ReadCursor readCursor,
        List<ChildRef> childRefs,
        boolean closed,
        SettableApiFuture<byte[]> complete) {
      super(readId, readCursor, childRefs, closed, complete);
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readCursor.advance(size);
    }

    @Override
    void eof() throws IOException {
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
      return new ByteArrayAccumulatingRead(newReadId, readCursor, childRefs, closed, complete);
    }
  }

  static final class ZeroCopyByteStringAccumulatingRead
      extends AccumulatingRead<DisposableByteString> implements DisposableByteString {

    private volatile ByteString byteString;

    private ZeroCopyByteStringAccumulatingRead(
        long readId, ReadCursor readCursor, SettableApiFuture<DisposableByteString> complete) {
      super(readId, readCursor, complete);
    }

    @Override
    public ByteString byteString() {
      return byteString;
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readCursor.advance(size);
    }

    @Override
    void eof() throws IOException {
      ByteString base = ByteString.empty();
      for (ChildRef ref : childRefs) {
        base = base.concat(ref.byteString());
      }
      byteString = base;
      complete.set(this);
    }

    @Override
    ZeroCopyByteStringAccumulatingRead withNewReadId(long newReadId) {
      return new ZeroCopyByteStringAccumulatingRead(newReadId, readCursor, complete);
    }
  }
}
