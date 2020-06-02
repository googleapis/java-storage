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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.Tuple;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.spi.v1.StorageRpc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FakeStorageRpcTest {

  private FakeStorageRpc instance;
  private Map<StorageRpc.Option, Object> options;
  private static final String BUCKET_NAME = "fake-bucket";
  private static final String OBJECT_NAME = "fake-obj";
  private static final long GENERATION = 12345L;
  private static final Bucket BUCKET = new Bucket().setName("fake-bucket");
  private static final byte[] BYTES = {0, 1, 2, 3, 4, 5, 6, 7};

  private static final StorageObject OBJECT =
      new StorageObject().setName(OBJECT_NAME).setBucket(BUCKET_NAME).setGeneration(GENERATION);
  private static final String FULLNAME = fullname(OBJECT);

  private static final String fullname(StorageObject object) {
    return object.getBucket() + "/" + object.getName();
  }

  @Before
  public void setUp() throws Exception {
    instance = new FakeStorageRpc(true);
    options = new HashMap<>();
  }

  @After
  public void tearDown() throws Exception {}

  // Using Callable would require to return null each time
  interface FakeCall {
    void call();
  }

  private void verifyUnsupported(FakeCall fakeCall) {
    try {
      fakeCall.call();
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  private void verifyNotFoundStorageException(String name, FakeCall fakeCall) {
    try {
      fakeCall.call();
      fail("StorageException expected");
    } catch (StorageException expected) {
      assertEquals(404, expected.getCode());
      assertEquals("File not found: " + name, expected.getMessage());
    }
  }

  private void verifyFields(int metadataSize, int contentsSize, int futureContentsSize) {
    assertEquals(metadataSize, instance.metadata.size());
    assertEquals(contentsSize, instance.contents.size());
    assertEquals(futureContentsSize, instance.futureContents.size());
  }

  @Test
  public void testReset() {
    instance.metadata.put("x", OBJECT);
    instance.contents.put("y", new byte[1]);
    instance.futureContents.put("z", new byte[2]);
    instance.reset();
    verifyFields(0, 0, 1);
  }

  @Test
  public void testCreateBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.create(BUCKET, options);
          }
        });
  }

  @Test
  public void testCreateObject() {
    instance.create(OBJECT, new ByteArrayInputStream(BYTES), options);
    verifyFields(1, 1, 0);
    assertSame(OBJECT, instance.metadata.get(FULLNAME));
    assertArrayEquals(BYTES, instance.contents.get(FULLNAME));
  }

  @Test
  public void testList() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.list(options);
          }
        });
  }

  @Test
  public void testListBucket() {
    Tuple<String, Iterable<StorageObject>> tuple = instance.list(BUCKET.getName(), options);
    assertNull(tuple.x());
    assertFalse(tuple.y().iterator().hasNext());

    instance.create(OBJECT, new ByteArrayInputStream(new byte[1]), options);
    StorageObject anotherObject = new StorageObject().setBucket("x2").setName("y2");
    instance.create(anotherObject, new ByteArrayInputStream(new byte[2]), options);
    options.put(StorageRpc.Option.PAGE_TOKEN, "abc");

    tuple = instance.list(null, options);
    assertEquals("abc", tuple.x());
    List<StorageObject> objectList = new ArrayList<>();
    for (Iterator<StorageObject> it = tuple.y().iterator(); it.hasNext(); ) {
      objectList.add(it.next());
    }
    assertEquals(2, objectList.size());
    assertTrue(objectList.contains(OBJECT));
    assertTrue(objectList.contains(anotherObject));
  }

  @Test
  public void testGetBucket() {
    assertNull(instance.get(BUCKET, options));
    options.put(StorageRpc.Option.USER_PROJECT, "what ever");

    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.get(BUCKET, options);
          }
        });
  }

  @Test
  public void testGetObject() {
    assertNull(instance.get(OBJECT, options));

    instance.metadata.put("x", OBJECT);
    assertNull(instance.get(OBJECT, options));

    instance.metadata.put(FULLNAME, OBJECT);
    assertSame(OBJECT, instance.get(OBJECT, options));
  }

  @Test
  public void testPatchBucket() {
    assertNull(instance.patch(BUCKET, options));

    options.put(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, "some");
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.patch(BUCKET, options);
          }
        });
  }

  @Test
  public void testPatchObject() {
    assertNull(instance.patch(OBJECT, options));

    options.put(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, "some");
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.patch(OBJECT, options);
          }
        });
  }

  @Test
  public void testDeleteBucket() {
    assertFalse(instance.delete(BUCKET, options));
    options.put(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, "some");
    assertFalse(instance.delete(BUCKET, options));
  }

  @Test
  public void testDeleteObject() {
    assertFalse(instance.delete(OBJECT, options));
    options.put(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY, "some");
    assertFalse(instance.delete(OBJECT, options));
  }

  @Test
  public void testCreateBatch() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createBatch();
          }
        });
  }

  @Test
  public void testCompose() {
    StorageObject[] array = {OBJECT};
    Iterable<StorageObject> iterable = Arrays.asList(array);
    assertNull(instance.compose(iterable, OBJECT, options));
  }

  @Test
  public void testLoad() {
    verifyNotFoundStorageException(
        FULLNAME,
        new FakeCall() {
          @Override
          public void call() {
            instance.load(OBJECT, options);
          }
        });

    instance.contents.put(FULLNAME, new byte[42]);
    byte[] bytes = instance.load(OBJECT, options);
    assertArrayEquals(new byte[42], bytes);
  }

  @Test
  public void testReadBytes() {
    verifyNotFoundStorageException(
        FULLNAME,
        new FakeCall() {
          @Override
          public void call() {
            instance.read(OBJECT, options, 0, 0);
          }
        });
    instance.contents.put(FULLNAME, BYTES);
    Tuple<String, byte[]> read = instance.read(OBJECT, options, 1, BYTES.length - 2);
    byte[] expected = new byte[BYTES.length - 2];
    System.arraycopy(BYTES, 1, expected, 0, expected.length);
    assertEquals("etag-goes-here", read.x());
    assertArrayEquals(expected, read.y());
  }

  @Test
  public void testReadOutputStream() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
    verifyNotFoundStorageException(
        FULLNAME,
        new FakeCall() {
          @Override
          public void call() {
            instance.read(OBJECT, options, 0, outputStream);
          }
        });
    instance.contents.put(FULLNAME, BYTES);
    long read = instance.read(OBJECT, options, 0, outputStream);
    assertEquals(BYTES.length, read);
    assertArrayEquals(BYTES, outputStream.toByteArray());
  }

  @Test
  public void testOpenObject() {
    String name = instance.open(OBJECT, options);
    assertEquals(FULLNAME, name);
    instance.metadata.put(FULLNAME, OBJECT);
    options.put(StorageRpc.Option.IF_GENERATION_MATCH, new Long(GENERATION));
    name = instance.open(OBJECT, options);
    assertEquals(FULLNAME, name);
    options.put(StorageRpc.Option.IF_GENERATION_MATCH, new Long(123));
    try {
      instance.open(OBJECT, options);
      fail();
    } catch (StorageException e) {
      assertEquals("Generation mismatch. Requested 123 but got " + GENERATION, e.getMessage());
    }
  }

  @Test
  public void testOpenSignedURL() {
    assertNull(instance.open("something"));
  }

  @Test
  public void testWrite() {
    String uploadId = "upload-id";
    byte[] part1 = {1, 2, 3};
    byte[] part2 = {3, 4, 5, 6};
    instance.write(uploadId, part1, 0, 0L, part1.length, false);
    verifyFields(0, 0, 1);
    assertArrayEquals(part1, instance.futureContents.get(uploadId));

    instance.write(uploadId, part2, 0, 0L, part2.length, true);
    verifyFields(0, 1, 0);
    assertArrayEquals(part2, instance.contents.get(uploadId));
  }

  @Test
  public void testOpenRewrite() {
    StorageObject source =
        new StorageObject().setBucket(BUCKET_NAME).setName("source").setGeneration(555L);
    StorageObject target =
        new StorageObject().setBucket(BUCKET_NAME).setName("target").setGeneration(777L);

    final StorageRpc.RewriteRequest request =
        new StorageRpc.RewriteRequest(source, options, false, target, options, 10L);

    String sourceFullname = fullname(source);
    verifyNotFoundStorageException(
        sourceFullname,
        new FakeCall() {
          @Override
          public void call() {
            instance.openRewrite(request);
          }
        });
    String targetFullname = fullname(target);
    instance.metadata.put(targetFullname, target);
    instance.contents.put(sourceFullname, BYTES);
    StorageRpc.RewriteResponse response = instance.openRewrite(request);
    assertSame(request, response.rewriteRequest);
    assertEquals(BYTES.length, response.blobSize);
    assertArrayEquals(BYTES, instance.contents.get(fullname(target)));
    assertSame(target, instance.metadata.get(fullname(target)));
    assertEquals(778L, (long) target.getGeneration());
  }

  @Test
  public void testContinueRewrite() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.continueRewrite(null);
          }
        });
  }

  @Test
  public void testGetAclBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.getAcl(BUCKET_NAME, "entiry", options);
          }
        });
  }

  @Test
  public void testGetAclObject() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.getAcl(BUCKET_NAME, "object", 0L, "entity");
          }
        });
  }

  @Test
  public void testDeleteAclBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.deleteAcl(BUCKET_NAME, "entity", options);
          }
        });
  }

  @Test
  public void testDeleteAclObject() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.deleteAcl(BUCKET_NAME, "object", 0L, "entity");
          }
        });
  }

  @Test
  public void testCreateAclBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createAcl(null, options);
          }
        });
  }

  @Test
  public void testCreateAclObject() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createAcl(null);
          }
        });
  }

  @Test
  public void testPatchAclBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.patchAcl(null, options);
          }
        });
  }

  @Test
  public void testPatchAclObject() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.patchAcl(null);
          }
        });
  }

  @Test
  public void testListAclsBucket() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.listAcls(BUCKET_NAME, options);
          }
        });
  }

  @Test
  public void testListAclsObject() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.listAcls(BUCKET_NAME, OBJECT_NAME, 100L);
          }
        });
  }

  @Test
  public void testCreateHmacKey() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createHmacKey("account", options);
          }
        });
  }

  @Test
  public void testListHmacKeys() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.listHmacKeys(options);
          }
        });
  }

  @Test
  public void testUpdateHmacKey() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.updateHmacKey(null, options);
          }
        });
  }

  @Test
  public void testGetHmacKey() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.getHmacKey("account", options);
          }
        });
  }

  @Test
  public void testDeleteHmacKey() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.deleteHmacKey(null, options);
          }
        });
  }

  @Test
  public void testGetDefaultAcl() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.getDefaultAcl("bucket", "entity");
          }
        });
  }

  @Test
  public void testDeleteDefaultAcl() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.deleteDefaultAcl("bucket", "entity");
          }
        });
  }

  @Test
  public void testCreateDefaultAcl() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createDefaultAcl(null);
          }
        });
  }

  @Test
  public void testPatchDefaultAcl() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.patchDefaultAcl(null);
          }
        });
  }

  @Test
  public void testListDefaultAcls() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.listDefaultAcls("bucket");
          }
        });
  }

  @Test
  public void testGetIamPolicy() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.getIamPolicy("bucket", options);
          }
        });
  }

  @Test
  public void testSetIamPolicy() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.setIamPolicy("bucket", null, options);
          }
        });
  }

  @Test
  public void testTestIamPermissions() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.testIamPermissions("bucket", null, options);
          }
        });
  }

  @Test
  public void testDeleteNotification() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.deleteNotification("bucket", "entity");
          }
        });
  }

  @Test
  public void testListNotifications() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.listNotifications("bucket");
          }
        });
  }

  @Test
  public void testCreateNotification() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.createNotification("bucket", null);
          }
        });
  }

  @Test
  public void testLockRetentionPolicy() {
    verifyUnsupported(
        new FakeCall() {
          @Override
          public void call() {
            instance.lockRetentionPolicy(BUCKET, options);
          }
        });
  }

  @Test
  public void testGetServiceAccount() {
    assertNull(instance.getServiceAccount("project"));
  }
}
