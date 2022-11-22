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

import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import com.google.cloud.storage.it.runner.annotations.Parameterized;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.Registry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * Storage custom runner which will handle {@link CrossRun}, {@link SingleBackend}, {@link
 * com.google.cloud.storage.it.runner.annotations.ParallelFriendly} and {@link
 * com.google.cloud.storage.it.runner.annotations.Inject} suite computation.
 *
 * <p>Use in place of the usual default JUnit test runner.
 *
 * <p>If writing a Parameterized test, instead use {@link StorageITParamRunner}
 *
 * @see StorageITParamRunner
 * @see org.junit.runners.BlockJUnit4ClassRunner
 */
public final class StorageITRunner extends StorageSuite {

  public StorageITRunner(Class<?> klass) throws InitializationError {
    super(klass, computeRunners(klass, Registry.getInstance()));
  }

  @SuppressWarnings("SameParameterValue")
  private static List<Runner> computeRunners(Class<?> klass, Registry registry)
      throws InitializationError {
    TestClass testClass = new TestClass(klass);

    if (testClass.getAnnotation(Parameterized.class) != null) {
      throw new InitializationError(
          String.format(
              "Class annotated with @%s. You probably want to use @RunWith(%s.class) as your runner.",
              Parameterized.class.getSimpleName(), StorageITParamRunner.class.getSimpleName()));
    }

    CrossRun crossRun = testClass.getAnnotation(CrossRun.class);
    SingleBackend singleBackend = testClass.getAnnotation(SingleBackend.class);
    StorageSuite.validateBackendAnnotations(crossRun, singleBackend);

    if (crossRun != null) {
      // TODO
      //   add warning message if @ClassRule or @BeforeClass/AfterClass are used, as these will run
      //   multiple times due to the class being run for each crossRun result.
      return SneakyException.unwrap(
          () ->
              ImmutableSet.copyOf(crossRun.backends()).stream()
                  .flatMap(
                      b ->
                          ImmutableSet.copyOf(crossRun.transports()).stream()
                              .map(t -> CrossRunIntersection.of(b, t)))
                  .map(
                      c -> {
                        TestInitializer ti = registry.newTestInitializerForCell(c);
                        return StorageITLeafRunner.unsafeOf(testClass, c, c.fmtSuiteName(), ti);
                      })
                  .collect(ImmutableList.toImmutableList()));
    } else {
      Backend backend = singleBackend.value();
      CrossRunIntersection crossRunIntersection = CrossRunIntersection.of(backend, null);
      TestInitializer ti = registry.newTestInitializerForCell(crossRunIntersection);
      return ImmutableList.of(StorageITLeafRunner.of(testClass, crossRunIntersection, null, ti));
    }
  }
}
