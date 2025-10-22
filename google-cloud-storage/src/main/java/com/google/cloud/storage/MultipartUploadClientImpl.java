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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.core.BetaApi;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.common.net.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link MultipartUploadClient} that uses the Google Cloud
 * Storage XML API to perform multipart uploads.
 */
@BetaApi
public final class MultipartUploadClientImpl extends MultipartUploadClient {

  private final MultipartUploadHttpRequestManager httpRequestManager;
  private final XmlMapper xmlMapper;
  private final HttpStorageOptions options;
  private final Retrier retrier;
  private final URI uri;

  public MultipartUploadClientImpl(
      URI uri, HttpRequestFactory requestFactory, Retrier retrier, HttpStorageOptions options) {
    this.httpRequestManager = new MultipartUploadHttpRequestManager(requestFactory);
    this.xmlMapper = new XmlMapper();
    this.options = options;
    this.retrier = retrier;
    this.uri = uri;
  }

  private Map<String, String> getGenericExtensionHeader() {
    Map<String, String> extensionHeaders = new HashMap<>();
    if (options.getClientLibToken() != null) {
      extensionHeaders.put("x-goog-api-client", options.getClientLibToken());
    }
    if (options.getProjectId() != null) {
      extensionHeaders.put("x-goog-user-project", options.getProjectId());
    }
    extensionHeaders.put("Date", getRfc1123Date());
    return extensionHeaders;
  }

  @BetaApi
  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String createUri = uri.toString() + resourcePath + "?uploads";

    String contentType;
    if (request.getContentType() == null) {
      contentType = "application/x-www-form-urlencoded";
    } else {
      try {
        contentType = MediaType.parse(request.getContentType()).toString();
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
            "Invalid Content-Type header provided: " + request.getContentType(), e);
      }
    }

    HttpResponse response =
        httpRequestManager.sendCreateMultipartUploadRequest(
            createUri, contentType, request, getExtensionHeadersForCreateMultipartUpload(request));

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to initiate upload: " + response.getStatusCode() + " " + error);
    }

    return xmlMapper.readValue(response.getContent(), CreateMultipartUploadResponse.class);
  }

  private Map<String, String> getExtensionHeadersForCreateMultipartUpload(
      CreateMultipartUploadRequest request) {
    Map<String, String> extensionHeaders = getGenericExtensionHeader();
    if (request.getCannedAcl() != null) {
      extensionHeaders.put("x-goog-acl", request.getCannedAcl().toString());
    }
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
    // x-goog-object-lock-mode and x-goog-object-lock-retain-until-date should be specified together
    // Refer: https://cloud.google.com/storage/docs/xml-api/post-object-multipart#request_headers
    if (request.getObjectLockMode() != null && request.getObjectLockRetainUntilDate() != null) {
      extensionHeaders.put("x-goog-object-lock-mode", request.getObjectLockMode().toString());
      extensionHeaders.put(
          "x-goog-object-lock-retain-until-date",
          toRfc3339String(request.getObjectLockRetainUntilDate()));
    }
    if (request.getCustomTime() != null) {
      extensionHeaders.put("x-goog-custom-time", toRfc3339String(request.getCustomTime()));
    }
    return extensionHeaders;
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }

  private String toRfc3339String(OffsetDateTime dateTime) {
    return DateTimeFormatter.ISO_INSTANT.format(dateTime);
  }

  public static String getRfc1123Date() {
    return DateTimeFormatter.RFC_1123_DATE_TIME
        .withZone(ZoneId.of("GMT"))
        .format(ZonedDateTime.now());
  }
}
