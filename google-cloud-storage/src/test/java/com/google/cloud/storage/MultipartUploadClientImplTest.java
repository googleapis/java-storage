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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompletedMultipartUpload;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.CompletedPart;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class MultipartUploadClientImplTest {

  private static final String BUCKET = "bucket";
  private static final String KEY = "key";
  private static final String UPLOAD_ID = "uploadId";
  private static final String CONTENT_TYPE = "application/octet-stream";
  private static final String PROJECT_ID = "project-id";

  private MultipartUploadClientImpl multipartUploadClient;

  @Mock private HttpRequestManager httpRequestManager;

  @Mock private Retrier retrier;

  @Captor private ArgumentCaptor<Map<String, String>> extensionHeadersCaptor;

  @Captor private ArgumentCaptor<String> uriCaptor;

  private final XmlMapper xmlMapper = new XmlMapper();
  private AutoCloseable mocks;

  @Before
  public void setUp() throws Exception {
    mocks = MockitoAnnotations.openMocks(this);
    HttpStorageOptions options = HttpStorageOptions.newBuilder().setProjectId(PROJECT_ID).build();
    multipartUploadClient =
        new MultipartUploadClientImpl(new URI("https://storage.googleapis.com"), null, retrier, options);
    // Replace the httpRequestManager with a mock
    java.lang.reflect.Field field =
        MultipartUploadClientImpl.class.getDeclaredField("httpRequestManager");
    field.setAccessible(true);
    field.set(multipartUploadClient, httpRequestManager);
  }

  @After
  public void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  public void testCreateMultipartUpload() throws IOException {
    CreateMultipartUploadRequest request =
        CreateMultipartUploadRequest.builder().bucket(BUCKET).key(KEY).contentType(CONTENT_TYPE).build();
    CreateMultipartUploadResponse expectedResponse =
        CreateMultipartUploadResponse.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).build();
    String responseXml = xmlMapper.writeValueAsString(expectedResponse);
    HttpResponse httpResponse = createHttpResponse(200, responseXml);

    when(httpRequestManager.sendCreateMultipartUploadRequest(
            any(String.class), any(String.class), any(), any()))
        .thenReturn(httpResponse);

    CreateMultipartUploadResponse actualResponse =
        multipartUploadClient.createMultipartUpload(request);

    assertThat(actualResponse.bucket()).isEqualTo(expectedResponse.bucket());
    assertThat(actualResponse.key()).isEqualTo(expectedResponse.key());
    assertThat(actualResponse.uploadId()).isEqualTo(expectedResponse.uploadId());
  }

  @Test
  public void testCreateMultipartUpload_withHeaders() throws IOException {
    Map<String, String> metadata = Collections.singletonMap("key", "value");
    CreateMultipartUploadRequest request =
        CreateMultipartUploadRequest.builder().bucket(BUCKET).key(KEY)
            .contentType(CONTENT_TYPE)
            .cannedAcl(PredefinedAcl.PRIVATE)
            .metadata(metadata)
            .storageClass(StorageClass.COLDLINE)
            .build();
    CreateMultipartUploadResponse expectedResponse =
        CreateMultipartUploadResponse.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).build();
    String responseXml = xmlMapper.writeValueAsString(expectedResponse);
    HttpResponse httpResponse = createHttpResponse(200, responseXml);

    when(httpRequestManager.sendCreateMultipartUploadRequest(
            any(String.class), any(String.class), any(), extensionHeadersCaptor.capture()))
        .thenReturn(httpResponse);

    multipartUploadClient.createMultipartUpload(request);

    Map<String, String> capturedHeaders = extensionHeadersCaptor.getValue();
    assertThat(capturedHeaders).containsEntry("x-goog-acl", "PRIVATE");
    assertThat(capturedHeaders).containsEntry("x-goog-meta-key", "value");
    assertThat(capturedHeaders).containsEntry("x-goog-storage-class", "COLDLINE");
  }

  @Test
  public void testCreateMultipartUpload_error() throws IOException {
    CreateMultipartUploadRequest request =
        CreateMultipartUploadRequest.builder().bucket(BUCKET).key(KEY).contentType(CONTENT_TYPE).build();
    HttpResponse httpResponse = createHttpResponse(500, "Internal Server Error");

    when(httpRequestManager.sendCreateMultipartUploadRequest(
            any(String.class), any(String.class), any(), any()))
        .thenReturn(httpResponse);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> multipartUploadClient.createMultipartUpload(request));
    assertThat(exception.getMessage()).isEqualTo("Failed to initiate upload: 500 Internal Server Error");
  }

  @Test
  public void testUploadPart() throws IOException, NoSuchAlgorithmException {
    UploadPartRequest request =
        UploadPartRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).partNumber(1).build();
    UploadPartResponse expectedResponse =
        UploadPartResponse.builder().eTag("etag").crc32c("crc32c").md5("md5").build();
    HttpResponse httpResponse = createHttpResponse(200, "");
    httpResponse
        .getHeaders()
        .setETag(expectedResponse.eTag())
        .set("x-goog-hash", "crc32c=" + expectedResponse.crc32c() + ",md5=" + expectedResponse.md5());

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendUploadPartRequest(any(String.class), any(), any(String.class), any()))
        .thenReturn(httpResponse);

    UploadPartResponse actualResponse =
        multipartUploadClient.uploadPart(request, RequestBody.empty());

    assertThat(actualResponse.eTag()).isEqualTo(expectedResponse.eTag());
    assertThat(actualResponse.crc32c()).isEqualTo(expectedResponse.crc32c());
    assertThat(actualResponse.md5()).isEqualTo(expectedResponse.md5());
  }

  @Test
  public void testUploadPart_error() throws IOException {
    UploadPartRequest request =
        UploadPartRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).partNumber(1).build();
    HttpResponse httpResponse = createHttpResponse(500, "Internal Server Error");

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendUploadPartRequest(any(String.class), any(), any(String.class), any()))
        .thenReturn(httpResponse);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> multipartUploadClient.uploadPart(request, RequestBody.empty()));
    assertThat(exception.getMessage()).isEqualTo("Failed to upload part 1: 500 Internal Server Error");
  }

  @Test
  public void testCompleteMultipartUpload() throws IOException, NoSuchAlgorithmException {

    CompletedPart part = CompletedPart.builder().partNumber(1).eTag("etag").build();
    CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder().parts(Collections.singletonList(part)).build();
    CompleteMultipartUploadRequest request =
        CompleteMultipartUploadRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .uploadId(UPLOAD_ID)
            .multipartUpload(multipartUpload)
            .build();
    String responseXml =
        "<CompleteMultipartUploadResult>"
            + "<Location>location</Location>"
            + "<Bucket>"
            + BUCKET
            + "</Bucket>"
            + "<Key>"
            + KEY
            + "</Key>"
            + "<ETag>etag</ETag>"
            + "</CompleteMultipartUploadResult>";
    HttpResponse httpResponse = createHttpResponse(200, responseXml);

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendCompleteMultipartUploadRequest(
            any(String.class), any(byte[].class), any(String.class), any()))
        .thenReturn(httpResponse);

    CompleteMultipartUploadResponse actualResponse =
        multipartUploadClient.completeMultipartUpload(request);

    assertThat(actualResponse.bucket()).isEqualTo(BUCKET);
    assertThat(actualResponse.key()).isEqualTo(KEY);
    assertThat(actualResponse.etag()).isEqualTo("etag");
    assertThat(actualResponse.location()).isEqualTo("location");
  }

  @Test
  public void testCompleteMultipartUpload_error() throws IOException {
    CompletedPart part = CompletedPart.builder().partNumber(1).eTag("etag").build();
    CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder().parts(Collections.singletonList(part)).build();
    CompleteMultipartUploadRequest request =
        CompleteMultipartUploadRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .uploadId(UPLOAD_ID)
            .multipartUpload(multipartUpload)
            .build();
    HttpResponse httpResponse = createHttpResponse(500, "Internal Server Error");

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendCompleteMultipartUploadRequest(
            any(String.class), any(byte[].class), any(String.class), any()))
        .thenReturn(httpResponse);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> multipartUploadClient.completeMultipartUpload(request));
    assertThat(exception.getMessage()).isEqualTo("Failed to complete upload: 500 Internal Server Error");
  }

  @Test
  public void testAbortMultipartUpload() throws IOException, NoSuchAlgorithmException {
    AbortMultipartUploadRequest request =
        AbortMultipartUploadRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).build();
    HttpResponse httpResponse = createHttpResponse(204, "");

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendAbortMultipartUploadRequest(
            any(String.class), any(String.class), any()))
        .thenReturn(httpResponse);

    multipartUploadClient.abortMultipartUpload(request);
  }

  @Test
  public void testAbortMultipartUpload_error() throws IOException {
    AbortMultipartUploadRequest request =
        AbortMultipartUploadRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).build();
    HttpResponse httpResponse = createHttpResponse(500, "Internal Server Error");

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendAbortMultipartUploadRequest(
            any(String.class), any(String.class), any()))
        .thenReturn(httpResponse);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> multipartUploadClient.abortMultipartUpload(request));
    assertThat(exception.getMessage()).isEqualTo("Failed to abort upload: 500 Internal Server Error");
  }

  @Test
  public void testListParts() throws IOException {
    ListPartsRequest request =
        ListPartsRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).maxParts(10).build();
    ListPartsResponse expectedResponse =
        xmlMapper.readValue(
            "<ListPartsResult>\n"
                + "  <Bucket>bucket</Bucket>\n"
                + "  <Key>key</Key>\n"
                + "  <UploadId>uploadId</UploadId>\n"
                + "  <MaxParts>10</MaxParts>\n"
                + "  <PartNumberMarker>0</PartNumberMarker>\n"
                + "  <NextPartNumberMarker>1</NextPartNumberMarker>\n"
                + "  <Part>\n"
                + "    <PartNumber>1</PartNumber>\n"
                + "    <ETag>\"etag\"</ETag>\n"
                + "    <Size>1</Size>\n"
                + "    <LastModified>2023-05-11T09:30:00Z</LastModified>\n"
                + "  </Part>\n"
                + "  <StorageClass>STANDARD</StorageClass>\n"
                + "</ListPartsResult>",
            ListPartsResponse.class);
    String responseXml = xmlMapper.writeValueAsString(expectedResponse);
    HttpResponse httpResponse = createHttpResponse(200, responseXml);

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendListPartsRequest(any(String.class), any()))
        .thenReturn(httpResponse);

    ListPartsResponse actualResponse = multipartUploadClient.listParts(request);

    assertThat(actualResponse.getBucket()).isEqualTo(expectedResponse.getBucket());
    assertThat(actualResponse.getKey()).isEqualTo(expectedResponse.getKey());
    assertThat(actualResponse.getUploadId()).isEqualTo(expectedResponse.getUploadId());
    assertThat(actualResponse.getMaxParts()).isEqualTo(expectedResponse.getMaxParts());
    assertThat(actualResponse.getPartNumberMarker())
        .isEqualTo(expectedResponse.getPartNumberMarker());
    assertThat(actualResponse.getNextPartNumberMarker())
        .isEqualTo(expectedResponse.getNextPartNumberMarker());
    assertThat(actualResponse.getParts()).hasSize(1);
    assertThat(actualResponse.getStorageClass()).isEqualTo(expectedResponse.getStorageClass());
  }

  @Test
  public void testListParts_error() throws IOException {
    ListPartsRequest request =
        ListPartsRequest.builder().bucket(BUCKET).key(KEY).uploadId(UPLOAD_ID).maxParts(10).build();
    HttpResponse httpResponse = createHttpResponse(500, "Internal Server Error");

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendListPartsRequest(any(String.class), any()))
        .thenReturn(httpResponse);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> multipartUploadClient.listParts(request));
    assertThat(exception.getMessage()).isEqualTo("Failed to list parts: 500 Internal Server Error");
  }

  @Test
  public void testListParts_withQueryParameters() throws IOException {
    ListPartsRequest request =
        ListPartsRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .uploadId(UPLOAD_ID)
            .maxParts(10)
            .partNumberMarker(5)
            .build();
    ListPartsResponse expectedResponse =
        xmlMapper.readValue(
            "<ListPartsResult>\n"
                + "  <Bucket>bucket</Bucket>\n"
                + "  <Key>key</Key>\n"
                + "  <UploadId>uploadId</UploadId>\n"
                + "  <MaxParts>10</MaxParts>\n"
                + "  <PartNumberMarker>5</PartNumberMarker>\n"
                + "  <NextPartNumberMarker>10</NextPartNumberMarker>\n"
                + "</ListPartsResult>",
            ListPartsResponse.class);
    String responseXml = xmlMapper.writeValueAsString(expectedResponse);
    HttpResponse httpResponse = createHttpResponse(200, responseXml);

    when(retrier.run(any(), any(), any())).thenAnswer(invocation -> {
      invocation.getArgument(1, java.util.concurrent.Callable.class).call();
      return httpResponse;
    });
    when(httpRequestManager.sendListPartsRequest(uriCaptor.capture(), any()))
        .thenReturn(httpResponse);

    multipartUploadClient.listParts(request);

    String capturedUri = uriCaptor.getValue();
    assertThat(capturedUri).contains("&max-parts=10");
    assertThat(capturedUri).contains("&part-number-marker=5");
  }

  private HttpResponse createHttpResponse(int statusCode, String content) throws IOException {
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            return new MockLowLevelHttpRequest() {
              @Override
              public LowLevelHttpResponse execute() {
                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                response.setStatusCode(statusCode);
                response.setContentType(Json.MEDIA_TYPE);
                response.setContent(content);
                return response;
              }
            };
          }
        };
    HttpRequestFactory requestFactory = transport.createRequestFactory();
    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("https://storage.googleapis.com"));
    request.setThrowExceptionOnExecuteError(false);
    return request.execute();
  }
}
