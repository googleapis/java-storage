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

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.core.ApiClock;
import com.google.api.services.storage.model.Bucket;
import com.google.cloud.ServiceOptions;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
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
  // A test creates as many MockData objects as it issues RPC calls.
  private Queue<MockData> testMockData;

  private static final String BASE_URL = "https://storage.googleapis.com/storage/v1/b";
  private static final String URL_PROJECT = "project=projectId&projection=full";

  private static final Answer<Object> UNEXPECTED_CALL_ANSWER =
      new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) {
          throw new IllegalArgumentException(
              "Unexpected call of "
                  + invocation.getMethod()
                  + " with "
                  + Arrays.toString(invocation.getArguments()));
        }
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

  /**
   * Adds mock parameters for the RPC call.
   *
   * @param method expected HTTP method
   * @param url expected URL
   * @param response HTTP response object to be returned
   * @return a holder object which will contain the Http Request made by RPC
   */
  private RpcRequestHolder mockResponse(String method, String url, LowLevelHttpResponse response) {
    RpcRequestHolder holder = new RpcRequestHolder();
    testMockData.add(new MockData(method, url, response, holder));
    return holder;
  }

  private static String getUnzippedContent(LowLevelHttpRequest request) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    request.getStreamingContent().writeTo(outputStream);
    GZIPInputStream gzipInputStream =
        new GZIPInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
    return new String(ByteStreams.toByteArray(gzipInputStream), UTF_8);
  }

  @Before
  public void setUp() {
    testMockData = new ArrayDeque<>();
    TestHttpTransportFactory factoryMock = new TestHttpTransportFactory(testMockData);
    HttpTransportOptions transportOptions =
        HttpTransportOptions.newBuilder().setHttpTransportFactory(factoryMock).build();
    StorageOptions storageOptions =
        StorageOptions.newBuilder()
            .setProjectId("projectId")
            .setClock(TIME_SOURCE)
            .setServiceRpcFactory(RPC_FACTORY_MOCK)
            .setRetrySettings(ServiceOptions.getNoRetrySettings())
            .setTransportOptions(transportOptions)
            .build();

    rpc = new HttpStorageRpc(storageOptions);
  }

  @After
  public void tearDown() {
    assertTrue("testMockData must be clear at the end", testMockData.isEmpty());
  }

  @Test
  public void testCreateBucket() throws IOException {
    byte[] content = "{\"name\":\"yyy\"}".getBytes(UTF_8);
    String url = BASE_URL + '?' + URL_PROJECT;
    RpcRequestHolder holder = mockResponse("POST", url, new TestResponse(200, content));

    Bucket created = rpc.create(new Bucket().setName("xxx"), new HashMap());
    assertEquals("yyy", created.getName());
    assertNull(created.getId());

    assertEquals("{\"name\":\"xxx\"}", getUnzippedContent(holder.getRequest()));
  }

  @Test
  public void testCreateBucketWithOptions() {
    byte[] content = "{}".getBytes(UTF_8);
    Map<StorageRpc.Option, String> map = new HashMap<>();
    map.put(StorageRpc.Option.PREDEFINED_ACL, "value1");
    map.put(StorageRpc.Option.PREDEFINED_DEFAULT_OBJECT_ACL, "value2");
    mockResponse(
        "POST",
        BASE_URL + "?predefinedAcl=value1&predefinedDefaultObjectAcl=value2&" + URL_PROJECT,
        new TestResponse(200, content));
    rpc.create(new Bucket().setName("xxx"), map);
  }

  @Test
  public void testCreateBucketWithError() {
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
