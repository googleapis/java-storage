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

import static com.google.cloud.storage.PackagePrivateMethodWorkarounds.blobCopyWithStorage;
import static com.google.cloud.storage.PackagePrivateMethodWorkarounds.bucketCopyWithStorage;
import static com.google.cloud.storage.conformance.retry.Ctx.ctx;
import static com.google.cloud.storage.conformance.retry.State.empty;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertNotNull;

import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import com.google.cloud.conformance.storage.v1.RetryTest;
import com.google.cloud.conformance.storage.v1.RetryTests;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.conformance.retry.Functions.CtxFunction;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.AssumptionViolatedException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Load and dynamically generate a series of test cases to verify if the {@link Storage} and
 * associated high level classes adhere to expected retry behavior.
 *
 * <p>This class dynamically generates test cases based on resources from the
 * google-cloud-conformance-tests artifact and a set of defined mappings from {@link
 * RpcMethodMappings}.
 */
@RunWith(ParallelParameterized.class)
public class ITRetryConformanceTest {
  private static final Logger LOGGER = Logger.getLogger(ITRetryConformanceTest.class.getName());

  @ClassRule public static final TestBench TEST_BENCH = TestBench.newBuilder().build();

  @Rule(order = 1)
  public final GracefulConformanceEnforcement gracefulConformanceEnforcement;

  @Rule(order = 2)
  public final RetryTestFixture retryTestFixture;

  private final TestRetryConformance testRetryConformance;
  private final RpcMethodMapping mapping;

  public ITRetryConformanceTest(
      TestRetryConformance testRetryConformance, RpcMethodMapping mapping) {
    this.testRetryConformance = testRetryConformance;
    this.mapping = mapping;
    this.gracefulConformanceEnforcement =
        new GracefulConformanceEnforcement(testRetryConformance.getTestName());
    this.retryTestFixture =
        new RetryTestFixture(CleanupStrategy.ALWAYS, TEST_BENCH, testRetryConformance);
  }

