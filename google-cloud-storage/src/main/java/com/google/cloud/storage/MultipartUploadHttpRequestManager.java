/*
 * Copyright 2025 Google LLC
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

import static com.google.cloud.storage.Utils.ifNonNull;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.util.ObjectParser;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.services.storage.Storage;
import com.google.cloud.storage.Crc32cValue.Crc32cLengthKnown;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.AbortMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadRequest;
import com.google.cloud.storage.multipartupload.model.CreateMultipartUploadResponse;
import com.google.cloud.storage.multipartupload.model.ListPartsRequest;
import com.google.cloud.storage.multipartupload.model.ListPartsResponse;
import com.google.cloud.storage.multipartupload.model.UploadPartRequest;
import com.google.cloud.storage.multipartupload.model.UploadPartResponse;
import com.google.cloud.storage.multipartupload.model.UploadResponseParser;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MultipartUploadHttpRequestManager {

  private final HttpRequestFactory requestFactory;
  private final ObjectParser objectParser;
  private final HeaderProvider headerProvider;

  MultipartUploadHttpRequestManager(
      HttpRequestFactory requestFactory, ObjectParser objectParser, HeaderProvider headerProvider) {
    this.requestFactory = requestFactory;
    this.objectParser = objectParser;
    this.headerProvider = headerProvider;
  }

  CreateMultipartUploadResponse sendCreateMultipartUploadRequest(
      URI uri, CreateMultipartUploadRequest request) throws IOException {

    String encodedBucket = urlEncode(request.bucket());
    String encodedKey = urlEncode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String createUri = uri.toString() + resourcePath + "?uploads";

    HttpRequest httpRequest =
        requestFactory.buildPostRequest(
            new GenericUrl(createUri), new ByteArrayContent(request.getContentType(), new byte[0]));
    httpRequest.getHeaders().putAll(headerProvider.getHeaders());
    addHeadersForCreateMultipartUpload(request, httpRequest.getHeaders());
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(CreateMultipartUploadResponse.class);
  }

  ListPartsResponse sendListPartsRequest(URI uri, ListPartsRequest request) throws IOException {

    String encodedBucket = urlEncode(request.bucket());
    String encodedKey = urlEncode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + urlEncode(request.uploadId());

    if (request.getMaxParts() != null) {
      queryString += "&max-parts=" + request.getMaxParts();
    }
    if (request.getPartNumberMarker() != null) {
      queryString += "&part-number-marker=" + request.getPartNumberMarker();
    }
    String listUri = uri.toString() + resourcePath + queryString;
    HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(listUri));
    httpRequest.getHeaders().putAll(headerProvider.getHeaders());
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(ListPartsResponse.class);
  }

  AbortMultipartUploadResponse sendAbortMultipartUploadRequest(
      URI uri, AbortMultipartUploadRequest request) throws IOException {

    String encodedBucket = urlEncode(request.bucket());
    String encodedKey = urlEncode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString = "?uploadId=" + urlEncode(request.uploadId());
    String abortUri = uri.toString() + resourcePath + queryString;

    HttpRequest httpRequest = requestFactory.buildDeleteRequest(new GenericUrl(abortUri));
    httpRequest.setParser(objectParser);
    httpRequest.setThrowExceptionOnExecuteError(true);
    return httpRequest.execute().parseAs(AbortMultipartUploadResponse.class);
  }

  UploadPartResponse sendUploadPartRequest(
      URI uri, UploadPartRequest request, RewindableContent rewindableContent) throws IOException {
    String encodedBucket = urlEncode(request.bucket());
    String encodedKey = urlEncode(request.key());
    String resourcePath = "/" + encodedBucket + "/" + encodedKey;
    String queryString =
        "?partNumber=" + request.partNumber() + "&uploadId=" + urlEncode(request.uploadId());
    String uploadUri = uri.toString() + resourcePath + queryString;
    HttpRequest httpRequest =
        requestFactory.buildPutRequest(new GenericUrl(uploadUri), rewindableContent);
    httpRequest.getHeaders().putAll(headerProvider.getHeaders());
    addChecksumHeader(rewindableContent.getCrc32c(), httpRequest.getHeaders());
    httpRequest.setThrowExceptionOnExecuteError(true);
    return UploadResponseParser.parse(httpRequest.execute());
  }

  static MultipartUploadHttpRequestManager createFrom(HttpStorageOptions options) {
    Storage storage = options.getStorageRpcV1().getStorage();
    ImmutableMap.Builder<String, String> stableHeaders =
        ImmutableMap.<String, String>builder()
            // http-java-client will automatically append its own version to the user-agent
            .put("User-Agent", "gcloud-java/" + options.getLibraryVersion())
            .put(
                "x-goog-api-client",
                String.format(
                    "gl-java/%s gccl/%s %s/%s",
                    GaxProperties.getJavaVersion(),
                    options.getLibraryVersion(),
                    formatName(StandardSystemProperty.OS_NAME.value()),
                    formatSemver(StandardSystemProperty.OS_VERSION.value())));
    ifNonNull(options.getProjectId(), pid -> stableHeaders.put("x-goog-user-project", pid));
    return new MultipartUploadHttpRequestManager(
        storage.getRequestFactory(),
        new XmlObjectParser(new XmlMapper()),
        options.getMergedHeaderProvider(FixedHeaderProvider.create(stableHeaders.build())));
  }

  private void addChecksumHeader(Crc32cLengthKnown crc32c, HttpHeaders headers) {
    headers.put("x-goog-hash", "crc32c=" + Utils.crc32cCodec.encode(crc32c.getValue()));
  }

  private void addHeadersForCreateMultipartUpload(
      CreateMultipartUploadRequest request, HttpHeaders headers) {
    // TODO(shreyassinha): add a PredefinedAcl::getXmlEntry with the corresponding value from
    //   https://cloud.google.com/storage/docs/xml-api/reference-headers#xgoogacl
    if (request.getCannedAcl() != null) {
      headers.put("x-goog-acl", request.getCannedAcl().toString());
    }
    // TODO(shreyassinha) Add encoding for x-goog-meta-* headers
    if (request.getMetadata() != null) {
      for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
        if (entry.getKey() != null || entry.getValue() != null) {
          headers.put("x-goog-meta-" + entry.getKey(), entry.getValue());
        }
      }
    }
    if (request.getStorageClass() != null) {
      headers.put("x-goog-storage-class", request.getStorageClass().toString());
    }
    if (request.getKmsKeyName() != null && !request.getKmsKeyName().isEmpty()) {
      headers.put("x-goog-encryption-kms-key-name", request.getKmsKeyName());
    }
    if (request.getObjectLockMode() != null) {
      headers.put("x-goog-object-lock-mode", request.getObjectLockMode().toString());
    }
    if (request.getObjectLockRetainUntilDate() != null) {
      headers.put(
          "x-goog-object-lock-retain-until-date",
          Utils.offsetDateTimeRfc3339Codec.encode(request.getObjectLockRetainUntilDate()));
    }
    if (request.getCustomTime() != null) {
      headers.put(
          "x-goog-custom-time", Utils.offsetDateTimeRfc3339Codec.encode(request.getCustomTime()));
    }
  }

  private static String urlEncode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }

  private static String formatName(String name) {
    // Only lowercase letters, digits, and "-" are allowed
    return name.toLowerCase().replaceAll("[^\\w\\d\\-]", "-");
  }

  private static String formatSemver(String version) {
    return formatSemver(version, version);
  }

  private static String formatSemver(String version, String defaultValue) {
    if (version == null) {
      return null;
    }

    // Take only the semver version: x.y.z-a_b_c -> x.y.z
    Matcher m = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*").matcher(version);
    if (m.find()) {
      return m.group(1);
    } else {
      return defaultValue;
    }
  }
}
