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

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.storage.StorageException.IOExceptionCallable;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
enum JsonResumableSessionFailureScenario {
  // TODO: send more bytes than are in the Content-Range header
  SCENARIO_0(BaseServiceException.UNKNOWN_CODE, null, "Unknown Error"),
  SCENARIO_0_1(BaseServiceException.UNKNOWN_CODE, null, "Response not application/json."),
  SCENARIO_1(
      BaseServiceException.UNKNOWN_CODE,
      "invalid",
      "Attempt to append to already finalized resumable session."),
  SCENARIO_2(
      BaseServiceException.UNKNOWN_CODE,
      "invalid",
      "Attempt to finalize resumable session with fewer bytes than the backend has received."),
  SCENARIO_3(
      BaseServiceException.UNKNOWN_CODE,
      "dataLoss",
      "Attempt to finalize resumable session with more bytes than the backend has received."),
  SCENARIO_4(200, "ok", "Attempt to finalize an already finalized session with same object size"),
  SCENARIO_4_1(
      BaseServiceException.UNKNOWN_CODE,
      "dataLoss",
      "Finalized resumable session, but object size less than expected."),
  SCENARIO_4_2(
      BaseServiceException.UNKNOWN_CODE,
      "dataLoss",
      "Finalized resumable session, but object size greater than expected."),
  SCENARIO_5(
      BaseServiceException.UNKNOWN_CODE,
      "dataLoss",
      "Client side data loss detected. Attempt to append to a resumable session with an offset higher than the backend has"),
  SCENARIO_7(
      BaseServiceException.UNKNOWN_CODE,
      "dataLoss",
      "Client side data loss detected. Bytes acked is more than client sent."),
  SCENARIO_9(503, "backendNotConnected", "Ack less than bytes sent"),
  QUERY_SCENARIO_1(503, "", "Missing Range header in response");

  private static final String PREFIX_I = "\t|< ";
  private static final String PREFIX_O = "\t|> ";
  private static final String PREFIX_X = "\t|  ";

  private static final Predicate<String> includedHeaders =
      matches("Content-Length")
          .or(matches("Content-Encoding"))
          .or(matches("Content-Range"))
          .or(matches("Content-Type"))
          .or(matches("Range"))
          .or(startsWith("X-Goog-Stored-"))
          .or(matches("X-GUploader-UploadID"));

  private static final Predicate<Map.Entry<String, ?>> includeHeader =
      e -> includedHeaders.test(e.getKey());

  private final int code;
  @Nullable private final String reason;
  private final String message;

  JsonResumableSessionFailureScenario(int code, @Nullable String reason, String message) {
    this.code = code;
    this.reason = reason;
    this.message = message;
  }

  StorageException toStorageException(String uploadId, HttpResponse resp) {
    return toStorageException(
        uploadId, resp, null, () -> CharStreams.toString(new InputStreamReader(resp.getContent())));
  }

  StorageException toStorageException(
      String uploadId, @Nullable HttpResponse resp, @Nullable Throwable cause) {
    if (resp != null) {
      // an exception caused this, do not try to read the content from the response.
      return toStorageException(uploadId, resp, cause, () -> null);
    } else {
      return new StorageException(code, message, reason, cause);
    }
  }

  StorageException toStorageException(
      String uploadId,
      HttpResponse resp,
      @Nullable Throwable cause,
      IOExceptionCallable<@Nullable String> contentCallable) {
    return toStorageException(code, message, reason, uploadId, resp, cause, contentCallable);
  }

  static StorageException toStorageException(
      HttpResponse response, HttpResponseException cause, String uploadId) {
    String statusMessage = cause.getStatusMessage();
    StorageException se =
        JsonResumableSessionFailureScenario.toStorageException(
            cause.getStatusCode(),
            String.format(
                "%d %s", cause.getStatusCode(), statusMessage == null ? "" : statusMessage),
            "",
            uploadId,
            response,
            cause,
            () -> null);
    return se;
  }

  static StorageException toStorageException(
      int overrideCode,
      String message,
      @Nullable String reason,
      String uploadId,
      HttpResponse resp,
      @Nullable Throwable cause,
      IOExceptionCallable<@Nullable String> contentCallable) {
    Throwable suppress = null;
    StringBuilder sb = new StringBuilder();
    sb.append(message);
    // add request context
    sb.append("\n").append(PREFIX_O).append("PUT ").append(uploadId);
    recordHeaderTo(resp.getRequest().getHeaders(), PREFIX_O, sb);

    sb.append("\n").append(PREFIX_X);
    // add response context
    {
      int code = resp.getStatusCode();
      sb.append("\n").append(PREFIX_I).append("HTTP/1.1 ").append(code);
      if (resp.getStatusMessage() != null) {
        sb.append(" ").append(resp.getStatusMessage());
      }

      recordHeaderTo(resp.getHeaders(), PREFIX_I, sb);
      // try to include any body that we can handle
      if (isOk(code) || code == 503 || code == 400) {
        try {
          String content = contentCallable.call();
          if (content != null) {
            sb.append("\n").append(PREFIX_I);
            if (content.contains("\n") || content.contains("\r\n")) {
              sb.append("\n").append(PREFIX_I).append(content.replaceAll("\r?\n", "\n" + PREFIX_I));
            } else {
              sb.append("\n").append(PREFIX_I).append(content);
            }
          }
        } catch (IOException e) {
          // com.google.api.client.http.HttpResponseException.Builder.Builder
          // prints an exception which might occur while attempting to resolve the content
          // this can lose the context about the request it was for, instead we register it
          // as a suppressed exception
          suppress = new StorageException(0, "Error reading response content for diagnostics.", e);
        }
      }

      sb.append("\n").append(PREFIX_X);
    }
    StorageException storageException =
        new StorageException(overrideCode, sb.toString(), reason, cause);
    if (suppress != null) {
      storageException.addSuppressed(suppress);
    }
    return storageException;
  }

  static boolean isOk(int code) {
    return code == 200 || code == 201;
  }

  static boolean isContinue(int code) {
    return code == 308;
  }

  // The header names from HttpHeaders are lower cased, define some utility methods to create
  // predicates where we can specify values ignoring case
  private static Predicate<String> matches(String expected) {
    String lower = expected.toLowerCase(Locale.US);
    return lower::equals;
  }

  private static Predicate<String> startsWith(String prefix) {
    String lower = prefix.toLowerCase(Locale.US);
    return s -> s.startsWith(lower);
  }

  private static void recordHeaderTo(HttpHeaders h, String prefix, StringBuilder sb) {
    h.entrySet().stream()
        .filter(includeHeader)
        .forEach(
            e -> {
              String key = e.getKey();
              String value = headerValueToString(e.getValue());
              sb.append("\n").append(prefix).append(key).append(": ").append(value);
            });
  }

  private static String headerValueToString(Object o) {
    if (o instanceof List) {
      List<?> l = (List<?>) o;
      if (l.size() == 1) {
        return l.get(0).toString();
      }
    }

    return o.toString();
  }
}
