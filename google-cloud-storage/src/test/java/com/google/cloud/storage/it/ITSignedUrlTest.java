/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage.it;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.google.auth.ServiceAccountSigner;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.PostPolicyV4;
import com.google.cloud.storage.PostPolicyV4.PostFieldsV4;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITSignedUrlTest {
  @ClassRule public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  private static Storage storage;
  private static final String BUCKET = RemoteStorageHelper.generateBucketName();
  private static final byte[] BLOB_BYTE_CONTENT = {0xD, 0xE, 0xA, 0xD};
  private static final String BLOB_STRING_CONTENT = "Hello Google Cloud Storage!";

  @BeforeClass
  public static void beforeClass() throws IOException {
    storage = storageFixture.getInstance();
    storage.create(BucketInfo.newBuilder(BUCKET).build());
  }

  @Test
  public void testGetSignedUrl() throws IOException {
    if (storage.getOptions().getCredentials() != null) {
      assumeTrue(storage.getOptions().getCredentials() instanceof ServiceAccountSigner);
    }
    String blobName = "test-get-signed-url-blob/with/slashes/and?special=!#$&'()*+,:;=?@[]";
    BlobInfo blob = BlobInfo.newBuilder(BUCKET, blobName).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    for (Storage.SignUrlOption urlStyle :
        Arrays.asList(
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withVirtualHostedStyle())) {
      URL url = storage.signUrl(blob, 1, TimeUnit.HOURS, urlStyle);
      URLConnection connection = url.openConnection();
      byte[] readBytes = new byte[BLOB_BYTE_CONTENT.length];
      try (InputStream responseStream = connection.getInputStream()) {
        assertEquals(BLOB_BYTE_CONTENT.length, responseStream.read(readBytes));
        assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
      }
    }
  }

  @Test
  public void testGetV2SignedUrlWithAddlQueryParam() throws IOException {
    if (storage.getOptions().getCredentials() != null) {
      assumeTrue(storage.getOptions().getCredentials() instanceof ServiceAccountSigner);
    }
    String blobName = "test-get-v2-with-generation-param";
    BlobInfo blob = BlobInfo.newBuilder(BUCKET, blobName).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    for (Storage.SignUrlOption urlStyle :
        Arrays.asList(
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withVirtualHostedStyle())) {
      String generationStr = remoteBlob.getGeneration().toString();
      URL url =
          storage.signUrl(
              blob,
              1,
              TimeUnit.HOURS,
              urlStyle,
              Storage.SignUrlOption.withV2Signature(),
              Storage.SignUrlOption.withQueryParams(
                  ImmutableMap.<String, String>of("generation", generationStr)));
      // Finally, verify that the URL works and we can get the object as expected:
      URLConnection connection = url.openConnection();
      byte[] readBytes = new byte[BLOB_BYTE_CONTENT.length];
      try (InputStream responseStream = connection.getInputStream()) {
        assertEquals(BLOB_BYTE_CONTENT.length, responseStream.read(readBytes));
        assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
      }
    }
  }

  @Test
  public void testPostSignedUrl() throws IOException {
    if (storage.getOptions().getCredentials() != null) {
      assumeTrue(storage.getOptions().getCredentials() instanceof ServiceAccountSigner);
    }
    String blobName = "test-post-signed-url-blob";
    BlobInfo blob = BlobInfo.newBuilder(BUCKET, blobName).build();
    assertNotNull(storage.create(blob));
    for (Storage.SignUrlOption urlStyle :
        Arrays.asList(
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withVirtualHostedStyle())) {

      URL url =
          storage.signUrl(
              blob, 1, TimeUnit.HOURS, Storage.SignUrlOption.httpMethod(HttpMethod.POST), urlStyle);
      URLConnection connection = url.openConnection();
      connection.setDoOutput(true);
      connection.connect();
      Blob remoteBlob = storage.get(BUCKET, blobName);
      assertNotNull(remoteBlob);
      assertEquals(blob.getBucket(), remoteBlob.getBucket());
      assertEquals(blob.getName(), remoteBlob.getName());
    }
  }

  @Test
  public void testV4SignedUrl() throws IOException {
    if (storage.getOptions().getCredentials() != null) {
      assumeTrue(storage.getOptions().getCredentials() instanceof ServiceAccountSigner);
    }

    String blobName = "test-get-signed-url-blob/with/slashes/and?special=!#$&'()*+,:;=?@[]";
    BlobInfo blob = BlobInfo.newBuilder(BUCKET, blobName).build();
    Blob remoteBlob = storage.create(blob, BLOB_BYTE_CONTENT);
    assertNotNull(remoteBlob);
    for (Storage.SignUrlOption urlStyle :
        Arrays.asList(
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withVirtualHostedStyle())) {

      URL url =
          storage.signUrl(
              blob, 1, TimeUnit.HOURS, Storage.SignUrlOption.withV4Signature(), urlStyle);
      URLConnection connection = url.openConnection();
      byte[] readBytes = new byte[BLOB_BYTE_CONTENT.length];
      try (InputStream responseStream = connection.getInputStream()) {
        assertEquals(BLOB_BYTE_CONTENT.length, responseStream.read(readBytes));
        assertArrayEquals(BLOB_BYTE_CONTENT, readBytes);
      }
    }
  }

  @Test
  public void testSignedPostPolicyV4() throws Exception {
    PostFieldsV4 fields = PostFieldsV4.newBuilder().setAcl("public-read").build();

    PostPolicyV4 policy =
        storage.generateSignedPostPolicyV4(
            BlobInfo.newBuilder(BUCKET, "my-object").build(), 7, TimeUnit.DAYS, fields);

    HttpClient client = HttpClientBuilder.create().build();
    HttpPost request = new HttpPost(policy.getUrl());
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    for (Map.Entry<String, String> entry : policy.getFields().entrySet()) {
      builder.addTextBody(entry.getKey(), entry.getValue());
    }
    File file = File.createTempFile("temp", "file");
    Files.write(file.toPath(), "hello world".getBytes());
    builder.addBinaryBody(
        "file", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM, file.getName());
    request.setEntity(builder.build());
    client.execute(request);

    assertEquals("hello world", new String(storage.get(BUCKET, "my-object").getContent()));
  }

  @Test
  public void testUploadUsingSignedURL() throws Exception {
    String blobName = "test-signed-url-upload";
    BlobInfo blob = BlobInfo.newBuilder(BUCKET, blobName).build();
    assertNotNull(storage.create(blob));
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put("x-goog-resumable", "start");
    for (Storage.SignUrlOption urlStyle :
        Arrays.asList(
            Storage.SignUrlOption.withPathStyle(),
            Storage.SignUrlOption.withVirtualHostedStyle())) {
      URL signUrl =
          storage.signUrl(
              blob,
              1,
              TimeUnit.HOURS,
              Storage.SignUrlOption.httpMethod(HttpMethod.POST),
              Storage.SignUrlOption.withExtHeaders(extensionHeaders),
              urlStyle);
      byte[] bytesArrayToUpload = BLOB_STRING_CONTENT.getBytes();
      Storage unauthenticatedStorage = StorageOptions.getUnauthenticatedInstance().getService();
      try (WriteChannel writer = unauthenticatedStorage.writer(signUrl)) {
        writer.write(ByteBuffer.wrap(bytesArrayToUpload, 0, bytesArrayToUpload.length));
      }

      int lengthOfDownLoadBytes = -1;
      BlobId blobId = BlobId.of(BUCKET, blobName);
      Blob blobToRead = storage.get(blobId);
      try (ReadChannel reader = blobToRead.reader()) {
        ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
        lengthOfDownLoadBytes = reader.read(bytes);
      }

      assertEquals(bytesArrayToUpload.length, lengthOfDownLoadBytes);
      assertTrue(storage.delete(BUCKET, blobName));
    }
  }
}
