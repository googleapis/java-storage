/*
 * Copyright 2023 Google LLC
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

package com.google.storage.control.v2.stub;

import static com.google.storage.control.v2.StorageControlClient.ListFoldersPagedResponse;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.OperationCallable;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.api.pathtemplate.PathTemplate;
import com.google.longrunning.Operation;
import com.google.longrunning.stub.GrpcOperationsStub;
import com.google.protobuf.Empty;
import com.google.storage.control.v2.CreateFolderRequest;
import com.google.storage.control.v2.DeleteFolderRequest;
import com.google.storage.control.v2.Folder;
import com.google.storage.control.v2.GetFolderRequest;
import com.google.storage.control.v2.GetStorageLayoutRequest;
import com.google.storage.control.v2.ListFoldersRequest;
import com.google.storage.control.v2.ListFoldersResponse;
import com.google.storage.control.v2.RenameFolderMetadata;
import com.google.storage.control.v2.RenameFolderRequest;
import com.google.storage.control.v2.StorageLayout;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the StorageControl service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public class GrpcStorageControlStub extends StorageControlStub {
  private static final MethodDescriptor<CreateFolderRequest, Folder> createFolderMethodDescriptor =
      MethodDescriptor.<CreateFolderRequest, Folder>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.control.v2.StorageControl/CreateFolder")
          .setRequestMarshaller(ProtoUtils.marshaller(CreateFolderRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Folder.getDefaultInstance()))
          .build();

  private static final MethodDescriptor<DeleteFolderRequest, Empty> deleteFolderMethodDescriptor =
      MethodDescriptor.<DeleteFolderRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.control.v2.StorageControl/DeleteFolder")
          .setRequestMarshaller(ProtoUtils.marshaller(DeleteFolderRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .build();

  private static final MethodDescriptor<GetFolderRequest, Folder> getFolderMethodDescriptor =
      MethodDescriptor.<GetFolderRequest, Folder>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.storage.control.v2.StorageControl/GetFolder")
          .setRequestMarshaller(ProtoUtils.marshaller(GetFolderRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Folder.getDefaultInstance()))
          .build();

  private static final MethodDescriptor<ListFoldersRequest, ListFoldersResponse>
      listFoldersMethodDescriptor =
          MethodDescriptor.<ListFoldersRequest, ListFoldersResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.control.v2.StorageControl/ListFolders")
              .setRequestMarshaller(ProtoUtils.marshaller(ListFoldersRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListFoldersResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<RenameFolderRequest, Operation>
      renameFolderMethodDescriptor =
          MethodDescriptor.<RenameFolderRequest, Operation>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.control.v2.StorageControl/RenameFolder")
              .setRequestMarshaller(ProtoUtils.marshaller(RenameFolderRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Operation.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<GetStorageLayoutRequest, StorageLayout>
      getStorageLayoutMethodDescriptor =
          MethodDescriptor.<GetStorageLayoutRequest, StorageLayout>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.storage.control.v2.StorageControl/GetStorageLayout")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetStorageLayoutRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(StorageLayout.getDefaultInstance()))
              .build();

  private final UnaryCallable<CreateFolderRequest, Folder> createFolderCallable;
  private final UnaryCallable<DeleteFolderRequest, Empty> deleteFolderCallable;
  private final UnaryCallable<GetFolderRequest, Folder> getFolderCallable;
  private final UnaryCallable<ListFoldersRequest, ListFoldersResponse> listFoldersCallable;
  private final UnaryCallable<ListFoldersRequest, ListFoldersPagedResponse>
      listFoldersPagedCallable;
  private final UnaryCallable<RenameFolderRequest, Operation> renameFolderCallable;
  private final OperationCallable<RenameFolderRequest, Folder, RenameFolderMetadata>
      renameFolderOperationCallable;
  private final UnaryCallable<GetStorageLayoutRequest, StorageLayout> getStorageLayoutCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  private static final PathTemplate CREATE_FOLDER_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=**}");
  private static final PathTemplate DELETE_FOLDER_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=projects/*/buckets/*}/**");
  private static final PathTemplate GET_FOLDER_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=projects/*/buckets/*}/**");
  private static final PathTemplate LIST_FOLDERS_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=**}");
  private static final PathTemplate RENAME_FOLDER_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=projects/*/buckets/*}/**");
  private static final PathTemplate GET_STORAGE_LAYOUT_0_PATH_TEMPLATE =
      PathTemplate.create("{bucket=projects/*/buckets/*}/**");

  public static final GrpcStorageControlStub create(StorageControlStubSettings settings)
      throws IOException {
    return new GrpcStorageControlStub(settings, ClientContext.create(settings));
  }

  public static final GrpcStorageControlStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcStorageControlStub(
        StorageControlStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcStorageControlStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcStorageControlStub(
        StorageControlStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcStorageControlStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcStorageControlStub(StorageControlStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcStorageControlCallableFactory());
  }

  /**
   * Constructs an instance of GrpcStorageControlStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcStorageControlStub(
      StorageControlStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<CreateFolderRequest, Folder> createFolderTransportSettings =
        GrpcCallSettings.<CreateFolderRequest, Folder>newBuilder()
            .setMethodDescriptor(createFolderMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getParent(), "bucket", CREATE_FOLDER_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();
    GrpcCallSettings<DeleteFolderRequest, Empty> deleteFolderTransportSettings =
        GrpcCallSettings.<DeleteFolderRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteFolderMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getName(), "bucket", DELETE_FOLDER_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetFolderRequest, Folder> getFolderTransportSettings =
        GrpcCallSettings.<GetFolderRequest, Folder>newBuilder()
            .setMethodDescriptor(getFolderMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getName(), "bucket", GET_FOLDER_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ListFoldersRequest, ListFoldersResponse> listFoldersTransportSettings =
        GrpcCallSettings.<ListFoldersRequest, ListFoldersResponse>newBuilder()
            .setMethodDescriptor(listFoldersMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getParent(), "bucket", LIST_FOLDERS_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();
    GrpcCallSettings<RenameFolderRequest, Operation> renameFolderTransportSettings =
        GrpcCallSettings.<RenameFolderRequest, Operation>newBuilder()
            .setMethodDescriptor(renameFolderMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getName(), "bucket", RENAME_FOLDER_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetStorageLayoutRequest, StorageLayout> getStorageLayoutTransportSettings =
        GrpcCallSettings.<GetStorageLayoutRequest, StorageLayout>newBuilder()
            .setMethodDescriptor(getStorageLayoutMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(request.getName(), "bucket", GET_STORAGE_LAYOUT_0_PATH_TEMPLATE);
                  return builder.build();
                })
            .build();

    this.createFolderCallable =
        callableFactory.createUnaryCallable(
            createFolderTransportSettings, settings.createFolderSettings(), clientContext);
    this.deleteFolderCallable =
        callableFactory.createUnaryCallable(
            deleteFolderTransportSettings, settings.deleteFolderSettings(), clientContext);
    this.getFolderCallable =
        callableFactory.createUnaryCallable(
            getFolderTransportSettings, settings.getFolderSettings(), clientContext);
    this.listFoldersCallable =
        callableFactory.createUnaryCallable(
            listFoldersTransportSettings, settings.listFoldersSettings(), clientContext);
    this.listFoldersPagedCallable =
        callableFactory.createPagedCallable(
            listFoldersTransportSettings, settings.listFoldersSettings(), clientContext);
    this.renameFolderCallable =
        callableFactory.createUnaryCallable(
            renameFolderTransportSettings, settings.renameFolderSettings(), clientContext);
    this.renameFolderOperationCallable =
        callableFactory.createOperationCallable(
            renameFolderTransportSettings,
            settings.renameFolderOperationSettings(),
            clientContext,
            operationsStub);
    this.getStorageLayoutCallable =
        callableFactory.createUnaryCallable(
            getStorageLayoutTransportSettings, settings.getStorageLayoutSettings(), clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public UnaryCallable<CreateFolderRequest, Folder> createFolderCallable() {
    return createFolderCallable;
  }

  @Override
  public UnaryCallable<DeleteFolderRequest, Empty> deleteFolderCallable() {
    return deleteFolderCallable;
  }

  @Override
  public UnaryCallable<GetFolderRequest, Folder> getFolderCallable() {
    return getFolderCallable;
  }

  @Override
  public UnaryCallable<ListFoldersRequest, ListFoldersResponse> listFoldersCallable() {
    return listFoldersCallable;
  }

  @Override
  public UnaryCallable<ListFoldersRequest, ListFoldersPagedResponse> listFoldersPagedCallable() {
    return listFoldersPagedCallable;
  }

  @Override
  public UnaryCallable<RenameFolderRequest, Operation> renameFolderCallable() {
    return renameFolderCallable;
  }

  @Override
  public OperationCallable<RenameFolderRequest, Folder, RenameFolderMetadata>
      renameFolderOperationCallable() {
    return renameFolderOperationCallable;
  }

  @Override
  public UnaryCallable<GetStorageLayoutRequest, StorageLayout> getStorageLayoutCallable() {
    return getStorageLayoutCallable;
  }

  @Override
  public final void close() {
    try {
      backgroundResources.close();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to close resource", e);
    }
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
