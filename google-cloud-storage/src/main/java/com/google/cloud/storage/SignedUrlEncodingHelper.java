/*
 * Copyright 2019 Google LLC
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

import com.google.api.client.util.escape.PercentEscaper;

/** Helper for encoding URI segments appropriately when creating a Signed URL. */
class SignedUrlEncodingHelper {

  private static final PercentEscaper PATH_ENCODER =
      new PercentEscaper(PercentEscaper.SAFEPATHCHARS_URLENCODER, false);

  private static final PercentEscaper QUERY_ENCODER =
      new PercentEscaper(PercentEscaper.SAFEQUERYSTRINGCHARS_URLENCODER, false);

  static String encodeForPath(String segment, boolean encodeForwardSlash) {
    String encodedSegment = PATH_ENCODER.escape(segment);
    if (!encodeForwardSlash) {
      encodedSegment = encodedSegment.replace("%2F", "/");
    }
    return encodedSegment;
  }
  
  static String encodeForQueryString(String segment, boolean encodeForwardSlash) {
    String encodedSegment = QUERY_ENCODER.escape(segment);
    if (!encodeForwardSlash) {
      encodedSegment = encodedSegment.replace("%2F", "/");
    }
    return encodedSegment;
  }
}
