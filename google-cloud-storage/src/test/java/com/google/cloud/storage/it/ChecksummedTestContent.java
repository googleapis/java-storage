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

import com.google.common.base.MoreObjects;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public final class ChecksummedTestContent {

  private final byte[] bytes;
  private final int crc32c;
  private final String md5Base64;

  private ChecksummedTestContent(byte[] bytes, int crc32c, String md5Base64) {
    this.bytes = bytes;
    this.crc32c = crc32c;
    this.md5Base64 = md5Base64;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public int getCrc32c() {
    return crc32c;
  }

  public ByteString getMd5Bytes() {
    return ByteString.copyFrom(BaseEncoding.base64().decode(md5Base64));
  }

  public String getMd5Base64() {
    return md5Base64;
  }

  public String getCrc32cBase64() {
    return Base64.getEncoder().encodeToString(Ints.toByteArray(crc32c));
  }

  public byte[] concat(char c) {
    return concat((byte) c);
  }

  public byte[] concat(byte b) {
    int lenOrig = bytes.length;
    int lenNew = lenOrig + 1;
    byte[] newBytes = Arrays.copyOf(bytes, lenNew);
    newBytes[lenOrig] = b;
    return newBytes;
  }

  public ByteArrayInputStream bytesAsInputStream() {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("byteCount", bytes.length)
        .add("crc32c", crc32c)
        .add("md5Base64", md5Base64)
        .toString();
  }

  public static ChecksummedTestContent of(String content) {
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    return of(bytes);
  }

  public static ChecksummedTestContent of(byte[] bytes) {
    int crc32c = Hashing.crc32c().hashBytes(bytes).asInt();
    String md5Base64 = Base64.getEncoder().encodeToString(Hashing.md5().hashBytes(bytes).asBytes());
    return new ChecksummedTestContent(bytes, crc32c, md5Base64);
  }
}
