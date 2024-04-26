/*
 * Copyright 2024 Google LLC
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
package com.google.storage.control.v2;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 *
 *
 * <pre>
 * StorageControl service includes selected control plane operations.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/storage/control/v2/storage_control.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StorageControlGrpc {

  private StorageControlGrpc() {}

  public static final java.lang.String SERVICE_NAME = "google.storage.control.v2.StorageControl";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.CreateFolderRequest, com.google.storage.control.v2.Folder>
      getCreateFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateFolder",
      requestType = com.google.storage.control.v2.CreateFolderRequest.class,
      responseType = com.google.storage.control.v2.Folder.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.CreateFolderRequest, com.google.storage.control.v2.Folder>
      getCreateFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.CreateFolderRequest, com.google.storage.control.v2.Folder>
        getCreateFolderMethod;
    if ((getCreateFolderMethod = StorageControlGrpc.getCreateFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getCreateFolderMethod = StorageControlGrpc.getCreateFolderMethod) == null) {
          StorageControlGrpc.getCreateFolderMethod =
              getCreateFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.CreateFolderRequest,
                          com.google.storage.control.v2.Folder>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.CreateFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.Folder.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("CreateFolder"))
                      .build();
        }
      }
    }
    return getCreateFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.DeleteFolderRequest, com.google.protobuf.Empty>
      getDeleteFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteFolder",
      requestType = com.google.storage.control.v2.DeleteFolderRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.DeleteFolderRequest, com.google.protobuf.Empty>
      getDeleteFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.DeleteFolderRequest, com.google.protobuf.Empty>
        getDeleteFolderMethod;
    if ((getDeleteFolderMethod = StorageControlGrpc.getDeleteFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getDeleteFolderMethod = StorageControlGrpc.getDeleteFolderMethod) == null) {
          StorageControlGrpc.getDeleteFolderMethod =
              getDeleteFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.DeleteFolderRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.DeleteFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("DeleteFolder"))
                      .build();
        }
      }
    }
    return getDeleteFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetFolderRequest, com.google.storage.control.v2.Folder>
      getGetFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFolder",
      requestType = com.google.storage.control.v2.GetFolderRequest.class,
      responseType = com.google.storage.control.v2.Folder.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetFolderRequest, com.google.storage.control.v2.Folder>
      getGetFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.GetFolderRequest, com.google.storage.control.v2.Folder>
        getGetFolderMethod;
    if ((getGetFolderMethod = StorageControlGrpc.getGetFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getGetFolderMethod = StorageControlGrpc.getGetFolderMethod) == null) {
          StorageControlGrpc.getGetFolderMethod =
              getGetFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.GetFolderRequest,
                          com.google.storage.control.v2.Folder>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.GetFolderRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.Folder.getDefaultInstance()))
                      .setSchemaDescriptor(new StorageControlMethodDescriptorSupplier("GetFolder"))
                      .build();
        }
      }
    }
    return getGetFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.ListFoldersRequest,
          com.google.storage.control.v2.ListFoldersResponse>
      getListFoldersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListFolders",
      requestType = com.google.storage.control.v2.ListFoldersRequest.class,
      responseType = com.google.storage.control.v2.ListFoldersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.ListFoldersRequest,
          com.google.storage.control.v2.ListFoldersResponse>
      getListFoldersMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.ListFoldersRequest,
            com.google.storage.control.v2.ListFoldersResponse>
        getListFoldersMethod;
    if ((getListFoldersMethod = StorageControlGrpc.getListFoldersMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getListFoldersMethod = StorageControlGrpc.getListFoldersMethod) == null) {
          StorageControlGrpc.getListFoldersMethod =
              getListFoldersMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.ListFoldersRequest,
                          com.google.storage.control.v2.ListFoldersResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListFolders"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ListFoldersRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ListFoldersResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("ListFolders"))
                      .build();
        }
      }
    }
    return getListFoldersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.RenameFolderRequest, com.google.longrunning.Operation>
      getRenameFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RenameFolder",
      requestType = com.google.storage.control.v2.RenameFolderRequest.class,
      responseType = com.google.longrunning.Operation.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.RenameFolderRequest, com.google.longrunning.Operation>
      getRenameFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.RenameFolderRequest, com.google.longrunning.Operation>
        getRenameFolderMethod;
    if ((getRenameFolderMethod = StorageControlGrpc.getRenameFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getRenameFolderMethod = StorageControlGrpc.getRenameFolderMethod) == null) {
          StorageControlGrpc.getRenameFolderMethod =
              getRenameFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.RenameFolderRequest,
                          com.google.longrunning.Operation>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RenameFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.RenameFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.longrunning.Operation.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("RenameFolder"))
                      .build();
        }
      }
    }
    return getRenameFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetStorageLayoutRequest,
          com.google.storage.control.v2.StorageLayout>
      getGetStorageLayoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStorageLayout",
      requestType = com.google.storage.control.v2.GetStorageLayoutRequest.class,
      responseType = com.google.storage.control.v2.StorageLayout.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetStorageLayoutRequest,
          com.google.storage.control.v2.StorageLayout>
      getGetStorageLayoutMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.GetStorageLayoutRequest,
            com.google.storage.control.v2.StorageLayout>
        getGetStorageLayoutMethod;
    if ((getGetStorageLayoutMethod = StorageControlGrpc.getGetStorageLayoutMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getGetStorageLayoutMethod = StorageControlGrpc.getGetStorageLayoutMethod) == null) {
          StorageControlGrpc.getGetStorageLayoutMethod =
              getGetStorageLayoutMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.GetStorageLayoutRequest,
                          com.google.storage.control.v2.StorageLayout>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStorageLayout"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.GetStorageLayoutRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.StorageLayout.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("GetStorageLayout"))
                      .build();
        }
      }
    }
    return getGetStorageLayoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.CreateManagedFolderRequest,
          com.google.storage.control.v2.ManagedFolder>
      getCreateManagedFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateManagedFolder",
      requestType = com.google.storage.control.v2.CreateManagedFolderRequest.class,
      responseType = com.google.storage.control.v2.ManagedFolder.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.CreateManagedFolderRequest,
          com.google.storage.control.v2.ManagedFolder>
      getCreateManagedFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.CreateManagedFolderRequest,
            com.google.storage.control.v2.ManagedFolder>
        getCreateManagedFolderMethod;
    if ((getCreateManagedFolderMethod = StorageControlGrpc.getCreateManagedFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getCreateManagedFolderMethod = StorageControlGrpc.getCreateManagedFolderMethod)
            == null) {
          StorageControlGrpc.getCreateManagedFolderMethod =
              getCreateManagedFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.CreateManagedFolderRequest,
                          com.google.storage.control.v2.ManagedFolder>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "CreateManagedFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.CreateManagedFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ManagedFolder.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("CreateManagedFolder"))
                      .build();
        }
      }
    }
    return getCreateManagedFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.DeleteManagedFolderRequest, com.google.protobuf.Empty>
      getDeleteManagedFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteManagedFolder",
      requestType = com.google.storage.control.v2.DeleteManagedFolderRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.DeleteManagedFolderRequest, com.google.protobuf.Empty>
      getDeleteManagedFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.DeleteManagedFolderRequest, com.google.protobuf.Empty>
        getDeleteManagedFolderMethod;
    if ((getDeleteManagedFolderMethod = StorageControlGrpc.getDeleteManagedFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getDeleteManagedFolderMethod = StorageControlGrpc.getDeleteManagedFolderMethod)
            == null) {
          StorageControlGrpc.getDeleteManagedFolderMethod =
              getDeleteManagedFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.DeleteManagedFolderRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "DeleteManagedFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.DeleteManagedFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("DeleteManagedFolder"))
                      .build();
        }
      }
    }
    return getDeleteManagedFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetManagedFolderRequest,
          com.google.storage.control.v2.ManagedFolder>
      getGetManagedFolderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetManagedFolder",
      requestType = com.google.storage.control.v2.GetManagedFolderRequest.class,
      responseType = com.google.storage.control.v2.ManagedFolder.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.GetManagedFolderRequest,
          com.google.storage.control.v2.ManagedFolder>
      getGetManagedFolderMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.GetManagedFolderRequest,
            com.google.storage.control.v2.ManagedFolder>
        getGetManagedFolderMethod;
    if ((getGetManagedFolderMethod = StorageControlGrpc.getGetManagedFolderMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getGetManagedFolderMethod = StorageControlGrpc.getGetManagedFolderMethod) == null) {
          StorageControlGrpc.getGetManagedFolderMethod =
              getGetManagedFolderMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.GetManagedFolderRequest,
                          com.google.storage.control.v2.ManagedFolder>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetManagedFolder"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.GetManagedFolderRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ManagedFolder.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("GetManagedFolder"))
                      .build();
        }
      }
    }
    return getGetManagedFolderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.storage.control.v2.ListManagedFoldersRequest,
          com.google.storage.control.v2.ListManagedFoldersResponse>
      getListManagedFoldersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListManagedFolders",
      requestType = com.google.storage.control.v2.ListManagedFoldersRequest.class,
      responseType = com.google.storage.control.v2.ListManagedFoldersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.storage.control.v2.ListManagedFoldersRequest,
          com.google.storage.control.v2.ListManagedFoldersResponse>
      getListManagedFoldersMethod() {
    io.grpc.MethodDescriptor<
            com.google.storage.control.v2.ListManagedFoldersRequest,
            com.google.storage.control.v2.ListManagedFoldersResponse>
        getListManagedFoldersMethod;
    if ((getListManagedFoldersMethod = StorageControlGrpc.getListManagedFoldersMethod) == null) {
      synchronized (StorageControlGrpc.class) {
        if ((getListManagedFoldersMethod = StorageControlGrpc.getListManagedFoldersMethod)
            == null) {
          StorageControlGrpc.getListManagedFoldersMethod =
              getListManagedFoldersMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.storage.control.v2.ListManagedFoldersRequest,
                          com.google.storage.control.v2.ListManagedFoldersResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListManagedFolders"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ListManagedFoldersRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.storage.control.v2.ListManagedFoldersResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new StorageControlMethodDescriptorSupplier("ListManagedFolders"))
                      .build();
        }
      }
    }
    return getListManagedFoldersMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static StorageControlStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageControlStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageControlStub>() {
          @java.lang.Override
          public StorageControlStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageControlStub(channel, callOptions);
          }
        };
    return StorageControlStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StorageControlBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageControlBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageControlBlockingStub>() {
          @java.lang.Override
          public StorageControlBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageControlBlockingStub(channel, callOptions);
          }
        };
    return StorageControlBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static StorageControlFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageControlFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<StorageControlFutureStub>() {
          @java.lang.Override
          public StorageControlFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new StorageControlFutureStub(channel, callOptions);
          }
        };
    return StorageControlFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * StorageControl service includes selected control plane operations.
   * </pre>
   */
  public interface AsyncService {

    /**
     *
     *
     * <pre>
     * Creates a new folder. This operation is only applicable to a hierarchical
     * namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    default void createFolder(
        com.google.storage.control.v2.CreateFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getCreateFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty folder. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    default void deleteFolder(
        com.google.storage.control.v2.DeleteFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getDeleteFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    default void getFolder(
        com.google.storage.control.v2.GetFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of folders. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    default void listFolders(
        com.google.storage.control.v2.ListFoldersRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ListFoldersResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListFoldersMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Renames a source folder to a destination folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket. During a rename, the
     * source and destination folders are locked until the long running operation
     * completes.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    default void renameFolder(
        com.google.storage.control.v2.RenameFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getRenameFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the storage layout configuration for a given bucket.
     * </pre>
     */
    default void getStorageLayout(
        com.google.storage.control.v2.GetStorageLayoutRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.StorageLayout> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getGetStorageLayoutMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new managed folder.
     * </pre>
     */
    default void createManagedFolder(
        com.google.storage.control.v2.CreateManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getCreateManagedFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty managed folder.
     * </pre>
     */
    default void deleteManagedFolder(
        com.google.storage.control.v2.DeleteManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getDeleteManagedFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified managed folder.
     * </pre>
     */
    default void getManagedFolder(
        com.google.storage.control.v2.GetManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getGetManagedFolderMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of managed folders for a given bucket.
     * </pre>
     */
    default void listManagedFolders(
        com.google.storage.control.v2.ListManagedFoldersRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ListManagedFoldersResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListManagedFoldersMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service StorageControl.
   *
   * <pre>
   * StorageControl service includes selected control plane operations.
   * </pre>
   */
  public abstract static class StorageControlImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return StorageControlGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service StorageControl.
   *
   * <pre>
   * StorageControl service includes selected control plane operations.
   * </pre>
   */
  public static final class StorageControlStub
      extends io.grpc.stub.AbstractAsyncStub<StorageControlStub> {
    private StorageControlStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageControlStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageControlStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new folder. This operation is only applicable to a hierarchical
     * namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public void createFolder(
        com.google.storage.control.v2.CreateFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty folder. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public void deleteFolder(
        com.google.storage.control.v2.DeleteFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public void getFolder(
        com.google.storage.control.v2.GetFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetFolderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of folders. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public void listFolders(
        com.google.storage.control.v2.ListFoldersRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ListFoldersResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListFoldersMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Renames a source folder to a destination folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket. During a rename, the
     * source and destination folders are locked until the long running operation
     * completes.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public void renameFolder(
        com.google.storage.control.v2.RenameFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRenameFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the storage layout configuration for a given bucket.
     * </pre>
     */
    public void getStorageLayout(
        com.google.storage.control.v2.GetStorageLayoutRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.StorageLayout> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStorageLayoutMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new managed folder.
     * </pre>
     */
    public void createManagedFolder(
        com.google.storage.control.v2.CreateManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateManagedFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty managed folder.
     * </pre>
     */
    public void deleteManagedFolder(
        com.google.storage.control.v2.DeleteManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteManagedFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified managed folder.
     * </pre>
     */
    public void getManagedFolder(
        com.google.storage.control.v2.GetManagedFolderRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetManagedFolderMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of managed folders for a given bucket.
     * </pre>
     */
    public void listManagedFolders(
        com.google.storage.control.v2.ListManagedFoldersRequest request,
        io.grpc.stub.StreamObserver<com.google.storage.control.v2.ListManagedFoldersResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListManagedFoldersMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service StorageControl.
   *
   * <pre>
   * StorageControl service includes selected control plane operations.
   * </pre>
   */
  public static final class StorageControlBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<StorageControlBlockingStub> {
    private StorageControlBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageControlBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageControlBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new folder. This operation is only applicable to a hierarchical
     * namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.storage.control.v2.Folder createFolder(
        com.google.storage.control.v2.CreateFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty folder. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.protobuf.Empty deleteFolder(
        com.google.storage.control.v2.DeleteFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.storage.control.v2.Folder getFolder(
        com.google.storage.control.v2.GetFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of folders. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.storage.control.v2.ListFoldersResponse listFolders(
        com.google.storage.control.v2.ListFoldersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListFoldersMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Renames a source folder to a destination folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket. During a rename, the
     * source and destination folders are locked until the long running operation
     * completes.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.longrunning.Operation renameFolder(
        com.google.storage.control.v2.RenameFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRenameFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the storage layout configuration for a given bucket.
     * </pre>
     */
    public com.google.storage.control.v2.StorageLayout getStorageLayout(
        com.google.storage.control.v2.GetStorageLayoutRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStorageLayoutMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new managed folder.
     * </pre>
     */
    public com.google.storage.control.v2.ManagedFolder createManagedFolder(
        com.google.storage.control.v2.CreateManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateManagedFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty managed folder.
     * </pre>
     */
    public com.google.protobuf.Empty deleteManagedFolder(
        com.google.storage.control.v2.DeleteManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteManagedFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified managed folder.
     * </pre>
     */
    public com.google.storage.control.v2.ManagedFolder getManagedFolder(
        com.google.storage.control.v2.GetManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetManagedFolderMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of managed folders for a given bucket.
     * </pre>
     */
    public com.google.storage.control.v2.ListManagedFoldersResponse listManagedFolders(
        com.google.storage.control.v2.ListManagedFoldersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListManagedFoldersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service StorageControl.
   *
   * <pre>
   * StorageControl service includes selected control plane operations.
   * </pre>
   */
  public static final class StorageControlFutureStub
      extends io.grpc.stub.AbstractFutureStub<StorageControlFutureStub> {
    private StorageControlFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageControlFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageControlFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new folder. This operation is only applicable to a hierarchical
     * namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.control.v2.Folder>
        createFolder(com.google.storage.control.v2.CreateFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty folder. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteFolder(com.google.storage.control.v2.DeleteFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.storage.control.v2.Folder>
        getFolder(com.google.storage.control.v2.GetFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of folders. This operation is only applicable to a
     * hierarchical namespace enabled bucket.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.control.v2.ListFoldersResponse>
        listFolders(com.google.storage.control.v2.ListFoldersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListFoldersMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Renames a source folder to a destination folder. This operation is only
     * applicable to a hierarchical namespace enabled bucket. During a rename, the
     * source and destination folders are locked until the long running operation
     * completes.
     * Hierarchical namespace buckets are in allowlist preview.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation>
        renameFolder(com.google.storage.control.v2.RenameFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRenameFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the storage layout configuration for a given bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.control.v2.StorageLayout>
        getStorageLayout(com.google.storage.control.v2.GetStorageLayoutRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStorageLayoutMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new managed folder.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.control.v2.ManagedFolder>
        createManagedFolder(com.google.storage.control.v2.CreateManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateManagedFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Permanently deletes an empty managed folder.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteManagedFolder(com.google.storage.control.v2.DeleteManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteManagedFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns metadata for the specified managed folder.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.control.v2.ManagedFolder>
        getManagedFolder(com.google.storage.control.v2.GetManagedFolderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetManagedFolderMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Retrieves a list of managed folders for a given bucket.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.storage.control.v2.ListManagedFoldersResponse>
        listManagedFolders(com.google.storage.control.v2.ListManagedFoldersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListManagedFoldersMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_FOLDER = 0;
  private static final int METHODID_DELETE_FOLDER = 1;
  private static final int METHODID_GET_FOLDER = 2;
  private static final int METHODID_LIST_FOLDERS = 3;
  private static final int METHODID_RENAME_FOLDER = 4;
  private static final int METHODID_GET_STORAGE_LAYOUT = 5;
  private static final int METHODID_CREATE_MANAGED_FOLDER = 6;
  private static final int METHODID_DELETE_MANAGED_FOLDER = 7;
  private static final int METHODID_GET_MANAGED_FOLDER = 8;
  private static final int METHODID_LIST_MANAGED_FOLDERS = 9;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_FOLDER:
          serviceImpl.createFolder(
              (com.google.storage.control.v2.CreateFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder>) responseObserver);
          break;
        case METHODID_DELETE_FOLDER:
          serviceImpl.deleteFolder(
              (com.google.storage.control.v2.DeleteFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_FOLDER:
          serviceImpl.getFolder(
              (com.google.storage.control.v2.GetFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.Folder>) responseObserver);
          break;
        case METHODID_LIST_FOLDERS:
          serviceImpl.listFolders(
              (com.google.storage.control.v2.ListFoldersRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.ListFoldersResponse>)
                  responseObserver);
          break;
        case METHODID_RENAME_FOLDER:
          serviceImpl.renameFolder(
              (com.google.storage.control.v2.RenameFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.longrunning.Operation>) responseObserver);
          break;
        case METHODID_GET_STORAGE_LAYOUT:
          serviceImpl.getStorageLayout(
              (com.google.storage.control.v2.GetStorageLayoutRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.StorageLayout>)
                  responseObserver);
          break;
        case METHODID_CREATE_MANAGED_FOLDER:
          serviceImpl.createManagedFolder(
              (com.google.storage.control.v2.CreateManagedFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder>)
                  responseObserver);
          break;
        case METHODID_DELETE_MANAGED_FOLDER:
          serviceImpl.deleteManagedFolder(
              (com.google.storage.control.v2.DeleteManagedFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_MANAGED_FOLDER:
          serviceImpl.getManagedFolder(
              (com.google.storage.control.v2.GetManagedFolderRequest) request,
              (io.grpc.stub.StreamObserver<com.google.storage.control.v2.ManagedFolder>)
                  responseObserver);
          break;
        case METHODID_LIST_MANAGED_FOLDERS:
          serviceImpl.listManagedFolders(
              (com.google.storage.control.v2.ListManagedFoldersRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.storage.control.v2.ListManagedFoldersResponse>)
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
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
            getCreateFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.CreateFolderRequest,
                    com.google.storage.control.v2.Folder>(service, METHODID_CREATE_FOLDER)))
        .addMethod(
            getDeleteFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.DeleteFolderRequest, com.google.protobuf.Empty>(
                    service, METHODID_DELETE_FOLDER)))
        .addMethod(
            getGetFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.GetFolderRequest,
                    com.google.storage.control.v2.Folder>(service, METHODID_GET_FOLDER)))
        .addMethod(
            getListFoldersMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.ListFoldersRequest,
                    com.google.storage.control.v2.ListFoldersResponse>(
                    service, METHODID_LIST_FOLDERS)))
        .addMethod(
            getRenameFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.RenameFolderRequest,
                    com.google.longrunning.Operation>(service, METHODID_RENAME_FOLDER)))
        .addMethod(
            getGetStorageLayoutMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.GetStorageLayoutRequest,
                    com.google.storage.control.v2.StorageLayout>(
                    service, METHODID_GET_STORAGE_LAYOUT)))
        .addMethod(
            getCreateManagedFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.CreateManagedFolderRequest,
                    com.google.storage.control.v2.ManagedFolder>(
                    service, METHODID_CREATE_MANAGED_FOLDER)))
        .addMethod(
            getDeleteManagedFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.DeleteManagedFolderRequest,
                    com.google.protobuf.Empty>(service, METHODID_DELETE_MANAGED_FOLDER)))
        .addMethod(
            getGetManagedFolderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.GetManagedFolderRequest,
                    com.google.storage.control.v2.ManagedFolder>(
                    service, METHODID_GET_MANAGED_FOLDER)))
        .addMethod(
            getListManagedFoldersMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.storage.control.v2.ListManagedFoldersRequest,
                    com.google.storage.control.v2.ListManagedFoldersResponse>(
                    service, METHODID_LIST_MANAGED_FOLDERS)))
        .build();
  }

  private abstract static class StorageControlBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StorageControlBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.storage.control.v2.StorageControlProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StorageControl");
    }
  }

  private static final class StorageControlFileDescriptorSupplier
      extends StorageControlBaseDescriptorSupplier {
    StorageControlFileDescriptorSupplier() {}
  }

  private static final class StorageControlMethodDescriptorSupplier
      extends StorageControlBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    StorageControlMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (StorageControlGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new StorageControlFileDescriptorSupplier())
                      .addMethod(getCreateFolderMethod())
                      .addMethod(getDeleteFolderMethod())
                      .addMethod(getGetFolderMethod())
                      .addMethod(getListFoldersMethod())
                      .addMethod(getRenameFolderMethod())
                      .addMethod(getGetStorageLayoutMethod())
                      .addMethod(getCreateManagedFolderMethod())
                      .addMethod(getDeleteManagedFolderMethod())
                      .addMethod(getGetManagedFolderMethod())
                      .addMethod(getListManagedFoldersMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
