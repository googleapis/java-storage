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

final class JsonResumableWriteCtx {

  private Crc32cValue.Crc32cLengthKnown cumulativeCrc32c;

  JsonResumableWriteCtx() {
    this.cumulativeCrc32c = Crc32cValue.zero();
  }

  /** Calculates the hypothetical total checksum using the provided chunk CRC. */
  Crc32cValue.Crc32cLengthKnown peekCumulative(Crc32cValue.Crc32cLengthKnown chunkCrc) {
    if (chunkCrc != null) {
      return cumulativeCrc32c.concat(chunkCrc);
    }
    return cumulativeCrc32c;
  }

  /** Updates the persistent state using the provided chunk CRC. */
  void commit(Crc32cValue.Crc32cLengthKnown chunkCrc) {
    if (chunkCrc != null) {
      this.cumulativeCrc32c = this.cumulativeCrc32c.concat(chunkCrc);
    }
  }
}
