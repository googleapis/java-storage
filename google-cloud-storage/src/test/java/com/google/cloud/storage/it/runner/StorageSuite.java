/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.storage.it.runner;

import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.ParallelFriendly;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.Registry;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * Base class for our two runners. Centralized the logic necessary to augment most of the running of
 * our tests. The thing we can't abstract for here is Test resolution since that has to be done as
 * part of the {@code super} invocation in the constructor.
 */
abstract class StorageSuite extends Suite {

  private final Lock childrenLock = new ReentrantLock();
  private volatile ImmutableList<Runner> filteredChildren = null;

  protected StorageSuite(Class<?> klass, List<Runner> runners) throws InitializationError {
    super(klass, runners);
    boolean runInParallel = getTestClass().getAnnotation(ParallelFriendly.class) != null;
    if (runInParallel) {
      this.setScheduler(Registry.getInstance().parallelScheduler());
    }
  }

  @Override
  public void run(RunNotifier notifier) {
    super.run(new RunNotifierUnion(notifier, Registry.getInstance()));
  }

  /*
  Filter is how intellij picks an individual method to run
   */
  @Override
  public void filter(Filter filter) throws NoTestsRemainException {
    childrenLock.lock();
    try {
      // TODO: Figure out how/why the test name is being mangled when @CrossRun is present
      //    test_results
      //    | test1[http][prod]()
      //    \ StorageParamTest
      //  instead of
      //    StorageParamTest / [http][prod] / test1
      filteredChildren =
          getFilteredChildren().stream()
              .filter(c -> shouldRun(filter, c, this::describeChild))
              .collect(ImmutableList.toImmutableList());
      if (filteredChildren.isEmpty()) {
        throw new NoTestsRemainException();
      }
    } finally {
      childrenLock.unlock();
    }
  }

  private List<Runner> getFilteredChildren() {
    if (filteredChildren == null) {
      childrenLock.lock();
      try {
        if (filteredChildren == null) {
          filteredChildren = ImmutableList.copyOf(getChildren());
        }
      } finally {
        childrenLock.unlock();
      }
    }
    return filteredChildren;
  }

  static void validateBackendAnnotations(CrossRun crossRun, SingleBackend singleBackend)
      throws InitializationError {
    if (crossRun != null && singleBackend != null) {
      throw new InitializationError(
          String.format(
              "Class annotated with both @%s and @%s. Pick only one.",
              CrossRun.class.getSimpleName(), SingleBackend.class.getSimpleName()));
    } else if (crossRun == null && singleBackend == null) {
      throw new InitializationError(
          String.format(
              "Missing either of @%s and @%s.",
              CrossRun.class.getSimpleName(), SingleBackend.class.getSimpleName()));
    }
  }

  private static <T> boolean shouldRun(Filter filter, T each, Function<T, Description> describe) {
    if (each instanceof StorageITLeafRunner) {
      StorageITLeafRunner leaf = (StorageITLeafRunner) each;
      return testsRemaining(filter, leaf);
    }

    boolean b = filter.shouldRun(describe.apply(each));
    return b && testsRemaining(filter, each);
  }

  private static boolean testsRemaining(Filter f, Object o) {
    try {
      f.apply(o);
      return true;
    } catch (NoTestsRemainException e) {
      return false;
    }
  }
}
