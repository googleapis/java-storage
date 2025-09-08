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
package com.google.cloud.storage.multipartuploader;

import static com.google.cloud.storage.multipartuploader.MultipartUploaderUtility.getRfc1123Date;
import static com.google.cloud.storage.multipartuploader.MultipartUploaderUtility.readStream;
import static com.google.cloud.storage.multipartuploader.MultipartUploaderUtility.signRequest;

import com.google.cloud.storage.multipartuploader.data.CompleteMultipartRequest;
import com.google.cloud.storage.multipartuploader.data.CompleteMultipartResponse;
import com.google.cloud.storage.multipartuploader.data.CompletedPart;
import com.google.cloud.storage.multipartuploader.data.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartuploader.data.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartuploader.data.RequestBody;
import com.google.cloud.storage.multipartuploader.data.UploadPartRequest;
import com.google.cloud.storage.multipartuploader.data.UploadPartResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MultipartUploaderImpl implements MultipartUploader {

  private static final String BUCKET_NAME = "shreyassinha";
  private static final String OBJECT_NAME = "5mb"; // The name for the object in GCS
  private static final String FILE_PATH = "5mb-examplefile-com.txt"; // The local file to upload

  // Add HMAC keys from GCS Settings > Interoperability

  // --- End Configuration ---
  private static final String GCS_ENDPOINT = "https://storage.googleapis.com";

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    //String resourcePath = "/" + request.getBucketName() + "/" + request.getObjectName();
    String resourcePath = "/" + BUCKET_NAME + "/" + OBJECT_NAME;
    String uri = GCS_ENDPOINT + resourcePath + "?uploads";
    String date = getRfc1123Date();
    String contentType = "application/x-www-form-urlencoded";
    // GCS Signature Rule #1: The '?uploads' query string IS included for the initiate request.
    String signature = signRequest("POST", "", contentType, date, resourcePath + "?uploads", GOOGLE_SECRET_KEY);
    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Date", date);
    connection.setRequestProperty("Authorization", authHeader);
    connection.setRequestProperty("Content-Type", contentType);
    connection.setFixedLengthStreamingMode(0);
    connection.setDoOutput(true);

    if (connection.getResponseCode() != 200) {
      String error = readStream(connection.getErrorStream());
      throw new RuntimeException("Failed to initiate upload: " + connection.getResponseCode() + " " + error);
    }

    String responseBody = readStream(connection.getInputStream());
    String uploadIdTag = "<UploadId>";
    int start = responseBody.indexOf(uploadIdTag) + uploadIdTag.length();
    int end = responseBody.indexOf("</UploadId>");
    int uploadId = Integer.parseInt(responseBody.substring(start, end));
    return new CreateMultipartUploadResponse(uploadId);
  }

  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException {
    String resourcePath = "/" + BUCKET_NAME + "/" + OBJECT_NAME;
    String queryString = "?partNumber=" + request.getPartNumber() + "&uploadId=" + request.getUploadId();
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();
    String contentType = "application/octet-stream";
    // GCS Signature Rule #2: The query string IS NOT included for the PUT part request.
    String signature = signRequest("PUT", "", contentType, date, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
    connection.setRequestMethod("PUT");
    connection.setRequestProperty("Date", date);
    connection.setRequestProperty("Authorization", authHeader);
    connection.setRequestProperty("Content-Type", contentType);
    connection.setFixedLengthStreamingMode(requestBody.getPartDate().length);
    connection.setDoOutput(true);

    try (OutputStream os = connection.getOutputStream()) {
      os.write(requestBody.getPartDate());
    }

    if (connection.getResponseCode() != 200) {
      String error = readStream(connection.getErrorStream());
        throw new RuntimeException("Failed to upload part " + request.getPartNumber() + ": " + connection.getResponseCode() + " " + error);
    }
    String eTag = connection.getHeaderField("ETag");
    return new UploadPartResponse(eTag);
  }

  public CompleteMultipartResponse completeMultipartUpload(CompleteMultipartRequest request)
      throws NoSuchAlgorithmException {
    String resourcePath = "/" + BUCKET_NAME + "/" + OBJECT_NAME;
    String queryString = "?uploadId=" + request.getUploadId();
    String uri = GCS_ENDPOINT + resourcePath + queryString;

    StringBuilder xmlBodyBuilder = new StringBuilder("<CompleteMultipartUpload>\n");
    for (CompletedPart part : request.getCompletedParts()) {
      xmlBodyBuilder.append("  <Part>\n");
      xmlBodyBuilder.append("    <PartNumber>").append(part.getPartNumber()).append("</PartNumber>\n");
      xmlBodyBuilder.append("    <ETag>").append(part.getEtag()).append("</ETag>\n");
      xmlBodyBuilder.append("  </Part>\n");
    }
    xmlBodyBuilder.append("</CompleteMultipartUpload>");
    byte[] xmlBodyBytes = xmlBodyBuilder.toString().getBytes(StandardCharsets.UTF_8);

    MessageDigest md = MessageDigest.getInstance("MD5");
    String contentMd5 = Base64.getEncoder().encodeToString(md.digest(xmlBodyBytes));
    String date = getRfc1123Date();
    String contentType = "application/xml";

    // GCS Signature Rule #3: The query string IS NOT included for the POST complete request.
    String signature = signRequest("POST", contentMd5, contentType, date, resourcePath);
    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Date", date);
    connection.setRequestProperty("Authorization", authHeader);
    connection.setRequestProperty("Content-Type", contentType);
    connection.setRequestProperty("Content-MD5", contentMd5);
    connection.setFixedLengthStreamingMode(xmlBodyBytes.length);
    connection.setDoOutput(true);

    try (OutputStream os = connection.getOutputStream()) {
      os.write(xmlBodyBytes);
    }

    if (connection.getResponseCode() != 200) {
      String error = readStream(connection.getErrorStream());
      throw new RuntimeException("Failed to complete upload: " + connection.getResponseCode() + " " + error);
    }
    return null;
  }
}
