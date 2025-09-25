package com.example.storage.object;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobReadSession;
import com.google.cloud.storage.RangeSpec;
import com.google.cloud.storage.ReadAsFutureBytes;
import com.google.cloud.storage.ReadProjectionConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultipleAppendableObjectsSingleRangedRead {
  public static void multipleObjectsSingleRangedRead(
      String bucketName, List<String> objectNames, long startOffset, int length) throws Exception {

    RangeSpec singleRange = RangeSpec.of(startOffset, length);
    ReadAsFutureBytes rangeConfig =
        ReadProjectionConfigs.asFutureBytes().withRangeSpec(singleRange);

    try (Storage storage = StorageOptions.grpc().build().getService()) {
      List<ApiFuture<byte[]>> futuresToWaitOn = new ArrayList<>();

      System.out.printf(
          "Initiating single ranged read [%d, %d] on %d objects...%n",
          startOffset, startOffset + length - 1, objectNames.size());

      for (String objectName : objectNames) {
        BlobId blobId = BlobId.of(bucketName, objectName);
        ApiFuture<BlobReadSession> futureReadSession = storage.blobReadSession(blobId);

        ApiFuture<byte[]> readFuture =
            ApiFutures.transformAsync(
                futureReadSession,
                (BlobReadSession session) -> session.readAs(rangeConfig),
                MoreExecutors.directExecutor());

        futuresToWaitOn.add(readFuture);
      }
      ApiFutures.allAsList(futuresToWaitOn).get(30, TimeUnit.SECONDS);

      System.out.println("All concurrent single-ranged read operations are complete.");
    }
  }
}
