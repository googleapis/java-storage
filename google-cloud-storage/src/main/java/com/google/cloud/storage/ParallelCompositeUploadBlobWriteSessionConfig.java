/*
 * Copyright 2023 Google LLC
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.cloud.storage.MetadataField.PartRange;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.Immutable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Immutable config builder for a Parallel Composite Upload
 *
 * @see <a
 *     href="https://cloud.google.com/storage/docs/composing-objects">https://cloud.google.com/storage/docs/composing-objects</a>
 * @see <a
 *     href="https://cloud.google.com/storage/docs/parallel-composite-uploads">https://cloud.google.com/storage/docs/parallel-composite-uploads</a>
 */
@Immutable
@BetaApi
final class ParallelCompositeUploadBlobWriteSessionConfig extends BlobWriteSessionConfig {

  private static final int MAX_PARTS_PER_COMPOSE = 32;
  private final int maxPartsPerCompose;
  private final ExecutorSupplier executorSupplier;
  private final BufferStrategy bufferStrategy;
  private final PartNamingStrategy partNamingStrategy;
  private final PartCleanupStrategy partCleanupStrategy;

  private ParallelCompositeUploadBlobWriteSessionConfig(
      int maxPartsPerCompose,
      ExecutorSupplier executorSupplier,
      BufferStrategy bufferStrategy,
      PartNamingStrategy partNamingStrategy,
      PartCleanupStrategy partCleanupStrategy) {
    this.maxPartsPerCompose = maxPartsPerCompose;
    this.executorSupplier = executorSupplier;
    this.bufferStrategy = bufferStrategy;
    this.partNamingStrategy = partNamingStrategy;
    this.partCleanupStrategy = partCleanupStrategy;
  }

