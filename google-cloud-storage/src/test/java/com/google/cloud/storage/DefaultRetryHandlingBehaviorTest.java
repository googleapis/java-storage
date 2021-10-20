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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Validate the behavior of our new "default" ResultRetryAlgorithms against that of the legacy retry
 * handling behavior.
 */
@RunWith(Parameterized.class)
public final class DefaultRetryHandlingBehaviorTest {
  private static final String DEFAULT_MESSAGE = "err_default_message";

  private final Case c;
  private final DefaultStorageRetryStrategy defaultStrategy;
  private final StorageRetryStrategy legacyStrategy;

  @SuppressWarnings("deprecation")
  public DefaultRetryHandlingBehaviorTest(Case c) {
    this.c = c;
    defaultStrategy = new DefaultStorageRetryStrategy();
    legacyStrategy = StorageRetryStrategy.getLegacyStorageRetryStrategy();
  }

  /**
   * For a specified {@link Case}
   *
   * <ol>
   *   <li>Resolve the ResultRetryAlgorithm for the specific {@link HandlerCategory} for both
   *       default and legacy
   *   <li>Evaluate the retryability of the throwable {@link Case#getThrowable()} against both of
   *       the resolved {@code ResultRetryAlgorithm}s
   *   <li>Resolve the {@link Behavior} change if any between the two evaluation results
   *   <li>Assert the behavior change matches the {@link Case#expectedBehavior expected behavior}
   * </ol>
   */
  @SuppressWarnings("ConstantConditions")
  @Test
  public void validateBehavior() {
    ResultRetryAlgorithm<?> defaultAlgorithm = c.handlerCategory.apply(defaultStrategy);
    ResultRetryAlgorithm<?> legacyAlgorithm = c.handlerCategory.apply(legacyStrategy);
    Throwable throwable = c.getThrowable();

    boolean defaultShouldRetryResult = defaultAlgorithm.shouldRetry(throwable, null);
    boolean legacyShouldRetryResult = legacyAlgorithm.shouldRetry(throwable, null);

    Behavior actualBehavior = null;
    String message = null;
    boolean shouldRetry = c.getExpectRetry().shouldRetry;
    if (shouldRetry && !defaultShouldRetryResult && legacyShouldRetryResult) {
      actualBehavior = Behavior.defaultMoreStrict;
      message = "default is more strict";
    } else if (shouldRetry && !defaultShouldRetryResult && !legacyShouldRetryResult) {
      actualBehavior = Behavior.same;
      message = "both are rejecting when we want a retry";
    } else if (shouldRetry && defaultShouldRetryResult && legacyShouldRetryResult) {
      actualBehavior = Behavior.same;
      message = "both are allowing";
    } else if (shouldRetry && defaultShouldRetryResult && !legacyShouldRetryResult) {
      actualBehavior = Behavior.defaultMorePermissible;
      message = "default is more permissive";
    } else if (!shouldRetry && !defaultShouldRetryResult && legacyShouldRetryResult) {
      actualBehavior = Behavior.defaultMoreStrict;
      message = "default is more strict";
    } else if (!shouldRetry && !defaultShouldRetryResult && !legacyShouldRetryResult) {
      actualBehavior = Behavior.same;
      message = "both are rejecting as expected";
    } else if (!shouldRetry && defaultShouldRetryResult && legacyShouldRetryResult) {
      actualBehavior = Behavior.same;
      message = "both are too permissive";
    } else if (!shouldRetry && defaultShouldRetryResult && !legacyShouldRetryResult) {
      actualBehavior = Behavior.defaultMorePermissible;
      message = "default is too permissive";
    }

    assertWithMessage(message).that(actualBehavior).isEqualTo(c.expectedBehavior);
  }

