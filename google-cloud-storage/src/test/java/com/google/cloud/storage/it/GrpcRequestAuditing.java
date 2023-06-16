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

package com.google.cloud.storage.it;

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.collect.ImmutableList;
import com.google.common.truth.IterableSubject;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientStreamTracer;
import io.grpc.ClientStreamTracer.StreamInfo;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class GrpcRequestAuditing implements ClientInterceptor {

  private final List<Metadata> requestHeaders;

  GrpcRequestAuditing() {
    requestHeaders = Collections.synchronizedList(new ArrayList<>());
  }

  void clear() {
    requestHeaders.clear();
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    CallOptions withStreamTracerFactory = callOptions.withStreamTracerFactory(new Factory());
    return next.newCall(method, withStreamTracerFactory);
  }

  public <T> IterableSubject assertRequestHeader(Metadata.Key<T> key) {
    ImmutableList<Object> actual =
        requestHeaders.stream()
            .map(m -> m.get(key))
            .filter(Objects::nonNull)
            .distinct()
            .collect(ImmutableList.toImmutableList());
    return assertWithMessage(String.format("Headers %s", key.name())).that(actual);
  }

  private final class Factory extends ClientStreamTracer.Factory {
    @Override
    public ClientStreamTracer newClientStreamTracer(StreamInfo info, Metadata headers) {
      return new Tracer();
    }
  }

  private final class Tracer extends ClientStreamTracer {

    @Override
    public void streamCreated(Attributes transportAttrs, Metadata headers) {
      requestHeaders.add(headers);
    }
  }
}
