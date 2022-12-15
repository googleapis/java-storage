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
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
      List<DeleteResult> failedDeletes =
          deleteResults.stream().filter(r -> !r.success).collect(Collectors.toList());
      failedDeletes.forEach(
          r -> LOGGER.warning(String.format("Failed to delete object %s/%s", bucketName, r.name)));

      if (failedDeletes.isEmpty()) {
        s.delete(bucketName, BucketSourceOption.userProject(projectId));
      } else {
        LOGGER.warning("Unable to delete bucket due to previous failed object deletes");
      }
      LOGGER.fine("Bucket cleanup complete");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e, () -> "Error during bucket cleanup.");
    }
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
