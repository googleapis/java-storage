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

import com.google.api.client.http.HttpResponseException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.ExceptionHandler.Interceptor;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Set;

final class DefaultStorageExceptionHandlerFactory implements StorageExceptionHandlerFactory {

  private static final Interceptor INTERCEPTOR_IDEMPOTENT =
      new InterceptorImpl(true, StorageException.RETRYABLE_ERRORS);
  private static final Interceptor INTERCEPTOR_NON_IDEMPOTENT =
      new InterceptorImpl(false, ImmutableSet.of());

  private static final ExceptionHandler IDEMPOTENT_HANDLER = newHandler(INTERCEPTOR_IDEMPOTENT);
  private static final ExceptionHandler NON_IDEMPOTENT_HANDLER =
      newHandler(INTERCEPTOR_NON_IDEMPOTENT);

  @Override
  public ExceptionHandler getIdempotentHandler() {
    return IDEMPOTENT_HANDLER;
  }

  @Override
  public ExceptionHandler getNonidempotentHandler() {
    return NON_IDEMPOTENT_HANDLER;
  }

  private static ExceptionHandler newHandler(Interceptor interceptor) {
    return ExceptionHandler.newBuilder()
        .retryOn(BaseServiceException.class)
        .retryOn(IOException.class)
        .addInterceptors(interceptor)
        .build();
  }

  private static class InterceptorImpl implements Interceptor {

    private final boolean idempotent;
    private final ImmutableSet<BaseServiceException.Error> retryableErrors;

    private InterceptorImpl(boolean idempotent, Set<BaseServiceException.Error> retryableErrors) {
      this.idempotent = idempotent;
      this.retryableErrors = ImmutableSet.copyOf(retryableErrors);
    }

    @Override
    public RetryResult afterEval(Exception exception, RetryResult retryResult) {
      return RetryResult.CONTINUE_EVALUATION;
    }

    @Override
    public RetryResult beforeEval(Exception exception) {
      Throwable t = exception;

      if (t instanceof BaseServiceException) {
        BaseServiceException storageException = (BaseServiceException) t;
        Throwable cause = storageException.getCause();
        // if the cause of the exception is an IOException lift it before we continue
        // evaluation
        if (cause instanceof IOException) {
          t = cause;
        }
      }

      if (t instanceof BaseServiceException) {
        BaseServiceException baseServiceException = (BaseServiceException) t;
        int code = baseServiceException.getCode();
        String reason = baseServiceException.getReason();
        return shouldRetryCode(code, reason);
      } else if (t instanceof HttpResponseException) {
        int code = ((HttpResponseException) t).getStatusCode();
        return shouldRetryCode(code, null);
      } else if (t instanceof IOException) {
        IOException ioException = (IOException) t;
        return BaseServiceException.isRetryable(idempotent, ioException)
            ? RetryResult.RETRY
            : RetryResult.NO_RETRY;
      }
      return RetryResult.CONTINUE_EVALUATION;
    }

    private RetryResult shouldRetryCode(Integer code, String reason) {
      if (BaseServiceException.isRetryable(code, reason, idempotent, retryableErrors)) {
        return RetryResult.RETRY;
      } else {
        return RetryResult.NO_RETRY;
      }
    }
  }
}