  /** Resolve all the test cases and assert all permutations have a case defined. */
  @Parameters(name = "{0}")
  public static Collection<Object[]> testCases() {

    // define the list of cases to be validated
    List<Case> cases = getAllCases();

    /* perform validation of the defined list of cases to ensure all permutations are defined */

    // calculate all the possible permutations
    ImmutableSet<String> expectedTokens =
        Arrays.stream(HandlerCategory.values())
            .flatMap(
                handlerCategory ->
                    Arrays.stream(ThrowableCategory.values())
                        .map(throwableCategory -> token(throwableCategory, handlerCategory)))
            .collect(toImmutableSet());

    // calculate the actual defined permutations
    ImmutableSet<String> actualTokens =
        cases.stream()
            .map(c -> token(c.throwableCategory, c.handlerCategory))
            .collect(toImmutableSet());

    // calculate the difference if any between expected and actual, then sort and listify
    ImmutableList<String> difference =
        Sets.difference(expectedTokens, actualTokens).stream().sorted().collect(toImmutableList());

    // ensure all permutations are accounted for, reporting any that haven't been and providing
    // a stub which can be used to easily define them.
    assertWithMessage("Missing mappings for tokens").that(difference).isEmpty();

    // wrap our case in an array for ultimate passing to the constructor
    return cases.stream().map(c -> new Object[] {c}).collect(toImmutableList());
  }

  /**
   * Generate a token which represents a permutation for which a {@link Case} must be defined.
   *
   * <p>If a case is not defined, this value will be reported and functions as a stub to easily
   * define a new {@code Case}.
   */
  private static String token(ThrowableCategory t, HandlerCategory h) {
    return String.format(
        "new Case(ThrowableCategory.%s, HandlerCategory.%s, /*TODO*/ null, /*TODO*/ null)",
        t.name(), h.name());
  }

  /**
   * An individual case we want to validate.
   *
   * <p>Given a {@link HandlerCategory} and {@link ThrowableCategory} validate the retryability and
   * behavior between default and legacy handlers.
   */
  static final class Case {

    private final HandlerCategory handlerCategory;
    private final ThrowableCategory throwableCategory;
    private final ExpectRetry expectRetry;
    private final Behavior expectedBehavior;

    Case(
        ThrowableCategory throwableCategory,
        HandlerCategory handlerCategory,
        ExpectRetry expectRetry,
        Behavior expectedBehavior) {
      this.handlerCategory = handlerCategory;
      this.throwableCategory = throwableCategory;
      this.expectRetry = expectRetry;
      this.expectedBehavior = expectedBehavior;
    }

    Throwable getThrowable() {
      return throwableCategory.throwable;
    }

    public ExpectRetry getExpectRetry() {
      return expectRetry;
    }

    @Override
    public String toString() {
      return "Case{"
          + "throwableCategory="
          + throwableCategory
          + ", handlerCategory="
          + handlerCategory
          + ", expectRetry="
          + expectRetry
          + '}';
    }
  }

  /** Whether to expect a retry to happen or not */
  enum ExpectRetry {
    YES(true),
    NO(false);

    private final boolean shouldRetry;

    ExpectRetry(boolean shouldRetry) {
      this.shouldRetry = shouldRetry;
    }
  }

  /**
   * A category of handler type, and the ability to resolve the {@link ResultRetryAlgorithm} given a
   * {@link StorageRetryStrategy}
   */
  enum HandlerCategory implements Function<StorageRetryStrategy, ResultRetryAlgorithm<?>> {
    idempotent,
    nonidempotent;

    @Override
    public ResultRetryAlgorithm<?> apply(StorageRetryStrategy storageRetryStrategy) {
      switch (this) {
        case idempotent:
          return storageRetryStrategy.getIdempotentHandler();
        case nonidempotent:
          return storageRetryStrategy.getNonidempotentHandler();
        default:
          throw new IllegalStateException("Unmappable HandlerCategory: " + this.name());
      }
    }
  }

  /** Some states comparing behavior between default and legacy */
  enum Behavior {
    defaultMorePermissible,
    same,
    defaultMoreStrict
  }

