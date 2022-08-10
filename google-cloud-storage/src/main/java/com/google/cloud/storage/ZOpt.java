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

import com.google.cloud.storage.spi.v1.StorageRpc;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A new helper abstraction which makes working with {@link StorageRpc.Option}s more type safe, and
 * null safe. When working with a {@code Map<StorageRpc.Option, ?>} we loose the independent type
 * information for each key. The type conversion methods on StorageRpc.Option are package-private,
 * and we DO NOT want to make them public. Which leads to this new class, which essentially creates
 * a mapping between a java type and a specific {@code StorageRpc.Option}.
 *
 * <p>We are defining each Option as a constant rather than an enum, because enum's can't have
 * generic class parameters. This shouldn't pose an issue:
 *
 * <ol>
 *   <li>This class is package-private
 *   <li>This class is sealed, and can only be constructed from within it's definition
 *   <li>This class is strictly used for mapping, never a property in a model that could be
 *       serialized
 * </ol>
 */
final class ZOpt<T> {

  static final ZOpt<String> PREDEFINED_ACL = new ZOpt<>(StorageRpc.Option.PREDEFINED_ACL);
  static final ZOpt<String> PREDEFINED_DEFAULT_OBJECT_ACL =
      new ZOpt<>(StorageRpc.Option.PREDEFINED_DEFAULT_OBJECT_ACL);
  static final ZOpt<Long> IF_METAGENERATION_MATCH =
      new ZOpt<>(StorageRpc.Option.IF_METAGENERATION_MATCH);
  static final ZOpt<Long> IF_METAGENERATION_NOT_MATCH =
      new ZOpt<>(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH);
  static final ZOpt<Long> IF_GENERATION_MATCH = new ZOpt<>(StorageRpc.Option.IF_GENERATION_MATCH);
  static final ZOpt<Long> IF_GENERATION_NOT_MATCH =
      new ZOpt<>(StorageRpc.Option.IF_GENERATION_NOT_MATCH);
  static final ZOpt<Boolean> IF_DISABLE_GZIP_CONTENT =
      new ZOpt<>(StorageRpc.Option.IF_DISABLE_GZIP_CONTENT);
  static final ZOpt<String> PREFIX = new ZOpt<>(StorageRpc.Option.PREFIX);
  static final ZOpt<String> PROJECT_ID = new ZOpt<>(StorageRpc.Option.PROJECT_ID);
  static final ZOpt<Long> MAX_RESULTS = new ZOpt<>(StorageRpc.Option.MAX_RESULTS);
  static final ZOpt<String> PAGE_TOKEN = new ZOpt<>(StorageRpc.Option.PAGE_TOKEN);
  static final ZOpt<String> DELIMITER = new ZOpt<>(StorageRpc.Option.DELIMITER);
  static final ZOpt<String> START_OFF_SET = new ZOpt<>(StorageRpc.Option.START_OFF_SET);
  static final ZOpt<String> END_OFF_SET = new ZOpt<>(StorageRpc.Option.END_OFF_SET);
  static final ZOpt<String> CUSTOMER_SUPPLIED_KEY =
      new ZOpt<>(StorageRpc.Option.CUSTOMER_SUPPLIED_KEY);
  static final ZOpt<String> USER_PROJECT = new ZOpt<>(StorageRpc.Option.USER_PROJECT);
  static final ZOpt<String> KMS_KEY_NAME = new ZOpt<>(StorageRpc.Option.KMS_KEY_NAME);
  static final ZOpt<String> SERVICE_ACCOUNT_EMAIL =
      new ZOpt<>(StorageRpc.Option.SERVICE_ACCOUNT_EMAIL);
  static final ZOpt<Boolean> SHOW_DELETED_KEYS = new ZOpt<>(StorageRpc.Option.SHOW_DELETED_KEYS);

  private final StorageRpc.Option key;

  private ZOpt(StorageRpc.Option key) {
    this.key = key;
  }

  StorageRpc.Option getKey() {
    return key;
  }

  /**
   * This method is only ever called from package private scope, as such the Map of option values
   * has prior type checked values which are later erased. Here we suppress the unchecked warning
   * since it's guarded ealier in the conversion.
   */
  @SuppressWarnings("unchecked")
  T cast(Object o) {
    return (T) o;
  }

  /**
   * Attempt to get the typed value from {@code optionValues}. If the key is not present in the
   * provided {@code optionValues} null will be returned.
   */
  @Nullable
  T get(@NonNull Map<StorageRpc.Option, ?> optionValues) {
    Object o = optionValues.get(key);
    return o == null ? null : cast(o);
  }

  /**
   * Create a new {@link ZOptConsumer} bound to the provided {@code consumer}
   *
   * @see ZOptConsumer#apply(Map)
   */
  ZOptConsumer<T, T> consumeVia(Consumer<@NonNull T> consumer) {
    return new ZOptConsumer<>(this, Function.identity(), consumer);
  }

  /**
   * Create a new {@link ZOptConsumer} which will apply {@code mapper} before consumption.
   *
   * @see ZOptConsumer#apply(Map)
   */
  <X> ZOptConsumer<T, X> mapThenConsumeVia(Function<T, X> mapper, Consumer<@NonNull X> consumer) {
    return new ZOptConsumer<>(this, mapper, consumer);
  }

  /**
   * For each consumer in {@code consumers} invoke {@link ZOptConsumer#apply(Map)} with {@code
   * optionValues}
   */
  static void applyAll(Map<StorageRpc.Option, ?> optionValues, ZOptConsumer<?, ?>... consumers) {
    for (ZOptConsumer<?, ?> consumer : consumers) {
      consumer.apply(optionValues);
    }
  }

  static class ZOptConsumer<T, X> {
    private final ZOpt<T> opt;
    private final Function<@NonNull T, @Nullable X> mapper;
    private final Consumer<@Nullable X> consumer;

    private ZOptConsumer(
        ZOpt<T> opt, Function<@NonNull T, @Nullable X> mapper, Consumer<@NonNull X> consumer) {
      this.opt = opt;
      this.mapper = mapper;
      this.consumer = consumer;
    }

    /**
     * Apply this consumer to the provided {@code optionValues}.
     *
     * <p>A null value will never be passed to the {@code consumer}.
     *
     * <ul>
     *   <li>If the provided {@code optionValues} does not contain a value related to {@code
     *       this.opt.key}, the method will terminate.
     *   <li>If the provided {@code optionValues} does contains a value, it will be sent through
     *       {@code this.mapper} if the result is non-null it will be passed to {@code
     *       this.consumer} otherwise the method will terminate.
     * </ul>
     */
    void apply(Map<StorageRpc.Option, ?> optionValues) {
      T t = opt.get(optionValues);
      if (t != null) {
        X x = mapper.apply(t);
        if (x != null) {
          consumer.accept(x);
        }
      }
    }
  }
}
