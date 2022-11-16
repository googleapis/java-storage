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

package com.google.cloud.storage;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/** A JUnit 4 {@link TestRule} which creates a JUnit lifecycle bound instance of {@link Storage}. */
public final class StorageFixture implements TestRule {

  private final Supplier<@NonNull Storage> factory;

  private Storage instance;

  private StorageFixture(Supplier<@NonNull Storage> factory) {
    this.factory = factory;
  }

  public Storage getInstance() {
    checkState(instance != null, "not active. Should only be called within junit managed scope");
    return instance;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        instance = requireNonNull(factory.get(), "factory.get() must not provide a null value");
        try (Storage s = instance) {
          base.evaluate();
        } finally {
          instance = null;
        }
      }
    };
  }

  public static StorageFixture defaultHttp() {
    return from(() -> StorageOptions.http().build());
  }

  public static StorageFixture defaultGrpc() {
    return from(() -> StorageOptions.grpc().build());
  }

  public static StorageFixture from(Supplier<StorageOptions> opts) {
    return of(() -> opts.get().getService());
  }

  public static StorageFixture of(@NonNull Supplier<@NonNull Storage> s) {
    return new StorageFixture(s);
  }
}
