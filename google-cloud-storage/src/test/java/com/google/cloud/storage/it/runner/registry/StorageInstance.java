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

package com.google.cloud.storage.it.runner.registry;

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.StorageOptions;
import com.google.common.reflect.Reflection;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

final class StorageInstance implements ManagedLifecycle {

  private final StorageOptions options;
  private Storage storage;

  private Storage proxy;

  private final ProtectedBucketNames protectedBucketNames;

  StorageInstance(StorageOptions options, ProtectedBucketNames protectedBucketNames) {
    this.options = options;
    this.protectedBucketNames = protectedBucketNames;
  }

  Storage getStorage() {
    return proxy;
  }

  @Override
  public Object get() {
    return proxy;
  }

  private static final Object[] updateParameters = {BucketInfo.class, BucketTargetOption[].class};
  private static final Object[] deleteParameters = {String.class, BucketSourceOption[].class};

  @Override
  public void start() {
    storage = options.getService();
    // Define a proxy which can veto calls which attempt to mutate protected buckets
    //   this helps guard against a test trying to mutate the global bucket rather than creating its
    //   own bucket.
    proxy =
        Reflection.newProxy(
            Storage.class,
            ((proxy1, method, args) -> {
              String methodName = method.getName();
              Class<?>[] parameterTypes = method.getParameterTypes();
              if (methodName.equals("update")
                  && Arrays.deepEquals(parameterTypes, updateParameters)) {
                BucketInfo bucketInfo = (BucketInfo) args[0];
                if (protectedBucketNames.isProtected(bucketInfo.getName())) {
                  throw err(bucketInfo.getName());
                }
              } else if (methodName.equals("delete")
                  && Arrays.deepEquals(parameterTypes, deleteParameters)) {
                String bucketName = (String) args[0];
                if (protectedBucketNames.isProtected(bucketName)) {
                  throw err(bucketName);
                }
              } else if (methodName.equals("setIamPolicy")) {
                String bucketName = (String) args[0];
                if (protectedBucketNames.isProtected(bucketName)) {
                  throw err(bucketName);
                }
              }
              try {
                return method.invoke(storage, args);
              } catch (InvocationTargetException e) {
                throw e.getCause();
              }
            }));
  }

  @Override
  public void stop() {
    try (Storage ignore = storage) {
      storage = null;
      proxy = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static VetoedBucketUpdateException err(String bucketName) {
    return new VetoedBucketUpdateException("Attempted to modify global bucket: " + bucketName);
  }

  private static final class VetoedBucketUpdateException extends RuntimeException {
    private VetoedBucketUpdateException(String message) {
      super(message);
    }
  }
}
