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
import static com.google.cloud.storage.MultipartUploadUtility.signRequest;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
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
import java.util.HashMap;
import java.util.Map;

public class MultipartUploadClientImpl extends MultipartUploadClient {

  // Add HMAC keys from GCS Settings > Interoperability


  // --- End Configuration ---
  private static final String GCS_ENDPOINT = "https://storage.googleapis.com";

  private final HttpRequestManager httpRequestManager;

  public MultipartUploadClientImpl(URI uri, HttpRequestFactory requestFactory, Retrier retrier) {
    this.httpRequestManager = new HttpRequestManager(requestFactory);
  }

  private Map<String, String> getExtensionHeader() {
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put(
        "x-goog-api-client",
        "gl-java/11.0.27__OpenLogic-OpenJDK__OpenLogic-OpenJDK gccl/2.56.1-SNAPSHOT--protobuf-3.25.8 gax/2.70.0 protobuf/3.25.8");
    extensionHeaders.put("x-goog-user-project", "aipp-internal-testing");
    return extensionHeaders;
  }

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String uri = GCS_ENDPOINT + resourcePath + "?uploads";
    String date = getRfc1123Date();
    String contentType = "application/x-www-form-urlencoded";
    Map<String, String> extensionHeaders = getExtensionHeader();
    // GCS Signature Rule #1: The '?uploads' query string IS included for the initiate request.
    String signature =
        signRequest(
            "POST",
            "",
            contentType,
            date,
            extensionHeaders,
            resourcePath + "?uploads",
            GOOGLE_SECRET_KEY);
    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpResponse response =
        httpRequestManager.sendCreateMultipartUploadRequest(
            uri, date, authHeader, contentType, extensionHeaders);

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to initiate upload: " + response.getStatusCode() + " " + error);
    }

    XmlMapper xmlMapper = new XmlMapper();
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
    // GCS Signature Rule #2: The query string IS NOT included for the PUT part request.
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
    String signature =
        signRequest(
            "PUT", contentMd5, contentType, date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

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

    XmlMapper xmlMapper = new XmlMapper();
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

    // GCS Signature Rule #3: The query string IS NOT included for the POST complete request.
    Map<String, String> extensionHeaders = getExtensionHeader();
    extensionHeaders.put("x-goog-hash", "crc32c=" + crc32cString + ",md5=" + contentMd5);
    String signature =
        signRequest(
            "POST", contentMd5, contentType, date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);
    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

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

    // GCS Signature Rule #4: The query string IS NOT included for the DELETE abort request.
    String signature =
        signRequest("DELETE", "", contentType, date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

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

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }
}
