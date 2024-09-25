package com.example.storage;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.it.BucketCleaner;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.storage.control.v2.StorageControlClient;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

public final class ITVerboseBucketCleanupTest {
  private static final Logger LOGGER = Logger.getLogger(ITVerboseBucketCleanupTest.class.getName());

  @Test
  public void verboseBucketCleanup() throws Exception {

    ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("cp-pool-%03d")
        .build();

    ListeningExecutorService exec = MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(4 * Runtime.getRuntime().availableProcessors(), threadFactory)
    );

    OffsetDateTime now = Instant.now().atOffset(ZoneOffset.UTC);
    OffsetDateTime _3DaysAgo = now.minusDays(3);
    LOGGER.info(String.format("_3DaysAgo = %s", _3DaysAgo));

    try (Storage s = StorageOptions.http().build().getService();
        StorageControlClient ctrl = StorageControlClient.create()) {
      List<ListenableFuture<Void>> deleteFutures = s.list(
              BucketListOption.fields(BucketField.NAME, BucketField.TIME_CREATED))
          .streamAll()
          .map(bucket -> {
            String name = bucket.getName();
            OffsetDateTime ctime = bucket.getCreateTimeOffsetDateTime();
            String action = null;
            try {
              if (name.startsWith("gcloud-test") && ctime.isBefore(_3DaysAgo)) {
                action = "Cleaning up";
                return exec.<Void>submit(() -> {
                  BucketCleaner.doCleanup(name, s, ctrl);
                  return null;
                });
              } else {
                action = "Skipping";
                return Futures.immediateVoidFuture();
              }
            } finally {
              LOGGER.warning(String.format("%11s bucket {name: '%s', ctime: '%s'}", action, name, ctime));
            }
          })
          .collect(Collectors.toList());

      ListenableFuture<List<@Nullable Void>> allFuture = Futures.allAsList(deleteFutures);

      allFuture.get(15, TimeUnit.MINUTES);
    }

  }
}
