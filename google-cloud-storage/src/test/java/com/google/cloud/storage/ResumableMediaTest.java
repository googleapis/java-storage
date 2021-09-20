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

import static org.junit.Assert.assertNotNull;

import com.google.cloud.BaseService;
import com.google.cloud.ExceptionHandler;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

public final class ResumableMediaTest {
  private static final String SIGNED_URL =
      "http://www.test.com/test-bucket/test1.txt?GoogleAccessId=testClient-test@test.com&Expires=1553839761&Signature=MJUBXAZ7";

  private final ExceptionHandler createResultExceptionHandler = BaseService.EXCEPTION_HANDLER;

  @Test
  public void startUploadForSignedUrl_expectStorageException_whenUrlInvalid() throws Exception {
    try {
      ResumableMedia.startUploadForSignedUrl(
              StorageOptions.newBuilder().build(),
              new URL(SIGNED_URL),
              createResultExceptionHandler)
          .get();
      Assert.fail();
    } catch (StorageException ex) {
      assertNotNull(ex.getMessage());
    }
  }
}
