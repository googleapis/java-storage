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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ServerStream;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.util.Arrays;
import java.util.Iterator;

final class GapicUnbufferedReadableByteChannel
    implements UnbufferedReadableByteChannel, ScatteringByteChannel {

  private final SettableApiFuture<Object> result;
  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
  private final ReadObjectRequest req;
  private final Hasher hasher;
  private final LazyServerStreamIterator iter;

  private boolean open = true;
  private boolean complete = false;
  private long blobOffset;

  private Object metadata;

  private ByteBuffer leftovers;

  GapicUnbufferedReadableByteChannel(
      SettableApiFuture<Object> result,
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      ReadObjectRequest req,
      Hasher hasher) {
    this.result = result;
    this.read = read;
    this.req = req;
    this.hasher = hasher;
    this.blobOffset = req.getReadOffset();
    this.iter = new LazyServerStreamIterator();
  }

  @Override
  public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
    if (complete && open) {
      close();
      return -1;
    }
    if (!open) {
      throw new ClosedChannelException();
    }

    long totalBufferCapacity = Arrays.stream(dsts).mapToLong(Buffer::remaining).sum();
    ReadCursor c = new ReadCursor(blobOffset, blobOffset + totalBufferCapacity);
    while (c.hasRemaining()) {
      if (leftovers != null) {
        copy(c, leftovers, dsts, offset, length);
        if (!leftovers.hasRemaining()) {
          leftovers = null;
        }
        continue;
      }

      if (iter.hasNext()) {
        ReadObjectResponse resp = iter.next();
        if (resp.hasMetadata()) {
          Object respMetadata = resp.getMetadata();
          if (metadata == null) {
            metadata = respMetadata;
          } else if (metadata.getGeneration() != respMetadata.getGeneration()) {
            throw closeWithError(
                String.format(
                    "Mismatch Generation between subsequent reads. Expected %d but received %d",
                    metadata.getGeneration(), respMetadata.getGeneration()));
          }

          if (!result.isDone()) {
            result.set(metadata);
          }
        }
        ChecksummedData checksummedData = resp.getChecksummedData();
        ByteBuffer content = checksummedData.getContent().asReadOnlyByteBuffer();
        // very important to know whether a crc32c value is set. Without checking, protobuf will
        // happily return 0, which is a valid crc32c value.
        if (checksummedData.hasCrc32C()) {
          Crc32cLengthKnown expected =
              Crc32cValue.of(checksummedData.getCrc32C(), checksummedData.getContent().size());
          try {
            hasher.validate(expected, content::duplicate);
          } catch (IOException e) {
            close();
            throw e;
          }
        }
        copy(c, content, dsts, offset, length);
        if (content.hasRemaining()) {
          leftovers = content;
        }
      } else {
        complete = true;
        break;
      }
    }
    long read = c.read();

    blobOffset += read;

    return read;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void close() throws IOException {
    open = false;
    iter.close();
  }

  ApiFuture<Object> getResult() {
    return result;
  }

  private void copy(ReadCursor c, ByteBuffer content, ByteBuffer[] dsts, int offset, int length) {
    long copiedBytes = Buffers.copy(content, dsts, offset, length);
    c.advance(copiedBytes);
  }

  private IOException closeWithError(String message) throws IOException {
    close();
    StorageException cause =
        new StorageException(HttpStatusCodes.STATUS_CODE_PRECONDITION_FAILED, message);
    throw new IOException(message, cause);
  }

  /**
   * Shrink wraps a beginning, offset and limit for tracking state of an individual invocation of
   * {@link #read}
   */
  private static final class ReadCursor {
    private final long beginning;
    private long offset;
    private final long limit;

    private ReadCursor(long beginning, long limit) {
      this.limit = limit;
      this.beginning = beginning;
      this.offset = beginning;
    }

    public boolean hasRemaining() {
      return limit - offset > 0;
    }

    public void advance(long incr) {
      checkArgument(incr >= 0);
      offset += incr;
    }

    public long read() {
      return offset - beginning;
    }

    @Override
    public String toString() {
      return String.format("ReadCursor{begin=%d, offset=%d, limit=%d}", beginning, offset, limit);
    }
  }

  private final class LazyServerStreamIterator implements Iterator<ReadObjectResponse>, Closeable {
    private ServerStream<ReadObjectResponse> serverStream;
    private Iterator<ReadObjectResponse> responseIterator;

    private volatile boolean streamInitialized = false;

    @Override
    public boolean hasNext() {
      try {
        return ensureResponseIteratorOpen().hasNext();
      } catch (RuntimeException e) {
        if (!result.isDone()) {
          result.setException(StorageException.coalesce(e));
        }
        throw e;
      }
    }

    @Override
    public ReadObjectResponse next() {
      try {
        return ensureResponseIteratorOpen().next();
      } catch (RuntimeException e) {
        if (!result.isDone()) {
          result.setException(StorageException.coalesce(e));
        }
        throw e;
      }
    }

    @Override
    public void close() {
      if (serverStream != null) {
        // todo: do we need to "drain" anything?
        serverStream.cancel();
      }
    }

    private Iterator<ReadObjectResponse> ensureResponseIteratorOpen() {
      boolean initialized = streamInitialized;
      if (initialized) {
        return responseIterator;
      } else {
        synchronized (this) {
          if (!streamInitialized) {
            if (serverStream == null) {
              serverStream = read.call(req);
            }
            responseIterator = serverStream.iterator();
            streamInitialized = true;
          }
          return responseIterator;
        }
      }
    }
  }
}
