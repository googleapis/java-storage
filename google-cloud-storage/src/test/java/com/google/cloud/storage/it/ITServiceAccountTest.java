package com.google.cloud.storage.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageFixture;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ITServiceAccountTest {

  @ClassRule public static final StorageFixture storageFixture = StorageFixture.defaultHttp();

  private static final String SERVICE_ACCOUNT_EMAIL_SUFFIX =
      "@gs-project-accounts.iam.gserviceaccount.com";

  private static Storage storage;

  @BeforeClass
  public static void setup() {
    storage = storageFixture.getInstance();
  }

  @Test
  public void testGetServiceAccount() {
    String projectId = storage.getOptions().getProjectId();
    ServiceAccount serviceAccount = storage.getServiceAccount(projectId);
    assertNotNull(serviceAccount);
    assertTrue(serviceAccount.getEmail().endsWith(SERVICE_ACCOUNT_EMAIL_SUFFIX));
  }
}
