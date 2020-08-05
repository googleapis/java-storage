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

import com.google.api.client.http.LowLevelHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

class TestResponse extends LowLevelHttpResponse {

  private final int statusCode;
  private final byte[] bytes;

  TestResponse(int statusCode, byte[] bytes) {
    this.statusCode = statusCode;
    this.bytes = bytes;
  }

  @Override
  public InputStream getContent() {
    return bytes == null ? null : new ByteArrayInputStream(bytes);
  }

  @Override
  public String getContentEncoding() {
    return "UTF-8";
  }

  @Override
  public long getContentLength() {
    return bytes == null ? 0 : bytes.length;
  }

  @Override
  public String getContentType() {
    return "application/test";
  }

  @Override
  public String getStatusLine() {
    return "status: " + statusCode;
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getReasonPhrase() {
    return "reason phrase";
  }

  @Override
  public int getHeaderCount() {
    return 0;
  }

  @Override
  public String getHeaderName(int i) {
    return null;
  }

  @Override
  public String getHeaderValue(int i) {
    return null;
  }
}
