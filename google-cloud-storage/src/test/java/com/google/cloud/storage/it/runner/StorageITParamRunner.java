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
import com.google.cloud.storage.it.runner.annotations.Parameterized.Parameter;
import com.google.cloud.storage.it.runner.annotations.Parameterized.ParametersProvider;
import com.google.cloud.storage.it.runner.annotations.SingleBackend;
import com.google.cloud.storage.it.runner.registry.Registry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.junit.runner.Runner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * Storage custom runner which will handle {@link CrossRun}, {@link SingleBackend}, {@link
 * com.google.cloud.storage.it.runner.annotations.ParallelFriendly} and {@link
 * com.google.cloud.storage.it.runner.annotations.Inject} suite computation.
 *
 * <p>Use in place of the {@link org.junit.runners.Parameterized}.
 *
 * <p>Must specify an {@code @}{@link Parameterized} annotation pointing to a class which the
 * parameter values will be resolved from. The specified class must implement {@link
 * ParametersProvider}, and may optionally itself specific {@code @}{@link
 * com.google.cloud.storage.it.runner.annotations.Inject} fields for use when computing the
 * parameters.
 *
 * <p>A field in the test class must be annotated {@link Parameter} denoting which fields the
 * parameter value should be set to. Only single dimension types are supported for injection. The
 * whole value will be set to the field, unlike {@link org.junit.runners.Parameterized} which will
 * sub-tuple selection.
 */
public final class StorageITParamRunner extends StorageSuite {

  public StorageITParamRunner(Class<?> klass) throws InitializationError {
    super(klass, computeRunners(klass, Registry.getInstance()));
  }

  private static List<Runner> computeRunners(Class<?> klass, Registry registry)
      throws InitializationError {
    TestClass testClass = new TestClass(klass);

    Parameterized parameterized = testClass.getAnnotation(Parameterized.class);
    if (parameterized == null) {
      throw new InitializationError("null @Parameterized annotation");
    }

    CrossRun crossRun = testClass.getAnnotation(CrossRun.class);
    SingleBackend singleBackend = testClass.getAnnotation(SingleBackend.class);
    StorageSuite.validateBackendAnnotations(crossRun, singleBackend);

    ImmutableList<?> parameters;
    try {
      Class<? extends ParametersProvider> ppC = parameterized.value();
      ParametersProvider pp = ppC.newInstance();
      registry.injectFields(pp, null);
      parameters = pp.parameters();
      if (parameters == null || parameters.isEmpty()) {
        throw new InitializationError(
            "Null or empty parameters from ParameterProvider: " + ppC.getName());
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new InitializationError(e);
    }

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
                  .flatMap(
                      c -> {
                        TestInitializer ti = registry.newTestInitializerForCell(c);
                        return parameters.stream()
                            .map(
                                param ->
                                    StorageITLeafRunner.unsafeOf(
                                        testClass,
                                        c,
                                        fmtParam(c, param),
                                        ti.andThen(setFieldTo(testClass, param))));
                      })
                  .collect(ImmutableList.toImmutableList()));
    } else {
      Backend backend = singleBackend.value();
      CrossRunIntersection crossRunIntersection = CrossRunIntersection.of(backend, null);
      TestInitializer ti = registry.newTestInitializerForCell(crossRunIntersection);
      return SneakyException.unwrap(
          () ->
              parameters.stream()
                  .map(
                      param ->
                          StorageITLeafRunner.unsafeOf(
                              testClass,
                              crossRunIntersection,
                              fmtParam(param),
                              ti.andThen(setFieldTo(testClass, param))))
                  .collect(ImmutableList.toImmutableList()));
    }
  }

  static String fmtParam(Object param) {
    return String.format("[%s]", param.toString());
  }

  static String fmtParam(CrossRunIntersection c, Object param) {
    return c.fmtSuiteName() + fmtParam(param);
  }

  private static TestInitializer setFieldTo(TestClass testClass, Object param) {
    return o -> {
      List<FrameworkField> ffs = testClass.getAnnotatedFields(Parameter.class);
      for (FrameworkField ff : ffs) {
        ff.getField().set(o, param);
      }
      return o;
    };
  }
}
