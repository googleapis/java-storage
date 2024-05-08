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

package com.google.storage.control.v2;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.paging.AbstractFixedSizeCollection;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.AbstractPagedListResponse;
import com.google.api.gax.rpc.OperationCallable;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.longrunning.Operation;
import com.google.longrunning.OperationsClient;
import com.google.protobuf.Empty;
import com.google.storage.control.v2.stub.StorageControlStub;
import com.google.storage.control.v2.stub.StorageControlStubSettings;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: StorageControl service includes selected control plane operations.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
 *   BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
 *   Folder folder = Folder.newBuilder().build();
 *   String folderId = "folderId294109737";
 *   Folder response = storageControlClient.createFolder(parent, folder, folderId);
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the StorageControlClient object to clean up resources such
 * as threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <table>
 *    <caption>Methods</caption>
 *    <tr>
 *      <th>Method</th>
 *      <th>Description</th>
 *      <th>Method Variants</th>
 *    </tr>
 *    <tr>
 *      <td><p> CreateFolder</td>
 *      <td><p> Creates a new folder. This operation is only applicable to a hierarchical namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> createFolder(CreateFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> createFolder(BucketName parent, Folder folder, String folderId)
 *           <li><p> createFolder(String parent, Folder folder, String folderId)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> createFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> DeleteFolder</td>
 *      <td><p> Permanently deletes an empty folder. This operation is only applicable to a hierarchical namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> deleteFolder(DeleteFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> deleteFolder(FolderName name)
 *           <li><p> deleteFolder(String name)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> deleteFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> GetFolder</td>
 *      <td><p> Returns metadata for the specified folder. This operation is only applicable to a hierarchical namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> getFolder(GetFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> getFolder(FolderName name)
 *           <li><p> getFolder(String name)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> getFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> ListFolders</td>
 *      <td><p> Retrieves a list of folders. This operation is only applicable to a hierarchical namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> listFolders(ListFoldersRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> listFolders(BucketName parent)
 *           <li><p> listFolders(String parent)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> listFoldersPagedCallable()
 *           <li><p> listFoldersCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> RenameFolder</td>
 *      <td><p> Renames a source folder to a destination folder. This operation is only applicable to a hierarchical namespace enabled bucket. During a rename, the source and destination folders are locked until the long running operation completes. Hierarchical namespace buckets are in allowlist preview.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> renameFolderAsync(RenameFolderRequest request)
 *      </ul>
 *      <p>Methods that return long-running operations have "Async" method variants that return `OperationFuture`, which is used to track polling of the service.</p>
 *      <ul>
 *           <li><p> renameFolderAsync(FolderName name, String destinationFolderId)
 *           <li><p> renameFolderAsync(String name, String destinationFolderId)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> renameFolderOperationCallable()
 *           <li><p> renameFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> GetStorageLayout</td>
 *      <td><p> Returns the storage layout configuration for a given bucket.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> getStorageLayout(GetStorageLayoutRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> getStorageLayout(StorageLayoutName name)
 *           <li><p> getStorageLayout(String name)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> getStorageLayoutCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> CreateManagedFolder</td>
 *      <td><p> Creates a new managed folder.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> createManagedFolder(CreateManagedFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> createManagedFolder(BucketName parent, ManagedFolder managedFolder, String managedFolderId)
 *           <li><p> createManagedFolder(String parent, ManagedFolder managedFolder, String managedFolderId)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> createManagedFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> DeleteManagedFolder</td>
 *      <td><p> Permanently deletes an empty managed folder.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> deleteManagedFolder(DeleteManagedFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> deleteManagedFolder(ManagedFolderName name)
 *           <li><p> deleteManagedFolder(String name)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> deleteManagedFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> GetManagedFolder</td>
 *      <td><p> Returns metadata for the specified managed folder.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> getManagedFolder(GetManagedFolderRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> getManagedFolder(ManagedFolderName name)
 *           <li><p> getManagedFolder(String name)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> getManagedFolderCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> ListManagedFolders</td>
 *      <td><p> Retrieves a list of managed folders for a given bucket.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> listManagedFolders(ListManagedFoldersRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> listManagedFolders(BucketName parent)
 *           <li><p> listManagedFolders(String parent)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> listManagedFoldersPagedCallable()
 *           <li><p> listManagedFoldersCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *  </table>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of StorageControlSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * StorageControlSettings storageControlSettings =
 *     StorageControlSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * StorageControlClient storageControlClient = StorageControlClient.create(storageControlSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * StorageControlSettings storageControlSettings =
 *     StorageControlSettings.newBuilder().setEndpoint(myEndpoint).build();
 * StorageControlClient storageControlClient = StorageControlClient.create(storageControlSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class StorageControlClient implements BackgroundResource {
  private final StorageControlSettings settings;
  private final StorageControlStub stub;
  private final OperationsClient operationsClient;

  /** Constructs an instance of StorageControlClient with default settings. */
  public static final StorageControlClient create() throws IOException {
    return create(StorageControlSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of StorageControlClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final StorageControlClient create(StorageControlSettings settings)
      throws IOException {
    return new StorageControlClient(settings);
  }

  /**
   * Constructs an instance of StorageControlClient, using the given stub for making calls. This is
   * for advanced usage - prefer using create(StorageControlSettings).
   */
  public static final StorageControlClient create(StorageControlStub stub) {
    return new StorageControlClient(stub);
  }

  /**
   * Constructs an instance of StorageControlClient, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected StorageControlClient(StorageControlSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((StorageControlStubSettings) settings.getStubSettings()).createStub();
    this.operationsClient = OperationsClient.create(this.stub.getOperationsStub());
  }

  protected StorageControlClient(StorageControlStub stub) {
    this.settings = null;
    this.stub = stub;
    this.operationsClient = OperationsClient.create(this.stub.getOperationsStub());
  }

  public final StorageControlSettings getSettings() {
    return settings;
  }

  public StorageControlStub getStub() {
    return stub;
  }

  /**
   * Returns the OperationsClient that can be used to query the status of a long-running operation
   * returned by another API method call.
   */
  public final OperationsClient getOperationsClient() {
    return operationsClient;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new folder. This operation is only applicable to a hierarchical namespace enabled
   * bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
   *   Folder folder = Folder.newBuilder().build();
   *   String folderId = "folderId294109737";
   *   Folder response = storageControlClient.createFolder(parent, folder, folderId);
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket in which the folder will reside. The bucket must be
   *     a hierarchical namespace enabled bucket.
   * @param folder Required. Properties of the new folder being created. The bucket and name of the
   *     folder are specified in the parent and folder_id fields, respectively. Populating those
   *     fields in `folder` will result in an error.
   * @param folderId Required. The full name of a folder, including all its parent folders. Folders
   *     use single '/' characters as a delimiter. The folder_id must end with a slash. For example,
   *     the folder_id of "books/biographies/" would create a new "biographies/" folder under the
   *     "books/" folder.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder createFolder(BucketName parent, Folder folder, String folderId) {
    CreateFolderRequest request =
        CreateFolderRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .setFolder(folder)
            .setFolderId(folderId)
            .build();
    return createFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new folder. This operation is only applicable to a hierarchical namespace enabled
   * bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String parent = BucketName.of("[PROJECT]", "[BUCKET]").toString();
   *   Folder folder = Folder.newBuilder().build();
   *   String folderId = "folderId294109737";
   *   Folder response = storageControlClient.createFolder(parent, folder, folderId);
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket in which the folder will reside. The bucket must be
   *     a hierarchical namespace enabled bucket.
   * @param folder Required. Properties of the new folder being created. The bucket and name of the
   *     folder are specified in the parent and folder_id fields, respectively. Populating those
   *     fields in `folder` will result in an error.
   * @param folderId Required. The full name of a folder, including all its parent folders. Folders
   *     use single '/' characters as a delimiter. The folder_id must end with a slash. For example,
   *     the folder_id of "books/biographies/" would create a new "biographies/" folder under the
   *     "books/" folder.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder createFolder(String parent, Folder folder, String folderId) {
    CreateFolderRequest request =
        CreateFolderRequest.newBuilder()
            .setParent(parent)
            .setFolder(folder)
            .setFolderId(folderId)
            .build();
    return createFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new folder. This operation is only applicable to a hierarchical namespace enabled
   * bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   CreateFolderRequest request =
   *       CreateFolderRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setFolder(Folder.newBuilder().build())
   *           .setFolderId("folderId294109737")
   *           .setRecursive(true)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   Folder response = storageControlClient.createFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder createFolder(CreateFolderRequest request) {
    return createFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new folder. This operation is only applicable to a hierarchical namespace enabled
   * bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   CreateFolderRequest request =
   *       CreateFolderRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setFolder(Folder.newBuilder().build())
   *           .setFolderId("folderId294109737")
   *           .setRecursive(true)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Folder> future = storageControlClient.createFolderCallable().futureCall(request);
   *   // Do something.
   *   Folder response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CreateFolderRequest, Folder> createFolderCallable() {
    return stub.createFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
   *   storageControlClient.deleteFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the folder. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteFolder(FolderName name) {
    DeleteFolderRequest request =
        DeleteFolderRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    deleteFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString();
   *   storageControlClient.deleteFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the folder. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteFolder(String name) {
    DeleteFolderRequest request = DeleteFolderRequest.newBuilder().setName(name).build();
    deleteFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   DeleteFolderRequest request =
   *       DeleteFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   storageControlClient.deleteFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteFolder(DeleteFolderRequest request) {
    deleteFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   DeleteFolderRequest request =
   *       DeleteFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Empty> future = storageControlClient.deleteFolderCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteFolderRequest, Empty> deleteFolderCallable() {
    return stub.deleteFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
   *   Folder response = storageControlClient.getFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the folder. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder getFolder(FolderName name) {
    GetFolderRequest request =
        GetFolderRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    return getFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString();
   *   Folder response = storageControlClient.getFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the folder. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder getFolder(String name) {
    GetFolderRequest request = GetFolderRequest.newBuilder().setName(name).build();
    return getFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetFolderRequest request =
   *       GetFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   Folder response = storageControlClient.getFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Folder getFolder(GetFolderRequest request) {
    return getFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified folder. This operation is only applicable to a hierarchical
   * namespace enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetFolderRequest request =
   *       GetFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Folder> future = storageControlClient.getFolderCallable().futureCall(request);
   *   // Do something.
   *   Folder response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetFolderRequest, Folder> getFolderCallable() {
    return stub.getFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of folders. This operation is only applicable to a hierarchical namespace
   * enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
   *   for (Folder element : storageControlClient.listFolders(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket in which to look for folders. The bucket must be a
   *     hierarchical namespace enabled bucket.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListFoldersPagedResponse listFolders(BucketName parent) {
    ListFoldersRequest request =
        ListFoldersRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .build();
    return listFolders(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of folders. This operation is only applicable to a hierarchical namespace
   * enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String parent = BucketName.of("[PROJECT]", "[BUCKET]").toString();
   *   for (Folder element : storageControlClient.listFolders(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket in which to look for folders. The bucket must be a
   *     hierarchical namespace enabled bucket.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListFoldersPagedResponse listFolders(String parent) {
    ListFoldersRequest request = ListFoldersRequest.newBuilder().setParent(parent).build();
    return listFolders(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of folders. This operation is only applicable to a hierarchical namespace
   * enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListFoldersRequest request =
   *       ListFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setDelimiter("delimiter-250518009")
   *           .setLexicographicStart("lexicographicStart-2093413008")
   *           .setLexicographicEnd("lexicographicEnd1646968169")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   for (Folder element : storageControlClient.listFolders(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListFoldersPagedResponse listFolders(ListFoldersRequest request) {
    return listFoldersPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of folders. This operation is only applicable to a hierarchical namespace
   * enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListFoldersRequest request =
   *       ListFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setDelimiter("delimiter-250518009")
   *           .setLexicographicStart("lexicographicStart-2093413008")
   *           .setLexicographicEnd("lexicographicEnd1646968169")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Folder> future =
   *       storageControlClient.listFoldersPagedCallable().futureCall(request);
   *   // Do something.
   *   for (Folder element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListFoldersRequest, ListFoldersPagedResponse>
      listFoldersPagedCallable() {
    return stub.listFoldersPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of folders. This operation is only applicable to a hierarchical namespace
   * enabled bucket. Hierarchical namespace buckets are in allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListFoldersRequest request =
   *       ListFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setDelimiter("delimiter-250518009")
   *           .setLexicographicStart("lexicographicStart-2093413008")
   *           .setLexicographicEnd("lexicographicEnd1646968169")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   while (true) {
   *     ListFoldersResponse response = storageControlClient.listFoldersCallable().call(request);
   *     for (Folder element : response.getFoldersList()) {
   *       // doThingsWith(element);
   *     }
   *     String nextPageToken = response.getNextPageToken();
   *     if (!Strings.isNullOrEmpty(nextPageToken)) {
   *       request = request.toBuilder().setPageToken(nextPageToken).build();
   *     } else {
   *       break;
   *     }
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListFoldersRequest, ListFoldersResponse> listFoldersCallable() {
    return stub.listFoldersCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Renames a source folder to a destination folder. This operation is only applicable to a
   * hierarchical namespace enabled bucket. During a rename, the source and destination folders are
   * locked until the long running operation completes. Hierarchical namespace buckets are in
   * allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   FolderName name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]");
   *   String destinationFolderId = "destinationFolderId-480084905";
   *   Folder response = storageControlClient.renameFolderAsync(name, destinationFolderId).get();
   * }
   * }</pre>
   *
   * @param name Required. Name of the source folder being renamed. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @param destinationFolderId Required. The destination folder ID, e.g. `foo/bar/`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final OperationFuture<Folder, RenameFolderMetadata> renameFolderAsync(
      FolderName name, String destinationFolderId) {
    RenameFolderRequest request =
        RenameFolderRequest.newBuilder()
            .setName(name == null ? null : name.toString())
            .setDestinationFolderId(destinationFolderId)
            .build();
    return renameFolderAsync(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Renames a source folder to a destination folder. This operation is only applicable to a
   * hierarchical namespace enabled bucket. During a rename, the source and destination folders are
   * locked until the long running operation completes. Hierarchical namespace buckets are in
   * allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString();
   *   String destinationFolderId = "destinationFolderId-480084905";
   *   Folder response = storageControlClient.renameFolderAsync(name, destinationFolderId).get();
   * }
   * }</pre>
   *
   * @param name Required. Name of the source folder being renamed. Format:
   *     `projects/{project}/buckets/{bucket}/folders/{folder}`
   * @param destinationFolderId Required. The destination folder ID, e.g. `foo/bar/`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final OperationFuture<Folder, RenameFolderMetadata> renameFolderAsync(
      String name, String destinationFolderId) {
    RenameFolderRequest request =
        RenameFolderRequest.newBuilder()
            .setName(name)
            .setDestinationFolderId(destinationFolderId)
            .build();
    return renameFolderAsync(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Renames a source folder to a destination folder. This operation is only applicable to a
   * hierarchical namespace enabled bucket. During a rename, the source and destination folders are
   * locked until the long running operation completes. Hierarchical namespace buckets are in
   * allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   RenameFolderRequest request =
   *       RenameFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setDestinationFolderId("destinationFolderId-480084905")
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   Folder response = storageControlClient.renameFolderAsync(request).get();
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final OperationFuture<Folder, RenameFolderMetadata> renameFolderAsync(
      RenameFolderRequest request) {
    return renameFolderOperationCallable().futureCall(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Renames a source folder to a destination folder. This operation is only applicable to a
   * hierarchical namespace enabled bucket. During a rename, the source and destination folders are
   * locked until the long running operation completes. Hierarchical namespace buckets are in
   * allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   RenameFolderRequest request =
   *       RenameFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setDestinationFolderId("destinationFolderId-480084905")
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   OperationFuture<Folder, RenameFolderMetadata> future =
   *       storageControlClient.renameFolderOperationCallable().futureCall(request);
   *   // Do something.
   *   Folder response = future.get();
   * }
   * }</pre>
   */
  public final OperationCallable<RenameFolderRequest, Folder, RenameFolderMetadata>
      renameFolderOperationCallable() {
    return stub.renameFolderOperationCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Renames a source folder to a destination folder. This operation is only applicable to a
   * hierarchical namespace enabled bucket. During a rename, the source and destination folders are
   * locked until the long running operation completes. Hierarchical namespace buckets are in
   * allowlist preview.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   RenameFolderRequest request =
   *       RenameFolderRequest.newBuilder()
   *           .setName(FolderName.of("[PROJECT]", "[BUCKET]", "[FOLDER]").toString())
   *           .setDestinationFolderId("destinationFolderId-480084905")
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Operation> future = storageControlClient.renameFolderCallable().futureCall(request);
   *   // Do something.
   *   Operation response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<RenameFolderRequest, Operation> renameFolderCallable() {
    return stub.renameFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the storage layout configuration for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   StorageLayoutName name = StorageLayoutName.of("[PROJECT]", "[BUCKET]");
   *   StorageLayout response = storageControlClient.getStorageLayout(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the StorageLayout resource. Format:
   *     `projects/{project}/buckets/{bucket}/storageLayout`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final StorageLayout getStorageLayout(StorageLayoutName name) {
    GetStorageLayoutRequest request =
        GetStorageLayoutRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    return getStorageLayout(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the storage layout configuration for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = StorageLayoutName.of("[PROJECT]", "[BUCKET]").toString();
   *   StorageLayout response = storageControlClient.getStorageLayout(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the StorageLayout resource. Format:
   *     `projects/{project}/buckets/{bucket}/storageLayout`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final StorageLayout getStorageLayout(String name) {
    GetStorageLayoutRequest request = GetStorageLayoutRequest.newBuilder().setName(name).build();
    return getStorageLayout(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the storage layout configuration for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetStorageLayoutRequest request =
   *       GetStorageLayoutRequest.newBuilder()
   *           .setName(StorageLayoutName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPrefix("prefix-980110702")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   StorageLayout response = storageControlClient.getStorageLayout(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final StorageLayout getStorageLayout(GetStorageLayoutRequest request) {
    return getStorageLayoutCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the storage layout configuration for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetStorageLayoutRequest request =
   *       GetStorageLayoutRequest.newBuilder()
   *           .setName(StorageLayoutName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPrefix("prefix-980110702")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<StorageLayout> future =
   *       storageControlClient.getStorageLayoutCallable().futureCall(request);
   *   // Do something.
   *   StorageLayout response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetStorageLayoutRequest, StorageLayout> getStorageLayoutCallable() {
    return stub.getStorageLayoutCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
   *   ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
   *   String managedFolderId = "managedFolderId-2027084056";
   *   ManagedFolder response =
   *       storageControlClient.createManagedFolder(parent, managedFolder, managedFolderId);
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket this managed folder belongs to.
   * @param managedFolder Required. Properties of the managed folder being created. The bucket and
   *     managed folder names are specified in the `parent` and `managed_folder_id` fields.
   *     Populating these fields in `managed_folder` will result in an error.
   * @param managedFolderId Required. The name of the managed folder. It uses a single `/` as
   *     delimiter and leading and trailing `/` are allowed.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder createManagedFolder(
      BucketName parent, ManagedFolder managedFolder, String managedFolderId) {
    CreateManagedFolderRequest request =
        CreateManagedFolderRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .setManagedFolder(managedFolder)
            .setManagedFolderId(managedFolderId)
            .build();
    return createManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String parent = BucketName.of("[PROJECT]", "[BUCKET]").toString();
   *   ManagedFolder managedFolder = ManagedFolder.newBuilder().build();
   *   String managedFolderId = "managedFolderId-2027084056";
   *   ManagedFolder response =
   *       storageControlClient.createManagedFolder(parent, managedFolder, managedFolderId);
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket this managed folder belongs to.
   * @param managedFolder Required. Properties of the managed folder being created. The bucket and
   *     managed folder names are specified in the `parent` and `managed_folder_id` fields.
   *     Populating these fields in `managed_folder` will result in an error.
   * @param managedFolderId Required. The name of the managed folder. It uses a single `/` as
   *     delimiter and leading and trailing `/` are allowed.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder createManagedFolder(
      String parent, ManagedFolder managedFolder, String managedFolderId) {
    CreateManagedFolderRequest request =
        CreateManagedFolderRequest.newBuilder()
            .setParent(parent)
            .setManagedFolder(managedFolder)
            .setManagedFolderId(managedFolderId)
            .build();
    return createManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   CreateManagedFolderRequest request =
   *       CreateManagedFolderRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setManagedFolder(ManagedFolder.newBuilder().build())
   *           .setManagedFolderId("managedFolderId-2027084056")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ManagedFolder response = storageControlClient.createManagedFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder createManagedFolder(CreateManagedFolderRequest request) {
    return createManagedFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   CreateManagedFolderRequest request =
   *       CreateManagedFolderRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setManagedFolder(ManagedFolder.newBuilder().build())
   *           .setManagedFolderId("managedFolderId-2027084056")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<ManagedFolder> future =
   *       storageControlClient.createManagedFolderCallable().futureCall(request);
   *   // Do something.
   *   ManagedFolder response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CreateManagedFolderRequest, ManagedFolder>
      createManagedFolderCallable() {
    return stub.createManagedFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");
   *   storageControlClient.deleteManagedFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the managed folder. Format:
   *     `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteManagedFolder(ManagedFolderName name) {
    DeleteManagedFolderRequest request =
        DeleteManagedFolderRequest.newBuilder()
            .setName(name == null ? null : name.toString())
            .build();
    deleteManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString();
   *   storageControlClient.deleteManagedFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the managed folder. Format:
   *     `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteManagedFolder(String name) {
    DeleteManagedFolderRequest request =
        DeleteManagedFolderRequest.newBuilder().setName(name).build();
    deleteManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   DeleteManagedFolderRequest request =
   *       DeleteManagedFolderRequest.newBuilder()
   *           .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setAllowNonEmpty(true)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   storageControlClient.deleteManagedFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteManagedFolder(DeleteManagedFolderRequest request) {
    deleteManagedFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Permanently deletes an empty managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   DeleteManagedFolderRequest request =
   *       DeleteManagedFolderRequest.newBuilder()
   *           .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setAllowNonEmpty(true)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<Empty> future =
   *       storageControlClient.deleteManagedFolderCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteManagedFolderRequest, Empty> deleteManagedFolderCallable() {
    return stub.deleteManagedFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ManagedFolderName name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]");
   *   ManagedFolder response = storageControlClient.getManagedFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the managed folder. Format:
   *     `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder getManagedFolder(ManagedFolderName name) {
    GetManagedFolderRequest request =
        GetManagedFolderRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    return getManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String name = ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString();
   *   ManagedFolder response = storageControlClient.getManagedFolder(name);
   * }
   * }</pre>
   *
   * @param name Required. Name of the managed folder. Format:
   *     `projects/{project}/buckets/{bucket}/managedFolders/{managedFolder}`
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder getManagedFolder(String name) {
    GetManagedFolderRequest request = GetManagedFolderRequest.newBuilder().setName(name).build();
    return getManagedFolder(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetManagedFolderRequest request =
   *       GetManagedFolderRequest.newBuilder()
   *           .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ManagedFolder response = storageControlClient.getManagedFolder(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ManagedFolder getManagedFolder(GetManagedFolderRequest request) {
    return getManagedFolderCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns metadata for the specified managed folder.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   GetManagedFolderRequest request =
   *       GetManagedFolderRequest.newBuilder()
   *           .setName(ManagedFolderName.of("[PROJECT]", "[BUCKET]", "[MANAGED_FOLDER]").toString())
   *           .setIfMetagenerationMatch(1043427781)
   *           .setIfMetagenerationNotMatch(1025430873)
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<ManagedFolder> future =
   *       storageControlClient.getManagedFolderCallable().futureCall(request);
   *   // Do something.
   *   ManagedFolder response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetManagedFolderRequest, ManagedFolder> getManagedFolderCallable() {
    return stub.getManagedFolderCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of managed folders for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   BucketName parent = BucketName.of("[PROJECT]", "[BUCKET]");
   *   for (ManagedFolder element : storageControlClient.listManagedFolders(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket this managed folder belongs to.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListManagedFoldersPagedResponse listManagedFolders(BucketName parent) {
    ListManagedFoldersRequest request =
        ListManagedFoldersRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .build();
    return listManagedFolders(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of managed folders for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   String parent = BucketName.of("[PROJECT]", "[BUCKET]").toString();
   *   for (ManagedFolder element : storageControlClient.listManagedFolders(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. Name of the bucket this managed folder belongs to.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListManagedFoldersPagedResponse listManagedFolders(String parent) {
    ListManagedFoldersRequest request =
        ListManagedFoldersRequest.newBuilder().setParent(parent).build();
    return listManagedFolders(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of managed folders for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListManagedFoldersRequest request =
   *       ListManagedFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   for (ManagedFolder element : storageControlClient.listManagedFolders(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListManagedFoldersPagedResponse listManagedFolders(
      ListManagedFoldersRequest request) {
    return listManagedFoldersPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of managed folders for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListManagedFoldersRequest request =
   *       ListManagedFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   ApiFuture<ManagedFolder> future =
   *       storageControlClient.listManagedFoldersPagedCallable().futureCall(request);
   *   // Do something.
   *   for (ManagedFolder element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListManagedFoldersRequest, ListManagedFoldersPagedResponse>
      listManagedFoldersPagedCallable() {
    return stub.listManagedFoldersPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Retrieves a list of managed folders for a given bucket.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (StorageControlClient storageControlClient = StorageControlClient.create()) {
   *   ListManagedFoldersRequest request =
   *       ListManagedFoldersRequest.newBuilder()
   *           .setParent(BucketName.of("[PROJECT]", "[BUCKET]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .setPrefix("prefix-980110702")
   *           .setRequestId("requestId693933066")
   *           .build();
   *   while (true) {
   *     ListManagedFoldersResponse response =
   *         storageControlClient.listManagedFoldersCallable().call(request);
   *     for (ManagedFolder element : response.getManagedFoldersList()) {
   *       // doThingsWith(element);
   *     }
   *     String nextPageToken = response.getNextPageToken();
   *     if (!Strings.isNullOrEmpty(nextPageToken)) {
   *       request = request.toBuilder().setPageToken(nextPageToken).build();
   *     } else {
   *       break;
   *     }
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListManagedFoldersRequest, ListManagedFoldersResponse>
      listManagedFoldersCallable() {
    return stub.listManagedFoldersCallable();
  }

  @Override
  public final void close() {
    stub.close();
  }

  @Override
  public void shutdown() {
    stub.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return stub.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return stub.isTerminated();
  }

  @Override
  public void shutdownNow() {
    stub.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return stub.awaitTermination(duration, unit);
  }

  public static class ListFoldersPagedResponse
      extends AbstractPagedListResponse<
          ListFoldersRequest,
          ListFoldersResponse,
          Folder,
          ListFoldersPage,
          ListFoldersFixedSizeCollection> {

    public static ApiFuture<ListFoldersPagedResponse> createAsync(
        PageContext<ListFoldersRequest, ListFoldersResponse, Folder> context,
        ApiFuture<ListFoldersResponse> futureResponse) {
      ApiFuture<ListFoldersPage> futurePage =
          ListFoldersPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage, input -> new ListFoldersPagedResponse(input), MoreExecutors.directExecutor());
    }

    private ListFoldersPagedResponse(ListFoldersPage page) {
      super(page, ListFoldersFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListFoldersPage
      extends AbstractPage<ListFoldersRequest, ListFoldersResponse, Folder, ListFoldersPage> {

    private ListFoldersPage(
        PageContext<ListFoldersRequest, ListFoldersResponse, Folder> context,
        ListFoldersResponse response) {
      super(context, response);
    }

    private static ListFoldersPage createEmptyPage() {
      return new ListFoldersPage(null, null);
    }

    @Override
    protected ListFoldersPage createPage(
        PageContext<ListFoldersRequest, ListFoldersResponse, Folder> context,
        ListFoldersResponse response) {
      return new ListFoldersPage(context, response);
    }

    @Override
    public ApiFuture<ListFoldersPage> createPageAsync(
        PageContext<ListFoldersRequest, ListFoldersResponse, Folder> context,
        ApiFuture<ListFoldersResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListFoldersFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListFoldersRequest,
          ListFoldersResponse,
          Folder,
          ListFoldersPage,
          ListFoldersFixedSizeCollection> {

    private ListFoldersFixedSizeCollection(List<ListFoldersPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListFoldersFixedSizeCollection createEmptyCollection() {
      return new ListFoldersFixedSizeCollection(null, 0);
    }

    @Override
    protected ListFoldersFixedSizeCollection createCollection(
        List<ListFoldersPage> pages, int collectionSize) {
      return new ListFoldersFixedSizeCollection(pages, collectionSize);
    }
  }

  public static class ListManagedFoldersPagedResponse
      extends AbstractPagedListResponse<
          ListManagedFoldersRequest,
          ListManagedFoldersResponse,
          ManagedFolder,
          ListManagedFoldersPage,
          ListManagedFoldersFixedSizeCollection> {

    public static ApiFuture<ListManagedFoldersPagedResponse> createAsync(
        PageContext<ListManagedFoldersRequest, ListManagedFoldersResponse, ManagedFolder> context,
        ApiFuture<ListManagedFoldersResponse> futureResponse) {
      ApiFuture<ListManagedFoldersPage> futurePage =
          ListManagedFoldersPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage,
          input -> new ListManagedFoldersPagedResponse(input),
          MoreExecutors.directExecutor());
    }

    private ListManagedFoldersPagedResponse(ListManagedFoldersPage page) {
      super(page, ListManagedFoldersFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListManagedFoldersPage
      extends AbstractPage<
          ListManagedFoldersRequest,
          ListManagedFoldersResponse,
          ManagedFolder,
          ListManagedFoldersPage> {

    private ListManagedFoldersPage(
        PageContext<ListManagedFoldersRequest, ListManagedFoldersResponse, ManagedFolder> context,
        ListManagedFoldersResponse response) {
      super(context, response);
    }

    private static ListManagedFoldersPage createEmptyPage() {
      return new ListManagedFoldersPage(null, null);
    }

    @Override
    protected ListManagedFoldersPage createPage(
        PageContext<ListManagedFoldersRequest, ListManagedFoldersResponse, ManagedFolder> context,
        ListManagedFoldersResponse response) {
      return new ListManagedFoldersPage(context, response);
    }

    @Override
    public ApiFuture<ListManagedFoldersPage> createPageAsync(
        PageContext<ListManagedFoldersRequest, ListManagedFoldersResponse, ManagedFolder> context,
        ApiFuture<ListManagedFoldersResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListManagedFoldersFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListManagedFoldersRequest,
          ListManagedFoldersResponse,
          ManagedFolder,
          ListManagedFoldersPage,
          ListManagedFoldersFixedSizeCollection> {

    private ListManagedFoldersFixedSizeCollection(
        List<ListManagedFoldersPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListManagedFoldersFixedSizeCollection createEmptyCollection() {
      return new ListManagedFoldersFixedSizeCollection(null, 0);
    }

    @Override
    protected ListManagedFoldersFixedSizeCollection createCollection(
        List<ListManagedFoldersPage> pages, int collectionSize) {
      return new ListManagedFoldersFixedSizeCollection(pages, collectionSize);
    }
  }
}
