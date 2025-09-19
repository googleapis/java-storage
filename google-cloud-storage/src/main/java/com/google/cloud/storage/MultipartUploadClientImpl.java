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
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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

  private final HttpRequestFactory requestFactory;
  private final Map<String, String> extensionHeaders;

  public MultipartUploadClientImpl(URI uri, HttpRequestFactory requestFactory, Retrier retrier) {
    this.requestFactory = requestFactory;
    this.extensionHeaders = new HashMap<>();
    //TODO fix the hard coded header
    this.extensionHeaders.put(
        "x-goog-api-client",
        "gl-java/11.0.27__OpenLogic-OpenJDK__OpenLogic-OpenJDK gccl/2.56.1-SNAPSHOT--protobuf-3.25.8 gax/2.70.0 protobuf/3.25.8");
    this.extensionHeaders.put("x-goog-user-project", "aipp-internal-testing");
  }

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String uri = GCS_ENDPOINT + resourcePath + "?uploads";
    String date = getRfc1123Date();
    String contentType = "application/x-www-form-urlencoded";
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

    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, new byte[0]));
    httpRequest.getHeaders().set("Date", date);
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    HttpResponse response = httpRequest.execute();

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
    String signature =
        signRequest(
            "PUT", contentMd5, contentType, date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpRequest httpRequest =
        requestFactory.buildPutRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, partData));
    httpRequest.getHeaders().set("Date", date);
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    httpRequest.getHeaders().setContentMD5(contentMd5);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    HttpResponse response = httpRequest.execute();

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

    // GCS Signature Rule #3: The query string IS NOT included for the POST complete request.
    String signature =
        signRequest(
            "POST", contentMd5, contentType, date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);
    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(uri), new ByteArrayContent(contentType, xmlBodyBytes));
    httpRequest.getHeaders().set("Date", date);
    httpRequest.getHeaders().setAuthorization(authHeader);
    httpRequest.getHeaders().setContentType(contentType);
    httpRequest.getHeaders().setContentMD5(contentMd5);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    HttpResponse response = httpRequest.execute();

    if (!response.isSuccessStatusCode()) {
      String error = response.parseAsString();
      throw new RuntimeException(
          "Failed to complete upload: " + response.getStatusCode() + " " + error);
    }
    return xmlMapper.readValue(response.getContent(), CompleteMultipartUploadResponse.class);
  }

  @Override
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request)
      throws IOException {
    String encodedBucket = encode(request.bucket());
    String encodedKey = encode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + encode(request.uploadId());
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();

    // GCS Signature Rule #4: The query string IS NOT included for the DELETE abort request.
    String signature =
        signRequest("DELETE", "", "", date, extensionHeaders, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpRequest httpRequest = requestFactory.buildDeleteRequest(new GenericUrl(uri));
    httpRequest.getHeaders().set("Date", date);
    httpRequest.getHeaders().setAuthorization(authHeader);
    for (Map.Entry<String, String> entry : extensionHeaders.entrySet()) {
      httpRequest.getHeaders().set(entry.getKey(), entry.getValue());
    }
    httpRequest.setThrowExceptionOnExecuteError(false);
    HttpResponse response = httpRequest.execute();

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