  /**
   * A set of exceptions we want to validate behavior for.
   *
   * <p>This class is an enum for convenience of specifying a closed set, along with providing easy
   * to read names in code thereby forgoing the need to maintain a separate set of strings.
   */
  enum ThrowableCategory {
    socketTimeoutException(C.SOCKET_TIMEOUT_EXCEPTION),
    socketException(C.SOCKET_EXCEPTION),
    sslException(C.SSL_EXCEPTION),
    sslException_connectionShutdown(C.SSL_EXCEPTION_CONNECTION_SHUTDOWN),
    sslHandshakeException(C.SSL_HANDSHAKE_EXCEPTION),
    sslHandshakeException_causedByCertificateException(
        C.SSL_HANDSHAKE_EXCEPTION_CERTIFICATE_EXCEPTION),
    insufficientData(C.INSUFFICIENT_DATA_WRITTEN),
    errorWritingRequestBody(C.ERROR_WRITING_REQUEST_BODY),
    httpResponseException_401(C.HTTP_401),
    httpResponseException_403(C.HTTP_403),
    httpResponseException_404(C.HTTP_404),
    httpResponseException_408(C.HTTP_409),
    httpResponseException_429(C.HTTP_429),
    httpResponseException_500(C.HTTP_500),
    httpResponseException_502(C.HTTP_502),
    httpResponseException_503(C.HTTP_503),
    httpResponseException_504(C.HTTP_504),
    storageException_httpResponseException_401(new StorageException(C.HTTP_401)),
    storageException_httpResponseException_403(new StorageException(C.HTTP_403)),
    storageException_httpResponseException_404(new StorageException(C.HTTP_404)),
    storageException_httpResponseException_408(new StorageException(C.HTTP_409)),
    storageException_httpResponseException_429(new StorageException(C.HTTP_429)),
    storageException_httpResponseException_500(new StorageException(C.HTTP_500)),
    storageException_httpResponseException_502(new StorageException(C.HTTP_502)),
    storageException_httpResponseException_503(new StorageException(C.HTTP_503)),
    storageException_httpResponseException_504(new StorageException(C.HTTP_504)),
    storageException_googleJsonError_401(new StorageException(C.JSON_401)),
    storageException_googleJsonError_403(new StorageException(C.JSON_403)),
    storageException_googleJsonError_404(new StorageException(C.JSON_404)),
    storageException_googleJsonError_408(new StorageException(C.JSON_408)),
    storageException_googleJsonError_429(new StorageException(C.JSON_429)),
    storageException_googleJsonError_500(new StorageException(C.JSON_500)),
    storageException_googleJsonError_502(new StorageException(C.JSON_502)),
    storageException_googleJsonError_503(new StorageException(C.JSON_503)),
    storageException_googleJsonError_504(new StorageException(C.JSON_504)),
    storageException_socketTimeoutException(new StorageException(C.SOCKET_TIMEOUT_EXCEPTION)),
    storageException_socketException(new StorageException(C.SOCKET_EXCEPTION)),
    storageException_sslException(new StorageException(C.SSL_EXCEPTION)),
    storageException_sslException_connectionShutdown(
        new StorageException(C.SSL_EXCEPTION_CONNECTION_SHUTDOWN)),
    storageException_sslHandshakeException(new StorageException(C.SSL_HANDSHAKE_EXCEPTION)),
    storageException_sslHandshakeException_causedByCertificateException(
        new StorageException(C.SSL_HANDSHAKE_EXCEPTION_CERTIFICATE_EXCEPTION)),
    storageException_insufficientData(new StorageException(C.INSUFFICIENT_DATA_WRITTEN)),
    storageException_errorWritingRequestBody(new StorageException(C.ERROR_WRITING_REQUEST_BODY)),
    illegalArgumentException(C.ILLEGAL_ARGUMENT_EXCEPTION),
    storageException_illegalArgumentException(
        StorageException.coalesce(C.ILLEGAL_ARGUMENT_EXCEPTION)),
    storageException_0_internalError(
        new StorageException(0, "internalError", "internalError", null)),
    storageException_0_connectionClosedPrematurely(
        new StorageException(
            0, "connectionClosedPrematurely", "connectionClosedPrematurely", null)),
    ;

