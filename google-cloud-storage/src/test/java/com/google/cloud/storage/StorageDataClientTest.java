/*
 * Copyright 2024 Google LLC
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

import static org.junit.Assert.assertThrows;

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.storage.v2.BidiReadObjectRequest;
import com.google.storage.v2.ReadRange;
import java.io.IOException;
import org.junit.Test;

public final class StorageDataClientTest {

  @Test
  public void readSession_requestWithRangeRead_noAllowed() throws IOException {
    try (StorageDataClient dc =
        StorageDataClient.create(null, null, null, IOAutoCloseable.noOp())) {
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            BidiReadObjectRequest req =
                BidiReadObjectRequest.newBuilder()
                    .addReadRanges(ReadRange.newBuilder().setReadId(1))
                    .build();
            dc.readSession(req, GrpcCallContext.createDefault());
          });
    }
  }
}
