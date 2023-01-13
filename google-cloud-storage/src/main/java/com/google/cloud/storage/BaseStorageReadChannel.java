/*
 * Copyright 2023 Google LLC
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

import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static java.util.Objects.requireNonNull;

import com.google.cloud.storage.BufferedReadableByteChannelSession.BufferedReadableByteChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class BaseStorageReadChannel<T> implements StorageReadChannel {

  private boolean open;
  private ByteRangeSpec byteRangeSpec;
  private int chunkSize = _2MiB;
  private BufferHandle bufferHandle;
  private LazyReadChannel<T> lazyReadChannel;

  @Nullable private T resolvedObject;

  protected BaseStorageReadChannel() {
    this.open = true;
    this.byteRangeSpec = ByteRangeSpec.nullRange();
  }

  @Override
  public final synchronized void setChunkSize(int chunkSize) {
    StorageException.wrapIOException(() -> maybeResetChannel(true));
    this.chunkSize = chunkSize;
  }

  @Override
  public final synchronized boolean isOpen() {
    return open;
  }

  @Override
  public final synchronized void close() {
    open = false;
    if (internalGetLazyChannel().isOpen()) {
      StorageException.wrapIOException(internalGetLazyChannel().getChannel()::close);
    }
  }

  @Override
  public final synchronized StorageReadChannel setByteRangeSpec(ByteRangeSpec byteRangeSpec) {
    requireNonNull(byteRangeSpec, "byteRangeSpec must be non null");
    StorageException.wrapIOException(() -> maybeResetChannel(false));
    this.byteRangeSpec = byteRangeSpec;
    return this;
  }

  @Override
  public final ByteRangeSpec getByteRangeSpec() {
    return byteRangeSpec;
  }

  @Override
  public final synchronized int read(ByteBuffer dst) throws IOException {
    // BlobReadChannel only considered itself closed if close had been called on it.
    if (!open) {
      throw new ClosedChannelException();
    }
    long diff = byteRangeSpec.length();
    if (diff <= 0) {
      return -1;
    }
    try {
      // trap if the fact that tmp is already closed, and instead return -1
      BufferedReadableByteChannel tmp = internalGetLazyChannel().getChannel();
      if (!tmp.isOpen()) {
        return -1;
      }
      int read = tmp.read(dst);
      if (read != -1) {
        byteRangeSpec = byteRangeSpec.withShiftBeginOffset(read);
      }
      return read;
    } catch (StorageException e) {
      if (e.getCode() == 416) {
        // HttpStorageRpc turns 416 into a null etag with an empty byte array, leading
        // BlobReadChannel to believe it read 0 bytes, returning -1 and leaving the channel open.
        // Emulate that same behavior here to preserve behavior compatibility, though this should
        // be removed in the next major version.
        return -1;
      } else {
        throw new IOException(e);
      }
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(StorageException.coalesce(e));
    }
  }

  protected final BufferHandle getBufferHandle() {
    if (bufferHandle == null) {
      bufferHandle = BufferHandle.allocate(chunkSize);
    }
    return bufferHandle;
  }

  protected final int getChunkSize() {
    return chunkSize;
  }

  @Nullable
  protected T getResolvedObject() {
    return resolvedObject;
  }

  protected void setResolvedObject(@Nullable T resolvedObject) {
    this.resolvedObject = resolvedObject;
  }

  protected abstract LazyReadChannel<T> newLazyReadChannel();

  private void maybeResetChannel(boolean freeBuffer) throws IOException {
    if (lazyReadChannel != null) {
      if (lazyReadChannel.isOpen()) {
        lazyReadChannel.getChannel().close();
      }
      if (bufferHandle != null && !freeBuffer) {
        bufferHandle.get().clear();
      } else if (freeBuffer) {
        bufferHandle = null;
      }
      lazyReadChannel = null;
    }
  }

  private LazyReadChannel<T> internalGetLazyChannel() {
    if (lazyReadChannel == null) {
      lazyReadChannel = newLazyReadChannel();
    }
    return lazyReadChannel;
  }
}
