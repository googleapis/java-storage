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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.UnsafeByteOperations;
import com.google.rpc.DebugInfo;
import com.google.rpc.ErrorInfo;
import com.google.storage.v2.BidiReadObjectResponse;
import com.google.storage.v2.BidiWriteObjectRequest;
import com.google.storage.v2.ObjectRangeData;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client side interceptor which will log gRPC request, headers, response, status and trailers in
 * plain text, rather than the byte encoded text
 * io.grpc.netty.shaded.io.grpc.netty.NettyClientHandler does.
 *
 * <p>This interceptor does not include the other useful information that NettyClientHandler
 * provides such as method names, peers etc.
 */
public final class GrpcPlainRequestLoggingInterceptor implements ClientInterceptor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GrpcPlainRequestLoggingInterceptor.class);

  private static final GrpcPlainRequestLoggingInterceptor INSTANCE =
      new GrpcPlainRequestLoggingInterceptor();

  private static final Metadata.Key<String> X_GOOG_REQUEST_PARAMS =
      Metadata.Key.of("x-goog-request-params", Metadata.ASCII_STRING_MARSHALLER);

  /**
   * Define a map of message types we want to try to unpack from an {@link Any}.
   *
   * <p>The keys are the {@code type_url}, and the values are the default instances of each message.
   */
  private static final Map<String, Message> anyParsers =
      Stream.of(
              com.google.rpc.ErrorInfo.getDefaultInstance(),
              com.google.rpc.DebugInfo.getDefaultInstance(),
              com.google.rpc.QuotaFailure.getDefaultInstance(),
              com.google.rpc.PreconditionFailure.getDefaultInstance(),
              com.google.rpc.BadRequest.getDefaultInstance(),
              com.google.rpc.Help.getDefaultInstance(),
              com.google.storage.v2.BidiReadObjectError.getDefaultInstance(),
              com.google.storage.v2.BidiReadObjectRedirectedError.getDefaultInstance(),
              com.google.storage.v2.BidiWriteObjectRedirectedError.getDefaultInstance())
          // take the stream of Message default instances and collect them to map entries
          .collect(
              Collectors.toMap(
                  // resolve the type_url of the message
                  m -> Any.pack(m).getTypeUrl(),
                  // return the message default instance as is for the value
                  Function.identity()));

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
        if (headers.containsKey(X_GOOG_REQUEST_PARAMS)) {
          LOGGER.atDebug().log(() -> String.format(">>> headers = %s", headers));
        }
        SimpleForwardingClientCallListener<RespT> listener =
            new SimpleForwardingClientCallListener<RespT>(responseListener) {
              @Override
              public void onMessage(RespT message) {
                LOGGER.atDebug().log(
                    () ->
                        String.format(
                            Locale.US,
                            "<<< %s{%n%s}",
                            message.getClass().getSimpleName(),
                            fmtProto(message)));
                super.onMessage(message);
              }

              @Override
              public void onClose(Status status, Metadata trailers) {
                LOGGER.atDebug().log(lazyOnCloseLogString(status, trailers));
                super.onClose(status, trailers);
              }
            };
        super.start(listener, headers);
      }

      @Override
      public void sendMessage(ReqT message) {
        LOGGER.atDebug().log(
            () ->
                String.format(
                    Locale.US,
                    ">>> %s{%n%s}",
                    message.getClass().getSimpleName(),
                    fmtProto(message)));
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
    } else if (obj instanceof BidiReadObjectResponse) {
      return fmtProto((BidiReadObjectResponse) obj);
    } else if (obj instanceof MessageOrBuilder) {
      return fmtProto((MessageOrBuilder) obj);
    } else {
      return obj.toString();
    }
  }

  @NonNull
  static String fmtProto(@NonNull final MessageOrBuilder msg) {
    return TextFormat.printer().printToString(msg);
  }

  @NonNull
  static String fmtProto(@NonNull WriteObjectRequest msg) {
    if (msg.hasChecksummedData()) {
      ByteString content = msg.getChecksummedData().getContent();
      if (content.size() > 20) {
        WriteObjectRequest.Builder b = msg.toBuilder();
        ByteString trim = snipBytes(content);
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
        ByteString trim = snipBytes(content);
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
        ByteString trim = snipBytes(content);
        b.getChecksummedDataBuilder().setContent(trim);

        return b.build().toString();
      }
    }
    return msg.toString();
  }

  @NonNull
  public static String fmtProto(@NonNull BidiReadObjectResponse msg) {
    List<ObjectRangeData> rangeData = msg.getObjectDataRangesList();
    if (!rangeData.isEmpty()) {
      List<ObjectRangeData> snips = new ArrayList<>();
      for (ObjectRangeData rd : rangeData) {
        if (rd.hasChecksummedData()) {
          ByteString content = rd.getChecksummedData().getContent();
          if (content.size() > 20) {
            ObjectRangeData.Builder b = rd.toBuilder();
            ByteString trim = snipBytes(content);
            b.getChecksummedDataBuilder().setContent(trim);
            snips.add(b.build());
          } else {
            snips.add(rd);
          }
        }
      }
      BidiReadObjectResponse snipped =
          msg.toBuilder().clearObjectDataRanges().addAllObjectDataRanges(snips).build();
      return snipped.toString();
    }
    return msg.toString();
  }

  private static ByteString snipBytes(ByteString content) {
    ByteString snip =
        ByteString.copyFromUtf8(String.format(Locale.US, "<snip (%d)>", content.size()));
    return content.substring(0, 20).concat(snip);
  }

  // Suppress DataFlowIssue warnings for this method.
  // While the declared return type of trailers.get is @Nullable T, we're always calling get with a
  // key we know to be present because we found the key name by calling trailers.keys().
  @SuppressWarnings("DataFlowIssue")
  @VisibleForTesting
  public static @NonNull Supplier<String> lazyOnCloseLogString(Status status, Metadata trailers) {
    return () -> {
      final StringBuilder sb = new StringBuilder();
      String description = status.getDescription();
      sb.append("<<< status = {").append("\n  code[4]=").append(status.getCode());
      if (description != null) {
        sb.append(",\n  description[")
            .append(description.getBytes(StandardCharsets.US_ASCII).length)
            .append("]='")
            .append(description)
            .append("'");
      }
      sb.append("\n},\ntrailers = {");
      Set<String> keys = trailers.keys();
      for (String key : keys) {
        sb.append("\n  ").append(key);
        if (key.endsWith("-bin")) {
          byte[] bytes = trailers.get(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER));
          sb.append("[").append(bytes.length).append("]").append(": ");
          if (key.equals("grpc-status-details-bin")) {
            com.google.rpc.Status s;
            try {
              s = com.google.rpc.Status.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
              sb.append(TextFormat.escapeBytes(UnsafeByteOperations.unsafeWrap(bytes)));
              continue;
            }
            sb.append(com.google.rpc.Status.getDescriptor().getFullName()).append("{");
            s.getDetailsList()
                .forEach(
                    a -> {
                      Message maybeParseAs = anyParsers.get(a.getTypeUrl());
                      Message m = maybeParseAs == null ? a : unpack(a, maybeParseAs);
                      // base indentation, single uppercase variable name to make easier to read in
                      // the following code block
                      String I = "    ";
                      sb.append("\n");
                      sb.append(I).append("details {\n");
                      sb.append(I).append("  type_url: ").append(a.getTypeUrl()).append("\n");
                      sb.append(I).append("  value: {\n  ");
                      sb.append(I).append("  ").append(fmtDetails(m, I)).append("\n");
                      sb.append(I).append("  }\n");
                      sb.append(I).append("}");
                    });
            if (!s.getDetailsList().isEmpty()) {
              sb.append("\n");
            }
            sb.append("  }");
          } else if (key.contains("debuginfo")) {
            sb.append("{")
                .append(parseBytesAsMessage(DebugInfo.getDefaultInstance(), bytes))
                .append("}");
          } else if (key.contains("errorinfo")) {
            sb.append("{")
                .append(parseBytesAsMessage(ErrorInfo.getDefaultInstance(), bytes))
                .append("}");
          } else {
            sb.append("{").append(parseBytesAsMessage(Any.getDefaultInstance(), bytes)).append("}");
          }
        } else {
          String asciiStr = trailers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
          sb.append("[")
              .append(asciiStr.getBytes(StandardCharsets.US_ASCII).length)
              .append("]")
              .append(": ");
          sb.append(asciiStr);
        }
      }
      if (!keys.isEmpty()) {
        sb.append("\n");
      }
      sb.append("}");
      return sb.toString();
    };
  }

  private static String fmtDetails(Message m, String baseIndentation) {
    String fmt = fmtProto(m);
    return fmt.substring(0, fmt.length() - 1).replace("\n", "\n" + baseIndentation + "    ");
  }

  private static <M extends Message> String parseBytesAsMessage(M m, byte[] bytes) {
    boolean targetAny = m instanceof Any;
    try {
      Message parsed = m.getParserForType().parseFrom(bytes);
      return fmtProto(parsed);
    } catch (InvalidProtocolBufferException e) {
      if (!targetAny) {
        return parseBytesAsMessage(Any.getDefaultInstance(), bytes);
      } else {
        return TextFormat.escapeBytes(UnsafeByteOperations.unsafeWrap(bytes));
      }
    }
  }

  /**
   * Helper method to unpack an Any. This is unsafe based on the contract of Any.unpack, however the
   * Any we are unpacking here is limited to a set of known types which we have already checked, and
   * the Any is already packed in a Status message, so we know it is already deserializable.
   */
  private static <M extends Message> M unpack(Any any, M m) {
    try {
      return any.unpackSameTypeAs(m);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
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
