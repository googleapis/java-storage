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

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.Retrying.RetrierWithAlg;
import com.google.cloud.storage.spi.v1.HttpRpcContext;
import com.google.cloud.storage.spi.v1.HttpStorageRpc;
import io.opencensus.trace.EndSpanOptions;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.checkerframework.checker.nullness.qual.Nullable;

final class JsonResumableSession {

  static final String SPAN_NAME_WRITE =
      String.format(Locale.US, "Sent.%s.write", HttpStorageRpc.class.getName());
  static final EndSpanOptions END_SPAN_OPTIONS =
      EndSpanOptions.builder().setSampleToLocalSpanStore(true).build();

  private final HttpClientContext context;
  private final RetrierWithAlg retrier;
  private final JsonResumableWrite resumableWrite;

  JsonResumableSession(
      HttpClientContext context, RetrierWithAlg retrier, JsonResumableWrite resumableWrite) {
    this.context = context;
    this.retrier = retrier;
    this.resumableWrite = resumableWrite;
  }

  /**
   * Not automatically retried. Usually called from within another retrying context. We don't yet
   * have the concept of nested retry handling.
   */
  ResumableOperationResult<@Nullable StorageObject> query() {
    return new JsonResumableSessionQueryTask(context, resumableWrite).call();
  }

  ResumableOperationResult<@Nullable StorageObject> put(
      RewindableContent content, HttpContentRange contentRange) {
    JsonResumableSessionPutTask task =
        new JsonResumableSessionPutTask(context, resumableWrite, content, contentRange);
    HttpRpcContext httpRpcContext = HttpRpcContext.getInstance();
    try {
      httpRpcContext.newInvocationId();
      AtomicBoolean dirty = new AtomicBoolean(false);
      return retrier.run(
          () -> {
            if (dirty.getAndSet(true)) {
              ResumableOperationResult<@Nullable StorageObject> query = query();
              long persistedSize = query.getPersistedSize();
              if (contentRange.endOffsetEquals(persistedSize) || query.getObject() != null) {
                return query;
              } else {
                task.rewindTo(persistedSize);
              }
            }
            return task.call();
          },
          Decoder.identity());
    } finally {
      httpRpcContext.clearInvocationId();
    }
  }
}
