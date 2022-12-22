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

package com.google.cloud.storage.it.runner.registry;

import com.google.cloud.storage.TransportCompatibility.Transport;
import com.google.cloud.storage.it.runner.CrossRunIntersection;
import com.google.cloud.storage.it.runner.TestInitializer;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.Inject;
import com.google.cloud.storage.it.runner.annotations.StorageFixture;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.TestClass;

/**
 * Global tests resource registry which encapsulates the lifecycle of all integration tests global
 * resources including but not limited to {@link com.google.cloud.storage.Storage} instances, {@link
 * com.google.cloud.storage.BucketInfo} buckets, Test Bench, Thread Pool, etc.
 *
 * <p>All resources are lazily initialized and will live until the registry is shutdown.
 *
 * <p>All resources registered here are available for field {@code @}{@link Inject}ion when run via
 * {@link com.google.cloud.storage.it.runner.StorageITRunner}.
 *
 * <p>This class shouldn't be used outside of {@code com.google.cloud.storage.it.runner}. When we
 * have access to java modules this will be enforced.
 */
public final class Registry extends RunListener {

  private static volatile boolean shutdownHookRegistered = false;
  private static final Object shutdownHookRegistrationLock = new Object();

  private static final Registry INSTANCE = new Registry();
  private final ListeningScheduledExecutorService exec =
      MoreExecutors.listeningDecorator(
          Executors.newScheduledThreadPool(
              2 * Runtime.getRuntime().availableProcessors(),
              new ThreadFactoryBuilder().setDaemon(true).setNameFormat("test-run-%d").build()));

  private final TestRunScopedInstance<TestBench> testBench =
      TestRunScopedInstance.of("TEST_BENCH", () -> TestBench.newBuilder().build());

  private final TestRunScopedInstance<Generator> generator =
      TestRunScopedInstance.of("GENERATOR", Generator::new);

  private final BackendResources prodBackendResources = BackendResources.of(Backend.PROD);
  private final BackendResources testBenchBackendResource = BackendResources.of(Backend.TEST_BENCH);

  private final ImmutableList<RegistryEntry<?>> entries =
      new ImmutableList.Builder<RegistryEntry<?>>()
          .add(
              RegistryEntry.of(0, TestBench.class, testBench),
              RegistryEntry.of(1, Generator.class, generator),
              registryEntry(2, Backend.class, CrossRunIntersection::getBackend),
              registryEntry(3, Transport.class, CrossRunIntersection::getTransport))
          .addAll(prodBackendResources.getRegistryEntries())
          .addAll(testBenchBackendResource.getRegistryEntries())
          .build();

  private final ImmutableSet<Class<?>> injectableTypes =
      entries.stream().map(RegistryEntry::getType).collect(ImmutableSet.toImmutableSet());
  private final String injectableTypesString = Joiner.on("|").join(injectableTypes);
  private final ThreadLocal<Description> currentTest = new ThreadLocal<>();

  private Registry() {}

  public static Registry getInstance() {
    if (!shutdownHookRegistered) {
      synchronized (shutdownHookRegistrationLock) {
        if (!shutdownHookRegistered) {
          Runtime.getRuntime().addShutdownHook(new Thread(() -> Registry.getInstance().shutdown()));
          shutdownHookRegistered = true;
        }
      }
    }
    return INSTANCE;
  }

  public ImmutableSet<Class<?>> injectableTypes() {
    return injectableTypes;
  }

  public String injectableTypesString() {
    return injectableTypesString;
  }

  TestBench testBench() {
    return testBench.get();
  }

  @Nullable
  public Description getCurrentTest() {
    return currentTest.get();
  }

  @Override
  public void testStarted(Description description) {
    currentTest.set(description);
  }

  @Override
  public void testFinished(Description description) {
    currentTest.remove();
  }

  public RunnerScheduler parallelScheduler() {
    // Schedulers can't be shared, create a new one with the shared thread pool
    return new ParallelScheduler(exec);
  }

  public TestInitializer newTestInitializerForCell(CrossRunIntersection c) {
    return o -> {
      injectFields(o, c);
      return o;
    };
  }

  public void injectFields(Object test, CrossRunIntersection crossRunIntersection)
      throws IllegalAccessException {
    TestClass testClass = new TestClass(test.getClass());
    List<FrameworkField> annotatedFields = testClass.getAnnotatedFields(Inject.class);
    for (FrameworkField ff : annotatedFields) {
      Object resolve = resolve(ff, crossRunIntersection);
      ff.getField().set(test, resolve);
    }
  }

  public Object resolve(FrameworkField ff, CrossRunIntersection crossRunIntersection) {
    StorageFixture sf = ff.getAnnotation(StorageFixture.class);
    final CrossRunIntersection finalCrossRunIntersection;
    if (sf != null) {
      finalCrossRunIntersection = crossRunIntersection.withTransport(sf.value());
    } else {
      finalCrossRunIntersection = crossRunIntersection;
    }
    Optional<RegistryEntry<?>> first =
        entries.stream()
            .filter(re -> re.getPredicate().test(ff, finalCrossRunIntersection))
            .findFirst();
    if (first.isPresent()) {
      TestRunScopedInstance<?> instance = first.get().getInstance();
      ManagedLifecycle o = instance.get();
      if (o instanceof StatelessManagedLifecycle) {
        StatelessManagedLifecycle<?> sml = (StatelessManagedLifecycle) o;
        return sml.resolve(ff, crossRunIntersection);
      } else {
        return o.get();
      }
    } else {
      throw new IllegalArgumentException(
          String.format("Invalid: ff: %s, crossRunIntersection: %s", ff, crossRunIntersection));
    }
  }

  // private volatile ListenableFuture<?> shutdownF;

  private void shutdown() {
    entries.stream()
        .sorted()
        .forEach(
            re -> {
              try {
                re.getInstance().shutdown();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
  }

  @FunctionalInterface
  private interface StatelessManagedLifecycle<T> extends ManagedLifecycle {
    T resolve(FrameworkField ff, CrossRunIntersection crossRunIntersection);

    @Override
    default Object get() {
      return this;
    }

    @Override
    default void start() {}

    @Override
    default void stop() {}
  }

  private static <T> RegistryEntry<?> registryEntry(
      int shutdownPriority, Class<T> c, Function<CrossRunIntersection, T> f) {
    TestRunScopedInstance<?> of = TestRunScopedInstance.of(c.getSimpleName(), () -> lift(f));
    RegistryApplicabilityPredicate pred = RegistryApplicabilityPredicate.cellWith(f);
    return RegistryEntry.of(
        shutdownPriority,
        c,
        of,
        RegistryApplicabilityPredicate.annotatedWith(Inject.class)
            .and(RegistryApplicabilityPredicate.assignableFrom(c))
            .and(pred));
  }

  private static <T> StatelessManagedLifecycle<T> lift(Function<CrossRunIntersection, T> f) {
    return (ff, cell) -> f.apply(cell);
  }
}
