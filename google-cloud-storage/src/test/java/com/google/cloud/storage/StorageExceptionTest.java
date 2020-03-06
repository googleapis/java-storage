/*
 * Copyright 2015 Google LLC
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.ReadChannel;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StorageExceptionTest {
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private final Storage gcsStorage = mock(Storage.class);
  private final BlobId file = BlobId.of("blob", "attack");
  private final Blob metadata = mock(Blob.class);
  private final ReadChannel gcsChannel = mock(ReadChannel.class);

  @Before
  public void before() throws IOException {
    when(metadata.getSize()).thenReturn(42L);
    when(metadata.getGeneration()).thenReturn(2L);
    Storage.BlobGetOption options = Storage.BlobGetOption.fields(Storage.BlobField.GENERATION, Storage.BlobField.SIZE);
    List optionsList = Lists.newArrayList(options);
    when(gcsStorage.get(
            file, options))
            .thenReturn(metadata);
    when(gcsStorage.reader(file, Storage.BlobSourceOption.generationMatch(2L)))
            .thenReturn(gcsChannel);
    when(gcsChannel.isOpen()).thenReturn(true);
    gcsStorage.reader(file);
    verify(gcsStorage).get(
                    eq(file),
                    eq(Storage.BlobGetOption.fields(Storage.BlobField.GENERATION, Storage.BlobField.SIZE)));
    verify(gcsStorage).reader(eq(file), eq(Storage.BlobSourceOption.generationMatch(2L)));
  }

  @Test
  public void testStorageException() {
    StorageException exception = new StorageException(500, "message");
    assertEquals(500, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(502, "message");
    assertEquals(502, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(503, "message");
    assertEquals(503, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(504, "message");
    assertEquals(504, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(429, "message");
    assertEquals(429, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(408, "message");
    assertEquals(408, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertTrue(exception.isRetryable());

    exception = new StorageException(400, "message");
    assertEquals(400, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertFalse(exception.isRetryable());

    IOException cause = new SocketTimeoutException();
    exception = new StorageException(cause);
    assertNull(exception.getReason());
    assertNull(exception.getMessage());
    assertTrue(exception.isRetryable());
    assertSame(cause, exception.getCause());

    GoogleJsonError error = new GoogleJsonError();
    error.setCode(503);
    error.setMessage("message");
    exception = new StorageException(error);
    assertEquals(503, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertTrue(exception.isRetryable());

    exception = new StorageException(400, "message", cause);
    assertEquals(400, exception.getCode());
    assertEquals("message", exception.getMessage());
    assertNull(exception.getReason());
    assertFalse(exception.isRetryable());
    assertSame(cause, exception.getCause());

    HttpResponseException httpResponseException =
        new HttpResponseException.Builder(404, "Service Unavailable", new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(404, exception.getCode());
    assertFalse(exception.isRetryable());

    httpResponseException = new HttpResponseException.Builder(503, null, new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(503, exception.getCode());
    assertTrue(exception.isRetryable());

    httpResponseException = new HttpResponseException.Builder(502, null, new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(502, exception.getCode());
    assertTrue(exception.isRetryable());

    httpResponseException = new HttpResponseException.Builder(500, null, new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(500, exception.getCode());
    assertTrue(exception.isRetryable());

    httpResponseException = new HttpResponseException.Builder(429, null, new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(429, exception.getCode());
    assertTrue(exception.isRetryable());

    httpResponseException = new HttpResponseException.Builder(408, null, new HttpHeaders()).build();
    exception = new StorageException(httpResponseException);
    assertEquals(408, exception.getCode());
    assertTrue(exception.isRetryable());
  }

  @Test
  public void testReadRetry() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(1);
    when(gcsChannel.read(eq(buffer)))
            .thenThrow(
                    new StorageException(
                            new IOException(
                                    "outer",
                                    new IOException(
                                            "Connection closed prematurely: bytesRead = 33554432, Content-Length = 41943040"))))
            .thenReturn(1);
    verify(gcsChannel, times(2)).read(any(ByteBuffer.class));
  }


  @Test
  public void testTranslateAndThrow() throws Exception {
    Exception cause = new StorageException(503, "message");
    RetryHelperException exceptionMock = createMock(RetryHelperException.class);
    expect(exceptionMock.getCause()).andReturn(cause).times(2);
    replay(exceptionMock);
    try {
      StorageException.translateAndThrow(exceptionMock);
    } catch (BaseServiceException ex) {
      assertEquals(503, ex.getCode());
      assertEquals("message", ex.getMessage());
      assertTrue(ex.isRetryable());
    } finally {
      verify(exceptionMock);
    }
    cause = new IllegalArgumentException("message");
    exceptionMock = createMock(RetryHelperException.class);
    expect(exceptionMock.getMessage()).andReturn("message").times(1);
    expect(exceptionMock.getCause()).andReturn(cause).times(2);
    replay(exceptionMock);
    try {
      StorageException.translateAndThrow(exceptionMock);
    } catch (BaseServiceException ex) {
      assertEquals(StorageException.UNKNOWN_CODE, ex.getCode());
      assertEquals("message", ex.getMessage());
      assertFalse(ex.isRetryable());
      assertSame(cause, ex.getCause());
    } finally {
      verify(exceptionMock);
    }
  }
}
