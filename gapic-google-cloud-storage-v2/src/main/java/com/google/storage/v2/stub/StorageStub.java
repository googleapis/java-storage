/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.storage.v2.stub;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the Storage service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class StorageStub implements BackgroundResource {

  public ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> readObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: readObjectCallable()");
  }

  public ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> writeObjectCallable() {
    throw new UnsupportedOperationException("Not implemented: writeObjectCallable()");
  }

  public UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable() {
    throw new UnsupportedOperationException("Not implemented: startResumableWriteCallable()");
  }

  public UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable() {
    throw new UnsupportedOperationException("Not implemented: queryWriteStatusCallable()");
  }

  @Override
  public abstract void close();
}
