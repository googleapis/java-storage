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

import static com.google.cloud.storage.HttpClientContext.firstHeaderValue;

import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.storage.model.StorageObject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import org.checkerframework.checker.nullness.qual.Nullable;

final class JsonResumableSessionQueryTask
    implements Callable<ResumableOperationResult<@Nullable StorageObject>> {

  private final HttpClientContext context;
  private final String uploadId;

  JsonResumableSessionQueryTask(HttpClientContext context, String uploadId) {
    this.context = context;
    this.uploadId = uploadId;
  }

  public ResumableOperationResult<@Nullable StorageObject> call() {
    HttpResponse response = null;
    try {
      HttpRequest req =
          context
              .getRequestFactory()
              .buildPutRequest(new GenericUrl(uploadId), new EmptyContent())
              .setParser(context.getObjectParser());
      req.setThrowExceptionOnExecuteError(false);
      req.getHeaders().setContentRange(HttpContentRange.query().getHeaderValue());

      response = req.execute();

      int code = response.getStatusCode();
      if (JsonResumableSessionFailureScenario.isOk(code)) {
        @Nullable StorageObject storageObject;
        @Nullable BigInteger actualSize;

        Long contentLength = response.getHeaders().getContentLength();
        String contentType = response.getHeaders().getContentType();
        String storedContentLength =
            firstHeaderValue(response.getHeaders(), "x-goog-stored-content-length");
        boolean isJson = contentType != null && contentType.startsWith("application/json");
        if (isJson) {
          storageObject = response.parseAs(StorageObject.class);
          actualSize = storageObject != null ? storageObject.getSize() : null;
        } else if ((contentLength == null || contentLength == 0) && storedContentLength != null) {
          // when a signed url is used, the finalize response is empty
          response.ignore();
          actualSize = new BigInteger(storedContentLength, 10);
          storageObject = null;
        } else {
          response.ignore();
          throw JsonResumableSessionFailureScenario.SCENARIO_0_1.toStorageException(
              uploadId, response, null, () -> null);
        }
        if (actualSize != null) {
          if (storageObject != null) {
            return ResumableOperationResult.complete(storageObject, actualSize.longValue());
          } else {
            return ResumableOperationResult.incremental(actualSize.longValue());
          }
        } else {
          throw JsonResumableSessionFailureScenario.SCENARIO_0.toStorageException(
              uploadId,
              response,
              null,
              () -> storageObject != null ? storageObject.toString() : null);
        }
      } else if (JsonResumableSessionFailureScenario.isContinue(code)) {
        String range1 = response.getHeaders().getRange();
        if (range1 != null) {
          ByteRangeSpec range = ByteRangeSpec.parse(range1);
          long endOffset = range.endOffset();
          return ResumableOperationResult.incremental(endOffset);
        } else {
          throw JsonResumableSessionFailureScenario.QUERY_SCENARIO_1.toStorageException(
              uploadId, response);
        }
      } else {
        HttpResponseException cause = new HttpResponseException(response);
        throw JsonResumableSessionFailureScenario.toStorageException(response, cause, uploadId);
      }
    } catch (StorageException se) {
      throw se;
    } catch (Exception e) {
      throw JsonResumableSessionFailureScenario.SCENARIO_0.toStorageException(
          uploadId, response, e);
    } finally {
      if (response != null) {
        try {
          response.ignore();
        } catch (IOException ignore) {
        }
      }
    }
  }
}
