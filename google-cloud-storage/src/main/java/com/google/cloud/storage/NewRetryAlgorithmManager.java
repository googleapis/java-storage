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

package com.google.cloud.storage;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.HmacKeyMetadata;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Policy;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.BaseServiceException;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.ExceptionHandler.Interceptor;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteRequest;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;

final class NewRetryAlgorithmManager implements RetryAlgorithmManager {

  private static final Interceptor INTERCEPTOR_IDEMPOTENT =
      new InterceptorImpl(
          true,
          ImmutableSet.<Integer>builder()
              .add(408)
              .add(429)
              .add(500)
              .add(502)
              .add(503)
              .add(504)
              .build());
  private static final Interceptor INTERCEPTOR_IDEMPOTENT_RESUMABLE =
      new InterceptorImpl(
          true,
          ImmutableSet.<Integer>builder().add(408).add(500).add(502).add(503).add(504).build());
  private static final Interceptor INTERCEPTOR_NON_IDEMPOTENT =
      new InterceptorImpl(false, ImmutableSet.<Integer>builder().build());

  private static final ExceptionHandler IDEMPOTENT_HANDLER =
      ExceptionHandler.newBuilder()
          .retryOn(RuntimeException.class)
          .addInterceptors(INTERCEPTOR_IDEMPOTENT)
          .build();

  private static final ExceptionHandler IDEMPOTENT_RESUMABLE_UPLOAD_HANDLER =
      ExceptionHandler.newBuilder()
          .retryOn(RuntimeException.class)
          .addInterceptors(INTERCEPTOR_IDEMPOTENT_RESUMABLE)
          .build();

  private static final ExceptionHandler NON_IDEMPOTENT_HANDLER =
      ExceptionHandler.newBuilder()
          .retryOn(RuntimeException.class)
          .addInterceptors(INTERCEPTOR_NON_IDEMPOTENT)
          .build();

  @Override
  public ExceptionHandler getForBucketAclCreate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclDelete(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclGet(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclUpdate(
      BucketAccessControl pb, Map<StorageRpc.Option, ?> optionsMap) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketAclList(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsCreate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsDelete(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsGet(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsUpdate(Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    // TODO: Include etag when it is supported by the library
    return optionsMap.containsKey(StorageRpc.Option.IF_METAGENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsList(Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsLockRetentionPolicy(
      Bucket pb, Map<StorageRpc.Option, ?> optionsMap) {
    // Always idempotent because IfMetagenerationMatch is required
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsGetIamPolicy(
      String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsSetIamPolicy(
      String bucket, Policy pb, Map<StorageRpc.Option, ?> optionsMap) {
    return pb.getEtag() != null ? IDEMPOTENT_HANDLER : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForBucketsTestIamPermissions(
      String bucket, List<String> permissions, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclCreate(ObjectAccessControl pb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclDelete(String pb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclGet(String pb) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclUpdate(ObjectAccessControl pb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForDefaultObjectAclList(String pb) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyCreate(String pb, Map<StorageRpc.Option, ?> optionsMap) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyDelete(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyGet(String accessId, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyUpdate(
      HmacKeyMetadata pb, Map<StorageRpc.Option, ?> optionsMap) {
    // TODO: Include etag when it is supported by the library
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForHmacKeyList(Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclCreate(ObjectAccessControl aclPb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclDelete(
      String bucket, String name, Long generation, String pb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclList(String bucket, String name, Long generation) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclGet(
      String bucket, String name, Long generation, String pb) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectAclUpdate(ObjectAccessControl aclPb) {
    return NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsCreate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    if (pb.getGeneration() != null && pb.getGeneration() == 0) {
      return IDEMPOTENT_HANDLER;
    }
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsDelete(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsGet(StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsUpdate(
      StorageObject pb, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_METAGENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsList(String bucket, Map<StorageRpc.Option, ?> optionsMap) {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsRewrite(RewriteRequest pb) {
    return pb.targetOptions.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForObjectsCompose(
      List<StorageObject> sources, StorageObject target, Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForResumableUploadSessionCreate(Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForResumableUploadSessionWrite(Map<StorageRpc.Option, ?> optionsMap) {
    return optionsMap.containsKey(StorageRpc.Option.IF_GENERATION_MATCH)
        ? IDEMPOTENT_RESUMABLE_UPLOAD_HANDLER
        : NON_IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getForServiceAccountGet(String pb) {
    return IDEMPOTENT_HANDLER;
  }

  private static class InterceptorImpl implements Interceptor {

    private final boolean idempotent;
    private final ImmutableSet<Integer> retryableCodes;

    private InterceptorImpl(boolean idempotent, ImmutableSet<Integer> retryableCodes) {
      this.idempotent = idempotent;
      this.retryableCodes = retryableCodes;
    }

    @Override
    public RetryResult afterEval(Exception exception, RetryResult retryResult) {
      return RetryResult.CONTINUE_EVALUATION;
    }

    @Override
    public RetryResult beforeEval(Exception exception) {

      // first check if an IO exception has been wrapped by a StorageException, fallback to
      // a general BaseServiceException to check status code
      if (exception instanceof StorageException) {
        StorageException storageException = (StorageException) exception;
        Throwable cause = storageException.getCause();
        //noinspection StatementWithEmptyBody
        if (cause instanceof GoogleJsonResponseException) {
          // this is handled by the case for BaseServiceException below
        } else if (cause instanceof HttpResponseException) {
          int code = ((HttpResponseException) cause).getStatusCode();
          return shouldRetryCode(code);
        } else if (cause instanceof IOException) {
          IOException ioException = (IOException) cause;
          return BaseServiceException.isRetryable(idempotent, ioException)
              ? RetryResult.RETRY
              : RetryResult.NO_RETRY;
        }
      }

      if (exception instanceof BaseServiceException) {
        int code = ((BaseServiceException) exception).getCode();
        return shouldRetryCode(code);
      }
      return RetryResult.CONTINUE_EVALUATION;
    }

    private RetryResult shouldRetryCode(int code) {
      if (retryableCodes.contains(code)) {
        return RetryResult.RETRY;
      } else {
        return RetryResult.NO_RETRY;
      }
    }
  }
}
