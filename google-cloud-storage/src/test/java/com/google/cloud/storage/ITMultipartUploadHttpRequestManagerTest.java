/*
 * Copyright 2025 Google LLC
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.FakeHttpServer.HttpRequestHandler;
import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.ParallelFriendly;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.ObjectLockMode;
import com.google.cloud.storage.multipartupload.model.Part;
import com.google.common.collect.ImmutableMap;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.FullHttpResponse;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@SingleBackend(Backend.PROD)
@ParallelFriendly
public final class ITMultipartUploadHttpRequestManagerTest {
  private static final XmlMapper xmlMapper;

  static {
    xmlMapper = new XmlMapper();
    xmlMapper.registerModule(new JavaTimeModule());
  }

  private MultipartUploadHttpRequestManager multipartUploadHttpRequestManager;
  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    HttpStorageOptions httpStorageOptions =
        HttpStorageOptions.newBuilder()
            .setProjectId("test-project")
            .setCredentials(NoCredentials.getInstance())
            .build();
    multipartUploadHttpRequestManager =
        MultipartUploadHttpRequestManager.createFrom(httpStorageOptions);
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
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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
          multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);

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

      assertThrows(
          HttpResponseException.class,
          () ->
              multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(
                  endpoint, request));
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withCannedAcl() throws Exception {
    HttpRequestHandler handler =
        req -> {
          assertThat(req.headers().get("x-goog-acl")).isEqualTo("AUTHENTICATED_READ");
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
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
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
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
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
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
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
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
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withObjectLockRetainUntilDate() throws Exception {
    OffsetDateTime retainUtil = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    HttpRequestHandler handler =
        req -> {
          OffsetDateTime actual =
              Utils.offsetDateTimeRfc3339Codec.decode(
                  req.headers().get("x-goog-object-lock-retain-until-date"));
          assertThat(actual).isEqualTo(retainUtil);
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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
              .objectLockRetainUntilDate(retainUtil)
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
    }
  }

  @Test
  public void sendCreateMultipartUploadRequest_withCustomTime() throws Exception {
    OffsetDateTime customTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    HttpRequestHandler handler =
        req -> {
          OffsetDateTime actual =
              Utils.offsetDateTimeRfc3339Codec.decode(req.headers().get("x-goog-custom-time"));
          assertThat(actual).isEqualTo(customTime);
          CreateMultipartUploadResponse response =
              CreateMultipartUploadResponse.builder()
                  .bucket("test-bucket")
                  .key("test-key")
                  .uploadId("test-upload-id")
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(response));

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
              .customTime(customTime)
              .build();

      multipartUploadHttpRequestManager.sendCreateMultipartUploadRequest(endpoint, request);
    }
  }

  @Test
  public void sendListPartsRequest_success() throws Exception {
    HttpRequestHandler handler =
        req -> {
          OffsetDateTime lastModified = OffsetDateTime.of(2024, 5, 8, 17, 50, 0, 0, ZoneOffset.UTC);
          ListPartsResponse listPartsResponse =
              ListPartsResponse.builder()
                  .setBucket("test-bucket")
                  .setKey("test-key")
                  .setUploadId("test-upload-id")
                  .setPartNumberMarker(0)
                  .setNextPartNumberMarker(1)
                  .setMaxParts(1)
                  .setIsTruncated(false)
                  .setParts(
                      Collections.singletonList(
                          Part.builder()
                              .partNumber(1)
                              .eTag("\"etag\"")
                              .size(123)
                              .lastModified(lastModified)
                              .build()))
                  .build();
          ByteBuf buf = Unpooled.wrappedBuffer(xmlMapper.writeValueAsBytes(listPartsResponse));

          DefaultFullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), OK, buf);
          resp.headers().set(CONTENT_TYPE, "application/xml; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      ListPartsRequest request =
          ListPartsRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .uploadId("test-upload-id")
              .maxParts(1)
              .partNumberMarker(0)
              .build();

      ListPartsResponse response =
          multipartUploadHttpRequestManager.sendListPartsRequest(endpoint, request);

      assertThat(response).isNotNull();
      assertThat(response.getBucket()).isEqualTo("test-bucket");
      assertThat(response.getKey()).isEqualTo("test-key");
      assertThat(response.getUploadId()).isEqualTo("test-upload-id");
      assertThat(response.getPartNumberMarker()).isEqualTo(0);
      assertThat(response.getNextPartNumberMarker()).isEqualTo(1);
      assertThat(response.getMaxParts()).isEqualTo(1);
      assertThat(response.isTruncated()).isFalse();
      assertThat(response.getParts()).hasSize(1);
      Part part = response.getParts().get(0);
      assertThat(part.partNumber()).isEqualTo(1);
      assertThat(part.eTag()).isEqualTo("\"etag\"");
      assertThat(part.size()).isEqualTo(123);
      assertThat(part.lastModified())
          .isEqualTo(OffsetDateTime.of(2024, 5, 8, 17, 50, 0, 0, ZoneOffset.UTC));
    }
  }

  @Test
  public void sendListPartsRequest_error() throws Exception {
    HttpRequestHandler handler =
        req -> {
          FullHttpResponse resp =
              new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_REQUEST);
          resp.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
          return resp;
        };

    try (FakeHttpServer fakeHttpServer = FakeHttpServer.of(handler)) {
      URI endpoint = fakeHttpServer.getEndpoint();
      ListPartsRequest request =
          ListPartsRequest.builder()
              .bucket("test-bucket")
              .key("test-key")
              .uploadId("test-upload-id")
              .build();

      assertThrows(
          HttpResponseException.class,
          () -> multipartUploadHttpRequestManager.sendListPartsRequest(endpoint, request));
    }
  }
}
