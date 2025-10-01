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

import static com.google.cloud.storage.MultipartUploadUtility.getRfc1123Date;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultipartUploadClientImpl extends MultipartUploadClient {

  private static final String GCS_ENDPOINT = "https://storage.googleapis.com";

  private final HttpRequestManager httpRequestManager;
  private final GoogleCredentials credentials;
  private final XmlMapper xmlMapper;
  private final HttpStorageOptions options;

  public MultipartUploadClientImpl(
      URI uri, HttpRequestFactory requestFactory, Retrier retrier, HttpStorageOptions options) {
    this.httpRequestManager = new HttpRequestManager(requestFactory);
    this.xmlMapper = new XmlMapper();
    this.options = options;
    try {
      this.credentials =
          GoogleCredentials.getApplicationDefault()
              .createScoped(Collections.singleton("https://www.googleapis.com/auth/devstorage.read_write"));
    } catch (IOException e) {
      throw new RuntimeException("Failed to get application default credentials", e);
    }
  }

  private Map<String, String> getExtensionHeader() {
    Map<String, String> extensionHeaders = new HashMap<>();
    if (options.getClientLibToken() != null) {
      extensionHeaders.put("x-goog-api-client", options.getClientLibToken());
    }
    if (options.getProjectId() != null) {
      extensionHeaders.put("x-goog-user-project", options.getProjectId());
    }
    return extensionHeaders;
  }

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String uri = GCS_ENDPOINT + resourcePath + "?uploads";
    String date = getRfc1123Date();
    String contentType =
        request.getContentType() == null
            ? "application/x-www-form-urlencoded"
            : request.getContentType();
    Map<String, String> extensionHeaders = getExtensionHeader();
    if (request.getCannedAcl() != null) {
      extensionHeaders.put("x-goog-acl", request.getCannedAcl().toString());
    }
    if (request.getMetadata() != null) {
      for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
        extensionHeaders.put("x-goog-meta-" + entry.getKey(), entry.getValue());
      }
    }
    if (request.getStorageClass() != null) {
      extensionHeaders.put("x-goog-storage-class", request.getStorageClass());
    }

    credentials.refreshIfExpired();
    AccessToken accessToken = credentials.getAccessToken();
    String authHeader = "Bearer " + accessToken.getTokenValue();

    HttpResponse response =
        httpRequestManager.sendCreateMultipartUploadRequest(
            uri,
            date,
            authHeader,
            contentType,
            request.getContentDisposition(),
            request.getContentEncoding(),
            request.getContentLanguage(),
            extensionHeaders);

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to initiate upload: " + response.getStatusCode() + " " + error);
    }

    return xmlMapper.readValue(response.getContent(), CreateMultipartUploadResponse.class);
  }

  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException, NoSuchAlgorithmException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString =
        "?partNumber=" + request.partNumber() + "&uploadId=" + encode(request.uploadId());
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();
    String contentType = "application/octet-stream";
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] partData = requestBody.getPartData();
    String contentMd5 = Base64.getEncoder().encodeToString(md.digest(partData));
    String crc32cString =
        Base64.getEncoder()
            .encodeToString(
                ByteBuffer.allocate(4)
                    .putInt(Hashing.crc32c().hashBytes(partData).asInt())
                    .array());
    Map<String, String> extensionHeaders = getExtensionHeader();
    extensionHeaders.put("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);

    credentials.refreshIfExpired();
    AccessToken accessToken = credentials.getAccessToken();
    String authHeader = "Bearer " + accessToken.getTokenValue();

    HttpResponse response =
        httpRequestManager.sendUploadPartRequest(
            uri,
            partData,
            date,
            authHeader,
            contentType,
            contentMd5,
            crc32cString,
            extensionHeaders);

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to upload part "
              + request.partNumber()
              + ": "
              + response.getStatusCode()
              + " "
              + error);
    }
    String eTag = response.getHeaders().getETag();
    return UploadPartResponse.builder().eTag(eTag).build();
  }

  public CompleteMultipartUploadResponse completeMultipartUpload(
      CompleteMultipartUploadRequest request) throws NoSuchAlgorithmException, IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + encode(request.uploadId());
    String uri = GCS_ENDPOINT + resourcePath + queryString;

    byte[] xmlBodyBytes = xmlMapper.writeValueAsBytes(request.multipartUpload());

    MessageDigest md = MessageDigest.getInstance("MD5");
    String contentMd5 = Base64.getEncoder().encodeToString(md.digest(xmlBodyBytes));
    String date = getRfc1123Date();
    String contentType = "application/xml";
    String crc32cString =
        Base64.getEncoder()
            .encodeToString(
                ByteBuffer.allocate(4)
                    .putInt(Hashing.crc32c().hashBytes(xmlBodyBytes).asInt())
                    .array());

    Map<String, String> extensionHeaders = getExtensionHeader();
    extensionHeaders.put("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);
    if (request.requestPayer() != null) {
      extensionHeaders.put("x-amz-request-payer", request.requestPayer());
    }
    if (request.expectedBucketOwner() != null) {
      extensionHeaders.put("x-amz-expected-bucket-owner", request.expectedBucketOwner());
    }

    credentials.refreshIfExpired();
    AccessToken accessToken = credentials.getAccessToken();
    String authHeader = "Bearer " + accessToken.getTokenValue();

    HttpResponse response =
        httpRequestManager.sendCompleteMultipartUploadRequest(
            uri,
            xmlBodyBytes,
            date,
            authHeader,
            contentType,
            contentMd5,
            crc32cString,
            extensionHeaders);

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to complete upload: " + response.getStatusCode() + " " + error);
    }
    return xmlMapper.readValue(response.getContent(), CompleteMultipartUploadResponse.class);
  }

  @Override
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request)
      throws IOException, NoSuchAlgorithmException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + encode(request.uploadId());
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();
    String contentType = "application/x-www-form-urlencoded";
    Map<String, String> extensionHeaders = getExtensionHeader();

    credentials.refreshIfExpired();
    AccessToken accessToken = credentials.getAccessToken();
    String authHeader = "Bearer " + accessToken.getTokenValue();

    HttpResponse response =
        httpRequestManager.sendAbortMultipartUploadRequest(
            uri, date, authHeader, contentType, extensionHeaders);

    if (response.getStatusCode() != 204) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to abort upload: " + response.getStatusCode() + " " + error);
    }
    return new AbortMultipartUploadResponse();
  }

  @Override
  public ListPartsResponse listParts(ListPartsRequest request) throws IOException {
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
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();
    Map<String, String> extensionHeaders = getExtensionHeader();

    credentials.refreshIfExpired();
    AccessToken accessToken = credentials.getAccessToken();
    String authHeader = "Bearer " + accessToken.getTokenValue();

    HttpResponse response =
        httpRequestManager.sendListPartsRequest(uri, date, authHeader, extensionHeaders);

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to list parts: " + response.getStatusCode() + " " + error);
    }

    return xmlMapper.readValue(response.getContent(), ListPartsResponse.class);
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }
}
