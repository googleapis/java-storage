/*
 * Copyright 2020 Google LLC
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

package com.google.cloud.storage.testing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.HmacKey;
import com.google.api.services.storage.model.HmacKeyMetadata;
import com.google.api.services.storage.model.Notification;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Policy;
import com.google.api.services.storage.model.ServiceAccount;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.services.storage.model.TestIamPermissionsResponse;
import com.google.cloud.Tuple;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageRpcTestBaseTest {

  private StorageRpc valueInstance;
  private StorageRpc exceptionInstance;
  private RpcCall call;

  private static final Map<StorageRpc.Option, Object> OPTIONS = new HashMap<>();
  private static final Bucket BUCKET = new Bucket().setName("fake-bucket");
  private static final byte[] BYTES = {0, 1, 2, 3, 4, 5, 6, 7};
  private static final StorageObject OBJECT =
      new StorageObject().setName("object name").setBucket("bucket name");

  @Before
  public void setUp() {
    valueInstance = new StorageRpcTestBase();
    exceptionInstance = new StorageRpcTestBase(false);
    call = null;
  }

  @After
  public void tearDown() {
    assertNotNull(call);
    verifyUnsupported(call);
    verifyReturnValue(call);
  }

  interface RpcCall<V> {
    V call(StorageRpc storageRpc);
  }

  private void verifyUnsupported(RpcCall rpcCall) {
    try {
      rpcCall.call(exceptionInstance);
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  private void verifyReturnValue(RpcCall rpcCall) {
    Object value = rpcCall.call(valueInstance);
    if (value instanceof Boolean) {
      assertEquals(Boolean.FALSE, value);
    } else if (value instanceof Long) {
      assertEquals(new Long(0), value);
    } else if (value instanceof byte[]) {
      assertArrayEquals(new byte[0], (byte[]) value);
    } else {
      assertNull(value);
    }
  }

  @Test
  public void testCreateBucket() {
    call =
        new RpcCall<Bucket>() {
          @Override
          public Bucket call(StorageRpc storageRpc) {
            return storageRpc.create(BUCKET, OPTIONS);
          }
        };
  }

  @Test
  public void testCreateObject() {
    call =
        new RpcCall<StorageObject>() {
          @Override
          public StorageObject call(StorageRpc storageRpc) {
            return storageRpc.create(OBJECT, new ByteArrayInputStream(BYTES), OPTIONS);
          }
        };
  }

  @Test
  public void testList() {
    call =
        new RpcCall<Tuple<String, Iterable<Bucket>>>() {
          @Override
          public Tuple<String, Iterable<Bucket>> call(StorageRpc storageRpc) {
            return storageRpc.list(OPTIONS);
          }
        };
  }

  @Test
  public void testListBucket() {
    call =
        new RpcCall<Tuple<String, Iterable<StorageObject>>>() {
          @Override
          public Tuple<String, Iterable<StorageObject>> call(StorageRpc storageRpc) {
            return storageRpc.list(BUCKET.getName(), OPTIONS);
          }
        };
  }

  @Test
  public void testGetBucket() {
    call =
        new RpcCall<Bucket>() {
          @Override
          public Bucket call(StorageRpc storageRpc) {
            return storageRpc.get(BUCKET, OPTIONS);
          }
        };
  }

  @Test
  public void testGetObject() {
    call =
        new RpcCall<StorageObject>() {
          @Override
          public StorageObject call(StorageRpc storageRpc) {
            return storageRpc.get(OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testPatchBucket() {
    call =
        new RpcCall<Bucket>() {
          @Override
          public Bucket call(StorageRpc storageRpc) {
            return storageRpc.patch(BUCKET, OPTIONS);
          }
        };
  }

  @Test
  public void testPatchObject() {
    call =
        new RpcCall<StorageObject>() {
          @Override
          public StorageObject call(StorageRpc storageRpc) {
            return storageRpc.patch(OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testDeleteBucket() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.delete(BUCKET, OPTIONS);
          }
        };
  }

  @Test
  public void testDeleteObject() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.delete(OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testCreateBatch() {
    call =
        new RpcCall<RpcBatch>() {
          @Override
          public RpcBatch call(StorageRpc storageRpc) {
            return storageRpc.createBatch();
          }
        };
  }

  @Test
  public void testCompose() {
    call =
        new RpcCall<StorageObject>() {
          @Override
          public StorageObject call(StorageRpc storageRpc) {
            return storageRpc.compose(null, OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testLoad() {
    call =
        new RpcCall<byte[]>() {
          @Override
          public byte[] call(StorageRpc storageRpc) {
            return storageRpc.load(OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testReadBytes() {
    call =
        new RpcCall<Tuple<String, byte[]>>() {
          @Override
          public Tuple<String, byte[]> call(StorageRpc storageRpc) {
            return storageRpc.read(OBJECT, OPTIONS, 0, 0);
          }
        };
  }

  @Test
  public void testReadOutputStream() {
    call =
        new RpcCall<Long>() {
          @Override
          public Long call(StorageRpc storageRpc) {
            return storageRpc.read(OBJECT, OPTIONS, 0, new ByteArrayOutputStream(100));
          }
        };
  }

  @Test
  public void testOpenObject() {
    call =
        new RpcCall<String>() {
          @Override
          public String call(StorageRpc storageRpc) {
            return storageRpc.open(OBJECT, OPTIONS);
          }
        };
  }

  @Test
  public void testOpenSignedURL() {
    call =
        new RpcCall<String>() {
          @Override
          public String call(StorageRpc storageRpc) {
            return storageRpc.open("signedURL");
          }
        };
  }

  @Test
  public void testWrite() {
    call =
        new RpcCall<Void>() {
          @Override
          public Void call(StorageRpc storageRpc) {
            storageRpc.write("uploadId", new byte[10], 1, 2L, 3, false);
            return null;
          }
        };
  }

  @Test
  public void testOpenRewrite() {
    call =
        new RpcCall<StorageRpc.RewriteResponse>() {
          @Override
          public StorageRpc.RewriteResponse call(StorageRpc storageRpc) {
            return storageRpc.openRewrite(null);
          }
        };
  }

  @Test
  public void testContinueRewrite() {
    call =
        new RpcCall<StorageRpc.RewriteResponse>() {
          @Override
          public StorageRpc.RewriteResponse call(StorageRpc storageRpc) {
            return storageRpc.continueRewrite(null);
          }
        };
  }

  @Test
  public void testGetAclBucket() {
    call =
        new RpcCall<BucketAccessControl>() {
          @Override
          public BucketAccessControl call(StorageRpc storageRpc) {
            return storageRpc.getAcl("bucket", "entity", OPTIONS);
          }
        };
  }

  @Test
  public void testGetAclObject() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.getAcl("bucket", "object", 1L, "entity");
          }
        };
  }

  @Test
  public void testDeleteAclBucket() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.deleteAcl("bucketName", "entity", OPTIONS);
          }
        };
  }

  @Test
  public void testDeleteAclObject() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.deleteAcl("bucketName", "object", 0L, "entity");
          }
        };
  }

  @Test
  public void testCreateAclBucket() {
    call =
        new RpcCall<BucketAccessControl>() {
          @Override
          public BucketAccessControl call(StorageRpc storageRpc) {
            return storageRpc.createAcl(null, OPTIONS);
          }
        };
  }

  @Test
  public void testCreateAclObject() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.createAcl(null);
          }
        };
  }

  @Test
  public void testPatchAclBucket() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.createAcl(null);
          }
        };
  }

  @Test
  public void testPatchAclObject() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.patchAcl(null);
          }
        };
  }

  @Test
  public void testListAclsBucket() {
    call =
        new RpcCall<List<BucketAccessControl>>() {
          @Override
          public List<BucketAccessControl> call(StorageRpc storageRpc) {
            return storageRpc.listAcls("BUCKET_NAME", OPTIONS);
          }
        };
  }

  @Test
  public void testListAclsObject() {
    call =
        new RpcCall<List<ObjectAccessControl>>() {
          @Override
          public List<ObjectAccessControl> call(StorageRpc storageRpc) {
            return storageRpc.listAcls("BUCKET_NAME", "OBJECT_NAME", 100L);
          }
        };
  }

  @Test
  public void testCreateHmacKey() {
    call =
        new RpcCall<HmacKey>() {
          @Override
          public HmacKey call(StorageRpc storageRpc) {
            return storageRpc.createHmacKey("account", OPTIONS);
          }
        };
  }

  @Test
  public void testListHmacKeys() {
    call =
        new RpcCall<Tuple<String, Iterable<HmacKeyMetadata>>>() {
          @Override
          public Tuple<String, Iterable<HmacKeyMetadata>> call(StorageRpc storageRpc) {
            return storageRpc.listHmacKeys(OPTIONS);
          }
        };
  }

  @Test
  public void testUpdateHmacKey() {
    call =
        new RpcCall<HmacKeyMetadata>() {
          @Override
          public HmacKeyMetadata call(StorageRpc storageRpc) {
            return storageRpc.updateHmacKey(null, OPTIONS);
          }
        };
  }

  @Test
  public void testGetHmacKey() {
    call =
        new RpcCall<HmacKeyMetadata>() {
          @Override
          public HmacKeyMetadata call(StorageRpc storageRpc) {
            return storageRpc.getHmacKey("account", OPTIONS);
          }
        };
  }

  @Test
  public void testDeleteHmacKey() {
    call =
        new RpcCall<Void>() {
          @Override
          public Void call(StorageRpc storageRpc) {
            storageRpc.deleteHmacKey(null, OPTIONS);
            return null;
          }
        };
  }

  @Test
  public void testGetDefaultAcl() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.getDefaultAcl("bucket", "entity");
          }
        };
  }

  @Test
  public void testDeleteDefaultAcl() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.deleteDefaultAcl("bucket", "entity");
          }
        };
  }

  @Test
  public void testCreateDefaultAcl() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.createDefaultAcl(null);
          }
        };
  }

  @Test
  public void testPatchDefaultAcl() {
    call =
        new RpcCall<ObjectAccessControl>() {
          @Override
          public ObjectAccessControl call(StorageRpc storageRpc) {
            return storageRpc.patchDefaultAcl(null);
          }
        };
  }

  @Test
  public void testListDefaultAcls() {
    call =
        new RpcCall<List<ObjectAccessControl>>() {
          @Override
          public List<ObjectAccessControl> call(StorageRpc storageRpc) {
            return storageRpc.listDefaultAcls("bucket");
          }
        };
  }

  @Test
  public void testGetIamPolicy() {
    call =
        new RpcCall<Policy>() {
          @Override
          public Policy call(StorageRpc storageRpc) {
            return storageRpc.getIamPolicy("bucket", OPTIONS);
          }
        };
  }

  @Test
  public void testSetIamPolicy() {
    call =
        new RpcCall<Policy>() {
          @Override
          public Policy call(StorageRpc storageRpc) {
            return storageRpc.setIamPolicy("bucket", null, OPTIONS);
          }
        };
  }

  @Test
  public void testTestIamPermissions() {
    call =
        new RpcCall<TestIamPermissionsResponse>() {
          @Override
          public TestIamPermissionsResponse call(StorageRpc storageRpc) {
            return storageRpc.testIamPermissions("bucket", null, OPTIONS);
          }
        };
  }

  @Test
  public void testDeleteNotification() {
    call =
        new RpcCall<Boolean>() {
          @Override
          public Boolean call(StorageRpc storageRpc) {
            return storageRpc.deleteNotification("bucket", "entity");
          }
        };
  }

  @Test
  public void testListNotifications() {
    call =
        new RpcCall<List<Notification>>() {
          @Override
          public List<Notification> call(StorageRpc storageRpc) {
            return storageRpc.listNotifications("bucket");
          }
        };
  }

  @Test
  public void testCreateNotification() {
    call =
        new RpcCall<Notification>() {
          @Override
          public Notification call(StorageRpc storageRpc) {
            return storageRpc.createNotification("bucket", null);
          }
        };
  }

  @Test
  public void testLockRetentionPolicy() {
    call =
        new RpcCall<Bucket>() {
          @Override
          public Bucket call(StorageRpc storageRpc) {
            return storageRpc.lockRetentionPolicy(BUCKET, OPTIONS);
          }
        };
  }

  @Test
  public void testGetServiceAccount() {
    call =
        new RpcCall<ServiceAccount>() {
          @Override
          public ServiceAccount call(StorageRpc storageRpc) {
            return storageRpc.getServiceAccount("project");
          }
        };
  }
}
