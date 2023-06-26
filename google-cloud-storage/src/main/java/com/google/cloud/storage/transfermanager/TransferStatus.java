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

package com.google.cloud.storage.transfermanager;

import com.google.api.core.BetaApi;
import java.util.Comparator;

/** The status of a Upload/Download operation performed by Transfer Manager. */
@BetaApi
public enum TransferStatus {
  /** The transfer failed before bytes could be moved. */
  @BetaApi
  FAILED_TO_START,
  /** The transfer failed after bytes could be moved. */
  @BetaApi
  FAILED_TO_FINISH,
  /**
   * The transfer failed because the object/file already exists and skipIfExists was set to true.
   *
   * @see ParallelUploadConfig
   */
  @BetaApi
  SKIPPED,
  /** The transfer was successful. */
  @BetaApi
  SUCCESS;

  /** A null value is considered to be greater than all values */
  static final Comparator<TransferStatus> COMPARE_NULL_SAFE =
      Comparator.nullsLast(Comparator.comparingInt(TransferStatus::ordinal));
}