  /**
   * Run an individual test case. 1. Create two storage clients, one for setup/teardown and one for
   * test execution 2. Run setup 3. Run test 4. Run teardown
   */
  @Test
  public void test() throws Throwable {
    Storage nonTestStorage = retryTestFixture.getNonTestStorage();
    Storage testStorage = retryTestFixture.getTestStorage();

    Ctx ctx = ctx(nonTestStorage, empty());

    LOGGER.fine("Running setup...");
    Ctx postSetupCtx =
        mapping.getSetup().apply(ctx, testRetryConformance).leftMap(s -> testStorage);
    LOGGER.fine("Running setup complete");

    LOGGER.fine("Running test...");
    Ctx postTestCtx =
        getReplaceStorageInObjectsFromCtx()
            .andThen(mapping.getTest())
            .apply(postSetupCtx, testRetryConformance)
            .leftMap(s -> nonTestStorage);
    LOGGER.fine("Running test complete");

    LOGGER.fine("Running teardown...");
    getReplaceStorageInObjectsFromCtx()
        .andThen(mapping.getTearDown())
        .apply(postTestCtx, testRetryConformance);
    LOGGER.fine("Running teardown complete");
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
    RetryTestCaseResolver resolver =
        RetryTestCaseResolver.newBuilder()
            .setRetryTestsJsonResourcePath(
                "com/google/cloud/conformance/storage/v1/retry_tests.json")
            .setMappings(new RpcMethodMappings())
            .setHost(TEST_BENCH.getBaseUri().replaceAll("https?://", ""))
            .setTestAllowFilter(RetryTestCaseResolver.includeAll())
            .build();

    return resolver.getRetryTestCases().stream()
        .map(rtc -> new Object[] {rtc.testRetryConformance, rtc.rpcMethodMapping})
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * When a "higher level object" ({@link com.google.cloud.storage.Bucket}, {@link
   * com.google.cloud.storage.Blob}, etc.) is created as part of setup it keeps a reference to the
   * instance of {@link Storage} used to create it. When we run our tests we need the instance of
   * {@link Storage} to be the instance with the headers to signal the retry test.
   *
   * <p>The function returned will inspect the {@link State} and create copies of any "higher level
   * objects" which are present replacing the instance of {@link Storage} from the provided ctx.
   */
  private static CtxFunction getReplaceStorageInObjectsFromCtx() {
    return (ctx, c) -> {
      State s = ctx.getState();
      if (s.hasBucket()) {
        s = s.with(bucketCopyWithStorage(s.getBucket(), ctx.getStorage()));
      }
      if (s.hasBlob()) {
        s = s.with(blobCopyWithStorage(s.getBlob(), ctx.getStorage()));
      }
      final State state = s;
      return ctx.map(x -> state);
    };
  }

  /**
   * Helper class which encapsulates all the logic necessary to resolve and crete a test case for
   * each defined scenario from google-cloud-conformance-tests and our defined {@link
   * RpcMethodMappings}.
   */
  private static final class RetryTestCaseResolver {
    private static final String HEX_SHUFFLE_SEED_OVERRIDE =
        System.getProperty("HEX_SHUFFLE_SEED_OVERRIDE");

    private final String retryTestsJsonResourcePath;
    private final RpcMethodMappings mappings;
    private final BiPredicate<RpcMethod, TestRetryConformance> testAllowFilter;
    private final Random rand;
    private final String host;

    RetryTestCaseResolver(
        String retryTestsJsonResourcePath,
        RpcMethodMappings mappings,
        BiPredicate<RpcMethod, TestRetryConformance> testAllowFilter,
        Random rand,
        String host) {
      this.retryTestsJsonResourcePath = retryTestsJsonResourcePath;
      this.mappings = mappings;
      this.testAllowFilter = testAllowFilter;
      this.rand = rand;
      this.host = host;
    }

    /** Load, permute and generate all RetryTestCases which are to be run in this suite */
    List<RetryTestCase> getRetryTestCases() throws IOException {
      RetryTests retryTests = loadRetryTestsDefinition();

      // sort the defined RetryTest by id, so we have a stable ordering while generating cases.
      List<RetryTest> retryTestCases =
          retryTests.getRetryTestsList().stream()
              .sorted(Comparator.comparingInt(RetryTest::getId))
              .collect(Collectors.toList());

      List<RetryTestCase> testCases = generateTestCases(mappings, retryTestCases);

      // Shuffle our test cases to ensure we don't have any between case ordering weirdness
      Collections.shuffle(testCases, rand);

      validateGeneratedTestCases(mappings, testCases);

      return testCases;
    }

    /** Load the defined scenarios from google-cloud-conformance-tests */
    private RetryTests loadRetryTestsDefinition() throws IOException {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      InputStream dataJson = cl.getResourceAsStream(retryTestsJsonResourcePath);
      assertNotNull(
          String.format("Unable to load test definition: %s", retryTestsJsonResourcePath),
          dataJson);

      InputStreamReader reader = new InputStreamReader(dataJson, Charsets.UTF_8);
      RetryTests.Builder testBuilder = RetryTests.newBuilder();
      JsonFormat.parser().merge(reader, testBuilder);
      return testBuilder.build();
    }

    /** Permute the RetryTest, Instructions and methods with our mappings */
    private List<RetryTestCase> generateTestCases(
        RpcMethodMappings rpcMethodMappings, List<RetryTest> retryTests) {

      List<RetryTestCase> testCases = new ArrayList<>();
      for (RetryTest testCase : retryTests) {
        for (InstructionList instructionList : testCase.getCasesList()) {
          for (Method method : testCase.getMethodsList()) {
            String methodName = method.getName();
            RpcMethod key = RpcMethod.storage.lookup.get(methodName);
            assertNotNull(
                String.format("Unable to resolve RpcMethod for value '%s'", methodName), key);
            // get all RpcMethodMappings which are defined for key
            List<RpcMethodMapping> mappings =
                rpcMethodMappings.get(key).stream()
                    .sorted(Comparator.comparingInt(RpcMethodMapping::getMappingId))
                    .collect(Collectors.toList());
            // if we don't have any mappings defined for the provide key, generate a case that when
            // run reports an ignored test. This is done for the sake of completeness and to be
            // aware of a lack of mapping.
            if (mappings.isEmpty()) {
              TestRetryConformance testRetryConformance =
                  new TestRetryConformance(
                      host,
                      testCase.getId(),
                      method,
                      instructionList,
                      testCase.getPreconditionProvided(),
                      false);
              if (testAllowFilter.test(key, testRetryConformance)) {
                testCases.add(
                    new RetryTestCase(testRetryConformance, RpcMethodMapping.notImplemented(key)));
              }
            } else {
              for (RpcMethodMapping mapping : mappings) {
                TestRetryConformance testRetryConformance =
                    new TestRetryConformance(
                        host,
                        testCase.getId(),
                        method,
                        instructionList,
                        testCase.getPreconditionProvided(),
                        testCase.getExpectSuccess(),
                        mapping.getMappingId());
                // check that this case is allowed based on the provided filter
                if (testAllowFilter.test(key, testRetryConformance)) {
                  // check that the defined mapping is applicable to the case we've resolved.
                  // Many mappings are conditionally valid and depend on the defined case.
                  if (mapping.getApplicable().test(testRetryConformance)) {
                    testCases.add(new RetryTestCase(testRetryConformance, mapping));
                  } else {
                    // when the mapping is determined to not be applicable to this case, generate
                    // a synthetic mapping which  will report as an ignored test. This is done for
                    // the sake of completeness.
                    RpcMethodMapping build =
                        mapping
                            .toBuilder()
                            .withSetup(CtxFunction.identity())
                            .withTest(
                                (s, c) -> {
                                  throw new AssumptionViolatedException(
                                      "applicability predicate evaluated to false");
                                })
                            .withTearDown(CtxFunction.identity())
                            .build();
                    testCases.add(new RetryTestCase(testRetryConformance, build));
                  }
                }
              }
            }
          }
        }
      }
      return testCases;
    }

    private void validateGeneratedTestCases(
        RpcMethodMappings rpcMethodMappings, List<RetryTestCase> data) {
      Set<Integer> unusedMappings =
          rpcMethodMappings.differenceMappingIds(
              data.stream()
                  .map(rtc -> rtc.testRetryConformance.getMappingId())
                  .collect(Collectors.toSet()));

      if (!unusedMappings.isEmpty()) {
        LOGGER.warning(
            String.format(
                "Declared but unused mappings with ids: [%s]",
                Joiner.on(", ").join(unusedMappings)));
      }
    }

    static Builder newBuilder() {
      return new Builder();
    }

    /** Filtering predicate in which all test cases will be included and run. */
    static BiPredicate<RpcMethod, TestRetryConformance> includeAll() {
      return (m, c) -> true;
    }

    /**
     * Filtering predicate in which only those test cases which match up to the specified {@code
     * mappingIds} will be included and run.
     */
    static BiPredicate<RpcMethod, TestRetryConformance> specificMappings(int... mappingIds) {
      ImmutableSet<Integer> set = Arrays.stream(mappingIds).boxed()
          .collect(ImmutableSet.toImmutableSet());
      return (m, c) -> set.contains(c.getMappingId());
    }

    static final class Builder {
      private String retryTestsJsonResourcePath;
      private RpcMethodMappings mappings;
      private String host;
      private BiPredicate<RpcMethod, TestRetryConformance> testAllowFilter;
      private final Random rand;

      public Builder() {
        this.rand = resolveRand();
      }

      /**
       * Set the resource path of where to resolve the retry_tests.json from
       * google-cloud-conformance-tests
       */
      public Builder setRetryTestsJsonResourcePath(String retryTestsJsonResourcePath) {
        this.retryTestsJsonResourcePath = retryTestsJsonResourcePath;
        return this;
      }

      /** Set the defined mappings which are to be used in test generation */
      public Builder setMappings(RpcMethodMappings mappings) {
        this.mappings = requireNonNull(mappings, "mappings must be non null");
        return this;
      }

      /** Set the host string of where the testbench will be available during a test run */
      public Builder setHost(String host) {
        this.host = host;
        return this;
      }

      /**
       * Set the allow filter for determining if a particular {@link RpcMethod} and {@link
       * TestRetryConformance} should be included in the generated test suite.
       */
      public Builder setTestAllowFilter(
          BiPredicate<RpcMethod, TestRetryConformance> testAllowFilter) {
        this.testAllowFilter = requireNonNull(testAllowFilter, "testAllowFilter must be non null");
        return this;
      }

      public RetryTestCaseResolver build() {
        return new RetryTestCaseResolver(
            requireNonNull(
                retryTestsJsonResourcePath, "retryTestsJsonResourcePath must be non null"),
            requireNonNull(mappings, "mappings must be non null"),
            requireNonNull(testAllowFilter, "testAllowList must be non null"),
            rand,
            host);
      }

      /**
       * As part of test generation and execution we are shuffling the order to ensure there is no
       * ordering dependency between individual cases. Given this fact, we report the seed used for
       * performing the shuffle. If an explicit seed is provided via environment variable that will
       * take precedence.
       */
      private static Random resolveRand() {
        try {
          long seed;
          if (HEX_SHUFFLE_SEED_OVERRIDE != null) {
            LOGGER.info(
                "Shuffling test order using Random with override seed: "
                    + HEX_SHUFFLE_SEED_OVERRIDE);
            seed = new BigInteger(HEX_SHUFFLE_SEED_OVERRIDE.replace("0x", ""), 16).longValue();
          } else {
            seed =
                SecureRandom.getInstanceStrong()
                    .longs(100)
                    .reduce((first, second) -> second)
                    .orElseThrow(
                        () -> {
                          throw new IllegalStateException("Unable to generate seed");
                        });
            String msg =
                String.format("Shuffling test order using Random with seed: 0x%016X", seed);
            LOGGER.info(msg);
          }
          return new Random(seed);
        } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  /**
   * Simple typed tuple class to bind together a {@link TestRetryConformance} and {@link
   * RpcMethodMapping} during resolution.
   */
  private static final class RetryTestCase {
    private final TestRetryConformance testRetryConformance;
    private final RpcMethodMapping rpcMethodMapping;

    RetryTestCase(TestRetryConformance testRetryConformance, RpcMethodMapping rpcMethodMapping) {
      this.testRetryConformance = testRetryConformance;
      this.rpcMethodMapping = rpcMethodMapping;
    }
  }
}
