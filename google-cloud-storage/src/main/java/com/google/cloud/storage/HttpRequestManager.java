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

import static com.google.cloud.storage.MultipartUploadUtility.getRfc1123Date;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import java.io.IOException;
import java.util.Map;

public class HttpRequestManager {

  private final HttpRequestFactory requestFactory;

  public HttpRequestManager(HttpRequestFactory requestFactory) {
    this.requestFactory = requestFactory;
  }

  public HttpResponse sendCreateMultipartUploadRequest(
      String uri,
      String authHeader,
      String contentType,
      CreateMultipartUploadRequest request,
      Map<String, String> extensionHeaders)
      throws IOException {
    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, new byte[0]));
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    return httpRequest.execute();
  }

  public HttpResponse sendUploadPartRequest(
      String uri,
      byte[] partData,
      String authHeader,
      String contentType,
      String contentMd5,
      String crc32cString,
      Map<String, String> extensionHeaders)
      throws IOException {
    HttpRequest httpRequest =
        requestFactory.buildPutRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, partData));
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    httpRequest.getHeaders().setContentMD5(contentMd5);
    httpRequest.getHeaders().set("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    return httpRequest.execute();
  }

  public HttpResponse sendCompleteMultipartUploadRequest(
      String uri,
      byte[] xmlBodyBytes,
      String authHeader,
      String contentType,
      String contentMd5,
      String crc32cString,
      Map<String, String> extensionHeaders)
      throws IOException {
    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, xmlBodyBytes));
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    httpRequest.getHeaders().setContentMD5(contentMd5);
    httpRequest.getHeaders().set("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    return httpRequest.execute();
  }

  public HttpResponse sendAbortMultipartUploadRequest(
      String uri, String authHeader, String contentType, Map<String, String> extensionHeaders)
      throws IOException {
    HttpRequest httpRequest = requestFactory.buildDeleteRequest(new GenericUrl(uri));
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    return httpRequest.execute();
  }

  public HttpResponse sendListPartsRequest(
      String uri, String authHeader, Map<String, String> extensionHeaders)
      throws IOException {
    HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(uri));
    httpRequest.getHeaders().setAuthorization(authHeader);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    return httpRequest.execute();
  }
}
