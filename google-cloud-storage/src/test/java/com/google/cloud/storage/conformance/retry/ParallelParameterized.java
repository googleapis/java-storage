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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

/**
 * Extends off the provided {@link Parameterized} runner provided by junit, only augmenting which
 * scheduler is used so that tests can run in parallel by using a thread pool.
 */
public final class ParallelParameterized extends Parameterized {

  public ParallelParameterized(Class<?> klass) throws Throwable {
    super(klass);
    this.setScheduler(new ParallelScheduler());
  }

  private static class ParallelScheduler implements RunnerScheduler {
    private static final Logger LOGGER = Logger.getLogger(ParallelScheduler.class.getName());

    private final Phaser childCounter;
    private final ExecutorService executorService;

    private ParallelScheduler() {
      ThreadFactory threadFactory =
          new ThreadFactoryBuilder()
              .setDaemon(true)
              .setNameFormat("parallel-test-runner-%02d")
              .build();
      // attempt to leave some space for the testbench server running alongside these tests
      int coreCount = Runtime.getRuntime().availableProcessors() - 2;
      int threadCount = Math.max(2, coreCount);
      LOGGER.info("Using up to " + threadCount + " threads to run tests.");
      executorService = Executors.newFixedThreadPool(threadCount, threadFactory);
      childCounter = new Phaser();
    }

    @Override
    public void schedule(Runnable childStatement) {
      childCounter.register();
      executorService.submit(
          () -> {
            try {
              childStatement.run();
            } finally {
              childCounter.arrive();
            }
          });
    }

    @Override
    public void finished() {
      try {
        childCounter.awaitAdvance(0);
      } finally {
        executorService.shutdownNow();
      }
    }
  }
}
