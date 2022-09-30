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

import org.junit.Test;

/**
 * Somehow by having this class defined, it makes it so the native-image builds are able to find
 * tests. If it isn't able to find tests, it will error and fail the build.
 */
public final class ShimClientTest {

  @Test
  public void fakeOutNativeMavenPlugin_test() {
    assertThat(true).isTrue();
  }
}
