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

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HttpRequestManagerTest {

  private static final String BUCKET = "bucket";
  private static final String KEY = "key";
  private static final String CONTENT_TYPE = "application/octet-stream";
  private static final String URI = "https://storage.googleapis.com/" + BUCKET + "/" + KEY;

  @Test
  public void testSendCreateMultipartUploadRequest() throws IOException {
    final AtomicReference<String> capturedMethod = new AtomicReference<>();
    final MockLowLevelHttpRequest lowLevelRequest = new MockLowLevelHttpRequest();
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            capturedMethod.set(method);
            lowLevelRequest.setUrl(url);
            return lowLevelRequest;
          }
        };
    HttpRequestManager httpRequestManager = new HttpRequestManager(transport.createRequestFactory());

    CreateMultipartUploadRequest createRequest =
        CreateMultipartUploadRequest.builder().bucket(BUCKET).key(KEY).build();
    Map<String, String> headers = Collections.singletonMap("x-goog-test-header", "test-value");

    httpRequestManager.sendCreateMultipartUploadRequest(URI, CONTENT_TYPE, createRequest, headers);

    assertThat(capturedMethod.get()).isEqualTo("POST");
    assertThat(lowLevelRequest.getUrl()).isEqualTo(URI);
    assertThat(lowLevelRequest.getHeaderValues("x-goog-test-header")).containsExactly("test-value");
    assertThat(lowLevelRequest.getContentAsString()).isEqualTo("");
  }

  @Test
  public void testSendUploadPartRequest() throws IOException {
    final AtomicReference<String> capturedMethod = new AtomicReference<>();
    final MockLowLevelHttpRequest lowLevelRequest = new MockLowLevelHttpRequest();
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            capturedMethod.set(method);
            lowLevelRequest.setUrl(url);
            return lowLevelRequest;
          }
        };
    HttpRequestManager httpRequestManager = new HttpRequestManager(transport.createRequestFactory());

    byte[] contentBytes = "test content".getBytes();
    HttpContent content = new ByteArrayContent(CONTENT_TYPE, contentBytes);
    Map<String, String> headers = Collections.singletonMap("x-goog-test-header", "test-value");

    httpRequestManager.sendUploadPartRequest(URI, content, CONTENT_TYPE, headers);

    assertThat(capturedMethod.get()).isEqualTo("PUT");
    assertThat(lowLevelRequest.getUrl()).isEqualTo(URI);
    assertThat(lowLevelRequest.getHeaderValues("x-goog-test-header")).containsExactly("test-value");
    assertThat(lowLevelRequest.getContentAsString()).isEqualTo("test content");
  }

  @Test
  public void testSendCompleteMultipartUploadRequest() throws IOException {
    final AtomicReference<String> capturedMethod = new AtomicReference<>();
    final MockLowLevelHttpRequest lowLevelRequest = new MockLowLevelHttpRequest();
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            capturedMethod.set(method);
            lowLevelRequest.setUrl(url);
            return lowLevelRequest;
          }
        };
    HttpRequestManager httpRequestManager = new HttpRequestManager(transport.createRequestFactory());

    byte[] xmlBodyBytes = "<CompleteMultipartUpload/>".getBytes();
    Map<String, String> headers = Collections.singletonMap("x-goog-test-header", "test-value");

    httpRequestManager.sendCompleteMultipartUploadRequest(URI, xmlBodyBytes, CONTENT_TYPE, headers);

    assertThat(capturedMethod.get()).isEqualTo("POST");
    assertThat(lowLevelRequest.getUrl()).isEqualTo(URI);
    assertThat(lowLevelRequest.getHeaderValues("x-goog-test-header")).containsExactly("test-value");
    assertThat(lowLevelRequest.getContentAsString()).isEqualTo("<CompleteMultipartUpload/>");
  }

  @Test
  public void testSendAbortMultipartUploadRequest() throws IOException {
    final AtomicReference<String> capturedMethod = new AtomicReference<>();
    final MockLowLevelHttpRequest lowLevelRequest = new MockLowLevelHttpRequest();
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            capturedMethod.set(method);
            lowLevelRequest.setUrl(url);
            return lowLevelRequest;
          }
        };
    HttpRequestManager httpRequestManager = new HttpRequestManager(transport.createRequestFactory());

    Map<String, String> headers = Collections.singletonMap("x-goog-test-header", "test-value");

    httpRequestManager.sendAbortMultipartUploadRequest(URI, CONTENT_TYPE, headers);

    assertThat(capturedMethod.get()).isEqualTo("DELETE");
    assertThat(lowLevelRequest.getUrl()).isEqualTo(URI);
    assertThat(lowLevelRequest.getHeaderValues("x-goog-test-header")).containsExactly("test-value");
  }

  @Test
  public void testSendListPartsRequest() throws IOException {
    final AtomicReference<String> capturedMethod = new AtomicReference<>();
    final MockLowLevelHttpRequest lowLevelRequest = new MockLowLevelHttpRequest();
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            capturedMethod.set(method);
            lowLevelRequest.setUrl(url);
            return lowLevelRequest;
          }
        };
    HttpRequestManager httpRequestManager = new HttpRequestManager(transport.createRequestFactory());

    Map<String, String> headers = Collections.singletonMap("x-goog-test-header", "test-value");

    httpRequestManager.sendListPartsRequest(URI, headers);

    assertThat(capturedMethod.get()).isEqualTo("GET");
    assertThat(lowLevelRequest.getUrl()).isEqualTo(URI);
    assertThat(lowLevelRequest.getHeaderValues("x-goog-test-header")).containsExactly("test-value");
  }
}