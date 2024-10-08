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

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.BaseServiceException;
import com.google.cloud.storage.BlobDescriptor.ZeroCopySupport.DisposableByteString;
import com.google.cloud.storage.ResponseContentLifecycleHandle.ChildRef;
import com.google.cloud.storage.RetryContext.OnFailure;
import com.google.cloud.storage.RetryContext.OnSuccess;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ReadRange;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class BlobDescriptorStreamRead implements AutoCloseable, Closeable {

  protected final long readId;
  protected final RangeSpec rangeSpec;
  protected final RetryContext retryContext;
  protected final AtomicLong readOffset;
  protected boolean closed;
  protected boolean tombstoned;

  BlobDescriptorStreamRead(long readId, RangeSpec rangeSpec, RetryContext retryContext) {
    this(readId, rangeSpec, new AtomicLong(rangeSpec.begin()), retryContext, false);
  }

  BlobDescriptorStreamRead(
      long readId,
      RangeSpec rangeSpec,
      AtomicLong readOffset,
      RetryContext retryContext,
      boolean closed) {
    this.readId = readId;
    this.rangeSpec = rangeSpec;
    this.retryContext = retryContext;
    this.readOffset = readOffset;
    this.closed = closed;
    this.tombstoned = false;
  }

  long readOffset() {
    return readOffset.get();
  }

  abstract boolean acceptingBytes();

  abstract void accept(ChildRef childRef) throws IOException;

  abstract void eof() throws IOException;

  final void preFail() {
    tombstoned = true;
  }

  abstract ApiFuture<?> fail(Throwable t);

  abstract BlobDescriptorStreamRead withNewReadId(long newReadId);

  final ReadRange makeReadRange() {
    long currentOffset = readOffset.get();
    ReadRange.Builder b = ReadRange.newBuilder().setReadId(readId).setReadOffset(currentOffset);
    rangeSpec
        .limit()
        .ifPresent(
            limit -> {
              long readSoFar = currentOffset - rangeSpec.begin();
              b.setReadLength(limit - readSoFar);
            });
    return b.build();
  }

  @Override
  public abstract void close() throws IOException;

  <T extends Throwable> void recordError(T t, OnSuccess onSuccess, OnFailure<T> onFailure) {
    retryContext.recordError(t, onSuccess, onFailure);
  }

  boolean readyToSend() {
    return !tombstoned && !retryContext.inBackoff();
  }

  static AccumulatingRead<byte[]> createByteArrayAccumulatingRead(
      long readId,
      RangeSpec rangeSpec,
      RetryContext retryContext,
      SettableApiFuture<byte[]> complete) {
    return new ByteArrayAccumulatingRead(readId, rangeSpec, retryContext, complete);
  }

  static ZeroCopyByteStringAccumulatingRead createZeroCopyByteStringAccumulatingRead(
      long readId,
      RangeSpec rangeSpec,
      SettableApiFuture<DisposableByteString> complete,
      RetryContext retryContext) {
    return new ZeroCopyByteStringAccumulatingRead(readId, rangeSpec, retryContext, complete);
  }

  static StreamingRead streamingRead(long readId, RangeSpec rangeSpec, RetryContext retryContext) {
    return new StreamingRead(readId, rangeSpec, retryContext, false);
  }

  /** Base class of a read that will accumulate before completing by resolving a future */
  abstract static class AccumulatingRead<Result> extends BlobDescriptorStreamRead {
    protected final List<ChildRef> childRefs;
    protected final SettableApiFuture<Result> complete;

    private AccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        RetryContext retryContext,
        SettableApiFuture<Result> complete) {
      super(readId, rangeSpec, retryContext);
      this.complete = complete;
      this.childRefs = Collections.synchronizedList(new ArrayList<>());
    }

    private AccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        List<ChildRef> childRefs,
        AtomicLong readOffset,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<Result> complete) {
      super(readId, rangeSpec, readOffset, retryContext, closed);
      this.childRefs = childRefs;
      this.complete = complete;
    }

    @Override
    boolean acceptingBytes() {
      return !complete.isDone() && !tombstoned;
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      retryContext.reset();
      int size = childRef.byteString().size();
      childRefs.add(childRef);
      readOffset.addAndGet(size);
    }

    @Override
    ApiFuture<?> fail(Throwable t) {
      try {
        tombstoned = true;
        close();
      } catch (IOException e) {
        t.addSuppressed(t);
      } finally {
        complete.setException(t);
      }
      return complete;
    }

    @Override
    public void close() throws IOException {
      if (!closed) {
        retryContext.reset();
        closed = true;
        GrpcUtils.closeAll(childRefs);
      }
    }
  }

  /**
   * Base class of a read that will be processed in a streaming manner (e.g. {@link
   * ReadableByteChannel})
   */
  static class StreamingRead extends BlobDescriptorStreamRead
      implements UnbufferedReadableByteChannel {
    private final SettableApiFuture<Void> failFuture;
    private final BlockingQueue<Closeable> queue;

    private boolean complete;
    @Nullable private ChildRefHelper leftovers;

    private StreamingRead(
        long readId, RangeSpec rangeSpec, RetryContext retryContext, boolean closed) {
      this(
          readId,
          rangeSpec,
          new AtomicLong(rangeSpec.begin()),
          retryContext,
          closed,
          SettableApiFuture.create(),
          new ArrayBlockingQueue<>(2),
          false,
          null);
    }

    private StreamingRead(
        long newReadId,
        RangeSpec rangeSpec,
        AtomicLong readOffset,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<Void> failFuture,
        BlockingQueue<Closeable> queue,
        boolean complete,
        @Nullable ChildRefHelper leftovers) {
      super(newReadId, rangeSpec, readOffset, retryContext, closed);
      this.failFuture = failFuture;
      this.queue = queue;
      this.complete = complete;
      this.leftovers = leftovers;
    }

    @Override
    boolean acceptingBytes() {
      return !closed && !tombstoned;
    }

    @Override
    void accept(ChildRef childRef) throws IOException {
      retryContext.reset();
      int size = childRef.byteString().size();
      offer(childRef);
      readOffset.addAndGet(size);
    }

    @Override
    void eof() throws IOException {
      retryContext.reset();
      offer(EofMarker.INSTANCE);
    }

    @Override
    ApiFuture<?> fail(Throwable t) {
      try {
        offer(new SmuggledFailure(t));
        failFuture.set(null);
      } catch (InterruptedIOException e) {
        Thread.currentThread().interrupt();
        failFuture.setException(e);
      }
      return failFuture;
    }

    @Override
    StreamingRead withNewReadId(long newReadId) {
      tombstoned = true;
      return new StreamingRead(
          newReadId,
          rangeSpec,
          readOffset,
          retryContext,
          closed,
          failFuture,
          queue,
          complete,
          leftovers);
    }

    @Override
    public void close() throws IOException {
      if (!closed) {
        retryContext.reset();
        closed = true;
        if (leftovers != null) {
          leftovers.ref.close();
        }
        GrpcUtils.closeAll(queue);
      }
    }

    @Override
    public boolean isOpen() {
      return !closed;
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (closed) {
        throw new ClosedChannelException();
      }
      if (complete) {
        close();
        return -1;
      }

      long read = 0;
      long dstsRemaining = Buffers.totalRemaining(dsts, offset, length);
      if (leftovers != null) {
        read += leftovers.copy(dsts, offset, length);
        if (!leftovers.hasRemaining()) {
          leftovers.ref.close();
          leftovers = null;
        }
      }

      java.lang.Object poll;
      while (read < dstsRemaining && (poll = queue.poll()) != null) {
        if (poll instanceof ChildRef) {
          ChildRefHelper ref = new ChildRefHelper((ChildRef) poll);
          read += ref.copy(dsts, offset, length);
          if (ref.hasRemaining()) {
            leftovers = ref;
            break;
          } else {
            ref.ref.close();
          }
        } else if (poll == EofMarker.INSTANCE) {
          complete = true;
          if (read == 0) {
            close();
            return -1;
          }
          break;
        } else if (poll instanceof SmuggledFailure) {
          SmuggledFailure throwable = (SmuggledFailure) poll;
          BaseServiceException coalesce = StorageException.coalesce(throwable.getSmuggled());
          throw new IOException(coalesce);
        } else {
          //noinspection DataFlowIssue
          Preconditions.checkState(
              false, "unhandled queue element type %s", poll.getClass().getName());
        }
      }

      return read;
    }

    private void offer(Closeable offer) throws InterruptedIOException {
      try {
        queue.put(offer);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new InterruptedIOException();
      }
    }

    /**
     * The queue items are added to is a queue of {@link Closeable}. This class smuggles a Throwable
     * in a no-op Closable, such that the throwable can be in the queue.
     *
     * <p>Refer to {@link #fail(Throwable)} to see where this class is instantiated.
     */
    static final class SmuggledFailure implements Closeable {
      private final Throwable smuggled;

      private SmuggledFailure(Throwable smuggled) {
        this.smuggled = smuggled;
      }

      Throwable getSmuggled() {
        return smuggled;
      }

      @Override
      public void close() throws IOException {}
    }

    static final class ChildRefHelper {
      private final ChildRef ref;

      private final List<ByteBuffer> buffers;

      private ChildRefHelper(ChildRef ref) {
        this.ref = ref;
        this.buffers = ref.byteString().asReadOnlyByteBufferList();
      }

      long copy(ByteBuffer[] dsts, int offset, int length) {
        long copied = 0;
        for (ByteBuffer b : buffers) {
          long copiedBytes = Buffers.copy(b, dsts, offset, length);
          copied += copiedBytes;
          if (b.hasRemaining()) break;
        }
        return copied;
      }

      boolean hasRemaining() {
        for (ByteBuffer b : buffers) {
          if (b.hasRemaining()) return true;
        }
        return false;
      }
    }

    private static final class EofMarker implements Closeable {
      private static final EofMarker INSTANCE = new EofMarker();

      private EofMarker() {}

      @Override
      public void close() {}
    }
  }

  static final class ByteArrayAccumulatingRead extends AccumulatingRead<byte[]> {

    private ByteArrayAccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        RetryContext retryContext,
        SettableApiFuture<byte[]> complete) {
      super(readId, rangeSpec, retryContext, complete);
    }

    private ByteArrayAccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        List<ChildRef> childRefs,
        RetryContext retryContext,
        AtomicLong readOffset,
        boolean closed,
        SettableApiFuture<byte[]> complete) {
      super(readId, rangeSpec, childRefs, readOffset, retryContext, closed, complete);
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
      this.tombstoned = true;
      return new ByteArrayAccumulatingRead(
          newReadId, rangeSpec, childRefs, retryContext, readOffset, closed, complete);
    }
  }

  static final class ZeroCopyByteStringAccumulatingRead
      extends AccumulatingRead<DisposableByteString> implements DisposableByteString {

    private volatile ByteString byteString;

    private ZeroCopyByteStringAccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        RetryContext retryContext,
        SettableApiFuture<DisposableByteString> complete) {
      super(readId, rangeSpec, retryContext, complete);
    }

    public ZeroCopyByteStringAccumulatingRead(
        long readId,
        RangeSpec rangeSpec,
        List<ChildRef> childRefs,
        AtomicLong readOffset,
        RetryContext retryContext,
        boolean closed,
        SettableApiFuture<DisposableByteString> complete,
        ByteString byteString) {
      super(readId, rangeSpec, childRefs, readOffset, retryContext, closed, complete);
      this.byteString = byteString;
    }

    @Override
    public ByteString byteString() {
      return byteString;
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
      this.tombstoned = true;
      return new ZeroCopyByteStringAccumulatingRead(
          newReadId, rangeSpec, childRefs, readOffset, retryContext, closed, complete, byteString);
    }
  }
}
