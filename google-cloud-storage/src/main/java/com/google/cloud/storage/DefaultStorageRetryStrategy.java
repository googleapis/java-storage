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

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.ExceptionHandler;
import com.google.cloud.ExceptionHandler.Interceptor;
import com.google.common.collect.ImmutableSet;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.util.Set;

final class DefaultStorageRetryStrategy implements StorageRetryStrategy {

  private static final long serialVersionUID = -6145057244885961913L;

  private static final Interceptor INTERCEPTOR_IDEMPOTENT =
      new InterceptorImpl(true, StorageException.RETRYABLE_ERRORS);
  private static final Interceptor INTERCEPTOR_NON_IDEMPOTENT =
      new InterceptorImpl(false, ImmutableSet.of());

  private static final ExceptionHandler IDEMPOTENT_HANDLER =
      newHandler(new EmptyJsonParsingExceptionInterceptor(), INTERCEPTOR_IDEMPOTENT);
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

  private static ExceptionHandler newHandler(Interceptor... interceptors) {
    return ExceptionHandler.newBuilder().addInterceptors(interceptors).build();
  }

  private static class InterceptorImpl implements BaseInterceptor {

    private static final long serialVersionUID = -5153236691367895096L;
    private final boolean idempotent;
    private final ImmutableSet<BaseServiceException.Error> retryableErrors;

    private InterceptorImpl(boolean idempotent, Set<BaseServiceException.Error> retryableErrors) {
      this.idempotent = idempotent;
      this.retryableErrors = ImmutableSet.copyOf(retryableErrors);
    }

    @Override
    public RetryResult beforeEval(Exception exception) {
      if (exception instanceof BaseServiceException) {
        BaseServiceException baseServiceException = (BaseServiceException) exception;
        return deepShouldRetry(baseServiceException);
      } else if (exception instanceof HttpResponseException) {
        int code = ((HttpResponseException) exception).getStatusCode();
        return shouldRetryCodeReason(code, null);
      } else if (exception instanceof IOException) {
        IOException ioException = (IOException) exception;
        return shouldRetryIOException(ioException);
      }
      return RetryResult.CONTINUE_EVALUATION;
    }

    private RetryResult shouldRetryCodeReason(Integer code, String reason) {
      if (BaseServiceException.isRetryable(code, reason, idempotent, retryableErrors)) {
        return RetryResult.RETRY;
      } else {
        return RetryResult.NO_RETRY;
      }
    }

    private RetryResult shouldRetryIOException(IOException ioException) {
      if (ioException instanceof JsonEOFException && idempotent) { // Jackson
        return RetryResult.RETRY;
      } else if (ioException instanceof MalformedJsonException && idempotent) { // Gson
        return RetryResult.RETRY;
      } else if (BaseServiceException.isRetryable(idempotent, ioException)) {
        return RetryResult.RETRY;
      } else {
        return RetryResult.NO_RETRY;
      }
    }

    private RetryResult deepShouldRetry(BaseServiceException baseServiceException) {
      if (baseServiceException.getCode() == BaseServiceException.UNKNOWN_CODE
          && baseServiceException.getReason() == null) {
        final Throwable cause = baseServiceException.getCause();
        if (cause instanceof IOException) {
          IOException ioException = (IOException) cause;
          return shouldRetryIOException(ioException);
        }
      }

      int code = baseServiceException.getCode();
      String reason = baseServiceException.getReason();
      return shouldRetryCodeReason(code, reason);
    }
  }

  private static final class EmptyJsonParsingExceptionInterceptor implements BaseInterceptor {
    private static final long serialVersionUID = -3320984020388043628L;

    @Override
    public RetryResult beforeEval(Exception exception) {
      if (exception instanceof IllegalArgumentException) {
        IllegalArgumentException illegalArgumentException = (IllegalArgumentException) exception;
        if (illegalArgumentException.getMessage().equals("no JSON input found")) {
          return RetryResult.RETRY;
        }
      }
      return RetryResult.CONTINUE_EVALUATION;
    }
  }

  private interface BaseInterceptor extends Interceptor {
    @Override
    default RetryResult afterEval(Exception exception, RetryResult retryResult) {
      return RetryResult.CONTINUE_EVALUATION;
    }
  }
}