    private final Throwable throwable;

    ThrowableCategory(Throwable throwable) {
      this.throwable = throwable;
    }

    public Throwable getThrowable() {
      return throwable;
    }

    /**
     * A class of constants for use by the containing enum.
     *
     * <p>Enums can't have static fields, so we use this class to hold constants which are used by
     * the enum values.
     */
    private static final class C {
      private static final SocketTimeoutException SOCKET_TIMEOUT_EXCEPTION =
          new SocketTimeoutException();
      private static final SocketException SOCKET_EXCEPTION = new SocketException();
      private static final SSLException SSL_EXCEPTION = new SSLException("unknown");
      private static final SSLException SSL_EXCEPTION_CONNECTION_SHUTDOWN =
          new SSLException("Connection has been shutdown: asdf");
      private static final SSLHandshakeException SSL_HANDSHAKE_EXCEPTION =
          newSslHandshakeExceptionWithCause(new SSLProtocolException(DEFAULT_MESSAGE));
      private static final SSLHandshakeException SSL_HANDSHAKE_EXCEPTION_CERTIFICATE_EXCEPTION =
          newSslHandshakeExceptionWithCause(new CertificateException());
      private static final IOException INSUFFICIENT_DATA_WRITTEN =
          new IOException("insufficient data written");
      private static final IOException ERROR_WRITING_REQUEST_BODY =
          new IOException("Error writing request body to server");
      private static final HttpResponseException HTTP_401 =
          newHttpResponseException(401, "Unauthorized");
      private static final HttpResponseException HTTP_403 =
          newHttpResponseException(403, "Forbidden");
      private static final HttpResponseException HTTP_404 =
          newHttpResponseException(404, "Not Found");
      private static final HttpResponseException HTTP_409 =
          newHttpResponseException(408, "Request Timeout");
      private static final HttpResponseException HTTP_429 =
          newHttpResponseException(429, "Too Many Requests");
      private static final HttpResponseException HTTP_500 =
          newHttpResponseException(500, "Internal Server Error");
      private static final HttpResponseException HTTP_502 =
          newHttpResponseException(502, "Bad Gateway");
      private static final HttpResponseException HTTP_503 =
          newHttpResponseException(503, "Service Unavailable");
      private static final HttpResponseException HTTP_504 =
          newHttpResponseException(504, "Gateway Timeout");
      private static final GoogleJsonError JSON_401 = newGoogleJsonError(401, "Unauthorized");
      private static final GoogleJsonError JSON_403 = newGoogleJsonError(403, "Forbidden");
      private static final GoogleJsonError JSON_404 = newGoogleJsonError(404, "Not Found");
      private static final GoogleJsonError JSON_408 = newGoogleJsonError(408, "Request Timeout");
      private static final GoogleJsonError JSON_429 = newGoogleJsonError(429, "Too Many Requests");
      private static final GoogleJsonError JSON_500 =
          newGoogleJsonError(500, "Internal Server Error");
      private static final GoogleJsonError JSON_502 = newGoogleJsonError(502, "Bad Gateway");
      private static final GoogleJsonError JSON_503 =
          newGoogleJsonError(503, "Service Unavailable");
      private static final GoogleJsonError JSON_504 = newGoogleJsonError(504, "Gateway Timeout");
      private static final IllegalArgumentException ILLEGAL_ARGUMENT_EXCEPTION =
          new IllegalArgumentException("illegal argument");

      private static HttpResponseException newHttpResponseException(
          int httpStatusCode, String name) {
        return new HttpResponseException.Builder(httpStatusCode, name, new HttpHeaders()).build();
      }

