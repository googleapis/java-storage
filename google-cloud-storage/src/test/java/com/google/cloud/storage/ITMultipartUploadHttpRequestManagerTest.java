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

import static com.google.common.truth.Truth.assertThat;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.cloud.storage.FakeHttpServer.HttpRequestHandler;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.ParallelFriendly;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.common.collect.ImmutableMap;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.FullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.PROD)
@ParallelFriendly
public final class ITMultipartUploadHttpRequestManagerTest {
  private static final GsonFactory gson = GsonFactory.getDefaultInstance();
  private static final NetHttpTransport transport = new NetHttpTransport.Builder().build();
  private MultipartUploadHttpRequestManager multipartUploadHttpRequestManager;
  private HttpStorageOptions httpStorageOptions;

  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    multipartUploadHttpRequestManager =
        new MultipartUploadHttpRequestManager(
            transport.createRequestFactory(), new XmlObjectParser(new XmlMapper()));
    httpStorageOptions = HttpStorageOptions.newBuilder().setProjectId("test-project").build();
  }

  @Test
  public void sendCreateMultipartUploadRequest_success() throws Exception {
    HttpRequestHandler handler =
        req -> {
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .build();

      CreateMultipartUploadResponse response =
          multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
              endpoint, request, httpStorageOptions);

      assertThat(response).isNotNull();
      assertThat(response.bucket()).isEqualTo("test-bucket");
      assertThat(response.key()).isEqualTo("test-key");
      assertThat(response.uploadId()).isEqualTo("test-upload-id");
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_error() throws Exception {
    HttpRequestHandler handler =
        req -> {
          FullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST);
          resp.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .build();

      StorageException se =
          assertThrows(
              StorageException.class,
              () ->
                  multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
                      endpoint, request, httpStorageOptions));
      assertThat(se.getCode()).isEqualTo(400);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withCannedAcl() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-acl")).isEqualTo("authenticatedRead");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .cannedAcl(Storage.PredefinedAcl.AUTHENTICATED_READ)
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withMetadata() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-meta-key1")).isEqualTo("value1");
          assertThat(req.headers().get("x-goog-meta-key2")).isEqualTo("value2");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .metadata(ImmutableMap.of("key1", "value1", "key2", "value2"))
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withStorageClass() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-storage-class")).isEqualTo("ARCHIVE");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .storageClass(StorageClass.ARCHIVE)
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withKmsKeyName() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-encryption-kms-key-name"))
              .isEqualTo("projects/p/locations/l/keyRings/r/cryptoKeys/k");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .kmsKeyName("projects/p/locations/l/keyRings/r/cryptoKeys/k")
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withObjectLockMode() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-object-lock-mode")).isEqualTo("GOVERNANCE");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .objectLockMode(ObjectLockMode.GOVERNANCE)
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withObjectLockRetainUntilDate() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-object-lock-retain-until-date"))
              .isEqualTo("2024-01-01T00:00:00Z");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .objectLockRetainUntilDate(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withCustomTime() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-custom-time")).isEqualTo("2024-01-01T00:00:00Z");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(gson.toByteArray(response));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      CreateMultipartUploadRequest request =
          CreateMultipartUploadRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .contentType("application/octet-stream")
              .customTime(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
          endpoint, request, httpStorageOptions);
    }
  }
}
