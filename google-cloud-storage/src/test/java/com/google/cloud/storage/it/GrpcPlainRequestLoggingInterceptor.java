/*
 * Copyright 2024 Google LLC
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

import com.google.api.gax.grpc.GrpcInterceptorProvider;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.WriteObjectRequest;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Client side interceptor which will log gRPC request, response and status messages in plain text,
 * rather than the byte encoded text io.grpc.netty.shaded.io.grpc.netty.NettyClientHandler does.
 *
 * <p>This interceptor does not include the other useful information that NettyClientHandler
 * provides such as headers, method names, peers etc.
 */
public final class GrpcPlainRequestLoggingInterceptor implements ClientInterceptor {

  private static final Logger LOGGER =
      Logger.getLogger(GrpcPlainRequestLoggingInterceptor.class.getName());

  private static final GrpcPlainRequestLoggingInterceptor INSTANCE =
      new GrpcPlainRequestLoggingInterceptor();

  private GrpcPlainRequestLoggingInterceptor() {}

  public static GrpcPlainRequestLoggingInterceptor getInstance() {
    return INSTANCE;
  }

  public static GrpcInterceptorProvider getInterceptorProvider() {
    return InterceptorProvider.INSTANCE;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
    return new SimpleForwardingClientCall<ReqT, RespT>(call) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        SimpleForwardingClientCallListener<RespT> listener =
            new SimpleForwardingClientCallListener<RespT>(responseListener) {
              @Override
              public void onMessage(RespT message) {
                LOGGER.log(
                    Level.CONFIG,
                    () ->
                        String.format(
                            "<<< %s{%n%s}", message.getClass().getSimpleName(), fmtProto(message)));
                super.onMessage(message);
              }

              @Override
              public void onClose(Status status, Metadata trailers) {
                LOGGER.log(
                    Level.CONFIG,
                    () ->
                        String.format(
                            "<<< status = %s, trailers = %s",
                            status.toString(), trailers.toString()));
                super.onClose(status, trailers);
              }
            };
        super.start(listener, headers);
      }

      @Override
      public void sendMessage(ReqT message) {
        LOGGER.log(
            Level.CONFIG,
            () ->
                String.format(
                    ">>> %s{%n%s}", message.getClass().getSimpleName(), fmtProto(message)));
        super.sendMessage(message);
      }
    };
  }

  @NonNull
  static String fmtProto(@NonNull Object obj) {
    if (obj instanceof WriteObjectRequest) {
      return fmtProto((WriteObjectRequest) obj);
    } else if (obj instanceof BidiWriteObjectRequest) {
      return fmtProto((BidiWriteObjectRequest) obj);
    } else if (obj instanceof ReadObjectResponse) {
      return fmtProto((ReadObjectResponse) obj);
    } else if (obj instanceof MessageOrBuilder) {
      return fmtProto((MessageOrBuilder) obj);
    } else {
      return obj.toString();
    }
  }

  @NonNull
  static String fmtProto(@NonNull final MessageOrBuilder msg) {
    return msg.toString();
  }

  @NonNull
  static String fmtProto(@NonNull WriteObjectRequest msg) {
    if (msg.hasChecksummedData()) {
      ByteString content = msg.getChecksummedData().getContent();
      if (content.size() > 20) {
        WriteObjectRequest.Builder b = msg.toBuilder();
        ByteString snip = ByteString.copyFromUtf8(String.format("<snip (%d)>", content.size()));
        ByteString trim = content.substring(0, 20).concat(snip);
        b.getChecksummedDataBuilder().setContent(trim);

        return b.build().toString();
      }
    }
    return msg.toString();
  }

  @NonNull
  static String fmtProto(@NonNull BidiWriteObjectRequest msg) {
    if (msg.hasChecksummedData()) {
      ByteString content = msg.getChecksummedData().getContent();
      if (content.size() > 20) {
        BidiWriteObjectRequest.Builder b = msg.toBuilder();
        ByteString snip = ByteString.copyFromUtf8(String.format("<snip (%d)>", content.size()));
        ByteString trim = content.substring(0, 20).concat(snip);
        b.getChecksummedDataBuilder().setContent(trim);

        return b.build().toString();
      }
    }
    return msg.toString();
  }

  @NonNull
  static String fmtProto(@NonNull ReadObjectResponse msg) {
    if (msg.hasChecksummedData()) {
      ByteString content = msg.getChecksummedData().getContent();
      if (content.size() > 20) {
        ReadObjectResponse.Builder b = msg.toBuilder();
        ByteString snip = ByteString.copyFromUtf8(String.format("<snip (%d)>", content.size()));
        ByteString trim = content.substring(0, 20).concat(snip);
        b.getChecksummedDataBuilder().setContent(trim);

        return b.build().toString();
      }
    }
    return msg.toString();
  }

  private static final class InterceptorProvider implements GrpcInterceptorProvider {
    private static final InterceptorProvider INSTANCE = new InterceptorProvider();

    private final List<ClientInterceptor> interceptors;

    private InterceptorProvider() {
      this.interceptors = ImmutableList.of(GrpcPlainRequestLoggingInterceptor.INSTANCE);
    }

    @Override
    public List<ClientInterceptor> getInterceptors() {
      return interceptors;
    }
  }
}
