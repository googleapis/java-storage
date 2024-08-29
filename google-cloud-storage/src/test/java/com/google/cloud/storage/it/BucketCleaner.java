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

package com.google.cloud.storage.it;

import com.google.api.gax.paging.Page;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.FailedPreconditionException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.common.collect.ImmutableList;
import com.google.storage.control.v2.BucketName;
import com.google.storage.control.v2.Folder;
import com.google.storage.control.v2.StorageControlClient;
import com.google.storage.control.v2.StorageLayout;
import com.google.storage.control.v2.StorageLayoutName;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class BucketCleaner {

  private static final Logger LOGGER = Logger.getLogger(BucketCleaner.class.getName());

  public static void doCleanup(String bucketName, Storage s) {
    LOGGER.fine("Starting bucket cleanup...");
    String projectId = s.getOptions().getProjectId();
    try {
      // TODO: probe bucket existence, a bad test could have deleted the bucket
      Page<Blob> page1 =
          s.list(bucketName, BlobListOption.userProject(projectId), BlobListOption.versions(true));

      List<DeleteResult> deleteResults =
          StreamSupport.stream(page1.iterateAll().spliterator(), false)
              .map(
                  b ->
                      new DeleteResult(
                          b.getName(),
                          s.delete(b.getBlobId(), BlobSourceOption.userProject(projectId))))
              .collect(Collectors.toList());
      boolean anyFailedObjectDeletes = getIfAnyFailedAndReport(bucketName, deleteResults, "object");

      if (!anyFailedObjectDeletes) {
        s.delete(bucketName, BucketSourceOption.userProject(projectId));
      } else {
        LOGGER.warning("Unable to delete bucket due to previous failed object deletes");
      }
      LOGGER.fine("Bucket cleanup complete");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e, () -> "Error during bucket cleanup.");
    }
  }

  public static void doCleanup(String bucketName, Storage s, StorageControlClient ctrl) {
    LOGGER.warning("Starting bucket cleanup: " + bucketName);
    String projectId = s.getOptions().getProjectId();
    try {
      // TODO: probe bucket existence, a bad test could have deleted the bucket
      Page<Blob> page1 =
          s.list(
              bucketName,
              BlobListOption.userProject(projectId),
              BlobListOption.versions(true),
              BlobListOption.fields(BlobField.NAME));

      List<DeleteResult> objectResults =
          StreamSupport.stream(page1.iterateAll().spliterator(), false)
              .map(
                  b ->
                      new DeleteResult(
                          b.getName(),
                          s.delete(b.getBlobId(), BlobSourceOption.userProject(projectId))))
              .collect(Collectors.toList());
      boolean anyFailedObjectDelete = getIfAnyFailedAndReport(bucketName, objectResults, "object");
      boolean anyFailedFolderDelete = false;
      boolean anyFailedManagedFolderDelete = false;

      if (!anyFailedObjectDelete) {
        BucketName parent = BucketName.of("_", bucketName);
        StorageLayout storageLayout =
            ctrl.getStorageLayout(StorageLayoutName.of(parent.getProject(), parent.getBucket()));
        List<DeleteResult> folderDeletes;
        if (storageLayout.hasHierarchicalNamespace()
            && storageLayout.getHierarchicalNamespace().getEnabled()) {
          folderDeletes =
              StreamSupport.stream(ctrl.listFolders(parent).iterateAll().spliterator(), false)
                  .collect(Collectors.toList())
                  .stream()
                  .sorted(Collections.reverseOrder(Comparator.comparing(Folder::getName)))
                  .map(
                      folder -> {
                        LOGGER.warning(String.format("folder = %s", folder.getName()));
                        boolean success = true;
                        try {
                          ctrl.deleteFolder(folder.getName());
                        } catch (ApiException e) {
                          success = false;
                        }
                        return new DeleteResult(folder.getName(), success);
                      })
                  .collect(Collectors.toList());
        } else {
          folderDeletes = ImmutableList.of();
        }

        List<DeleteResult> managedFolderDeletes;
        try {
          managedFolderDeletes =
              StreamSupport.stream(
                      ctrl.listManagedFolders(parent).iterateAll().spliterator(), false)
                  .map(
                      managedFolder -> {
                        LOGGER.warning(
                            String.format("managedFolder = %s", managedFolder.getName()));
                        boolean success = true;
                        try {
                          ctrl.deleteFolder(managedFolder.getName());
                        } catch (ApiException e) {
                          success = false;
                        }
                        return new DeleteResult(managedFolder.getName(), success);
                      })
                  .collect(Collectors.toList());
        } catch (FailedPreconditionException fpe) {
          // FAILED_PRECONDITION: Uniform bucket-level access is required to be enabled on the
          //   bucket in order to perform this operation. Read more at
          //   https://cloud.google.com/storage/docs/uniform-bucket-level-access
          managedFolderDeletes = ImmutableList.of();
        }

        anyFailedFolderDelete = getIfAnyFailedAndReport(bucketName, folderDeletes, "folder");
        anyFailedManagedFolderDelete =
            getIfAnyFailedAndReport(bucketName, managedFolderDeletes, "managed folder");
      }

      List<String> failed =
          Stream.of(
                  anyFailedObjectDelete ? "object" : "",
                  anyFailedFolderDelete ? "folder" : "",
                  anyFailedManagedFolderDelete ? "managed folder" : "")
              .filter(ss -> !ss.isEmpty())
              .collect(Collectors.toList());

      if (!anyFailedObjectDelete && !anyFailedFolderDelete && !anyFailedManagedFolderDelete) {
        s.delete(bucketName, BucketSourceOption.userProject(projectId));
      } else {
        LOGGER.warning(
            String.format(
                "Unable to delete bucket %s due to previous failed %s deletes",
                bucketName, failed));
      }

      LOGGER.warning("Bucket cleanup complete: " + bucketName);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e, () -> "Error during bucket cleanup.");
    }
  }

  private static boolean getIfAnyFailedAndReport(
      String bucketName, List<DeleteResult> deleteResults, String resourceType) {
    List<DeleteResult> failedDeletes =
        deleteResults.stream().filter(r -> !r.success).collect(Collectors.toList());
    failedDeletes.forEach(
        r ->
            LOGGER.warning(
                String.format("Failed to delete %s %s/%s", resourceType, bucketName, r.name)));
    return !failedDeletes.isEmpty();
  }

  private static final class DeleteResult {
    private final String name;
    private final boolean success;

    DeleteResult(String name, boolean success) {
      this.name = name;
      this.success = success;
    }
  }
}
