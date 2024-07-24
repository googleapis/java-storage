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

import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StateCheckingResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.rpc.Status;
import com.google.storage.v2.BidiReadObjectError;
import com.google.storage.v2.BidiReadObjectRedirectedError;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

final class GrpcUtils {

  static final Metadata.Key<Status> GRPC_STATUS_DETAILS_KEY =
      Metadata.Key.of(
          "grpc-status-details-bin", ProtoUtils.metadataMarshaller(Status.getDefaultInstance()));

  private GrpcUtils() {}

  static GrpcCallContext contextWithBucketName(String bucketName, GrpcCallContext baseContext) {
    if (bucketName != null && !bucketName.isEmpty()) {
      return baseContext.withExtraHeaders(
          ImmutableMap.of(
              "x-goog-request-params",
              ImmutableList.of(String.format(Locale.US, "bucket=%s", bucketName))));
    }
    return baseContext;
  }

  /**
   * In the event closing the streams results in multiple streams throwing IOExceptions, collect
   * them all as suppressed exceptions on the first occurrence.
   */
  static <C extends Closeable> void closeAll(Collection<C> closeables) throws IOException {
    IOException ioException =
        closeables.stream()
            .map(
                stream -> {
                  try {
                    stream.close();
                    return null;
                  } catch (IOException e) {
                    return e;
                  }
                })
            .filter(Objects::nonNull)
            .reduce(
                null,
                (l, r) -> {
                  if (l != null) {
                    l.addSuppressed(r);
                    return l;
                  } else {
                    return r;
                  }
                },
                (l, r) -> l);

    if (ioException != null) {
      throw ioException;
    }
  }

  /**
   * Returns the first occurrence of a {@link BidiReadObjectRedirectedError} if the throwable is or
   * is caused by a {@link StatusRuntimeException} that contains trailers, the trailers contain an
   * entry {@code grpc-status-details-bin}, which contains a valid {@link Status}, and the status
   * contains an entry in its details that is a {@link BidiReadObjectRedirectedError} (evaluated
   * from index 0 to length). {@code null} otherwise.
   */
  @Nullable
  static BidiReadObjectRedirectedError getBidiReadObjectRedirectedError(Throwable t) {
    return findFirstPackedAny(t, BidiReadObjectRedirectedError.class);
  }

  /**
   * Returns the first occurrence of a {@link BidiReadObjectError} if the throwable is or is caused
   * by a {@link StatusRuntimeException} that contains trailers, the trailers contain an entry
   * {@code grpc-status-details-bin}, which contains a valid {@link Status}, and the status contains
   * an entry in its details that is a {@link BidiReadObjectError} (evaluated from index 0 to
   * length). {@code null} otherwise.
   */
  static BidiReadObjectError getBidiReadObjectError(Throwable t) {
    return findFirstPackedAny(t, BidiReadObjectError.class);
  }

  @Nullable
  private static <M extends Message> M findFirstPackedAny(Throwable t, Class<M> clazz) {
    if (t instanceof ApiException) {
      t = t.getCause();
    }
    if (!(t instanceof StatusRuntimeException)) {
      return null;
    }
    StatusRuntimeException sre = (StatusRuntimeException) t;
    Metadata trailers = sre.getTrailers();
    if (trailers == null) {
      return null;
    }
    Status status = trailers.get(GRPC_STATUS_DETAILS_KEY);
    if (status == null) {
      return null;
    }
    List<Any> detailsList = status.getDetailsList();
    for (Any any : detailsList) {
      if (any.is(clazz)) {
        try {
          return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
          // ignore it, falling back to regular retry behavior
        }
      }
    }
    return null;
  }

  static <R> StateCheckingResponseObserver<R> decorateAsStateChecking(
      ResponseObserver<R> delegate) {
    return new DecoratingStateCheckingResponseObserver<>(delegate);
  }

  private static final class DecoratingStateCheckingResponseObserver<Response>
      extends StateCheckingResponseObserver<Response> {
    private final ResponseObserver<Response> delegate;

    private DecoratingStateCheckingResponseObserver(ResponseObserver<Response> delegate) {
      this.delegate = delegate;
    }

    @Override
    protected void onStartImpl(StreamController controller) {
      delegate.onStart(controller);
    }

    @Override
    protected void onResponseImpl(Response response) {
      delegate.onResponse(response);
    }

    @Override
    protected void onErrorImpl(Throwable t) {
      delegate.onError(t);
    }

    @Override
    protected void onCompleteImpl() {
      delegate.onComplete();
    }
  }
}
