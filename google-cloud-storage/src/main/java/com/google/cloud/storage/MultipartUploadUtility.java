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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MultipartUploadUtility {
  public static String readStream(InputStream inputStream) throws IOException {
    if (inputStream == null) return "";
    StringBuilder response = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
    }
    return response.toString();
  }

  public static byte[] readPart(File file, long position, int size) throws IOException {
    byte[] buffer = new byte[size];
    try (FileInputStream fis = new FileInputStream(file)) {
      fis.getChannel().position(position);
      int bytesRead = fis.read(buffer, 0, size);
      if (bytesRead != size) {
        byte[] smallerBuffer = new byte[bytesRead];
        System.arraycopy(buffer, 0, smallerBuffer, 0, bytesRead);
        return smallerBuffer;
      }
    }
    return buffer;
  }

  public static String getRfc1123Date() {
    return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT")).format(ZonedDateTime.now());
  }
}
