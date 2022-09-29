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

import static com.google.cloud.storage.ByteSizeConstants._2MiB;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.GapicUnbufferedWritableByteChannelTest.DirectWriteService;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.storage.v2.ChecksummedData;
import com.google.storage.v2.Object;
import com.google.storage.v2.ObjectChecksums;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import com.google.storage.v2.WriteObjectSpec;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Verify some simplistic retries can take place and that we're constructing {@link
 * WriteObjectRequest}s as expected.
 */
public final class GrpcStorageImplUploadRetryTest {
  private static final String FORMATTED_BUCKET_NAME = "projects/_/buckets/buck";
  private static final int objectContentSize = 64;
  private static final byte[] bytes = DataGenerator.base64Characters().genBytes(objectContentSize);

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private Path baseDir;

  @Before
  public void setUp() throws Exception {
    baseDir = tmpDir.getRoot().toPath();
  }

  @Test
  public void create_bytes() throws Exception {
    Direct.FakeService service = Direct.FakeService.create();

    try (FakeServer server = FakeServer.of(service);
        Storage s = server.getGrpcStorageOptions().getService()) {
      BlobInfo info = BlobInfo.newBuilder("buck", "obj").build();
      s.create(info, bytes, BlobTargetOption.doesNotExist());
    }

    assertThat(service.returnError.get()).isFalse();
  }

  @Test
  public void create_inputStream() throws Exception {
    Resumable.FakeService service = Resumable.FakeService.create();
    try (TmpFile tmpFile = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        FakeServer server = FakeServer.of(service);
        Storage s = server.getGrpcStorageOptions().getService();
        InputStream in = Channels.newInputStream(tmpFile.reader())) {
      BlobInfo info = BlobInfo.newBuilder("buck", "obj").build();
      s.create(info, in, BlobWriteOption.doesNotExist());
    }

    assertThat(service.returnError.get()).isFalse();
  }

  @Test
  public void createFrom_path_smallerThanBufferSize() throws Exception {
    Direct.FakeService service = Direct.FakeService.create();

    try (TmpFile tmpFile = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        FakeServer server = FakeServer.of(service);
        Storage s = server.getGrpcStorageOptions().getService()) {
      BlobInfo info = BlobInfo.newBuilder("buck", "obj").build();
      s.createFrom(info, tmpFile.getPath(), _2MiB, BlobWriteOption.doesNotExist());
    }

    assertThat(service.returnError.get()).isFalse();
  }

  @Test
  public void createFrom_path_largerThanBufferSize() throws Exception {
    Resumable.FakeService service = Resumable.FakeService.create();
    try (TmpFile tmpFile = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        FakeServer server = FakeServer.of(service);
        Storage s = server.getGrpcStorageOptions().getService()) {
      BlobInfo info = BlobInfo.newBuilder("buck", "obj").build();
      s.createFrom(info, tmpFile.getPath(), 16, BlobWriteOption.doesNotExist());
    }

    assertThat(service.returnError.get()).isFalse();
  }

  @Test
  public void createFrom_inputStream() throws Exception {
    Resumable.FakeService service = Resumable.FakeService.create();
    try (TmpFile tmpFile = DataGenerator.base64Characters().tempFile(baseDir, objectContentSize);
        FakeServer server = FakeServer.of(service);
        Storage s = server.getGrpcStorageOptions().getService();
        InputStream in = Channels.newInputStream(tmpFile.reader())) {
      BlobInfo info = BlobInfo.newBuilder("buck", "obj").build();
      s.createFrom(info, in, BlobWriteOption.doesNotExist());
    }

    assertThat(service.returnError.get()).isFalse();
  }

  private static final class Direct {
    private static final Object obj =
        Object.newBuilder().setBucket(FORMATTED_BUCKET_NAME).setName("obj").build();
    private static final WriteObjectSpec spec =
        WriteObjectSpec.newBuilder().setResource(obj).setIfGenerationMatch(0).build();

