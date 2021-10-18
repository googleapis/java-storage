/*
 * Copyright 2020 Google LLC
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
package com.google.storage.v2;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 *
 *
 * <pre>
 * Manages Google Cloud Storage resources.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/storage/v2/storage.proto")
public final class StorageGrpc {

  private StorageGrpc() {}

  public static final String SERVICE_NAME = "google.storage.v2.Storage";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v2.ReadObjectRequest, com.google.storage.v2.ReadObjectResponse>
      getReadObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReadObject",
      requestType = com.google.storage.v2.ReadObjectRequest.class,
      responseType = com.google.storage.v2.ReadObjectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v2.ReadObjectRequest, com.google.storage.v2.ReadObjectResponse>
      getReadObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v2.ReadObjectRequest, com.google.storage.v2.ReadObjectResponse>
        getReadObjectMethod;
    if ((getReadObjectMethod = StorageGrpc.getReadObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getReadObjectMethod = StorageGrpc.getReadObjectMethod) == null) {
          StorageGrpc.getReadObjectMethod =
              getReadObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v2.ReadObjectRequest,
                          com.google.storage.v2.ReadObjectResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReadObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.ReadObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.ReadObjectResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ReadObject"))
                      .build();
        }
      }
    }
    return getReadObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v2.WriteObjectRequest, com.google.storage.v2.WriteObjectResponse>
      getWriteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WriteObject",
      requestType = com.google.storage.v2.WriteObjectRequest.class,
      responseType = com.google.storage.v2.WriteObjectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v2.WriteObjectRequest, com.google.storage.v2.WriteObjectResponse>
      getWriteObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v2.WriteObjectRequest, com.google.storage.v2.WriteObjectResponse>
        getWriteObjectMethod;
    if ((getWriteObjectMethod = StorageGrpc.getWriteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getWriteObjectMethod = StorageGrpc.getWriteObjectMethod) == null) {
          StorageGrpc.getWriteObjectMethod =
              getWriteObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v2.WriteObjectRequest,
                          com.google.storage.v2.WriteObjectResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "WriteObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.WriteObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.WriteObjectResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("WriteObject"))
                      .build();
        }
      }
    }
    return getWriteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v2.StartResumableWriteRequest,
          com.google.storage.v2.StartResumableWriteResponse>
      getStartResumableWriteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StartResumableWrite",
      requestType = com.google.storage.v2.StartResumableWriteRequest.class,
      responseType = com.google.storage.v2.StartResumableWriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v2.StartResumableWriteRequest,
          com.google.storage.v2.StartResumableWriteResponse>
      getStartResumableWriteMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v2.StartResumableWriteRequest,
            com.google.storage.v2.StartResumableWriteResponse>
        getStartResumableWriteMethod;
    if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
          StorageGrpc.getStartResumableWriteMethod =
              getStartResumableWriteMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v2.StartResumableWriteRequest,
                          com.google.storage.v2.StartResumableWriteResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "StartResumableWrite"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.StartResumableWriteRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.StartResumableWriteResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("StartResumableWrite"))
                      .build();
        }
      }
    }
    return getStartResumableWriteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v2.QueryWriteStatusRequest,
          com.google.storage.v2.QueryWriteStatusResponse>
      getQueryWriteStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryWriteStatus",
      requestType = com.google.storage.v2.QueryWriteStatusRequest.class,
      responseType = com.google.storage.v2.QueryWriteStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v2.QueryWriteStatusRequest,
          com.google.storage.v2.QueryWriteStatusResponse>
      getQueryWriteStatusMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v2.QueryWriteStatusRequest,
            com.google.storage.v2.QueryWriteStatusResponse>
        getQueryWriteStatusMethod;
    if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
          StorageGrpc.getQueryWriteStatusMethod =
              getQueryWriteStatusMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v2.QueryWriteStatusRequest,
                          com.google.storage.v2.QueryWriteStatusResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryWriteStatus"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.QueryWriteStatusRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v2.QueryWriteStatusResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("QueryWriteStatus"))
                      .build();
        }
      }
    }
    return getQueryWriteStatusMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static StorageStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageStub>() {
          @java.lang.Override
          public StorageStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageStub(channel, callOptions);
          }
        };
    return StorageStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StorageBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageBlockingStub>() {
          @java.lang.Override
          public StorageBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageBlockingStub(channel, callOptions);
          }
        };
    return StorageBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static StorageFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageFutureStub>() {
          @java.lang.Override
          public StorageFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageFutureStub(channel, callOptions);
          }
        };
    return StorageFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * Manages Google Cloud Storage resources.
   * </pre>
   */
  public abstract static class StorageImplBase implements io.grpc.BindableService {

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void readObject(
        com.google.storage.v2.ReadObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReadObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `WriteObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()` and provide that method an `WriteObjectSpec.`
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `Create`. If there is an error or the connection is
     * broken during the resumable `Create()`, the client should check the status
     * of the `Create()` by calling `QueryWriteStatus()` and continue writing from
     * the returned `persisted_size`. This may be less than the amount of data the
     * client previously sent.
     * The service will not view the object as complete until the client has
     * sent a `WriteObjectRequest` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the response it
     * receives to determine how much data the service was able to commit and
     * whether the service views the object as complete.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectRequest> writeObject(
        io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(
          getWriteObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public void startResumableWrite(
        com.google.storage.v2.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getStartResumableWriteMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(
        com.google.storage.v2.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getQueryWriteStatusMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getReadObjectMethod(),
              io.grpc.stub.ServerCalls.asyncServerStreamingCall(
                  new MethodHandlers<
                      com.google.storage.v2.ReadObjectRequest,
                      com.google.storage.v2.ReadObjectResponse>(this, METHODID_READ_OBJECT)))
          .addMethod(
              getWriteObjectMethod(),
              io.grpc.stub.ServerCalls.asyncClientStreamingCall(
                  new MethodHandlers<
                      com.google.storage.v2.WriteObjectRequest,
                      com.google.storage.v2.WriteObjectResponse>(this, METHODID_WRITE_OBJECT)))
          .addMethod(
              getStartResumableWriteMethod(),
              io.grpc.stub.ServerCalls.asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v2.StartResumableWriteRequest,
                      com.google.storage.v2.StartResumableWriteResponse>(
                      this, METHODID_START_RESUMABLE_WRITE)))
          .addMethod(
              getQueryWriteStatusMethod(),
              io.grpc.stub.ServerCalls.asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v2.QueryWriteStatusRequest,
                      com.google.storage.v2.QueryWriteStatusResponse>(
                      this, METHODID_QUERY_WRITE_STATUS)))
          .build();
    }
  }

  /**
   *
   *
   * <pre>
   * Manages Google Cloud Storage resources.
   * </pre>
   */
  public static final class StorageStub extends io.grpc.stub.AbstractAsyncStub<StorageStub> {
    private StorageStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void readObject(
        com.google.storage.v2.ReadObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getReadObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `WriteObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()` and provide that method an `WriteObjectSpec.`
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `Create`. If there is an error or the connection is
     * broken during the resumable `Create()`, the client should check the status
     * of the `Create()` by calling `QueryWriteStatus()` and continue writing from
     * the returned `persisted_size`. This may be less than the amount of data the
     * client previously sent.
     * The service will not view the object as complete until the client has
     * sent a `WriteObjectRequest` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the response it
     * receives to determine how much data the service was able to commit and
     * whether the service views the object as complete.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectRequest> writeObject(
        io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getWriteObjectMethod(), getCallOptions()), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public void startResumableWrite(
        com.google.storage.v2.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(
        com.google.storage.v2.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   *
   *
   * <pre>
   * Manages Google Cloud Storage resources.
   * </pre>
   */
  public static final class StorageBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<StorageBlockingStub> {
    private StorageBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public java.util.Iterator<com.google.storage.v2.ReadObjectResponse> readObject(
        com.google.storage.v2.ReadObjectRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getReadObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public com.google.storage.v2.StartResumableWriteResponse startResumableWrite(
        com.google.storage.v2.StartResumableWriteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStartResumableWriteMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.storage.v2.QueryWriteStatusResponse queryWriteStatus(
        com.google.storage.v2.QueryWriteStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryWriteStatusMethod(), getCallOptions(), request);
    }
  }

  /**
   *
   *
   * <pre>
   * Manages Google Cloud Storage resources.
   * </pre>
   */
  public static final class StorageFutureStub
      extends io.grpc.stub.AbstractFutureStub<StorageFutureStub> {
    private StorageFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Starts a resumable write. How long the write operation remains valid, and
     * what happens when the write operation becomes invalid, are
     * service-dependent.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v2.StartResumableWriteResponse>
        startResumableWrite(com.google.storage.v2.StartResumableWriteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Determines the `persisted_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `persisted_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v2.QueryWriteStatusResponse>
        queryWriteStatus(com.google.storage.v2.QueryWriteStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_READ_OBJECT = 0;
  private static final int METHODID_START_RESUMABLE_WRITE = 1;
  private static final int METHODID_QUERY_WRITE_STATUS = 2;
  private static final int METHODID_WRITE_OBJECT = 3;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StorageImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StorageImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_READ_OBJECT:
          serviceImpl.readObject(
              (com.google.storage.v2.ReadObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.ReadObjectResponse>)
                  responseObserver);
          break;
        case METHODID_START_RESUMABLE_WRITE:
          serviceImpl.startResumableWrite(
              (com.google.storage.v2.StartResumableWriteRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.StartResumableWriteResponse>)
                  responseObserver);
          break;
        case METHODID_QUERY_WRITE_STATUS:
          serviceImpl.queryWriteStatus(
              (com.google.storage.v2.QueryWriteStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v2.QueryWriteStatusResponse>)
                  responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WRITE_OBJECT:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.writeObject(
                  (io.grpc.stub.StreamObserver<com.google.storage.v2.WriteObjectResponse>)
                      responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private abstract static class StorageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StorageBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.storage.v2.StorageProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Storage");
    }
  }

  private static final class StorageFileDescriptorSupplier extends StorageBaseDescriptorSupplier {
    StorageFileDescriptorSupplier() {}
  }

  private static final class StorageMethodDescriptorSupplier extends StorageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StorageMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StorageGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new StorageFileDescriptorSupplier())
                      .addMethod(getReadObjectMethod())
                      .addMethod(getWriteObjectMethod())
                      .addMethod(getStartResumableWriteMethod())
                      .addMethod(getQueryWriteStatusMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
