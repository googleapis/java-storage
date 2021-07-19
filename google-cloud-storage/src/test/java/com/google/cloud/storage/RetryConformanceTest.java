/*
 * Copyright 2019 Google LLC
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

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RetryConformanceTest {

  private static final String PROJECT_ID = "myproj";
  private static final String BUCKET_NAME = "test-bucket";
  private static final String OBJECT_NAME = "file.txt";
  private static final Map<String, BiFunc> funcMap = new ImmutableMap.Builder<String, BiFunc>()
      .put("storage.objects.get", new BiFunc() {
        @Override
        public void apply(Storage s, TestCaseConfig c) {
          final BlobId blobId;
          if (c.preconditionsProvided) {
            blobId = BlobId.of(BUCKET_NAME, OBJECT_NAME, 0L);
          } else {
            blobId = BlobId.of(BUCKET_NAME, OBJECT_NAME);
          }
          Blob blob = s.get(blobId);
          assertNotNull(blob);
          Map<String, String> metadata = blob.getMetadata();
        }
      })
      .build();

  private final TestCaseConfig testCaseConfig;

  /**
   * @param description Not used by the test, but used by the parameterized test runner as the name
   *     of the test.
   */
  public RetryConformanceTest(@SuppressWarnings("unused") String description, TestCaseConfig testCaseConfig) {
    this.testCaseConfig = testCaseConfig;
  }

  @Before
  public void setUp() throws Exception {
    Storage setupStorage = newStorage(false);
    Bucket bucket = setupStorage.create(BucketInfo.of(BUCKET_NAME));
    bucket.create(OBJECT_NAME, "abcdef".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void test() {
    BiFunc func = funcMap.get(testCaseConfig.method);
    assertNotNull(String.format("Unable to find func for method '%s'", testCaseConfig.method), func);
    Storage storage = newStorage(true);
    if (testCaseConfig.expectSuccess) {
      func.apply(storage, testCaseConfig);
    } else {
      try {
        func.apply(storage, testCaseConfig);
        fail("expected failure, but succeeded");
      } catch (Exception e) {
        // pass
      }
    }
  }

  private Storage newStorage(boolean forTest) {
    StorageOptions.Builder builder = StorageOptions.newBuilder()
        .setHost("http://localhost:9000");
    if (forTest) {
      builder
          .setHeaderProvider(new FixedHeaderProvider() {
            @Override
            public Map<String, String> getHeaders() {
              return ImmutableMap.of(
                  "x-goog-testbench-instructions", testCaseConfig.instruction,
                  "User-Agent", "custom-user-agent"
              );
            }
          })
          .setRetrySettings(
              RetrySettings.newBuilder()
                  .setMaxAttempts(testCaseConfig.forcedRetries + 1)
                  .build()
          );
    } else {
      builder
          .setHeaderProvider(new FixedHeaderProvider() {
            @Override
            public Map<String, String> getHeaders() {
              return ImmutableMap.of(
                  "User-Agent", "java-conformance-tests/"
              );
            }
          })
          .setRetrySettings(
              RetrySettings.newBuilder()
                  .setMaxAttempts(1)
                  .build()
          );
    }
    return builder.build().getService();
  }

  /**
   * Load all of the tests and return a {@code Collection<Object[]>} representing the set of tests.
   * Each entry in the returned collection is the set of parameters to the constructor of this test
   * class.
   *
   * <p>The results of this method will then be run by JUnit's Parameterized test runner
   */
  @Parameters(name = "{0}")
  public static Collection<Object[]> testCases() throws IOException {

    List<RetryCase> tests = newArrayList(
        new RetryCase(
            "retry idempotent operations",
            newArrayList("return-503", "return-504"),
            newArrayList("storage.objects.get"),
            4,
            false,
            true
        ),
        new RetryCase(
            "retry Non-idempotent operations",
            newArrayList("return-503", "return-504"),
            newArrayList("storage.objects.patch"),
            4,
            true,
            true
        ),
        new RetryCase(
            "retry Non-idempotent operations",
            newArrayList("return-503", "return-504"),
            newArrayList("storage.objects.get"),
            1,
            false,
            false
        )
    );
    List<Object[]> data = new ArrayList<>(tests.size());
    for (RetryCase test : tests) {
      for (String i : test.instructions) {
        for (String m : test.methods) {
          String testName = String.format("[%s] %s", test.description, i);
          data.add(new Object[] {testName, new TestCaseConfig(
              i, m, test.forcedRetries, test.preconditionsProvided, test.expectSuccess
          )});
        }
      }
    }
    return data;
  }

  private static final class RetryCase {
    private final String description;
    private final List<String> instructions;
    private final List<String> methods;
    private final int forcedRetries;
    private final boolean preconditionsProvided;
    private final boolean expectSuccess;

    public RetryCase(String description, List<String> instructions,
        List<String> methods, int forcedRetries, boolean preconditionsProvided,
        boolean expectSuccess) {
      this.description = description;
      this.instructions = instructions;
      this.methods = methods;
      this.forcedRetries = forcedRetries;
      this.preconditionsProvided = preconditionsProvided;
      this.expectSuccess = expectSuccess;
    }
  }

  private static final class RetryCase2 {
    private String description;
    private List<Assertion> asseretions;
    private List<Scenario> scenarios;
    private int forcedRetries;
    private boolean preconditionsProvided;
    private boolean expectSuccess;
  }

  private static final class Scenario {
    private String instruction;
    private String method;
    private List<Assertion> assertions;
  }

  private static final class Assertion {
  }

  private static final class TestCaseConfig {
    private final String instruction;
    private final String method;
    private final int forcedRetries;
    private final boolean preconditionsProvided;
    private final boolean expectSuccess;

    public TestCaseConfig(String instruction, String method, int forcedRetries,
        boolean preconditionsProvided, boolean expectSuccess) {
      this.instruction = instruction;
      this.method = method;
      this.forcedRetries = forcedRetries;
      this.preconditionsProvided = preconditionsProvided;
      this.expectSuccess = expectSuccess;
    }
  }

  private interface BiFunc {
    void apply(Storage s, TestCaseConfig c);
  }
}
