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

package com.google.cloud.storage;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.cloud.storage.WriteFlushStrategy.Flusher;
import com.google.cloud.storage.WriteFlushStrategy.FlusherFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.Test;

public final class WriteFlushStrategyTest {
  private static final Map<String, List<String>> expectedHeaderNonNullNonEmpty =
      ImmutableMap.of("x-goog-request-params", ImmutableList.of("bucket=bucket-name"));
  private static final Map<String, List<String>> expectedHeaderNonNullEmpty = ImmutableMap.of();
  private static final Map<String, List<String>> expectedHeaderNull = ImmutableMap.of();

  @Test
  public void bucketNameAddedToXGoogRequestParams_nonNull_nonEmpty_fsyncEveryFlush() {
    doTest(WriteFlushStrategy::fsyncEveryFlush, "bucket-name", expectedHeaderNonNullNonEmpty);
  }

  @Test
  public void bucketNameAddedToXGoogRequestParams_nonNull_nonEmpty_fsyncOnClose() {
    doTest(WriteFlushStrategy::fsyncOnClose, "bucket-name", expectedHeaderNonNullNonEmpty);
  }

  @Test
  public void bucketNameNotAddedToXGoogRequestParams_nonNull_empty_fsyncEveryFlush() {
    doTest(WriteFlushStrategy::fsyncEveryFlush, "", expectedHeaderNonNullEmpty);
  }

  @Test
  public void bucketNameNotAddedToXGoogRequestParams_nonNull_empty_fsyncOnClose() {
    doTest(WriteFlushStrategy::fsyncOnClose, "", expectedHeaderNonNullEmpty);
  }

  @Test
  public void bucketNameNotAddedToXGoogRequestParams_null_fsyncEveryFlush() {
    doTest(WriteFlushStrategy::fsyncEveryFlush, null, expectedHeaderNull);
  }

  @Test
  public void bucketNameNotAddedToXGoogRequestParams_null_fsyncOnClose() {
    doTest(WriteFlushStrategy::fsyncOnClose, null, expectedHeaderNull);
  }

  private static void doTest(
      Function<ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse>, FlusherFactory> ff,
      String bucketName,
      Map<String, List<String>> expectedHeader) {
    AtomicLong c = new AtomicLong(0);
    AtomicReference<WriteObjectResponse> ref = new AtomicReference<>();
    AtomicReference<Map<String, List<String>>> actualHeader = new AtomicReference<>();
    ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> write =
        new ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse>() {
          @Override
          public ApiStreamObserver<WriteObjectRequest> clientStreamingCall(
              ApiStreamObserver<WriteObjectResponse> responseObserver, ApiCallContext context) {
            Map<String, List<String>> extraHeaders = context.getExtraHeaders();
            actualHeader.compareAndSet(null, extraHeaders);
            return new ApiStreamObserver<WriteObjectRequest>() {
              @Override
              public void onNext(WriteObjectRequest value) {}

              @Override
              public void onError(Throwable t) {}

              @Override
              public void onCompleted() {
                responseObserver.onCompleted();
              }
            };
          }
        };
    FlusherFactory factory = ff.apply(write);
    Flusher flusher = factory.newFlusher(bucketName, c::addAndGet, ref::set);
    flusher.flush(Collections.emptyList());
    flusher.close(null);
    assertThat(actualHeader.get()).isEqualTo(expectedHeader);
  }
}
