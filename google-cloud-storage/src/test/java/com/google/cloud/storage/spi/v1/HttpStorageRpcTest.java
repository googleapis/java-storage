/*
 * Copyright 2020 Google LLC
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

package com.google.cloud.storage.spi.v1;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.core.ApiClock;
import com.google.api.services.storage.model.Bucket;
import com.google.auth.http.HttpTransportFactory;
import com.google.cloud.ServiceOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.spi.StorageRpcFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.zip.GZIPInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HttpStorageRpcTest {

  // object under test, created before each test case
  private HttpStorageRpc rpc;

  // Objects for a test case: one object per one RPC mock.
  // A test creates as many MockData objects as many RPC call it issues.
  private static final Queue<MockData> TEST_MOCK_DATA = new LinkedList<>();

  private static final String BASE_URL = "https://storage.googleapis.com/storage/v1/b";
  private static final String URL_PROJECT = "project=projectId&projection=full";

  private static final Answer UNEXPECTED_CALL_ANSWER =
      new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) {
          throw new IllegalArgumentException(
              "Unexpected call of "
                  + invocation.getMethod()
                  + " with "
                  + Arrays.toString(invocation.getArguments()));
        };
      };

  private static final ApiClock TIME_SOURCE =
      new ApiClock() {
        @Override
        public long nanoTime() {
          return 42_000_000_000L;
        }

        @Override
        public long millisTime() {
          return 42_000L;
        }
      };

  private static final StorageRpcFactory RPC_FACTORY_MOCK =
      mock(StorageRpcFactory.class, UNEXPECTED_CALL_ANSWER);

  private static class TestRequest extends LowLevelHttpRequest {
    private final LowLevelHttpResponse response;
    private final Map<String, String> addedHeaders = new HashMap<>();

    TestRequest(LowLevelHttpResponse response) {
      this.response = response;
    }

    @Override
    public void addHeader(String name, String value) throws IOException {
      if (addedHeaders != null) {
        addedHeaders.put(name, value);
      }
    }

    @Override
    public LowLevelHttpResponse execute() throws IOException {
      return response;
    }
  };

  private static class TestResponse extends LowLevelHttpResponse {

    private final int statusCode;
    private final byte[] bytes;

    TestResponse(int statusCode, byte[] bytes) {
      this.statusCode = statusCode;
      this.bytes = bytes;
    }

    @Override
    public InputStream getContent() throws IOException {
      return bytes == null ? null : new ByteArrayInputStream(bytes);
    }

    @Override
    public String getContentEncoding() throws IOException {
      return "UTF-8";
    }

    @Override
    public long getContentLength() throws IOException {
      return bytes == null ? 0 : bytes.length;
    }

    @Override
    public String getContentType() throws IOException {
      return "application/test";
    }

    @Override
    public String getStatusLine() throws IOException {
      return "status: " + statusCode;
    }

    @Override
    public int getStatusCode() throws IOException {
      return statusCode;
    }

    @Override
    public String getReasonPhrase() throws IOException {
      return "reason phrase";
    }

    @Override
    public int getHeaderCount() throws IOException {
      return 0;
    }

    @Override
    public String getHeaderName(int i) throws IOException {
      return null;
    }

    @Override
    public String getHeaderValue(int i) throws IOException {
      return null;
    }
  }

  private static final HttpTransportFactory TRANSPORT_FACTORY_MOCK =
      new HttpTransportFactory() {
        @Override
        public HttpTransport create() {
          return new HttpTransport() {
            @Override
            protected LowLevelHttpRequest buildRequest(String method, String url)
                throws IOException {
              if (TEST_MOCK_DATA.isEmpty()) {
                throw new IllegalStateException("No test data provided");
              }
              MockData data = TEST_MOCK_DATA.poll();
              assertEquals(data.method, method);
              assertEquals(data.url, url);
              TestRequest testRequest = new TestRequest(data.response);
              data.rpcRequestHolder.setRequest(testRequest);
              return testRequest;
            }
          };
        }
      };

  private static final HttpTransportOptions TRANSPORT_OPTIONS =
      HttpTransportOptions.newBuilder().setHttpTransportFactory(TRANSPORT_FACTORY_MOCK).build();

  private static final StorageOptions STORAGE_OPTIONS =
      StorageOptions.newBuilder()
          .setProjectId("projectId")
          .setClock(TIME_SOURCE)
          .setServiceRpcFactory(RPC_FACTORY_MOCK)
          .setRetrySettings(ServiceOptions.getNoRetrySettings())
          .setTransportOptions(TRANSPORT_OPTIONS)
          .build();

  static class RpcRequestHolder {
    TestRequest request = null;

    void setRequest(TestRequest request) {
      this.request = request;
    }

    TestRequest getRequest() {
      return request;
    }
  }

  static class MockData {
    final String method;
    final String url;
    final LowLevelHttpResponse response;
    final RpcRequestHolder rpcRequestHolder;

    MockData(
        String method, String url, LowLevelHttpResponse response, RpcRequestHolder requestHeaders) {
      this.method = method;
      this.url = url;
      this.response = response;
      this.rpcRequestHolder = requestHeaders;
    }
  }

  /**
   * Adds mock parameters for the RPC call.
   *
   * @param method expected HTTP method
   * @param url expected URL
   * @param response HTTP response object to be returned
   * @return a holder object which will contain the Http Request made by RPC
   */
  private static RpcRequestHolder mockResponse(
      String method, String url, LowLevelHttpResponse response) {
    RpcRequestHolder holder = new RpcRequestHolder();
    TEST_MOCK_DATA.add(new MockData(method, url, response, holder));
    return holder;
  }

  private static String getUnzippedContent(LowLevelHttpRequest request) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      request.getStreamingContent().writeTo(outputStream);
      GZIPInputStream is =
          new GZIPInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
      byte[] buffer = new byte[1000000];
      int size = is.read(buffer);
      return new String(buffer, 0, size, UTF_8);
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  @Before
  public void setUp() throws Exception {
    rpc = new HttpStorageRpc(STORAGE_OPTIONS);
    TEST_MOCK_DATA.clear();
  }

  @After
  public void tearDown() throws Exception {
    assertTrue("TEST_MOCK_DATA must be clear at the end", TEST_MOCK_DATA.isEmpty());
  }

  @Test
  public void testCreateBucket() throws Exception {
    byte[] content = "{\"name\":\"yyy\"}".getBytes(UTF_8);
    String url = BASE_URL + '?' + URL_PROJECT;
    RpcRequestHolder holder = mockResponse("POST", url, new TestResponse(200, content));

    Bucket created = rpc.create(new Bucket().setName("xxx"), new HashMap());
    assertEquals("yyy", created.getName());
    assertNull(created.getId());

    assertEquals("{\"name\":\"xxx\"}", getUnzippedContent(holder.getRequest()));
  }

  @Test
  public void testCreateBucketWithOptions() throws Exception {
    byte[] content = "{}".getBytes(UTF_8);
    Map map = new HashMap();
    map.put(StorageRpc.Option.PREDEFINED_ACL, "value1");
    map.put(StorageRpc.Option.PREDEFINED_DEFAULT_OBJECT_ACL, "value2");
    mockResponse(
        "POST",
        BASE_URL + "?predefinedAcl=value1&predefinedDefaultObjectAcl=value2&" + URL_PROJECT,
        new TestResponse(200, content));
    rpc.create(new Bucket().setName("xxx"), map);
  }

  @Test
  public void testCreateBucketWithError() throws Exception {
    String url = BASE_URL + '?' + URL_PROJECT;
    mockResponse("POST", url, new TestResponse(400, null));
    try {
      rpc.create(new Bucket().setName("xxx"), new HashMap());
      fail();
    } catch (StorageException e) {
      assertTrue(e.getMessage().startsWith("400 reason phrase"));
      assertTrue(e.getMessage().contains("POST " + url));
    }
  }
}
