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

import static com.google.cloud.storage.TestUtils.xxd;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.BlobWriteSessionConfig.WriterFactory;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

public final class BufferToDiskThenUploadTest {

  @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Rule public final TestName testName = new TestName();

  @Test
  public void happyPath() throws IOException {
    Path tempDir = temporaryFolder.newFolder(testName.getMethodName()).toPath();

    BufferToDiskThenUpload btdtu =
        BlobWriteSessionConfigs.bufferToDiskThenUpload(tempDir).withIncludeLoggingSink();
    TestClock clock = TestClock.tickBy(Instant.EPOCH, Duration.ofSeconds(1));
    WriterFactory factory = btdtu.createFactory(clock);

    BlobInfo blobInfo = BlobInfo.newBuilder("bucket", "object").build();
    AtomicReference<byte[]> actualBytes = new AtomicReference<>(null);
    WritableByteChannelSession<?, BlobInfo> writeSession =
        factory.writeSession(
            new StorageInternal() {
              @Override
              public BlobInfo internalCreateFrom(
                  Path path, BlobInfo info, Opts<ObjectTargetOpt> opts) throws IOException {
                byte[] actual = Files.readAllBytes(path);
                actualBytes.compareAndSet(null, actual);
                return info;
              }
            },
            blobInfo,
            Opts.empty());

    byte[] bytes = DataGenerator.base64Characters().genBytes(128);
    try (WritableByteChannel open = writeSession.open()) {
      open.write(ByteBuffer.wrap(bytes));
    }
    String xxdActual = xxd(actualBytes.get());
    String xxdExpected = xxd(bytes);
    assertThat(xxdActual).isEqualTo(xxdExpected);
  }
}