    private static final ChecksummedData checksummedData =
        TestUtils.getChecksummedData(ByteString.copyFrom(bytes), Hasher.enabled());
    private static final WriteObjectRequest req1 =
        WriteObjectRequest.newBuilder()
            .setWriteObjectSpec(spec)
            .setChecksummedData(checksummedData)
            .setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(checksummedData.getCrc32C()))
            .setFinishWrite(true)
            .build();
    private static final WriteObjectResponse resp1 =
        WriteObjectResponse.newBuilder()
            .setResource(obj.toBuilder().setSize(objectContentSize))
            .build();

    private static final class FakeService extends DirectWriteService {
      private final AtomicBoolean returnError;

      private FakeService(AtomicBoolean returnError) {
        super(
            (obs, reqs) -> {
              if (reqs.equals(ImmutableList.of(req1))) {
                if (returnError.get()) {
                  returnError.compareAndSet(true, false);
                  obs.onError(TestUtils.apiException(Code.INTERNAL, "should retry"));
                } else {
                  obs.onNext(resp1);
                  obs.onCompleted();
                }
              } else {
                obs.onError(
                    TestUtils.apiException(Code.PERMISSION_DENIED, "Unexpected request chain."));
              }
            });
        this.returnError = returnError;
      }

      // a bit of constructor lifecycle hackery to appease the compiler
      // Even though the thing past to super() is a lazy function, the closing over of the outer
      // fields happens earlier than they are available. To side step this fact, we provide the
      // AtomicBoolean as a constructor argument which can be closed over without issue, and then
      // bind it to the class field after super().
      static Direct.FakeService create() {
        return new Direct.FakeService(new AtomicBoolean(true));
      }
    }
  }

  private static final class Resumable {

    private static final String uploadId = "upload-id";

    private static final Object obj =
        Object.newBuilder().setBucket(FORMATTED_BUCKET_NAME).setName("obj").build();
    private static final WriteObjectSpec spec =
        WriteObjectSpec.newBuilder().setResource(obj).setIfGenerationMatch(0).build();

    private static final StartResumableWriteRequest startReq =
        StartResumableWriteRequest.newBuilder().setWriteObjectSpec(spec).build();
    private static final StartResumableWriteResponse startResp =
        StartResumableWriteResponse.newBuilder().setUploadId(uploadId).build();

    private static final ChecksummedData checksummedData =
        TestUtils.getChecksummedData(ByteString.copyFrom(bytes), Hasher.enabled());
    private static final WriteObjectRequest req1 =
        WriteObjectRequest.newBuilder()
            .setUploadId(uploadId)
            .setChecksummedData(checksummedData)
            .setObjectChecksums(ObjectChecksums.newBuilder().setCrc32C(checksummedData.getCrc32C()))
            .setFinishWrite(true)
            .build();
    private static final WriteObjectResponse resp1 =
        WriteObjectResponse.newBuilder()
            .setResource(obj.toBuilder().setSize(objectContentSize))
            .build();

    private static final class FakeService extends DirectWriteService {
      private final AtomicBoolean returnError;

      private FakeService(AtomicBoolean returnError) {
        super(
            (obs, reqs) -> {
              if (reqs.equals(ImmutableList.of(req1))) {
                if (returnError.get()) {
                  returnError.compareAndSet(true, false);
                  obs.onError(TestUtils.apiException(Code.INTERNAL, "should retry"));
                } else {
                  obs.onNext(resp1);
                  obs.onCompleted();
                }
              } else {
                obs.onError(
                    TestUtils.apiException(Code.PERMISSION_DENIED, "Unexpected request chain."));
              }
            });
        this.returnError = returnError;
      }

      @Override
      public void startResumableWrite(
          StartResumableWriteRequest request, StreamObserver<StartResumableWriteResponse> obs) {
        if (request.equals(startReq)) {
          obs.onNext(startResp);
          obs.onCompleted();
        } else {
          obs.onError(TestUtils.apiException(Code.PERMISSION_DENIED, "Unexpected request chain."));
        }
      }

      // a bit of constructor lifecycle hackery to appease the compiler
      // Even though the thing past to super() is a lazy function, the closing over of the outer
      // fields happens earlier than they are available. To side step this fact, we provide the
      // AtomicBoolean as a constructor argument which can be closed over without issue, and then
      // bind it to the class field after super().
      static FakeService create() {
        return new FakeService(new AtomicBoolean(true));
      }
    }
  }
}
