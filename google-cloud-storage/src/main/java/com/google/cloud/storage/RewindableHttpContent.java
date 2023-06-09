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

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpMediaType;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

abstract class RewindableHttpContent extends AbstractHttpContent {

  private RewindableHttpContent() {
    super((HttpMediaType) null);
  }

  @Override
  public abstract long getLength();

  abstract void rewindTo(long offset);

  @Override
  public final boolean retrySupported() {
    return false;
  }

  static RewindableHttpContent empty() {
    return EmptyRewindableContent.INSTANCE;
  }

  static RewindableHttpContent of(Path path) throws IOException {
    return new PathRewindableHttpContent(path);
  }

  private static final class EmptyRewindableContent extends RewindableHttpContent {
    private static final EmptyRewindableContent INSTANCE = new EmptyRewindableContent();

    @Override
    public long getLength() {
      return 0L;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      out.flush();
    }

    @Override
    protected void rewindTo(long offset) {}
  }

  private static final class PathRewindableHttpContent extends RewindableHttpContent {

    private final Path path;
    private final long size;

    private long readOffset;

    private PathRewindableHttpContent(Path path) throws IOException {
      this.path = path;
      this.size = Files.size(path);
      this.readOffset = 0;
    }

    @Override
    public long getLength() {
      return size - readOffset;
    }

    @Override
    protected void rewindTo(long offset) {
      Preconditions.checkArgument(
          offset < size, "provided offset must be less than size (%d < %d)", offset, size);
      this.readOffset = offset;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      try (SeekableByteChannel in = Files.newByteChannel(path, StandardOpenOption.READ)) {
        in.position(readOffset);
        ByteStreams.copy(in, Channels.newChannel(out));
        out.flush();
      }
    }
  }
}
