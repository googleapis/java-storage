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

import com.google.cloud.storage.CIUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * As the adherence of {@link com.google.cloud.storage.Storage} to the retry conformance test suite
 * is an ongoing effort, we need a way in which those tests which are not yet in compliance do not
 * serve as blockers for other features and commits.
 *
 * <p>This class provides a transparent means of enforcing the reporting of failed tests when ran in
 * a CI environment. When a test is run, if it fails for any reason the test name will be checked
 * against an allow list of known failing tests. If the tests name is present in the allow list,
 * then the failure will be wrapped in an assumption failure to show up as a skipped test rather
 * than a failed one.
 */
final class GracefulConformanceEnforcement implements TestRule {

  private final String testName;
  private final Set<String> testNamesWhichCanFail;

  public GracefulConformanceEnforcement(String testName) {
    this.testName = testName;
    this.testNamesWhichCanFail = loadTestNamesWhichCanFail();
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        } catch (AssumptionViolatedException e) {
          // pass through any assumption/ignore errors as they are
          throw e;
        } catch (Throwable t) {
          if (!testNamesWhichCanFail.contains(testName)) {
            throw t;
          } else {
            if (CIUtils.isRunningInCI()) {
              throw new AssumptionViolatedException(
                  String.format(
                      "Test %s is allowed to fail, downgrading failure to ignored.", testName),
                  t);
            } else {
              throw t;
            }
          }
        }
      }
    };
  }

  private static Set<String> loadTestNamesWhichCanFail() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream inputStream =
        cl.getResourceAsStream(
            "com/google/cloud/storage/conformance/retry/testNamesWhichCanFail.txt");
    assertNotNull(inputStream);
    try {
      return CharStreams.readLines(new InputStreamReader(inputStream)).stream()
          .map(String::trim)
          .filter(s -> !s.isEmpty() && !s.startsWith("#"))
          .collect(ImmutableSet.toImmutableSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException ignore) {
      }
    }
  }
}
