package com.example.storage.bucket;

import com.example.storage.TestBase;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.BucketInfo.Autoclass;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import org.junit.Assert;
import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;

public class AutoclassTest extends TestBase {

  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");

  @Test
  public void testSetGetBucketAutoclass() {
    String autoclassBucket = RemoteStorageHelper.generateBucketName();
    storage.create(BucketInfo.newBuilder(autoclassBucket)
        .setAutoclass(Autoclass.newBuilder().setEnabled(true).build())
        .build());
    try {
      SetBucketAutoclass.setBucketAutoclass(PROJECT_ID, autoclassBucket);
      Autoclass autoclass = storage.get(autoclassBucket).getAutoclass();
      Assert.assertFalse(autoclass.getEnabled());

      GetBucketAutoclass.getBucketAutoclass(PROJECT_ID, autoclassBucket);
      assertThat(stdOut.getCapturedOutputAsUtf8String())
          .contains(autoclass.getToggleTime().toString());
    } finally {
      RemoteStorageHelper.forceDelete(storage, autoclassBucket);
    }
  }
}
