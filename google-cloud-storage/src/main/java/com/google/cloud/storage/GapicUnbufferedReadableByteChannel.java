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

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ServerStream;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthUnknown;
import com.google.cloud.storage.UnbufferedReadableByteChannelSession.UnbufferedReadableByteChannel;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.Object;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
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
  // TODO: keep the stream open and continue reading from it
  // refer to gcsio about draining as to not leak resources
  // more from gcsio
  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read;
  private final Object obj;
  private final Hasher hasher;

  private boolean open = true;
  private boolean complete = false;
  private long blobOffset = 0;
  private long blobLimit = Long.MAX_VALUE;
  private long totalSize = Long.MAX_VALUE; // initial sentinel value, will be updated upon responses

  private ServerStream<ReadObjectResponse> serverStream;
  private Iterator<ReadObjectResponse> responseIterator;
  private ByteBuffer leftovers;

  GapicUnbufferedReadableByteChannel(
      SettableApiFuture<Object> result, // TODO: track and set this
      ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> read,
      Object obj,
      Hasher hasher) {
    this.result = result;
    this.read = read;
    this.obj = obj;
    ReadObjectRequest req =
        ReadObjectRequest.newBuilder()
            .setBucket(obj.getBucket())
            .setObject(obj.getName())
            .setGeneration(obj.getGeneration())
            .setReadOffset(blobOffset)
            .build();
    ApiCallContext ctx = GrpcCallContext.createDefault();
    serverStream = this.read.call(req, ctx);
    responseIterator = serverStream.iterator();
    this.hasher = hasher;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    return Math.toIntExact(read(new ByteBuffer[] {dst}));
  }

  @Override
  public long read(ByteBuffer[] dsts) throws IOException {
    return read(dsts, 0, dsts.length);
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
    long toRead = Math.min(blobLimit, totalBufferCapacity);

    ReadCursor c = new ReadCursor(blobOffset, blobOffset + toRead);
    while (c.hasRemaining()) {
      if (leftovers != null) {
        copy(c, leftovers, dsts, offset, length);
        if (!leftovers.hasRemaining()) {
          leftovers = null;
        }
        continue;
      }

      if (responseIterator.hasNext()) {
        ReadObjectResponse resp = responseIterator.next();
        if (resp.hasMetadata()) {
          Object metadata = resp.getMetadata();
          result.set(metadata);
          totalSize = metadata.getSize();
        } else if (resp.hasContentRange()) {
          totalSize = resp.getContentRange().getCompleteLength();
        }
        ChecksummedData checksummedData = resp.getChecksummedData();
        ByteBuffer content = checksummedData.getContent().asReadOnlyByteBuffer();
        Crc32cLengthUnknown expected = Crc32cValue.of(checksummedData.getCrc32C());
        try {
          hasher.validate(expected, content.duplicate());
        } catch (IOException e) {
          close();
          throw e;
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
  public boolean isComplete() {
    return complete;
  }

  @Override
  public void close() throws IOException {
    open = false;
    if (responseIterator != null) {
      responseIterator = null;
    }
    if (serverStream != null) {
      serverStream.cancel();
      serverStream = null;
    }
  }

  private void copy(ReadCursor c, ByteBuffer content, ByteBuffer[] dsts, int offset, int length) {
    long copiedBytes = Buffers.copy(content, dsts, offset, length);
    c.advance(copiedBytes);
  }

  /**
   * Shrink wraps a beginning, offset and limit for tracking state of an individual invocation of
   * {@link #read}
   */
  private static final class ReadCursor {
    private final long beginning;
    private long offset;
    private final long limit;

    public ReadCursor(long beginning, long limit) {
      checkArgument(0 <= beginning && beginning <= limit);
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
      return String.format("Cursor{begin=%d, offset=%d, capacity=%d}", beginning, offset, limit);
    }
  }
}
