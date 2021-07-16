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
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientStreamingCallable;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.longrunning.stub.GrpcOperationsStub;
import com.google.storage.v2.QueryWriteStatusRequest;
import com.google.storage.v2.QueryWriteStatusResponse;
import com.google.storage.v2.ReadObjectRequest;
import com.google.storage.v2.ReadObjectResponse;
import com.google.storage.v2.StartResumableWriteRequest;
import com.google.storage.v2.StartResumableWriteResponse;
import com.google.storage.v2.WriteObjectRequest;
import com.google.storage.v2.WriteObjectResponse;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the Storage service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public class GrpcStorageStub extends StorageStub {
  private static final MethodDescriptor<ReadObjectRequest, ReadObjectResponse>
      readObjectMethodDescriptor =
          MethodDescriptor.<ReadObjectRequest, ReadObjectResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName("google.storage.v2.Storage/ReadObject")
              .setRequestMarshaller(ProtoUtils.marshaller(ReadObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(ReadObjectResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<WriteObjectRequest, WriteObjectResponse>
      writeObjectMethodDescriptor =
          MethodDescriptor.<WriteObjectRequest, WriteObjectResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName("google.storage.v2.Storage/WriteObject")
              .setRequestMarshaller(ProtoUtils.marshaller(WriteObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(WriteObjectResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteMethodDescriptor =
          MethodDescriptor.<StartResumableWriteRequest, StartResumableWriteResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v2.Storage/StartResumableWrite")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(StartResumableWriteRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(StartResumableWriteResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusMethodDescriptor =
          MethodDescriptor.<QueryWriteStatusRequest, QueryWriteStatusResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.v2.Storage/QueryWriteStatus")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(QueryWriteStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(QueryWriteStatusResponse.getDefaultInstance()))
              .build();

  private final ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> readObjectCallable;
  private final ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse>
      writeObjectCallable;
  private final UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable;
  private final UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcStorageStub create(StorageStubSettings settings) throws IOException {
    return new GrpcStorageStub(settings, ClientContext.create(settings));
  }

  public static final GrpcStorageStub create(ClientContext clientContext) throws IOException {
    return new GrpcStorageStub(StorageStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcStorageStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcStorageStub(
        StorageStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcStorageStub, using the given settings. This is protected so that
   * it is easy to make a subclass, but otherwise, the static factory methods should be preferred.
   */
  protected GrpcStorageStub(StorageStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcStorageCallableFactory());
  }

  /**
   * Constructs an instance of GrpcStorageStub, using the given settings. This is protected so that
   * it is easy to make a subclass, but otherwise, the static factory methods should be preferred.
   */
  protected GrpcStorageStub(
      StorageStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<ReadObjectRequest, ReadObjectResponse> readObjectTransportSettings =
        GrpcCallSettings.<ReadObjectRequest, ReadObjectResponse>newBuilder()
            .setMethodDescriptor(readObjectMethodDescriptor)
            .build();
    GrpcCallSettings<WriteObjectRequest, WriteObjectResponse> writeObjectTransportSettings =
        GrpcCallSettings.<WriteObjectRequest, WriteObjectResponse>newBuilder()
            .setMethodDescriptor(writeObjectMethodDescriptor)
            .build();
    GrpcCallSettings<StartResumableWriteRequest, StartResumableWriteResponse>
        startResumableWriteTransportSettings =
            GrpcCallSettings.<StartResumableWriteRequest, StartResumableWriteResponse>newBuilder()
                .setMethodDescriptor(startResumableWriteMethodDescriptor)
                .build();
    GrpcCallSettings<QueryWriteStatusRequest, QueryWriteStatusResponse>
        queryWriteStatusTransportSettings =
            GrpcCallSettings.<QueryWriteStatusRequest, QueryWriteStatusResponse>newBuilder()
                .setMethodDescriptor(queryWriteStatusMethodDescriptor)
                .build();

    this.readObjectCallable =
        callableFactory.createServerStreamingCallable(
            readObjectTransportSettings, settings.readObjectSettings(), clientContext);
    this.writeObjectCallable =
        callableFactory.createClientStreamingCallable(
            writeObjectTransportSettings, settings.writeObjectSettings(), clientContext);
    this.startResumableWriteCallable =
        callableFactory.createUnaryCallable(
            startResumableWriteTransportSettings,
            settings.startResumableWriteSettings(),
            clientContext);
    this.queryWriteStatusCallable =
        callableFactory.createUnaryCallable(
            queryWriteStatusTransportSettings, settings.queryWriteStatusSettings(), clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public ServerStreamingCallable<ReadObjectRequest, ReadObjectResponse> readObjectCallable() {
    return readObjectCallable;
  }

  @Override
  public ClientStreamingCallable<WriteObjectRequest, WriteObjectResponse> writeObjectCallable() {
    return writeObjectCallable;
  }

  @Override
  public UnaryCallable<StartResumableWriteRequest, StartResumableWriteResponse>
      startResumableWriteCallable() {
    return startResumableWriteCallable;
  }

  @Override
  public UnaryCallable<QueryWriteStatusRequest, QueryWriteStatusResponse>
      queryWriteStatusCallable() {
    return queryWriteStatusCallable;
  }

  @Override
  public final void close() {
    shutdown();
  }

  @Override
  public void shutdown() {
    backgroundResources.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return backgroundResources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return backgroundResources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    backgroundResources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }
}
