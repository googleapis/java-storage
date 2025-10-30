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

package com.google.cloud.storage.multipartupload.model;

import com.google.api.client.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class UploadResponseParser {

  private UploadResponseParser() {}

  public static UploadPartResponse parse(HttpResponse response) {
    String eTag = response.getHeaders().getETag();
    Map<String, String> hashes = extractHashesFromHeader(response);
    return UploadPartResponse.builder()
        .eTag(eTag)
        .crc32c(hashes.get("crc32c"))
        .md5(hashes.get("md5"))
        .build();
  }

  static Map<String, String> extractHashesFromHeader(HttpResponse response) {
    return Optional.ofNullable(response.getHeaders().getFirstHeaderStringValue("x-goog-hash"))
        .map(
            h ->
                Arrays.stream(h.split(","))
                    .map(s -> s.trim().split("=", 2))
                    .filter(a -> a.length == 2)
                    .filter(a -> "crc32c".equalsIgnoreCase(a[0]) || "md5".equalsIgnoreCase(a[0]))
                    .collect(Collectors.toMap(a -> a[0].toLowerCase(), a -> a[1], (v1, v2) -> v1)))
        .orElse(Collections.emptyMap());
  }
}
