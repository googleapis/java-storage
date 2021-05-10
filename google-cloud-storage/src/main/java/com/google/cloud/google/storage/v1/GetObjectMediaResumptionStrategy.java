/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.google.storage.v1;

import com.google.api.core.InternalApi;
import com.google.api.gax.retrying.StreamResumptionStrategy;
import com.google.storage.v1.GetObjectMediaRequest;
import com.google.storage.v1.GetObjectMediaResponse;
import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;

/**
 * An implementation of a {@link StreamResumptionStrategy} for object media. This class tracks the
 * amount of data read and upon retry can build a request to resume the stream from where it left
 * off.
 *
 * <p>This class is considered an internal implementation detail and not meant to be used by
 * applications.
 */
@InternalApi
public class GetObjectMediaResumptionStrategy<GetObjectMediaResponse>
    implements StreamResumptionStrategy<GetObjectMediaRequest, GetObjectMediaResponse> {
  private long bytesProcessed;
  private long offsetOfLast;

  public GetObjectMediaResumptionStrategy() {}

  @Override
  public boolean canResume() {
    return true;
  }

  @Override
  public StreamResumptionStrategy<GetObjectMediaRequest, GetObjectMediaResponse> createNew() {
    return new GetObjectMediaResumptionStrategy<>();
  }

  @Override
  public GetObjectMediaResponse processResponse(GetObjectMediaResponse response) {
    offsetOfLast = bytesProcessed;
    bytesProcessed += response.getContent().size();
    
    return response;
  }
  @Override
  public GetObjectMediaRequest getResumeRequest(GetObjectMediaRequest originalRequest) {
    // An empty offsetOfLast means that we have not successfully read the first chunk,
    // so resume with the original request object.
    //
    // TODO we could probably use some of the first-response-only fields to help with this.
    if (offsetOfLast == 0) {
      return originalRequest;
    }

    // Edge case: retrying a fulfilled request.
    // A fulfilled request is one that has had all of the Object data read, or if it
    // had a read limit, has read enough data.
    if ((originalRequest.getReadLimit() > 0 && originalRequest.getReadLimit() == bytesProcessed)) {
        // TODO we are actually done, return something more meaningful here.
        return originalRequest;
    }

    GetObjectMediaRequest.Builder builder = originalRequest.toBuilder().setReadOffset(offsetOfLast);

    if (originalRequest.getReadLimit() > 0) {
      Preconditions.checkState(
          originalRequest.getReadLimit() > bytesProcessed,
          "Detected too many bytes for the current read limit during a retry.");
      builder.setReadLimit(originalRequest.getReadLimit() - bytesProcessed);
    }

    return builder.build();
  }
}