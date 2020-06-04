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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * A stub implementation of {@link StorageRpc} which could be used outside of the Storage module for
 * testing purposes. All the methods are implemented either to return the default value or to throw
 * an {@code UnsupportedOperationException}, depending on the boolean parameter given to create an
 * instance. {@code new StorageRpcTestBase()} and {@code new StorageRpcTestBase(true)} create an
 * instance with methods returning a value. {@code new StorageRpcTestBase(false)} creates an
 * instance which methods throwing the exception.
 */
public class StorageRpcTestBase implements StorageRpc {

  private final boolean isImplemented;

  /** Creates an instance with methods returning the default value. */
  public StorageRpcTestBase() {
    this(true);
  }

  /**
   * Creates an instance with methods either returning the default value or throwing {@code
   * UnsupportedOperationException}, depending on the {@code isImplemented} parameter.
   *
   * @param isImplemented {@code true} to return a value, {@code false} to throw an exception.
   */
  public StorageRpcTestBase(boolean isImplemented) {
    this.isImplemented = isImplemented;
  }

  @Override
  public Bucket create(Bucket bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public StorageObject create(StorageObject object, InputStream content, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Tuple<String, Iterable<Bucket>> list(Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Tuple<String, Iterable<StorageObject>> list(String bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Bucket get(Bucket bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public StorageObject get(StorageObject object, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Bucket patch(Bucket bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public StorageObject patch(StorageObject storageObject, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean delete(Bucket bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean delete(StorageObject object, Map<Option, ?> options) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public RpcBatch createBatch() {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public StorageObject compose(
      Iterable<StorageObject> sources, StorageObject target, Map<Option, ?> targetOptions) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public byte[] load(StorageObject storageObject, Map<Option, ?> options) {
    if (isImplemented) {
      return new byte[0];
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Tuple<String, byte[]> read(
      StorageObject from, Map<Option, ?> options, long position, int bytes) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public long read(
      StorageObject from, Map<Option, ?> options, long position, OutputStream outputStream) {
    if (isImplemented) {
      return 0;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public String open(StorageObject object, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public String open(String signedURL) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public void write(
      String uploadId,
      byte[] toWrite,
      int toWriteOffset,
      long destOffset,
      int length,
      boolean last) {
    if (isImplemented) {
      return;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public RewriteResponse openRewrite(RewriteRequest rewriteRequest) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public RewriteResponse continueRewrite(RewriteResponse previousResponse) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public BucketAccessControl getAcl(String bucket, String entity, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean deleteAcl(String bucket, String entity, Map<Option, ?> options) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public BucketAccessControl createAcl(BucketAccessControl acl, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public BucketAccessControl patchAcl(BucketAccessControl acl, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public List<BucketAccessControl> listAcls(String bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl getDefaultAcl(String bucket, String entity) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean deleteDefaultAcl(String bucket, String entity) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl createDefaultAcl(ObjectAccessControl acl) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl patchDefaultAcl(ObjectAccessControl acl) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public List<ObjectAccessControl> listDefaultAcls(String bucket) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl getAcl(String bucket, String object, Long generation, String entity) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean deleteAcl(String bucket, String object, Long generation, String entity) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl createAcl(ObjectAccessControl acl) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ObjectAccessControl patchAcl(ObjectAccessControl acl) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public List<ObjectAccessControl> listAcls(String bucket, String object, Long generation) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public HmacKey createHmacKey(String serviceAccountEmail, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Tuple<String, Iterable<HmacKeyMetadata>> listHmacKeys(Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public HmacKeyMetadata updateHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public HmacKeyMetadata getHmacKey(String accessId, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options) {
    if (isImplemented) {
      return;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Policy getIamPolicy(String bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Policy setIamPolicy(String bucket, Policy policy, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public TestIamPermissionsResponse testIamPermissions(
      String bucket, List<String> permissions, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public boolean deleteNotification(String bucket, String notification) {
    if (isImplemented) {
      return false;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public List<Notification> listNotifications(String bucket) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Notification createNotification(String bucket, Notification notification) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public Bucket lockRetentionPolicy(Bucket bucket, Map<Option, ?> options) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public ServiceAccount getServiceAccount(String projectId) {
    if (isImplemented) {
      return null;
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }
}
