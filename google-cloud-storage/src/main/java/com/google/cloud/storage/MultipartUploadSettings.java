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
package com.google.cloud.storage;

public final class MultipartUploadSettings {
  private final HttpStorageOptions options;

  private MultipartUploadSettings(HttpStorageOptions options) {
    this.options = options;
  }

  public HttpStorageOptions getOptions() {
    return options;
  }

  public static MultipartUploadSettings of(HttpStorageOptions options) {
    return new MultipartUploadSettings(options);
  }
}