      private static GoogleJsonError newGoogleJsonError(int code, String message) {
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(code);
        error.setMessage(message);
        return error;
      }

      private static SSLHandshakeException newSslHandshakeExceptionWithCause(Throwable cause) {
        SSLHandshakeException sslHandshakeException = new SSLHandshakeException(DEFAULT_MESSAGE);
        Throwable throwable = sslHandshakeException.initCause(cause);
        return (SSLHandshakeException) throwable;
      }
    }
  }

  /**
   * A corralled method which allows us to move the individual cases away from the rest of the code
   * since our code formatter is very strict and seems to feel 475 lines of code at 100 columns is
   * better than 90 lines at 200 columns.
   *
   * <p>this method returns a list that essentially is a table of where each row is an individual
   * test case
   *
   * <table>
   *   <tr>
   *     <th>{@link ThrowableCategory throwable category}</th>
   *     <th>{@link HandlerCategory handler category}</th>
   *     <th>{@link ExpectRetry whether retry is expected}</th>
   *     <th>{@link Behavior whether the expect behavior comparison is}</th>
   *   </tr>
   *   <tr>
   *     <td>{@link ThrowableCategory#storageException_googleJsonError_500 storageException_googleJsonError_500}</td>
   *     <td>{@link HandlerCategory#nonidempotent nonidempotent}</td>
   *     <td>{@link ExpectRetry#NO NO}</td>
   *     <td>{@link Behavior#defaultMoreStrict defaultMoreStrict}</td>
   *   </tr>
   * </table>
   */
  private static ImmutableList<Case> getAllCases() {
    return ImmutableList.<Case>builder()
        .add(
            new Case(
                ThrowableCategory.errorWritingRequestBody,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.errorWritingRequestBody,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_401,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_401,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_403,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_403,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_404,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_404,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_408,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_408,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_429,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_429,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_500,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_500,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_502,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_502,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_503,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_503,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.httpResponseException_504,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.httpResponseException_504,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.illegalArgumentException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.illegalArgumentException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.insufficientData,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.insufficientData,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.socketException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.socketException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.socketTimeoutException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.socketTimeoutException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslException_connectionShutdown,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.sslException_connectionShutdown,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslHandshakeException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.defaultMorePermissible),
            new Case(
                ThrowableCategory.sslHandshakeException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslHandshakeException_causedByCertificateException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.sslHandshakeException_causedByCertificateException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_errorWritingRequestBody,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_errorWritingRequestBody,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_401,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_401,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_403,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_403,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_404,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_404,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_408,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_408,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_429,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_429,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_500,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_500,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_502,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_502,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_503,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_503,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_googleJsonError_504,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_googleJsonError_504,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_401,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_401,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_403,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_403,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_404,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_404,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_408,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_408,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_429,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_429,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_500,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_500,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_502,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_502,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_503,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_503,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_httpResponseException_504,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_httpResponseException_504,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_illegalArgumentException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_illegalArgumentException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_insufficientData,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_insufficientData,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_socketException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_socketException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_socketTimeoutException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_socketTimeoutException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_sslException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_sslException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_sslException_connectionShutdown,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_sslException_connectionShutdown,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_sslHandshakeException,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_sslHandshakeException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory
                    .storageException_sslHandshakeException_causedByCertificateException,
                HandlerCategory.idempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory
                    .storageException_sslHandshakeException_causedByCertificateException,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_0_connectionClosedPrematurely,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_0_connectionClosedPrematurely,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict),
            new Case(
                ThrowableCategory.storageException_0_internalError,
                HandlerCategory.idempotent,
                ExpectRetry.YES,
                Behavior.same),
            new Case(
                ThrowableCategory.storageException_0_internalError,
                HandlerCategory.nonidempotent,
                ExpectRetry.NO,
                Behavior.defaultMoreStrict))
        .build();
  }
}
