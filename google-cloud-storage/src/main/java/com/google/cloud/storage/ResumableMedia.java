/*
 * Copyright 2021 Google LLC
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

import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.cloud.storage.spi.v1.StorageRpc;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

final class ResumableMedia {

  static Supplier<String> startUploadForBlobInfo(
      final StorageOptions storageOptions,
      final BlobInfo blob,
      final Map<StorageRpc.Option, ?> optionsMap,
      ResultRetryAlgorithm<?> algorithm) {
    return () ->
        Retrying.run(
            storageOptions,
            algorithm,
            () ->
                storageOptions
                    .getStorageRpcV1()
                    .open(Conversions.apiary().blobInfo().encode(blob), optionsMap),
            Function.identity());
  }

  static Supplier<String> startUploadForSignedUrl(
      final StorageOptions storageOptions, final URL signedURL, ResultRetryAlgorithm<?> algorithm) {
    if (!isValidSignedURL(signedURL.getQuery())) {
      throw new StorageException(2, "invalid signedURL");
    }
    return () ->
        Retrying.run(
            storageOptions,
            algorithm,
            () -> storageOptions.getStorageRpcV1().open(signedURL.toString()),
            Function.identity());
  }

  private static boolean isValidSignedURL(String signedURLQuery) {
    boolean isValid = true;
    if (signedURLQuery.startsWith("X-Goog-Algorithm=")) {
      if (!signedURLQuery.contains("&X-Goog-Credential=")
          || !signedURLQuery.contains("&X-Goog-Date=")
          || !signedURLQuery.contains("&X-Goog-Expires=")
          || !signedURLQuery.contains("&X-Goog-SignedHeaders=")
          || !signedURLQuery.contains("&X-Goog-Signature=")) {
        isValid = false;
      }
    } else if (signedURLQuery.startsWith("GoogleAccessId=")) {
      if (!signedURLQuery.contains("&Expires=") || !signedURLQuery.contains("&Signature=")) {
        isValid = false;
      }
    } else {
      isValid = false;
    }
    return isValid;
  }
}
