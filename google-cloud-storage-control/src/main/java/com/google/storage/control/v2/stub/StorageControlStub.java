/*
 * Copyright 2024 Google LLC
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
import static com.google.storage.control.v2.StorageControlClient.ListManagedFoldersPagedResponse;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.OperationCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.longrunning.Operation;
import com.google.longrunning.stub.OperationsStub;
import com.google.protobuf.Empty;
import com.google.storage.control.v2.CreateFolderRequest;
import com.google.storage.control.v2.CreateManagedFolderRequest;
import com.google.storage.control.v2.DeleteFolderRequest;
import com.google.storage.control.v2.DeleteManagedFolderRequest;
import com.google.storage.control.v2.Folder;
import com.google.storage.control.v2.GetFolderRequest;
import com.google.storage.control.v2.GetManagedFolderRequest;
import com.google.storage.control.v2.GetStorageLayoutRequest;
import com.google.storage.control.v2.ListFoldersRequest;
import com.google.storage.control.v2.ListFoldersResponse;
import com.google.storage.control.v2.ListManagedFoldersRequest;
import com.google.storage.control.v2.ListManagedFoldersResponse;
import com.google.storage.control.v2.ManagedFolder;
import com.google.storage.control.v2.RenameFolderMetadata;
import com.google.storage.control.v2.RenameFolderRequest;
import com.google.storage.control.v2.StorageLayout;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the StorageControl service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class StorageControlStub implements BackgroundResource {

  public OperationsStub getOperationsStub() {
    throw new UnsupportedOperationException("Not implemented: getOperationsStub()");
  }

  public UnaryCallable<CreateFolderRequest, Folder> createFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: createFolderCallable()");
  }

  public UnaryCallable<DeleteFolderRequest, Empty> deleteFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteFolderCallable()");
  }

  public UnaryCallable<GetFolderRequest, Folder> getFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: getFolderCallable()");
  }

  public UnaryCallable<ListFoldersRequest, ListFoldersPagedResponse> listFoldersPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listFoldersPagedCallable()");
  }

  public UnaryCallable<ListFoldersRequest, ListFoldersResponse> listFoldersCallable() {
    throw new UnsupportedOperationException("Not implemented: listFoldersCallable()");
  }

  public OperationCallable<RenameFolderRequest, Folder, RenameFolderMetadata>
      renameFolderOperationCallable() {
    throw new UnsupportedOperationException("Not implemented: renameFolderOperationCallable()");
  }

  public UnaryCallable<RenameFolderRequest, Operation> renameFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: renameFolderCallable()");
  }

  public UnaryCallable<GetStorageLayoutRequest, StorageLayout> getStorageLayoutCallable() {
    throw new UnsupportedOperationException("Not implemented: getStorageLayoutCallable()");
  }

  public UnaryCallable<CreateManagedFolderRequest, ManagedFolder> createManagedFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: createManagedFolderCallable()");
  }

  public UnaryCallable<DeleteManagedFolderRequest, Empty> deleteManagedFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteManagedFolderCallable()");
  }

  public UnaryCallable<GetManagedFolderRequest, ManagedFolder> getManagedFolderCallable() {
    throw new UnsupportedOperationException("Not implemented: getManagedFolderCallable()");
  }

  public UnaryCallable<ListManagedFoldersRequest, ListManagedFoldersPagedResponse>
      listManagedFoldersPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listManagedFoldersPagedCallable()");
  }

  public UnaryCallable<ListManagedFoldersRequest, ListManagedFoldersResponse>
      listManagedFoldersCallable() {
    throw new UnsupportedOperationException("Not implemented: listManagedFoldersCallable()");
  }

  @Override
  public abstract void close();
}
