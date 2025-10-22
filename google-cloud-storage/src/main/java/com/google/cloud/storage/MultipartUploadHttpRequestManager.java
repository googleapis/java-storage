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

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.util.ObjectParser;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

final class MultipartUploadHttpRequestManager {

  private final HttpRequestFactory requestFactory;
  private final ObjectParser objectParser;

  MultipartUploadHttpRequestManager(HttpRequestFactory requestFactory, ObjectParser objectParser) {
    this.requestFactory = requestFactory;
    this.objectParser = objectParser;
  }

  CreateMultipartUploadResponse sendCreateMultipartUploadRequest(
      URI uri, CreateMultipartUploadRequest request, HttpStorageOptions options)
      throws IOException {

    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String createUri = uri.toString() + resourcePath + "?uploads";

    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(createUri), new ByteArrayContent(request.getContentType(), new byte[0]));
    httpRequest.getHeaders().setContentType(request.getContentType());
    for (Map.Entry<String, String> entry :
        getExtensionHeadersForCreateMultipartUpload(request, options).entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(CreateMultipartUploadResponse.class);
  }

  ListPartsResponse sendListPartsRequest(
      URI uri, ListPartsRequest request, HttpStorageOptions options) throws IOException {

    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + encode(request.uploadId());

    if (request.getMaxParts() != null) {
      queryString += "&max-parts=" + request.getMaxParts();
    }
    if (request.getPartNumberMarker() != null) {
      queryString += "&part-number-marker=" + request.getPartNumberMarker();
    }
    String listUri = uri.toString() + resourcePath + queryString;
    Map<String, String> extensionHeaders = getGenericExtensionHeader(options);
    HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(listUri));
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(ListPartsResponse.class);
  }

  AbortMultipartUploadResponse sendAbortMultipartUploadRequest(
      URI uri, AbortMultipartUploadRequest request, HttpStorageOptions options) throws IOException {

    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + encode(request.uploadId());
    String abortUri = uri.toString() + resourcePath + queryString;
    String contentType = "application/x-www-form-urlencoded";
    Map<String, String> extensionHeaders = getGenericExtensionHeader(options);

    HttpRequest httpRequest = requestFactory.buildDeleteRequest(new GenericUrl(abortUri));
    httpRequest.getHeaders().setContentType(contentType);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(AbortMultipartUploadResponse.class);
  }

  private Map<String, String> getExtensionHeadersForCreateMultipartUpload(
      CreateMultipartUploadRequest request, HttpStorageOptions options) {
    Map<String, String> extensionHeaders = getGenericExtensionHeader(options);
    if (request.getCannedAcl() != null) {
      extensionHeaders.put("x-goog-acl", request.getCannedAcl().toString());
    }
    // TODO(shreyassinha) Add encoding for x-goog-meta-* headers
    if (request.getMetadata() != null) {
      for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
        if (entry.getKey() != null || entry.getValue() != null) {
          extensionHeaders.put("x-goog-meta-" + entry.getKey(), entry.getValue());
        }
      }
    }
    if (request.getStorageClass() != null) {
      extensionHeaders.put("x-goog-storage-class", request.getStorageClass().toString());
    }
    if (request.getKmsKeyName() != null && !request.getKmsKeyName().isEmpty()) {
      extensionHeaders.put("x-goog-encryption-kms-key-name", request.getKmsKeyName());
    }
    if (request.getObjectLockMode() != null) {
      extensionHeaders.put("x-goog-object-lock-mode", request.getObjectLockMode().toString());
    }
    if (request.getObjectLockRetainUntilDate() != null) {
      extensionHeaders.put(
          "x-goog-object-lock-retain-until-date",
          toRfc3339String(request.getObjectLockRetainUntilDate()));
    }
    if (request.getCustomTime() != null) {
      extensionHeaders.put("x-goog-custom-time", toRfc3339String(request.getCustomTime()));
    }
    return extensionHeaders;
  }

  private Map<String, String> getGenericExtensionHeader(HttpStorageOptions options) {
    Map<String, String> extensionHeaders = new HashMap<>();
    if (options.getClientLibToken() != null) {
      extensionHeaders.put("x-goog-api-client", options.getClientLibToken());
    }
    if (options.getProjectId() != null) {
      extensionHeaders.put("x-goog-user-project", options.getProjectId());
    }
    return extensionHeaders;
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }

  private String toRfc3339String(OffsetDateTime dateTime) {
    return DateTimeFormatter.ISO_INSTANT.format(dateTime);
  }
}
