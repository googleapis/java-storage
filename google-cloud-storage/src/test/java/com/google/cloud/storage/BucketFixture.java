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

package com.google.cloud.storage;

import static java.util.Objects.requireNonNull;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.conformance.retry.CleanupStrategy;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public final class BucketFixture implements TestRule {

  private static final Logger LOGGER = Logger.getLogger(BucketFixture.class.getName());

  private final Supplier<Storage> storageHandle;
  private final String bucketNameFmtString;
  private final CleanupStrategy cleanupStrategy;

  private BucketInfo bucketInfo;

  private BucketFixture(
      Supplier<Storage> storageHandle,
      String bucketNameFmtString,
      CleanupStrategy cleanupStrategy) {
    this.storageHandle = storageHandle;
    this.bucketNameFmtString = bucketNameFmtString;
    this.cleanupStrategy = cleanupStrategy;
  }

  public BucketInfo getBucketInfo() {
    return bucketInfo;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        String bucketName = String.format(bucketNameFmtString, UUID.randomUUID());
        Storage s = storageHandle.get();
        Bucket bucket = s.create(BucketInfo.of(bucketName));
        bucketInfo = bucket;
        boolean success = false;
        try {
          base.evaluate();
          success = true;
        } finally {
          bucketInfo = null;
          if (bucket != null) {
            String name = bucket.getName();
            switch (cleanupStrategy) {
              case ALWAYS:
                doCleanup(name, s);
                break;
              case ONLY_ON_SUCCESS:
                if (success) {
                  doCleanup(name, s);
                }
                break;
              case NEVER:
                break;
            }
          }
        }
      }
    };
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private void doCleanup(String bucketName, Storage s) {
    LOGGER.info("Starting bucket cleanup...");
    try {
      // TODO: probe bucket existence, a bad test could have deleted the bucket
      Page<Blob> page1 = s.list(bucketName);

      int availableProcessors = Runtime.getRuntime().availableProcessors();
      ListeningExecutorService exec =
          MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2 * availableProcessors));

      List<ListenableFuture<DeleteResult>> futures =
          StreamSupport.stream(page1.iterateAll().spliterator(), false)
              .map(b -> exec.submit(() -> new DeleteResult(b.getName(), b.delete())))
              .collect(Collectors.toList());

      List<DeleteResult> deleteResults = Futures.allAsList(futures).get(5, TimeUnit.MINUTES);
      List<DeleteResult> failedDeletes =
          deleteResults.stream().filter(r -> !r.success).collect(Collectors.toList());
      failedDeletes.forEach(
          r -> LOGGER.warning(String.format("Failed to delete object %s/%s", bucketName, r.name)));

      if (failedDeletes.isEmpty()) {
        s.delete(bucketName);
      } else {
        LOGGER.warning("Unable to delete bucket due to previous failed object deletes");
      }
      exec.shutdown();
      LOGGER.info("Bucket cleanup complete");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e, () -> "Error during bucket cleanup.");
    }
  }

  public static final class Builder {
    private Supplier<Storage> handle;
    private String bucketNameFmtString = "gcloud-test-bucket-temp-%s";
    private CleanupStrategy cleanupStrategy = CleanupStrategy.ALWAYS;

    public Builder setHandle(Supplier<Storage> handle) {
      this.handle = requireNonNull(handle, "handle must be non null");
      return this;
    }

    public Builder setBucketNameFmtString(String bucketNameFmtString) {
      this.bucketNameFmtString =
          requireNonNull(bucketNameFmtString, "bucketNameFmtString must be non null");
      return this;
    }

    public Builder setCleanupStrategy(CleanupStrategy cleanupStrategy) {
      this.cleanupStrategy = requireNonNull(cleanupStrategy, "cleanupStrategy must be non null");
      return this;
    }

    public BucketFixture build() {
      return new BucketFixture(
          requireNonNull(handle, "handle must be non null"), bucketNameFmtString, cleanupStrategy);
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
