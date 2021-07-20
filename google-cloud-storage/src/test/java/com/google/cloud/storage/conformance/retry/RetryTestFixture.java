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

import static org.junit.Assert.assertTrue;

import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.cloud.NoCredentials;
import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.TestServer.RetryTestResource;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

final class RetryTestFixture implements TestRule {

  private final CleanupStrategy cleanupStrategy;
  private final TestServer testServer;
  private final TestCaseConfig testCaseConfig;

  private RetryTestResource retryTest;
  private Storage nonTestStorage;
  private Storage testStorage;

  RetryTestFixture(CleanupStrategy cleanupStrategy, TestServer testServer,
      TestCaseConfig testCaseConfig) {
    this.cleanupStrategy = cleanupStrategy;
    this.testServer = testServer;
    this.testCaseConfig = testCaseConfig;
  }

  public Storage getNonTestStorage() {
    if (nonTestStorage == null) {
      this.nonTestStorage = newStorage(false);
    }
    return nonTestStorage;
  }

  public Storage getTestStorage() {
    if (testStorage == null) {
      this.testStorage = newStorage(true);
    }
    return testStorage;
  }


  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        boolean testSuccess = false;
        try {
          RetryTestResource retryTestResource =
              newRetryTestResource(testCaseConfig.getMethod(), testCaseConfig.getInstruction());
          retryTest = testServer.createRetryTest(retryTestResource);
          base.evaluate();
          testSuccess = true;
        } finally {
          try {
            if (testSuccess && retryTest != null) {
              RetryTestResource postTestState = testServer.getRetryTest(retryTest);
              assertTrue("expected completed to be true, but was false", postTestState.completed);
            }
          } finally {
            if (shouldCleanup(testSuccess) && retryTest != null) {
              testServer.deleteRetryTest(retryTest);
              retryTest = null;
            }
          }
        }
      }
    };
  }

  private boolean shouldCleanup(boolean testSuccess) {
    return cleanupStrategy == CleanupStrategy.ALWAYS
        || (testSuccess && cleanupStrategy == CleanupStrategy.ONLY_ON_SUCCESS);
  }

  private static RetryTestResource newRetryTestResource(Method m, InstructionList l) {
    RetryTestResource resource = new RetryTestResource();
    resource.instructions = new JsonObject();
    JsonArray instructions = new JsonArray();
    for (String s : l.getInstructionsList()) {
      instructions.add(s);
    }
    resource.instructions.add(m.getName(), instructions);
    return resource;
  }

  private Storage newStorage(boolean forTest) {
    StorageOptions.Builder builder =
        StorageOptions.newBuilder()
            .setHost(testServer.getHost())
            .setCredentials(NoCredentials.getInstance())
            .setProjectId("conformance-tests");
    if (forTest) {
      builder.setHeaderProvider(
          new FixedHeaderProvider() {
            @Override
            public Map<String, String> getHeaders() {
              return ImmutableMap.of("x-goog-testbench-instructions", retryTest.id);
            }
          });
    } else {
      builder
          .setHeaderProvider(
              new FixedHeaderProvider() {
                @Override
                public Map<String, String> getHeaders() {
                  return ImmutableMap.of("User-Agent", "java-conformance-tests/");
                }
              })
          .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build());
    }
    return builder.build().getService();
  }

  enum CleanupStrategy {
    ALWAYS,
    ONLY_ON_SUCCESS,
    NEVER
  }
}
