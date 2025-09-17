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
import static com.google.cloud.storage.MultipartUploadUtility.readStream;
import static com.google.cloud.storage.MultipartUploadUtility.signRequest;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.util.ObjectParser;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.UnimplementedException;
import com.google.cloud.storage.Retrying.Retrier;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CompleteMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import io.grpc.Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MultipartUploadClientImpl extends MultipartUploadClient {

  // Add HMAC keys from GCS Settings > Interoperability

  // --- End Configuration ---
  private static final String GCS_ENDPOINT = "https://storage.googleapis.com";

  public MultipartUploadClientImpl(URI uri, HttpRequestFactory requestFactory, Retrier retrier) {
  }

  public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest request)
      throws IOException {
    String resourcePath = "/" + request.bucket() + "/" + request.key();
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

    XmlMapper xmlMapper = new XmlMapper();
    return xmlMapper.readValue(
        connection.getInputStream(), CreateMultipartUploadResponse.class);
  }

  public UploadPartResponse uploadPart(UploadPartRequest request, RequestBody requestBody)
      throws IOException {
    String resourcePath = "/" + request.bucket() + "/" + request.key();
    String queryString = "?partNumber=" + request.partNumber() + "&uploadId=" + request.uploadId();
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
    connection.setFixedLengthStreamingMode(requestBody.getPartData().length);
    connection.setDoOutput(true);

    try (OutputStream os = connection.getOutputStream()) {
      os.write(requestBody.getPartData());
    }

    if (connection.getResponseCode() != 200) {
      String error = readStream(connection.getErrorStream());
        throw new RuntimeException("Failed to upload part " + request.partNumber() + ": " + connection.getResponseCode() + " " + error);
    }
    String eTag = connection.getHeaderField("ETag");
    return UploadPartResponse.builder().eTag(eTag).build();
  }

  public CompleteMultipartUploadResponse completeMultipartUpload(CompleteMultipartUploadRequest request)
      throws NoSuchAlgorithmException, IOException {
    String resourcePath = "/" + request.bucket() + "/" + request.key();
    String queryString = "?uploadId=" + request.uploadId();
    String uri = GCS_ENDPOINT + resourcePath + queryString;

    XmlMapper xmlMapper = new XmlMapper();
    byte[] xmlBodyBytes = xmlMapper.writeValueAsBytes(request.multipartUpload());

    MessageDigest md = MessageDigest.getInstance("MD5");
    String contentMd5 = Base64.getEncoder().encodeToString(md.digest(xmlBodyBytes));
    String date = getRfc1123Date();
    String contentType = "application/xml";

    // GCS Signature Rule #3: The query string IS NOT included for the POST complete request.
    String signature = signRequest("POST", contentMd5, contentType, date, resourcePath, GOOGLE_SECRET_KEY);
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

  @Override
  public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest request) throws IOException{
    String resourcePath = "/" + request.bucket() + "/" + request.key();
    String queryString = "?uploadId=" + request.uploadId();
    String uri = GCS_ENDPOINT + resourcePath + queryString;
    String date = getRfc1123Date();

    // GCS Signature Rule #4: The query string IS NOT included for the DELETE abort request.
    String signature = signRequest("DELETE", "", "", date, resourcePath, GOOGLE_SECRET_KEY);

    String authHeader = "GOOG1 " + GOOGLE_ACCESS_KEY + ":" + signature;

    HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
    connection.setRequestMethod("DELETE");
    connection.setRequestProperty("Date", date);
    connection.setRequestProperty("Authorization", authHeader);

    if (connection.getResponseCode() != 204) {
      String error = readStream(connection.getErrorStream());
      throw new RuntimeException("Failed to abort upload: " + connection.getResponseCode() + " " + error);
    }
    return new AbortMultipartUploadResponse();
  }

  private static final class Utf8StringRequestContent extends AbstractHttpContent {

    private final byte[] xml;

    private Utf8StringRequestContent(byte[] xml) {
      // https://www.ietf.org/rfc/rfc2376.txt#:~:text=6.1%20text/xml%20with%20UTF%2D8%20Charset
      super("text/xml;charset=utf-8");
      this.xml = xml;
    }

    @Override
    public long getLength() throws IOException {
      return super.getLength();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      out.write(xml);
    }

    public static Utf8StringRequestContent of(String xml) {
      return new Utf8StringRequestContent(xml.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static class XmlObjectParser implements ObjectParser {

    @Override
    public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass)
        throws IOException {
      try (InputStream is = in) {
        return todo();
      }
    }

    @Override
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
      try (InputStream is = in) {
        return todo();
      }
    }

    @Override
    public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
      try (Reader r = reader) {
        return todo();
      }
    }

    @Override
    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
      try (Reader r = reader) {
        return todo();
      }
    }

    private static <T> T todo() {
      throw new UnimplementedException("todo", null, GrpcStatusCode.of(Status.Code.UNIMPLEMENTED), false);
    }
  }
}
