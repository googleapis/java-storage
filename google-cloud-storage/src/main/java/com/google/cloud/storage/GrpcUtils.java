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

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

final class GrpcUtils {

  private GrpcUtils() {}

  static GrpcCallContext contextWithBucketName(String bucketName, GrpcCallContext baseContext) {
    if (bucketName != null && !bucketName.isEmpty()) {
      return baseContext.withExtraHeaders(
          ImmutableMap.of(
              "x-goog-request-params",
              ImmutableList.of(String.format(Locale.US, "bucket=%s", bucketName))));
    }
    return baseContext;
  }

  /**
   * In the event closing the streams results in multiple streams throwing IOExceptions, collect
   * them all as suppressed exceptions on the first occurrence.
   */
  static <C extends Closeable> void closeAll(Collection<C> closeables) throws IOException {
    IOException ioException =
        closeables.stream()
            .map(
                stream -> {
                  try {
                    stream.close();
                    return null;
                  } catch (IOException e) {
                    return e;
                  }
                })
            .filter(Objects::nonNull)
            .reduce(
                null,
                (l, r) -> {
                  if (l != null) {
                    l.addSuppressed(r);
                    return l;
                  } else {
                    return r;
                  }
                },
                (l, r) -> l);

    if (ioException != null) {
      throw ioException;
    }
  }
}
