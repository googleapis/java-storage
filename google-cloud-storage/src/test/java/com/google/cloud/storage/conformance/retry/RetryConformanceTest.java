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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.cloud.ReadChannel;
import com.google.cloud.conformance.storage.v1.InstructionList;
import com.google.cloud.conformance.storage.v1.Method;
import com.google.cloud.conformance.storage.v1.RetryTest;
import com.google.cloud.conformance.storage.v1.RetryTests;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RetryConformanceTest {

  private static final String BUCKET_NAME = "test-bucket";
  private static final String OBJECT_NAME = "file.txt";
  private static final Map<String, List<BiFunc>> funcMap =
      new ImmutableMap.Builder<String, List<BiFunc>>()
          .put(
              "storage.objects.get",
              ImmutableList.of(
                  new BiFunc() {
                    @Override
                    public void apply(Storage s, TestCaseConfig c) {
                      final BlobId blobId = getBlobId(c);
                      Blob blob = s.get(blobId);
                      assertNotNull(blob);
                      Map<String, String> metadata = blob.getMetadata();
                    }
                  },
                  new BiFunc() {
                    @Override
                    public void apply(Storage s, TestCaseConfig c) throws IOException {
                      BlobId blobId = getBlobId(c);
                      ReadChannel reader = s.reader(blobId);
                      WritableByteChannel write = Channels.newChannel(NullOutputStream.INSTANCE);
                      ByteStreams.copy(reader, write);
                    }
                  }))
          .put(
              "storage.objects.update",
              ImmutableList.<BiFunc>of(
                  new BiFunc() {
                    @Override
                    public void apply(Storage s, TestCaseConfig c) {
                      final BlobId blobId = getBlobId(c);
                      Blob blob =
                          s.update(
                              BlobInfo.newBuilder(blobId)
                                  .setMetadata(ImmutableMap.of("foo", "bar"))
                                  .build());
                      assertNotNull(blob);
                      Map<String, String> metadata = blob.getMetadata();
                    }
                  }))
          .build();

  @ClassRule public static final TestServer testServer = TestServer.newBuilder().build();

  @Rule public final RetryTestFixture retryTestFixture;

  private final TestCaseConfig testCaseConfig;

  /**
   * @param description Not used by the test, but used by the parameterized test runner as the name
   *     of the test.
   */
  public RetryConformanceTest(
      @SuppressWarnings("unused") String description, TestCaseConfig testCaseConfig) {
    this.testCaseConfig = testCaseConfig;
    this.retryTestFixture =
        new RetryTestFixture(RetryTestFixture.CleanupStrategy.ONLY_ON_SUCCESS, testServer, testCaseConfig);
  }

  @Before
  public void setUp() throws Exception {
    Bucket bucket = retryTestFixture.getNonTestStorage().create(BucketInfo.of(BUCKET_NAME));
    bucket.create(OBJECT_NAME, "abcdef".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void test() throws Throwable {
    Storage storage = retryTestFixture.getTestStorage();
    testCaseConfig.getFunc().apply(storage, testCaseConfig);
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
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    String testDataJsonResource = "com/google/cloud/conformance/storage/v1/retry_tests.json";
    InputStream dataJson = cl.getResourceAsStream(testDataJsonResource);
    assertNotNull(
        String.format("Unable to load test definition: %s", testDataJsonResource), dataJson);

    InputStreamReader reader = new InputStreamReader(dataJson, Charsets.UTF_8);
    RetryTests.Builder testBuilder = RetryTests.newBuilder();
    JsonFormat.parser().merge(reader, testBuilder);
    RetryTests retryTests = testBuilder.build();

    List<RetryTest> testCases = retryTests.getRetryTestsList();
    List<Object[]> data = new ArrayList<>(testCases.size());
    for (RetryTest testCase : testCases) {
      for (InstructionList instructionList : testCase.getCasesList()) {
        String instructionsDesc = Joiner.on("_").join(instructionList.getInstructionsList());
        for (Method method : testCase.getMethodsList()) {
          String methodName = method.getName();
          List<BiFunc> funcs = funcMap.get(methodName);
          if (funcs == null) {
            String testName =
                String.format(
                    "TestRetryConformance/%d-[%s]-%s-%d",
                    testCase.getId(), instructionsDesc, methodName, 0);
            data.add(
                new Object[] {
                  testName,
                  new TestCaseConfig(
                      method,
                      instructionList,
                      testCase.getPreconditionProvided(),
                      FailFunc.INSTANCE)
                });
          } else {
            for (int i = 0; i < funcs.size(); i++) {
              BiFunc func = funcs.get(i);
              String testName =
                  String.format(
                      "TestRetryConformance/%d-[%s]-%s-%d",
                      testCase.getId(), instructionsDesc, methodName, i);
              BiFunc func1;
              if (testCase.getExpectSuccess()) {
                func1 = new ExpectSuccessFunc(func);
              } else {
                func1 = new ExpectFailureFunc(func);
              }
              TestCaseConfig testCaseConfig =
                  new TestCaseConfig(
                      method, instructionList, testCase.getPreconditionProvided(), func1);
              data.add(new Object[] {testName, testCaseConfig});
            }
          }
        }
      }
    }
    return data;
  }

  private static BlobId getBlobId(TestCaseConfig c) {
    final BlobId blobId;
    if (c.isPreconditionsProvided()) {
      blobId = BlobId.of(BUCKET_NAME, OBJECT_NAME, 0L);
    } else {
      blobId = BlobId.of(BUCKET_NAME, OBJECT_NAME);
    }
    return blobId;
  }

  private static final class NullOutputStream extends OutputStream {
    private static final NullOutputStream INSTANCE = new NullOutputStream();

    @Override
    public void write(int b) {}
  }

  private static class FailFunc implements BiFunc {
    private static final FailFunc INSTANCE = new FailFunc();

    @Override
    public void apply(Storage s, TestCaseConfig c) {
      fail("not implemented");
    }
  }

  private static final class ExpectSuccessFunc implements BiFunc {
    private final BiFunc delegate;

    private ExpectSuccessFunc(BiFunc delegate) {
      this.delegate = delegate;
    }

    @Override
    public void apply(Storage s, TestCaseConfig c) throws Throwable {
      delegate.apply(s, c);
    }
  }

  private static final class ExpectFailureFunc implements BiFunc {
    private final BiFunc delegate;

    public ExpectFailureFunc(BiFunc delegate) {
      this.delegate = delegate;
    }

    @Override
    public void apply(Storage s, TestCaseConfig c) throws Throwable {
      try {
        delegate.apply(s, c);
        fail("expected failure, but succeeded");
      } catch (Exception e) {
        // pass
      }
    }
  }
}
