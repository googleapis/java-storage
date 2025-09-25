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

import com.google.api.core.BetaApi;
import com.google.api.core.InternalExtensionOnly;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

@BetaApi
@InternalExtensionOnly
public final class RequestBody {

  private final RewindableContent content;
  private byte[] byteArray;

  private RequestBody(RewindableContent content) {
    this.content = content;
  }

  RewindableContent getContent() {
    return content;
  }

  public static RequestBody empty() {
    RequestBody requestBody = new RequestBody(RewindableContent.empty());
    requestBody.byteArray = new byte[0];
    return requestBody;
  }

  public static RequestBody of(ByteBuffer... buffers) {
    return new RequestBody(RewindableContent.of(buffers));
  }

  public static RequestBody fromByteBuffer(ByteBuffer buffer) {
    ByteBuffer duplicate = buffer.duplicate();
    byte[] arr = new byte[duplicate.remaining()];
    duplicate.get(arr);
    RequestBody requestBody = new RequestBody(RewindableContent.of(buffer));
    requestBody.byteArray = arr;
    return requestBody;
  }

  public static RequestBody of(ByteBuffer[] srcs, int srcsOffset, int srcsLength) {
    return new RequestBody(RewindableContent.of(srcs, srcsOffset, srcsLength));
  }

  public static RequestBody of(Path path) throws IOException {
    return new RequestBody(RewindableContent.of(path));
  }

  public byte[] getPartData() {
    return byteArray;
  }
}
