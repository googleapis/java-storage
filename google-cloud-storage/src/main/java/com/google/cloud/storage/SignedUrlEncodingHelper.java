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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.google.api.client.util.escape.PercentEscaper;

/** Helper for encoding URI segments when creating a Signed URL. */
class SignedUrlEncodingHelper {

  /**
   * @deprecated use the escaper designed for the specific part of the URL you're
   *     escaping. 
   */
  @Deprecated
  static String Rfc3986UriEncode(String segment, boolean encodeForwardSlash) {
    try {
      String encodedSegment = URLEncoder.encode(segment, "UTF-8");

      // URLEncoder.encode() does mostly what we want, with the exception of a few characters that
      // we fix in a second phase:
      encodedSegment =
          encodedSegment
              .replace("*", "%2A") // Asterisks should be encoded.
              .replace("+", "%20") // Spaces should be encoded as %20 instead of a plus sign.
              .replace("%7E", "~"); // Tildes should not be encoded.
      // Forward slashes should NOT be encoded in the segment of the URI that represents the
      // object's name, but should be encoded for all other segments.
      if (!encodeForwardSlash) {
        encodedSegment = encodedSegment.replace("%2F", "/");
      }
      return encodedSegment;
    } catch (UnsupportedEncodingException exception) {
      throw new RuntimeException(exception);
    }
  }  
  
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
