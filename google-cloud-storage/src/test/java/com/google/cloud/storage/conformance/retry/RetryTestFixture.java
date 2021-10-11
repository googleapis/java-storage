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

import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.cloud.NoCredentials;
import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import com.google.cloud.storage.PackagePrivateMethodWorkarounds;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.conformance.retry.TestBench.RetryTestResource;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit 4 {@link TestRule} which integrates with {@link TestBench} and {@link
 * TestRetryConformance} to provide transparent lifecycle integration of setup/validation/cleanup of
 * {@code /retry_test} resources. This rule expects to be bound as an {@link org.junit.Rule @Rule}
 * field.
 *
 * <p>Provides pre-configured instances of {@link Storage} for setup/teardown & test.
 */
final class RetryTestFixture implements TestRule {
  private static final Logger LOGGER = Logger.getLogger(RetryTestFixture.class.getName());

  private final CleanupStrategy cleanupStrategy;
  private final TestBench testBench;
  private final TestRetryConformance testRetryConformance;

  private RetryTestResource retryTest;
  private Storage nonTestStorage;
  private Storage testStorage;

  RetryTestFixture(
      CleanupStrategy cleanupStrategy,
      TestBench testBench,
      TestRetryConformance testRetryConformance) {
    this.cleanupStrategy = cleanupStrategy;
    this.testBench = testBench;
    this.testRetryConformance = testRetryConformance;
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
        boolean testSkipped = false;
        try {
          LOGGER.fine("Setting up retry_test resource...");
          RetryTestResource retryTestResource =
              newRetryTestResource(
                  testRetryConformance.getMethod(), testRetryConformance.getInstruction());
          retryTest = testBench.createRetryTest(retryTestResource);
          LOGGER.fine("Setting up retry_test resource complete");
          base.evaluate();
          testSuccess = true;
        } catch (AssumptionViolatedException e) {
          testSkipped = true;
          throw e;
        } finally {
          LOGGER.fine("Verifying end state of retry_test resource...");
          try {
            if (retryTest != null) {
              RetryTestResource postTestState = testBench.getRetryTest(retryTest);
              if (testSuccess) {
                assertTrue("expected completed to be true, but was false", postTestState.completed);
              }
            }
          } finally {
            LOGGER.fine("Verifying end state of retry_test resource complete");
            if ((shouldCleanup(testSuccess, testSkipped)) && retryTest != null) {
              testBench.deleteRetryTest(retryTest);
              retryTest = null;
            }
          }
        }
      }
    };
  }

  private boolean shouldCleanup(boolean testSuccess, boolean testSkipped) {
    return cleanupStrategy == CleanupStrategy.ALWAYS
        || ((testSuccess || testSkipped) && cleanupStrategy == CleanupStrategy.ONLY_ON_SUCCESS);
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
            .setHost(testBench.getBaseUri())
            .setCredentials(NoCredentials.getInstance())
            .setProjectId(testRetryConformance.getProjectId());
    builder = PackagePrivateMethodWorkarounds.useNewRetryAlgorithmManager(builder);
    if (forTest) {
      builder
          .setHeaderProvider(
              new FixedHeaderProvider() {
                @Override
                public Map<String, String> getHeaders() {
                  return ImmutableMap.of(
                      "x-retry-test-id", retryTest.id, "User-Agent", "java-conformance-tests/");
                }
              })
          .setRetrySettings(
              StorageOptions.getDefaultRetrySettings().toBuilder().setMaxAttempts(3).build());
    } else {
      builder
          .setHeaderProvider(
              new FixedHeaderProvider() {
                @Override
                public Map<String, String> getHeaders() {
                  return ImmutableMap.of("User-Agent", "java-conformance-tests/");
                }
              })
          .setRetrySettings(
              StorageOptions.getDefaultRetrySettings().toBuilder().setMaxAttempts(1).build());
    }
    return builder.build().getService();
  }
}
