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
package com.google.storage.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 *
 * <pre>
 * Manages Google Cloud Storage resources.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/storage/v1/storage.proto")
public final class StorageGrpc {

  private StorageGrpc() {}

  public static final String SERVICE_NAME = "google.storage.v1.Storage";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteBucketAccessControlRequest, com.google.protobuf.Empty>
      getDeleteBucketAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteBucketAccessControl",
      requestType = com.google.storage.v1.DeleteBucketAccessControlRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteBucketAccessControlRequest, com.google.protobuf.Empty>
      getDeleteBucketAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.DeleteBucketAccessControlRequest, com.google.protobuf.Empty>
        getDeleteBucketAccessControlMethod;
    if ((getDeleteBucketAccessControlMethod = StorageGrpc.getDeleteBucketAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteBucketAccessControlMethod = StorageGrpc.getDeleteBucketAccessControlMethod)
            == null) {
          StorageGrpc.getDeleteBucketAccessControlMethod =
              getDeleteBucketAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteBucketAccessControlRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "DeleteBucketAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteBucketAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("DeleteBucketAccessControl"))
                      .build();
        }
      }
    }
    return getDeleteBucketAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getGetBucketAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBucketAccessControl",
      requestType = com.google.storage.v1.GetBucketAccessControlRequest.class,
      responseType = com.google.storage.v1.BucketAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getGetBucketAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetBucketAccessControlRequest,
            com.google.storage.v1.BucketAccessControl>
        getGetBucketAccessControlMethod;
    if ((getGetBucketAccessControlMethod = StorageGrpc.getGetBucketAccessControlMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetBucketAccessControlMethod = StorageGrpc.getGetBucketAccessControlMethod)
            == null) {
          StorageGrpc.getGetBucketAccessControlMethod =
              getGetBucketAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetBucketAccessControlRequest,
                          com.google.storage.v1.BucketAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "GetBucketAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetBucketAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.BucketAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("GetBucketAccessControl"))
                      .build();
        }
      }
    }
    return getGetBucketAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getInsertBucketAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertBucketAccessControl",
      requestType = com.google.storage.v1.InsertBucketAccessControlRequest.class,
      responseType = com.google.storage.v1.BucketAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getInsertBucketAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertBucketAccessControlRequest,
            com.google.storage.v1.BucketAccessControl>
        getInsertBucketAccessControlMethod;
    if ((getInsertBucketAccessControlMethod = StorageGrpc.getInsertBucketAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertBucketAccessControlMethod = StorageGrpc.getInsertBucketAccessControlMethod)
            == null) {
          StorageGrpc.getInsertBucketAccessControlMethod =
              getInsertBucketAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertBucketAccessControlRequest,
                          com.google.storage.v1.BucketAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "InsertBucketAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertBucketAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.BucketAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("InsertBucketAccessControl"))
                      .build();
        }
      }
    }
    return getInsertBucketAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListBucketAccessControlsRequest,
          com.google.storage.v1.ListBucketAccessControlsResponse>
      getListBucketAccessControlsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListBucketAccessControls",
      requestType = com.google.storage.v1.ListBucketAccessControlsRequest.class,
      responseType = com.google.storage.v1.ListBucketAccessControlsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListBucketAccessControlsRequest,
          com.google.storage.v1.ListBucketAccessControlsResponse>
      getListBucketAccessControlsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListBucketAccessControlsRequest,
            com.google.storage.v1.ListBucketAccessControlsResponse>
        getListBucketAccessControlsMethod;
    if ((getListBucketAccessControlsMethod = StorageGrpc.getListBucketAccessControlsMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getListBucketAccessControlsMethod = StorageGrpc.getListBucketAccessControlsMethod)
            == null) {
          StorageGrpc.getListBucketAccessControlsMethod =
              getListBucketAccessControlsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListBucketAccessControlsRequest,
                          com.google.storage.v1.ListBucketAccessControlsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListBucketAccessControls"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListBucketAccessControlsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListBucketAccessControlsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("ListBucketAccessControls"))
                      .build();
        }
      }
    }
    return getListBucketAccessControlsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getUpdateBucketAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateBucketAccessControl",
      requestType = com.google.storage.v1.UpdateBucketAccessControlRequest.class,
      responseType = com.google.storage.v1.BucketAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getUpdateBucketAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateBucketAccessControlRequest,
            com.google.storage.v1.BucketAccessControl>
        getUpdateBucketAccessControlMethod;
    if ((getUpdateBucketAccessControlMethod = StorageGrpc.getUpdateBucketAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateBucketAccessControlMethod = StorageGrpc.getUpdateBucketAccessControlMethod)
            == null) {
          StorageGrpc.getUpdateBucketAccessControlMethod =
              getUpdateBucketAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateBucketAccessControlRequest,
                          com.google.storage.v1.BucketAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "UpdateBucketAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateBucketAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.BucketAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("UpdateBucketAccessControl"))
                      .build();
        }
      }
    }
    return getUpdateBucketAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getPatchBucketAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PatchBucketAccessControl",
      requestType = com.google.storage.v1.PatchBucketAccessControlRequest.class,
      responseType = com.google.storage.v1.BucketAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchBucketAccessControlRequest,
          com.google.storage.v1.BucketAccessControl>
      getPatchBucketAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.PatchBucketAccessControlRequest,
            com.google.storage.v1.BucketAccessControl>
        getPatchBucketAccessControlMethod;
    if ((getPatchBucketAccessControlMethod = StorageGrpc.getPatchBucketAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getPatchBucketAccessControlMethod = StorageGrpc.getPatchBucketAccessControlMethod)
            == null) {
          StorageGrpc.getPatchBucketAccessControlMethod =
              getPatchBucketAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.PatchBucketAccessControlRequest,
                          com.google.storage.v1.BucketAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "PatchBucketAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.PatchBucketAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.BucketAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("PatchBucketAccessControl"))
                      .build();
        }
      }
    }
    return getPatchBucketAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteBucketRequest, com.google.protobuf.Empty>
      getDeleteBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteBucket",
      requestType = com.google.storage.v1.DeleteBucketRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteBucketRequest, com.google.protobuf.Empty>
      getDeleteBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.DeleteBucketRequest, com.google.protobuf.Empty>
        getDeleteBucketMethod;
    if ((getDeleteBucketMethod = StorageGrpc.getDeleteBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteBucketMethod = StorageGrpc.getDeleteBucketMethod) == null) {
          StorageGrpc.getDeleteBucketMethod =
              getDeleteBucketMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteBucketRequest, com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteBucket"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteBucketRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteBucket"))
                      .build();
        }
      }
    }
    return getDeleteBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetBucketRequest, com.google.storage.v1.Bucket>
      getGetBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBucket",
      requestType = com.google.storage.v1.GetBucketRequest.class,
      responseType = com.google.storage.v1.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetBucketRequest, com.google.storage.v1.Bucket>
      getGetBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.GetBucketRequest, com.google.storage.v1.Bucket>
        getGetBucketMethod;
    if ((getGetBucketMethod = StorageGrpc.getGetBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetBucketMethod = StorageGrpc.getGetBucketMethod) == null) {
          StorageGrpc.getGetBucketMethod =
              getGetBucketMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetBucketRequest, com.google.storage.v1.Bucket>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBucket"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetBucketRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Bucket.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetBucket"))
                      .build();
        }
      }
    }
    return getGetBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertBucketRequest, com.google.storage.v1.Bucket>
      getInsertBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertBucket",
      requestType = com.google.storage.v1.InsertBucketRequest.class,
      responseType = com.google.storage.v1.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertBucketRequest, com.google.storage.v1.Bucket>
      getInsertBucketMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertBucketRequest, com.google.storage.v1.Bucket>
        getInsertBucketMethod;
    if ((getInsertBucketMethod = StorageGrpc.getInsertBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertBucketMethod = StorageGrpc.getInsertBucketMethod) == null) {
          StorageGrpc.getInsertBucketMethod =
              getInsertBucketMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertBucketRequest, com.google.storage.v1.Bucket>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InsertBucket"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertBucketRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Bucket.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("InsertBucket"))
                      .build();
        }
      }
    }
    return getInsertBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListChannelsRequest, com.google.storage.v1.ListChannelsResponse>
      getListChannelsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListChannels",
      requestType = com.google.storage.v1.ListChannelsRequest.class,
      responseType = com.google.storage.v1.ListChannelsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListChannelsRequest, com.google.storage.v1.ListChannelsResponse>
      getListChannelsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListChannelsRequest, com.google.storage.v1.ListChannelsResponse>
        getListChannelsMethod;
    if ((getListChannelsMethod = StorageGrpc.getListChannelsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListChannelsMethod = StorageGrpc.getListChannelsMethod) == null) {
          StorageGrpc.getListChannelsMethod =
              getListChannelsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListChannelsRequest,
                          com.google.storage.v1.ListChannelsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListChannels"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListChannelsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListChannelsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListChannels"))
                      .build();
        }
      }
    }
    return getListChannelsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListBucketsRequest, com.google.storage.v1.ListBucketsResponse>
      getListBucketsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListBuckets",
      requestType = com.google.storage.v1.ListBucketsRequest.class,
      responseType = com.google.storage.v1.ListBucketsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListBucketsRequest, com.google.storage.v1.ListBucketsResponse>
      getListBucketsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListBucketsRequest, com.google.storage.v1.ListBucketsResponse>
        getListBucketsMethod;
    if ((getListBucketsMethod = StorageGrpc.getListBucketsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListBucketsMethod = StorageGrpc.getListBucketsMethod) == null) {
          StorageGrpc.getListBucketsMethod =
              getListBucketsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListBucketsRequest,
                          com.google.storage.v1.ListBucketsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListBuckets"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListBucketsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListBucketsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListBuckets"))
                      .build();
        }
      }
    }
    return getListBucketsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.LockRetentionPolicyRequest, com.google.storage.v1.Bucket>
      getLockBucketRetentionPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LockBucketRetentionPolicy",
      requestType = com.google.storage.v1.LockRetentionPolicyRequest.class,
      responseType = com.google.storage.v1.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.LockRetentionPolicyRequest, com.google.storage.v1.Bucket>
      getLockBucketRetentionPolicyMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.LockRetentionPolicyRequest, com.google.storage.v1.Bucket>
        getLockBucketRetentionPolicyMethod;
    if ((getLockBucketRetentionPolicyMethod = StorageGrpc.getLockBucketRetentionPolicyMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getLockBucketRetentionPolicyMethod = StorageGrpc.getLockBucketRetentionPolicyMethod)
            == null) {
          StorageGrpc.getLockBucketRetentionPolicyMethod =
              getLockBucketRetentionPolicyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.LockRetentionPolicyRequest,
                          com.google.storage.v1.Bucket>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "LockBucketRetentionPolicy"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.LockRetentionPolicyRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Bucket.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("LockBucketRetentionPolicy"))
                      .build();
        }
      }
    }
    return getLockBucketRetentionPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
      getGetBucketIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBucketIamPolicy",
      requestType = com.google.storage.v1.GetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
      getGetBucketIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
        getGetBucketIamPolicyMethod;
    if ((getGetBucketIamPolicyMethod = StorageGrpc.getGetBucketIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetBucketIamPolicyMethod = StorageGrpc.getGetBucketIamPolicyMethod) == null) {
          StorageGrpc.getGetBucketIamPolicyMethod =
              getGetBucketIamPolicyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBucketIamPolicy"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetIamPolicyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.Policy.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("GetBucketIamPolicy"))
                      .build();
        }
      }
    }
    return getGetBucketIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
      getSetBucketIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetBucketIamPolicy",
      requestType = com.google.storage.v1.SetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
      getSetBucketIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
        getSetBucketIamPolicyMethod;
    if ((getSetBucketIamPolicyMethod = StorageGrpc.getSetBucketIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getSetBucketIamPolicyMethod = StorageGrpc.getSetBucketIamPolicyMethod) == null) {
          StorageGrpc.getSetBucketIamPolicyMethod =
              getSetBucketIamPolicyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetBucketIamPolicy"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.SetIamPolicyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.Policy.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("SetBucketIamPolicy"))
                      .build();
        }
      }
    }
    return getSetBucketIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.TestIamPermissionsRequest,
          com.google.iam.v1.TestIamPermissionsResponse>
      getTestBucketIamPermissionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TestBucketIamPermissions",
      requestType = com.google.storage.v1.TestIamPermissionsRequest.class,
      responseType = com.google.iam.v1.TestIamPermissionsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.TestIamPermissionsRequest,
          com.google.iam.v1.TestIamPermissionsResponse>
      getTestBucketIamPermissionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.TestIamPermissionsRequest,
            com.google.iam.v1.TestIamPermissionsResponse>
        getTestBucketIamPermissionsMethod;
    if ((getTestBucketIamPermissionsMethod = StorageGrpc.getTestBucketIamPermissionsMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getTestBucketIamPermissionsMethod = StorageGrpc.getTestBucketIamPermissionsMethod)
            == null) {
          StorageGrpc.getTestBucketIamPermissionsMethod =
              getTestBucketIamPermissionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.TestIamPermissionsRequest,
                          com.google.iam.v1.TestIamPermissionsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "TestBucketIamPermissions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.TestIamPermissionsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.TestIamPermissionsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("TestBucketIamPermissions"))
                      .build();
        }
      }
    }
    return getTestBucketIamPermissionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchBucketRequest, com.google.storage.v1.Bucket>
      getPatchBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PatchBucket",
      requestType = com.google.storage.v1.PatchBucketRequest.class,
      responseType = com.google.storage.v1.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchBucketRequest, com.google.storage.v1.Bucket>
      getPatchBucketMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.PatchBucketRequest, com.google.storage.v1.Bucket>
        getPatchBucketMethod;
    if ((getPatchBucketMethod = StorageGrpc.getPatchBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getPatchBucketMethod = StorageGrpc.getPatchBucketMethod) == null) {
          StorageGrpc.getPatchBucketMethod =
              getPatchBucketMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.PatchBucketRequest, com.google.storage.v1.Bucket>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PatchBucket"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.PatchBucketRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Bucket.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("PatchBucket"))
                      .build();
        }
      }
    }
    return getPatchBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateBucketRequest, com.google.storage.v1.Bucket>
      getUpdateBucketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateBucket",
      requestType = com.google.storage.v1.UpdateBucketRequest.class,
      responseType = com.google.storage.v1.Bucket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateBucketRequest, com.google.storage.v1.Bucket>
      getUpdateBucketMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateBucketRequest, com.google.storage.v1.Bucket>
        getUpdateBucketMethod;
    if ((getUpdateBucketMethod = StorageGrpc.getUpdateBucketMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateBucketMethod = StorageGrpc.getUpdateBucketMethod) == null) {
          StorageGrpc.getUpdateBucketMethod =
              getUpdateBucketMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateBucketRequest, com.google.storage.v1.Bucket>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateBucket"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateBucketRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Bucket.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateBucket"))
                      .build();
        }
      }
    }
    return getUpdateBucketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.StopChannelRequest, com.google.protobuf.Empty>
      getStopChannelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StopChannel",
      requestType = com.google.storage.v1.StopChannelRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.StopChannelRequest, com.google.protobuf.Empty>
      getStopChannelMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.StopChannelRequest, com.google.protobuf.Empty>
        getStopChannelMethod;
    if ((getStopChannelMethod = StorageGrpc.getStopChannelMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getStopChannelMethod = StorageGrpc.getStopChannelMethod) == null) {
          StorageGrpc.getStopChannelMethod =
              getStopChannelMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.StopChannelRequest, com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StopChannel"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.StopChannelRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("StopChannel"))
                      .build();
        }
      }
    }
    return getStopChannelMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteDefaultObjectAccessControlRequest, com.google.protobuf.Empty>
      getDeleteDefaultObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteDefaultObjectAccessControl",
      requestType = com.google.storage.v1.DeleteDefaultObjectAccessControlRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteDefaultObjectAccessControlRequest, com.google.protobuf.Empty>
      getDeleteDefaultObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.DeleteDefaultObjectAccessControlRequest,
            com.google.protobuf.Empty>
        getDeleteDefaultObjectAccessControlMethod;
    if ((getDeleteDefaultObjectAccessControlMethod =
            StorageGrpc.getDeleteDefaultObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteDefaultObjectAccessControlMethod =
                StorageGrpc.getDeleteDefaultObjectAccessControlMethod)
            == null) {
          StorageGrpc.getDeleteDefaultObjectAccessControlMethod =
              getDeleteDefaultObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteDefaultObjectAccessControlRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "DeleteDefaultObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteDefaultObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("DeleteDefaultObjectAccessControl"))
                      .build();
        }
      }
    }
    return getDeleteDefaultObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getGetDefaultObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDefaultObjectAccessControl",
      requestType = com.google.storage.v1.GetDefaultObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getGetDefaultObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetDefaultObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getGetDefaultObjectAccessControlMethod;
    if ((getGetDefaultObjectAccessControlMethod =
            StorageGrpc.getGetDefaultObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetDefaultObjectAccessControlMethod =
                StorageGrpc.getGetDefaultObjectAccessControlMethod)
            == null) {
          StorageGrpc.getGetDefaultObjectAccessControlMethod =
              getGetDefaultObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetDefaultObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "GetDefaultObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetDefaultObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("GetDefaultObjectAccessControl"))
                      .build();
        }
      }
    }
    return getGetDefaultObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getInsertDefaultObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertDefaultObjectAccessControl",
      requestType = com.google.storage.v1.InsertDefaultObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getInsertDefaultObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertDefaultObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getInsertDefaultObjectAccessControlMethod;
    if ((getInsertDefaultObjectAccessControlMethod =
            StorageGrpc.getInsertDefaultObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertDefaultObjectAccessControlMethod =
                StorageGrpc.getInsertDefaultObjectAccessControlMethod)
            == null) {
          StorageGrpc.getInsertDefaultObjectAccessControlMethod =
              getInsertDefaultObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertDefaultObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "InsertDefaultObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertDefaultObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("InsertDefaultObjectAccessControl"))
                      .build();
        }
      }
    }
    return getInsertDefaultObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListDefaultObjectAccessControlsRequest,
          com.google.storage.v1.ListObjectAccessControlsResponse>
      getListDefaultObjectAccessControlsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListDefaultObjectAccessControls",
      requestType = com.google.storage.v1.ListDefaultObjectAccessControlsRequest.class,
      responseType = com.google.storage.v1.ListObjectAccessControlsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListDefaultObjectAccessControlsRequest,
          com.google.storage.v1.ListObjectAccessControlsResponse>
      getListDefaultObjectAccessControlsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListDefaultObjectAccessControlsRequest,
            com.google.storage.v1.ListObjectAccessControlsResponse>
        getListDefaultObjectAccessControlsMethod;
    if ((getListDefaultObjectAccessControlsMethod =
            StorageGrpc.getListDefaultObjectAccessControlsMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getListDefaultObjectAccessControlsMethod =
                StorageGrpc.getListDefaultObjectAccessControlsMethod)
            == null) {
          StorageGrpc.getListDefaultObjectAccessControlsMethod =
              getListDefaultObjectAccessControlsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListDefaultObjectAccessControlsRequest,
                          com.google.storage.v1.ListObjectAccessControlsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListDefaultObjectAccessControls"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListDefaultObjectAccessControlsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListObjectAccessControlsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("ListDefaultObjectAccessControls"))
                      .build();
        }
      }
    }
    return getListDefaultObjectAccessControlsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getPatchDefaultObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PatchDefaultObjectAccessControl",
      requestType = com.google.storage.v1.PatchDefaultObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getPatchDefaultObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.PatchDefaultObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getPatchDefaultObjectAccessControlMethod;
    if ((getPatchDefaultObjectAccessControlMethod =
            StorageGrpc.getPatchDefaultObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getPatchDefaultObjectAccessControlMethod =
                StorageGrpc.getPatchDefaultObjectAccessControlMethod)
            == null) {
          StorageGrpc.getPatchDefaultObjectAccessControlMethod =
              getPatchDefaultObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.PatchDefaultObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "PatchDefaultObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.PatchDefaultObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("PatchDefaultObjectAccessControl"))
                      .build();
        }
      }
    }
    return getPatchDefaultObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getUpdateDefaultObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateDefaultObjectAccessControl",
      requestType = com.google.storage.v1.UpdateDefaultObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateDefaultObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getUpdateDefaultObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateDefaultObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getUpdateDefaultObjectAccessControlMethod;
    if ((getUpdateDefaultObjectAccessControlMethod =
            StorageGrpc.getUpdateDefaultObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateDefaultObjectAccessControlMethod =
                StorageGrpc.getUpdateDefaultObjectAccessControlMethod)
            == null) {
          StorageGrpc.getUpdateDefaultObjectAccessControlMethod =
              getUpdateDefaultObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateDefaultObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "UpdateDefaultObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateDefaultObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("UpdateDefaultObjectAccessControl"))
                      .build();
        }
      }
    }
    return getUpdateDefaultObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteNotificationRequest, com.google.protobuf.Empty>
      getDeleteNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteNotification",
      requestType = com.google.storage.v1.DeleteNotificationRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteNotificationRequest, com.google.protobuf.Empty>
      getDeleteNotificationMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.DeleteNotificationRequest, com.google.protobuf.Empty>
        getDeleteNotificationMethod;
    if ((getDeleteNotificationMethod = StorageGrpc.getDeleteNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteNotificationMethod = StorageGrpc.getDeleteNotificationMethod) == null) {
          StorageGrpc.getDeleteNotificationMethod =
              getDeleteNotificationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteNotificationRequest, com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteNotification"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteNotificationRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("DeleteNotification"))
                      .build();
        }
      }
    }
    return getDeleteNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetNotificationRequest, com.google.storage.v1.Notification>
      getGetNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNotification",
      requestType = com.google.storage.v1.GetNotificationRequest.class,
      responseType = com.google.storage.v1.Notification.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetNotificationRequest, com.google.storage.v1.Notification>
      getGetNotificationMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetNotificationRequest, com.google.storage.v1.Notification>
        getGetNotificationMethod;
    if ((getGetNotificationMethod = StorageGrpc.getGetNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetNotificationMethod = StorageGrpc.getGetNotificationMethod) == null) {
          StorageGrpc.getGetNotificationMethod =
              getGetNotificationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetNotificationRequest,
                          com.google.storage.v1.Notification>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetNotification"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetNotificationRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Notification.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetNotification"))
                      .build();
        }
      }
    }
    return getGetNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertNotificationRequest, com.google.storage.v1.Notification>
      getInsertNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertNotification",
      requestType = com.google.storage.v1.InsertNotificationRequest.class,
      responseType = com.google.storage.v1.Notification.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertNotificationRequest, com.google.storage.v1.Notification>
      getInsertNotificationMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertNotificationRequest, com.google.storage.v1.Notification>
        getInsertNotificationMethod;
    if ((getInsertNotificationMethod = StorageGrpc.getInsertNotificationMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertNotificationMethod = StorageGrpc.getInsertNotificationMethod) == null) {
          StorageGrpc.getInsertNotificationMethod =
              getInsertNotificationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertNotificationRequest,
                          com.google.storage.v1.Notification>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InsertNotification"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertNotificationRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Notification.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("InsertNotification"))
                      .build();
        }
      }
    }
    return getInsertNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListNotificationsRequest,
          com.google.storage.v1.ListNotificationsResponse>
      getListNotificationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListNotifications",
      requestType = com.google.storage.v1.ListNotificationsRequest.class,
      responseType = com.google.storage.v1.ListNotificationsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListNotificationsRequest,
          com.google.storage.v1.ListNotificationsResponse>
      getListNotificationsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListNotificationsRequest,
            com.google.storage.v1.ListNotificationsResponse>
        getListNotificationsMethod;
    if ((getListNotificationsMethod = StorageGrpc.getListNotificationsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListNotificationsMethod = StorageGrpc.getListNotificationsMethod) == null) {
          StorageGrpc.getListNotificationsMethod =
              getListNotificationsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListNotificationsRequest,
                          com.google.storage.v1.ListNotificationsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListNotifications"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListNotificationsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListNotificationsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListNotifications"))
                      .build();
        }
      }
    }
    return getListNotificationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteObjectAccessControlRequest, com.google.protobuf.Empty>
      getDeleteObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteObjectAccessControl",
      requestType = com.google.storage.v1.DeleteObjectAccessControlRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteObjectAccessControlRequest, com.google.protobuf.Empty>
      getDeleteObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.DeleteObjectAccessControlRequest, com.google.protobuf.Empty>
        getDeleteObjectAccessControlMethod;
    if ((getDeleteObjectAccessControlMethod = StorageGrpc.getDeleteObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteObjectAccessControlMethod = StorageGrpc.getDeleteObjectAccessControlMethod)
            == null) {
          StorageGrpc.getDeleteObjectAccessControlMethod =
              getDeleteObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteObjectAccessControlRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "DeleteObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("DeleteObjectAccessControl"))
                      .build();
        }
      }
    }
    return getDeleteObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getGetObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetObjectAccessControl",
      requestType = com.google.storage.v1.GetObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getGetObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getGetObjectAccessControlMethod;
    if ((getGetObjectAccessControlMethod = StorageGrpc.getGetObjectAccessControlMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetObjectAccessControlMethod = StorageGrpc.getGetObjectAccessControlMethod)
            == null) {
          StorageGrpc.getGetObjectAccessControlMethod =
              getGetObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "GetObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("GetObjectAccessControl"))
                      .build();
        }
      }
    }
    return getGetObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getInsertObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertObjectAccessControl",
      requestType = com.google.storage.v1.InsertObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getInsertObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getInsertObjectAccessControlMethod;
    if ((getInsertObjectAccessControlMethod = StorageGrpc.getInsertObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertObjectAccessControlMethod = StorageGrpc.getInsertObjectAccessControlMethod)
            == null) {
          StorageGrpc.getInsertObjectAccessControlMethod =
              getInsertObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "InsertObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("InsertObjectAccessControl"))
                      .build();
        }
      }
    }
    return getInsertObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListObjectAccessControlsRequest,
          com.google.storage.v1.ListObjectAccessControlsResponse>
      getListObjectAccessControlsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListObjectAccessControls",
      requestType = com.google.storage.v1.ListObjectAccessControlsRequest.class,
      responseType = com.google.storage.v1.ListObjectAccessControlsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListObjectAccessControlsRequest,
          com.google.storage.v1.ListObjectAccessControlsResponse>
      getListObjectAccessControlsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListObjectAccessControlsRequest,
            com.google.storage.v1.ListObjectAccessControlsResponse>
        getListObjectAccessControlsMethod;
    if ((getListObjectAccessControlsMethod = StorageGrpc.getListObjectAccessControlsMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getListObjectAccessControlsMethod = StorageGrpc.getListObjectAccessControlsMethod)
            == null) {
          StorageGrpc.getListObjectAccessControlsMethod =
              getListObjectAccessControlsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListObjectAccessControlsRequest,
                          com.google.storage.v1.ListObjectAccessControlsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListObjectAccessControls"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListObjectAccessControlsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListObjectAccessControlsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("ListObjectAccessControls"))
                      .build();
        }
      }
    }
    return getListObjectAccessControlsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getPatchObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PatchObjectAccessControl",
      requestType = com.google.storage.v1.PatchObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getPatchObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.PatchObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getPatchObjectAccessControlMethod;
    if ((getPatchObjectAccessControlMethod = StorageGrpc.getPatchObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getPatchObjectAccessControlMethod = StorageGrpc.getPatchObjectAccessControlMethod)
            == null) {
          StorageGrpc.getPatchObjectAccessControlMethod =
              getPatchObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.PatchObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "PatchObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.PatchObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("PatchObjectAccessControl"))
                      .build();
        }
      }
    }
    return getPatchObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getUpdateObjectAccessControlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateObjectAccessControl",
      requestType = com.google.storage.v1.UpdateObjectAccessControlRequest.class,
      responseType = com.google.storage.v1.ObjectAccessControl.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateObjectAccessControlRequest,
          com.google.storage.v1.ObjectAccessControl>
      getUpdateObjectAccessControlMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateObjectAccessControlRequest,
            com.google.storage.v1.ObjectAccessControl>
        getUpdateObjectAccessControlMethod;
    if ((getUpdateObjectAccessControlMethod = StorageGrpc.getUpdateObjectAccessControlMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateObjectAccessControlMethod = StorageGrpc.getUpdateObjectAccessControlMethod)
            == null) {
          StorageGrpc.getUpdateObjectAccessControlMethod =
              getUpdateObjectAccessControlMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateObjectAccessControlRequest,
                          com.google.storage.v1.ObjectAccessControl>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "UpdateObjectAccessControl"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateObjectAccessControlRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ObjectAccessControl.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("UpdateObjectAccessControl"))
                      .build();
        }
      }
    }
    return getUpdateObjectAccessControlMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ComposeObjectRequest, com.google.storage.v1.Object>
      getComposeObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ComposeObject",
      requestType = com.google.storage.v1.ComposeObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ComposeObjectRequest, com.google.storage.v1.Object>
      getComposeObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ComposeObjectRequest, com.google.storage.v1.Object>
        getComposeObjectMethod;
    if ((getComposeObjectMethod = StorageGrpc.getComposeObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getComposeObjectMethod = StorageGrpc.getComposeObjectMethod) == null) {
          StorageGrpc.getComposeObjectMethod =
              getComposeObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ComposeObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ComposeObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ComposeObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ComposeObject"))
                      .build();
        }
      }
    }
    return getComposeObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.CopyObjectRequest, com.google.storage.v1.Object>
      getCopyObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CopyObject",
      requestType = com.google.storage.v1.CopyObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.CopyObjectRequest, com.google.storage.v1.Object>
      getCopyObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.CopyObjectRequest, com.google.storage.v1.Object>
        getCopyObjectMethod;
    if ((getCopyObjectMethod = StorageGrpc.getCopyObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getCopyObjectMethod = StorageGrpc.getCopyObjectMethod) == null) {
          StorageGrpc.getCopyObjectMethod =
              getCopyObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.CopyObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CopyObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.CopyObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("CopyObject"))
                      .build();
        }
      }
    }
    return getCopyObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteObjectRequest, com.google.protobuf.Empty>
      getDeleteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteObject",
      requestType = com.google.storage.v1.DeleteObjectRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteObjectRequest, com.google.protobuf.Empty>
      getDeleteObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.DeleteObjectRequest, com.google.protobuf.Empty>
        getDeleteObjectMethod;
    if ((getDeleteObjectMethod = StorageGrpc.getDeleteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteObjectMethod = StorageGrpc.getDeleteObjectMethod) == null) {
          StorageGrpc.getDeleteObjectMethod =
              getDeleteObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteObjectRequest, com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteObject"))
                      .build();
        }
      }
    }
    return getDeleteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectRequest, com.google.storage.v1.Object>
      getGetObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetObject",
      requestType = com.google.storage.v1.GetObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectRequest, com.google.storage.v1.Object>
      getGetObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.GetObjectRequest, com.google.storage.v1.Object>
        getGetObjectMethod;
    if ((getGetObjectMethod = StorageGrpc.getGetObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetObjectMethod = StorageGrpc.getGetObjectMethod) == null) {
          StorageGrpc.getGetObjectMethod =
              getGetObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetObject"))
                      .build();
        }
      }
    }
    return getGetObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectMediaRequest, com.google.storage.v1.GetObjectMediaResponse>
      getGetObjectMediaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetObjectMedia",
      requestType = com.google.storage.v1.GetObjectMediaRequest.class,
      responseType = com.google.storage.v1.GetObjectMediaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetObjectMediaRequest, com.google.storage.v1.GetObjectMediaResponse>
      getGetObjectMediaMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetObjectMediaRequest,
            com.google.storage.v1.GetObjectMediaResponse>
        getGetObjectMediaMethod;
    if ((getGetObjectMediaMethod = StorageGrpc.getGetObjectMediaMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetObjectMediaMethod = StorageGrpc.getGetObjectMediaMethod) == null) {
          StorageGrpc.getGetObjectMediaMethod =
              getGetObjectMediaMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetObjectMediaRequest,
                          com.google.storage.v1.GetObjectMediaResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetObjectMedia"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetObjectMediaRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetObjectMediaResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetObjectMedia"))
                      .build();
        }
      }
    }
    return getGetObjectMediaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertObjectRequest, com.google.storage.v1.Object>
      getInsertObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InsertObject",
      requestType = com.google.storage.v1.InsertObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.InsertObjectRequest, com.google.storage.v1.Object>
      getInsertObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.InsertObjectRequest, com.google.storage.v1.Object>
        getInsertObjectMethod;
    if ((getInsertObjectMethod = StorageGrpc.getInsertObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getInsertObjectMethod = StorageGrpc.getInsertObjectMethod) == null) {
          StorageGrpc.getInsertObjectMethod =
              getInsertObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.InsertObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InsertObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.InsertObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("InsertObject"))
                      .build();
        }
      }
    }
    return getInsertObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListObjectsRequest, com.google.storage.v1.ListObjectsResponse>
      getListObjectsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListObjects",
      requestType = com.google.storage.v1.ListObjectsRequest.class,
      responseType = com.google.storage.v1.ListObjectsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListObjectsRequest, com.google.storage.v1.ListObjectsResponse>
      getListObjectsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListObjectsRequest, com.google.storage.v1.ListObjectsResponse>
        getListObjectsMethod;
    if ((getListObjectsMethod = StorageGrpc.getListObjectsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListObjectsMethod = StorageGrpc.getListObjectsMethod) == null) {
          StorageGrpc.getListObjectsMethod =
              getListObjectsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListObjectsRequest,
                          com.google.storage.v1.ListObjectsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListObjects"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListObjectsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListObjectsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListObjects"))
                      .build();
        }
      }
    }
    return getListObjectsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.RewriteObjectRequest, com.google.storage.v1.RewriteResponse>
      getRewriteObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RewriteObject",
      requestType = com.google.storage.v1.RewriteObjectRequest.class,
      responseType = com.google.storage.v1.RewriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.RewriteObjectRequest, com.google.storage.v1.RewriteResponse>
      getRewriteObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.RewriteObjectRequest, com.google.storage.v1.RewriteResponse>
        getRewriteObjectMethod;
    if ((getRewriteObjectMethod = StorageGrpc.getRewriteObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getRewriteObjectMethod = StorageGrpc.getRewriteObjectMethod) == null) {
          StorageGrpc.getRewriteObjectMethod =
              getRewriteObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.RewriteObjectRequest,
                          com.google.storage.v1.RewriteResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RewriteObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.RewriteObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.RewriteResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("RewriteObject"))
                      .build();
        }
      }
    }
    return getRewriteObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.StartResumableWriteRequest,
          com.google.storage.v1.StartResumableWriteResponse>
      getStartResumableWriteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StartResumableWrite",
      requestType = com.google.storage.v1.StartResumableWriteRequest.class,
      responseType = com.google.storage.v1.StartResumableWriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.StartResumableWriteRequest,
          com.google.storage.v1.StartResumableWriteResponse>
      getStartResumableWriteMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.StartResumableWriteRequest,
            com.google.storage.v1.StartResumableWriteResponse>
        getStartResumableWriteMethod;
    if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getStartResumableWriteMethod = StorageGrpc.getStartResumableWriteMethod) == null) {
          StorageGrpc.getStartResumableWriteMethod =
              getStartResumableWriteMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.StartResumableWriteRequest,
                          com.google.storage.v1.StartResumableWriteResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "StartResumableWrite"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.StartResumableWriteRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.StartResumableWriteResponse
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
          com.google.storage.v1.QueryWriteStatusRequest,
          com.google.storage.v1.QueryWriteStatusResponse>
      getQueryWriteStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryWriteStatus",
      requestType = com.google.storage.v1.QueryWriteStatusRequest.class,
      responseType = com.google.storage.v1.QueryWriteStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.QueryWriteStatusRequest,
          com.google.storage.v1.QueryWriteStatusResponse>
      getQueryWriteStatusMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.QueryWriteStatusRequest,
            com.google.storage.v1.QueryWriteStatusResponse>
        getQueryWriteStatusMethod;
    if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getQueryWriteStatusMethod = StorageGrpc.getQueryWriteStatusMethod) == null) {
          StorageGrpc.getQueryWriteStatusMethod =
              getQueryWriteStatusMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.QueryWriteStatusRequest,
                          com.google.storage.v1.QueryWriteStatusResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryWriteStatus"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.QueryWriteStatusRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.QueryWriteStatusResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("QueryWriteStatus"))
                      .build();
        }
      }
    }
    return getQueryWriteStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchObjectRequest, com.google.storage.v1.Object>
      getPatchObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PatchObject",
      requestType = com.google.storage.v1.PatchObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.PatchObjectRequest, com.google.storage.v1.Object>
      getPatchObjectMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.PatchObjectRequest, com.google.storage.v1.Object>
        getPatchObjectMethod;
    if ((getPatchObjectMethod = StorageGrpc.getPatchObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getPatchObjectMethod = StorageGrpc.getPatchObjectMethod) == null) {
          StorageGrpc.getPatchObjectMethod =
              getPatchObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.PatchObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PatchObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.PatchObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("PatchObject"))
                      .build();
        }
      }
    }
    return getPatchObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateObjectRequest, com.google.storage.v1.Object>
      getUpdateObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateObject",
      requestType = com.google.storage.v1.UpdateObjectRequest.class,
      responseType = com.google.storage.v1.Object.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateObjectRequest, com.google.storage.v1.Object>
      getUpdateObjectMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateObjectRequest, com.google.storage.v1.Object>
        getUpdateObjectMethod;
    if ((getUpdateObjectMethod = StorageGrpc.getUpdateObjectMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateObjectMethod = StorageGrpc.getUpdateObjectMethod) == null) {
          StorageGrpc.getUpdateObjectMethod =
              getUpdateObjectMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateObjectRequest, com.google.storage.v1.Object>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateObject"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateObjectRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Object.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateObject"))
                      .build();
        }
      }
    }
    return getUpdateObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
      getGetObjectIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetObjectIamPolicy",
      requestType = com.google.storage.v1.GetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
      getGetObjectIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
        getGetObjectIamPolicyMethod;
    if ((getGetObjectIamPolicyMethod = StorageGrpc.getGetObjectIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetObjectIamPolicyMethod = StorageGrpc.getGetObjectIamPolicyMethod) == null) {
          StorageGrpc.getGetObjectIamPolicyMethod =
              getGetObjectIamPolicyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetObjectIamPolicy"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetIamPolicyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.Policy.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("GetObjectIamPolicy"))
                      .build();
        }
      }
    }
    return getGetObjectIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
      getSetObjectIamPolicyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetObjectIamPolicy",
      requestType = com.google.storage.v1.SetIamPolicyRequest.class,
      responseType = com.google.iam.v1.Policy.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
      getSetObjectIamPolicyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
        getSetObjectIamPolicyMethod;
    if ((getSetObjectIamPolicyMethod = StorageGrpc.getSetObjectIamPolicyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getSetObjectIamPolicyMethod = StorageGrpc.getSetObjectIamPolicyMethod) == null) {
          StorageGrpc.getSetObjectIamPolicyMethod =
              getSetObjectIamPolicyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetObjectIamPolicy"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.SetIamPolicyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.Policy.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("SetObjectIamPolicy"))
                      .build();
        }
      }
    }
    return getSetObjectIamPolicyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.TestIamPermissionsRequest,
          com.google.iam.v1.TestIamPermissionsResponse>
      getTestObjectIamPermissionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TestObjectIamPermissions",
      requestType = com.google.storage.v1.TestIamPermissionsRequest.class,
      responseType = com.google.iam.v1.TestIamPermissionsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.TestIamPermissionsRequest,
          com.google.iam.v1.TestIamPermissionsResponse>
      getTestObjectIamPermissionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.TestIamPermissionsRequest,
            com.google.iam.v1.TestIamPermissionsResponse>
        getTestObjectIamPermissionsMethod;
    if ((getTestObjectIamPermissionsMethod = StorageGrpc.getTestObjectIamPermissionsMethod)
        == null) {
      synchronized (StorageGrpc.class) {
        if ((getTestObjectIamPermissionsMethod = StorageGrpc.getTestObjectIamPermissionsMethod)
            == null) {
          StorageGrpc.getTestObjectIamPermissionsMethod =
              getTestObjectIamPermissionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.TestIamPermissionsRequest,
                          com.google.iam.v1.TestIamPermissionsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "TestObjectIamPermissions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.TestIamPermissionsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.iam.v1.TestIamPermissionsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageMethodDescriptorSupplier("TestObjectIamPermissions"))
                      .build();
        }
      }
    }
    return getTestObjectIamPermissionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.WatchAllObjectsRequest, com.google.storage.v1.Channel>
      getWatchAllObjectsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WatchAllObjects",
      requestType = com.google.storage.v1.WatchAllObjectsRequest.class,
      responseType = com.google.storage.v1.Channel.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.WatchAllObjectsRequest, com.google.storage.v1.Channel>
      getWatchAllObjectsMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.WatchAllObjectsRequest, com.google.storage.v1.Channel>
        getWatchAllObjectsMethod;
    if ((getWatchAllObjectsMethod = StorageGrpc.getWatchAllObjectsMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getWatchAllObjectsMethod = StorageGrpc.getWatchAllObjectsMethod) == null) {
          StorageGrpc.getWatchAllObjectsMethod =
              getWatchAllObjectsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.WatchAllObjectsRequest, com.google.storage.v1.Channel>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "WatchAllObjects"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.WatchAllObjectsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.Channel.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("WatchAllObjects"))
                      .build();
        }
      }
    }
    return getWatchAllObjectsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetProjectServiceAccountRequest,
          com.google.storage.v1.ServiceAccount>
      getGetServiceAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetServiceAccount",
      requestType = com.google.storage.v1.GetProjectServiceAccountRequest.class,
      responseType = com.google.storage.v1.ServiceAccount.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetProjectServiceAccountRequest,
          com.google.storage.v1.ServiceAccount>
      getGetServiceAccountMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetProjectServiceAccountRequest,
            com.google.storage.v1.ServiceAccount>
        getGetServiceAccountMethod;
    if ((getGetServiceAccountMethod = StorageGrpc.getGetServiceAccountMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetServiceAccountMethod = StorageGrpc.getGetServiceAccountMethod) == null) {
          StorageGrpc.getGetServiceAccountMethod =
              getGetServiceAccountMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetProjectServiceAccountRequest,
                          com.google.storage.v1.ServiceAccount>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetServiceAccount"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetProjectServiceAccountRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ServiceAccount.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetServiceAccount"))
                      .build();
        }
      }
    }
    return getGetServiceAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.CreateHmacKeyRequest, com.google.storage.v1.CreateHmacKeyResponse>
      getCreateHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateHmacKey",
      requestType = com.google.storage.v1.CreateHmacKeyRequest.class,
      responseType = com.google.storage.v1.CreateHmacKeyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.CreateHmacKeyRequest, com.google.storage.v1.CreateHmacKeyResponse>
      getCreateHmacKeyMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.CreateHmacKeyRequest, com.google.storage.v1.CreateHmacKeyResponse>
        getCreateHmacKeyMethod;
    if ((getCreateHmacKeyMethod = StorageGrpc.getCreateHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getCreateHmacKeyMethod = StorageGrpc.getCreateHmacKeyMethod) == null) {
          StorageGrpc.getCreateHmacKeyMethod =
              getCreateHmacKeyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.CreateHmacKeyRequest,
                          com.google.storage.v1.CreateHmacKeyResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateHmacKey"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.CreateHmacKeyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.CreateHmacKeyResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("CreateHmacKey"))
                      .build();
        }
      }
    }
    return getCreateHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteHmacKeyRequest, com.google.protobuf.Empty>
      getDeleteHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteHmacKey",
      requestType = com.google.storage.v1.DeleteHmacKeyRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.DeleteHmacKeyRequest, com.google.protobuf.Empty>
      getDeleteHmacKeyMethod() {
    io.grpc.MethodDescriptor<com.google.storage.v1.DeleteHmacKeyRequest, com.google.protobuf.Empty>
        getDeleteHmacKeyMethod;
    if ((getDeleteHmacKeyMethod = StorageGrpc.getDeleteHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getDeleteHmacKeyMethod = StorageGrpc.getDeleteHmacKeyMethod) == null) {
          StorageGrpc.getDeleteHmacKeyMethod =
              getDeleteHmacKeyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.DeleteHmacKeyRequest, com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteHmacKey"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.DeleteHmacKeyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("DeleteHmacKey"))
                      .build();
        }
      }
    }
    return getDeleteHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.GetHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
      getGetHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetHmacKey",
      requestType = com.google.storage.v1.GetHmacKeyRequest.class,
      responseType = com.google.storage.v1.HmacKeyMetadata.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.GetHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
      getGetHmacKeyMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.GetHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
        getGetHmacKeyMethod;
    if ((getGetHmacKeyMethod = StorageGrpc.getGetHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getGetHmacKeyMethod = StorageGrpc.getGetHmacKeyMethod) == null) {
          StorageGrpc.getGetHmacKeyMethod =
              getGetHmacKeyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.GetHmacKeyRequest,
                          com.google.storage.v1.HmacKeyMetadata>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetHmacKey"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.GetHmacKeyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.HmacKeyMetadata.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("GetHmacKey"))
                      .build();
        }
      }
    }
    return getGetHmacKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.ListHmacKeysRequest, com.google.storage.v1.ListHmacKeysResponse>
      getListHmacKeysMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListHmacKeys",
      requestType = com.google.storage.v1.ListHmacKeysRequest.class,
      responseType = com.google.storage.v1.ListHmacKeysResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.ListHmacKeysRequest, com.google.storage.v1.ListHmacKeysResponse>
      getListHmacKeysMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.ListHmacKeysRequest, com.google.storage.v1.ListHmacKeysResponse>
        getListHmacKeysMethod;
    if ((getListHmacKeysMethod = StorageGrpc.getListHmacKeysMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getListHmacKeysMethod = StorageGrpc.getListHmacKeysMethod) == null) {
          StorageGrpc.getListHmacKeysMethod =
              getListHmacKeysMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.ListHmacKeysRequest,
                          com.google.storage.v1.ListHmacKeysResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListHmacKeys"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListHmacKeysRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.ListHmacKeysResponse.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("ListHmacKeys"))
                      .build();
        }
      }
    }
    return getListHmacKeysMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
      getUpdateHmacKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateHmacKey",
      requestType = com.google.storage.v1.UpdateHmacKeyRequest.class,
      responseType = com.google.storage.v1.HmacKeyMetadata.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.v1.UpdateHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
      getUpdateHmacKeyMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.v1.UpdateHmacKeyRequest, com.google.storage.v1.HmacKeyMetadata>
        getUpdateHmacKeyMethod;
    if ((getUpdateHmacKeyMethod = StorageGrpc.getUpdateHmacKeyMethod) == null) {
      synchronized (StorageGrpc.class) {
        if ((getUpdateHmacKeyMethod = StorageGrpc.getUpdateHmacKeyMethod) == null) {
          StorageGrpc.getUpdateHmacKeyMethod =
              getUpdateHmacKeyMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.v1.UpdateHmacKeyRequest,
                          com.google.storage.v1.HmacKeyMetadata>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateHmacKey"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.UpdateHmacKeyRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.v1.HmacKeyMetadata.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageMethodDescriptorSupplier("UpdateHmacKey"))
                      .build();
        }
      }
    }
    return getUpdateHmacKeyMethod;
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
     * Permanently deletes the ACL entry for the specified entity on the specified
     * bucket.
     * </pre>
     */
    public void deleteBucketAccessControl(
        com.google.storage.v1.DeleteBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteBucketAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified bucket.
     * </pre>
     */
    public void getBucketAccessControl(
        com.google.storage.v1.GetBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getGetBucketAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified bucket.
     * </pre>
     */
    public void insertBucketAccessControl(
        com.google.storage.v1.InsertBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getInsertBucketAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified bucket.
     * </pre>
     */
    public void listBucketAccessControls(
        com.google.storage.v1.ListBucketAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketAccessControlsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListBucketAccessControlsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket. Equivalent to
     * PatchBucketAccessControl, but all unspecified fields will be
     * reset to their default values.
     * </pre>
     */
    public void updateBucketAccessControl(
        com.google.storage.v1.UpdateBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateBucketAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket.
     * </pre>
     */
    public void patchBucketAccessControl(
        com.google.storage.v1.PatchBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getPatchBucketAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public void deleteBucket(
        com.google.storage.v1.DeleteBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteBucketMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public void getBucket(
        com.google.storage.v1.GetBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnimplementedUnaryCall(getGetBucketMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public void insertBucket(
        com.google.storage.v1.InsertBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnimplementedUnaryCall(getInsertBucketMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * List active object change notification channels for this bucket.
     * </pre>
     */
    public void listChannels(
        com.google.storage.v1.ListChannelsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListChannelsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListChannelsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public void listBuckets(
        com.google.storage.v1.ListBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListBucketsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public void lockBucketRetentionPolicy(
        com.google.storage.v1.LockRetentionPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnimplementedUnaryCall(getLockBucketRetentionPolicyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified bucket.
     * </pre>
     */
    public void getBucketIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnimplementedUnaryCall(getGetBucketIamPolicyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public void setBucketIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnimplementedUnaryCall(getSetBucketIamPolicyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testBucketIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getTestBucketIamPermissionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public void patchBucket(
        com.google.storage.v1.PatchBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnimplementedUnaryCall(getPatchBucketMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Equivalent to PatchBucket, but always replaces all
     * mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * Like PatchBucket, Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public void updateBucket(
        com.google.storage.v1.UpdateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateBucketMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Halts "Object Change Notification" push messagages.
     * See https://cloud.google.com/storage/docs/object-change-notification
     * Note: this is not related to the newer "Notifications" resource, which
     * are stopped using DeleteNotification.
     * </pre>
     */
    public void stopChannel(
        com.google.storage.v1.StopChannelRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getStopChannelMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the default object ACL entry for the specified entity
     * on the specified bucket.
     * </pre>
     */
    public void deleteDefaultObjectAccessControl(
        com.google.storage.v1.DeleteDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteDefaultObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the default object ACL entry for the specified entity on the
     * specified bucket.
     * </pre>
     */
    public void getDefaultObjectAccessControl(
        com.google.storage.v1.GetDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getGetDefaultObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new default object ACL entry on the specified bucket.
     * </pre>
     */
    public void insertDefaultObjectAccessControl(
        com.google.storage.v1.InsertDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getInsertDefaultObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves default object ACL entries on the specified bucket.
     * </pre>
     */
    public void listDefaultObjectAccessControls(
        com.google.storage.v1.ListDefaultObjectAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListDefaultObjectAccessControlsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket.
     * </pre>
     */
    public void patchDefaultObjectAccessControl(
        com.google.storage.v1.PatchDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getPatchDefaultObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket. Equivalent to
     * PatchDefaultObjectAccessControl, but modifies all unspecified fields to
     * their default values.
     * </pre>
     */
    public void updateDefaultObjectAccessControl(
        com.google.storage.v1.UpdateDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateDefaultObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes a notification subscription.
     * Note: Older, "Object Change Notification" push subscriptions should be
     * deleted using StopChannel instead.
     * </pre>
     */
    public void deleteNotification(
        com.google.storage.v1.DeleteNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteNotificationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * View a notification configuration.
     * </pre>
     */
    public void getNotification(
        com.google.storage.v1.GetNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Notification> responseObserver) {
      asyncUnimplementedUnaryCall(getGetNotificationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Cloud Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public void insertNotification(
        com.google.storage.v1.InsertNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Notification> responseObserver) {
      asyncUnimplementedUnaryCall(getInsertNotificationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public void listNotifications(
        com.google.storage.v1.ListNotificationsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListNotificationsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListNotificationsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the ACL entry for the specified entity on the specified
     * object.
     * </pre>
     */
    public void deleteObjectAccessControl(
        com.google.storage.v1.DeleteObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified object.
     * </pre>
     */
    public void getObjectAccessControl(
        com.google.storage.v1.GetObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified object.
     * </pre>
     */
    public void insertObjectAccessControl(
        com.google.storage.v1.InsertObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getInsertObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified object.
     * </pre>
     */
    public void listObjectAccessControls(
        com.google.storage.v1.ListObjectAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListObjectAccessControlsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Patches an ACL entry on the specified object.  Patch is similar to update,
     * but only applies or appends the specified fields in the
     * object_access_control object.  Other fields are unaffected.
     * </pre>
     */
    public void patchObjectAccessControl(
        com.google.storage.v1.PatchObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getPatchObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified object.
     * </pre>
     */
    public void updateObjectAccessControl(
        com.google.storage.v1.UpdateObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateObjectAccessControlMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public void composeObject(
        com.google.storage.v1.ComposeObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnimplementedUnaryCall(getComposeObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Copies a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void copyObject(
        com.google.storage.v1.CopyObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnimplementedUnaryCall(getCopyObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public void deleteObject(
        com.google.storage.v1.DeleteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public void getObject(
        com.google.storage.v1.GetObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void getObjectMedia(
        com.google.storage.v1.GetObjectMediaRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.GetObjectMediaResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectMediaMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `InsertObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()` and provide that method an `InsertObjectSpec.`
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `Insert`. If there is an error or the connection is
     * broken during the resumable `Insert()`, the client should check the status
     * of the `Insert()` by calling `QueryWriteStatus()` and continue writing from
     * the returned `committed_size`. This may be less than the amount of data the
     * client previously sent.
     * The service will not view the object as complete until the client has
     * sent an `Insert` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the
     * `Object` it receives to determine how much data the service was
     * able to commit and whether the service views the object as complete.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v1.InsertObjectRequest> insertObject(
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      return asyncUnimplementedStreamingCall(getInsertObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public void listObjects(
        com.google.storage.v1.ListObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListObjectsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void rewriteObject(
        com.google.storage.v1.RewriteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.RewriteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRewriteObjectMethod(), responseObserver);
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
        com.google.storage.v1.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.StartResumableWriteResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getStartResumableWriteMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Determines the `committed_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `committed_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(
        com.google.storage.v1.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.QueryWriteStatusResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getQueryWriteStatusMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata.
     * </pre>
     */
    public void patchObject(
        com.google.storage.v1.PatchObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnimplementedUnaryCall(getPatchObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata. Equivalent to PatchObject, but always
     * replaces all mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * </pre>
     */
    public void updateObject(
        com.google.storage.v1.UpdateObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateObjectMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified object.
     * </pre>
     */
    public void getObjectIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectIamPolicyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified object.
     * </pre>
     */
    public void setObjectIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnimplementedUnaryCall(getSetObjectIamPolicyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given object to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testObjectIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getTestObjectIamPermissionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Watch for changes on all objects in a bucket.
     * </pre>
     */
    public void watchAllObjects(
        com.google.storage.v1.WatchAllObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Channel> responseObserver) {
      asyncUnimplementedUnaryCall(getWatchAllObjectsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public void getServiceAccount(
        com.google.storage.v1.GetProjectServiceAccountRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ServiceAccount> responseObserver) {
      asyncUnimplementedUnaryCall(getGetServiceAccountMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public void createHmacKey(
        com.google.storage.v1.CreateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.CreateHmacKeyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateHmacKeyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public void deleteHmacKey(
        com.google.storage.v1.DeleteHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteHmacKeyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public void getHmacKey(
        com.google.storage.v1.GetHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata> responseObserver) {
      asyncUnimplementedUnaryCall(getGetHmacKeyMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public void listHmacKeys(
        com.google.storage.v1.ListHmacKeysRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListHmacKeysResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListHmacKeysMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public void updateHmacKey(
        com.google.storage.v1.UpdateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateHmacKeyMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getDeleteBucketAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteBucketAccessControlRequest,
                      com.google.protobuf.Empty>(this, METHODID_DELETE_BUCKET_ACCESS_CONTROL)))
          .addMethod(
              getGetBucketAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetBucketAccessControlRequest,
                      com.google.storage.v1.BucketAccessControl>(
                      this, METHODID_GET_BUCKET_ACCESS_CONTROL)))
          .addMethod(
              getInsertBucketAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertBucketAccessControlRequest,
                      com.google.storage.v1.BucketAccessControl>(
                      this, METHODID_INSERT_BUCKET_ACCESS_CONTROL)))
          .addMethod(
              getListBucketAccessControlsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListBucketAccessControlsRequest,
                      com.google.storage.v1.ListBucketAccessControlsResponse>(
                      this, METHODID_LIST_BUCKET_ACCESS_CONTROLS)))
          .addMethod(
              getUpdateBucketAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateBucketAccessControlRequest,
                      com.google.storage.v1.BucketAccessControl>(
                      this, METHODID_UPDATE_BUCKET_ACCESS_CONTROL)))
          .addMethod(
              getPatchBucketAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.PatchBucketAccessControlRequest,
                      com.google.storage.v1.BucketAccessControl>(
                      this, METHODID_PATCH_BUCKET_ACCESS_CONTROL)))
          .addMethod(
              getDeleteBucketMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteBucketRequest, com.google.protobuf.Empty>(
                      this, METHODID_DELETE_BUCKET)))
          .addMethod(
              getGetBucketMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetBucketRequest, com.google.storage.v1.Bucket>(
                      this, METHODID_GET_BUCKET)))
          .addMethod(
              getInsertBucketMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertBucketRequest, com.google.storage.v1.Bucket>(
                      this, METHODID_INSERT_BUCKET)))
          .addMethod(
              getListChannelsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListChannelsRequest,
                      com.google.storage.v1.ListChannelsResponse>(this, METHODID_LIST_CHANNELS)))
          .addMethod(
              getListBucketsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListBucketsRequest,
                      com.google.storage.v1.ListBucketsResponse>(this, METHODID_LIST_BUCKETS)))
          .addMethod(
              getLockBucketRetentionPolicyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.LockRetentionPolicyRequest,
                      com.google.storage.v1.Bucket>(this, METHODID_LOCK_BUCKET_RETENTION_POLICY)))
          .addMethod(
              getGetBucketIamPolicyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>(
                      this, METHODID_GET_BUCKET_IAM_POLICY)))
          .addMethod(
              getSetBucketIamPolicyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>(
                      this, METHODID_SET_BUCKET_IAM_POLICY)))
          .addMethod(
              getTestBucketIamPermissionsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.TestIamPermissionsRequest,
                      com.google.iam.v1.TestIamPermissionsResponse>(
                      this, METHODID_TEST_BUCKET_IAM_PERMISSIONS)))
          .addMethod(
              getPatchBucketMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.PatchBucketRequest, com.google.storage.v1.Bucket>(
                      this, METHODID_PATCH_BUCKET)))
          .addMethod(
              getUpdateBucketMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateBucketRequest, com.google.storage.v1.Bucket>(
                      this, METHODID_UPDATE_BUCKET)))
          .addMethod(
              getStopChannelMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.StopChannelRequest, com.google.protobuf.Empty>(
                      this, METHODID_STOP_CHANNEL)))
          .addMethod(
              getDeleteDefaultObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteDefaultObjectAccessControlRequest,
                      com.google.protobuf.Empty>(
                      this, METHODID_DELETE_DEFAULT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getGetDefaultObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetDefaultObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_GET_DEFAULT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getInsertDefaultObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertDefaultObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_INSERT_DEFAULT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getListDefaultObjectAccessControlsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListDefaultObjectAccessControlsRequest,
                      com.google.storage.v1.ListObjectAccessControlsResponse>(
                      this, METHODID_LIST_DEFAULT_OBJECT_ACCESS_CONTROLS)))
          .addMethod(
              getPatchDefaultObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.PatchDefaultObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_PATCH_DEFAULT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getUpdateDefaultObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateDefaultObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_UPDATE_DEFAULT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getDeleteNotificationMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteNotificationRequest, com.google.protobuf.Empty>(
                      this, METHODID_DELETE_NOTIFICATION)))
          .addMethod(
              getGetNotificationMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetNotificationRequest,
                      com.google.storage.v1.Notification>(this, METHODID_GET_NOTIFICATION)))
          .addMethod(
              getInsertNotificationMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertNotificationRequest,
                      com.google.storage.v1.Notification>(this, METHODID_INSERT_NOTIFICATION)))
          .addMethod(
              getListNotificationsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListNotificationsRequest,
                      com.google.storage.v1.ListNotificationsResponse>(
                      this, METHODID_LIST_NOTIFICATIONS)))
          .addMethod(
              getDeleteObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteObjectAccessControlRequest,
                      com.google.protobuf.Empty>(this, METHODID_DELETE_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getGetObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_GET_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getInsertObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_INSERT_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getListObjectAccessControlsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListObjectAccessControlsRequest,
                      com.google.storage.v1.ListObjectAccessControlsResponse>(
                      this, METHODID_LIST_OBJECT_ACCESS_CONTROLS)))
          .addMethod(
              getPatchObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.PatchObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_PATCH_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getUpdateObjectAccessControlMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateObjectAccessControlRequest,
                      com.google.storage.v1.ObjectAccessControl>(
                      this, METHODID_UPDATE_OBJECT_ACCESS_CONTROL)))
          .addMethod(
              getComposeObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ComposeObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_COMPOSE_OBJECT)))
          .addMethod(
              getCopyObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.CopyObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_COPY_OBJECT)))
          .addMethod(
              getDeleteObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteObjectRequest, com.google.protobuf.Empty>(
                      this, METHODID_DELETE_OBJECT)))
          .addMethod(
              getGetObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_GET_OBJECT)))
          .addMethod(
              getGetObjectMediaMethod(),
              asyncServerStreamingCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetObjectMediaRequest,
                      com.google.storage.v1.GetObjectMediaResponse>(
                      this, METHODID_GET_OBJECT_MEDIA)))
          .addMethod(
              getInsertObjectMethod(),
              asyncClientStreamingCall(
                  new MethodHandlers<
                      com.google.storage.v1.InsertObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_INSERT_OBJECT)))
          .addMethod(
              getListObjectsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListObjectsRequest,
                      com.google.storage.v1.ListObjectsResponse>(this, METHODID_LIST_OBJECTS)))
          .addMethod(
              getRewriteObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.RewriteObjectRequest,
                      com.google.storage.v1.RewriteResponse>(this, METHODID_REWRITE_OBJECT)))
          .addMethod(
              getStartResumableWriteMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.StartResumableWriteRequest,
                      com.google.storage.v1.StartResumableWriteResponse>(
                      this, METHODID_START_RESUMABLE_WRITE)))
          .addMethod(
              getQueryWriteStatusMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.QueryWriteStatusRequest,
                      com.google.storage.v1.QueryWriteStatusResponse>(
                      this, METHODID_QUERY_WRITE_STATUS)))
          .addMethod(
              getPatchObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.PatchObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_PATCH_OBJECT)))
          .addMethod(
              getUpdateObjectMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateObjectRequest, com.google.storage.v1.Object>(
                      this, METHODID_UPDATE_OBJECT)))
          .addMethod(
              getGetObjectIamPolicyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetIamPolicyRequest, com.google.iam.v1.Policy>(
                      this, METHODID_GET_OBJECT_IAM_POLICY)))
          .addMethod(
              getSetObjectIamPolicyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.SetIamPolicyRequest, com.google.iam.v1.Policy>(
                      this, METHODID_SET_OBJECT_IAM_POLICY)))
          .addMethod(
              getTestObjectIamPermissionsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.TestIamPermissionsRequest,
                      com.google.iam.v1.TestIamPermissionsResponse>(
                      this, METHODID_TEST_OBJECT_IAM_PERMISSIONS)))
          .addMethod(
              getWatchAllObjectsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.WatchAllObjectsRequest, com.google.storage.v1.Channel>(
                      this, METHODID_WATCH_ALL_OBJECTS)))
          .addMethod(
              getGetServiceAccountMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetProjectServiceAccountRequest,
                      com.google.storage.v1.ServiceAccount>(this, METHODID_GET_SERVICE_ACCOUNT)))
          .addMethod(
              getCreateHmacKeyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.CreateHmacKeyRequest,
                      com.google.storage.v1.CreateHmacKeyResponse>(this, METHODID_CREATE_HMAC_KEY)))
          .addMethod(
              getDeleteHmacKeyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.DeleteHmacKeyRequest, com.google.protobuf.Empty>(
                      this, METHODID_DELETE_HMAC_KEY)))
          .addMethod(
              getGetHmacKeyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.GetHmacKeyRequest,
                      com.google.storage.v1.HmacKeyMetadata>(this, METHODID_GET_HMAC_KEY)))
          .addMethod(
              getListHmacKeysMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.ListHmacKeysRequest,
                      com.google.storage.v1.ListHmacKeysResponse>(this, METHODID_LIST_HMAC_KEYS)))
          .addMethod(
              getUpdateHmacKeyMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.storage.v1.UpdateHmacKeyRequest,
                      com.google.storage.v1.HmacKeyMetadata>(this, METHODID_UPDATE_HMAC_KEY)))
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
     * Permanently deletes the ACL entry for the specified entity on the specified
     * bucket.
     * </pre>
     */
    public void deleteBucketAccessControl(
        com.google.storage.v1.DeleteBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteBucketAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified bucket.
     * </pre>
     */
    public void getBucketAccessControl(
        com.google.storage.v1.GetBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetBucketAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified bucket.
     * </pre>
     */
    public void insertBucketAccessControl(
        com.google.storage.v1.InsertBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInsertBucketAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified bucket.
     * </pre>
     */
    public void listBucketAccessControls(
        com.google.storage.v1.ListBucketAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketAccessControlsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBucketAccessControlsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket. Equivalent to
     * PatchBucketAccessControl, but all unspecified fields will be
     * reset to their default values.
     * </pre>
     */
    public void updateBucketAccessControl(
        com.google.storage.v1.UpdateBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateBucketAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket.
     * </pre>
     */
    public void patchBucketAccessControl(
        com.google.storage.v1.PatchBucketAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPatchBucketAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public void deleteBucket(
        com.google.storage.v1.DeleteBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteBucketMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public void getBucket(
        com.google.storage.v1.GetBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetBucketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public void insertBucket(
        com.google.storage.v1.InsertBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInsertBucketMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * List active object change notification channels for this bucket.
     * </pre>
     */
    public void listChannels(
        com.google.storage.v1.ListChannelsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListChannelsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListChannelsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public void listBuckets(
        com.google.storage.v1.ListBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBucketsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public void lockBucketRetentionPolicy(
        com.google.storage.v1.LockRetentionPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLockBucketRetentionPolicyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified bucket.
     * </pre>
     */
    public void getBucketIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetBucketIamPolicyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public void setBucketIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetBucketIamPolicyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testBucketIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTestBucketIamPermissionsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public void patchBucket(
        com.google.storage.v1.PatchBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPatchBucketMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Equivalent to PatchBucket, but always replaces all
     * mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * Like PatchBucket, Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public void updateBucket(
        com.google.storage.v1.UpdateBucketRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateBucketMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Halts "Object Change Notification" push messagages.
     * See https://cloud.google.com/storage/docs/object-change-notification
     * Note: this is not related to the newer "Notifications" resource, which
     * are stopped using DeleteNotification.
     * </pre>
     */
    public void stopChannel(
        com.google.storage.v1.StopChannelRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStopChannelMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the default object ACL entry for the specified entity
     * on the specified bucket.
     * </pre>
     */
    public void deleteDefaultObjectAccessControl(
        com.google.storage.v1.DeleteDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteDefaultObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the default object ACL entry for the specified entity on the
     * specified bucket.
     * </pre>
     */
    public void getDefaultObjectAccessControl(
        com.google.storage.v1.GetDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetDefaultObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new default object ACL entry on the specified bucket.
     * </pre>
     */
    public void insertDefaultObjectAccessControl(
        com.google.storage.v1.InsertDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInsertDefaultObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves default object ACL entries on the specified bucket.
     * </pre>
     */
    public void listDefaultObjectAccessControls(
        com.google.storage.v1.ListDefaultObjectAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListDefaultObjectAccessControlsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket.
     * </pre>
     */
    public void patchDefaultObjectAccessControl(
        com.google.storage.v1.PatchDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPatchDefaultObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket. Equivalent to
     * PatchDefaultObjectAccessControl, but modifies all unspecified fields to
     * their default values.
     * </pre>
     */
    public void updateDefaultObjectAccessControl(
        com.google.storage.v1.UpdateDefaultObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateDefaultObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes a notification subscription.
     * Note: Older, "Object Change Notification" push subscriptions should be
     * deleted using StopChannel instead.
     * </pre>
     */
    public void deleteNotification(
        com.google.storage.v1.DeleteNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteNotificationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * View a notification configuration.
     * </pre>
     */
    public void getNotification(
        com.google.storage.v1.GetNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Notification> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetNotificationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Cloud Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public void insertNotification(
        com.google.storage.v1.InsertNotificationRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Notification> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInsertNotificationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public void listNotifications(
        com.google.storage.v1.ListNotificationsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListNotificationsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListNotificationsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the ACL entry for the specified entity on the specified
     * object.
     * </pre>
     */
    public void deleteObjectAccessControl(
        com.google.storage.v1.DeleteObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified object.
     * </pre>
     */
    public void getObjectAccessControl(
        com.google.storage.v1.GetObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified object.
     * </pre>
     */
    public void insertObjectAccessControl(
        com.google.storage.v1.InsertObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInsertObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified object.
     * </pre>
     */
    public void listObjectAccessControls(
        com.google.storage.v1.ListObjectAccessControlsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListObjectAccessControlsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Patches an ACL entry on the specified object.  Patch is similar to update,
     * but only applies or appends the specified fields in the
     * object_access_control object.  Other fields are unaffected.
     * </pre>
     */
    public void patchObjectAccessControl(
        com.google.storage.v1.PatchObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPatchObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified object.
     * </pre>
     */
    public void updateObjectAccessControl(
        com.google.storage.v1.UpdateObjectAccessControlRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateObjectAccessControlMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public void composeObject(
        com.google.storage.v1.ComposeObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getComposeObjectMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Copies a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void copyObject(
        com.google.storage.v1.CopyObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCopyObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public void deleteObject(
        com.google.storage.v1.DeleteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteObjectMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public void getObject(
        com.google.storage.v1.GetObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public void getObjectMedia(
        com.google.storage.v1.GetObjectMediaRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.GetObjectMediaResponse>
            responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetObjectMediaMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Stores a new object and metadata.
     * An object can be written either in a single message stream or in a
     * resumable sequence of message streams. To write using a single stream,
     * the client should include in the first message of the stream an
     * `InsertObjectSpec` describing the destination bucket, object, and any
     * preconditions. Additionally, the final message must set 'finish_write' to
     * true, or else it is an error.
     * For a resumable write, the client should instead call
     * `StartResumableWrite()` and provide that method an `InsertObjectSpec.`
     * They should then attach the returned `upload_id` to the first message of
     * each following call to `Insert`. If there is an error or the connection is
     * broken during the resumable `Insert()`, the client should check the status
     * of the `Insert()` by calling `QueryWriteStatus()` and continue writing from
     * the returned `committed_size`. This may be less than the amount of data the
     * client previously sent.
     * The service will not view the object as complete until the client has
     * sent an `Insert` with `finish_write` set to `true`. Sending any
     * requests on a stream after sending a request with `finish_write` set to
     * `true` will cause an error. The client **should** check the
     * `Object` it receives to determine how much data the service was
     * able to commit and whether the service views the object as complete.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.storage.v1.InsertObjectRequest> insertObject(
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getInsertObjectMethod(), getCallOptions()), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public void listObjects(
        com.google.storage.v1.ListObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListObjectsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public void rewriteObject(
        com.google.storage.v1.RewriteObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.RewriteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRewriteObjectMethod(), getCallOptions()),
          request,
          responseObserver);
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
        com.google.storage.v1.StartResumableWriteRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.StartResumableWriteResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Determines the `committed_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `committed_size` values will be
     * non-decreasing.
     * </pre>
     */
    public void queryWriteStatus(
        com.google.storage.v1.QueryWriteStatusRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.QueryWriteStatusResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata.
     * </pre>
     */
    public void patchObject(
        com.google.storage.v1.PatchObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPatchObjectMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata. Equivalent to PatchObject, but always
     * replaces all mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * </pre>
     */
    public void updateObject(
        com.google.storage.v1.UpdateObjectRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Object> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateObjectMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified object.
     * </pre>
     */
    public void getObjectIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectIamPolicyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified object.
     * </pre>
     */
    public void setObjectIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.Policy> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetObjectIamPolicyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given object to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public void testObjectIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request,
        io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTestObjectIamPermissionsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Watch for changes on all objects in a bucket.
     * </pre>
     */
    public void watchAllObjects(
        com.google.storage.v1.WatchAllObjectsRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.Channel> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getWatchAllObjectsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public void getServiceAccount(
        com.google.storage.v1.GetProjectServiceAccountRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ServiceAccount> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetServiceAccountMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public void createHmacKey(
        com.google.storage.v1.CreateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.CreateHmacKeyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateHmacKeyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public void deleteHmacKey(
        com.google.storage.v1.DeleteHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteHmacKeyMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public void getHmacKey(
        com.google.storage.v1.GetHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetHmacKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public void listHmacKeys(
        com.google.storage.v1.ListHmacKeysRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.ListHmacKeysResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListHmacKeysMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public void updateHmacKey(
        com.google.storage.v1.UpdateHmacKeyRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateHmacKeyMethod(), getCallOptions()),
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
     * Permanently deletes the ACL entry for the specified entity on the specified
     * bucket.
     * </pre>
     */
    public com.google.protobuf.Empty deleteBucketAccessControl(
        com.google.storage.v1.DeleteBucketAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteBucketAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.BucketAccessControl getBucketAccessControl(
        com.google.storage.v1.GetBucketAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetBucketAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.BucketAccessControl insertBucketAccessControl(
        com.google.storage.v1.InsertBucketAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getInsertBucketAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.ListBucketAccessControlsResponse listBucketAccessControls(
        com.google.storage.v1.ListBucketAccessControlsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListBucketAccessControlsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket. Equivalent to
     * PatchBucketAccessControl, but all unspecified fields will be
     * reset to their default values.
     * </pre>
     */
    public com.google.storage.v1.BucketAccessControl updateBucketAccessControl(
        com.google.storage.v1.UpdateBucketAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateBucketAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.BucketAccessControl patchBucketAccessControl(
        com.google.storage.v1.PatchBucketAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getPatchBucketAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public com.google.protobuf.Empty deleteBucket(
        com.google.storage.v1.DeleteBucketRequest request) {
      return blockingUnaryCall(getChannel(), getDeleteBucketMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.Bucket getBucket(com.google.storage.v1.GetBucketRequest request) {
      return blockingUnaryCall(getChannel(), getGetBucketMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public com.google.storage.v1.Bucket insertBucket(
        com.google.storage.v1.InsertBucketRequest request) {
      return blockingUnaryCall(getChannel(), getInsertBucketMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * List active object change notification channels for this bucket.
     * </pre>
     */
    public com.google.storage.v1.ListChannelsResponse listChannels(
        com.google.storage.v1.ListChannelsRequest request) {
      return blockingUnaryCall(getChannel(), getListChannelsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public com.google.storage.v1.ListBucketsResponse listBuckets(
        com.google.storage.v1.ListBucketsRequest request) {
      return blockingUnaryCall(getChannel(), getListBucketsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public com.google.storage.v1.Bucket lockBucketRetentionPolicy(
        com.google.storage.v1.LockRetentionPolicyRequest request) {
      return blockingUnaryCall(
          getChannel(), getLockBucketRetentionPolicyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.iam.v1.Policy getBucketIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetBucketIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.iam.v1.Policy setBucketIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetBucketIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.iam.v1.TestIamPermissionsResponse testBucketIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getTestBucketIamPermissionsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public com.google.storage.v1.Bucket patchBucket(
        com.google.storage.v1.PatchBucketRequest request) {
      return blockingUnaryCall(getChannel(), getPatchBucketMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Equivalent to PatchBucket, but always replaces all
     * mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * Like PatchBucket, Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public com.google.storage.v1.Bucket updateBucket(
        com.google.storage.v1.UpdateBucketRequest request) {
      return blockingUnaryCall(getChannel(), getUpdateBucketMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Halts "Object Change Notification" push messagages.
     * See https://cloud.google.com/storage/docs/object-change-notification
     * Note: this is not related to the newer "Notifications" resource, which
     * are stopped using DeleteNotification.
     * </pre>
     */
    public com.google.protobuf.Empty stopChannel(com.google.storage.v1.StopChannelRequest request) {
      return blockingUnaryCall(getChannel(), getStopChannelMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the default object ACL entry for the specified entity
     * on the specified bucket.
     * </pre>
     */
    public com.google.protobuf.Empty deleteDefaultObjectAccessControl(
        com.google.storage.v1.DeleteDefaultObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteDefaultObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the default object ACL entry for the specified entity on the
     * specified bucket.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl getDefaultObjectAccessControl(
        com.google.storage.v1.GetDefaultObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetDefaultObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new default object ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl insertDefaultObjectAccessControl(
        com.google.storage.v1.InsertDefaultObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getInsertDefaultObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves default object ACL entries on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.ListObjectAccessControlsResponse listDefaultObjectAccessControls(
        com.google.storage.v1.ListDefaultObjectAccessControlsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListDefaultObjectAccessControlsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl patchDefaultObjectAccessControl(
        com.google.storage.v1.PatchDefaultObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getPatchDefaultObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket. Equivalent to
     * PatchDefaultObjectAccessControl, but modifies all unspecified fields to
     * their default values.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl updateDefaultObjectAccessControl(
        com.google.storage.v1.UpdateDefaultObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateDefaultObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes a notification subscription.
     * Note: Older, "Object Change Notification" push subscriptions should be
     * deleted using StopChannel instead.
     * </pre>
     */
    public com.google.protobuf.Empty deleteNotification(
        com.google.storage.v1.DeleteNotificationRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteNotificationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * View a notification configuration.
     * </pre>
     */
    public com.google.storage.v1.Notification getNotification(
        com.google.storage.v1.GetNotificationRequest request) {
      return blockingUnaryCall(getChannel(), getGetNotificationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Cloud Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public com.google.storage.v1.Notification insertNotification(
        com.google.storage.v1.InsertNotificationRequest request) {
      return blockingUnaryCall(
          getChannel(), getInsertNotificationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public com.google.storage.v1.ListNotificationsResponse listNotifications(
        com.google.storage.v1.ListNotificationsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListNotificationsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the ACL entry for the specified entity on the specified
     * object.
     * </pre>
     */
    public com.google.protobuf.Empty deleteObjectAccessControl(
        com.google.storage.v1.DeleteObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified object.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl getObjectAccessControl(
        com.google.storage.v1.GetObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified object.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl insertObjectAccessControl(
        com.google.storage.v1.InsertObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getInsertObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified object.
     * </pre>
     */
    public com.google.storage.v1.ListObjectAccessControlsResponse listObjectAccessControls(
        com.google.storage.v1.ListObjectAccessControlsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListObjectAccessControlsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Patches an ACL entry on the specified object.  Patch is similar to update,
     * but only applies or appends the specified fields in the
     * object_access_control object.  Other fields are unaffected.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl patchObjectAccessControl(
        com.google.storage.v1.PatchObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getPatchObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified object.
     * </pre>
     */
    public com.google.storage.v1.ObjectAccessControl updateObjectAccessControl(
        com.google.storage.v1.UpdateObjectAccessControlRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateObjectAccessControlMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public com.google.storage.v1.Object composeObject(
        com.google.storage.v1.ComposeObjectRequest request) {
      return blockingUnaryCall(getChannel(), getComposeObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Copies a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.storage.v1.Object copyObject(
        com.google.storage.v1.CopyObjectRequest request) {
      return blockingUnaryCall(getChannel(), getCopyObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public com.google.protobuf.Empty deleteObject(
        com.google.storage.v1.DeleteObjectRequest request) {
      return blockingUnaryCall(getChannel(), getDeleteObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public com.google.storage.v1.Object getObject(com.google.storage.v1.GetObjectRequest request) {
      return blockingUnaryCall(getChannel(), getGetObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Reads an object's data.
     * </pre>
     */
    public java.util.Iterator<com.google.storage.v1.GetObjectMediaResponse> getObjectMedia(
        com.google.storage.v1.GetObjectMediaRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getGetObjectMediaMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public com.google.storage.v1.ListObjectsResponse listObjects(
        com.google.storage.v1.ListObjectsRequest request) {
      return blockingUnaryCall(getChannel(), getListObjectsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.storage.v1.RewriteResponse rewriteObject(
        com.google.storage.v1.RewriteObjectRequest request) {
      return blockingUnaryCall(getChannel(), getRewriteObjectMethod(), getCallOptions(), request);
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
    public com.google.storage.v1.StartResumableWriteResponse startResumableWrite(
        com.google.storage.v1.StartResumableWriteRequest request) {
      return blockingUnaryCall(
          getChannel(), getStartResumableWriteMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Determines the `committed_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `committed_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.storage.v1.QueryWriteStatusResponse queryWriteStatus(
        com.google.storage.v1.QueryWriteStatusRequest request) {
      return blockingUnaryCall(
          getChannel(), getQueryWriteStatusMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata.
     * </pre>
     */
    public com.google.storage.v1.Object patchObject(
        com.google.storage.v1.PatchObjectRequest request) {
      return blockingUnaryCall(getChannel(), getPatchObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata. Equivalent to PatchObject, but always
     * replaces all mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * </pre>
     */
    public com.google.storage.v1.Object updateObject(
        com.google.storage.v1.UpdateObjectRequest request) {
      return blockingUnaryCall(getChannel(), getUpdateObjectMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified object.
     * </pre>
     */
    public com.google.iam.v1.Policy getObjectIamPolicy(
        com.google.storage.v1.GetIamPolicyRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified object.
     * </pre>
     */
    public com.google.iam.v1.Policy setObjectIamPolicy(
        com.google.storage.v1.SetIamPolicyRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetObjectIamPolicyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given object to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.iam.v1.TestIamPermissionsResponse testObjectIamPermissions(
        com.google.storage.v1.TestIamPermissionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getTestObjectIamPermissionsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Watch for changes on all objects in a bucket.
     * </pre>
     */
    public com.google.storage.v1.Channel watchAllObjects(
        com.google.storage.v1.WatchAllObjectsRequest request) {
      return blockingUnaryCall(getChannel(), getWatchAllObjectsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public com.google.storage.v1.ServiceAccount getServiceAccount(
        com.google.storage.v1.GetProjectServiceAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetServiceAccountMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public com.google.storage.v1.CreateHmacKeyResponse createHmacKey(
        com.google.storage.v1.CreateHmacKeyRequest request) {
      return blockingUnaryCall(getChannel(), getCreateHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public com.google.protobuf.Empty deleteHmacKey(
        com.google.storage.v1.DeleteHmacKeyRequest request) {
      return blockingUnaryCall(getChannel(), getDeleteHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public com.google.storage.v1.HmacKeyMetadata getHmacKey(
        com.google.storage.v1.GetHmacKeyRequest request) {
      return blockingUnaryCall(getChannel(), getGetHmacKeyMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public com.google.storage.v1.ListHmacKeysResponse listHmacKeys(
        com.google.storage.v1.ListHmacKeysRequest request) {
      return blockingUnaryCall(getChannel(), getListHmacKeysMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public com.google.storage.v1.HmacKeyMetadata updateHmacKey(
        com.google.storage.v1.UpdateHmacKeyRequest request) {
      return blockingUnaryCall(getChannel(), getUpdateHmacKeyMethod(), getCallOptions(), request);
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
     * Permanently deletes the ACL entry for the specified entity on the specified
     * bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteBucketAccessControl(com.google.storage.v1.DeleteBucketAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteBucketAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.BucketAccessControl>
        getBucketAccessControl(com.google.storage.v1.GetBucketAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetBucketAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.BucketAccessControl>
        insertBucketAccessControl(com.google.storage.v1.InsertBucketAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInsertBucketAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListBucketAccessControlsResponse>
        listBucketAccessControls(com.google.storage.v1.ListBucketAccessControlsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBucketAccessControlsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket. Equivalent to
     * PatchBucketAccessControl, but all unspecified fields will be
     * reset to their default values.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.BucketAccessControl>
        updateBucketAccessControl(com.google.storage.v1.UpdateBucketAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateBucketAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.BucketAccessControl>
        patchBucketAccessControl(com.google.storage.v1.PatchBucketAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPatchBucketAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteBucket(com.google.storage.v1.DeleteBucketRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteBucketMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Bucket>
        getBucket(com.google.storage.v1.GetBucketRequest request) {
      return futureUnaryCall(getChannel().newCall(getGetBucketMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Bucket>
        insertBucket(com.google.storage.v1.InsertBucketRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInsertBucketMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * List active object change notification channels for this bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListChannelsResponse>
        listChannels(com.google.storage.v1.ListChannelsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListChannelsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of buckets for a given project.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListBucketsResponse>
        listBuckets(com.google.storage.v1.ListBucketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBucketsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Locks retention policy on a bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Bucket>
        lockBucketRetentionPolicy(com.google.storage.v1.LockRetentionPolicyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLockBucketRetentionPolicyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy>
        getBucketIamPolicy(com.google.storage.v1.GetIamPolicyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetBucketIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy>
        setBucketIamPolicy(com.google.storage.v1.SetIamPolicyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetBucketIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given bucket to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.iam.v1.TestIamPermissionsResponse>
        testBucketIamPermissions(com.google.storage.v1.TestIamPermissionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTestBucketIamPermissionsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Bucket>
        patchBucket(com.google.storage.v1.PatchBucketRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPatchBucketMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a bucket. Equivalent to PatchBucket, but always replaces all
     * mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * Like PatchBucket, Changes to the bucket will be readable immediately after
     * writing, but configuration changes may take time to propagate.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Bucket>
        updateBucket(com.google.storage.v1.UpdateBucketRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateBucketMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Halts "Object Change Notification" push messagages.
     * See https://cloud.google.com/storage/docs/object-change-notification
     * Note: this is not related to the newer "Notifications" resource, which
     * are stopped using DeleteNotification.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        stopChannel(com.google.storage.v1.StopChannelRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStopChannelMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the default object ACL entry for the specified entity
     * on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteDefaultObjectAccessControl(
            com.google.storage.v1.DeleteDefaultObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteDefaultObjectAccessControlMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Returns the default object ACL entry for the specified entity on the
     * specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        getDefaultObjectAccessControl(
            com.google.storage.v1.GetDefaultObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetDefaultObjectAccessControlMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new default object ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        insertDefaultObjectAccessControl(
            com.google.storage.v1.InsertDefaultObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInsertDefaultObjectAccessControlMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves default object ACL entries on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListObjectAccessControlsResponse>
        listDefaultObjectAccessControls(
            com.google.storage.v1.ListDefaultObjectAccessControlsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListDefaultObjectAccessControlsMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        patchDefaultObjectAccessControl(
            com.google.storage.v1.PatchDefaultObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPatchDefaultObjectAccessControlMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Updates a default object ACL entry on the specified bucket. Equivalent to
     * PatchDefaultObjectAccessControl, but modifies all unspecified fields to
     * their default values.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        updateDefaultObjectAccessControl(
            com.google.storage.v1.UpdateDefaultObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateDefaultObjectAccessControlMethod(), getCallOptions()),
          request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes a notification subscription.
     * Note: Older, "Object Change Notification" push subscriptions should be
     * deleted using StopChannel instead.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteNotification(com.google.storage.v1.DeleteNotificationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteNotificationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * View a notification configuration.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Notification>
        getNotification(com.google.storage.v1.GetNotificationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetNotificationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a notification subscription for a given bucket.
     * These notifications, when triggered, publish messages to the specified
     * Cloud Pub/Sub topics.
     * See https://cloud.google.com/storage/docs/pubsub-notifications.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Notification>
        insertNotification(com.google.storage.v1.InsertNotificationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInsertNotificationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of notification subscriptions for a given bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListNotificationsResponse>
        listNotifications(com.google.storage.v1.ListNotificationsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListNotificationsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes the ACL entry for the specified entity on the specified
     * object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteObjectAccessControl(com.google.storage.v1.DeleteObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteObjectAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the ACL entry for the specified entity on the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        getObjectAccessControl(com.google.storage.v1.GetObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new ACL entry on the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        insertObjectAccessControl(com.google.storage.v1.InsertObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInsertObjectAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves ACL entries on the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListObjectAccessControlsResponse>
        listObjectAccessControls(com.google.storage.v1.ListObjectAccessControlsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListObjectAccessControlsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Patches an ACL entry on the specified object.  Patch is similar to update,
     * but only applies or appends the specified fields in the
     * object_access_control object.  Other fields are unaffected.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        patchObjectAccessControl(com.google.storage.v1.PatchObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPatchObjectAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an ACL entry on the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ObjectAccessControl>
        updateObjectAccessControl(com.google.storage.v1.UpdateObjectAccessControlRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateObjectAccessControlMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Concatenates a list of existing objects into a new object in the same
     * bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Object>
        composeObject(com.google.storage.v1.ComposeObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getComposeObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Copies a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Object>
        copyObject(com.google.storage.v1.CopyObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCopyObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes an object and its metadata. Deletions are permanent if versioning
     * is not enabled for the bucket, or if the `generation` parameter
     * is used.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteObject(com.google.storage.v1.DeleteObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves an object's metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Object>
        getObject(com.google.storage.v1.GetObjectRequest request) {
      return futureUnaryCall(getChannel().newCall(getGetObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of objects matching the criteria.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListObjectsResponse>
        listObjects(com.google.storage.v1.ListObjectsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListObjectsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Rewrites a source object to a destination object. Optionally overrides
     * metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.RewriteResponse>
        rewriteObject(com.google.storage.v1.RewriteObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRewriteObjectMethod(), getCallOptions()), request);
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
            com.google.storage.v1.StartResumableWriteResponse>
        startResumableWrite(com.google.storage.v1.StartResumableWriteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStartResumableWriteMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Determines the `committed_size` for an object that is being written, which
     * can then be used as the `write_offset` for the next `Write()` call.
     * If the object does not exist (i.e., the object has been deleted, or the
     * first `Write()` has not yet reached the service), this method returns the
     * error `NOT_FOUND`.
     * The client **may** call `QueryWriteStatus()` at any time to determine how
     * much data has been processed for this object. This is useful if the
     * client is buffering data and needs to know which data can be safely
     * evicted. For any sequence of `QueryWriteStatus()` calls for a given
     * object name, the sequence of returned `committed_size` values will be
     * non-decreasing.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.QueryWriteStatusResponse>
        queryWriteStatus(com.google.storage.v1.QueryWriteStatusRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getQueryWriteStatusMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Object>
        patchObject(com.google.storage.v1.PatchObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPatchObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an object's metadata. Equivalent to PatchObject, but always
     * replaces all mutatable fields of the bucket with new values, reverting all
     * unspecified fields to their default values.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Object>
        updateObject(com.google.storage.v1.UpdateObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateObjectMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Gets the IAM policy for the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy>
        getObjectIamPolicy(com.google.storage.v1.GetIamPolicyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates an IAM policy for the specified object.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.iam.v1.Policy>
        setObjectIamPolicy(com.google.storage.v1.SetIamPolicyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetObjectIamPolicyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Tests a set of permissions on the given object to see which, if
     * any, are held by the caller.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.iam.v1.TestIamPermissionsResponse>
        testObjectIamPermissions(com.google.storage.v1.TestIamPermissionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTestObjectIamPermissionsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Watch for changes on all objects in a bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.Channel>
        watchAllObjects(com.google.storage.v1.WatchAllObjectsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getWatchAllObjectsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves the name of a project's Google Cloud Storage service account.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.ServiceAccount>
        getServiceAccount(com.google.storage.v1.GetProjectServiceAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetServiceAccountMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new HMAC key for the given service account.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.CreateHmacKeyResponse>
        createHmacKey(com.google.storage.v1.CreateHmacKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes a given HMAC key.  Key must be in an INACTIVE state.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteHmacKey(com.google.storage.v1.DeleteHmacKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Gets an existing HMAC key metadata for the given id.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.HmacKeyMetadata>
        getHmacKey(com.google.storage.v1.GetHmacKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetHmacKeyMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Lists HMAC keys under a given project with the additional filters provided.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.v1.ListHmacKeysResponse>
        listHmacKeys(com.google.storage.v1.ListHmacKeysRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListHmacKeysMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates a given HMAC key state between ACTIVE and INACTIVE.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.v1.HmacKeyMetadata>
        updateHmacKey(com.google.storage.v1.UpdateHmacKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateHmacKeyMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DELETE_BUCKET_ACCESS_CONTROL = 0;
  private static final int METHODID_GET_BUCKET_ACCESS_CONTROL = 1;
  private static final int METHODID_INSERT_BUCKET_ACCESS_CONTROL = 2;
  private static final int METHODID_LIST_BUCKET_ACCESS_CONTROLS = 3;
  private static final int METHODID_UPDATE_BUCKET_ACCESS_CONTROL = 4;
  private static final int METHODID_PATCH_BUCKET_ACCESS_CONTROL = 5;
  private static final int METHODID_DELETE_BUCKET = 6;
  private static final int METHODID_GET_BUCKET = 7;
  private static final int METHODID_INSERT_BUCKET = 8;
  private static final int METHODID_LIST_CHANNELS = 9;
  private static final int METHODID_LIST_BUCKETS = 10;
  private static final int METHODID_LOCK_BUCKET_RETENTION_POLICY = 11;
  private static final int METHODID_GET_BUCKET_IAM_POLICY = 12;
  private static final int METHODID_SET_BUCKET_IAM_POLICY = 13;
  private static final int METHODID_TEST_BUCKET_IAM_PERMISSIONS = 14;
  private static final int METHODID_PATCH_BUCKET = 15;
  private static final int METHODID_UPDATE_BUCKET = 16;
  private static final int METHODID_STOP_CHANNEL = 17;
  private static final int METHODID_DELETE_DEFAULT_OBJECT_ACCESS_CONTROL = 18;
  private static final int METHODID_GET_DEFAULT_OBJECT_ACCESS_CONTROL = 19;
  private static final int METHODID_INSERT_DEFAULT_OBJECT_ACCESS_CONTROL = 20;
  private static final int METHODID_LIST_DEFAULT_OBJECT_ACCESS_CONTROLS = 21;
  private static final int METHODID_PATCH_DEFAULT_OBJECT_ACCESS_CONTROL = 22;
  private static final int METHODID_UPDATE_DEFAULT_OBJECT_ACCESS_CONTROL = 23;
  private static final int METHODID_DELETE_NOTIFICATION = 24;
  private static final int METHODID_GET_NOTIFICATION = 25;
  private static final int METHODID_INSERT_NOTIFICATION = 26;
  private static final int METHODID_LIST_NOTIFICATIONS = 27;
  private static final int METHODID_DELETE_OBJECT_ACCESS_CONTROL = 28;
  private static final int METHODID_GET_OBJECT_ACCESS_CONTROL = 29;
  private static final int METHODID_INSERT_OBJECT_ACCESS_CONTROL = 30;
  private static final int METHODID_LIST_OBJECT_ACCESS_CONTROLS = 31;
  private static final int METHODID_PATCH_OBJECT_ACCESS_CONTROL = 32;
  private static final int METHODID_UPDATE_OBJECT_ACCESS_CONTROL = 33;
  private static final int METHODID_COMPOSE_OBJECT = 34;
  private static final int METHODID_COPY_OBJECT = 35;
  private static final int METHODID_DELETE_OBJECT = 36;
  private static final int METHODID_GET_OBJECT = 37;
  private static final int METHODID_GET_OBJECT_MEDIA = 38;
  private static final int METHODID_LIST_OBJECTS = 39;
  private static final int METHODID_REWRITE_OBJECT = 40;
  private static final int METHODID_START_RESUMABLE_WRITE = 41;
  private static final int METHODID_QUERY_WRITE_STATUS = 42;
  private static final int METHODID_PATCH_OBJECT = 43;
  private static final int METHODID_UPDATE_OBJECT = 44;
  private static final int METHODID_GET_OBJECT_IAM_POLICY = 45;
  private static final int METHODID_SET_OBJECT_IAM_POLICY = 46;
  private static final int METHODID_TEST_OBJECT_IAM_PERMISSIONS = 47;
  private static final int METHODID_WATCH_ALL_OBJECTS = 48;
  private static final int METHODID_GET_SERVICE_ACCOUNT = 49;
  private static final int METHODID_CREATE_HMAC_KEY = 50;
  private static final int METHODID_DELETE_HMAC_KEY = 51;
  private static final int METHODID_GET_HMAC_KEY = 52;
  private static final int METHODID_LIST_HMAC_KEYS = 53;
  private static final int METHODID_UPDATE_HMAC_KEY = 54;
  private static final int METHODID_INSERT_OBJECT = 55;

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
        case METHODID_DELETE_BUCKET_ACCESS_CONTROL:
          serviceImpl.deleteBucketAccessControl(
              (com.google.storage.v1.DeleteBucketAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_BUCKET_ACCESS_CONTROL:
          serviceImpl.getBucketAccessControl(
              (com.google.storage.v1.GetBucketAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl>)
                  responseObserver);
          break;
        case METHODID_INSERT_BUCKET_ACCESS_CONTROL:
          serviceImpl.insertBucketAccessControl(
              (com.google.storage.v1.InsertBucketAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl>)
                  responseObserver);
          break;
        case METHODID_LIST_BUCKET_ACCESS_CONTROLS:
          serviceImpl.listBucketAccessControls(
              (com.google.storage.v1.ListBucketAccessControlsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketAccessControlsResponse>)
                  responseObserver);
          break;
        case METHODID_UPDATE_BUCKET_ACCESS_CONTROL:
          serviceImpl.updateBucketAccessControl(
              (com.google.storage.v1.UpdateBucketAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl>)
                  responseObserver);
          break;
        case METHODID_PATCH_BUCKET_ACCESS_CONTROL:
          serviceImpl.patchBucketAccessControl(
              (com.google.storage.v1.PatchBucketAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.BucketAccessControl>)
                  responseObserver);
          break;
        case METHODID_DELETE_BUCKET:
          serviceImpl.deleteBucket(
              (com.google.storage.v1.DeleteBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_BUCKET:
          serviceImpl.getBucket(
              (com.google.storage.v1.GetBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket>) responseObserver);
          break;
        case METHODID_INSERT_BUCKET:
          serviceImpl.insertBucket(
              (com.google.storage.v1.InsertBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket>) responseObserver);
          break;
        case METHODID_LIST_CHANNELS:
          serviceImpl.listChannels(
              (com.google.storage.v1.ListChannelsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListChannelsResponse>)
                  responseObserver);
          break;
        case METHODID_LIST_BUCKETS:
          serviceImpl.listBuckets(
              (com.google.storage.v1.ListBucketsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListBucketsResponse>)
                  responseObserver);
          break;
        case METHODID_LOCK_BUCKET_RETENTION_POLICY:
          serviceImpl.lockBucketRetentionPolicy(
              (com.google.storage.v1.LockRetentionPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket>) responseObserver);
          break;
        case METHODID_GET_BUCKET_IAM_POLICY:
          serviceImpl.getBucketIamPolicy(
              (com.google.storage.v1.GetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_SET_BUCKET_IAM_POLICY:
          serviceImpl.setBucketIamPolicy(
              (com.google.storage.v1.SetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_TEST_BUCKET_IAM_PERMISSIONS:
          serviceImpl.testBucketIamPermissions(
              (com.google.storage.v1.TestIamPermissionsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>)
                  responseObserver);
          break;
        case METHODID_PATCH_BUCKET:
          serviceImpl.patchBucket(
              (com.google.storage.v1.PatchBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket>) responseObserver);
          break;
        case METHODID_UPDATE_BUCKET:
          serviceImpl.updateBucket(
              (com.google.storage.v1.UpdateBucketRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Bucket>) responseObserver);
          break;
        case METHODID_STOP_CHANNEL:
          serviceImpl.stopChannel(
              (com.google.storage.v1.StopChannelRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_DELETE_DEFAULT_OBJECT_ACCESS_CONTROL:
          serviceImpl.deleteDefaultObjectAccessControl(
              (com.google.storage.v1.DeleteDefaultObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_DEFAULT_OBJECT_ACCESS_CONTROL:
          serviceImpl.getDefaultObjectAccessControl(
              (com.google.storage.v1.GetDefaultObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_INSERT_DEFAULT_OBJECT_ACCESS_CONTROL:
          serviceImpl.insertDefaultObjectAccessControl(
              (com.google.storage.v1.InsertDefaultObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_LIST_DEFAULT_OBJECT_ACCESS_CONTROLS:
          serviceImpl.listDefaultObjectAccessControls(
              (com.google.storage.v1.ListDefaultObjectAccessControlsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>)
                  responseObserver);
          break;
        case METHODID_PATCH_DEFAULT_OBJECT_ACCESS_CONTROL:
          serviceImpl.patchDefaultObjectAccessControl(
              (com.google.storage.v1.PatchDefaultObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_UPDATE_DEFAULT_OBJECT_ACCESS_CONTROL:
          serviceImpl.updateDefaultObjectAccessControl(
              (com.google.storage.v1.UpdateDefaultObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_DELETE_NOTIFICATION:
          serviceImpl.deleteNotification(
              (com.google.storage.v1.DeleteNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_NOTIFICATION:
          serviceImpl.getNotification(
              (com.google.storage.v1.GetNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Notification>) responseObserver);
          break;
        case METHODID_INSERT_NOTIFICATION:
          serviceImpl.insertNotification(
              (com.google.storage.v1.InsertNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Notification>) responseObserver);
          break;
        case METHODID_LIST_NOTIFICATIONS:
          serviceImpl.listNotifications(
              (com.google.storage.v1.ListNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListNotificationsResponse>)
                  responseObserver);
          break;
        case METHODID_DELETE_OBJECT_ACCESS_CONTROL:
          serviceImpl.deleteObjectAccessControl(
              (com.google.storage.v1.DeleteObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_OBJECT_ACCESS_CONTROL:
          serviceImpl.getObjectAccessControl(
              (com.google.storage.v1.GetObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_INSERT_OBJECT_ACCESS_CONTROL:
          serviceImpl.insertObjectAccessControl(
              (com.google.storage.v1.InsertObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_LIST_OBJECT_ACCESS_CONTROLS:
          serviceImpl.listObjectAccessControls(
              (com.google.storage.v1.ListObjectAccessControlsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectAccessControlsResponse>)
                  responseObserver);
          break;
        case METHODID_PATCH_OBJECT_ACCESS_CONTROL:
          serviceImpl.patchObjectAccessControl(
              (com.google.storage.v1.PatchObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_UPDATE_OBJECT_ACCESS_CONTROL:
          serviceImpl.updateObjectAccessControl(
              (com.google.storage.v1.UpdateObjectAccessControlRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ObjectAccessControl>)
                  responseObserver);
          break;
        case METHODID_COMPOSE_OBJECT:
          serviceImpl.composeObject(
              (com.google.storage.v1.ComposeObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
          break;
        case METHODID_COPY_OBJECT:
          serviceImpl.copyObject(
              (com.google.storage.v1.CopyObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
          break;
        case METHODID_DELETE_OBJECT:
          serviceImpl.deleteObject(
              (com.google.storage.v1.DeleteObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_OBJECT:
          serviceImpl.getObject(
              (com.google.storage.v1.GetObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
          break;
        case METHODID_GET_OBJECT_MEDIA:
          serviceImpl.getObjectMedia(
              (com.google.storage.v1.GetObjectMediaRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.GetObjectMediaResponse>)
                  responseObserver);
          break;
        case METHODID_LIST_OBJECTS:
          serviceImpl.listObjects(
              (com.google.storage.v1.ListObjectsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListObjectsResponse>)
                  responseObserver);
          break;
        case METHODID_REWRITE_OBJECT:
          serviceImpl.rewriteObject(
              (com.google.storage.v1.RewriteObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.RewriteResponse>)
                  responseObserver);
          break;
        case METHODID_START_RESUMABLE_WRITE:
          serviceImpl.startResumableWrite(
              (com.google.storage.v1.StartResumableWriteRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.StartResumableWriteResponse>)
                  responseObserver);
          break;
        case METHODID_QUERY_WRITE_STATUS:
          serviceImpl.queryWriteStatus(
              (com.google.storage.v1.QueryWriteStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.QueryWriteStatusResponse>)
                  responseObserver);
          break;
        case METHODID_PATCH_OBJECT:
          serviceImpl.patchObject(
              (com.google.storage.v1.PatchObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
          break;
        case METHODID_UPDATE_OBJECT:
          serviceImpl.updateObject(
              (com.google.storage.v1.UpdateObjectRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
          break;
        case METHODID_GET_OBJECT_IAM_POLICY:
          serviceImpl.getObjectIamPolicy(
              (com.google.storage.v1.GetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_SET_OBJECT_IAM_POLICY:
          serviceImpl.setObjectIamPolicy(
              (com.google.storage.v1.SetIamPolicyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.Policy>) responseObserver);
          break;
        case METHODID_TEST_OBJECT_IAM_PERMISSIONS:
          serviceImpl.testObjectIamPermissions(
              (com.google.storage.v1.TestIamPermissionsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.iam.v1.TestIamPermissionsResponse>)
                  responseObserver);
          break;
        case METHODID_WATCH_ALL_OBJECTS:
          serviceImpl.watchAllObjects(
              (com.google.storage.v1.WatchAllObjectsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.Channel>) responseObserver);
          break;
        case METHODID_GET_SERVICE_ACCOUNT:
          serviceImpl.getServiceAccount(
              (com.google.storage.v1.GetProjectServiceAccountRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ServiceAccount>) responseObserver);
          break;
        case METHODID_CREATE_HMAC_KEY:
          serviceImpl.createHmacKey(
              (com.google.storage.v1.CreateHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.CreateHmacKeyResponse>)
                  responseObserver);
          break;
        case METHODID_DELETE_HMAC_KEY:
          serviceImpl.deleteHmacKey(
              (com.google.storage.v1.DeleteHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_HMAC_KEY:
          serviceImpl.getHmacKey(
              (com.google.storage.v1.GetHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata>)
                  responseObserver);
          break;
        case METHODID_LIST_HMAC_KEYS:
          serviceImpl.listHmacKeys(
              (com.google.storage.v1.ListHmacKeysRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.ListHmacKeysResponse>)
                  responseObserver);
          break;
        case METHODID_UPDATE_HMAC_KEY:
          serviceImpl.updateHmacKey(
              (com.google.storage.v1.UpdateHmacKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.v1.HmacKeyMetadata>)
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
        case METHODID_INSERT_OBJECT:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.insertObject(
                  (io.grpc.stub.StreamObserver<com.google.storage.v1.Object>) responseObserver);
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
      return com.google.storage.v1.StorageOuterClass.getDescriptor();
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
                      .addMethod(getDeleteBucketAccessControlMethod())
                      .addMethod(getGetBucketAccessControlMethod())
                      .addMethod(getInsertBucketAccessControlMethod())
                      .addMethod(getListBucketAccessControlsMethod())
                      .addMethod(getUpdateBucketAccessControlMethod())
                      .addMethod(getPatchBucketAccessControlMethod())
                      .addMethod(getDeleteBucketMethod())
                      .addMethod(getGetBucketMethod())
                      .addMethod(getInsertBucketMethod())
                      .addMethod(getListChannelsMethod())
                      .addMethod(getListBucketsMethod())
                      .addMethod(getLockBucketRetentionPolicyMethod())
                      .addMethod(getGetBucketIamPolicyMethod())
                      .addMethod(getSetBucketIamPolicyMethod())
                      .addMethod(getTestBucketIamPermissionsMethod())
                      .addMethod(getPatchBucketMethod())
                      .addMethod(getUpdateBucketMethod())
                      .addMethod(getStopChannelMethod())
                      .addMethod(getDeleteDefaultObjectAccessControlMethod())
                      .addMethod(getGetDefaultObjectAccessControlMethod())
                      .addMethod(getInsertDefaultObjectAccessControlMethod())
                      .addMethod(getListDefaultObjectAccessControlsMethod())
                      .addMethod(getPatchDefaultObjectAccessControlMethod())
                      .addMethod(getUpdateDefaultObjectAccessControlMethod())
                      .addMethod(getDeleteNotificationMethod())
                      .addMethod(getGetNotificationMethod())
                      .addMethod(getInsertNotificationMethod())
                      .addMethod(getListNotificationsMethod())
                      .addMethod(getDeleteObjectAccessControlMethod())
                      .addMethod(getGetObjectAccessControlMethod())
                      .addMethod(getInsertObjectAccessControlMethod())
                      .addMethod(getListObjectAccessControlsMethod())
                      .addMethod(getPatchObjectAccessControlMethod())
                      .addMethod(getUpdateObjectAccessControlMethod())
                      .addMethod(getComposeObjectMethod())
                      .addMethod(getCopyObjectMethod())
                      .addMethod(getDeleteObjectMethod())
                      .addMethod(getGetObjectMethod())
                      .addMethod(getGetObjectMediaMethod())
                      .addMethod(getInsertObjectMethod())
                      .addMethod(getListObjectsMethod())
                      .addMethod(getRewriteObjectMethod())
                      .addMethod(getStartResumableWriteMethod())
                      .addMethod(getQueryWriteStatusMethod())
                      .addMethod(getPatchObjectMethod())
                      .addMethod(getUpdateObjectMethod())
                      .addMethod(getGetObjectIamPolicyMethod())
                      .addMethod(getSetObjectIamPolicyMethod())
                      .addMethod(getTestObjectIamPermissionsMethod())
                      .addMethod(getWatchAllObjectsMethod())
                      .addMethod(getGetServiceAccountMethod())
                      .addMethod(getCreateHmacKeyMethod())
                      .addMethod(getDeleteHmacKeyMethod())
                      .addMethod(getGetHmacKeyMethod())
                      .addMethod(getListHmacKeysMethod())
                      .addMethod(getUpdateHmacKeyMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
