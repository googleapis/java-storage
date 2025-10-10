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
import com.google.cloud.storage.Conversions.Decoder;
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
import com.google.common.net.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultipartUploadClientImpl extends MultipartUploadClient {

  private static final String GCS_ENDPOINT = "https://storage.googleapis.com";

  private final HttpRequestManager httpRequestManager;
  private final XmlMapper xmlMapper;
  private final HttpStorageOptions options;
  private final Retrier retrier;

  public MultipartUploadClientImpl(
      URI uri, HttpRequestFactory requestFactory, Retrier retrier, HttpStorageOptions options) {
    this.httpRequestManager = new HttpRequestManager(requestFactory);
    this.xmlMapper = new XmlMapper();
    this.options = options;
    this.retrier = retrier;
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

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String uri = GCS_ENDPOINT + resourcePath + "?uploads";

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
            uri,
            contentType,
            request,
            getExtensionHeadersForCreateMultipartUpload(request));

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to initiate upload: " + response.getStatusCode() + " " + error);
    }

    return xmlMapper.readValue(response.getContent(), CreateMultipartUploadResponse.class);
  }

  private Map<String, String> getExtensionHeadersForCreateMultipartUpload(CreateMultipartUploadRequest request){
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
      extensionHeaders.put("x-goog-object-lock-retain-until-date", toRfc3339String(request.getObjectLockRetainUntilDate()));
    }
    if (request.getCustomTime() != null) {
      extensionHeaders.put("x-goog-custom-time", toRfc3339String(request.getCustomTime()));
    }
    return extensionHeaders;
  }

  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException, NoSuchAlgorithmException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString =
        "?partNumber=" + request.partNumber() + "&uploadId=" + encode(request.uploadId());
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String contentType = "application/octet-stream";
    String contentMd5 = requestBody.getContent().getMd5();
    String crc32cString = requestBody.getContent().getCrc32c();
    Map<String, String> extensionHeaders = getGenericExtensionHeader();
    extensionHeaders.put("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);
    HttpResponse response = retrier.run(
        Retrying.alwaysRetry(),
        () -> {
          requestBody.getContent().rewindTo(0);
          return httpRequestManager.sendUploadPartRequest(uri, requestBody.getContent(), contentType,
              extensionHeaders);
        },
        Decoder.identity());

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
    String crc32cFromHeader = null;
    String md5FromHeader = null;
    String hashHeader = response.getHeaders().getFirstHeaderStringValue("x-goog-hash");
    if (hashHeader != null) {
      String[] hashes = hashHeader.split(",");
      for (String hash : hashes) {
        String[] kv = hash.trim().split("=", 2);
        if (kv.length == 2) {
          if ("crc32c".equalsIgnoreCase(kv[0])) {
            crc32cFromHeader = kv[1];
          } else if ("md5".equalsIgnoreCase(kv[0])) {
            md5FromHeader = kv[1];
          }
        }
      }
    }
    return UploadPartResponse.builder().eTag(eTag).crc32c(crc32cFromHeader).md5(md5FromHeader).build();
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
    String contentType = "application/xml";
    String crc32cString =
        Base64.getEncoder()
            .encodeToString(
                ByteBuffer.allocate(4)
                    .putInt(Hashing.crc32c().hashBytes(xmlBodyBytes).asInt())
                    .array());

    Map<String, String> extensionHeaders = getGenericExtensionHeader();
    extensionHeaders.put("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);

    HttpResponse response = retrier.run(
        Retrying.alwaysRetry(),
        () -> {
          return httpRequestManager.sendCompleteMultipartUploadRequest(
              uri,
              xmlBodyBytes,
              contentType,
              extensionHeaders);
        },
        Decoder.identity());

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
    String contentType = "application/x-www-form-urlencoded";
    Map<String, String> extensionHeaders = getGenericExtensionHeader();

    HttpResponse response = retrier.run(
        Retrying.alwaysRetry(),
        () -> {
          return httpRequestManager.sendAbortMultipartUploadRequest(
              uri, contentType, extensionHeaders);
        },
        Decoder.identity());

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
    Map<String, String> extensionHeaders = getGenericExtensionHeader();

    HttpResponse response = retrier.run(
        Retrying.alwaysRetry(),
        () -> {
          return httpRequestManager.sendListPartsRequest(uri, extensionHeaders);
        },
        Decoder.identity());

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

  private String toRfc3339String(Date date) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    df.setTimeZone(tz);
    return df.format(date);
  }
}
