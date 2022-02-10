package com.example.storage.bucket;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.cloud.testing.junit4.StdOutCaptureRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PrintBucketAclTest {

  @Rule
  public StdOutCaptureRule stdOut = new StdOutCaptureRule();
  private static final String USER_EMAIL =
      "google-cloud-java-tests@" + "java-docs-samples-tests.iam.gserviceaccount.com";

  private String bucketName;
  private Storage storage;

  @Before
  public void setUp() {
    bucketName = RemoteStorageHelper.generateBucketName();
    storage = StorageOptions.getDefaultInstance().getService();
    storage.create(BucketInfo.of(bucketName));
  }

  @After
  public void tearDown() {
    storage.delete(bucketName);
  }

  @Test
  public void testPrintBucketAcls() {
    Entity testUser = new User(USER_EMAIL);
    storage.createAcl(bucketName, Acl.of(testUser, Role.READER));
    PrintBucketAcl.printBucketAcl(bucketName);
    assertThat(stdOut.getCapturedOutputAsUtf8String()).contains("READER: USER");
  }


}
