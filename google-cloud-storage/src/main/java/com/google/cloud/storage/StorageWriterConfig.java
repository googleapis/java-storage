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

import com.google.api.core.InternalApi;
import com.google.cloud.storage.Conversions.Decoder;
import com.google.cloud.storage.UnifiedOpts.ObjectTargetOpt;
import com.google.cloud.storage.UnifiedOpts.Opts;
import com.google.storage.v2.WriteObjectResponse;
import java.io.IOException;
import java.time.Clock;

public abstract class StorageWriterConfig {

  @InternalApi
  StorageWriterConfig() {}

  @InternalApi
  abstract WriterFactory createFactory(Clock clock) throws IOException;

  @InternalApi
  interface WriterFactory {
    @InternalApi
    WritableByteChannelSession<?, BlobInfo> writeSession(
        StorageInternal s,
        BlobInfo info,
        Opts<ObjectTargetOpt> opts,
        Decoder<WriteObjectResponse, BlobInfo> d);
  }
}
