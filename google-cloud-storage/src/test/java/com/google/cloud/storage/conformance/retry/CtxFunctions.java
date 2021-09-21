/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.storage.conformance.retry;

import static com.google.common.collect.Sets.newHashSet;

import com.google.cloud.conformance.storage.v1.Resource;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.conformance.retry.Functions.CtxFunction;
import com.google.common.base.Joiner;
import java.util.HashSet;

/**
 * Define a set of {@link CtxFunction} which are used in mappings as well as general setup/tear down
 * of specific tests.
 *
 * <p>Functions are grouped into nested classes which try to hint at the area they operate within.
 * Client side-only, or performing an RPC, setup or tear down and so on.
 *
 * @see RpcMethodMapping
 * @see RpcMethodMapping.Builder
 * @see RpcMethodMappings
 */
final class CtxFunctions {

  private static final class Util {
    private static final CtxFunction blobIdAndBlobInfo =
        (ctx, c) -> ctx.map(state -> state.with(BlobInfo.newBuilder(state.getBlobId()).build()));
  }

  static final class Local {
    static final CtxFunction blobCopy =
        (ctx, c) -> ctx.map(s -> s.withCopyDest(BlobId.of(c.getBucketName2(), c.getObjectName())));

    static final CtxFunction bucketInfo =
        (ctx, c) -> ctx.map(s -> s.with(BucketInfo.of(c.getBucketName())));
    static final CtxFunction blobIdWithoutGeneration =
        (ctx, c) -> ctx.map(s -> s.with(BlobId.of(c.getBucketName(), c.getObjectName())));
    static final CtxFunction blobIdWithGenerationZero =
        (ctx, c) -> ctx.map(s -> s.with(BlobId.of(c.getBucketName(), c.getObjectName(), 0L)));
    static final CtxFunction blobInfoWithoutGeneration =
        blobIdWithoutGeneration.andThen(Util.blobIdAndBlobInfo);
    static final CtxFunction blobInfoWithGenerationZero =
        blobIdWithGenerationZero.andThen(Util.blobIdAndBlobInfo);
  }

  static final class Rpc {
    static final CtxFunction bucket =
        (ctx, c) ->
            ctx.map(state -> state.with(ctx.getStorage().get(state.getBucketInfo().getName())));
    static final CtxFunction blobWithGeneration =
        (ctx, c) ->
            ctx.map(
                state ->
                    state.with(
                        ctx.getStorage()
                            .create(
                                BlobInfo.newBuilder(state.getBlobId()).build(),
                                c.getHelloWorldUtf8Bytes())));
    static final CtxFunction createEmptyBlob =
        (ctx, c) -> ctx.map(state -> state.with(ctx.getStorage().create(state.getBlobInfo())));
  }

  static final class ResourceSetup {
    private static final CtxFunction bucket =
        (ctx, c) -> {
          BucketInfo bucketInfo = BucketInfo.newBuilder(c.getBucketName()).build();
          Bucket resolvedBucket = ctx.getStorage().create(bucketInfo);
          return ctx.map(s -> s.with(resolvedBucket));
        };
    private static final CtxFunction object =
        (ctx, c) -> {
          BlobInfo blobInfo =
              BlobInfo.newBuilder(ctx.getState().getBucket().getName(), c.getObjectName()).build();
          Blob resolvedBlob = ctx.getStorage().create(blobInfo);
          return ctx.map(s -> s.with(resolvedBlob));
        };
    private static final CtxFunction serviceAccount =
        (ctx, c) ->
            ctx.map(s -> s.with(ServiceAccount.of(c.getServiceAccountSigner().getAccount())));
    private static final CtxFunction hmacKey =
        (ctx, c) -> ctx.map(s -> s.with(ctx.getStorage().createHmacKey(s.getServiceAccount())));

    private static final CtxFunction processResources =
        (ctx, c) -> {
          HashSet<Resource> resources = newHashSet(c.getMethod().getResourcesList());
          CtxFunction f = CtxFunction.identity();
          if (resources.contains(Resource.BUCKET)) {
            f = f.andThen(ResourceSetup.bucket);
            resources.remove(Resource.BUCKET);
          }

          if (resources.contains(Resource.OBJECT)) {
            f = f.andThen(ResourceSetup.object);
            resources.remove(Resource.OBJECT);
          }

          if (resources.contains(Resource.HMAC_KEY)) {
            f = f.andThen(serviceAccount).andThen(hmacKey);
            resources.remove(Resource.HMAC_KEY);
          }

          if (!resources.isEmpty()) {
            throw new IllegalStateException(
                String.format("Unhandled Method Resource [%s]", Joiner.on(", ").join(resources)));
          }

          return f.apply(ctx, c);
        };

    private static final CtxFunction allUsersReaderAcl =
        (ctx, c) -> ctx.map(s -> s.with(Acl.of(User.ofAllUsers(), Role.READER)));

    static final CtxFunction defaultSetup = processResources.andThen(allUsersReaderAcl);
  }

  static final class ResourceTeardown {
    static final CtxFunction object =
        (ctx, c) -> {
          BlobInfo blobInfo =
              BlobInfo.newBuilder(ctx.getState().getBucket().getName(), c.getObjectName()).build();
          ctx.getStorage().delete(blobInfo.getBlobId());
          return ctx.map(s -> s.with((Blob) null));
        };
    static final CtxFunction bucket =
        (ctx, c) -> {
          ctx.getState().getBucket().delete();
          return ctx.map(s -> s.with((Bucket) null));
        };
  }
}
