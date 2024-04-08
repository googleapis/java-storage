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

import static com.google.cloud.storage.ByteSizeConstants._256KiB;
import static com.google.cloud.storage.ByteSizeConstants._256KiBL;
import static com.google.cloud.storage.ByteSizeConstants._512KiB;
import static com.google.cloud.storage.ByteSizeConstants._512KiBL;
import static com.google.cloud.storage.ByteSizeConstants._768KiBL;
import static com.google.common.truth.Truth.assertThat;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderNames.CONTENT_RANGE;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderNames.RANGE;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.core.ApiClock;
import com.google.api.core.NanoClock;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.FakeHttpServer.HttpRequestHandler;
import com.google.cloud.storage.Retrying.RetryingDependencies;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.grpc.netty.shaded.io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpRequest;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class ITJsonResumableSessionTest {
  private static final GsonFactory gson = GsonFactory.getDefaultInstance();
  private static final NetHttpTransport transport = new NetHttpTransport.Builder().build();
  private static final HttpResponseStatus RESUME_INCOMPLETE =
      HttpResponseStatus.valueOf(308, "Resume Incomplete");
  private static final RetryingDependencies RETRYING_DEPENDENCIES =
      new RetryingDependencies() {
        @Override
        public RetrySettings getRetrySettings() {
          return RetrySettings.newBuilder().setMaxAttempts(3).build();
        }

        @Override
        public ApiClock getClock() {
          return NanoClock.getDefaultClock();
        }
      };
  private static final ResultRetryAlgorithm<?> RETRY_ALGORITHM =
      StorageRetryStrategy.getUniformStorageRetryStrategy().getIdempotentHandler();
  private HttpClientContext httpClientContext;

  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    httpClientContext =
        HttpClientContext.of(transport.createRequestFactory(), new JsonObjectParser(gson));
  }

  @Test
  public void rewindWillQueryStatusOnlyWhenDirty() throws Exception {
    HttpContentRange range1 = HttpContentRange.of(ByteRangeSpec.explicit(0L, _512KiBL));
    HttpContentRange range2 = HttpContentRange.query();
    HttpContentRange range3 = HttpContentRange.of(ByteRangeSpec.explicit(_256KiBL, _512KiBL));

    final List<HttpRequest> requests = Collections.synchronizedList(new ArrayList<>());
    HttpRequestHandler handler =
        req -> {
          requests.add(req);
          String contentRange = req.headers().get(CONTENT_RANGE);
          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
          if (range1.getHeaderValue().equals(contentRange)) {
            return new DefaultFullHttpResponse(req.protocolVersion(), SERVICE_UNAVAILABLE);
          } else if (range2.getHeaderValue().equals(contentRange)) {
            resp.headers().set(RANGE, ByteRangeSpec.explicit(0L, _256KiBL).getHttpRangeHeader());
          } else {
            resp.headers().set(RANGE, ByteRangeSpec.explicit(0L, _512KiBL).getHttpRangeHeader());
          }
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler);
        TmpFile tmpFile =
            DataGenerator.base64Characters().tempFile(temp.newFolder().toPath(), _512KiBL)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      String uploadUrl = String.format("%s/upload/%s", endpoint.toString(), UUID.randomUUID());

      JsonResumableWrite resumableWrite =
          JsonResumableWrite.of(null, ImmutableMap.of(), uploadUrl, 0);
      JsonResumableSession session =
          new JsonResumableSession(
              httpClientContext, RETRYING_DEPENDENCIES, RETRY_ALGORITHM, resumableWrite);

      ResumableOperationResult<@Nullable StorageObject> operationResult =
          session.put(RewindableContent.of(tmpFile.getPath()), range1);
      StorageObject call = operationResult.getObject();
      assertThat(call).isNull();
      assertThat(operationResult.getPersistedSize()).isEqualTo(_512KiBL);
    }

    List<String> actual =
        requests.stream().map(r -> r.headers().get(CONTENT_RANGE)).collect(Collectors.toList());

    List<String> expected =
        ImmutableList.of(range1.getHeaderValue(), range2.getHeaderValue(), range3.getHeaderValue());

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void retryAttemptWillReturnQueryResultIfPersistedSizeMatchesSpecifiedEndOffset()
      throws Exception {
    HttpContentRange range1 = HttpContentRange.of(ByteRangeSpec.explicit(0L, _512KiBL));
    HttpContentRange range2 = HttpContentRange.query();
    HttpContentRange range3 = HttpContentRange.of(ByteRangeSpec.explicit(_512KiBL, _768KiBL));

    final List<HttpRequest> requests = Collections.synchronizedList(new ArrayList<>());
    HttpRequestHandler handler =
        req -> {
          requests.add(req);
          String contentRange = req.headers().get(CONTENT_RANGE);
          DefaultFullHttpResponse resp;
          if (range1.getHeaderValue().equals(contentRange)) {
            resp = new DefaultFullHttpResponse(req.protocolVersion(), SERVICE_UNAVAILABLE);
          } else if (range2.getHeaderValue().equals(contentRange)) {
            resp = new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
            resp.headers().set(RANGE, ByteRangeSpec.explicit(0L, _512KiBL).getHttpRangeHeader());
          } else {
            resp = new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
            resp.headers()
                .set(RANGE, ByteRangeSpec.explicit(_512KiBL, _768KiBL).getHttpRangeHeader());
          }
          return resp;
        };

    ByteBuffer buf1 = DataGenerator.base64Characters().genByteBuffer(_512KiB);
    ByteBuffer buf2 = DataGenerator.base64Characters().genByteBuffer(_256KiB);

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      String uploadUrl = String.format("%s/upload/%s", endpoint.toString(), UUID.randomUUID());

      JsonResumableWrite resumableWrite =
          JsonResumableWrite.of(null, ImmutableMap.of(), uploadUrl, 0);
      JsonResumableSession session =
          new JsonResumableSession(
              httpClientContext, RETRYING_DEPENDENCIES, RETRY_ALGORITHM, resumableWrite);

      ResumableOperationResult<@Nullable StorageObject> operationResult1 =
          session.put(RewindableContent.of(buf1), range1);
      StorageObject call1 = operationResult1.getObject();
      assertThat(call1).isNull();
      assertThat(operationResult1.getPersistedSize()).isEqualTo(_512KiBL);

      ResumableOperationResult<@Nullable StorageObject> operationResult2 =
          session.put(RewindableContent.of(buf2), range3);
      StorageObject call2 = operationResult2.getObject();
      assertThat(call2).isNull();
      assertThat(operationResult2.getPersistedSize()).isEqualTo(_768KiBL);
    }

    List<String> actual =
        requests.stream().map(r -> r.headers().get(CONTENT_RANGE)).collect(Collectors.toList());

    List<String> expected =
        ImmutableList.of(range1.getHeaderValue(), range2.getHeaderValue(), range3.getHeaderValue());

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void rewindOfContentIsRelativeToItsBeginOffsetOfTheOverallObject() throws Exception {
    HttpContentRange range1 = HttpContentRange.of(ByteRangeSpec.explicit(0L, _512KiBL));
    HttpContentRange range2 = HttpContentRange.of(ByteRangeSpec.explicit(_512KiBL, _768KiBL));
    HttpContentRange range3 = HttpContentRange.query();

    final AtomicBoolean fail = new AtomicBoolean(true);
    final List<HttpRequest> requests = Collections.synchronizedList(new ArrayList<>());
    HttpRequestHandler handler =
        req -> {
          requests.add(req);
          String contentRange = req.headers().get(CONTENT_RANGE);
          DefaultFullHttpResponse resp;
          if (range1.getHeaderValue().equals(contentRange)
              || range3.getHeaderValue().equals(contentRange)) {
            resp = new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
            resp.headers().set(RANGE, ByteRangeSpec.explicit(0L, _512KiBL).getHttpRangeHeader());
          } else if (range2.getHeaderValue().equals(contentRange)) {
            if (fail.getAndSet(false)) {
              resp = new DefaultFullHttpResponse(req.protocolVersion(), SERVICE_UNAVAILABLE);
            } else {
              resp = new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
              resp.headers()
                  .set(RANGE, ByteRangeSpec.explicit(_512KiBL, _768KiBL).getHttpRangeHeader());
            }
          } else {
            resp = new DefaultFullHttpResponse(req.protocolVersion(), RESUME_INCOMPLETE);
            resp.headers()
                .set(RANGE, ByteRangeSpec.explicit(_512KiBL, _768KiBL).getHttpRangeHeader());
          }
          return resp;
        };

    ByteBuffer buf1 = DataGenerator.base64Characters().genByteBuffer(_512KiB);
    ByteBuffer buf2 = DataGenerator.base64Characters().genByteBuffer(_256KiB);

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      String uploadUrl = String.format("%s/upload/%s", endpoint.toString(), UUID.randomUUID());

      JsonResumableWrite resumableWrite =
          JsonResumableWrite.of(null, ImmutableMap.of(), uploadUrl, 0);
      JsonResumableSession session =
          new JsonResumableSession(
              httpClientContext, RETRYING_DEPENDENCIES, RETRY_ALGORITHM, resumableWrite);

      ResumableOperationResult<@Nullable StorageObject> operationResult1 =
          session.put(RewindableContent.of(buf1), range1);
      StorageObject call1 = operationResult1.getObject();
      assertThat(call1).isNull();
      assertThat(operationResult1.getPersistedSize()).isEqualTo(_512KiBL);

      ResumableOperationResult<@Nullable StorageObject> operationResult2 =
          session.put(RewindableContent.of(buf2), range2);
      StorageObject call2 = operationResult2.getObject();
      assertThat(call2).isNull();
      assertThat(operationResult2.getPersistedSize()).isEqualTo(_768KiBL);
    }

    List<String> actual =
        requests.stream().map(r -> r.headers().get(CONTENT_RANGE)).collect(Collectors.toList());

    List<String> expected =
        ImmutableList.of(
            range1.getHeaderValue(),
            range2.getHeaderValue(),
            range3.getHeaderValue(),
            range2.getHeaderValue());

    assertThat(actual).isEqualTo(expected);
  }
}