  @InternalApi
  ParallelCompositeUploadBlobWriteSessionConfig withMaxPartsPerCompose(int maxPartsPerCompose) {
    checkArgument(
        2 <= maxPartsPerCompose && maxPartsPerCompose <= 32,
        "2 <= maxPartsPerCompose <= 32 (2 <= %s <= 32)",
        maxPartsPerCompose);
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        maxPartsPerCompose,
        executorSupplier,
        bufferStrategy,
        partNamingStrategy,
        partCleanupStrategy);
  }

  @BetaApi
  public ParallelCompositeUploadBlobWriteSessionConfig withExecutorSupplier(
      ExecutorSupplier executorSupplier) {
    checkNotNull(executorSupplier, "executorSupplier must be non null");
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        maxPartsPerCompose,
        executorSupplier,
        bufferStrategy,
        partNamingStrategy,
        partCleanupStrategy);
  }

  @BetaApi
  public ParallelCompositeUploadBlobWriteSessionConfig withBufferStrategy(
      BufferStrategy bufferStrategy) {
    checkNotNull(bufferStrategy, "bufferStrategy must be non null");
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        maxPartsPerCompose,
        executorSupplier,
        bufferStrategy,
        partNamingStrategy,
        partCleanupStrategy);
  }

  @BetaApi
  public ParallelCompositeUploadBlobWriteSessionConfig withPartNamingStrategy(
      PartNamingStrategy partNamingStrategy) {
    checkNotNull(partNamingStrategy, "partNamingStrategy must be non null");
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        maxPartsPerCompose,
        executorSupplier,
        bufferStrategy,
        partNamingStrategy,
        partCleanupStrategy);
  }

  @BetaApi
  public ParallelCompositeUploadBlobWriteSessionConfig withPartCleanupStrategy(
      PartCleanupStrategy partCleanupStrategy) {
    checkNotNull(partCleanupStrategy, "partCleanupStrategy must be non null");
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        maxPartsPerCompose,
        executorSupplier,
        bufferStrategy,
        partNamingStrategy,
        partCleanupStrategy);
  }

  @BetaApi
  static ParallelCompositeUploadBlobWriteSessionConfig of() {
    return new ParallelCompositeUploadBlobWriteSessionConfig(
        MAX_PARTS_PER_COMPOSE,
        ExecutorSupplier.cachedPool(),
        BufferStrategy.simple(ByteSizeConstants._16MiB),
        PartNamingStrategy.noPrefix(),
        PartCleanupStrategy.always());
  }

  @InternalApi
  @Override
  WriterFactory createFactory(Clock clock) throws IOException {
    Executor executor = executorSupplier.get();
    BufferHandlePool bufferHandlePool = bufferStrategy.get();
    throw new IllegalStateException("Not yet implemented");
  }

  @BetaApi
  @Immutable
  public abstract static class BufferStrategy extends Factory<BufferHandlePool> {

    private BufferStrategy() {}

    @BetaApi
    public static BufferStrategy simple(int capacity) {
      return new SimpleBufferStrategy(capacity);
    }

    @BetaApi
    public static BufferStrategy fixedPool(int bufferCount, int bufferCapacity) {
      return new FixedBufferStrategy(bufferCount, bufferCapacity);
    }

    private static class SimpleBufferStrategy extends BufferStrategy {

      private final int capacity;

      private SimpleBufferStrategy(int capacity) {
        this.capacity = capacity;
      }

      @Override
      BufferHandlePool get() {
        return BufferHandlePool.simple(capacity);
      }
    }

    private static class FixedBufferStrategy extends BufferStrategy {

      private final int bufferCount;
      private final int bufferCapacity;

      private FixedBufferStrategy(int bufferCount, int bufferCapacity) {
        this.bufferCount = bufferCount;
        this.bufferCapacity = bufferCapacity;
      }

      @Override
      BufferHandlePool get() {
        return BufferHandlePool.fixedPool(bufferCount, bufferCapacity);
      }
    }
  }

  @BetaApi
  @Immutable
  public abstract static class ExecutorSupplier extends Factory<Executor> {
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(1);

    private ExecutorSupplier() {}

    @BetaApi
    public static ExecutorSupplier cachedPool() {
      return new ExecutorSupplier() {
        @Override
        Executor get() {
          ThreadFactory threadFactory = newThreadFactory();
          return Executors.newCachedThreadPool(threadFactory);
        }
      };
    }

    @BetaApi
    public static ExecutorSupplier fixedPool(int poolSize) {
      return new ExecutorSupplier() {
        @Override
        Executor get() {
          ThreadFactory threadFactory = newThreadFactory();
          return Executors.newFixedThreadPool(poolSize, threadFactory);
        }
      };
    }

    @BetaApi
    public static ExecutorSupplier useExecutor(Executor executor) {
      return new SuppliedExecutorSupplier(executor);
    }

    @NonNull
    private static ThreadFactory newThreadFactory() {
      return new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("c.g.c:g-c-s:pcu-" + INSTANCE_COUNTER.getAndIncrement() + "-%d")
          .build();
    }

    private static class SuppliedExecutorSupplier extends ExecutorSupplier {

      private final Executor executor;

      public SuppliedExecutorSupplier(Executor executor) {
        this.executor = executor;
      }

      @Override
      Executor get() {
        return executor;
      }
    }
  }

  @BetaApi
  @Immutable
  public abstract static class PartNamingStrategy {
    private static final String FIELD_SEPARATOR = ";";
    private static final Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final HashFunction OBJECT_NAME_HASH_FUNCTION = Hashing.goodFastHash(128);
    private final SecureRandom rand;

    @VisibleForTesting
    @InternalApi
    PartNamingStrategy(SecureRandom rand) {
      this.rand = rand;
    }

    String fmtName(String ultimateObjectName, PartRange partRange) {
      // generate 128 bits of random data
      byte[] bytes = new byte[16];
      rand.nextBytes(bytes);

      // encode it to base 64, yielding 22 characters
      String randomKey = B64.encodeToString(bytes);
      HashCode hashCode =
          OBJECT_NAME_HASH_FUNCTION.hashString(ultimateObjectName, StandardCharsets.UTF_8);
      String nameDigest = B64.encodeToString(hashCode.asBytes());
      return fmtFields(randomKey, nameDigest, partRange.encode());
    }

    protected abstract String fmtFields(String randomKey, String nameDigest, String partRange);

    @BetaApi
    public static PartNamingStrategy noPrefix() {
      SecureRandom rand = new SecureRandom();
      return new NoPrefix(rand);
    }

    @BetaApi
    public static PartNamingStrategy prefix(String prefixPattern) {
      checkNotNull(prefixPattern, "prefixPattern must be non null");
      SecureRandom rand = new SecureRandom();
      return new WithPrefix(rand, prefixPattern);
    }

    static final class WithPrefix extends PartNamingStrategy {

      private final String prefix;

      private WithPrefix(SecureRandom rand, String prefix) {
        super(rand);
        this.prefix = prefix;
      }

      @Override
      protected String fmtFields(String randomKey, String nameDigest, String partRange) {
        return prefix
            + "/"
            + randomKey
            + FIELD_SEPARATOR
            + nameDigest
            + FIELD_SEPARATOR
            + partRange
            + ".part";
      }
    }

    static final class NoPrefix extends PartNamingStrategy {
      public NoPrefix(SecureRandom rand) {
        super(rand);
      }

      @Override
      protected String fmtFields(String randomKey, String nameDigest, String partRange) {
        return randomKey
            + FIELD_SEPARATOR
            // todo: do we want to
            // include a hint where the object came from, similar to gcloud
            // https://cloud.google.com/storage/docs/parallel-composite-uploads#gcloud-pcu
            // + "com.google.cloud:google-cloud-storage"
            // + FIELD_SEPARATOR
            + nameDigest
            + FIELD_SEPARATOR
            + partRange
            + ".part";
      }
    }
  }

  @BetaApi
  @Immutable
  public static class PartCleanupStrategy {
    private final boolean deleteParts;
    private final boolean deleteOnError;

    private PartCleanupStrategy(boolean deleteParts, boolean deleteOnError) {
      this.deleteParts = deleteParts;
      this.deleteOnError = deleteOnError;
    }

    boolean isDeleteParts() {
      return deleteParts;
    }

    boolean isDeleteOnError() {
      return deleteOnError;
    }

    /**
     * If an unrecoverable error is encountered, define whether to attempt to delete any object
     * parts already uploaded.
     */
    @BetaApi
    public PartCleanupStrategy withDeleteOnError(boolean deleteOnError) {
      return new PartCleanupStrategy(deleteParts, deleteOnError);
    }

    @BetaApi
    public static PartCleanupStrategy always() {
      return new PartCleanupStrategy(true, true);
    }

    @BetaApi
    public static PartCleanupStrategy never() {
      return new PartCleanupStrategy(false, false);
    }
  }

  private abstract static class Factory<T> {
    abstract T get();
  }
}
