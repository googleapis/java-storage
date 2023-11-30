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

import static com.google.cloud.storage.TestUtils.assertAll;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.UnifiedOpts.Opt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import org.junit.Test;

public final class UtilsTest {
  private static final Opts<Opt> autoGzipDecompress_undefined = Opts.empty();
  private static final Opts<Opt> autoGzipDecompress_no =
      Opts.from(UnifiedOpts.returnRawInputStream(true));
  private static final Opts<Opt> autoGzipDecompress_yes =
      Opts.from(UnifiedOpts.returnRawInputStream(false));

  @Test
  public void isAutoGzipDecompression() throws Exception {
    assertAll(
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_undefined, /*defaultWhenUndefined=*/ false))
                .isFalse(),
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_undefined, /*defaultWhenUndefined=*/ true))
                .isTrue(),
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_no, /*defaultWhenUndefined=*/ false))
                .isFalse(),
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_no, /*defaultWhenUndefined=*/ true))
                .isFalse(),
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_yes, /*defaultWhenUndefined=*/ false))
                .isTrue(),
        () ->
            assertThat(
                    Utils.isAutoGzipDecompression(
                        autoGzipDecompress_yes, /*defaultWhenUndefined=*/ true))
                .isTrue());
  }
}
