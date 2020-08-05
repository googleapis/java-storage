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

import static org.junit.Assert.assertEquals;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.auth.http.HttpTransportFactory;
import java.util.Queue;

class TestHttpTransportFactory implements HttpTransportFactory {

  final Queue<MockData> mockData;

  TestHttpTransportFactory(Queue<MockData> mockData) {
    this.mockData = mockData;
  }

  @Override
  public HttpTransport create() {
    return new HttpTransport() {
      @Override
      protected LowLevelHttpRequest buildRequest(String method, String url) {
        if (mockData.isEmpty()) {
          throw new IllegalStateException("No test data provided");
        }
        MockData data = mockData.poll();
        assertEquals(data.method, method);
        assertEquals(data.url, url);
        TestRequest testRequest = new TestRequest(data.response);
        data.rpcRequestHolder.setRequest(testRequest);
        return testRequest;
      }
    };
  }
}
